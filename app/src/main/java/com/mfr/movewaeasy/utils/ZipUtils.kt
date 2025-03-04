package com.mfr.movewaeasy.utils

import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import com.mfr.movewaeasy.utils.FileUtils.getFolderSize
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
                if (file.isFile) {
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

    // Function to unzip the backup file, with progress updates
    fun extractZip(sourcePath: String, destinationPath: String, onProgress: (Float) -> Unit) {
        val zipFile = File(sourcePath)
        val totalSize = zipFile.length()
        var processedBytes = 0L
        ZipInputStream(FileInputStream(zipFile)).use { zipIn ->
            var entry: ZipEntry? = zipIn.nextEntry
            while (entry != null) {
                val destFile = File(destinationPath, entry.name)
                if (entry.isDirectory) {
                    destFile.mkdirs()
                } else {
                    destFile.parentFile?.mkdirs()
                    FileOutputStream(destFile).use { output ->
                        zipIn.copyTo(output)
                    }
                }
                processedBytes += entry.size
                val progress = processedBytes.toFloat() / totalSize
                Log.d("Zip", "Extract Progress: $progress")
                onProgress(progress)
                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
        }
    }
}