package com.mfr.movewaeasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mfr.movewaeasy.ui.theme.MoveWAEasyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveWAEasyTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("permission") { PermissionScreen(navController) }
                    composable("main") { MainScreen(navController) }
                    composable("backup") { BackupScreen(navController) }
                    composable("restore") { RestoreScreen(navController) }
                }
            }
        }
    }
}

