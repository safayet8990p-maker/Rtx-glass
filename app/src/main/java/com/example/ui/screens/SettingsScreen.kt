package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.BuildConfig
import com.example.glass.AppLanguage
import com.example.glass.AppThemeMode
import com.example.glass.FlatTonalRow
import com.example.glass.GlassAlert
import com.example.glass.GlassBottomSheet
import com.example.glass.GlassButton
import com.example.glass.GlassButtonShape
import com.example.glass.GlassCard
import com.example.glass.GlassBackdropProvider
import com.example.glass.GlassContainer
import com.example.glass.GlassSegmentedControl
import com.example.glass.GlassSlider
import com.example.glass.GlassStepper
import com.example.glass.GlassSwitch
import com.example.glass.GlassVariant
import com.example.glass.LiquidGlassSurface
import com.example.glass.LocalGlassConfig
import com.example.ui.theme.AccentTintOption
import com.example.ui.theme.GlassRed
import com.example.viewmodel.AppSubScreen
import com.example.viewmodel.GlasslyViewModel
import com.example.viewmodel.MediaPermissionLevel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: GlasslyViewModel,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config = LocalGlassConfig.current
    val isBangla = config.language == AppLanguage.BANGLA

    val gridColumns by viewModel.gridColumns.collectAsStateWithLifecycle()
    val isSquareGrid by viewModel.isSquareGrid.collectAsStateWithLifecycle()
    val showBadges by viewModel.showBadges.collectAsStateWithLifecycle()
    val sortByCaptured by viewModel.sortByCaptured.collectAsStateWithLifecycle()
    val openAtLatest by viewModel.openAtLatest.collectAsStateWithLifecycle()
    val showLocationHeaders by viewModel.showLocationInHeaders.collectAsStateWithLifecycle()

    val autoplayMotion by viewModel.autoplayMotion.collectAsStateWithLifecycle()
    val autoplayVideos by viewModel.autoplayVideos.collectAsStateWithLifecycle()
    val loopVideos by viewModel.loopVideos.collectAsStateWithLifecycle()
    val defaultSpeed by viewModel.defaultPlaybackSpeed.collectAsStateWithLifecycle()
    val keepScreenOn by viewModel.keepScreenOn.collectAsStateWithLifecycle()
    val showInfoSwipeUp by viewModel.showInfoOnSwipeUp.collectAsStateWithLifecycle()
    val showSpatialPhotoControl by viewModel.showSpatialPhotoControl.collectAsStateWithLifecycle()

    val hiddenLock by viewModel.hiddenAlbumLockEnabled.collectAsStateWithLifecycle()
    val biometricUnlock by viewModel.biometricUnlockEnabled.collectAsStateWithLifecycle()
    val hiddenPin by viewModel.hiddenPin.collectAsStateWithLifecycle()
    val hidePreviewsRecents by viewModel.hidePreviewsInRecents.collectAsStateWithLifecycle()
    val secureScreen by viewModel.secureScreenEnabled.collectAsStateWithLifecycle()

    val cacheSizeMb by viewModel.cacheSizeMb.collectAsStateWithLifecycle()
    val trashRetentionDays by viewModel.trashRetentionDays.collectAsStateWithLifecycle()
    val duplicateGroups by viewModel.duplicateGroups.collectAsStateWithLifecycle()
    val largeFiles by viewModel.largeFilesMedia.collectAsStateWithLifecycle()
    val permissionLevel by viewModel.permissionLevel.collectAsStateWithLifecycle()

    var showResetConfirmAlert by remember { mutableStateOf(false) }
    var showChangePinAlert by remember { mutableStateOf(false) }
    var pinInputText by remember(hiddenPin) { mutableStateOf(hiddenPin) }
    var showLicensesSheet by remember { mutableStateOf(false) }
    var showPrivacyPolicySheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 90 }
    }

    GlassBackdropProvider(
        backdropLuminanceHint = if (config.isDarkTheme) 0.18f else 0.78f,
        backgroundContent = {
            // Soft colorful gradient mesh backdrop so refraction is vivid under every Settings glass card
            ColorfulSettingsRefractionBackdrop(
                isDark = config.isDarkTheme,
                accentColor = config.accentTint
            )

            LazyColumn(
                state = listState,
                modifier = modifier
                    .fillMaxSize()
                    .testTag("settings_lazy_column"),
                contentPadding = PaddingValues(
                    top = 24.dp,
                    bottom = 135.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Large collapsing title header
                item {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(top = 12.dp, bottom = 4.dp)
                    ) {
                        Text(
                            text = if (isBangla) "সেটিংস" else "Settings",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isBangla) {
                                "iOS 27 লিকুইড গ্লাস ইঞ্জিন ও গ্যালারি পছন্দসমূহ"
                            } else {
                                "iOS 27 Liquid Glass Engine & Photos Preferences"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // LIVE PREVIEW CARD AT THE TOP (matches reference PDF page 17 & page 40)
                item {
                    LiveLiquidGlassPreviewCard(isBangla = isBangla)
                }

                // 1. APPEARANCE SECTION
                item {
                    SettingsSectionHeader(if (isBangla) "অ্যাপিয়ারেন্স ও লিকুইড গ্লাস" else "Appearance & Liquid Glass")
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = if (isBangla) "থিম (System / Light / Dark)" else "Theme",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            GlassSegmentedControl(
                                options = AppThemeMode.entries,
                                selectedOption = config.themeMode,
                                onOptionSelected = { viewModel.setThemeMode(it) },
                                labelProvider = { if (isBangla) it.labelBn else it.labelEn },
                                testTagPrefix = "settings_theme"
                            )

                            Text(
                                text = if (isBangla) "গ্লাস স্টাইল (Regular / Clear / Tinted)" else "Liquid Glass Style",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            GlassSegmentedControl(
                                options = GlassVariant.entries,
                                selectedOption = config.variant,
                                onOptionSelected = { viewModel.setGlassVariant(it) },
                                labelProvider = { if (isBangla) it.labelBn else it.labelEn },
                                testTagPrefix = "settings_glass_variant"
                            )

                            Text(
                                text = if (isBangla) "অ্যাকসেন্ট রঙ" else "Accent Tint",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AccentTintOption.entries.forEach { swatch ->
                                    val selected = swatch == config.accentOption
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(swatch.color)
                                            .border(
                                                width = if (selected) 3.dp else 1.dp,
                                                color = if (selected) Color.White else Color.White.copy(alpha = 0.4f),
                                                shape = CircleShape
                                            )
                                            .clickable { viewModel.setAccentOption(swatch) }
                                            .testTag("swatch_${swatch.name.lowercase()}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (selected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = swatch.labelEn,
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.padding(top = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isBangla) "গ্লাসের তীব্রতা (প্রতিসরণ ও ব্লার)" else "Glass Intensity & Refraction",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${(config.intensity * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = config.accentTint
                                    )
                                }
                                GlassSlider(
                                    value = config.intensity,
                                    onValueChange = { viewModel.setGlassIntensity(it) },
                                    valueRange = 0.15f..1.0f,
                                    leadingIcon = Icons.Default.AutoAwesome,
                                    trailingIcon = Icons.Default.Tune,
                                    contentDescription = "Glass Intensity",
                                    testTag = "settings_glass_intensity_slider"
                                )
                            }

                            FlatTonalRow(
                                title = if (isBangla) "গ্লাস কমান (Reduce Glass)" else "Reduce Glass",
                                subtitle = if (isBangla) "স্বচ্ছতা কমিয়ে সলিড টোনাল সারফেস ব্যবহার করুন" else "Replace refractive glass with high-contrast solid tonal surfaces",
                                leadingIcon = Icons.Default.Palette,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = config.reduceGlass,
                                        onCheckedChange = { viewModel.setReduceGlass(it) },
                                        contentDescription = "Reduce Glass",
                                        testTag = "settings_reduce_glass_switch"
                                    )
                                }
                            )
                        }
                    }
                }

                // 2. LIBRARY SECTION
                item {
                    SettingsSectionHeader(if (isBangla) "লাইব্রেরি ও গ্রিড" else "Library")
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FlatTonalRow(
                                title = if (isBangla) "ডিফল্ট গ্রিড কলাম" else "Default Grid Density",
                                subtitle = if (isBangla) "প্রতি সারিতে ছবির সংখ্যা" else "$gridColumns columns per row",
                                leadingIcon = Icons.Default.GridView,
                                trailingContent = {
                                    GlassStepper(
                                        value = gridColumns,
                                        onValueChange = { viewModel.setGridColumns(it) },
                                        range = 2..6,
                                        unitSuffix = " col",
                                        testTag = "settings_grid_columns_stepper"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "বর্গাকার থাম্বনেইল (Square Grid)" else "Square Photo Grid",
                                subtitle = if (isBangla) "বন্ধ থাকলে আসল অনুপাতে দেখাবে" else "Turn off for full aspect-fit thumbnails",
                                leadingIcon = Icons.Default.PhotoLibrary,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = isSquareGrid,
                                        onCheckedChange = { viewModel.setSquareGrid(it) },
                                        contentDescription = "Square photo grid"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "ব্যাজ দেখান (ভিডিও, ফেভারিট, RAW)" else "Show Media Badges",
                                subtitle = if (isBangla) "থাম্বনেইলের উপর সময়, হার্ট এবং ফরম্যাট ব্যাজ" else "Duration, favorite heart, Live, RAW, GIF & Screenshot chips",
                                leadingIcon = Icons.Default.AutoAwesome,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = showBadges,
                                        onCheckedChange = { viewModel.setShowBadges(it) },
                                        contentDescription = "Show Media Badges"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "তোলার তারিখ অনুযায়ী সাজান" else "Sort by Date Captured",
                                subtitle = if (isBangla) "বন্ধ থাকলে যুক্ত করার তারিখ অনুযায়ী সাজাবে" else "When off, sorts by Date Added",
                                leadingIcon = Icons.Default.Tune,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = sortByCaptured,
                                        onCheckedChange = { viewModel.setSortByCaptured(it) },
                                        contentDescription = "Sort by Date Captured"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "সর্বশেষ ছবিতে খুলুন" else "Open at Latest Photos",
                                subtitle = if (isBangla) "iOS Photos-এর মতো নিচে নতুন ছবিতে স্ক্রল করবে" else "Scrolls to newest items at the bottom on launch",
                                leadingIcon = Icons.Default.Refresh,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = openAtLatest,
                                        onCheckedChange = { viewModel.setOpenAtLatest(it) },
                                        contentDescription = "Open at Latest Photos"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "হেডারে অবস্থান দেখান" else "Show Location in Date Headers",
                                subtitle = if (isBangla) "তারিখের সাথে স্থানের নাম দেখান" else "Display city or landmark in sticky headers",
                                leadingIcon = Icons.Default.Info,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = showLocationHeaders,
                                        onCheckedChange = { viewModel.setShowLocationInHeaders(it) },
                                        contentDescription = "Show Location in Date Headers"
                                    )
                                }
                            )
                        }
                    }
                }

                // 3. VIEWER & VIDEO SECTION
                item {
                    SettingsSectionHeader(if (isBangla) "ভিউয়ার ও ভিডিও" else "Viewer & Video")
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FlatTonalRow(
                                title = if (isBangla) "অটো-প্লে মোশন ফটো" else "Auto-Play Motion Photos",
                                subtitle = if (isBangla) "লং-প্রেস ছাড়াই লাইভ ফটো চলবে" else "Animate Live/Motion photos automatically when opened",
                                leadingIcon = Icons.Default.MotionPhotosOn,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = autoplayMotion,
                                        onCheckedChange = { viewModel.setAutoplayMotion(it) },
                                        contentDescription = "Auto-Play Motion Photos"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "অটো-প্লে ভিডিও" else "Auto-Play Videos",
                                subtitle = if (isBangla) "মিউট অবস্থায় সাথে সাথে ভিডিও চালু হবে" else "Start video playback automatically when viewing",
                                leadingIcon = Icons.Default.PlayCircleOutline,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = autoplayVideos,
                                        onCheckedChange = { viewModel.setAutoplayVideos(it) },
                                        contentDescription = "Auto-Play Videos"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "লুপ ভিডিও (Loop)" else "Loop Videos",
                                subtitle = if (isBangla) "ভিডিও শেষ হলে পুনরায় চালু হবে" else "Continuously repeat video playback",
                                leadingIcon = Icons.Default.SmartDisplay,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = loopVideos,
                                        onCheckedChange = { viewModel.setLoopVideos(it) },
                                        contentDescription = "Loop Videos"
                                    )
                                }
                            )

                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = if (isBangla) "ডিফল্ট প্লেব্যাক স্পিড" else "Default Playback Speed (${defaultSpeed}x)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                GlassSegmentedControl(
                                    options = listOf(0.5f, 1.0f, 1.5f, 2.0f),
                                    selectedOption = defaultSpeed,
                                    onOptionSelected = { viewModel.setDefaultPlaybackSpeed(it) },
                                    labelProvider = { "${it}x" },
                                    testTagPrefix = "settings_speed"
                                )
                            }

                            FlatTonalRow(
                                title = if (isBangla) "স্ক্রিন চালু রাখুন" else "Keep Screen On While Viewing",
                                leadingIcon = Icons.Default.Speed,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = keepScreenOn,
                                        onCheckedChange = { viewModel.setKeepScreenOn(it) },
                                        contentDescription = "Keep Screen On"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "সোয়াইপ আপ করলে ইনফো প্যানেল" else "Show Info Panel on Swipe Up",
                                subtitle = if (isBangla) "EXIF, ক্যামেরা লেন্স এবং ম্যাপ ডিটেইলস" else "Reveal EXIF metadata, camera lens & location sheet",
                                leadingIcon = Icons.Default.Info,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = showInfoSwipeUp,
                                        onCheckedChange = { viewModel.setShowInfoOnSwipeUp(it) },
                                        contentDescription = "Show Info on Swipe Up"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "স্পেশিয়াল ফটো তৈরির কন্ট্রোল" else "Control for Creating Spatial Photos",
                                subtitle = if (isBangla) "ফটো ভিউয়ারে 3D Spatial Scene হেক্সাগন বাটন দেখান" else "Show the 3D Spatial Scene hexagon toggle in Photo Viewer",
                                leadingIcon = Icons.Default.AutoAwesome,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = showSpatialPhotoControl,
                                        onCheckedChange = { viewModel.setShowSpatialPhotoControl(it) },
                                        contentDescription = "Control for Creating Spatial Photos",
                                        testTag = "settings_spatial_photo_switch"
                                    )
                                }
                            )
                        }
                    }
                }

                // 4. PRIVACY & SECURITY SECTION
                item {
                    SettingsSectionHeader(if (isBangla) "প্রাইভেসি ও সিকিউরিটি" else "Privacy & Security")
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FlatTonalRow(
                                title = if (isBangla) "হিডেন অ্যালবাম লক" else "Hidden Album Lock",
                                subtitle = if (isBangla) "পিন এবং বায়োমেট্রিক দিয়ে সুরক্ষিত করুন" else "Require PIN or Biometric unlock to view Hidden photos",
                                leadingIcon = Icons.Default.Lock,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = hiddenLock,
                                        onCheckedChange = { viewModel.setHiddenAlbumLockEnabled(it) },
                                        contentDescription = "Hidden Album Lock"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "বায়োমেট্রিক আনলক" else "Biometric Unlock",
                                subtitle = if (isBangla) "ফিঙ্গারপ্রিন্ট বা ফেস আনলক ব্যবহার করুন" else "Allow fingerprint / face authentication",
                                leadingIcon = Icons.Default.Fingerprint,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = biometricUnlock,
                                        onCheckedChange = { viewModel.setBiometricUnlockEnabled(it) },
                                        contentDescription = "Biometric Unlock"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "হিডেন অ্যালবাম পিন পরিবর্তন করুন" else "Set / Change Hidden Album PIN",
                                subtitle = if (isBangla) "বর্তমান পিন: ••••" else "Current 4-digit PIN is active (Tap to change)",
                                leadingIcon = Icons.Default.Security,
                                onClick = { showChangePinAlert = true },
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "রিসেন্ট অ্যাপসে প্রিভিউ লুকান" else "Hide Previews in Recent Apps",
                                leadingIcon = Icons.Default.VisibilityOff,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = hidePreviewsRecents,
                                        onCheckedChange = { viewModel.setHidePreviewsInRecents(it) },
                                        contentDescription = "Hide Previews in Recent Apps"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "সিকিউর স্ক্রিন (স্ক্রিনশট ব্লক)" else "Secure Screen (FLAG_SECURE)",
                                subtitle = if (isBangla) "হিডেন অ্যালবামে স্ক্রিন রেকর্ডিং বন্ধ রাখুন" else "Prevent screenshots inside locked albums",
                                leadingIcon = Icons.Default.PrivacyTip,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = secureScreen,
                                        onCheckedChange = { viewModel.setSecureScreenEnabled(it) },
                                        contentDescription = "Secure Screen"
                                    )
                                }
                            )
                        }
                    }
                }

                // 5. STORAGE & CLEANER SECTION
                item {
                    SettingsSectionHeader(if (isBangla) "স্টোরেজ ও ক্লিনার" else "Storage & Utilities")
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FlatTonalRow(
                                title = if (isBangla) "থাম্বনেইল ক্যাশ সাইজ" else "Thumbnail Cache",
                                subtitle = String.format(java.util.Locale.US, "%.1f MB used on device", cacheSizeMb),
                                leadingIcon = Icons.Default.Storage,
                                trailingContent = {
                                    GlassButton(
                                        onClick = { viewModel.clearThumbnailCache() },
                                        text = if (isBangla) "ক্লিয়ার" else "Clear Cache",
                                        testTag = "settings_clear_cache_button"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "রিসেন্টলি ডিলিটেড মেয়াদ" else "Recently Deleted Retention",
                                subtitle = "$trashRetentionDays days before permanent removal",
                                leadingIcon = Icons.Default.DeleteSweep,
                                trailingContent = {
                                    GlassStepper(
                                        value = trashRetentionDays,
                                        onValueChange = { viewModel.setTrashRetentionDays(it) },
                                        range = 7..60,
                                        step = 5,
                                        unitSuffix = "d"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "ডুপ্লিকেট ফাইন্ডার (Duplicates)" else "Duplicate Photos Finder",
                                subtitle = "${duplicateGroups.size} duplicate groups detected",
                                leadingIcon = Icons.Default.ContentCopy,
                                onClick = { viewModel.navigateToSubScreen(AppSubScreen.DuplicatesCleaner) },
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "বড় ফাইল ক্লিনার (Large Files)" else "Large Files Storage Cleaner",
                                subtitle = "${largeFiles.size} large videos & RAW files (>15 MB)",
                                leadingIcon = Icons.Default.CleaningServices,
                                onClick = { viewModel.navigateToSubScreen(AppSubScreen.LargeFilesCleaner) },
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                // 6. LANGUAGE SECTION (Bangla / English instant switch)
                item {
                    SettingsSectionHeader(if (isBangla) "ভাষা (Language)" else "Language / ভাষা")
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (isBangla) "অ্যাপের ভাষা নির্বাচন করুন (তাত্ক্ষণিক পরিবর্তন)" else "Instant App Language Switch",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            GlassSegmentedControl(
                                options = AppLanguage.entries,
                                selectedOption = config.language,
                                onOptionSelected = { viewModel.setLanguage(it) },
                                labelProvider = { it.displayName },
                                testTagPrefix = "settings_language"
                            )
                        }
                    }
                }

                // 7. ACCESSIBILITY SECTION
                item {
                    SettingsSectionHeader(if (isBangla) "অ্যাক্সেসিবিলিটি" else "Accessibility")
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FlatTonalRow(
                                title = if (isBangla) "অ্যানিমেশন কমান (Reduce Motion)" else "Reduce Motion",
                                subtitle = if (isBangla) "স্প্রিং বাউন্স এবং মরফিং সহজ করুন" else "Simplify spring bounces and morph transitions",
                                leadingIcon = Icons.Default.AccessibilityNew,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = config.reduceMotion,
                                        onCheckedChange = { viewModel.setReduceMotion(it) },
                                        contentDescription = "Reduce Motion"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "হ্যাপটিক ফিডব্যাক" else "Haptic Tick Feedback",
                                subtitle = if (isBangla) "প্রতিটি গ্লাস কন্ট্রোল প্রেসে স্পর্শ অনুভূতি" else "Tactile pulse on glass buttons, sliders, and switches",
                                leadingIcon = Icons.Default.Vibration,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = config.hapticsEnabled,
                                        onCheckedChange = { viewModel.setHapticsEnabled(it) },
                                        contentDescription = "Haptic Feedback"
                                    )
                                }
                            )

                            if (config.hapticsEnabled) {
                                Column(modifier = Modifier.padding(horizontal = 6.dp)) {
                                    Text(
                                        text = if (isBangla) "হ্যাপটিক শক্তি" else "Haptic Strength (${(config.hapticStrength * 100).toInt()}%)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    GlassSlider(
                                        value = config.hapticStrength,
                                        onValueChange = { viewModel.setHapticStrength(it) },
                                        valueRange = 0.3f..1.0f,
                                        contentDescription = "Haptic Strength"
                                    )
                                }
                            }

                            FlatTonalRow(
                                title = if (isBangla) "বড় টাচ টার্গেট (54dp+)" else "Larger Touch Targets",
                                subtitle = if (isBangla) "বাটন ও সুইচের আকার ৪৮ডিপি থেকে ৫৪ডিপি করুন" else "Expand controls beyond 48dp minimum to 54dp",
                                leadingIcon = Icons.Default.AccessibilityNew,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = config.largerTouchTargets,
                                        onCheckedChange = { viewModel.setLargerTouchTargets(it) },
                                        contentDescription = "Larger Touch Targets"
                                    )
                                }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "হাই কনট্রাস্ট গ্লাস (WCAG AA+)" else "High Contrast Glass",
                                subtitle = if (isBangla) "লেখার স্পষ্টতার জন্য গ্লাসের বর্ডার ও অপাসিটি বাড়ান" else "Boost specular rim and backdrop contrast for maximum legibility",
                                leadingIcon = Icons.Default.ColorLens,
                                trailingContent = {
                                    GlassSwitch(
                                        checked = config.highContrastGlass,
                                        onCheckedChange = { viewModel.setHighContrastGlass(it) },
                                        contentDescription = "High Contrast Glass"
                                    )
                                }
                            )
                        }
                    }
                }

                // 8. NOTIFICATIONS & PERMISSIONS SECTION
                item {
                    SettingsSectionHeader(if (isBangla) "পারমিশন ও অ্যাক্সেস" else "Notifications & Media Permissions")
                    GlassCard {
                        val statusText = when (permissionLevel) {
                            MediaPermissionLevel.FULL_ACCESS -> if (isBangla) "সম্পূর্ণ মিডিয়া অ্যাক্সেস চালু" else "Full Photo & Video Access Granted"
                            MediaPermissionLevel.PARTIAL_ACCESS -> if (isBangla) "নির্বাচিত ফটো অ্যাক্সেস (Android 14+)" else "Selected Photos Access (Android 14+ Partial)"
                            MediaPermissionLevel.DENIED_OR_PROMPT -> if (isBangla) "স্টুডিও ক্যাটালগ + পারমিশন প্রয়োজন" else "Studio Catalog Active · Tap to Grant Device Media"
                        }
                        FlatTonalRow(
                            title = if (isBangla) "মিডিয়া লাইব্রেরি পারমিশন" else "Media Library Permission State",
                            subtitle = statusText,
                            leadingIcon = Icons.Default.NotificationsActive,
                            trailingContent = {
                                GlassButton(
                                    onClick = onRequestPermissions,
                                    text = if (isBangla) "পরিবর্তন" else "Manage",
                                    variant = GlassVariant.TINTED,
                                    testTag = "settings_manage_permissions_button"
                                )
                            }
                        )
                    }
                }

                // 9. ABOUT SECTION (Glassly, Version 0.01.10 single source of truth from BuildConfig)
                item {
                    SettingsSectionHeader(if (isBangla) "অ্যাপ সম্পর্কে (About)" else "About Glassly")
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Layered Liquid Glass App Logo Emblem
                                LiquidGlassSurface(
                                    modifier = Modifier.size(56.dp),
                                    cornerRadius = 16.dp,
                                    variant = GlassVariant.TINTED
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Glassly Logo",
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "Glassly",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Version ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = config.accentTint,
                                        modifier = Modifier.testTag("about_version_text")
                                    )
                                    Text(
                                        text = "Package: ${BuildConfig.APPLICATION_ID}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            FlatTonalRow(
                                title = if (isBangla) "ওপেন-সোর্স লাইসেন্স" else "Open-Source Licenses",
                                subtitle = "Jetpack Compose, Room, Coil, Media3, AGSL",
                                leadingIcon = Icons.Default.FolderCopy,
                                onClick = { showLicensesSheet = true }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "প্রাইভেসি পলিসি" else "Privacy Policy",
                                subtitle = if (isBangla) "১০০% অন-ডিভাইস প্রসেসিং, কোনো ক্লাউড ট্র্যাকিং নেই" else "100% On-Device Media Processing · Zero Cloud Upload",
                                leadingIcon = Icons.Default.PrivacyTip,
                                onClick = { showPrivacyPolicySheet = true }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "ফিডব্যাক পাঠান" else "Send Feedback",
                                leadingIcon = Icons.Default.Share,
                                onClick = { viewModel.showToast("Feedback log prepared for Glassly v${BuildConfig.VERSION_NAME}") }
                            )

                            FlatTonalRow(
                                title = if (isBangla) "রেটিং দিন" else "Rate Glassly",
                                leadingIcon = Icons.Default.StarRate,
                                onClick = { viewModel.showToast("Thank you for rating Glassly 5 Stars!") }
                            )

                            // Hidden / Debug Build "Glass Gallery" Proving Ground Screen
                            if (BuildConfig.DEBUG) {
                                FlatTonalRow(
                                    title = "Glass Component Gallery (Debug)",
                                    subtitle = "Inspect all 20 Liquid Glass components in every state",
                                    leadingIcon = Icons.Default.BugReport,
                                    onClick = { viewModel.navigateToSubScreen(AppSubScreen.GlassGalleryDebug) },
                                    trailingContent = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                            contentDescription = null,
                                            tint = config.accentTint,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // 10. RESET ALL SETTINGS BUTTON (expands glass confirmation alert)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassButton(
                            onClick = { showResetConfirmAlert = true },
                            text = if (isBangla) "সব সেটিংস রিসেট করুন" else "Reset All Settings",
                            icon = Icons.Default.Restore,
                            isDestructive = true,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "settings_reset_all_button"
                        )
                    }
                }
            }
        },
        floatingOverlayContent = {
            // Collapsing floating glass top bar on scroll
            AnimatedVisibility(
                visible = isScrolled,
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                LiquidGlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    cornerRadius = 999.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "সেটিংস" else "Settings",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "v${BuildConfig.VERSION_NAME}",
                            style = MaterialTheme.typography.labelMedium,
                            color = config.accentTint
                        )
                    }
                }
            }

            // Reset All Settings In-Place Glass Alert
            GlassAlert(
                visible = showResetConfirmAlert,
                title = if (isBangla) "সব সেটিংস রিসেট করবেন?" else "Reset All Settings?",
                message = if (isBangla) {
                    "আপনার গ্লাস স্টাইল, থিম এবং লাইব্রেরি সেটিংস ডিফল্ট অবস্থায় ফিরে যাবে। কোনো ছবি মুছবে না।"
                } else {
                    "This will restore all Liquid Glass tunables, theme, and viewer preferences to factory defaults. Your photos and albums will not be affected."
                },
                confirmText = if (isBangla) "রিসেট করুন" else "Reset Settings",
                dismissText = if (isBangla) "বাতিল" else "Cancel",
                isDestructive = true,
                onConfirm = {
                    showResetConfirmAlert = false
                    viewModel.resetAllSettings()
                },
                onDismiss = { showResetConfirmAlert = false }
            )

            // Change Hidden PIN Glass Alert
            GlassAlert(
                visible = showChangePinAlert,
                title = if (isBangla) "হিডেন অ্যালবাম পিন" else "Set Hidden Album PIN",
                message = if (isBangla) "৪ সংখ্যার একটি নতুন পিন দিন" else "Enter a 4-digit PIN code for unlocking your Hidden Album:",
                confirmText = if (isBangla) "সেভ করুন" else "Save PIN",
                onConfirm = {
                    if (pinInputText.length == 4 && pinInputText.all { it.isDigit() }) {
                        viewModel.updateHiddenPin(pinInputText)
                        showChangePinAlert = false
                    } else {
                        viewModel.showToast("Please enter exactly 4 digits")
                    }
                },
                onDismiss = { showChangePinAlert = false },
                extraContent = {
                    OutlinedTextField(
                        value = pinInputText,
                        onValueChange = { if (it.length <= 4) pinInputText = it },
                        singleLine = true,
                        label = { Text("4-Digit PIN") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )

            // Open-Source Licenses Glass Bottom Sheet
            GlassBottomSheet(
                visible = showLicensesSheet,
                title = "Open-Source Licenses",
                onDismiss = { showLicensesSheet = false }
            ) {
                FlatTonalRow(
                    title = "Android Jetpack Compose & Material 3",
                    subtitle = "Apache License 2.0 · Google LLC"
                )
                FlatTonalRow(
                    title = "Android Graphics Shading Language (AGSL)",
                    subtitle = "Real-time SDF refraction, dispersion & specular bevels"
                )
                FlatTonalRow(
                    title = "Coil Compose Image Loader",
                    subtitle = "Apache License 2.0 · Coil Contributors"
                )
                FlatTonalRow(
                    title = "Room Persistence Library & DataStore",
                    subtitle = "Apache License 2.0 · AndroidX"
                )
            }

            // Privacy Policy Glass Bottom Sheet
            GlassBottomSheet(
                visible = showPrivacyPolicySheet,
                title = "Privacy Policy",
                onDismiss = { showPrivacyPolicySheet = false }
            ) {
                FlatTonalRow(
                    title = "100% On-Device Gallery",
                    subtitle = "Glassly reads your photos and videos locally via Android MediaStore. No media or metadata ever leaves your device."
                )
                FlatTonalRow(
                    title = "Non-Destructive Room Edits",
                    subtitle = "Crop, exposure, filters, and favorite tags are stored locally in an encrypted-ready SQLite database so you can revert to original at any time."
                )
                FlatTonalRow(
                    title = "Locked Hidden Album",
                    subtitle = "Hidden items are excluded from the main grid and protected by your 4-digit PIN and Biometric verification."
                )
            }
        }
    )
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
    )
}

