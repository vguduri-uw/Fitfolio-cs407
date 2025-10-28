package com.cs407.fitfolio.ui.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationModal (onDismiss: () -> Unit, screen: String) {
    // Track sheet state and open to full screen
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            InformationHeader()

            // Dynamic information/tips for each screen
            when (screen) {
                "My Outfits" -> OutfitScreenInfo()
                "Calendar" -> CalendarScreenInfo()
                "Wardrobe" -> WardrobeScreenInfo()
                "Add" -> AddScreenInfo()
                "My Closet" -> ClosetScreenInfo()
            }
        }
    }
}

@Composable
fun InformationHeader() {
    Image(
        // TODO: replace with LOGO
        painter = painterResource(id = R.drawable.user),
        contentDescription = "User profile image",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        alignment = Alignment.Center
    )

    Spacer(modifier = Modifier.size(16.dp))
    Text(text = "FitFolio", style = MaterialTheme.typography.titleLarge)
    Text(text = "Information", style = MaterialTheme.typography.titleLarge)
}

// TODO: insert instructions/tips composable for each screen
@Composable
fun OutfitScreenInfo() {
    Text("Outfit")
}

@Composable
fun CalendarScreenInfo() {
    Text("Calendar")
}

@Composable
fun WardrobeScreenInfo() {
    Text("Wardrobe")
}

@Composable
fun AddScreenInfo() {
    Text("Add")
}

@Composable
fun ClosetScreenInfo() {
    Text("Closet")
}
