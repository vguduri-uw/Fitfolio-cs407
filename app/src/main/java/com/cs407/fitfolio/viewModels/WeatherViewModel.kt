package com.cs407.fitfolio.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.fitfolio.weather.RetrofitInstance
import com.cs407.fitfolio.weather.WeatherApiResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class WeatherState(
    val weatherData: WeatherApiResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val locationPermissionGranted: Boolean = false
)

class WeatherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherState())
    val uiState = _uiState.asStateFlow()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun initializeLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun updateLocationPermission(granted: Boolean) {
        _uiState.value = _uiState.value.copy(locationPermissionGranted = granted)
        if (granted) {
            fetchWeatherForCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchWeatherForCurrentLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    val response = RetrofitInstance.weatherApi.getWeatherData(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    _uiState.value = _uiState.value.copy(
                        weatherData = response,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Unable to get location"
                    )
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to fetch weather data"
                )
            }
        }
    }
}