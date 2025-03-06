package com.mfr.movewaeasy.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
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
import com.mfr.movewaeasy.utils.FileUtils.convertBytesToGB
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
            Text("File Size: ${convertBytesToGB(state.fileSize)}")
            Text("Creation Time: ${state.creationTime}")
            if (state.isRestoring) {
                LinearProgressIndicator(progress = { state.progress })
            } else {
                Button(onClick = { viewModel.restoreFile(context = context) }) {
                    Text("Restore")
                }
            }
        } else {
            Text("File not selected or Bad file")
        }
    }
}