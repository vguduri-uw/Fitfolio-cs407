package com.cs407.fitfolio.ui.viewModels

import androidx.lifecycle.ViewModel
import com.cs407.fitfolio.ui.enums.DeletionStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.Serializable
import java.util.UUID

// data class representing a single saved outfit (a look made of multiple clothing items)
data class OutfitEntry(
    var outfitName: String,            // the name of the outfit
    var outfitDescription: String,     // the description of the outfit
    var outfitTags: List<String>,      // e.g. ["athletic", "winter", "interview"]
    var isFavorite: Boolean,           // whether or not the item is in favorites
    var isDeletionCandidate: Boolean,  // whether or not the item is selected to be deleted
    var outfitPhoto: Int,              // todo: figure out what type...drawable? int?
    var itemList: List<ItemEntry>,     // all the items featured in the outfit
    val outfitId: String,              // the unique id of the outfit
) : Serializable

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
    val outfitToShow: String = ""                               // outfit ID of the outfit to be shown
)

class OutfitsViewModel : ViewModel() {

    // backing property (private) for state
    private val _outfitsState = MutableStateFlow(OutfitsState())

    // publicly exposed immutable stateflow for the ui layer to observe changes safely
    val outfitsState = _outfitsState.asStateFlow()


   /* ==========================================================================================
                                        OUTFIT FUNCTIONS
   ========================================================================================== */

    // adds an outfit to the list of outfits
    fun addOutfit(
        name: String,
        description: String,
        tags: List<String>,
        isFavorite: Boolean,
        photo: Int,
        itemList: List<ItemEntry>,
    ) {
        val newOutfit = OutfitEntry(
            outfitName = name,
            outfitDescription = description,
            outfitTags = tags,
            isFavorite = isFavorite,
            isDeletionCandidate = false,
            outfitPhoto = photo,
            itemList = itemList,
            outfitId = UUID.randomUUID().toString(),
        )

        val updatedOutfits = _outfitsState.value.outfits + newOutfit
        _outfitsState.value = _outfitsState.value.copy(
            outfits = updatedOutfits,
        )
    }

    // Retrieves an ItemEntry based on it's itemId
    // Throws an exception if item with that id is not found
    // TODO: make sure wherever we call this catches the exception and displays error accordingly
    fun getOutfit(outfitId: String): OutfitEntry {
        return _outfitsState.value.outfits.find { it.outfitId == outfitId }
            ?: throw NoSuchElementException("Outfit with id $outfitId not found")
    }

    // deletes all specified items
    // todo: implement Room database (then I don't think the outfitsViewModel needs to be passed in)
    fun delete(outfits: List<OutfitEntry>) {
        for (outfit in outfits) {
            val updatedOutfits = _outfitsState.value.outfits - outfit
            _outfitsState.value = _outfitsState.value.copy(
                outfits = updatedOutfits,
            )
        }
    }

    // SETTERS FOR ITEM PROPERTIES (for use in wardrobe screen and outfit modal)
    fun editOutfitName(outfit: OutfitEntry, name: String) {
        outfit.outfitName = name
    }

    fun editOutfitDescription(outfit: OutfitEntry, description: String) {
        outfit.outfitDescription = description
    }

    fun editOutfitTags(outfit: OutfitEntry, tag: String, isRemoving: Boolean) {
        if (isRemoving) {
            outfit.outfitTags -= tag
        } else {
            outfit.outfitTags += tag
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

    fun editItemList(outfit: OutfitEntry, item: ItemEntry, isRemoving: Boolean){
        if (isRemoving) {
            outfit.itemList -= item
        } else {
            outfit.itemList += item
        }
    }

    /* ==========================================================================================
                                            OUTFITS FUNCTIONS
       ========================================================================================== */
    
    // todo: add CircularProgressIndicator? when calling this (in closet screen), do in coroutine
    // apply all tag filters at once
    fun applyFilters() {
        var updatedFilteredOutfits = _outfitsState.value.outfits.filter { outfit ->
            var passesAllFilters = true

            // Filter through favorites
            if (_outfitsState.value.isFavoritesActive) {
                if (!outfit.isFavorite) passesAllFilters = false
            }

            // Filter with search query
            if (_outfitsState.value.isSearchActive) {
                val query = _outfitsState.value.searchQuery.lowercase()
                if (!(outfit.outfitName.lowercase().contains(query) ||
                            outfit.outfitDescription.lowercase().contains(query))) {
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

        // Update filteredItems
        _outfitsState.value = _outfitsState.value.copy(
            filteredOutfits = updatedFilteredOutfits
        )
    }

    // adds a new tag/type option the user can choose from
    fun addTag(newTag: String) {
        if (newTag !in _outfitsState.value.tags) {
            val updatedTags = _outfitsState.value.tags + newTag
            _outfitsState.value = _outfitsState.value.copy(
                tags = updatedTags
            )
        } // todo: if tag already exists, notify user
    }

    // removes a tag/type from the list of selectable tags
    // todo: warn the user that deleting the tag will remove it from already-saved outfits
    fun deleteTag(tag: String) {
        val updatedTags = _outfitsState.value.tags - tag
        _outfitsState.value = _outfitsState.value.copy(
            tags = updatedTags
        )

        val outfitsWithTag = _outfitsState.value.outfits.filter { tag in it.outfitTags }

        for (outfitWithTag in outfitsWithTag){
            editOutfitTags(outfitWithTag, tag, true)
        }
    }

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
    fun updateOutfitToShow(outfitId: String) {
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
        // update item isDeletionCandidate property
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

    // todo: return a list of type string containing outfit Ids for each outfit containing the item
    fun getOutfitsForItem(itm: ItemEntry) {
        val outfitsWithItem = emptyList<OutfitEntry>().toMutableList()

        for (outfit in _outfitsState.value.outfits) {
            for (item in outfit.itemList) {
                if (item.itemId == itm.itemId) {
                    outfitsWithItem += outfit
                }
            }
        }
    }

    // clears any applied filters and resets properties
    fun clearFilters() {
        _outfitsState.value = _outfitsState.value.copy(
            isDeleteActive = DeletionStates.Inactive.name,
            isFavoritesActive = false,
            activeTags = emptyList(),
            searchQuery = "",
            filteredOutfits = _outfitsState.value.outfits
        )
    }
}