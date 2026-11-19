package com.example.glass

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.intellij.lang.annotations.Language
import kotlin.math.cos
import kotlin.math.sin

/**
 * Source-node / effect-node backdrop capture state using Jetpack Compose GraphicsLayer.
 * Prevents infinite draw recursion by recording ONLY the background media/content layer
 * into `backdropLayer` and drawing it inside floating glass controls with AGSL lens shader.
 */
@Stable
class GlassBackdropState(
    val graphicsLayer: GraphicsLayer
) {
    var rootBounds by mutableStateOf(Rect.Zero)
    var averageLuminance by mutableStateOf(0.22f)
}

val LocalGlassBackdrop = staticCompositionLocalOf<GlassBackdropState?> { null }

/**
 * Wraps the background content (photo grid, full-screen photo, gradient canvas) and records
 * its drawing commands into a shared [GraphicsLayer] without drawing floating glass controls into itself.
 */
@Composable
fun GlassBackdropProvider(
    backdropLuminanceHint: Float = 0.22f,
    backgroundContent: @Composable BoxScope.() -> Unit,
    floatingOverlayContent: @Composable BoxScope.() -> Unit
) {
    val graphicsLayer = rememberGraphicsLayer()
    val state = remember(graphicsLayer) {
        GlassBackdropState(graphicsLayer).apply {
            averageLuminance = backdropLuminanceHint
        }
    }
    state.averageLuminance = backdropLuminanceHint

    Box {
        // Source node: records content into graphicsLayer AND renders to screen.
        // CRITICAL: Provide null for LocalGlassBackdrop inside backgroundContent so any
        // GlassCard / GlassBadge / GlassButton placed inside backgroundContent never calls
        // drawLayer(graphicsLayer) while graphicsLayer is currently recording (which would
        // cause cyclic Skia DisplayList recursion and a native RenderThread SIGSEGV crash).
        CompositionLocalProvider(LocalGlassBackdrop provides null) {
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coords ->
                        state.rootBounds = coords.boundsInRoot()
                    }
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    },
                content = backgroundContent
            )
        }
        // Effect nodes live here above the source node and safely read the recorded backdrop
        CompositionLocalProvider(LocalGlassBackdrop provides state) {
            floatingOverlayContent()
        }
    }
}

/**
 * Custom AGSL RuntimeShader for iOS 27 Liquid Glass on API 33+:
 * 1. Rounded-rectangle Signed Distance Field (SDF) lens profile near the rim.
 * 2. Edge refraction bending background pixels along the normal of the rounded rim.
 * 3. Subtle chromatic dispersion (R/G/B wavelength separation along the rim bevel).
 * 4. Specular highlight reacting to touch offset and smoothed device tilt.
 * 5. Fine procedural film grain & soft inner glow.
 */
@Language("AGSL")
private const val LIQUID_GLASS_AGSL_SHADER = """
    uniform shader content;
    uniform float2 resolution;
    uniform float cornerRadius;
    uniform float rimWidth;
    uniform float refractionStrength;
    uniform float dispersion;
    uniform float2 lightDir;
    uniform float specularStrength;
    uniform float noiseAlpha;
    uniform float4 tintColor;
    uniform float thicknessMultiplier;

    float sdRoundRect(float2 p, float2 b, float r) {
        float2 q = abs(p) - b + r;
        return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
    }

    float hash(float2 p) {
        return fract(sin(dot(p, float2(127.1, 311.7))) * 43758.5453123);
    }

    half4 main(float2 coord) {
        float2 halfSize = resolution * 0.5;
        float2 centerCoord = coord - halfSize;
        float r = min(cornerRadius, min(halfSize.x, halfSize.y));
        float dist = sdRoundRect(centerCoord, halfSize, r);

        // Inside the rounded shape dist <= 0. Near rim: dist is between -rimWidth and 0
        float rimFactor = clamp(1.0 + dist / max(rimWidth, 1.0), 0.0, 1.0);
        // Smooth cubic lens curve at the rim
        float lensCurve = rimFactor * rimFactor * (3.0 - 2.0 * rimFactor);

        // Compute outward surface normal from SDF gradient approximation
        float2 eps = float2(1.5, 0.0);
        float2 grad = float2(
            sdRoundRect(centerCoord + eps.xy, halfSize, r) - sdRoundRect(centerCoord - eps.xy, halfSize, r),
            sdRoundRect(centerCoord + eps.yx, halfSize, r) - sdRoundRect(centerCoord - eps.yx, halfSize, r)
        );
        float2 normal = length(grad) > 0.0001 ? normalize(grad) : float2(0.0, -1.0);

        // Refract background outward/inward along the curved glass bevel
        float2 refractOffset = -normal * lensCurve * refractionStrength * thicknessMultiplier;
        float2 dispOffset = normal * lensCurve * dispersion * thicknessMultiplier;

        float2 uvR = clamp(coord + refractOffset + dispOffset, float2(0.0), resolution);
        float2 uvG = clamp(coord + refractOffset, float2(0.0), resolution);
        float2 uvB = clamp(coord + refractOffset - dispOffset, float2(0.0), resolution);

        half4 colR = content.eval(uvR);
        half4 colG = content.eval(uvG);
        half4 colB = content.eval(uvB);
        half3 refracted = half3(colR.r, colG.g, colB.b);

        // Adaptive glass tint blend
        half3 tinted = mix(refracted, tintColor.rgb, half(tintColor.a));

        // Directional specular highlight on the bevel + opposite secondary rim reflection
        float2 normLight = length(lightDir) > 0.001 ? normalize(lightDir) : float2(-0.6, -0.7);
        float primarySpec = pow(max(dot(normal, normLight), 0.0), 3.0) * lensCurve;
        float secondarySpec = pow(max(dot(normal, -normLight), 0.0), 4.0) * lensCurve * 0.42;
        float rimGlow = (primarySpec + secondarySpec) * specularStrength;

        // Subtle procedural noise grain so glass feels physical, not flat plastic
        float grain = (hash(coord) - 0.5) * noiseAlpha;

        half3 finalRgb = clamp(tinted + half3(rimGlow + grain), half3(0.0), half3(1.0));
        return half4(finalRgb, 1.0);
    }
"""

