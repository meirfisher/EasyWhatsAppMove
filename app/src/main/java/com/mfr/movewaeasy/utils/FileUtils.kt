package com.mfr.movewaeasy.utils

import android.os.Environment
import android.os.StatFs
import com.mfr.movewaeasy.R.string.whatsapp_folder_path
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

object FileUtils {

    private const val GB = 1024f * 1024f * 1024f
    private val whatsappFolderPath = whatsapp_folder_path.toString()
    private const val BACKUP_FOLDER_PATH = "WhatsAppTransfer"
    private val DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")

    // Func to get the path to the WhatsApp folder
    fun getWhatsAppFolderPath(): File {
        return File(
            Environment.getExternalStorageDirectory(),
            whatsappFolderPath
        )
    }

    // Func to get the path to the destination backup file
    fun getDestinationBackupFile(): File {
        return File(
            Environment.getExternalStorageDirectory(),
            BACKUP_FOLDER_PATH +
                    File.separator +
                    getBackupFileName()
        )
    }

    /**
     * Generates a backup filename with the current date and time in "yyyyMMdd_HHmmss" format.
     * Example output: "backup_20250304_143022.zip"
     */
    private fun getBackupFileName(): String {
        val dateTime = LocalDateTime.now().format(DATE_TIME_FORMAT)
        return "backup_$dateTime.zip"
    }

    // Function to get the size of a folder in bytes
    fun getFolderSize(file: File): Long {
        return file
            .walkTopDown()
            .asSequence()
            .filter { it.isFile && !it.isHidden }
            .sumOf { it.length() }
    }

    // Function to get the free space on the device
    fun getFreeSpace(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.availableBytes
    }

    // Convert bytes to a human-readable format GB
    fun convertBytesToGB(bytes: Long): String {
        return "%.2f GB".format(bytes / GB)
    }
}