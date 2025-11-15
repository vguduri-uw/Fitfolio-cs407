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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.components.DeleteOutfitDialog
import com.cs407.fitfolio.ui.enums.DeletionStates
import com.cs407.fitfolio.ui.viewModels.ItemEntry
import com.cs407.fitfolio.ui.viewModels.OutfitEntry
import com.cs407.fitfolio.ui.viewModels.OutfitsViewModel
import androidx.compose.foundation.lazy.items

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

            outfitHeaderBox(
                outfit = outfit,
                isEditing = isEditing,
                outfitsViewModel = outfitsViewModel
            )

            outfitIconBox(
                outfit = outfit,
                outfitsViewModel = outfitsViewModel,
                onDismiss = onDismiss,
                onNavigateToCalendarScreen = onNavigateToCalendarScreen,
                isEditing = isEditing
            )

            // outfit description
            DescriptionCard(
                outfit = outfit,
                isEditing = isEditing,
                outfitsViewModel = outfitsViewModel
            )

            // items list
            ItemsInOutfitCard(
                outfit = outfit,
                isEditing = isEditing,
                outfitsViewModel = outfitsViewModel
            )

            // tags
            TagsEditableCard(
                outfit = outfit,
                outfitsViewModel = outfitsViewModel
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
    imageRes: Int
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
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .padding(bottom = 15.dp)
        ) {
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

                if (editMode) {
                    // add tag
                    IconButton (
                        onClick = { showAddDialog = true },
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = "Add tag")
                    }
                    // save changes
                    IconButton (
                        onClick = {
                            val current = outfit.outfitTags.toSet()
                            val toAdd = selectedTags - current
                            val toRemove = current - selectedTags
                            toAdd.forEach { t -> outfitsViewModel.editOutfitTags(outfit, t, isRemoving = false) }
                            toRemove.forEach { t -> outfitsViewModel.editOutfitTags(outfit, t, isRemoving = true) }
                            editMode = false
                        },
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Save changes")
                    }
                } else {
                    // edit mode
                    IconButton (
                        onClick = { editMode = true }
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit tags")
                    }
                }
            }

            // show ALL global tags as chips (selectable only when editing)
            val allTags = outfitsState.tags
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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

