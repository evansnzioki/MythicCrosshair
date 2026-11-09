package com.mythic.crosshair
import android.content.Context
class Prefs(c: Context) {
    private val sp = c.getSharedPreferences("cfg", Context.MODE_PRIVATE)
    var style: Int get() = sp.getInt("style", 0); set(v) = sp.edit().putInt("style", v).apply()
    var size: Int get() = sp.getInt("size", 60); set(v) = sp.edit().putInt("size", v).apply()      // dp
    var opacity: Int get() = sp.getInt("opacity", 90); set(v) = sp.edit().putInt("opacity", v).apply() // 10..100
    var auto: Boolean get() = sp.getBoolean("auto", true); set(v) = sp.edit().putBoolean("auto", v).apply()
}
