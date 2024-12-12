package com.example.loppuprojektisovohj.screens

import androidx.compose.ui.text.input.ImeAction
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.loppuprojektisovohj.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.loppuprojektisovohj.network.RetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var cityName by remember {
        mutableStateOf(sharedPreferences.getString("last_city", "") ?: "")
    }
    var latLon by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var weatherInfo by remember { mutableStateOf<WeatherData?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                fetchLocation(fusedLocationClient, context) { location ->
                    latLon = location
                    fetchWeatherByLocation(location, context, coroutineScope, onWeatherFetched = { weather ->
                        weatherInfo = weather
                        errorMessage = null
                    }, onError = { error ->
                        errorMessage = error
                    })
                }
            } else {
                errorMessage = context.getString(R.string.location_permission_denied)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = cityName,
            onValueChange = { cityName = it },
            label = { Text(stringResource(R.string.enter_city)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    coroutineScope.launch {
                        try {
                            val response = withContext(Dispatchers.IO) {
                                RetrofitInstance.api.getCityWeather(
                                    city = cityName,
                                    apiKey = "1241c96f889d7c99bf061bbac05dba58"
                                )
                            }
                            val localizedDescription = getLocalizedDescription(
                                response.weather.firstOrNull()?.description.orEmpty(),
                                context
                            )
                            weatherInfo = WeatherData(
                                temperature = response.main.temp,
                                feelsLike = response.main.feels_like,
                                windSpeed = response.wind.speed,
                                description = localizedDescription,
                                iconUrl = "https://openweathermap.org/img/wn/${response.weather.firstOrNull()?.icon}@2x.png"
                            )
                            errorMessage = null

                            // Tallenna viimeksi haettu kaupunki
                            with(sharedPreferences.edit()) {
                                putString("last_city", cityName)
                                apply()
                            }
                        } catch (e: Exception) {
                            errorMessage = context.getString(R.string.error_fetching_weather)
                            weatherInfo = null
                        }
                    }
                }
            )
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            RetrofitInstance.api.getCityWeather(
                                city = cityName,
                                apiKey = "1241c96f889d7c99bf061bbac05dba58"
                            )
                        }
                        val localizedDescription = getLocalizedDescription(
                            response.weather.firstOrNull()?.description.orEmpty(),
                            context
                        )
                        weatherInfo = WeatherData(
                            temperature = response.main.temp,
                            feelsLike = response.main.feels_like,
                            windSpeed = response.wind.speed,
                            description = localizedDescription,
                            iconUrl = "https://openweathermap.org/img/wn/${response.weather.firstOrNull()?.icon}@2x.png"
                        )
                        errorMessage = null

                        // Tallenna viimeksi haettu kaupunki
                        with(sharedPreferences.edit()) {
                            putString("last_city", cityName)
                            apply()
                        }
                    } catch (e: Exception) {
                        errorMessage = context.getString(R.string.error_fetching_weather)
                        weatherInfo = null
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.fetch_weather))
        }

        Button(
            onClick = {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fetchLocation(fusedLocationClient, context) { location ->
                        latLon = location
                        fetchWeatherByLocation(location, context, coroutineScope, onWeatherFetched = { weather ->
                            weatherInfo = weather
                            errorMessage = null
                        }, onError = { error ->
                            errorMessage = error
                        })
                    }
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.fetch_location_weather))
        }

        weatherInfo?.let { weather ->
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberImagePainter(weather.iconUrl),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Text("${stringResource(R.string.temperature)}: ${weather.temperature}°C")
            Text("${stringResource(R.string.feels_like)}: ${weather.feelsLike}°C")
            Text("${stringResource(R.string.wind_speed)}: ${weather.windSpeed} m/s")
            Text("${stringResource(R.string.weather_description)}: ${weather.description}")
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("about") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.about_app))
        }
    }
}

private fun fetchLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationReceived: (Pair<Double, Double>) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(location.latitude to location.longitude)
            } else {
                println("Location is null")
            }
        }.addOnFailureListener { exception ->
            println("Failed to fetch location: ${exception.message}")
        }
    }
}

private fun fetchWeatherByLocation(
    location: Pair<Double, Double>,
    context: Context,
    coroutineScope: CoroutineScope,
    onWeatherFetched: (WeatherData) -> Unit,
    onError: (String) -> Unit
) {
    coroutineScope.launch {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.api.getLocationWeather(
                    lat = location.first,
                    lon = location.second,
                    apiKey = "1241c96f889d7c99bf061bbac05dba58"
                )
            }
            val localizedDescription = getLocalizedDescription(
                response.weather.firstOrNull()?.description.orEmpty(),
                context
            )
            onWeatherFetched(
                WeatherData(
                    temperature = response.main.temp,
                    feelsLike = response.main.feels_like,
                    windSpeed = response.wind.speed,
                    description = localizedDescription,
                    iconUrl = "https://openweathermap.org/img/wn/${response.weather.firstOrNull()?.icon}@2x.png"
                )
            )
        } catch (e: Exception) {
            onError(context.getString(R.string.error_fetching_weather))
        }
    }
}

private fun getLocalizedDescription(description: String, context: Context): String {
    val key = description.lowercase().replace(" ", "_")
    val resId = context.resources.getIdentifier(key, "string", context.packageName)
    return if (resId != 0) context.getString(resId) else description
}

data class WeatherData(
    val temperature: Double,
    val feelsLike: Double,
    val windSpeed: Double,
    val description: String,
    val iconUrl: String
)

