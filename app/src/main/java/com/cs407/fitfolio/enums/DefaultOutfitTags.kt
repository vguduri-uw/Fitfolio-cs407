package com.cs407.fitfolio.enums

// Enum for outfit tags that come with the app to be populated into the DB
enum class DefaultOutfitTags(val tagName: String) {
    // Seasons
    SPRING("Spring"),
    SUMMER("Summer"),
    FALL("Fall"),
    WINTER("Winter"),

    // Weather / Temperature
    COLD_WEATHER("Cold Weather"),
    MILD_WEATHER("Mild Weather"),
    HOT_WEATHER("Hot Weather"),
    RAIN_FRIENDLY("Rain Friendly"),
    WINDY("Windy"),

    // Style / Occasion
    CASUAL("Casual"),
    SMART_CASUAL("Smart Casual"),
    BUSINESS("Business"),
    FORMAL("Formal"),
    ATHLEISURE("Athleisure"),
    LOUNGE("Loungewear"),
    PARTY("Party"),
    BEACH("Beach"),
    TRAVEL("Travel"),
    DATE_NIGHT("Date Night"),
    STREETSTYLE("Street Style"),

    // Color Palette
    NEUTRAL_TONES("Neutral Tones"),         // white, beige, grey, black, taupe
    EARTH_TONES("Earth Tones"),             // browns, greens, rusts
    WARM_TONES("Warm Tones"),               // red, orange, yellow
    COOL_TONES("Cool Tones"),               // blue, green, purple
    PASTELS("Pastels"),                     // light pink, mint, lavender
    MONOCHROME("Monochrome"),               // all one color family
    COLORFUL("Colorful"),                   // multiple bright colors
    BLACK_AND_WHITE("Black & White"),

    // Outfit Silhouette / Fit
    RELAXED("Relaxed"),
    TAILORED("Tailored"),
    FORM_FITTING("Form Fitting"),
    OVERSIZED("Oversized"),
    MINIMALIST("Minimalist"),
    LAYERED("Layered"),

}