package com.mfr.movewaeasy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.mfr.movewaeasy.utils.FileUtils.toStringSize
import com.mfr.movewaeasy.viewmodels.BackupViewModel

@Composable
fun BackupScreen(navController: NavController) {
    val viewModel: BackupViewModel = viewModel()
    val state by viewModel.backupState.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text("Free Space on Device: ${state.freeSpace.toStringSize()}")

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
            Text("backup Size: ${state.folderSize.toStringSize()}")
        }
        if (state.isCompressing) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text("Backing up files...")

                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Progress: ${(state.progress * 100).toInt()}%",
                    modifier = Modifier.padding(top = 4.dp)
                    )

                Text("Files Processed: ${state.fileOnProgress} out of ${state.filesCount}")

                // A text box for showing the backup file path information
                Text("Backup File Path: ${state.backupFilePath}")

                Button(
                    onClick = { viewModel.cancelBackup() },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Cancel Backup")
                }
            }
        } else {
            Button(
                onClick = { viewModel.startBackup() },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Start Backup")
            }
        }
        state.errorMessage?.let {
            Text(
                text = it,
                color = androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}