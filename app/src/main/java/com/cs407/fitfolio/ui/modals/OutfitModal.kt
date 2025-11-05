package com.cs407.fitfolio.ui.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.components.DeleteOutfitDialog
import com.cs407.fitfolio.ui.enums.DeletionStates
import com.cs407.fitfolio.ui.viewModels.OutfitEntry
import com.cs407.fitfolio.ui.viewModels.OutfitsViewModel

// modal sheet that displays full outfit details and actions
// shows outfit photo, description, items, and tags, with editing modes
// opened when the user selects an outfit from the outfits screen
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
            val outfit = outfitsState.outfits.find { it.outfitId == outfitId }
                ?: throw NoSuchElementException("Item with id $outfitId not found")

            // todo: figure out logic for how to edit outfit (change name, description, and items in outfit)
            // Track whether outfit is editable
            var isEditing by remember { mutableStateOf(false) }

            isEditing = outfitIconBox(
                outfit = outfit,
                outfitsViewModel = outfitsViewModel,
                onDismiss = onDismiss,
                onNavigateToCalendarScreen = onNavigateToCalendarScreen
            )

            outfitInformation(
                outfit = outfit,
                outfitsViewModel = outfitsViewModel,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

// reusable icon wrapper that provides a consistent touch target and behavior
// used for small icon actions in cards (edit, add, save) to improve UI clarity
// replaces IconButton for cleaner spacing and visuals
@Composable
private fun ClickableIcon(
    onClick: () -> Unit,
    contentDescription: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)                        // consistent hit target
            .clip(MaterialTheme.shapes.small)
            .background(Color.Transparent)
            .clickable(onClick = onClick)
            .padding(8.dp),                     // icon padding
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// dialog for adding a new global tag to the list of available outfit tags
// user enters a tag name, and it becomes selectable for all outfits
// opened from the tags card while in edit mode
@Composable
private fun AddTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a tag") },
        text = {
            androidx.compose.material3.OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Tag name") },
                singleLine = true
            )
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = { onConfirm(text) }) {
                Text("Add")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// confirmation dialog for dangerous or irreversible actions
// displays a title, message, and confirm/cancel actions
// used for deleting a global tag from all outfits
@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// small card that displays a single clothing item inside an outfit
// shows photo, item name, and type for quick visual reference
// used inside the "items in this outfit" horizontal list
@Composable
private fun ItemCard(
    name: String,
    type: String,
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
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = "$name image",
            modifier = Modifier.size(72.dp)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center
        )
        Text(
            text = type,
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF666666)),
            textAlign = TextAlign.Center
        )
    }
}

// chip used for showing a single tag with optional interaction
// supports select/deselect for the current outfit when editing
// can show a delete icon in edit mode to remove a tag globally
@Composable
private fun TagChipSelectable(
    text: String,
    selected: Boolean,
    editing: Boolean,
    onToggleForOutfit: () -> Unit,
    onDeleteGlobal: () -> Unit
) {
    // Your chosen colors
    val backgroundColor = when {
        selected && editing -> Color(0xFFB8B8B8) // darker gray (when selected + editing)
        selected -> Color(0xFFD0D0D0)            // medium gray (when selected)
        else -> Color(0xFFF2F2F2)                // light gray (default)
    }

    // if not editing, no clickable modifier
    val chipModifier =
        if (editing) {
            Modifier
                .clip(MaterialTheme.shapes.small)
                .background(backgroundColor)
                .clickable { onToggleForOutfit() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        } else {
            Modifier
                .clip(MaterialTheme.shapes.small)
                .background(backgroundColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        }

    Row(
        modifier = chipModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                color = Color.Black
            )
        )

        // show small delete icon ONLY when editing
        if (editing) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete tag",
                tint = Color.Red.copy(alpha = 0.85f),
                modifier = Modifier
                    .size(14.dp)
                    .clickable { onDeleteGlobal() }
            )
        }
    }
}

