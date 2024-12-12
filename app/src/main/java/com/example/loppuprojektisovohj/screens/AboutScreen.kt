package com.example.loppuprojektisovohj.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.loppuprojektisovohj.R

@Composable
fun AboutScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.api_info))
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val openUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://openweathermap.org/"))
                navController.context.startActivity(openUrlIntent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.visit_openweathermap))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("weather") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.back_to_weather))
        }
    }
}

