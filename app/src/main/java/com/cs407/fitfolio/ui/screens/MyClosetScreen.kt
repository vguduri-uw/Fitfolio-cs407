package com.cs407.fitfolio.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.data.ClosetState
import com.cs407.fitfolio.ui.viewModels.ClosetViewModel

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

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
        ) {
            TopHeaderSection()

            Spacer(modifier = Modifier.size(10.dp))

            ItemTypeRow(closetState)

            Spacer(modifier = Modifier.size(10.dp))

            FilterRow(closetState)

            Spacer(modifier = Modifier.size(10.dp))
        }

        // Settings button
        IconButton(
            onClick = {}, // TODO: add settings onClick lambda
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun TopHeaderSection () {
    Image(
        // TODO: replace with actual profile image
        painter = painterResource(id = R.drawable.user),
        contentDescription = "User profile image",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        alignment = Alignment.Center
    )

    Spacer(modifier = Modifier.size(8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "My Outfits", style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = {}) { // TODO: add info onClick lambda
            Icon(Icons.Outlined.Info,
                contentDescription = "Information",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Scrollable row of item types for filtering
// TODO: make each column clickable (modifier.clickable)
@Composable
fun ItemTypeRow(closetState: ClosetState) {
    // Scroll state for the item type row
    val scrollState = rememberScrollState()

    // Tracks the selected item type for filtering
    // TODO: move to view model
    var selectedType by remember { mutableStateOf("All") }

    // TODO: update icons
    // TODO: implement filtering
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
                IconButton(onClick = {selectedType = "All"}) {
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
                    IconButton(onClick = {selectedType = itemType}) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = itemType
                        )
                    }
                    Text(itemType)
                }
            }
        }

        // TODO: implement adding a type
        // Icon button for adding a type
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add type"
                )
            }
            Text("Add type")
        }
    }
}

// Row of action buttons for filtering, shuffling, and searching
@Composable
fun FilterRow(closetState: ClosetState) {
    // Tracks whether or not the favorites filtering is toggled
    // TODO: move to view model
    var isFilteredByFav by remember { mutableStateOf(false) }

    // Tracks whether tags filter is expanded or not
    var expanded by remember { mutableStateOf(false) }

    // Tracks search bar information
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
            IconButton(onClick = { isFilteredByFav = !isFilteredByFav }) {
                Icon(
                    imageVector = if (isFilteredByFav) Icons.Filled.Favorite else Icons.Outlined.Favorite,
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
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Shuffle",
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
                        // TODO: feed value into view model to filter by
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
                            onClick = { /* handle tag click */ }
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
            // TODO: implement clear filter button
            IconButton(onClick = { }) {
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
fun ClosetGrid(closetState: ClosetState) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(200.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(closetState.filteredItems) { item ->
                ElevatedCard() {
                    // TODO: implement item card
                }
            }
        }
    )
}
