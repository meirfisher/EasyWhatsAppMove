package com.mfr.movewaeasy.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mfr.movewaeasy.utils.FileUtils.toStringSize
import com.mfr.movewaeasy.viewmodels.RestoreViewModel

@Composable
fun RestoreScreen(navController: NavController) {
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
            Text("File not selected or Bad file")
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