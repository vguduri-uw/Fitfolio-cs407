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

    private val _wardrobeState = MutableStateFlow(WardrobeState())
    val wardrobeState: StateFlow<WardrobeState> = _wardrobeState.asStateFlow()

    private val _tryOnPreview = MutableStateFlow<String?>(null)
    val tryOnPreview: StateFlow<String?> = _tryOnPreview.asStateFlow()

    // Store blocked combinations as lists of itemIds
    // store blocked combos as sets of item IDs
    private val _blockedCombos = mutableStateListOf<Set<Int>>()
    val blockedCombos: List<Set<Int>> get() = _blockedCombos

    fun blockCurrentCombination(
        headwear: ItemEntry?,
        topwear: ItemEntry?,
        bottomwear: ItemEntry?,
        shoes: ItemEntry?
    ) {
        val comboSet = listOf(headwear, topwear, bottomwear, shoes)
            .filterNotNull()
            .map { it.itemId }
            .toSet()

        if (comboSet.isEmpty() || comboSet in _blockedCombos) return

        _blockedCombos.add(comboSet)

        // Save into Room
        viewModelScope.launch(Dispatchers.IO) {
            db.blockedCombinationDao().insertCombination(
                BlockedCombination(
                    headwearId = headwear?.itemId,
                    topwearId = topwear?.itemId,
                    bottomwearId = bottomwear?.itemId,
                    shoesId = shoes?.itemId
                )
            )
        }
    }

    fun isComboBlocked(combo: List<ItemEntry?>): Boolean {
        val ids = combo.filterNotNull().map { it.itemId }.toSet()
        return _blockedCombos.any { it == ids }
    }
    init {
        viewModelScope.launch {
            loadBlockedCombinations()
        }
    }

    private suspend fun loadBlockedCombinations() {
        val combos = withContext(Dispatchers.IO) {
            db.blockedCombinationDao().getAllBlockedCombinations()
        }
        // Convert to Set<Int> for easy checking
        _blockedCombos.clear()
        combos.forEach { combo ->
            val ids = listOfNotNull(combo.headwearId, combo.topwearId, combo.bottomwearId, combo.shoesId).toSet()
            _blockedCombos.add(ids)
        }
    }
    private fun filterItems(headwear: List<ItemEntry>, topwear: List<ItemEntry>,
                            bottomwear: List<ItemEntry>, shoes: List<ItemEntry>): WardrobeState {
        // Only keep items that aren't part of blocked combos with current selection
        val newHead = headwear.firstOrNull { hw ->
            _blockedCombos.none { combo -> combo.contains(hw.itemId) }
        }
        val newTop = topwear.firstOrNull { tw ->
            _blockedCombos.none { combo -> combo.contains(tw.itemId) }
        }
        val newBottom = bottomwear.firstOrNull { bw ->
            _blockedCombos.none { combo -> combo.contains(bw.itemId) }
        }
        val newShoes = shoes.firstOrNull { s ->
            _blockedCombos.none { combo -> combo.contains(s.itemId) }
        }

        return WardrobeState(
            centeredHeadwear = newHead,
            centeredTopwear = newTop,
            centeredBottomwear = newBottom,
            centeredShoes = newShoes
        )
    }
    fun loadWardrobe(headwear: List<ItemEntry>, topwear: List<ItemEntry>,
                     bottomwear: List<ItemEntry>, shoes: List<ItemEntry>) {
        viewModelScope.launch {
            // Ensure blocked combos are loaded first
            if (_blockedCombos.isEmpty()) loadBlockedCombinations()

            // Filter first, then update state
            val filteredState = filterItems(headwear, topwear, bottomwear, shoes)
            _wardrobeState.value = filteredState
        }
    }

    /** Shuffle items randomly for each category, respecting exclusions */
    fun shuffleItems(
        headwearList: List<ItemEntry>,
        topwearList: List<ItemEntry>,
        bottomwearList: List<ItemEntry>,
        shoesList: List<ItemEntry>
    ) {
        viewModelScope.launch {
            val maxAttempts = 50 // prevent infinite loops
            var attempts = 0
            var shuffled: WardrobeState? = null

            while (attempts < maxAttempts && shuffled == null) {
                attempts++

                val head = if (headwearList.isNotEmpty()) headwearList.random() else null
                val top = if (topwearList.isNotEmpty()) topwearList.random() else null
                val bottom = if (bottomwearList.isNotEmpty()) bottomwearList.random() else null
                val shoes = if (shoesList.isNotEmpty()) shoesList.random() else null

                val combo = listOf(head, top, bottom, shoes)
                if (!isComboBlocked(combo)) {
                    shuffled = WardrobeState(
                        centeredHeadwear = head,
                        centeredTopwear = top,
                        centeredBottomwear = bottom,
                        centeredShoes = shoes
                    )
                }
            }

            // If a valid combo was found, update state
            shuffled?.let { _wardrobeState.value = it }
        }
    }

    /** Update the centered item for a specific category */
    fun updateCenteredItem(category: String, item: ItemEntry?) {
        val current = _wardrobeState.value

        // Build the hypothetical new outfit
        val newHead = if (category == "Headwear") item else current.centeredHeadwear
        val newTop = if (category == "Topwear") item else current.centeredTopwear
        val newBottom = if (category == "Bottomwear") item else current.centeredBottomwear
        val newShoes = if (category == "Shoes") item else current.centeredShoes

        // Check if new outfit is blocked BEFORE showing it
        val isBlocked = isComboBlocked(listOf(newHead, newTop, newBottom, newShoes))

        if (isBlocked) {
            return
        }

        // ✅ Safe → Update UI
        _wardrobeState.value = current.copy(
            centeredHeadwear = newHead,
            centeredTopwear = newTop,
            centeredBottomwear = newBottom,
            centeredShoes = newShoes
        )
    }

//    /** Optional: retrieve all excluded combinations for the current user */
//    suspend fun getRemovedCombinations(): List<RemovedCombination> {
//        return removedDao.getByUser(userViewModel.userState.value.id)
//    }


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

}