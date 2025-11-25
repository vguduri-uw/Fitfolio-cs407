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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.enums.DeletionStates
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import com.cs407.fitfolio.data.OutfitEntry

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ItemModal(
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel,
    itemId: Int,
    onDismiss: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit
) {
    // Track sheet state and open to full screen
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Tracks whether item is in editing mode
    var isEditing by remember { mutableStateOf(false) }

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
                isEditing = isEditing,
                onToggleEditing = { isEdit -> isEditing = isEdit },
                onNavigateToCalendarScreen = onNavigateToCalendarScreen
            )

            ItemInformation(
                itemId = itemId,
                closetViewModel = closetViewModel,
                outfitsViewModel = outfitsViewModel,
                isEditing = isEditing,
                onToggleEditing = { isEdit -> isEditing = isEdit },
                onNavigateToCalendarScreen = onNavigateToCalendarScreen,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

// Item photo and icon buttons
@Composable
fun IconBox (
    itemId: Int,
    closetViewModel: ClosetViewModel,
    onDismiss: () -> Unit,
    isEditing: Boolean,
    onToggleEditing: (Boolean) -> Unit,
    onNavigateToCalendarScreen: () -> Unit
) {
    // Observe the current UI state from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    val item = closetState.items.find { it.itemId == itemId }
        ?: throw NoSuchElementException("Item with id $itemId not found")

    // Mutable states
    var expanded by remember { mutableStateOf(false) }
    var selectedItemType by remember { mutableStateOf(item.itemType) }
    var isEditingName by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(item.itemName) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Item name
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    enabled = isEditingName || isEditing,
                    textStyle = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = Color.Black
                    ),
                    maxLines = Int.MAX_VALUE,
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                )

                // Edit icon
                IconButton(
                    onClick = {
                        if (isEditingName || isEditing) {
                            closetViewModel.editItemName(item, name)
                            isEditingName = false
                            onToggleEditing(false)
                        } else {
                            isEditingName = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isEditingName || isEditing) Icons.Filled.Check else Icons.Outlined.Edit,
                        contentDescription = "Edit title",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Item type dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(Color(0xFFF7F7F7))
                            .clickable { expanded = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedItemType)
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Item type dropdown",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        closetState.itemTypes.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    closetViewModel.editItemType(item, option)
                                    selectedItemType = option
                                    expanded = false
                                },
                                text = {
                                    Row {
                                        Text(option)
                                        if (option == selectedItemType) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = "Item type",
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier
                                                    .padding(start = 6.dp)
                                                    .size(16.dp)
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
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
            if (isEditing) {
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = { onToggleEditing(false) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Save edits",
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = { onToggleEditing(true) }
                ) {
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

// Item description, outfits featuring the item, and composable call for item tags
@Composable
fun ItemInformation(
    itemId: Int,
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel,
    isEditing: Boolean,
    onToggleEditing: (Boolean) -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    modifier: Modifier
) {
    // Observe the current UI state from the ViewModel
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    val item = closetState.items.find { it.itemId == itemId }
        ?: throw NoSuchElementException("Item with id $itemId not found")

    var isEditingDescription by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf(item.itemDescription) }
    var outfitList by remember { mutableStateOf(emptyList<OutfitEntry>()) }

    LaunchedEffect(itemId, closetState.items) {
        outfitList = closetViewModel.getOutfitsList(itemId)
    }

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
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp, end = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        if (isEditingDescription || isEditing) {
                            IconButton(onClick = {
                                // updates globally
                                closetViewModel.editItemDescription(item, description)

                                isEditingDescription = false
                                onToggleEditing(false)
                            } ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Save edits",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        } else {
                            IconButton(onClick = { isEditingDescription = true } ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Edit description",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                    if (item.itemDescription.isNotEmpty()) {
                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            enabled = isEditing || isEditingDescription,
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            )
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
                    if (outfitList.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(outfitList.size) { idx ->
                                val outfit = outfitList[idx]
                                OutfitsCard(
                                    outfitName = outfit.outfitName,
                                    outfitId = outfit.outfitId,
                                    outfitsViewModel = outfitsViewModel,
                                    onNavigateToCalendarScreen = onNavigateToCalendarScreen,
                                    imageRes = R.drawable.shirt, // swap to item.itemPhotoUri when ready
                                    closetViewModel = closetViewModel
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
            TagsEditableCard(
                itemId = itemId,
                closetViewModel = closetViewModel
            )
        }
    }
}

// Card for each outfit an item features
@Composable
private fun OutfitsCard(
    outfitName: String,
    outfitId: Int,
    outfitsViewModel: OutfitsViewModel,
    onNavigateToCalendarScreen: () -> Unit,
    closetViewModel: ClosetViewModel,
    imageRes: Int,
) {
    var showOutfitModal by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(Color(0xFFF7F7F7))
            .padding(10.dp)
            .fillMaxSize()
            .clickable{showOutfitModal = true}
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

    if (showOutfitModal) {
        OutfitModal(
            outfitsViewModel = outfitsViewModel,
            outfitId = outfitId,
            onDismiss = { showOutfitModal = false },
            onNavigateToCalendarScreen = onNavigateToCalendarScreen,
            closetViewModel = closetViewModel
        )
    }
}

// Card for viewing and editing tags assigned to this item
// Edit mode unlocks selecting tags, adding new tags, or deleting global tags
// Displays all global tags so user can modify which apply to this item
@Composable
private fun TagsEditableCard(
    itemId: Int,
    closetViewModel: ClosetViewModel
) {
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    val item = closetState.items.find { it.itemId == itemId }
        ?: throw NoSuchElementException("Item with id $itemId not found")

    var editMode by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var pendingGlobalDelete by remember { mutableStateOf<String?>(null) }

    // local selection buffer for tags; sync when item changes
    var selectedTags by remember(item.itemId) { mutableStateOf(item.itemTags.toSet()) }
    LaunchedEffect(item.itemTags) { selectedTags = item.itemTags.toSet() }

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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // add tag
                        IconButton(
                            onClick = { showAddDialog = true },
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = "Add tag")
                        }
                        // save changes
                        IconButton(
                            onClick = {
                                val current = item.itemTags.toSet()
                                val toAdd = selectedTags - current
                                val toRemove = current - selectedTags
                                toAdd.forEach { t ->
                                    closetViewModel.editItemTags(
                                        item,
                                        t,
                                        isRemoving = false
                                    )
                                }
                                toRemove.forEach { t ->
                                    closetViewModel.editItemTags(
                                        item,
                                        t,
                                        isRemoving = true
                                    )
                                }
                                editMode = false
                            },
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = "Save changes")
                        }
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
            val sortedTags = closetState.tags.sortedByDescending { it in selectedTags }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedTags.size) { idx ->
                    val tag = sortedTags[idx]
                    val isSelected = tag in selectedTags
                    TagChipSelectable(
                        text = tag,
                        selected = isSelected,
                        editing = editMode,
                        onToggleForItem = {
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
                        closetViewModel.addTag(t)      // add globally
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
                message = "Deleting \"$tag\" removes it from your tag list and from ALL items. This cannot be undone.",
                confirmText = "Delete",
                onDismiss = { pendingGlobalDelete = null },
                onConfirm = {
                    closetViewModel.deleteTag(tag)
                    selectedTags = selectedTags - tag
                    pendingGlobalDelete = null
                }
            )
        }
    }
}

// Chip used for showing a single tag with optional interaction
// Supports select/deselect for the current item when editing
// Can show a delete icon in edit mode to remove a tag globally
@Composable
private fun TagChipSelectable(
    text: String,
    selected: Boolean,
    editing: Boolean,
    onToggleForItem: () -> Unit,
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
                .clickable { onToggleForItem() }
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

// Dialog for user to type a custom tag
@Composable
private fun AddTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a tag") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Tag name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Confirmation dialog for dangerous or irreversible actions
// Displays a title, message, and confirm/cancel actions
// Used for deleting a global tag from all items
@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
