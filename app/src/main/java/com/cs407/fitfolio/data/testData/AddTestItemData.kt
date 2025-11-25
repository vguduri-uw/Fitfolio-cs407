package com.cs407.fitfolio.data.testData

import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.OutfitsViewModel

suspend fun AddTestItemData(
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel
) {
    // Insert outfits
    outfitsViewModel.addOutfit("Red Shirt & Jeans", "Classic casual look",
        tags = listOf("Casual", "Summer", "Red", "Tag", "Tag", "Tag"),
        isFavorite = true, photoUri = ""
    )
    outfitsViewModel.addOutfit("Green Tee & Black Skirt", "Cute spring fit",
        tags = listOf("Spring", "Casual"), isFavorite = false, photoUri = ""
    )
    outfitsViewModel.addOutfit("Fall Jacket & Jeans", "Cozy autumn streetwear",
        tags = listOf("Fall", "Casual"), isFavorite = false, photoUri = ""
    )
    outfitsViewModel.addOutfit("Summer Shorts & Sneakers", "Bright casual summer look",
        tags = listOf("Summer", "Casual"), isFavorite = true, photoUri = ""
    )

    // Wait for outfits to exist
    val outfits = outfitsViewModel.outfitsState.value.outfits
    if (outfits.size < 4) return

    val redShirtJeans = outfits.first { it.outfitName == "Red Shirt & Jeans" }
    val greenTeeBlackSkirt = outfits.first { it.outfitName == "Green Tee & Black Skirt" }
    val fallJacketJeans = outfits.first { it.outfitName == "Fall Jacket & Jeans" }
    val summerShortsSneakers = outfits.first { it.outfitName == "Summer Shorts & Sneakers" }

    // Prevent duplicates
    if (closetViewModel.closetState.value.items.isNotEmpty()) return

    // Insert items
    closetViewModel.addItemWithOutfitsTest("Red Shirt", "Shirts", "Red satin shirt",
        listOf("Summer", "Red"), true, "", listOf(redShirtJeans, fallJacketJeans, summerShortsSneakers))

    closetViewModel.addItemWithOutfitsTest("Blue Jeans", "Jeans", "Ripped jeans",
        listOf("Fall", "Blue"), true, "", listOf(redShirtJeans, fallJacketJeans))

    closetViewModel.addItemWithOutfitsTest("Orange Dress", "Dresses", "",
        listOf("Fall", "Orange"), false, "", emptyList())

    closetViewModel.addItemWithOutfitsTest("Green T-Shirt", "T-Shirts",
        "Bright green casual t-shirt", listOf("Spring", "Green", "Casual"),
        false, "", listOf(greenTeeBlackSkirt, summerShortsSneakers))

    closetViewModel.addItemWithOutfitsTest("Black Skirt", "Skirts",
        "Elegant black skirt", listOf("Winter", "Black", "Casual"),
        true, "", listOf(greenTeeBlackSkirt))

    closetViewModel.addItemWithOutfitsTest("Brown Outerwear Jacket Brown Outerwear Jacket Brown Outerwear Jacket Brown Outerwear Jacket",
        "Outerwear", "Warm brown jacket for fall",
        listOf("Fall", "Brown"), false, "", listOf(fallJacketJeans))

    closetViewModel.addItemWithOutfitsTest("Yellow Shorts", "Shorts",
        "Bright yellow summer shorts", listOf("Summer", "Yellow", "Casual"),
        true, "", listOf(summerShortsSneakers))

    closetViewModel.addItemWithOutfitsTest("Purple Sneakers", "Shoes",
        "Comfortable purple sneakers", listOf("Spring", "Purple", "Casual"),
        false, "", listOf(greenTeeBlackSkirt, redShirtJeans, fallJacketJeans, summerShortsSneakers))
}
