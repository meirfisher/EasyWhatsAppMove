package com.mfr.movewaeasy.ui.screens

import android.app.Activity
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
import com.mfr.movewaeasy.utils.PermissionUtils.requestPermissions

@Composable
fun PermissionScreen(navController: NavController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(stringResource(R.string.permission_request_text))
        Button(onClick = { requestPermissions(context as Activity) }) {
            Text(stringResource(R.string.permission_request_button))
        }
    }
}