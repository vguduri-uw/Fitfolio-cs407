package com.cs407.fitfolio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.R

@Composable
fun MyOutfitsScreen(
    onNavigateToClosetScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToWardrobeScreen: () -> Unit,
    onNavigateToAddScreen: () -> Unit
) {
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
//                .background(Color(0xFF000000))
        ) {
            // profile image, my outfits title, information icon
            TopHeaderSection()

            Spacer(modifier = Modifier.size(10.dp))

            // horizontally scrollable weather info row
            WeatherRow()

            Spacer(modifier = Modifier.size(10.dp))

            // search bar, tags filter, favorites toggle, shuffle button
            SearchRow()

            Spacer(modifier = Modifier.size(10.dp))

            // vertically scrollable outfits grid
            OutfitGrid()
        }

        // settings button
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
fun SearchRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(), // matches rest of screen
        verticalAlignment = Alignment.CenterVertically
    ) {
        // favorites filter toggle
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0))
                .padding(horizontal = 10.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            // TODO: replace with toggle button
            Icon(
                Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorites",
                tint = Color.Black,
                modifier = Modifier.size(20.dp),
            )
        }

        // shuffle button
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0))
                .padding(horizontal = 10.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            // TODO: replace with shuffle button
            Icon(
                painter = painterResource(id = R.drawable.shuffle),
                contentDescription = "Shuffle",
                tint = Color.Black,
                modifier = Modifier.size(20.dp),
            )
        }

        // search bar
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0))
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            // TODO: swap out with real search logic
            Row {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        }

        // tags drop down menu
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFE0E0E0))
                .padding(horizontal = 10.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row {
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                )
                Icon(
                    Icons.Outlined.ArrowDropDown,
                    contentDescription = "Favorites",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}


@Composable
fun OutfitGrid() {}
