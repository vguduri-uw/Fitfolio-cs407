package com.cs407.fitfolio.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.cs407.fitfolio.R
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.ui.modals.InformationModal
import com.cs407.fitfolio.ui.screens.createImageUri
import com.cs407.fitfolio.ui.theme.Google_Sans_Flex
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.viewModels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun TopHeader (title: String? = null, userViewModel: UserViewModel) {
    var showInformation by remember { mutableStateOf(false) }
    var showProfilePictureDialog by remember { mutableStateOf(false) }
    val currentUserState by userViewModel.userState.collectAsState()

    Spacer(modifier = Modifier.size(10.dp))

    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { showProfilePictureDialog = true },
        contentAlignment = Alignment.Center
    ) {
        if (currentUserState.profilePictureUri.isBlank()) {
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "User profile image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = currentUserState.profilePictureUri,
                contentDescription = "User profile image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Spacer(modifier = Modifier.size(4.dp))
    if (title != null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.size(41.dp))

                Text(text = title, fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 30.sp)

                IconButton(onClick = { showInformation = true }) { // todo: add info onClick lambda
                    Icon(
                        painter = painterResource(R.drawable.info),
                        contentDescription = "Information",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        if (showInformation) {
            InformationModal(onDismiss = { showInformation = false}, screen = title)
        }
    }
    if (showProfilePictureDialog) {
        ProfilePictureDialog(
            userId = currentUserState.id,
            existingProfilePictureUrl = currentUserState.profilePictureUri,
            onProfilePictureSaved = { newUrl ->
                userViewModel.setUser(currentUserState.copy(profilePictureUri = newUrl))
                showProfilePictureDialog = false
            },
            onDismiss = { showProfilePictureDialog = false }
        )
    }
}

//Profile picture dialog box similar to the avatar adding box
@Composable
fun ProfilePictureDialog(
    userId: Int,
    existingProfilePictureUrl: String,
    onProfilePictureSaved: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val db = FitfolioDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageUri by remember {
        mutableStateOf<String?>(existingProfilePictureUrl.ifBlank { null })
    }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            localImageUri = uri
            selectedImageUri = uri.toString()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            localImageUri = cameraImageUri
            selectedImageUri = cameraImageUri.toString()
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
        title = { Text("Update Profile Picture") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        selectedImageUri != null -> {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected profile picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Text("No photo selected yet", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
                        }
                    }
                }

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
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text("Cancel", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.size(8.dp))

                Button(
                    onClick = {
                        val finalUrl = selectedImageUri
                        if (finalUrl == null) {
                            Toast.makeText(
                                context,
                                "Please select an image before saving.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        scope.launch {
                            db.userDao().updateProfilePicture(userId, finalUrl)
                            onProfilePictureSaved(finalUrl)
                            Toast.makeText(context, "Profile picture saved.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = selectedImageUri != null,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text("Save", fontFamily = Google_Sans_Flex, fontSize = 15.sp)
                }
            }
        }
    )
}
