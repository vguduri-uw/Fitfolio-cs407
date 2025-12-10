package com.cs407.fitfolio.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.R
import com.cs407.fitfolio.data.User
import com.cs407.fitfolio.ui.modals.EditableField
import com.cs407.fitfolio.viewModels.UserState
import com.cs407.fitfolio.viewModels.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.ui.theme.Kudryashev_Regular

fun createAccount(
    context: Context,
    email: String,
    password: String,
    onSuccess: (FirebaseUser) -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    user.sendEmailVerification().addOnCompleteListener { verifyTask ->
                        if (verifyTask.isSuccessful) {
                            auth.signOut() // prevent login until verified
                            onSuccess(user)
                        } else {
                            onError(verifyTask.exception?.message ?: "Failed to send verification email.")
                        }
                    }
                } else {
                    onError("Failed to get current user.")
                }
            } else {
                onError(task.exception?.message ?: "Failed to create account.")
            }
        }
}

@Composable
fun SignUpScreen (
    onNavigateToAppNav: () -> Unit,
    onNavigateToSignInScreen: () -> Unit,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val db = FitfolioDatabase.getDatabase(context)
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp)
        ) {
            // profile image, my outfits title, information icon
            SignUpScreenTopHeader()

            // sign up form - name, email, password, re-enter password
            SignUpForm(onNavigateToSignInScreen,
                onNavigateToAppNav,
                userViewModel
            )
        }

    }
}

@Composable
fun SignUpScreenTopHeader() {
    Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = "App logo",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(160.dp)
            .clip(CircleShape),
        alignment = Alignment.Center
    )

    Spacer(modifier = Modifier.size(15.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "FitFolio",
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 60.sp
        )
    }
}

@Composable
fun SignUpForm( onNavigateToSignInScreen: () -> Unit,onNavigateToAppNav: () -> Unit, userViewModel: UserViewModel) {
    // User information
    // TODO: implement the way to get this from storage (these are placeholders)
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error: String? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val db = FitfolioDatabase.getDatabase(context)
    // todo: replace with actual error handling
    var reenteredPassword by remember { mutableStateOf("") }
    val passwordsMatch = password == reenteredPassword
    val scope = rememberCoroutineScope()
    // Track whether all fields are filled (to make sign up button available)
    val allFieldsFilled = name.isNotEmpty() && email.isNotEmpty() &&
            password.isNotEmpty() && reenteredPassword.isNotEmpty()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 48.dp, start = 24.dp, end = 24.dp)
    ) {
        // Fields for editing user information
        // TODO: add edit button and only enable then??
        EditableField(
            label = "Name",
            value = name,
            onValueChange = { name = it },
            isPassword = false,
        )

        EditableField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            isPassword = false
        )
        EditableField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            isPassword = true
        )

        EditableField(
            label = "Re-enter Password",
            value = reenteredPassword,
            onValueChange = { reenteredPassword = it },
            isPassword = true
        )
        // todo: implement actual error handling logic
        if (password.isNotEmpty() && reenteredPassword.isNotEmpty() && !passwordsMatch) {
            Text(text = "Passwords do not match", color = MaterialTheme.colorScheme.error)
        }

        // show Firebase / other errors in consistent red text
        ErrorText(error = error)

//        // Sign up button
        Button(
            enabled = allFieldsFilled && passwordsMatch,
            onClick = {
                error = null
                createAccount(context, email, password,
                    onSuccess = { firebaseUser ->
                        scope.launch {
                            try {
                                var localUser = db.userDao().getByUID(firebaseUser.uid)
                                if (localUser == null) {
                                    val newUser = User(
                                        userUID = firebaseUser.uid,
                                        username = name,
                                        email = email,
                                        newUser = true
                                    )
                                    db.userDao().insert(newUser)
                                    localUser = db.userDao().getByUID(firebaseUser.uid)
                                }

                                if (localUser != null) {
                                    userViewModel.setUser(
                                        UserState(
                                            id = localUser.userId,
                                            name = localUser.username,
                                            uid = firebaseUser.uid,
                                            newUser = true
                                        )
                                    )
                                    // Instead of navigating to app, maybe navigate to "Check your email" screen
                                    Toast.makeText(
                                        context,
                                        "Account created! Please verify your email before logging in.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    onNavigateToSignInScreen()
                                } else {
                                    error = "Failed to create local user"
                                }
                            } catch (e: Exception) {
                                error = e.message ?: "Unknown database error"
                            }
                        }
                    },
                    onError = { e ->
                        error = e
                    }
                )
            },
            modifier = Modifier.padding(top = 25.dp)
        ) {
            Text("Sign Up", fontFamily = Kudryashev_Regular, fontSize = 18.sp)
        }

        // move to sign in page if account exists already
        Row {
            Text(
                text = "Already have an account?",
                fontFamily = Kudryashev_Regular,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Sign in",
                fontFamily = Kudryashev_Regular,
                fontSize = 15.sp,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable(onClick = { onNavigateToSignInScreen() })
            )
        }
    }
}