package com.mfr.movewaeasy

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mfr.movewaeasy.ui.screens.BackupScreen
import com.mfr.movewaeasy.ui.screens.MainScreen
import com.mfr.movewaeasy.ui.screens.PermissionScreen
import com.mfr.movewaeasy.ui.screens.RestoreScreen
import com.mfr.movewaeasy.ui.theme.MoveWAEasyTheme
import com.mfr.movewaeasy.utils.PermissionUtils.hasPermissions


@Composable
fun ScreenNavigation(context: Context) {
    MoveWAEasyTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = if (hasPermissions(context)) Routes.HomeScreen else Routes.PermissionsScreen
        ) {
            composable(Routes.PermissionsScreen) {
                PermissionScreen(navController)
            }
            composable(Routes.HomeScreen) {
                MainScreen(navController)
            }
            composable(Routes.BackupScreen) {
                BackupScreen()
            }
            composable(Routes.RestoreScreen) {
                RestoreScreen()
            }
        }
    }
}