package com.mfr.movewaeasy.viewmodels

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfr.movewaeasy.utils.FileUtils.getWhatsAppFolder
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
        val uri: Uri? = null,
        val fileName: String? = null,
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

    // Function to set and Update state with file details when selected.
    fun setBackupFile(uri: Uri, context: Context) {
        _state.value = _state.value.copy(
            uri = uri,
            filePath = uri.path,
        )
        uri.getDetails(context = context)

        Log.d("Restore",
            "File Selected: ${_state.value.filePath}, " +
                    "file size: ${_state.value.fileSize}, "
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
            val destPath = getWhatsAppFolder().absolutePath
            _state.value = _state.value.copy(isRestoring = true)
            // Extract the zip file
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

    private fun Uri.getDetails(context: Context) {

        context.contentResolver.query(
            this,
            arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
            null,
            null,
            null
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                _state.value = _state.value.copy(
                    fileName = if (nameIndex != -1) cursor.getString(nameIndex) else null,
                    fileSize = if (sizeIndex != -1) cursor.getLong(sizeIndex) else 0L
                )
            }
        }
        Log.d("Uri", "Content Details: ${_state.value}")
    }
}