package com.devsrimanth.visionAi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.devsrimanth.visionAi.screens.CameraScreen
import com.devsrimanth.visionAi.ui.theme.SmartCameraAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCameraAITheme {
                AppNavigation()
            }
        }
    }
}
