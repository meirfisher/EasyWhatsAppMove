package com.mfr.movewaeasy.viewmodels


import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfr.movewaeasy.R.string.whatsapp_folder_path
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
        val errorMessage: String? = null
    )
    private val _state = MutableStateFlow(BackupState())
    val backupState: StateFlow<BackupState> = _state
    // Paths for the folders
    private val whatsappPath = Environment.getExternalStorageDirectory().path +
            whatsapp_folder_path
    private val backupPath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOCUMENTS
    ).path + "/WhatsAppTransfer/backup.zip"

    init {
        _state.value = _state.value.copy(
            folderSize = getFolderSize(whatsappPath),
            freeSpace = getFreeSpace()
        )

    }


    fun startBackup() {
        viewModelScope.launch (Dispatchers.IO) {
            _state.value = _state.value.copy(isCompressing = true)
            compressFolder(
                sourcePath = whatsappPath,
                destinationPath = backupPath,
                onProgress = { progress ->
                    _state.value = _state.value.copy(progress = progress)
                }
            )
            _state.value = _state.value.copy(isCompressing = false)
        }
    }
}
