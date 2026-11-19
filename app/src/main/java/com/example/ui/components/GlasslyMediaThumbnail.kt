package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.MediaItem
import com.example.data.MediaOverrideEntity
import kotlin.math.cos
import kotlin.math.sin

// High-saturation coastal, portrait, sunset, and architectural palettes matching the reference prototype
private val StudioPalettes = listOf(
    listOf(Color(0xFF3D7FD6), Color(0xFFCFE3F7), Color(0xFFC4A574), Color(0xFF8C7650)),
    listOf(Color(0xFFF6A04D), Color(0xFFF8DCAE), Color(0xFF44633F), Color(0xFF26402D)),
    listOf(Color(0xFF6FA9DC), Color(0xFFEEF3F8), Color(0xFFE8C24A), Color(0xFF2F6F8F)),
    listOf(Color(0xFF2A1A24), Color(0xFF7A2A30), Color(0xFF8D1F1F), Color(0xFF3A0D10)),
    listOf(Color(0xFFA8D0EA), Color(0xFFF2F6F9), Color(0xFF6D9250), Color(0xFF3F6140)),
    listOf(Color(0xFFF2B8A0), Color(0xFFFBE6D6), Color(0xFFCF8C70), Color(0xFF8D5A4A)),
    listOf(Color(0xFF7AB5C9), Color(0xFFD9EEF2), Color(0xFFE7DCC4), Color(0xFFA89A7C)),
    listOf(Color(0xFFE7C27A), Color(0xFFF7EAD0), Color(0xFFA1703D), Color(0xFF5A3A22))
)

private val PortraitSkinTones = listOf(
    Color(0xFFF1C9A5),
    Color(0xFFD9A47F),
    Color(0xFFA9714F),
    Color(0xFF7A4A30)
)

private val PortraitClothingTones = listOf(
    Color(0xFFE8662A),
    Color(0xFFF2D34A),
    Color(0xFF2F7A46),
    Color(0xFF3A5FB0),
    Color(0xFFE4DFD8),
    Color(0xFFB8324A),
    Color(0xFF2B2B30)
)

private val PortraitHairTones = listOf(
    Color(0xFF1C1410),
    Color(0xFF3A2A1E),
    Color(0xFFC9C9C9),
    Color(0xFF6B3F22)
)

/**
 * Renders either a real device MediaStore URI via Coil [AsyncImage] OR a vivid high-detail
 * landscape/portrait vector artwork (matching the uploaded reference `Glassly___Liquid_Glass_Photos.html`),
 * with live non-destructive edits (exposure, contrast, saturation, warmth, vignette, rotation,
 * straighten dial, horizontal flip, filter preset, and markup overlay).
 */
