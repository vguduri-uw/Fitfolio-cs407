package com.cs407.fitfolio.viewModels

import android.content.Context
import android.widget.Toast
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

data class WardrobeState(
    val centeredAccessories: ItemEntry? = null,
    val centeredTopwear: ItemEntry? = null,
    val centeredBottomwear: ItemEntry? = null,
    val centeredShoes: ItemEntry? = null
)

class WardrobeViewModel(
    private val db: FitfolioDatabase,
    private val userViewModel: UserViewModel
) : ViewModel() {
    private val _tryOnPreview = MutableStateFlow<String?>(null)
    val tryOnPreview: StateFlow<String?> = _tryOnPreview.asStateFlow()
    private val _wardrobeState = MutableStateFlow(WardrobeState())
    val wardrobeState: StateFlow<WardrobeState> = _wardrobeState.asStateFlow()

    private val _blockedCombos = mutableStateListOf<Set<Int>>()
    val blockedCombos: List<Set<Int>> get() = _blockedCombos

    init {
        viewModelScope.launch { loadBlockedCombinations() }
    }

    /** BLOCK CURRENT COMBINATION */
    fun blockCurrentCombination() {
        val current = _wardrobeState.value
        val ids = listOfNotNull(
            current.centeredAccessories?.itemId,
            current.centeredTopwear?.itemId,
            current.centeredBottomwear?.itemId,
            current.centeredShoes?.itemId
        ).toSet()

        if (ids.isEmpty() || _blockedCombos.contains(ids)) return

        _blockedCombos.add(ids)

        viewModelScope.launch(Dispatchers.IO) {
            db.blockedCombinationDao().insertCombination(
                BlockedCombination(
                    accessoryId = current.centeredAccessories?.itemId,
                    topwearId = current.centeredTopwear?.itemId,
                    bottomwearId = current.centeredBottomwear?.itemId,
                    shoesId = current.centeredShoes?.itemId
                )
            )
        }
    }

    /** REMOVE CURRENT COMBINATION AND RELOAD WARDROBE */
    fun removeCurrentCombination(allItemsByCategory: Map<CarouselTypes, List<ItemEntry>>) {
        blockCurrentCombination()
        loadWardrobe(
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
        val current = _wardrobeState.value
        val newState = when (category) {
            CarouselTypes.ACCESSORIES -> current.copy(centeredAccessories = item)
            CarouselTypes.TOPWEAR -> current.copy(centeredTopwear = item)
            CarouselTypes.BOTTOMWEAR -> current.copy(centeredBottomwear = item)
            CarouselTypes.FOOTWEAR -> current.copy(centeredShoes = item)
            else -> current
        }
        if (item == null || !isComboBlocked(
                listOf(
                    newState.centeredAccessories,
                    newState.centeredTopwear,
                    newState.centeredBottomwear,
                    newState.centeredShoes
                )
            )
        ) {
            _wardrobeState.value = newState
        }
    }

    /** GET VALID ITEMS FOR A CATEGORY (FILTER BLOCKED) */
    fun getValidItemsForCategory(category: CarouselTypes, allItems: List<ItemEntry>): List<ItemEntry> {
        val current = _wardrobeState.value
        return allItems.filter { item ->
            val combo = when (category) {
                CarouselTypes.ACCESSORIES -> setOf(
                    item.itemId,
                    current.centeredTopwear?.itemId,
                    current.centeredBottomwear?.itemId,
                    current.centeredShoes?.itemId
                ).filterNotNull().toSet()
                CarouselTypes.TOPWEAR -> setOf(
                    current.centeredAccessories?.itemId,
                    item.itemId,
                    current.centeredBottomwear?.itemId,
                    current.centeredShoes?.itemId
                ).filterNotNull().toSet()
                CarouselTypes.BOTTOMWEAR -> setOf(
                    current.centeredAccessories?.itemId,
                    current.centeredTopwear?.itemId,
                    item.itemId,
                    current.centeredShoes?.itemId
                ).filterNotNull().toSet()
                CarouselTypes.FOOTWEAR -> setOf(
                    current.centeredAccessories?.itemId,
                    current.centeredTopwear?.itemId,
                    current.centeredBottomwear?.itemId,
                    item.itemId
                ).filterNotNull().toSet()
                else -> setOf(item.itemId)
            }
            _blockedCombos.none { it == combo }
        }
    }

    /** LOAD WARDROBE WITH BLOCKED FILTER */
    fun loadWardrobe(
        accessories: List<ItemEntry>,
        topwear: List<ItemEntry>,
        bottomwear: List<ItemEntry>,
        shoes: List<ItemEntry>
    ) {
        val newState = WardrobeState(
            centeredAccessories = accessories.firstOrNull { hw -> _blockedCombos.none { it.contains(hw.itemId) } },
            centeredTopwear = topwear.firstOrNull { tw -> _blockedCombos.none { it.contains(tw.itemId) } },
            centeredBottomwear = bottomwear.firstOrNull { bw -> _blockedCombos.none { it.contains(bw.itemId) } },
            centeredShoes = shoes.firstOrNull { s -> _blockedCombos.none { it.contains(s.itemId) } }
        )
        _wardrobeState.value = newState
    }

    fun shuffleItems(
        accessoriesList: List<ItemEntry>,
        topwearList: List<ItemEntry>,
        bottomwearList: List<ItemEntry>,
        shoesList: List<ItemEntry>
    ) {
        loadWardrobe(
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


    fun generateTryOnPreview(
        context: Context,
        apiKey: String,
        avatarUri: String? = null,
        onError: (String) -> Unit = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    ) {
        // Gather current centered items as garment URLs
        val currentState = _wardrobeState.value
        val garmentUrls = listOfNotNull(
            currentState.centeredAccessories?.itemPhotoUri,
            currentState.centeredTopwear?.itemPhotoUri,
            currentState.centeredBottomwear?.itemPhotoUri,
            currentState.centeredShoes?.itemPhotoUri
        )

        if (garmentUrls.isEmpty()) {
            onError("No items selected for try-on.")
            return
        }

        // Call dressMe internally
        // TODO: Generate try on
//        dressMe(
//            context = context,
//            apiKey = apiKey,
//            avatarUri = avatarUri,
//            garmentUrls = garmentUrls,
//            onError = onError
//        )
    }
}