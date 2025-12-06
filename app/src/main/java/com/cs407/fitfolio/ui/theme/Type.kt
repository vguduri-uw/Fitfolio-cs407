package com.cs407.fitfolio.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import com.cs407.fitfolio.R


// Typography for our app (custom font)
val FitFolioFontFamily = FontFamily(
    Font(R.font.kudryashev_display_sans_regular, FontWeight.Bold)
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),
    displayMedium = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),
    displaySmall = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),

    headlineLarge = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),

    titleLarge = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),
    titleSmall = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),

    bodyLarge = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),
    bodyMedium = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),
    bodySmall = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),

    labelLarge = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),
    labelMedium = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),
    labelSmall = TextStyle(fontFamily = FitFolioFontFamily, fontWeight = FontWeight.Bold),
)

// Set of Material typography styles to start with
//val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
//)