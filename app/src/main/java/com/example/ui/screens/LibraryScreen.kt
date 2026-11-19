package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MediaCategory
import com.example.data.MediaItem
import com.example.glass.AppLanguage
import com.example.glass.FlatTonalRow
import com.example.glass.GlassAlert
import com.example.glass.GlassBackdropProvider
import com.example.glass.GlassBadge
import com.example.glass.GlassBottomSheet
import com.example.glass.GlassButton
import com.example.glass.GlassButtonShape
import com.example.glass.GlassCard
import com.example.glass.GlassMenu
import com.example.glass.GlassMenuItemData
import com.example.glass.GlassProgressLoader
import com.example.glass.GlassScrubberBubble
import com.example.glass.GlassToolbar
import com.example.glass.GlassVariant
import com.example.glass.LiquidGlassSurface
import com.example.glass.LocalGlassConfig
import com.example.glass.performGlassHaptic
import com.example.ui.components.GlasslyMediaThumbnail
import com.example.ui.theme.GlassRed
import com.example.viewmodel.AppSubScreen
import com.example.viewmodel.GlasslyViewModel
import com.example.viewmodel.LibraryFilterType
import com.example.viewmodel.MediaPermissionLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    viewModel: GlasslyViewModel,
    onRequestPermissions: () -> Unit,
    onScrollDirectionChanged: (Boolean) -> Unit,
    scrollToNewestTrigger: Int = 0,
    modifier: Modifier = Modifier
) {
    val config = LocalGlassConfig.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val isBangla = config.language == AppLanguage.BANGLA

    val items by viewModel.visibleLibraryMedia.collectAsStateWithLifecycle()
    val isLoading by viewModel.repository.isLoading.collectAsStateWithLifecycle()
    val gridColumns by viewModel.gridColumns.collectAsStateWithLifecycle()
    val isSquareGrid by viewModel.isSquareGrid.collectAsStateWithLifecycle()
    val showBadges by viewModel.showBadges.collectAsStateWithLifecycle()
    val sortByCaptured by viewModel.sortByCaptured.collectAsStateWithLifecycle()
    val openAtLatest by viewModel.openAtLatest.collectAsStateWithLifecycle()
    val showLocationHeaders by viewModel.showLocationInHeaders.collectAsStateWithLifecycle()
    val includeScreenshots by viewModel.includeScreenshotsInLibrary.collectAsStateWithLifecycle()
    val includeSharedWithYou by viewModel.includeSharedWithYouInLibrary.collectAsStateWithLifecycle()
    val timeSegment by viewModel.libraryTimeSegment.collectAsStateWithLifecycle()
    val activeFilters by viewModel.activeLibraryFilters.collectAsStateWithLifecycle()
    val isSelectionMode by viewModel.isSelectionMode.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedIds.collectAsStateWithLifecycle()
    val customAlbums by viewModel.customAlbums.collectAsStateWithLifecycle()
    val permissionLevel by viewModel.permissionLevel.collectAsStateWithLifecycle()

    var showFilterMenu by remember { mutableStateOf(false) }
    var showSelectionMoreMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmAlert by remember { mutableStateOf(false) }
    var showAddToAlbumSheet by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    var showAdjustDateDialog by remember { mutableStateOf(false) }
    var adjustDateText by remember { mutableStateOf("Nov 19, 2026") }
    var showAdjustLocationDialog by remember { mutableStateOf(false) }
    var adjustLocationText by remember { mutableStateOf("Santorini, Greece") }

    var isRefreshing by remember { mutableStateOf(false) }
    var isFastScrubbing by remember { mutableStateOf(false) }
    var fastScrubFraction by remember { mutableFloatStateOf(1f) }
    var pinchZoomAccumulator by remember { mutableFloatStateOf(1f) }

    val gridState = rememberLazyGridState()

    // Scroll to newest at the bottom on initial load (like iOS Photos)
    var didInitialScroll by remember { mutableStateOf(false) }
    LaunchedEffect(items.size, openAtLatest) {
        if (!didInitialScroll && items.isNotEmpty() && openAtLatest) {
            didInitialScroll = true
            gridState.scrollToItem((items.size - 1).coerceAtLeast(0))
        }
    }

    // Scroll back to newest at bottom when bottom Library tab is tapped (video 2:24)
    LaunchedEffect(scrollToNewestTrigger) {
        if (scrollToNewestTrigger > 0 && items.isNotEmpty()) {
            gridState.animateScrollToItem((items.size - 1).coerceAtLeast(0))
        }
    }

    // Track scroll direction so floating tab bar shrinks/morphs into Years/Months/All when scrolling
    var previousIndex by remember { mutableStateOf(0) }
    var previousOffset by remember { mutableStateOf(0) }
    LaunchedEffect(gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset, timeSegment) {
        val currIndex = gridState.firstVisibleItemIndex
        val currOffset = gridState.firstVisibleItemScrollOffset
        val isScrollingUp = currIndex < previousIndex || (currIndex == previousIndex && currOffset < previousOffset - 12)
        val isNearBottom = currIndex >= (items.size - gridColumns * 4).coerceAtLeast(0)
        onScrollDirectionChanged((isScrollingUp && !isNearBottom) || timeSegment != "All")
        previousIndex = currIndex
        previousOffset = currOffset
    }

    // Compute dynamic date range / subtitle from current visible items (video 2:11–2:27)
    val currentVisibleItem by remember(items) {
        derivedStateOf {
            if (items.isEmpty()) null
            else items.getOrNull(gridState.firstVisibleItemIndex.coerceIn(0, items.lastIndex))
        }
    }

    val headerSubtitle = remember(currentVisibleItem, items, showLocationHeaders, activeFilters, gridState.firstVisibleItemIndex) {
        val base = if (items.isNotEmpty() && gridState.firstVisibleItemIndex <= 2) {
            val oldest = items.first().dateFormattedShort
            val newest = items.last().dateFormattedShort
            "$oldest – $newest"
        } else {
            currentVisibleItem?.let { item ->
                if (showLocationHeaders && item.locationName.isNotBlank()) {
                    "${item.dateFormattedShort} · ${item.locationName}"
                } else {
                    item.dateFormattedShort
                }
            } ?: "${items.size} Items"
        }
        if (activeFilters.isNotEmpty()) {
            val filterLabel = activeFilters.joinToString(", ") {
                if (isBangla) it.labelBn else it.labelEn
            }
            "Filtered by: $filterLabel"
        } else base
    }

    // Effective column count based on Years / Months / Days / All segment + pinch density
    val effectiveColumns = when (timeSegment) {
        "Years" -> 1
        "Months" -> 2
        "Days" -> 2
        else -> gridColumns
    }

    GlassBackdropProvider(
        backdropLuminanceHint = 0.32f,
        backgroundContent = {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    // Pinch-to-zoom changes grid density smoothly between 1 and 5 columns (video 2:55–3:14)
                    .pointerInput(timeSegment, gridColumns) {
                        detectTransformGestures { _, _, zoom, _ ->
                            if (timeSegment == "All") {
                                pinchZoomAccumulator *= zoom
                                if (pinchZoomAccumulator > 1.24f && gridColumns > 1) {
                                    pinchZoomAccumulator = 1f
                                    view.performGlassHaptic(config)
                                    viewModel.setGridColumns(gridColumns - 1)
                                } else if (pinchZoomAccumulator < 0.78f && gridColumns < 5) {
                                    pinchZoomAccumulator = 1f
                                    view.performGlassHaptic(config)
                                    viewModel.setGridColumns(gridColumns + 1)
                                }
                            }
                        }
                    }
            ) {
                if (isLoading && items.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassProgressLoader(
                            label = if (isBangla) "লাইব্রেরি লোড হচ্ছে…" else "Loading Library…"
                        )
                    }
                } else if (items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassCard {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = config.accentTint,
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = if (isBangla) "কোনো ছবি পাওয়া যায়নি" else "No Items Match Filter",
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = if (isBangla) {
                                        "ফিল্টার সরিয়ে সব ছবি দেখুন অথবা মিডিয়া পারমিশন দিন।"
                                    } else {
                                        "Clear the current filters or grant device media access to view all photos and videos."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                GlassButton(
                                    onClick = { viewModel.clearLibraryFilters() },
                                    text = if (isBangla) "ফিল্টার সরান" else "Show All Items",
                                    variant = GlassVariant.TINTED
                                )
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(effectiveColumns),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("library_photo_grid"),
                        contentPadding = PaddingValues(
                            top = 104.dp,
                            bottom = 140.dp,
                            start = if (timeSegment == "All") 0.dp else 14.dp,
                            end = if (timeSegment == "All") 0.dp else 14.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(if (timeSegment == "All") 2.dp else 12.dp),
                        verticalArrangement = Arrangement.spacedBy(if (timeSegment == "All") 2.dp else 12.dp)
                    ) {
                        if (permissionLevel != MediaPermissionLevel.FULL_ACCESS) {
                            item(span = { GridItemSpan(effectiveColumns) }) {
                                Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                                    GlassCard {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (permissionLevel == MediaPermissionLevel.PARTIAL_ACCESS) {
                                                        if (isBangla) "নির্বাচিত ফটো অ্যাক্সেস চালু" else "Partial Photo Access Active"
                                                    } else {
                                                        if (isBangla) "ডিভাইস ফটো যুক্ত করুন" else "Sync Device Camera Roll"
                                                    },
                                                    style = MaterialTheme.typography.titleSmall
                                                )
                                                Text(
                                                    text = if (isBangla) {
                                                        "আপনার ফোনের সব ছবি ও ভিডিও দেখতে অ্যাক্সেস দিন"
                                                    } else {
                                                        "Grant Android 13/14/15 media permission to include local camera photos"
                                                    },
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            GlassButton(
                                                onClick = onRequestPermissions,
                                                text = if (isBangla) "অনুমতি দিন" else "Allow",
                                                variant = GlassVariant.TINTED,
                                                testTag = "library_allow_permission_button"
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        when (timeSegment) {
                            "Years" -> {
                                val groupedByYear = items.groupBy { it.yearLabel }
                                groupedByYear.forEach { (year, yearItems) ->
                                    val cover = yearItems.last()
                                    item(key = "year_$year", span = { GridItemSpan(effectiveColumns) }) {
                                        TimeSegmentHeroCard(
                                            title = year,
                                            subtitle = "${yearItems.size} Items · ${cover.locationName}",
                                            coverItem = cover,
                                            heightDp = 230,
                                            onClick = { viewModel.setLibraryTimeSegment("Months") }
                                        )
                                    }
                                }
                            }
                            "Months" -> {
                                val groupedByMonth = items.groupBy { it.monthYearLabel }
                                groupedByMonth.forEach { (monthLabel, monthItems) ->
                                    val cover = monthItems.last()
                                    item(key = "month_$monthLabel") {
                                        TimeSegmentHeroCard(
                                            title = monthLabel,
                                            subtitle = "${monthItems.size} Items",
                                            coverItem = cover,
                                            heightDp = 190,
                                            onClick = { viewModel.setLibraryTimeSegment("All") }
                                        )
                                    }
                                }
                            }
                            "Days" -> {
                                val groupedByDay = items.groupBy { it.dateFormattedShort }
                                groupedByDay.forEach { (dayLabel, dayItems) ->
                                    val cover = dayItems.last()
                                    item(key = "day_$dayLabel") {
                                        TimeSegmentHeroCard(
                                            title = dayLabel,
                                            subtitle = if (showLocationHeaders) cover.locationName else "${dayItems.size} Photos",
                                            coverItem = cover,
                                            heightDp = 175,
                                            onClick = { viewModel.openViewer(cover.id) }
                                        )
                                    }
                                }
                            }
                            else -> { // "All"
                                items(items, key = { it.id }) { item ->
                                    val isSelected = item.id in selectedIds
                                    LibraryGridThumbnailCell(
                                        item = item,
                                        isSquare = isSquareGrid && effectiveColumns > 1,
                                        showBadges = showBadges,
                                        isSelectionMode = isSelectionMode,
                                        isSelected = isSelected,
                                        accentColor = config.accentTint,
                                        onClick = {
                                            if (isSelectionMode) {
                                                view.performGlassHaptic(config)
                                                viewModel.toggleSelectMedia(item.id)
                                            } else {
                                                viewModel.openViewer(item.id)
                                            }
                                        },
                                        onLongClick = {
                                            view.performGlassHaptic(config, isConfirm = true)
                                            if (!isSelectionMode) {
                                                viewModel.enterSelectionMode(item.id)
                                            } else {
                                                viewModel.toggleSelectMedia(item.id)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Top dark legibility gradient scrim under floating Library title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(118.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.52f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Bottom soft veil under floating tab bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(125.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.38f)
                                )
                            )
                        )
                )
            }
        },
        floatingOverlayContent = {
            // 1. Floating Top Glass Toolbar
            // Tapping top-left Library header/date smoothly jumps to oldest media at index 0 (video 2:11–2:22)
            GlassToolbar(
                title = if (isBangla) "লাইব্রেরি" else "Library",
                subtitle = headerSubtitle,
                onTitleClick = {
                    if (items.isNotEmpty()) {
                        view.performGlassHaptic(config)
                        scope.launch {
                            gridState.animateScrollToItem(0)
                        }
                    }
                },
                actions = {
                    if (!isSelectionMode) {
                        val hasActiveFilter = activeFilters.isNotEmpty()
                        GlassButton(
                            onClick = { showFilterMenu = !showFilterMenu },
                            icon = Icons.Default.FilterList,
                            contentDescription = "Filter & View Options",
                            shapeType = GlassButtonShape.CIRCLE,
                            variant = if (hasActiveFilter) GlassVariant.TINTED else GlassVariant.REGULAR,
                            selected = hasActiveFilter,
                            testTag = "library_filter_button"
                        )
                        GlassButton(
                            onClick = { viewModel.enterSelectionMode() },
                            text = if (isBangla) "সিলেক্ট" else "Select",
                            testTag = "library_select_button"
                        )
                    } else {
                        // Selection mode top-right: circular "..." button + "Cancel" pill matching video 3:40–3:59
                        GlassButton(
                            onClick = { showSelectionMoreMenu = true },
                            icon = Icons.Default.MoreHoriz,
                            contentDescription = "Batch Selection Actions",
                            shapeType = GlassButtonShape.CIRCLE,
                            testTag = "library_selection_more_button"
                        )
                        GlassButton(
                            onClick = { viewModel.exitSelectionMode() },
                            text = if (isBangla) "বাতিল" else "Cancel",
                            testTag = "library_cancel_selection_button"
                        )
                    }
                }
            )

            // 2. Pull-to-refresh indicator
            AnimatedVisibility(
                visible = isRefreshing,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 80.dp)
            ) {
                GlassProgressLoader(label = if (isBangla) "রিফ্রেশ হচ্ছে…" else "Refreshing Library…")
            }

            // 3. Fast Scroll Bar & Glass Date Scrubber Bubble on the right edge
            if (items.size > 12 && timeSegment == "All") {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(0.58f)
                        .width(36.dp)
                        .pointerInput(items.size) {
                            detectVerticalDragGestures(
                                onDragStart = {
                                    isFastScrubbing = true
                                    view.performGlassHaptic(config)
                                },
                                onDragEnd = { isFastScrubbing = false },
                                onDragCancel = { isFastScrubbing = false },
                                onVerticalDrag = { change, _ ->
                                    change.consume()
                                    val frac = (change.position.y / size.height.toFloat()).coerceIn(0f, 1f)
                                    fastScrubFraction = frac
                                    val targetIdx = (frac * (items.size - 1)).roundToInt().coerceIn(0, items.lastIndex)
                                    scope.launch { gridState.scrollToItem(targetIdx) }
                                }
                            )
                        },
                    contentAlignment = Alignment.CenterEnd
                ) {
                    LiquidGlassSurface(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .width(6.dp)
                            .height(48.dp),
                        cornerRadius = 999.dp
                    ) {}
                }

                AnimatedVisibility(
                    visible = isFastScrubbing,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 44.dp)
                ) {
                    GlassScrubberBubble(
                        dateLabel = currentVisibleItem?.monthYearLabel ?: "2026"
                    )
                }
            }

            // 4. Multi-Select Bottom Glass Bar matching video 3:42 (Left Share circle, Center "X Photos Selected", Right Trash circle)
            AnimatedVisibility(
                visible = isSelectionMode,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassButton(
                        onClick = {
                            if (selectedIds.isNotEmpty()) showShareSheet = true
                            else viewModel.showToast("Select at least 1 photo to share")
                        },
                        icon = Icons.Default.Share,
                        contentDescription = "Share Selected",
                        shapeType = GlassButtonShape.CIRCLE,
                        testTag = "action_bar_share"
                    )

                    LiquidGlassSurface(
                        cornerRadius = 999.dp,
                        thicknessMultiplier = 1.25f
                    ) {
                        Text(
                            text = if (selectedIds.isEmpty()) {
                                if (isBangla) "ছবি নির্বাচন করুন" else "Select Items"
                            } else {
                                if (isBangla) "${selectedIds.size}টি ছবি নির্বাচিত" else "${selectedIds.size} Photos Selected"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }

                    GlassButton(
                        onClick = {
                            if (selectedIds.isNotEmpty()) showDeleteConfirmAlert = true
                            else viewModel.showToast("Select items to delete")
                        },
                        icon = Icons.Default.Delete,
                        contentDescription = "Delete Selected",
                        shapeType = GlassButtonShape.CIRCLE,
                        isDestructive = true,
                        testTag = "action_bar_delete"
                    )
                }
            }

            // 5. Unified Expandable Filter, Sort & View Options Liquid Glass Popover (video 1:51–3:37)
            LibraryFilterAndViewOptionsPopover(
                expanded = showFilterMenu,
                onDismiss = { showFilterMenu = false },
                sortByCaptured = sortByCaptured,
                onSetSortByCaptured = { viewModel.setSortByCaptured(it) },
                activeFilters = activeFilters,
                onToggleFilter = { viewModel.toggleLibraryFilter(it) },
                onClearFilters = { viewModel.clearLibraryFilters() },
                gridColumns = gridColumns,
                onZoomIn = {
                    if (gridColumns > 1) viewModel.setGridColumns(gridColumns - 1)
                },
                onZoomOut = {
                    if (gridColumns < 5) viewModel.setGridColumns(gridColumns + 1)
                },
                isSquareGrid = isSquareGrid,
                onToggleSquareGrid = { viewModel.setSquareGrid(!isSquareGrid) },
                includeScreenshots = includeScreenshots,
                onToggleIncludeScreenshots = { viewModel.setIncludeScreenshotsInLibrary(!includeScreenshots) },
                includeSharedWithYou = includeSharedWithYou,
                onToggleIncludeSharedWithYou = { viewModel.setIncludeSharedWithYouInLibrary(!includeSharedWithYou) },
                isBangla = isBangla
            )

            // 6. Selection Mode Top-Right Ellipsis ("...") Batch Menu matching video 3:45
            GlassMenu(
                expanded = showSelectionMoreMenu,
                onDismiss = { showSelectionMoreMenu = false },
                items = listOf(
                    GlassMenuItemData(
                        title = if (isBangla) "কপি করুন" else "Copy",
                        icon = Icons.Default.ContentCopy,
                        onClick = {
                            if (selectedIds.isNotEmpty()) {
                                viewModel.showToast("Copied ${selectedIds.size} item(s) to clipboard")
                                viewModel.exitSelectionMode()
                            }
                        }
                    ),
                    GlassMenuItemData(
                        title = if (isBangla) "প্রিয়তে যোগ করুন" else "Add to Favourites",
                        icon = Icons.Default.FavoriteBorder,
                        onClick = { viewModel.favoriteSelected() }
                    ),
                    GlassMenuItemData(
                        title = if (isBangla) "ডুপ্লিকেট করুন" else "Duplicate",
                        icon = Icons.Default.Add,
                        onClick = { viewModel.duplicateSelected() }
                    ),
                    GlassMenuItemData(
                        title = if (isBangla) "লুকান (Hide)" else "Hide",
                        icon = Icons.Default.VisibilityOff,
                        onClick = {
                            if (selectedIds.isNotEmpty()) {
                                selectedIds.forEach { viewModel.hideSingle(it) }
                                viewModel.exitSelectionMode()
                            }
                        }
                    ),
                    GlassMenuItemData(
                        title = if (isBangla) "স্লাইডশো চালান" else "Play as Slideshow",
                        icon = Icons.Default.PlayArrow,
                        onClick = {
                            val selectedItems = items.filter { it.id in selectedIds }.ifEmpty { items.take(10) }
                            viewModel.exitSelectionMode()
                            viewModel.navigateToSubScreen(
                                AppSubScreen.Slideshow("Selected Slideshow", selectedItems)
                            )
                        }
                    ),
                    GlassMenuItemData(
                        title = if (isBangla) "অ্যালবামে যোগ করুন" else "Add to Album",
                        icon = Icons.Default.Collections,
                        onClick = {
                            if (selectedIds.isNotEmpty()) showAddToAlbumSheet = true
                            else viewModel.showToast("Select items first")
                        }
                    ),
                    GlassMenuItemData(
                        title = if (isBangla) "তারিখ ও সময় পরিবর্তন" else "Adjust Date & Time",
                        icon = Icons.Default.CalendarToday,
                        onClick = {
                            if (selectedIds.isNotEmpty()) showAdjustDateDialog = true
                            else viewModel.showToast("Select items first")
                        }
                    ),
                    GlassMenuItemData(
                        title = if (isBangla) "অবস্থান পরিবর্তন" else "Adjust Location",
                        icon = Icons.Default.EditLocation,
                        onClick = {
                            if (selectedIds.isNotEmpty()) showAdjustLocationDialog = true
                            else viewModel.showToast("Select items first")
                        }
                    ),
                    GlassMenuItemData(
                        title = if (isBangla) "এই ব্যক্তিকে ফিচার করবেন না" else "Don't Feature a Person",
                        icon = Icons.Default.PersonOff,
                        isDestructive = true,
                        onClick = {
                            viewModel.showToast("Updated Featured People preferences")
                            viewModel.exitSelectionMode()
                        }
                    )
                )
            )

            // 7. Delete Selected Confirmation Glass Alert
            GlassAlert(
                visible = showDeleteConfirmAlert,
                title = if (isBangla) "মুছে ফেলবেন?" else "Delete ${selectedIds.size} Items?",
                message = if (isBangla) {
                    "নির্বাচিত ${selectedIds.size}টি আইটেম মুছে ফেলবেন? আপনি ৩০ দিনের মধ্যে 'Recently Deleted' থেকে পুনরুদ্ধার করতে পারবেন।"
                } else {
                    "These items will be moved to Recently Deleted and permanently removed after 30 days."
                },
                confirmText = if (isBangla) "মুছুন" else "Delete",
                dismissText = if (isBangla) "বাতিল" else "Cancel",
                isDestructive = true,
                onConfirm = {
                    showDeleteConfirmAlert = false
                    viewModel.trashSelected()
                },
                onDismiss = { showDeleteConfirmAlert = false }
            )

            // Adjust Date & Time Glass Alert
            GlassAlert(
                visible = showAdjustDateDialog,
                title = "Adjust Date & Time",
                message = "Enter adjusted capture date for ${selectedIds.size} selected item(s):",
                confirmText = "Adjust",
                onConfirm = {
                    showAdjustDateDialog = false
                    viewModel.adjustSelectedDateTime(adjustDateText)
                },
                onDismiss = { showAdjustDateDialog = false }
            ) {
                OutlinedTextField(
                    value = adjustDateText,
                    onValueChange = { adjustDateText = it },
                    singleLine = true,
                    label = { Text("Date & Time") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Adjust Location Glass Alert
            GlassAlert(
                visible = showAdjustLocationDialog,
                title = "Adjust Location",
                message = "Enter location for ${selectedIds.size} selected item(s):",
                confirmText = "Apply",
                onConfirm = {
                    showAdjustLocationDialog = false
                    viewModel.adjustSelectedLocation(adjustLocationText)
                },
                onDismiss = { showAdjustLocationDialog = false }
            ) {
                OutlinedTextField(
                    value = adjustLocationText,
                    onValueChange = { adjustLocationText = it },
                    singleLine = true,
                    label = { Text("City or Landmark") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 8. Add to Album Glass Bottom Sheet
            GlassBottomSheet(
                visible = showAddToAlbumSheet,
                title = if (isBangla) "অ্যালবামে যুক্ত করুন" else "Add ${selectedIds.size} Items to Album",
                onDismiss = { showAddToAlbumSheet = false }
            ) {
                customAlbums.forEach { album ->
                    FlatTonalRow(
                        title = album.name,
                        subtitle = if (album.isPinned) "Pinned Album" else "Custom Album",
                        leadingIcon = Icons.Default.Collections,
                        onClick = {
                            viewModel.addMediaToAlbum(selectedIds, album.name)
                            showAddToAlbumSheet = false
                        }
                    )
                }
            }

            // 9. Share Sheet
            ShareMediaGlassSheet(
                visible = showShareSheet,
                selectedItems = items.filter { it.id in selectedIds }.ifEmpty { items.take(1) },
                onDismiss = { showShareSheet = false },
                onActionTriggered = { msg ->
                    showShareSheet = false
                    viewModel.showToast(msg)
                    viewModel.exitSelectionMode()
                }
            )
        }
    )
}

/**
 * Expandable Liquid Glass Popover replicating the exact iOS 26 Photos top-right filter menu (1:51–3:37):
 * - Sort by Recently Added
 * - Sort by Date Captured
 * - Expandable "Filter" section with multi-select checkmarks (All Items, Favourites, Edited, Photos, Videos, Screenshots, Shared with You, Not in an Album)
 * - Expandable "View Options" section (Zoom In, Zoom Out, Aspect Ratio Grid / Square Photo Grid, and Show: Screenshots / Shared with You)
 */
@Composable
private fun LibraryFilterAndViewOptionsPopover(
    expanded: Boolean,
    onDismiss: () -> Unit,
    sortByCaptured: Boolean,
    onSetSortByCaptured: (Boolean) -> Unit,
    activeFilters: Set<LibraryFilterType>,
    onToggleFilter: (LibraryFilterType) -> Unit,
    onClearFilters: () -> Unit,
    gridColumns: Int,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    isSquareGrid: Boolean,
    onToggleSquareGrid: () -> Unit,
    includeScreenshots: Boolean,
    onToggleIncludeScreenshots: () -> Unit,
    includeSharedWithYou: Boolean,
    onToggleIncludeSharedWithYou: () -> Unit,
    isBangla: Boolean
) {
    val config = LocalGlassConfig.current
    val view = LocalView.current

    var filterSectionExpanded by remember { mutableStateOf(true) }
    var viewOptionsSectionExpanded by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(spring(stiffness = config.springStiffness)) + scaleIn(
            initialScale = 0.35f,
            transformOrigin = TransformOrigin(0.88f, 0f),
            animationSpec = spring(
                dampingRatio = config.springDampingRatio,
                stiffness = config.springStiffness
            )
        ),
        exit = fadeOut(spring(stiffness = config.springStiffness)) + scaleOut(
            targetScale = 0.35f,
            transformOrigin = TransformOrigin(0.88f, 0f),
            animationSpec = spring(stiffness = config.springStiffness)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
                .statusBarsPadding()
                .padding(top = 58.dp, end = 16.dp, start = 16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            LiquidGlassSurface(
                modifier = Modifier
                    .widthIn(min = 258.dp, max = 296.dp)
                    .heightIn(max = 560.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .testTag("library_filter_popover"),
                cornerRadius = 28.dp,
                thicknessMultiplier = 1.45f
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // 1. Sort by Recently Added
                    PopoverMenuRow(
                        title = if (isBangla) "সম্প্রতি যুক্ত অনুযায়ী সাজান" else "Sort by Recently Added",
                        icon = Icons.Default.SwapVert,
                        isSelected = !sortByCaptured,
                        onClick = {
                            view.performGlassHaptic(config)
                            onSetSortByCaptured(false)
                        }
                    )

                    // 2. Sort by Date Captured
                    PopoverMenuRow(
                        title = if (isBangla) "তোলার তারিখ অনুযায়ী সাজান" else "Sort by Date Captured",
                        icon = Icons.Default.CalendarToday,
                        isSelected = sortByCaptured,
                        onClick = {
                            view.performGlassHaptic(config)
                            onSetSortByCaptured(true)
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )

                    // 3. Expandable "Filter" Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                view.performGlassHaptic(config)
                                filterSectionExpanded = !filterSectionExpanded
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(
                                text = if (isBangla) "ফিল্টার (Filter)" else "Filter",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (activeFilters.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(config.accentTint)
                                )
                            }
                            Icon(
                                imageVector = if (filterSectionExpanded) Icons.Default.ExpandMore else Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Toggle Filter Section",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(if (filterSectionExpanded) 20.dp else 14.dp)
                            )
                        }
                    }

                    AnimatedVisibility(visible = filterSectionExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            PopoverMenuRow(
                                title = if (isBangla) "সব আইটেম" else "All Items",
                                icon = Icons.Default.PhotoLibrary,
                                isSelected = activeFilters.isEmpty(),
                                onClick = {
                                    view.performGlassHaptic(config)
                                    onClearFilters()
                                }
                            )
                            val filterRows = listOf(
                                Triple(LibraryFilterType.FAVORITES, Icons.Default.FavoriteBorder, "Favourites"),
                                Triple(LibraryFilterType.EDITED, Icons.Default.Tune, "Edited"),
                                Triple(LibraryFilterType.PHOTOS, Icons.Default.Photo, "Photos"),
                                Triple(LibraryFilterType.VIDEOS, Icons.Default.Videocam, "Videos"),
                                Triple(LibraryFilterType.SCREENSHOTS, Icons.Default.Screenshot, "Screenshots"),
                                Triple(LibraryFilterType.SHARED_WITH_YOU, Icons.Default.People, "Shared with You"),
                                Triple(LibraryFilterType.NOT_IN_ALBUM, Icons.Default.Collections, "Not in an Album")
                            )
                            filterRows.forEach { (type, iconVec, _) ->
                                PopoverMenuRow(
                                    title = if (isBangla) type.labelBn else type.labelEn,
                                    icon = iconVec,
                                    isSelected = type in activeFilters,
                                    onClick = {
                                        view.performGlassHaptic(config)
                                        onToggleFilter(type)
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )

                    // 4. Expandable "View Options" Header Row (video 2:55–3:37)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                view.performGlassHaptic(config)
                                viewOptionsSectionExpanded = !viewOptionsSectionExpanded
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(
                                text = if (isBangla) "ভিউ অপশন (View Options)" else "View Options",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = if (viewOptionsSectionExpanded) Icons.Default.ExpandMore else Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Toggle View Options Section",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(if (viewOptionsSectionExpanded) 20.dp else 14.dp)
                        )
                    }

                    AnimatedVisibility(visible = viewOptionsSectionExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            PopoverMenuRow(
                                title = if (isBangla) "জুম ইন (Zoom In)" else "Zoom In",
                                icon = Icons.Default.Add,
                                isSelected = false,
                                enabled = gridColumns > 1,
                                onClick = {
                                    view.performGlassHaptic(config)
                                    onZoomIn()
                                }
                            )
                            PopoverMenuRow(
                                title = if (isBangla) "জুম আউট (Zoom Out)" else "Zoom Out",
                                icon = Icons.Default.Remove,
                                isSelected = false,
                                enabled = gridColumns < 5,
                                onClick = {
                                    view.performGlassHaptic(config)
                                    onZoomOut()
                                }
                            )
                            PopoverMenuRow(
                                title = if (isSquareGrid) {
                                    if (isBangla) "আসল অনুপাত গ্রিড" else "Aspect Ratio Grid"
                                } else {
                                    if (isBangla) "বর্গাকার ফটো গ্রিড" else "Square Photo Grid"
                                },
                                icon = Icons.Default.GridView,
                                isSelected = false,
                                onClick = {
                                    view.performGlassHaptic(config)
                                    onToggleSquareGrid()
                                }
                            )

                            Text(
                                text = if (isBangla) "দেখান (Show)" else "Show",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 40.dp, top = 6.dp, bottom = 2.dp)
                            )

                            PopoverMenuRow(
                                title = if (isBangla) "স্ক্রিনশট" else "Screenshots",
                                icon = Icons.Default.Screenshot,
                                isSelected = includeScreenshots,
                                onClick = {
                                    view.performGlassHaptic(config)
                                    onToggleIncludeScreenshots()
                                }
                            )
                            PopoverMenuRow(
                                title = if (isBangla) "আপনার সাথে শেয়ার করা" else "Shared with You",
                                icon = Icons.Default.People,
                                isSelected = includeSharedWithYou,
                                onClick = {
                                    view.performGlassHaptic(config)
                                    onToggleIncludeSharedWithYou()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PopoverMenuRow(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val config = LocalGlassConfig.current
    val alpha = if (enabled) 1f else 0.42f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 42.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.width(20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = config.accentTint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.85f),
            modifier = Modifier.size(19.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryGridThumbnailCell(
    item: MediaItem,
    isSquare: Boolean,
    showBadges: Boolean,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aspect = if (isSquare) 1f else 0.78f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspect)
            .testTag("grid_cell_${item.id}")
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        GlasslyMediaThumbnail(
            item = item,
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isSelected) 3.dp else 0.dp)
                .clip(RoundedCornerShape(if (isSelected) 8.dp else 0.dp))
        )

        if (showBadges) {
            if (item.isFavorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .size(16.dp)
                )
            }

            val badgeLabel = when (item.category) {
                MediaCategory.LIVE_PHOTO -> "LIVE"
                MediaCategory.RAW -> "RAW"
                MediaCategory.GIF -> "GIF"
                MediaCategory.SCREENSHOT -> "SHOT"
                else -> null
            }
            if (badgeLabel != null) {
                GlassBadge(
                    text = badgeLabel,
                    icon = if (item.isLivePhoto) Icons.Default.MotionPhotosOn else null,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(5.dp)
                )
            }

            if (item.isVideo && item.durationFormatted != null && !isSelectionMode) {
                GlassBadge(
                    text = item.durationFormatted,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(5.dp)
                )
            }
        }

        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) accentColor else Color.Black.copy(alpha = 0.25f))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimeSegmentHeroCard(
    title: String,
    subtitle: String,
    coverItem: MediaItem,
    heightDp: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(RoundedCornerShape(26.dp))
            .combinedClickable(onClick = onClick)
    ) {
        GlasslyMediaThumbnail(
            item = coverItem,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.62f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(18.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun ShareMediaGlassSheet(
    visible: Boolean,
    selectedItems: List<MediaItem>,
    onDismiss: () -> Unit,
    onActionTriggered: (String) -> Unit
) {
    val first = selectedItems.firstOrNull()
    GlassBottomSheet(
        visible = visible,
        title = "${selectedItems.size} Photo${if (selectedItems.size > 1) "s" else ""} Selected",
        onDismiss = onDismiss
    ) {
        if (first != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    GlasslyMediaThumbnail(item = first, modifier = Modifier.fillMaxSize())
                }
                Column {
                    Text(
                        text = first.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Location Included · ${first.locationName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        FlatTonalRow(
            title = "Nearby Share / Quick Share",
            subtitle = "Send full-resolution HDR media to nearby devices",
            leadingIcon = Icons.Default.Share,
            onClick = { onActionTriggered("Shared ${selectedItems.size} item(s) via Quick Share") }
        )
        FlatTonalRow(
            title = "Copy Photo to Clipboard",
            leadingIcon = Icons.Default.Collections,
            onClick = { onActionTriggered("Copied to Clipboard") }
        )
        FlatTonalRow(
            title = "Set as Wallpaper",
            subtitle = "Apply to Home Screen or Lock Screen with Spatial Depth",
            leadingIcon = Icons.Default.AutoAwesome,
            onClick = { onActionTriggered("Wallpaper updated successfully") }
        )
        FlatTonalRow(
            title = "Print / Export PDF",
            leadingIcon = Icons.Default.PhotoCamera,
            onClick = { onActionTriggered("Prepared print layout for ${selectedItems.size} item(s)") }
        )
    }
}
