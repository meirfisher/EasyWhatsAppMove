package com.mfr.movewaeasy.viewmodels

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfr.movewaeasy.R.string.whatsapp_folder_path
import com.mfr.movewaeasy.utils.ZipUtils.extractZip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RestoreViewModel : ViewModel() {

    // Data classes for UI state
    data class RestoreState(
        val isFileSelected: Boolean = false,
        val uri: Uri? = null,
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
    fun setBackupFile(uri: Uri) {

        _state.value = _state.value.copy(
            uri = uri,
            filePath = uri.path,
            fileSize = uri.toFile().length(),
            creationTime = uri.toFile().lastModified()
        )
        Log.d("Restore",
            "File Selected: ${_state.value.filePath}, " +
                    "file size: ${_state.value.fileSize}, " +
                    "creation time: ${
                        _state.value.uri?.
                        toFile()?.
                        lastModified()
                    }"
        )
        if (_state.value.fileSize <= 0) {
            _state.value = _state.value.copy(errorMessage = "Invalid file size")
            return
        }
        _state.value = _state.value.copy(isFileSelected = true)
    }

    // Function to start the restore process
    fun restoreFile(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val fileUri = _state.value.uri ?: return@launch // Exit if no file selected
            val fileSize = _state.value.fileSize
            _state.value = _state.value.copy(isRestoring = true)
            // Perform the restore logic here
            extractZip(
                context = context,
                sourceFile = fileUri,
                totalSize = fileSize,
                destinationPath = destPath,
                onProgress = { progress ->
                    _state.value = _state.value.copy(progress = progress)
                }
            )
            _state.value = _state.value.copy(isRestoring = false)
        }
    }
}