package com.mfr.movewaeasy.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.util.Log
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

object FileUtils {

    private const val KB = 1024f
    private const val MB = KB * KB
    private const val GB = MB * KB
    private const val WHATSAPP_FOLDER_PATH = "Android/media/com.whatsapp/WhatsApp"
    private const val BACKUP_FOLDER_PATH = "WhatsAppTransfer"
    private val DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("ddMMyy_HHmmss")

    // Func to get the path to the WhatsApp folder
    fun getWhatsAppFolder(): Result<File> {
        val path = File(
            Environment.getExternalStorageDirectory(),
            WHATSAPP_FOLDER_PATH
        )
        return if (path.exists() && path.isDirectory) {
            Result.success(path)
        } else {
            Result.failure(Exception("WhatsApp folder not found"))
        }
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
     * Generates a backup filename with the current date and time in "ddMMyy_HHmmss" format.
     * Example output: "backup_040325_143022.zip"
     */
    private fun getBackupFileName(): String {
        val dateTime = LocalDateTime.now().format(DATE_TIME_FORMAT)
        return "backup_$dateTime.zip"
    }


    /**
     * Extract the timestamp from backup filename("backup_040325_143022.zip") to simple date-time string.
     * Example output: "04/06/25 14:30:22"
     */
    fun getBackupFileTimestamp(string: String?): String {
        Log.d("getBackupFileTimestamp", "String: \"$string\"")
         return if (string != null) {
             try {
                 val dateTime = LocalDateTime.parse(string.substring(7, string.length - 4), DATE_TIME_FORMAT)
                 dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss"))
             } catch (e: Exception) {
                 Log.e("getBackupFileTimestamp", "Error in file name: ${e.message}")
                 ""
             }
             } else {
             ""
         }
    }

    /**
     * Calculates the total size and file count of a folder.
     * @param file The directory to analyze
     * @return Pair containing total size in bytes (first) and number of files (second)
     */
    fun getFolderSize(file: File): Pair<Long, Long> {
        require(file.isDirectory) {"Input must be a directory"}

        var fileCounter: Long = 0
        return Pair (
            file.walkTopDown()
                .filter { it.isFile && !it.isHidden }
                .onEach { fileCounter++ }
                .sumOf { it.length() },

            fileCounter
        )
    }

    // Function to get the free space on the device
    fun getFreeSpace(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.availableBytes
    }

    // Convert bytes to a human-readable format GB
    private fun Long.toStringGB(): String {
        return "%.2f GB".format(this / GB)
    }

    private fun Long.toStringMB(): String {
        return "%.2f MB".format(this / MB)
    }

    private fun Long.toStringKB(): String {
        return "%.2f KB".format(this / KB)
    }

    fun Long.toStringSize(): String {
        return when {
            this >= GB -> this.toStringGB()
            this >= MB -> this.toStringMB()
            this >= KB -> this.toStringKB()
            else -> "$this bytes"
        }
    }

    fun File.size(): String {
        return if (this.isFile) {
            this.length().toStringSize()
        } else {
            "Folder"
        }
    }

    // Checks if file is Eligible for backup
    fun File.isEligible(): Boolean {
        return this.isFile && !this.isHidden
    }

    // Get the prepended files count for the zip file from Uri
    fun Uri.getPrependedFilesCount(context: Context): Long {
        try {
            context.contentResolver.openInputStream(this)?.use { input ->
                val reader = input.bufferedReader()
                val line = reader.readLine() ?: return -1
                Log.d("getPrependedFilesCount", "Line: $line")
                return line.substringAfter("FileCount:").toLongOrNull() ?: -1
            }
        } catch (e: Exception) {
            Log.e("getPrependedFilesCount", "Error getting prepended files count: ${e.message}")
        }
        return -1
    }
}