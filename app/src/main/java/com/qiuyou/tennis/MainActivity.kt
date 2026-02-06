package com.qiuyou.tennis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.qiuyou.tennis.presentation.navigation.TennisNavHost
import com.qiuyou.tennis.presentation.theme.TennisAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.e("TennisApp", "MainActivity: onCreate started")
        
        try {
            // enableEdgeToEdge() // Commented out for debugging
            
            setContent {
                TennisAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TennisNavHost()
                }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("TennisApp", "Crash in MainActivity onCreate", e)
        }
    }
}
