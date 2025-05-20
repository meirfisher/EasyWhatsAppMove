package com.mfr.movewaeasy

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mfr.movewaeasy.ui.theme.MoveWAEasyTheme

@Composable
fun EasyMoveApp(context: Context) {
    // Using MaterialTheme for overall styling.
    // You can customize the theme further in your app's Theme.kt file.
    MoveWAEasyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ScreenNavigation(context)
        }
    }
}