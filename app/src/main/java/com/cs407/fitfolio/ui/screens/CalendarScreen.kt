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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.cs407.fitfolio.data.OutfitEntry
import com.cs407.fitfolio.ui.components.WeatherCarousel
import com.cs407.fitfolio.ui.modals.InformationModal
import com.cs407.fitfolio.ui.modals.SettingsModal
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import com.cs407.fitfolio.viewModels.WeatherViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.material3.ExperimentalMaterial3Api
import com.cs407.fitfolio.ui.modals.OutfitModal
import com.cs407.fitfolio.viewModels.ClosetViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.res.painterResource
import com.cs407.fitfolio.R
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp

@Composable
fun CalendarScreen(
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToWardrobeScreen: () -> Unit,
    onNavigateToAddScreen: () -> Unit,
    onNavigateToClosetScreen: () -> Unit,
    onNavigateToSignInScreen: () -> Unit,
    weatherViewModel: WeatherViewModel,
    outfitsViewModel: OutfitsViewModel,
    closetViewModel: ClosetViewModel
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showModal by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val customGrey = Color(0xFFE0E0E0)
    var scheduledDates by remember { mutableStateOf<Set<LocalDate>>(emptySet()) }
    val scope = rememberCoroutineScope()
    var showSearchResults by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<Map<LocalDate, List<OutfitEntry>>>(emptyMap()) }

    // Load scheduled dates when screen appears or month changes
    LaunchedEffect(currentMonth) {
        scope.launch {
            val dates = outfitsViewModel.getAllScheduledDates()
            scheduledDates = dates.map { timestamp ->
                LocalDate.ofEpochDay(timestamp / (24 * 60 * 60 * 1000))
            }.toSet()
        }
    }

    fun performSearch(query: String) {
        if (query.isBlank()) {
            showSearchResults = false
            searchResults = emptyMap()
            return
        }

        //get outfits schedule
        scope.launch {
            val results = mutableMapOf<LocalDate, List<OutfitEntry>>()
            val allDates = outfitsViewModel.getAllScheduledDates()
            for (timestamp in allDates) {
                val date = LocalDate.ofEpochDay(timestamp / (24 * 60 * 60 * 1000))
                val outfits = outfitsViewModel.getOutfitsForDate(timestamp)

                val matchingOutfits = outfits.filter { outfit ->
                    outfit.outfitName.contains(query, ignoreCase = true) ||
                            outfit.outfitDescription.contains(query, ignoreCase = true) ||
                            outfit.outfitTags.any { it.contains(query, ignoreCase = true) }
                }

                if (matchingOutfits.isNotEmpty()) {
                    results[date] = matchingOutfits
                }
            }

            searchResults = results.toSortedMap()
            showSearchResults = results.isNotEmpty()
        }
    }

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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    performSearch(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search for Outfits by Date", fontSize = 18.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            showSearchResults = false
                            searchResults = emptyMap()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.size(18.dp))

            if (showSearchResults) {
                SearchResultsView(
                    searchResults = searchResults,
                    onDateClick = { date ->
                        selectedDate = date
                        showModal = true
                    }
                )
            } else {
                // Weather
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
                        customGrey = customGrey,
                        scheduledDates = scheduledDates
                    )
                }
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
            SettingsModal(onDismiss = { showSettings = false }, onSignOut = onNavigateToSignInScreen)
        }
    }

    // Outfit Modal
    if (showModal && selectedDate != null) {
        OutfitDateModal(
            date = selectedDate!!,
            outfitsViewModel = outfitsViewModel,
            closetViewModel = closetViewModel,
            onDismiss = {
                showModal = false
                scope.launch {
                    val dates = outfitsViewModel.getAllScheduledDates()
                    scheduledDates = dates.map { timestamp ->
                        LocalDate.ofEpochDay(timestamp / (24 * 60 * 60 * 1000))
                    }.toSet()
                }
                if (searchQuery.isNotEmpty()) {
                    performSearch(searchQuery)
                }
            },
            onNavigateToOutfits = onNavigateToOutfitsScreen
        )
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    onDateClick: (LocalDate) -> Unit,
    customGrey: Color,
    scheduledDates: Set<LocalDate> = emptySet()
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
        items(daysInMonth) { index ->
            val day = index + 1
            val date = currentMonth.atDay(day)
            val isToday = date == today
            val hasOutfit = date in scheduledDates

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            hasOutfit -> Color(0xFFE6FFE6) //green for scheduled days
                            isToday -> MaterialTheme.colorScheme.primaryContainer
                            else -> Color.White
                        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDateModal(
    date: LocalDate,
    outfitsViewModel: OutfitsViewModel,
    closetViewModel: ClosetViewModel,
    onDismiss: () -> Unit,
    onNavigateToOutfits: () -> Unit
) {
    var scheduledOutfits by remember { mutableStateOf<List<OutfitEntry>>(emptyList()) }
    var selectedOutfitId by remember { mutableStateOf<Int?>(null) }
    var outfitToDelete by remember { mutableStateOf<OutfitEntry?>(null) }
    val scope = rememberCoroutineScope()

    //load outfits for the date
    LaunchedEffect(date) {
        scope.launch {
            val timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            scheduledOutfits = outfitsViewModel.getOutfitsForDate(timestamp)
        }
    }

    if (selectedOutfitId != null) {
        OutfitModal(
            outfitsViewModel = outfitsViewModel,
            closetViewModel = closetViewModel,
            outfitId = selectedOutfitId!!,
            onDismiss = {
                selectedOutfitId = null
                scope.launch {
                    val timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    scheduledOutfits = outfitsViewModel.getOutfitsForDate(timestamp)
                }
            },
            onNavigateToCalendarScreen = { }
        )
    } else {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = date.format(
                            java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (scheduledOutfits.isNotEmpty()) {
                        Text(
                            text = "Scheduled Outfits:",
                            style = MaterialTheme.typography.titleMedium
                        )

                        //Carousel
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(scheduledOutfits) { outfit ->
                                    OutfitPreviewCard(
                                        outfit = outfit,
                                        onClick = { selectedOutfitId = outfit.outfitId },
                                        onRemove = { outfitToDelete = outfit }
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No outfits scheduled for this day",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )

                        TextButton(
                            onClick = {
                                onDismiss()
                                onNavigateToOutfits() //TODO idk i could remove this
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = "Add outfit",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Schedule an Outfit")
                        }
                    }

                    // Close button
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }

        //delete dialog
        if (outfitToDelete != null) {
            AlertDialog(
                onDismissRequest = { outfitToDelete = null },
                title = { Text("Remove from Schedule?") },
                text = {
                    Text(
                        "Are you sure you want to remove \"${outfitToDelete!!.outfitName}\" from ${date.format(
                            java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy")
                        )}? The outfit will remain in your Outfits collection."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                val timestamp = date.atStartOfDay(ZoneId.systemDefault())
                                    .toInstant().toEpochMilli()
                                outfitsViewModel.removeOutfitFromDate(timestamp, outfitToDelete!!.outfitId)
                                scheduledOutfits = outfitsViewModel.getOutfitsForDate(timestamp)
                                outfitToDelete = null
                            }
                        }
                    ) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { outfitToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

//cards in the carousel in the day modal
@Composable
private fun OutfitPreviewCard(
    outfit: OutfitEntry,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F7F7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (outfit.outfitPhotoUri.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(outfit.outfitPhotoUri),
                        contentDescription = "${outfit.outfitName} image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback icon if no photo TODO remove?
                    Icon(
                        painter = painterResource(R.drawable.shirt),
                        contentDescription = "Default outfit icon",
                        modifier = Modifier.size(120.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //name
            Text(
                text = outfit.outfitName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            //remove button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Remove outfit from this date",
                    tint = Color.Red,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

//search results
@Composable
fun SearchResultsView(
    searchResults: Map<LocalDate, List<OutfitEntry>>,
    onDateClick: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0E0E0)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Search Results (${searchResults.size} date${if (searchResults.size != 1) "s" else ""})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (searchResults.isEmpty()) {
                Text(
                    text = "No scheduled outfits found matching your search.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(searchResults.entries.toList().size) { index ->
                        val entry = searchResults.entries.toList()[index]
                        val date = entry.key
                        val outfits = entry.value

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDateClick(date) },
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = date.format(
                                        java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")
                                    ),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "${outfits.size} outfit${if (outfits.size != 1) "s" else ""}: ${outfits.joinToString(", ") { it.outfitName }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}