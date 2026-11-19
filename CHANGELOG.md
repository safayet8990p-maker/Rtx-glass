# Changelog

All notable changes to **Glassly** are documented in this file.

## [0.1.02] - 2026-11-19 (versionCode 112)

### Added & Updated (iOS 26 Photos Walkthrough Parity)
- **Bottom Navigation & Morphing Liquid Glass Tab Bar**:
  - Updated floating bottom bar to the exact 2-tab pill on the bottom-left (`Library` and `Collections`) paired with a circular `Search` glass button on the bottom-right.
  - Scrolling inside `Library` collapses the bottom pill into a circular photo-grid icon on the left, a centered `Years | Months | All` segmented glass pill, and the circular `Search` button on the right.
  - Tapping the top-left `Library` title/date area jumps to the oldest media at the top; tapping the bottom-left `Library` button scrolls back to the newest media at the bottom.
- **Library Filter, Sort & View Options Popover + Batch Multi-Select**:
  - Added expandable `Filter` submenu supporting combinable multi-select filters (`All Items`, `Favourites`, `Edited`, `Photos`, `Videos`, `Screenshots`, `Not in an Album`, `Shared with You`, `Captured by Me`) and active blue filter dot indicator.
  - Added expandable `View Options` submenu (`Zoom In`, `Zoom Out`, `Aspect Ratio Grid / Square Photo Grid`, and toggles for `Show: Screenshots` and `Show: Shared with You`).
  - Added full Multi-Select mode with top-right `...` and `Cancel` glass buttons, bottom bar (`Share`, `X Photos Selected`, `Trash`), and batch `...` menu (`Copy`, `Add to Favourites`, `Duplicate`, `Hide`, `Play as Slideshow`, `Add to Album`, `Adjust Date & Time`, `Adjust Location`, `Don't Feature a Person`).
- **Collections Customization, Collapsible Sections, In-Place Reorder & Pinned Editor**:
  - Added top-right `...` menu and circular Profile Avatar badge (`PG`) that opens **Settings**.
  - Added the 3-icon tile layout selector row (`Top-Row Emphasis`, `Dense Tiles`, `Large Tiles`) plus `Show All`, `Collapse All`, and `Reorder` actions.
  - Implemented individual section expand/collapse chevrons (`>` / `⌄`) and in-place Reorder mode with drag handles (`≡`) and top-right blue checkmark (`✓`) across all 11 sections (`Pinned`, `Shared Albums`, `Media Types`, `People & Pets`, `Utilities`, `Featured Photos`, `Memories`, `Albums`, `Trips`, `Wallpaper Suggestions`, `Recent Days`).
  - Implemented the **Modify Pinned Collections** sheet (`Edit` pill on `Pinned`) with red minus (`⊖`) remove buttons, red `Delete` confirmation pill, drag handles, `Suggestions` with green plus (`⊕`), and `Any Collection or Album` picker, plus long-press `Pin` / `Unpin` context menus.
- **Spatial Scene 3D Parallax Viewer & Control Toggle**:
  - Added top-right hexagon **Spatial Scene** toggle button in the Photo Viewer that activates an interactive multi-layer 3D tilt parallax depth effect reacting to device motion sensors and touch drag.
  - Added **Control for Creating Spatial Photos** toggle switch in **Settings > Viewer & Video**.
- **Photo Editor Appearance Switcher & Interactive Markup Magnifying Loupe**:
  - Added top-right Markup shortcut icon and `...` Editor menu (`Copy Edits`, `Paste Edits`, `Save as Duplicate`, expandable `Appearance: System / Dark / Light` switching editor background & chrome live, `Undo`, `Redo`, `Red-Eye`).
  - Added bottom-right `+` Markup tool menu (`Image Description`, `Add Sticker`, `Add Text`, `Add Signature`, `Add Shape`, `Add Loupe`) and interactive **Magnifying Loupe** on the canvas with draggable position, blue radius handle, green clock-arc zoom magnification handle, and stroke color & width (`pt`) toolbar.
- **Real Device MediaStore Integration (Photos + Videos)**:
  - Queries both `MediaStore.Images` and `MediaStore.Video` with live `ContentObserver` updates while blending curated sample albums when device folders are empty.

## [0.01.10] - 2026-11-19 (versionCode 110)

### Added
- **App Identity & Versioning**:
  - Standardized single-source-of-truth version `0.01.10` (`versionCode = 110`) across `app/build.gradle.kts`, `BuildConfig.VERSION_NAME`, `Settings > About`, and the `< 1s` layered-glass splash screen.
  - Custom layered-glass adaptive, round, and monochrome launcher icons (`com.glassly.app`).
