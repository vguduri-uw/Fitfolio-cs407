package com.cs407.fitfolio.ui.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.R
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.viewModels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModal (
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
    onSignOut: () -> Unit
    ) {
    // User information
//    val userState by userViewModel.userState.collectAsState()
    // TODO: implement the way to get this from storage (these are placeholders)
    var name by remember { mutableStateOf("name") }
    var email by remember { mutableStateOf("emailaddress@gmail.com") }
    var password by remember { mutableStateOf("password") }

    // Track whether there is information to be saved
    var saveable by remember { mutableStateOf(false) }

    // Track sheet state and open to full screen
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    // Track whether sign-out dialog is showing
    var showSignOutDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = FitfolioDatabase.getDatabase(context)
    val currentUserState by userViewModel.userState.collectAsState()
    val scope = rememberCoroutineScope()


    LaunchedEffect(currentUserState.id) {
        if (currentUserState.id != 0) {
            val localUser = db.userDao().getByUID(currentUserState.uid)
            if (localUser != null) {
                name = localUser.username
                email = localUser.email ?: ""  // Assuming you added an email field in User entity
            }
        }
    }
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            SettingsHeader() // TODO: allow update profile image??

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
                    scope.launch {
                        db.userDao().updateUser(
                            id = currentUserState.id,
                            username = name,
                            email = email
                        )
                        userViewModel.setUser(currentUserState.copy(name = name, uid = currentUserState.uid))
                    }
                    // TODO: implement saving information to storage
                    // TODO: implement updating information everywhere else in app
                    // there should be an alert dialog that requires them to confirm
                },
                enabled = saveable,
                content = { Text("Save") },
            )

            // Sign out button
            Button( // TODO: implement sign out functionality
                // there should be an alert dialog that requires them to confirm
                onClick = {
                    showSignOutDialog = true

                },
                content = { Text("Sign Out") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )

            // Reset account link
            Text(
                text = "Reset your account",
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable(onClick = {})
                // TODO: implement reset account implementation
                // there should be an alert dialog that requires them to confirm
            )
        }
    }
    // Confirmation dialog for sign out
    if (showSignOutDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        userViewModel.logoutUser()
                        showSignOutDialog = false
                        onSignOut()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsHeader () {
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
    Text(text = "Settings", style = MaterialTheme.typography.titleLarge)
}

@Composable
fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean
) {
    Row {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

