package com.fftool.soundboard.ui.compose

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.HideSource
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fftool.soundboard.data.SoundRepository
import com.fftool.soundboard.data.db.SoundEntity
import com.fftool.soundboard.data.preferences.SecurePreferences
import com.fftool.soundboard.service.SoundPlayer
import com.fftool.soundboard.ui.components.SoundBoxTile
import com.fftool.soundboard.ui.theme.AccentPrimary
import com.fftool.soundboard.ui.theme.BackgroundSecondary
import com.fftool.soundboard.ui.theme.BorderSubtle
import com.fftool.soundboard.ui.theme.SurfaceElevated
import com.fftool.soundboard.ui.theme.TextPrimary
import com.fftool.soundboard.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun FloatingPanelContent(
    soundPlayer: SoundPlayer,
    showHideButton: Boolean = false,
    onCollapseToBubble: () -> Unit,
    onStopService: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { SecurePreferences(context) }
    val repo = remember { SoundRepository(context) }
    val sounds by repo.allSounds.collectAsState(initial = emptyList())
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    var gridSize by remember { mutableStateOf(IntSize.Zero) }

    Row(
        modifier = Modifier
            .width(320.dp)
            .height(420.dp)
            .background(BackgroundSecondary, RoundedCornerShape(20.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
    ) {
        // Left edge: vertical social icons
        Column(
            modifier = Modifier
                .width(44.dp)
                .fillMaxHeight()
                .background(BackgroundSecondary, RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                .border(1.dp, BorderSubtle, RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
        ) {
            SocialIconSmall(
                icon = Icons.Outlined.SmartToy,
                label = "TikTok",
                size = 32,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(prefs.tiktokUrl))
                    context.startActivity(intent)
                }
            )
            SocialIconSmall(
                icon = Icons.Outlined.MusicNote,
                label = "WA",
                size = 32,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${prefs.whatsappNumber}"))
                    context.startActivity(intent)
                }
            )
            SocialIconSmall(
                icon = Icons.Outlined.VideoLibrary,
                label = "YT",
                size = 32,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(prefs.youtubeUrl))
                    context.startActivity(intent)
                }
            )
            SocialIconSmall(
                icon = Icons.Outlined.Language,
                label = "Web",
                size = 32,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(prefs.websiteUrl))
                    context.startActivity(intent)
                }
            )
        }

        // Main content area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 6.dp, end = 0.dp)
        ) {
            // Header: logo + title + optional Hide button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(AccentPrimary)
                        .clickable(onClick = onCollapseToBubble),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Headphones,
                        contentDescription = "Collapse to bubble",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FF Tool",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                if (showHideButton) {
                    Icon(
                        imageVector = Icons.Outlined.HideSource,
                        contentDescription = "Hide",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onCollapseToBubble)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle))

            // Sound grid
            val displaySounds = (1..24).map { boxNum ->
                sounds.find { it.boxNumber == boxNum }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { gridSize = it }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    state = gridState
                ) {
                    items(displaySounds) { sound ->
                        SoundBoxTile(
                            boxNumber = sound?.boxNumber ?: (displaySounds.indexOf(sound) + 1),
                            displayName = sound?.displayName,
                            imagePath = sound?.imagePath,
                            isPlaying = sound != null && soundPlayer.isPlaying(sound.filePath),
                            onClick = {
                                if (sound != null) {
                                    soundPlayer.play(sound.filePath)
                                }
                            }
                        )
                    }
                }

                // Right scrollbar (fast-scroll handle)
                val firstVisible = gridState.firstVisibleItemIndex
                val totalItems = displaySounds.size.coerceAtLeast(1)
                val scrollbarHeight = (gridSize.height * 0.15f).coerceAtLeast(20f)
                val scrollOffset = (gridSize.height - scrollbarHeight) * (firstVisible.toFloat() / totalItems)

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(6.dp)
                        .height(scrollbarHeight.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(AccentPrimary.copy(alpha = 0.5f))
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { _, dragAmount ->
                                val totalScroll = gridState.layoutInfo.totalItemsCount * 60
                                scope.launch {
                                    gridState.scrollBy(dragAmount * 3)
                                }
                            }
                        }
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))
    }
}

@Composable
private fun SocialIconSmall(
    icon: ImageVector,
    label: String,
    size: Int = 32,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(SurfaceElevated)
                .border(1.dp, BorderSubtle, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextPrimary,
                modifier = Modifier.size((size * 0.55f).dp)
            )
        }
    }
}
