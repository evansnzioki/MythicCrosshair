package com.mythic.crosshair
import android.Manifest
import androidx.activity.ComponentActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup.LayoutParams.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var prefs: Prefs
    private lateinit var preview: CrosshairView
    private val picker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        contentResolver.openInputStream(uri)?.use { i -> File(filesDir, "custom.png").outputStream().use { o -> i.copyTo(o) } }
        prefs.style = 5; refresh()
    }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun refresh() { preview.load(prefs); if (OverlayService.running) OverlayService.start(this) }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        prefs = Prefs(this)
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(16), dp(16), dp(16)) }
        fun btn(t: String, a: () -> Unit) = Button(this).apply { text = t; setOnClickListener { a() }; root.addView(this) }
        fun label(t: String) = TextView(this).apply { text = t; setPadding(0, dp(12), 0, 0); root.addView(this) }

        preview = CrosshairView(this).also { root.addView(it, LinearLayout.LayoutParams(dp(prefs.size), dp(prefs.size))) }
        root.setBackgroundColor(0xFF222222.toInt())
        (root.getChildAt(0) as CrosshairView).setBackgroundColor(0xFF444444.toInt())

        label("Crosshair style").setTextColor(0xFFFFFFFF.toInt())
        val rg = RadioGroup(this)
        CrosshairView.NAMES.forEachIndexed { i, n -> rg.addView(RadioButton(this).apply { text = n; id = 100 + i; setTextColor(0xFFFFFFFF.toInt()) }) }
        rg.check(100 + prefs.style.coerceAtLeast(0))
        rg.setOnCheckedChangeListener { _, id -> prefs.style = id - 100; refresh() }
        root.addView(rg)

        fun slider(name: String, min: Int, max: Int, get: () -> Int, set: (Int) -> Unit) {
            label(name).setTextColor(0xFFFFFFFF.toInt())
            root.addView(SeekBar(this).apply {
                this.max = max - min; progress = get() - min
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar?, p: Int, u: Boolean) {
                        set(p + min)
                        preview.layoutParams = LinearLayout.LayoutParams(dp(prefs.size), dp(prefs.size)); refresh() }
                    override fun onStartTrackingTouch(s: SeekBar?) {}
                    override fun onStopTrackingTouch(s: SeekBar?) {}
                })
            })
        }
        slider("Size", 20, 200, { prefs.size }, { prefs.size = it })
        slider("Opacity", 10, 100, { prefs.opacity }, { prefs.opacity = it })

        btn("Import my own PNG") { picker.launch(arrayOf("image/png")) }
        btn("1. Allow draw-over-apps") { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) }
        btn("2. Enable auto-launch with CODM") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        val sw = Switch(this).apply { text = "Auto-launch with CODM"; setTextColor(0xFFFFFFFF.toInt()); isChecked = prefs.auto
            setOnCheckedChangeListener { _, v -> prefs.auto = v } }
        root.addView(sw)
        btn("Start / stop overlay now") {
            if (!Settings.canDrawOverlays(this)) Toast.makeText(this, "Grant overlay permission first", Toast.LENGTH_SHORT).show()
            else if (OverlayService.running) OverlayService.stop(this) else OverlayService.start(this)
        }
        setContentView(ScrollView(this).apply { setBackgroundColor(0xFF222222.toInt()); addView(root) })
        refresh()
    }
}
