package com.mfr.movewaeasy.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Im in my old Device")
        Button(
            onClick = { navController.navigate("Backup") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Backup")
        }
        Text("Im in my new Device")
        Button(
            onClick = { navController.navigate("Restore") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Restore")
        }
    }
}