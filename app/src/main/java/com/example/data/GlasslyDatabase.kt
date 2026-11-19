package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.flow.Flow

enum class MediaCategory {
    PHOTO,
    VIDEO,
    LIVE_PHOTO,
    SCREENSHOT,
    SELFIE,
    RAW,
    GIF,
    PORTRAIT,
    DOCUMENT
}

@Entity(tableName = "media_overrides")
data class MediaOverrideEntity(
    @PrimaryKey val mediaId: Long,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val isTrashed: Boolean = false,
    val trashedTimestamp: Long = 0L,
    val customTitle: String? = null,
    val albumNamesCsv: String = "", // Comma-separated custom album names
    // Non-destructive editor adjustments
    val exposure: Float = 0f,       // -100f..100f
    val contrast: Float = 0f,       // -100f..100f
    val saturation: Float = 0f,     // -100f..100f
    val warmth: Float = 0f,         // -100f..100f
    val sharpness: Float = 0f,      // 0f..100f
    val vignette: Float = 0f,       // 0f..100f
    val rotationDegrees: Float = 0f,// 0, 90, 180, 270
    val straightenDegrees: Float = 0f, // -45f..45f
    val flipHorizontal: Boolean = false,
    val cropRatioLabel: String = "Free",
    val filterName: String = "Original",
    val markupText: String = "",
    val markupColorHex: Long = 0xFFFFD60A
) {
    val isEdited: Boolean
        get() = exposure != 0f || contrast != 0f || saturation != 0f ||
            warmth != 0f || sharpness != 0f || vignette != 0f ||
            rotationDegrees != 0f || straightenDegrees != 0f ||
            flipHorizontal || cropRatioLabel != "Free" ||
            filterName != "Original" || markupText.isNotBlank()
}

@Entity(tableName = "custom_albums")
data class CustomAlbumEntity(
    @PrimaryKey val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val sortOrderIndex: Int = 0,
    val coverMediaId: Long? = null
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Rich MediaItem domain model combining device MediaStore metadata (or built-in colorful studio
 * photographs on fresh emulators) with Room non-destructive overrides.
 */
data class MediaItem(
    val id: Long,
    val uriString: String,
    val title: String,
    val dateTakenMillis: Long,
    val dateFormattedShort: String,  // e.g. "Jul 13, 2025"
    val dateFormattedLong: String,   // e.g. "July 13, 2025"
    val monthYearLabel: String,      // e.g. "July 2025"
    val yearLabel: String,           // e.g. "2025"
    val timeFormatted: String,       // e.g. "6:07 PM"
    val locationName: String,        // e.g. "Nanakuli Forest Reserve"
    val latitude: Double = 21.38,
    val longitude: Double = -158.14,
    val category: MediaCategory = MediaCategory.PHOTO,
    val durationFormatted: String? = null, // e.g. "0:24" for videos
    val durationSeconds: Int = 0,
    val sizeBytes: Long = 2_450_000L,
    val widthPx: Int = 4032,
    val heightPx: Int = 3024,
    val cameraModel: String = "Pixel 9 Pro",
    val lensInfo: String = "ƒ/1.7 · 1/120 s · 6.9 mm · ISO 64",
    val filePath: String = "DCIM/Camera",
    val isCapturedByMe: Boolean = true,
    val duplicateGroupKey: String? = null,
    // Procedural colorful artwork palette index for high-res vivid HDR canvas rendering when uriString is studio://
    val paletteIndex: Int = 0,
    // Merged from MediaOverrideEntity
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val isTrashed: Boolean = false,
    val trashedTimestamp: Long = 0L,
    val albums: List<String> = emptyList(),
    val overrideState: MediaOverrideEntity = MediaOverrideEntity(mediaId = id)
) {
    val isVideo: Boolean
        get() = category == MediaCategory.VIDEO

    val isLivePhoto: Boolean
        get() = category == MediaCategory.LIVE_PHOTO

    val isEdited: Boolean
        get() = overrideState.isEdited

    val sizeFormatted: String
        get() {
            val mb = sizeBytes / (1024f * 1024f)
            return String.format(java.util.Locale.US, "%.1f MB", mb)
        }

    val daysRemainingInTrash: Int
        get() {
            if (!isTrashed || trashedTimestamp == 0L) return 30
            val elapsedDays = ((System.currentTimeMillis() - trashedTimestamp) / (1000L * 60 * 60 * 24)).toInt()
            return (30 - elapsedDays).coerceIn(1, 30)
        }
}

@Dao
interface GlasslyDao {
    @Query("SELECT * FROM media_overrides")
    fun observeAllOverrides(): Flow<List<MediaOverrideEntity>>

    @Query("SELECT * FROM media_overrides WHERE mediaId = :mediaId LIMIT 1")
    suspend fun getOverride(mediaId: Long): MediaOverrideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOverride(entity: MediaOverrideEntity)

    @Query("DELETE FROM media_overrides WHERE isTrashed = 1")
    suspend fun emptyTrashOverrides()

    @Query("SELECT * FROM custom_albums ORDER BY sortOrderIndex ASC, createdAt DESC")
    fun observeAlbums(): Flow<List<CustomAlbumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlbum(album: CustomAlbumEntity)

    @Query("DELETE FROM custom_albums WHERE name = :name")
    suspend fun deleteAlbum(name: String)

    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 12")
    fun observeRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(search: RecentSearchEntity)

    @Query("DELETE FROM recent_searches")
    suspend fun clearRecentSearches()
}

@Database(
    entities = [
        MediaOverrideEntity::class,
        CustomAlbumEntity::class,
        RecentSearchEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GlasslyDatabase : RoomDatabase() {
    abstract fun dao(): GlasslyDao

    companion object {
        @Volatile
        private var INSTANCE: GlasslyDatabase? = null

        fun getInstance(context: Context): GlasslyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    GlasslyDatabase::class.java,
                    "glassly_media.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
