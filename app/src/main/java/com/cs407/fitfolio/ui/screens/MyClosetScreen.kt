package com.cs407.fitfolio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.components.TopHeader
import com.cs407.fitfolio.ui.modals.SettingsModal
import com.cs407.fitfolio.ui.viewModels.ClosetState
import com.cs407.fitfolio.ui.viewModels.ClosetViewModel

// TODO: shuffle icon, item icons, put toggle fav in view model, make columns for item types
// clickable, implement item card, add settings and info screens, add coroutines for filtering calls??
@Composable
fun MyClosetScreen(
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToWardrobeScreen: () -> Unit,
    onNavigateToAddScreen: () -> Unit,
    closetViewModel: ClosetViewModel
) {
    // Observe the current UI state from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()

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
            TopHeader(title = "My Closet")

            Spacer(modifier = Modifier.size(10.dp))

            ItemTypeRow(closetState, closetViewModel)

            Spacer(modifier = Modifier.size(10.dp))

            FilterRow(closetState, closetViewModel)

            Spacer(modifier = Modifier.size(10.dp))

            ClosetGrid(closetState, closetViewModel)
        }

        // Settings button
        IconButton(
            onClick = { showSettings = true },
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                Modifier.size(36.dp)
            )
        }

        // Show settings or information modals
        if (showSettings) {
            SettingsModal(onDismiss = { showSettings = false })
        }
    }
}

// Scrollable row of item types for filtering
// TODO: make each column clickable (modifier.clickable)
@Composable
fun ItemTypeRow(closetState: ClosetState, closetViewModel: ClosetViewModel) {
    // Scroll state for the item type row
    val scrollState = rememberScrollState()

    // Tracks the selected item type for filtering
    // TODO: move to view model
    var selectedType by remember { mutableStateOf("All") }

    // TODO: update icons
    Row(modifier = Modifier
        .horizontalScroll(scrollState)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(color = if (selectedType == "All") Color(0xFFE0E0E0) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            // Show all items icon button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                IconButton(onClick = {
                    selectedType = "All"
                    closetViewModel.filterByItemType("All")
                }) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "All items"
                    )
                }
                Text("All")
            }
        }

        // Icon buttons for each item type to filter by
        closetState.itemTypes.forEach { itemType ->
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(color = if (selectedType == itemType) Color(0xFFE0E0E0) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    IconButton(onClick = {
                        selectedType = itemType
                        closetViewModel.filterByItemType(itemType)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = itemType
                        )
                    }
                    Text(itemType)
                }
            }
        }
    }
}

// Row of action buttons for filtering, shuffling, and searching
@Composable
fun FilterRow(closetState: ClosetState, closetViewModel: ClosetViewModel) {
    // Tracks whether or not the favorites filtering is toggled
    // TODO: move to view model
    var isFilteredByFav by remember { mutableStateOf(false) }

    // Tracks whether tags filter is expanded or not
    var expanded by remember { mutableStateOf(false) }

    // Tracks search bar information
    // TODO: move to view model
    var searchText by remember { mutableStateOf("")}
    var isSearchActive by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Filter by favorites
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = {
                isFilteredByFav = !isFilteredByFav
                closetViewModel.filterByFavorites()
            }) {
                Icon(
                    imageVector = if (isFilteredByFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFilteredByFav) "Remove favorites filter" else "Filter by favorites",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Shuffle items
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            // TODO: add a shuffle icon
            IconButton(onClick = {closetViewModel.shuffleItems()}) {
                Icon(
                    painter = painterResource(R.drawable.shuffle),
                    contentDescription = "shuffle",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Search bar
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(onClick = { isSearchActive = true }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
            }
        }

        // Search bar dialog
        if (isSearchActive) {
            AlertDialog(
                title = {
                    Text(text = "Search for an item")
                },
                text = {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Enter item name") },
                    )
                },
                onDismissRequest = { isSearchActive = false },
                confirmButton = {
                    Button(onClick = {
                        isSearchActive = false
                        closetViewModel.searchItems(searchText)
                        searchText = ""
                    }) {
                        Text(text = "Search")
                    }
                }
            )
        }

        // Tags filtering
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Text(
                    "Tags",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Tag options",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    closetState.tags.forEach { tag ->
                        DropdownMenuItem(
                            text = { Text(tag) },
                            onClick = { closetViewModel.filterByTags(tag) }
                        )
                    }
                }
            }
        }

        // Clear all filters
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { closetViewModel.clearFilters() }) {
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = "Clear filters",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Grid of the items currently shown in the closet
@Composable
fun ClosetGrid(closetState: ClosetState, closetViewModel: ClosetViewModel) {
    // TODO: pull back in the if/else and the iteration through filteredItems when ready
    /*if (closetState.filteredItems.isEmpty()) {
        Text(
            "No items found.",
            modifier = Modifier
                .padding(16.dp)
        )
    } else {*/
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth(),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            /*items(closetState.filteredItems) { item ->
                    ElevatedCard() {
                        // TODO: implement item card
                    }
                }*/
            items(30) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((150..250).random().dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Item $index")
                }
            }
        }
    //}
}
