package com.example.glass

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassGreen
import com.example.ui.theme.GlassRed
import com.example.ui.theme.LocalGlassTokens

/**
 * Triggers a crisp haptic tick respecting GlassConfig.hapticsEnabled.
 */
fun View.performGlassHaptic(config: GlassConfig, isConfirm: Boolean = false) {
    if (!config.hapticsEnabled) return
    val constant = if (isConfirm) {
        HapticFeedbackConstants.CONFIRM
    } else {
        HapticFeedbackConstants.CLOCK_TICK
    }
    performHapticFeedback(constant)
}

enum class GlassButtonShape {
    CAPSULE,
    CIRCLE,
    ROUNDED_RECT
}

/**
 * 1. GlassButton: supports Regular, Clear, Tinted/Prominent, Icon-only, Capsule, Circle.
 * Includes spring scale bounce (1.0 -> 1.06), slight vertical squash (0.95), shimmer sweep,
 * haptic tick, 48dp minimum touch target, and TalkBack label.
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    contentDescription: String? = text,
    variant: GlassVariant = LocalGlassConfig.current.variant,
    shapeType: GlassButtonShape = if (text == null && icon != null) GlassButtonShape.CIRCLE else GlassButtonShape.CAPSULE,
    enabled: Boolean = true,
    selected: Boolean = false,
    loading: Boolean = false,
    isDestructive: Boolean = false,
    customTint: Color? = null,
    testTag: String = "glass_button_${text ?: contentDescription ?: "action"}"
) {
    val config = LocalGlassConfig.current
    val tokens = LocalGlassTokens.current
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val minTouch = if (config.largerTouchTargets) 54.dp else tokens.minTouchTarget

    val targetScaleX = when {
        !enabled -> 1f
        isPressed && !config.reduceMotion -> config.pressScaleBounce
        selected && !config.reduceMotion -> 1.02f
        else -> 1f
    }
    val targetScaleY = when {
        !enabled -> 1f
        isPressed && !config.reduceMotion -> 0.94f // Slight liquid squash on press
        selected && !config.reduceMotion -> 1.02f
        else -> 1f
    }

    val scaleX by animateFloatAsState(
        targetValue = targetScaleX,
        animationSpec = spring(
            dampingRatio = config.springDampingRatio,
            stiffness = config.springStiffness
        ),
        label = "glassBtnScaleX"
    )
    val scaleY by animateFloatAsState(
        targetValue = targetScaleY,
        animationSpec = spring(
            dampingRatio = config.springDampingRatio,
            stiffness = config.springStiffness
        ),
        label = "glassBtnScaleY"
    )

    val shimmerAnim = remember { Animatable(0f) }
    LaunchedEffect(isPressed) {
        if (isPressed && enabled && !config.reduceMotion) {
            shimmerAnim.snapTo(0f)
            shimmerAnim.animateTo(1f, tween(360))
        } else {
            shimmerAnim.snapTo(0f)
        }
    }

    val cornerRadius = when (shapeType) {
        GlassButtonShape.CAPSULE, GlassButtonShape.CIRCLE -> 999.dp
        GlassButtonShape.ROUNDED_RECT -> 18.dp
    }

    val effectiveVariant = if (selected) GlassVariant.TINTED else variant
    val effectiveTint = when {
        customTint != null -> customTint
        isDestructive -> GlassRed.copy(alpha = 0.25f)
        selected -> config.accentTint.copy(alpha = 0.42f)
        else -> null
    }

    val fgColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        isDestructive -> GlassRed
        effectiveVariant == GlassVariant.TINTED || selected -> Color.White
        config.isDarkTheme -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    LiquidGlassSurface(
        modifier = modifier
            .testTag(testTag)
            .semantics {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
                this.role = Role.Button
            }
            .graphicsLayer {
                this.scaleX = scaleX
                this.scaleY = scaleY
                this.alpha = if (enabled) 1f else 0.5f
            }
            .then(
                if (shapeType == GlassButtonShape.CIRCLE) {
                    Modifier.size(minTouch)
                } else {
                    Modifier.heightIn(min = minTouch)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading
            ) {
                view.performGlassHaptic(config)
                onClick()
            },
        cornerRadius = cornerRadius,
        variant = effectiveVariant,
        customTint = effectiveTint,
        pressProgress = shimmerAnim.value
    ) {
        Row(
            modifier = Modifier
                .then(
                    if (shapeType == GlassButtonShape.CIRCLE) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    }
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = fgColor,
                    strokeWidth = 2.dp
                )
            } else {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = fgColor,
                        modifier = Modifier.size(21.dp)
                    )
                }
                if (icon != null && text != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
                if (text != null) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        color = fgColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * 2. GlassContainer: Morphing & fusing neighbor glass shapes like liquid droplets (SDF smooth-union).
 * Similar to iOS 26/27 GlassEffectContainer. When two child glass elements are close together,
 * renders a liquid meniscus bridge that stretches and snaps cleanly as they separate.
 */
