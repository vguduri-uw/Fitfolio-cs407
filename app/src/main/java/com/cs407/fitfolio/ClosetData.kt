package com.cs407.fitfolio

import androidx.compose.runtime.mutableStateListOf
import java.io.Serializable

// Data class representing a single item
data class ItemEntry(
    val itemName: String,
    val itemType: String,
    val itemDescription: String,
    val tags: List<String>,
    val isFavorite: Boolean = false,
    val id : String
) : Serializable

// Object to hold all items in the closet
object Closet{
    val closet = mutableStateListOf<ItemEntry>()

    // Adds an item to favorites only if it's not already present
    fun add(entry: ItemEntry) {
        if (closet.none { it.itemName == entry.itemName
                    && it.itemType == entry.itemType && it.itemDescription == entry.itemDescription }) {
            closet.add(entry)
        }
    }

    // Deletes the specified item from the closet
    fun delete(entry: ItemEntry) {
        closet.removeAll { it.itemName == entry.itemName
                && it.itemType == entry.itemType && it.itemDescription == entry.itemDescription }
    }

    // Editable list of item types
    val itemTypes = mutableStateListOf(
        "T-Shirts", "Shirts", "Jeans", "Pants", "Shorts", "Skirts", "Dresses", "Outerwear", "Shoes"
    )

    // Adds an item type to the itemTypes list
    fun addItemType(itemType: String) {
        if (itemType !in itemTypes) itemTypes.add(itemType)
    }

    // Removes the specified item type from the itemTypes list
    // TODO: warn the user that deleting the type will delete all clothes of that type
    fun deleteItemType(itemType: String) {
        if (itemType in itemTypes) itemTypes.remove(itemType)
        closet.removeAll { it.itemType == itemType }
    }
}
