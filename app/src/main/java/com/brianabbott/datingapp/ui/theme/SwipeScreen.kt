package com.brianabbott.datingapp.ui.theme

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brianabbott.datingapp.swipecards.Direction
import com.brianabbott.datingapp.swipecards.MatchProfile
import com.brianabbott.datingapp.swipecards.profiles
import com.brianabbott.datingapp.swipecards.rememberSwipeableCardState
import com.brianabbott.datingapp.swipecards.swipableCard
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun SwipeCards() {
    TransparentSystemBars()
    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(
                Brush.Companion.verticalGradient(
                    listOf(
                        Color(0xfff68084),
                        Color(0xffa6c0fe),
                    )
                )
            )
//                        .systemBarsPadding()
    ) {
        Box {
            val states = profiles.reversed()
                .map { it to rememberSwipeableCardState() }
            var hint by remember {
                mutableStateOf("Swipe a card or press a button below")
            }

            Hint(hint)

            val scope = rememberCoroutineScope()
            Box(
                Modifier.Companion
                    .padding(24.dp)
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .align(Alignment.Companion.Center)
            ) {
                states.forEach { (matchProfile, state) ->
                    if (state.swipedDirection == null) {
                        ProfileCard(
                            modifier = Modifier.Companion
                                .fillMaxSize()
                                .swipableCard(
                                    state = state,
                                    blockedDirections = listOf(Direction.Down),
                                    onSwiped = {
                                        // swipes are handled by the LaunchedEffect
                                        // so that we track button clicks & swipes
                                        // from the same place
                                    },
                                    onSwipeCancel = {
                                        Log.d("Swipeable-Card", "Cancelled swipe")
                                        hint = "You canceled the swipe"
                                    }
                                ),
                            matchProfile = matchProfile
                        )
                    }
                    LaunchedEffect(matchProfile, state.swipedDirection) {
                        if (state.swipedDirection != null) {
                            hint = "You swiped ${stringFrom(state.swipedDirection!!)}"
                        }
                    }
                }
            }
            Row(
                Modifier.Companion
                    .align(Alignment.Companion.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircleButton(
                    onClick = {
                        scope.launch {
                            val last = states.reversed()
                                .firstOrNull {
                                    it.second.offset.value == Offset(0f, 0f)
                                }?.second
                            last?.swipe(Direction.Left)
                        }
                    },
                    icon = Icons.Rounded.Close
                )
                CircleButton(
                    onClick = {
                        scope.launch {
                            val last = states.reversed()
                                .firstOrNull {
                                    it.second.offset.value == Offset(0f, 0f)
                                }?.second

                            last?.swipe(Direction.Right)
                        }
                    },
                    icon = Icons.Rounded.Favorite
                )
            }
        }
    }
}

@Composable
private fun CircleButton(
    onClick: () -> Unit,
    icon: ImageVector,
) {
    IconButton(
        modifier = Modifier.Companion
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .size(56.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
        onClick = onClick
    ) {
        Icon(
            icon, null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun ProfileCard(
    modifier: Modifier,
    matchProfile: MatchProfile,
) {
    Card(modifier) {
        Box {
            Image(
                contentScale = ContentScale.Companion.Crop,
                modifier = Modifier.Companion.fillMaxSize(),
                painter = painterResource(matchProfile.drawableResId),
                contentDescription = null
            )
            Scrim(Modifier.Companion.align(Alignment.Companion.BottomCenter))
            Column(Modifier.Companion.align(Alignment.Companion.BottomStart)) {
                Text(
                    text = matchProfile.name,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Companion.Medium,
                    modifier = Modifier.Companion.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun Hint(text: String) {
    Box(
        contentAlignment = Alignment.Companion.Center,
        modifier = Modifier.Companion
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Companion.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Companion.Center
        )
    }
}

@Composable
private fun TransparentSystemBars() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = false

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(
            color = Color.Companion.Transparent,
            darkIcons = useDarkIcons,
            isNavigationBarContrastEnforced = false
        )
        onDispose {}
    }
}

private fun stringFrom(direction: Direction): String {
    return when (direction) {
        Direction.Left -> "Left 👈"
        Direction.Right -> "Right 👉"
        Direction.Up -> "Up 👆"
        Direction.Down -> "Down 👇"
    }
}


@Composable
fun Scrim(modifier: Modifier = Modifier.Companion) {
    Box(
        modifier
            .background(
                Brush.Companion.verticalGradient(
                    listOf(
                        Color.Companion.Transparent,
                        Color.Companion.Black
                    )
                )
            )
            .height(180.dp)
            .fillMaxWidth()
    )
}