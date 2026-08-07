package com.example.notolyzard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notolyzard.navigation.NotoLyzardNavHost
import com.example.notolyzard.ui.theme.NotoLyzardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotoLyzardTheme {
                NotoLyzardNavHost()
            }
        }
    }
}
