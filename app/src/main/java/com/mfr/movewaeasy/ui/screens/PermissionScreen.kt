package com.mfr.movewaeasy.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mfr.movewaeasy.R
import com.mfr.movewaeasy.utils.PermissionUtils.hasPermissions
import com.mfr.movewaeasy.utils.PermissionUtils.isAndroid11OrAbove
import com.mfr.movewaeasy.utils.PermissionUtils.readWritePermission
import com.mfr.movewaeasy.utils.PermissionUtils.requestManageExternalStoragePermission

@Composable
fun PermissionScreen(navController: NavController) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val areGranted = permissionsMap.values.all { it }
        if (areGranted || hasPermissions(context)) {
            navController.navigate("main") {
                popUpTo("permission") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(stringResource(R.string.permission_request_text))
        if (isAndroid11OrAbove()) {
            Button(onClick = { requestManageExternalStoragePermission(context as Activity) }) {
                Text(stringResource(R.string.permission_request_button))
            }
            Text("android 10 bla bla TODO")
        } else {
            Button(onClick = { permissionLauncher.launch(readWritePermission) }) {
                Text(stringResource(R.string.permission_request_button))
            }
        }
    }
}