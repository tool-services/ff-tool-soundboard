package com.fftool.soundboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fftool.soundboard.ui.components.GradientBackground
import com.fftool.soundboard.ui.components.PremiumButton
import com.fftool.soundboard.ui.components.PremiumCard
import com.fftool.soundboard.ui.components.SoundBoxTile
import com.fftool.soundboard.ui.theme.BackgroundPrimary
import com.fftool.soundboard.ui.theme.TextPrimary
import com.fftool.soundboard.ui.theme.TextSecondary

@Composable
fun ComponentPreviewScreen() {
    var playingBox by remember { mutableStateOf<Int?>(null) }

    GradientBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Design System Preview",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Phase 2 - Component Library",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // PremiumButton variants
            Text("Premium Buttons", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            PremiumButton(text = "Primary Action", onClick = {})
            Spacer(modifier = Modifier.height(12.dp))
            PremiumButton(
                text = "Disabled Button",
                onClick = {},
                enabled = false
            )
            Spacer(modifier = Modifier.height(12.dp))
            PremiumButton(
                text = "Loading State",
                onClick = {},
                isLoading = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // PremiumCard
            Text("Premium Card", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Card Content Here",
                    color = TextPrimary,
                    fontSize = 16.sp
                )
                Text(
                    "This is a premium card with glassmorphic styling.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // SoundBoxTile grid preview
            Text("Sound Box Tiles", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SoundBoxTile(
                    boxNumber = 1,
                    displayName = "Sound One",
                    isPlaying = playingBox == 1,
                    onClick = { playingBox = if (playingBox == 1) null else 1 },
                    modifier = Modifier.weight(1f)
                )
                SoundBoxTile(
                    boxNumber = 2,
                    displayName = "Sound Two Longer Name",
                    onClick = { playingBox = if (playingBox == 2) null else 2 },
                    modifier = Modifier.weight(1f),
                    isPlaying = playingBox == 2
                )
                SoundBoxTile(
                    boxNumber = 3,
                    displayName = null,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Gradient background preview
            Text("Gradient Background", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(
                                com.fftool.soundboard.ui.theme.AccentGradientStart,
                                com.fftool.soundboard.ui.theme.AccentGradientEnd
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Gradient Preview",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
