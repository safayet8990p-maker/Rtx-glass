# Glassly — Liquid Glass Coverage & Quality Audit (`v0.01.10`)

Every floating control, bar, button, menu, sheet, alert, slider, switch, chip, field, toast, and picker in **Glassly** uses the shared 20-component Liquid Glass library (`com.example.glass`). Main media content (photos, videos, and grid cells) remains crisp and non-glass, and rows nested inside glass cards or bottom sheets use `FlatTonalRow` so glass is never nested inside glass.

## 1. Component & Screen Glass Coverage Matrix

| Screen / Area | Control / Component | Uses Glass? | Variant | Pressed Feedback (Spring + Shimmer + Haptic) | Light & Dark Adaptive Tint | Reduce-Glass Solid Fallback | TalkBack & >=48dp Target | Status |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Splash Screen** | Layered Glass Logo Container | Yes | `TINTED` | N/A (Auto <1s settle) | Yes | Yes | Yes | **PASS** |
| **Permission Onboarding** | Hero Illustration Card & Feature Cards | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Permission Onboarding** | Allow Full Access Primary Button | Yes | `TINTED` | Yes (`1.06x` spring + haptic) | Yes | Yes | Yes | **PASS** |
| **Permission Onboarding** | Select Specific Photos (Android 14+) Button | Yes | `REGULAR` | Yes (`1.06x` spring + haptic) | Yes | Yes | Yes | **PASS** |
| **Global Navigation** | Floating Shrinking `GlassTabBar` + Search Orb | Yes | `REGULAR` | Yes (Morph + SDF liquid bridge) | Yes | Yes | Yes | **PASS** |
| **Global Notifications** | Top Floating `GlassToast` Banner | Yes | `REGULAR` | N/A | Yes | Yes | Yes | **PASS** |
| **Settings (Proving Ground)** | Collapsible Floating `GlassToolbar` | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Settings (Proving Ground)** | Live Liquid Glass Preview Card & Interactive Demo | Yes | `REGULAR` / `CLEAR` / `TINTED` | Yes | Yes | Yes | Yes | **PASS** |
| **Settings (Proving Ground)** | Theme & Glass Style `GlassSegmentedControl` | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Settings (Proving Ground)** | Glass Intensity & Playback Speed `GlassSlider` | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Settings (Proving Ground)** | All Toggle Controls (`GlassSwitch`) | Yes | `REGULAR` / `TINTED` | Yes | Yes | Yes | Yes | **PASS** |
| **Settings (Proving Ground)** | Grid Density & Retention `GlassStepper` | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Settings (Proving Ground)** | Nested Settings Rows inside `GlassCard` | No (`FlatTonalRow` — no glass-in-glass) | Flat Tonal | Yes | Yes | Yes | Yes | **PASS** |
| **Settings (Proving Ground)** | Reset All Settings & PIN Dialog (`GlassAlert`) | Yes | `REGULAR` / `TINTED` | Yes (Expands from trigger) | Yes | Yes | Yes | **PASS** |
| **Glass Gallery (Debug)** | 20-Component Interactive State Proving Ground | Yes | All 3 Variants | Yes | Yes | Yes | Yes | **PASS** |
| **Library Tab** | Top Floating `Filter` Circle & `Select` Pill (`GlassContainer`) | Yes | `REGULAR` / `TINTED` | Yes (SDF smooth-union bridge) | Yes | Yes | Yes | **PASS** |
| **Library Tab** | Sticky Date & Location Header Chip | Yes | `CLEAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Library Tab** | Thumbnail Badges (`LIVE`, Duration, `RAW`, `GIF`) | Yes | `CLEAR` | N/A | Yes | Yes | Yes | **PASS** |
| **Library Tab** | Fast-Scroll Date Scrubber (`GlassScrubberBubble`) | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Library Tab** | Bottom `Years / Months / Days / All` Segmented Pill | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Library Tab** | Filter & View Options Popover (`GlassMenu`) | Yes | `REGULAR` | Yes (Expands from top-left button) | Yes | Yes | Yes | **PASS** |
| **Library Tab** | Multi-Select Bottom Bar (`GlassActionBar`) | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Collections Tab** | Top Floating `GlassToolbar` (`+`, Tile Size, `...`) | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Collections Tab** | Memories Hero Play Chip & Pinned Badges | Yes | `CLEAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Collections Tab** | Media Types & Utilities Grouped Cards (`GlassCard` + `FlatTonalRow`) | Yes (Outer Card) | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Collections Tab** | Create / Rename / Delete Album Dialog (`GlassAlert`) | Yes | `REGULAR` / `TINTED` | Yes | Yes | Yes | Yes | **PASS** |
| **Search Overlay** | Thumb-Reach Bottom `GlassSearchField` & Close Orb | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Search Overlay** | Suggestion Chips (`GlassChip`) & Recent Searches Card | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Photo Viewer** | Top Back Button, Date/Place Pill, & More Menu Button | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Photo Viewer** | Sub-Header `PORTRAIT` / `LIVE` & Zoom `GlassBadge` | Yes | `REGULAR` / `TINTED` | Yes | Yes | Yes | Yes | **PASS** |
| **Photo Viewer** | Bottom Share Circle, 3-Icon Capsule (Heart/Info/Edit), Trash Circle | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Photo Viewer** | Swipe-Up EXIF / Camera / GPS Map Sheet (`GlassBottomSheet`) | Yes | `REGULAR` | Yes (`FlatTonalRow` inside) | Yes | Yes | Yes | **PASS** |
| **Video Player** | Floating Playback Card (`GlassSlider`, Speed, Mute, Loop, Rotate, PiP) | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Photo Editor** | Top `Cancel`, `Revert`, Undo/Redo Capsule, & `Compare` Pill | Yes | `REGULAR` / `TINTED` | Yes | Yes | Yes | Yes | **PASS** |
| **Photo Editor** | Circular Adjustment Dials, Tick-Mark Ruler, & `GlassSlider` | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Photo Editor** | Crop Aspect Ratio & Markup Preset Chips (`GlassChip`) | Yes | `REGULAR` / `TINTED` | Yes | Yes | Yes | Yes | **PASS** |
| **Photo Editor** | Bottom Tool Mode Pill (`Adjust / Styles / Crop / Markup`) | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |
| **Hidden Album** | 4-Digit PIN Keypad & Biometric Unlock Buttons | Yes | `REGULAR` / `TINTED` | Yes | Yes | Yes | Yes | **PASS** |
| **Trash / Duplicates / Large Files** | Toolbars, Merge Buttons, & Empty Trash Confirmation | Yes | `REGULAR` / `TINTED` | Yes | Yes | Yes | Yes | **PASS** |
| **Slideshow** | Auto-Hiding Top & Bottom Minimal Playback Controls | Yes | `REGULAR` | Yes | Yes | Yes | Yes | **PASS** |

