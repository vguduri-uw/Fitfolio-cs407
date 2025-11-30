package com.cs407.fitfolio.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.modals.EditableField
import com.cs407.fitfolio.ui.theme.Kudryashev_Bold_Regular
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.ui.theme.Kudryashev_Regular
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun ErrorText(error: String?, modifier: Modifier = Modifier) {
    if (error != null)
        Text(text = error, color = Color.Red, textAlign = TextAlign.Center)
}

fun signIn(
    email: String,
    password: String,
    onComplete: (Boolean, Exception?, FirebaseUser?) -> Unit,
) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(true, null, auth.currentUser)
            } else {
                onComplete(false, task.exception, null)
            }
        }
}

@Composable
fun SignInScreen (
    onSignInSuccess: () -> Unit,
    onNavigateToSignUpScreen: () -> Unit,
) {

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
            SignInScreenTopHeader()

            // sign in form - name, email, password
            SignInForm(onNavigateToSignUpScreen, onSignInSuccess)
        }

    }
}

@Composable
fun SignInScreenTopHeader() {
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
        Text(text = "FitFolio", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 60.sp)
    }
}

@Composable
fun SignInForm(
    onNavigateToSignUpScreen: () -> Unit,
    onSignInSuccess: () -> Unit,
//    userDao: UserDao,
) {
    // User information
    // TODO: implement the way to get this from storage (these are placeholders)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null)}
    // Track whether all fields are filled (to make sign in button available)
    val allFieldsFilled by remember {
        derivedStateOf {
            email.isNotEmpty() && password.isNotEmpty()
        }
    }

    Box {
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

            Spacer(modifier = Modifier.size(10.dp))
            // displays if passwords do not match
            ErrorText(error = errorMessage)

            // Sign in button
            Button(
                onClick = {
                    signIn(email, password) { success, exception, user ->
                        if (success && user != null) {
                            onSignInSuccess()
                        } else {
                            errorMessage = exception?.message ?: "Sign in failed"
                        }
                    }
                },
                enabled = allFieldsFilled,
                content = {
                    Text(
                        text = "Sign Up",
                        fontFamily = Kudryashev_Regular,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                },
            )

            // move to sign up page if account doesn't exist
            Row{
                Text(
                    text = "Don't have an account?",
                    fontFamily = Kudryashev_Regular,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "Sign up",
                    fontFamily = Kudryashev_Regular,
                    fontSize = 15.sp,
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .clickable(onClick = { onNavigateToSignUpScreen() })
                )
            }
        }
    }
}