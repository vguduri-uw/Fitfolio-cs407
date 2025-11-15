package com.cs407.fitfolio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.ui.components.WeatherCarousel
import com.cs407.fitfolio.ui.modals.InformationModal
import com.cs407.fitfolio.ui.modals.SettingsModal
import com.cs407.fitfolio.ui.viewModels.WeatherViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToWardrobeScreen: () -> Unit,
    onNavigateToAddScreen: () -> Unit,
    onNavigateToClosetScreen: () -> Unit,
    weatherViewModel: WeatherViewModel
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showModal by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val customGrey = Color(0xFFE0E0E0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 16.dp)
        ) {
            // Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                var showInformation by remember { mutableStateOf(false) }

                Spacer(modifier = Modifier.size(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "My Calendar", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = { showInformation = true }) { // todo: add info onClick lambda
                        Icon(Icons.Outlined.Info,
                            contentDescription = "Information",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                if (showInformation) {
                    InformationModal(onDismiss = { showInformation = false}, screen = "My Calendar")
                }
            }

            Spacer(modifier = Modifier.size(24.dp))

            // Search
            //TODO: search for item and see what dates it was worn
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            // Weather
            Spacer(modifier = Modifier.size(18.dp))

            WeatherCarousel(
                weatherData = weatherState.weatherData,
                isLoading = weatherState.isLoading
            )

            Spacer(modifier = Modifier.size(18.dp))

            // Calendar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = customGrey
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Month header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
                        }

                        Text(
                            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                            style = MaterialTheme.typography.titleLarge
                        )

                        IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
                        }
                    }

                    // week header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // grid
                    CalendarGrid(
                        currentMonth = currentMonth,
                        onDateClick = { date ->
                            selectedDate = date
                            showModal = true
                        },
                        customGrey = customGrey
                    )
                }
            }
        }

        // Settings
        IconButton(
            onClick = { showSettings = true },
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(36.dp)
            )
        }

        // modal
        if (showSettings) {
            SettingsModal(onDismiss = { showSettings = false })
        }
    }

    // Outfit Modal
    //TODO: make the modal display outfit for the day
    if (showModal && selectedDate != null) {
        Dialog(onDismissRequest = { showModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "test: ${selectedDate!!.format(
                            java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy")
                        )}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    onDateClick: (LocalDate) -> Unit,
    customGrey: Color
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()
    val today = LocalDate.now()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.heightIn(max = 400.dp)
    ) {
        items(firstDayOfWeek) {
            Spacer(modifier = Modifier.aspectRatio(1f))
        }

        // Days of the month
        items(daysInMonth) { index ->
            val day = index + 1
            val date = currentMonth.atDay(day)
            val isToday = date == today

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isToday) MaterialTheme.colorScheme.primaryContainer
                        else Color.White
                    )
                    .clickable { onDateClick(date) }
                    .padding(6.dp)
            ) {
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer
                    else Color.Black,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}