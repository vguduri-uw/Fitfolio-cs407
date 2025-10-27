package com.cs407.fitfolio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import androidx.lifecycle.viewmodel.compose.viewModel
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

    // Scroll state for the item type row
    val scrollState = rememberScrollState()

    // Remembers the selected item type for filtering
    var selectedType by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter)
            ) {
                Image(
                    // TODO: replace with actual profile image
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "User profile image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "My Closet", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = {}) { // TODO: add info onClick lambda
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = "Information",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
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
        Column() {
            // TODO: update icons
            // TODO: implement filtering
            // Scrollable row of item types for filtering
            Row(modifier = Modifier
                .horizontalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(
                            color = if (selectedType == "All") MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
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
                            .padding(horizontal = 8.dp)
                            .background(
                                color = if (selectedType == itemType) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
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
            }

            // Row of action buttons for filtering, shuffling, and searching
            Row() {

            }

        }
    }
}

