# CareTail Day 02 Export Guide

This carousel uses real screenshots captured from the Android emulator. Do not replace the screenshots with fake mockups or imaginary app screens.

Source screenshots:

`content/tiktok/caretail/day-02/raw-screenshots/`

Slide exporter:

`content/tiktok/caretail/day-02/raw-assets/render_slides.py`

Review gallery:

`content/tiktok/caretail/day-02/raw-assets/render.html`

Final PNG files:

`content/tiktok/caretail/day-02/final-slides/`

Export settings:

- Format: PNG
- Size: 1080x1920
- Slides: 7
- Content type: TikTok photo slideshow / carousel
- Motion/audio: none

Re-export command from the repository root:

```powershell
& 'C:\Users\Dan\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe' 'content\tiktok\caretail\day-02\raw-assets\render_slides.py'
```

Raw screenshot capture method used:

```powershell
.\gradlew.bat assembleDebug
& 'C:\Users\Dan\AppData\Local\Android\Sdk\platform-tools\adb.exe' install -r 'app\build\outputs\apk\debug\app-debug.apk'
& 'C:\Users\Dan\AppData\Local\Android\Sdk\platform-tools\adb.exe' shell monkey -p com.caretail.app -c android.intent.category.LAUNCHER 1
& 'C:\Users\Dan\AppData\Local\Android\Sdk\platform-tools\adb.exe' exec-out screencap -p > 'content\tiktok\caretail\day-02\raw-screenshots\01-home.png'
```

Use semantic navigation or direct taps only to reach real app screens. Do not capture loading states, keyboard-open states, emulator chrome, or unfinished/broken UI.

Quality checks:

- Confirm each final slide uses at least one real file from `raw-screenshots/`.
- Confirm slide numbering runs from `01/07` through `07/07`.
- Confirm final PNG dimensions are `1080x1920`.
- Confirm title/subtitle/CTA text stays inside TikTok-safe margins.
- Confirm screenshot framing is readable and not accidentally cropped.
- Confirm the final slide clearly works as the CTA slide with `Visit CareTail`.
