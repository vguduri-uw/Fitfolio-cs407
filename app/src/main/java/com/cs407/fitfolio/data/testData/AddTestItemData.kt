package com.cs407.fitfolio.data.testData

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.OutfitsViewModel

@Composable
fun AddTestItemData(
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel
) {
    // Outfit 1
    outfitsViewModel.addOutfit(
        name = "Red Shirt & Jeans",
        description = "Classic casual look",
        tags = listOf("Casual", "Summer, Red, Tag, Tag, Tag"),
        isFavorite = true,
        photo = -1,
        itemList = emptyList()
    )

    // Outfit 2
    outfitsViewModel.addOutfit(
        name = "Green Tee & Black Skirt",
        description = "Cute spring fit",
        tags = listOf("Spring", "Casual"),
        isFavorite = false,
        photo = -1,
        itemList = emptyList()
    )

    // Outfit 3
    outfitsViewModel.addOutfit(
        name = "Fall Jacket & Jeans",
        description = "Cozy autumn streetwear",
        tags = listOf("Fall", "Casual"),
        isFavorite = false,
        photo = -1,
        itemList = emptyList()
    )

    // Outfit 4
    outfitsViewModel.addOutfit(
        name = "Summer Shorts & Sneakers",
        description = "Bright casual summer look",
        tags = listOf("Summer", "Casual"),
        isFavorite = true,
        photo = -1,
        itemList = emptyList()
    )

    val outfitsState = outfitsViewModel.outfitsState.collectAsStateWithLifecycle()
    val outfits = outfitsState.value.outfits

    val redShirtJeans = outfits.find { it.outfitName == "Red Shirt & Jeans" }!!
    val greenTeeBlackSkirt = outfits.find { it.outfitName == "Green Tee & Black Skirt" }!!
    val fallJacketJeans = outfits.find { it.outfitName == "Fall Jacket & Jeans" }!!
    val summerShortsSneakers = outfits.find { it.outfitName == "Summer Shorts & Sneakers" }!!

    // Item 1
    closetViewModel.addItem(
        name = "Red Shirt",
        type = "Shirts",
        description = "Red satin shirt",
        tags = listOf("Summer", "Red"),
        isFavorites = true,
        photo = -1,
        outfitList = listOf(redShirtJeans, fallJacketJeans, summerShortsSneakers)
    )

    // Item 2
    closetViewModel.addItem(
        name = "Blue Jeans",
        type = "Jeans",
        description = "Ripped jeans",
        tags = listOf("Fall", "Blue"),
        isFavorites = true,
        photo = -1,
        outfitList = listOf(redShirtJeans, fallJacketJeans)
    )

    // Item 3
    closetViewModel.addItem(
        name = "Orange Dress",
        type = "Dresses",
        description = "",
        tags = listOf("Fall", "Orange"),
        isFavorites = false,
        photo = -1,
        outfitList = emptyList()
    )

    // Item 4
    closetViewModel.addItem(
        name = "Green T-Shirt",
        type = "T-Shirts",
        description = "Bright green casual t-shirt",
        tags = listOf("Spring", "Green", "Casual"),
        isFavorites = false,
        photo = -1,
        outfitList = listOf(greenTeeBlackSkirt, summerShortsSneakers)
    )

    // Item 5
    closetViewModel.addItem(
        name = "Black Skirt",
        type = "Skirts",
        description = "Elegant black skirt",
        tags = listOf("Winter", "Black", "Casual"),
        isFavorites = true,
        photo = -1,
        outfitList = listOf(greenTeeBlackSkirt)
    )

    // Item 6
    closetViewModel.addItem(
        name = "Brown Outerwear Jacket Brown Outerwear Jacket Brown Outerwear Jacket Brown Outerwear Jacket",
        type = "Outerwear",
        description = "Warm brown jacket for fall",
        tags = listOf("Fall", "Brown"),
        isFavorites = false,
        photo = -1,
        outfitList = listOf(fallJacketJeans)
    )

    // Item 7
    closetViewModel.addItem(
        name = "Yellow Shorts",
        type = "Shorts",
        description = "Bright yellow summer shorts",
        tags = listOf("Summer", "Yellow", "Casual"),
        isFavorites = true,
        photo = -1,
        outfitList = listOf(summerShortsSneakers)
    )

    // Item 8
    closetViewModel.addItem(
        name = "Purple Sneakers",
        type = "Shoes",
        description = "Comfortable purple sneakers",
        tags = listOf("Spring", "Purple", "Casual"),
        isFavorites = false,
        photo = -1,
        outfitList = listOf(greenTeeBlackSkirt, redShirtJeans, fallJacketJeans, summerShortsSneakers)
    )
}