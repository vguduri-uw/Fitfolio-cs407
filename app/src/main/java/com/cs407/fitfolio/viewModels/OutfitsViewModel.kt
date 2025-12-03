package com.cs407.fitfolio.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.data.ItemEntry
import com.cs407.fitfolio.data.ItemOutfitRelation
import com.cs407.fitfolio.data.ItemTag
import com.cs407.fitfolio.data.OutfitEntry
import com.cs407.fitfolio.data.OutfitTag
import com.cs407.fitfolio.enums.DefaultItemTags
import com.cs407.fitfolio.enums.DefaultOutfitTags
import com.cs407.fitfolio.enums.DeletionStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.collections.map
import com.cs407.fitfolio.data.ScheduledOutfit
import java.time.LocalDate
import java.time.ZoneId

// data class representing the entire collection of outfits
data class OutfitsState(
    val outfits: List<OutfitEntry> = emptyList(), // all outfits in the outfits screen
    val filteredOutfits: List<OutfitEntry> = emptyList(), // the outfits currently rendered on the screen

    // all possible tags/types a user can apply to outfits
    // (user can add more)
    // todo: should tags be an enum??
    val tags: List<String> = listOf(
        "Athletic",
        "Business",
        "Business Casual",
        "Casual",
        "Formal",
        "Streetwear",
        "Loungewear"
    ),

    val activeTags: List<String> = emptyList(),                 // currently selected tags for filtering
    val isFavoritesActive: Boolean = false,                     // whether "favorites only" toggle is on
    val isSearchActive: Boolean = false,                        // whether or not a search query is active
    val searchQuery: String = "",                               // current search input
    val deletionCandidates: List<OutfitEntry> = emptyList(),    // the item that is potentially deleted
    val isDeleteActive: String = DeletionStates.Inactive.name,  // the status of the deletion process
    val outfitToShow: Int = -1,                                 // outfit ID of the outfit to be shown
    val isFiltering: Boolean = false                            // whether the outfits screen is actively in a filtering call
)

