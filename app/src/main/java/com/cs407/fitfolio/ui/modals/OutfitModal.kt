package com.cs407.fitfolio.ui.modals

import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.components.DeleteOutfitDialog
import com.cs407.fitfolio.ui.enums.DeletionStates
import com.cs407.fitfolio.ui.viewModels.OutfitEntry
import com.cs407.fitfolio.ui.viewModels.OutfitsViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OutfitModal(
    outfitsViewModel: OutfitsViewModel,
    outfitId: String,
    onDismiss: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
) {
    // Observe the current UI state from the ViewModel
    val outfitsState by outfitsViewModel.outfitsState.collectAsStateWithLifecycle()

    // Track sheet state and open to full screen
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Scroll state for the outfit modal
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
            val outfit = outfitsState.outfits.find { it.outfitId == outfitId }
                ?: throw NoSuchElementException("Item with id $outfitId not found")

            // Track whether outfit is editable
            var isEditing by remember { mutableStateOf(false) }

            isEditing = outfitIconBox(
                outfit = outfit,
                outfitsViewModel = outfitsViewModel,
                onDismiss = onDismiss,
                onNavigateToCalendarScreen = onNavigateToCalendarScreen
            )
        }
    }
}

@Composable
fun outfitIconBox (
    outfit: OutfitEntry,
    outfitsViewModel: OutfitsViewModel,
    onDismiss: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit
) : Boolean {
    // Observe the current UI state from the ViewModel
    val outfitsState by outfitsViewModel.outfitsState.collectAsStateWithLifecycle()

    // Track whether outfit is editable
    var isEditing by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // outfit name
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .fillMaxWidth()
        ) {
            Text(
                text = outfit.outfitName,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 15.dp)
            )
        }

        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // outfit photo
            Image(
                painter = painterResource(R.drawable.shirt),
                contentDescription = "Item photo",
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.Center)
            )

            // calendar icon button
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

            // edit icon button
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
                        contentDescription = "Edit outfit",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // delete icon button
            IconButton(
                // TODO: get rid of outfits view model pass in eventually...
                // TODO: add in alert dialog to warn about deleting outfits... make it its own reusable composable??
                onClick = {
                    onDismiss()
                    outfitsViewModel.toggleDeleteState(DeletionStates.Active.name)
                    outfitsViewModel.setDeletionCandidates(outfit)
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete outfit",
                    modifier = Modifier.size(28.dp)
                )
            }

            // favorite icon button
            IconButton(
                onClick = { outfitsViewModel.toggleFavoritesProperty(outfit) },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                if (outfit.isFavorite) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Unfavorite outfit",
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = "Favorite outfit",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }

    LazyColumn() {

    }

        if (outfitsState.isDeleteActive == DeletionStates.Active.name) {
            DeleteOutfitDialog(outfitsViewModel)
        }
    return isEditing
}