/**
 * LIVE PREVIEW card at the top of Settings (modeled directly on reference PDF page 17 & 40
 * showing the rainbow arch / colorful waves with floating Liquid Glass controls reacting in real time).
 */
@Composable
private fun LiveLiquidGlassPreviewCard(isBangla: Boolean) {
    val config = LocalGlassConfig.current

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isBangla) "লাইভ প্রিভিউ (LIVE PREVIEW)" else "LIVE PREVIEW",
                style = MaterialTheme.typography.labelMedium,
                color = config.accentTint
            )
            Text(
                text = "${config.variant.labelEn} · ${(config.intensity * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(28.dp))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            GlassBackdropProvider(
                backdropLuminanceHint = 0.45f,
                backgroundContent = {
                    // Vivid rainbow arch & cyan/cobalt waves from reference PDF page 17 & 40
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0E2954),
                                    Color(0xFF1F6E8C),
                                    Color(0xFF84A7A1)
                                ),
                                start = Offset.Zero,
                                end = Offset(w, h)
                            )
                        )
                        // Colorful diagonal ribbons behind the glass pill to demonstrate lens refraction
                        val ribbonColors = listOf(
                            Color(0xFF0A84FF),
                            Color(0xFF32ADE6),
                            Color(0xFF34C759),
                            Color(0xFFFFD60A),
                            Color(0xFFFF9F0A),
                            Color(0xFFFF375F)
                        )
                        ribbonColors.forEachIndexed { idx, col ->
                            val startX = w * (0.12f + idx * 0.13f)
                            val path = Path().apply {
                                moveTo(startX, h)
                                quadraticTo(
                                    w * 0.5f,
                                    h * (0.05f + idx * 0.08f),
                                    w,
                                    h * (0.3f + idx * 0.1f)
                                )
                                lineTo(w, h * (0.42f + idx * 0.1f))
                                quadraticTo(
                                    w * 0.5f,
                                    h * (0.18f + idx * 0.08f),
                                    startX + w * 0.11f,
                                    h
                                )
                                close()
                            }
                            drawPath(path = path, color = col)
                        }
                    }
                },
                floatingOverlayContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LiquidGlassSurface(
                                cornerRadius = 999.dp
                            ) {
                                Text(
                                    text = if (isBangla) "লিকুইড গ্লাস লেন্স" else "Liquid Glass Lens",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            LiquidGlassSurface(
                                cornerRadius = 999.dp,
                                variant = GlassVariant.TINTED
                            ) {
                                Text(
                                    text = "iOS 27",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Center floating 3-icon capsule from reference PDF page 17 & 38
                        GlassContainer(spacing = 12.dp) {
                            LiquidGlassSurface(
                                cornerRadius = 999.dp,
                                thicknessMultiplier = 1.25f
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(26.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = "Preview Delete",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.FolderCopy,
                                        contentDescription = "Preview Folder",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Preview Share",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isBangla) {
                                "উপরের স্লাইডার বা স্টাইল পরিবর্তন করে রিয়েল-টাইম প্রতিসরণ দেখুন"
                            } else {
                                "Tilt phone or adjust controls below to see live edge refraction"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun ColorfulSettingsRefractionBackdrop(
    isDark: Boolean,
    accentColor: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val baseBg = if (isDark) Color(0xFF07080C) else Color(0xFFF3F5FA)
        drawRect(color = baseBg)

        // Soft glowing orbs behind the settings cards so glass refraction & dispersion are always visible
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = if (isDark) 0.28f else 0.22f),
                    Color.Transparent
                ),
                center = Offset(w * 0.82f, h * 0.16f),
                radius = w * 0.65f
            ),
            radius = w * 0.65f,
            center = Offset(w * 0.82f, h * 0.16f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF375F).copy(alpha = if (isDark) 0.20f else 0.15f),
                    Color.Transparent
                ),
                center = Offset(w * 0.18f, h * 0.48f),
                radius = w * 0.6f
            ),
            radius = w * 0.6f,
            center = Offset(w * 0.18f, h * 0.48f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF32ADE6).copy(alpha = if (isDark) 0.22f else 0.18f),
                    Color.Transparent
                ),
                center = Offset(w * 0.78f, h * 0.78f),
                radius = w * 0.65f
            ),
            radius = w * 0.65f,
            center = Offset(w * 0.78f, h * 0.78f)
        )
    }
}
