package com.cs407.fitfolio.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cs407.fitfolio.enums.DefaultItemTypes
import com.cs407.fitfolio.ui.modals.InformationModal
import com.cs407.fitfolio.viewModels.ClosetViewModel
import androidx.core.content.ContextCompat

fun createImageUri(context: Context): Uri {
    val contentResolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "fitfolio_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )!!
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedType: String,
    allTypes: List<String>,
    onTypeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = {}, // read-only
            label = { Text("Category") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 250.dp)
        ) {
            allTypes.forEach { typeName ->
                DropdownMenuItem(
                    text = { Text(typeName) },
                    onClick = {
                        onTypeSelected(typeName)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddScreen(
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToWardrobeScreen: () -> Unit,
    onNavigateToClosetScreen: () -> Unit,
    onNavigateToSignInScreen: () -> Unit,
    closetViewModel: ClosetViewModel // used for adding single clothes
) {
    var showInfo by remember { mutableStateOf(false) } // informationModal
    val context = LocalContext.current // context for toast message

    val closetState by closetViewModel.closetState.collectAsState()
    val availableTypes = closetState.itemTypes.filter { it != DefaultItemTypes.ALL.typeName }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // selected pic
    var selectedType by remember {
        mutableStateOf(
            availableTypes.firstOrNull() ?: DefaultItemTypes.T_SHIRTS.typeName
        )
    }

    // gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) } // new taken pic
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = cameraImageUri
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
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        //show the selected/taken photo
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFECECEC)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { showInfo = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info for camera use",
                    tint = Color.Black
                )
            }

            if (showInfo) {
                InformationModal(
                    onDismiss = { showInfo = false },
                    screen = "Add"
                )
            }

            if (selectedImageUri == null) {
                Text("No image selected yet")
            } else {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "selected photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        //two button: upload & take photo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) { Text("Upload") }

            Button(
                onClick = {
                    val permissionStatus = ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.CAMERA
                    )

                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        val uri = createImageUri(context)
                        cameraImageUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Take Photo") }
        }

        Spacer(Modifier.height(12.dp))

        //dropdown menu
        CategoryDropdown(
            selectedType = selectedType,
            allTypes = availableTypes,
            onTypeSelected = { selectedType = it }
        )

        Spacer(Modifier.height(12.dp))

        // Save button
        Button(
            onClick = {
                if (selectedImageUri == null) {
                    Toast.makeText(
                        context,
                        "Please upload or take a photo first.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                val imageUriString = selectedImageUri.toString()
                val categoryString = selectedType

                closetViewModel.addItem(
                    name = "",
                    type = categoryString,
                    description = "",
                    tags = emptyList(),
                    isFavorites = false,
                    photoUri = imageUriString
                )

                Toast.makeText(context, "Saved to closet", Toast.LENGTH_SHORT).show()
                onNavigateToClosetScreen()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save to Closet")
        }

        Spacer(Modifier.height(12.dp))
    }
}
