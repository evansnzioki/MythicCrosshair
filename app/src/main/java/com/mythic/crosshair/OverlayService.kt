package com.mythic.crosshair
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager

class OverlayService : Service() {
    companion object { var running = false
        fun start(c: Context) = c.startForegroundService(Intent(c, OverlayService::class.java))
        fun stop(c: Context) { c.stopService(Intent(c, OverlayService::class.java)) } }
    private var view: CrosshairView? = null
    private val wm get() = getSystemService(WINDOW_SERVICE) as WindowManager

    override fun onBind(i: Intent?): IBinder? = null
    override fun onStartCommand(i: Intent?, f: Int, id: Int): Int {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel("x", "Crosshair", NotificationManager.IMPORTANCE_MIN))
        startForeground(1, Notification.Builder(this, "x").setContentTitle("Crosshair active").setSmallIcon(android.R.drawable.ic_menu_view).build())
        show(); running = true
        return START_STICKY
    }
    private fun show() {
        view?.let { wm.removeView(it) }
        val prefs = Prefs(this)
        val px = (prefs.size * resources.displayMetrics.density).toInt()
        val v = CrosshairView(this).also { it.load(prefs) }
        val lp = WindowManager.LayoutParams(px, px, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT).apply { gravity = Gravity.CENTER }
        wm.addView(v, lp); view = v
    }
    override fun onDestroy() { view?.let { wm.removeView(it) }; view = null; running = false; super.onDestroy() }
}
