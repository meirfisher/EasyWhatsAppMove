package com.mfr.movewaeasy.viewmodels

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfr.movewaeasy.utils.FileUtils
import com.mfr.movewaeasy.utils.FileUtils.getWhatsAppFolder
import com.mfr.movewaeasy.utils.FileUtils.getWhatsAppPath
import com.mfr.movewaeasy.utils.ZipUtils
import com.mfr.movewaeasy.utils.ZipUtils.extractZip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestoreViewModel : ViewModel() {

    // Data classes for UI state
    data class RestoreState(
        val isFileSelected: Boolean = false,
        val fileName: String? = null,
        val filePath: String? = null, // Null when until file is selected
        val fileSize: Long = 0L,
        val creationTime: String? = null,
        val isWhatsappFolderFound: Boolean = false,
        val restoreDestination: String? = null,
        val progress: Float = 0f,
        val fileOnProgress: String? = null,
        val filesRestoredCount: Long = 0L,
        val filesTotalCount: Long? = 0L,
        val isRestoring: Boolean = false,
        val errorMessage: String? = null
    )
    // Other UI state variables
    private val _state = MutableStateFlow(RestoreState())
    val state: StateFlow<RestoreState> = _state.asStateFlow()

    private var restoreFileUri: Uri? = null

    private var restoreJob: Job? = null

    // Function to set and Update state with file details when selected.
    fun setBackupFile(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                restoreFileUri = uri
                uri.getDetails(context = context)

                if (!checkFileName()) {
                    Log.e("SetBackupUri", "Invalid file name")
                    _state.value = _state.value.copy(errorMessage = "Invalid file name")
                    return@launch
                }

                if (_state.value.fileSize <= 0) {
                    Log.e("SetBackupUri", "Invalid file size")
                    _state.value = _state.value.copy(errorMessage = "Invalid file size")
                    return@launch
                }
                val destPath = getWhatsAppPath()
                _state.value = _state.value.copy(
                    filePath = uri.path,
                    isFileSelected = true,
                    creationTime = FileUtils.getBackupFileTimestamp(_state.value.fileName),
                    filesTotalCount = ZipUtils.readManifastFromZip(uri, context),
                    isWhatsappFolderFound = destPath.first,
                    restoreDestination = destPath.second
                )
                Log.d("set Backup Uri", "Content details: ${_state.value}")
            } catch (e: Exception) {
                Log.e("set Backup Uri", "Error setting backup file: ${e.message}", e)
                _state.value = _state.value.copy(errorMessage = e.message)
            }
        }
    }

    // Function to start the restore process
    @OptIn(InternalCoroutinesApi::class)
    fun restoreFile(context: Context) {
        restoreJob = viewModelScope.launch(Dispatchers.IO) {
            val fileUri = restoreFileUri ?: return@launch // Exit if no file selected
            val fileSize = _state.value.fileSize
            val destPath = _state.value.restoreDestination ?: return@launch
            try {
                _state.value = _state.value.copy(isRestoring = true)
                // Extract the zip file
                extractZip(
                    context = context,
                    sourceFile = fileUri,
                    totalSize = fileSize,
                    destinationPath = destPath,
                    onProgress = { progress ->
                        _state.value = _state.value.copy(progress = progress)
                    },
                    fileCounter = { counter ->
                        _state.value = _state.value.copy(filesRestoredCount = counter)
                    },
                    filePath = { path ->
                        _state.value = _state.value.copy(fileOnProgress = path)
                    }
                )
            } catch (e: Exception) {
                Log.e("Restore", "Error restoring file: ${e.message}")
                restoreStops(false, e.message)
            }
        }
        restoreJob?.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true
        ) {
            restoreStops(true)
        }
    }

    fun cancelRestore() {
        restoreJob?.cancel()
        restoreJob = null
        restoreStops(false)

    }

    private fun restoreStops(inSuccess: Boolean, errorMessage: String? = null) {
        Log.d("Restore", "Restore stops. inSuccess: $inSuccess")
        _state.value = _state.value.copy(
            isRestoring = false,
            progress = 0f,
            errorMessage =  if (inSuccess) null else "Restore failed or was cancelled: $errorMessage"
        )
    }

    private fun Uri.getDetails(context: Context) {
        try {
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
        } catch (e: Exception) {
            Log.e("getDetails", "Error getting details: ${e.message}", e)
        }
    }

    // Function to validate if the file name is in the correct pattern of "backup_ddMMyy_HHmmss.zip"
    private fun checkFileName(): Boolean {
        val fileName = _state.value.fileName ?: return false
        val pattern = "backup_\\d{6}_\\d{6}.zip"
        return fileName.matches(pattern.toRegex())
    }
}
