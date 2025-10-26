package com.cs407.fitfolio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.fitfolio.ui.theme.FitfolioTheme
import com.cs407.fitfolio.ui.screens.MyOutfitsScreen
import com.cs407.fitfolio.ui.screens.CalendarScreen
import com.cs407.fitfolio.ui.screens.MyWardrobeScreen
import com.cs407.fitfolio.ui.screens.AddScreen
import com.cs407.fitfolio.ui.screens.MyClosetScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitfolioTheme {
                AppNavigation()
            }
        }
    }
}

// Composable function responsible for navigation between screens
@Composable
fun AppNavigation() {
    // Creates and remembers a NavController to manage navigation state
    val navController = rememberNavController()

    // NavHost sets up the navigation graph for the app
    NavHost(
        navController = navController, // Controller that handles navigation
        startDestination = "outfits" // First screen to display when app starts
    ) {
        // Defines the "outfits" route and what UI to display there
        composable("outfits") {
            MyOutfitsScreen(
                onNavigateToCalendarScreen = { navController.navigate("calendar") },
                onNavigateToWardrobeScreen = { navController.navigate("wardrobe") },
                onNavigateToAddScreen = { navController.navigate("add") },
                onNavigateToClosetScreen = { navController.navigate("closet") }
            )
        }
        // Defines the "calendar" route and what UI to display there
        composable("calendar") {
            CalendarScreen(
                onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                onNavigateToWardrobeScreen = { navController.navigate("wardrobe") },
                onNavigateToAddScreen = { navController.navigate("add") },
                onNavigateToClosetScreen = { navController.navigate("closet") }
            )
        }
        // Defines the "wardrobe" route and what UI to display there
        composable(route="wardrobe") {
            MyWardrobeScreen(
                onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                onNavigateToCalendarScreen = { navController.navigate("calendar") },
                onNavigateToAddScreen = { navController.navigate("add") },
                onNavigateToClosetScreen = { navController.navigate("closet") }
            )
        }
        // Defines the "add" route and what UI to display there
        composable (route="add") {
            AddScreen(
                onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                onNavigateToCalendarScreen = { navController.navigate("calendar") },
                onNavigateToWardrobeScreen = { navController.navigate("wardrobe") },
                onNavigateToClosetScreen = { navController.navigate("closet") }
            )
        }
        // Defines the "closet" route and what UI to display there
        composable (route="closet") {
            MyClosetScreen(
                onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                onNavigateToCalendarScreen = { navController.navigate("calendar") },
                onNavigateToWardrobeScreen = { navController.navigate("wardrobe") },
                onNavigateToAddScreen = { navController.navigate("add") },
            )
        }
    }
}