/**
 * Core Liquid Glass Surface modifier & composable that renders:
 * - Layered soft drop shadow (scales with thicknessMultiplier when expanding to menu/alert)
 * - Backdrop capture clipped to the glass shape, blurred + refracted via AGSL on API 33+
 *   or RenderEffect blur on API 31-32, or adaptive tonal fill when Reduce Glass is on
 * - Adaptive tint that auto-adjusts based on backdrop luminance and glass variant
 * - Top-left bevel specular rim + interactive shimmer sweep on press
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 999.dp,
    variant: GlassVariant = LocalGlassConfig.current.variant,
    customTint: Color? = null,
    thicknessMultiplier: Float = 1.0f, // > 1.0f when control expands into menu/sheet
    pressProgress: Float = 0f,         // 0f..1f for press shimmer sweep
    touchOffset: Offset = Offset.Zero,
    content: @Composable BoxScope.() -> Unit
) {
    val config = LocalGlassConfig.current
    val backdrop = LocalGlassBackdrop.current
    val density = LocalDensity.current
    val tiltOffset by rememberDeviceTiltOffset(enabled = !config.reduceMotion && !config.effectiveReduceGlass)

    val shape = RoundedCornerShape(cornerRadius)
    val cornerPx = with(density) { cornerRadius.toPx() }
    val rimWidthPx = with(density) { config.rimWidth.dp.toPx() }

    // Adaptive tint computation based on backdrop luminance, theme, and variant
    val isBackdropBright = (backdrop?.averageLuminance ?: config.backdropLuminance) > 0.62f
    val useDarkGlass = config.isDarkTheme || (variant == GlassVariant.CLEAR && !isBackdropBright)

    val baseTint = when {
        config.effectiveReduceGlass -> {
            if (config.isDarkTheme) Color(0xFF242428) else Color(0xFFEEEEF4)
        }
        customTint != null -> customTint
        variant == GlassVariant.TINTED -> {
            val alpha = if (config.highContrastGlass) 0.78f else 0.58f
            config.accentTint.copy(alpha = alpha)
        }
        variant == GlassVariant.CLEAR -> {
            // Clear glass includes a subtle legibility scrim so text stays WCAG AA compliant
            if (useDarkGlass) Color(0x6B1E1E22) else Color(0x78FFFFFF)
        }
        else -> { // REGULAR
            val baseAlpha = if (config.highContrastGlass) 0.78f else 0.56f
            if (useDarkGlass) Color(0xFF3A3A3E).copy(alpha = baseAlpha)
            else Color(0xFFFFFFFF).copy(alpha = (baseAlpha + 0.08f).coerceAtMost(0.88f))
        }
    }

    val shadowElevation = if (config.effectiveReduceGlass) {
        2.dp
    } else {
        (config.shadowElevationDp * thicknessMultiplier).dp
    }

    var localBoundsInRoot by remember { mutableStateOf(Rect.Zero) }

    Box(
        modifier = modifier
            .onGloballyPositioned { coords ->
                localBoundsInRoot = coords.boundsInRoot()
            }
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.25f * thicknessMultiplier),
                spotColor = Color.Black.copy(alpha = 0.30f * thicknessMultiplier)
            )
            .clip(shape)
    ) {
        // 1. Captured & Refracted Backdrop Layer (if available and Reduce Glass is off)
        if (backdrop != null && !config.effectiveReduceGlass && localBoundsInRoot.width > 0f) {
            val blurPx = with(density) { config.effectiveBlurDp.dp.toPx() }.coerceAtLeast(1f)
            val lightVector = remember(tiltOffset, touchOffset, pressProgress) {
                if (touchOffset != Offset.Zero) {
                    Offset(
                        (touchOffset.x / 150f).coerceIn(-1f, 1f),
                        (touchOffset.y / 150f).coerceIn(-1f, 1f)
                    )
                } else {
                    tiltOffset
                }
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .liquidGlassEffectModifier(
                        config = config,
                        cornerPx = cornerPx,
                        rimWidthPx = rimWidthPx,
                        blurPx = blurPx,
                        tintColor = baseTint,
                        lightVector = lightVector,
                        thicknessMultiplier = thicknessMultiplier
                    )
                    .drawWithContent {
                        val offsetX = localBoundsInRoot.left - backdrop.rootBounds.left
                        val offsetY = localBoundsInRoot.top - backdrop.rootBounds.top
                        drawContext.canvas.save()
                        drawContext.canvas.translate(-offsetX, -offsetY)
                        drawLayer(backdrop.graphicsLayer)
                        drawContext.canvas.restore()
                    }
            )
        } else {
            // Fallback or Reduce-Glass surface with adaptive gradient sheen
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(baseTint)
            )
        }

        // 2. Specular Bevel Rim, Inner Glow, Noise Grain & Shimmer Sweep Canvas
        Canvas(modifier = Modifier.matchParentSize()) {
            val minDim = minOf(size.width, size.height)
            val actualCorner = cornerPx.coerceAtMost(minDim / 2f)

            // If we didn't run the AGSL shader (API < 33 or no backdrop), apply base tint overlay
            if (backdrop == null || !config.supportsRuntimeShader) {
                drawRoundRect(
                    color = baseTint,
                    cornerRadius = CornerRadius(actualCorner, actualCorner)
                )
            }

            if (!config.effectiveReduceGlass) {
                // Soft inner glow
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = config.innerGlowAlpha * config.intensity),
                            Color.Transparent,
                            Color.White.copy(alpha = config.innerGlowAlpha * 0.35f * config.intensity)
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    cornerRadius = CornerRadius(actualCorner, actualCorner)
                )

                // Interactive Press Shimmer Sweep
                if (pressProgress > 0.01f) {
                    val sweepX = size.width * (-0.3f + pressProgress * 1.6f)
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.28f * (1f - pressProgress * 0.5f)),
                                Color.Transparent
                            ),
                            start = Offset(sweepX - size.width * 0.35f, 0f),
                            end = Offset(sweepX + size.width * 0.35f, size.height)
                        ),
                        cornerRadius = CornerRadius(actualCorner, actualCorner)
                    )
                }

                // Specular top-left bevel highlight & bottom-right counter-rim
                val lightAngle = atan2Safe(tiltOffset.y, tiltOffset.x)
                val startOffset = Offset(
                    x = size.width * (0.5f + 0.5f * cos(lightAngle)),
                    y = size.height * (0.5f + 0.5f * sin(lightAngle))
                )
                val endOffset = Offset(
                    x = size.width * (0.5f - 0.5f * cos(lightAngle)),
                    y = size.height * (0.5f - 0.5f * sin(lightAngle))
                )

                val topRimAlpha = if (config.highContrastGlass) 0.95f else (0.78f * config.specularIntensity)
                val bottomRimAlpha = if (config.highContrastGlass) 0.55f else (0.26f * config.specularIntensity)

                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = topRimAlpha),
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = bottomRimAlpha)
                        ),
                        start = startOffset,
                        end = endOffset
                    ),
                    cornerRadius = CornerRadius(actualCorner, actualCorner),
                    style = Stroke(width = 1.35.dp.toPx())
                )
            } else {
                // High-contrast clean border when Reduce Glass is active
                drawRoundRect(
                    color = if (config.isDarkTheme) Color.White.copy(alpha = 0.24f) else Color.Black.copy(alpha = 0.16f),
                    cornerRadius = CornerRadius(actualCorner, actualCorner),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // 3. Foreground content
        content()
    }
}

private fun atan2Safe(y: Float, x: Float): Float = kotlin.math.atan2(y, x)

private fun Modifier.liquidGlassEffectModifier(
    config: GlassConfig,
    cornerPx: Float,
    rimWidthPx: Float,
    blurPx: Float,
    tintColor: Color,
    lightVector: Offset,
    thicknessMultiplier: Float
): Modifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && config.supportsRuntimeShader) {
        this.agslLiquidGlassModifier(
            config = config,
            cornerPx = cornerPx,
            rimWidthPx = rimWidthPx,
            blurPx = blurPx,
            tintColor = tintColor,
            lightVector = lightVector,
            thicknessMultiplier = thicknessMultiplier
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && config.supportsRenderEffectBlur) {
        this.graphicsLayer {
            renderEffect = RenderEffect.createBlurEffect(
                blurPx,
                blurPx,
                Shader.TileMode.CLAMP
            ).asComposeRenderEffect()
        }
    } else {
        this
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun Modifier.agslLiquidGlassModifier(
    config: GlassConfig,
    cornerPx: Float,
    rimWidthPx: Float,
    blurPx: Float,
    tintColor: Color,
    lightVector: Offset,
    thicknessMultiplier: Float
): Modifier = this.graphicsLayer {
    val widthPx = size.width.coerceAtLeast(1f)
    val heightPx = size.height.coerceAtLeast(1f)
    if (widthPx > 1f && heightPx > 1f) {
        val runtimeShader = RuntimeShader(LIQUID_GLASS_AGSL_SHADER)
        val clampedCorner = cornerPx.coerceAtMost(minOf(widthPx, heightPx) * 0.5f)

        runtimeShader.setFloatUniform("resolution", widthPx, heightPx)
        runtimeShader.setFloatUniform("cornerRadius", clampedCorner)
        runtimeShader.setFloatUniform("rimWidth", rimWidthPx.coerceAtMost(minOf(widthPx, heightPx) * 0.45f))
        runtimeShader.setFloatUniform("refractionStrength", config.effectiveRefraction)
        runtimeShader.setFloatUniform("dispersion", config.effectiveDispersion)
        runtimeShader.setFloatUniform("lightDir", lightVector.x, lightVector.y)
        runtimeShader.setFloatUniform("specularStrength", config.specularIntensity * config.intensity)
        runtimeShader.setFloatUniform("noiseAlpha", config.noiseGrainAlpha)
        runtimeShader.setFloatUniform(
            "tintColor",
            tintColor.red,
            tintColor.green,
            tintColor.blue,
            tintColor.alpha
        )
        runtimeShader.setFloatUniform("thicknessMultiplier", thicknessMultiplier)

        val safeBlurPx = blurPx.coerceIn(0.5f, 60f)
        val blurEffect = RenderEffect.createBlurEffect(
            safeBlurPx,
            safeBlurPx,
            Shader.TileMode.CLAMP
        )
        val shaderEffect = RenderEffect.createRuntimeShaderEffect(runtimeShader, "content")
        renderEffect = RenderEffect.createChainEffect(shaderEffect, blurEffect).asComposeRenderEffect()
    }
}

/**
 * Helper for drawing an SDF smooth-union (liquid droplet fusion) bridge between two nearby
 * glass pills/circles in a [GlassContainer] (like the iOS 26/27 Tab Bar + Search button).
 */
