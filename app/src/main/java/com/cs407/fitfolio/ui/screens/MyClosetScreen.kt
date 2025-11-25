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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.enums.DefaultItemTypes
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
    onNavigateToSignInScreen: () -> Unit,
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel,
) {
    // Observe the current UI states from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    val outfitsState by outfitsViewModel.outfitsState.collectAsStateWithLifecycle()

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
            TopHeader(title = "My Closet") // TODO: make string resource

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
            SettingsModal(onDismiss = { showSettings = false }, onSignOut = onNavigateToSignInScreen)
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

        // Show deletion dialog
        if (closetState.isDeleteActive == DeletionStates.Confirmed.name) {
            DeleteItemDialog(closetViewModel)
        }
    }
}

// Scrollable row of item types for filtering
@Composable
fun ItemTypeRow(closetState: ClosetState, closetViewModel: ClosetViewModel) {
    // Scroll state for the item type row
    val scrollState = rememberScrollState()

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
                .background(color = if (closetState.activeItemType == DefaultItemTypes.ALL.typeName) Color(0xFFE0E0E0) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            // Show all items icon button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = {
                        closetViewModel.updateActiveItemType(DefaultItemTypes.ALL.typeName)
                    })
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.hanger),
                    contentDescription = "All items",
                    modifier = Modifier.size(36.dp)
                )
                Text(DefaultItemTypes.ALL.typeName)
            }
        }

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
                    Icon(
                        painter = painterResource(id = R.drawable.hanger),
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
                    // TODO: should this be the exit mode?? and we have a separate confirm button
                    // ^^ currently, the clear filters takes it out of delete mode, prob should be separate so the filters don't disappear if we exit out of delete mode
                    IconButton(onClick = {
                        if (closetState.deletionCandidates.isEmpty()) {
                            closetViewModel.toggleDeleteState(DeletionStates.Inactive.name)
                        } else {
                            closetViewModel.toggleDeleteState(DeletionStates.Confirmed.name)
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
    if (closetState.filteredItems.isEmpty()) {
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
                    Row(modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
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

                    Text("PHOTO OF ITEM HERE")
                }
            }
        }
    }
}
