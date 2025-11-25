package com.cs407.fitfolio.data.testData

import android.util.Log
import kotlinx.coroutines.delay
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.OutfitsViewModel

suspend fun AddTestItemData(
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel
) {
    Log.d("TestData", "Reached AddTestItemData")

    // Prevent duplicates
    if (closetViewModel.closetState.value.items.isNotEmpty()) return

    Log.d("TestData", "Inserting outfits...")

    outfitsViewModel.addOutfit("Red Shirt & Jeans", "Classic casual look",
        listOf("Casual", "Summer", "Red", "Tag", "Tag", "Tag"),
        true, ""
    )
    outfitsViewModel.addOutfit("Green Tee & Black Skirt", "Cute spring fit",
        listOf("Spring", "Casual"),
        false, ""
    )
    outfitsViewModel.addOutfit("Fall Jacket & Jeans", "Cozy autumn streetwear",
        listOf("Fall", "Casual"),
        false, ""
    )
    outfitsViewModel.addOutfit("Summer Shorts & Sneakers", "Bright casual summer look",
        listOf("Summer", "Casual"),
        true, ""
    )

    Log.d("TestData", "Waiting for real DB outfit IDs...")

    // Check the DB until real IDs exist
    var outfits = emptyList<com.cs407.fitfolio.data.OutfitEntry>()
    repeat(20) {      // ~2 seconds max wait
        outfits = outfitsViewModel.db.userDao().getOutfitsByUserId(outfitsViewModel.userId)
        if (outfits.size >= 4 && outfits.none { it.outfitId == 0 }) return@repeat
        delay(100)
    }

    if (outfits.size < 4 || outfits.any { it.outfitId == 0 }) {
        Log.e("TestData", "Outfits not ready — cancelling test insert")
        return
    }

    Log.d("TestData", "Outfits ready with IDs: ${outfits.map { it.outfitId }}")

    val redShirtJeans = outfits.first { it.outfitName == "Red Shirt & Jeans" }
    val greenTeeBlackSkirt = outfits.first { it.outfitName == "Green Tee & Black Skirt" }
    val fallJacketJeans = outfits.first { it.outfitName == "Fall Jacket & Jeans" }
    val summerShortsSneakers = outfits.first { it.outfitName == "Summer Shorts & Sneakers" }

    Log.d("TestData", "Inserting items")

    closetViewModel.addItemWithOutfitsTest("Red Shirt", "Shirts", "Red satin shirt",
        listOf("Summer", "Red"), true, "",
        listOf(redShirtJeans, fallJacketJeans, summerShortsSneakers)
    )

    closetViewModel.addItemWithOutfitsTest("Blue Jeans", "Jeans", "Ripped jeans",
        listOf("Fall", "Blue"), true, "",
        listOf(redShirtJeans, fallJacketJeans)
    )

    closetViewModel.addItemWithOutfitsTest("Orange Dress", "Dresses", "",
        listOf("Fall", "Orange"), false, "",
        emptyList()
    )

    closetViewModel.addItemWithOutfitsTest("Green T-Shirt", "T-Shirts",
        "Bright green casual t-shirt", listOf("Spring", "Green", "Casual"),
        false, "",
        listOf(greenTeeBlackSkirt, summerShortsSneakers)
    )

    closetViewModel.addItemWithOutfitsTest("Black Skirt", "Skirts",
        "Elegant black skirt", listOf("Winter", "Black", "Casual"),
        true, "",
        listOf(greenTeeBlackSkirt)
    )

    closetViewModel.addItemWithOutfitsTest(
        "Brown Outerwear Jacket Brown Outerwear Jacket Brown Outerwear Jacket Brown Outerwear Jacket",
        "Outerwear", "Warm brown jacket for fall", listOf("Fall", "Brown"),
        false, "", listOf(fallJacketJeans)
    )

    closetViewModel.addItemWithOutfitsTest("Yellow Shorts", "Shorts",
        "Bright yellow summer shorts", listOf("Summer", "Yellow", "Casual"),
        true, "",
        listOf(summerShortsSneakers)
    )

    closetViewModel.addItemWithOutfitsTest("Purple Sneakers", "Shoes",
        "Comfortable purple sneakers", listOf("Spring", "Purple", "Casual"),
        false, "",
        listOf(greenTeeBlackSkirt, redShirtJeans, fallJacketJeans, summerShortsSneakers)
    )

    Log.d("TestData", "Test outfits + items added")
}
