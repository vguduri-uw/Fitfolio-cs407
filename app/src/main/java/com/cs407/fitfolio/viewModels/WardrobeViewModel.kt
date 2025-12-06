package com.cs407.fitfolio.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.data.ItemEntry
import com.cs407.fitfolio.data.OutfitEntry
import com.cs407.fitfolio.data.OutfitDao
import com.cs407.fitfolio.ui.screens.uploadToImgbb
import com.cs407.fitfolio.viewModels.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.core.net.toUri
import com.cs407.fitfolio.data.BlockedCombination
import com.cs407.fitfolio.data.BlockedCombinationDao
import com.cs407.fitfolio.enums.CarouselTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

data class WardrobeState(
    val centeredHeadwear: ItemEntry? = null,
    val centeredTopwear: ItemEntry? = null,
    val centeredBottomwear: ItemEntry? = null,
    val centeredShoes: ItemEntry? = null
) {
    fun getCenteredItem(category: String): ItemEntry? = when (category) {
        "Headwear" -> centeredHeadwear
        "Topwear" -> centeredTopwear
        "Bottomwear" -> centeredBottomwear
        "Shoes" -> centeredShoes
        else -> null
    }
}

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
            current.centeredHeadwear?.itemId,
            current.centeredTopwear?.itemId,
            current.centeredBottomwear?.itemId,
            current.centeredShoes?.itemId
        ).toSet()

        if (ids.isEmpty() || _blockedCombos.contains(ids)) return

        _blockedCombos.add(ids)

        viewModelScope.launch(Dispatchers.IO) {
            db.blockedCombinationDao().insertCombination(
                BlockedCombination(
                    headwearId = current.centeredHeadwear?.itemId,
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
            headwear = allItemsByCategory[CarouselTypes.HEADWEAR] ?: emptyList(),
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
            val ids = listOfNotNull(combo.headwearId, combo.topwearId, combo.bottomwearId, combo.shoesId).toSet()
            _blockedCombos.add(ids)
        }
    }

    /** UPDATE CENTERED ITEM */
    fun updateCenteredItem(category: CarouselTypes, item: ItemEntry?) {
        val current = _wardrobeState.value
        val newState = when (category) {
            CarouselTypes.HEADWEAR -> current.copy(centeredHeadwear = item)
            CarouselTypes.TOPWEAR -> current.copy(centeredTopwear = item)
            CarouselTypes.BOTTOMWEAR -> current.copy(centeredBottomwear = item)
            CarouselTypes.FOOTWEAR -> current.copy(centeredShoes = item)
            else -> current
        }
        if (item == null || !isComboBlocked(
                listOf(
                    newState.centeredHeadwear,
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
                CarouselTypes.HEADWEAR -> setOf(
                    item.itemId,
                    current.centeredTopwear?.itemId,
                    current.centeredBottomwear?.itemId,
                    current.centeredShoes?.itemId
                ).filterNotNull().toSet()
                CarouselTypes.TOPWEAR -> setOf(
                    current.centeredHeadwear?.itemId,
                    item.itemId,
                    current.centeredBottomwear?.itemId,
                    current.centeredShoes?.itemId
                ).filterNotNull().toSet()
                CarouselTypes.BOTTOMWEAR -> setOf(
                    current.centeredHeadwear?.itemId,
                    current.centeredTopwear?.itemId,
                    item.itemId,
                    current.centeredShoes?.itemId
                ).filterNotNull().toSet()
                CarouselTypes.FOOTWEAR -> setOf(
                    current.centeredHeadwear?.itemId,
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
        headwear: List<ItemEntry>,
        topwear: List<ItemEntry>,
        bottomwear: List<ItemEntry>,
        shoes: List<ItemEntry>
    ) {
        val newState = WardrobeState(
            centeredHeadwear = headwear.firstOrNull { hw -> _blockedCombos.none { it.contains(hw.itemId) } },
            centeredTopwear = topwear.firstOrNull { tw -> _blockedCombos.none { it.contains(tw.itemId) } },
            centeredBottomwear = bottomwear.firstOrNull { bw -> _blockedCombos.none { it.contains(bw.itemId) } },
            centeredShoes = shoes.firstOrNull { s -> _blockedCombos.none { it.contains(s.itemId) } }
        )
        _wardrobeState.value = newState
    }
    private val _shuffledItems = MutableStateFlow<Map<CarouselTypes, List<ItemEntry>>>(emptyMap())
    val shuffledItems = _shuffledItems.asStateFlow()

    fun shuffleItems(
        headwearList: List<ItemEntry>,
        topwearList: List<ItemEntry>,
        bottomwearList: List<ItemEntry>,
        shoesList: List<ItemEntry>
    ) {
        _shuffledItems.value = mapOf(
            CarouselTypes.HEADWEAR to headwearList.shuffled(),
            CarouselTypes.TOPWEAR to topwearList.shuffled(),
            CarouselTypes.BOTTOMWEAR to bottomwearList.shuffled(),
            CarouselTypes.FOOTWEAR to shoesList.shuffled(),
        )
    }


    fun dressMe(
        context: Context,
        apiKey: String,
        avatarUri: String?,          // optional user avatar
        garmentUrls: List<String>,   // local or remote garment images
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1️⃣ Prepare garment URLs: upload local files if needed
                val hostedUrls = mutableListOf<String>()
                for (u in garmentUrls) {
                    if (u.startsWith("http://") || u.startsWith("https://")) {
                        hostedUrls.add(u)
                    } else {
                        val localUri = Uri.parse(u)
                        val uploaded =
                            withContext(Dispatchers.IO) { uploadToImgbb(localUri, context) }
                        if (uploaded.isNullOrBlank()) {
                            onError("Failed to upload local image: $u")
                            return@launch
                        }
                        hostedUrls.add(uploaded)
                    }
                }
                if (hostedUrls.isEmpty()) {
                    onError("No valid garment images available.")
                    return@launch
                }

                // 2️⃣ Prepare model/avatar URL if available
                val modelUrl = avatarUri?.let { uri ->
                    if (uri.startsWith("http")) uri
                    else withContext(Dispatchers.IO) { uploadToImgbb(Uri.parse(uri), context) }
                }

                // 3️⃣ Call Fashn API to start try-on job
                val client = OkHttpClient()
                val payload = JSONObject().apply {
                    put("garment_image", hostedUrls[0])       // only the first garment
                    modelUrl?.let { put("model_image", it) }  // optional avatar
                }
                val body = payload.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("https://api.fashn.ai/v1/tryon")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.use { it.string() }
                if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                    onError("Try-on request failed: code=${response.code} body=$responseBody")
                    return@launch
                }

                val json = JSONObject(responseBody)
                val jobId = json.optString("id")
                if (jobId.isBlank()) {
                    onError("Try-on API did not return a job ID.")
                    return@launch
                }
                Log.d("WardrobeVM", "Fashn try-on job started: $jobId")

                // 4️⃣ Poll for job result
                val previewUrl = getTryOnResult(apiKey, jobId)
                if (previewUrl.isNullOrBlank()) {
                    onError("Fashn failed to generate try-on image.")
                    return@launch
                }

                // 5️⃣ Success -> update preview state
                _tryOnPreview.value = previewUrl
                Log.d("WardrobeVM", "Try-on preview ready: $previewUrl")

            } catch (e: Exception) {
                e.printStackTrace()
                onError("Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    /** Poll the Fashn API for try-on result until ready (max ~20 seconds) */
    private suspend fun getTryOnResult(apiKey: String, jobId: String): String? {
        val client = OkHttpClient()
        val url = "https://api.fashn.ai/v1/jobs/$jobId"  // ✅ correct endpoint
        repeat(20) { // retry up to 20 times
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .get()
                .build()
            try {
                val response = client.newCall(request).execute()
                val body = response.body?.use { it.string() }
                if (!response.isSuccessful) {
                    Log.d("WardrobeVM", "Job poll failed: code=${response.code} body=$body")
                } else if (!body.isNullOrBlank()) {
                    val json = JSONObject(body)
                    val output = json.optJSONArray("output")?.optString(0)
                    if (!output.isNullOrBlank()) return output
                }
            } catch (e: Exception) {
                Log.e("WardrobeVM", "Error polling job $jobId", e)
            }
            delay(1000) // wait 1 second before next retry
        }
        return null
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
            currentState.centeredHeadwear?.itemPhotoUri,
            currentState.centeredTopwear?.itemPhotoUri,
            currentState.centeredBottomwear?.itemPhotoUri,
            currentState.centeredShoes?.itemPhotoUri
        )

        if (garmentUrls.isEmpty()) {
            onError("No items selected for try-on.")
            return
        }

        // Call dressMe internally
        dressMe(
            context = context,
            apiKey = apiKey,
            avatarUri = avatarUri,
            garmentUrls = garmentUrls,
            onError = onError
        )
    }

}