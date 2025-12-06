package com.cs407.fitfolio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.fitfolio.ui.theme.Google_Sans_Flex
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.ui.theme.Kudryashev_Regular
import com.cs407.fitfolio.ui.theme.LightPeachFuzz
import com.cs407.fitfolio.weather.WeatherApiResponse
import com.cs407.fitfolio.weather.WeatherUtils
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun WeatherCarousel(
    weatherData: WeatherApiResponse?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(75.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            weatherData != null -> {
                WeatherDataCarousel(weatherData = weatherData)
            }
            else -> {
                Text(
                    text = "Unable to load weather",
                    fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
//for calendar and outfits screens
@Composable
private fun WeatherDataCarousel(weatherData: WeatherApiResponse) {
    val today = LocalDate.now().toString()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(weatherData.daily.time) { index, date ->
            val isToday = date == today
            WeatherDayCard(
                date = date,
                tempMax = weatherData.daily.temperature_2m_max[index],
                tempMin = weatherData.daily.temperature_2m_min[index],
                weatherCode = weatherData.daily.weather_code[index],
                isToday = isToday
            )
        }
    }
}

//card for carousel
@Composable
private fun WeatherDayCard(
    date: String,
    tempMax: Double,
    tempMin: Double,
    weatherCode: Int,
    isToday: Boolean
) {
    val localDate = LocalDate.parse(date)
    val dayOfWeek = localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val month = localDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val dayOfMonth = localDate.dayOfMonth
    val cardBackgroundColor = LightPeachFuzz
    //weather cards
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(70.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$month $dayOfMonth",
                        fontSize = 14.sp,
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dayOfWeek,
                        fontSize = 14.sp,
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
                Text(
                    text = when {
                        isToday -> "Today"
                        localDate == LocalDate.now().plusDays(1) -> "Tomorrow"
                        else -> ""
                    },
                    fontSize = 14.sp,
                    fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = WeatherUtils.getWeatherIconResource(weatherCode)),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "${tempMax.toInt()}/${tempMin.toInt()} °F",
                    fontSize = 16.sp,
                    fontFamily = Kudryashev_Regular,
                    color = Color.Black
                )
            }
        }
    }
}

//for WardrobeScreen
@Composable
fun WeatherCard(
    weatherCode: Int,
    tempHigh: Int,
    tempLow: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .width(100.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(LightPeachFuzz),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Image(
                painter = painterResource(id = WeatherUtils.getWeatherIconResource(weatherCode)),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "$tempHigh/$tempLow°F",
                fontFamily = Kudryashev_Regular,
                color = Color.Black,
                fontSize = 12.sp
            )
        }
    }
}

//for Wardrobe again, similar to the carousel
@Composable
fun WeatherDataChip(weatherData: WeatherApiResponse?) {
    if (weatherData != null) {
        WeatherCard(
            weatherCode = weatherData.daily.weather_code[0],
            tempHigh = weatherData.daily.temperature_2m_max[0].toInt(),
            tempLow = weatherData.daily.temperature_2m_min[0].toInt()
        )
    } else {
        Box(
            modifier = Modifier
                .height(40.dp)
                .width(100.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "weather",
                fontFamily = Google_Sans_Flex,
                color = Color.DarkGray
            )
        }
    }
}
