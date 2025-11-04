package com.cs407.fitfolio.ui.modals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.ui.components.ItemOrOutfitModalBox
import com.cs407.fitfolio.ui.viewModels.ClosetViewModel
import com.cs407.fitfolio.ui.viewModels.ItemEntry

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ItemModal(
    closetViewModel: ClosetViewModel,
    itemId: String,
    onDismiss: () -> Unit,
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
) {
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
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 48.dp, start = 24.dp, end = 24.dp)
                .verticalScroll(scrollState)
        ) {
            //val item: ItemEntry = closetViewModel.getItem(itemId)
            //ItemOrOutfitModalBox(title = item.itemName, photo = item.itemPhoto)

            ItemOrOutfitModalBox(title = "Item Title", photo = 0, onNavigateToCalendarScreen = onNavigateToCalendarScreen)
        }

    }
}