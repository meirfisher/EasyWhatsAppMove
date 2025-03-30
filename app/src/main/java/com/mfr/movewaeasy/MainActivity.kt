package com.mfr.movewaeasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jakewharton.threetenabp.AndroidThreeTen

class MainActivity : ComponentActivity() {

    // Check if the app has the required permissions
 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContent {
            ScreenNavigation(context = this)
        }
    }
}
