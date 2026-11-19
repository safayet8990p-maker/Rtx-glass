package com.example.glass

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassRed
import com.example.ui.theme.LocalGlassTokens

enum class GlassTabDestination(val labelEn: String, val labelBn: String, val icon: ImageVector) {
    LIBRARY("Library", "লাইব্রেরি", Icons.Default.PhotoLibrary),
    COLLECTIONS("Collections", "কালেকশন", Icons.Default.Collections),
    SETTINGS("Settings", "সেটিংস", Icons.Default.Settings)
}

/**
 * 10. GlassTabBar: iOS 26/27 floating bottom tab bar with shrink-on-scroll-down and
 * expand-on-scroll-up/tap, fused via [GlassContainer] with the floating Search circle button
 * and optional Years/Months/Days/All segmented pill when collapsed in Library.
 */
@Composable
fun GlassTabBar(
    currentTab: GlassTabDestination,
    onTabSelected: (GlassTabDestination) -> Unit,
    onSearchClicked: () -> Unit,
    isCollapsed: Boolean,
    libraryTimeSegment: String? = null,
    onLibraryTimeSegmentSelected: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val config = LocalGlassConfig.current
    val view = LocalView.current
    val isBangla = config.language == AppLanguage.BANGLA

    val pillHeight by animateDpAsState(
        targetValue = if (isCollapsed) 48.dp else 56.dp,
        animationSpec = spring(
            dampingRatio = config.springDampingRatio,
            stiffness = config.springStiffness
        ),
        label = "tabBarHeight"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        GlassContainer(
            modifier = Modifier.fillMaxWidth(),
            spacing = 10.dp
        ) {
            // Left main navigation capsule (2-tab pill: Library & Collections, matching iOS 26 Photos video)
            val mainBottomTabs = listOf(
                GlassTabDestination.LIBRARY,
                GlassTabDestination.COLLECTIONS
            )
            val showCollapsedLibraryIconOnly = isCollapsed && currentTab == GlassTabDestination.LIBRARY

            LiquidGlassSurface(
                modifier = Modifier
                    .height(pillHeight)
                    .then(if (showCollapsedLibraryIconOnly) Modifier.width(pillHeight) else Modifier.weight(1f)),
                cornerRadius = 999.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = if (showCollapsedLibraryIconOnly) 0.dp else 5.dp, vertical = 4.dp)
                        .then(if (showCollapsedLibraryIconOnly) Modifier.fillMaxSize() else Modifier.fillMaxWidth()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val visibleTabs = if (showCollapsedLibraryIconOnly) {
                        listOf(GlassTabDestination.LIBRARY)
                    } else {
                        mainBottomTabs
                    }

                    visibleTabs.forEach { tab ->
                        val isSelected = tab == currentTab
                        val label = if (isBangla) tab.labelBn else tab.labelEn
                        val activeTint = config.accentTint

                        val itemBg = when {
                            showCollapsedLibraryIconOnly -> Color.Transparent
                            !isSelected -> Color.Transparent
                            config.isDarkTheme -> Color.Black.copy(alpha = 0.34f)
                            else -> activeTint.copy(alpha = 0.15f)
                        }

                        val itemFg = when {
                            isSelected -> if (config.isDarkTheme) Color(0xFF58A6FF) else activeTint
                            config.isDarkTheme -> Color.White.copy(alpha = 0.78f)
                            else -> Color(0xFF3A3A40)
                        }

                        Box(
                            modifier = Modifier
                                .then(if (visibleTabs.size > 1) Modifier.weight(1f) else Modifier.fillMaxSize())
                                .height(pillHeight - 8.dp)
                                .testTag("tab_${tab.name.lowercase()}")
                                .clip(RoundedCornerShape(999.dp))
                                .background(itemBg)
                                .clickable {
                                    view.performGlassHaptic(config)
                                    onTabSelected(tab)
                                }
                                .padding(horizontal = if (showCollapsedLibraryIconOnly) 0.dp else 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = label,
                                    tint = itemFg,
                                    modifier = Modifier.size(if (showCollapsedLibraryIconOnly) 22.dp else 20.dp)
                                )
                                if (!showCollapsedLibraryIconOnly) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = itemFg,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Center morphing Time Segment capsule (Years | Months | All) when Library is scrolled/collapsed (11:03 in video)
            if (showCollapsedLibraryIconOnly &&
                libraryTimeSegment != null && onLibraryTimeSegmentSelected != null
            ) {
                val segments = listOf("Years", "Months", "All")
                GlassSegmentedControl(
                    options = segments,
                    selectedOption = if (libraryTimeSegment in segments) libraryTimeSegment else "All",
                    onOptionSelected = onLibraryTimeSegmentSelected,
                    labelProvider = { seg ->
                        if (isBangla) {
                            when (seg) {
                                "Years" -> "বছর"
                                "Months" -> "মাস"
                                else -> "সব"
                            }
                        } else seg
                    },
                    modifier = Modifier.weight(1f),
                    testTagPrefix = "lib_seg"
                )
            }

            // Right floating Search glass droplet button
            GlassButton(
                onClick = onSearchClicked,
                icon = Icons.Default.Search,
                contentDescription = if (isBangla) "অনুসন্ধান" else "Search",
                shapeType = GlassButtonShape.CIRCLE,
                modifier = Modifier.size(pillHeight),
                testTag = "tab_search_button"
            )
        }
    }
}

/**
 * 11. GlassToolbar: Floating top header bar matching iOS 26/27 Photos reference screenshots
 * (large title + subtitle on left, clickable for oldest jump in Library; floating glass icon & Select pills on right).
 */
@Composable
fun GlassToolbar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    onTitleClick: (() -> Unit)? = null,
    centerPillContent: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            if (navigationIcon != null && onNavigationClick != null) {
                GlassButton(
                    onClick = onNavigationClick,
                    icon = navigationIcon,
                    contentDescription = "Back",
                    shapeType = GlassButtonShape.CIRCLE,
                    testTag = "toolbar_back_button"
                )
            }

            if (centerPillContent == null) {
                Column(
                    modifier = Modifier
                        .then(
                            if (onTitleClick != null) {
                                Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onTitleClick() }
                                    .testTag("toolbar_title_clickable")
                            } else Modifier
                        )
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        if (centerPillContent != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                centerPillContent()
            }
        }

        GlassContainer(spacing = 8.dp) {
            actions()
        }
    }
}

/**
 * 12. GlassActionBar: Multi-select floating bottom bar (Share, Favorite, Add to Album, Delete).
 */
@data_model_marker
data class GlassActionItem(
    val label: String,
    val icon: ImageVector,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@Target(AnnotationTarget.CLASS)
private annotation class data_model_marker

@Composable
fun GlassActionBar(
    selectedCount: Int,
    actions: List<GlassActionItem>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        LiquidGlassSurface(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 999.dp,
            thicknessMultiplier = 1.2f
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions.forEach { action ->
                    val tint = if (action.isDestructive) GlassRed else MaterialTheme.colorScheme.onSurface
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { action.onClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("action_bar_${action.label.lowercase()}"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.label,
                            tint = tint,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = action.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = tint
                        )
                    }
                }
            }
        }
    }
}

