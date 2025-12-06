package com.cs407.fitfolio.ui.screens

import android.net.Uri
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.cs407.fitfolio.BuildConfig.FASHN_API_KEY
import coil.compose.AsyncImage
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.data.ItemEntry
import com.cs407.fitfolio.enums.CarouselTypes
import com.cs407.fitfolio.ui.modals.OutfitModal
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import com.cs407.fitfolio.viewModels.UserViewModel
import com.cs407.fitfolio.viewModels.WardrobeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs

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
    userViewModel: UserViewModel,
    wardrobeViewModel: WardrobeViewModel
) {
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val wardrobeState by wardrobeViewModel.wardrobeState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showTryOnModal by remember { mutableStateOf(false) }
    var showOutfitModal by remember { mutableStateOf(false) }
    var createdOutfitId by remember { mutableIntStateOf(-1) }
    var saveError by remember { mutableStateOf(false) }

    val categories = listOf(
        "Headwear" to listOf("Hats", "Headbands"),
        "Topwear" to listOf("T-Shirts", "Shirts", "Dresses"),
        "Bottomwear" to listOf("Jeans", "Pants", "Shorts", "Skirts"),
        "Shoes" to listOf("Shoes")
    )
    val selectedItems by closetViewModel.selectedItems.collectAsStateWithLifecycle()
    val db = FitfolioDatabase.getDatabase(context)
    val categories = CarouselTypes.entries.filter { it != CarouselTypes.DEFAULT }

    LaunchedEffect(Unit) {
        // Load wardrobe once when screen appears
        wardrobeViewModel.loadWardrobe(
            headwear = closetState.filteredItems.filter { it.itemType in listOf("Hats", "Headbands") },
            topwear = closetState.filteredItems.filter { it.itemType in listOf("T-Shirts", "Shirts", "Dresses") },
            bottomwear = closetState.filteredItems.filter { it.itemType in listOf("Jeans", "Pants", "Shorts", "Skirts") },
            shoes = closetState.filteredItems.filter { it.itemType == "Shoes" }
        )
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .clickable {
            // Trigger shuffle when wardrobe is clicked
            wardrobeViewModel.shuffleItems(
                headwearList = closetState.filteredItems.filter { it.itemType in listOf("Hats", "Headbands") },
                topwearList = closetState.filteredItems.filter { it.itemType in listOf("T-Shirts", "Shirts", "Dresses") },
                bottomwearList = closetState.filteredItems.filter { it.itemType in listOf("Jeans", "Pants", "Shorts", "Skirts") },
                shoesList = closetState.filteredItems.filter { it.itemType == "Shoes" }
            )}
    ){
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SimpleHeader("My Wardrobe")
                Spacer(modifier = Modifier.weight(1f))
                WeatherDataChip(weatherData = weatherState.weatherData)
            }

            // Clothing categories
            categories.forEach { (category, types) ->
                val filteredItems = closetState.filteredItems.filter { it.itemType in types }
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
                    items = filteredItems,
                    selectedItem = when (category) {
                        "Headwear" -> wardrobeState.centeredHeadwear
                        "Topwear" -> wardrobeState.centeredTopwear
                        "Bottomwear" -> wardrobeState.centeredBottomwear
                        "Shoes" -> wardrobeState.centeredShoes
                        else -> null
                    },
                    category = category,
                    onCenteredItemChange = { item -> wardrobeViewModel.updateCenteredItem(category, item) },
                    wardrobeViewModel = wardrobeViewModel
                    onCenteredItemChange = { centeredItem ->
                        when (category) {
                            CarouselTypes.HEADWEAR -> centeredHeadwear = centeredItem
                            CarouselTypes.TOPWEAR -> centeredTopwear = centeredItem
                            CarouselTypes.BOTTOMWEAR ->  centeredBottomwear = centeredItem
                            CarouselTypes.FOOTWEAR ->  centeredShoes = centeredItem
                            CarouselTypes.DEFAULT -> null
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                // Add Outfit
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        val itemsToAdd = listOfNotNull(
                            wardrobeState.centeredHeadwear?.takeIf { it.itemId > 0 },
                            wardrobeState.centeredTopwear?.takeIf { it.itemId > 0 },
                            wardrobeState.centeredBottomwear?.takeIf { it.itemId > 0 },
                            wardrobeState.centeredShoes?.takeIf { it.itemId > 0 }
                        )
                        if (itemsToAdd.isNotEmpty()) {
                            scope.launch {
                                val outfitId = outfitsViewModel.addOutfit(
                                    name = "New Outfit",
                                    description = "Created from My Wardrobe",
                                    tags = emptyList(),
                                    isFavorite = false,
                                    photoUri = "", // optional
                                    itemList = itemsToAdd
                                )
                                if (outfitId > 0) {
                                    createdOutfitId = outfitId
                                    showOutfitModal = true
                                    Toast.makeText(context, "Outfit saved.", Toast.LENGTH_SHORT).show()
                                } else saveError = true
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
                        } else saveError = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.add),
                            painter = painterResource(R.drawable.add_nav_thin),
                            contentDescription = "add outfit",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Shuffle
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        wardrobeViewModel.shuffleItems(
                            headwearList = closetState.filteredItems.filter { it.itemType in listOf("Hats", "Headbands") },
                            topwearList = closetState.filteredItems.filter { it.itemType in listOf("T-Shirts", "Shirts", "Dresses") },
                            bottomwearList = closetState.filteredItems.filter { it.itemType in listOf("Jeans", "Pants", "Shorts", "Skirts") },
                            shoesList = closetState.filteredItems.filter { it.itemType == "Shoes" }
                        )
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.shuffle),
                            contentDescription = "shuffle",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Remove combination
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        val hasItems = listOf(
                            wardrobeState.centeredHeadwear,
                            wardrobeState.centeredTopwear,
                            wardrobeState.centeredBottomwear,
                            wardrobeState.centeredShoes
                        ).any { it != null && it.itemId > 0 }

                        if (hasItems) {
                            wardrobeViewModel.blockCurrentCombination(
                                wardrobeState.centeredHeadwear,
                                wardrobeState.centeredTopwear,
                                wardrobeState.centeredBottomwear,
                                wardrobeState.centeredShoes
                            ) // ✅ call the function
                            Toast.makeText(context, "Combination removed", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No items selected to remove", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.minus),
                            contentDescription = "remove combination",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Dress Me
                Button(
                    onClick = {
                        scope.launch {
                            val garmentUrls = listOfNotNull(
                                wardrobeState.centeredHeadwear?.takeIf { it.itemId > 0 }?.itemPhotoUri?.takeIf { it.isNotEmpty() },
                                wardrobeState.centeredTopwear?.takeIf { it.itemId > 0 }?.itemPhotoUri?.takeIf { it.isNotEmpty() },
                                wardrobeState.centeredBottomwear?.takeIf { it.itemId > 0 }?.itemPhotoUri?.takeIf { it.isNotEmpty() },
                                wardrobeState.centeredShoes?.takeIf { it.itemId > 0 }?.itemPhotoUri?.takeIf { it.isNotEmpty() }
                            )

                            if (garmentUrls.isEmpty()) {
                                Toast.makeText(context, "No garments selected to try on.", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            wardrobeViewModel.dressMe(
                                context = context,
                                apiKey = FASHN_API_KEY,
                                garmentUrls = garmentUrls,
                                onError = { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                },
                                avatarUri = userViewModel.userState.value.avatarUri
                            )
                            showTryOnModal = true
                        }
                    },
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Dress Me!")
                }
            }
        }
    }

    // Try-On Preview Modal
    if (showTryOnModal) {
        val tryOnPreview by wardrobeViewModel.tryOnPreview.collectAsState()
        Dialog(onDismissRequest = { showTryOnModal = false }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                tryOnPreview?.let { url ->
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = "Try-On Preview",
                        modifier = Modifier.size(300.dp)
                    )
                }
                Row {
                    Button(onClick = {
                        val itemsToAdd = listOfNotNull(
                            wardrobeState.centeredHeadwear?.takeIf { it.itemId > 0 },
                            wardrobeState.centeredTopwear?.takeIf { it.itemId > 0 },
                            wardrobeState.centeredBottomwear?.takeIf { it.itemId > 0 },
                            wardrobeState.centeredShoes?.takeIf { it.itemId > 0 }
                        )
                        if (itemsToAdd.isNotEmpty() && !tryOnPreview.isNullOrEmpty()) {
                            scope.launch {
                                val outfitId = outfitsViewModel.addOutfit(
                                    name = "New Outfit",
                                    description = "Created from My Wardrobe",
                                    tags = emptyList(),
                                    isFavorite = false,
                                    photoUri = tryOnPreview ?: "",
                                    itemList = itemsToAdd
                                )
                                if (outfitId > 0) {
                                    Toast.makeText(context, "Outfit saved!", Toast.LENGTH_SHORT).show()
                                    showTryOnModal = false
                                } else {
                                    Toast.makeText(context, "Failed to save outfit.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Cannot save outfit: no items or try-on image.", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Save Outfit")
                    }

                    Button(onClick = { showTryOnModal = false }) { Text("Cancel") }
                }
            }
        }
    }

    // Outfit modal
    if (showOutfitModal) {
        OutfitModal(
            closetViewModel = closetViewModel,
            outfitsViewModel = outfitsViewModel,
            outfitId = createdOutfitId,
            onDismiss = { showOutfitModal = false },
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
    category: String,
    wardrobeViewModel: WardrobeViewModel,
    onSelect: (ItemEntry) -> Unit,
    category: CarouselTypes,
    onCenteredItemChange: (ItemEntry?) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val wardrobeState = wardrobeViewModel.wardrobeState.collectAsState().value
    val blockedCombos = wardrobeViewModel.blockedCombos

    val placeholderItem = ItemEntry(
        itemId = -1,
        itemName = "No $category",
        itemType = category,
        itemDescription = "",
        itemTags = emptyList(),
        isFavorite = false,
        isDeletionCandidate = false,
        itemPhotoUri = ""
    )

    val filteredItems = remember(items, wardrobeState, blockedCombos) {
        val base = if (items.isEmpty()) listOf(placeholderItem) else items
        base.filter { candidate ->
            val combo = listOf(
                if (category == "Headwear") candidate else wardrobeState.centeredHeadwear,
                if (category == "Topwear") candidate else wardrobeState.centeredTopwear,
                if (category == "Bottomwear") candidate else wardrobeState.centeredBottomwear,
                if (category == "Shoes") candidate else wardrobeState.centeredShoes
            )
            !wardrobeViewModel.isComboBlocked(combo)
        }.ifEmpty { listOf(placeholderItem) }
    }

    // ---- CIRCULAR SCROLL CONSTANTS ----
    val LOOP_SIZE = 10_000
    val middleIndex = LOOP_SIZE / 2
    val startIndex = middleIndex - (middleIndex % filteredItems.size)

    // Jump to center once on launch
    LaunchedEffect(filteredItems) {
        if (listState.firstVisibleItemIndex == 0)
            listState.scrollToItem(startIndex)
    }

    if (items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            contentAlignment = Alignment.Center
        ) { Text("No ${category.carouselType} Items Found") }
        return
    }
    // ---- UI ----
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        IconButton(
            onClick = {
                scope.launch {
                    listState.animateScrollToItem(listState.firstVisibleItemIndex - 1)
                }
            },
            modifier = Modifier.size(48.dp)
        ) { Icon(Icons.Default.KeyboardArrowLeft, null) }

        LazyRow(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .height(150.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 75.dp),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            items(LOOP_SIZE) { index ->
                val realIndex = index % filteredItems.size
                val item = filteredItems[realIndex]

                ClothingItemCard(
                    item = item,
                    isBlocked = false,
                    isSelected = (item == selectedItem)
                )
            }
        }

        IconButton(
            onClick = {
                scope.launch {
                    listState.animateScrollToItem(listState.firstVisibleItemIndex + 1)
                }
            },
            modifier = Modifier.size(48.dp)
        ) { Icon(Icons.Default.KeyboardArrowRight, null) }
    }

    // ---- CENTERED ITEM LOGIC ----
    LaunchedEffect(listState, filteredItems, wardrobeState, blockedCombos) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val visible = layoutInfo.visibleItemsInfo
                if (visible.isEmpty()) return@collect

                val center = layoutInfo.viewportStartOffset +
                        (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2

                val closest = visible.minByOrNull { itemInfo ->
                    val itemCenter = itemInfo.offset + itemInfo.size / 2
                    kotlin.math.abs(itemCenter - center)
                } ?: return@collect

                val realIndex = closest.index % filteredItems.size
                val centered = filteredItems[realIndex]

                val centeredCandidate = if (centered.itemId <= 0) null else centered

                // Auto-skip blocked centered item
                if (centeredCandidate != null) {
                    val combo = listOf(
                        if (category == "Headwear") centeredCandidate else wardrobeViewModel.wardrobeState.value.centeredHeadwear,
                        if (category == "Topwear") centeredCandidate else wardrobeViewModel.wardrobeState.value.centeredTopwear,
                        if (category == "Bottomwear") centeredCandidate else wardrobeViewModel.wardrobeState.value.centeredBottomwear,
                        if (category == "Shoes") centeredCandidate else wardrobeViewModel.wardrobeState.value.centeredShoes
                    )

                    if (wardrobeViewModel.isComboBlocked(combo)) {
                        val nextIndex = closest.index + 1
                        listState.animateScrollToItem(nextIndex)
                        return@collect
                    }
                }

                onCenteredItemChange(centeredCandidate)
            }
    }
}

@Composable
fun ClothingItemCard(item: ItemEntry) {
    var aspectRatio by remember { mutableFloatStateOf(1f) }

fun ClothingItemCard(
    item: ItemEntry,
    isBlocked: Boolean = false,    // kept for visual hints if you want later
    isSelected: Boolean = false    // we won't draw an outline — kept if you want styling
) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Color(0xFFE0E0E0).copy(alpha = if (isBlocked) 0.1f else 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        if (item.itemPhotoUri.isNotEmpty()) {
            AsyncImage(
                model = item.itemPhotoUri,
                contentDescription = item.itemName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (isBlocked) 0.5f else 1f)
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
            Text(text = item.itemName, color = if (isBlocked) Color.Gray else Color.Black)
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