class OutfitsViewModel(
    val db: FitfolioDatabase,
    val userId: Int // TODO: make these private again after testing
) : ViewModel() {
    // backing property (private) for state
    private val _outfitsState = MutableStateFlow(OutfitsState())

    // publicly exposed immutable stateflow for the ui layer to observe changes safely
    val outfitsState = _outfitsState.asStateFlow()

    // initialize outfits state items and filtered outfits with data from db
    init {
        viewModelScope.launch(Dispatchers.IO) {
            var tags = db.userDao().getOutfitsTagsByUserId(userId).map { it.outfitTag }

            if (tags.isEmpty()) {
                // insert defaults outfit tags into DB
                DefaultOutfitTags.entries.forEach { tag ->
                    db.outfitDao().upsertOutfitTag(OutfitTag(outfitTag = tag.tagName), userId)
                }

                tags = DefaultOutfitTags.entries.map { it.tagName }
            }

            val outfits = db.userDao().getOutfitsByUserId(userId)
            _outfitsState.value = _outfitsState.value.copy(
                outfits = outfits,
                filteredOutfits = outfits,
                tags = tags
            )
        }
    }

    // TODO: remove after testing
    suspend fun getOutfits(): List<OutfitEntry> {
        return db.userDao().getOutfitsByUserId(userId)
    }

   /* ==========================================================================================
                                        OUTFIT FUNCTIONS
   ========================================================================================== */

    // adds an outfit to the list of outfits
    suspend fun addOutfit(
        name: String,
        description: String,
        tags: List<String>,
        isFavorite: Boolean,
        photoUri: String,
        itemList: List<ItemEntry>
    ): Int {
        val newOutfit = OutfitEntry(
            outfitId = 0,
            outfitName = name,
            outfitDescription = description,
            outfitTags = tags,
            isFavorite = isFavorite,
            isDeletionCandidate = false,
            outfitPhotoUri = photoUri,
        )

        // insert outfit, get its generated ID
        val outfitId = db.outfitDao().upsertOutfit(newOutfit, userId)

        // insert relations with the real outfitId
        itemList.forEach { item ->
            db.outfitDao().insertRelation(
                ItemOutfitRelation(item.itemId, outfitId)
            )
        }

        // refresh state
        val outfits = db.userDao().getOutfitsByUserId(userId)
        _outfitsState.value = _outfitsState.value.copy(outfits = outfits, filteredOutfits = outfits)

        return outfitId
    }

    // deletes all specified outfits
    fun deleteOutfits(outfits: List<OutfitEntry>) {
        viewModelScope.launch {
            // create list of ids for outfits to be deleted
            val outfitIds = emptySet<Int>()
            for (outfit in outfits) {
                outfitIds + outfit.outfitId
            }

            // remove relation between outfit and items
            for (outfit in outfits) {
                for (item in getItemsList(outfit.outfitId)) {
                    db.deleteDao().deleteRelation(ItemOutfitRelation(item.itemId, outfit.outfitId))
                }
            }

            // remove outfits
            db.deleteDao().deleteOutfits(outfitIds.toList())

            // update global outfits
            val updatedOutfits = db.userDao().getOutfitsByUserId(userId)
            _outfitsState.value = _outfitsState.value.copy(outfits = updatedOutfits)
        }
    }

    // SETTERS FOR OUTFIT PROPERTIES (for use in wardrobe screen and outfit modal)
    // call this function to upsert the outfit's photo uri
    fun editOutfitPhoto(outfit: OutfitEntry, photoUri: String) {
        viewModelScope.launch {
            val updatedOutfit = outfit.copy(outfitPhotoUri = photoUri)
            db.outfitDao().upsertOutfit(updatedOutfit, userId)

            val outfits = db.userDao().getOutfitsByUserId(userId)
            _outfitsState.value = _outfitsState.value.copy(outfits = outfits)
        }
    }



    fun editOutfitName(outfit: OutfitEntry, name: String) {
        outfit.outfitName = name

        _outfitsState.value = _outfitsState.value.copy(
            outfits = _outfitsState.value.outfits
        )
    }

    fun editOutfitDescription(outfit: OutfitEntry, description: String) {
        outfit.outfitDescription = description

        _outfitsState.value = _outfitsState.value.copy(
            outfits = _outfitsState.value.outfits
        )
    }

    fun editOutfitTags(outfit: OutfitEntry, tag: String, isRemoving: Boolean) {
        if (isRemoving) {
            outfit.outfitTags -= tag
        } else {
            outfit.outfitTags += tag
        }

        _outfitsState.value = _outfitsState.value.copy(
            outfits = _outfitsState.value.outfits
        )
    }

    fun removeItemsFromItemsList(itemIds: List<Int>, outfitId: Int) {
        viewModelScope.launch {
            // remove relation between outfit and items
            for (itemId in itemIds) {
                db.deleteDao().deleteRelation(ItemOutfitRelation(itemId, outfitId))
            }

            // update global outfits
            val updatedOutfits = db.userDao().getOutfitsByUserId(userId)
            _outfitsState.value = _outfitsState.value.copy(outfits = updatedOutfits)
        }
    }

    fun toggleFavoritesProperty(outfit: OutfitEntry) {
        val updatedOutfits = _outfitsState.value.outfits.map {
            if (it.outfitId == outfit.outfitId) it.copy(isFavorite = !it.isFavorite) else it
        }

        _outfitsState.value = _outfitsState.value.copy(outfits = updatedOutfits)
        applyFilters()
    }

    fun toggleDeletionCandidate(outfit: OutfitEntry, isCandidate: Boolean){
        val updatedOutfits = _outfitsState.value.outfits.map {
            if (it.outfitId == outfit.outfitId) it.copy(isDeletionCandidate = isCandidate) else it
        }

        _outfitsState.value = _outfitsState.value.copy(outfits = updatedOutfits)
    }

    suspend fun getItemsList(outfitId: Int): List<ItemEntry> {
        return db.outfitDao().getItemsByOutfitId(outfitId)
    }

    // todo: remove after testing
    fun addOutfitWithItemsTest(
        name: String,
        description: String,
        tags: List<String>,
        isFavorites: Boolean,
        photoUri: String,
        itemList: List<ItemEntry>
    ) {
        val newOutfit = OutfitEntry(
            outfitId = 0,
            outfitName = name,
            outfitDescription = description,
            outfitTags = tags,
            isFavorite = isFavorites,
            isDeletionCandidate = false,
            outfitPhotoUri = photoUri,
        )

        viewModelScope.launch {
            // Insert outfit
            val outfitId = db.outfitDao().upsertOutfit(newOutfit, userId)

            // Insert relations between the outfit and each item in the item list
            itemList.forEach { item ->
                db.outfitDao().insertRelation(
                    ItemOutfitRelation(outfitId, item.itemId)
                )
            }

            val outfits = db.userDao().getOutfitsByUserId(userId)
            _outfitsState.value = _outfitsState.value.copy(outfits = outfits)
        }
    }


    /* ==========================================================================================
                                            OUTFITS FUNCTIONS
       ========================================================================================== */
    
    // apply all tag filters at once
    fun applyFilters() {
        viewModelScope.launch(Dispatchers.Default) {
            // Begin filtering
            _outfitsState.value = _outfitsState.value.copy(isFiltering = true)

            val updatedFilteredOutfits = _outfitsState.value.outfits.filter { outfit ->
                var passesAllFilters = true

                // Filter through favorites
                if (_outfitsState.value.isFavoritesActive) {
                    if (!outfit.isFavorite) passesAllFilters = false
                }


                // Filter with search query
                if (_outfitsState.value.isSearchActive) {
                    val query = _outfitsState.value.searchQuery.lowercase()
                    if (!(outfit.outfitName.lowercase().contains(query) ||
                                outfit.outfitDescription.lowercase().contains(query))
                    ) {
                        passesAllFilters = false
                    }
                }

                // Filter through active tags
                // TODO: decide if this should be inclusive (1 matching tag means its valid, how it currently is rn), or if it must match all tags
                if (_outfitsState.value.activeTags.isNotEmpty()) {
                    val hasMatchingTag = outfit.outfitTags.any { it in _outfitsState.value.activeTags }
                    if (!hasMatchingTag) {
                        passesAllFilters = false
                    }
                }

                passesAllFilters
            }

            // Update filteredOutfits
            _outfitsState.value = _outfitsState.value.copy(
                filteredOutfits = updatedFilteredOutfits
            )

            // Filtering is done
            _outfitsState.value = _outfitsState.value.copy(isFiltering = false)
        }
    }

    // adds a new tag/type option the user can choose from
    fun addTag(newTag: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // prevent empty tags
            if (newTag.trim().isEmpty()) return@launch

            // prevent duplicates in the DB
            val existingTags = db.userDao().getOutfitsTagsByUserId(userId).map { it.outfitTag }
            if (newTag.trim() !in existingTags) {
                // update database
                db.outfitDao().upsertOutfitTag(OutfitTag(outfitTag = newTag.trim()), userId)
            } else {
                return@launch
            }

            val updatedTags = db.userDao().getOutfitsTagsByUserId(userId).map { it.outfitTag }
            _outfitsState.value = _outfitsState.value.copy(
                tags = updatedTags
            )
        }
    }

    // removes a tag/type from the list of selectable tags
    // todo: warn the user that deleting the tag will remove it from already-saved outfits
    fun deleteTag(tag: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Remove the tag from the tag table
            db.deleteDao().deleteOutfitTag(tag)

            // Remove the tag from any outfits containing it
            val outfitsWithTag = _outfitsState.value.outfits.filter { tag in it.outfitTags }
            for (outfit in outfitsWithTag) {
                val updatedOutfit = outfit.copy(outfitTags = outfit.outfitTags - tag)
                db.outfitDao().upsert(updatedOutfit)
            }

            val updatedOutfits = db.userDao().getOutfitsByUserId(userId)
            val updatedTags = db.userDao().getOutfitsTagsByUserId(userId).map { it.outfitTag }
            _outfitsState.value = _outfitsState.value.copy(
                outfits = updatedOutfits,
                filteredOutfits = updatedOutfits,
                tags = updatedTags
            )
        }
    }

    fun outfitFlow(outfitId: Int): Flow<OutfitEntry?> =
        outfitsState.map { state -> state.outfits.find { it.outfitId == outfitId } }

    // toggles the favorites state for all outfits
    fun toggleFavoritesState() {
        val isToggled = _outfitsState.value.isFavoritesActive
        _outfitsState.value = _outfitsState.value.copy(
            isFavoritesActive = !isToggled
        )
        applyFilters()
    }

    // todo: implement - randomizes the order of the currently filtered outfits
    // shuffles all outfits available
    fun shuffleOutfits() {
        val shuffledOutfits = _outfitsState.value.filteredOutfits.shuffled()
        _outfitsState.value = _outfitsState.value.copy(
            filteredOutfits = shuffledOutfits
        )
    }

    // updates whether the search filter is activated or not
    fun toggleSearchState(isActive: Boolean) {
        _outfitsState.value = _outfitsState.value.copy(
            isSearchActive = isActive
        )
    }

    // updates the search query
    fun updateSearchQuery(query: String) {
        _outfitsState.value = _outfitsState.value.copy(
            searchQuery = query
        )
    }

    // update the outfit to show in outfit modal
    fun updateOutfitToShow(outfitId: Int) {
        _outfitsState.value = _outfitsState.value.copy(
            outfitToShow = outfitId
        )
    }

    // adds a tag to active tags
    fun addToActiveTags (tag: String) {
        val updatedActiveTags = _outfitsState.value.activeTags + tag
        _outfitsState.value = _outfitsState.value.copy(
            activeTags = updatedActiveTags
        )
        applyFilters()
    }

    // removes a tag from active tags
    fun removeFromActiveTags(tag: String) {
        val updatedActiveTags = _outfitsState.value.activeTags - tag
        _outfitsState.value = _outfitsState.value.copy(
            activeTags = updatedActiveTags
        )
        applyFilters()
    }

    // sets the deletion candidates
    fun setDeletionCandidates(outfit: OutfitEntry){
        // update outfit's isDeletionCandidate property
        toggleDeletionCandidate(outfit, true)

        val updatedDeletionCandidates = outfitsState.value.deletionCandidates + outfit
        _outfitsState.value = _outfitsState.value.copy(
            deletionCandidates = updatedDeletionCandidates
        )

    }

    // removes deletion candidates
    fun removeDeletionCandidates(outfit: OutfitEntry) {
        // update outfit's isDeletionCandidate property
        toggleDeletionCandidate(outfit, false)

        val updatedDeletionCandidates = outfitsState.value.deletionCandidates - outfit
        _outfitsState.value = _outfitsState.value.copy(
            deletionCandidates = updatedDeletionCandidates
        )
    }

    // clears the deletion candidates
    fun clearDeletionCandidates() {
        // update outfit isDeletionCandidate property
        for (outfit in _outfitsState.value.deletionCandidates) {
            toggleDeletionCandidate(outfit, false)
        }

        // update list
        _outfitsState.value = _outfitsState.value.copy(
            deletionCandidates = emptyList()
        )
    }

    // toggle the deletion state
    // see DeletionStates.kt enum class
    fun toggleDeleteState(status: String) {
        _outfitsState.value = _outfitsState.value.copy(
            isDeleteActive = status
        )
    }

