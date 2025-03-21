package com.mfr.movewaeasy.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mfr.movewaeasy.utils.FileUtils.toStringSize
import com.mfr.movewaeasy.viewmodels.RestoreViewModel

@Composable
fun RestoreScreen() {
    val context = LocalContext.current
    val viewModel: RestoreViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.setBackupFile(uri = it, context = context) }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { launcher.launch("application/zip") }) {
            Text("Select Backup File")
        }
        if (state.isFileSelected) {
            Text("Selected File: ${state.fileName}")
            Text("File Size: ${state.fileSize.toStringSize()}")
            Text("Creation Time: ${state.creationTime}")
            if (state.isRestoring) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Progress: ${(state.progress * 100).toInt()}%",
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Restoring...")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Files restored: ${state.filesRestoredCount} of ${state.filesTotalCount}",
                                color = Color.White
                            )
                            Text(
                                text = "Restoring ${state.fileOnProgress}",
                                color = Color.White
                            )
                        }
                    }
                    Button(
                        onClick = { viewModel.cancelRestore() },
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text("Cancel Restore")
                    }
                }
            } else {
                Button(
                    onClick = { viewModel.restoreFile(context = context) },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Restore")
                }
            }
        } else {
            Text("please select a valid backup file")
        }

        state.errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RestoreScreenPreview() {
    RestoreScreen()
}