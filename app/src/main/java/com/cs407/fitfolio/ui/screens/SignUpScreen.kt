package com.cs407.fitfolio.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.components.TopHeader
import com.cs407.fitfolio.ui.modals.EditableField
import com.cs407.fitfolio.ui.modals.SettingsHeader

@Composable
fun SignUpScreen (
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToWardrobeScreen: () -> Unit,
    onNavigateToAddScreen: () -> Unit,
    onNavigateToClosetScreen: () -> Unit,
    onNavigateToSignInScreen: () -> Unit
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
            SignUpScreenTopHeader()

            // sign up form - name, email, password, re-enter password
            SignUpForm(onNavigateToSignInScreen)
        }

        // back button (navigates back to my outfits for now)
        // navigate to sign up and sign in screens
        IconButton(
            onClick = { onNavigateToOutfitsScreen() },
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back arrow",
                Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun SignUpScreenTopHeader() {
    Image(
        // todo: replace with actual profile image
        painter = painterResource(id = R.drawable.user),
        contentDescription = "User profile image",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        alignment = Alignment.Center
    )

    Spacer(modifier = Modifier.size(15.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "FitFolio", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun SignUpForm( onNavigateToSignInScreen: () -> Unit ) {
    // User information
    // TODO: implement the way to get this from storage (these are placeholders)
    var name by remember { mutableStateOf("Name") }
    var email by remember { mutableStateOf("emailaddress@gmail.com") }
    var password by remember { mutableStateOf("password") }

    // todo: replace with actual error handling
    var reenteredPassword by remember { mutableStateOf("password") }
    val passwordsMatch = password == reenteredPassword

    // Track whether all fields are filled (to make sign up button available)
    val allFieldsFilled by remember {
        derivedStateOf {
            name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && reenteredPassword.isNotEmpty()
        }
    }

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
        if (!passwordsMatch) {
            Text(text = "Passwords do not match", color = MaterialTheme.colorScheme.error)
        }

        // Sign up button
        Button(
            onClick = { },
            enabled = allFieldsFilled,
            content = { Text("Sign Up") },
        )

        // move to sign in page if account exists already
        Row {
            Text(
                text = "Already have an account?",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Sign in",
                style = MaterialTheme.typography.bodyMedium.merge(
                    TextStyle(textDecoration = TextDecoration.Underline)),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable(onClick = { onNavigateToSignInScreen() })
            )
        }
    }
}