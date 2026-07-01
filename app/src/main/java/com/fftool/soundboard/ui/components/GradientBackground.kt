package com.fftool.soundboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFF0B0E14),
        Color(0xFF0D1120),
        Color(0xFF0B0E14)
    ),
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(colors)
        )
    ) {
        content()
    }
}
