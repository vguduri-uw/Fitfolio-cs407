package com.cs407.fitfolio.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.outlined.LayersClear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.max
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.cs407.fitfolio.data.ItemEntry
import com.cs407.fitfolio.enums.CarouselTypes
import com.cs407.fitfolio.ui.modals.OutfitModal
import com.cs407.fitfolio.ui.theme.LightChocolate
import com.cs407.fitfolio.ui.theme.LightPeachFuzz
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.viewModels.ClosetState
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import com.cs407.fitfolio.viewModels.UserViewModel
import com.cs407.fitfolio.viewModels.CarouselState
import com.cs407.fitfolio.viewModels.CarouselViewModel
import kotlinx.coroutines.CoroutineScope
import kotlin.math.abs

@Composable
fun CarouselScreen(
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToAddScreen: () -> Unit,
    onNavigateToClosetScreen: () -> Unit,
    onNavigateToSignInScreen: () -> Unit,
    closetViewModel: ClosetViewModel,
    weatherViewModel: WeatherViewModel,
    outfitsViewModel: OutfitsViewModel,
    userViewModel: UserViewModel,
    carouselViewModel: CarouselViewModel
) {
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val carouselState by carouselViewModel.carouselState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showTryOnModal by remember { mutableStateOf(false) }
    var showOutfitModal by remember { mutableStateOf(false) }
    var createdOutfitId by remember { mutableIntStateOf(-1) }
    var saveError by remember { mutableStateOf(false) }

    val categories = CarouselTypes.entries.filter {
        it != CarouselTypes.DEFAULT && it != CarouselTypes.ONE_PIECES
    }
    // Use filteredItems if available, else fallback to full item list
    val allItems = closetState.filteredItems.takeIf { !it.isNullOrEmpty() } ?: closetState.items

    // Load carousel items by carouselType
    LaunchedEffect(Unit) {
        carouselViewModel.loadCarousel(
            accessories = allItems.filter { it.carouselType == CarouselTypes.ACCESSORIES },
            topwear = allItems.filter {
                it.carouselType == CarouselTypes.TOPWEAR || it.carouselType == CarouselTypes.ONE_PIECES
            },
            bottomwear = allItems.filter { it.carouselType == CarouselTypes.BOTTOMWEAR },
            shoes = allItems.filter { it.carouselType == CarouselTypes.FOOTWEAR }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SimpleHeader(
                title = "Outfit Carousel",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            WeatherDataChip(weatherData = weatherState.weatherData)
        }

        categories.forEach { category ->
            val filteredItems =
                when(category) {
                    CarouselTypes.TOPWEAR ->
                        allItems.filter {
                            it.carouselType == CarouselTypes.TOPWEAR ||
                                    it.carouselType == CarouselTypes.ONE_PIECES
                        }

                    else -> allItems.filter { it.carouselType == category }
                }
            val placeholder = carouselViewModel.getPlaceholder(category)

            val itemsWithPlaceholder =
                if (filteredItems.isNotEmpty()) listOf(placeholder) + filteredItems
                else filteredItems

            if (itemsWithPlaceholder.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No ${category.carouselType.lowercase()} found",
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontSize = 18.sp
                    )
                }
            } else {
                ClothingScroll(
                    selectedItem = when (category) {
                        CarouselTypes.ACCESSORIES -> carouselState.centeredAccessory
                        CarouselTypes.TOPWEAR -> carouselState.centeredTopwear
                        CarouselTypes.BOTTOMWEAR -> carouselState.centeredBottomwear
                        CarouselTypes.FOOTWEAR -> carouselState.centeredShoes
                        else -> null
                    },
                    category = category,
                    carouselViewModel = carouselViewModel,
                    onCenteredItemChange = { centered -> carouselViewModel.updateCenteredItem(category, centered) },
                    closetItems = itemsWithPlaceholder
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ActionButtonsRow(
            closetState = closetState,
            closetViewModel = closetViewModel,
            carouselState = carouselState,
            carouselViewModel = carouselViewModel,
            outfitsViewModel = outfitsViewModel,
            userViewModel = userViewModel,
            scope = scope,
            context = context,
            onTryOn = { showTryOnModal = true },
            onOutfitSaved = { id -> createdOutfitId = id; showOutfitModal = true },
            onSaveError = { saveError = true }
        )
    }

    if (showTryOnModal) {
        TryOnModal(
            carouselState = carouselState,
            carouselViewModel = carouselViewModel,
            outfitsViewModel = outfitsViewModel,
            userViewModel = userViewModel,
            onDismiss = { showTryOnModal = false },
            context = context,
            scope = scope
        )
    }

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
fun ActionButtonsRow(
    closetState: ClosetState,
    closetViewModel: ClosetViewModel,
    carouselState: CarouselState,
    carouselViewModel: CarouselViewModel,
    outfitsViewModel: OutfitsViewModel,
    userViewModel: UserViewModel,
    scope: CoroutineScope,
    context: Context,
    onTryOn: () -> Unit,
    onOutfitSaved: (Int) -> Unit,
    onSaveError: () -> Unit
) {
    val userState by userViewModel.userState.collectAsState()
    var isUploading by remember { mutableStateOf(false) }

    val itemsToAdd by remember(carouselState) {
        derivedStateOf {
            listOfNotNull(
                carouselState.centeredAccessory,
                carouselState.centeredTopwear,
                carouselState.centeredBottomwear.takeIf {
                    carouselState.centeredTopwear?.carouselType != CarouselTypes.ONE_PIECES
                },
                carouselState.centeredShoes
            ).filter { it.itemId > 0 }
        }
    }

    if (isUploading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightChocolate),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                // Shuffle button using carouselType
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(LightPeachFuzz),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            carouselViewModel.shuffleItems(
                                accessoriesList = closetState.items.filter { it.carouselType == CarouselTypes.ACCESSORIES },
                                topwearList = closetState.items.filter { it.carouselType == CarouselTypes.TOPWEAR },
                                bottomwearList = closetState.items.filter { it.carouselType == CarouselTypes.BOTTOMWEAR },
                                shoesList = closetState.items.filter { it.carouselType == CarouselTypes.FOOTWEAR }
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.shuffle),
                            contentDescription = "shuffle",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))

                // Filter by favorites button
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(LightPeachFuzz),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { carouselViewModel.toggleFavorites() }
                    ) {
                        Icon(
                            painter = if (carouselState.isFavoritesActive) painterResource(R.drawable.heart_filled_red) else painterResource(
                                R.drawable.heart_outline
                            ),
                            contentDescription = "Filter by favorites",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))

                // TODO: decide if we want remove button or not
                // Remove combination button
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(LightPeachFuzz),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            val allItemsByCategory = mapOf(
                                CarouselTypes.ACCESSORIES to closetState.items.filter { it.carouselType == CarouselTypes.ACCESSORIES },
                                CarouselTypes.TOPWEAR to closetState.items.filter { it.carouselType == CarouselTypes.TOPWEAR },
                                CarouselTypes.BOTTOMWEAR to closetState.items.filter { it.carouselType == CarouselTypes.BOTTOMWEAR },
                                CarouselTypes.FOOTWEAR to closetState.items.filter { it.carouselType == CarouselTypes.FOOTWEAR }
                            )

                            carouselViewModel.removeCurrentCombination(allItemsByCategory)
                            Toast.makeText(context, "Combination removed!", Toast.LENGTH_SHORT)
                                .show()
                        },
                        enabled = itemsToAdd.isNotEmpty()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.minus),
                            contentDescription = "Remove combination",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))

                // Reset button
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(LightPeachFuzz),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            carouselViewModel.updateCenteredItem(CarouselTypes.TOPWEAR, carouselState.placeholderTopwear)
                            carouselViewModel.updateCenteredItem(CarouselTypes.BOTTOMWEAR, carouselState.placeholderBottomwear)
                            carouselViewModel.updateCenteredItem(CarouselTypes.FOOTWEAR, carouselState.placeholderShoes)
                            carouselViewModel.updateCenteredItem(CarouselTypes.ACCESSORIES, carouselState.placeholderAccessory)
                        },
                        enabled = itemsToAdd.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LayersClear,
                            contentDescription = "Set placeholder item",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            // Add / Save Outfit / Dress me button
            Row(horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = {
                        scope.launch {
                            // Verify that there are items to add
                            if (itemsToAdd.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Select at least one item to save an outfit.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@launch
                            }

                            // Verify that an avatar exists
                            if (userState.avatarUri.isNullOrEmpty()) {
                                Toast.makeText(
                                    context,
                                    " Please upload your personal avatar in settings before you can save an outfit.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@launch
                            }

                            try {
                                isUploading = true
                                val result = carouselViewModel.dressMe(
                                    productUrls = itemsToAdd.map { it.itemPhotoUri },
                                    modelUrl = userState.avatarUri,
                                    carouselTypes = itemsToAdd.map { it.carouselType }
                                )

                                Log.d("Carousel", "dressMe returned = $result")

                                if (result.isNullOrEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "Could not generate outfit image. Please try again.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    isUploading = false
                                    return@launch
                                }

                                val outfitId = outfitsViewModel.addOutfit(
                                    name = "New Outfit",
                                    description = "Created from Carousel",
                                    tags = emptyList(),
                                    isFavorite = false,
                                    photoUri = result,
                                    itemList = itemsToAdd
                                )
                                isUploading = false

                                if (outfitId > 0) {
                                    Toast.makeText(context, "Outfit saved!", Toast.LENGTH_SHORT)
                                        .show()
                                    onOutfitSaved(outfitId)
                                } else {
                                    onSaveError()
                                }

                            } catch (e: Exception) {
                                isUploading = false
                                onSaveError()
                            }
                        }
                    },
                    enabled = itemsToAdd.isNotEmpty()
                ) {
                    Text("Dress me ✨")
                }
            }
        }
    }
}