@Composable
fun GlassContainer(
    modifier: Modifier = Modifier,
    spacing: Dp = 10.dp,
    maxFuseDistance: Dp = 22.dp,
    content: @Composable RowScope.() -> Unit
) {
    val config = LocalGlassConfig.current
    val density = LocalDensity.current
    val spacingPx = with(density) { spacing.toPx() }
    val maxFusePx = with(density) { maxFuseDistance.toPx() }

    val animatedGapPx by animateFloatAsState(
        targetValue = spacingPx,
        animationSpec = spring(
            dampingRatio = config.springDampingRatio,
            stiffness = config.springStiffness
        ),
        label = "glassContainerGap"
    )

    val bridgeTint = if (config.isDarkTheme) {
        Color(0x883A3A3E)
    } else {
        Color(0x99FFFFFF)
    }

    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
        // Subtle liquid droplet fusion glow between close neighbors when Reduce Glass is off
        if (!config.effectiveReduceGlass && animatedGapPx < maxFusePx) {
            val fuseAlpha = (1f - (animatedGapPx / maxFusePx).coerceIn(0f, 1f)) * 0.45f
            Canvas(modifier = Modifier.matchParentSize()) {
                if (size.width > 80f && size.height > 20f) {
                    val leftRect = Rect(0f, 0f, size.width * 0.65f, size.height)
                    val rightRect = Rect(size.width * 0.65f + animatedGapPx, 0f, size.width, size.height)
                    val path = DrawScopeDrawLiquidBridge(
                        leftRect = leftRect,
                        rightRect = rightRect,
                        gapPx = animatedGapPx,
                        maxFuseDistancePx = maxFusePx,
                        bridgeColor = bridgeTint,
                        rimColor = Color.White
                    )
                    if (path != null) {
                        drawPath(path = path, color = bridgeTint.copy(alpha = fuseAlpha))
                        drawPath(
                            path = path,
                            color = Color.White.copy(alpha = fuseAlpha * 0.8f),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3. GlassSegmentedControl: iOS 27 pill segmented control with sliding liquid glass thumb.
 * Used for Years / Months / Days / All Photos, Theme (System/Light/Dark), Glass Style, etc.
 */
@Composable
fun <T> GlassSegmentedControl(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    labelProvider: (T) -> String,
    modifier: Modifier = Modifier,
    testTagPrefix: String = "glass_seg"
) {
    val config = LocalGlassConfig.current
    val view = LocalView.current
    val tokens = LocalGlassTokens.current
    val minHeight = if (config.largerTouchTargets) 52.dp else 44.dp

    LiquidGlassSurface(
        modifier = modifier.heightIn(min = minHeight),
        cornerRadius = tokens.radiusCapsule
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                val label = labelProvider(option)
                val pillScale by animateFloatAsState(
                    targetValue = if (isSelected && !config.reduceMotion) 1.02f else 1f,
                    animationSpec = spring(
                        dampingRatio = config.springDampingRatio,
                        stiffness = config.springStiffness
                    ),
                    label = "segPillScale"
                )

                val pillBg = when {
                    !isSelected -> Color.Transparent
                    config.isDarkTheme -> Color(0xCC222228)
                    else -> Color.White.copy(alpha = 0.88f)
                }

                val textColor = when {
                    isSelected && config.isDarkTheme -> Color.White
                    isSelected && !config.isDarkTheme -> Color(0xFF0A0B0E)
                    config.isDarkTheme -> Color.White.copy(alpha = 0.78f)
                    else -> Color(0xFF3A3A40)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = minHeight - 8.dp)
                        .testTag("${testTagPrefix}_$label")
                        .graphicsLayer {
                            scaleX = pillScale
                            scaleY = pillScale
                        }
                        .clip(RoundedCornerShape(tokens.radiusCapsule))
                        .background(pillBg)
                        .then(
                            if (isSelected && !config.effectiveReduceGlass) {
                                Modifier.border(
                                    width = 0.8.dp,
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.65f),
                                            Color.White.copy(alpha = 0.15f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(tokens.radiusCapsule)
                                )
                            } else Modifier
                        )
                        .clickable {
                            view.performGlassHaptic(config)
                            onOptionSelected(option)
                        }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * 4. GlassSwitch: iOS 26/27 Liquid Glass toggle switch where the thumb becomes a refractive
 * glass droplet that scales up slightly when pressed/toggled.
 */
@Composable
fun GlassSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String = "Toggle switch",
    testTag: String = "glass_switch"
) {
    val config = LocalGlassConfig.current
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = config.springDampingRatio,
            stiffness = config.springStiffness
        ),
        label = "switchOffset"
    )
    val thumbScale by animateFloatAsState(
        targetValue = if (isPressed && !config.reduceMotion) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = config.springDampingRatio,
            stiffness = config.springStiffness
        ),
        label = "switchScale"
    )

    val trackColor = when {
        checked -> GlassGreen
        config.isDarkTheme -> Color(0xFF39393D)
        else -> Color(0xFFD8D8E0)
    }

    Box(
        modifier = modifier
            .testTag(testTag)
            .size(width = 56.dp, height = 48.dp) // 48dp minimum accessible touch target
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.Switch
                this.stateDescription = if (checked) "On" else "Off"
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                view.performGlassHaptic(config)
                onCheckedChange(!checked)
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(52.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(trackColor.copy(alpha = if (enabled) 1f else 0.45f))
                .border(
                    width = 0.8.dp,
                    color = Color.White.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(999.dp)
                )
        ) {
            // Liquid Glass droplet thumb
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset, y = 2.dp)
                    .size(28.dp)
                    .graphicsLayer {
                        scaleX = thumbScale
                        scaleY = thumbScale
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = if (isPressed && !config.effectiveReduceGlass) {
                                listOf(
                                    Color.White.copy(alpha = 0.92f),
                                    Color.White.copy(alpha = 0.65f)
                                )
                            } else {
                                listOf(Color.White, Color(0xFFF2F2F7))
                            }
                        )
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            listOf(Color.White, Color.Black.copy(alpha = 0.15f))
                        ),
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * 5. GlassSlider: iOS 26/27 Liquid Glass slider with refractive capsule lens thumb
 * (matching reference PDF page 16, 33, 34, 40).
 */
@Composable
fun GlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    contentDescription: String = "Slider",
    testTag: String = "glass_slider"
) {
    val config = LocalGlassConfig.current
    val view = LocalView.current
    var trackWidthPx by remember { mutableFloatStateOf(1f) }
    var isDragging by remember { mutableStateOf(false) }

    val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)

    val thumbScale by animateFloatAsState(
        targetValue = if (isDragging && !config.reduceMotion) 1.16f else 1f,
        animationSpec = spring(
            dampingRatio = config.springDampingRatio,
            stiffness = config.springStiffness
        ),
        label = "sliderThumbScale"
    )

    Row(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .semantics {
                this.contentDescription = "$contentDescription ${(fraction * 100).toInt()} percent"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .onGloballyPositioned { trackWidthPx = it.size.width.toFloat().coerceAtLeast(1f) }
                .pointerInput(valueRange) {
                    detectTapGestures { offset ->
                        val f = (offset.x / trackWidthPx).coerceIn(0f, 1f)
                        val newVal = valueRange.start + f * (valueRange.endInclusive - valueRange.start)
                        view.performGlassHaptic(config)
                        onValueChange(newVal)
                    }
                }
                .pointerInput(valueRange) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                            view.performGlassHaptic(config)
                        },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onHorizontalDrag = { change, _ ->
                            change.consume()
                            val f = (change.position.x / trackWidthPx).coerceIn(0f, 1f)
                            val newVal = valueRange.start + f * (valueRange.endInclusive - valueRange.start)
                            onValueChange(newVal)
                        }
                    )
                },
            contentAlignment = Alignment.CenterStart
        ) {
            // Background track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (config.isDarkTheme) Color.White.copy(alpha = 0.18f)
                        else Color.Black.copy(alpha = 0.12f)
                    )
            )
            // Active accent fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(config.accentTint)
            )

            // Liquid Glass capsule lens thumb (like iOS 26/27 WWDC glass slider)
            val density = LocalDensity.current
            val thumbWidthDp = 38.dp
            val thumbWidthPx = with(density) { thumbWidthDp.toPx() }
            val availablePx = (trackWidthPx - thumbWidthPx).coerceAtLeast(0f)
            val offsetDp = with(density) { (availablePx * fraction).toDp() }

            LiquidGlassSurface(
                modifier = Modifier
                    .offset(x = offsetDp)
                    .width(thumbWidthDp)
                    .height(26.dp)
                    .graphicsLayer {
                        scaleX = thumbScale
                        scaleY = thumbScale
                    },
                cornerRadius = 999.dp,
                variant = if (isDragging) GlassVariant.CLEAR else GlassVariant.REGULAR,
                thicknessMultiplier = if (isDragging) 1.35f else 1.0f
            ) {}
        }

        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 6. GlassStepper: Liquid Glass minus/plus stepper control with value pill.
 */
