package com.cs407.fitfolio.ui.viewModels

import androidx.lifecycle.ViewModel
import com.cs407.fitfolio.Closet
import com.cs407.fitfolio.ItemEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ClosetViewModel : ViewModel() {
    // Backing property (private) for state
    private val _closet = MutableStateFlow(Closet())

    // Publicly exposed immutable StateFlow for the UI layer to observe changes safely
    val closet = _closet.asStateFlow()

    // Adds an item to the closet
    fun addItem(name: String, type: String, description: String, tags: List<String>) {
        val newItem = ItemEntry(
            id = UUID.randomUUID().toString(),
            itemName = name,
            itemType = type,
            itemDescription = description,
            tags = tags
        )

        val updatedItems = _closet.value.items + newItem
        _closet.value = _closet.value.copy(
            items = updatedItems
        )
    }

    // Deletes a specified item from the closet
    fun delete(item: ItemEntry) {
        val updatedItems = _closet.value.items - item
        _closet.value = _closet.value.copy(
            items = updatedItems
        )
    }

    // Toggles whether an item of clothing is favorited or not
    // TODO: implement this!!
    fun toggleFavorites(itemId: String) {}

    // Adds an item type to the itemTypes list
    fun addItemType(itemType: String) {
        if (itemType !in _closet.value.itemTypes) {
            val updatedItemTypes = _closet.value.itemTypes + itemType
            _closet.value = _closet.value.copy(
                itemTypes = updatedItemTypes
            )
        }
    }

    // Removes the specified item type from the itemTypes list
    // TODO: warn the user that deleting the type will delete all clothes of that type
    // TODO: do this in MyClosetScreen (not here)
    fun deleteItemType(itemType: String) {
        val updatedItemTypes = _closet.value.itemTypes - itemType
        _closet.value = _closet.value.copy(
            itemTypes = updatedItemTypes
        )
    }
}