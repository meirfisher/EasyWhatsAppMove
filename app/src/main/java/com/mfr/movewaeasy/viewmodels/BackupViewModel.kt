package com.mfr.movewaeasy.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfr.movewaeasy.utils.FileUtils
import com.mfr.movewaeasy.utils.FileUtils.getFolderSize
import com.mfr.movewaeasy.utils.FileUtils.getFreeSpace
import com.mfr.movewaeasy.utils.ZipUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BackupViewModel : ViewModel() {
    data class BackupState(
        val folderSize: Long = 0L, // Bytes
        val filesCount: Long = 0L,
        val fileOnProgress: Long = 0L,
        val backupFilePath: String? = null,
        val freeSpace: Long = 0L,
        val progress: Float = 0f, // 0 to 1
        val isCompressing: Boolean = false,
        val isCalculatingSize: Boolean = false,
        val readyToBackup: Boolean = false,
        val errorMessage: String? = null
    )
    private val _state = MutableStateFlow(BackupState())
    val backupState: StateFlow<BackupState> = _state
    // Paths for the folders
    private val sourceDir = FileUtils.getWhatsAppFolder().getOrThrow()
    private val backupFile = FileUtils.getDestinationBackupFile()
    private val GBYTE: Long = 1073741824

    // Job to track the backup process
    private var backupJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(
                freeSpace = getFreeSpace()
            )
            // Calculate the size of the WhatsApp folder
            sizeCalculating()
            Log.d("BackupViewModel.init", "Free Space: ${_state.value.freeSpace}, WhatsApp Folder Size: ${_state.value.folderSize}")
            // Check if backup folder is larger than the free space on the device - 1 GB extra free space for safety
            memoryChecks()
            Log.d("BackupViewModel.init", "Ready to backup: ${_state.value.readyToBackup}")
        }
    }


    @OptIn(InternalCoroutinesApi::class)
    fun startBackup() {

         backupJob = viewModelScope.launch (Dispatchers.IO) {
            _state.value = _state.value.copy(isCompressing = true)
             try {
                ZipUtils.compressFolder(
                    sourceDir = sourceDir,
                    destinationFile = backupFile,
                    totalFilesCount = _state.value.filesCount,
                    onProgress = { progress ->
                        _state.value = _state.value.copy(progress = progress)
                    },
                    fileCounter = { counter ->
                        _state.value = _state.value.copy(fileOnProgress = counter)
                    },
                    filePath = { path ->
                        _state.value = _state.value.copy(backupFilePath = path)
                    }
                )
             } catch (e: Exception) {
                 Log.e("Backup", "Error in backup process: ${e.message}", e)
                 processStops(false, e.message)
             }
        }
        backupJob?.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true
        ) {
            processStops(true)
            Log.d("Backup", "Backup job completed successfully")
        }
    }

    fun cancelBackup() {
        backupJob?.cancel()
        backupJob = null
        processStops(false)
    }

    private fun processStops(inSuccess: Boolean, errorMessage: String? = null) {
        _state.value = _state.value.copy(
            isCompressing = false,
            progress = 0f,
            errorMessage =  if (inSuccess) null else "Backup failed or was cancelled: $errorMessage"
        )
        if (!inSuccess && backupFile.exists()) {
            viewModelScope.launch(Dispatchers.IO) {
                backupFile.delete()
            }
        }
    }

    private fun sizeCalculating() {

        _state.value = _state.value.copy(isCalculatingSize = true)
        val (totalSize, fileCount) = getFolderSize(sourceDir)
        _state.value = _state.value.copy(
            folderSize = totalSize,
            filesCount = fileCount,
            isCalculatingSize = false,
        )
        Log.d("BackupViewModel.sizeCalculating", "Total Size: ${_state.value.folderSize}, File Count: ${_state.value.filesCount}")
    }

    private fun memoryChecks() {
        Log.d("BackupViewModel.memoryChecks", "Free Space: ${_state.value.freeSpace}, WhatsApp Folder Size: ${_state.value.folderSize}")

        if (_state.value.folderSize > _state.value.freeSpace - GBYTE) {
            _state.value = _state.value.copy(
                errorMessage = "Backup folder is larger than the free space on the device"
            )
            Log.e("Backup", "Backup folder is larger than the free space on the device")
        } else if (_state.value.folderSize <= 0L) {
            _state.value = _state.value.copy(
                errorMessage = "WhatsApp folder is empty"
            )
            Log.e("BackupViewModel.memoryChecks", "WhatsApp folder is empty: Size: ${_state.value.folderSize}")
        } else {
            _state.value = _state.value.copy(
                readyToBackup = true
            )
        }
    }
}