---

## 2. Master Quality Bar Checklist

| Check Item | Verification Result | Status |
| :--- | :--- | :---: |
| **1. Alignment & 4/8-pt Grid** | All screens use `GlassDesignTokens` spacing (`4.dp`, `8.dp`, `12.dp`, `16.dp`, `20.dp`, `24.dp`, `32.dp`). | **PASS** |
| **2. Concentric Corner Radii** | Outer cards use `24.dp` radius with `12.dp` padding; inner rows (`FlatTonalRow`) use `concentricInnerRadius(24.dp, 8.dp) = 16.dp`. | **PASS** |
| **3. Glass Refraction & Dispersion Visible** | Custom AGSL `LIQUID_GLASS_AGSL_SHADER` bends the captured backdrop along the rounded-rect SDF rim with R/G/B chromatic dispersion. | **PASS** |
| **4. Specular Bevel & Tilt Response** | `rememberDeviceTiltOffset` reads accelerometer/gravity sensors and smoothly shifts the top-left specular highlight angle. | **PASS** |
| **5. Text Legibility & WCAG AA Contrast** | Adaptive tinting + dark scrim on `CLEAR` glass badges + high-contrast mode ensure legible text over bright and dark photos alike. | **PASS** |
| **6. No Glass Inside Glass** | Enforced everywhere: `GlassCard`, `GlassBottomSheet`, and `GlassAlert` contain `FlatTonalRow` children without nested shaders. | **PASS** |
| **7. Animation Smoothness & Press Feedback** | Every glass control uses interruptible spring physics (`scale 1.0 -> 1.06`, slight squash, shimmer sweep, and haptic tick). | **PASS** |
| **8. Empty, Loading, Error & Denied States** | Implemented across Library, Collections, Search, Album Detail, Recently Deleted, Hidden Album, Duplicates, and Permissions. | **PASS** |
| **9. Accessibility & Bangla/English Localization** | Minimum `48.dp` touch targets, TalkBack semantics, Reduce Motion, Reduce Glass, and instant English / বাংলা switching. | **PASS** |
| **10. Android 13/14/15 Permission Flow** | Full access (`READ_MEDIA_IMAGES` / `READ_MEDIA_VIDEO`) and Android 14+ partial photo access (`READ_MEDIA_VISUAL_USER_SELECTED`) supported. | **PASS** |
