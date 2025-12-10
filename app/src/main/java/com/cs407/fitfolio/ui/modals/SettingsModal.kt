package com.cs407.fitfolio.ui.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.R
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.viewModels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.cs407.fitfolio.ui.screens.createImageUri
import com.cs407.fitfolio.services.RetrofitInstance
import com.cs407.fitfolio.services.FashnRunRequest
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import com.cs407.fitfolio.ui.components.TopHeader
import com.cs407.fitfolio.viewModels.UserState
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import com.cs407.fitfolio.ui.theme.FloralWhite
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt
import androidx.exifinterface.media.ExifInterface
import android.graphics.Matrix
import java.io.ByteArrayInputStream

// Loads the user from Room DB, shows editable fields, avatar creation/edit, and sign-out logic
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
    //current email, password and username
    var currentName by remember { mutableStateOf("Name") }
    var currentEmail by remember { mutableStateOf("email@gmail.com") }
    var currentPassword by remember { mutableStateOf("") }

    //new email, password, and username
    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    // Track sheet state and open to full screen
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    // Track whether sign-out dialog is showing
    var showSignOutDialog by remember { mutableStateOf(false) }

    // track whether delete dialog shows
    var showDeleteDialog by remember { mutableStateOf(false) }

    //authentification dialog to change info
    var showReauthDialog by remember { mutableStateOf(false) }

    // track whether avatar instructions and dialog are showing
    var showAvatarInstructions by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = FitfolioDatabase.getDatabase(context)
    val currentUserState by userViewModel.userState.collectAsState()
    val scope = rememberCoroutineScope()


