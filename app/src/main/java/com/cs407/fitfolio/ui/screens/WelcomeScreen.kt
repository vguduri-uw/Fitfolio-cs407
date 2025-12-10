package com.cs407.fitfolio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cs407.fitfolio.R
import com.cs407.fitfolio.viewModels.UserState
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular

@Composable
fun WelcomeScreen(
    userState: UserState,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // App logo
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Welcome, ${userState.name}!",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Welcome to your digital closet!",
            style = MaterialTheme.typography.titleLarge,
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "FitFolio helps you upload clothing items, organize your closet, and build outfits for every occasion. " +
                    "You can browse your wardrobe via an item carousel, plan looks on the calendar, favorite items and outfits, and more!",
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(12.dp))

        val text = buildAnnotatedString {
            append("Each page has helpful instructions via the information icon ")
            appendInlineContent(id = "information_icon", alternateText = "information icon")
            append(". Be sure to check those out!")
        }

        val inlineContent = mapOf(
            "information_icon" to InlineTextContent(
                Placeholder(
                    width = 18.sp,
                    height = 18.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.info),
                    contentDescription = "icon",
                    modifier = Modifier.fillMaxSize()
                )
            }
        )

        Text(
            text = text,
            inlineContent = inlineContent,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

//        Text(
//            text = "Each page has helpful instructions via the \u2139 information icon. Be sure to check those out!",
//            style = MaterialTheme.typography.bodyMedium,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.padding(horizontal = 32.dp)
//        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Before creating outfits, upload an avatar photo in the Settings page so your outfits appear on your personal avatar!",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = Kudryashev_Display_Sans_Regular,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(Modifier.height(28.dp))

        Button(onClick = onContinue) {
            Text(
                text = "Let’s Get Started ✨",
                fontFamily = Kudryashev_Display_Sans_Regular,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
