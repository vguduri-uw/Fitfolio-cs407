package com.cs407.fitfolio.ui.modals

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.components.DeleteItemDialog
import com.cs407.fitfolio.ui.enums.DeletionStates
import com.cs407.fitfolio.ui.viewModels.ClosetViewModel
import com.cs407.fitfolio.ui.viewModels.ItemEntry
import com.cs407.fitfolio.ui.viewModels.OutfitsViewModel
import java.util.UUID

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ItemModal(
    closetViewModel: ClosetViewModel,
    itemId: String,
    onDismiss: () -> Unit,
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
) {
    // Observe the current UI state from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()

    // Track sheet state and open to full screen
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Scroll state for the item modal
    val scrollState = rememberScrollState()

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
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
                .verticalScroll(scrollState)
        ) {
            // TODO: fix the stale thingy
            val item = closetState.items.find { it.itemId == itemId }
                ?: throw NoSuchElementException("Item with id $itemId not found")

            // Track whether item is editable
            var isEditing by remember { mutableStateOf(false) }

            isEditing = IconBox(
                item = item,
                closetViewModel = closetViewModel,
                onDismiss = onDismiss,
                onNavigateToCalendarScreen = onNavigateToCalendarScreen
            )
        }
    }
}

@Composable
fun IconBox (
    item: ItemEntry,
    closetViewModel: ClosetViewModel,
    onDismiss: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit
) : Boolean {
    // Observe the current UI state from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()

    LaunchedEffect(closetState.deletionCandidates) {
        Log.d("deletionCandidtates", closetState.deletionCandidates.toString())
    }

    // Track whether item is editable
    var isEditing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(Color.White)
    ) {
        // TODO: get icons to line up vertically
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Calendar icon button
                IconButton(
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

                // Title
                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.titleLarge
                )

                // Edit icon button
                IconButton(
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
            }

            // Image and bottom icons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.shirt),
                    contentDescription = "Item photo",
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.Center)
                )

                // Delete icon button
                IconButton(
                    // TODO: get rid of outfits view model pass in eventually...
                    // TODO: add in alert dialog to warn about deleting outfits... make it its own reusable composable??
                    onClick = {
                        onDismiss()
                        closetViewModel.toggleDeleteState(DeletionStates.Active.name)
                        closetViewModel.setDeletionCandidates(item)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
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
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    if (item.isFavorite) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Unfavorite item",
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            contentDescription = "Favorite item",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        if (closetState.isDeleteActive == DeletionStates.Active.name) {
            DeleteItemDialog(closetViewModel)
        }
    }

    return isEditing
}
