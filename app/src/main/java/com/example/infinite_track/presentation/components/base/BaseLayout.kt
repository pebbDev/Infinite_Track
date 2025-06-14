package com.example.infinite_track.presentation.components.base

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.infinite_track.presentation.theme.Blue_500
import com.example.infinite_track.presentation.theme.Blue_Accent_500
import com.example.infinite_track.presentation.theme.Infinite_TrackTheme
import com.example.infinite_track.presentation.theme.Orange_500

@Composable
fun BaseLayout() {
    val animationSpec = remember {
        infiniteRepeatable(
            animation = keyframes {
                durationMillis = 10000
                0f at 0
                1f at 0 // Half the duration for reaching enlargedSize
                0f at 0 // Back to defaultSize
            },
            repeatMode = RepeatMode.Restart
        )
    }

    val sizeTransition = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        sizeTransition.animateTo(
            targetValue = 1f,
            animationSpec = animationSpec
        )
    }

    val size = sizeTransition.value

    val rotationAnimation = rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    ).value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .blur(200.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 250.dp)
                .rotate(rotationAnimation)
        ) {
            CircleShapeBox(
                modifier = Modifier.offset(x = (-150).dp, y = (-300).dp),
                size = size,
                color = Blue_500
            )
            CircleShapeBox(
                modifier = Modifier.offset(x = (150).dp, y = (0).dp),
                size = size,
                color = Orange_500
            )
            CircleShapeBox(
                modifier = Modifier.offset(x = (-150).dp, y = 300.dp),
                size = size,
                color = Blue_Accent_500
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0x80FFFFFF))
    )
}

@Composable
fun CircleShapeBox(modifier: Modifier, size: Float, color: Color) {
    val defaultSize = 425.dp
    val enlargedSize = 450.dp

    Box(
        modifier = modifier
            .clip(CircleShape)
            .size((defaultSize + (enlargedSize - defaultSize) * size))
            .background(color)
    ) {}
}

@Preview(showSystemUi = true)
@Composable
fun BaseLayoutPreview() {
    Infinite_TrackTheme {
        BaseLayout()
    }
}