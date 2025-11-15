package com.cs407.fitfolio.weather

import com.cs407.fitfolio.R

object WeatherUtils {
    fun getWeatherIconResource(weatherCode: Int): Int {
        return when (weatherCode) {
            0 -> R.drawable.sunny
            1, 2, 3 -> R.drawable.partly_cloudy
            45, 48 -> R.drawable.foggy
            51, 53, 55, 56, 57 -> R.drawable.drizzle
            61, 63, 65, 66, 67, 80, 81, 82 -> R.drawable.rainy
            71, 73, 75, 77, 85, 86 -> R.drawable.snowy
            95, 96, 99 -> R.drawable.thunderstorm
            else -> R.drawable.partly_cloudy
        }
    }
}