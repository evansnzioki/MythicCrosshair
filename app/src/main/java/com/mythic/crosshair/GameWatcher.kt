package com.mythic.crosshair
import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/** Detects when CODM comes to the foreground and starts/stops the overlay automatically. */
class GameWatcher : AccessibilityService() {
    private val codm = setOf("com.activision.callofduty.shooter", "com.garena.game.codm", "com.tencent.tmgp.cod")
    private val ignore = setOf("com.android.systemui", "com.mythic.crosshair")
    override fun onAccessibilityEvent(e: AccessibilityEvent) {
        val pkg = e.packageName?.toString() ?: return
        if (!Prefs(this).auto) return
        if (pkg in codm) { if (!OverlayService.running) OverlayService.start(this) }
        else if (pkg !in ignore && OverlayService.running) OverlayService.stop(this)
    }
    override fun onInterrupt() {}
}
