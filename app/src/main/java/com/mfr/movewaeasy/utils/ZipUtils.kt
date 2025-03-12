package com.mfr.movewaeasy.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.mfr.movewaeasy.utils.FileUtils.getFolderSize
import com.mfr.movewaeasy.utils.FileUtils.isEligible
import com.mfr.movewaeasy.utils.FileUtils.size
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


object ZipUtils {

    // Buffer size 16KB
    private const val BUFFER_SIZE = 16384

    // Function to compress a folder and save it to a zip file, with progress updates
    suspend fun compressFolder(
        sourceDir: File,
        destinationFile: File,
        onProgress: (Float) -> Unit,
        fileCounter: (Long) -> Unit,
        filePath: (String?) -> Unit
    ) {
        try {
            val totalSize = getFolderSize(sourceDir).first
            if (totalSize == 0L) {
                Log.d("Zip", "Empty folder or doesn't exist ${sourceDir.path}")
                return
            } // Exit if the folder is empty or doesn't exist

            val destFile = destinationFile
            destFile.parentFile?.mkdirs()

            var processedBytes = 0f
            var counter = 0L
            ZipOutputStream(BufferedOutputStream(FileOutputStream(destFile))).use { zipOut ->
                sourceDir.walk().forEach { file ->
                    if (!currentCoroutineContext().isActive) {
                        Log.d("Zip", "Cancelled")
                        throw Exception("Backup operation cancelled")
                    }
                    if (file.isEligible()) {
                        val relativePath = file.relativeTo(sourceDir).path
                        zipOut.putNextEntry(ZipEntry(relativePath).apply {
                            method = ZipOutputStream.STORED
                            size = file.length()
                            crc = file.calculateCrc32()
                        })
                        // Copy file content to the zip entry
                        Log.d("Zip", "Adding file: ${file.relativeTo(sourceDir).path}, size: ${file.size()} ")
                        BufferedInputStream(file.inputStream()).use { input ->
                            val buffer = ByteArray(BUFFER_SIZE)
                            var bytesRead: Int
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                if (!currentCoroutineContext().isActive) {
                                    Log.d("Zip", "Cancelled")
                                    throw Exception("Backup operation cancelled")
                                }
                                zipOut.write(buffer, 0, bytesRead)
                                processedBytes += bytesRead
                                val progress = (processedBytes / totalSize).coerceIn(0f, 1f)
                                //Log.d("Zip", "Progress: $progress")
                                onProgress(progress)
                            }
                        }
                        zipOut.closeEntry()
                        counter++
                        fileCounter(counter)
                        filePath(file.relativeTo(sourceDir).path)
                        Log.d("Zip", "Added file: ${file.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("compressFolder", "Error compressing folder: ${e.message}")
            if (destinationFile.exists()) {
                destinationFile.delete()
            }
            throw e
        }
    }

    /*
     * Calculate the CRC32 before adding the file to the ZIP
     */
    private fun File.calculateCrc32(): Long {
        val crc = CRC32()
        BufferedInputStream(FileInputStream(this)).use { fis ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                crc.update(buffer, 0, bytesRead)
            }
        }
        return crc.value
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
                                val buffer = ByteArray(BUFFER_SIZE)
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