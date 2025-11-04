package com.cs407.fitfolio.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.ui.viewModels.OutfitsViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OutfitModal(
    outfitsViewModel: OutfitsViewModel,
    outfitId: String,
    onDismiss: () -> Unit,
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
            //val outfit: OutfitEntry = outfitViewModal.getOutfit(outfitId)
            //ItemOrOutfitModalBox(title = item.itemName, photo = item.itemPhoto)
            //ItemOrOutfitModalBox(title = "Outfit Title", photo = 0, onNavigateToCalendarScreen = onNavigateToCalendarScreen)
            
        }

    }
}