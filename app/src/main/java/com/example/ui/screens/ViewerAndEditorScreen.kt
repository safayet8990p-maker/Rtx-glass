package com.example.ui.screens

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Rational
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LineWeight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Rotate90DegreesCcw
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MediaCategory
import com.example.data.MediaItem
import com.example.data.MediaOverrideEntity
import com.example.glass.AppThemeMode
import com.example.glass.FlatTonalRow
import com.example.glass.GlassAlert
import com.example.glass.GlassBackdropProvider
import com.example.glass.GlassBadge
import com.example.glass.GlassBottomSheet
import com.example.glass.GlassButton
import com.example.glass.GlassButtonShape
import com.example.glass.GlassChip
import com.example.glass.GlassContainer
import com.example.glass.GlassMenu
import com.example.glass.GlassMenuItemData
import com.example.glass.GlassSlider
import com.example.glass.GlassVariant
import com.example.glass.LiquidGlassSurface
import com.example.glass.LocalGlassConfig
import com.example.glass.performGlassHaptic
import com.example.glass.rememberDeviceTiltOffset
import com.example.ui.components.GlasslyMediaThumbnail
import com.example.ui.theme.GlassRed
import com.example.viewmodel.GlasslyViewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PhotoViewerOverlay(
    item: MediaItem,
    allItems: List<MediaItem>,
    viewModel: GlasslyViewModel,
    onClose: () -> Unit,
    onOpenEditor: () -> Unit,
    onStartSlideshow: () -> Unit
) {
    BackHandler(onBack = onClose)
    val context = LocalContext.current
    val view = LocalView.current
    val config = LocalGlassConfig.current

    val keepScreenOn by viewModel.keepScreenOn.collectAsStateWithLifecycle()
    val autoplayMotion by viewModel.autoplayMotion.collectAsStateWithLifecycle()
    val autoplayVideos by viewModel.autoplayVideos.collectAsStateWithLifecycle()
    val loopVideos by viewModel.loopVideos.collectAsStateWithLifecycle()
    val defaultPlaybackSpeed by viewModel.defaultPlaybackSpeed.collectAsStateWithLifecycle()
    val showInfoOnSwipeUp by viewModel.showInfoOnSwipeUp.collectAsStateWithLifecycle()
    val showSpatialControl by viewModel.showSpatialPhotoControl.collectAsStateWithLifecycle()
    val customAlbums by viewModel.customAlbums.collectAsStateWithLifecycle()

    DisposableEffect(keepScreenOn) {
        val prev = view.keepScreenOn
        if (keepScreenOn) {
            view.keepScreenOn = true
        }
        onDispose {
            view.keepScreenOn = prev
        }
    }

    var showChrome by remember { mutableStateOf(true) }
    var showInfoSheet by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    var showAlbumPickerSheet by remember { mutableStateOf(false) }
    var renameText by remember(item.title) { mutableStateOf(item.title) }

    // Spatial Scene 3D Parallax state (video 09:47–11:13)
    var isSpatialSceneActive by remember(item.id) { mutableStateOf(false) }
    var spatialCreatingProgress by remember(item.id) { mutableStateOf(false) }
    var spatialDragTilt by remember(item.id) { mutableStateOf(Offset.Zero) }
    val sensorTilt by rememberDeviceTiltOffset(enabled = isSpatialSceneActive)

    LaunchedEffect(isSpatialSceneActive) {
        if (isSpatialSceneActive) {
            spatialCreatingProgress = true
            delay(420)
            spatialCreatingProgress = false
        } else {
            spatialDragTilt = Offset.Zero
        }
    }

    val spatialTiltX by animateFloatAsState(
        targetValue = if (isSpatialSceneActive) ((sensorTilt.x - 0.5f) * 2.2f + spatialDragTilt.x).coerceIn(-1f, 1f) else 0f,
        animationSpec = spring(stiffness = 180f, dampingRatio = 0.75f),
        label = "spatialTiltX"
    )
    val spatialTiltY by animateFloatAsState(
        targetValue = if (isSpatialSceneActive) ((sensorTilt.y - 0.5f) * 2.2f + spatialDragTilt.y).coerceIn(-1f, 1f) else 0f,
        animationSpec = spring(stiffness = 180f, dampingRatio = 0.75f),
        label = "spatialTiltY"
    )

    // Zoom, Pan & Dismiss gesture state
    var zoomScale by remember(item.id) { mutableFloatStateOf(1f) }
    var panOffset by remember(item.id) { mutableStateOf(Offset.Zero) }
    var dismissOffsetY by remember(item.id) { mutableFloatStateOf(0f) }
    var isMotionPressing by remember(item.id) { mutableStateOf(false) }

    // Video playback state
    var isVideoPlaying by remember(item.id) { mutableStateOf(autoplayVideos) }
    var videoProgress by remember(item.id) { mutableFloatStateOf(0.18f) }
    var videoMuted by remember { mutableStateOf(false) }
    var videoLoop by remember { mutableStateOf(loopVideos) }
    var playbackSpeed by remember { mutableFloatStateOf(defaultPlaybackSpeed) }

    LaunchedEffect(item.id, isVideoPlaying, item.isVideo, playbackSpeed) {
        if (item.isVideo && isVideoPlaying) {
            while (isVideoPlaying) {
                delay(100)
                val step = 0.008f * playbackSpeed
                val next = videoProgress + step
                if (next >= 1f) {
                    if (videoLoop) {
                        videoProgress = 0f
                    } else {
                        videoProgress = 1f
                        isVideoPlaying = false
                    }
                } else {
                    videoProgress = next
                }
            }
        }
    }

    LaunchedEffect(item.id, autoplayMotion) {
        if (item.isLivePhoto && autoplayMotion) {
            isMotionPressing = true
            delay(1300)
            isMotionPressing = false
        }
    }

    val motionScale by animateFloatAsState(
        targetValue = if (isMotionPressing) 1.07f else 1f,
        animationSpec = spring(stiffness = 220f, dampingRatio = 0.72f),
        label = "motion_photo_scale"
    )

    val dismissProgress = (abs(dismissOffsetY) / 600f).coerceIn(0f, 1f)
    val backgroundAlpha = (1f - dismissProgress * 0.65f).coerceIn(0.2f, 1f)
    val dismissScale = (1f - dismissProgress * 0.18f).coerceIn(0.78f, 1f)

    val statusPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val currentIndex = remember(item.id, allItems) {
        allItems.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
    }
    val filmstripListState = rememberLazyListState()
    LaunchedEffect(currentIndex) {
        if (allItems.isNotEmpty() && currentIndex in allItems.indices) {
            filmstripListState.animateScrollToItem((currentIndex - 3).coerceAtLeast(0))
        }
    }

    GlassBackdropProvider(
        backdropLuminanceHint = 0.24f,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (showChrome) MaterialTheme.colorScheme.background.copy(alpha = backgroundAlpha)
                        else Color.Black.copy(alpha = backgroundAlpha)
                    )
                    .testTag("photo_viewer_overlay")
                    .pointerInput(item.id) {
                        detectTapGestures(
                            onTap = { showChrome = !showChrome },
                            onDoubleTap = {
                                if (zoomScale > 1.05f) {
                                    zoomScale = 1f
                                    panOffset = Offset.Zero
                                } else {
                                    zoomScale = 2.5f
                                }
                            },
                            onPress = {
                                if (item.isLivePhoto) {
                                    isMotionPressing = true
                                    tryAwaitRelease()
                                    isMotionPressing = false
                                }
                            }
                        )
                    }
                    .pointerInput(item.id, isSpatialSceneActive) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            if (isSpatialSceneActive && zoom == 1f && zoomScale <= 1.01f) {
                                // Interactive 3D tilt parallax drag while Spatial Scene is active
                                spatialDragTilt = Offset(
                                    x = (spatialDragTilt.x + pan.x / 240f).coerceIn(-0.85f, 0.85f),
                                    y = (spatialDragTilt.y + pan.y / 240f).coerceIn(-0.85f, 0.85f)
                                )
                            } else if (zoom != 1f || zoomScale > 1.01f) {
                                zoomScale = (zoomScale * zoom).coerceIn(1f, 5f)
                                if (zoomScale > 1.02f) {
                                    panOffset += pan
                                } else {
                                    panOffset = Offset.Zero
                                }
                            } else {
                                if (abs(pan.y) > abs(pan.x)) {
                                    dismissOffsetY += pan.y
                                    if (dismissOffsetY < -140f && showInfoOnSwipeUp) {
                                        showInfoSheet = true
                                        dismissOffsetY = 0f
                                    } else if (dismissOffsetY > 260f) {
                                        dismissOffsetY = 0f
                                        onClose()
                                    }
                                } else if (abs(pan.x) > 36f) {
                                    if (pan.x < -36f && currentIndex < allItems.lastIndex) {
                                        viewModel.openViewer(allItems[currentIndex + 1].id)
                                    } else if (pan.x > 36f && currentIndex > 0) {
                                        viewModel.openViewer(allItems[currentIndex - 1].id)
                                    }
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(if (showChrome) 0.78f else 0.92f)
                        .offset {
                            IntOffset(
                                x = panOffset.x.roundToInt(),
                                y = (panOffset.y + dismissOffsetY.coerceAtLeast(0f)).roundToInt()
                            )
                        }
                        .graphicsLayer {
                            val totalScale = zoomScale * motionScale * dismissScale
                            scaleX = totalScale
                            scaleY = totalScale
                            if (isSpatialSceneActive) {
                                rotationY = spatialTiltX * 14f
                                rotationX = -spatialTiltY * 12f
                                cameraDistance = 14f * density
                            }
                        }
                ) {
                    // Background depth layer (moves opposite to tilt for 3D Spatial Scene parallax)
                    GlasslyMediaThumbnail(
                        item = item,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                if (isSpatialSceneActive) {
                                    scaleX = 1.08f
                                    scaleY = 1.08f
                                    translationX = -spatialTiltX * 22f
                                    translationY = -spatialTiltY * 18f
                                }
                            }
                    )

                    // Foreground subject parallax depth layer when Spatial Scene is active (video 09:50–10:35)
                    if (isSpatialSceneActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(28.dp)
                                .graphicsLayer {
                                    scaleX = 1.03f
                                    scaleY = 1.03f
                                    translationX = spatialTiltX * 26f
                                    translationY = spatialTiltY * 22f
                                }
                                .clip(RoundedCornerShape(24.dp))
                                .border(
                                    1.5.dp,
                                    Brush.linearGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.55f),
                                            Color(0xFF0A84FF).copy(alpha = 0.45f)
                                        )
                                    ),
                                    RoundedCornerShape(24.dp)
                                )
                        ) {
                            GlasslyMediaThumbnail(
                                item = item,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        },
        floatingOverlayContent = {
            AnimatedVisibility(
                visible = showChrome,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200)),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 1. Top Glass Bar (Back circle, Center Date/Place Glass Pill, Right More Circle)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = statusPadding + 8.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassButton(
                                onClick = onClose,
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to library",
                                shapeType = GlassButtonShape.CIRCLE,
                                testTag = "viewer_back_button"
                            )

                            LiquidGlassSurface(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .padding(horizontal = 10.dp)
                                    .clickable { showInfoSheet = true },
                                cornerRadius = 999.dp,
                                variant = GlassVariant.REGULAR
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = item.locationName.ifBlank { item.title },
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${item.dateFormattedShort} • ${item.timeFormatted}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                                        maxLines = 1
                                    )
                                }
                            }

                            GlassButton(
                                onClick = { showMoreMenu = true },
                                icon = Icons.Default.MoreHoriz,
                                contentDescription = "More photo actions",
                                shapeType = GlassButtonShape.CIRCLE,
                                testTag = "viewer_more_button"
                            )
                        }

                        // 2. Sub-header glass badges (SPATIAL SCENE / LIVE / PORTRAIT on left, Spatial Hexagon button on right — video 09:50–10:15)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val badgeLabel = when {
                                spatialCreatingProgress -> "CREATING SPATIAL SCENE…"
                                isSpatialSceneActive -> "SPATIAL SCENE"
                                isMotionPressing -> "LIVE PLAYING"
                                item.isLivePhoto -> "LIVE"
                                item.isVideo -> "VIDEO · ${item.durationFormatted ?: formatVideoTime(item.durationSeconds)}"
                                item.category == MediaCategory.RAW -> "RAW · DNG"
                                item.category == MediaCategory.SCREENSHOT -> "SCREENSHOT"
                                else -> "PORTRAIT"
                            }
                            LiquidGlassSurface(
                                modifier = Modifier.clickable {
                                    if (isSpatialSceneActive) {
                                        isSpatialSceneActive = false
                                    } else if (item.isLivePhoto) {
                                        isMotionPressing = !isMotionPressing
                                    }
                                },
                                cornerRadius = 999.dp,
                                variant = if (isSpatialSceneActive || isMotionPressing) GlassVariant.TINTED else GlassVariant.REGULAR
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSpatialSceneActive) Icons.Default.ViewInAr else Icons.Default.MotionPhotosOn,
                                        contentDescription = null,
                                        tint = if (isSpatialSceneActive || isMotionPressing) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = badgeLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp,
                                        color = if (isSpatialSceneActive || isMotionPressing) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (zoomScale > 1.05f) {
                                    GlassBadge(
                                        text = "${String.format(Locale.US, "%.1f", zoomScale)}×",
                                        modifier = Modifier.clickable {
                                            zoomScale = 1f
                                            panOffset = Offset.Zero
                                        }
                                    )
                                }
                                if (item.isEdited) {
                                    GlassBadge(text = "EDITED")
                                }

                                // Top-right Hexagon Spatial Scene button matching video 09:50–11:13
                                if (showSpatialControl && !item.isVideo) {
                                    GlassButton(
                                        onClick = {
                                            view.performGlassHaptic(config, isConfirm = true)
                                            isSpatialSceneActive = !isSpatialSceneActive
                                        },
                                        icon = Icons.Default.Hexagon,
                                        contentDescription = "Toggle 3D Spatial Scene",
                                        shapeType = GlassButtonShape.CIRCLE,
                                        variant = if (isSpatialSceneActive) GlassVariant.TINTED else GlassVariant.REGULAR,
                                        selected = isSpatialSceneActive,
                                        testTag = "viewer_spatial_scene_button"
                                    )
                                }
                            }
                        }
                    }

                    // 3. Bottom area: Video Player Glass Controls (if video) + Filmstrip Scrubber + Bottom Glass Action Bar
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = navPadding + 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (item.isVideo) {
                            VideoPlayerGlassControls(
                                item = item,
                                isPlaying = isVideoPlaying,
                                progress = videoProgress,
                                isMuted = videoMuted,
                                isLooping = videoLoop,
                                speed = playbackSpeed,
                                onTogglePlay = { isVideoPlaying = !isVideoPlaying },
                                onSeek = { videoProgress = it },
                                onToggleMute = { videoMuted = !videoMuted },
                                onToggleLoop = { videoLoop = !videoLoop },
                                onCycleSpeed = {
                                    playbackSpeed = when (playbackSpeed) {
                                        0.5f -> 1.0f
                                        1.0f -> 1.5f
                                        1.5f -> 2.0f
                                        else -> 0.5f
                                    }
                                },
                                onRotateFullscreen = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        activity.requestedOrientation =
                                            if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                                                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                                            } else {
                                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                            }
                                    }
                                },
                                onEnterPip = {
                                    val activity = context as? Activity
                                    if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        runCatching {
                                            val params = PictureInPictureParams.Builder()
                                                .setAspectRatio(Rational(16, 9))
                                                .build()
                                            activity.enterPictureInPictureMode(params)
                                        }.onFailure {
                                            viewModel.showToast("Picture-in-Picture is not supported on this display")
                                        }
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Filmstrip Scrubber
                        LazyRow(
                            state = filmstripListState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("viewer_filmstrip"),
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(allItems, key = { it.id }) { stripItem ->
                                val isCurrent = stripItem.id == item.id
                                val thumbWidth by animateDpAsState(
                                    targetValue = if (isCurrent) 42.dp else 20.dp,
                                    animationSpec = spring(stiffness = 380f, dampingRatio = 0.78f),
                                    label = "filmstrip_thumb_width"
                                )
                                Box(
                                    modifier = Modifier
                                        .width(thumbWidth)
                                        .height(34.dp)
                                        .then(
                                            if (isCurrent) Modifier.padding(horizontal = 4.dp)
                                            else Modifier
                                        )
                                        .clip(RoundedCornerShape(4.dp))
                                        .then(
                                            if (isCurrent) Modifier.border(
                                                1.5.dp,
                                                Color.White.copy(alpha = 0.9f),
                                                RoundedCornerShape(4.dp)
                                            ) else Modifier
                                        )
                                        .clickable { viewModel.openViewer(stripItem.id) }
                                ) {
                                    GlasslyMediaThumbnail(
                                        item = stripItem,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bottom Glass Bar: Left Share circle, Center 3-icon glass capsule (Heart, Info, Edit), Right Trash circle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassButton(
                                onClick = { showShareSheet = true },
                                icon = Icons.Default.Share,
                                contentDescription = "Share photo",
                                shapeType = GlassButtonShape.CIRCLE,
                                testTag = "viewer_share_button"
                            )

                            LiquidGlassSurface(
                                modifier = Modifier.height(48.dp),
                                cornerRadius = 999.dp,
                                variant = GlassVariant.REGULAR
                            ) {
                                GlassContainer(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    spacing = 4.dp
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .clickable { viewModel.toggleFavoriteSingle(item) }
                                            .testTag("viewer_favorite_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = if (item.isFavorite) Color(0xFFFF375F) else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .clickable { showInfoSheet = true }
                                            .testTag("viewer_info_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Photo Info",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .clickable { onOpenEditor() }
                                            .testTag("viewer_edit_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = "Edit Photo",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }

                            GlassButton(
                                onClick = {
                                    viewModel.trashSingle(item.id)
                                    onClose()
                                },
                                icon = Icons.Default.Delete,
                                contentDescription = "Delete photo",
                                shapeType = GlassButtonShape.CIRCLE,
                                testTag = "viewer_delete_button"
                            )
                        }
                    }
                }
            }

            // Top-right More Glass Menu
            GlassMenu(
                expanded = showMoreMenu,
                onDismiss = { showMoreMenu = false },
                items = listOf(
                    GlassMenuItemData("Play Slideshow", Icons.Default.PlayArrow) {
                        onStartSlideshow()
                    },
                    GlassMenuItemData("Add to Album", Icons.Default.FavoriteBorder) {
                        showAlbumPickerSheet = true
                    },
                    GlassMenuItemData(if (item.isHidden) "Unhide Photo" else "Hide Photo", Icons.Default.Info) {
                        if (item.isHidden) viewModel.unhideMedia(setOf(item.id))
                        else viewModel.hideSingle(item.id)
                        onClose()
                    },
                    GlassMenuItemData("Rename File", Icons.Default.Edit) {
                        renameText = item.title
                        showRenameDialog = true
                    },
                    GlassMenuItemData("Set as Wallpaper", Icons.Default.CameraAlt) {
                        viewModel.showToast("Applied '${item.title}' as Home & Lock Wallpaper")
                    },
                    GlassMenuItemData("Print Photo", Icons.Default.Share) {
                        viewModel.showToast("Prepared print layout for '${item.title}'")
                    }
                )
            )

            // Info Glass Bottom Sheet
            GlassBottomSheet(
                visible = showInfoSheet,
                title = "Photo Metadata & EXIF",
                onDismiss = { showInfoSheet = false }
            ) {
                PhotoInfoMetadataContent(
                    item = item,
                    onClose = { showInfoSheet = false }
                )
            }

            // Share Sheet
            GlassBottomSheet(
                visible = showShareSheet,
                title = "Share ${item.title}",
                onDismiss = { showShareSheet = false }
            ) {
                FlatTonalRow(
                    title = "Copy High-Res Link",
                    subtitle = "${item.widthPx} × ${item.heightPx} · ${item.sizeFormatted}",
                    onClick = {
                        showShareSheet = false
                        viewModel.showToast("Copied link for ${item.title}")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlatTonalRow(
                    title = "Add to Album",
                    subtitle = "Albums: ${item.albums.joinToString().ifBlank { "Library" }}",
                    onClick = {
                        showShareSheet = false
                        showAlbumPickerSheet = true
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlatTonalRow(
                    title = "Export Unmodified Original",
                    subtitle = item.filePath,
                    onClick = {
                        showShareSheet = false
                        viewModel.showToast("Exported original to Downloads")
                    }
                )
            }

            // Add to Album Sheet
            GlassBottomSheet(
                visible = showAlbumPickerSheet,
                title = "Move or Copy to Album",
                onDismiss = { showAlbumPickerSheet = false }
            ) {
                val albumNames = (listOf("Aegean Adventure", "Top Moments", "Coastal Days", "Kyoto Autumn") +
                    customAlbums.map { it.name }).distinct()
                albumNames.forEach { album ->
                    FlatTonalRow(
                        title = album,
                        subtitle = if (album in item.albums) "Already in album" else "Add item here",
                        onClick = {
                            viewModel.addMediaToAlbum(setOf(item.id), album)
                            showAlbumPickerSheet = false
                        }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // Rename Glass Alert
            GlassAlert(
                visible = showRenameDialog,
                title = "Rename Media",
                message = "Enter a new filename for this item:",
                confirmText = "Save",
                dismissText = "Cancel",
                onConfirm = {
                    viewModel.renameMedia(item.id, renameText)
                    showRenameDialog = false
                },
                onDismiss = { showRenameDialog = false }
            ) {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
private fun VideoPlayerGlassControls(
    item: MediaItem,
    isPlaying: Boolean,
    progress: Float,
    isMuted: Boolean,
    isLooping: Boolean,
    speed: Float,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleLoop: () -> Unit,
    onCycleSpeed: () -> Unit,
    onRotateFullscreen: () -> Unit,
    onEnterPip: () -> Unit
) {
    val totalSeconds = item.durationSeconds.coerceAtLeast(15)
    val currentSeconds = (totalSeconds * progress).roundToInt()

    LiquidGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        cornerRadius = 24.dp,
        variant = GlassVariant.REGULAR
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause video" else "Play video",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onTogglePlay)
                )
                Text(
                    text = formatVideoTime(currentSeconds),
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
                GlassSlider(
                    value = progress,
                    onValueChange = onSeek,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatVideoTime(totalSeconds),
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassChip(
                    label = "${speed}×",
                    selected = speed != 1.0f,
                    leadingIcon = Icons.Default.Speed,
                    onClick = onCycleSpeed
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassChip(
                        label = if (isMuted) "Muted" else "Sound",
                        selected = !isMuted,
                        leadingIcon = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        onClick = onToggleMute
                    )
                    GlassChip(
                        label = "Loop",
                        selected = isLooping,
                        leadingIcon = Icons.Default.Repeat,
                        onClick = onToggleLoop
                    )
                    Icon(
                        imageVector = Icons.Default.ScreenRotation,
                        contentDescription = "Rotate fullscreen",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onRotateFullscreen)
                            .padding(5.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.PictureInPictureAlt,
                        contentDescription = "Picture in Picture",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onEnterPip)
                            .padding(5.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoInfoMetadataContent(
    item: MediaItem,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FlatTonalRow(
            title = item.title,
            subtitle = "${item.dateFormattedLong} • ${item.timeFormatted}"
        )

        FlatTonalRow(
            title = item.cameraModel,
            subtitle = item.lensInfo,
            leadingIcon = Icons.Default.CameraAlt,
            trailingContent = {
                GlassBadge(text = "${String.format(Locale.US, "%.1f", (item.widthPx * item.heightPx) / 1_000_000f)} MP")
            }
        )

        FlatTonalRow(
            title = "Resolution & Size",
            subtitle = "${item.widthPx} × ${item.heightPx} px • ${item.sizeFormatted}"
        )

        FlatTonalRow(
            title = "File Path",
            subtitle = item.filePath
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF1D4E5F),
                            Color(0xFF28705A),
                            Color(0xFF1A3A4A)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridColor = Color.White.copy(alpha = 0.12f)
                for (i in 1..4) {
                    val y = size.height * (i / 5f)
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5f)
                    val x = size.width * (i / 5f)
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.5f)
                }
            }
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF375F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = item.locationName.ifBlank { "San Francisco, CA" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = String.format(Locale.US, "%.4f° N, %.4f° W • GPS Verified", item.latitude, item.longitude),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.78f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        GlassButton(
            onClick = onClose,
            text = "Done",
            variant = GlassVariant.TINTED,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private enum class EditorToolMode(val title: String) {
    ADJUST("ADJUST"),
    STYLES("STYLES"),
    CROP("CROP"),
    MARKUP("MARKUP")
}

private enum class AdjustParameter(val label: String, val min: Float, val max: Float) {
    EXPOSURE("EXPOSURE", -100f, 100f),
    CONTRAST("CONTRAST", -100f, 100f),
    SATURATION("SATURATION", -100f, 100f),
    WARMTH("WARMTH", -100f, 100f),
    SHARPNESS("SHARPNESS", 0f, 100f),
    VIGNETTE("VIGNETTE", 0f, 100f)
}

@Composable
fun PhotoEditorOverlay(
    item: MediaItem,
    viewModel: GlasslyViewModel,
    onClose: () -> Unit
) {
    BackHandler(onBack = onClose)
    val config = LocalGlassConfig.current
    val view = LocalView.current
    val density = LocalDensity.current
    val statusPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val editorAppearanceMode by viewModel.editorAppearanceMode.collectAsStateWithLifecycle()
    val copiedEditsBuffer by viewModel.copiedEditsBuffer.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()

    // Independent Editor Lighting Appearance Override matching video 08:54–09:46
    val isEditorDark = when (editorAppearanceMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> systemDark
    }
    val editorBgColor = if (isEditorDark) Color.Black else Color(0xFFF5F6FA)
    val editorFgColor = if (isEditorDark) Color.White else Color(0xFF14151A)

    var currentTool by remember { mutableStateOf(EditorToolMode.ADJUST) }
    var selectedParam by remember { mutableStateOf(AdjustParameter.EXPOSURE) }
    var showSaveChoiceSheet by remember { mutableStateOf(false) }
    var showEditorMoreMenu by remember { mutableStateOf(false) }
    var showAppearanceSubMenu by remember { mutableStateOf(false) }
    var showMarkupPlusMenu by remember { mutableStateOf(false) }

    // Interactive Magnifying Loupe State (video 11:14–12:20)
    var isLoupeActive by remember { mutableStateOf(false) }
    var loupeCenterNormX by remember { mutableFloatStateOf(0.5f) }
    var loupeCenterNormY by remember { mutableFloatStateOf(0.45f) }
    var loupeRadiusDp by remember { mutableFloatStateOf(96f) }
    var loupeMagnification by remember { mutableFloatStateOf(2.2f) }
    var loupeStrokeColor by remember { mutableStateOf<Color?>(Color.White) }
    var loupeStrokeWidthPt by remember { mutableIntStateOf(6) }
    var showLoupeStrokePopup by remember { mutableStateOf(false) }

    // Non-destructive working override state initialized from item.overrideState
    var working by remember(item.id, item.overrideState) {
        mutableStateOf(item.overrideState)
    }

    val undoStack = remember { mutableStateListOf<MediaOverrideEntity>() }
    val redoStack = remember { mutableStateListOf<MediaOverrideEntity>() }

    fun updateWorking(transform: (MediaOverrideEntity) -> MediaOverrideEntity) {
        undoStack.add(working)
        redoStack.clear()
        working = transform(working)
    }

    val compareInteraction = remember { MutableInteractionSource() }
    val isComparingOriginal by compareInteraction.collectIsPressedAsState()

    var photoContainerSize by remember { mutableStateOf(IntSize.Zero) }

    GlassBackdropProvider(
        backdropLuminanceHint = if (isEditorDark) 0.15f else 0.85f,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(editorBgColor)
                    .testTag("photo_editor_overlay")
                    .padding(
                        top = statusPadding + 104.dp,
                        bottom = navPadding + 225.dp,
                        start = 24.dp,
                        end = 24.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                val targetAspect = when (working.cropRatioLabel) {
                    "1:1" -> 1f
                    "4:3" -> 4f / 3f
                    "16:9" -> 16f / 9f
                    "9:16" -> 9f / 16f
                    else -> null
                }

                Box(
                    modifier = Modifier
                        .then(
                            if (targetAspect != null) Modifier.aspectRatio(targetAspect)
                            else Modifier.fillMaxSize()
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .onSizeChanged { photoContainerSize = it }
                ) {
                    GlasslyMediaThumbnail(
                        item = item,
                        overridePreview = working,
                        ignoreEdits = isComparingOriginal,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Interactive Magnifying Loupe Overlay matching video 11:30–12:19
                    if (isLoupeActive && photoContainerSize.width > 0 && photoContainerSize.height > 0) {
                        val containerW = photoContainerSize.width.toFloat()
                        val containerH = photoContainerSize.height.toFloat()
                        val radiusPx = with(density) { loupeRadiusDp.dp.toPx() }
                        val centerX = loupeCenterNormX * containerW
                        val centerY = loupeCenterNormY * containerH
                        val diameterDp = (loupeRadiusDp * 2f).dp

                        // Magnified Circular Loupe Lens
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        x = (centerX - radiusPx).roundToInt(),
                                        y = (centerY - radiusPx).roundToInt()
                                    )
                                }
                                .size(diameterDp)
                                .clip(CircleShape)
                                .then(
                                    if (loupeStrokeColor != null && loupeStrokeWidthPt > 0) {
                                        Modifier.border(
                                            width = (loupeStrokeWidthPt * 0.75f).dp,
                                            color = loupeStrokeColor!!,
                                            shape = CircleShape
                                        )
                                    } else Modifier
                                )
                                .pointerInput(containerW, containerH) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        loupeCenterNormX = (loupeCenterNormX + dragAmount.x / containerW).coerceIn(0.12f, 0.88f)
                                        loupeCenterNormY = (loupeCenterNormY + dragAmount.y / containerH).coerceIn(0.12f, 0.88f)
                                    }
                                }
                                .testTag("editor_loupe_lens")
                        ) {
                            // Render zoomed underlying image centered on (loupeCenterNormX, loupeCenterNormY)
                            GlasslyMediaThumbnail(
                                item = item,
                                overridePreview = working,
                                ignoreEdits = isComparingOriginal,
                                modifier = Modifier
                                    .size(
                                        width = with(density) { containerW.toDp() },
                                        height = with(density) { containerH.toDp() }
                                    )
                                    .graphicsLayer {
                                        scaleX = loupeMagnification
                                        scaleY = loupeMagnification
                                        val shiftX = (0.5f - loupeCenterNormX) * containerW * loupeMagnification
                                        val shiftY = (0.5f - loupeCenterNormY) * containerH * loupeMagnification
                                        translationX = shiftX - (containerW / 2f - radiusPx)
                                        translationY = shiftY - (containerH / 2f - radiusPx)
                                    }
                            )
                        }

                        // Blue Radius Handle on Bottom-Left / 7-o'clock of Loupe Rim (video 11:36)
                        val blueAngleRad = (135.0 * PI / 180.0).toFloat()
                        val blueHandleX = centerX + cos(blueAngleRad) * radiusPx
                        val blueHandleY = centerY + sin(blueAngleRad) * radiusPx
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        x = (blueHandleX - with(density) { 11.dp.toPx() }).roundToInt(),
                                        y = (blueHandleY - with(density) { 11.dp.toPx() }).roundToInt()
                                    )
                                }
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0A84FF))
                                .border(2.5.dp, Color.White, CircleShape)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        val delta = (-dragAmount.x + dragAmount.y) * 0.35f
                                        loupeRadiusDp = (loupeRadiusDp + delta).coerceIn(58f, 150f)
                                    }
                                }
                                .testTag("loupe_blue_radius_handle")
                        )

                        // Green Magnification Clock-Arc Handle (12 o'clock = 1.4x min zoom, 3 o'clock = 3.6x max zoom) — video 11:40–11:52
                        val zoomFraction = ((loupeMagnification - 1.4f) / (3.6f - 1.4f)).coerceIn(0f, 1f)
                        val greenAngleDeg = -90f + zoomFraction * 90f // -90 deg (12 o'clock) to 0 deg (3 o'clock)
                        val greenAngleRad = (greenAngleDeg * PI / 180.0).toFloat()
                        val greenHandleX = centerX + cos(greenAngleRad) * radiusPx
                        val greenHandleY = centerY + sin(greenAngleRad) * radiusPx
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        x = (greenHandleX - with(density) { 11.dp.toPx() }).roundToInt(),
                                        y = (greenHandleY - with(density) { 11.dp.toPx() }).roundToInt()
                                    )
                                }
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34C759))
                                .border(2.5.dp, Color.White, CircleShape)
                                .pointerInput(centerX, centerY) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        val deltaZoom = (dragAmount.x + dragAmount.y) * 0.012f
                                        loupeMagnification = (loupeMagnification + deltaZoom).coerceIn(1.4f, 3.6f)
                                    }
                                }
                                .testTag("loupe_green_zoom_handle")
                        )
                    }

                    if (currentTool == EditorToolMode.CROP) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeColor = Color.White.copy(alpha = 0.85f)
                            drawRect(
                                color = strokeColor,
                                style = Stroke(width = 2.dp.toPx())
                            )
                            val thirdW = size.width / 3f
                            val thirdH = size.height / 3f
                            for (i in 1..2) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.35f),
                                    start = Offset(thirdW * i, 0f),
                                    end = Offset(thirdW * i, size.height),
                                    strokeWidth = 1.dp.toPx()
                                )
                                drawLine(
                                    color = Color.White.copy(alpha = 0.35f),
                                    start = Offset(0f, thirdH * i),
                                    end = Offset(size.width, thirdH * i),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            val cornerLen = 22.dp.toPx()
                            val cornerStroke = 4.dp.toPx()
                            drawLine(Color.White, Offset(0f, 0f), Offset(cornerLen, 0f), cornerStroke)
                            drawLine(Color.White, Offset(0f, 0f), Offset(0f, cornerLen), cornerStroke)
                            drawLine(Color.White, Offset(size.width, 0f), Offset(size.width - cornerLen, 0f), cornerStroke)
                            drawLine(Color.White, Offset(size.width, 0f), Offset(size.width, cornerLen), cornerStroke)
                            drawLine(Color.White, Offset(0f, size.height), Offset(cornerLen, size.height), cornerStroke)
                            drawLine(Color.White, Offset(0f, size.height), Offset(0f, size.height - cornerLen), cornerStroke)
                            drawLine(Color.White, Offset(size.width, size.height), Offset(size.width - cornerLen, 0f), cornerStroke)
                            drawLine(Color.White, Offset(size.width, size.height), Offset(size.width, size.height - cornerLen), cornerStroke)
                        }
                    }

                    if (isComparingOriginal) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 12.dp)
                        ) {
                            GlassBadge(text = "ORIGINAL")
                        }
                    }
                }
            }
        },
        floatingOverlayContent = {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top 2-Row Editor Header matching video 08:54–09:46 & 11:18
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = statusPadding + 8.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassButton(
                            onClick = onClose,
                            text = "Cancel",
                            variant = GlassVariant.REGULAR,
                            testTag = "editor_cancel_button"
                        )

                        if (item.isEdited || working.isEdited) {
                            GlassButton(
                                onClick = {
                                    viewModel.revertPhotoEdit(item.id)
                                    onClose()
                                },
                                text = "Revert",
                                variant = GlassVariant.CLEAR,
                                testTag = "editor_revert_button"
                            )
                        }

                        // Top-right Markup Pen button + "..." Menu button + Iconic Yellow "Done" Pill Button
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassButton(
                                onClick = {
                                    view.performGlassHaptic(config)
                                    currentTool = EditorToolMode.MARKUP
                                },
                                icon = Icons.Default.Create,
                                contentDescription = "Markup Tool",
                                shapeType = GlassButtonShape.CIRCLE,
                                selected = currentTool == EditorToolMode.MARKUP,
                                testTag = "editor_markup_top_button"
                            )

                            GlassButton(
                                onClick = {
                                    view.performGlassHaptic(config)
                                    showEditorMoreMenu = !showEditorMoreMenu
                                },
                                icon = Icons.Default.MoreHoriz,
                                contentDescription = "Editor Options & Appearance",
                                shapeType = GlassButtonShape.CIRCLE,
                                testTag = "editor_more_button"
                            )

                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Color(0xFFFFD60A))
                                    .clickable { showSaveChoiceSheet = true }
                                    .padding(horizontal = 20.dp)
                                    .testTag("editor_done_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Done",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    // Row 2: Undo/Redo glass capsule, Mode title, Hold-to-Compare button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LiquidGlassSurface(
                            modifier = Modifier.height(36.dp),
                            cornerRadius = 999.dp,
                            variant = GlassVariant.REGULAR
                        ) {
                            GlassContainer(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                spacing = 6.dp
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Undo,
                                    contentDescription = "Undo edit",
                                    tint = if (undoStack.isNotEmpty()) editorFgColor else editorFgColor.copy(alpha = 0.35f),
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .clickable(enabled = undoStack.isNotEmpty()) {
                                            val prev = undoStack.removeAt(undoStack.lastIndex)
                                            redoStack.add(working)
                                            working = prev
                                        }
                                        .padding(4.dp)
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Redo,
                                    contentDescription = "Redo edit",
                                    tint = if (redoStack.isNotEmpty()) editorFgColor else editorFgColor.copy(alpha = 0.35f),
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .clickable(enabled = redoStack.isNotEmpty()) {
                                            val next = redoStack.removeAt(redoStack.lastIndex)
                                            undoStack.add(working)
                                            working = next
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }

                        Text(
                            text = if (currentTool == EditorToolMode.ADJUST) selectedParam.label else currentTool.title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp,
                            color = editorFgColor.copy(alpha = 0.9f)
                        )

                        LiquidGlassSurface(
                            modifier = Modifier
                                .height(36.dp)
                                .clickable(
                                    interactionSource = compareInteraction,
                                    indication = null,
                                    onClick = {}
                                )
                                .testTag("editor_compare_button"),
                            cornerRadius = 999.dp,
                            variant = if (isComparingOriginal) GlassVariant.TINTED else GlassVariant.REGULAR
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Compare,
                                    contentDescription = "Hold to Compare Original",
                                    tint = editorFgColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Compare",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = editorFgColor
                                )
                            }
                        }
                    }
                }

                // Floating Loupe Stroke Color / Width Toolbar when Loupe is active (video 11:54–12:15)
                AnimatedVisibility(
                    visible = isLoupeActive,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = navPadding + 160.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedVisibility(visible = showLoupeStrokePopup) {
                            LiquidGlassSurface(
                                cornerRadius = 22.dp,
                                thicknessMultiplier = 1.35f
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // No Stroke Slash Circle (video 12:08)
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.14f))
                                            .border(
                                                width = if (loupeStrokeColor == null) 2.dp else 1.dp,
                                                color = if (loupeStrokeColor == null) Color(0xFFFFD60A) else Color.White.copy(alpha = 0.5f),
                                                shape = CircleShape
                                            )
                                            .clickable { loupeStrokeColor = null }
                                            .testTag("loupe_no_stroke_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Block,
                                            contentDescription = "No Stroke",
                                            tint = GlassRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    val strokeSwatches = listOf(
                                        Color.White,
                                        Color(0xFFFFD60A),
                                        Color(0xFF0A84FF),
                                        Color(0xFF34C759),
                                        Color(0xFFFF375F),
                                        Color.Black
                                    )
                                    strokeSwatches.forEach { col ->
                                        val isSelected = loupeStrokeColor == col
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(col)
                                                .border(
                                                    width = if (isSelected) 2.5.dp else 1.dp,
                                                    color = if (isSelected) Color(0xFFFFD60A) else Color.White.copy(alpha = 0.5f),
                                                    shape = CircleShape
                                                )
                                                .clickable { loupeStrokeColor = col }
                                        )
                                    }

                                    // Stroke Width Stepper (- / pt / +) matching video 12:02
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Decrease Stroke Width",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    loupeStrokeWidthPt = (loupeStrokeWidthPt - 2).coerceAtLeast(0)
                                                }
                                        )
                                        Text(
                                            text = "${loupeStrokeWidthPt}pt",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Increase Stroke Width",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    loupeStrokeWidthPt = (loupeStrokeWidthPt + 2).coerceAtMost(24)
                                                }
                                        )
                                    }
                                }
                            }
                        }

                        // Floating Loupe Action Pill (Stroke Line icon, Zoom Badge, Duplicate, Delete)
                        LiquidGlassSurface(
                            cornerRadius = 999.dp,
                            thicknessMultiplier = 1.3f
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LineWeight,
                                    contentDescription = "Loupe Stroke Style",
                                    tint = if (showLoupeStrokePopup) config.accentTint else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { showLoupeStrokePopup = !showLoupeStrokePopup }
                                        .testTag("loupe_stroke_button")
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%.1f", loupeMagnification)}×",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF34C759)
                                )
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Center Loupe",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clickable {
                                            loupeCenterNormX = 0.5f
                                            loupeCenterNormY = 0.45f
                                            viewModel.showToast("Centered Magnifying Loupe")
                                        }
                                )
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Loupe",
                                    tint = GlassRed,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clickable {
                                            isLoupeActive = false
                                            showLoupeStrokePopup = false
                                        }
                                        .testTag("loupe_delete_button")
                                )
                            }
                        }
                    }
                }

                // Bottom Controls Area (Tool-Specific Controls + Bottom Glass Tool Pill)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = navPadding + 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (currentTool) {
                        EditorToolMode.ADJUST -> {
                            AdjustToolControls(
                                working = working,
                                selectedParam = selectedParam,
                                onSelectParam = { selectedParam = it },
                                onAutoEnhance = {
                                    updateWorking {
                                        it.copy(
                                            exposure = 16f,
                                            contrast = 18f,
                                            saturation = 22f,
                                            warmth = 10f,
                                            sharpness = 25f
                                        )
                                    }
                                },
                                onValueChange = { newVal ->
                                    updateWorking { cur ->
                                        when (selectedParam) {
                                            AdjustParameter.EXPOSURE -> cur.copy(exposure = newVal)
                                            AdjustParameter.CONTRAST -> cur.copy(contrast = newVal)
                                            AdjustParameter.SATURATION -> cur.copy(saturation = newVal)
                                            AdjustParameter.WARMTH -> cur.copy(warmth = newVal)
                                            AdjustParameter.SHARPNESS -> cur.copy(sharpness = newVal)
                                            AdjustParameter.VIGNETTE -> cur.copy(vignette = newVal)
                                        }
                                    }
                                }
                            )
                        }

                        EditorToolMode.STYLES -> {
                            StylesFilterCarousel(
                                item = item,
                                working = working,
                                selectedFilter = working.filterName,
                                onSelectFilter = { filter ->
                                    updateWorking { it.copy(filterName = filter) }
                                }
                            )
                        }

                        EditorToolMode.CROP -> {
                            CropAndStraightenControls(
                                working = working,
                                onUpdate = { transform -> updateWorking(transform) }
                            )
                        }

                        EditorToolMode.MARKUP -> {
                            MarkupTextAndLoupeControls(
                                working = working,
                                isLoupeActive = isLoupeActive,
                                onOpenMarkupPlusMenu = { showMarkupPlusMenu = true },
                                onUpdate = { transform -> updateWorking(transform) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom Floating Glass Tool Mode Pill (Adjust / Styles / Crop / Markup)
                    LiquidGlassSurface(
                        modifier = Modifier.height(50.dp),
                        cornerRadius = 999.dp,
                        variant = GlassVariant.REGULAR
                    ) {
                        GlassContainer(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
                            spacing = 4.dp
                        ) {
                            EditorToolMode.entries.forEach { mode ->
                                val isSelected = currentTool == mode
                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(
                                            if (isSelected) editorFgColor.copy(alpha = 0.18f)
                                            else Color.Transparent
                                        )
                                        .clickable { currentTool = mode }
                                        .padding(horizontal = 16.dp)
                                        .testTag("editor_tab_${mode.name.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = mode.title.lowercase().replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) editorFgColor else editorFgColor.copy(alpha = 0.65f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Editor Top-Right "..." Menu with Appearance (System / Dark / Light) matching video 08:54–09:46
            AnimatedVisibility(visible = showEditorMoreMenu) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { showEditorMoreMenu = false }
                        )
                        .padding(top = statusPadding + 56.dp, end = 16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    LiquidGlassSurface(
                        modifier = Modifier
                            .widthIn(min = 246.dp, max = 278.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {}
                            )
                            .testTag("editor_options_popover"),
                        cornerRadius = 26.dp,
                        thicknessMultiplier = 1.45f
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            EditorMenuRow(
                                title = "Copy Edits",
                                icon = Icons.Default.ContentCopy,
                                onClick = {
                                    viewModel.copyEditsFrom(working)
                                    showEditorMoreMenu = false
                                }
                            )
                            EditorMenuRow(
                                title = "Paste Edits",
                                icon = Icons.Default.ContentPaste,
                                enabled = copiedEditsBuffer != null,
                                onClick = {
                                    val buf = copiedEditsBuffer
                                    if (buf != null) {
                                        updateWorking {
                                            it.copy(
                                                exposure = buf.exposure,
                                                contrast = buf.contrast,
                                                saturation = buf.saturation,
                                                warmth = buf.warmth,
                                                sharpness = buf.sharpness,
                                                vignette = buf.vignette,
                                                filterName = buf.filterName
                                            )
                                        }
                                        viewModel.showToast("Pasted Edits")
                                    }
                                    showEditorMoreMenu = false
                                }
                            )
                            EditorMenuRow(
                                title = "Save as Duplicate",
                                icon = Icons.Default.Add,
                                onClick = {
                                    showEditorMoreMenu = false
                                    viewModel.savePhotoEdit(item, working, saveAsCopy = true)
                                    onClose()
                                }
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )

                            // Expandable "Appearance" Submenu (System, Dark, Light) — video 09:05–09:45
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        view.performGlassHaptic(config)
                                        showAppearanceSubMenu = !showAppearanceSubMenu
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("editor_appearance_row"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (showAppearanceSubMenu) Icons.Default.ExpandMore else Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Appearance",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Contrast,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            AnimatedVisibility(visible = showAppearanceSubMenu) {
                                Column(
                                    modifier = Modifier.padding(start = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    listOf(
                                        AppThemeMode.SYSTEM to "System",
                                        AppThemeMode.DARK to "Dark",
                                        AppThemeMode.LIGHT to "Light"
                                    ).forEach { (mode, label) ->
                                        val isSelected = editorAppearanceMode == mode
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    view.performGlassHaptic(config)
                                                    viewModel.setEditorAppearanceMode(mode)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                                .testTag("editor_appearance_${label.lowercase()}"),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(modifier = Modifier.width(18.dp)) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = config.accentTint,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )

                            EditorMenuRow(
                                title = "Undo",
                                icon = Icons.AutoMirrored.Filled.Undo,
                                enabled = undoStack.isNotEmpty(),
                                onClick = {
                                    if (undoStack.isNotEmpty()) {
                                        val prev = undoStack.removeAt(undoStack.lastIndex)
                                        redoStack.add(working)
                                        working = prev
                                    }
                                    showEditorMoreMenu = false
                                }
                            )
                            EditorMenuRow(
                                title = "Redo",
                                icon = Icons.AutoMirrored.Filled.Redo,
                                enabled = redoStack.isNotEmpty(),
                                onClick = {
                                    if (redoStack.isNotEmpty()) {
                                        val next = redoStack.removeAt(redoStack.lastIndex)
                                        undoStack.add(working)
                                        working = next
                                    }
                                    showEditorMoreMenu = false
                                }
                            )
                            EditorMenuRow(
                                title = "Red-Eye",
                                icon = Icons.Default.RemoveRedEye,
                                onClick = {
                                    showEditorMoreMenu = false
                                    viewModel.showToast("Red-Eye correction active: tap eyes on photo")
                                }
                            )
                        }
                    }
                }
            }

            // Markup "+" Menu (Image Description, Add Sticker, Add Text, Add Signature, Add Shape, Add Loupe) — video 11:25
            GlassMenu(
                expanded = showMarkupPlusMenu,
                onDismiss = { showMarkupPlusMenu = false },
                headerTitle = "Markup Tools",
                items = listOf(
                    GlassMenuItemData(
                        title = "Image Description",
                        icon = Icons.Default.Description,
                        onClick = { updateWorking { it.copy(markupText = "Accessibility Description Added") } }
                    ),
                    GlassMenuItemData(
                        title = "Add Sticker",
                        icon = Icons.Default.EmojiEmotions,
                        onClick = { updateWorking { it.copy(markupText = "✨ Golden Hour") } }
                    ),
                    GlassMenuItemData(
                        title = "Add Text",
                        icon = Icons.Default.TextFields,
                        onClick = { updateWorking { it.copy(markupText = "Shot on Glassly") } }
                    ),
                    GlassMenuItemData(
                        title = "Add Signature",
                        icon = Icons.Default.Brush,
                        onClick = { updateWorking { it.copy(markupText = "Signed · PG") } }
                    ),
                    GlassMenuItemData(
                        title = "Add Shape",
                        icon = Icons.Default.Hexagon,
                        onClick = { viewModel.showToast("Added Vector Shape overlay") }
                    ),
                    GlassMenuItemData(
                        title = "Add Loupe",
                        icon = Icons.Default.Search,
                        isSelected = isLoupeActive,
                        onClick = {
                            isLoupeActive = true
                            showLoupeStrokePopup = true
                        }
                    )
                )
            )

            // Non-Destructive Save Choice Sheet (Save Overwrite vs Save as Copy)
            GlassBottomSheet(
                visible = showSaveChoiceSheet,
                title = "Save Non-Destructive Edits",
                onDismiss = { showSaveChoiceSheet = false }
            ) {
                FlatTonalRow(
                    title = "Save Changes",
                    subtitle = "Non-destructively updates this photo (you can Revert anytime)",
                    onClick = {
                        showSaveChoiceSheet = false
                        viewModel.savePhotoEdit(item, working, saveAsCopy = false)
                        onClose()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlatTonalRow(
                    title = "Save as Duplicate",
                    subtitle = "Keeps original untouched and creates '${item.title} (Edited)'",
                    onClick = {
                        showSaveChoiceSheet = false
                        viewModel.savePhotoEdit(item, working, saveAsCopy = true)
                        onClose()
                    }
                )
            }
        }
    )
}

@Composable
private fun EditorMenuRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.38f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun AdjustToolControls(
    working: MediaOverrideEntity,
    selectedParam: AdjustParameter,
    onSelectParam: (AdjustParameter) -> Unit,
    onAutoEnhance: () -> Unit,
    onValueChange: (Float) -> Unit
) {
    val currentValue = when (selectedParam) {
        AdjustParameter.EXPOSURE -> working.exposure
        AdjustParameter.CONTRAST -> working.contrast
        AdjustParameter.SATURATION -> working.saturation
        AdjustParameter.WARMTH -> working.warmth
        AdjustParameter.SHARPNESS -> working.sharpness
        AdjustParameter.VIGNETTE -> working.vignette
    }

    val normalizedDisplay = currentValue.roundToInt()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD60A).copy(alpha = 0.2f))
                    .border(1.5.dp, Color(0xFFFFD60A), CircleShape)
                    .clickable(onClick = onAutoEnhance)
                    .testTag("editor_auto_wand"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoFixHigh,
                    contentDescription = "Auto Enhance",
                    tint = Color(0xFFFFD60A),
                    modifier = Modifier.size(20.dp)
                )
            }

            AdjustParameter.entries.forEach { param ->
                val isSelected = param == selectedParam
                val paramVal = when (param) {
                    AdjustParameter.EXPOSURE -> working.exposure / 100f
                    AdjustParameter.CONTRAST -> working.contrast / 100f
                    AdjustParameter.SATURATION -> working.saturation / 100f
                    AdjustParameter.WARMTH -> working.warmth / 100f
                    AdjustParameter.SHARPNESS -> working.sharpness / 100f
                    AdjustParameter.VIGNETTE -> working.vignette / 100f
                }
                val ringSweep = (paramVal.coerceIn(-1f, 1f)) * 180f

                Box(
                    modifier = Modifier
                        .size(if (isSelected) 50.dp else 44.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color.White.copy(alpha = 0.16f)
                            else Color.White.copy(alpha = 0.08f)
                        )
                        .clickable { onSelectParam(param) },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(3.dp)) {
                        drawCircle(
                            color = Color.White.copy(alpha = if (isSelected) 0.5f else 0.22f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        if (abs(ringSweep) > 2f) {
                            drawArc(
                                color = Color(0xFFFFD60A),
                                startAngle = -90f,
                                sweepAngle = ringSweep,
                                useCenter = false,
                                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }
                    Text(
                        text = if (isSelected) "$normalizedDisplay" else param.label.take(3),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFFFFD60A) else Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TickMarkRulerScrubber(
            value = currentValue,
            valueRange = selectedParam.min..selectedParam.max,
            onValueChange = onValueChange
        )
    }
}

@Composable
private fun TickMarkRulerScrubber(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    val span = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.001f)
    val fraction = ((value - valueRange.start) / span).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .pointerInput(valueRange) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        val deltaFraction = -dragAmount / size.width.coerceAtLeast(1)
                        val newFraction = (fraction + deltaFraction).coerceIn(0f, 1f)
                        onValueChange(valueRange.start + newFraction * span)
                    }
                }
        ) {
            val totalTicks = 41
            val centerX = size.width / 2f
            val offsetPx = (0.5f - fraction) * size.width * 0.7f
            for (i in 0 until totalTicks) {
                val tickNorm = (i - totalTicks / 2) / (totalTicks / 2f)
                val x = centerX + tickNorm * (size.width * 0.44f) + offsetPx
                if (x in 0f..size.width) {
                    val isMajor = i % 5 == 0
                    val distFromCenter = abs(x - centerX) / (size.width / 2f)
                    val alpha = (1f - distFromCenter).coerceIn(0.15f, 0.9f)
                    val tickH = if (isMajor) 14.dp.toPx() else 8.dp.toPx()
                    drawLine(
                        color = Color.White.copy(alpha = alpha),
                        start = Offset(x, (size.height - tickH) / 2f),
                        end = Offset(x, (size.height + tickH) / 2f),
                        strokeWidth = if (isMajor) 2.dp.toPx() else 1.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
            drawLine(
                color = Color(0xFFFFD60A),
                start = Offset(centerX, 0f),
                end = Offset(centerX, size.height),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        GlassSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StylesFilterCarousel(
    item: MediaItem,
    working: MediaOverrideEntity,
    selectedFilter: String,
    onSelectFilter: (String) -> Unit
) {
    val filters = listOf("Original", "Vivid", "Dramatic", "Mono", "Silvertone", "Noir")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filters) { filterName ->
            val isSelected = selectedFilter == filterName
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelectFilter(filterName) }
                    .testTag("filter_chip_${filterName.lowercase()}")
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .then(
                            if (isSelected) Modifier.border(
                                2.dp,
                                Color(0xFFFFD60A),
                                RoundedCornerShape(14.dp)
                            ) else Modifier
                        )
                ) {
                    GlasslyMediaThumbnail(
                        item = item,
                        overridePreview = working.copy(filterName = filterName),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = filterName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color(0xFFFFD60A) else Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun CropAndStraightenControls(
    working: MediaOverrideEntity,
    onUpdate: ((MediaOverrideEntity) -> MediaOverrideEntity) -> Unit
) {
    val ratios = listOf("Free", "1:1", "4:3", "16:9", "9:16")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassButton(
                onClick = {
                    onUpdate { it.copy(rotationDegrees = (it.rotationDegrees + 90f) % 360f) }
                },
                icon = Icons.Default.Rotate90DegreesCcw,
                text = "Rotate",
                testTag = "crop_rotate_button"
            )
            GlassButton(
                onClick = {
                    onUpdate { it.copy(flipHorizontal = !it.flipHorizontal) }
                },
                icon = Icons.Default.Flip,
                text = "Flip",
                testTag = "crop_flip_button"
            )
            GlassBadge(text = "${working.straightenDegrees.roundToInt()}°")
        }

        TickMarkRulerScrubber(
            value = working.straightenDegrees,
            valueRange = -45f..45f,
            onValueChange = { deg ->
                onUpdate { it.copy(straightenDegrees = deg) }
            }
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ratios.forEach { label ->
                GlassChip(
                    label = label,
                    selected = working.cropRatioLabel == label,
                    onClick = { onUpdate { it.copy(cropRatioLabel = label) } }
                )
            }
        }
    }
}

@Composable
private fun MarkupTextAndLoupeControls(
    working: MediaOverrideEntity,
    isLoupeActive: Boolean,
    onOpenMarkupPlusMenu: () -> Unit,
    onUpdate: ((MediaOverrideEntity) -> MediaOverrideEntity) -> Unit
) {
    val presets = listOf("", "Golden Hour ✨", "Shot on Glassly", "Summer '26")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                val label = if (preset.isEmpty()) "None" else preset
                GlassChip(
                    label = label,
                    selected = working.markupText == preset,
                    onClick = { onUpdate { it.copy(markupText = preset) } }
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Bottom-right "+" Markup Button opening the Add Loupe / Add Sticker / Add Text / Add Shape menu (video 11:20–11:28)
        GlassButton(
            onClick = onOpenMarkupPlusMenu,
            icon = Icons.Default.Add,
            contentDescription = "Add Markup Element or Loupe",
            shapeType = GlassButtonShape.CIRCLE,
            variant = if (isLoupeActive) GlassVariant.TINTED else GlassVariant.REGULAR,
            testTag = "editor_markup_plus_button"
        )
    }
}

private fun formatVideoTime(totalSeconds: Int): String {
    val mins = totalSeconds / 60
    val secs = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", mins, secs)
}
