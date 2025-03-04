package com.mfr.movewaeasy.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mfr.movewaeasy.utils.FileUtils.convertBytesToGB
import com.mfr.movewaeasy.viewmodels.BackupViewModel

@Composable
fun BackupScreen(navController: NavController) {
    val viewModel: BackupViewModel = viewModel()
    val state by viewModel.backupState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Folder Size: ${convertBytesToGB(state.folderSize)}")
        Text("Free Space: ${convertBytesToGB(state.freeSpace)}")
        if (state.isCompressing) {
            LinearProgressIndicator(
                progress = { state.progress },
            )
        } else {
            Button(onClick = { viewModel.startBackup() }) {
                Text("Start Backup")
            }
        }
    }
}