@Composable
fun GlasslyMediaThumbnail(
    item: MediaItem,
    modifier: Modifier = Modifier,
    overridePreview: MediaOverrideEntity = item.overrideState,
    ignoreEdits: Boolean = false
) {
    val activeOverride = if (ignoreEdits) MediaOverrideEntity(item.id) else overridePreview

    val colorMatrix = remember(
        activeOverride.exposure,
        activeOverride.contrast,
        activeOverride.saturation,
        activeOverride.warmth,
        activeOverride.filterName
    ) {
        buildPhotoEditColorMatrix(activeOverride)
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationZ = activeOverride.rotationDegrees + activeOverride.straightenDegrees
                scaleX = if (activeOverride.flipHorizontal) -1f else 1f
            }
    ) {
        if (!item.uriString.startsWith("studio://")) {
            AsyncImage(
                model = item.uriString,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(colorMatrix),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            StudioPhotoCanvas(
                paletteIndex = item.paletteIndex,
                overrideState = activeOverride,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Vignette & Warmth Tint Overlay
        if (activeOverride.vignette > 0.5f || activeOverride.warmth != 0f || activeOverride.filterName != "Original") {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (activeOverride.warmth != 0f) {
                    val warmColor = if (activeOverride.warmth > 0f) {
                        Color(0xFFFF9F0A).copy(alpha = (activeOverride.warmth / 100f) * 0.22f)
                    } else {
                        Color(0xFF0A84FF).copy(alpha = (-activeOverride.warmth / 100f) * 0.22f)
                    }
                    drawRect(color = warmColor)
                }

                when (activeOverride.filterName) {
                    "Vivid" -> drawRect(Color(0x18FF9500))
                    "Dramatic" -> drawRect(Color(0x280A1128))
                    "Mono" -> drawRect(Color(0x661C1C1E))
                    "Silvertone" -> drawRect(Color(0x448E8E93))
                    "Noir" -> drawRect(Color(0x88000000))
                }

                if (activeOverride.vignette > 0.5f) {
                    val vAlpha = (activeOverride.vignette / 100f).coerceIn(0f, 0.85f)
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = vAlpha)
                            ),
                            center = center,
                            radius = maxOf(size.width, size.height) * 0.65f
                        )
                    )
                }
            }
        }

        // Markup text overlay if present
        if (activeOverride.markupText.isNotBlank()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeOverride.markupText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(activeOverride.markupColorHex),
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Composable
private fun StudioPhotoCanvas(
    paletteIndex: Int,
    overrideState: MediaOverrideEntity,
    modifier: Modifier = Modifier
) {
    val palette = StudioPalettes[paletteIndex % StudioPalettes.size]
    val r1 = ((paletteIndex * 37 + 91) % 100) / 100f
    val r2 = ((paletteIndex * 37 + 182) % 100) / 100f
    val r3 = ((paletteIndex * 37 + 273) % 100) / 100f
    val r4 = ((paletteIndex * 37 + 364) % 100) / 100f
    val r5 = ((paletteIndex * 37 + 455) % 100) / 100f
    val r6 = ((paletteIndex * 37 + 546) % 100) / 100f

    val exposureFactor = (1f + overrideState.exposure / 140f).coerceIn(0.35f, 1.85f)
    val contrastFactor = (1f + overrideState.contrast / 160f).coerceIn(0.5f, 1.7f)

    fun adjustColor(c: Color): Color {
        val r = (((c.red - 0.5f) * contrastFactor + 0.5f) * exposureFactor).coerceIn(0f, 1f)
        val g = (((c.green - 0.5f) * contrastFactor + 0.5f) * exposureFactor).coerceIn(0f, 1f)
        val b = (((c.blue - 0.5f) * contrastFactor + 0.5f) * exposureFactor).coerceIn(0f, 1f)
        if (overrideState.filterName == "Mono" || overrideState.filterName == "Noir") {
            val lum = (0.299f * r + 0.587f * g + 0.114f * b).coerceIn(0f, 1f)
            return Color(lum, lum, lum, c.alpha)
        }
        return Color(r, g, b, c.alpha)
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Sky gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(adjustColor(palette[0]), adjustColor(palette[1])),
                startY = 0f,
                endY = h * 0.72f
            )
        )

        // Sun / Moon orb
        drawCircle(
            color = Color.White.copy(alpha = 0.58f),
            radius = minOf(w, h) * 0.095f,
            center = Offset(w * (0.2f + r2 * 0.6f), h * (0.18f + r3 * 0.14f))
        )

        // Distant mountain ridge
        val hill1 = Path().apply {
            moveTo(0f, h * 0.64f)
            quadraticTo(
                w * 0.25f,
                h * (0.48f + r4 * 0.12f),
                w * 0.52f,
                h * 0.62f
            )
            quadraticTo(
                w * 0.78f,
                h * (0.50f + r5 * 0.10f),
                w,
                h * (0.54f + r5 * 0.10f)
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(path = hill1, color = adjustColor(palette[2]))

        // Foreground coastal slope
        val hill2 = Path().apply {
            moveTo(0f, h * 0.78f)
            quadraticTo(
                w * 0.42f,
                h * (0.65f + r6 * 0.08f),
                w,
                h * 0.76f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(path = hill2, color = adjustColor(palette[3]))

        // Portrait subject silhouette (matching reference prototype SVG portraits)
        if (paletteIndex % 5 != 3) {
            val subjectX = w * (0.28f + r1 * 0.44f)
            val hairColor = adjustColor(PortraitHairTones[paletteIndex % PortraitHairTones.size])
            val skinColor = adjustColor(PortraitSkinTones[paletteIndex % PortraitSkinTones.size])
            val clothColor = adjustColor(PortraitClothingTones[(paletteIndex * 3) % PortraitClothingTones.size])

            drawCircle(
                color = hairColor,
                radius = minOf(w, h) * 0.108f,
                center = Offset(subjectX, h * 0.43f)
            )
            drawCircle(
                color = skinColor,
                radius = minOf(w, h) * 0.086f,
                center = Offset(subjectX, h * 0.45f)
            )

            val shoulders = Path().apply {
                moveTo(subjectX - w * 0.24f, h)
                quadraticTo(
                    subjectX - w * 0.22f,
                    h * 0.62f,
                    subjectX,
                    h * 0.60f
                )
                quadraticTo(
                    subjectX + w * 0.22f,
                    h * 0.62f,
                    subjectX + w * 0.24f,
                    h
                )
                close()
            }
            drawPath(path = shoulders, color = clothColor)
        }
    }
}

private fun buildPhotoEditColorMatrix(override: MediaOverrideEntity): ColorMatrix {
    val sat = when (override.filterName) {
        "Mono", "Noir" -> 0f
        "Silvertone" -> 0.25f
        "Vivid" -> (1.25f + override.saturation / 100f).coerceAtLeast(0f)
        else -> (1f + override.saturation / 100f).coerceAtLeast(0f)
    }
    val matrix = ColorMatrix()
    matrix.setToSaturation(sat)
    return matrix
}
