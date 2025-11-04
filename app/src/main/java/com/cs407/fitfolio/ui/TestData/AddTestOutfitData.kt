package com.cs407.fitfolio.ui.TestData

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.ui.viewModels.ClosetViewModel
import com.cs407.fitfolio.ui.viewModels.ItemEntry
import com.cs407.fitfolio.ui.viewModels.OutfitsViewModel

@Composable
fun AddTestOutfitData(outfitsViewModel: OutfitsViewModel) {
    val outfitsState by outfitsViewModel.outfitsState.collectAsStateWithLifecycle()

    val itemList: List<ItemEntry> = listOf(
        ItemEntry(
            itemName = "Red Shirt",
            itemType = "Shirts",
            itemDescription = "Red satin shirt",
            itemTags = listOf("Summer", "Red"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = ""
        ),
        ItemEntry(
            itemName = "Black Skirt",
            itemType = "Skirts",
            itemDescription = "Elegant black skirt",
            itemTags = listOf("Winter", "Black", "Casual"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = ""
        ),
        ItemEntry(
            itemName = "Brown Outerwear Jacket",
            itemType = "Outerwear",
            itemDescription = "Warm brown jacket for fall",
            itemTags = listOf("Fall", "Brown"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = ""
        ),ItemEntry(
            itemName = "Purple Sneakers",
            itemType = "Shoes",
            itemDescription = "Comfortable purple sneakers",
            itemTags = listOf("Spring", "Purple", "Casual"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = ""
        ),
    )

    // Outfit 1
    outfitsViewModel.addOutfit(
        name = "Example Outfit Summer and Red",
        description = "Summer and Red",
        tags = listOf("Summer", "Red"),
        isFavorite = true,
        itemList = itemList,
        photo = -1
    )

    // Outfit 2
    outfitsViewModel.addOutfit(
        name = "Example Outfit Fall and Blue",
        description = "Fall and Blue",
        tags = listOf("Fall", "Blue"),
        isFavorite = true,
        itemList = itemList,
        photo = -1
    )

    // Outfit 3
    outfitsViewModel.addOutfit(
        name = "Example Outfit Fall and Orange",
        description = "Fall and Orange",
        tags = listOf("Fall", "Orange"),
        isFavorite = false,
        itemList = itemList,
        photo = -1
    )

    // Outfit 4
    outfitsViewModel.addOutfit(
        name = "Example Outfit Spring, Green, and Casual",
        description = "Spring, Green, and Casual",
        tags = listOf("Spring", "Green", "Casual"),
        isFavorite = false,
        itemList = itemList,
        photo = -1
    )

    // Outfit 5
    outfitsViewModel.addOutfit(
        name = "Example Outfit Winter, Black, and Casual",
        description = "Winter, Black, and Casual",
        tags = listOf("Winter", "Black", "Casual"),
        isFavorite = true,
        itemList = emptyList(),
        photo = -1
    )

    // Outfit 6
    outfitsViewModel.addOutfit(
        name = "Example Outfit Fall and Brown",
        description = "Fall and Brown",
        tags = listOf("Fall", "Brown"),
        isFavorite = false,
        itemList = itemList,
        photo = -1
    )

    // Outfit 7
    outfitsViewModel.addOutfit(
        name = "Example Outfit Summer, Yellow, and Casual",
        description = "Summer, Yellow, and Casual",
        tags = listOf("Summer", "Yellow", "Casual"),
        isFavorite = true,
        itemList = itemList,
        photo = -1
    )

    // Outfit 8
    outfitsViewModel.addOutfit(
        name = "Example Outfit Spring, Purple, and Casual",
        description = "Spring, Purple, and Casual",
        tags = listOf("Spring", "Purple", "Casual"),
        isFavorite = false,
        itemList = itemList,
        photo = -1
    )
}