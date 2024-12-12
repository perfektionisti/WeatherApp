package com.example.loppuprojektisovohj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.loppuprojektisovohj.screens.AboutScreen
import com.example.loppuprojektisovohj.screens.WeatherScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                Surface {
                    NavHost(navController = navController, startDestination = "weather") {
                        composable("weather") { WeatherScreen(navController) }
                        composable("about") { AboutScreen(navController) }
                    }
                }
            }
        }
    }
}


