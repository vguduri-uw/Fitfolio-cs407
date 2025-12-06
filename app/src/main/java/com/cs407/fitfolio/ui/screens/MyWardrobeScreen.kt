package com.cs407.fitfolio.ui.screens

import android.util.Log
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.components.SimpleHeader
import com.cs407.fitfolio.ui.components.WeatherDataChip
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.WeatherViewModel
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.data.ItemEntry
import com.cs407.fitfolio.enums.CarouselTypes
import com.cs407.fitfolio.ui.modals.OutfitModal
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import com.cs407.fitfolio.viewModels.UserViewModel

@Composable
fun MyWardrobeScreen(
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToAddScreen: () -> Unit,
    onNavigateToClosetScreen: () -> Unit,
    onNavigateToSignInScreen: () -> Unit,
    closetViewModel: ClosetViewModel,
    weatherViewModel: WeatherViewModel,
    outfitsViewModel: OutfitsViewModel,
    userViewModel: UserViewModel
) {

    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    //for weather
    val scope = rememberCoroutineScope()
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    var centeredHeadwear by remember { mutableStateOf<ItemEntry?>(null) }
    var centeredTopwear by remember { mutableStateOf<ItemEntry?>(null) }
    var centeredBottomwear by remember { mutableStateOf<ItemEntry?>(null) }
    var centeredShoes by remember { mutableStateOf<ItemEntry?>(null) }
    val context = LocalContext.current
    val selectedItems by closetViewModel.selectedItems.collectAsStateWithLifecycle()
    val db = FitfolioDatabase.getDatabase(context)
    val categories = CarouselTypes.entries

    var showOutfitModal by remember { mutableStateOf(false) }
    var createdOutfitId by remember { mutableIntStateOf(-1) }
    var saveError by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
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
            categories.forEach { category ->
                val filteredItems = closetState.filteredItems.filter { it.carouselType == category }

                val itemsWithOptional =
                    if (category == CarouselTypes.HEADWEAR && filteredItems.isNotEmpty()) {
                        listOf(
                            ItemEntry(
                                itemId = -1,
                                itemName = "No Headwear",
                                carouselType = category,
                                itemType = "",
                                itemDescription = "",
                                itemTags = emptyList(),
                                isFavorite = false,
                                isDeletionCandidate = false,
                                itemPhotoUri = ""
                            )
                        ) + filteredItems
                    } else {
                        filteredItems
                    }

                ClothingScroll(
                    items = itemsWithOptional,
                    selectedItem = selectedItems[category],
                    onSelect = { item -> closetViewModel.selectItem(category, item) },
                    category = category,
                    onCenteredItemChange = { centeredItem ->
                        when (category) {
                            CarouselTypes.HEADWEAR -> centeredHeadwear = centeredItem
                            CarouselTypes.TOPWEAR -> centeredTopwear = centeredItem
                            CarouselTypes.BOTTOMWEAR ->  centeredBottomwear = centeredItem
                            CarouselTypes.FOOTWEAR ->  centeredShoes = centeredItem
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
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
                        val itemsToAdd = listOfNotNull(
                            centeredHeadwear?.takeIf { it.itemId > 0 },
                            centeredTopwear?.takeIf { it.itemId > 0 },
                            centeredBottomwear?.takeIf { it.itemId > 0 },
                            centeredShoes?.takeIf { it.itemId > 0 }
                        )
                        if (itemsToAdd.isEmpty()) return@IconButton

                        // Add outfit
                        scope.launch {
                            val outfitId = outfitsViewModel.addOutfit(
                                name = "New Outfit",
                                description = "Created from My Wardrobe",
                                tags = emptyList(),
                                isFavorite = false,
                                photoUri = "",
                                itemList = itemsToAdd
                            )

                            if (outfitId > 0) {
                                createdOutfitId = outfitId
                                Toast.makeText(context, "Outfit saved.", Toast.LENGTH_SHORT).show()
                                showOutfitModal = true
                                Log.d("WARDROBE", "head=$centeredHeadwear top=$centeredTopwear bottom=$centeredBottomwear shoes=$centeredShoes")
                            } else {
                                saveError = true
                            }
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.add_nav_thin),
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

    if (showOutfitModal) {
        OutfitModal(
            closetViewModel = closetViewModel,
            outfitsViewModel = outfitsViewModel,
            outfitId = createdOutfitId,
            onDismiss = {
                showOutfitModal = false
            },
            onNavigateToCalendarScreen = onNavigateToCalendarScreen
        )
    } else if (saveError) {
        Toast.makeText(context, "Outfit could not be saved. Please try again.", Toast.LENGTH_SHORT).show()
        saveError = false
    }
}
@Composable
fun ClothingScroll(
    items: List<ItemEntry>,
    selectedItem: ItemEntry?,
    onSelect: (ItemEntry) -> Unit,
    category: CarouselTypes,
    onCenteredItemChange: (ItemEntry?) -> Unit

) {
//    val items = closetState.items.filter{ it.itemType in types}
    val listState = rememberLazyListState( initialFirstVisibleItemIndex = Int.MAX_VALUE / 2)
    val scope = rememberCoroutineScope()
    val itemSize = 150.dp
//    LaunchedEffect(centeredItem) {
//        centeredItem?.let {
//            val index = items.indexOf(it)
//            if (index != -1) listState.animateScrollToItem(index)
//        }
//    }
    LaunchedEffect(selectedItem) {
        selectedItem?.let {
            val index = items.indexOf(it)
            if (index != -1) listState.animateScrollToItem(index)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val visibleItems = layoutInfo.visibleItemsInfo
                if (visibleItems.isEmpty()) return@collect

                // Center of the LazyRow viewport
                val center = layoutInfo.viewportStartOffset +
                        layoutInfo.viewportEndOffset / 2

                // Find item whose center is closest to viewport center
                val centered = visibleItems.minByOrNull { item ->
                    val itemCenter = item.offset + item.size / 2
                    kotlin.math.abs(itemCenter - center)
                }

                centered?.let {
                    val index = it.index % items.size
                    onCenteredItemChange(items[index])
                }
            }
    }

    if (items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            contentAlignment = Alignment.Center
        ) { Text("No ${category.carouselType} Items Found") }
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
    var aspectRatio by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Color(0xFFE0E0E0).copy(alpha = .2f)),
        contentAlignment = Alignment.Center
    ) {
        if (item.itemPhotoUri.isNotEmpty()) {
            AsyncImage(
                model = item.itemPhotoUri,
                contentDescription = item.itemName,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio),
                contentScale = ContentScale.Fit,
                onSuccess = {
                    val w = it.result.drawable.intrinsicWidth
                    val h = it.result.drawable.intrinsicHeight
                    if (w > 0 && h > 0) {
                        val r = w.toFloat() / h.toFloat()
                        aspectRatio = maxOf(r, 0.55f)
                    }
                }
            )
        } else {
            // fallback if no image is available
            if (item.itemId != -1) { // do not show icon for optional slot
                Icon(
                    painter = painterResource(R.drawable.hanger),
                    contentDescription = "No item image found"
                )
            }
        }
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