/**
 * 13. GlassMenu / ContextMenu: Expands FROM the trigger button with increased glass thickness
 * (shadow + refraction multiplier = 1.45f) matching reference screenshot `captured-by-me-ios-27-photos-filter`.
 */
data class GlassMenuItemData(
    val title: String,
    val icon: ImageVector? = null,
    val isSelected: Boolean = false,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun GlassMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    items: List<GlassMenuItemData>,
    modifier: Modifier = Modifier,
    headerTitle: String? = null,
    topSegmentedPair: Pair<String, String>? = null,
    selectedTopSegmentIndex: Int = 0,
    onTopSegmentSelected: ((Int) -> Unit)? = null,
    transformOrigin: TransformOrigin = TransformOrigin(1f, 0f)
) {
    val config = LocalGlassConfig.current
    val view = LocalView.current

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(
            spring(stiffness = config.springStiffness)
        ) + scaleIn(
            initialScale = 0.35f,
            transformOrigin = transformOrigin,
            animationSpec = spring(
                dampingRatio = config.springDampingRatio,
                stiffness = config.springStiffness
            )
        ),
        exit = fadeOut(
            spring(stiffness = config.springStiffness)
        ) + scaleOut(
            targetScale = 0.35f,
            transformOrigin = transformOrigin,
            animationSpec = spring(stiffness = config.springStiffness)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
                .statusBarsPadding()
                .padding(top = 56.dp, end = 16.dp, start = 16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            // Thickness increases when control expands into a menu
            LiquidGlassSurface(
                modifier = modifier
                    .widthIn(min = 240.dp, max = 285.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Prevent click-through
                    ),
                cornerRadius = 28.dp,
                thicknessMultiplier = 1.45f
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (topSegmentedPair != null && onTopSegmentSelected != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(topSegmentedPair.first, topSegmentedPair.second).forEachIndexed { idx, label ->
                                val active = idx == selectedTopSegmentIndex
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            if (active) Color.White.copy(alpha = 0.22f)
                                            else Color.Transparent
                                        )
                                        .clickable {
                                            view.performGlassHaptic(config)
                                            onTopSegmentSelected(idx)
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    if (!headerTitle.isNullOrBlank()) {
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    items.forEach { item ->
                        val itemColor = if (item.isDestructive) GlassRed else MaterialTheme.colorScheme.onSurface
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    view.performGlassHaptic(config)
                                    item.onClick()
                                    onDismiss()
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier.width(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (item.isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = config.accentTint,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            if (item.icon != null) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = itemColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = itemColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 14. GlassAlert / Dialog: In-place alert expanding from the trigger button (like reference PDF page 47 Delete alert).
 * Inside the glass alert, buttons use flat tonal pills ("Never glass inside glass").
 */
@Composable
fun GlassAlert(
    visible: Boolean,
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissText: String = "Cancel",
    isDestructive: Boolean = false,
    transformOrigin: TransformOrigin = TransformOrigin(0.5f, 0.85f),
    extraContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    val config = LocalGlassConfig.current
    val view = LocalView.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(
            initialScale = 0.45f,
            transformOrigin = transformOrigin,
            animationSpec = spring(
                dampingRatio = config.springDampingRatio,
                stiffness = config.springStiffness
            )
        ),
        exit = fadeOut() + scaleOut(
            targetScale = 0.45f,
            transformOrigin = transformOrigin,
            animationSpec = spring(stiffness = config.springStiffness)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.38f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
                .padding(20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            LiquidGlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .padding(bottom = 24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                cornerRadius = 30.dp,
                thicknessMultiplier = 1.5f
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    if (extraContent != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        extraContent()
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Flat tonal buttons inside the glass alert (never glass inside glass)
                    val flatBtnBg = if (config.isDarkTheme) {
                        Color.White.copy(alpha = 0.12f)
                    } else {
                        Color.Black.copy(alpha = 0.07f)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(flatBtnBg)
                                .clickable {
                                    view.performGlassHaptic(config)
                                    onDismiss()
                                }
                                .testTag("alert_dismiss_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dismissText,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isDestructive) GlassRed.copy(alpha = 0.22f)
                                    else config.accentTint.copy(alpha = 0.25f)
                                )
                                .clickable {
                                    view.performGlassHaptic(config, isConfirm = true)
                                    onConfirm()
                                }
                                .testTag("alert_confirm_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = confirmText,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isDestructive) GlassRed else config.accentTint
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 15. GlassBottomSheet: Liquid Glass bottom sheet (38.dp top corners) with grab handle, close button,
 * and flat tonal rows inside.
 */
@Composable
fun GlassBottomSheet(
    visible: Boolean,
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val config = LocalGlassConfig.current
    val tokens = LocalGlassTokens.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = config.springDampingRatio,
                stiffness = config.springStiffness
            )
        ),
        exit = fadeOut() + slideOutVertically(
            targetOffsetY = { it },
            animationSpec = spring(stiffness = config.springStiffness)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            LiquidGlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                cornerRadius = tokens.radiusSheet,
                thicknessMultiplier = 1.4f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top grabber handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close sheet",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    content()
                }
            }
        }
    }
}

/**
 * 16. GlassToast / Snackbar, 17. GlassProgress / Loader, 18. GlassScrubber, 19. GlassPicker
 */
@Composable
fun GlassToast(
    message: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !message.isNullOrBlank(),
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        exit = fadeOut() + scaleOut(targetScale = 0.8f),
        modifier = modifier
    ) {
        LiquidGlassSurface(
            cornerRadius = 999.dp,
            thicknessMultiplier = 1.25f
        ) {
            Text(
                text = message ?: "",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 11.dp)
            )
        }
    }
}

@Composable
fun GlassProgressLoader(
    label: String? = null,
    modifier: Modifier = Modifier
) {
    val config = LocalGlassConfig.current
    LiquidGlassSurface(
        modifier = modifier,
        cornerRadius = 24.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = config.accentTint,
                strokeWidth = 2.5.dp
            )
            if (!label.isNullOrBlank()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun GlassScrubberBubble(
    dateLabel: String,
    modifier: Modifier = Modifier
) {
    LiquidGlassSurface(
        modifier = modifier,
        cornerRadius = 999.dp,
        variant = GlassVariant.REGULAR,
        thicknessMultiplier = 1.2f
    ) {
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun GlassPicker(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                FlatTonalRow(
                    title = option,
                    onClick = { onOptionSelected(option) },
                    trailingContent = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = LocalGlassConfig.current.accentTint
                            )
                        }
                    }
                )
            }
        }
    }
}
