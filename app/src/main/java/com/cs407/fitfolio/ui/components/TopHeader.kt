package com.cs407.fitfolio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.modals.InformationModal

@Composable
fun TopHeader (title: String) {
    var showInformation by remember { mutableStateOf(false) }
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

    Spacer(modifier = Modifier.size(8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = { showInformation = true }) { // todo: add info onClick lambda
            Icon(Icons.Outlined.Info,
                contentDescription = "Information",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (showInformation) {
        InformationModal(onDismiss = { showInformation = false}, screen = title)
    }
}
