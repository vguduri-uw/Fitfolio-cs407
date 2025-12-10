package com.cs407.fitfolio.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cs407.fitfolio.R
import com.cs407.fitfolio.enums.DeletionStates
import com.cs407.fitfolio.ui.components.DeleteItemDialog
import com.cs407.fitfolio.ui.components.TopHeader
import com.cs407.fitfolio.ui.modals.ItemModal
import com.cs407.fitfolio.ui.modals.SettingsModal
import com.cs407.fitfolio.ui.theme.DrySage
import com.cs407.fitfolio.ui.theme.FloralWhite
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.ui.theme.LightPeachFuzz
import com.cs407.fitfolio.viewModels.ClosetState
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import com.cs407.fitfolio.viewModels.UserViewModel

@Composable
fun MyClosetScreen(
    onNavigateToCalendarScreen: () -> Unit,
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel,
    userViewModel: UserViewModel,
    onSignOut: () -> Unit
) {
    // Observe the current UI states from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()

    // Re-filter when an item is added or deleted
    LaunchedEffect(closetState.items) {
        closetViewModel.applyFilters()
    }

    // Track whether the settings modal is shown or not
    var showSettings by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FloralWhite)
            .padding(horizontal = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
        ) {
            TopHeader(title = stringResource(R.string.closet_title), userViewModel = userViewModel )

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
                    painter = painterResource(R.drawable.cog),
                    contentDescription = "Settings",
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Show settings
        if (showSettings) {
            SettingsModal(
                onDismiss = { showSettings = false }, userViewModel = userViewModel,
                onSignOut = onSignOut)
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
                containerColor = LightPeachFuzz.copy(alpha = 0.9f),
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
                    Text(
                        "Cancel",
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        painter = painterResource(R.drawable.clear),
                        contentDescription = "Exit delete mode",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
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
                    Text(
                        "Delete",
                        color = Color.White,
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        painter = painterResource(R.drawable.delete),
                        contentDescription = "Confirm delete mode",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
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

    Row(
        modifier = Modifier
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
                    .background(color = if (closetState.activeItemType == itemType) LightPeachFuzz else Color.Transparent),
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
                    Text(
                        itemType,
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
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

    // Context for Toast messages
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Filter by favorites
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = {
                closetViewModel.toggleFavoritesState()
            }) {
                Icon(
                    painter = if (closetState.isFavoritesActive)
                        painterResource(R.drawable.heart_filled_red)
                    else
                        painterResource(R.drawable.heart_outline),
                    contentDescription = if (closetState.isFavoritesActive) "Remove favorites filter" else "Filter by favorites",
                    tint = if (closetState.isFavoritesActive) Color.Red else Color.Black,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Shuffle items
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { closetViewModel.shuffleItems() },
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
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(onClick = {
                closetViewModel.toggleSearchState(true)
                showSearchDialog = true
            }) {
                Icon(
                    painter = painterResource(R.drawable.loupe),
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
                    Text(
                        text = "Search for an item",
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    TextField(
                        value = closetState.searchQuery,
                        onValueChange = { it -> closetViewModel.updateSearchQuery(it)},
                        placeholder = {
                            Text(
                                "Enter item name",
                                fontFamily = Kudryashev_Display_Sans_Regular,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightPeachFuzz,
                            unfocusedContainerColor = LightPeachFuzz,
                            disabledContainerColor = LightPeachFuzz.copy(alpha = 0.7f)
                        ),
                        textStyle = TextStyle(
                            fontFamily = Kudryashev_Display_Sans_Regular,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                onDismissRequest = { showSearchDialog = false },
                confirmButton = {
                    Button(onClick = {
                        closetViewModel.applyFilters()
                        showSearchDialog = false
                    }) {
                        Text(
                            text = "Search",
                            fontFamily = Kudryashev_Display_Sans_Regular,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                containerColor = FloralWhite
            )
        }

        // Tags filtering
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz)
                .padding(horizontal = 15.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Text(
                    "Tags",
                    style = TextStyle(
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    Icons.Outlined.ArrowDropDown,
                    contentDescription = "Tags",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp),
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = DrySage,
                modifier = Modifier
                    .height(450.dp)
            ) {
                closetState.tags
                    .sortedByDescending { it in closetState.activeTags }
                    .forEach { tag ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    tag,
                                    fontFamily = Kudryashev_Display_Sans_Regular,
                                    fontWeight = FontWeight.Bold
                                )
                            },
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
                                        painter = painterResource(R.drawable.clear),
                                        contentDescription = "Remove tag",
                                        tint = Color.Black,
                                        modifier = Modifier.size(15.dp)
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.add),
                                        contentDescription = "Add tag",
                                        tint = Color.Black,
                                        modifier = Modifier.size(15.dp)
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
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz),
            contentAlignment = Alignment.Center
        ) {
            when (closetState.isDeleteActive) {
                DeletionStates.Inactive.name -> {
                    IconButton(onClick = {
                        closetViewModel.toggleDeleteState(DeletionStates.Active.name)
                        Toast.makeText(
                            context,
                            "Select all items to delete, then press Delete. Otherwise, press Cancel.",
                            Toast.LENGTH_LONG
                        ).show()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.delete),
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
                            painter = painterResource(R.drawable.delete),
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
                            painter = painterResource(R.drawable.delete),
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
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { closetViewModel.clearFilters() }) {
                Icon(
                    painter = painterResource(R.drawable.clear),
                    contentDescription = "Clear filters",
                    tint = Color.Black,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

// Grid of the items currently shown in the closet
@Composable
fun ClosetGrid(closetState: ClosetState, closetViewModel: ClosetViewModel) {
    var aspectRatio by remember { mutableFloatStateOf(1f) }

    if (closetState.isFiltering) {
        CircularProgressIndicator(
            modifier = Modifier.padding(32.dp)
        )
    } else if (closetState.filteredItems.isEmpty()) {
        Text(
            "No items found.",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(16.dp)
        )
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth(),
            verticalItemSpacing = 5.dp,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(closetState.filteredItems) { item ->
                Box(
                    modifier = Modifier.padding(bottom = 10.dp)
                ){

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .shadow(
                                elevation = 3.dp,
                                shape = MaterialTheme.shapes.medium,
                                clip = false
                            )
                            .clip(MaterialTheme.shapes.medium)
                            .background(LightPeachFuzz)
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
                                    fontFamily = Kudryashev_Display_Sans_Regular,
                                    fontWeight = FontWeight.Bold,
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
//                                        Box(modifier = Modifier.padding(5.dp)){
//                                        }
                                        Icon(
                                            painter = if (item.isFavorite)
                                                painterResource(R.drawable.heart_filled_red)
                                            else
                                                painterResource(R.drawable.heart_outline),
                                            contentDescription = "Toggles favorites",
                                            tint = if (item.isFavorite) Color.Red else Color.Black,
                                            modifier = Modifier.size(20.dp),
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
                                        .fillMaxWidth()
                                        .aspectRatio(aspectRatio),
                                    contentScale = ContentScale.Fit,
                                    onSuccess = {
                                        val w = it.result.drawable.intrinsicWidth
                                        val h = it.result.drawable.intrinsicHeight
                                        if (w > 0 && h > 0) {
                                            val r = w.toFloat() / h.toFloat()
                                            aspectRatio = maxOf(r, 0.55f)
                                        }
                                    }
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
}