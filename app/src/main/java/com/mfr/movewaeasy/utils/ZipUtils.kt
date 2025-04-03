package com.mfr.movewaeasy.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.mfr.movewaeasy.utils.FileUtils.getFolderSize
import com.mfr.movewaeasy.utils.FileUtils.isEligible
import com.mfr.movewaeasy.utils.FileUtils.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
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
        totalFilesCount: Long,
        onProgress: (Float) -> Unit,
        fileCounter: (Long) -> Unit,
        filePath: (String?) -> Unit
    ) {
        var processedBytes = 0f
        var counter = 0L
        try {
            val totalSize = getFolderSize(sourceDir).first
            if (totalSize == 0L) {
                Log.d("Zip", "Empty folder or doesn't exist ${sourceDir.path}")
                return
            } // Exit if the folder is empty or doesn't exist

            destinationFile.parentFile?.mkdirs()

            ZipOutputStream(BufferedOutputStream(FileOutputStream(destinationFile))).use { zipOut ->
                addManifestToZip(zipOut, totalFilesCount)
                sourceDir.walk().filter { !it.isHidden }.forEach { file ->
                    checkIfCancelled("Compressing folder")
                    if (file.isEligible()) {
                        val relativePath = file.relativeTo(sourceDir).path
                        zipOut.putNextEntry(ZipEntry(relativePath).apply {
                            method = ZipOutputStream.STORED
                            size = file.length()
                            crc = file.calculateCrc32()
                        })
                        // Copy file content to the zip entry
                        Log.d(
                            "Zip",
                            "Adding file: ${file.relativeTo(sourceDir).path}, size: ${file.size()} "
                        )
                        BufferedInputStream(file.inputStream()).use { input ->
                            val buffer = ByteArray(BUFFER_SIZE)
                            var bytesRead: Int

                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                checkIfCancelled("Compressing folder")
                                zipOut.write(buffer, 0, bytesRead)
                                processedBytes += bytesRead
                                val progress = (processedBytes / totalSize).coerceIn(0f, 1f)
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
            Log.e("compressFolder", "Error compressing folder: $e")

            if (destinationFile.exists()) {
                try {
                    val isDeleted = destinationFile.delete()
                    Log.d("compressFolder", "Deleted partial file: $isDeleted")
                }
                catch (e: Exception) {
                    Log.e("compressFolder", "Error deleting partial file: ${e.message}")
                }
            }
            throw e
        }
        Log.d(
            "compressFolder",
            "Folder compressed successfully with path: ${destinationFile.path}, " +
                    "size: ${destinationFile.length()}, " +
                    "files: $counter"
        )
    }


    // Function to unzip the backup file, with progress updates
    suspend fun extractZip(
        context: Context,
        sourceFile: Uri,
        totalSize: Long,
        destinationPath: String,
        onProgress: (Float) -> Unit,
        fileCounter: (Long) -> Unit,
        filePath: (String?) -> Unit
    ) {
        var processedBytes = 0L
        var processedFiles = 0L
        try {
            context.contentResolver.openInputStream(sourceFile)?.use { inputStream ->
                ZipInputStream(inputStream).use { zipIn ->
                    var entry: ZipEntry? = zipIn.nextEntry
                    while (entry != null) {
                        checkIfCancelled("Extracting zip file")
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
                                    checkIfCancelled("Extracting zip file")
                                    output.write(buffer, 0, bytesRead)
                                    processedBytes += bytesRead
                                    val progress = processedBytes.toFloat() / totalSize
                                    //Log.d("Zip", "Extract Progress: $progress")
                                    onProgress(progress)
                                }
                            }
                        }
                        zipIn.closeEntry()
                        processedFiles++
                        fileCounter(processedFiles)
                        filePath(destFile.relativeTo(File(destinationPath)).path)
                        Log.d("Zip", "Extracted file: ${destFile.name}")
                        entry = zipIn.nextEntry
                    }
                }
            } ?: throw Exception("Failed to open input stream for uri: $sourceFile")
        } catch (e: Exception) {
            Log.e("Zip", "Error extracting zip file", e)
            throw e
        }
    }

    // Check if the process is still active
    private suspend fun checkIfCancelled(operation: String) {
        if (!currentCoroutineContext().isActive) {
            Log.d("Zip", "$operation cancelled")
            throw Exception("$operation operation cancelled")
        }
    }

    private fun addManifestToZip(zipOut: ZipOutputStream, fileCount: Long) {
        try {
            val manifestContent = """
                FileCount: $fileCount
            """.trimIndent()
            val manifestEntry = ZipEntry("manifest.txt")
            manifestEntry.method = ZipEntry.STORED
            manifestEntry.size = manifestContent.length.toLong()
            manifestEntry.crc = CRC32().apply { update(manifestContent.toByteArray()) }.value
            zipOut.putNextEntry(manifestEntry)
            zipOut.write(manifestContent.toByteArray(charset = Charsets.UTF_8))
            zipOut.closeEntry()
            Log.d("addManifestToZip", "Manifest added to zip file")
        } catch (e: Exception) {
            Log.e("addManifestToZip", "Error adding manifest to zip file: ${e.message}")
            throw e
        }
    }

    suspend fun readManifastFromZip(zipUri: Uri, context: Context): Long? {
        Log.d("readManifastFromZip", "Reading manifest from zip file: $zipUri")
        var fileCount: Long? = null
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(zipUri)?.use { inputStream ->
                    ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                        val entry: ZipEntry? = zipIn.nextEntry
                        if (entry != null && entry.name == "manifest.txt") {
                            val manifestContent = zipIn.bufferedReader().use { it.readText() }
                            Log.d("readManifastFromZip", "Manifest content: $manifestContent")
                            val regex = Regex("FileCount: (\\d+)")
                            val matchResult = regex.find(manifestContent)
                            fileCount = matchResult?.groupValues?.get(1)?.toLongOrNull()
                            Log.d("readManifastFromZip", "File count: $fileCount")
                        }
                    }
                }
            }catch (e: Exception) {
                Log.e("readManifastFromZip", "Error reading manifest from zip file: ${e.message}", e)
                throw e
            }
        }

        if (fileCount == null) {
            Log.e("readManifastFromZip", "File count is null")
            throw Exception("File count is null")
        }

        Log.d("readManifastFromZip", "File count return: $fileCount")
        return fileCount
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
}
