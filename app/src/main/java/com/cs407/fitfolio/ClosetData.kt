package com.cs407.fitfolio

import java.io.Serializable

// Data class representing a single item of clothing
data class ItemEntry(
    val itemName: String,
    val itemType: String,
    val itemDescription: String,
    val tags: List<String>,
    val isFavorite: Boolean = false,
    val id : String
) : Serializable

data class Closet(
    val items: List<ItemEntry> = emptyList(),
    val itemTypes: List<String> = listOf(
        "T-Shirts", "Shirts", "Jeans", "Pants", "Shorts", "Skirts", "Dresses", "Outerwear", "Shoes"
    ),
    val tags: List<String> = emptyList()
)
