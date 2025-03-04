package com.mfr.movewaeasy.viewmodels

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfr.movewaeasy.R.string.whatsapp_folder_path
import com.mfr.movewaeasy.utils.ZipUtils.extractZip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class RestoreViewModel : ViewModel() {

    // Data classes for UI state
    data class RestoreState(
        val isFileSelected: Boolean = false,
        val filePath: String? = null, // Null when until file is selected
        val fileSize: Long = 0L,
        val creationTime: Long = 0L, // Milliseconds since epoch
        val progress: Float = 0f,
        val isRestoring: Boolean = false,
        val errorMessage: String? = null
    )
    // Other UI state variables
    private val _state = MutableStateFlow(RestoreState())
    val state: StateFlow<RestoreState> = _state
    // Paths for the folders
    private val destPath = Environment.getExternalStorageDirectory().path +
            whatsapp_folder_path

    // Function to set and Update state with file details when selected.
    fun setBackupFile(filePath: String) {
        val file = File(filePath)
        Log.d("Restore", "File Selected: $filePath, file size: ${file.length()}, creation time: ${file.lastModified()}")
        _state.value = _state.value.copy(
            filePath = filePath,
            fileSize = file.length(),
            creationTime = file.lastModified()
        )
        _state.value = _state.value.copy(isFileSelected = true)
    }

    // Function to start the restore process
    fun restoreFile() {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = _state.value.filePath ?: return@launch // Exit if no file selected
            _state.value = _state.value.copy(isRestoring = true)
            // Perform the restore logic here
            extractZip(
                sourcePath = filePath,
                destinationPath = destPath,
                onProgress = { progress ->
                    _state.value = _state.value.copy(progress = progress)
                }
            )
            _state.value = _state.value.copy(isRestoring = false)
        }
    }
}