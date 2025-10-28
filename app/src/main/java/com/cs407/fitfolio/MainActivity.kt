package com.cs407.fitfolio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.fitfolio.ui.theme.FitfolioTheme
import com.cs407.fitfolio.ui.screens.MyOutfitsScreen
import com.cs407.fitfolio.ui.screens.CalendarScreen
import com.cs407.fitfolio.ui.screens.MyWardrobeScreen
import com.cs407.fitfolio.ui.screens.AddScreen
import com.cs407.fitfolio.ui.screens.MyClosetScreen
import com.cs407.fitfolio.ui.navigation.BottomNavigationBar
import com.cs407.fitfolio.ui.screens.SignUpScreen
import com.cs407.fitfolio.ui.viewModels.ClosetViewModel
import com.cs407.fitfolio.ui.viewModels.OutfitsViewModel

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
    val closetViewModel: ClosetViewModel = viewModel()
    val outfitsViewModel: OutfitsViewModel = viewModel()

    Scaffold(
        // Creates bottom navigation bar with centered floating action button
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            Box{
                FloatingActionButton(
                    onClick = { navController.navigate("wardrobe") },
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp)
                        .offset(y = 50.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Wardrobe")
                        Text(stringResource(R.string.carousel))
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        // NavHost sets up the navigation graph for the app for navigation outside of bottom bar
        NavHost(
            navController = navController, // Controller that handles navigation
            startDestination = "outfits", // First screen to display when app starts
            modifier = Modifier.padding(innerPadding)
        ) {
            // Defines the "outfits" route and what UI to display there
            composable("outfits") {
                MyOutfitsScreen(
                    onNavigateToCalendarScreen = { navController.navigate("calendar") },
                    onNavigateToWardrobeScreen = { navController.navigate("wardrobe") },
                    onNavigateToAddScreen = { navController.navigate("add") },
                    onNavigateToClosetScreen = { navController.navigate("closet") },
                    outfitsViewModel = outfitsViewModel,

                    // temporary routes to sign in and sign up pages
                    onNavigateToSignUpScreen = { navController.navigate("sign_up") },

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
            composable(route = "wardrobe") {
                MyWardrobeScreen(
                    onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                    onNavigateToCalendarScreen = { navController.navigate("calendar") },
                    onNavigateToAddScreen = { navController.navigate("add") },
                    onNavigateToClosetScreen = { navController.navigate("closet") }
                )
            }
            // Defines the "add" route and what UI to display there
            composable(route = "add") {
                AddScreen(
                    onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                    onNavigateToCalendarScreen = { navController.navigate("calendar") },
                    onNavigateToWardrobeScreen = { navController.navigate("wardrobe") },
                    onNavigateToClosetScreen = { navController.navigate("closet") },
                    closetViewModel = closetViewModel
                )
            }
            // Defines the "closet" route and what UI to display there
            composable(route = "closet") {
                MyClosetScreen(
                    onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                    onNavigateToCalendarScreen = { navController.navigate("calendar") },
                    onNavigateToWardrobeScreen = { navController.navigate("wardrobe") },
                    onNavigateToAddScreen = { navController.navigate("add") },
                    closetViewModel = closetViewModel
                )
            }
            // Defines the "sign up" route and what UI to display there
            composable(route = "sign_up") {
                SignUpScreen(
                    onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                    onNavigateToCalendarScreen = { navController.navigate("calendar") },
                    onNavigateToWardrobeScreen = { navController.navigate("wardrobe") },
                    onNavigateToAddScreen = { navController.navigate("add") },
                    onNavigateToClosetScreen = { navController.navigate("closet") },
                    onNavigateToSignInScreen = { navController.navigate("sign_in") },
                )
            }
        }
    }
}