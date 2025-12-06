package com.cs407.fitfolio.navigation

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.fitfolio.R
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.ui.screens.AddScreen
import com.cs407.fitfolio.ui.screens.CalendarScreen
import com.cs407.fitfolio.ui.screens.MyClosetScreen
import com.cs407.fitfolio.ui.screens.MyOutfitsScreen
import com.cs407.fitfolio.ui.screens.MyWardrobeScreen
import com.cs407.fitfolio.ui.theme.ChocolateBrown
import com.cs407.fitfolio.ui.theme.FloralWhite
import com.cs407.fitfolio.ui.theme.GoldenApricot
import com.cs407.fitfolio.ui.theme.Google_Sans_Flex
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.ui.theme.Kudryashev_Regular
import com.cs407.fitfolio.ui.theme.RustBrown
import com.cs407.fitfolio.ui.theme.TrueBlack
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.ClosetViewModelFactory
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import com.cs407.fitfolio.viewModels.OutfitsViewModelFactory
import com.cs407.fitfolio.viewModels.UserViewModel
import com.cs407.fitfolio.viewModels.WeatherViewModel

// Composable function responsible for the main app navigation
// Separation of navigation allows for outfit and item VMs to only be created when a user is logged in
@Composable
fun AppNavigation(userViewModel: UserViewModel, onSignOut: () -> Unit) {
    val userState by userViewModel.userState.collectAsState()
    val navController = rememberNavController()

    val context = LocalContext.current
    val db = FitfolioDatabase.getDatabase(context)

    // View models
    val closetViewModel: ClosetViewModel = viewModel(
        factory = ClosetViewModelFactory(db, userState.id)
    )
    val outfitsViewModel: OutfitsViewModel = viewModel(
        factory = OutfitsViewModelFactory(db, userState.id)
    )
    val weatherViewModel: WeatherViewModel = viewModel()

    //Veda: location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("AppNavigation", "Permission result: $isGranted")
        weatherViewModel.updateLocationPermission(isGranted)
    }

    //Veda: initialize and request permission
    LaunchedEffect(Unit) {
        Log.d("AppNavigation", "LaunchedEffect started")
        weatherViewModel.initializeLocationClient(context)

        kotlinx.coroutines.delay(500)

        if (weatherViewModel.uiState.value.locationPermissionGranted) {
            weatherViewModel.fetchWeatherForCurrentLocation()
        }

        Log.d("AppNavigation", "Requesting location permission")
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        // Creates bottom navigation bar with centered floating action button
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("wardrobe") },
                shape = CircleShape,
                containerColor = GoldenApricot,
                contentColor = TrueBlack,
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = 81.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.carousel),
                        contentDescription = "Wardrobe",
                        modifier = Modifier.size(40.dp),
                    )
                    Text(
                        text = stringResource(R.string.carousel),
                        fontSize = 15.sp,
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.offset(y = -5.dp)
                    )}
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
                    outfitsViewModel = outfitsViewModel,
                    weatherViewModel = weatherViewModel,
                    onSignOut = onSignOut,
                    closetViewModel = closetViewModel,
                    userViewModel = userViewModel
                )
            }
            // Defines the "calendar" route and what UI to display there
            composable("calendar") {
                CalendarScreen(
                    onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                    onNavigateToWardrobeScreen = { navController.navigate("wardrobe") },
                    onNavigateToAddScreen = { navController.navigate("add") },
                    onNavigateToClosetScreen = { navController.navigate("closet") },
                    onNavigateToSignInScreen = {navController.navigate("sign_in")},
                    weatherViewModel = weatherViewModel,
                    outfitsViewModel = outfitsViewModel,
                    closetViewModel = closetViewModel,
                    userViewModel = userViewModel,
                    onSignOut = onSignOut
                )
            }
            // Defines the "wardrobe" route and what UI to display there
            composable(route = "wardrobe") {
                MyWardrobeScreen(
                    onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                    onNavigateToCalendarScreen = { navController.navigate("calendar") },
                    onNavigateToAddScreen = { navController.navigate("add") },
                    onNavigateToClosetScreen = { navController.navigate("closet") },
                    onNavigateToSignInScreen = { navController.navigate("sign_in") },
                    closetViewModel = closetViewModel,
                    weatherViewModel = weatherViewModel,
                    userViewModel = userViewModel,
                    outfitsViewModel = outfitsViewModel
                )
            }
            // Defines the "add" route and what UI to display there
            composable(route = "add") {
                AddScreen(
                    closetViewModel = closetViewModel,
                    outfitsViewModel = outfitsViewModel,
                    onNavigateToCalendarScreen = { navController.navigate("calendar") },

                )
            }
            // Defines the "closet" route and what UI to display there
            composable(route = "closet") {
                MyClosetScreen(
                    onNavigateToCalendarScreen = { navController.navigate("calendar") },
                    closetViewModel = closetViewModel,
                    outfitsViewModel = outfitsViewModel,
                    userViewModel = userViewModel,
                    onSignOut = onSignOut
                )
            }
        }
    }
}
