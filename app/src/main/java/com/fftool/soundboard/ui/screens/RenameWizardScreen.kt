package com.fftool.soundboard.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fftool.soundboard.data.SoundRepository
import com.fftool.soundboard.ui.theme.AccentPrimary
import com.fftool.soundboard.ui.theme.BackgroundPrimary
import com.fftool.soundboard.ui.theme.BackgroundSecondary
import com.fftool.soundboard.ui.theme.BorderSubtle
import com.fftool.soundboard.ui.theme.TextDisabled
import com.fftool.soundboard.ui.theme.TextPrimary
import com.fftool.soundboard.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun RenameWizardScreen(
    fileUris: List<Uri>,
    originalNames: List<String>,
    onComplete: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { SoundRepository(context) }

    var currentIndex by remember { mutableStateOf(0) }
    var customName by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val currentUri = fileUris.getOrNull(currentIndex)
    val currentOriginalName = originalNames.getOrNull(currentIndex) ?: "Unknown"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        TopAppBar(
            title = { Text("Rename Files", color = TextPrimary) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentUri != null) {
                Text(
                    text = "File ${currentIndex + 1} of ${fileUris.size}",
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentOriginalName,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = customName.ifBlank { currentOriginalName.substringBeforeLast(".") },
                    onValueChange = { customName = it },
                    label = { Text("Display Name") },
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

                Spacer(modifier = Modifier.weight(1f))

                // Next / Skip buttons
                Button(
                    onClick = {
                        scope.launch {
                            isProcessing = true
                            val name = customName.ifBlank {
                                currentOriginalName.substringBeforeLast(".")
                            }
                            repo.importSound(currentUri, name, isCustomName = true)
                            customName = ""
                            if (currentIndex + 1 < fileUris.size) {
                                currentIndex++
                            } else {
                                onComplete()
                            }
                            isProcessing = false
                        }
                    },
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary
                    )
                ) {
                    Text(
                        text = if (currentIndex + 1 < fileUris.size) "Next" else "Finish",
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            // Skip remaining - use default names
                            for (i in currentIndex until fileUris.size) {
                                val uri = fileUris[i]
                                val name = originalNames.getOrElse(i) { "Unknown" }
                                    .substringBeforeLast(".")
                                repo.importSound(uri, name, isCustomName = false)
                            }
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    )
                ) {
                    Text(
                        text = "Skip Remaining (Use Default Names)",
                        fontSize = 13.sp
                    )
                }
            } else {
                // No more files
                Text(
                    text = "All files processed!",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary
                    )
                ) {
                    Text("Done", color = TextPrimary)
                }
            }
        }
    }
}
