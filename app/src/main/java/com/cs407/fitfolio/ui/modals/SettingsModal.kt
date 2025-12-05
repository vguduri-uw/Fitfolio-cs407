package com.cs407.fitfolio.ui.modals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.cs407.fitfolio.ui.theme.Google_Sans_Flex
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt

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

    // track whether avatar instructions and dialog are showing
    var showAvatarInstructions by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentUserState.id) {
        if (currentUserState.id != 0) {
            val localUser = db.userDao().getByUID(currentUserState.uid)
            if (localUser != null) {
                name = localUser.username
                email = localUser.email ?: ""
            }
        }
    }
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            SettingsHeader() // TODO: allow update profile image??

            // avatar button
            val avatarButtonLabel =
                if (currentUserState.avatarUri.isBlank()) "Create Avatar" else "Edit Avatar"

            Button(
                onClick = { showAvatarInstructions = true },
                modifier = Modifier
                    .offset(y = -10.dp)
                    .width(160.dp)
            ) {
                Text(text = avatarButtonLabel, fontFamily = Google_Sans_Flex, fontSize = 15.sp)
            }

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
                content = { Text("Save", fontFamily = Google_Sans_Flex, fontSize = 15.sp) },
                modifier = Modifier.width(125.dp)
            )

            // Sign out button
            Button( // TODO: implement sign out functionality
                // there should be an alert dialog that requires them to confirm
                onClick = {
                    showSignOutDialog = true

                },
                content = { Text("Sign Out", fontFamily = Google_Sans_Flex, fontSize = 15.sp) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .width(125.dp)
                    .offset(y = -10.dp)
            )

            // Reset account link
            Text(
                text = "Reset your account",
                fontFamily = Google_Sans_Flex,
                textDecoration = TextDecoration.Underline,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable(onClick = {})
                    .offset(y = -20.dp)
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
                    Text("Yes", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
                }
            },
            dismissButton = {
                Button(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
                }
            }
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
                    fontFamily = Google_Sans_Flex, fontSize = 15.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAvatarInstructions = false
                        showAvatarDialog = true
                    }
                ) { Text("Continue", fontFamily = Google_Sans_Flex, fontSize = 15.sp) }
            },
            dismissButton = {
                Button(onClick = { showAvatarInstructions = false }) {
                    Text("Cancel", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
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

    Text(text = "FitFolio", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 30.sp)
    Text(text = "Settings", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 20.sp, modifier = Modifier.offset(y = -15.dp))
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
            onValueChange = onValueChange,
            label = { Text(label, fontFamily = Google_Sans_Flex) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

// Converts a given img uri to a resized JPEG Base64 string to prevent issues when sending to FASHN API
suspend fun uriToJpegBase64(
    context: android.content.Context,
    uri: Uri,
    maxPixels: Int = 1_050_000
): String? = withContext(Dispatchers.IO) {
    // Decode to bitmap
    val input = context.contentResolver.openInputStream(uri) ?: return@withContext null
    val originalBitmap = BitmapFactory.decodeStream(input)
    input.close()

    if (originalBitmap == null) return@withContext null

    val width = originalBitmap.width
    val height = originalBitmap.height
    val totalPixels = width.toLong() * height.toLong()

    val bitmap: Bitmap = if (totalPixels > maxPixels) {
        val scale = sqrt(maxPixels.toDouble() / totalPixels.toDouble())
        val newWidth = (width * scale).toInt().coerceAtLeast(1)
        val newHeight = (height * scale).toInt().coerceAtLeast(1)
        Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
    } else {
        originalBitmap
    }

    // Compress to JPEG
    val output = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
    val jpegBytes = output.toByteArray()
    output.close()

    // Encode to base64
    val encoded = Base64.encodeToString(jpegBytes, Base64.NO_WRAP)
    "data:image/jpeg;base64,$encoded"
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
        title = { Text("Create / Edit Avatar") },
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
                            Text("No photo selected yet", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
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
                        Text("Upload", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
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
                        Text("Take Photo", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
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
                        fontFamily = Google_Sans_Flex,
                        fontSize = 15.sp
                    )
                }

                if (processedAvatarUrl != null) {
                    Text(
                        "Looks good? You can save this as your avatar or retake/reupload.",
                        fontFamily = Google_Sans_Flex, fontSize = 15.sp
                    )
                }
            }
        },
        confirmButton = {
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
                enabled = processedAvatarUrl != null && !isProcessing
            ) {
                Text("Save Avatar", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
            }
        }
    )
}

