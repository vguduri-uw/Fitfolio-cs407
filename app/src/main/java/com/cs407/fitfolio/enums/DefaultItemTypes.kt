package com.cs407.fitfolio.enums

import com.cs407.fitfolio.R

enum class DefaultItemTypes(val typeName: String, val iconRes: Int) {
    // Default item type
    ALL("All", iconRes = R.drawable.hanger),

    // Shirts
    T_SHIRTS("T-Shirts", iconRes = R.drawable.shirt), // TODO: update
    SHIRTS("Shirts", iconRes = R.drawable.shirt),
    LONG_SLEEVE_SHIRTS("Long Sleeve Tops", iconRes = R.drawable.long_sleeve_shirt),
    SWEATSHIRTS("Sweatshirts", iconRes = R.drawable.hanger), // TODO: update

    // Bottoms
    JEANS("Jeans", iconRes = R.drawable.jeans),
    PANTS("Pants", iconRes = R.drawable.pants),
    SHORTS("Shorts", iconRes = R.drawable.shorts), // TODO: update
    SKIRTS("Skirts", iconRes = R.drawable.skirt),

    // Other
    DRESSES("Dresses", iconRes = R.drawable.dress),
    OUTERWEAR("Outerwear", iconRes = R.drawable.jacket),
    SHOES("Shoes", iconRes = R.drawable.shoe)
}
