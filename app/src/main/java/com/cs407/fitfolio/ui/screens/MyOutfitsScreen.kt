package com.cs407.fitfolio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.data.testData.AddTestItemData
import com.cs407.fitfolio.ui.components.DeleteOutfitDialog
import com.cs407.fitfolio.ui.components.TopHeader
import com.cs407.fitfolio.ui.components.WeatherCarousel
import com.cs407.fitfolio.enums.DeletionStates
import com.cs407.fitfolio.ui.modals.OutfitModal
import com.cs407.fitfolio.ui.modals.SettingsModal
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.OutfitsState
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import com.cs407.fitfolio.viewModels.WeatherViewModel

@Composable
fun MyOutfitsScreen(
    onNavigateToCalendarScreen: () -> Unit,
    onSignOut: () -> Unit,
    outfitsViewModel: OutfitsViewModel,
    weatherViewModel: WeatherViewModel,
    closetViewModel: ClosetViewModel // TODO: remove after testing
) {
    // observes current ui state from the outfits view model
    val outfitsState by outfitsViewModel.outfitsState.collectAsStateWithLifecycle()
    //for weather
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()

    // TODO: remove after testing
    LaunchedEffect(Unit) {
        val dbOutfits = outfitsViewModel.getOutfits()

        if (dbOutfits.isEmpty()) {
            AddTestItemData(closetViewModel, outfitsViewModel)
        }
    }

    // re-filter when an item is added or deleted
    LaunchedEffect(outfitsState.outfits) {
        outfitsViewModel.applyFilters()
    }

    // track settings modal state
    var showSettings by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
        ) {
            // profile image, my outfits title, information icon
            TopHeader("My Outfits")

            Spacer(modifier = Modifier.size(10.dp))

            // horizontally scrollable weather info row
            WeatherCarousel(
                weatherData = weatherState.weatherData,
                isLoading = weatherState.isLoading
            )

            Spacer(modifier = Modifier.size(10.dp))

            // search bar, tags filter, favorites toggle, shuffle button
            FilterRow(outfitsState, outfitsViewModel)

            Spacer(modifier = Modifier.size(10.dp))

            // vertically scrollable outfits grid
            OutfitGrid(outfitsState, outfitsViewModel)
        }

        // settings button
        IconButton(
            onClick = { showSettings = true }, // TODO: add settings onClick lambda
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                Modifier.size(36.dp)
            )
        }

        // pull up settings modal
        if (showSettings) {
            SettingsModal(onDismiss = { showSettings = false }, onSignOut = onSignOut)
        }

        // show outfit modal
        if (outfitsState.outfitToShow != -1) {
            OutfitModal(
                outfitsViewModel = outfitsViewModel,
                outfitId = outfitsState.outfitToShow,
                onDismiss = { outfitsViewModel.updateOutfitToShow(-1) },
                onNavigateToCalendarScreen = onNavigateToCalendarScreen,
                closetViewModel = closetViewModel
            )
        }

        // navigate to sign up and sign in screens
//        Column(
//            modifier = Modifier
//                .align(alignment = Alignment.TopStart)
//                .padding(bottom = 16.dp)
//        ) {
//            Button(
//                onClick = { onNavigateToSignUpScreen() },
//                modifier = Modifier.width(100.dp)
//            ) {
//                Text("Sign Up")
//            }
//            Button(
//                onClick = { onNavigateToSignInScreen() },
//                modifier = Modifier.width(100.dp)
//            ) {
//                Text("Sign In")
//            }
//        }
    }
}

// TODO: Veda will provide weather section
//@Composable
//fun WeatherRow() {
//    LazyRow (
//        horizontalArrangement = Arrangement.spacedBy(15.dp),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        items(7) { index ->
//            Box (
//                modifier = Modifier
//                    .clip(shape = MaterialTheme.shapes.medium)
//                    .background(Color(0xFFE0E0E0)),
//            ) {
//                Text(
//                    "Weather for Day ${index + 1}",
//                    modifier = Modifier
//                        .padding(horizontal = 55.dp, vertical = 20.dp)
//                )
//            }
//        }
//    }
//}

