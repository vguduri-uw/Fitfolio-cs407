package com.cs407.fitfolio.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.Serializable
import java.util.UUID

// data class representing a single saved outfit (a look made of multiple clothing items)
data class OutfitEntry(
    val outfitName: String,
    val outfitDescription: String,
    val outfitTags: List<String>,      // e.g. ["athletic", "winter", "interview"]
    val isFavorite: Boolean,
    val outfitPhoto: Int,              // todo: figure out what type...drawable? int?
    val itemIds: List<String>,         // references itemEntry.itemId values from the closet
    val outfitId: String
) : Serializable

// data class representing the entire collection of outfits
data class OutfitsState(
    val outfits: List<OutfitEntry> = emptyList(),
    val filteredOutfits: List<OutfitEntry> = emptyList(),

    // all possible tags/types a user can apply to outfits
    // (user can add more)
    val allTags: List<String> = listOf(
        "Athletic",
        "Business",
        "Business Casual",
        "Casual",
        "Formal",
        "Streetwear",
        "Loungewear"
    ),

    val activeTags: List<String> = emptyList(), // currently selected tags for filtering
    val toggleFavorites: Boolean = false,       // whether "favorites only" is on
    val searchQuery: String = ""               // current search input
)

class OutfitsViewModel : ViewModel() {

    // backing property (private) for state
    private val _outfitsState = MutableStateFlow(OutfitsState())

    // publicly exposed immutable stateflow for the ui layer to observe changes safely
    val outfitsState = _outfitsState.asStateFlow()

    // adds an outfit to the list of outfits
    fun addOutfit(
        name: String,
        description: String,
        tags: List<String>,
        isFavorite: Boolean,
        photo: Int,
        itemIds: List<String>
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
            filteredOutfits = updatedOutfits // todo: apply filters instead of just copying
        )
    }

    // deletes a specified outfit from the list
    fun delete(outfit: OutfitEntry) {
        val updatedOutfits = _outfitsState.value.outfits - outfit
        _outfitsState.value = _outfitsState.value.copy(
            outfits = updatedOutfits,
            filteredOutfits = updatedOutfits // todo: apply filters instead of just copying
        )
    }

    // todo: implement this - marks an outfit as a favorite
    fun addToFavorites(outfit: OutfitEntry) {

    }

    // todo: implement - removes an outfit from favorites
    fun removeFromFavorites(outfit: OutfitEntry) {

    }

    // todo: implement - filters outfits so only "favorite" outfits show
    fun filterByFavorites() {

    }

    // adds a new tag/type option the user can choose from
    fun addTag(newTag: String) {
        if (newTag !in _outfitsState.value.allTags) {
            val updatedTags = _outfitsState.value.allTags + newTag
            _outfitsState.value = _outfitsState.value.copy(
                allTags = updatedTags
            )
        }
    }

    // removes a tag/type from the list of selectable tags
    // todo: warn the user that deleting the tag will not remove it from already-saved outfits
    fun deleteTag(tag: String) {
        val updatedTags = _outfitsState.value.allTags - tag
        _outfitsState.value = _outfitsState.value.copy(
            allTags = updatedTags
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
    fun removeFromActiveTags (tag: String) {
        val updatedActiveTags = _outfitsState.value.activeTags - tag
        _outfitsState.value = _outfitsState.value.copy(
            activeTags = updatedActiveTags
        )
    }

    // todo: implement
    // filters outfits by tag(s). if a tag is active, only show outfits
    // that contain that tag in outfitTags.
    fun filterByTags(tag: String) {
        // toggles tag in activetags
        var currentTags = _outfitsState.value.activeTags

        currentTags = if (currentTags.contains(tag)) {
            currentTags - tag
        } else {
            currentTags + tag
        }

        // todo: after updating currenttags, update filteredoutfits so it only
        // includes outfits where any of the activetags appear in outfittags

        _outfitsState.value = _outfitsState.value.copy(
            activeTags = currentTags
            // todo: filteredoutfits = ...
        )
    }

    // todo: implement - randomizes the order of the currently filtered outfits
    fun shuffleOutfits() {

    }

    // todo: implement - searches outfits by name/description
    fun searchOutfits(searchValue: String) {

    }

    // clears any applied filters (favorites, tags, search)
    fun clearFilters() {
        _outfitsState.value = _outfitsState.value.copy(
            toggleFavorites = false,
            activeTags = emptyList(),
            searchQuery = "",
            filteredOutfits = _outfitsState.value.outfits
        )
    }
}