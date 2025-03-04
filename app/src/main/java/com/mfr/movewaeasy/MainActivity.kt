package com.mfr.movewaeasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.Navigation.findNavController
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

    // Check if the app has the required permissions
    private val startDestination = if (hasPermissions(this)) "main" else "permission"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveWAEasyTheme {
                val navController = rememberNavController()
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
}

