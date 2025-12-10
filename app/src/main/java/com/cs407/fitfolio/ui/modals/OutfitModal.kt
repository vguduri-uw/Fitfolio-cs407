package com.cs407.fitfolio.ui.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.data.ItemEntry
import com.cs407.fitfolio.data.OutfitEntry
import com.cs407.fitfolio.enums.DeletionStates
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import com.cs407.fitfolio.ui.components.DeleteOutfitDialog
import com.cs407.fitfolio.ui.theme.FloralWhite
import com.cs407.fitfolio.ui.theme.GoldenApricot
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.ui.theme.LightPeachFuzz
import kotlinx.coroutines.launch

// modal sheet that displays full outfit details and actions
// shows outfit photo, description, items, and tags, with editing modes
// opened when the user selects an outfit from the outfits screen
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OutfitModal(
    outfitsViewModel: OutfitsViewModel,
    closetViewModel: ClosetViewModel,
    outfitId: Int,
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
        containerColor = FloralWhite,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 45.dp)
    ) {
        val outfit = outfitsState.outfits.find { it.outfitId == outfitId }
            ?: throw NoSuchElementException("Item with id $outfitId not found")

        // load the items that belong to this outfit
        var outfitItems by remember { mutableStateOf<List<ItemEntry>>(emptyList()) }

        LaunchedEffect(outfit.outfitId) {
            outfitItems = outfitsViewModel.getItemsList(outfit.outfitId)
        }

        // track whether outfit is editable (if you want a shared flag later)
        var isEditing by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxHeight() // important: gives us a height to scroll within
                .padding(bottom = 12.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //
            OutfitHeaderBox(
                outfit = outfit,
                isEditing = isEditing,
                outfitsViewModel = outfitsViewModel
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutfitIconBox(
                outfit = outfit,
                outfitsViewModel = outfitsViewModel,
                onDismiss = onDismiss,
                items = outfitItems,
                onNavigateToCalendarScreen = onNavigateToCalendarScreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            // scrollable content (description, item list, tags)
            Column(
                modifier = Modifier
                    .weight(1f) // take up remaining height
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DescriptionCard(
                    outfit = outfit,
                    isEditing = isEditing,
                    outfitsViewModel = outfitsViewModel
                )

                ItemsInOutfitCard(
                    outfit = outfit,
                    isEditing = isEditing,
                    outfitsViewModel = outfitsViewModel,
                    items = outfitItems,
                    closetViewModel = closetViewModel,
                    onNavigateToCalendarScreen = onNavigateToCalendarScreen
                )

                TagsEditableCard(
                    outfit = outfit,
                    outfitsViewModel = outfitsViewModel
                )

                Spacer(Modifier.height(8.dp)) // small bottom padding
            }
        }
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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add a tag",
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            androidx.compose.material3.OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Tag name", fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold) },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }) {
                Text("Add", fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel", fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold)
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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold) },
        text = { Text(message, fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(confirmText, fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel", fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// small card that displays a single clothing item inside an outfit
// shows photo, item name, and type for quick visual reference
// used inside the "items in this outfit" horizontal list
@Composable
private fun ItemCard(
    item: ItemEntry,
    name: String,
    type: String,
    imageRes: Int,
    photoUri: String,
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel,
    onNavigateToCalendarScreen: () -> Unit,
) {
    var showItemModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(FloralWhite)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .clickable { showItemModal = true }
                .padding(10.dp)
        ) {
            val hasPhoto = photoUri.isNotBlank()

            if (hasPhoto) {
                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = "$name image",
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = "Placeholder image for $name",
                    modifier = Modifier.size(85.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = name,
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = type,
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }

    if (showItemModal) {
        ItemModal(
            closetViewModel = closetViewModel,
            outfitsViewModel = outfitsViewModel,
            itemId = item.itemId,
            onDismiss = { showItemModal = false },
            onNavigateToCalendarScreen = onNavigateToCalendarScreen
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
        selected && editing -> GoldenApricot // darker gray (when selected + editing)
        selected -> GoldenApricot    // light brown (when selected)
        else -> FloralWhite                // cream white (default)
    }

    // if not editing, no clickable modifier
    val chipModifier =
        if (editing) {
            Modifier
                .shadow(
                    elevation = 3.dp,
                    shape = MaterialTheme.shapes.small,
                    clip = false
                )
                .clip(MaterialTheme.shapes.small)
                .background(backgroundColor)
                .clickable { onToggleForOutfit() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        } else {
            Modifier
                .shadow(
                    elevation = 3.dp,
                    shape = MaterialTheme.shapes.small,
                    clip = false
                )
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
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 15.sp
        )

        // show small delete icon ONLY when editing
        if (editing) {
            Icon(
                painter = painterResource(R.drawable.delete_filled_red),
                contentDescription = "Delete tag",
                tint = Color.Red,
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

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(LightPeachFuzz)
            .padding(15.dp)
            .fillMaxWidth()
    ) {
        Column(
        ) {
            // header row: title + actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tags",
                    fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )

                if (editMode) {
                    Row {
                        // add tag
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clickable(
                                    onClick = { showAddDialog = true }
                                )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add),
                                contentDescription = "Add tag",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.size(15.dp))

                        // save changes
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clickable(
                                    onClick = {
                                        val current = outfit.outfitTags.toSet()
                                        val toAdd = selectedTags - current
                                        val toRemove = current - selectedTags
                                        toAdd.forEach { t -> outfitsViewModel.editOutfitTags(outfit, t, isRemoving = false) }
                                        toRemove.forEach { t -> outfitsViewModel.editOutfitTags(outfit, t, isRemoving = true) }
                                        editMode = false
                                    }
                                )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.check),
                                contentDescription = "Save changes",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    // edit mode
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                                onClick = { editMode = true }
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit),
                            contentDescription = "Edits tags",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // show ALL global tags as chips (selectable only when editing)
            val allTags = outfitsState.tags
            val sortedTags = allTags.sortedByDescending { it in selectedTags }
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
fun OutfitHeaderBox(
    outfit: OutfitEntry,
    isEditing: Boolean,
    outfitsViewModel: OutfitsViewModel
) {
    var localIsEditing by remember { mutableStateOf(isEditing) }
    var outfitName by remember { mutableStateOf(outfit.outfitName) }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(LightPeachFuzz)
            .fillMaxWidth()
    ) {
        TextField(
            value = outfitName,
            onValueChange = { newOutfitName ->
                outfitName = newOutfitName
            },
            enabled = localIsEditing,
            textStyle = TextStyle(
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                textAlign = TextAlign.Start,
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
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
        )

        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = {
                if (localIsEditing) {
                    // leaving edit mode → save
                    outfitsViewModel.editOutfitName(outfit, outfitName)
                }
                localIsEditing = !localIsEditing
            }
        ) {
            Icon(
                painter = if (localIsEditing) {
                    painterResource(R.drawable.check)
                } else {
                    painterResource(R.drawable.edit)
                },
                contentDescription = if (localIsEditing) "Save edits" else "Edit outfit name",
                modifier = Modifier.size(20.dp)
            )

        }
    }
}

// top section of the outfit modal showing the outfit name, image, and action icons
// provides actions to favorite, edit, delete, or open calendar for the outfit
// remains pinned at the top while other content scrolls underneath
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitIconBox(
    outfit: OutfitEntry,
    outfitsViewModel: OutfitsViewModel,
    onDismiss: () -> Unit,
    items: List<ItemEntry>,
    onNavigateToCalendarScreen: () -> Unit,
) {
    // Observe the current UI state from the ViewModel
    val outfitsState by outfitsViewModel.outfitsState.collectAsStateWithLifecycle()

    // track outfit photo
    var outfitPhotoUri by remember { mutableStateOf(outfit.outfitPhotoUri) }

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // main photo area
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(LightPeachFuzz)
                .height(350.dp)
                .width(250.dp)
        ) {
            when {
                // use the saved outfit photo if present
                outfit.outfitPhotoUri.isNotBlank() -> {
                    Image(
                        painter = rememberAsyncImagePainter(outfit.outfitPhotoUri),
                        contentDescription = "${outfit.outfitName} photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }

                // if outfit photo can't be found, show a placeholder icon
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(FloralWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.hanger),
                            contentDescription = "Outfit placeholder image",
                            modifier = Modifier.size(96.dp)
                        )
                    }
                }
            }
        }

        // row of calendar, delete, and favorite buttons (unchanged)
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // calendar icon
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(LightPeachFuzz),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.TopStart),
                    onClick = { showDatePicker = true } //Veda
                ) {
                    Icon(
                        painter = painterResource(R.drawable.schedule),
                        contentDescription = "Calendar",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // delete icon
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(LightPeachFuzz),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        showDeleteDialog = true
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete),
                        contentDescription = "Delete outfit",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // favorite icon
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(LightPeachFuzz),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { outfitsViewModel.toggleFavoritesProperty(outfit) }
                ) {
                    if (outfit.isFavorite) {
                        Icon(
                            painter = painterResource(R.drawable.heart_filled_red),
                            contentDescription = "Unfavorite outfit",
                            tint = Color.Red,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.heart_outline),
                            contentDescription = "Favorite outfit",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        if (outfitsState.isDeleteActive == DeletionStates.Active.name) {
            DeleteOutfitDialog(outfitsViewModel)
        }
        //Veda: Date picker dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = System.currentTimeMillis()
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedMillis = datePickerState.selectedDateMillis
                            if (selectedMillis != null) {
                                scope.launch {
                                    // Schedule the outfit for the selected date
                                    outfitsViewModel.scheduleOutfit(outfit.outfitId, selectedMillis)
                                    showDatePicker = false
                                    onDismiss()
                                    onNavigateToCalendarScreen()
                                }
                            }
                        }
                    ) {
                        Text("Schedule")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }

    if (showDeleteDialog) {
        DeleteDialog(
            title = "Delete outfit?",
            message = "This action cannot be undone.",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                scope.launch {
                    outfitsViewModel.deleteOutfits(listOf(outfit))
                    outfitsViewModel.refresh()
                    onDismiss()
                }
            }
        )
    }
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
            .background(LightPeachFuzz)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = "Description",
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 15.dp, top = 15.dp)
            )
            TextField(
                value = outfitDescription,
                onValueChange = { outfitDescription = it },
                enabled = localEditing,
                textStyle = TextStyle(
                    fontFamily = Kudryashev_Display_Sans_Regular,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
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
                    painter = painterResource(R.drawable.check),
                    contentDescription = "Save edits",
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.edit),
                    contentDescription = "Edit outfit description",
                    modifier = Modifier.size(20.dp),
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
    outfitsViewModel: OutfitsViewModel,
    items: List<ItemEntry>,
    closetViewModel: ClosetViewModel,
    onNavigateToCalendarScreen: () -> Unit
) {
    // local copy of the items passed in from the modal
    var itemList by remember(outfit.outfitId, items) {
        mutableStateOf(items)
    }

    // tracks state of whether editing is enabled
    var localEditing by remember { mutableStateOf(isEditing) }

    // tracks state of ids of items selected to be deleted
    var selectedIds by remember(outfit.outfitId) { mutableStateOf<Set<Int>>(emptySet()) }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(LightPeachFuzz)
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
                    fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }

            // items row
            if (itemList.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(
                        items = itemList,
                        key = { it.itemId }
                    ) { item ->
                        val selected = item.itemId in selectedIds

                        Box(
                            modifier = Modifier
                                .shadow(
                                    elevation = 3.dp,
                                    shape = MaterialTheme.shapes.medium,
                                    clip = false
                                )
                                .clip(MaterialTheme.shapes.medium)
                                .background(FloralWhite)
                        ) {
                            ItemCard(
                                item = item,
                                name = item.itemName,
                                type = item.itemType,
                                imageRes = R.drawable.shirt, // swap to item.itemPhoto when ready
                                photoUri = item.itemPhotoUri, //Uploaded item photo
                                closetViewModel = closetViewModel,
                                outfitsViewModel = outfitsViewModel,
                                onNavigateToCalendarScreen = onNavigateToCalendarScreen
                            )
                        }
                    }
                }
            }
        }
    }
}