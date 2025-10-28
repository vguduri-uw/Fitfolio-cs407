package com.cs407.fitfolio.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import java.io.Serializable

// TODO: add functions for editing the item itself (from the item modal)
// Data class representing a single item of clothing
data class ItemEntry(
    val itemName: String,
    val itemType: String,
    val itemDescription: String,
    val itemTags: List<String>,
    val isFavorite: Boolean,
    val itemPhoto: Int, // TODO: figure out what type photo will be... if it is a drawable, it is Int
    val itemId : String
) : Serializable

// Data class representing the entire closet of clothing
data class ClosetState(
    val items: List<ItemEntry> = emptyList(),
    val filteredItems : List<ItemEntry> = emptyList(),
    val itemTypes: List<String> = listOf(
        "T-Shirts", "Shirts", "Jeans", "Pants", "Shorts", "Skirts", "Dresses", "Outerwear", "Shoes"
    ),
    val activeItemType: String = "All",
    val isFavoritesActive: Boolean = false,
    val isSearchActive: Boolean = false,
    val tags: List<String> = listOf("tag1", "tag2", "tag3", "tag4"), // TODO: establish some starter tags
    val activeTags: List<String> = emptyList(),
    val searchQuery: String = ""
)

class ClosetViewModel : ViewModel() {
    // Backing property (private) for state
    private val _closetState = MutableStateFlow(ClosetState())

    // Publicly exposed immutable StateFlow for the UI layer to observe changes safely
    val closetState = _closetState.asStateFlow()

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
            itemPhoto = photo,
            itemId = UUID.randomUUID().toString()
        )

        val updatedItems = _closetState.value.items + newItem
        val updatedFilteredItems = _closetState.value.filteredItems + newItem
        _closetState.value = _closetState.value.copy(
            items = updatedItems,
            filteredItems = updatedFilteredItems
        )
    }

    // Deletes a specified item from the closet
    fun delete(item: ItemEntry) {
        val updatedItems = _closetState.value.items - item
        val updatedFilteredItems = _closetState.value.filteredItems - item
        _closetState.value = _closetState.value.copy(
            items = updatedItems,
            filteredItems = updatedFilteredItems
        )
    }

    // TODO: implement this!!
    fun addToFavorites(item: ItemEntry) {

    }

    // TODO: implement this!!
    fun removeFromFavorites(item: ItemEntry) {

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
    fun toggleFavorites() {
        val isToggled = _closetState.value.isFavoritesActive
        _closetState.value = _closetState.value.copy(
            isFavoritesActive = !isToggled
        )
    }

    fun shuffleItems() {
        val shuffledItems = _closetState.value.filteredItems.shuffled()
        _closetState.value = _closetState.value.copy(
            filteredItems = shuffledItems
        )
    }

    // Updates whether the search filter is activated or not
    fun toggleSearch(isActive: Boolean) {
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

    // TODO: implement
    // TODO: decide if multiple tags means the item must share those, or if we show any item that has at least 1 of the tags
    fun applyFilters() {

    }

    // Clears any applied filters and resets properties
    fun clearFilters() {
        _closetState.value = _closetState.value.copy(
            filteredItems = _closetState.value.items,
            activeItemType = "All",
            isFavoritesActive = false,
            activeTags = emptyList(),
            searchQuery = ""
        )
    }
}
