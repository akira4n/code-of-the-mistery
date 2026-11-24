package com.example.codeofthemistery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.codeofthemistery.core.navigation.AppNavGraph
import com.example.codeofthemistery.core.theme.CodeOfTheMisteryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.parseColor("#0C0E12")),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.parseColor("#0C0E12"))
        )
        setContent {
            CodeOfTheMisteryTheme {
                AppNavGraph()
            }
        }
    }
}
