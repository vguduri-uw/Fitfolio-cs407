package com.cs407.fitfolio.ui.data

import java.io.Serializable

// Data class representing a single item of clothing
data class ItemEntry(
    val itemName: String,
    val itemType: String,
    val itemDescription: String,
    val tags: List<String>,
    val isFavorite: Boolean = false,
    val photo: Int, // TODO: figure out what type photo will be... if it is a drawable, it is Int
    val id : String
) : Serializable

// Data class representing the entire closet of clothing
data class ClosetState(
    val items: List<ItemEntry> = emptyList(),
    val filteredItems : List<ItemEntry> = emptyList(),
    val itemTypes: List<String> = listOf(
        "T-Shirts", "Shirts", "Jeans", "Pants", "Shorts", "Skirts", "Dresses", "Outerwear", "Shoes"
    ),
    val tags: List<String> = emptyList()
)
