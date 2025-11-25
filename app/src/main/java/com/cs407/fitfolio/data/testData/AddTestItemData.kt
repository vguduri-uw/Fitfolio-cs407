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
        photoUri = ""
    )

    // Outfit 2
    outfitsViewModel.addOutfit(
        name = "Green Tee & Black Skirt",
        description = "Cute spring fit",
        tags = listOf("Spring", "Casual"),
        isFavorite = false,
        photoUri = ""
    )

    // Outfit 3
    outfitsViewModel.addOutfit(
        name = "Fall Jacket & Jeans",
        description = "Cozy autumn streetwear",
        tags = listOf("Fall", "Casual"),
        isFavorite = false,
        photoUri = ""
    )

    // Outfit 4
    outfitsViewModel.addOutfit(
        name = "Summer Shorts & Sneakers",
        description = "Bright casual summer look",
        tags = listOf("Summer", "Casual"),
        isFavorite = true,
        photoUri = ""
    )

    val outfitsState = outfitsViewModel.outfitsState.collectAsStateWithLifecycle()
    val outfits = outfitsState.value.outfits

    val redShirtJeans = outfits.find { it.outfitName == "Red Shirt & Jeans" }!!
    val greenTeeBlackSkirt = outfits.find { it.outfitName == "Green Tee & Black Skirt" }!!
    val fallJacketJeans = outfits.find { it.outfitName == "Fall Jacket & Jeans" }!!
    val summerShortsSneakers = outfits.find { it.outfitName == "Summer Shorts & Sneakers" }!!

    // Item 1
    closetViewModel.addItemWithOutfitsTest(
        name = "Red Shirt",
        type = "Shirts",
        description = "Red satin shirt",
        tags = listOf("Summer", "Red"),
        isFavorites = true,
        photoUri = "",
        outfitList = listOf(redShirtJeans, fallJacketJeans, summerShortsSneakers)
    )

    // Item 2
    closetViewModel.addItemWithOutfitsTest(
        name = "Blue Jeans",
        type = "Jeans",
        description = "Ripped jeans",
        tags = listOf("Fall", "Blue"),
        isFavorites = true,
        photoUri = "",
        outfitList = listOf(redShirtJeans, fallJacketJeans)
    )

    // Item 3
    closetViewModel.addItemWithOutfitsTest(
        name = "Orange Dress",
        type = "Dresses",
        description = "",
        tags = listOf("Fall", "Orange"),
        isFavorites = false,
        photoUri = "",
        outfitList = emptyList()
    )

    // Item 4
    closetViewModel.addItemWithOutfitsTest(
        name = "Green T-Shirt",
        type = "T-Shirts",
        description = "Bright green casual t-shirt",
        tags = listOf("Spring", "Green", "Casual"),
        isFavorites = false,
        photoUri = "",
        outfitList = listOf(greenTeeBlackSkirt, summerShortsSneakers)
    )

    // Item 5
    closetViewModel.addItemWithOutfitsTest(
        name = "Black Skirt",
        type = "Skirts",
        description = "Elegant black skirt",
        tags = listOf("Winter", "Black", "Casual"),
        isFavorites = true,
        photoUri = "",
        outfitList = listOf(greenTeeBlackSkirt)
    )

    // Item 6
    closetViewModel.addItemWithOutfitsTest(
        name = "Brown Outerwear Jacket Brown Outerwear Jacket Brown Outerwear Jacket Brown Outerwear Jacket",
        type = "Outerwear",
        description = "Warm brown jacket for fall",
        tags = listOf("Fall", "Brown"),
        isFavorites = false,
        photoUri = "",
        outfitList = listOf(fallJacketJeans)
    )

    // Item 7
    closetViewModel.addItemWithOutfitsTest(
        name = "Yellow Shorts",
        type = "Shorts",
        description = "Bright yellow summer shorts",
        tags = listOf("Summer", "Yellow", "Casual"),
        isFavorites = true,
        photoUri = "",
        outfitList = listOf(summerShortsSneakers)
    )

    // Item 8
    closetViewModel.addItemWithOutfitsTest(
        name = "Purple Sneakers",
        type = "Shoes",
        description = "Comfortable purple sneakers",
        tags = listOf("Spring", "Purple", "Casual"),
        isFavorites = false,
        photoUri = "",
        outfitList = listOf(greenTeeBlackSkirt, redShirtJeans, fallJacketJeans, summerShortsSneakers)
    )
}