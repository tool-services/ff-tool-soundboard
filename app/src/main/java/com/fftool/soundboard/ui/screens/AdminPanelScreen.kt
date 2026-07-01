package com.fftool.soundboard.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fftool.soundboard.data.AiPromptHelper
import com.fftool.soundboard.data.preferences.SecurePreferences
import com.fftool.soundboard.ui.theme.AccentPrimary
import com.fftool.soundboard.ui.theme.AccentSecondary
import com.fftool.soundboard.ui.theme.BackgroundPrimary
import com.fftool.soundboard.ui.theme.BackgroundSecondary
import com.fftool.soundboard.ui.theme.BorderSubtle
import com.fftool.soundboard.ui.theme.ErrorRed
import com.fftool.soundboard.ui.theme.SuccessGreen
import com.fftool.soundboard.ui.theme.SurfaceElevated
import com.fftool.soundboard.ui.theme.TextPrimary
import com.fftool.soundboard.ui.theme.TextSecondary

enum class AdminSection { SOCIAL, THEME, AI, CREDENTIALS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { SecurePreferences(context) }
    var currentSection by remember { mutableStateOf(AdminSection.SOCIAL) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        TopAppBar(
            title = { Text("Admin Panel", color = TextPrimary) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundSecondary)
        )

        // Section tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundSecondary)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SectionTab("Social", Icons.Outlined.Link, currentSection == AdminSection.SOCIAL) {
                currentSection = AdminSection.SOCIAL
            }
            SectionTab("Theme", Icons.Outlined.ColorLens, currentSection == AdminSection.THEME) {
                currentSection = AdminSection.THEME
            }
            SectionTab("AI Prompt", Icons.Outlined.Psychology, currentSection == AdminSection.AI) {
                currentSection = AdminSection.AI
            }
            SectionTab("Credentials", Icons.Outlined.Person, currentSection == AdminSection.CREDENTIALS) {
                currentSection = AdminSection.CREDENTIALS
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (currentSection) {
                AdminSection.SOCIAL -> SocialLinksSection(prefs)
                AdminSection.THEME -> ThemeEditorSection(prefs)
                AdminSection.AI -> AiPromptSection(context)
                AdminSection.CREDENTIALS -> CredentialsSection(context, prefs)
            }
        }
    }
}

@Composable
private fun SectionTab(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) AccentPrimary.copy(alpha = 0.2f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) AccentPrimary else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            color = if (selected) AccentPrimary else TextSecondary,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SocialLinksSection(prefs: SecurePreferences) {
    var tiktok by remember { mutableStateOf(prefs.tiktokUrl) }
    var whatsapp by remember { mutableStateOf(prefs.whatsappNumber) }
    var youtube by remember { mutableStateOf(prefs.youtubeUrl) }
    var website by remember { mutableStateOf(prefs.websiteUrl) }
    var saved by remember { mutableStateOf(false) }

    Text("Social Media Links", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Text("URLs used in the floating panel", color = TextSecondary, fontSize = 13.sp)
    Spacer(Modifier.height(16.dp))

    SocialField("TikTok URL", tiktok, { tiktok = it; saved = false })
    Spacer(Modifier.height(8.dp))
    SocialField("WhatsApp Number", whatsapp, { whatsapp = it; saved = false })
    Spacer(Modifier.height(8.dp))
    SocialField("YouTube URL", youtube, { youtube = it; saved = false })
    Spacer(Modifier.height(8.dp))
    SocialField("Website URL", website, { website = it; saved = false })
    Spacer(Modifier.height(16.dp))

    Button(
        onClick = {
            prefs.tiktokUrl = tiktok
            prefs.whatsappNumber = whatsapp
            prefs.youtubeUrl = youtube
            prefs.websiteUrl = website
            saved = true
        },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
    ) {
        Icon(Icons.Outlined.Save, null, Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(if (saved) "Saved!" else "Save Changes", color = TextPrimary)
    }
}

@Composable
private fun SocialField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = fieldColors()
    )
}

