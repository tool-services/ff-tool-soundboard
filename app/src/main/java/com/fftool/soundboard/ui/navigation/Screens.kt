package com.fftool.soundboard.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Permissions : Screen("permissions")
    data object Home : Screen("home")
    data object Upload : Screen("upload")
    data object RenameWizard : Screen("rename_wizard/{uris}/{names}") {
        fun createRoute() = "rename_wizard/-/-"
    }
    data object Settings : Screen("settings")
}
