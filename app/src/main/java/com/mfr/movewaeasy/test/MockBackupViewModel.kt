package com.mfr.movewaeasy.test

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.math.log10

// Mock BackupViewModel and BackupState for preview purposes
// Replace with your actual ViewModel and State
class MockBackupViewModel {
    val backupState = kotlinx.coroutines.flow.MutableStateFlow(BackupState())
    fun startBackup() {
        backupState.value = backupState.value.copy(isCompressing = true, progress = 0.1f, filesCount = 100, fileOnProgress = 10)
    }
    fun cancelBackup() {
        backupState.value = backupState.value.copy(isCompressing = false, progress = 0f)
    }
    @OptIn(DelicateCoroutinesApi::class)
    fun calculateSize() {
        backupState.value = backupState.value.copy(isCalculatingSize = true, folderSize = 0)
        // Simulate calculation
        kotlinx.coroutines.GlobalScope.launch { // Use a proper scope in real app
            kotlinx.coroutines.delay(2000)
            backupState.value = backupState.value.copy(isCalculatingSize = false, folderSize = 1024L * 1024 * 500) // 500MB
        }
    }
    init {
        calculateSize()
    }
}

data class BackupState(
    val freeSpace: Long = 1024L * 1024 * 1024 * 2, // 2GB
    val folderSize: Long = 0L,
    val isCalculatingSize: Boolean = true,
    val isCompressing: Boolean = false,
    val readyToBackup: Boolean = false,
    val progress: Float = 0.0f,
    val filesCount: Int = 0,
    val fileOnProgress: Int = 0,
    val backupFilePath: String? = "WhatsApp/Media/image_2023.jpg",
    val errorMessage: String? = null
) {
    // Mock for preview
    fun Long.toStringSize(): String {
        if (this <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(this.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.1f %s", this / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }
}
// End of Mock Data