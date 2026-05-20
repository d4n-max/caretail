package com.caretail.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.caretail.app.ui.navigation.CareTailApp
import com.caretail.app.ui.theme.CareTailTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CareTailTheme {
                CareTailApp()
            }
        }
    }
}
