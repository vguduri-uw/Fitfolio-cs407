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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.components.DeleteItemDialog
import com.cs407.fitfolio.ui.components.TopHeader
import com.cs407.fitfolio.enums.DeletionStates
import com.cs407.fitfolio.ui.modals.ItemModal
import com.cs407.fitfolio.ui.modals.SettingsModal
import com.cs407.fitfolio.viewModels.ClosetState
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.OutfitsViewModel

@Composable
fun MyClosetScreen(
    onNavigateToCalendarScreen: () -> Unit,
    onSignOut: () -> Unit,
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel,
) {
    // Observe the current UI states from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()

    // Re-filter when an item is added or deleted
    LaunchedEffect(closetState.items) {
        closetViewModel.applyFilters()
    }

    // Track whether the settings modal is shown or not
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
            TopHeader(title = stringResource(R.string.closet_title))

            Spacer(modifier = Modifier.size(10.dp))

            ItemTypeRow(closetState, closetViewModel)

            Spacer(modifier = Modifier.size(10.dp))

            FilterRow(closetState, closetViewModel)

            Spacer(modifier = Modifier.size(10.dp))

            ClosetGrid(closetState, closetViewModel)
        }

        Column(
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
        ) {
            // Settings button
            IconButton(
                onClick = { showSettings = true }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    Modifier.size(36.dp)
                )
            }
        }

        // Show settings
        if (showSettings) {
            SettingsModal(onDismiss = { showSettings = false }, onSignOut = onSignOut)
        }

        // Show item modal
        if (closetState.itemToShow != -1) {
            ItemModal(
                closetViewModel = closetViewModel,
                itemId = closetState.itemToShow,
                onDismiss = { closetViewModel.updateItemToShow(-1)},
                outfitsViewModel = outfitsViewModel,
                onNavigateToCalendarScreen = onNavigateToCalendarScreen,
            )
        }

        if (closetState.isDeleteActive == DeletionStates.Active.name) {
            // Exit delete mode
            FloatingActionButton(
                onClick = { closetViewModel.toggleDeleteState(DeletionStates.Inactive.name) },
                containerColor = Color.LightGray.copy(alpha = 0.75f),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Cancel")
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = "Exit delete mode",
                        tint = Color.Black
                    )
                }
            }

            // Confirm delete mode
            FloatingActionButton(
                onClick = {
                    if (closetState.deletionCandidates.isEmpty()) {
                        closetViewModel.toggleDeleteState(DeletionStates.Inactive.name)
                    } else {
                        closetViewModel.toggleDeleteState(DeletionStates.Confirmed.name)
                    }
                },
                containerColor = Color.Red.copy(alpha = 0.75f),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Delete", color = Color.White)
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Confirm delete mode",
                        tint = Color.White
                    )
                }
            }
        }

        // Show deletion dialog
        if (closetState.isDeleteActive == DeletionStates.Confirmed.name) {
            DeleteItemDialog(closetViewModel, outfitsViewModel)
        }
    }
}

// Scrollable row of item types for filtering
@Composable
fun ItemTypeRow(closetState: ClosetState, closetViewModel: ClosetViewModel) {
    // Scroll state for the item type row
    val scrollState = rememberScrollState()

    Row(modifier = Modifier
        .horizontalScroll(scrollState)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon buttons for each item type to filter by
        closetState.itemTypes.forEach { itemType ->
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(color = if (closetState.activeItemType == itemType) Color(0xFFE0E0E0) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(onClick = {
                            closetViewModel.updateActiveItemType(itemType)
                        })
                ) {
                    val icon = closetViewModel.getItemTypeIcon(itemType)
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = itemType,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(itemType)
                }
            }
        }
    }
}

