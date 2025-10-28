package com.cs407.fitfolio.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import java.io.Serializable

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
    val toggleFavorites: Boolean = false,
    val tags: List<String> = emptyList(),
    val activeTags: List<String> = emptyList(),
    val searchQuery: String = ""
)

class ClosetViewModel : ViewModel() {
    // Backing property (private) for state
    private val _closetState = MutableStateFlow(ClosetState())

    // Publicly exposed immutable StateFlow for the UI layer to observe changes safely
    val closetState = _closetState.asStateFlow()

    // Adds an item to the closet
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
            itemId = UUID.randomUUID().toString(),
            )

        val updatedItems = _closetState.value.items + newItem
        _closetState.value = _closetState.value.copy(
            items = updatedItems,
            filteredItems = updatedItems
        )
    }

    // Deletes a specified item from the closet
    fun delete(item: ItemEntry) {
        val updatedItems = _closetState.value.items - item
        _closetState.value = _closetState.value.copy(
            items = updatedItems,
            filteredItems = updatedItems
        )
    }

    // TODO: implement this!!
    fun addToFavorites(item: ItemEntry) {

    }

    // TODO: implement this!!
    fun removeFromFavorites(item: ItemEntry) {

    }

    // Adds an item type to the itemTypes list
    fun addItemType(itemType: String) {
        if (itemType !in _closetState.value.itemTypes) {
            val updatedItemTypes = _closetState.value.itemTypes + itemType
            _closetState.value = _closetState.value.copy(
                itemTypes = updatedItemTypes
            )
        }
    }

    // Removes the specified item type from the itemTypes list
    // TODO: warn the user that deleting the type will delete all clothes of that type
    // TODO: do this in MyClosetScreen (not here)
    fun deleteItemType(itemType: String) {
        val updatedItemTypes = _closetState.value.itemTypes - itemType
        _closetState.value = _closetState.value.copy(
            itemTypes = updatedItemTypes
        )
    }

    // TODO: implement
    fun filterByItemType(itemType: String) {

    }

    // TODO: implement
    // Filters closet to only show favorite items
    fun filterByFavorites() {
        return
    }

    // TODO: implement
    fun shuffleItems() {

    }

    // TODO: implement
    fun searchItems(searchValue: String) {

    }

    // TODO: fix logic
    // Filters out items that do not have the specified tag
    fun filterByTags(tag: String) {
        var currentTags = _closetState.value.activeTags

        currentTags = if (currentTags.contains(tag)) {
            currentTags - tag
        } else {
            currentTags + tag
        }

        val postFilterItems = _closetState.value.filteredItems.filter { item -> tag in item.itemTags }
        _closetState.value = _closetState.value.copy(
            filteredItems = postFilterItems
        )
    }

    // Clears any applied filters
    fun clearFilters() {
        _closetState.value = _closetState.value.copy(
            filteredItems = _closetState.value.items
        )
    }
}
