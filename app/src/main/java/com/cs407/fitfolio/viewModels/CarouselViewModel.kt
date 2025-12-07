package com.cs407.fitfolio.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.data.ItemEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.cs407.fitfolio.data.BlockedCombination
import com.cs407.fitfolio.enums.CarouselTypes
import com.cs407.fitfolio.services.FashnRunRequest
import com.cs407.fitfolio.services.FashnStatusResponse
import com.cs407.fitfolio.services.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

data class CarouselState(
    val centeredAccessory: ItemEntry? = null,
    val centeredTopwear: ItemEntry? = null,
    val centeredBottomwear: ItemEntry? = null,
    val centeredShoes: ItemEntry? = null,
    val isFavoritesActive: Boolean = false,
    val placeholderTopwear: ItemEntry = ItemEntry(-1, "No Topwear", "", CarouselTypes.TOPWEAR, "", emptyList(), false, false, ""),
    val placeholderBottomwear: ItemEntry = ItemEntry(-1, "No Bottomwear", "", CarouselTypes.BOTTOMWEAR, "", emptyList(), false, false, ""),
    val placeholderShoes: ItemEntry = ItemEntry(-1, "No Footwear", "", CarouselTypes.FOOTWEAR, "", emptyList(), false, false, ""),
    val placeholderAccessory: ItemEntry = ItemEntry(-1, "No Accessories", "", CarouselTypes.ACCESSORIES, "", emptyList(), false, false, ""),
)