@Composable
fun GlassStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier,
    step: Int = 1,
    unitSuffix: String = "",
    testTag: String = "glass_stepper"
) {
    val config = LocalGlassConfig.current
    val view = LocalView.current

    LiquidGlassSurface(
        modifier = modifier
            .testTag(testTag)
            .heightIn(min = 48.dp),
        cornerRadius = 999.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(enabled = value > range.first) {
                        view.performGlassHaptic(config)
                        onValueChange((value - step).coerceAtLeast(range.first))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "−",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (value > range.first) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                )
            }

            Text(
                text = "$value$unitSuffix",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(enabled = value < range.last) {
                        view.performGlassHaptic(config)
                        onValueChange((value + step).coerceAtMost(range.last))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (value < range.last) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                )
            }
        }
    }
}

/**
 * 7. GlassCard & FlatTonalRow: Settings and grouped sections use GlassCard for the outer floating
 * container, and flat tonal rows inside ("Never glass inside glass" rule).
 * Concentric corner radius: outer = 26.dp, inner rows = 18.dp.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = LocalGlassTokens.current.radiusCardOuter,
    variant: GlassVariant = LocalGlassConfig.current.variant,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    LiquidGlassSurface(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = cornerRadius,
        variant = variant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Flat tonal row for use INSIDE a GlassCard or GlassBottomSheet (respects "Never glass inside glass").
 */