//    // todo: return a list of type string containing outfit Ids for each outfit containing the item
//    fun getOutfitsForItem(itm: ItemEntry) {
//        val outfitsWithItem = emptyList<OutfitEntry>().toMutableList()
//
//        viewModelScope.launch {
//            for (outfit in _outfitsState.value.outfits) {
//                for (item in getItemsList(outfit.outfitId)) {
//                    if (item.itemId == itm.itemId) {
//                        outfitsWithItem += outfit
//                    }
//                }
//            }
//        }
//    }

    // clears any applied filters and resets properties
    fun clearFilters() {
        _outfitsState.value = _outfitsState.value.copy(
            isFavoritesActive = false,
            activeTags = emptyList(),
            searchQuery = "",
            filteredOutfits = _outfitsState.value.outfits
        )
    }

    // refreshes the outfits state after an item is deleted
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedOutfits = db.userDao().getOutfitsByUserId(userId)
            _outfitsState.value = _outfitsState.value.copy(outfits = updatedOutfits)
            applyFilters()
        }
    }

    //Veda: schedule an outfit for a specific date -> Veda here onwards
    fun scheduleOutfit(outfitId: Int, dateMillis: Long) {
        viewModelScope.launch {
            val localDate = LocalDate.ofEpochDay(dateMillis / (24 * 60 * 60 * 1000))
            val normalizedMillis = localDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            val scheduledOutfit = ScheduledOutfit(
                outfitId = outfitId,
                scheduledDate = normalizedMillis
            )
            db.outfitDao().scheduleOutfit(scheduledOutfit)
        }
    }

    //Veda: get the outfit scheduled for a specific date
    suspend fun getOutfitsForDate(dateMillis: Long): List<OutfitEntry> {
        val localDate = LocalDate.ofEpochDay(dateMillis / (24 * 60 * 60 * 1000))
        val normalizedMillis = localDate.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return db.outfitDao().getOutfitsForDate(normalizedMillis)
    }

    //Veda: remove a outfit from a  date
    suspend fun removeOutfitFromDate(dateMillis: Long, outfitId: Int) {
        val localDate = LocalDate.ofEpochDay(dateMillis / (24 * 60 * 60 * 1000))
        val normalizedMillis = localDate.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        db.deleteDao().removeOutfitFromDate(normalizedMillis, outfitId)
    }

    //Veda: get all dates that have scheduled outfits
    suspend fun getAllScheduledDates(): List<Long> {
        return db.outfitDao().getAllScheduledDates()
    }

    //Veda: get all dates where a specific outfit is scheduled
//    suspend fun getDatesForOutfit(outfitId: Int): List<Long> {
//        return db.outfitDao().getDatesForOutfit(outfitId)
//    }
}