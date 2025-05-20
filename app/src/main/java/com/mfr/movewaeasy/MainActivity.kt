package com.mfr.movewaeasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jakewharton.threetenabp.AndroidThreeTen

class MainActivity : ComponentActivity() {
 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeThreeTen()
        setContent {
            EasyMoveApp(context = this)
        }
    }

    private fun initializeThreeTen() {
        try {
            AndroidThreeTen.init(this)
        } catch (e: Exception) {
            // Log error but don't crash; ThreeTenABP is critical for date-time operations
            e.printStackTrace()
        }
    }
}
