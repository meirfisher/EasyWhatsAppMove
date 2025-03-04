package com.mfr.movewaeasy.utils

import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import com.mfr.movewaeasy.utils.FileUtils.getFolderSize


object ZipUtils {

    fun compressFolder(sourcePath: String, destinationPath: String, onProgress: (Float) -> Unit) {
        val sourceFolder = File(sourcePath)
        val totalSize = getFolderSize(sourcePath)
        if (totalSize == 0L) {
            Log.d("Zip", "Empty folder or doesn't exist $sourcePath")
            return
        } // Exit if the folder is empty or doesn't exist

        val destFile = File(destinationPath)
        destFile.parentFile?.mkdirs()

        var processedBytes = 0L
        ZipOutputStream(FileOutputStream(destFile)).use { zipOut ->
            sourceFolder.walk().forEach { file ->
                if (file.isFile) {
                    val relativePath = file.relativeTo(sourceFolder).path
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
}