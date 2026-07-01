package com.fftool.soundboard.ui.components

import android.graphics.BitmapFactory
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fftool.soundboard.ui.theme.AccentSecondary
import com.fftool.soundboard.ui.theme.BorderSubtle
import com.fftool.soundboard.ui.theme.SurfaceElevated
import com.fftool.soundboard.ui.theme.TextDisabled
import com.fftool.soundboard.ui.theme.TextPrimary
import com.fftool.soundboard.ui.theme.TextSecondary
import java.io.File

@Composable
fun SoundBoxTile(
    boxNumber: Int,
    displayName: String?,
    imagePath: String? = null,
    isLoading: Boolean = false,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmpty = displayName == null

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPlaying) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isPlaying) 0.8f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val borderColor = when {
        isPlaying -> AccentSecondary.copy(alpha = glowAlpha)
        isEmpty -> TextDisabled.copy(alpha = 0.5f)
        else -> BorderSubtle
    }

    val borderWidth = if (isPlaying) 2.dp else 1.dp

    val imageBitmap = remember(imagePath) {
        if (imagePath != null) {
            val file = File(imagePath)
            if (file.exists()) {
                BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
            } else null
        } else null
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(if (isPlaying) pulseScale else 1f)
            .shadow(
                elevation = if (isPlaying) 8.dp else 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = if (isPlaying) AccentSecondary.copy(alpha = glowAlpha * 0.3f)
                    else Color.Black.copy(alpha = 0.2f),
                spotColor = if (isPlaying) AccentSecondary.copy(alpha = glowAlpha * 0.3f)
                    else Color.Black.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isPlaying) SurfaceElevated.copy(alpha = 0.9f) else SurfaceElevated
            )
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isEmpty) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Upload Sound",
                    tint = TextDisabled,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Upload Sound",
                    color = TextDisabled,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = displayName,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
                // Label overlay at bottom
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName.orEmpty(),
                        color = TextPrimary,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MusicNote,
                        contentDescription = null,
                        tint = if (isPlaying) AccentSecondary else TextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = "#$boxNumber",
                        color = TextDisabled,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = displayName.orEmpty(),
                        color = if (isPlaying) AccentSecondary else TextPrimary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
