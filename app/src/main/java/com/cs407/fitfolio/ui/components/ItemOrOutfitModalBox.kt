package com.cs407.fitfolio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.R

@Composable
fun ItemOrOutfitModalBox(
    title: String,
    photo: Int,
    onNavigateToCalendarScreen: () -> Unit
) {
    // Track whether item is editable
    var isEditing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceEvenly, // fix horizontal layout
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Calendar icon button
            IconButton(
                onClick = {onNavigateToCalendarScreen}
            ) {
                Icon(
                    painter = painterResource(R.drawable.schedule),
                    contentDescription = "Calendar",
                    modifier = Modifier.size(36.dp)
                )
            }

            // Title
            Text(text = title, style = MaterialTheme.typography.titleLarge)

            // Edit icon button
            IconButton(
                onClick = { isEditing = !isEditing }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Calendar",
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.shirt),
            contentDescription = "Shirt",
            modifier = Modifier.align(Alignment.Center)
        )
        Row(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly, // fix horizontal layout
            verticalAlignment = Alignment.Bottom // fix vertical alignment
        ) {
            // Delete icon button
            IconButton(
                onClick = {} // TODO: DELETE LOGIC??
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Calendar",
                    modifier = Modifier.size(36.dp)
                )
            }

            // Edit icon button
            IconButton(
                onClick = { isEditing = !isEditing }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Favorite, // if/else logic for outlined vs filled
                    contentDescription = "Favorite",
                    modifier = Modifier.size(36.dp)
                )
            }
        }

    }
}