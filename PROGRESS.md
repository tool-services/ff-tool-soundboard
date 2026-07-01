# Build Progress Log

## 2026-07-01 — Phase 1: Project Skeleton & CI Pipeline
- Created complete Android project structure with Gradle 8.4 wrapper, AGP 8.2.2, Kotlin 1.9.22, Compose BOM 2024.01.00
- Set up project-level and app-level `build.gradle.kts` with all required dependencies:
  - Compose (UI, Material3, icons-extended)
  - Navigation Compose
  - DataStore Preferences
  - Media3 ExoPlayer
  - Room (database)
  - Jetpack Security (EncryptedSharedPreferences)
- Created `AndroidManifest.xml` with all required permissions and BubbleOverlayService declaration
- Established dark theme foundation: colors (`Color.kt`), typography (`Type.kt`), theme (`Theme.kt`)
- Implemented minimal SplashScreen composable as launcher
- Created vector adaptive app icon (FF diamond logo)
- Added `.gitignore` and `.github/workflows/build.yml` for CI
- Created `PROGRESS.md` and `PROJECT_STATE.txt` tracking files
- Created placeholder `BubbleOverlayService` and `Sound` data model
- Pushed to GitHub, build verified successful

## 2026-07-01 — Phase 2: Design System Foundation
- PremiumButton: pill shape (28dp), gradient fill, glow shadow, press scale animation
- PremiumCard: 16dp radius, SurfaceElevated bg, BorderSubtle border, soft shadow
- SoundBoxTile: square grid tile with empty (dashed/+), loaded, and playing states
- Playing state: animated pulse scale (1.0-1.04) and glow border in AccentSecondary
- GradientBackground: full-screen vertical gradient composable
- ComponentPreviewScreen: visual verification of all design components
- Fixed background overload ambiguity and nullable String? compilation errors
- GitHub Actions build verified successful

## 2026-07-01 — Phase 3: Splash + Login + Navigation
- Animated SplashScreen with fade-in logo/subtitle, auto-transition after 1.5s
- LoginScreen with styled OutlinedTextFields, test/test123 credential check
- Error display for invalid credentials
- HomeScreen placeholder with Upload/Launch buttons and Settings icon
- Navigation graph: Splash → Login → Home with clean backstack pop
- MainActivity refactored to NavHost with Compose Navigation
- GitHub Actions build verified successful

## 2026-07-01 — Phase 4: Home Screen + Permissions Onboarding
- PermissionsScreen with 3 premium-styled rationale cards (media, overlay, notifications)
- Each card: icon, description, grant button, granted/green status when done
- Overlay permission uses Settings.ACTION_MANAGE_OVERLAY_PERMISSION intent
- Continue button activates only when all permissions granted
- Navigation: Login → Permissions → Home
- HomeScreen polished with placeholders for Upload/Launch
- Fixed missing Box import compilation error
- GitHub Actions build verified successful

## 2026-07-01 — Phase 5: Upload System + Room Database
- Room database: SoundEntity, SoundDao, SoundDatabase (singleton)
- SoundRepository: file copy to private storage, extension detection
- UploadScreen: single file picker, folder picker, Default/Custom name dropdown
- RenameWizardScreen: step-by-step rename with Next/Skip Remaining
- Live grid preview showing 12 boxes on UploadScreen
- Navigation: Home → Upload → RenameWizard → back to Upload
- kapt plugin added for Room annotation processing
- Fixed @OptIn(ExperimentalMaterial3Api) compilation error
- GitHub Actions build verified successful

## 2026-07-01 — Phase 6: Floating Bubble Overlay Service
- BubbleOverlayService as foreground service with WindowManager overlay
- Draggable circular bubble (56dp) with edge-snap behavior
- Resides above all apps via TYPE_APPLICATION_OVERLAY
- NotificationHelper for notification channel + foreground notification
- Stop action button in notification to end service
- Notification channel created in MainActivity.onCreate
- HomeScreen Launch button starts foreground service
- XML layout for bubble: purple circle with FF text
- Fixed explicit R import compilation error
- GitHub Actions build verified successful

