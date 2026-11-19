package com.example.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Loads real photos & videos from Android MediaStore with live ContentObserver updates,
 * AND includes a curated colorful HDR photography catalog so that even on a blank cloud emulator
 * with zero camera photos, the Liquid Glass refraction, collections, albums, duplicates, large files,
 * videos, and live photos are immediately vibrant and testable.
 */
class MediaRepository(
    private val context: Context,
    private val dao: GlasslyDao,
    private val externalScope: CoroutineScope
) {
    private val _rawMediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val rawMediaItems: StateFlow<List<MediaItem>> = _rawMediaItems.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val overridesFlow = dao.observeAllOverrides()
    val albumsFlow = dao.observeAlbums()
    val recentSearchesFlow = dao.observeRecentSearches()

    private var contentObserver: ContentObserver? = null

    init {
        registerLiveMediaObserver()
        externalScope.launch {
            seedDefaultAlbumsIfEmpty()
            refreshMediaLibrary()
        }
    }

    private suspend fun seedDefaultAlbumsIfEmpty() {
        val defaultAlbums = listOf(
            CustomAlbumEntity("Aegean Adventure", isPinned = true, sortOrderIndex = 0, coverMediaId = 1001L),
            CustomAlbumEntity("Top Moments", isPinned = true, sortOrderIndex = 1, coverMediaId = 1005L),
            CustomAlbumEntity("Close Friends", isPinned = false, sortOrderIndex = 2, coverMediaId = 1002L),
            CustomAlbumEntity("Coastal Days", isPinned = true, sortOrderIndex = 3, coverMediaId = 1000L),
            CustomAlbumEntity("Kyoto Autumn", isPinned = false, sortOrderIndex = 4, coverMediaId = 1007L),
            CustomAlbumEntity("Architecture & Glass", isPinned = false, sortOrderIndex = 5, coverMediaId = 1012L)
        )
        defaultAlbums.forEach { dao.upsertAlbum(it) }
    }

    private fun registerLiveMediaObserver() {
        try {
            val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    externalScope.launch { refreshMediaLibrary() }
                }
            }
            context.contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
            )
            context.contentResolver.registerContentObserver(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
            )
            contentObserver = observer
        } catch (_: SecurityException) {
            // Observer registered once permissions are granted
        } catch (_: Exception) {
        }
    }

    suspend fun refreshMediaLibrary() = withContext(Dispatchers.IO) {
        _isLoading.value = true
        val deviceItems = queryDeviceMediaStore(context.contentResolver)
        val studioItems = buildCuratedColorfulCatalog()
        // Combine real device media + curated studio media, sorted oldest -> newest so newest sits at bottom like iOS Photos
        val combined = (studioItems + deviceItems).sortedBy { it.dateTakenMillis }
        _rawMediaItems.value = combined
        _isLoading.value = false
    }

    private fun queryDeviceMediaStore(resolver: ContentResolver): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        val shortFmt = SimpleDateFormat("MMM d, yyyy", Locale.US)
        val longFmt = SimpleDateFormat("MMMM d, yyyy", Locale.US)
        val monthYearFmt = SimpleDateFormat("MMMM yyyy", Locale.US)
        val yearFmt = SimpleDateFormat("yyyy", Locale.US)
        val timeFmt = SimpleDateFormat("h:mm a", Locale.US)

        try {
            val imageProjection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageProjection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val takenCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val addedCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                val bucketCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                var count = 0
                while (cursor.moveToNext() && count < 400) {
                    count++
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: "IMG_$id.JPG"
                    val taken = cursor.getLong(takenCol).takeIf { it > 0 }
                        ?: (cursor.getLong(addedCol) * 1000L)
                    val size = cursor.getLong(sizeCol).coerceAtLeast(512_000L)
                    val w = cursor.getInt(widthCol).takeIf { it > 0 } ?: 4032
                    val h = cursor.getInt(heightCol).takeIf { it > 0 } ?: 3024
                    val mime = cursor.getString(mimeCol) ?: "image/jpeg"
                    val bucket = cursor.getString(bucketCol) ?: "Camera"
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val date = Date(taken)
                    val cat = when {
                        name.contains("screenshot", ignoreCase = true) ||
                            bucket.contains("screenshot", ignoreCase = true) -> MediaCategory.SCREENSHOT
                        mime.contains("gif", ignoreCase = true) -> MediaCategory.GIF
                        mime.contains("raw", ignoreCase = true) ||
                            name.endsWith(".dng", ignoreCase = true) -> MediaCategory.RAW
                        bucket.contains("selfie", ignoreCase = true) -> MediaCategory.SELFIE
                        else -> MediaCategory.PHOTO
                    }
                    list.add(
                        MediaItem(
                            id = id,
                            uriString = contentUri.toString(),
                            title = name,
                            dateTakenMillis = taken,
                            dateFormattedShort = shortFmt.format(date),
                            dateFormattedLong = longFmt.format(date),
                            monthYearLabel = monthYearFmt.format(date),
                            yearLabel = yearFmt.format(date),
                            timeFormatted = timeFmt.format(date),
                            locationName = bucket,
                            category = cat,
                            sizeBytes = size,
                            widthPx = w,
                            heightPx = h,
                            filePath = "DCIM/$bucket",
                            paletteIndex = (id % 12).toInt()
                        )
                    )
                }
            }

            // Also query real device Videos from MediaStore
            val videoProjection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME
            )
            resolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoProjection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val takenCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)
                val addedCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
                val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
                val durCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val bucketCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

                var count = 0
                while (cursor.moveToNext() && count < 150) {
                    count++
                    val id = cursor.getLong(idCol) + 500_000L
                    val rawId = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: "VID_$rawId.MP4"
                    val taken = cursor.getLong(takenCol).takeIf { it > 0 }
                        ?: (cursor.getLong(addedCol) * 1000L)
                    val size = cursor.getLong(sizeCol).coerceAtLeast(2_048_000L)
                    val w = cursor.getInt(widthCol).takeIf { it > 0 } ?: 1920
                    val h = cursor.getInt(heightCol).takeIf { it > 0 } ?: 1080
                    val durMs = cursor.getLong(durCol).coerceAtLeast(1000L)
                    val durSec = (durMs / 1000L).toInt().coerceAtLeast(1)
                    val mins = durSec / 60
                    val secs = durSec % 60
                    val durFormatted = String.format(Locale.US, "%d:%02d", mins, secs)
                    val bucket = cursor.getString(bucketCol) ?: "Movies"
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        rawId
                    )
                    val date = Date(taken)
                    list.add(
                        MediaItem(
                            id = id,
                            uriString = contentUri.toString(),
                            title = name,
                            dateTakenMillis = taken,
                            dateFormattedShort = shortFmt.format(date),
                            dateFormattedLong = longFmt.format(date),
                            monthYearLabel = monthYearFmt.format(date),
                            yearLabel = yearFmt.format(date),
                            timeFormatted = timeFmt.format(date),
                            locationName = bucket,
                            category = MediaCategory.VIDEO,
                            durationFormatted = durFormatted,
                            durationSeconds = durSec,
                            sizeBytes = size,
                            widthPx = w,
                            heightPx = h,
                            filePath = "Movies/$bucket",
                            paletteIndex = (rawId % 12).toInt()
                        )
                    )
                }
            }
        } catch (_: Exception) {
            // Permission not granted yet or empty MediaStore
        }
        return list
    }

    /**
     * Generates 68 rich, realistic photo & video items across 2024–2026 matching the reference
     * iOS 26/27 Photos app (`Glassly___Liquid_Glass_Photos_7ed3e40d.html` and `/references`)
     * so every time segment (Years, Months, Days, All), smart album (Videos, Live Photos,
     * Screenshots, Selfies, Portraits, RAW, GIF, Documents, Duplicates, Large Files), and search
     * phrase ("videos last month", "Santorini", "2025", "Favorites") works out of the box.
     */
    internal fun buildCuratedColorfulCatalog(): List<MediaItem> {
        val places = listOf(
            "Big Sur, California",
            "Nanakuli Forest Reserve",
            "Santorini, Greece",
            "Lisbon, Portugal",
            "Lake Tahoe, Nevada",
            "Kyoto, Japan"
        )
        val albumCycle = listOf(
            listOf("Coastal Days", "Aegean Adventure"),
            listOf("Top Moments"),
            listOf("Close Friends", "Aegean Adventure"),
            listOf("Coastal Days"),
            listOf("Top Moments", "Kyoto Autumn"),
            listOf("Architecture & Glass")
        )
        val shortFmt = SimpleDateFormat("MMM d, yyyy", Locale.US)
        val longFmt = SimpleDateFormat("MMMM d, yyyy", Locale.US)
        val monthYearFmt = SimpleDateFormat("MMMM yyyy", Locale.US)
        val yearFmt = SimpleDateFormat("yyyy", Locale.US)
        val timeFmt = SimpleDateFormat("h:mm a", Locale.US)

        val now = System.currentTimeMillis()
        val dayMs = 24L * 60 * 60 * 1000L

        return (0 until 66).map { idx ->
            val id = 1000L + idx
            // Spread items across 2024, 2025, and recent days/last month
            val daysAgo = when {
                idx >= 58 -> (65 - idx).toLong() // Today & this week
                idx >= 44 -> (12 + (58 - idx) * 2).toLong() // Last month
                idx >= 20 -> (70 + (44 - idx) * 8).toLong() // Earlier this year
                else -> (380 + (20 - idx) * 18).toLong() // Previous years
            }
            val timestamp = now - daysAgo * dayMs - ((idx * 47) % 600) * 60_000L
            val date = Date(timestamp)

            val category = when {
                idx in listOf(4, 15, 33, 47, 55, 60, 63) -> MediaCategory.VIDEO
                idx in listOf(2, 11, 27, 49, 61) -> MediaCategory.LIVE_PHOTO
                idx in listOf(7, 23, 51) -> MediaCategory.SCREENSHOT
                idx in listOf(5, 19, 38, 57) -> MediaCategory.SELFIE
                idx in listOf(9, 31, 53) -> MediaCategory.PORTRAIT
                idx in listOf(14, 42) -> MediaCategory.RAW
                idx in listOf(21, 46) -> MediaCategory.GIF
                idx in listOf(17, 29, 40) -> MediaCategory.DOCUMENT
                else -> MediaCategory.PHOTO
            }

            val isVideo = category == MediaCategory.VIDEO
            val durationSec = if (isVideo) 12 + (idx % 45) else 0
            val durationStr = if (isVideo) String.format(Locale.US, "0:%02d", durationSec) else null

            val isLargeFile = isVideo || category == MediaCategory.RAW || idx in listOf(18, 36, 52, 64)
            val sizeBytes = when {
                isVideo -> (28_400_000L + idx * 950_000L)
                category == MediaCategory.RAW -> 42_600_000L
                isLargeFile -> 19_800_000L
                else -> (2_300_000L + (idx % 9) * 420_000L)
            }

            // Create 3 intentional duplicate pairs for Duplicate Finder utility
            val duplicateGroup = when (idx) {
                10, 12 -> "dup_group_santorini_sunset"
                28, 30 -> "dup_group_nanakuli_ridge"
                50, 54 -> "dup_group_lisbon_tram"
                else -> null
            }

            val ext = when (category) {
                MediaCategory.VIDEO -> "MP4"
                MediaCategory.RAW -> "DNG"
                MediaCategory.GIF -> "GIF"
                MediaCategory.SCREENSHOT -> "PNG"
                else -> "HEIC"
            }

            MediaItem(
                id = id,
                uriString = "studio://photo/$idx",
                title = "IMG_${1000 + idx * 7}.$ext",
                dateTakenMillis = timestamp,
                dateFormattedShort = shortFmt.format(date),
                dateFormattedLong = longFmt.format(date),
                monthYearLabel = monthYearFmt.format(date),
                yearLabel = yearFmt.format(date),
                timeFormatted = timeFmt.format(date),
                locationName = places[idx % places.size],
                category = category,
                durationFormatted = durationStr,
                durationSeconds = durationSec,
                sizeBytes = sizeBytes,
                widthPx = if (isVideo) 3840 else 4032,
                heightPx = if (isVideo) 2160 else 3024,
                cameraModel = if (idx % 4 == 1) "Saved from Messages" else "Pixel 9 Pro",
                lensInfo = "ƒ/1.7 · 1/120 s · 6.9 mm · ISO 64",
                filePath = if (category == MediaCategory.SCREENSHOT) "Pictures/Screenshots" else "DCIM/Camera",
                isCapturedByMe = idx % 4 != 1,
                duplicateGroupKey = duplicateGroup,
                paletteIndex = idx,
                isFavorite = (idx % 9 == 2),
                albums = albumCycle[idx % albumCycle.size]
            )
        }
    }

    suspend fun toggleFavorite(item: MediaItem) {
        val current = dao.getOverride(item.id) ?: item.overrideState
        dao.upsertOverride(current.copy(isFavorite = !item.isFavorite))
    }

    suspend fun setFavorites(ids: Set<Long>, favorite: Boolean) {
        ids.forEach { id ->
            val current = dao.getOverride(id) ?: MediaOverrideEntity(mediaId = id)
            dao.upsertOverride(current.copy(isFavorite = favorite))
        }
    }

    suspend fun moveToTrash(ids: Set<Long>) {
        val now = System.currentTimeMillis()
        ids.forEach { id ->
            val current = dao.getOverride(id) ?: MediaOverrideEntity(mediaId = id)
            dao.upsertOverride(current.copy(isTrashed = true, trashedTimestamp = now))
        }
    }

    suspend fun restoreFromTrash(ids: Set<Long>) {
        ids.forEach { id ->
            val current = dao.getOverride(id) ?: MediaOverrideEntity(mediaId = id)
            dao.upsertOverride(current.copy(isTrashed = false, trashedTimestamp = 0L))
        }
    }

    suspend fun deleteForever(ids: Set<Long>) {
        val currentList = _rawMediaItems.value.filterNot { it.id in ids }
        _rawMediaItems.value = currentList
    }

    suspend fun emptyTrash(trashedIds: Set<Long>) {
        deleteForever(trashedIds)
        dao.emptyTrashOverrides()
    }

    suspend fun setHidden(ids: Set<Long>, hidden: Boolean) {
        ids.forEach { id ->
            val current = dao.getOverride(id) ?: MediaOverrideEntity(mediaId = id)
            dao.upsertOverride(current.copy(isHidden = hidden))
        }
    }

    suspend fun renameMedia(mediaId: Long, newTitle: String) {
        val current = dao.getOverride(mediaId) ?: MediaOverrideEntity(mediaId = mediaId)
        dao.upsertOverride(current.copy(customTitle = newTitle.trim()))
    }

    suspend fun addMediaToAlbum(ids: Set<Long>, albumName: String) {
        ids.forEach { id ->
            val current = dao.getOverride(id) ?: MediaOverrideEntity(mediaId = id)
            val existing = current.albumNamesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
            existing.add(albumName)
            dao.upsertOverride(current.copy(albumNamesCsv = existing.joinToString(",")))
        }
    }

    suspend fun savePhotoEdit(
        item: MediaItem,
        updatedOverride: MediaOverrideEntity,
        saveAsCopy: Boolean
    ) {
        if (saveAsCopy) {
            val newId = (_rawMediaItems.value.maxOfOrNull { it.id } ?: 2000L) + 1L
            val copyItem = item.copy(
                id = newId,
                title = "COPY_${item.title}",
                dateTakenMillis = System.currentTimeMillis()
            )
            _rawMediaItems.value = _rawMediaItems.value + copyItem
            dao.upsertOverride(updatedOverride.copy(mediaId = newId))
        } else {
            dao.upsertOverride(updatedOverride.copy(mediaId = item.id))
        }
    }

    suspend fun revertPhotoEdit(mediaId: Long) {
        val current = dao.getOverride(mediaId) ?: return
        dao.upsertOverride(
            current.copy(
                exposure = 0f,
                contrast = 0f,
                saturation = 0f,
                warmth = 0f,
                sharpness = 0f,
                vignette = 0f,
                rotationDegrees = 0f,
                straightenDegrees = 0f,
                flipHorizontal = false,
                cropRatioLabel = "Free",
                filterName = "Original",
                markupText = ""
            )
        )
    }

    suspend fun createAlbum(name: String) {
        if (name.isBlank()) return
        dao.upsertAlbum(CustomAlbumEntity(name = name.trim(), isPinned = false, sortOrderIndex = 10))
    }

    suspend fun renameAlbum(oldName: String, newName: String) {
        if (newName.isBlank() || oldName == newName) return
        dao.deleteAlbum(oldName)
        dao.upsertAlbum(CustomAlbumEntity(name = newName.trim(), isPinned = true))
    }

    suspend fun deleteAlbum(name: String) {
        dao.deleteAlbum(name)
    }

    suspend fun togglePinAlbum(album: CustomAlbumEntity) {
        dao.upsertAlbum(album.copy(isPinned = !album.isPinned))
    }

    suspend fun recordRecentSearch(query: String) {
        if (query.isBlank()) return
        dao.insertRecentSearch(RecentSearchEntity(query = query.trim()))
    }

    suspend fun duplicateItems(ids: Set<Long>) {
        val current = _rawMediaItems.value
        var nextId = (current.maxOfOrNull { it.id } ?: 2000L) + 1L
        val clones = current.filter { it.id in ids }.map { item ->
            val newId = nextId++
            item.copy(
                id = newId,
                title = "DUP_${item.title}",
                dateTakenMillis = System.currentTimeMillis()
            )
        }
        if (clones.isNotEmpty()) {
            _rawMediaItems.value = (current + clones).sortedBy { it.dateTakenMillis }
        }
    }

    suspend fun adjustDateTime(ids: Set<Long>, dateLabel: String) {
        val current = _rawMediaItems.value.map { item ->
            if (item.id in ids) {
                item.copy(
                    dateFormattedShort = dateLabel,
                    dateFormattedLong = dateLabel
                )
            } else item
        }
        _rawMediaItems.value = current
    }

    suspend fun adjustLocation(ids: Set<Long>, newLocation: String) {
        val current = _rawMediaItems.value.map { item ->
            if (item.id in ids) {
                item.copy(locationName = newLocation)
            } else item
        }
        _rawMediaItems.value = current
    }

    suspend fun clearRecentSearches() {
        dao.clearRecentSearches()
    }
}
