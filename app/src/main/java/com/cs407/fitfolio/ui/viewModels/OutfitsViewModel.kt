package com.cs407.fitfolio.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.Serializable
import java.util.UUID

// data class representing a single saved outfit (a look made of multiple clothing items)
data class OutfitEntry(
    var outfitName: String,
    var outfitDescription: String,
    var outfitTags: List<String>,      // e.g. ["athletic", "winter", "interview"]
    var isFavorite: Boolean,
    var outfitPhoto: Int,              // todo: figure out what type...drawable? int?
    val itemIds: List<String>,         // references itemEntry.itemId values from the closet
    val outfitId: String
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
    val deletionCandidates: List<OutfitEntry> = emptyList()     // the item that is potentially deleted
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
        itemIds: List<String>,
    ) {
        val newOutfit = OutfitEntry(
            outfitName = name,
            outfitDescription = description,
            outfitTags = tags,
            isFavorite = isFavorite,
            outfitPhoto = photo,
            itemIds = itemIds,
            outfitId = UUID.randomUUID().toString()
        )

        val updatedOutfits = _outfitsState.value.outfits + newOutfit
        _outfitsState.value = _outfitsState.value.copy(
            outfits = updatedOutfits,
        )
    }

    // deletes all specified items
    fun delete(outfits: List<OutfitEntry>) {
        for (outfit in outfits) {
            val updatedOutfits = _outfitsState.value.outfits - outfit
            _outfitsState.value = _outfitsState.value.copy(
                outfits = updatedOutfits,
            )
        }
    }

    // adds/removes outfit from favorites
    fun toggleFavoritesProperty(outfit: OutfitEntry) {
        outfit.isFavorite = !outfit.isFavorite
    }

    // adds a new tag/type option the user can choose from
    fun addTag(newTag: String) {
        if (newTag !in _outfitsState.value.tags) {
            val updatedTags = _outfitsState.value.tags + newTag
            _outfitsState.value = _outfitsState.value.copy(
                tags = updatedTags
            )
        }
    }

    // removes a tag/type from the list of selectable tags
    // todo: warn the user that deleting the tag will not remove it from already-saved outfits
    fun deleteTag(tag: String) {
        val updatedTags = _outfitsState.value.tags - tag
        _outfitsState.value = _outfitsState.value.copy(
            tags = updatedTags
        )
    }

    /* ==========================================================================================
                                            OUTFITS FUNCTIONS
       ========================================================================================== */

    // toggles the favorites state for all outfits
    fun toggleFavoritesState() {
        val isToggled = _outfitsState.value.isFavoritesActive
        _outfitsState.value = _outfitsState.value.copy(
            isFavoritesActive = !isToggled
        )
    }

    // adds a tag to active tags
    fun addToActiveTags (tag: String) {
        val updatedActiveTags = _outfitsState.value.activeTags + tag
        _outfitsState.value = _outfitsState.value.copy(
            activeTags = updatedActiveTags
        )
    }

    // removes a tag from active tags
    fun removeFromActiveTags(tag: String) {
        val updatedActiveTags = _outfitsState.value.activeTags - tag
        _outfitsState.value = _outfitsState.value.copy(
            activeTags = updatedActiveTags
        )
    }

    // todo: if allowing deleting multiple deletions at once change to iterate through LIST of deletion candidates
    // sets the deletion candidates
    fun setDeletionCandidates(outfit: OutfitEntry){
        val updatedDeletionCandidates = outfitsState.value.deletionCandidates + outfit
        _outfitsState.value = _outfitsState.value.copy(
            deletionCandidates = updatedDeletionCandidates
        )

    }

    // clears the deletion candidates
    fun clearDeletionCandidates() {
        _outfitsState.value = _outfitsState.value.copy(
            deletionCandidates = emptyList()
        )
    }

    // todo: implement - only show items that include ALL selected filters
    // todo: add loading indicator
    // apply all tag filters at once
    fun applyFilters() {

    }

    // todo: implement - randomizes the order of the currently filtered outfits
    // shuffles all outfits available
    fun shuffleOutfits() {

    }

    // todo: implement - searches outfits by name/description
    // filters out outfits based on search query (regex??)
    fun searchOutfits(searchValue: String) {

    }

    // todo: return a list of type string containing outfit Ids for each outfit containing the item
    fun getOutfitsForItem(itemId: String) {

    }

    // clears any applied filters (favorites, tags, search)
    fun clearFilters() {
        _outfitsState.value = _outfitsState.value.copy(
            isFavoritesActive = false,
            activeTags = emptyList(),
            searchQuery = "",
            filteredOutfits = _outfitsState.value.outfits
        )
    }
}