package com.cs407.fitfolio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.navigation.AppNavigation
import com.cs407.fitfolio.ui.screens.SignInScreen
import com.cs407.fitfolio.ui.screens.SignUpScreen
import com.cs407.fitfolio.ui.theme.FitfolioTheme
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

    // Navigate to main app navigation if user is signed in
    LaunchedEffect(userState.id) {
        val dest = if (userState.id == 0) "sign_in" else "app_nav"
        navController.navigate(dest) {
            popUpTo(0)
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (userState.id == 0) "sign_in" else "app_nav"
    ) {
        composable("sign_in") {
            SignInScreen(
                onSignInSuccess = { userViewModel.loginUser(db) },
                onNavigateToSignUpScreen = { navController.navigate("sign_up") },
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
            AppNavigation(userViewModel)
        }
    }
}