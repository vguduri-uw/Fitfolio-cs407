package com.cs407.fitfolio.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.cs407.fitfolio.R
import com.cs407.fitfolio.data.ItemEntry
import com.cs407.fitfolio.data.testData.AddTestItemData
import com.cs407.fitfolio.enums.DeletionStates
import com.cs407.fitfolio.ui.components.DeleteOutfitDialog
import com.cs407.fitfolio.ui.components.TopHeader
import com.cs407.fitfolio.ui.components.WeatherCarousel
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

        if (outfitsState.isDeleteActive == DeletionStates.Active.name) {
            // Exit delete mode
            FloatingActionButton(
                onClick = { outfitsViewModel.toggleDeleteState(DeletionStates.Inactive.name) },
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
                    if (outfitsState.deletionCandidates.isEmpty()) {
                        outfitsViewModel.toggleDeleteState(DeletionStates.Inactive.name)
                    } else {
                        outfitsViewModel.toggleDeleteState(DeletionStates.Confirmed.name)
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
    }
}

// build up to 4 preview slots from the outfit's items
// if there are fewer than 4 items, the remaining slots are null
fun buildPreviewSlots(items: List<ItemEntry>): List<ItemEntry?> {

    // the final list of the four item preview slots
    val slots = mutableListOf<ItemEntry?>()

    // add up to the first 4 items into the slot list (ignore everything after)
    for (i in 0 until items.size) {
        if (i < 4) {
            slots.add(items[i])
        } else {
            break
        }
    }

    // if we have fewer than 4 items, fill the remaining spots with null
    while (slots.size < 4) {
        slots.add(null)
    }

    return slots
}

@Composable
private fun PreviewSquare(
    item: ItemEntry?,
    modifier: Modifier = Modifier,
    iconSize: Dp,
    squarePadding: Dp
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(squarePadding)
            .clip(MaterialTheme.shapes.small)
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {

        when {
            item == null -> {
                // placeholder image for an empty slot
                Image(
                    painter = painterResource(R.drawable.hanger),
                    contentDescription = "Empty slot",
                    modifier = Modifier.size(iconSize)
                )
            }

            item.itemPhotoUri.isNotBlank() -> {
                // outfit image
                Image(
                    painter = rememberAsyncImagePainter(item.itemPhotoUri),
                    contentDescription = item.itemName,
                    modifier = Modifier.size(iconSize),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                // placeholder image if outfit photo can't be found
                Image(
                    painter = painterResource(R.drawable.hanger),
                    contentDescription = "Placeholder for ${item.itemName}",
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
fun OutfitPreviewGrid(
    items: List<ItemEntry>,
    modifier: Modifier = Modifier,
    iconSize: Dp = 32.dp,
    squarePadding: Dp = 6.dp
) {
    val slots = remember(items) { buildPreviewSlots(items) }

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            PreviewSquare(slots[0], Modifier.weight(1f), iconSize, squarePadding)
            PreviewSquare(slots[1], Modifier.weight(1f), iconSize, squarePadding)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            PreviewSquare(slots[2], Modifier.weight(1f), iconSize, squarePadding)
            PreviewSquare(slots[3], Modifier.weight(1f), iconSize, squarePadding)
        }
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

    val context = LocalContext.current

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
            ) {
                Icon(
                    imageVector = if (outfitsState.isFavoritesActive) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (outfitsState.isFavoritesActive) "Remove favorites filter" else "Filter by favorites",
                    tint = if (outfitsState.isFavoritesActive) Color.Red else Color.Black,
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
                        onValueChange = { it -> outfitsViewModel.updateSearchQuery(it) },
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
                    IconButton(onClick = {
                        outfitsViewModel.toggleDeleteState(DeletionStates.Active.name)
                        Toast.makeText(
                            context,
                            "Select outfits, then tap the filled delete icon to confirm deletion.",
                            Toast.LENGTH_LONG
                        ).show()
                    }) {
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
                        enabled = outfitsState.isDeleteActive == DeletionStates.Inactive.name
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Inactive delete icon during active delete state",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                DeletionStates.Confirmed.name -> {
                    IconButton(
                        onClick = {},
                        enabled = outfitsState.isDeleteActive == DeletionStates.Inactive.name
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

    // grid of outfits currently shown in outfits
    @Composable
    fun OutfitGrid(outfitsState: OutfitsState, outfitsViewModel: OutfitsViewModel) {
        if (outfitsState.isFiltering) {
            CircularProgressIndicator(
                modifier = Modifier.padding(32.dp)
            )
        } else if (outfitsState.filteredOutfits.isEmpty()) {
            Text(
                "No items found.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(outfitsState.filteredOutfits) { outfit ->
                    // load the items for this outfit from the ViewModel
                    var items by remember { mutableStateOf<List<ItemEntry>>(emptyList()) }

                    LaunchedEffect(outfit.outfitId) {
                        items = outfitsViewModel.getItemsList(outfit.outfitId)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color(0xFFE0E0E0))
                            .clickable(
                                enabled = outfitsState.isDeleteActive != DeletionStates.Confirmed.name,
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
                            )
                    ) {
                        // outfit name
                        Text(
                            text = outfit.outfitName,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 12.dp, top = 8.dp, end = 45.dp)
                        )

                        // icon: heart or check
                        if (outfitsState.isDeleteActive == DeletionStates.Inactive.name) {
                            // favorite toggle
                            IconButton(
                                onClick = { outfitsViewModel.toggleFavoritesProperty(outfit) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 8.dp, end = 10.dp)
                                    .size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (outfit.isFavorite)
                                        Icons.Filled.Favorite
                                    else
                                        Icons.Outlined.FavoriteBorder,
                                    contentDescription = if (outfit.isFavorite)
                                        "Remove item from favorites"
                                    else
                                        "Add item to favorites",
                                    tint = if (outfit.isFavorite) Color.Red else Color.Black
                                )
                            }
                        } else if (outfitsState.isDeleteActive == DeletionStates.Active.name) {
                            // deletion candidate icon
                            Icon(
                                imageVector = if (outfit.isDeletionCandidate)
                                    Icons.Filled.CheckCircle
                                else
                                    Icons.Outlined.CheckCircle,
                                contentDescription = if (outfit.isDeletionCandidate)
                                    "Remove item from deletion candidates"
                                else
                                    "Add item to deletion candidates",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 6.dp, end = 6.dp)
                                    .size(22.dp)
                            )
                        }

                        // 2×2 image grid
                        OutfitPreviewGrid(
                            items = items,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                    top = 55.dp
                                )
                                .align(Alignment.TopCenter)
                                .clip(MaterialTheme.shapes.small),
                            iconSize = 40.dp,
                            squarePadding = 3.dp
                        )
                    }
                }
            }

            if (outfitsState.isDeleteActive == DeletionStates.Confirmed.name) {
                DeleteOutfitDialog(outfitsViewModel)
            }
        }
    }