// card for viewing and editing tags assigned to this outfit
// edit mode unlocks selecting tags, adding new tags, or deleting global tags
// displays all global tags so user can modify which apply to this outfit
@Composable
private fun TagsEditableCard(
    outfit: OutfitEntry,
    outfitsViewModel: OutfitsViewModel
) {
    val outfitsState by outfitsViewModel.outfitsState.collectAsStateWithLifecycle()

    var editMode by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var pendingGlobalDelete by remember { mutableStateOf<String?>(null) }

    // local selection buffer for tags; sync when outfit changes
    var selectedTags by remember(outfit.outfitId) { mutableStateOf(outfit.outfitTags.toSet()) }
    LaunchedEffect(outfit.outfitTags) { selectedTags = outfit.outfitTags.toSet() }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(Color.White)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(15.dp)) {

            // header row: title + actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (editMode) {
                        // + Add tag
                        ClickableIcon(
                            onClick = { showAddDialog = true },
                            contentDescription = "Add tag"
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null)
                        }
                        // ✓ Save changes
                        ClickableIcon(
                            onClick = {
                                val current = outfit.outfitTags.toSet()
                                val toAdd = selectedTags - current
                                val toRemove = current - selectedTags
                                toAdd.forEach { t -> outfitsViewModel.editOutfitTags(outfit, t, isRemoving = false) }
                                toRemove.forEach { t -> outfitsViewModel.editOutfitTags(outfit, t, isRemoving = true) }
                                editMode = false
                            },
                            contentDescription = "Save changes"
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    } else {
                        // ✎ Edit mode
                        ClickableIcon(
                            onClick = { editMode = true },
                            contentDescription = "Edit tags"
                        ) {
                            Icon(Icons.Outlined.Edit, contentDescription = null)
                        }
                    }
                }
            }

            // show ALL global tags as chips (selectable only when editing)
            val allTags = outfitsState.tags
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                items(allTags.size) { idx ->
                    val tag = allTags[idx]
                    val isSelected = tag in selectedTags
                    TagChipSelectable(
                        text = tag,
                        selected = isSelected,
                        editing = editMode,
                        onToggleForOutfit = {
                            selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                        },
                        onDeleteGlobal = {
                            if (editMode) pendingGlobalDelete = tag
                        }
                    )
                }
            }
        }

        // add-tag dialog
        if (showAddDialog) {
            AddTagDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { newTag ->
                    val t = newTag.trim()
                    if (t.isNotEmpty()) {
                        outfitsViewModel.addTag(t)      // add globally
                        selectedTags = selectedTags + t // preselect to apply on Save
                    }
                    showAddDialog = false
                }
            )
        }

        // confirm global delete dialog
        pendingGlobalDelete?.let { tag ->
            ConfirmDialog(
                title = "Delete tag everywhere?",
                message = "Deleting \"$tag\" removes it from the global tag list and from ALL outfits. This cannot be undone.",
                confirmText = "Delete",
                onDismiss = { pendingGlobalDelete = null },
                onConfirm = {
                    outfitsViewModel.deleteTag(tag)
                    selectedTags = selectedTags - tag
                    pendingGlobalDelete = null
                }
            )
        }
    }
}

// top section of the outfit modal showing the outfit name, image, and action icons
// provides actions to favorite, edit, delete, or open calendar for the outfit
// remains pinned at the top while other content scrolls underneath
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
        if (outfitsState.isDeleteActive == DeletionStates.Active.name) {
            DeleteOutfitDialog(outfitsViewModel)
        }
    return isEditing
}

// main scrollable content of the outfit modal showing description, items, and tags
// wraps the detailed information cards inside a LazyColumn for scrolling
// placed below the pinned top section of the modal
@Composable
fun outfitInformation(
    outfit: OutfitEntry,
    outfitsViewModel: OutfitsViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // description
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
                    Text(
                        text = outfit.outfitDescription,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // items in this outfit
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
                        text = "Items in this outfit",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(outfit.itemList.size) { idx ->
                            val item = outfit.itemList[idx]
                            ItemCard(
                                name = item.itemName,
                                type = item.itemType,
                                imageRes = R.drawable.shirt // swap to item.itemPhoto when ready
                            )
                        }
                    }
                }
            }
        }

        // tags (editable)
        item {
            TagsEditableCard(
                outfit = outfit,
                outfitsViewModel = outfitsViewModel
            )
        }
    }
}