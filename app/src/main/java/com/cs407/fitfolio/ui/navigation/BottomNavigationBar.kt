package com.cs407.fitfolio.ui.navigation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cs407.fitfolio.R

// UI and navigation for the bottom navigation bar
@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomAppBar {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { navController.navigate("outfits") }) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Outfits")
                    }
                    Text(stringResource(R.string.outfits))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { navController.navigate("calendar") }) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Calendar")
                    }
                    Text(stringResource(R.string.calendar))
                }
            }
            Spacer(modifier = Modifier.size(56.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { navController.navigate("add") }) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Add")
                    }
                    Text(stringResource(R.string.add))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { navController.navigate("closet") }) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Closet")
                    }
                    Text(stringResource(R.string.closet))
                }
            }
        }
    }
}





