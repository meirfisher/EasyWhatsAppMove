package com.mfr.movewaeasy.preview

import androidx.lifecycle.ViewModel
import com.mfr.movewaeasy.viewmodels.BackupViewModel.BackupState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//Mock implementation of the BackupViewModel
class MockBackupViewModel(initialState: BackupState) : ViewModel() {

    // Override the backupState to return a MutableStateFlow with the initial state
    private val _backupState = MutableStateFlow(initialState)
    val backupState: StateFlow<BackupState> = _backupState

    // Provide sample implementations or leave them as no-ops for the functions used in the UI
    fun startBackup() {
        _backupState.value = _backupState.value.copy(
            isCompressing = true,
            progress = 0.5f,
            fileOnProgress = 10,
            backupFilePath = "path/to/file"
        )
    }

    fun cancelBackup() {
        _backupState.value = _backupState.value.copy(
            isCompressing = false,
            progress = 0f,
            fileOnProgress = 0,
            backupFilePath = null,
            errorMessage = "Backup cancelled"
        )
    }
}

// Define some sample states for easy access in previews
object SampleBackupStates {
    val Initial = BackupState(
        freeSpace = 100L,
    )
}