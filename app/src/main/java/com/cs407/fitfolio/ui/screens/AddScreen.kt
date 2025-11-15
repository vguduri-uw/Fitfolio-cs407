package com.cs407.fitfolio.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.ui.viewModels.ClosetViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.cs407.fitfolio.ui.modals.InformationModal
import com.cs407.fitfolio.ui.modals.SettingsModal

@Composable
fun AddScreen(
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToWardrobeScreen: () -> Unit,
    onNavigateToClosetScreen: () -> Unit,
    onNavigateToSignInScreen: () -> Unit,
    closetViewModel: ClosetViewModel? //used for adding single clothes?
) {

    //informationModal
    var showInfo by remember { mutableStateOf(false) }
    //context for toast message
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        //actual camera view with a suggested body outline,
        // or the uploaded picture preview and editing
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFECECEC)),
            contentAlignment = Alignment.Center
        ) {
            // info icon (camera instruction, other info...
            IconButton(
                onClick = {showInfo = true},
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
            //show the half screen
            if( showInfo ){
                InformationModal (
                    onDismiss = { showInfo = false},
                    screen = "Add"
                )
            }
            Text("Camera Preview (placeholder)")
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { /* access the device album to choose picture */ },
                modifier = Modifier.weight(1f)
            ) { Text("Upload") }

            Button(
                onClick = { /* allow user to take picture */ },
                modifier = Modifier.weight(1f)
            ) { Text("Take Photo") }
        }

        Spacer(Modifier.height(12.dp))

        // save the edited picture as a single clothes?
        Button(
            onClick = {
                Toast.makeText(context, "Saved to closet", Toast.LENGTH_SHORT).show()
                onNavigateToClosetScreen},
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save to Closet") }

        Spacer(Modifier.height(12.dp))


    }


}
