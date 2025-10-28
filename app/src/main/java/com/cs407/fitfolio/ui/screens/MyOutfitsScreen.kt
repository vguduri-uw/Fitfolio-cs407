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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.cs407.fitfolio.ui.viewModels.OutfitsState
import com.cs407.fitfolio.ui.viewModels.OutfitsViewModel

@Composable
fun MyOutfitsScreen(
    onNavigateToClosetScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToWardrobeScreen: () -> Unit,
    onNavigateToAddScreen: () -> Unit,
    onNavigateToSignUpScreen: () -> Unit,
    onNavigateToSignInScreen: () -> Unit,
    outfitsViewModel: OutfitsViewModel
) {
    // observes current ui state from the outfits view model
    val outfitsState by outfitsViewModel.outfitsState.collectAsStateWithLifecycle()

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
            WeatherRow()

            Spacer(modifier = Modifier.size(10.dp))

            // search bar, tags filter, favorites toggle, shuffle button
            SearchRow(outfitsState, outfitsViewModel)

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
            SettingsModal(onDismiss = { showSettings = false })
        }

        // navigate to sign up and sign in screens
        Column(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .padding(bottom = 16.dp)
        ) {
            Button(
                onClick = { onNavigateToSignUpScreen() },
                modifier = Modifier.width(100.dp)
            ) {
                Text("Sign Up")
            }
            Button(
                onClick = { onNavigateToSignInScreen() },
                modifier = Modifier.width(100.dp)
            ) {
                Text("Sign In")
            }
        }
    }
}

// TODO: Veda will provide weather section
@Composable
fun WeatherRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "weather row coming soon",
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )
    }
}

@Composable
fun SearchRow(outfitsState: OutfitsState, outfitsViewModel: OutfitsViewModel) {
    // tracks favorites filter toggle status
    // todo: move to view model
    var isFilteredByFavorites by remember { mutableStateOf(false) }

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
            IconButton(onClick = {
                isFilteredByFavorites = !isFilteredByFavorites
                outfitsViewModel.filterByFavorites()
            }) {
                Icon(
                    imageVector = if (isFilteredByFavorites) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFilteredByFavorites) "remove favorites filter" else "filter by favorites",
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
            IconButton(onClick = {
                // todo: open search dialog, then call outfitsViewModel.searchOutfits(...)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "search",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
            }
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
                outfitsState.allTags
                    .sortedByDescending { it in outfitsState.activeTags }
                    .forEach { tag ->
                    DropdownMenuItem(
                        text = { Text(tag) },
                        onClick = {
                            outfitsViewModel.filterByTags(tag)
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


@Composable
fun OutfitGrid(outfitsState: OutfitsState, outfitsViewModel: OutfitsViewModel) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth(),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(30) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((150..250).random().dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Text("Outfit $index")
            }
        }
    }
}
