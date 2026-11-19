package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MediaItem
import com.example.glass.GlassAlert
import com.example.glass.GlassBackdropProvider
import com.example.glass.GlassBadge
import com.example.glass.GlassButton
import com.example.glass.GlassChip
import com.example.glass.GlassContainer
import com.example.glass.GlassMenu
import com.example.glass.GlassMenuItemData
import com.example.glass.GlassToolbar
import com.example.glass.GlassVariant
import com.example.glass.LiquidGlassSurface
import com.example.glass. GlassCard
import com.example.ui.components.GlasslyMediaThumbnail
import com.example.viewmodel.AppSubScreen
import com.example.viewmodel.GlasslyViewModel
import kotlinx.coroutines.delay
import java.util.Locale

private enum class AlbumSortOption(val label: String) {
    NEWEST_FIRST("Newest First"),
    OLDEST_FIRST("Oldest First"),
    NAME_A_Z("Name (A–Z)"),
    LARGEST_SIZE("Largest File Size")
}

@Composable
fun ManagementSubScreenHost(
    subScreen: AppSubScreen,
    viewModel: GlasslyViewModel,
    onBack: () -> Unit,
    onOpenPhoto: (Long) -> Unit
) {
    if (subScreen == AppSubScreen.None) return
    BackHandler(onBack = onBack)

    val allMedia by viewModel.allMergedMedia.collectAsStateWithLifecycle()

    when (subScreen) {
        AppSubScreen.None -> Unit

        is AppSubScreen.AlbumDetail -> {
            val filtered = remember(allMedia, subScreen) {
                allMedia.filter { !it.isTrashed && !it.isHidden }.filter(subScreen.filterPredicate)
            }
            AlbumDetailScreen(
                title = subScreen.title,
                items = filtered,
                viewModel = viewModel,
                onBack = onBack,
                onOpenPhoto = onOpenPhoto
            )
        }

        AppSubScreen.RecentlyDeleted -> {
            RecentlyDeletedScreen(
                viewModel = viewModel,
                onBack = onBack
            )
        }

        AppSubScreen.HiddenAlbum -> {
            HiddenAlbumScreen(
                viewModel = viewModel,
                onBack = onBack,
                onOpenPhoto = onOpenPhoto
            )
        }

        AppSubScreen.FavoritesCollection -> {
            val favorites by viewModel.favoriteMedia.collectAsStateWithLifecycle()
            AlbumDetailScreen(
                title = "Favorites",
                items = favorites,
                viewModel = viewModel,
                onBack = onBack,
                onOpenPhoto = onOpenPhoto
            )
        }

        AppSubScreen.DuplicatesCleaner -> {
            DuplicatesCleanerScreen(
                viewModel = viewModel,
                onBack = onBack,
                onOpenPhoto = onOpenPhoto
            )
        }

        AppSubScreen.LargeFilesCleaner -> {
            LargeFilesCleanerScreen(
                viewModel = viewModel,
                onBack = onBack,
                onOpenPhoto = onOpenPhoto
            )
        }

        is AppSubScreen.Slideshow -> {
            SlideshowScreen(
                title = subScreen.title,
                items = subScreen.items,
                onClose = onBack
            )
        }

        AppSubScreen.GlassGalleryDebug -> {
            GlassGalleryDebugScreen(onBack = onBack)
        }
    }
}

