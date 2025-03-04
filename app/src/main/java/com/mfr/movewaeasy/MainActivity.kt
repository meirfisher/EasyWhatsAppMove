package com.mfr.movewaeasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mfr.movewaeasy.ui.screens.BackupScreen
import com.mfr.movewaeasy.ui.screens.MainScreen
import com.mfr.movewaeasy.ui.screens.PermissionScreen
import com.mfr.movewaeasy.ui.screens.RestoreScreen
import com.mfr.movewaeasy.ui.theme.MoveWAEasyTheme
import com.mfr.movewaeasy.utils.PermissionUtils
import com.mfr.movewaeasy.utils.PermissionUtils.hasPermissions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveWAEasyTheme {
                val navController = rememberNavController()
                val startDestination = if (hasPermissions(this)) "main" else "permission"
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("permission") {  PermissionScreen(navController)  }
                    composable("main") { MainScreen(navController) }
                    composable("backup") { BackupScreen(navController) }
                    composable("restore") { RestoreScreen(navController) }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
    }
}

