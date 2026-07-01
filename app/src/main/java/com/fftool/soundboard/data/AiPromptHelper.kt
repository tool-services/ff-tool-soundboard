package com.fftool.soundboard.data

data class PromptEntry(
    val componentName: String,
    val files: List<String>,
    val currentBehavior: String,
    val iconResName: String = "Psychology"
)

object AiPromptHelper {

    val entries: List<PromptEntry> = listOf(
        PromptEntry(
            "Splash Screen",
            listOf("app/src/main/java/com/fftool/soundboard/ui/screens/SplashScreen.kt"),
            "Shows FF TOOL branding with fade-in animation, then auto-navigates to Login after 1.5s."
        ),
        PromptEntry(
            "User Login Screen",
            listOf(
                "app/src/main/java/com/fftool/soundboard/ui/screens/LoginScreen.kt",
                "app/src/main/java/com/fftool/soundboard/data/preferences/SecurePreferences.kt"
            ),
            "Username/password fields with admin/user credential validation against EncryptedSharedPreferences."
        ),
        PromptEntry(
            "Home Screen Layout",
            listOf("app/src/main/java/com/fftool/soundboard/ui/screens/HomeScreen.kt"),
            "Hub screen with Upload, Settings, and Launch buttons to start the overlay service."
        ),
        PromptEntry(
            "Upload Flow (Single File)",
            listOf(
                "app/src/main/java/com/fftool/soundboard/ui/screens/UploadScreen.kt",
                "app/src/main/java/com/fftool/soundboard/data/SoundRepository.kt"
            ),
            "Single audio file picker with optional cover image picker, naming dropdown, and save to Room DB."
        ),
        PromptEntry(
            "Upload Flow (Folder)",
            listOf(
                "app/src/main/java/com/fftool/soundboard/ui/screens/UploadScreen.kt",
                "app/src/main/java/com/fftool/soundboard/data/SoundRepository.kt"
            ),
            "Folder/document-tree picker scans for audio/* files, optionally renames each via wizard, bulk-imports."
        ),
        PromptEntry(
            "Launch Button Logic",
            listOf(
                "app/src/main/java/com/fftool/soundboard/ui/screens/HomeScreen.kt",
                "app/src/main/java/com/fftool/soundboard/service/BubbleOverlayService.kt"
            ),
            "Tapping Launch starts BubbleOverlayService as a foreground service and displays the floating bubble."
        ),
        PromptEntry(
            "Floating Bubble Behavior",
            listOf(
                "app/src/main/java/com/fftool/soundboard/service/BubbleOverlayService.kt"
            ),
            "Draggable circular bubble (56dp) with edge-snap. Tap to open panel. Clamps on rotation."
        ),
        PromptEntry(
            "Floating Panel Layout",
            listOf(
                "app/src/main/java/com/fftool/soundboard/ui/compose/FloatingPanelContent.kt"
            ),
            "Floating window with logo header, left-edge social icons, sound grid, right fast-scroll bar."
        ),
        PromptEntry(
            "Sound Box Playback Logic",
            listOf(
                "app/src/main/java/com/fftool/soundboard/service/SoundPlayer.kt",
                "app/src/main/java/com/fftool/soundboard/ui/components/SoundBoxTile.kt"
            ),
            "Single-channel ExoPlayer playback. Tapping a new sound stops current and plays new. Pulse glow on active."
        ),
        PromptEntry(
            "Social Icons Links",
            listOf(
                "app/src/main/java/com/fftool/soundboard/ui/compose/FloatingPanelContent.kt",
                "app/src/main/java/com/fftool/soundboard/data/preferences/SecurePreferences.kt"
            ),
            "Four social icons (TikTok, WhatsApp, YouTube, Website) on the left edge of the panel. URLs editable via Admin Panel."
        ),
        PromptEntry(
            "Settings Screen",
            listOf("app/src/main/java/com/fftool/soundboard/ui/screens/SettingsScreen.kt"),
            "Settings with theme info, Only Access button, about section."
        ),
        PromptEntry(
            "Admin PIN / Login Screens",
            listOf(
                "app/src/main/java/com/fftool/soundboard/ui/screens/PinEntryScreen.kt",
                "app/src/main/java/com/fftool/soundboard/ui/screens/AdminLoginScreen.kt"
            ),
            "5-digit PIN gate (default 42201) followed by admin login (default only/bypass) before accessing Admin Panel."
        ),
        PromptEntry(
            "Admin Dashboard",
            listOf("app/src/main/java/com/fftool/soundboard/ui/screens/AdminPanelScreen.kt"),
            "Tabbed admin panel: Social Links, Theme Editor, AI Prompt Generator, Credentials Manager."
        ),
        PromptEntry(
            "Theme / Color Editor",
            listOf(
                "app/src/main/java/com/fftool/soundboard/ui/theme/Color.kt",
                "app/src/main/java/com/fftool/soundboard/ui/theme/Theme.kt"
            ),
            "Seven-token dark color palette displayed with hex values in the Admin Theme Editor tab."
        ),
        PromptEntry(
            "About Section",
            listOf("app/src/main/java/com/fftool/soundboard/ui/screens/SettingsScreen.kt"),
            "App version and developer info shown in Settings."
        ),
        PromptEntry(
            "App Icon Changer",
            listOf(
                "app/src/main/java/com/fftool/soundboard/AndroidManifest.xml",
                "app/src/main/java/com/fftool/soundboard/ui/screens/IconPickerScreen.kt"
            ),
            "Activity-alias icon switching from Admin Panel. Choose from pre-bundled icon variants."
        ),
        PromptEntry(
            "Credentials Manager",
            listOf(
                "app/src/main/java/com/fftool/soundboard/data/preferences/SecurePreferences.kt",
                "app/src/main/java/com/fftool/soundboard/ui/screens/AdminPanelScreen.kt"
            ),
            "Admin and User username/password management saved to EncryptedSharedPreferences."
        ),
        PromptEntry(
            "Sound Box Images",
            listOf(
                "app/src/main/java/com/fftool/soundboard/ui/screens/UploadScreen.kt",
                "app/src/main/java/com/fftool/soundboard/ui/components/SoundBoxTile.kt",
                "app/src/main/java/com/fftool/soundboard/data/SoundRepository.kt"
            ),
            "Optional cover image upload per sound box. Stored as 300x300 JPEG in app-private images/ directory."
        ),
        PromptEntry(
            "Panel Opacity / Auto-Hide",
            listOf(
                "app/src/main/java/com/fftool/soundboard/ui/screens/SettingsScreen.kt",
                "app/src/main/java/com/fftool/soundboard/service/BubbleOverlayService.kt"
            ),
            "Settings toggles for panel opacity slider and auto-hide timer (configurable seconds of inactivity)."
        )
    )

