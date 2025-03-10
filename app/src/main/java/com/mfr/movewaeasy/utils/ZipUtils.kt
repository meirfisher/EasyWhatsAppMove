package com.mfr.movewaeasy.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import com.mfr.movewaeasy.utils.FileUtils.getFolderSize
import com.mfr.movewaeasy.utils.FileUtils.isEligible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.util.zip.ZipInputStream


object ZipUtils {

    // Function to compress a folder and save it to a zip file, with progress updates
    fun compressFolder(sourceDir: File, destinationFile: File, onProgress: (Float) -> Unit) {
        val totalSize = getFolderSize(sourceDir)
        if (totalSize == 0L) {
            Log.d("Zip", "Empty folder or doesn't exist ${sourceDir.path}")
            return
        } // Exit if the folder is empty or doesn't exist

        val destFile = destinationFile
        destFile.parentFile?.mkdirs()

        var processedBytes = 0L
        ZipOutputStream(FileOutputStream(destFile)).use { zipOut ->
            sourceDir.walk().forEach { file ->
                if (file.isEligible()) {
                    val relativePath = file.relativeTo(sourceDir).path
                    zipOut.putNextEntry(ZipEntry(relativePath))
                    // Copy file content to the zip entry
                    file.inputStream().use { input ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } > 0) {
                            zipOut.write(buffer, 0, bytesRead)
                        }
                    }
                    processedBytes += file.length()
                    val progress = processedBytes.toFloat() / totalSize
                    Log.d("Zip", "Progress: $progress")
                    onProgress(progress)
                    zipOut.closeEntry()
                }
            }
        }
    }

    /*
     * File extension function to compress a folder and save it to a zip file, with progress updates
     */
    suspend fun File.compressFolderTo(destinationFile: File, onProgress: (Float) -> Unit) = withContext(Dispatchers.IO) {
        val totalSize = getFolderSize(this@compressFolderTo)
        if (totalSize == 0L) {
            Log.d("Zip", "Empty folder or doesn't exist ${this@compressFolderTo.path}")
            return@withContext // Exit if the folder is empty or doesn't exist
        }
        // If the destination file doesn't exist, create it
        destinationFile.parentFile?.mkdirs()

        var processedBytes = 0L
        ZipOutputStream(FileOutputStream(destinationFile)).use { zipOut ->
            this@compressFolderTo.walk().forEach { file ->
                if (file.isEligible()) {
                    val relativePath = file.relativeTo(this@compressFolderTo).path
                    zipOut.putNextEntry(ZipEntry(relativePath))
                    // Copy file content to the zip entry
                    file.inputStream().use { input ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } > 0) {
                            zipOut.write(buffer, 0, bytesRead)
                        }
                    }
                    processedBytes += file.length()
                    val progress = processedBytes.toFloat() / totalSize
                    Log.d("Zip", "Progress: $progress")
                    onProgress(progress)
                    zipOut.closeEntry()
                }
            }
        }
    }

    // Function to unzip the backup file, with progress updates
    fun extractZip(context: Context, sourceFile: Uri, totalSize: Long, destinationPath: String, onProgress: (Float) -> Unit) {
        var processedBytes = 0L
        try {
            context.contentResolver.openInputStream(sourceFile)?.use { inputStream ->
                ZipInputStream(inputStream).use { zipIn ->
                    var entry: ZipEntry? = zipIn.nextEntry
                    while (entry != null) {
                        val destFile = File(destinationPath, entry.name)
                        if (entry.isDirectory) {
                            destFile.mkdirs()
                        } else {
                            // Make sure parent directories exist
                            destFile.parentFile?.mkdirs()
                            // Write the file content to the destination
                            FileOutputStream(destFile).use { output ->
                                val buffer = ByteArray(1024)
                                var bytesRead: Int
                                while (zipIn.read(buffer).also { bytesRead = it } > 0) {
                                    output.write(buffer, 0, bytesRead)
                                    processedBytes += bytesRead
                                    val progress = processedBytes.toFloat() / totalSize
                                    Log.d("Zip", "Extract Progress: $progress")
                                    onProgress(progress)
                                }
                            }
                        }

                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                    }
                }
            } ?: throw Exception("Failed to open input stream for uri: $sourceFile")
        } catch (e: Exception) {
            Log.e("Zip", "Error extracting zip file", e)
            throw e
        }
    }
}