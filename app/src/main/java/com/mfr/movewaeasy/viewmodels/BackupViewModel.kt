package com.mfr.movewaeasy.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BackupViewModel : ViewModel() {

    // Data class to hold the backup progress
    data class BackupState(
        val foderSize: Long = 0L, // Bytes
        val freeSpace: Long = 0L,
        val progress: Float = 0f, // 0 to 1
        val isCompressing: Boolean = false,
        val errorMessage: String? = null
    )
    private val _state = MutableStateFlow(BackupState())
    val state: StateFlow<BackupState> = _state

    init {
        val folderPath = "Android/media/com.whatsapp/WhatsApp"
        _state.value = _state.value.copy(
            foderSize = getFolderSize(folderPath),
            freeSpace = getFreeSpace()
        )
    }

    fun startBackup() {
        _state.value = _state.value.copy(isCompressing = true)
        // Start the backup process

    }
}