- **Liquid Glass Material Engine (`com.example.glass`)**:
  - Central `GlassConfig` data class controlling refraction strength, rim width, chromatic dispersion, blur radius, specular intensity, noise grain, spring stiffness/damping, variant (`REGULAR`, `CLEAR`, `TINTED`), theme mode (`SYSTEM`, `LIGHT`, `DARK`), and accessibility fallbacks (`reduceGlass`, `reduceMotion`, `highContrastGlass`, `largerTouchTargets`).
  - Split source-node / effect-node backdrop capture via `GlassBackdropProvider` and `rememberGraphicsLayer()` preventing infinite draw recursion.
  - Custom AGSL `RuntimeShader` (`LIQUID_GLASS_AGSL_SHADER`) on Android 13+ (API 33+) with rounded-rect SDF lensing, R/G/B edge dispersion, accelerometer-driven directional specular bevel highlight, procedural noise grain, and adaptive light/dark tinting.
  - Graceful fallback to `RenderEffect.createBlurEffect` on Android 12/12L (API 31–32) and accessible high-contrast opaque tonal surfaces when **Reduce Glass** is enabled.
- **20-Component Shared Liquid Glass Library**:
  - `GlassButton`, `GlassContainer` (with SDF smooth-union liquid bridge between neighbors), `GlassSegmentedControl`, `GlassSwitch`, `GlassSlider`, `GlassStepper`, `GlassCard`, `FlatTonalRow` (enforcing the "Never glass inside glass" rule), `GlassChip`, `GlassBadge`, `GlassSearchField`, `GlassTabBar` (floating + scroll shrink/expand + standalone search orb), `GlassToolbar`, `GlassActionBar`, `GlassMenu`, `GlassAlert`, `GlassBottomSheet`, `GlassToast`, `GlassProgressLoader`, `GlassScrubberBubble`, and `GlassPicker`.
  - Hidden debug proving ground screen (**Glass Gallery Debug**) showcasing all 20 components across every variant and state.
- **Settings Screen (Proving Ground)**:
  - Collapsible floating glass toolbar over a rich colorful backdrop with a real-time **LIVE PREVIEW** card at the top.
  - Complete 10-section settings suite: Appearance, Library, Viewer & Video, Privacy & Security (`FLAG_SECURE` & 4-digit PIN / Biometric lock), Storage, Language (instant English / বাংলা switch), Accessibility, Notifications & Permissions, About (`Glassly • Version 0.01.10`), and Reset All Settings.
- **Library Tab**:
  - iOS 26/27 newest-at-bottom photo/video grid with automatic scroll-to-latest on open, sticky date & location glass pills, pinch-to-zoom column density (`1` to `5` columns), square vs aspect-fit toggle, `Years / Months / Days / All` floating glass segmented control, filter & view-options glass menu (`Added / Captured`, `All Items`, `Favorites`, `Edited`, `Captured by Me`, `Not in an Album`, `Videos`), multi-select glass action bar, fast-scroll scrubber bubble, and media badges (`LIVE`, video duration, `RAW`, `GIF`, favorite heart).
- **Collections & Search Tabs**:
  - Featured Memories hero card, Pinned collections, user-creatable/renamable/deletable Albums, Media Types, Utilities (`Recently Deleted`, `Hidden`, `Favorites`, `Duplicates`, `Large Files`), tile layout mode selector (`Large`, `Dense`, `Top-Row Emphasis`), and Section Reorder modal.
  - Thumb-reach bottom glass search field supporting natural-language queries (`"videos last month"`, `"favorites"`, `"portraits"`, `"raw"`, location, year/month) and recent search history.
- **Photo Viewer, Custom Video Player & Non-Destructive Editor**:
  - Pinch-to-zoom, double-tap zoom (`1.0× ↔ 2.5×`), swipe-down to dismiss, tap to toggle black chrome, long-press Live Photo motion playback, filmstrip scrubber, and swipe-up EXIF / Camera / GPS Map info sheet.
  - Custom glass video controls with play/pause, interactive scrubber, playback speed (`0.5×–2.0×`), mute, loop, fullscreen rotation, and Picture-in-Picture (PiP).
  - Non-destructive Photo Editor with circular adjustment dials, tick-mark ruler scrubber, Auto-Enhance wand, 6 filter styles, crop ratios & straighten dial (`-45°..+45°`), markup text, Undo/Redo stack, Hold-to-Compare original, Revert to Original, and Save Overwrite vs Save as Copy.
