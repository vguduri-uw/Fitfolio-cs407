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
    Font(R.font.kudryashev_display_sans_regular, FontWeight.Normal),
    Font(R.font.kudryashev_display_sans_regular, FontWeight.Bold)
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = FitFolioFontFamily),
    displayMedium = TextStyle(fontFamily = FitFolioFontFamily),
    displaySmall = TextStyle(fontFamily = FitFolioFontFamily),

    headlineLarge = TextStyle(fontFamily = FitFolioFontFamily),
    headlineMedium = TextStyle(fontFamily = FitFolioFontFamily),
    headlineSmall = TextStyle(fontFamily = FitFolioFontFamily),

    titleLarge = TextStyle(fontFamily = FitFolioFontFamily),
    titleMedium = TextStyle(fontFamily = FitFolioFontFamily),
    titleSmall = TextStyle(fontFamily = FitFolioFontFamily),

    bodyLarge = TextStyle(fontFamily = FitFolioFontFamily),
    bodyMedium = TextStyle(fontFamily = FitFolioFontFamily),
    bodySmall = TextStyle(fontFamily = FitFolioFontFamily),

    labelLarge = TextStyle(fontFamily = FitFolioFontFamily),
    labelMedium = TextStyle(fontFamily = FitFolioFontFamily),
    labelSmall = TextStyle(fontFamily = FitFolioFontFamily),
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