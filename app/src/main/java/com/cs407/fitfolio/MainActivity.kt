package com.cs407.fitfolio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.navigation.AppNavigation
import com.cs407.fitfolio.ui.screens.SignInScreen
import com.cs407.fitfolio.ui.screens.SignUpScreen
import com.cs407.fitfolio.ui.screens.WelcomeScreen
import com.cs407.fitfolio.ui.theme.FitfolioTheme
import com.cs407.fitfolio.viewModels.UserState
import com.cs407.fitfolio.viewModels.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        enableEdgeToEdge()
        setContent {
            FitfolioTheme {
                AuthNavigation()
            }
        }
    }
}

// Composable function responsible for authentification navigation
@Composable
fun AuthNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val userState by userViewModel.userState.collectAsState()

    val context = LocalContext.current
    val db = FitfolioDatabase.getDatabase(context)

    var isLoading by remember { mutableStateOf(true) }

    // Navigate to main app navigation if user is signed in
    LaunchedEffect(true) {
        val current = FirebaseAuth.getInstance().currentUser
        if (current != null) {
            current.reload() // suspend if using `reloadSuspend()` helper
            if (current.isEmailVerified) {
                val local = db.userDao().getByUID(current.uid) // suspend function
                local?.let {
                    userViewModel.setUser(
                        UserState(
                            id = it.userId,
                            name = it.username,
                            uid = it.userUID,
                            email = it.email,
                            avatarUri = it.avatarUri
                        )
                    )
                }
            } else {
                FirebaseAuth.getInstance().signOut()
            }
        }
        isLoading = false
    }

    if (isLoading) {
        // Blank screen while loading
        LoadingScreen()
    } else {
        // Only start NavHost after we know if user is signed in
        // If user is new, navigate to welcome screen
        val startDestination = when {
            FirebaseAuth.getInstance().currentUser?.isEmailVerified == true -> {
                if (userState.newUser) "welcome" else "app_nav"
            }
            else -> "sign_in"
        }
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable("sign_in") {
                SignInScreen(
                    userViewModel = userViewModel,
                    onNavigateToSignUpScreen = { navController.navigate("sign_up") },
                    onLoginSuccess = {
                        val newUser = userViewModel.userState.value.newUser
                        val route = if (newUser) "welcome" else "app_nav"

                        navController.navigate(route) {
                            popUpTo("sign_in") { inclusive = true }
                        }
                    }
                )
            }
            composable("sign_up") {
                SignUpScreen(
                    onNavigateToAppNav = { navController.navigate("app_nav") },
                    onNavigateToSignInScreen = { navController.navigate("sign_in") },
                    userViewModel = userViewModel
                )
            }
            composable("app_nav") {
                // Main app navigation
                AppNavigation(userViewModel,
                    onSignOut = {
                    userViewModel.logoutUser()  // update user state
                    navController.navigate("sign_in") {
                        popUpTo(0) { inclusive = true }
                    }
                })
            }
            composable("welcome") {
                WelcomeScreen(
                    userState = userState,
                    onContinue = {
                        // Clear the newUser flag and navigate to main app navigation
                        userViewModel.setUserFlag(db)
                        navController.navigate("app_nav") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun LoadingScreen() {
    // Blank screen to prevent flashing
}