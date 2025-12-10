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
import androidx.compose.material.icons.outlined.ArrowDropDown
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.cs407.fitfolio.R
import com.cs407.fitfolio.data.ItemEntry
import com.cs407.fitfolio.enums.DeletionStates
import com.cs407.fitfolio.ui.components.DeleteOutfitDialog
import com.cs407.fitfolio.ui.components.TopHeader
import com.cs407.fitfolio.ui.components.WeatherCarousel
import com.cs407.fitfolio.ui.modals.OutfitModal
import com.cs407.fitfolio.ui.modals.SettingsModal
import com.cs407.fitfolio.ui.theme.DrySage
import com.cs407.fitfolio.ui.theme.FloralWhite
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.ui.theme.LightPeachFuzz
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.OutfitsState
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import com.cs407.fitfolio.viewModels.UserViewModel
import com.cs407.fitfolio.viewModels.WeatherViewModel
import com.cs407.fitfolio.ui.theme.GoldenApricot
import com.cs407.fitfolio.ui.theme.SoftRust

@Composable
fun MyOutfitsScreen(
    onNavigateToCalendarScreen: () -> Unit,
    onSignOut: () -> Unit,
    outfitsViewModel: OutfitsViewModel,
    weatherViewModel: WeatherViewModel,
    closetViewModel: ClosetViewModel, // TODO: remove after testing
    userViewModel: UserViewModel
) {
    // observes current ui state from the outfits view model
    val outfitsState by outfitsViewModel.outfitsState.collectAsStateWithLifecycle()
    //for weather
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()

//    // TODO: remove after testing
//    LaunchedEffect(Unit) {
//        val dbOutfits = outfitsViewModel.getOutfits()
//
//        if (dbOutfits.isEmpty()) {
//            AddTestItemData(closetViewModel, outfitsViewModel)
//        }
//    }

    // re-filter when an item is added or deleted
    LaunchedEffect(outfitsState.outfits) {
        outfitsViewModel.applyFilters()
    }

    // track settings modal state
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
            // profile image, my outfits title, information icon
            TopHeader("My Outfits", userViewModel = userViewModel )

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

        Spacer(modifier = Modifier.size(10.dp))

        // settings button
        IconButton(
            onClick = { showSettings = true }, // TODO: add settings onClick lambda
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(R.drawable.cog),
                contentDescription = "Settings",
                Modifier.size(36.dp)
            )
        }

        // pull up settings modal
        if (showSettings) {
            SettingsModal(
                onDismiss = { showSettings = false },
                userViewModel = userViewModel,
                onSignOut = onSignOut
            )
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

        if (outfitsState.isDeleteActive == DeletionStates.Active.name) {
            // Exit delete mode
            FloatingActionButton(
                onClick = { outfitsViewModel.toggleDeleteState(DeletionStates.Inactive.name) },
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
                    Text("Cancel", fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.size(10.dp))

                    Icon(
                        painter = painterResource(R.drawable.clear),
                        contentDescription = "Exit delete mode",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(20.dp)
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
                    Text("Delete", color = Color.White, fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.size(10.dp))

                    Icon(
                        painter = painterResource(R.drawable.delete),
                        contentDescription = "Confirm delete mode",
                        tint = Color.White,
                        modifier = Modifier
                            .size(22.dp)
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
            .background(FloralWhite),
        contentAlignment = Alignment.Center
    ) {
        when {
            item == null -> {
                Image(
                    painter = painterResource(R.drawable.hanger),
                    contentDescription = "Empty slot",
                    modifier = Modifier
                        .size(35.dp),
                    contentScale = ContentScale.Fit
                )
            }

            item.itemPhotoUri.isNotBlank() -> {
                Image(
                    painter = rememberAsyncImagePainter(item.itemPhotoUri),
                    contentDescription = item.itemName,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                Image(
                    painter = painterResource(R.drawable.hanger),
                    contentDescription = "Placeholder for ${item.itemName}",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
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

@Composable
fun FilterRow(outfitsState: OutfitsState, outfitsViewModel: OutfitsViewModel) {
    // tracks whether tags dropdown is expanded
    var expanded by remember { mutableStateOf(false) }

    // tracks whether search dialog is visible or not
    var showSearchDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(), // matches rest of screen
        verticalAlignment = Alignment.CenterVertically
    ) {
        // favorites filter toggle
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz)
                .size(40.dp),
        contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = {
                    outfitsViewModel.toggleFavoritesState()
                }
            ) {
                Icon(
                    painter = if (outfitsState.isFavoritesActive)
                        painterResource(R.drawable.heart_filled_red)
                    else
                        painterResource(R.drawable.heart_outline),
                    contentDescription = if (outfitsState.isFavoritesActive) "Remove favorites filter" else "Filter by favorites",
                    tint = if (outfitsState.isFavoritesActive) Color.Red else Color.Black,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // shuffle button
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz)
                .size(40.dp),

            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { outfitsViewModel.shuffleOutfits() }) {
                Icon(
                    painter = painterResource(R.drawable.shuffle),
                    contentDescription = "shuffle",
                    tint = Color.Black,
                    modifier = Modifier.size(23.dp),
                )
            }
        }

        // search button
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz)
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = {
                outfitsViewModel.toggleSearchState(true)
                showSearchDialog = true
            }) {
                Icon(
                    painter = painterResource(R.drawable.loupe),
                    contentDescription = "search",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
            }
        }

        // search bar dialog
        if (showSearchDialog) {
            AlertDialog(
                title = {
                    Text(text = "Search for an outfit", fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold)
                },
                text = {
                    TextField(
                        value = outfitsState.searchQuery,
                        onValueChange = { it -> outfitsViewModel.updateSearchQuery(it) },
                        placeholder = { Text("Enter outfit name", fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightPeachFuzz,
                            unfocusedContainerColor = LightPeachFuzz,
                            disabledContainerColor = LightPeachFuzz.copy(alpha = 0.7f)
                        ),
                        textStyle = TextStyle(fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold)
                    )
                },
                onDismissRequest = { showSearchDialog = false },
                confirmButton = {
                    Button(onClick = {
                        outfitsViewModel.applyFilters()
                        showSearchDialog = false
                    }) {
                        Text(text = "Search", fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = FloralWhite,
            )
        }

        // tags drop down menu
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz)
                .padding(horizontal = 10.dp)
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Text(
                    text = "Tags",
                    style = TextStyle(
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.size(4.dp))

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
                // note: outfitsState.allTags is the master list of tags/types
                outfitsState.tags
                    .sortedByDescending { it in outfitsState.activeTags }
                    .forEach { tag ->
                        DropdownMenuItem(
                            text = { Text(tag, fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold) },
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
                                        painter = painterResource(R.drawable.clear),
                                        contentDescription = "Remove tag",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(15.dp)
                                            .weight(.25f),
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.add),
                                        contentDescription = "Add tag",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(15.dp)
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
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz)
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            when (outfitsState.isDeleteActive) {
                DeletionStates.Inactive.name -> {
                    IconButton(onClick = {
                        outfitsViewModel.toggleDeleteState(DeletionStates.Active.name)
                        Toast.makeText(
                            context,
                            "Select all outfits to delete, then press Delete. Otherwise, press Cancel.",
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
                        enabled = outfitsState.isDeleteActive == DeletionStates.Inactive.name
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
                        enabled = outfitsState.isDeleteActive == DeletionStates.Inactive.name
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

        // clear filters button
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz)
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { outfitsViewModel.clearFilters() }) {
                Icon(
                    painter = painterResource(R.drawable.clear),
                    contentDescription = "clear filters",
                    tint = Color.Black,
                    modifier = Modifier.size(17.dp)
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
            text = "No outfits found.",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            verticalItemSpacing = 0.dp,
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
                        .padding(bottom = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .shadow(
                                elevation = 3.dp,
                                shape = MaterialTheme.shapes.medium,
                                clip = false
                            )
                            .clip(MaterialTheme.shapes.medium)
                            .background(LightPeachFuzz)
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
                            fontFamily = Kudryashev_Display_Sans_Regular,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 12.dp, top = 8.dp, end = 45.dp)
                        )

                        // icon: heart or delete selection
                        if (outfitsState.isDeleteActive == DeletionStates.Inactive.name) {
                            IconButton(
                                onClick = { outfitsViewModel.toggleFavoritesProperty(outfit) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 10.dp, end = 10.dp)
                                    .size(24.dp)
                            ) {
                                Icon(
                                    painter = if (outfit.isFavorite)
                                        painterResource(R.drawable.heart_filled_red)
                                    else
                                        painterResource(R.drawable.heart_outline),
                                    contentDescription = if (outfit.isFavorite)
                                        "Remove item from favorites"
                                    else
                                        "Add item to favorites",
                                    tint = if (outfit.isFavorite) Color.Red else Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else if (outfitsState.isDeleteActive == DeletionStates.Active.name) {
                            Icon(
                                painter = painterResource(R.drawable.delete),
                                contentDescription = if (outfit.isDeletionCandidate)
                                    "Remove item from deletion candidates"
                                else
                                    "Add item to deletion candidates",
                                tint = if (outfit.isDeletionCandidate) Color.Red else Color.Gray,
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
        }

        if (outfitsState.isDeleteActive == DeletionStates.Confirmed.name) {
            DeleteOutfitDialog(outfitsViewModel)
        }
    }
}