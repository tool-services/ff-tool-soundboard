package com.fftool.soundboard.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fftool.soundboard.data.SoundRepository
import com.fftool.soundboard.data.db.SoundDatabase
import com.fftool.soundboard.ui.components.PremiumButton
import com.fftool.soundboard.ui.components.SoundBoxTile
import com.fftool.soundboard.ui.theme.AccentPrimary
import com.fftool.soundboard.ui.theme.BackgroundPrimary
import com.fftool.soundboard.ui.theme.BackgroundSecondary
import com.fftool.soundboard.ui.theme.BorderSubtle
import com.fftool.soundboard.ui.theme.TextDisabled
import com.fftool.soundboard.ui.theme.TextPrimary
import com.fftool.soundboard.ui.theme.TextSecondary
import kotlinx.coroutines.launch

enum class NamingOption { DEFAULT, CUSTOM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRenameWizard: (List<Uri>, List<String>) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { SoundRepository(context) }
    val sounds by repo.allSounds.collectAsState(initial = emptyList())

    var namingOption by remember { mutableStateOf(NamingOption.DEFAULT) }
    var customName by remember { mutableStateOf("") }
    var namingDropdownExpanded by remember { mutableStateOf(false) }

    val singleFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val name = getFileName(context, uri)
                val displayName = if (namingOption == NamingOption.CUSTOM) {
                    customName.ifBlank { name }
                } else name
                repo.importSound(uri, displayName, isCustomName = namingOption == NamingOption.CUSTOM)
            }
        }
    }

    val folderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val audioFiles = mutableListOf<Pair<Uri, String>>()
                val docUri = uri.buildUpon().appendPath("").build()
                // Scan folder for audio files via ContentResolver
                val childrenUri = android.provider.DocumentsContract.buildChildDocumentsUriUsingTree(
                    uri,
                    android.provider.DocumentsContract.getTreeDocumentId(uri)
                )
                val cursor = context.contentResolver.query(
                    childrenUri, null, null, null, null
                )
                cursor?.use { c ->
                    while (c.moveToNext()) {
                        val docId = c.getString(c.getColumnIndexOrThrow(
                            android.provider.DocumentsContract.Document.COLUMN_DOCUMENT_ID
                        ))
                        val mime = c.getString(c.getColumnIndexOrThrow(
                            android.provider.DocumentsContract.Document.COLUMN_MIME_TYPE
                        ))
                        val displayName = c.getString(c.getColumnIndexOrThrow(
                            android.provider.DocumentsContract.Document.COLUMN_DISPLAY_NAME
                        ))
                        if (mime?.startsWith("audio/") == true) {
                            val fileUri = android.provider.DocumentsContract.buildDocumentUriUsingTree(
                                uri,
                                docId
                            )
                            audioFiles.add(fileUri to displayName)
                        }
                    }
                }

                if (audioFiles.isNotEmpty()) {
                    if (namingOption == NamingOption.CUSTOM) {
                        onNavigateToRenameWizard(
                            audioFiles.map { it.first },
                            audioFiles.map { it.second }
                        )
                    } else {
                        for ((fileUri, displayName) in audioFiles) {
                            val name = displayName.substringBeforeLast(".")
                            repo.importSound(fileUri, name, isCustomName = false)
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        // Top bar
        TopAppBar(
            title = {
                Text("Upload Sounds", color = TextPrimary)
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundSecondary
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Naming option dropdown
            Text(
                text = "File Naming",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = namingDropdownExpanded,
                onExpandedChange = { namingDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = if (namingOption == NamingOption.DEFAULT) "Use Default Name" else "Type Custom Name",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = namingDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                ExposedDropdownMenu(
                    expanded = namingDropdownExpanded,
                    onDismissRequest = { namingDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Use Default Name") },
                        onClick = {
                            namingOption = NamingOption.DEFAULT
                            namingDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Type Custom Name") },
                        onClick = {
                            namingOption = NamingOption.CUSTOM
                            namingDropdownExpanded = false
                        }
                    )
                }
            }

            if (namingOption == NamingOption.CUSTOM) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customName,
                    onValueChange = { customName = it },
                    label = { Text("Custom Name") },
                    placeholder = { Text("Enter sound name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = BorderSubtle,
                        cursorColor = AccentPrimary,
                        focusedLabelColor = AccentPrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Upload options
            Text(
                text = "Upload Options",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Single file button
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BackgroundSecondary)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FileUpload,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Upload File",
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Select a single MP3",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PremiumButton(
                        text = "Browse",
                        onClick = {
                            singleFileLauncher.launch(arrayOf("audio/*"))
                        },
                        height = 40.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Folder button
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BackgroundSecondary)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FolderOpen,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Upload Folder",
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Import all audio files",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PremiumButton(
                        text = "Browse",
                        onClick = { folderLauncher.launch(null) },
                        height = 40.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sound grid preview
            Text(
                text = "Sound Boxes",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "${sounds.size} sounds loaded",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp) // Fixed height for scrollable area
            ) {
                // Show empty boxes up to 12
                val allBoxes = (1..12).map { boxNum ->
                    sounds.find { it.boxNumber == boxNum }
                }
                items(allBoxes) { sound ->
                    SoundBoxTile(
                        boxNumber = sound?.boxNumber ?: (allBoxes.indexOf(sound) + 1),
                        displayName = sound?.displayName,
                        onClick = { /* Playback handled in Phase 7 */ }
                    )
                }
            }
        }
    }
}

private fun getFileName(context: android.content.Context, uri: Uri): String {
    var name = "Unknown"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use { c ->
        val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (c.moveToFirst() && nameIndex >= 0) {
            name = c.getString(nameIndex) ?: "Unknown"
        }
    }
    return name.substringBeforeLast(".").ifBlank { "Unknown" }
}
