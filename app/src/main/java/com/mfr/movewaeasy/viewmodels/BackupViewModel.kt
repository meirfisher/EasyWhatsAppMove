package com.mfr.movewaeasy.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfr.movewaeasy.utils.FileUtils
import com.mfr.movewaeasy.utils.FileUtils.getFolderSize
import com.mfr.movewaeasy.utils.FileUtils.getFreeSpace
import com.mfr.movewaeasy.utils.ZipUtils.compressFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BackupViewModel : ViewModel() {
    data class BackupState(
        val folderSize: Long = 0L, // Bytes
        val freeSpace: Long = 0L,
        val progress: Float = 0f, // 0 to 1
        val isCompressing: Boolean = false,
        val isCalculatingSize: Boolean = false,
        val errorMessage: String? = null
    )
    private val _state = MutableStateFlow(BackupState())
    val backupState: StateFlow<BackupState> = _state
    // Paths for the folders
    private val sourceDir = FileUtils.getWhatsAppFolder()
    private val backupFile = FileUtils.getDestinationBackupFile()

    init {
        _state.value = _state.value.copy(
            freeSpace = getFreeSpace(),
            isCalculatingSize = true
        )
        viewModelScope.launch (Dispatchers.IO) {
            _state.value = _state.value.copy(
                folderSize = getFolderSize(sourceDir),
                isCalculatingSize = false
            )
        }
    }


    fun startBackup() {
         viewModelScope.launch (Dispatchers.IO) {
            _state.value = _state.value.copy(isCompressing = true)
            compressFolder(
                sourceDir = sourceDir,
                destinationFile = backupFile,
                onProgress = { progress ->
                    _state.value = _state.value.copy(progress = progress)
                }
            )
            _state.value = _state.value.copy(isCompressing = false)
        }
    }
}
