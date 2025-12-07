package com.cs407.fitfolio.ui.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.sp
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.theme.FloralWhite
import com.cs407.fitfolio.ui.theme.Google_Sans_Flex
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationModal (onDismiss: () -> Unit, screen: String) {
    // Track sheet state and open to full screen
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        containerColor = FloralWhite,
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            InformationHeader()

            // Dynamic information/tips for each screen
            when (screen) {
                "My Outfits" -> OutfitScreenInfo()
                "My Calendar" -> CalendarScreenInfo()
                "Outfit Carousel" -> CarouselScreenInfo()
                "Add" -> AddScreenInfo()
                "My Closet" -> ClosetScreenInfo()
            }
        }
    }
}

@Composable
fun InformationHeader() {
    Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = "App logo",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(130.dp)
            .clip(CircleShape),
        alignment = Alignment.Center
    )

    Spacer(modifier = Modifier.size(25.dp))
    Text(text = "FitFolio", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 30.sp)
    Spacer(modifier = Modifier.size(15.dp))
    Text(text = "Information", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 25.sp)
    Spacer(modifier = Modifier.size(10.dp))
}

// TODO: insert instructions/tips composable for each screen
@Composable
fun OutfitScreenInfo() {
    Text("Outfits", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 20.sp)
}

@Composable
fun CalendarScreenInfo() {
    Text("Calendar", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 20.sp)
}

@Composable
fun CarouselScreenInfo() {
    Text("Carousel", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 20.sp)
}

@Composable
fun AddScreenInfo() {
    Text("Add", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 20.sp)
}

@Composable
fun ClosetScreenInfo() {
    Text("Closet", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
}
