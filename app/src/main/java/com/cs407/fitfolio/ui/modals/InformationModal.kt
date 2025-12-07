package com.cs407.fitfolio.ui.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.theme.Google_Sans_Flex
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationModal(onDismiss: () -> Unit, screen: String) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.50f))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 64.dp, bottom = 48.dp, start = 24.dp, end = 24.dp)
            ) {
                InformationHeader()

                // Dynamic information/tips for each screen
                when (screen) {
                    "My Outfits" -> OutfitScreenInfo()
                    "My Calendar" -> CalendarScreenInfo()
                    "Outfit Carousel" -> WardrobeScreenInfo()
                    "Add" -> AddScreenInfo()
                    "My Closet" -> ClosetScreenInfo()
                }
            }

            // Close button - placed last so it's on top
            IconButton(
                onClick = { onDismiss() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.clear),
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
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
            .size(90.dp)
            .clip(CircleShape),
        alignment = Alignment.Center
    )

    Spacer(modifier = Modifier.size(25.dp))
    Text(text = "FitFolio", fontFamily = Kudryashev_Display_Sans_Regular, color = Color.White, fontSize = 20.sp)
    Text(text = "Information", fontFamily = Kudryashev_Display_Sans_Regular, color = Color.White , fontSize = 15.sp)
    Spacer(modifier = Modifier.size(10.dp))
}

@Composable
fun OutfitScreenInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "My Outfits Icons",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.cog),
            description = "Settings - Access app settings and account options"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.heart_outline),
            description = "Favorite - Mark outfits as favorites for quick access"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.shuffle),
            description = "Shuffle - Randomize the order of your outfits"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.loupe),
            description = "Search - Find specific outfits"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.add),
            description = "Add - Include tags in your filter selection"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.schedule),
            description = "Schedule - Add this outfit to specific dates on your calendar. Multiple outfits can be planned for the same day"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.delete),
            description = "Delete - Remove outfits from your collection"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.clear),
            description = "Clear - Remove all active filters"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.hanger),
            description = "Hanger - Empty slot when not all parts of outfit are used"
        )

        Text(
            text = "Clicking an outfit pulls up more information that can be viewed and edited",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CalendarScreenInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "My Calendar Icons",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.cog),
            description = "Settings - Access app settings and account options"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.loupe),
            description = "Search - Find dates for specific outfits based on name, description or tags"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.add),
            description = "Add - Add an outfit to a specific day on your calendar"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.delete),
            description = "Delete - Remove outfits from your calendar"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.left_arrow, R.drawable.right_arrow),
            description = "Navigate through your calendar"
        )

        Text(
            text = "Click on any day see what outfit(s) you have scheduled, Light gray indicates an outfit(s) is scheduled for that day",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

@Composable
fun WardrobeScreenInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Outfit Creation Icons",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.cog),
            description = "Settings - Access app settings and account options"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.shuffle),
            description = "Shuffle - Randomize items across all clothing categories"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.heart_outline),
            description = "Favorite - Toggle to show only favorited items in carousel"
        )

        Row {
            Text(
                text = "—",
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.weight(.14f)
            )

            Text(
                text = "Delete - Block current item combination from future suggestions",
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.weight(.86f)
            )
        }

        Row {
            Text(
                text = "< >",
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.weight(.14f)
            )

            Text(
                text = "Navigate through carousel items in each category",
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.weight(.86f)
            )
        }

        Text(
            text = "Dress me ✨ - Generate an AI-powered try-on image with your selected items and save as an outfit",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 14.sp,
            color = Color.White
        )

        Text(
            text = "Requires at least one item selected and a personal avatar uploaded in settings. You can do this in your settings",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

@Composable
fun AddScreenInfo() {
    Text("Add", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 20.sp)
}

@Composable
fun ClosetScreenInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "My Wardrobe Icons",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.cog),
            description = "Settings - Access app settings and account options"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.heart_outline),
            description = "Favorite - Mark items as favorites for quick access"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.shuffle),
            description = "Shuffle - Randomize the order of your clothing items"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.loupe),
            description = "Search - Find specific items"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.add),
            description = "Add - Include tags in your filter selection"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.schedule),
            description = "Schedule - Shows item's past and planned usage"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.delete),
            description = "Delete - Remove item(s) from your collection, removes all outfits with this item"
        )

        IconInfoRow(
            iconRes = listOf(R.drawable.clear),
            description = "Clear - Remove all active filters"
        )

        Text(
            text = "Clicking an item pulls up more information that can be viewed and edited",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun IconInfoRow(iconRes: List<Int>, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconRes.forEach { icon ->
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = description,
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }
}