class CarouselViewModel(
    private val db: FitfolioDatabase,
    private val userViewModel: UserViewModel
) : ViewModel() {
    private val _tryOnPreview = MutableStateFlow<String?>(null)
    val tryOnPreview: StateFlow<String?> = _tryOnPreview.asStateFlow()
    private val _carouselState = MutableStateFlow(CarouselState())
    val carouselState: StateFlow<CarouselState> = _carouselState.asStateFlow()

    private val _blockedCombos = mutableStateListOf<Set<Int>>()
    val blockedCombos: List<Set<Int>> get() = _blockedCombos

    init {
        viewModelScope.launch { loadBlockedCombinations() }
    }

    // Gets the place holder card for carousel types
    fun getPlaceholder(category: CarouselTypes): ItemEntry =
        when(category) {
            CarouselTypes.TOPWEAR -> _carouselState.value.placeholderTopwear
            CarouselTypes.ONE_PIECES -> _carouselState.value.placeholderTopwear // use top slot
            CarouselTypes.BOTTOMWEAR -> _carouselState.value.placeholderBottomwear
            CarouselTypes.FOOTWEAR -> _carouselState.value.placeholderShoes
            CarouselTypes.ACCESSORIES -> _carouselState.value.placeholderAccessory
            else -> throw IllegalArgumentException("Invalid category")
    }

    /** BLOCK CURRENT COMBINATION */
    fun blockCurrentCombination() {
        val current = _carouselState.value
        val ids = listOfNotNull(
            current.centeredAccessory?.itemId,
            current.centeredTopwear?.itemId,
            current.centeredBottomwear?.itemId,
            current.centeredShoes?.itemId
        ).toSet()

        if (ids.isEmpty() || _blockedCombos.contains(ids)) return

        _blockedCombos.add(ids)

        viewModelScope.launch(Dispatchers.IO) {
            db.blockedCombinationDao().insertCombination(
                BlockedCombination(
                    accessoryId = current.centeredAccessory?.itemId,
                    topwearId = current.centeredTopwear?.itemId,
                    bottomwearId = current.centeredBottomwear?.itemId,
                    shoesId = current.centeredShoes?.itemId
                )
            )
        }
    }

    /** REMOVE CURRENT COMBINATION AND RELOAD CAROUSEL */
    fun removeCurrentCombination(allItemsByCategory: Map<CarouselTypes, List<ItemEntry>>) {
        blockCurrentCombination()
        loadCarousel(
            accessories = allItemsByCategory[CarouselTypes.ACCESSORIES] ?: emptyList(),
            topwear = allItemsByCategory[CarouselTypes.TOPWEAR] ?: emptyList(),
            bottomwear = allItemsByCategory[CarouselTypes.BOTTOMWEAR] ?: emptyList(),
            shoes = allItemsByCategory[CarouselTypes.FOOTWEAR] ?: emptyList()
        )
    }

    /** CHECK IF A COMBINATION IS BLOCKED */
    fun isComboBlocked(combo: List<ItemEntry?>): Boolean {
        val ids = combo.filterNotNull().map { it.itemId }.toSet()
        return _blockedCombos.any { it == ids }
    }

    /** LOAD BLOCKED COMBINATIONS FROM DATABASE */
    private suspend fun loadBlockedCombinations() {
        val combos = withContext(Dispatchers.IO) {
            db.blockedCombinationDao().getAllBlockedCombinations()
        }
        _blockedCombos.clear()
        combos.forEach { combo ->
            val ids = listOfNotNull(combo.accessoryId, combo.topwearId, combo.bottomwearId, combo.shoesId).toSet()
            _blockedCombos.add(ids)
        }
    }

    /** UPDATE CENTERED ITEM */
    fun updateCenteredItem(category: CarouselTypes, item: ItemEntry?) {
        val current = _carouselState.value

        val newState = when (category) {

            CarouselTypes.ONE_PIECES ->
                current.copy(
                    centeredTopwear = item,
                    centeredBottomwear = null
                )

            CarouselTypes.TOPWEAR ->
                current.copy(centeredTopwear = item)

            CarouselTypes.BOTTOMWEAR ->
                if (current.centeredTopwear?.carouselType == CarouselTypes.ONE_PIECES)
                    current
                else
                    current.copy(centeredBottomwear = item)

            CarouselTypes.FOOTWEAR ->
                current.copy(centeredShoes = item)

            CarouselTypes.ACCESSORIES ->
                current.copy(centeredAccessory = item)

            else -> current
        }

        _carouselState.value = newState
    }

    /** GET VALID ITEMS FOR A CATEGORY (FILTER BLOCKED) */
    fun getValidItemsForCategory(category: CarouselTypes, allItems: List<ItemEntry>): List<ItemEntry> {
        val current = _carouselState.value
        return allItems.filter { item ->
            val combo = when (category) {
                CarouselTypes.ACCESSORIES -> setOf(
                    item.itemId,
                    current.centeredTopwear?.itemId,
                    current.centeredBottomwear?.itemId,
                    current.centeredShoes?.itemId
                ).filterNotNull().toSet()
                CarouselTypes.TOPWEAR -> setOf(
                    current.centeredAccessory?.itemId,
                    item.itemId,
                    current.centeredBottomwear?.itemId,
                    current.centeredShoes?.itemId
                ).filterNotNull().toSet()
                CarouselTypes.BOTTOMWEAR -> setOf(
                    current.centeredAccessory?.itemId,
                    current.centeredTopwear?.itemId,
                    item.itemId,
                    current.centeredShoes?.itemId
                ).filterNotNull().toSet()
                CarouselTypes.FOOTWEAR -> setOf(
                    current.centeredAccessory?.itemId,
                    current.centeredTopwear?.itemId,
                    current.centeredBottomwear?.itemId,
                    item.itemId
                ).filterNotNull().toSet()
                else -> setOf(item.itemId)
            }
            _blockedCombos.none { it == combo }
        }
    }

    /** LOAD CAROUSEL WITH BLOCKED FILTER */
    fun loadCarousel(
        accessories: List<ItemEntry>,
        topwear: List<ItemEntry>,
        bottomwear: List<ItemEntry>,
        shoes: List<ItemEntry>
    ) {
        val newState = CarouselState(
            centeredAccessory = accessories.firstOrNull { hw -> _blockedCombos.none { it.contains(hw.itemId) } },
            centeredTopwear = topwear.firstOrNull { tw -> _blockedCombos.none { it.contains(tw.itemId) } },
            centeredBottomwear = bottomwear.firstOrNull { bw -> _blockedCombos.none { it.contains(bw.itemId) } },
            centeredShoes = shoes.firstOrNull { s -> _blockedCombos.none { it.contains(s.itemId) } }
        )
        _carouselState.value = newState
    }

    // Toggle favorites property
    fun toggleFavorites() {
        val current = _carouselState.value
        _carouselState.value = current.copy(
            isFavoritesActive = !current.isFavoritesActive
        )
    }

    // Shuffle carousel screen
    fun shuffleItems(
        accessoriesList: List<ItemEntry>,
        topwearList: List<ItemEntry>,
        bottomwearList: List<ItemEntry>,
        shoesList: List<ItemEntry>
    ) {
        loadCarousel(
            accessories = accessoriesList.shuffled(),
            topwear = topwearList.shuffled(),
            bottomwear = bottomwearList.shuffled(),
            shoes = shoesList.shuffled()
        )
    }

    suspend fun dressMe(
        productUrls: List<String>,
        modelUrl: String,
        carouselTypes: List<CarouselTypes>
    ): String? {
        val ordered = productUrls.zip(carouselTypes).sortedBy { (_, type) ->
            when (type) {
                CarouselTypes.BOTTOMWEAR -> 0
                CarouselTypes.TOPWEAR    -> 1
                CarouselTypes.FOOTWEAR   -> 2
                CarouselTypes.ACCESSORIES   -> 3
                else -> 99
            }
        }
        var currOutfitImage: String? = modelUrl

        for ((url, type) in ordered) {
            currOutfitImage = productToModel(url, currOutfitImage)
            if (currOutfitImage == null) return null
        }

        return currOutfitImage
    }

    suspend fun productToModel(
        productUrl: String,
        modelUrl: String?
    ): String? = withContext(Dispatchers.IO) {
        println("modelUrl = $modelUrl")
        println("productUrl = $productUrl")

        try {
            val runRequest = FashnRunRequest(
                model_name = "product-to-model",
                inputs = buildMap {
                    put("product_image", productUrl)
                    modelUrl?.let { put("model_image", it) }
                    put("output_format", "png")
                    put("return_base64", false)
                }
            )

            val runResponse = RetrofitInstance.fashnApi.runModel(runRequest)
            val predictionId = runResponse.id

            println("productToModel RUN PREDICTION ID = $predictionId")

            var status: FashnStatusResponse
            do {
                delay(1500)
                status = RetrofitInstance.fashnApi.getPredictionStatus(predictionId)
            } while (status.status != "completed" && status.status != "failed")

            return@withContext when (status.status) {
                "completed" -> status.output.firstOrNull()
                else -> {
                    println("productToModel FAILED: ${status.error?.message}")
                    null
                }
            }

        } catch (e: Exception) {
            println("productToModel EXCEPTION: ${e.message}")
            null
        }
    }
}