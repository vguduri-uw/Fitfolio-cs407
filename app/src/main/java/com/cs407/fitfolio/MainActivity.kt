package com.cs407.fitfolio

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cs407.fitfolio.data.testData.AddTestItemData
import com.cs407.fitfolio.ui.theme.FitfolioTheme
import com.cs407.fitfolio.ui.screens.MyOutfitsScreen
import com.cs407.fitfolio.ui.screens.CalendarScreen
import com.cs407.fitfolio.ui.screens.MyWardrobeScreen
import com.cs407.fitfolio.ui.screens.AddScreen
import com.cs407.fitfolio.ui.screens.MyClosetScreen
import com.cs407.fitfolio.navigation.BottomNavigationBar
import com.cs407.fitfolio.ui.screens.SignUpScreen
import com.cs407.fitfolio.ui.screens.SignInScreen
import com.cs407.fitfolio.data.testData.AddTestOutfitData
import com.cs407.fitfolio.viewModels.ClosetViewModel
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import com.cs407.fitfolio.viewModels.UserState
import com.cs407.fitfolio.viewModels.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.cs407.fitfolio.viewModels.WeatherViewModel

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        enableEdgeToEdge()
        setContent {
            FitfolioTheme {
                // FOR TESTING PURPOSES ONLY
                val closetViewModel: ClosetViewModel = viewModel()
                val outfitsViewModel: OutfitsViewModel = viewModel()

                var populateTestData by remember { mutableStateOf(true) }
                var hasPopulated = remember { mutableStateOf(false) }
                LaunchedEffect(hasPopulated) { if (hasPopulated.value) { populateTestData = false } }

                if (populateTestData) {
                    AddTestItemData(closetViewModel = closetViewModel, outfitsViewModel = outfitsViewModel)
                    AddTestOutfitData(outfitsViewModel)
                    hasPopulated.value = true
                }

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
    val viewModel: UserViewModel = viewModel()
    val userState by viewModel.userState.collectAsState()
    val weatherViewModel: WeatherViewModel = viewModel()

    val context = LocalContext.current
    var isAuthChecked by remember { mutableStateOf(false) }

    //weather permissions for app

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        weatherViewModel.updateLocationPermission(isGranted)
    }
    // Check Firebase auth on launch
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // You can also load extra user info from DB if needed
            viewModel.setUser(
                UserState(
                    id = 1,
                    name = currentUser.displayName ?: "User",
                    uid = currentUser.uid
                )
            )
        }
        isAuthChecked = true
    }

    // Show nothing (or a splash screen) until we know auth state
    if (!isAuthChecked) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading...") // optional splash
        }
        return
    }
    LaunchedEffect(Unit) {
        weatherViewModel.initializeLocationClient(context)
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            weatherViewModel.updateLocationPermission(true)
        } else {
            // Request permission if not granted
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Now render NavHost after we know if user is signed in
    val startDestination = if (userState.id != 0 && userState.name.isNotEmpty()) "outfits" else "sign_in"

    // Determine if bottom bar should be shown
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute !in listOf("sign_in", "sign_up")
    Scaffold(
        // Creates bottom navigation bar with centered floating action button
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        },
        floatingActionButton = {
            if(showBottomBar) {
                Box {
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
                            Icon(
                                painter = painterResource(R.drawable.carousel),
                                contentDescription = "Wardrobe",
                                modifier = Modifier.size(35.dp),
                            )
                            Text(stringResource(R.string.carousel))
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        // NavHost sets up the navigation graph for the app for navigation outside of bottom bar
        NavHost(
            navController = navController, // Controller that handles navigation
            startDestination = startDestination, // First screen to display when app starts
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
                    weatherViewModel = weatherViewModel,

                            // temporary routes to sign in and sign up pages
                    onNavigateToSignUpScreen = { navController.navigate("sign_up") },
                    onNavigateToSignInScreen = { navController.navigate("sign_in") },


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
                    weatherViewModel = weatherViewModel
                )
            }
            // Defines the "wardrobe" route and what UI to display there
            composable(route = "wardrobe") {
                MyWardrobeScreen(
                    onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                    onNavigateToCalendarScreen = { navController.navigate("calendar") },
                    onNavigateToAddScreen = { navController.navigate("add") },
                    onNavigateToClosetScreen = { navController.navigate("closet") },
                    onNavigateToSignInScreen = {navController.navigate("sign_in")},
                    closetViewModel = closetViewModel,
                    weatherViewModel = weatherViewModel,
                    outfitsViewModel = outfitsViewModel
                )
            }
            // Defines the "add" route and what UI to display there
            composable(route = "add") {
                AddScreen(
                    onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                    onNavigateToCalendarScreen = { navController.navigate("calendar") },
                    onNavigateToWardrobeScreen = { navController.navigate("wardrobe") },
                    onNavigateToClosetScreen = { navController.navigate("closet") },
                    onNavigateToSignInScreen = {navController.navigate("sign_in")},
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
                    onNavigateToSignInScreen = {navController.navigate("sign_in")},
                    closetViewModel = closetViewModel,
                    outfitsViewModel = outfitsViewModel
                )
            }
            // Defines the "sign up" route and what UI to display there
            composable(route = "sign_up") {
                SignUpScreen(
                    onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                    onNavigateToSignInScreen = { navController.navigate("sign_in") },
                    userViewModel = viewModel
                )
            }
            // Defines the "sign in" route and what UI to display there
            composable(route = "sign_in") {
                SignInScreen(
                    onNavigateToOutfitsScreen = { navController.navigate("outfits") },
                    onNavigateToSignUpScreen = { navController.navigate("sign_up") },
                )
            }
        }
    }
}