// card for viewing and editing the outfit's name
// edit mode unlocks renaming the outfit and saving the updated name to the ViewModel
// displayed at the top of the outfit modal for quick identification and editing
@Composable
fun outfitHeaderBox (
    outfit: OutfitEntry,
    isEditing: Boolean,
    outfitsViewModel: OutfitsViewModel
) {
    // track outfit editing state
    var isEditing by remember { mutableStateOf(isEditing) }

    // track outfit name
    var outfitName by remember { mutableStateOf(outfit.outfitName) }

    // outfit name
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(Color.White)
            .fillMaxWidth()
    ) {
        TextField(
            value = outfitName,
            onValueChange = { newOutfitName: String ->
                // updates locally within modal
                outfitName = newOutfitName
            },
            enabled = isEditing,
            textStyle = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 25.sp, textAlign = TextAlign.Center, color = Color.Black),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .padding(vertical = 15.dp)
        )
        // edit icon button
        IconButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = { isEditing = !isEditing }
        ) {
            if (isEditing) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Save edits",
                    modifier = Modifier.size(28.dp)
                )
                // updates globally
                outfitsViewModel.editOutfitName(outfit, outfitName)
            } else {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit outfit name",
                    modifier = Modifier.size(28.dp)
                )
            }
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
    onNavigateToCalendarScreen: () -> Unit,
    isEditing: Boolean
) : Boolean {
    // Observe the current UI state from the ViewModel
    val outfitsState by outfitsViewModel.outfitsState.collectAsStateWithLifecycle()

    // track outfit editing state
    var isEditing by remember { mutableStateOf(isEditing) }

    // track outfit photo
    var outfitPhoto by remember { mutableIntStateOf(outfit.outfitPhoto) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // todo: figure out logic on how to display updated photo
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

                    // updates globally
                    outfitsViewModel.editOutfitPhoto(outfit, outfitPhoto)
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit outfit photo",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // delete icon button
            IconButton(
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

// card for viewing and editing the outfit's description
// edit mode unlocks modifying the text and saving the updated description to the ViewModel
// provides context and details about the outfit to help with organization and recall
@Composable
private fun DescriptionCard(
    outfit: OutfitEntry,
    isEditing: Boolean,
    outfitsViewModel: OutfitsViewModel
) {
    // tracks state of whether editing is enabled
    var localEditing by remember { mutableStateOf(isEditing) }

    // tracks state of the outfit's description's value
    var outfitDescription by remember { mutableStateOf(outfit.outfitDescription) }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(Color.White)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(start = 15.dp, top = 15.dp)
            )
            TextField(
                value = outfitDescription,
                onValueChange = { outfitDescription = it },
                enabled = localEditing,
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    textAlign = TextAlign.Left,
                    color = Color.Black
                ),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
            )
        }

        // edit button
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = {
                localEditing = !localEditing
                if (!localEditing) {
                    outfitsViewModel.editOutfitDescription(outfit, outfitDescription)
                }
            }
        ) {
            if (localEditing) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Save edits",
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit outfit description",
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

// card for viewing and managing the list of items assigned to this outfit
// edit mode unlocks multi-select delete, allowing users to remove one or more items at once
// selected items can be removed in a single confirmation step; removed items stay in the Closet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemsInOutfitCard(
    outfit: OutfitEntry,
    isEditing: Boolean,
    outfitsViewModel: OutfitsViewModel
) {
    // tracks state of whether editing is enabled
    var localEditing by remember { mutableStateOf(isEditing) }

    // tracks state of ids of items selected to be deleted
    var selectedIds by remember(outfit.outfitId) { mutableStateOf<Set<String>>(emptySet()) }

    // tracks state of alert dialog
    var showBatchDeleteDialog by remember { mutableStateOf(false) }

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
            // header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Items in this outfit",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                // actions on the right
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (localEditing) {
                        // trash/delete icon (enabled only if there's a selection)
                        IconButton(
                            onClick = { showBatchDeleteDialog = true },
                            enabled = selectedIds.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete selected items",
                                modifier = Modifier.size(28.dp),
                                tint = if (selectedIds.isNotEmpty()) Color.Red else Color.Gray
                            )
                        }
                        // confirm/save changes (exists edit mode)
                        IconButton(onClick = {
                            localEditing = false
                            selectedIds = emptySet()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Done editing",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        // enter edit mode
                        IconButton(onClick = { localEditing = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit list of items",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // items row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(
                    items = outfit.itemList,
                    key = { it.itemId }
                ) { item ->
                    val selected = item.itemId in selectedIds

                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color(0xFFF7F7F7))
                            .clickable(enabled = localEditing) {
                                selectedIds = if (selected) selectedIds - item.itemId
                                else selectedIds + item.itemId
                            }
                    ) {
                        ItemCard(
                            name = item.itemName,
                            type = item.itemType,
                            imageRes = R.drawable.shirt // swap to item.itemPhoto when ready
                        )

                        if (localEditing && selected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(20.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(Color.Black.copy(alpha = 0.75f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // does deletion in batches
    if (showBatchDeleteDialog) {
        val count = selectedIds.size
        AlertDialog(
            onDismissRequest = { showBatchDeleteDialog = false },
            title = { Text("Remove $count item${if (count == 1) "" else "s"} from this outfit?") },
            text = {
                Text(
                    "This will remove the selected item${if (count == 1) "" else "s"} from this outfit. " +
                            "They will remain in your Closet. This action cannot be undone."
                )
            },
            confirmButton = {
                Button(onClick = {
                    // perform the deletions
                    if (selectedIds.isNotEmpty()) {
                        // Map back to actual ItemEntry objects
                        val toDelete = outfit.itemList.filter { it.itemId in selectedIds }
                        toDelete.forEach { item ->
                            outfitsViewModel.removeItemFromItemsList(outfit, item)
                        }
                    }
                    // exits edit mode
                    showBatchDeleteDialog = false
                    localEditing = false
                    selectedIds = emptySet()
                }) { Text("Delete") }
            },
            dismissButton = {
                Button(onClick = {
                    // exits edit mode
                    showBatchDeleteDialog = false
                    localEditing = false
                    selectedIds = emptySet()
                }) { Text("Cancel") }
            }
        )
    }
}