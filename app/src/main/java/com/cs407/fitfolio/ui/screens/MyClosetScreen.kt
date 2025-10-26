package com.cs407.fitfolio.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MyClosetScreen(
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToWardrobeScreen: () -> Unit,
    onNavigateToAddScreen: () -> Unit
) {
    Text("Closet screen")
}

