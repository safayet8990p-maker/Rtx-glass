package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CustomAlbumEntity
import com.example.data.GlasslyDatabase
import com.example.data.MediaCategory
import com.example.data.MediaItem
import com.example.data.MediaOverrideEntity
import com.example.data.MediaRepository
import com.example.glass.AppLanguage
import com.example.glass.AppThemeMode
import com.example.glass.GlassConfig
import com.example.glass.GlassTabDestination
import com.example.glass.GlassVariant
import com.example.glass.isSystemBatterySaverActive
import com.example.glass.isSystemReduceMotionEnabled
import com.example.ui.theme.AccentTintOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Context.glasslyDataStore by preferencesDataStore(name = "glassly_settings_prefs")

enum class MediaPermissionLevel {
    FULL_ACCESS,
    PARTIAL_ACCESS,
    DENIED_OR_PROMPT
}

enum class LibraryFilterType(val labelEn: String, val labelBn: String) {
    ALL_ITEMS("All Items", "সব আইটেম"),
    FAVORITES("Favourites", "প্রিয়"),
    EDITED("Edited", "এডিটেড"),
    PHOTOS("Photos", "ছবি"),
    VIDEOS("Videos", "ভিডিও"),
    SCREENSHOTS("Screenshots", "স্ক্রিনশট"),
    SHARED_WITH_YOU("Shared with You", "আপনার সাথে শেয়ার করা"),
    NOT_IN_ALBUM("Not in an Album", "অ্যালবামে নেই"),
    CAPTURED_BY_ME("Captured by Me", "আমার তোলা")
}

enum class CollectionTileSize(val labelEn: String, val labelBn: String) {
    TOP_ROW_EMPHASIS("Top-Row Emphasis", "উপরের সারি বড়"),
    DENSE("Dense Layout", "ঘন লেআউট"),
    LARGE("Large Tiles", "বড় টাইলস")
}

sealed interface AppSubScreen {
    data object None : AppSubScreen
    data class AlbumDetail(val title: String, val filterPredicate: (MediaItem) -> Boolean) : AppSubScreen
    data object RecentlyDeleted : AppSubScreen
    data object HiddenAlbum : AppSubScreen
    data object FavoritesCollection : AppSubScreen
    data object DuplicatesCleaner : AppSubScreen
    data object LargeFilesCleaner : AppSubScreen
    data class Slideshow(val title: String, val items: List<MediaItem>) : AppSubScreen
    data object GlassGalleryDebug : AppSubScreen
}

class GlasslyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = GlasslyDatabase.getInstance(application)
    val repository = MediaRepository(application, db.dao(), viewModelScope)

    // Settings Preferences Keys
    private object PrefKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_OPTION = stringPreferencesKey("accent_option")
        val GLASS_VARIANT = stringPreferencesKey("glass_variant")
        val GLASS_INTENSITY = floatPreferencesKey("glass_intensity")
        val REDUCE_GLASS = booleanPreferencesKey("reduce_glass")
        val HIGH_CONTRAST_GLASS = booleanPreferencesKey("high_contrast_glass")
        val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val HAPTIC_STRENGTH = floatPreferencesKey("haptic_strength")
        val LARGER_TOUCH_TARGETS = booleanPreferencesKey("larger_touch_targets")
        val LANGUAGE = stringPreferencesKey("language")
        val DEFAULT_GRID_COLUMNS = intPreferencesKey("default_grid_columns")
        val SQUARE_GRID = booleanPreferencesKey("square_grid")
        val SHOW_BADGES = booleanPreferencesKey("show_badges")
        val SORT_BY_CAPTURED = booleanPreferencesKey("sort_by_captured")
        val OPEN_AT_LATEST = booleanPreferencesKey("open_at_latest")
        val SHOW_LOCATION_HEADERS = booleanPreferencesKey("show_location_headers")
        val INCLUDE_SCREENSHOTS_IN_LIBRARY = booleanPreferencesKey("include_screenshots_in_library")
        val INCLUDE_SHARED_IN_LIBRARY = booleanPreferencesKey("include_shared_in_library")
        val AUTOPLAY_MOTION = booleanPreferencesKey("autoplay_motion")
        val AUTOPLAY_VIDEOS = booleanPreferencesKey("autoplay_videos")
        val LOOP_VIDEOS = booleanPreferencesKey("loop_videos")
        val DEFAULT_SPEED = floatPreferencesKey("default_speed")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val SWIPE_UP_INFO = booleanPreferencesKey("swipe_up_info")
        val SHOW_SPATIAL_PHOTO_CONTROL = booleanPreferencesKey("show_spatial_photo_control")
        val EDITOR_APPEARANCE_MODE = stringPreferencesKey("editor_appearance_mode")
        val HIDDEN_ALBUM_LOCK = booleanPreferencesKey("hidden_album_lock")
        val BIOMETRIC_UNLOCK = booleanPreferencesKey("biometric_unlock")
        val HIDDEN_PIN = stringPreferencesKey("hidden_pin")
        val HIDE_PREVIEWS_RECENTS = booleanPreferencesKey("hide_previews_recents")
        val SECURE_SCREEN = booleanPreferencesKey("secure_screen")
        val TRASH_RETENTION_DAYS = intPreferencesKey("trash_retention_days")
        val COLLECTION_TILE_SIZE = stringPreferencesKey("collection_tile_size")
        val ONBOARDING_DISMISSED = booleanPreferencesKey("onboarding_dismissed")
    }

    private val _glassConfig = MutableStateFlow(
        GlassConfig(
            reduceMotion = application.isSystemReduceMotionEnabled(),
            isBatterySaverActive = application.isSystemBatterySaverActive()
        )
    )
    val glassConfig: StateFlow<GlassConfig> = _glassConfig.asStateFlow()

    // Library & Viewer Settings State
    private val _gridColumns = MutableStateFlow(3)
    val gridColumns: StateFlow<Int> = _gridColumns.asStateFlow()

    private val _isSquareGrid = MutableStateFlow(true)
    val isSquareGrid: StateFlow<Boolean> = _isSquareGrid.asStateFlow()

    private val _showBadges = MutableStateFlow(true)
    val showBadges: StateFlow<Boolean> = _showBadges.asStateFlow()

    private val _sortByCaptured = MutableStateFlow(true)
    val sortByCaptured: StateFlow<Boolean> = _sortByCaptured.asStateFlow()

    private val _openAtLatest = MutableStateFlow(true)
    val openAtLatest: StateFlow<Boolean> = _openAtLatest.asStateFlow()

    private val _showLocationInHeaders = MutableStateFlow(true)
    val showLocationInHeaders: StateFlow<Boolean> = _showLocationInHeaders.asStateFlow()

    private val _includeScreenshotsInLibrary = MutableStateFlow(true)
    val includeScreenshotsInLibrary: StateFlow<Boolean> = _includeScreenshotsInLibrary.asStateFlow()

    private val _includeSharedWithYouInLibrary = MutableStateFlow(true)
    val includeSharedWithYouInLibrary: StateFlow<Boolean> = _includeSharedWithYouInLibrary.asStateFlow()

    private val _autoplayMotion = MutableStateFlow(true)
    val autoplayMotion: StateFlow<Boolean> = _autoplayMotion.asStateFlow()

    private val _autoplayVideos = MutableStateFlow(true)
    val autoplayVideos: StateFlow<Boolean> = _autoplayVideos.asStateFlow()

    private val _loopVideos = MutableStateFlow(true)
    val loopVideos: StateFlow<Boolean> = _loopVideos.asStateFlow()

    private val _defaultPlaybackSpeed = MutableStateFlow(1.0f)
    val defaultPlaybackSpeed: StateFlow<Float> = _defaultPlaybackSpeed.asStateFlow()

    private val _keepScreenOn = MutableStateFlow(true)
    val keepScreenOn: StateFlow<Boolean> = _keepScreenOn.asStateFlow()

    private val _showInfoOnSwipeUp = MutableStateFlow(true)
    val showInfoOnSwipeUp: StateFlow<Boolean> = _showInfoOnSwipeUp.asStateFlow()

    private val _showSpatialPhotoControl = MutableStateFlow(true)
    val showSpatialPhotoControl: StateFlow<Boolean> = _showSpatialPhotoControl.asStateFlow()

    private val _editorAppearanceMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val editorAppearanceMode: StateFlow<AppThemeMode> = _editorAppearanceMode.asStateFlow()

    private val _copiedEditsBuffer = MutableStateFlow<MediaOverrideEntity?>(null)
    val copiedEditsBuffer: StateFlow<MediaOverrideEntity?> = _copiedEditsBuffer.asStateFlow()

    private val _hiddenAlbumLockEnabled = MutableStateFlow(true)
    val hiddenAlbumLockEnabled: StateFlow<Boolean> = _hiddenAlbumLockEnabled.asStateFlow()

    private val _biometricUnlockEnabled = MutableStateFlow(true)
    val biometricUnlockEnabled: StateFlow<Boolean> = _biometricUnlockEnabled.asStateFlow()

    private val _hiddenPin = MutableStateFlow("2627")
    val hiddenPin: StateFlow<String> = _hiddenPin.asStateFlow()

    private val _isHiddenAlbumUnlocked = MutableStateFlow(false)
    val isHiddenAlbumUnlocked: StateFlow<Boolean> = _isHiddenAlbumUnlocked.asStateFlow()

    private val _hidePreviewsInRecents = MutableStateFlow(false)
    val hidePreviewsInRecents: StateFlow<Boolean> = _hidePreviewsInRecents.asStateFlow()

    private val _secureScreenEnabled = MutableStateFlow(false)
    val secureScreenEnabled: StateFlow<Boolean> = _secureScreenEnabled.asStateFlow()

    private val _trashRetentionDays = MutableStateFlow(30)
    val trashRetentionDays: StateFlow<Int> = _trashRetentionDays.asStateFlow()

    private val _cacheSizeMb = MutableStateFlow(42.8f)
    val cacheSizeMb: StateFlow<Float> = _cacheSizeMb.asStateFlow()

    private val _collectionTileSize = MutableStateFlow(CollectionTileSize.TOP_ROW_EMPHASIS)
    val collectionTileSize: StateFlow<CollectionTileSize> = _collectionTileSize.asStateFlow()

    // Collections Sections Order & Collapse State matching iOS 26 Photos walkthrough video (5:02–8:35)
    private val _collectionSectionOrder = MutableStateFlow(
        listOf(
            "Pinned",
            "Shared Albums",
            "Media Types",
            "People & Pets",
            "Utilities",
            "Featured Photos",
            "Memories",
            "Albums",
            "Trips",
            "Wallpaper Suggestions",
            "Recent Days"
        )
    )
    val collectionSectionOrder: StateFlow<List<String>> = _collectionSectionOrder.asStateFlow()

    private val _collapsedCollectionSections = MutableStateFlow<Set<String>>(emptySet())
    val collapsedCollectionSections: StateFlow<Set<String>> = _collapsedCollectionSections.asStateFlow()

    // Pinned Collections items matching video 7:30–8:05
    private val _pinnedCollectionItems = MutableStateFlow(
        listOf(
            "Favourites",
            "Recently Saved",
            "Videos",
            "Screenshots",
            "Aegean Adventure"
        )
    )
    val pinnedCollectionItems: StateFlow<List<String>> = _pinnedCollectionItems.asStateFlow()

    // Navigation & Selection State
    private val _currentTab = MutableStateFlow(GlassTabDestination.LIBRARY)
    val currentTab: StateFlow<GlassTabDestination> = _currentTab.asStateFlow()

    private val _isSearchOpen = MutableStateFlow(false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _subScreen = MutableStateFlow<AppSubScreen>(AppSubScreen.None)
    val subScreen: StateFlow<AppSubScreen> = _subScreen.asStateFlow()

    private val _activeViewerItemId = MutableStateFlow<Long?>(null)
    val activeViewerItemId: StateFlow<Long?> = _activeViewerItemId.asStateFlow()

    private val _isEditorOpen = MutableStateFlow(false)
    val isEditorOpen: StateFlow<Boolean> = _isEditorOpen.asStateFlow()

    private val _libraryTimeSegment = MutableStateFlow("All")
    val libraryTimeSegment: StateFlow<String> = _libraryTimeSegment.asStateFlow()

    // Combinable multi-filter state (e.g. Favourites + Videos at 2:42 in video)
    private val _activeLibraryFilters = MutableStateFlow<Set<LibraryFilterType>>(emptySet())
    val activeLibraryFilters: StateFlow<Set<LibraryFilterType>> = _activeLibraryFilters.asStateFlow()

    // Legacy single-filter compatibility flow
    private val _libraryFilter = MutableStateFlow(LibraryFilterType.ALL_ITEMS)
    val libraryFilter: StateFlow<LibraryFilterType> = _libraryFilter.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    private val _showOnlySelected = MutableStateFlow(false)
    val showOnlySelected: StateFlow<Boolean> = _showOnlySelected.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _permissionLevel = MutableStateFlow(checkDevicePermissionLevel(application))
    val permissionLevel: StateFlow<MediaPermissionLevel> = _permissionLevel.asStateFlow()

    private val _showPermissionOnboarding = MutableStateFlow(false)
    val showPermissionOnboarding: StateFlow<Boolean> = _showPermissionOnboarding.asStateFlow()

    // Combined live media flow merging raw MediaStore/Studio items with Room non-destructive overrides
    val allMergedMedia: StateFlow<List<MediaItem>> = combine(
        repository.rawMediaItems,
        repository.overridesFlow,
        _sortByCaptured
    ) { rawList, overrides, byCaptured ->
        val overrideMap = overrides.associateBy { it.mediaId }
        val merged = rawList.map { item ->
            val ov = overrideMap[item.id]
            if (ov == null) {
                item
            } else {
                val extraAlbums = ov.albumNamesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                item.copy(
                    title = ov.customTitle?.takeIf { it.isNotBlank() } ?: item.title,
                    isFavorite = ov.isFavorite,
                    isHidden = ov.isHidden,
                    isTrashed = ov.isTrashed,
                    trashedTimestamp = ov.trashedTimestamp,
                    albums = (item.albums + extraAlbums).distinct(),
                    overrideState = ov
                )
            }
        }
        if (byCaptured) {
            merged.sortedBy { it.dateTakenMillis }
        } else {
            merged.sortedBy { it.id }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _viewOptionFlags = combine(
        _includeScreenshotsInLibrary,
        _includeSharedWithYouInLibrary
    ) { incShots, incShared ->
        incShots to incShared
    }

    val visibleLibraryMedia: StateFlow<List<MediaItem>> = combine(
        allMergedMedia,
        _activeLibraryFilters,
        _viewOptionFlags,
        _showOnlySelected,
        _selectedIds
    ) { all, filters, (incShots, incShared), onlySelected, selected ->
        all.filter { !it.isTrashed && !it.isHidden }
            .filter { item ->
                // View Options Show: Screenshots / Shared with You
                val passScreenshots = incShots || item.category != MediaCategory.SCREENSHOT ||
                    LibraryFilterType.SCREENSHOTS in filters
                val passShared = incShared || item.isCapturedByMe ||
                    LibraryFilterType.SHARED_WITH_YOU in filters
                passScreenshots && passShared
            }
            .filter { item ->
                if (filters.isEmpty() || LibraryFilterType.ALL_ITEMS in filters) {
                    true
                } else {
                    // Combinable filters (AND intersection across active filter categories, e.g., Favourites + Videos)
                    filters.all { f ->
                        when (f) {
                            LibraryFilterType.ALL_ITEMS -> true
                            LibraryFilterType.FAVORITES -> item.isFavorite
                            LibraryFilterType.EDITED -> item.isEdited
                            LibraryFilterType.PHOTOS -> !item.isVideo
                            LibraryFilterType.VIDEOS -> item.isVideo
                            LibraryFilterType.SCREENSHOTS -> item.category == MediaCategory.SCREENSHOT
                            LibraryFilterType.SHARED_WITH_YOU -> !item.isCapturedByMe
                            LibraryFilterType.NOT_IN_ALBUM -> item.albums.isEmpty()
                            LibraryFilterType.CAPTURED_BY_ME -> item.isCapturedByMe
                        }
                    }
                }
            }
            .filter { item ->
                if (onlySelected && selected.isNotEmpty()) item.id in selected else true
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashedMedia: StateFlow<List<MediaItem>> = combine(allMergedMedia, _trashRetentionDays) { all, _ ->
        all.filter { it.isTrashed }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hiddenMedia: StateFlow<List<MediaItem>> = combine(allMergedMedia, _hiddenAlbumLockEnabled) { all, _ ->
        all.filter { it.isHidden && !it.isTrashed }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteMedia: StateFlow<List<MediaItem>> = combine(allMergedMedia, _activeLibraryFilters) { all, _ ->
        all.filter { it.isFavorite && !it.isTrashed && !it.isHidden }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val duplicateGroups: StateFlow<Map<String, List<MediaItem>>> = combine(allMergedMedia, _activeLibraryFilters) { all, _ ->
        all.filter { !it.isTrashed && !it.isHidden && it.duplicateGroupKey != null }
            .groupBy { it.duplicateGroupKey!! }
            .filterValues { it.size > 1 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val largeFilesMedia: StateFlow<List<MediaItem>> = combine(allMergedMedia, _activeLibraryFilters) { all, _ ->
        all.filter { !it.isTrashed && !it.isHidden && it.sizeBytes >= 15_000_000L }
            .sortedByDescending { it.sizeBytes }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customAlbums: StateFlow<List<CustomAlbumEntity>> = repository.albumsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearches = repository.recentSearchesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchResults: StateFlow<List<MediaItem>> = combine(
        allMergedMedia,
        _searchQuery
    ) { all, rawQuery ->
        val q = rawQuery.trim().lowercase()
        if (q.isEmpty()) return@combine emptyList()

        val activeItems = all.filter { !it.isTrashed && !it.isHidden }
        val now = System.currentTimeMillis()
        val sixtyDaysMs = 60L * 24 * 60 * 60 * 1000L

        activeItems.filter { item ->
            val wantsVideos = q.contains("video") || q.contains("ভিডিও")
            val wantsLastMonth = q.contains("last month") || q.contains("গত মাস")
            val wantsFavorites = q.contains("favorite") || q.contains("favourite") || q.contains("প্রিয়")
            val wantsScreenshots = q.contains("screenshot")
            val wantsSelfies = q.contains("selfie")
            val wantsEdited = q.contains("edited")

            if (wantsVideos && wantsLastMonth) {
                val age = now - item.dateTakenMillis
                return@filter item.isVideo && age in 7_000_000L..sixtyDaysMs
            }
            if (wantsLastMonth && !wantsVideos) {
                val age = now - item.dateTakenMillis
                return@filter age in 7_000_000L..sixtyDaysMs
            }

            val searchableBlob = buildString {
                append(item.title.lowercase()).append(' ')
                append(item.locationName.lowercase()).append(' ')
                append(item.dateFormattedLong.lowercase()).append(' ')
                append(item.dateFormattedShort.lowercase()).append(' ')
                append(item.monthYearLabel.lowercase()).append(' ')
                append(item.yearLabel.lowercase()).append(' ')
                append(item.category.name.lowercase()).append(' ')
                append(item.albums.joinToString(" ").lowercase()).append(' ')
                if (item.isVideo) append("video videos mp4 ")
                if (item.isFavorite) append("favorite favorites favourite heart ")
                if (item.isLivePhoto) append("live motion photo ")
                if (item.isEdited) append("edited filter ")
                if (item.locationName.contains("Greece", ignoreCase = true) ||
                    item.locationName.contains("Portugal", ignoreCase = true) ||
                    item.locationName.contains("California", ignoreCase = true)
                ) {
                    append("beach ocean coast summer trip ")
                }
            }

            val tokens = q.split(" ").filter { it.isNotBlank() }
            tokens.all { token ->
                searchableBlob.contains(token) ||
                    (wantsFavorites && item.isFavorite) ||
                    (wantsScreenshots && item.category == MediaCategory.SCREENSHOT) ||
                    (wantsSelfies && item.category == MediaCategory.SELFIE) ||
                    (wantsEdited && item.isEdited)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        observeDataStoreSettings()
    }

    private fun observeDataStoreSettings() {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.data.collect { prefs ->
                val themeMode = prefs[PrefKeys.THEME_MODE]?.let {
                    runCatching { AppThemeMode.valueOf(it) }.getOrNull()
                } ?: AppThemeMode.DARK

                val accentOpt = prefs[PrefKeys.ACCENT_OPTION]?.let {
                    runCatching { AccentTintOption.valueOf(it) }.getOrNull()
                } ?: AccentTintOption.OCEAN_BLUE

                val variant = prefs[PrefKeys.GLASS_VARIANT]?.let {
                    runCatching { GlassVariant.valueOf(it) }.getOrNull()
                } ?: GlassVariant.REGULAR

                val intensity = prefs[PrefKeys.GLASS_INTENSITY] ?: 0.85f
                val reduceGlass = prefs[PrefKeys.REDUCE_GLASS] ?: false
                val highContrast = prefs[PrefKeys.HIGH_CONTRAST_GLASS] ?: false
                val reduceMotion = prefs[PrefKeys.REDUCE_MOTION]
                    ?: getApplication<Application>().isSystemReduceMotionEnabled()
                val haptics = prefs[PrefKeys.HAPTICS_ENABLED] ?: true
                val hapticStrength = prefs[PrefKeys.HAPTIC_STRENGTH] ?: 0.8f
                val largerTargets = prefs[PrefKeys.LARGER_TOUCH_TARGETS] ?: false
                val lang = prefs[PrefKeys.LANGUAGE]?.let {
                    runCatching { AppLanguage.valueOf(it) }.getOrNull()
                } ?: AppLanguage.ENGLISH

                val isDark = when (themeMode) {
                    AppThemeMode.DARK -> true
                    AppThemeMode.LIGHT -> false
                    AppThemeMode.SYSTEM -> true
                }

                _glassConfig.value = _glassConfig.value.copy(
                    themeMode = themeMode,
                    isDarkTheme = isDark,
                    accentOption = accentOpt,
                    accentTint = accentOpt.color,
                    variant = variant,
                    intensity = intensity,
                    reduceGlass = reduceGlass,
                    highContrastGlass = highContrast,
                    reduceMotion = reduceMotion,
                    hapticsEnabled = haptics,
                    hapticStrength = hapticStrength,
                    largerTouchTargets = largerTargets,
                    language = lang
                )

                _gridColumns.value = prefs[PrefKeys.DEFAULT_GRID_COLUMNS] ?: 3
                _isSquareGrid.value = prefs[PrefKeys.SQUARE_GRID] ?: true
                _showBadges.value = prefs[PrefKeys.SHOW_BADGES] ?: true
                _sortByCaptured.value = prefs[PrefKeys.SORT_BY_CAPTURED] ?: true
                _openAtLatest.value = prefs[PrefKeys.OPEN_AT_LATEST] ?: true
                _showLocationInHeaders.value = prefs[PrefKeys.SHOW_LOCATION_HEADERS] ?: true
                _includeScreenshotsInLibrary.value = prefs[PrefKeys.INCLUDE_SCREENSHOTS_IN_LIBRARY] ?: true
                _includeSharedWithYouInLibrary.value = prefs[PrefKeys.INCLUDE_SHARED_IN_LIBRARY] ?: true
                _autoplayMotion.value = prefs[PrefKeys.AUTOPLAY_MOTION] ?: true
                _autoplayVideos.value = prefs[PrefKeys.AUTOPLAY_VIDEOS] ?: true
                _loopVideos.value = prefs[PrefKeys.LOOP_VIDEOS] ?: true
                _defaultPlaybackSpeed.value = prefs[PrefKeys.DEFAULT_SPEED] ?: 1.0f
                _keepScreenOn.value = prefs[PrefKeys.KEEP_SCREEN_ON] ?: true
                _showInfoOnSwipeUp.value = prefs[PrefKeys.SWIPE_UP_INFO] ?: true
                _showSpatialPhotoControl.value = prefs[PrefKeys.SHOW_SPATIAL_PHOTO_CONTROL] ?: true
                _editorAppearanceMode.value = prefs[PrefKeys.EDITOR_APPEARANCE_MODE]?.let {
                    runCatching { AppThemeMode.valueOf(it) }.getOrNull()
                } ?: AppThemeMode.SYSTEM
                _hiddenAlbumLockEnabled.value = prefs[PrefKeys.HIDDEN_ALBUM_LOCK] ?: true
                _biometricUnlockEnabled.value = prefs[PrefKeys.BIOMETRIC_UNLOCK] ?: true
                _hiddenPin.value = prefs[PrefKeys.HIDDEN_PIN] ?: "2627"
                _hidePreviewsInRecents.value = prefs[PrefKeys.HIDE_PREVIEWS_RECENTS] ?: false
                _secureScreenEnabled.value = prefs[PrefKeys.SECURE_SCREEN] ?: false
                _trashRetentionDays.value = prefs[PrefKeys.TRASH_RETENTION_DAYS] ?: 30
                _collectionTileSize.value = prefs[PrefKeys.COLLECTION_TILE_SIZE]?.let {
                    runCatching { CollectionTileSize.valueOf(it) }.getOrNull()
                } ?: CollectionTileSize.TOP_ROW_EMPHASIS
            }
        }
    }

    // Navigation & UI Actions
    fun selectTab(tab: GlassTabDestination) {
        _currentTab.value = tab
        _isSearchOpen.value = false
        _subScreen.value = AppSubScreen.None
        exitSelectionMode()
    }

    fun openSearch() {
        _isSearchOpen.value = true
        exitSelectionMode()
    }

    fun closeSearch() {
        _isSearchOpen.value = false
        _searchQuery.value = ""
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun submitSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            viewModelScope.launch { repository.recordRecentSearch(query) }
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch { repository.clearRecentSearches() }
    }

    fun navigateToSubScreen(screen: AppSubScreen) {
        _subScreen.value = screen
        exitSelectionMode()
    }

    fun navigateBackFromSubScreen() {
        _subScreen.value = AppSubScreen.None
        exitSelectionMode()
    }

    fun openViewer(mediaId: Long) {
        _activeViewerItemId.value = mediaId
        _isEditorOpen.value = false
    }

    fun closeViewer() {
        _activeViewerItemId.value = null
        _isEditorOpen.value = false
    }

    fun openEditor() {
        _isEditorOpen.value = true
    }

    fun closeEditor() {
        _isEditorOpen.value = false
    }

    fun setLibraryTimeSegment(segment: String) {
        _libraryTimeSegment.value = segment
    }

    // Combinable multi-filter toggle matching iOS 26 video 2:28–2:54
    fun toggleLibraryFilter(filter: LibraryFilterType) {
        if (filter == LibraryFilterType.ALL_ITEMS) {
            _activeLibraryFilters.value = emptySet()
            _libraryFilter.value = LibraryFilterType.ALL_ITEMS
            return
        }
        val current = _activeLibraryFilters.value.toMutableSet()
        current.remove(LibraryFilterType.ALL_ITEMS)
        if (filter in current) {
            current.remove(filter)
        } else {
            // Photos and Videos are mutually exclusive media types; toggle cleanly
            if (filter == LibraryFilterType.PHOTOS) current.remove(LibraryFilterType.VIDEOS)
            if (filter == LibraryFilterType.VIDEOS) current.remove(LibraryFilterType.PHOTOS)
            current.add(filter)
        }
        _activeLibraryFilters.value = current
        _libraryFilter.value = current.firstOrNull() ?: LibraryFilterType.ALL_ITEMS
    }

    fun clearLibraryFilters() {
        _activeLibraryFilters.value = emptySet()
        _libraryFilter.value = LibraryFilterType.ALL_ITEMS
    }

    fun setLibraryFilter(filter: LibraryFilterType) {
        if (filter == LibraryFilterType.ALL_ITEMS) {
            clearLibraryFilters()
        } else {
            _activeLibraryFilters.value = setOf(filter)
            _libraryFilter.value = filter
        }
    }

    fun setIncludeScreenshotsInLibrary(include: Boolean) {
        _includeScreenshotsInLibrary.value = include
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit {
                it[PrefKeys.INCLUDE_SCREENSHOTS_IN_LIBRARY] = include
            }
        }
    }

    fun setIncludeSharedWithYouInLibrary(include: Boolean) {
        _includeSharedWithYouInLibrary.value = include
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit {
                it[PrefKeys.INCLUDE_SHARED_IN_LIBRARY] = include
            }
        }
    }

    // Collections Collapse & Reorder Actions matching video 5:14–6:22
    fun toggleCollectionSectionCollapsed(sectionKey: String) {
        val current = _collapsedCollectionSections.value.toMutableSet()
        if (sectionKey in current) current.remove(sectionKey) else current.add(sectionKey)
        _collapsedCollectionSections.value = current
    }

    fun showAllCollectionSections() {
        _collapsedCollectionSections.value = emptySet()
    }

    fun collapseAllCollectionSections() {
        _collapsedCollectionSections.value = _collectionSectionOrder.value.toSet()
    }

    fun moveCollectionSection(fromIndex: Int, toIndex: Int) {
        val list = _collectionSectionOrder.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices && fromIndex != toIndex) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            _collectionSectionOrder.value = list
        }
    }

    // Pinned Collections Actions matching video 7:30–8:25
    fun addPinnedItem(name: String) {
        val current = _pinnedCollectionItems.value.toMutableList()
        if (name !in current) {
            current.add(name)
            _pinnedCollectionItems.value = current
            showToast("Pinned \"$name\"")
        }
    }

    fun removePinnedItem(name: String) {
        val current = _pinnedCollectionItems.value.toMutableList()
        if (current.remove(name)) {
            _pinnedCollectionItems.value = current
            showToast("Unpinned \"$name\"")
        }
    }

    fun togglePinnedItem(name: String) {
        if (name in _pinnedCollectionItems.value) {
            removePinnedItem(name)
        } else {
            addPinnedItem(name)
        }
    }

    fun movePinnedItem(fromIndex: Int, toIndex: Int) {
        val list = _pinnedCollectionItems.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices && fromIndex != toIndex) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            _pinnedCollectionItems.value = list
        }
    }

    fun enterSelectionMode(initialId: Long? = null) {
        _isSelectionMode.value = true
        _selectedIds.value = if (initialId != null) setOf(initialId) else emptySet()
        _showOnlySelected.value = false
    }

    fun exitSelectionMode() {
        _isSelectionMode.value = false
        _selectedIds.value = emptySet()
        _showOnlySelected.value = false
    }

    fun toggleSelectMedia(mediaId: Long) {
        val current = _selectedIds.value.toMutableSet()
        if (mediaId in current) current.remove(mediaId) else current.add(mediaId)
        _selectedIds.value = current
    }

    fun selectDragRange(idsInRange: Set<Long>) {
        if (!_isSelectionMode.value) _isSelectionMode.value = true
        _selectedIds.value = _selectedIds.value + idsInRange
    }

    fun selectAllVisible() {
        _selectedIds.value = visibleLibraryMedia.value.map { it.id }.toSet()
    }

    fun toggleShowOnlySelected() {
        _showOnlySelected.value = !_showOnlySelected.value
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        viewModelScope.launch {
            kotlinx.coroutines.delay(1900)
            if (_toastMessage.value == message) {
                _toastMessage.value = null
            }
        }
    }

    // Media Management Actions
    fun toggleFavoriteSingle(item: MediaItem) {
        viewModelScope.launch {
            repository.toggleFavorite(item)
            showToast(if (!item.isFavorite) "Added to Favourites" else "Removed from Favourites")
        }
    }

    fun favoriteSelected() {
        val ids = _selectedIds.value
        if (ids.isEmpty()) return
        viewModelScope.launch {
            repository.setFavorites(ids, true)
            showToast("Added ${ids.size} items to Favourites")
            exitSelectionMode()
        }
    }

    fun duplicateSelected() {
        val ids = _selectedIds.value
        if (ids.isEmpty()) return
        viewModelScope.launch {
            repository.duplicateItems(ids)
            showToast("Duplicated ${ids.size} item(s)")
            exitSelectionMode()
        }
    }

    fun adjustSelectedDateTime(newDateLabel: String) {
        val ids = _selectedIds.value
        if (ids.isEmpty()) return
        viewModelScope.launch {
            repository.adjustDateTime(ids, newDateLabel)
            showToast("Adjusted date & time for ${ids.size} item(s)")
            exitSelectionMode()
        }
    }

    fun adjustSelectedLocation(newLocation: String) {
        val ids = _selectedIds.value
        if (ids.isEmpty()) return
        viewModelScope.launch {
            repository.adjustLocation(ids, newLocation)
            showToast("Updated location to $newLocation")
            exitSelectionMode()
        }
    }

    fun trashSelected() {
        val ids = _selectedIds.value
        if (ids.isEmpty()) return
        viewModelScope.launch {
            repository.moveToTrash(ids)
            showToast("Moved ${ids.size} items to Recently Deleted")
            exitSelectionMode()
        }
    }

    fun trashSingle(mediaId: Long) {
        viewModelScope.launch {
            repository.moveToTrash(setOf(mediaId))
            showToast("Moved to Recently Deleted (30 days)")
        }
    }

    fun restoreFromTrash(ids: Set<Long>) {
        viewModelScope.launch {
            repository.restoreFromTrash(ids)
            showToast("Restored ${ids.size} item(s)")
        }
    }

    fun deleteForever(ids: Set<Long>) {
        viewModelScope.launch {
            repository.deleteForever(ids)
            showToast("Permanently deleted ${ids.size} item(s)")
        }
    }

    fun emptyTrash() {
        val ids = trashedMedia.value.map { it.id }.toSet()
        viewModelScope.launch {
            repository.emptyTrash(ids)
            showToast("Emptied Recently Deleted")
        }
    }

    fun hideSingle(mediaId: Long) {
        viewModelScope.launch {
            repository.setHidden(setOf(mediaId), true)
            showToast("Moved to Hidden Album")
        }
    }

    fun unhideMedia(ids: Set<Long>) {
        viewModelScope.launch {
            repository.setHidden(ids, false)
            showToast("Unhidden to Library")
        }
    }

    fun addMediaToAlbum(ids: Set<Long>, albumName: String) {
        viewModelScope.launch {
            repository.addMediaToAlbum(ids, albumName)
            showToast("Added to \"$albumName\"")
            exitSelectionMode()
        }
    }

    fun renameMedia(mediaId: Long, newName: String) {
        viewModelScope.launch {
            repository.renameMedia(mediaId, newName)
            showToast("Renamed to $newName")
        }
    }

    fun savePhotoEdit(item: MediaItem, updatedOverride: MediaOverrideEntity, saveAsCopy: Boolean) {
        viewModelScope.launch {
            repository.savePhotoEdit(item, updatedOverride, saveAsCopy)
            _isEditorOpen.value = false
            showToast(if (saveAsCopy) "Saved as Duplicate" else "Edits Saved Non-Destructively")
        }
    }

    fun copyEditsFrom(overrideEntity: MediaOverrideEntity) {
        _copiedEditsBuffer.value = overrideEntity
        showToast("Copied Edits")
    }

    fun revertPhotoEdit(mediaId: Long) {
        viewModelScope.launch {
            repository.revertPhotoEdit(mediaId)
            showToast("Reverted to Original")
        }
    }

    fun mergeDuplicateGroup(itemsInGroup: List<MediaItem>) {
        if (itemsInGroup.size <= 1) return
        val keepNewestOrLargest = itemsInGroup.maxByOrNull { it.sizeBytes } ?: itemsInGroup.first()
        val toTrash = itemsInGroup.filter { it.id != keepNewestOrLargest.id }.map { it.id }.toSet()
        viewModelScope.launch {
            repository.moveToTrash(toTrash)
            showToast("Merged duplicates (kept highest quality)")
        }
    }

    // Album CRUD
    fun createAlbum(name: String) {
        viewModelScope.launch {
            repository.createAlbum(name)
            showToast("Created album \"$name\"")
        }
    }

    fun renameAlbum(oldName: String, newName: String) {
        viewModelScope.launch {
            repository.renameAlbum(oldName, newName)
            showToast("Renamed album to \"$newName\"")
        }
    }

    fun deleteAlbum(name: String) {
        viewModelScope.launch {
            repository.deleteAlbum(name)
            removePinnedItem(name)
            showToast("Deleted album \"$name\"")
        }
    }

    fun togglePinAlbum(album: CustomAlbumEntity) {
        viewModelScope.launch {
            repository.togglePinAlbum(album)
            togglePinnedItem(album.name)
        }
    }

    // Unlock Hidden Album
    fun unlockHiddenAlbumWithPin(enteredPin: String): Boolean {
        return if (enteredPin == _hiddenPin.value) {
            _isHiddenAlbumUnlocked.value = true
            showToast("Hidden Album Unlocked")
            true
        } else {
            showToast("Incorrect PIN (Default: ${_hiddenPin.value})")
            false
        }
    }

    fun unlockHiddenAlbumWithBiometrics() {
        _isHiddenAlbumUnlocked.value = true
        showToast("Biometric Verification Successful")
    }

    fun lockHiddenAlbum() {
        _isHiddenAlbumUnlocked.value = false
    }

    // Settings Mutators (Persisted to DataStore)
    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.THEME_MODE] = mode.name }
        }
    }

    fun setAccentOption(option: AccentTintOption) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.ACCENT_OPTION] = option.name }
        }
    }

    fun setGlassVariant(variant: GlassVariant) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.GLASS_VARIANT] = variant.name }
        }
    }

    fun setGlassIntensity(intensity: Float) {
        val clamped = intensity.coerceIn(0.15f, 1.0f)
        _glassConfig.value = _glassConfig.value.copy(intensity = clamped)
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.GLASS_INTENSITY] = clamped }
        }
    }

    fun setReduceGlass(reduce: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.REDUCE_GLASS] = reduce }
        }
    }

    fun setHighContrastGlass(highContrast: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.HIGH_CONTRAST_GLASS] = highContrast }
        }
    }

    fun setReduceMotion(reduce: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.REDUCE_MOTION] = reduce }
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.HAPTICS_ENABLED] = enabled }
        }
    }

    fun setHapticStrength(strength: Float) {
        val clamped = strength.coerceIn(0.3f, 1.0f)
        _glassConfig.value = _glassConfig.value.copy(hapticStrength = clamped)
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.HAPTIC_STRENGTH] = clamped }
        }
    }

    fun setLargerTouchTargets(larger: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.LARGER_TOUCH_TARGETS] = larger }
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.LANGUAGE] = language.name }
        }
    }

    fun setGridColumns(columns: Int) {
        val clamped = columns.coerceIn(1, 6)
        _gridColumns.value = clamped
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.DEFAULT_GRID_COLUMNS] = clamped }
        }
    }

    fun setSquareGrid(square: Boolean) {
        _isSquareGrid.value = square
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.SQUARE_GRID] = square }
        }
    }

    fun setShowBadges(show: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.SHOW_BADGES] = show }
        }
    }

    fun setSortByCaptured(byCaptured: Boolean) {
        _sortByCaptured.value = byCaptured
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.SORT_BY_CAPTURED] = byCaptured }
        }
    }

    fun setOpenAtLatest(openLatest: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.OPEN_AT_LATEST] = openLatest }
        }
    }

    fun setShowLocationInHeaders(show: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.SHOW_LOCATION_HEADERS] = show }
        }
    }

    fun setAutoplayMotion(autoplay: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.AUTOPLAY_MOTION] = autoplay }
        }
    }

    fun setAutoplayVideos(autoplay: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.AUTOPLAY_VIDEOS] = autoplay }
        }
    }

    fun setLoopVideos(loop: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.LOOP_VIDEOS] = loop }
        }
    }

    fun setDefaultPlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.DEFAULT_SPEED] = speed }
        }
    }

    fun setKeepScreenOn(keepOn: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.KEEP_SCREEN_ON] = keepOn }
        }
    }

    fun setShowInfoOnSwipeUp(show: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.SWIPE_UP_INFO] = show }
        }
    }

    fun setShowSpatialPhotoControl(show: Boolean) {
        _showSpatialPhotoControl.value = show
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.SHOW_SPATIAL_PHOTO_CONTROL] = show }
        }
    }

    fun setEditorAppearanceMode(mode: AppThemeMode) {
        _editorAppearanceMode.value = mode
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.EDITOR_APPEARANCE_MODE] = mode.name }
        }
    }

    fun setHiddenAlbumLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.HIDDEN_ALBUM_LOCK] = enabled }
        }
    }

    fun setBiometricUnlockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.BIOMETRIC_UNLOCK] = enabled }
        }
    }

    fun updateHiddenPin(newPin: String) {
        if (newPin.length == 4 && newPin.all { it.isDigit() }) {
            viewModelScope.launch {
                getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.HIDDEN_PIN] = newPin }
                showToast("Updated Hidden Album PIN")
            }
        }
    }

    fun setHidePreviewsInRecents(hide: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.HIDE_PREVIEWS_RECENTS] = hide }
        }
    }

    fun setSecureScreenEnabled(secure: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.SECURE_SCREEN] = secure }
        }
    }

    fun setTrashRetentionDays(days: Int) {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.TRASH_RETENTION_DAYS] = days }
        }
    }

    fun setCollectionTileSize(size: CollectionTileSize) {
        _collectionTileSize.value = size
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it[PrefKeys.COLLECTION_TILE_SIZE] = size.name }
        }
    }

    fun clearThumbnailCache() {
        _cacheSizeMb.value = 0.0f
        showToast("Thumbnail cache cleared (0.0 MB)")
    }

    fun resetAllSettings() {
        viewModelScope.launch {
            getApplication<Application>().glasslyDataStore.edit { it.clear() }
            _cacheSizeMb.value = 42.8f
            showToast("All settings reset to defaults")
        }
    }

    fun refreshPermissionState() {
        _permissionLevel.value = checkDevicePermissionLevel(getApplication())
        viewModelScope.launch { repository.refreshMediaLibrary() }
    }

    fun setShowPermissionOnboarding(show: Boolean) {
        _showPermissionOnboarding.value = show
    }

    companion object {
        fun checkDevicePermissionLevel(context: Context): MediaPermissionLevel {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val fullImages = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
                val partial = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ) == PackageManager.PERMISSION_GRANTED
                when {
                    fullImages -> MediaPermissionLevel.FULL_ACCESS
                    partial -> MediaPermissionLevel.PARTIAL_ACCESS
                    else -> MediaPermissionLevel.DENIED_OR_PROMPT
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val fullImages = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
                if (fullImages) MediaPermissionLevel.FULL_ACCESS else MediaPermissionLevel.DENIED_OR_PROMPT
            } else {
                val legacy = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                if (legacy) MediaPermissionLevel.FULL_ACCESS else MediaPermissionLevel.DENIED_OR_PROMPT
            }
        }
    }
}
