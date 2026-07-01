package com.fftool.soundboard.service

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class SoundPlayer(context: Context) {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    var currentFilePath: String? = null
        private set

    private var listeners = mutableListOf<() -> Unit>()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    currentFilePath = null
                    notifyListeners()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                currentFilePath = null
                notifyListeners()
            }
        })
    }

    fun play(filePath: String) {
        if (currentFilePath == filePath && exoPlayer.isPlaying) {
            stop()
            return
        }
        currentFilePath = filePath
        val mediaItem = MediaItem.fromUri(filePath)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        notifyListeners()
    }

    fun stop() {
        exoPlayer.stop()
        currentFilePath = null
        notifyListeners()
    }

    fun isPlaying(filePath: String?): Boolean {
        return currentFilePath == filePath && exoPlayer.isPlaying
    }

    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it() }
    }

    fun release() {
        exoPlayer.release()
        listeners.clear()
    }
}
