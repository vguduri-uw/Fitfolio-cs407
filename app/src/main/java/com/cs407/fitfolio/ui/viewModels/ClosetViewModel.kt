package com.cs407.fitfolio.ui.viewModels

import androidx.lifecycle.ViewModel
import com.cs407.fitfolio.ui.enums.DeletionStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import java.io.Serializable
import kotlin.collections.minus
import kotlin.collections.plus

// Data class representing a single item of clothing
data class ItemEntry(
    var itemName: String, // the name of the item
    var itemType: String, // the type of the item
    var itemDescription: String, // the description of the item
    var itemTags: List<String>, // the tags corresponding to the item
    var isFavorite: Boolean, // whether or not the item is favorited
    var isDeletionCandidate: Boolean, // whether or not the item is selected to be deleted
    var itemPhoto: Int, // TODO: figure out what type photo will be... if it is a drawable, it is Int
    var outfitList: List<OutfitEntry>, // the outfits that the item is featured in
    val itemId : String, // the unique id of the item
) : Serializable

// Data class representing the entire closet of clothing
data class ClosetState(
    val items: List<ItemEntry> = emptyList(), // all items in the closet
    val filteredItems : List<ItemEntry> = emptyList(), // the items currently rendered on the screen
    // TODO: should itemTypes be an enum?? also, confirm default item types
    val itemTypes: List<String> = listOf(
        "T-Shirts", "Shirts", "Jeans", "Pants", "Shorts", "Skirts", "Dresses", "Outerwear", "Shoes"
    ), // item types
    val activeItemType: String = "All", // item type that is currently rendered on the screen
    val isFavoritesActive: Boolean = false, // whether or not the favorites filter is active
    val isSearchActive: Boolean = false, // whether or not a search query filter is active
    val searchQuery: String = "",
    // TODO: establish some starter tags
    val tags: List<String> = listOf("tag1", "tag2", "tag3", "tag4"), // all tags for closet items
    val activeTags: List<String> = emptyList(), // the tags currently rendered on the screen
    val deletionCandidates: List<ItemEntry> = emptyList(), // the items that can be potentially deleted
    val isDeleteActive: String = DeletionStates.Inactive.name // the status of the deletion process
)

// ViewModel representing the state of the closet
class ClosetViewModel : ViewModel() {
    // Backing property (private) for state
    private val _closetState = MutableStateFlow(ClosetState())

    // Publicly exposed immutable StateFlow for the UI layer to observe changes safely
    val closetState = _closetState.asStateFlow()

    // ITEM FUNCTIONS //

    // Adds an item to the closet (from add screen)
    fun addItem(
        name: String, type: String, description: String, tags: List<String>,
        isFavorites: Boolean, photo: Int
    ) {
        val newItem = ItemEntry(
            itemName = name,
            itemType = type,
            itemDescription = description,
            itemTags = tags,
            isFavorite = isFavorites,
            isDeletionCandidate = false,
            itemPhoto = photo,
            itemId = UUID.randomUUID().toString(),
            outfitList = emptyList()
        )

        val updatedItems = _closetState.value.items + newItem
        _closetState.value = _closetState.value.copy(
            items = updatedItems
        )
    }

    // Deletes specified items from the closet
    fun delete(items: List<ItemEntry>) {
        for (item in items) {
            val updatedItems = _closetState.value.items - item
            _closetState.value = _closetState.value.copy(
                items = updatedItems
            )
        }
    }

    // Setters for item properties to be used in the add screen and item modal
    fun editItemName(item: ItemEntry, name: String) {
        item.itemName = name
    }
    fun editItemType(item: ItemEntry, type: String) {
        item.itemType = type
    }
    fun editItemDescription(item: ItemEntry, description: String) {
        item.itemDescription = description
    }
    fun editItemTags(item: ItemEntry, tag: String, isRemoving: Boolean) {
        if (isRemoving) {
            item.itemTags -= tag
        } else {
            item.itemTags += tag
        }
    }
    fun toggleFavoritesProperty(item: ItemEntry) {
        item.isFavorite = !item.isFavorite
    }
    fun toggleDeletionCandidate(item: ItemEntry, isCandidate: Boolean) {
        item.isDeletionCandidate = isCandidate
    }
    fun editItemPhoto(item: ItemEntry, photo: Int) {
        item.itemPhoto = photo
    }
    fun editOutfitList(item: ItemEntry, outfit: OutfitEntry, isRemoving: Boolean) {
        if (isRemoving) {
            item.outfitList -= outfit
        } else {
            item.outfitList += outfit
        }
    }

