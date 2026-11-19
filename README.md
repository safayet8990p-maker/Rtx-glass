# Glassly — iOS 27 Liquid Glass Photos for Android

**Version:** `0.01.10` (`versionCode = 110`)  
**Package ID:** `com.glassly.app`  
**Minimum SDK:** Android 7.0+ (API 24+ compatible build; hardware RenderEffect blur on API 31–32; full AGSL `RuntimeShader` refractive lens & chromatic dispersion on API 33+).

---

## 1. Plain-Language Summary for the Owner

### What Was Built
**Glassly** is a complete, flagship Android Photo & Video Gallery engineered to look and feel like Apple's iOS 26/27 **Liquid Glass** Photos app:
1. **True Liquid Glass Material**: Floating bars, buttons, menus, sheets, sliders, switches, and badges bend and refract the colorful photos behind them, complete with a subtle rainbow prism edge (chromatic dispersion), a top-left light reflection that shifts as you tilt your phone, and a springy water-droplet bounce when pressed.
2. **Complete Shared Glass Component Library (20 Components)**: Built once and reused across every screen, with a dedicated **Settings** proving ground (featuring a live real-time glass preview card at the top) and a hidden **Glass Gallery** debug screen.
3. **Full iOS 27 Photos Experience**:
   - **Library Tab**: Newest photos at the bottom, smooth pinch-to-zoom grid columns, `Years / Months / Days / All` glass switcher, filter popover (`Added / Captured`, `Favorites`, `Edited`, `Captured by Me`, `Videos`), multi-select action bar, and fast-scroll date bubble.
   - **Collections Tab**: Memories hero slideshow, Pinned collections, custom Albums (create, rename, delete, reorder), Media Types, and Utilities.
   - **Search Tab**: Thumb-reach bottom glass search bar supporting natural phrases like `"videos last month"`, `"favorites"`, `"portraits"`, `"raw"`, or place names.
   - **Photo Viewer & Custom Video Player**: Pinch/double-tap zoom, swipe-down to dismiss, bottom filmstrip scrubber, swipe-up EXIF camera & GPS map sheet, Live Photo motion playback on long-press, and custom glass video controls (scrubber, speed `0.5×–2.0×`, mute, loop, fullscreen rotation, Picture-in-Picture).
   - **Non-Destructive Photo Editor**: Circular adjustment dials, tick-mark ruler scrubber, Auto-Enhance wand, 6 filter styles, crop ratios & straighten dial (`-45°..+45°`), markup text, Hold-to-Compare original, Revert to Original, and Save Overwrite vs Save as Copy.
   - **Privacy & Storage Utilities**: 30-day Recently Deleted trash, Hidden Album protected by a Liquid Glass 4-digit PIN keypad & biometric unlock, Duplicate photo finder & merger, Large files cleaner, and clean-mode Slideshow.
   - **Bilingual Support**: Instant switch between **English** and **বাংলা (Bangla)** in Settings.

---

## 2. How `GlassConfig` & the Liquid Glass Shader Work

All glass parameters live in a single immutable `GlassConfig` data class (`app/src/main/java/com/example/glass/GlassConfig.kt`) provided across the Compose hierarchy via `LocalGlassConfig`:

- `refractionStrength` (`0.045f` default): Controls how strongly background pixels bend inward along the rounded-rectangle Signed Distance Field (SDF) rim.
- `rimWidthDp` (`14.dp` default): Width of the curved lens bevel around the perimeter of each glass control.
- `chromaticDispersion` (`0.012f` default): Offsets Red, Green, and Blue UV sampling vectors along the normal vector at the rim for realistic optical dispersion.
- `blurRadiusDp` (`18.dp` default): Gaussian backdrop blur radius keeping the underlying photo colors recognizable while ensuring text contrast.
- `specularIntensity` (`0.52f` default) & `lightTiltOffset`: Drives the directional specular bevel highlight that responds smoothly to device tilt via accelerometer/gravity sensors (`rememberDeviceTiltOffset`).
- `noiseGrain` (`0.025f` default): Procedural micro-grain preventing color banding on gradients.
- `variant` (`REGULAR`, `CLEAR`, `TINTED`): Selects standard translucent glass, ultra-transmissive clear glass (with an automatic contrast scrim for legibility), or accent-tinted glass for primary actions.
- `reduceGlass` & `highContrastGlass`: Immediately replaces shader refraction with accessible, high-contrast solid tonal surfaces.

### Source-Node / Effect-Node Backdrop Capture
To prevent infinite draw recursion, `GlassBackdropProvider` records the underlying screen content into a dedicated `GraphicsLayer` (`rememberGraphicsLayer()`). Floating glass controls read their window bounds via `onGloballyPositioned` and draw the captured backdrop layer through the AGSL `RuntimeShader` (`LIQUID_GLASS_AGSL_SHADER`) clipped to the control's exact coordinates.

---

## 3. Honest List of Differences from Real iOS 26/27 Liquid Glass

1. **System Window Compositor vs In-App Backdrop Layer**:
   - On iOS 26/27, `UIGlassEffect` is composited by the system window server (`SpringBoard` / `RenderServer`) and can refract the user's home screen wallpaper even outside the app window.
   - On Android, app shaders cannot read pixels from other apps or the system status bar. Glassly captures the app's own full-screen photo/canvas layer via `rememberGraphicsLayer()` and refracts that live content in real time.
2. **API-Level Fallbacks**:
   - **Android 13+ (API 33+)**: Full AGSL `RuntimeShader` with SDF edge refraction, chromatic R/G/B dispersion, tilt-reactive specular bevel, noise grain, and hardware blur.
   - **Android 12 / 12L (API 31–32)**: Hardware `RenderEffect.createBlurEffect` frosted glass with vector specular rim and adaptive tint (since `RuntimeShader` was introduced in API 33).
   - **Reduce Glass / Below API 31**: High-contrast opaque Material 3 tonal surfaces with crisp borders.
3. **Apple Proprietary Assets**:
   - Per intellectual property rules, no Apple SF Symbols or Apple logos are bundled; all icons, procedural studio HDR portraits/landscapes, and the layered-glass launcher icon are 100% original assets created specifically for Glassly.

---

## 4. Setup & Build Instructions

1. **Build Debug APK**:
   ```bash
   gradle :app:assembleDebug
   ```
2. **Run Unit & Robolectric Verification Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```