@Composable
fun FilterRow(outfitsState: OutfitsState, outfitsViewModel: OutfitsViewModel) {
    // tracks whether tags dropdown is expanded
    var expanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(), // matches rest of screen
        verticalAlignment = Alignment.CenterVertically
    ) {
        // favorites filter toggle
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = {
                    outfitsViewModel.toggleFavoritesState()
                }
            ){
                Icon(
                    imageVector = if (outfitsState.isFavoritesActive) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (outfitsState.isFavoritesActive) "Remove favorites filter" else "Filter by favorites",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // shuffle button
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { outfitsViewModel.shuffleOutfits() }) {
                Icon(
                    painter = painterResource(R.drawable.shuffle),
                    contentDescription = "shuffle",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // search button
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { outfitsViewModel.toggleSearchState(true) }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "search",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
            }
        }

        // search bar dialog
        if (outfitsState.isSearchActive) {
            AlertDialog(
                title = {
                    Text(text = "Search for an item")
                },
                text = {
                    TextField(
                        value = outfitsState.searchQuery,
                        onValueChange = { it -> outfitsViewModel.updateSearchQuery(it)},
                        placeholder = { Text("Enter item name") },
                    )
                },
                onDismissRequest = { outfitsViewModel.toggleSearchState(false) },
                confirmButton = {
                    Button(onClick = {
                        outfitsViewModel.toggleSearchState(false)
                        outfitsViewModel.applyFilters()
                    }) {
                        Text(text = "Search")
                    }
                }
            )
        }

        // tags drop down menu
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0))
                .padding(horizontal = 10.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                )

                Spacer(modifier = Modifier.size(4.dp))

                Icon(
                    Icons.Outlined.ArrowDropDown,
                    contentDescription = "Favorites",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp),
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = -10.dp, y = 15.dp)
            ) {
                // note: outfitsState.allTags is the master list of tags/types
                outfitsState.tags
                    .sortedByDescending { it in outfitsState.activeTags }
                    .forEach { tag ->
                        DropdownMenuItem(
                            text = { Text(tag) },
                            onClick = {
                                if (tag in outfitsState.activeTags) {
                                    outfitsViewModel.removeFromActiveTags(tag)
                                } else {
                                    outfitsViewModel.addToActiveTags(tag)
                                }
                            },
                            trailingIcon = {
                                if (tag in outfitsState.activeTags) {
                                    Icon(
                                        Icons.Outlined.Clear,
                                        contentDescription = "Remove tag",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .weight(.25f),
                                    )
                                } else {
                                    Icon(
                                        Icons.Outlined.Add,
                                        contentDescription = "Add tag",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .weight(.25f),
                                    )
                                }
                            },
                            modifier = Modifier.width(140.dp)
                        )
                }
            }
        }

        // Enter deletion candidate state/confirm delete state
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            when (outfitsState.isDeleteActive) {
                DeletionStates.Inactive.name -> {
                    IconButton(onClick = { outfitsViewModel.toggleDeleteState(DeletionStates.Active.name) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Enter deletion candidate state",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                DeletionStates.Active.name -> {
                    // TODO: should this be the exit mode?? and we have a separate confirm button
                    // ^^ currently, the clear filters takes it out of delete mode, prob should be separate so the filters don't disappear if we exit out of delete mode
                    IconButton(onClick = {
                        if (outfitsState.deletionCandidates.isEmpty()) {
                            outfitsViewModel.toggleDeleteState(DeletionStates.Inactive.name)
                        } else {
                            outfitsViewModel.toggleDeleteState(DeletionStates.Confirmed.name)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Confirm delete state",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                DeletionStates.Confirmed.name -> {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Placeholder during alert dialog composition",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // clear filters button
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { outfitsViewModel.clearFilters() }) {
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = "clear filters",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// grid of items currently shown in closet
@Composable
fun OutfitGrid(outfitsState: OutfitsState, outfitsViewModel: OutfitsViewModel) {
    // TODO: pull back in the if/elses and the iteration through filteredItems when ready
    if (outfitsState.filteredOutfits.isEmpty()) {
        Text(
            "No items found.",
            modifier = Modifier
                .padding(16.dp)
        )
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth(),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(outfitsState.filteredOutfits) { outfit ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0))
                        .clickable(
                            enabled =
                                outfitsState.isDeleteActive != DeletionStates.Confirmed.name,
                            onClick = {
                                if (outfitsState.isDeleteActive == DeletionStates.Active.name) {
                                    if (outfit.isDeletionCandidate) {
                                        outfitsViewModel.removeDeletionCandidates(outfit)
                                    } else {
                                        outfitsViewModel.setDeletionCandidates(outfit)
                                    }
                                } else {
                                    outfitsViewModel.updateOutfitToShow(outfit.outfitId)
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .align(alignment = Alignment.TopEnd)
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Favorite item button (if not in delete state)
                        if (outfitsState.isDeleteActive == DeletionStates.Inactive.name) {
                            IconButton(
                                onClick = { outfitsViewModel.toggleFavoritesProperty(outfit) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (outfit.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = if (outfit.isFavorite) "Remove item from favorites" else "Add item to favorites"
                                )
                            }
                        }
                        // Toggle deletion candidate icon (if in delete state)
                        if (outfitsState.isDeleteActive == DeletionStates.Active.name) {
                            Icon(
                                imageVector = if (outfit.isDeletionCandidate) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                contentDescription = if (outfit.isDeletionCandidate) "Remove item from deletion candidates" else "Add item to deletion candidates"
                            )
                        }
                    }
                    Text(outfit.outfitName)
                }
            }
        }
        if (outfitsState.isDeleteActive == DeletionStates.Confirmed.name) {
            DeleteOutfitDialog(outfitsViewModel)
        }
    }
}
