package com.cs407.fitfolio.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.modals.InformationModal
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular

@Composable
fun SimpleHeader(title: String, modifier: Modifier = Modifier) {
    var showInformation by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = title,
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontSize = 30.sp
        )

        Box(
            modifier = Modifier
                .clickable(
                    onClick = { showInformation = true }
                )
                .padding(top = 10.dp, start = 10.dp, bottom = 15.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.info),
                contentDescription = "Information",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (showInformation) {
        InformationModal(onDismiss = { showInformation = false }, screen = title)
    }
}