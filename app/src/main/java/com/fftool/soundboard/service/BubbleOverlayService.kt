package com.fftool.soundboard.service

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import com.fftool.soundboard.R
import com.fftool.soundboard.ui.compose.FloatingPanelContent
import com.fftool.soundboard.ui.theme.FFToolTheme

class BubbleOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var soundPlayer: SoundPlayer

    private var bubbleView: View? = null
    private var bubbleParams: WindowManager.LayoutParams? = null
    private var panelView: ComposeView? = null
    private var panelParams: WindowManager.LayoutParams? = null

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false
    private var panelVisible = false

    private var displayWidth = 0
    private var displayHeight = 0
    private var bubbleSize = 0
    private var isLandscape = false

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        soundPlayer = SoundPlayer(this)
        updateDisplayMetrics()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_HIDE_PANEL -> {
                hidePanel()
                ensureBubble()
                return START_STICKY
            }
        }

        startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.buildNotification(this))
        setupBubble()
        return START_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateDisplayMetrics()

        // Clamp bubble position to new screen bounds
        bubbleParams?.let { params ->
            params.x = params.x.coerceIn(0, displayWidth - bubbleSize)
            params.y = params.y.coerceIn(0, displayHeight - bubbleSize)
            bubbleView?.let { windowManager.updateViewLayout(it, params) }
        }

        // Re-layout panel if visible
        if (panelVisible) {
            val pw = (320 * resources.displayMetrics.density).toInt()
            val ph = (420 * resources.displayMetrics.density).toInt()
            panelParams?.let { params ->
                params.width = pw
                params.height = ph
                params.x = params.x.coerceIn(0, displayWidth - pw)
                params.y = params.y.coerceIn(0, displayHeight - ph)
                panelView?.let { windowManager.updateViewLayout(it, params) }
            }

            // Recreate panel with rotation-adjusted Hide button
            setupPanel()
        }
    }

    private fun updateDisplayMetrics() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        displayWidth = size.x
        displayHeight = size.y
        val density = resources.displayMetrics.density
        bubbleSize = (56 * density).toInt()
        isLandscape = displayWidth > displayHeight

        // Recreate panel on rotation with updated params
        if (panelVisible) {
            setupPanel()
        }
    }

    private fun ensureBubble() {
        if (bubbleView == null || !bubbleView!!.isAttachedToWindow) {
            setupBubble()
        }
    }

    private fun setupBubble() {
        if (bubbleView != null && bubbleView!!.isAttachedToWindow) return

        val inflater = LayoutInflater.from(this)
        bubbleView = inflater.inflate(R.layout.bubble_overlay, null)

        bubbleParams = WindowManager.LayoutParams(
            bubbleSize,
            bubbleSize,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = displayWidth / 2 - bubbleSize / 2
            y = displayHeight / 3
        }

        bubbleView?.setOnTouchListener { _, event ->
            onBubbleTouch(event)
        }

        bubbleView?.setOnClickListener {
            if (!isDragging) {
                togglePanel()
            }
        }

        try {
            bubbleView?.let { windowManager.addView(it, bubbleParams) }
        } catch (e: Exception) {
            stopSelf()
        }
    }

    private fun setupPanel() {
        if (panelView != null && panelView!!.isAttachedToWindow) {
            try { windowManager.removeView(panelView) } catch (_: Exception) {}
            panelView = null
        }

        val panelWidth = (320 * resources.displayMetrics.density).toInt()
        val panelHeight = (420 * resources.displayMetrics.density).toInt()

        panelView = ComposeView(this).apply {
            setContent {
                FFToolTheme {
                    FloatingPanelContent(
                        soundPlayer = soundPlayer,
                        showHideButton = isLandscape,
                        onCollapseToBubble = { hidePanel(); ensureBubble() },
                        onStopService = { stopSelf() }
                    )
                }
            }
        }

        panelParams = WindowManager.LayoutParams(
            panelWidth,
            panelHeight,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (displayWidth - panelWidth) / 2
            y = (displayHeight - panelHeight) / 4
        }

        try {
            panelView?.let { windowManager.addView(it, panelParams) }
        } catch (e: Exception) {
            // Fallback
        }
    }

    private fun onBubbleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = bubbleParams!!.x
                initialY = bubbleParams!!.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.rawX - initialTouchX).toInt()
                val dy = (event.rawY - initialTouchY).toInt()
                if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                    isDragging = true
                }
                bubbleParams!!.x = (initialX + dx).coerceIn(0, displayWidth - bubbleSize)
                bubbleParams!!.y = (initialY + dy).coerceIn(0, displayHeight - bubbleSize)
                bubbleView?.let { windowManager.updateViewLayout(it, bubbleParams) }
            }
            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    snapToEdge()
                }
                isDragging = false
            }
        }
        return true
    }

    private fun snapToEdge() {
        val centerX = bubbleParams!!.x + bubbleSize / 2
        bubbleParams!!.x = if (centerX < displayWidth / 2) 0 else displayWidth - bubbleSize
        bubbleParams!!.y = bubbleParams!!.y.coerceIn(0, displayHeight - bubbleSize)
        bubbleView?.let { windowManager.updateViewLayout(it, bubbleParams) }
    }

    private fun togglePanel() {
        panelVisible = !panelVisible
        if (panelVisible) {
            setupPanel()
        } else {
            hidePanel()
        }
    }

    private fun hidePanel() {
        panelVisible = false
        panelView?.let {
            if (it.isAttachedToWindow) {
                try { windowManager.removeView(it) } catch (_: Exception) {}
            }
        }
        panelView = null
    }

    override fun onDestroy() {
        hidePanel()
        if (bubbleView != null && bubbleView!!.isAttachedToWindow) {
            try { windowManager.removeView(bubbleView) } catch (_: Exception) {}
        }
        soundPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_STOP = "com.fftool.soundboard.STOP"
        const val ACTION_HIDE_PANEL = "com.fftool.soundboard.HIDE_PANEL"
    }
}
