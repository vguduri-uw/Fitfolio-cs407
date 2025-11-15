package com.cs407.fitfolio.ui.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.enums.DeletionStates
import com.cs407.fitfolio.ui.viewModels.ClosetViewModel
import com.cs407.fitfolio.ui.viewModels.OutfitsViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ItemModal(
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel,
    itemId: String,
    onDismiss: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
) {
    // Track sheet state and open to full screen
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Color(0xFFE0E0E0),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 24.dp, end = 24.dp)
        ) {
            IconBox(
                itemId = itemId,
                closetViewModel = closetViewModel,
                onDismiss = onDismiss,
                onNavigateToCalendarScreen = onNavigateToCalendarScreen
            )

            ItemInformation(
                itemId = itemId,
                closetViewModel = closetViewModel,
                outfitsViewModel = outfitsViewModel,
                onNavigateToCalendarScreen = onNavigateToCalendarScreen,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun IconBox (
    itemId: String,
    closetViewModel: ClosetViewModel,
    onDismiss: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit
) {
    // Observe the current UI state from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    val item = closetState.items.find { it.itemId == itemId }
        ?: throw NoSuchElementException("Item with id $itemId not found")

    // Track whether item is editable
    var isEditing by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Item name
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .fillMaxWidth()
        ) {
            Text(
                text = item.itemName,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 15.dp)
                    .align(Alignment.Center)
            )
        }

        // Item photo and icon buttons
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // Item photo
            Image(
                painter = painterResource(R.drawable.shirt),
                contentDescription = "Item photo",
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.Center)
            )

            // Calendar icon button
            IconButton(
                modifier = Modifier.align(Alignment.TopStart),
                onClick = { // TODO: make it show the days in the calendar its featured??
                    onDismiss()
                    onNavigateToCalendarScreen()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.schedule),
                    contentDescription = "Calendar",
                    modifier = Modifier.size(28.dp)
                )
            }

            // Edit icon button
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { isEditing = !isEditing }
            ) {
                if (isEditing) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Save edits",
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit item",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Delete icon button
            IconButton(
                onClick = {
                    onDismiss()
                    closetViewModel.setDeletionCandidates(item)
                    closetViewModel.toggleDeleteState(DeletionStates.Confirmed.name)
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete item",
                    modifier = Modifier.size(28.dp)
                )
            }

            // Favorite icon button
            IconButton(
                onClick = { closetViewModel.toggleFavoritesProperty(item) },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                if (item.isFavorite) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Unfavorite item",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Red
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite item",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun ItemInformation(
    itemId: String,
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel,
    onNavigateToCalendarScreen: () -> Unit,
    modifier: Modifier
) {
    // Observe the current UI state from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    val item = closetState.items.find { it.itemId == itemId }
        ?: throw NoSuchElementException("Item with id $itemId not found")

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Item description
        item {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.White)
                    .fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(15.dp)
                ) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    if (item.itemDescription.isNotEmpty()) {
                        Text(
                            text = item.itemDescription,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text(
                            text = "No description found.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Outfits featuring this item
        item {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.White)
                    .fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(15.dp)
                ) {
                    Text(
                        text = "Outfits featuring this item",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    if (item.outfitList.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(item.outfitList.size) { idx ->
                                val outfit = item.outfitList[idx]
                                OutfitsCard(
                                    outfitName = outfit.outfitName,
                                    outfitId = outfit.outfitId,
                                    outfitsViewModel = outfitsViewModel,
                                    onNavigateToCalendarScreen = onNavigateToCalendarScreen,
                                    imageRes = R.drawable.shirt // swap to item.itemPhoto when ready
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No outfits found."
                        )
                    }
                }
            }
        }

        item {
            TagsAndItemType(
                itemId = itemId,
                closetViewModel = closetViewModel,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun OutfitsCard(
    outfitName: String,
    outfitId: String,
    outfitsViewModel: OutfitsViewModel,
    onNavigateToCalendarScreen: () -> Unit,
    imageRes: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(Color(0xFFF7F7F7))
            .padding(10.dp)
            .fillMaxSize()
        // TODO: uncomment when ready
            /*clickable(
                OutfitsModal(
                    outfitsViewModel = outfitsViewModel,
                    outfitId = outfitId,
                    onDismiss = { onNavigateToClosetScreen(itemToBeShown: item.itemId) }, // ???
                    onNavigateToCalendarScreen = onNavigateToCalendarScreen
                )
            )*/
        // outfitsViewModel, outfitId, onDismiss, onNavigateToCalendarScreen

    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = "$outfitName image",
            modifier = Modifier.size(72.dp)
        )
        Text(
            text = outfitName,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TagsAndItemType(
    itemId: String,
    closetViewModel: ClosetViewModel,
    modifier: Modifier
) {
    // Observe the current UI state from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    val item = closetState.items.find { it.itemId == itemId }
        ?: throw NoSuchElementException("Item with id $itemId not found")
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Item tags
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .padding(15.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Item Tags",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.itemTags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .background(Color(0xFFF7F7F7))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(tag)
                        }
                    }
                }
            }
        }

        // Item type
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .padding(15.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Item Type",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(Color(0xFFF7F7F7))
                        .clickable { expanded = true }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(item.itemType)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    closetState.itemTypes.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                closetViewModel.editItemType(item, option)
                                expanded = false
                            },
                            text = {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


