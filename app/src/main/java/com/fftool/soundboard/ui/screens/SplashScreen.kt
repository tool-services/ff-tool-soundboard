package com.fftool.soundboard.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fftool.soundboard.ui.theme.BackgroundPrimary
import com.fftool.soundboard.ui.theme.TextPrimary
import com.fftool.soundboard.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    val logoAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        subtitleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
        delay(500)
        onNavigateToLogin()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "FF",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(logoAlpha.value)
        )
        Text(
            text = "TOOL",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = 12.sp,
            color = com.fftool.soundboard.ui.theme.AccentSecondary,
            modifier = Modifier.alpha(logoAlpha.value)
        )
        Text(
            text = "Premium Soundboard",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(subtitleAlpha.value)
        )
    }
}
