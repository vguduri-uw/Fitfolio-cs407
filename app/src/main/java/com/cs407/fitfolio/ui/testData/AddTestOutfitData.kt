package com.cs407.fitfolio.ui.testData

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

    val itemList1: List<ItemEntry> = listOf(
        ItemEntry(
            itemName = "Red Shirt",
            itemType = "Shirts",
            itemDescription = "Red satin shirt",
            itemTags = listOf("Summer", "Red"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "11"
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
            itemId = "12"
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
            itemId = "13"
        ),ItemEntry(
            itemName = "Purple Sneakers",
            itemType = "Shoes",
            itemDescription = "Comfortable purple sneakers",
            itemTags = listOf("Spring", "Purple", "Casual"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "14"
        ),
    )

    val itemList2: List<ItemEntry> = listOf(
        ItemEntry(
            itemName = "Red Shirt",
            itemType = "Shirts",
            itemDescription = "Red satin shirt",
            itemTags = listOf("Summer", "Red"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "21"
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
            itemId = "22"
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
            itemId = "23"
        ),ItemEntry(
            itemName = "Purple Sneakers",
            itemType = "Shoes",
            itemDescription = "Comfortable purple sneakers",
            itemTags = listOf("Spring", "Purple", "Casual"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "24"
        ),
    )

    val itemList3: List<ItemEntry> = listOf(
        ItemEntry(
            itemName = "Red Shirt",
            itemType = "Shirts",
            itemDescription = "Red satin shirt",
            itemTags = listOf("Summer", "Red"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "31"
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
            itemId = "32"
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
            itemId = "33"
        ),ItemEntry(
            itemName = "Purple Sneakers",
            itemType = "Shoes",
            itemDescription = "Comfortable purple sneakers",
            itemTags = listOf("Spring", "Purple", "Casual"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "34"
        ),
    )

    val itemList4: List<ItemEntry> = listOf(
        ItemEntry(
            itemName = "Red Shirt",
            itemType = "Shirts",
            itemDescription = "Red satin shirt",
            itemTags = listOf("Summer", "Red"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "41"
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
            itemId = "42"
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
            itemId = "43"
        ),ItemEntry(
            itemName = "Purple Sneakers",
            itemType = "Shoes",
            itemDescription = "Comfortable purple sneakers",
            itemTags = listOf("Spring", "Purple", "Casual"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "44"
        ),
    )

    val itemList5: List<ItemEntry> = listOf(
        ItemEntry(
            itemName = "Red Shirt",
            itemType = "Shirts",
            itemDescription = "Red satin shirt",
            itemTags = listOf("Summer", "Red"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "51"
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
            itemId = "52"
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
            itemId = "53"
        ),ItemEntry(
            itemName = "Purple Sneakers",
            itemType = "Shoes",
            itemDescription = "Comfortable purple sneakers",
            itemTags = listOf("Spring", "Purple", "Casual"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "54"
        ),
    )

    val itemList6: List<ItemEntry> = listOf(
        ItemEntry(
            itemName = "Red Shirt",
            itemType = "Shirts",
            itemDescription = "Red satin shirt",
            itemTags = listOf("Summer", "Red"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "61"
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
            itemId = "62"
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
            itemId = "63"
        ),ItemEntry(
            itemName = "Purple Sneakers",
            itemType = "Shoes",
            itemDescription = "Comfortable purple sneakers",
            itemTags = listOf("Spring", "Purple", "Casual"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "64"
        ),
    )

    val itemList7: List<ItemEntry> = listOf(
        ItemEntry(
            itemName = "Red Shirt",
            itemType = "Shirts",
            itemDescription = "Red satin shirt",
            itemTags = listOf("Summer", "Red"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "71"
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
            itemId = "72"
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
            itemId = "73"
        ),ItemEntry(
            itemName = "Purple Sneakers",
            itemType = "Shoes",
            itemDescription = "Comfortable purple sneakers",
            itemTags = listOf("Spring", "Purple", "Casual"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "74"
        ),
    )

    val itemList8: List<ItemEntry> = listOf(
        ItemEntry(
            itemName = "Red Shirt",
            itemType = "Shirts",
            itemDescription = "Red satin shirt",
            itemTags = listOf("Summer", "Red"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "81"
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
            itemId = "82"
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
            itemId = "83"
        ),ItemEntry(
            itemName = "Purple Sneakers",
            itemType = "Shoes",
            itemDescription = "Comfortable purple sneakers",
            itemTags = listOf("Spring", "Purple", "Casual"),
            isFavorite = true,
            isDeletionCandidate = false,
            itemPhoto = -1,
            outfitList = emptyList(),
            itemId = "84"
        ),
    )

    // Outfit 1
    outfitsViewModel.addOutfit(
        name = "Example Outfit Summer and Red (Athletic)",
        description = "Summer and Red",
        tags = listOf("Athletic"),
        isFavorite = true,
        itemList = itemList1,
        photo = -1
    )

    // Outfit 2
    outfitsViewModel.addOutfit(
        name = "Example Outfit Fall and Blue (Business)",
        description = "Fall and Blue",
        tags = listOf("Business"),
        isFavorite = true,
        itemList = itemList2,
        photo = -1
    )

    // Outfit 3
    outfitsViewModel.addOutfit(
        name = "Example Outfit Fall and Orange (Business Casual)",
        description = "Fall and Orange",
        tags = listOf("Business Casual"),
        isFavorite = false,
        itemList = itemList3,
        photo = -1
    )

    // Outfit 4
    outfitsViewModel.addOutfit(
        name = "Example Outfit Spring, Green, and Casual (Casual)",
        description = "Spring, Green, and Casual",
        tags = listOf("Casual"),
        isFavorite = false,
        itemList = itemList4,
        photo = -1
    )

    // Outfit 5
    outfitsViewModel.addOutfit(
        name = "Example Outfit Winter, Black, and Casual (Formal)",
        description = "Winter, Black, and Casual",
        tags = listOf("Formal"),
        isFavorite = true,
        itemList = itemList5,
        photo = -1
    )

    // Outfit 6
    outfitsViewModel.addOutfit(
        name = "Example Outfit Fall and Brown (Streetwear)",
        description = "Fall and Brown",
        tags = listOf("Streetwear"),
        isFavorite = false,
        itemList = itemList6,
        photo = -1
    )

    // Outfit 7
    outfitsViewModel.addOutfit(
        name = "Example Outfit Summer, Yellow, and Casual (Loungewear)",
        description = "Summer, Yellow, and Casual",
        tags = listOf("Loungewear"),
        isFavorite = true,
        itemList = itemList7,
        photo = -1
    )

    // Outfit 8
    outfitsViewModel.addOutfit(
        name = "Example Outfit Spring, Purple, and Casual",
        description = "Spring, Purple, and Casual",
        tags = listOf("Spring", "Purple", "Casual"),
        isFavorite = false,
        itemList = itemList8,
        photo = -1
    )
}