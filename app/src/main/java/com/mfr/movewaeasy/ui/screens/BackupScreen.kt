package com.mfr.movewaeasy.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mfr.movewaeasy.utils.FileUtils.toStringSize
import com.mfr.movewaeasy.viewmodels.BackupViewModel


@Composable
fun BackupScreen(viewModel: BackupViewModel = viewModel()) {
    val state by viewModel.backupState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 300)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StorageInfoCard(state)
        if (state.isCompressing) {
            BackupProgress(state, onCancel = viewModel::cancelBackup)
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
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun StorageInfoCard(state: BackupViewModel.BackupState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Free Space on Device: ${state.freeSpace.toStringSize()}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (state.isCalculatingSize) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Calculating Backup file Size...", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Text("Backup Size: ${state.folderSize.toStringSize()}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun BackupProgress(state: BackupViewModel.BackupState, onCancel: () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Backing up files...", style = MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        Text(
            "Progress: ${(state.progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Files Processed: ${state.fileOnProgress}/${state.filesCount}",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "File in progress: ${state.backupFilePath ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Button(
            onClick = onCancel,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.End)
        ) {
            Text("Cancel Backup")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BackupScreenPreview() {
    BackupScreen()
}