package com.fftool.soundboard.data

object AiPromptHelper {

    private val fileMap = mapOf(
        "Home Screen Layout" to listOf(
            "app/src/main/java/com/fftool/soundboard/ui/screens/HomeScreen.kt",
            "app/src/main/java/com/fftool/soundboard/ui/components/PremiumButton.kt"
        ),
        "Floating Panel Style" to listOf(
            "app/src/main/java/com/fftool/soundboard/ui/compose/FloatingPanelContent.kt",
            "app/src/main/java/com/fftool/soundboard/service/BubbleOverlayService.kt",
            "app/src/main/java/com/fftool/soundboard/ui/components/SoundBoxTile.kt"
        ),
        "Login Screen" to listOf(
            "app/src/main/java/com/fftool/soundboard/ui/screens/LoginScreen.kt",
            "app/src/main/java/com/fftool/soundboard/data/preferences/SecurePreferences.kt"
        ),
        "Upload System" to listOf(
            "app/src/main/java/com/fftool/soundboard/ui/screens/UploadScreen.kt",
            "app/src/main/java/com/fftool/soundboard/ui/screens/RenameWizardScreen.kt",
            "app/src/main/java/com/fftool/soundboard/data/SoundRepository.kt",
            "app/src/main/java/com/fftool/soundboard/data/db/SoundEntity.kt"
        ),
        "Admin Panel" to listOf(
            "app/src/main/java/com/fftool/soundboard/ui/screens/AdminPanelScreen.kt",
            "app/src/main/java/com/fftool/soundboard/data/preferences/SecurePreferences.kt",
            "app/src/main/java/com/fftool/soundboard/ui/screens/AdminLoginScreen.kt"
        ),
        "Settings Screen" to listOf(
            "app/src/main/java/com/fftool/soundboard/ui/screens/SettingsScreen.kt",
            "app/src/main/java/com/fftool/soundboard/ui/screens/PinEntryScreen.kt"
        ),
        "Sound Playback" to listOf(
            "app/src/main/java/com/fftool/soundboard/service/SoundPlayer.kt",
            "app/src/main/java/com/fftool/soundboard/ui/compose/FloatingPanelContent.kt"
        ),
        "Permissions" to listOf(
            "app/src/main/java/com/fftool/soundboard/ui/screens/PermissionsScreen.kt",
            "app/src/main/java/com/fftool/soundboard/MainActivity.kt"
        ),
        "Add New Feature" to listOf(
            "Create new files as needed in app/src/main/java/com/fftool/soundboard/"
        ),
        "Fix a Bug" to listOf(
            "Check app/src/main/java/com/fftool/soundboard/ for the relevant files"
        ),
        "Theme/Colors" to listOf(
            "app/src/main/java/com/fftool/soundboard/ui/theme/Color.kt",
            "app/src/main/java/com/fftool/soundboard/ui/theme/Theme.kt"
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

    fun getCategories(): List<String> = fileMap.keys.toList()

    fun getFilesForCategory(category: String): List<String> =
        fileMap[category] ?: listOf("Unknown location")

    fun generatePrompt(category: String, userDescription: String): String {
        val files = getFilesForCategory(category)
        val filesList = files.joinToString("\n") { "  - $it" }

        return """
AI CODE UPDATE REQUEST

Category: $category

$techStack

Requested Change:
$userDescription

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
