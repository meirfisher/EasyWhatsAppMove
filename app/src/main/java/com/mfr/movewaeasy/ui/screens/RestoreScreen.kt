package com.mfr.movewaeasy.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mfr.movewaeasy.utils.FileUtils.toStringSize
import com.mfr.movewaeasy.viewmodels.RestoreViewModel

@Composable
fun RestoreScreen(viewModel: RestoreViewModel = viewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.setBackupFile(uri = it, context = context) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { launcher.launch("application/zip") }) {
            Text("Select Backup File")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (state.isFileSelected) {
            FileDetailsCard(state)
            if (state.isRestoring) {
                RestoreProgress(state, onCancel = viewModel::cancelRestore)
            } else {
                Button(
                    onClick = {
                        if (!state.isWhatsappFolderFound) {
                            viewModel.restoreFile(context = context)
                        }
                        else {
                            // Raise a confirmation dialog or similar to ask for confirmation
                            showConfirmationDialog = true
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Restore")
                }
            }
            if (showConfirmationDialog) {
               ConfirmationDialog(
                   onConfirm = {
                       showConfirmationDialog = false
                       viewModel.restoreFile(context = context)
                   },
                   onDismiss = { showConfirmationDialog = false }
               )
            }
        } else {
            Text("please select a valid backup file", style = MaterialTheme.typography.bodyMedium)
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
private fun FileDetailsCard(state: RestoreViewModel.RestoreState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Selected File: ${state.fileName ?: "None"}", style = MaterialTheme.typography.bodyMedium)
            Text("File Size: ${state.fileSize.toStringSize()}", style = MaterialTheme.typography.bodyMedium)
            Text("Created: ${state.creationTime ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun RestoreProgress(state: RestoreViewModel.RestoreState, onCancel: () -> Unit) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Progress: ${(state.progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        Text(
            text = "Restoring...",
            style = MaterialTheme.typography.bodyMedium
        )
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
                    text = "Files restored: ${state.filesRestoredCount}/${state.filesTotalCount ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Button(onClick = onCancel, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Cancel Restore Process")
        }
    }
}

@Composable
private fun ConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Restore") },
        text = {
            Text(
                "A WhatsApp folder was found. Data will be restored to \"WhatsApp-Backup\" Folder to avoid overwriting. Are you sure you want to continue?"
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun RestoreScreenPreview() {
    RestoreScreen()
}