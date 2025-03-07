package com.mfr.movewaeasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mfr.movewaeasy.ui.screens.BackupScreen
import com.mfr.movewaeasy.ui.screens.MainScreen
import com.mfr.movewaeasy.ui.screens.PermissionScreen
import com.mfr.movewaeasy.ui.screens.RestoreScreen
import com.mfr.movewaeasy.ui.theme.MoveWAEasyTheme
import com.mfr.movewaeasy.utils.PermissionUtils.hasPermissions

class MainActivity : ComponentActivity() {

    // Check if the app has the required permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContent {
            MoveWAEasyTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = if (hasPermissions(this)) "main" else "permission"
                ) {
                    composable("permission") {  PermissionScreen(navController)  }
                    composable("main") { MainScreen(navController) }
                    composable("backup") { BackupScreen(navController) }
                    composable("restore") { RestoreScreen(navController) }
                }
            }
        }
    }
}
