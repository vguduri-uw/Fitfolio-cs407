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

    // Mutable states
    var isEditing by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedItemType by remember { mutableStateOf(item.itemType)}

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item name
                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )

                // Item type dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(Color(0xFFF7F7F7))
                            .clickable { expanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedItemType)
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Change item type",
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
                                                contentDescription = null,
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
            TagsEditableCard(
                itemId = itemId,
                closetViewModel = closetViewModel
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

// TODO: get rid of eventually
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
    var selectedItemType by remember { mutableStateOf(item.itemType)}

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
                    item.itemTags
                        .sortedByDescending { it in item.itemTags }
                        .forEach { tag ->
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        "Item Type",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    IconButton(
                        onClick = {expanded = true}
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Item type dropdown",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

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
                                selectedItemType = option
                                expanded = false
                            },
                            text = {
                                Row() {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (option == selectedItemType) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Selected",
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .size(18.dp),
                                            tint = Color(0xFF2E7D32)
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
}

// Card for viewing and editing tags assigned to this item
// Edit mode unlocks selecting tags, adding new tags, or deleting global tags
// Displays all global tags so user can modify which apply to this item
@Composable
private fun TagsEditableCard(
    itemId: String,
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