@Composable
fun FlatTonalRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    leadingIconTint: Color = LocalGlassConfig.current.accentTint,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val config = LocalGlassConfig.current
    val tokens = LocalGlassTokens.current
    val view = LocalView.current
    val minHeight = if (config.largerTouchTargets) 58.dp else 50.dp

    val rowBg = if (config.isDarkTheme) {
        Color.White.copy(alpha = 0.06f)
    } else {
        Color.Black.copy(alpha = 0.045f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .clip(RoundedCornerShape(tokens.radiusRow))
            .background(rowBg)
            .then(
                if (onClick != null) {
                    Modifier.clickable {
                        view.performGlassHaptic(config)
                        onClick()
                    }
                } else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(leadingIconTint.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = leadingIconTint,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(10.dp))
            trailingContent()
        }
    }
}

/**
 * 8. GlassChip & GlassBadge:
 * - GlassChip: interactive suggestion / filter chip with 48dp minimum touch target
 * - GlassBadge: small clear-glass chip on photo/video thumbnails (duration, LIVE, RAW, GIF, Heart)
 */
@Composable
fun GlassChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    selected: Boolean = false,
    testTag: String = "glass_chip_$label"
) {
    GlassButton(
        onClick = onClick,
        text = label,
        icon = leadingIcon,
        selected = selected,
        variant = if (selected) GlassVariant.TINTED else GlassVariant.REGULAR,
        modifier = modifier,
        testTag = testTag
    )
}

@Composable
fun GlassBadge(
    text: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.White,
    contentDescription: String? = text
) {
    LiquidGlassSurface(
        modifier = modifier
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else Modifier
            ),
        cornerRadius = 999.dp,
        variant = GlassVariant.CLEAR,
        customTint = Color(0x78141418) // Clear glass with dark legibility scrim
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(13.dp)
                )
            }
            if (text != null) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }
}

/**
 * 9. GlassSearchField: Floating bottom glass search bar within thumb reach (like iOS 26/27 Photos & Maps).
 */
@Composable
fun GlassSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onClose: (() -> Unit)? = null,
    placeholder: String = "Places, dates, media types, 'videos last month'…",
    modifier: Modifier = Modifier,
    testTag: String = "glass_search_field"
) {
    val config = LocalGlassConfig.current
    val fgColor = if (config.isDarkTheme) Color.White else Color(0xFF0B0C10)
    val hintColor = if (config.isDarkTheme) Color.White.copy(alpha = 0.55f) else Color(0xFF6C6C72)

    GlassContainer(
        modifier = modifier.fillMaxWidth(),
        spacing = 10.dp
    ) {
        LiquidGlassSurface(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 52.dp),
            cornerRadius = 999.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search icon",
                    tint = hintColor,
                    modifier = Modifier.size(21.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = hintColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = fgColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(config.accentTint),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {}),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(testTag)
                    )
                }
                if (query.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(hintColor.copy(alpha = 0.25f))
                            .clickable { onClear() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = fgColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        if (onClose != null) {
            GlassButton(
                onClick = onClose,
                icon = Icons.Default.Close,
                contentDescription = "Close search",
                shapeType = GlassButtonShape.CIRCLE,
                modifier = Modifier.size(52.dp),
                testTag = "glass_search_close"
            )
        }
    }
}
