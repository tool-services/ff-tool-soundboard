package com.fftool.soundboard.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fftool.soundboard.ui.screens.AdminLoginScreen
import com.fftool.soundboard.ui.screens.AdminPanelScreen
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
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(350)) }
        ) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Login.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(250)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(250)) }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Permissions.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Permissions.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(250)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(250)) }
        ) {
            PermissionsScreen(
                onAllGranted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Permissions.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Home.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) + slideInHorizontally(animationSpec = tween(350)) { it / 4 } },
            exitTransition = { fadeOut(animationSpec = tween(250)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(250)) + slideOutHorizontally(animationSpec = tween(250)) { it / 4 } }
        ) {
            HomeScreen(
                onNavigateToUpload = {
                    navController.navigate(Screen.Upload.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Upload.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) + slideInHorizontally(animationSpec = tween(350)) { it / 4 } },
            exitTransition = { fadeOut(animationSpec = tween(250)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(250)) + slideOutHorizontally(animationSpec = tween(250)) { it / 4 } }
        ) {
            UploadScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRenameWizard = { uris, names ->
                    NavArgs.renameUris = uris
                    NavArgs.renameNames = names
                    navController.navigate(Screen.RenameWizard.createRoute())
                }
            )
        }

        composable(
            route = Screen.RenameWizard.createRoute(),
            enterTransition = { fadeIn(animationSpec = tween(350)) + slideInHorizontally(animationSpec = tween(350)) { it / 4 } },
            exitTransition = { fadeOut(animationSpec = tween(250)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(250)) + slideOutHorizontally(animationSpec = tween(250)) { it / 4 } }
        ) {
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

        composable(
            route = Screen.Settings.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) + slideInHorizontally(animationSpec = tween(350)) { it / 4 } },
            exitTransition = { fadeOut(animationSpec = tween(250)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(250)) + slideOutHorizontally(animationSpec = tween(250)) { it / 4 } }
        ) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPinEntry = {
                    navController.navigate(Screen.PinEntry.route)
                }
            )
        }

        composable(
            route = Screen.PinEntry.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) + slideInHorizontally(animationSpec = tween(350)) { it / 4 } },
            exitTransition = { fadeOut(animationSpec = tween(250)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(250)) + slideOutHorizontally(animationSpec = tween(250)) { it / 4 } }
        ) {
            PinEntryScreen(
                onNavigateBack = { navController.popBackStack() },
                onPinCorrect = {
                    navController.navigate(Screen.AdminLogin.route) {
                        popUpTo(Screen.PinEntry.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.AdminLogin.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) + slideInHorizontally(animationSpec = tween(350)) { it / 4 } },
            exitTransition = { fadeOut(animationSpec = tween(250)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(250)) + slideOutHorizontally(animationSpec = tween(250)) { it / 4 } }
        ) {
            AdminLoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(Screen.AdminPanel.route) {
                        popUpTo(Screen.AdminLogin.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.AdminPanel.route,
            enterTransition = { fadeIn(animationSpec = tween(350)) + slideInHorizontally(animationSpec = tween(350)) { it / 4 } },
            exitTransition = { fadeOut(animationSpec = tween(250)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(250)) + slideOutHorizontally(animationSpec = tween(250)) { it / 4 } }
        ) {
            AdminPanelScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
