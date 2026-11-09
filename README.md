# Mythic Crosshair
1. Open this folder in Android Studio (Koala+), let Gradle sync, press Run (or Build > Build APK).
2. In the app: allow draw-over-apps, then enable "Mythic Crosshair" in Accessibility (this is what detects CODM launching).
3. Pick a style, adjust size/opacity, or import your own transparent PNG.
Android shows an accessibility warning on install; that's normal for this auto-launch method.

## Build without Android Studio
Push this folder to a new GitHub repo. Open the Actions tab, run "Build APK", then download the
"MythicCrosshair-apk" artifact (contains app-debug.apk).
