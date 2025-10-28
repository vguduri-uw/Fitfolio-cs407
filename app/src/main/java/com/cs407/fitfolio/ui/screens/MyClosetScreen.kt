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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
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
import com.cs407.fitfolio.ui.components.TopHeader
import com.cs407.fitfolio.ui.modals.SettingsModal
import com.cs407.fitfolio.ui.viewModels.ClosetState
import com.cs407.fitfolio.ui.viewModels.ClosetViewModel

// TODO: different item icons?, add coroutines for filtering calls?, add scroll bars?, implement item card
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

        // Show settings
        if (showSettings) {
            SettingsModal(onDismiss = { showSettings = false })
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
                .background(color = if (closetState.activeItemType == "All") Color(0xFFE0E0E0) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            // Show all items icon button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = {
                        closetViewModel.updateItemType("All")
                        closetViewModel.applyFilters()
                    })
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.hanger),
                    contentDescription = "All items",
                    modifier = Modifier.size(36.dp)
                )
                Text("All")
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
                            closetViewModel.updateItemType(itemType)
                            closetViewModel.applyFilters()
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
// TODO: can this be created into 1 composable that both closet screen and outfit screen can share and pass in their own view models?? (if have the same named functions)
@Composable
fun FilterRow(closetState: ClosetState, closetViewModel: ClosetViewModel) {
    // Track whether tags filter is expanded or not
    var expanded by remember { mutableStateOf(false) }

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
                closetViewModel.toggleFavorites()
                closetViewModel.applyFilters()
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
            IconButton(onClick = { closetViewModel.toggleSearch(true) }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
            }
        }

        // Search bar dialog
        if (closetState.isSearchActive) {
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
                onDismissRequest = { closetViewModel.toggleSearch(false) },
                confirmButton = {
                    Button(onClick = {
                        closetViewModel.toggleSearch(false)
                        closetViewModel.applyFilters()
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
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = -14.dp, y = 14.dp)
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
                                closetViewModel.applyFilters()
                            },
                            trailingIcon = {
                                if (tag in closetState.activeTags) {
                                    Icon(
                                        imageVector = Icons.Outlined.Clear,
                                        contentDescription = "Clear tag"
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.Add,
                                        contentDescription = "Add tag"
                                    )
                                }
                            }
                        )
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
    // Track whether deletion confirm AlertDialog should be shown
    // TODO: move to view model... maybe put it in item (isDeletionCandidate)
    var showDeletionDialog by remember { mutableStateOf(false) }

    // TODO: pull back in the if/elses and the iteration through filteredItems when ready
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
            // for testing purposes only
            items(30) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((150..250).random().dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .padding(6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Favorite item button
                        IconButton(
                            onClick = {
                                /*if (item.isFavorite) {
                                closetViewModel.removeFromFavorites(item)
                            } else {
                                closetViewModel.addToFavorites(item)
                            } */
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                /*imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (item.isFavorite) "Remove item from favorites" else "Add item to favorites"*/

                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = "Add item to favorites"
                            )
                        }

                        // Delete item button
                        IconButton(
                            onClick = {
                                // closetViewModel.toggleDeletionCandidate(true, item.itemId)
                                showDeletionDialog = true
                            },
                            modifier = Modifier
                                .size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete item"
                            )
                        }
                    }
                    Text("Item $index")
                }
            }
        }

    // TODO: fix logic here... because rn we'll have to iterate over all to see if any is candidate...
    // TODO: move the dialog to its own component so the item modal can use it
    if (showDeletionDialog) {
        AlertDialog(
            onDismissRequest = { showDeletionDialog = false },
            title = {
                Text("Are you sure you want to delete this item?")
            },
            dismissButton = {
                Button(onClick = {
                    // closetViewModel.toggleDeletionCandidate(false, item.itemId)
                    showDeletionDialog = false
                }) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                Button(onClick = {
                    // closetViewModel.toggleDeletionCandidate(false, item.itemId)
                    showDeletionDialog = false
                    /*closetViewModel.delete(item)*/
                }) {
                    Text(text = "Delete")
                }
            }
        )
    }
    //}
}
