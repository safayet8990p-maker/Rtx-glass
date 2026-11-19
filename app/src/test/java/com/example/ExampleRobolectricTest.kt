package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GlasslyDatabase
import com.example.data.MediaCategory
import com.example.data.MediaRepository
import com.example.glass.AppLanguage
import com.example.glass.AppThemeMode
import com.example.glass.GlassConfig
import com.example.glass.GlassVariant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun verifyAppIdentityAndSingleSourceOfTruthVersion() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Glassly", appName)
        assertEquals("0.1.02", BuildConfig.VERSION_NAME)
        assertEquals(112, BuildConfig.VERSION_CODE)
        assertEquals("com.glassly.app", BuildConfig.APPLICATION_ID)
    }

    @Test
    fun verifyGlassConfigIntensityScalingAndDefaults() {
        val defaultConfig = GlassConfig()
        assertEquals(GlassVariant.REGULAR, defaultConfig.variant)
        assertEquals(AppThemeMode.DARK, defaultConfig.themeMode)
        assertEquals(AppLanguage.ENGLISH, defaultConfig.language)
        assertFalse(defaultConfig.reduceGlass)

        val intensified = defaultConfig.copy(intensity = 0.5f)
        assertEquals(defaultConfig.refractionStrength * 0.5f, intensified.effectiveRefraction, 0.0001f)
        assertEquals(defaultConfig.chromaticDispersion * 0.5f, intensified.effectiveDispersion, 0.0001f)
    }

    @Test
    fun verifyStudioMediaCatalogContainsAllRequiredCategories() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = GlasslyDatabase.getInstance(context)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val repo = MediaRepository(context, db.dao(), scope)
        val catalog = repo.buildCuratedColorfulCatalog()

        assertTrue("Catalog should contain at least 60 rich items", catalog.size >= 60)
        assertTrue("Catalog must contain videos", catalog.any { it.isVideo })
        assertTrue("Catalog must contain live photos", catalog.any { it.isLivePhoto })
        assertTrue("Catalog must contain screenshots", catalog.any { it.category == MediaCategory.SCREENSHOT })
        assertTrue("Catalog must contain RAW photos", catalog.any { it.category == MediaCategory.RAW })
        assertTrue("Catalog must contain duplicate groups", catalog.any { it.duplicateGroupKey != null })
        assertTrue("Catalog must contain large files (>=15MB)", catalog.any { it.sizeBytes >= 15_000_000L })
        assertNotNull(catalog.first().cameraModel)
    }
}
