package com.example.glass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.ui.theme.AccentTintOption
import com.example.ui.theme.GlassBlue

enum class GlassVariant(val labelEn: String, val labelBn: String) {
    REGULAR("Regular", "রেগুলার"),
    CLEAR("Clear", "ক্লিয়ার"),
    TINTED("Tinted", "টিন্টেড")
}

enum class AppThemeMode(val labelEn: String, val labelBn: String) {
    SYSTEM("System", "সিস্টেম"),
    LIGHT("Light", "লাইট"),
    DARK("Dark", "ডার্ক")
}

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    BANGLA("bn", "বাংলা")
}

/**
 * Central GlassConfig holding every tunable parameter for Liquid Glass material and motion:
 * refraction strength, rim width, chromatic dispersion, blur radius, tint, spring stiffness,
 * press scale, noise grain, and accessibility overrides.
 */
@Immutable
data class GlassConfig(
    val variant: GlassVariant = GlassVariant.REGULAR,
    val intensity: Float = 0.85f,            // 0f..1f master slider in Settings
    val refractionStrength: Float = 24f,     // px edge bending strength
    val rimWidth: Float = 22f,               // px bevel lens thickness
    val chromaticDispersion: Float = 3.2f,   // RGB channel split on rim
    val blurRadiusDp: Float = 22f,           // Background blur under glass
    val specularIntensity: Float = 0.72f,    // Top-left bevel specular highlight
    val noiseGrainAlpha: Float = 0.035f,     // Fine procedural noise grain
    val innerGlowAlpha: Float = 0.24f,       // Soft inner rim glow
    val shadowElevationDp: Float = 14f,      // Layered soft drop shadow
    val accentTint: Color = GlassBlue,
    val accentOption: AccentTintOption = AccentTintOption.OCEAN_BLUE,
    val isDarkTheme: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val reduceGlass: Boolean = false,        // Fallback to high-contrast tonal surface
    val highContrastGlass: Boolean = false,  // Extra border & opacity for WCAG AA+
    val reduceMotion: Boolean = false,       // Respect system or app reduce motion
    val hapticsEnabled: Boolean = true,
    val hapticStrength: Float = 0.8f,        // 0.3f..1.0f
    val largerTouchTargets: Boolean = false, // 48dp -> 56dp minimum interactive size
    val springStiffness: Float = 420f,       // Spring stiffness for press & morph
    val springDampingRatio: Float = 0.68f,   // Bouncy liquid droplet feel
    val pressScaleBounce: Float = 1.06f,     // 1.0 -> 1.06 scale bounce on press
    val expansionDurationMs: Int = 340,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val isBatterySaverActive: Boolean = false,
    // Sampled backdrop luminance (0 = dark background behind glass, 1 = bright background)
    val backdropLuminance: Float = 0.25f
) {
    val effectiveReduceGlass: Boolean
        get() = reduceGlass || isBatterySaverActive

    val effectiveRefraction: Float
        get() = if (effectiveReduceGlass) 0f else refractionStrength * intensity

    val effectiveDispersion: Float
        get() = if (effectiveReduceGlass) 0f else chromaticDispersion * intensity

    val effectiveBlurDp: Float
        get() = if (effectiveReduceGlass) 0f else blurRadiusDp * (0.45f + 0.55f * intensity)

    val supportsRuntimeShader: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !effectiveReduceGlass

    val supportsRenderEffectBlur: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !effectiveReduceGlass
}

val LocalGlassConfig = staticCompositionLocalOf { GlassConfig() }

/**
 * Smoothed device tilt vector from accelerometer/gravity sensor for interactive specular bevel highlight.
 */
@Composable
fun rememberDeviceTiltOffset(enabled: Boolean = true): State<Offset> {
    val context = LocalContext.current
    val tiltState = remember { mutableStateOf(Offset(-0.6f, -0.7f)) }

    DisposableEffect(context, enabled) {
        if (!enabled) {
            onDispose { }
        } else {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)
                ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

            val listener = object : SensorEventListener {
                private var smoothX = -0.6f
                private var smoothY = -0.7f
                override fun onSensorChanged(event: SensorEvent?) {
                    event ?: return
                    val rawX = (-event.values[0] / 9.81f).coerceIn(-1f, 1f)
                    val rawY = (-event.values[1] / 9.81f).coerceIn(-1f, 1f)
                    // Low-pass filter for silky smooth light reaction
                    smoothX = smoothX * 0.85f + rawX * 0.15f
                    smoothY = smoothY * 0.85f + rawY * 0.15f
                    tiltState.value = Offset(smoothX, smoothY)
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            if (sensor != null) {
                sensorManager?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
            }
            onDispose {
                sensorManager?.unregisterListener(listener)
            }
        }
    }
    return tiltState
}

/**
 * Checks system "Remove animations" setting and Power Save mode.
 */
fun Context.isSystemReduceMotionEnabled(): Boolean {
    return try {
        val scale = Settings.Global.getFloat(
            contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        scale == 0f
    } catch (_: Exception) {
        false
    }
}

fun Context.isSystemBatterySaverActive(): Boolean {
    return try {
        val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
        pm?.isPowerSaveMode == true
    } catch (_: Exception) {
        false
    }
}
