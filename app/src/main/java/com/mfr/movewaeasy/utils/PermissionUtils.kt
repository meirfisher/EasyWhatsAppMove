package com.mfr.movewaeasy.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

object PermissionUtils {
    private val readWritePermission = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private const val PERMISSION_REQUEST_CODE = 100

    /*
    / Function to check if the app has the required permissions
     */
    fun hasPermissions(context: Context): Boolean {
        return if (isAndroid11OrAbove()) {
            Environment.isExternalStorageManager()
        } else {
            // For versions below Android 11, implement your normal permission checks here
            hasReadWritePermission(context)
        }
    }

    /*
     * Function to check if the app has the read and write permissions
     * for android < 11
     */
    private fun hasReadWritePermission(context: Context): Boolean {
        return readWritePermission.all {
            context.checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    /*
     * Function to request the required permissions for the app
     */
    fun requestPermissions(activity: Activity) {
        if (isAndroid11OrAbove()) {
            requestManageExternalStoragePermission(activity)
        } else {
            requestReadWritePermission(activity)
        }
    }

    /*
     * Function to request the required permissions
     * For Android 11 and above
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun requestManageExternalStoragePermission(activity: Activity) {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse(
                String.format(
                    "package:%s",
                    activity.packageName
                )
            )
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            val fallBackIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            activity.startActivity(fallBackIntent)
        }
    }

    /*
     * Function to request the Read&Write permissions
     * For Android 10 and below
     */
    private fun requestReadWritePermission(activity: Activity) {
        try {
            ActivityCompat.requestPermissions(
                activity,
                readWritePermission,
                PERMISSION_REQUEST_CODE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /*
     * Function to check android version
     */
    private fun isAndroid11OrAbove(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}

