package com.cs407.fitfolio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.components.SimpleHeader
import com.cs407.fitfolio.ui.components.WeatherDataChip
import com.cs407.fitfolio.viewModels.ClosetState
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.ItemEntry
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import com.cs407.fitfolio.viewModels.WeatherViewModel
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun MyWardrobeScreen(
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToAddScreen: () -> Unit,
    onNavigateToClosetScreen: () -> Unit,
    onNavigateToSignInScreen: () -> Unit,
    closetViewModel: ClosetViewModel,
    weatherViewModel: WeatherViewModel,
    outfitsViewModel: OutfitsViewModel
) {

    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    //for weather
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    var centeredHeadwear by remember { mutableStateOf<ItemEntry?>(null) }
    var centeredTopwear by remember { mutableStateOf<ItemEntry?>(null) }
    var centeredBottomwear by remember { mutableStateOf<ItemEntry?>(null) }
    var centeredShoes by remember { mutableStateOf<ItemEntry?>(null) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        //background sillouette
        Image(
            painter = painterResource(R.drawable.silouette),
            contentDescription = "Silhouette background",
            modifier = Modifier
                .align(Alignment.Center)
                .size(550.dp)
                .alpha(0.12f),       // make semi-transparent
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                //TODO: Title of Page and Weather
                SimpleHeader("My Wardrobe")
                Spacer(modifier = Modifier.weight(1f))
                WeatherDataChip(
                    weatherData = weatherState.weatherData
                )
            }
            //Scroller for head pieces
            ClothingScroll(closetState, category = "Headwear", types = listOf("Hats"), centeredHeadwear)
            //Scroller for tops/shirts
            ClothingScroll(closetState, category = "Topwear", types = listOf("T-Shirts", "Shirts", "Dresses"), centeredTopwear)
            //Scroller for bottoms/pants
            ClothingScroll(closetState, category = "Bottomwear", types = listOf("Jeans", "Pants", "Shorts", "Skirts"), centeredBottomwear)
            //Scroller for shoes
            ClothingScroll(closetState, category = "Shoes", types = listOf("Shoes"), centeredShoes)
            Spacer(modifier = Modifier.size(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp), // space between icons
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
            ) {
                //adds outfit to the outfits page
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        val itemsToAdd = listOfNotNull(centeredHeadwear, centeredTopwear, centeredBottomwear, centeredShoes)
                        if (itemsToAdd.isEmpty()) return@IconButton

                        // Convert items to their real DB IDs
                        val newIds = itemsToAdd.mapNotNull { entry ->
                            closetState.items.firstOrNull { it.itemName == entry.itemName }?.itemId
                        }.sorted()
                        //TODO: fix this
                        // Check if an existing outfit has exactly the same items
                        val existing = outfitsViewModel.outfitsState.value.outfits.any { outfit ->
                            outfit.itemList.map { it.itemId }.sorted() == newIds
                        }

                        if (existing) {
                            // Toast notification
                            Toast.makeText(context, "This outfit already exists!", Toast.LENGTH_SHORT).show()
                            return@IconButton
                        }

                        outfitsViewModel.addOutfit(
                            name = "New Outfit",
                            description = "Created from My Wardrobe",
                            tags = emptyList(),
                            isFavorite = false,
                            photo = -1,
                            itemList = itemsToAdd
                        )

                        val lastOutfit = outfitsViewModel.outfitsState.value.outfits.lastOrNull()
                        lastOutfit?.let {
                            outfitsViewModel.updateOutfitToShow(it.outfitId)
                        }
                        onNavigateToOutfitsScreen()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.add),
                            contentDescription = "add outfit",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                //ensures the combo is never seen again
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { closetViewModel.shuffleItems() }) {
                        Icon(
                            painter = painterResource(R.drawable.minus),
                            contentDescription = "remove combination",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                // shuffle button
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        centeredHeadwear = closetState.items.filter { it.itemType in listOf("Hats") }.randomOrNull()
                        centeredTopwear = closetState.items.filter { it.itemType in listOf("T-Shirts", "Shirts", "Dresses") }.randomOrNull()
                        centeredBottomwear = closetState.items.filter { it.itemType in listOf("Jeans", "Pants", "Shorts", "Skirts") }.randomOrNull()
                        centeredShoes = closetState.items.filter { it.itemType == "Shoes" }.randomOrNull()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.shuffle),
                            contentDescription = "shuffle",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                //TODO: Dress me icon
                Button(
                    //change this when we figure out how we are implementing shuffleItems list
                    onClick = { closetViewModel.shuffleItems() },
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Dress Me!")
                }
            }
        }
    }
}
@Composable
fun ClothingScroll(
    closetState: ClosetState,
    category: String,
    types: List<String>,
    centeredItem: ItemEntry? = null
) {
    val items = closetState.items.filter{ it.itemType in types}
    val listState = rememberLazyListState( initialFirstVisibleItemIndex = Int.MAX_VALUE / 2)
    val scope = rememberCoroutineScope()
    val itemSize = 150.dp
    LaunchedEffect(centeredItem) {
        centeredItem?.let {
            val index = items.indexOf(it)
            if (index != -1) listState.animateScrollToItem(index)
        }
    }

    if (items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            contentAlignment = Alignment.Center
        ) { Text("No $category items") }
        return
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)    ) {
        IconButton(
            onClick = {
                scope.launch {
                    val prevIndex = (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                    listState.animateScrollToItem(prevIndex)
                }
            },
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.25f), shape = RectangleShape)
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous", tint = Color.White)
        }

            LazyRow(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .height(itemSize),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = itemSize / 2),
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
            ) {
                val total = items.size

                items(Int.MAX_VALUE) { index ->
                    val item = items[index % total]

                    ClothingItemCard(item)
                }
            }
        // Right arrow
        IconButton(
            onClick = {
                scope.launch {
                    val nextIndex = listState.firstVisibleItemIndex + 1
                    listState.animateScrollToItem(nextIndex)
                }
            },
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.25f), shape = RectangleShape)
        ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next", tint = Color.White)
        }
    }
}

@Composable
fun ClothingItemCard(item: ItemEntry) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Color(0xFFE0E0E0).copy(alpha = .2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(item.itemName)
//        Image(
//            painter = painterResource(id = R.drawable.shirt),
//            contentDescription = item.itemName,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )
    }
}


//@Composable
//fun OutfitPreviewBoxStack(
//    headwear: ItemEntry?,
//    topwear: ItemEntry?,
//    bottomwear: ItemEntry?,
//    shoes: ItemEntry?
//) {
//    val items = listOfNotNull(headwear, topwear, bottomwear, shoes)
//
//    Box(
//        modifier = Modifier
//            .size(200.dp) // overall preview size
//            .background(Color.LightGray.copy(alpha = 0.2f))
//            .clip(MaterialTheme.shapes.medium),
//        contentAlignment = Alignment.Center
//    ) {
//        items.forEachIndexed { index, item ->
//            // You can optionally offset each layer slightly for effect
//            val offset = (index * 4).dp
//            Image(
//                painter = painterResource(id = item.itemPhoto), // your drawable
//                contentDescription = item.itemName,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(offset)
//                    .clip(MaterialTheme.shapes.medium)
//            )
//        }
//    }
//}