// Track whether there is information to be saved
    val saveable by remember {
        derivedStateOf {
            newName != currentUserState.name ||
                    newEmail != currentUserState.email ||
                    newPassword.isNotBlank()
        }
    }
    LaunchedEffect(currentUserState) {
        //if there is a user according to the user state, then the current name and email should be based on them
        if (currentUserState.id != 0) {
            val localUser = db.userDao().getByUID(currentUserState.uid)
            if (localUser != null) {
                currentName = localUser.username
                currentEmail = localUser.email ?: ""

                newName = currentName
                newEmail = currentEmail
            }
        }
    }
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = FloralWhite,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 45.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            SettingsHeader(userViewModel = userViewModel) // TODO: allow update profile image??

            // avatar button
            val avatarButtonLabel =
                if (currentUserState.avatarUri.isBlank()) "Create Avatar" else "Edit Avatar"

            Button(
                onClick = { showAvatarInstructions = true },
                modifier = Modifier
                    .width(160.dp)
                    .padding(bottom = 10.dp)
            ) {
                Text(
                    text = avatarButtonLabel,
                    fontFamily = Kudryashev_Display_Sans_Regular,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Fields for editing user information
            // TODO: add edit button and only enable then??

            EditableField(
                label = "Name",
                value = newName,
                onValueChange = { newName = it},
                isPassword = false,
            )
            EditableField(
                label = "Email",
                value = newEmail,
                onValueChange = { newEmail = it},
                isPassword = false
            )
            EditableField(
                label = "Change Password",
                value = newPassword,
                onValueChange = { newPassword = it},
                isPassword = true
            )

            // row of buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
            ) {
                // Save button
                Button(
                    onClick = {
                        showReauthDialog = true
                    },
                    enabled = saveable,
                    content = {
                        Text(
                            "Save",
                            fontFamily = Kudryashev_Display_Sans_Regular,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.width(125.dp)
                )

                // Sign out button
                Button(
                    onClick = {
                        showSignOutDialog = true

                    },
                    content = {
                        Text(
                            "Sign out",
                            fontFamily = Kudryashev_Display_Sans_Regular,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.width(125.dp)
                )
            }

            // Delete account link
            Text(
                text = "Delete your account",
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .clickable(onClick = { showDeleteDialog = true })
            )
        }
    }
    if (showReauthDialog) {
        //alertdialog should come up to ensure the user want to change the username, password or email
        AlertDialog(
            onDismissRequest = { showReauthDialog = false },
            title = { Text("Confirm Changes") },
            text = {
                Column {
                    Text("Enter your current password to apply updates.")
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        visualTransformation = PasswordVisualTransformation(),
                        label = { Text("Current Password") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // takes the new email, newname, new password and old
                        updateUserAccount(
                            context = context,
                            db = db,
                            name = newName,
                            email = newEmail,
                            newPassword = newPassword,
                            currentPassword = currentPassword,
                            currentUserState = currentUserState,
                            onDone = {
                                showReauthDialog = false
                                currentPassword = ""
                            },
                            scope = scope
                        )
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReauthDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    var deletePassword by remember { mutableStateOf("") }

// Step 1: Show password input dialog
    if (showDeleteDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm to Delete") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Are you sure you want to delete your account? This cannot be undone.",
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = deletePassword,
                        onValueChange = { deletePassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    showDeleteDialog = false

                    // Step 2: Reauthenticate user
                    val auth = FirebaseAuth.getInstance()
                    val firebaseUser = auth.currentUser
                    val email = firebaseUser?.email

                    if (!email.isNullOrBlank() && firebaseUser != null) {
                        val credential = EmailAuthProvider.getCredential(email, deletePassword)
                        firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener { reauthTask ->
                                if (reauthTask.isSuccessful) {
                                    // Step 3: Delete account from Room and Firebase
                                    scope.launch {
                                        try {
                                            db.deleteDao().delete(currentUserState.id)

                                            firebaseUser.delete().addOnCompleteListener { deleteTask ->
                                                if (deleteTask.isSuccessful) {
                                                    Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                                                    userViewModel.logoutUser()
                                                    onSignOut()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Delete failed: ${deleteTask.exception?.message}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Wrong password or reauth failed", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "No user logged in", Toast.LENGTH_LONG).show()
                    }

                    deletePassword = ""
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDeleteDialog = false
                    deletePassword = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirmation dialog for sign out
    if (showSignOutDialog) {
        AlertDialog(
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
                    Text("Yes", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = FloralWhite
        )
    }

    // Instructions for taking avatar photo
    if (showAvatarInstructions) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAvatarInstructions = false },
            title = { Text("Avatar Photo Instructions") },
            text = {
                Text(
                    "For best results, take or upload a full-body photo from head to toe, " +
                            "standing straight, wearing a form-fitting black tank top and black shorts if possible. \n\n" +
                            "Make sure you’re in a well lit space and the background is as simple as possible.",
                    fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAvatarInstructions = false
                        showAvatarDialog = true
                    }
                ) { Text("Continue", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                Button(onClick = { showAvatarInstructions = false }) {
                    Text("Cancel", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showAvatarDialog) {
        AvatarDialog(
            userId = currentUserState.id,
            existingAvatarUrl = currentUserState.avatarUri,
            onAvatarSaved = { newUrl ->
                // update UserState with the new avatarUri
                userViewModel.setUser(
                    currentUserState.copy(avatarUri = newUrl)
                )
                showAvatarDialog = false
            },
            onDismiss = { showAvatarDialog = false }
        )
    }

}

// Simple header section for the settings sheet - user profile icon, app name, modal name
@Composable
fun SettingsHeader (userViewModel: UserViewModel) {
    TopHeader(userViewModel = userViewModel)

    Text(text = "FitFolio", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 30.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.size(2.dp))
    Text(text = "Settings", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.offset(y = -15.dp))
}

// Reusable text field used for Name, Email, and Password rows that enables editing and notifies parent when values change
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
            textStyle = TextStyle(fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold),
            onValueChange = onValueChange,
            label = { Text(label, fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

fun updateUserAccount(
    context: Context,
    db: FitfolioDatabase,
    name: String,
    email: String,
    newPassword: String,
    currentPassword: String,
    currentUserState: UserState,
    onDone: () -> Unit,
    scope: CoroutineScope
) {
    val firebaseUser = Firebase.auth.currentUser

    if (firebaseUser == null) {
        Toast.makeText(context, "Not logged in.", Toast.LENGTH_LONG).show()
        onDone()
        return
    }

    if (currentPassword.isBlank()) {
        Toast.makeText(context, "Please enter your current password.", Toast.LENGTH_LONG).show()
        onDone()
        return
    }

    val trimmedEmail = email.trim()
    val trimmedNewPassword = newPassword.trim()
    val userEmail = firebaseUser.email ?: ""

    // 🔥 REAUTH FIRST
    val credential = EmailAuthProvider.getCredential(userEmail, currentPassword)

    firebaseUser.reauthenticate(credential).addOnCompleteListener { reauth ->
        if (!reauth.isSuccessful) {
            Toast.makeText(context, "Incorrect password.", Toast.LENGTH_LONG).show()
            onDone()
            return@addOnCompleteListener
        }

        // 🟢 UPDATE USERNAME IN ROOM DB
        if (currentUserState.name != name) {
            val userDao = db.userDao()
            scope.launch {
                userDao.updateUser(
                    id = currentUserState.id,
                    username = name,
                    email = currentUserState.email // KEEP SAME EMAIL
                )
            }
        }
        // ✉️ UPDATE EMAIL IF CHANGED
        if (trimmedEmail != userEmail) {
            firebaseUser.verifyBeforeUpdateEmail(trimmedEmail)
                .addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        Toast.makeText(
                            context,
                            "A verification link was sent to $trimmedEmail. Click it to finish updating your email.",
                            Toast.LENGTH_LONG
                        ).show()
                        scope.launch{
                            db.userDao().updateUser(
                                id = currentUserState.id,
                                username = name,
                                email = trimmedEmail
                            )
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Email update failed: ${emailTask.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // Continue to password update after email verification step
                    updatePasswordIfNeeded(firebaseUser, newPassword, context, onDone)
                }

            return@addOnCompleteListener
        }

        // 🟣 If email unchanged, just update password
        updatePasswordIfNeeded(firebaseUser, newPassword, context, onDone)
    }
}


fun updatePasswordIfNeeded(
    firebaseUser: FirebaseUser,
    newPassword: String,
    context: Context,
    onDone: () -> Unit
) {
    if (newPassword.isNotBlank()) {
        firebaseUser.updatePassword(newPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Changes saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "Password update failed: ${task.exception?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            onDone()
        }
    } else {
        Toast.makeText(context, "Changes saved!", Toast.LENGTH_SHORT).show()
        onDone()
    }
}
// Converts a given img uri to a resized JPEG Base64 string to prevent issues when sending to FASHN API
suspend fun uriToJpegBase64(
    context: android.content.Context,
    uri: Uri,
    maxPixels: Int = 1_050_000
): String? = withContext(Dispatchers.IO) {

    // Read the raw bytes once
    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        ?: return@withContext null

    // Read EXIF orientation from the bytes
    val exif = ExifInterface(ByteArrayInputStream(bytes))
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    // Decode bitmap from bytes
    var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        ?: return@withContext null

    // Apply rotation/flip according to EXIF
    bitmap = applyExifTransform(bitmap, orientation)

    // Now check size and scale if needed
    val width = bitmap.width
    val height = bitmap.height
    val totalPixels = width.toLong() * height.toLong()

    val finalBitmap: Bitmap = if (totalPixels > maxPixels) {
        val scale = kotlin.math.sqrt(maxPixels.toDouble() / totalPixels.toDouble())
        val newWidth = (width * scale).toInt().coerceAtLeast(1)
        val newHeight = (height * scale).toInt().coerceAtLeast(1)
        Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    } else {
        bitmap
    }

    // Compress to JPEG
    val output = ByteArrayOutputStream()
    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
    val jpegBytes = output.toByteArray()
    output.close()

    val encoded = Base64.encodeToString(jpegBytes, Base64.NO_WRAP)
    "data:image/jpeg;base64,$encoded"
}

/**
 * Rotates/flips a bitmap based on EXIF orientation.
 */
private fun applyExifTransform(
    source: Bitmap,
    orientation: Int
): Bitmap {
    val matrix = Matrix()

    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> {
            matrix.postRotate(90f)
        }
        ExifInterface.ORIENTATION_ROTATE_180 -> {
            matrix.postRotate(180f)
        }
        ExifInterface.ORIENTATION_ROTATE_270 -> {
            matrix.postRotate(270f)
        }
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
            matrix.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
            matrix.postScale(1f, -1f)
        }
        ExifInterface.ORIENTATION_TRANSPOSE -> { // flip + rotate 90
            matrix.postRotate(90f)
            matrix.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> { // flip + rotate 270
            matrix.postRotate(270f)
            matrix.postScale(-1f, 1f)
        }
        else -> {
            // ORIENTATION_NORMAL or unknown; no transform
            return source
        }
    }

    return Bitmap.createBitmap(
        source,
        0,
        0,
        source.width,
        source.height,
        matrix,
        true
    )
}

// dialog to allow for creating/editing avatar
@Composable
fun AvatarDialog(
    userId: Int,
    existingAvatarUrl: String,
    onAvatarSaved: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val db = FitfolioDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var processedAvatarUrl by remember { mutableStateOf<String?>(existingAvatarUrl.ifBlank { null }) }
    var isUploading by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            localImageUri = uri
            processedAvatarUrl = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            localImageUri = cameraImageUri
            processedAvatarUrl = null
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create / Edit Avatar",
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Preview image for avatar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        processedAvatarUrl != null -> {
                            AsyncImage(
                                model = processedAvatarUrl,
                                contentDescription = "Processed avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        localImageUri != null -> {
                            AsyncImage(
                                model = localImageUri,
                                contentDescription = "Selected avatar photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Text("No photo selected yet", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Upload / Take photo buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Upload", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val permissionStatus = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            )
                            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                                val uri = createImageUri(context)
                                cameraImageUri = uri
                                cameraLauncher.launch(uri)
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Take Photo", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Button to send to FASHN background-change
                Button(
                    onClick = {
                        if (localImageUri == null) {
                            Toast.makeText(
                                context,
                                "Please upload or take a photo first.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        scope.launch {
                            isProcessing = true

                            try {
                                // 👇 Convert the selected image to a safe JPEG base64
                                val base64WithPrefix = uriToJpegBase64(context, localImageUri!!)
                                if (base64WithPrefix == null) {
                                    Toast.makeText(
                                        context,
                                        "Could not read image data.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    isProcessing = false
                                    return@launch
                                }

                                val fashnApi = RetrofitInstance.fashnApi
                                val runRequest = FashnRunRequest(
                                    model_name = "background-change",
                                    inputs = mapOf(
                                        "image" to base64WithPrefix,
                                        "prompt" to "plain light grey studio background",
                                        "output_format" to "png"
                                    )
                                )

                                val runResponse = withContext(Dispatchers.IO) {
                                    fashnApi.runModel(runRequest)
                                }

                                if (runResponse.error != null) {
                                    Toast.makeText(
                                        context,
                                        "FASHN API error: ${runResponse.error.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    isProcessing = false
                                    return@launch
                                }

                                val predictionId = runResponse.id

                                // Poll for completion
                                var outputUrl: String? = null
                                withContext(Dispatchers.IO) {
                                    while (true) {
                                        val status = fashnApi.getPredictionStatus(predictionId)

                                        if (status.status == "completed" || status.status == "succeeded") {
                                            outputUrl = status.output.firstOrNull()
                                            break
                                        }
                                        if (status.status == "failed") {
                                            throw Exception(status.error?.message ?: "FASHN API failed")
                                        }
                                        delay(1500)
                                    }
                                }

                                if (outputUrl == null) {
                                    Toast.makeText(
                                        context,
                                        "No output image returned.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    processedAvatarUrl = outputUrl
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(
                                    context,
                                    "Error processing avatar: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            } finally {
                                isProcessing = false
                            }
                        }
                    },
                    enabled = localImageUri != null && !isProcessing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isProcessing) "Processing..." else "Remove Background",
                        fontFamily = Kudryashev_Display_Sans_Regular,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (processedAvatarUrl != null) {
                    Text(
                        "Looks good? You can save this as your avatar or retake/reupload.",
                        fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 15.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Cancel button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text("Cancel", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.size(8.dp))

                // Save avatar button
                Button(
                    onClick = {
                        val finalUrl = processedAvatarUrl
                        if (finalUrl == null) {
                            Toast.makeText(
                                context,
                                "Please process an image before saving.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        // Save avatar URL to Room, then update ViewModel
                        scope.launch {
                            db.userDao().updateAvatar(userId, finalUrl)
                            onAvatarSaved(finalUrl)
                            Toast.makeText(context, "Avatar saved.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = processedAvatarUrl != null && !isProcessing,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text("Save Avatar", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}

