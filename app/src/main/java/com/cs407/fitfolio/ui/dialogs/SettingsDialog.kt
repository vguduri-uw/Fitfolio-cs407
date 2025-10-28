package com.cs407.fitfolio.ui.dialogs

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
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.R

@Composable
// TODO: edit style
fun SettingsDialog(onDismiss: () -> Unit) {
    // User information
    // TODO: implement the way to get this from storage (these are placeholders)
    var name by remember { mutableStateOf("Name") }
    var email by remember { mutableStateOf("emailaddress@gmail.com") }
    var password by remember { mutableStateOf("password") }

    // Track whether there is information to be saved
    var saveable by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        confirmButton = {},

        text = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Close button
                IconButton(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = "Close settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp, bottom = 48.dp)
                ) {
                    // Header and title
                    TopHeaderSection()
                    Text(text = "Settings", style = MaterialTheme.typography.titleLarge)

                    // Fields for editing user information
                    // TODO: add edit button and only enable then??
                    EditableField(
                        label = "Name",
                        value = name,
                        onValueChange = { name = it; saveable = true},
                        isPassword = false,
                    )
                    EditableField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it; saveable = true },
                        isPassword = false
                    )
                    EditableField(
                        label = "Password",
                        value = password,
                        onValueChange = { password = it; saveable = true},
                        isPassword = true
                    )

                    // Save button
                    Button(
                        onClick = {
                            saveable = false
                            // TODO: implement saving information to storage
                            // TODO: implement updating information everywhere else in app
                        },
                        enabled = saveable,
                        content = { Text("Save") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.size(32.dp))
                    Button( // TODO: implement sign out functionality
                        // there should be an alert dialog that requires them to confirm
                        onClick = {},
                        content = { Text("Sign Out") },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Reset account link
                Text(
                    text = "Reset your account",
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .clickable(onClick = {})
                    // TODO: implement reset account implementatiom
                    // there should be an alert dialog that requires them to confirm
                )
            }
        }
    )
}

@Composable
fun TopHeaderSection () {
    Image(
        // TODO: replace with actual profile image
        painter = painterResource(id = R.drawable.user),
        contentDescription = "User profile image",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        alignment = Alignment.Center
    )

    Spacer(modifier = Modifier.size(16.dp))
    Text(text = "FitFolio", style = MaterialTheme.typography.titleLarge)
}

@Composable
fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean
) {
    Row() {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