    private val techStack = """
Tech Stack:
- Language: Kotlin
- UI: Jetpack Compose with Material3
- Navigation: Compose Navigation (NavHost)
- Database: Room (SQLite)
- Media: Media3 ExoPlayer
- Overlay: WindowManager (TYPE_APPLICATION_OVERLAY)
- Storage: EncryptedSharedPreferences (Jetpack Security)
- Playback: Single-channel (no overlapping audio)
""".trimIndent()

    fun getComponentNames(): List<String> = entries.map { it.componentName }

    fun getEntry(name: String): PromptEntry? = entries.find { it.componentName == name }

    fun generatePrompt(componentName: String, userNotes: String): String {
        val entry = getEntry(componentName) ?: return "Unknown component: $componentName"
        val filesList = entry.files.joinToString("\n") { "  - $it" }

        return """
AI CODE UPDATE REQUEST

Component: ${entry.componentName}

$techStack

Current Behavior:
${entry.currentBehavior}

Requested Change:
${if (userNotes.isNotBlank()) userNotes else "[Describe the change you want here]"}

Relevant Files:
$filesList

Instructions:
- Return the full updated file content only for the files listed above.
- Follow the existing code style and patterns in those files.
- Use the existing design tokens from Color.kt and Theme.kt.
- Do not add any external libraries unless absolutely necessary.
- Ensure all imports are included.
""".trimIndent()
    }
}
