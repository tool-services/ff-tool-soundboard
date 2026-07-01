package com.fftool.soundboard.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

class BubbleOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var bubbleView: View? = null
    private var params: WindowManager.LayoutParams? = null

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false
    private var panelVisible = false

    private var displayWidth = 0
    private var displayHeight = 0

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_TOGGLE_PANEL -> {
                togglePanel()
                return START_STICKY
            }
        }

        startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.buildNotification(this))
        setupOverlay()
        return START_STICKY
    }

    private fun setupOverlay() {
        if (bubbleView != null && bubbleView!!.isAttachedToWindow) return

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        displayWidth = size.x
        displayHeight = size.y

        val inflater = LayoutInflater.from(this)
        bubbleView = inflater.inflate(R.layout.bubble_overlay, null)

        val density = resources.displayMetrics.density
        val bubbleSize = (56 * density).toInt()

        params = WindowManager.LayoutParams(
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
            onTouch(event)
            true
        }

        bubbleView?.setOnClickListener {
            if (!isDragging) {
                togglePanel()
            }
        }

        try {
            bubbleView?.let { windowManager.addView(it, params) }
        } catch (e: Exception) {
            stopSelf()
        }
    }

    private fun onTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = params!!.x
                initialY = params!!.y
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
                params!!.x = initialX + dx
                params!!.y = initialY + dy
                bubbleView?.let { windowManager.updateViewLayout(it, params) }
            }
            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    snapToEdge()
                }
                isDragging = false
            }
        }
    }

    private fun snapToEdge() {
        val bubbleWidth = params!!.width
        val centerX = params!!.x + bubbleWidth / 2

        params!!.x = if (centerX < displayWidth / 2) {
            0
        } else {
            displayWidth - bubbleWidth
        }
        params!!.y = params!!.y.coerceIn(0, displayHeight - params!!.height)

        bubbleView?.let { windowManager.updateViewLayout(it, params) }
    }

    private fun togglePanel() {
        panelVisible = !panelVisible
    }

    override fun onDestroy() {
        if (bubbleView != null && bubbleView!!.isAttachedToWindow) {
            try {
                windowManager.removeView(bubbleView)
            } catch (_: Exception) {}
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_STOP = "com.fftool.soundboard.STOP"
        const val ACTION_TOGGLE_PANEL = "com.fftool.soundboard.TOGGLE_PANEL"
    }
}
