package com.mfr.movewaeasy.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfr.movewaeasy.utils.FileUtils.getFolderSize
import com.mfr.movewaeasy.utils.FileUtils.getFreeSpace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BackupViewModel : ViewModel() {
    data class BackupState(
        val folderSize: Long = 0L, // Bytes
        val freeSpace: Long = 0L,
        val progress: Float = 0f, // 0 to 1
        val isCompressing: Boolean = false,
        val errorMessage: String? = null
    )
    private val _backupState = MutableStateFlow(BackupState())
    val backupState: StateFlow<BackupState> = _backupState

    init {
        val whatsappPath = "Android/media/com.whatsapp/WhatsApp"
        _backupState.value = _backupState.value.copy(
            folderSize = getFolderSize(whatsappPath),
            freeSpace = getFreeSpace()
        )

    }


    fun startBackup() {
        viewModelScope.launch {
            _backupState.value = _backupState.value.copy(isCompressing = true)
            // Compression logic with ZipUtils
        }
    }
}
