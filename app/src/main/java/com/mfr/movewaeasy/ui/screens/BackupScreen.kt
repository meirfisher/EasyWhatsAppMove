package com.mfr.movewaeasy.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
        Text("Free Space on Device: ${convertBytesToGB(state.freeSpace)}")
        if (state.isCalculatingSize) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ){
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calculating Backup file Size...")
            }
        } else {
            Text("Folder Size: ${convertBytesToGB(state.folderSize)}")
        }
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