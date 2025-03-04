package com.mfr.movewaeasy.utils

import android.os.Environment
import android.os.StatFs
import java.io.File

object FileUtils {

    // Function to get the size of a folder in bytes
    fun getFolderSize(path: String): Long {
        return File(path)
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
}