// Row of action buttons for filtering, shuffling, and searching
@Composable
fun FilterRow(closetState: ClosetState, closetViewModel: ClosetViewModel) {
    // Track whether tags filter is expanded or not
    var expanded by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }

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
                closetViewModel.toggleFavoritesState()
            }) {
                Icon(
                    imageVector = if (closetState.isFavoritesActive) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (closetState.isFavoritesActive) "Remove favorites filter" else "Filter by favorites",
                    tint = if (closetState.isFavoritesActive) Color.Red else Color.Black,
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
            IconButton(
                onClick = {closetViewModel.shuffleItems()},
                enabled = closetState.filteredItems.size > 1
            ) {
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
            IconButton(onClick = {
                closetViewModel.toggleSearchState(true)
                showSearchDialog = true
            }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
            }
        }

        // Search bar dialog
        if (showSearchDialog) {
            AlertDialog(
                title = {
                    Text(text = "Search for an item")
                },
                text = {
                    TextField(
                        value = closetState.searchQuery,
                        onValueChange = { it -> closetViewModel.updateSearchQuery(it)},
                        placeholder = { Text("Enter item name") },
                    )
                },
                onDismissRequest = { showSearchDialog = false },
                confirmButton = {
                    Button(onClick = {
                        closetViewModel.applyFilters()
                        showSearchDialog = false
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
                .padding(horizontal = 10.dp, vertical = 14.dp),
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
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = (-10).dp, y = 15.dp),
            ) {
                closetState.tags
                    .sortedByDescending { it in closetState.activeTags }
                    .forEach { tag ->
                        DropdownMenuItem(
                            text = { Text(tag) },
                            onClick = {
                                if (tag in closetState.activeTags) {
                                    closetViewModel.removeFromActiveTags(tag)
                                } else {
                                    closetViewModel.addToActiveTags(tag)
                                }
                            },
                            trailingIcon = {
                                if (tag in closetState.activeTags) {
                                    Icon(
                                        imageVector = Icons.Outlined.Clear,
                                        contentDescription = "Remove tag"
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.Add,
                                        contentDescription = "Add tag"
                                    )
                                }
                            },
                            modifier = Modifier.width(width = 140.dp)
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
            when (closetState.isDeleteActive) {
                DeletionStates.Inactive.name -> {
                    IconButton(onClick = { closetViewModel.toggleDeleteState(DeletionStates.Active.name) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Enter deletion candidate state",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                DeletionStates.Active.name -> {
                    IconButton(
                        onClick = {},
                        enabled = closetState.isDeleteActive == DeletionStates.Inactive.name
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Inactive delete icon during active delete state",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                DeletionStates.Confirmed.name -> {
                    IconButton(
                        onClick = {},
                        enabled = closetState.isDeleteActive == DeletionStates.Inactive.name
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Placeholder during alert dialog composition",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
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
    if (closetState.isFiltering) {
        CircularProgressIndicator(
            modifier = Modifier.padding(32.dp)
        )
    } else if (closetState.filteredItems.isEmpty()) {
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
            items(closetState.filteredItems) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0))
                        .clickable(
                            enabled =
                                closetState.isDeleteActive != DeletionStates.Confirmed.name,
                            onClick = {
                                if (closetState.isDeleteActive == DeletionStates.Active.name) {
                                    if (item.isDeletionCandidate) {
                                        closetViewModel.removeDeletionCandidate(item)
                                    } else {
                                        closetViewModel.setDeletionCandidates(item)
                                    }
                                } else {
                                    closetViewModel.updateItemToShow(item.itemId)
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .padding(6.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                item.itemName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .weight(1f)
                            )

                            // Favorite item button (if not in delete state)
                            if (closetState.isDeleteActive == DeletionStates.Inactive.name) {
                                IconButton(
                                    onClick = { closetViewModel.toggleFavoritesProperty(item) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = if (item.isFavorite) "Remove item from favorites" else "Add item to favorites",
                                        tint = if (item.isFavorite) Color.Red else Color.Black
                                    )
                                }
                            }

                            // Toggle deletion candidate icon (if in delete state)
                            if (closetState.isDeleteActive == DeletionStates.Active.name) {
                                Icon(
                                    imageVector = if (item.isDeletionCandidate) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                    contentDescription = if (item.isDeletionCandidate) "Remove item from deletion candidates" else "Add item to deletion candidates"
                                )

                            }
                        }

                        // Item image
                        if (item.itemPhotoUri.isNotEmpty()) {
                            AsyncImage(
                                model = item.itemPhotoUri,
                                contentDescription = item.itemName,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.hanger),
                                contentDescription = "Item photo",
                                modifier = Modifier
                                    .size(180.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