@Composable
fun ClothingScroll(
    closetItems: List<ItemEntry>,
    selectedItem: ItemEntry?,
    category: CarouselTypes,
    carouselViewModel: CarouselViewModel,
    onCenteredItemChange: (ItemEntry?) -> Unit
) {
    val carouselState by carouselViewModel.carouselState.collectAsState()
    val scope = rememberCoroutineScope()
    val cardHeight =
        if (category == CarouselTypes.TOPWEAR &&
            carouselState.centeredTopwear?.carouselType == CarouselTypes.ONE_PIECES)
            320.dp
        else
            150.dp
    val shouldShow = when(category) {
        CarouselTypes.BOTTOMWEAR ->
            carouselState.centeredTopwear?.carouselType != CarouselTypes.ONE_PIECES

        else -> true
    }
    if (!shouldShow) return

    // Dynamically filter items based on currently centered items in other categories
    val filteredItems by remember(carouselState, closetItems) {
        derivedStateOf {
            closetItems
                .filter { item ->
                    (!carouselState.isFavoritesActive || item.isFavorite) &&
                            carouselViewModel.blockedCombos.none { combo ->
                                combo == listOfNotNull(
                                    carouselState.centeredAccessory.takeIf { category == CarouselTypes.ACCESSORIES },
                                    carouselState.centeredTopwear.takeIf { category == CarouselTypes.TOPWEAR },
                                    carouselState.centeredBottomwear.takeIf { category == CarouselTypes.BOTTOMWEAR },
                                    carouselState.centeredShoes.takeIf { category == CarouselTypes.FOOTWEAR },
                                    item.takeIf { true }
                                ).mapNotNull { it?.itemId }.toSet()
                            }
                }
        }
    }

    if (filteredItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No ${category.name.lowercase().replace('_', ' ')} items available")
        }
        return
    }

    val stableItems = remember(filteredItems.size) { filteredItems.toList() }
    val listState = rememberLazyListState()
    val LOOP_SIZE = 10_000
    val middleIndex = LOOP_SIZE / 2
    val startIndex = middleIndex - (middleIndex % stableItems.size)

    LaunchedEffect(selectedItem) {
        selectedItem?.let {
            val index = stableItems.indexOf(it)
            if (index != -1) {
                val target = startIndex + index
                listState.scrollToItem(target)
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(listState.firstVisibleItemIndex - 1)
                        }
                    }
                )
                .padding(end = 20.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.left_chevron),
                contentDescription = "Previous",
                modifier = Modifier
                    .size(25.dp)
            )
        }

        val flingBehavior = rememberSnapFlingBehavior(listState)
        LazyRow(
            state = listState,
            modifier = Modifier.weight(1f).height(cardHeight),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 75.dp),
            flingBehavior = flingBehavior
        ) {
            items(LOOP_SIZE) { index ->
                val realIndex = index % stableItems.size
                ClothingItemCard(stableItems[realIndex])
            }
        }

        Box(
            modifier = Modifier
                .clickable(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(listState.firstVisibleItemIndex + 1)
                        }
                    }
                )
                .padding(start = 20.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.right_chevron),
                contentDescription = "Next",
                modifier = Modifier
                    .size(25.dp)
            )
        }
    }

    // Update the centered item
    val centeredItem by remember {
        derivedStateOf {
            val visible = listState.layoutInfo.visibleItemsInfo
            if (visible.isEmpty()) return@derivedStateOf null
            val center = (listState.layoutInfo.viewportStartOffset + listState.layoutInfo.viewportEndOffset) / 2
            val closet = visible.minByOrNull { abs(it.offset + it.size / 2 - center) } ?: return@derivedStateOf null
            stableItems[closet.index % stableItems.size]

        }
    }

    LaunchedEffect(centeredItem) {
        centeredItem?.let { onCenteredItemChange(it) }
    }
}
@Composable
fun ClothingItemCard(item: ItemEntry, isBlocked: Boolean = false) {
    var aspectRatio by remember { mutableFloatStateOf(1f) }
    val cardHeight =
        if (item.carouselType == CarouselTypes.ONE_PIECES)
            320.dp // one piece height
        else
            150.dp

    Box(
        modifier = Modifier
            .height( max(150.dp, cardHeight - 40.dp) )
            .width(150.dp)
            .clip(MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        if (item.itemPhotoUri.isNotBlank()) {
            AsyncImage(
                model = item.itemPhotoUri,
                contentDescription = item.itemName,
                modifier = Modifier.fillMaxSize().aspectRatio(aspectRatio),
                contentScale = ContentScale.Fit,
                alpha = if (isBlocked) 0.5f else 1f,
                onSuccess = { result ->
                    val w = result.result.drawable.intrinsicWidth
                    val h = result.result.drawable.intrinsicHeight
                    if (w > 0 && h > 0) aspectRatio = (w.toFloat() / h.toFloat()).coerceIn(0.55f, 2f)
                }
            )
        } else {
            // fallback if no image is available
            if (item.itemId != -1) { // do not show icon for placeholder slot
                Icon(
                    painter = painterResource(R.drawable.hanger),
                    contentDescription = "No item image found"
                )
            }
        }
    }
}

@Composable
fun TryOnModal(
    carouselState: CarouselState,
    carouselViewModel: CarouselViewModel,
    outfitsViewModel: OutfitsViewModel,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
    context: Context,
    scope: CoroutineScope
) {
    val tryOnUrl by carouselViewModel.tryOnPreview.collectAsState()

    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Try-On Preview", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                if (!tryOnUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = tryOnUrl,
                        contentDescription = "Try-On Image",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text("Loading...")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onDismiss() }) {
                    Text("Close")
                }
            }
        }
    }
}