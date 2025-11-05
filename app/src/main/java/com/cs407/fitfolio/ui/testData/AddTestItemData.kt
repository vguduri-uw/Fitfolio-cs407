package com.cs407.fitfolio.ui.testData

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.ui.viewModels.ClosetViewModel

@Composable
fun AddTestItemData(closetViewModel: ClosetViewModel) {
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()

    // Item 1
    closetViewModel.addItem(
        name = "Red Shirt",
        type = "Shirts",
        description = "Red satin shirt",
        tags = listOf("Summer", "Red"),
        isFavorites = true,
        photo = -1
    )

    // Item 2
    closetViewModel.addItem(
        name = "Blue Jeans",
        type = "Jeans",
        description = "Ripped jeans",
        tags = listOf("Fall", "Blue"),
        isFavorites = true,
        photo = -1
    )

    // Item 3
    closetViewModel.addItem(
        name = "Orange Dress",
        type = "Dresses",
        description = "Ripped jeans",
        tags = listOf("Fall", "Orange"),
        isFavorites = false,
        photo = -1
    )

    // Item 4
    closetViewModel.addItem(
        name = "Green T-Shirt",
        type = "T-Shirts",
        description = "Bright green casual t-shirt",
        tags = listOf("Spring", "Green", "Casual"),
        isFavorites = false,
        photo = -1
    )

    // Item 5
    closetViewModel.addItem(
        name = "Black Skirt",
        type = "Skirts",
        description = "Elegant black skirt",
        tags = listOf("Winter", "Black", "Casual"),
        isFavorites = true,
        photo = -1
    )

    // Item 6
    closetViewModel.addItem(
        name = "Brown Outerwear Jacket Brown Outerwear Jacket Brown Outerwear Jacket Brown Outerwear Jacket",
        type = "Outerwear",
        description = "Warm brown jacket for fall",
        tags = listOf("Fall", "Brown"),
        isFavorites = false,
        photo = -1
    )

    // Item 7
    closetViewModel.addItem(
        name = "Yellow Shorts",
        type = "Shorts",
        description = "Bright yellow summer shorts",
        tags = listOf("Summer", "Yellow", "Casual"),
        isFavorites = true,
        photo = -1
    )

    // Item 8
    closetViewModel.addItem(
        name = "Purple Sneakers",
        type = "Shoes",
        description = "Comfortable purple sneakers",
        tags = listOf("Spring", "Purple", "Casual"),
        isFavorites = false,
        photo = -1
    )
}