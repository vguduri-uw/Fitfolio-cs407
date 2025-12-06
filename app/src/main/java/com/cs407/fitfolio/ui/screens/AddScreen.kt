package com.cs407.fitfolio.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cs407.fitfolio.enums.DefaultItemTypes
import com.cs407.fitfolio.ui.modals.InformationModal
import com.cs407.fitfolio.viewModels.ClosetViewModel
import androidx.core.content.ContextCompat
import com.cs407.fitfolio.BuildConfig
import com.cs407.fitfolio.enums.CarouselTypes
import com.cs407.fitfolio.ui.modals.ItemModal
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.ui.theme.LightChocolate
import com.cs407.fitfolio.ui.theme.LightPeachFuzz
import com.cs407.fitfolio.viewModels.OutfitsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.enums.EnumEntries

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
fun ItemTypeDropdown(
    selectedItemType: String,
    allItemTypes: List<String>,
    onItemTypeSelected: (String) -> Unit,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            placeholder = {
                Text("Choose or create an item type",
                    fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.Bold) },
            value = selectedItemType,
            onValueChange = { newType -> onItemTypeSelected(newType) },
            label = {
                Text(
                    "Item Type",
                    fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.Bold
                )},
            modifier = Modifier
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 250.dp)
        ) {
            allItemTypes.forEach { typeName ->
                DropdownMenuItem(
                    text = {
                        Text(
                            typeName,
                            fontFamily = Kudryashev_Display_Sans_Regular,
                            fontWeight = FontWeight.Bold
                        ) },
                    onClick = {
                        onItemTypeSelected(typeName)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Carousel type dropdown for carousel screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarouselTypeDropdown(
    selectedCarouselType: CarouselTypes,
    allCarouselTypes: List<CarouselTypes>,
    onCarouselTypeSelected: (CarouselTypes) -> Unit,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCarouselType.carouselType,
            textStyle = TextStyle(fontFamily = Kudryashev_Display_Sans_Regular, fontWeight = FontWeight.Bold),
            onValueChange = {},
            label = { Text("Carousel Type", fontFamily = Kudryashev_Display_Sans_Regular,
                fontWeight = FontWeight.Bold) },
            modifier = Modifier
                .menuAnchor()
                .clickable { expanded = true },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 250.dp)
        ) {
            allCarouselTypes.forEach { typeName ->
                DropdownMenuItem(
                    text = { Text(typeName.carouselType, fontFamily = Kudryashev_Display_Sans_Regular,
                        fontWeight = FontWeight.Bold) },
                    onClick = {
                        onCarouselTypeSelected(typeName)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddScreen(
    closetViewModel: ClosetViewModel, // used for adding single clothes
    outfitsViewModel: OutfitsViewModel,
    onNavigateToCalendarScreen: () -> Unit
) {
    var showInfo by remember { mutableStateOf(false) } // informationModal
    val context = LocalContext.current // context for toast message

    val closetState by closetViewModel.closetState.collectAsState()
    val availableItemTypes = closetState.itemTypes.filter { it != DefaultItemTypes.ALL.typeName }
    val carouselTypes = CarouselTypes.entries.filter { it != CarouselTypes.DEFAULT }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // selected pic
    var selectedItemType by remember { mutableStateOf("") }
    var selectedCarouselType: CarouselTypes by remember { mutableStateOf(CarouselTypes.DEFAULT) }
    var showItemModal by remember { mutableStateOf(false) }
    var createdItemId: Int by remember { mutableIntStateOf(-1) }
    var saveError by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var aspectRatio by remember { mutableFloatStateOf(1f) }
    val scope =  rememberCoroutineScope()

    // reset screen after successful save to closet
    fun reset() {
        selectedImageUri = null
        selectedItemType = ""
        selectedCarouselType = CarouselTypes.DEFAULT
        createdItemId = -1
        saveError = false
        showInfo = false
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
            Toast.makeText(context, "Camera permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    if (isUploading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightChocolate),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
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
                .background(LightPeachFuzz),
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
                    Text("No image selected yet", fontFamily = Kudryashev_Display_Sans_Regular,
                        fontWeight = FontWeight.Bold)
                } else {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Photo $selectedImageUri",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(aspectRatio),
                        contentScale = ContentScale.Fit,
                        onSuccess = {
                            val w = it.result.drawable.intrinsicWidth
                            val h = it.result.drawable.intrinsicHeight
                            if (w > 0 && h > 0) {
                                val r = w.toFloat() / h.toFloat()
                                aspectRatio = maxOf(r, 0.55f)
                            }
                        }
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
                ) { Text("Upload", fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp) }

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
                ) { Text("Take Photo", fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp) }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item type dropdown menu
                ItemTypeDropdown(
                    selectedItemType = selectedItemType,
                    allItemTypes = availableItemTypes,
                    onItemTypeSelected = { selectedItemType = it },
                    modifier = Modifier.weight(1f)
                )

                // Carousel type dropdown menu
                CarouselTypeDropdown(
                    selectedCarouselType = selectedCarouselType,
                    allCarouselTypes = carouselTypes,
                    onCarouselTypeSelected = { selectedCarouselType = it },
                    modifier = Modifier.weight(1f)
                )
            }

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

                    scope.launch {
                        // upload photo to imgbb
                        isUploading = true
                        val hostedUrl = uploadToImgbb(selectedImageUri!!, context)

                        if (hostedUrl == null) {
                            Toast.makeText(
                                context,
                                "Could not upload image. Please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                            isUploading = false
                            return@launch
                        }

                        // remove background from item photo
                        var cleanedUrl = closetViewModel.removeBackground(hostedUrl)
                        if (cleanedUrl == null) {
                            cleanedUrl = hostedUrl
                            Toast.makeText(
                                context,
                                "Could not remove background from image.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        // add item to closet
                        val itemId = closetViewModel.addItem(
                            name = "New Item",
                            type = selectedItemType,
                            carouselType = selectedCarouselType,
                            description = "Created from Add Page",
                            tags = emptyList(),
                            isFavorites = false,
                            photoUri = cleanedUrl
                        )
                        isUploading = false

                        if (itemId > 0) {
                            createdItemId = itemId
                            Toast.makeText(context, "Item saved to closet.", Toast.LENGTH_SHORT)
                                .show()
                            showItemModal = true
                        } else {
                            saveError = true
                        }
                    }
                },
                enabled = !isUploading && selectedItemType.isNotEmpty() && selectedCarouselType != CarouselTypes.DEFAULT && selectedImageUri != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save to Closet", fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp))
            }

            Spacer(Modifier.height(12.dp))

            if (showItemModal) {
                ItemModal(
                    closetViewModel = closetViewModel,
                    outfitsViewModel = outfitsViewModel,
                    itemId = createdItemId,
                    onDismiss = {
                        showItemModal = false
                        reset()
                    },
                    onNavigateToCalendarScreen = onNavigateToCalendarScreen
                )
            } else if (saveError) {
                Toast.makeText(
                    context,
                    "Item could not be saved. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
                saveError = false
            }
        }
    }
}

suspend fun uploadToImgbb(localUri: Uri, context: Context): String? {
    return withContext(Dispatchers.IO) {
        runCatching {
            val stream = context.contentResolver.openInputStream(localUri)
                ?: return@withContext null
            val bytes = stream.readBytes()
            val encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT)

            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key", BuildConfig.IMGBB_API_KEY)
                .addFormDataPart("image", encodedImage)
                .build()

            val request = Request.Builder()
                .url("https://api.imgbb.com/1/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string()

            val json = JSONObject(bodyString ?: return@withContext null)
            val data = json.getJSONObject("data")

            when {
                data.has("image") && data.getJSONObject("image").has("url") ->
                    data.getJSONObject("image").getString("url")
                data.has("url") ->
                    data.getString("url")
                data.has("display_url") ->
                    data.getString("display_url")
                else -> null
            }
        }.getOrElse { e ->
            e.printStackTrace()
            null
        }
    }
}