fun DrawScopeDrawLiquidBridge(
    leftRect: Rect,
    rightRect: Rect,
    gapPx: Float,
    maxFuseDistancePx: Float,
    bridgeColor: Color,
    rimColor: Color
): Path? {
    if (gapPx >= maxFuseDistancePx || gapPx < -20f) return null
    val fuseFactor = (1f - (gapPx / maxFuseDistancePx).coerceIn(0f, 1f))
    if (fuseFactor <= 0.02f) return null

    val centerY = (leftRect.center.y + rightRect.center.y) * 0.5f
    val maxNeckHalfHeight = minOf(leftRect.height, rightRect.height) * 0.36f * fuseFactor
    val leftX = leftRect.right - 6f
    val rightX = rightRect.left + 6f
    val midX = (leftX + rightX) * 0.5f

    return Path().apply {
        moveTo(leftX, centerY - maxNeckHalfHeight * 1.35f)
        quadraticTo(
            midX,
            centerY - maxNeckHalfHeight * 0.55f,
            rightX,
            centerY - maxNeckHalfHeight * 1.35f
        )
        lineTo(rightX, centerY + maxNeckHalfHeight * 1.35f)
        quadraticTo(
            midX,
            centerY + maxNeckHalfHeight * 0.55f,
            leftX,
            centerY + maxNeckHalfHeight * 1.35f
        )
        close()
    }
}
