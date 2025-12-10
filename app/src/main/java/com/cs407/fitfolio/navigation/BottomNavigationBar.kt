package com.cs407.fitfolio.navigation
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.ui.theme.LightPeachFuzz
import com.cs407.fitfolio.ui.theme.TrueBlack

// UI and navigation for the bottom navigation bar
@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomAppBar(
        containerColor = LightPeachFuzz,
        contentColor = TrueBlack
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                    onClick = { navController.navigate("outfits") }
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.shirt_nav),
                    contentDescription = "Outfits",
                    tint = TrueBlack,
                    modifier = Modifier.size(33.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = stringResource(R.string.outfits),
                    fontSize = 13.sp,
                    fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                    onClick = { navController.navigate("closet") }                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.closet),
                    contentDescription = "Closet",
                    tint = TrueBlack,
                    modifier = Modifier.size(33 .dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = stringResource(R.string.closet),
                    fontSize = 13.sp,
                    fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))//for fab

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                    onClick = { navController.navigate("add") }
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_nav_thin),
                    contentDescription = "Add",
                    tint = TrueBlack,
                    modifier = Modifier.size(33.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = stringResource(R.string.add),
                    fontSize = 13.sp,
                    fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                    onClick = { navController.navigate("calendar") }
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.schedule),
                    contentDescription = "Calendar",
                    tint = TrueBlack,
                    modifier = Modifier.size(33.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = stringResource(R.string.calendar),
                    fontSize = 13.sp,
                    fontFamily = Kudryashev_Display_Sans_Regular,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}





