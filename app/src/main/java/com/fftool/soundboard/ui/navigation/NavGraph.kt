package com.fftool.soundboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fftool.soundboard.ui.screens.AdminLoginScreen
import com.fftool.soundboard.ui.screens.HomeScreen
import com.fftool.soundboard.ui.screens.LoginScreen
import com.fftool.soundboard.ui.screens.PermissionsScreen
import com.fftool.soundboard.ui.screens.PinEntryScreen
import com.fftool.soundboard.ui.screens.RenameWizardScreen
import com.fftool.soundboard.ui.screens.SettingsScreen
import com.fftool.soundboard.ui.screens.SplashScreen
import com.fftool.soundboard.ui.screens.UploadScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Permissions.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Permissions.route) {
            PermissionsScreen(
                onAllGranted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Permissions.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToUpload = {
                    navController.navigate(Screen.Upload.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Upload.route) {
            UploadScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRenameWizard = { uris, names ->
                    NavArgs.renameUris = uris
                    NavArgs.renameNames = names
                    navController.navigate(Screen.RenameWizard.createRoute())
                }
            )
        }

        composable(Screen.RenameWizard.createRoute()) {
            RenameWizardScreen(
                fileUris = NavArgs.renameUris,
                originalNames = NavArgs.renameNames,
                onComplete = {
                    NavArgs.renameUris = emptyList()
                    NavArgs.renameNames = emptyList()
                    navController.popBackStack(Screen.Upload.route, inclusive = false)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPinEntry = {
                    navController.navigate(Screen.PinEntry.route)
                }
            )
        }

        composable(Screen.PinEntry.route) {
            PinEntryScreen(
                onNavigateBack = { navController.popBackStack() },
                onPinCorrect = {
                    navController.navigate(Screen.AdminLogin.route) {
                        popUpTo(Screen.PinEntry.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminLogin.route) {
            AdminLoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(Screen.AdminPanel.route) {
                        popUpTo(Screen.AdminLogin.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminPanel.route) {
            /* Phase 9 - Admin Panel Dashboard */
            androidx.compose.material3.Text(
                "Admin Panel",
                color = com.fftool.soundboard.ui.theme.TextPrimary
            )
        }
    }
}
