package com.cs407.fitfolio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.fitfolio.R
import com.cs407.fitfolio.ui.components.SimpleHeader
import com.cs407.fitfolio.ui.components.WeatherCard
import com.cs407.fitfolio.ui.components.WeatherCarousel
import com.cs407.fitfolio.ui.components.WeatherDataChip
import com.cs407.fitfolio.ui.viewModels.ClosetState
import com.cs407.fitfolio.ui.viewModels.ClosetViewModel
import com.cs407.fitfolio.ui.viewModels.WeatherViewModel
import kotlinx.coroutines.launch

@Composable
fun MyWardrobeScreen(
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToAddScreen: () -> Unit,
    onNavigateToClosetScreen: () -> Unit,
    onNavigateToSignInScreen: () -> Unit,
    closetViewModel: ClosetViewModel,
    weatherViewModel: WeatherViewModel
) {
    val closetState by closetViewModel.closetState.collectAsStateWithLifecycle()
    //for weather
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        //bacground sillouette
        Image(
            painter = painterResource(R.drawable.silouette),
            contentDescription = "Silhouette background",
            modifier = Modifier
                .align(Alignment.Center)
                .size(550.dp)
                .alpha(0.12f),       // make semi-transparent
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                //TODO: Title of Page and Weather
                SimpleHeader("My Wardrobe")
                Spacer(modifier = Modifier.weight(1f))
                WeatherDataChip(
                    weatherData = weatherState.weatherData
                )
            }
            //Scroller for head pieces
            ClothingScroll(closetState, closetViewModel)
            //Scroller for tops/shirts
            ClothingScroll(closetState, closetViewModel)
            //Scroller for bottoms/pants
            ClothingScroll(closetState, closetViewModel)
            //Scroller for shoes
            ClothingScroll(closetState, closetViewModel)
            Spacer(modifier = Modifier.size(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp), // space between icons
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
            ) {
                //adds outfit to the outfits page
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    //change this to go to the pull up page
                    IconButton(onClick = { onNavigateToOutfitsScreen() }) {
                        Icon(
                            painter = painterResource(R.drawable.add),
                            contentDescription = "add outfit",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                //ensures the combo is never seen again
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { closetViewModel.shuffleItems() }) {
                        Icon(
                            painter = painterResource(R.drawable.minus),
                            contentDescription = "remove combination",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                // shuffle button
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { closetViewModel.shuffleItems() }) {
                        Icon(
                            painter = painterResource(R.drawable.shuffle),
                            contentDescription = "shuffle",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                //TODO: Dress me icon
                Button(
                    //change this when we figure out how we are implementing shuffleItems list
                    onClick = { closetViewModel.shuffleItems() },
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Dress Me!")
                }
            }
        }
    }
}
@Composable
fun ClothingScroll(closetState: ClosetState, closetViewModel: ClosetViewModel) {
    val itemsCount = 30
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = itemsCount * 1000)
    val scope = rememberCoroutineScope()
    val itemSize = 150.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val lazyRowWidth = screenWidth - 80.dp - 16.dp
    val horizontalPadding = (lazyRowWidth - itemSize) / 2

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)    ) {
        IconButton(
            onClick = {
                scope.launch {
                    val prevIndex = (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                    listState.animateScrollToItem(prevIndex)
                }
            },
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.25f), shape = RectangleShape)
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous", tint = Color.White)
        }

            LazyRow(
                state = listState,
                modifier = Modifier.weight(1f).height(itemSize),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = itemSize / 2),
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
            ) {
                items(Int.MAX_VALUE) { index ->
                    val realIndex = index % itemsCount
                    Box(
                        modifier = Modifier
                            .size(itemSize)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color(0xFFE0E0E0).copy(alpha = .2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Item $realIndex")
                    }

                }
            }
        // Right arrow
        IconButton(
            onClick = {
                scope.launch {
                    val nextIndex = listState.firstVisibleItemIndex + 1
                    listState.animateScrollToItem(nextIndex)
                }
            },
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.25f), shape = RectangleShape)
        ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next", tint = Color.White)
        }
    }
}