@Composable
fun AlbumDetailScreen(
    title: String,
    items: List<MediaItem>,
    viewModel: GlasslyViewModel,
    onBack: () -> Unit,
    onOpenPhoto: (Long) -> Unit
) {
    var sortOption by remember { mutableStateOf(AlbumSortOption.NEWEST_FIRST) }
    var showSortMenu by remember { mutableStateOf(false) }

    val sortedItems = remember(items, sortOption) {
        when (sortOption) {
            AlbumSortOption.NEWEST_FIRST -> items.sortedByDescending { it.dateTakenMillis }
            AlbumSortOption.OLDEST_FIRST -> items.sortedBy { it.dateTakenMillis }
            AlbumSortOption.NAME_A_Z -> items.sortedBy { it.title }
            AlbumSortOption.LARGEST_SIZE -> items.sortedByDescending { it.sizeBytes }
        }
    }

    val statusPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    GlassBackdropProvider(
        backdropLuminanceHint = 0.25f,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (sortedItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp)
                        ) {
                            Text(
                                text = "No items in $title",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Photos and videos matching this collection will appear here automatically.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(
                            top = statusPadding + 88.dp,
                            bottom = navPadding + 32.dp,
                            start = 2.dp,
                            end = 2.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item(span = { GridItemSpan(3) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .clip(RoundedCornerShape(26.dp))
                                    .clickable {
                                        viewModel.navigateToSubScreen(
                                            AppSubScreen.Slideshow(title, sortedItems)
                                        )
                                    }
                            ) {
                                GlasslyMediaThumbnail(
                                    item = sortedItems.first(),
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.72f)
                                                )
                                            )
                                        )
                                )
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${sortedItems.size} items • ${sortOption.label}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                    GlassButton(
                                        onClick = {
                                            viewModel.navigateToSubScreen(
                                                AppSubScreen.Slideshow(title, sortedItems)
                                            )
                                        },
                                        icon = Icons.Default.PlayArrow,
                                        text = "Movie",
                                        variant = GlassVariant.TINTED
                                    )
                                }
                            }
                        }

                        items(sortedItems, key = { it.id }) { item ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clickable { onOpenPhoto(item.id) }
                            ) {
                                GlasslyMediaThumbnail(
                                    item = item,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (item.isFavorite) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Favorite",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(6.dp)
                                            .size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingOverlayContent = {
            Box(modifier = Modifier.fillMaxSize()) {
                GlassToolbar(
                    title = title,
                    subtitle = "${sortedItems.size} Items",
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    onNavigationClick = onBack,
                    actions = {
                        GlassButton(
                            onClick = { showSortMenu = true },
                            icon = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort options"
                        )
                    }
                )

                GlassMenu(
                    expanded = showSortMenu,
                    onDismiss = { showSortMenu = false },
                    items = AlbumSortOption.entries.map { opt ->
                        GlassMenuItemData(
                            title = opt.label,
                            isSelected = sortOption == opt,
                            onClick = { sortOption = opt }
                        )
                    }
                )
            }
        }
    )
}

