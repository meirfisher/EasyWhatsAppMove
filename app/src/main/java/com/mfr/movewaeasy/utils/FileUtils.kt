package com.mfr.movewaeasy.utils

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
    fun getWhatsAppFolder(): File {
        return File(
            Environment.getExternalStorageDirectory(),
            WHATSAPP_FOLDER_PATH
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
             val dateTime = LocalDateTime.parse(string.substring(7, string.length - 4), DATE_TIME_FORMAT)
             dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss"))
             } else {
             ""
         }
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
        return if (this < MB) {
            toStringKB()
        } else if (this < GB) {
            toStringMB()
        } else {
            toStringGB()
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
}