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
            startDestination = getStartDestination(context)
        ) {
            composable(Routes.PERMISSIONS_SCREEN) {
                PermissionScreen(navController)
            }
            composable(Routes.HOME_SCREEN) {
                MainScreen(navController)
            }
            composable(Routes.BACKUP_SCREEN) {
                BackupScreen()
            }
            composable(Routes.RESTORE_SCREEN) {
                RestoreScreen()
            }
        }
    }
}

private fun getStartDestination(context: Context): String {
    return if (hasPermissions(context)) Routes.HOME_SCREEN else Routes.PERMISSIONS_SCREEN
}