@Composable
fun RecentlyDeletedScreen(
    viewModel: GlasslyViewModel,
    onBack: () -> Unit
) {
    val trashedItems by viewModel.trashedMedia.collectAsStateWithLifecycle()
    val retentionDays by viewModel.trashRetentionDays.collectAsStateWithLifecycle()
    var showEmptyTrashAlert by remember { mutableStateOf(false) }

    val statusPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    GlassBackdropProvider(
        backdropLuminanceHint = 0.22f,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (trashedItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp)
                        ) {
                            Text(
                                text = "Recently Deleted is Empty",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Deleted photos and videos stay here for $retentionDays days before permanent removal.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(
                            top = statusPadding + 88.dp,
                            bottom = navPadding + 96.dp,
                            start = 2.dp,
                            end = 2.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(trashedItems, key = { it.id }) { item ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clickable {
                                        viewModel.restoreFromTrash(setOf(item.id))
                                    }
                            ) {
                                GlasslyMediaThumbnail(
                                    item = item,
                                    modifier = Modifier.fillMaxSize()
                                )
                                GlassBadge(
                                    text = "${item.daysRemainingInTrash} Days",
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingOverlayContent = {
            Box(modifier = Modifier.fillMaxSize()) {
                GlassToolbar(
                    title = "Recently Deleted",
                    subtitle = "${trashedItems.size} items • $retentionDays-day retention",
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    onNavigationClick = onBack
                )

                if (trashedItems.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = navPadding + 16.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GlassButton(
                            onClick = {
                                viewModel.restoreFromTrash(trashedItems.map { it.id }.toSet())
                            },
                            text = "Restore All",
                            icon = Icons.Default.RestoreFromTrash,
                            variant = GlassVariant.REGULAR
                        )
                        GlassButton(
                            onClick = { showEmptyTrashAlert = true },
                            text = "Empty Trash",
                            icon = Icons.Default.DeleteForever,
                            variant = GlassVariant.TINTED,
                            isDestructive = true
                        )
                    }
                }

                GlassAlert(
                    visible = showEmptyTrashAlert,
                    title = "Empty Recently Deleted?",
                    message = "Permanently delete ${trashedItems.size} items? This action cannot be undone.",
                    confirmText = "Delete Forever",
                    dismissText = "Cancel",
                    isDestructive = true,
                    onConfirm = {
                        viewModel.emptyTrash()
                        showEmptyTrashAlert = false
                    },
                    onDismiss = { showEmptyTrashAlert = false }
                )
            }
        }
    )
}

@Composable
fun HiddenAlbumScreen(
    viewModel: GlasslyViewModel,
    onBack: () -> Unit,
    onOpenPhoto: (Long) -> Unit
) {
    val hiddenAlbumLockEnabled by viewModel.hiddenAlbumLockEnabled.collectAsStateWithLifecycle()
    val isHiddenAlbumUnlocked by viewModel.isHiddenAlbumUnlocked.collectAsStateWithLifecycle()
    val biometricUnlockEnabled by viewModel.biometricUnlockEnabled.collectAsStateWithLifecycle()
    val hiddenPin by viewModel.hiddenPin.collectAsStateWithLifecycle()
    val hiddenItems by viewModel.hiddenMedia.collectAsStateWithLifecycle()

    val isLocked = hiddenAlbumLockEnabled && !isHiddenAlbumUnlocked
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    val statusPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    GlassBackdropProvider(
        backdropLuminanceHint = 0.15f,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0B132B),
                                Color(0xFF1C2541),
                                Color(0xFF0F172A)
                            )
                        )
                    )
            ) {
                if (!isLocked) {
                    if (hiddenItems.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp)
                            ) {
                                Text(
                                    text = "No Hidden Photos",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Photos you hide from your Library are protected here behind your PIN and biometric lock.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.72f)
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(
                                top = statusPadding + 88.dp,
                                bottom = navPadding + 84.dp,
                                start = 2.dp,
                                end = 2.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(hiddenItems, key = { it.id }) { item ->
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clickable { onOpenPhoto(item.id) }
                                ) {
                                    GlasslyMediaThumbnail(
                                        item = item,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    GlassBadge(
                                        text = "Tap to Unhide",
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 6.dp)
                                            .clickable {
                                                viewModel.unhideMedia(setOf(item.id))
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingOverlayContent = {
            Box(modifier = Modifier.fillMaxSize()) {
                GlassToolbar(
                    title = "Hidden",
                    subtitle = if (isLocked) "Locked • Biometric / PIN Required" else "${hiddenItems.size} Private Items",
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    onNavigationClick = onBack,
                    actions = {
                        if (!isLocked) {
                            GlassButton(
                                onClick = { viewModel.lockHiddenAlbum() },
                                icon = Icons.Default.Lock,
                                text = "Lock"
                            )
                        }
                    }
                )

                if (isLocked) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = statusPadding + 96.dp,
                                bottom = navPadding + 28.dp,
                                start = 28.dp,
                                end = 28.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            LiquidGlassSurface(
                                modifier = Modifier.size(64.dp),
                                cornerRadius = 999.dp,
                                variant = GlassVariant.TINTED
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Unlock Hidden Album",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (pinError) "Incorrect PIN. Default PIN is $hiddenPin"
                                else "Enter your 4-digit PIN (Default: $hiddenPin)",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (pinError) Color(0xFFFF453A) else Color.White.copy(alpha = 0.72f)
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                repeat(4) { idx ->
                                    val filled = idx < enteredPin.length
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (filled) Color.White else Color.White.copy(alpha = 0.22f)
                                            )
                                    )
                                }
                            }
                        }

                        // Glass PIN Keypad (1-9, Biometric, 0, Backspace)
                        val rows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("BIO", "0", "DEL")
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            rows.forEach { rowKeys ->
                                Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                                    rowKeys.forEach { key ->
                                        LiquidGlassSurface(
                                            modifier = Modifier
                                                .size(68.dp)
                                                .clickable {
                                                    when (key) {
                                                        "BIO" -> {
                                                            if (biometricUnlockEnabled) {
                                                                viewModel.unlockHiddenAlbumWithBiometrics()
                                                            }
                                                        }
                                                        "DEL" -> {
                                                            if (enteredPin.isNotEmpty()) {
                                                                enteredPin = enteredPin.dropLast(1)
                                                                pinError = false
                                                            }
                                                        }
                                                        else -> {
                                                            if (enteredPin.length < 4) {
                                                                enteredPin += key
                                                                pinError = false
                                                                if (enteredPin.length == 4) {
                                                                    val ok = viewModel.unlockHiddenAlbumWithPin(enteredPin)
                                                                    if (!ok) {
                                                                        pinError = true
                                                                        enteredPin = ""
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                .testTag("pin_key_$key"),
                                            cornerRadius = 999.dp,
                                            variant = if (key == "BIO") GlassVariant.TINTED else GlassVariant.REGULAR
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                when (key) {
                                                    "BIO" -> Icon(
                                                        imageVector = Icons.Default.Fingerprint,
                                                        contentDescription = "Biometric Unlock",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                    "DEL" -> Icon(
                                                        imageVector = Icons.Default.Backspace,
                                                        contentDescription = "Backspace",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                    else -> Text(
                                                        text = key,
                                                        style = MaterialTheme.typography.headlineSmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DuplicatesCleanerScreen(
    viewModel: GlasslyViewModel,
    onBack: () -> Unit,
    onOpenPhoto: (Long) -> Unit
) {
    val duplicateGroups by viewModel.duplicateGroups.collectAsStateWithLifecycle()
    val statusPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    GlassBackdropProvider(
        backdropLuminanceHint = 0.25f,
        backgroundContent = {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = statusPadding + 88.dp,
                    bottom = navPadding + 40.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (duplicateGroups.isEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF30D158),
                                    modifier = Modifier.size(32.dp)
                                )
                                Column {
                                    Text(
                                        text = "No Exact Duplicates Found",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Your library is clean! All duplicate bursts have been merged.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    duplicateGroups.entries.forEach { entry ->
                        val groupId = entry.key
                        val groupItems = entry.value
                        item(key = groupId) {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${groupItems.size} Identical Shots",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        val totalMb = groupItems.sumOf { it.sizeBytes } / (1024.0 * 1024.0)
                                        Text(
                                            text = String.format(Locale.US, "%.1f MB total • Keep highest resolution", totalMb),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                    GlassButton(
                                        onClick = { viewModel.mergeDuplicateGroup(groupItems) },
                                        text = "Merge",
                                        variant = GlassVariant.TINTED
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    groupItems.forEachIndexed { idx, dupItem ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(14.dp))
                                                .clickable { onOpenPhoto(dupItem.id) }
                                        ) {
                                            GlasslyMediaThumbnail(
                                                item = dupItem,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            GlassBadge(
                                                text = if (idx == 0) "BEST • ${dupItem.sizeFormatted}"
                                                else dupItem.sizeFormatted,
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingOverlayContent = {
            GlassToolbar(
                title = "Duplicates",
                subtitle = "${duplicateGroups.size} Duplicate Sets",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        }
    )
}

@Composable
fun LargeFilesCleanerScreen(
    viewModel: GlasslyViewModel,
    onBack: () -> Unit,
    onOpenPhoto: (Long) -> Unit
) {
    val largeItems by viewModel.largeFilesMedia.collectAsStateWithLifecycle()
    val totalReclaimableMb = remember(largeItems) {
        largeItems.sumOf { it.sizeBytes } / (1024.0 * 1024.0)
    }
    val statusPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    GlassBackdropProvider(
        backdropLuminanceHint = 0.25f,
        backgroundContent = {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = statusPadding + 88.dp,
                    bottom = navPadding + 40.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = String.format(Locale.US, "%.1f MB Reviewable Media", totalReclaimableMb),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${largeItems.size} 4K Videos & Uncompressed RAW DNG files",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                items(largeItems, key = { it.id }) { item ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onOpenPhoto(item.id) }
                            ) {
                                GlasslyMediaThumbnail(
                                    item = item,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${item.sizeFormatted} • ${item.widthPx} × ${item.heightPx}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            GlassButton(
                                onClick = { viewModel.trashSingle(item.id) },
                                text = "Trash",
                                variant = GlassVariant.REGULAR
                            )
                        }
                    }
                }
            }
        },
        floatingOverlayContent = {
            GlassToolbar(
                title = "Large Files",
                subtitle = "${largeItems.size} High-Bitrate Files",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        }
    )
}

@Composable
fun SlideshowScreen(
    title: String,
    items: List<MediaItem>,
    onClose: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var speedSeconds by remember { mutableIntStateOf(3) }

    LaunchedEffect(isPlaying, currentIndex, speedSeconds, items.size) {
        if (isPlaying && items.isNotEmpty()) {
            delay(speedSeconds * 1000L)
            currentIndex = (currentIndex + 1) % items.size
        }
    }

    val statusPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    GlassBackdropProvider(
        backdropLuminanceHint = 0.2f,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { showControls = !showControls }
            ) {
                if (items.isNotEmpty()) {
                    val item = items[currentIndex.coerceIn(items.indices)]
                    AnimatedContent(
                        targetState = item,
                        transitionSpec = {
                            fadeIn(tween(700)) togetherWith fadeOut(tween(700))
                        },
                        label = "slideshow_crossfade"
                    ) { targetItem ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            GlasslyMediaThumbnail(
                                item = targetItem,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        },
        floatingOverlayContent = {
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(tween(220)),
                exit = fadeOut(tween(220)),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = statusPadding + 12.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassButton(
                            onClick = onClose,
                            icon = Icons.Default.Close,
                            text = title,
                            variant = GlassVariant.REGULAR
                        )
                        GlassBadge(
                            text = "${currentIndex + 1} / ${items.size.coerceAtLeast(1)} • Clean Mode"
                        )
                    }

                    LiquidGlassSurface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = navPadding + 24.dp)
                            .height(54.dp),
                        cornerRadius = 999.dp,
                        variant = GlassVariant.REGULAR
                    ) {
                        GlassContainer(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            spacing = 14.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous slide",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (items.isNotEmpty()) {
                                            currentIndex = (currentIndex - 1 + items.size) % items.size
                                        }
                                    }
                                    .padding(4.dp)
                            )
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause slideshow" else "Play slideshow",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable { isPlaying = !isPlaying }
                                    .padding(4.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next slide",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (items.isNotEmpty()) {
                                            currentIndex = (currentIndex + 1) % items.size
                                        }
                                    }
                                    .padding(4.dp)
                            )
                            GlassChip(
                                label = "${speedSeconds}s",
                                selected = true,
                                onClick = {
                                    speedSeconds = when (speedSeconds) {
                                        2 -> 3
                                        3 -> 5
                                        else -> 2
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
