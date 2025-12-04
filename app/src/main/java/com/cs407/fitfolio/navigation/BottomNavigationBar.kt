package com.cs407.fitfolio.navigation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.theme.PeachPuff
import com.cs407.fitfolio.ui.theme.RustBrown
import com.cs407.fitfolio.ui.theme.TrueBlack

// UI and navigation for the bottom navigation bar
@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomAppBar(
        containerColor = PeachPuff,
        contentColor = RustBrown
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { navController.navigate("outfits") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.shirt_nav),
                            contentDescription = "Outfits",
                            tint = RustBrown,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                    Text(stringResource(R.string.outfits), fontSize = 15.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { navController.navigate("calendar") }) {
                        Icon(
                            painter = painterResource(R.drawable.schedule),
                            contentDescription = "Calendar",
                            tint = RustBrown,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                    Text(stringResource(R.string.calendar), fontSize = 15.sp)
                }
            }
            Spacer(modifier = Modifier.size(56.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { navController.navigate("add") }) {
                        Icon(
                            Icons.Outlined.AddCircle,
                            contentDescription = "Add",
                            tint = RustBrown,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                    Text(stringResource(R.string.add), fontSize = 15.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { navController.navigate("closet") }) {
                        Icon(
                            painter = painterResource(R.drawable.closet),
                            contentDescription = "Closet",
                            tint = RustBrown,
                            modifier = Modifier.size(35 .dp)
                        )
                    }
                    Text(stringResource(R.string.closet), fontSize = 15.sp)
                }
            }
        }
    }
}