## 2026-07-01 — Phase 7: Floating Panel UI + ExoPlayer Playback
- SoundPlayer: ExoPlayer single-channel playback (play/stop/switch, no overlap)
- FloatingPanelContent: Compose panel with header, social icons, 4-col sound grid
- Social icons: TikTok, WhatsApp, YouTube, Website (Material icons) with ACTION_VIEW intents
- Sound grid loaded from Room DB with active-state glow animation
- ComposeView integrated into BubbleOverlayService as panel overlay
- Panel toggles on bubble tap, follows bubble position during drag
- Fixed Icons.Outlined.Globe → Language compilation error
- GitHub Actions build verified successful

## 2026-07-01 — Phase 8: Settings + PIN Entry + Admin Login Gate
- SecurePreferences: EncryptedSharedPreferences for all credentials + social links
- SettingsScreen: theme, about, change password, Only Access hidden button
- PinEntryScreen: premium numeric keypad, 5-digit PIN (42201), error on wrong
- AdminLoginScreen: admin login (only/bypass), reads from SecurePreferences
- Navigation: Settings → PIN Entry → Admin Login → Admin Panel (placeholder)
- Fixed @OptIn(ExperimentalMaterial3Api) compilation error
- GitHub Actions build verified successful

## 2026-07-01 — Phase 9: Admin Panel (Social, Theme, AI Prompt, Credentials)
- AdminPanelScreen with 4-tab layout: Social Links Manager, Theme Editor, AI Prompt Generator, Credentials Manager
- Social Links Manager: editable fields for TikTok, WhatsApp, YouTube, Website URLs saved to SecurePreferences
- Theme Editor: color swatch display for all 7 palette tokens with hex values
- AI Prompt Generator: ExposedDropdownMenu category selector, description input, file-mapping table from AiPromptHelper, copy-to-clipboard
- Credentials Manager: separate Admin and User username/password fields saved to SecurePreferences
- Updated FloatingPanelContent to read social URLs from SecurePreferences instead of hardcoded values
- Updated LoginScreen.validateLogin to check SecurePreferences (admin/user creds) instead of hardcoded test/test123
- Updated NavGraph AdminPanel route from placeholder to real AdminPanelScreen composable
- GitHub Actions build verified successful

## 2026-07-01 — Phase 10: Polish — Screen Transitions + Release APK
- Added fadeIn/fadeOut screen transition animations to all NavGraph composable routes
- Forward navigation (Home, Upload, Settings, Admin screens) uses slideInHorizontally + fadeIn
- Pop navigation uses slideOutHorizontally + fadeOut
- PremiumButton already had press-scale animation (0.96), SoundBoxTile already had pulse/glow animation for playing state
- Added release signing config to build.gradle.kts (reads from env vars with fallback defaults)
- Updated proguard-rules.pro with keep rules for Compose, Room, Security Crypto, ExoPlayer
- Updated GitHub Actions workflow: generates ephemeral keystore via keytool, builds both debug and release APKs
- Release APK uploaded as separate artifact (app-release)

## 2026-07-01 — Blueprint V2: Floating Panel, Sound Images, AI Prompts, Rotation
- Redesigned floating panel: bottom layout with vertical social icons bar, grid with scrollbar
- Header: clickable logo collapses to bubble, optional Hide button
- Sound box images: image picker during upload (300×300 square), stored as image_path in Room DB
- SoundBoxTile: displays image from file path via BitmapFactory, falls back to icon
- Per-component AI prompt registry in AdminPanel (one entry per major component)
- Rotation handling: BubbleOverlayService adjusts view positions on config change
- Favorites sorting: sounds ordered by is_favorite DESC, box_number ASC
- Fixed V2 build: replaced scrollBy (not on LazyGridState) with scrollToItem
- GitHub Actions build verified successful (#21)