@Composable
private fun ThemeEditorSection(prefs: SecurePreferences) {
    Text("Theme Colors", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Text("Customize the app color palette", color = TextSecondary, fontSize = 13.sp)
    Spacer(Modifier.height(16.dp))
    Text(
        "Color customization will be reflected across the app after restart.",
        color = TextSecondary, fontSize = 12.sp
    )
    Spacer(Modifier.height(16.dp))

    val colorTokens = listOf(
        "Background Primary" to "#0B0E14",
        "Background Secondary" to "#131826",
        "Surface Elevated" to "#1B2233",
        "Accent Primary" to "#6C5CE7",
        "Accent Secondary" to "#00D9C0",
        "Text Primary" to "#F5F6FA",
        "Text Secondary" to "#9AA3B2"
    )

    colorTokens.forEach { (name, hex) ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp)).background(SurfaceElevated).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(android.graphics.Color.parseColor(hex)))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(name, color = TextPrimary, fontSize = 14.sp)
                Text(hex, color = TextSecondary, fontSize = 12.sp)
            }
        }
    }

    Spacer(Modifier.height(16.dp))
    Text(
        text = "Reset to Default",
        color = AccentPrimary,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.clickable { /* Reset all colors */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiPromptSection(context: Context) {
    val entries = remember { AiPromptHelper.entries }
    var selectedEntry by remember { mutableStateOf(entries.firstOrNull()) }
    var userNotes by remember { mutableStateOf("") }
    var generatedPrompt by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    Text("Customize App", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Text("Tap a component to generate a ready-to-paste AI prompt", color = TextSecondary, fontSize = 13.sp)
    Spacer(Modifier.height(16.dp))

    // Per-component cards
    entries.forEach { entry ->
        val isSelected = selectedEntry?.componentName == entry.componentName
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) AccentPrimary.copy(alpha = 0.15f) else SurfaceElevated)
                .border(1.dp, if (isSelected) AccentPrimary else BorderSubtle, RoundedCornerShape(12.dp))
                .clickable {
                    selectedEntry = entry
                    userNotes = ""
                    generatedPrompt = ""
                    copied = false
                    showBottomSheet = true
                }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Psychology,
                    contentDescription = null,
                    tint = AccentPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(entry.componentName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(
                    entry.currentBehavior.take(60) + "...",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    // Bottom sheet when a component is selected
    if (showBottomSheet && selectedEntry != null) {
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceElevated)
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(selectedEntry!!.componentName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(selectedEntry!!.currentBehavior, color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))

                Text("Files:", color = TextSecondary, fontSize = 11.sp)
                selectedEntry!!.files.forEach { file ->
                    Text(file, color = TextPrimary.copy(alpha = 0.7f), fontSize = 10.sp)
                }
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = userNotes,
                    onValueChange = { userNotes = it },
                    placeholder = { Text("Describe the change you want here...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = fieldColors()
                )
                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        generatedPrompt = AiPromptHelper.generatePrompt(selectedEntry!!.componentName, userNotes)
                        copied = false
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
                ) {
                    Text("Generate Prompt", color = TextPrimary)
                }

                if (generatedPrompt.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundSecondary)
                            .padding(12.dp)
                    ) {
                        Text(generatedPrompt, color = TextSecondary, fontSize = 11.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("AI Prompt", generatedPrompt))
                            copied = true
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (copied) SuccessGreen else AccentPrimary
                        )
                    ) {
                        Icon(Icons.Outlined.ContentCopy, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (copied) "Copied!" else "Copy Prompt", color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun CredentialsSection(context: Context, prefs: SecurePreferences) {
    var adminUser by remember { mutableStateOf(prefs.adminUsername) }
    var adminPass by remember { mutableStateOf(prefs.adminPassword) }
    var userUser by remember { mutableStateOf(prefs.userUsername) }
    var userPass by remember { mutableStateOf(prefs.userPassword) }
    var adminSaved by remember { mutableStateOf(false) }
    var userSaved by remember { mutableStateOf(false) }

    Text("Admin Credentials", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(Modifier.height(12.dp))

    OutlinedTextField(adminUser, { adminUser = it; adminSaved = false }, label = { Text("Admin Username") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(adminPass, { adminPass = it; adminSaved = false }, label = { Text("Admin Password") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
    Spacer(Modifier.height(8.dp))
    Button(
        onClick = { prefs.adminUsername = adminUser; prefs.adminPassword = adminPass; adminSaved = true },
        modifier = Modifier.fillMaxWidth().height(44.dp),
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
    ) {
        Text(if (adminSaved) "Saved!" else "Update Admin Credentials", color = TextPrimary)
    }

    Spacer(Modifier.height(24.dp))
    Box(Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle))
    Spacer(Modifier.height(24.dp))

    Text("User Credentials", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(Modifier.height(12.dp))

    OutlinedTextField(userUser, { userUser = it; userSaved = false }, label = { Text("User Username") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(userPass, { userPass = it; userSaved = false }, label = { Text("User Password") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
    Spacer(Modifier.height(8.dp))
    Button(
        onClick = { prefs.userUsername = userUser; prefs.userPassword = userPass; userSaved = true },
        modifier = Modifier.fillMaxWidth().height(44.dp),
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
    ) {
        Text(if (userSaved) "Saved!" else "Update User Credentials", color = TextPrimary)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentPrimary,
    unfocusedBorderColor = BorderSubtle,
    cursorColor = AccentPrimary,
    focusedLabelColor = AccentPrimary,
    unfocusedLabelColor = TextSecondary,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)
