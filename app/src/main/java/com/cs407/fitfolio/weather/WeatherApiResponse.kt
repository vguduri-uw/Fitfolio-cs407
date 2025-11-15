package com.cs407.fitfolio.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherApiResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val daily: DailyData
)

data class DailyData(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val precipitation_probability_max: List<Int>,
    val weather_code: List<Int>
)

interface WeatherApiService {
    @GET(value = "v1/forecast")
    suspend fun getWeatherData(
        @Query(value = "latitude") latitude: Double,
        @Query(value = "longitude") longitude: Double,
        @Query(value = "daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_probability_max,weather_code",
        @Query(value = "temperature_unit") temperatureUnit: String = "fahrenheit",
        @Query(value = "timezone") timezone: String = "auto",
        @Query(value = "forecast_days") forecastDays: Int = 7
    ): WeatherApiResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.open-meteo.com/"

    val weatherApi: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}