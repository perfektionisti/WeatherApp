package com.example.loppuprojektisovohj.network

import retrofit2.http.GET
import retrofit2.http.Query

// JSON-mallinnus vastaamaan OpenWeatherMapin palautusta
data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val name: String
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)

data class Weather(
    val id: Int, // Sään ID (tunniste)
    val main: String, // Pääkuvaus, esim. "Rain"
    val description: String, // Yksityiskohtaisempi kuvaus, esim. "light rain"
    val icon: String // Kuvakkeen tunniste, esim. "10d"
)

data class Wind(
    val speed: Double,
    val deg: Int // Tuulen suunta asteina
)

interface WeatherApi {

    // Haku kaupungin nimen perusteella
    @GET("weather")
    suspend fun getCityWeather(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): WeatherResponse

    // Haku koordinaattien perusteella
    @GET("weather")
    suspend fun getLocationWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): WeatherResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    val api: WeatherApi by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}
