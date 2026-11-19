package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RawOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CustomAlbumEntity
import com.example.data.MediaCategory
import com.example.data.MediaItem
import com.example.glass.AppLanguage
import com.example.glass.FlatTonalRow
import com.example.glass.GlassAlert
import com.example.glass.GlassBackdropProvider
import com.example.glass.GlassBottomSheet
import com.example.glass.GlassButton
import com.example.glass.GlassButtonShape
import com.example.glass.GlassCard
import com.example.glass.GlassChip
import com.example.glass.GlassMenu
import com.example.glass.GlassMenuItemData
import com.example.glass.GlassSearchField
import com.example.glass.GlassSegmentedControl
import com.example.glass.GlassTabDestination
import com.example.glass.GlassToolbar
import com.example.glass.GlassVariant
import com.example.glass.LiquidGlassSurface
import com.example.glass.LocalGlassConfig
import com.example.glass.performGlassHaptic
import com.example.ui.components.GlasslyMediaThumbnail
import com.example.ui.theme.GlassRed
import com.example.viewmodel.AppSubScreen
import com.example.viewmodel.CollectionTileSize
import com.example.viewmodel.GlasslyViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionsScreen(
    viewModel: GlasslyViewModel,
    modifier: Modifier = Modifier
) {
    val config = LocalGlassConfig.current
    val view = LocalView.current
    val isBangla = config.language == AppLanguage.BANGLA

    val allMedia by viewModel.allMergedMedia.collectAsStateWithLifecycle()
    val activeMedia = remember(allMedia) { allMedia.filter { !it.isTrashed && !it.isHidden } }
    val trashedMedia by viewModel.trashedMedia.collectAsStateWithLifecycle()
    val hiddenMedia by viewModel.hiddenMedia.collectAsStateWithLifecycle()
    val favoriteMedia by viewModel.favoriteMedia.collectAsStateWithLifecycle()
    val duplicateGroups by viewModel.duplicateGroups.collectAsStateWithLifecycle()
    val largeFiles by viewModel.largeFilesMedia.collectAsStateWithLifecycle()
    val customAlbums by viewModel.customAlbums.collectAsStateWithLifecycle()
    val tileSize by viewModel.collectionTileSize.collectAsStateWithLifecycle()
    val sectionOrder by viewModel.collectionSectionOrder.collectAsStateWithLifecycle()
    val collapsedSections by viewModel.collapsedCollectionSections.collectAsStateWithLifecycle()
    val pinnedItems by viewModel.pinnedCollectionItems.collectAsStateWithLifecycle()

    var showMorePopover by remember { mutableStateOf(false) }
    var isReorderMode by remember { mutableStateOf(false) }
    var showEditPinnedSheet by remember { mutableStateOf(false) }
    var showAnyCollectionPicker by remember { mutableStateOf(false) }
    var pendingDeletePinnedItem by remember { mutableStateOf<String?>(null) }

    var showCreateAlbumAlert by remember { mutableStateOf(false) }
    var newAlbumName by remember { mutableStateOf("") }
    var contextMenuTargetName by remember { mutableStateOf<String?>(null) }
    var contextMenuCustomAlbum by remember { mutableStateOf<CustomAlbumEntity?>(null) }
    var renameAlbumInput by remember { mutableStateOf("") }
    var showRenameAlbumAlert by remember { mutableStateOf(false) }
    var showDeleteAlbumAlert by remember { mutableStateOf(false) }

    val tileWidthDp: Dp = when (tileSize) {
        CollectionTileSize.LARGE -> 162.dp
        CollectionTileSize.DENSE -> 112.dp
        CollectionTileSize.TOP_ROW_EMPHASIS -> 144.dp
    }

    GlassBackdropProvider(
        backdropLuminanceHint = if (config.isDarkTheme) 0.16f else 0.82f,
        backgroundContent = {
            if (isReorderMode) {
                // In-place Reorder Mode matching video 05:45–06:22 (right screen of images__16__7dfe0898.jpeg)
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .testTag("collections_reorder_list"),
                    contentPadding = PaddingValues(top = 98.dp, bottom = 135.dp, start = 18.dp, end = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(sectionOrder, key = { _, key -> "reorder_$key" }) { idx, sectionKey ->
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = sectionKey,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Move $sectionKey Up",
                                        tint = if (idx > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .clickable(enabled = idx > 0) {
                                                view.performGlassHaptic(config)
                                                viewModel.moveCollectionSection(idx, idx - 1)
                                            }
                                            .padding(4.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Move $sectionKey Down",
                                        tint = if (idx < sectionOrder.lastIndex) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .clickable(enabled = idx < sectionOrder.lastIndex) {
                                                view.performGlassHaptic(config)
                                                viewModel.moveCollectionSection(idx, idx + 1)
                                            }
                                            .padding(4.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.DragHandle,
                                        contentDescription = "Drag Handle",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .testTag("collections_lazy_column"),
                    contentPadding = PaddingValues(top = 96.dp, bottom = 135.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    sectionOrder.forEachIndexed { sectionIdx, sectionKey ->
                        val isCollapsed = sectionKey in collapsedSections
                        val currentTileWidth = if (tileSize == CollectionTileSize.TOP_ROW_EMPHASIS && sectionIdx == 0) {
                            176.dp
                        } else tileWidthDp

                        item(key = "sec_$sectionKey") {
                            Column {
                                when (sectionKey) {
                                    "Pinned" -> {
                                        CollectionSectionHeader(
                                            title = if (isBangla) "পিন করা" else "Pinned",
                                            isCollapsed = isCollapsed,
                                            onToggleCollapse = { viewModel.toggleCollectionSectionCollapsed(sectionKey) },
                                            actionText = if (isBangla) "এডিট" else "Edit",
                                            onActionClick = { showEditPinnedSheet = true }
                                        )
                                        AnimatedVisibility(visible = !isCollapsed) {
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                itemsIndexed(pinnedItems, key = { _, name -> "pinned_tile_$name" }) { pIdx, pinName ->
                                                    val tileW = if (tileSize == CollectionTileSize.TOP_ROW_EMPHASIS && pIdx == 0) 176.dp else tileWidthDp
                                                    val matchedMedia = resolveCollectionMedia(pinName, activeMedia, favoriteMedia, trashedMedia, hiddenMedia)
                                                    CollectionSquareTile(
                                                        title = pinName,
                                                        countLabel = "${matchedMedia.size}",
                                                        coverItem = matchedMedia.lastOrNull() ?: activeMedia.lastOrNull(),
                                                        tileSize = tileW,
                                                        onClick = {
                                                            openCollectionByName(pinName, viewModel, activeMedia)
                                                        },
                                                        onLongClick = {
                                                            contextMenuTargetName = pinName
                                                            contextMenuCustomAlbum = customAlbums.find { it.name == pinName }
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    "Shared Albums" -> {
                                        CollectionSectionHeader(
                                            title = if (isBangla) "শেয়ার করা অ্যালবাম" else "Shared Albums",
                                            isCollapsed = isCollapsed,
                                            onToggleCollapse = { viewModel.toggleCollectionSectionCollapsed(sectionKey) },
                                            actionText = "Activity",
                                            onActionClick = {
                                                viewModel.navigateToSubScreen(
                                                    AppSubScreen.AlbumDetail("Shared Albums Activity") { !it.isCapturedByMe }
                                                )
                                            }
                                        )
                                        AnimatedVisibility(visible = !isCollapsed) {
                                            val sharedAlbums = listOf(
                                                "Family Cloud" to activeMedia.take(14),
                                                "Weekend Trip" to activeMedia.drop(8).take(12),
                                                "Wallpapers" to activeMedia.drop(18).take(16)
                                            )
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                items(sharedAlbums, key = { "shared_${it.first}" }) { (name, list) ->
                                                    CollectionSquareTile(
                                                        title = name,
                                                        countLabel = "${list.size} Shared",
                                                        coverItem = list.firstOrNull() ?: activeMedia.firstOrNull(),
                                                        tileSize = currentTileWidth,
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail(name) { !it.isCapturedByMe || it.id % 3 == 0L }
                                                            )
                                                        },
                                                        onLongClick = {
                                                            contextMenuTargetName = name
                                                            contextMenuCustomAlbum = null
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    "Media Types" -> {
                                        CollectionSectionHeader(
                                            title = if (isBangla) "মিডিয়া টাইপ" else "Media Types",
                                            isCollapsed = isCollapsed,
                                            onToggleCollapse = { viewModel.toggleCollectionSectionCollapsed(sectionKey) }
                                        )
                                        AnimatedVisibility(visible = !isCollapsed) {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "ভিডিও" else "Videos",
                                                        count = activeMedia.count { it.isVideo },
                                                        icon = Icons.Default.Videocam,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail("Videos") { it.isVideo }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Videos" }
                                                    )
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "সেলফি" else "Selfies",
                                                        count = activeMedia.count { it.category == MediaCategory.SELFIE },
                                                        icon = Icons.Default.Person,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail("Selfies") { it.category == MediaCategory.SELFIE }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Selfies" }
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "লাইভ ফটো" else "Live Photos",
                                                        count = activeMedia.count { it.isLivePhoto },
                                                        icon = Icons.Default.MotionPhotosOn,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail("Live Photos") { it.isLivePhoto }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Live Photos" }
                                                    )
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "পোর্ট্রেট" else "Portrait",
                                                        count = activeMedia.count { it.category == MediaCategory.PORTRAIT },
                                                        icon = Icons.Default.PhotoCamera,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail("Portrait") { it.category == MediaCategory.PORTRAIT }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Portrait" }
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "স্ক্রিনশট" else "Screenshots",
                                                        count = activeMedia.count { it.category == MediaCategory.SCREENSHOT },
                                                        icon = Icons.Default.Screenshot,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail("Screenshots") { it.category == MediaCategory.SCREENSHOT }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Screenshots" }
                                                    )
                                                    SmartCollectionPillRow(
                                                        title = "Spatial & RAW",
                                                        count = activeMedia.count {
                                                            it.category == MediaCategory.RAW || it.category == MediaCategory.GIF
                                                        },
                                                        icon = Icons.Default.RawOn,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail("Spatial & RAW") {
                                                                    it.category == MediaCategory.RAW || it.category == MediaCategory.GIF
                                                                }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Spatial & RAW" }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    "People & Pets" -> {
                                        CollectionSectionHeader(
                                            title = if (isBangla) "মানুষ ও পোষা প্রাণী" else "People & Pets",
                                            isCollapsed = isCollapsed,
                                            onToggleCollapse = { viewModel.toggleCollectionSectionCollapsed(sectionKey) }
                                        )
                                        AnimatedVisibility(visible = !isCollapsed) {
                                            val people = listOf(
                                                "Sofia" to activeMedia.getOrNull(2),
                                                "Elena" to activeMedia.getOrNull(5),
                                                "Liam" to activeMedia.getOrNull(9),
                                                "Maya" to activeMedia.getOrNull(14),
                                                "Milo" to activeMedia.getOrNull(19)
                                            )
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                items(people, key = { "person_${it.first}" }) { (personName, cover) ->
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        modifier = Modifier.clickable {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail(personName) { it.id % 4 == 1L }
                                                            )
                                                        }
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(88.dp)
                                                                .clip(CircleShape)
                                                        ) {
                                                            if (cover != null) {
                                                                GlasslyMediaThumbnail(item = cover, modifier = Modifier.fillMaxSize())
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text(
                                                            text = personName,
                                                            style = MaterialTheme.typography.labelLarge,
                                                            color = MaterialTheme.colorScheme.onBackground
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    "Utilities" -> {
                                        CollectionSectionHeader(
                                            title = if (isBangla) "ইউটিলিটিজ" else "Utilities",
                                            isCollapsed = isCollapsed,
                                            onToggleCollapse = { viewModel.toggleCollectionSectionCollapsed(sectionKey) }
                                        )
                                        AnimatedVisibility(visible = !isCollapsed) {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "প্রিয়" else "Favourites",
                                                        count = favoriteMedia.size,
                                                        icon = Icons.Default.Favorite,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(AppSubScreen.FavoritesCollection)
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Favourites" }
                                                    )
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "হিডেন" else "Hidden",
                                                        count = hiddenMedia.size,
                                                        icon = Icons.Default.Lock,
                                                        showLockBadge = true,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(AppSubScreen.HiddenAlbum)
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Hidden" }
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "সম্প্রতি মুছে ফেলা" else "Recently Deleted",
                                                        count = trashedMedia.size,
                                                        icon = Icons.Default.Delete,
                                                        showLockBadge = true,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(AppSubScreen.RecentlyDeleted)
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Recently Deleted" }
                                                    )
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "ডুপ্লিকেট" else "Duplicates",
                                                        count = duplicateGroups.size,
                                                        icon = Icons.Default.ContentCopy,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(AppSubScreen.DuplicatesCleaner)
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Duplicates" }
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    SmartCollectionPillRow(
                                                        title = "Receipts",
                                                        count = activeMedia.count { it.category == MediaCategory.DOCUMENT },
                                                        icon = Icons.Default.Receipt,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail("Receipts") { it.category == MediaCategory.DOCUMENT }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Receipts" }
                                                    )
                                                    SmartCollectionPillRow(
                                                        title = "QR Codes",
                                                        count = 3,
                                                        icon = Icons.Default.QrCode,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail("QR Codes") { it.category == MediaCategory.DOCUMENT }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = "QR Codes" }
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "বড় ফাইল" else "Large Files",
                                                        count = largeFiles.size,
                                                        icon = Icons.Default.CleaningServices,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(AppSubScreen.LargeFilesCleaner)
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Large Files" }
                                                    )
                                                    SmartCollectionPillRow(
                                                        title = "Map",
                                                        count = activeMedia.size,
                                                        icon = Icons.Default.Map,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail("Map Photos") { true }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Map" }
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "ডকুমেন্টস" else "Documents",
                                                        count = activeMedia.count { it.category == MediaCategory.DOCUMENT },
                                                        icon = Icons.Default.Description,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail("Documents") { it.category == MediaCategory.DOCUMENT }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Documents" }
                                                    )
                                                    SmartCollectionPillRow(
                                                        title = if (isBangla) "আমার তোলা" else "Captured by Me",
                                                        count = activeMedia.count { it.isCapturedByMe },
                                                        icon = Icons.Default.PhotoCamera,
                                                        modifier = Modifier.weight(1f),
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail("Captured by Me") { it.isCapturedByMe }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = "Captured by Me" }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    "Featured Photos" -> {
                                        CollectionSectionHeader(
                                            title = if (isBangla) "ফিচার্ড ছবি" else "Featured Photos",
                                            isCollapsed = isCollapsed,
                                            onToggleCollapse = { viewModel.toggleCollectionSectionCollapsed(sectionKey) }
                                        )
                                        AnimatedVisibility(visible = !isCollapsed) {
                                            val featured = activeMedia.filter { it.isFavorite }.ifEmpty { activeMedia.take(6) }
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                items(featured, key = { "feat_${it.id}" }) { item ->
                                                    CollectionSquareTile(
                                                        title = item.locationName,
                                                        countLabel = item.dateFormattedShort,
                                                        coverItem = item,
                                                        tileSize = 185.dp,
                                                        onClick = { viewModel.openViewer(item.id) }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    "Memories" -> {
                                        CollectionSectionHeader(
                                            title = if (isBangla) "মেমোরিজ" else "Memories",
                                            isCollapsed = isCollapsed,
                                            onToggleCollapse = { viewModel.toggleCollectionSectionCollapsed(sectionKey) }
                                        )
                                        AnimatedVisibility(visible = !isCollapsed) {
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                val memoryCovers = activeMedia.takeLast(8).reversed()
                                                if (memoryCovers.isNotEmpty()) {
                                                    item {
                                                        val first = memoryCovers.first()
                                                        MemoryFeatureCard(
                                                            title = "Remember When…",
                                                            subtitle = first.dateFormattedShort.uppercase(),
                                                            coverItem = first,
                                                            showTypeToCreateBadge = true,
                                                            onClick = {
                                                                viewModel.navigateToSubScreen(
                                                                    AppSubScreen.Slideshow("Remember When…", memoryCovers)
                                                                )
                                                            }
                                                        )
                                                    }
                                                    if (memoryCovers.size > 2) {
                                                        val second = memoryCovers[2]
                                                        item {
                                                            MemoryFeatureCard(
                                                                title = second.locationName,
                                                                subtitle = second.monthYearLabel.uppercase(),
                                                                coverItem = second,
                                                                showTypeToCreateBadge = false,
                                                                onClick = {
                                                                    viewModel.navigateToSubScreen(
                                                                        AppSubScreen.Slideshow(second.locationName, memoryCovers)
                                                                    )
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    "Albums" -> {
                                        CollectionSectionHeader(
                                            title = if (isBangla) "অ্যালবাম" else "Albums",
                                            isCollapsed = isCollapsed,
                                            onToggleCollapse = { viewModel.toggleCollectionSectionCollapsed(sectionKey) },
                                            actionText = if (isBangla) "+ নতুন" else "+ New",
                                            onActionClick = {
                                                newAlbumName = ""
                                                showCreateAlbumAlert = true
                                            }
                                        )
                                        AnimatedVisibility(visible = !isCollapsed) {
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                items(customAlbums, key = { "alb_${it.name}" }) { album ->
                                                    val albumItems = activeMedia.filter { album.name in it.albums }
                                                        .ifEmpty { activeMedia.filter { (it.id + album.name.length) % 3 == 0L } }
                                                    CollectionSquareTile(
                                                        title = album.name,
                                                        countLabel = "${albumItems.size} Items",
                                                        coverItem = albumItems.firstOrNull() ?: activeMedia.firstOrNull(),
                                                        tileSize = currentTileWidth,
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail(album.name) {
                                                                    album.name in it.albums || (it.id + album.name.length) % 3 == 0L
                                                                }
                                                            )
                                                        },
                                                        onLongClick = {
                                                            contextMenuTargetName = album.name
                                                            contextMenuCustomAlbum = album
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    "Trips" -> {
                                        CollectionSectionHeader(
                                            title = if (isBangla) "ভ্রমণ (Trips)" else "Trips",
                                            isCollapsed = isCollapsed,
                                            onToggleCollapse = { viewModel.toggleCollectionSectionCollapsed(sectionKey) }
                                        )
                                        AnimatedVisibility(visible = !isCollapsed) {
                                            val trips = listOf(
                                                Triple("Santorini Escape", "2026 · 18 Photos", activeMedia.getOrNull(2)),
                                                Triple("Lisbon Coast", "2025 · 14 Photos", activeMedia.getOrNull(3)),
                                                Triple("Kyoto Autumn", "2025 · 22 Photos", activeMedia.getOrNull(5))
                                            )
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                items(trips, key = { "trip_${it.first}" }) { (tripTitle, sub, cover) ->
                                                    CollectionSquareTile(
                                                        title = tripTitle,
                                                        countLabel = sub,
                                                        coverItem = cover ?: activeMedia.firstOrNull(),
                                                        tileSize = currentTileWidth,
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail(tripTitle) { true }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = tripTitle }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    "Wallpaper Suggestions" -> {
                                        CollectionSectionHeader(
                                            title = if (isBangla) "ওয়ালপেপার পরামর্শ" else "Wallpaper Suggestions",
                                            isCollapsed = isCollapsed,
                                            onToggleCollapse = { viewModel.toggleCollectionSectionCollapsed(sectionKey) }
                                        )
                                        AnimatedVisibility(visible = !isCollapsed) {
                                            // Vertical lock-screen preview cards with "9:41" clock overlay (matching images__18__ba2e0802.jpeg)
                                            val wallpapers = activeMedia.take(5)
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                items(wallpapers, key = { "wp_${it.id}" }) { wpItem ->
                                                    Box(
                                                        modifier = Modifier
                                                            .width(122.dp)
                                                            .height(210.dp)
                                                            .clip(RoundedCornerShape(22.dp))
                                                            .clickable { viewModel.openViewer(wpItem.id) }
                                                    ) {
                                                        GlasslyMediaThumbnail(item = wpItem, modifier = Modifier.fillMaxSize())
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .background(
                                                                    Brush.verticalGradient(
                                                                        listOf(
                                                                            Color.Black.copy(alpha = 0.35f),
                                                                            Color.Transparent,
                                                                            Color.Black.copy(alpha = 0.45f)
                                                                        )
                                                                    )
                                                                )
                                                        )
                                                        Text(
                                                            text = "9:41",
                                                            style = MaterialTheme.typography.headlineLarge,
                                                            fontWeight = FontWeight.Light,
                                                            color = Color.White.copy(alpha = 0.88f),
                                                            modifier = Modifier
                                                                .align(Alignment.TopCenter)
                                                                .padding(top = 18.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    "Recent Days" -> {
                                        CollectionSectionHeader(
                                            title = if (isBangla) "সাম্প্রতিক দিনগুলো" else "Recent Days",
                                            isCollapsed = isCollapsed,
                                            onToggleCollapse = { viewModel.toggleCollectionSectionCollapsed(sectionKey) }
                                        )
                                        AnimatedVisibility(visible = !isCollapsed) {
                                            val recentByDay = activeMedia.takeLast(18).groupBy { it.dateFormattedShort }.entries.toList().reversed()
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                items(recentByDay, key = { "recday_${it.key}" }) { (dayStr, list) ->
                                                    CollectionSquareTile(
                                                        title = dayStr,
                                                        countLabel = "${list.size} Photos",
                                                        coverItem = list.lastOrNull(),
                                                        tileSize = currentTileWidth,
                                                        onClick = {
                                                            viewModel.navigateToSubScreen(
                                                                AppSubScreen.AlbumDetail(dayStr) { it.dateFormattedShort == dayStr }
                                                            )
                                                        },
                                                        onLongClick = { contextMenuTargetName = dayStr }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Blue "Reorder" text/pill button matching reference `images__16__7dfe0898.jpeg` & video 05:44
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp, bottom = 12.dp)
                        ) {
                            Text(
                                text = if (isBangla) "সেকশন সাজান (Reorder)" else "Reorder",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = config.accentTint,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        view.performGlassHaptic(config)
                                        isReorderMode = true
                                    }
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                                    .testTag("collections_reorder_button")
                            )
                        }
                    }
                }
            }
        },
        floatingOverlayContent = {
            // Top Glass Toolbar matching video 05:02–06:22:
            // Normal mode: "..." circle button + green/tinted Profile Avatar ("PG") button (tapping opens Settings)
            // Reorder mode: Blue circular Checkmark ("✓") confirm button
            GlassToolbar(
                title = if (isBangla) "কালেকশন" else "Collections",
                actions = {
                    if (isReorderMode) {
                        GlassButton(
                            onClick = {
                                view.performGlassHaptic(config, isConfirm = true)
                                isReorderMode = false
                            },
                            icon = Icons.Default.Check,
                            contentDescription = "Confirm Reorder",
                            shapeType = GlassButtonShape.CIRCLE,
                            variant = GlassVariant.TINTED,
                            testTag = "collections_reorder_done_button"
                        )
                    } else {
                        GlassButton(
                            onClick = { showMorePopover = !showMorePopover },
                            icon = Icons.Default.MoreHoriz,
                            contentDescription = "Collections Layout & Options",
                            shapeType = GlassButtonShape.CIRCLE,
                            testTag = "collections_more_button"
                        )

                        // Profile Avatar Badge in top-right (opens Settings when tapped, as chosen by owner)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF34C759), Color(0xFF0A84FF))
                                    )
                                )
                                .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                                .clickable {
                                    view.performGlassHaptic(config)
                                    viewModel.selectTab(GlassTabDestination.SETTINGS)
                                }
                                .testTag("collections_profile_avatar_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PG",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            )

            // Collections Top-Right "..." Popover (video 05:14–05:44):
            // Top 3-icon layout switcher row (Top-Row Emphasis | Dense Equal | Large Square) + Show All + Collapse All + Reorder
            AnimatedVisibility(visible = showMorePopover) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null,
                            onClick = { showMorePopover = false }
                        )
                        .statusBarsPadding()
                        .padding(top = 58.dp, end = 16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    LiquidGlassSurface(
                        modifier = Modifier
                            .widthIn(min = 236.dp, max = 268.dp)
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null,
                                onClick = {}
                            )
                            .testTag("collections_options_popover"),
                        cornerRadius = 26.dp,
                        thicknessMultiplier = 1.45f
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Top 3-icon layout switcher row matching video 05:14
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val layoutModes = listOf(
                                    Triple(CollectionTileSize.TOP_ROW_EMPHASIS, Icons.Default.Dashboard, "Top-Row Emphasis"),
                                    Triple(CollectionTileSize.DENSE, Icons.Default.GridOn, "Dense Tiles"),
                                    Triple(CollectionTileSize.LARGE, Icons.Default.GridView, "Large Tiles")
                                )
                                layoutModes.forEach { (mode, iconVec, desc) ->
                                    val isSelected = tileSize == mode
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (isSelected) config.accentTint.copy(alpha = 0.25f)
                                                else Color.Transparent
                                            )
                                            .clickable {
                                                view.performGlassHaptic(config)
                                                viewModel.setCollectionTileSize(mode)
                                            }
                                            .testTag("collections_layout_${mode.name.lowercase()}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = iconVec,
                                            contentDescription = desc,
                                            tint = if (isSelected) config.accentTint else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))

                            // Show All
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        view.performGlassHaptic(config)
                                        viewModel.showAllCollectionSections()
                                        showMorePopover = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBangla) "সব দেখান (Show All)" else "Show All",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Collapse All
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        view.performGlassHaptic(config)
                                        viewModel.collapseAllCollectionSections()
                                        showMorePopover = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBangla) "সব সংকুচিত করুন (Collapse All)" else "Collapse All",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandLess,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Reorder
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        view.performGlassHaptic(config)
                                        showMorePopover = false
                                        isReorderMode = true
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBangla) "সাজান (Reorder)" else "Reorder",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Context Menu on Long-Pressed Collection or Album (Share Photos, Add Photos, Album Details, Delete Album, Pin / Unpin) — video 08:06–08:25
            val targetName = contextMenuTargetName
            val targetCustomAlbum = contextMenuCustomAlbum
            GlassMenu(
                expanded = targetName != null,
                onDismiss = {
                    contextMenuTargetName = null
                    contextMenuCustomAlbum = null
                },
                headerTitle = targetName ?: "Collection Options",
                items = if (targetName == null) emptyList() else buildList {
                    add(
                        GlassMenuItemData(
                            title = "Share Photos",
                            icon = Icons.Default.Share,
                            onClick = { viewModel.showToast("Prepared share sheet for \"$targetName\"") }
                        )
                    )
                    if (targetCustomAlbum != null) {
                        add(
                            GlassMenuItemData(
                                title = "Add Photos",
                                icon = Icons.Default.Add,
                                onClick = { viewModel.showToast("Select photos in Library to add to \"$targetName\"") }
                            )
                        )
                        add(
                            GlassMenuItemData(
                                title = "Rename Album",
                                icon = Icons.Default.Edit,
                                onClick = {
                                    renameAlbumInput = targetCustomAlbum.name
                                    showRenameAlbumAlert = true
                                }
                            )
                        )
                        add(
                            GlassMenuItemData(
                                title = "Delete Album",
                                icon = Icons.Default.Delete,
                                isDestructive = true,
                                onClick = { showDeleteAlbumAlert = true }
                            )
                        )
                    }
                    val isCurrentlyPinned = targetName in pinnedItems
                    add(
                        GlassMenuItemData(
                            title = if (isCurrentlyPinned) "Unpin" else "Pin",
                            icon = Icons.Default.PushPin,
                            onClick = { viewModel.togglePinnedItem(targetName) }
                        )
                    )
                }
            )

            // Pinned Edit Sheet matching video 07:30–08:05
            GlassBottomSheet(
                visible = showEditPinnedSheet,
                title = if (isBangla) "পিন করা কালেকশন" else "Pinned",
                onDismiss = {
                    showEditPinnedSheet = false
                    showAnyCollectionPicker = false
                    pendingDeletePinnedItem = null
                }
            ) {
                Text(
                    text = "Pinned collections and albums appear at the top of Collections.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Currently Pinned Items with red minus (⊖), swipe/tap red Delete confirmation, and drag reorder
                pinnedItems.forEachIndexed { idx, pinName ->
                    val isConfirmingDelete = pendingDeletePinnedItem == pinName
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RemoveCircle,
                                contentDescription = "Unpin $pinName",
                                tint = GlassRed,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        view.performGlassHaptic(config)
                                        pendingDeletePinnedItem = if (isConfirmingDelete) null else pinName
                                    }
                            )
                            Text(
                                text = pinName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (isConfirmingDelete) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GlassRed)
                                    .clickable {
                                        view.performGlassHaptic(config, isConfirm = true)
                                        viewModel.removePinnedItem(pinName)
                                        pendingDeletePinnedItem = null
                                    }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Delete",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Move Up",
                                    tint = if (idx > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable(enabled = idx > 0) {
                                            viewModel.movePinnedItem(idx, idx - 1)
                                        }
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Move Down",
                                    tint = if (idx < pinnedItems.lastIndex) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable(enabled = idx < pinnedItems.lastIndex) {
                                            viewModel.movePinnedItem(idx, idx + 1)
                                        }
                                )
                                Icon(
                                    imageVector = Icons.Default.DragHandle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SUGGESTIONS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )

                val suggestions = listOf(
                    "Map",
                    "Recently Deleted",
                    "People & Pets",
                    "Albums",
                    "Trips",
                    "Memories",
                    "Featured Photos",
                    "Media Types",
                    "Utilities",
                    "Shared Albums",
                    "Selfies",
                    "Hidden",
                    "Imports"
                ).filter { it !in pinnedItems }

                suggestions.forEach { suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                            .clickable {
                                view.performGlassHaptic(config)
                                viewModel.addPinnedItem(suggestion)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add $suggestion",
                            tint = Color(0xFF34C759),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Bottom row with blue plus: "Any Collection or Album" (video 07:45)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(config.accentTint.copy(alpha = 0.14f))
                        .clickable {
                            view.performGlassHaptic(config)
                            showAnyCollectionPicker = !showAnyCollectionPicker
                        }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Any Collection or Album",
                            tint = config.accentTint,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Any Collection or Album",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = config.accentTint
                        )
                    }
                    Icon(
                        imageVector = if (showAnyCollectionPicker) Icons.Default.ExpandMore else Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = config.accentTint,
                        modifier = Modifier.size(16.dp)
                    )
                }

                AnimatedVisibility(visible = showAnyCollectionPicker) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        customAlbums.filter { it.name !in pinnedItems }.forEach { alb ->
                            FlatTonalRow(
                                title = alb.name,
                                subtitle = "Album",
                                leadingIcon = Icons.Default.Collections,
                                onClick = { viewModel.addPinnedItem(alb.name) }
                            )
                        }
                    }
                }
            }

            // Create Album In-Place Glass Alert
            GlassAlert(
                visible = showCreateAlbumAlert,
                title = if (isBangla) "নতুন অ্যালবাম" else "New Album",
                message = if (isBangla) "অ্যালবামের জন্য একটি নাম লিখুন:" else "Enter a name for this album:",
                confirmText = if (isBangla) "তৈরি করুন" else "Create",
                onConfirm = {
                    if (newAlbumName.isNotBlank()) {
                        viewModel.createAlbum(newAlbumName)
                        showCreateAlbumAlert = false
                    }
                },
                onDismiss = { showCreateAlbumAlert = false },
                extraContent = {
                    OutlinedTextField(
                        value = newAlbumName,
                        onValueChange = { newAlbumName = it },
                        singleLine = true,
                        label = { Text("Album Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )

            // Rename Album Glass Alert
            GlassAlert(
                visible = showRenameAlbumAlert && targetCustomAlbum != null,
                title = "Rename Album",
                message = "Enter a new name for \"${targetCustomAlbum?.name}\":",
                confirmText = "Save",
                onConfirm = {
                    val old = targetCustomAlbum?.name ?: return@GlassAlert
                    viewModel.renameAlbum(old, renameAlbumInput)
                    showRenameAlbumAlert = false
                    contextMenuTargetName = null
                    contextMenuCustomAlbum = null
                },
                onDismiss = {
                    showRenameAlbumAlert = false
                    contextMenuTargetName = null
                    contextMenuCustomAlbum = null
                },
                extraContent = {
                    OutlinedTextField(
                        value = renameAlbumInput,
                        onValueChange = { renameAlbumInput = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )

            // Delete Album Glass Alert
            GlassAlert(
                visible = showDeleteAlbumAlert && targetCustomAlbum != null,
                title = "Delete \"${targetCustomAlbum?.name}\"?",
                message = "The album will be removed. Photos inside it will remain safely in your Library.",
                confirmText = "Delete Album",
                isDestructive = true,
                onConfirm = {
                    val target = targetCustomAlbum?.name ?: return@GlassAlert
                    viewModel.deleteAlbum(target)
                    showDeleteAlbumAlert = false
                    contextMenuTargetName = null
                    contextMenuCustomAlbum = null
                },
                onDismiss = {
                    showDeleteAlbumAlert = false
                    contextMenuTargetName = null
                    contextMenuCustomAlbum = null
                }
            )
        }
    )
}

private fun resolveCollectionMedia(
    name: String,
    activeMedia: List<MediaItem>,
    favoriteMedia: List<MediaItem>,
    trashedMedia: List<MediaItem>,
    hiddenMedia: List<MediaItem>
): List<MediaItem> {
    return when (name) {
        "Favourites", "Favorites" -> favoriteMedia
        "Recently Saved" -> activeMedia.takeLast(24)
        "Videos" -> activeMedia.filter { it.isVideo }
        "Screenshots" -> activeMedia.filter { it.category == MediaCategory.SCREENSHOT }
        "Selfies" -> activeMedia.filter { it.category == MediaCategory.SELFIE }
        "Recently Deleted" -> trashedMedia
        "Hidden" -> hiddenMedia
        "Map" -> activeMedia
        else -> activeMedia.filter { name in it.albums }.ifEmpty { activeMedia.take(12) }
    }
}

private fun openCollectionByName(
    name: String,
    viewModel: GlasslyViewModel,
    activeMedia: List<MediaItem>
) {
    when (name) {
        "Favourites", "Favorites" -> viewModel.navigateToSubScreen(AppSubScreen.FavoritesCollection)
        "Recently Deleted" -> viewModel.navigateToSubScreen(AppSubScreen.RecentlyDeleted)
        "Hidden" -> viewModel.navigateToSubScreen(AppSubScreen.HiddenAlbum)
        "Videos" -> viewModel.navigateToSubScreen(AppSubScreen.AlbumDetail("Videos") { it.isVideo })
        "Screenshots" -> viewModel.navigateToSubScreen(AppSubScreen.AlbumDetail("Screenshots") { it.category == MediaCategory.SCREENSHOT })
        "Selfies" -> viewModel.navigateToSubScreen(AppSubScreen.AlbumDetail("Selfies") { it.category == MediaCategory.SELFIE })
        "Recently Saved", "Map", "Imports" -> viewModel.navigateToSubScreen(AppSubScreen.AlbumDetail(name) { true })
        "Memories" -> if (activeMedia.isNotEmpty()) {
            viewModel.navigateToSubScreen(AppSubScreen.Slideshow("Memories", activeMedia.takeLast(16)))
        }
        else -> viewModel.navigateToSubScreen(
            AppSubScreen.AlbumDetail(name) { name in it.albums || it.id % 4 == 0L }
        )
    }
}

@Composable
private fun CollectionSectionHeader(
    title: String,
    isCollapsed: Boolean = false,
    onToggleCollapse: (() -> Unit)? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isCollapsed) 2.dp else 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(enabled = onToggleCollapse != null) { onToggleCollapse?.invoke() }
                .padding(vertical = 2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(15.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isCollapsed && actionText != null && onActionClick != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        .clickable { onActionClick() }
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = actionText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = LocalGlassConfig.current.accentTint
                    )
                }
            }

            if (onToggleCollapse != null) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(LocalGlassConfig.current.accentTint.copy(alpha = 0.14f))
                        .clickable { onToggleCollapse() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCollapsed) Icons.AutoMirrored.Filled.ArrowForwardIos else Icons.Default.ExpandMore,
                        contentDescription = if (isCollapsed) "Expand $title" else "Collapse $title",
                        tint = LocalGlassConfig.current.accentTint,
                        modifier = Modifier.size(if (isCollapsed) 12.dp else 18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoryFeatureCard(
    title: String,
    subtitle: String,
    coverItem: MediaItem,
    showTypeToCreateBadge: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(290.dp)
            .height(235.dp)
            .clip(RoundedCornerShape(30.dp))
            .clickable(onClick = onClick)
    ) {
        GlasslyMediaThumbnail(item = coverItem, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.58f))
                    )
                )
        )
        if (showTypeToCreateBadge) {
            LiquidGlassSurface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp),
                cornerRadius = 999.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Memory Movie",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
        LiquidGlassSurface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp)
                .size(40.dp),
            cornerRadius = 999.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Memory",
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CollectionSquareTile(
    title: String,
    countLabel: String,
    coverItem: MediaItem?,
    tileSize: Dp,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(tileSize)
            .clip(RoundedCornerShape(22.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        if (coverItem != null) {
            GlasslyMediaThumbnail(item = coverItem, modifier = Modifier.fillMaxSize())
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = countLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SmartCollectionPillRow(
    title: String,
    count: Int,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLockBadge: Boolean = false,
    onLongClick: (() -> Unit)? = null
) {
    val config = LocalGlassConfig.current
    LiquidGlassSurface(
        modifier = modifier
            .height(50.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .testTag("collection_pill_${title.lowercase().replace(" ", "_")}"),
        cornerRadius = 14.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = config.accentTint,
                    modifier = Modifier.size(19.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (showLockBadge) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * SearchOverlayScreen: Replicates the exact iOS 26 Photos Search tab (`01:22–01:46`):
 * - Recents row cards (Recently Viewed, Recently Edited, Recently Shared)
 * - Time & place suggestion pills (Spring 2025, Autumn 2024, Beach, Santorini, videos last month)
 * - Photos / Collections result tab toggle
 * - Live floating autocomplete popup above the bottom thumb-reach Liquid Glass search bar
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchOverlayScreen(
    viewModel: GlasslyViewModel,
    modifier: Modifier = Modifier
) {
    val config = LocalGlassConfig.current
    val isBangla = config.language == AppLanguage.BANGLA

    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val results by viewModel.searchResults.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val showBadges by viewModel.showBadges.collectAsStateWithLifecycle()
    val allMedia by viewModel.allMergedMedia.collectAsStateWithLifecycle()
    val activeMedia = remember(allMedia) { allMedia.filter { !it.isTrashed && !it.isHidden } }

    var activeResultTab by remember { mutableStateOf("Photos") }

    val suggestions = listOf(
        "Beach",
        "Spring 2025",
        "Autumn 2024",
        "videos last month",
        "Favourites",
        "Santorini",
        "Lisbon",
        "Kyoto",
        "Screenshots"
    )

    GlassBackdropProvider(
        backdropLuminanceHint = if (config.isDarkTheme) 0.16f else 0.85f,
        backgroundContent = {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (isBangla) "অনুসন্ধান" else "Search",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (query.isBlank()) {
                    // Recents Cards (Recently Viewed, Recently Edited, Recently Shared) matching video 01:24
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isBangla) "সাম্প্রতিক" else "Recents",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    val recentCards = listOf(
                        Triple("Recently Viewed", Icons.Default.Visibility, activeMedia.lastOrNull()),
                        Triple("Recently Edited", Icons.Default.Edit, activeMedia.find { it.isEdited } ?: activeMedia.firstOrNull()),
                        Triple("Recently Shared", Icons.Default.Share, activeMedia.getOrNull(3))
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(recentCards, key = { it.first }) { (title, _, cover) ->
                            CollectionSquareTile(
                                title = title,
                                countLabel = "${activeMedia.take(12).size}",
                                coverItem = cover,
                                tileSize = 118.dp,
                                onClick = {
                                    viewModel.submitSearchQuery(title.substringAfter(" "))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = if (isBangla) "পরামর্শ (Suggestions)" else "Suggestions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        suggestions.forEach { chip ->
                            GlassChip(
                                label = chip,
                                onClick = { viewModel.submitSearchQuery(chip) }
                            )
                        }
                    }

                    if (recentSearches.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "সাম্প্রতিক অনুসন্ধান" else "Recent Searches",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            GlassButton(
                                onClick = { viewModel.clearRecentSearches() },
                                text = if (isBangla) "মুছুন" else "Clear",
                                testTag = "search_clear_recents_button"
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassCard {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                recentSearches.forEach { rec ->
                                    FlatTonalRow(
                                        title = rec.query,
                                        leadingIcon = Icons.Default.History,
                                        onClick = { viewModel.updateSearchQuery(rec.query) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Photos / Collections result tabs matching video 01:36
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassSegmentedControl(
                            options = listOf("Photos", "Collections"),
                            selectedOption = activeResultTab,
                            onOptionSelected = { activeResultTab = it },
                            labelProvider = { it },
                            modifier = Modifier.width(220.dp),
                            testTagPrefix = "search_result_tab"
                        )
                        Text(
                            text = "${results.size} Items",
                            style = MaterialTheme.typography.labelLarge,
                            color = config.accentTint
                        )
                    }

                    if (results.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            GlassCard {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = config.accentTint,
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Text(
                                        text = if (isBangla) "কোনো ফলাফল নেই" else "No Results for \"$query\"",
                                        style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Try searching for \"Beach\", \"Santorini\", \"2025\", or \"videos last month\".",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(bottom = 140.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(results, key = { it.id }) { item ->
                                LibraryGridThumbnailCell(
                                    item = item,
                                    isSquare = true,
                                    showBadges = showBadges,
                                    isSelectionMode = false,
                                    isSelected = false,
                                    accentColor = config.accentTint,
                                    onClick = {
                                        viewModel.submitSearchQuery(query)
                                        viewModel.openViewer(item.id)
                                    },
                                    onLongClick = {}
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingOverlayContent = {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Live floating autocomplete pill above the search bar (matching video 01:40 "Beach 89")
                if (query.isNotBlank() && results.isNotEmpty()) {
                    LiquidGlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.submitSearchQuery(query) },
                        cornerRadius = 18.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = config.accentTint,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = query.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "${results.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Bottom Glass Search Field within thumb reach
                GlassSearchField(
                    query = query,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onClear = { viewModel.updateSearchQuery("") },
                    onClose = { viewModel.closeSearch() },
                    placeholder = if (isBangla) "স্থান, তারিখ, 'videos last month'…" else "Places, People, Scenes and More"
                )
            }
        }
    )
}