    // CLOSET FUNCTIONS //

    // TODO: implement
    // TODO: add CircularProgressIndicator? when calling this (in closet screen), do in coroutine
    // TODO: decide if multiple tags means the item must share those, or if we show any item that has at least 1 of the tags
    // TODO: should this be called in closet screen or in the functions here?
    fun applyFilters() {

    }

    // Adds an item type to the itemTypes list (from the item modal)
    fun addItemType(itemType: String) {
        if (itemType !in _closetState.value.itemTypes) {
            val updatedItemTypes = _closetState.value.itemTypes + itemType
            _closetState.value = _closetState.value.copy(
                itemTypes = updatedItemTypes
            )
        }
    }

    // Removes the specified item type from the itemTypes list (from the item modal)
    // TODO: warn the user that deleting the type will delete all clothes of that type (in the modal)
    fun deleteItemType(itemType: String) {
        val updatedItemTypes = _closetState.value.itemTypes - itemType
        _closetState.value = _closetState.value.copy(
            itemTypes = updatedItemTypes
        )
    }

    // Updates the active item type
    fun updateItemType(itemType: String) {
        _closetState.value = _closetState.value.copy(
            activeItemType = itemType
        )
    }

    // Updates whether the favorites filter is activated or not
    fun toggleFavoritesState() {
        val isToggled = _closetState.value.isFavoritesActive
        _closetState.value = _closetState.value.copy(
            isFavoritesActive = !isToggled
        )
    }

    // Shuffles the order of the filtered items list
    fun shuffleItems() {
        val shuffledItems = _closetState.value.filteredItems.shuffled()
        _closetState.value = _closetState.value.copy(
            filteredItems = shuffledItems
        )
    }

    // Updates whether the search filter is activated or not
    fun toggleSearchState(isActive: Boolean) {
        _closetState.value = _closetState.value.copy(
            isSearchActive = isActive
        )
    }

    // Updates the search query
    fun updateSearchQuery(query: String) {
        _closetState.value = _closetState.value.copy(
            searchQuery = query
        )
    }

    // Adds a tag to the active tags list
    fun addToActiveTags(tag: String) {
        val updatedTags = _closetState.value.activeTags + tag
        _closetState.value = _closetState.value.copy(
            activeTags = updatedTags
        )
    }

    // Removes a tag from the active tags list
    fun removeFromActiveTags(tag: String) {
        val updatedTags = _closetState.value.activeTags - tag
        _closetState.value = _closetState.value.copy(
            activeTags = updatedTags
        )
    }

    // Sets the deletion candidates list
    fun setDeletionCandidates(item: ItemEntry) {
        // Update item isDeletionCandidate property
        toggleDeletionCandidate(item, true)

        // Update list
        val updatedDeletionCandidates = _closetState.value.deletionCandidates + item
        _closetState.value = _closetState.value.copy(
            deletionCandidates = updatedDeletionCandidates
        )
    }

    // Clears the deletion candidates list
    fun clearDeletionCandidates() {
        // Update item isDeletionCandidate property
        for (item in _closetState.value.deletionCandidates) {
            toggleDeletionCandidate(item, false)
        }

        // Update list
        _closetState.value = _closetState.value.copy(
            deletionCandidates = emptyList()
        )
    }

    fun toggleDeleteState(status: String) {
        _closetState.value = _closetState.value.copy(
            isDeleteActive = status
        )
    }

    // Clears any applied filters and resets properties
    fun clearFilters() {
        _closetState.value = _closetState.value.copy(
            filteredItems = _closetState.value.items,
            activeItemType = "All",
            isFavoritesActive = false,
            activeTags = emptyList(),
            searchQuery = "",
            isDeleteActive = DeletionStates.Inactive.name
        )
    }
}
