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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.R

@Composable
// TODO: edit style
fun SettingsDialog(onDismiss: () -> Unit) {
    // user values
    // TODO: implement the way to get this from storage (these are placeholders)
    var name by remember { mutableStateOf("Name") }
    var email by remember { mutableStateOf("emailaddress@gmail.com") }
    var password by remember { mutableStateOf("password") }

    AlertDialog(
        onDismissRequest = { onDismiss },
        modifier = Modifier.fillMaxSize(),
        confirmButton = {},

        text = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(alignment = Alignment.TopCenter)
                        .fillMaxWidth()
                ) {
                    TopHeaderSection()

                    Spacer(modifier = Modifier.size(16.dp))

                    // TODO: add edit button and only enable then??
                    EditableField(
                        label = "Name",
                        value = name,
                        onValueChange = { name = it},
                        isPassword = false,
                    )

                    Spacer(modifier = Modifier.size(8.dp))
                    EditableField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it },
                        isPassword = false
                    )

                    Spacer(modifier = Modifier.size(8.dp))
                    EditableField(
                        label = "Password",
                        value = password,
                        onValueChange = { password = it },
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.size(8.dp))
                    Button( // TODO: only enable if some text has edited
                        onClick = {},
                        enabled = true,
                        content = { Text("Save") }
                    )

                    Spacer(modifier = Modifier.size(32.dp))
                    Button( // TODO: implement sign out functionality
                        onClick = {},
                        enabled = true,
                        content = { Text("Sign Out") }
                    )
                }

                // TODO: make underlineable
                Text(modifier = Modifier
                    .clickable(onClick = {}) // TODO: implement reset account implementatiom
                    .align(alignment = Alignment.BottomCenter),
                    text = "Reset your account")
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

    Spacer(modifier = Modifier.size(16.dp))
    Text(text = "Settings", style = MaterialTheme.typography.titleLarge)
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

