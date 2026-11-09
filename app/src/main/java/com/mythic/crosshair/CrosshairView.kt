package com.mythic.crosshair
import android.content.Context
import android.graphics.*
import android.view.View
import java.io.File

class CrosshairView(c: Context) : View(c) {
    companion object {
        val NAMES = listOf("Mythic Inferno", "Mythic Void", "Mythic Gold", "Mythic Frost", "Mythic Toxic", "Custom PNG")
    }
    var style = 0
    var custom: Bitmap? = null
    private val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeCap = Paint.Cap.ROUND }

    fun load(prefs: Prefs) {
        style = prefs.style
        alpha = prefs.opacity / 100f
        val f = File(context.filesDir, "custom.png")
        custom = if (f.exists()) BitmapFactory.decodeFile(f.path) else null
        invalidate()
    }

    private fun ticks(cv: Canvas, c: Float, r1: Float, r2: Float) {
        for (i in 0..3) { cv.save(); cv.rotate(i * 90f, c, c); cv.drawLine(c, c - r1, c, c - r2, p); cv.restore() }
    }

    override fun onDraw(cv: Canvas) {
        val s = minOf(width, height).toFloat(); val c = s / 2
        p.style = Paint.Style.STROKE; p.strokeWidth = s * 0.03f
        when (style) {
            0 -> { // Inferno: ring, ticks, ember dot
                p.color = 0xFFFF6A00.toInt(); cv.drawCircle(c, c, s * .28f, p); ticks(cv, c, s * .34f, s * .48f)
                p.style = Paint.Style.FILL; p.color = 0xFFFF1E1E.toInt(); cv.drawCircle(c, c, s * .04f, p)
            }
            1 -> { // Void: diamond + dot
                p.color = 0xFFB026FF.toInt()
                val d = Path().apply { moveTo(c, c - s * .3f); lineTo(c + s * .3f, c); lineTo(c, c + s * .3f); lineTo(c - s * .3f, c); close() }
                cv.drawPath(d, p); ticks(cv, c, s * .36f, s * .48f)
                p.style = Paint.Style.FILL; cv.drawCircle(c, c, s * .035f, p)
            }
            2 -> { // Gold: gapped cross + small circle
                p.color = 0xFFFFC400.toInt(); ticks(cv, c, s * .1f, s * .45f); cv.drawCircle(c, c, s * .06f, p)
            }
            3 -> { // Frost: chevron ticks
                p.color = 0xFF00E5FF.toInt()
                for (i in 0..3) { cv.save(); cv.rotate(i * 90f + 45f, c, c)
                    cv.drawLine(c - s * .08f, c - s * .3f, c, c - s * .18f, p); cv.drawLine(c + s * .08f, c - s * .3f, c, c - s * .18f, p); cv.restore() }
                p.style = Paint.Style.FILL; cv.drawCircle(c, c, s * .03f, p)
            }
            4 -> { // Toxic: T-shape + dot
                p.color = 0xFF39FF14.toInt()
                cv.drawLine(c - s * .4f, c, c - s * .1f, c, p); cv.drawLine(c + s * .1f, c, c + s * .4f, c, p); cv.drawLine(c, c + s * .1f, c, c + s * .4f, p)
                p.style = Paint.Style.FILL; cv.drawCircle(c, c, s * .035f, p)
            }
            else -> custom?.let { cv.drawBitmap(it, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), null) }
        }
    }
}
