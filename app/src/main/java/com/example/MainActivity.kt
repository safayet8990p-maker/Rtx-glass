package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.glass.AppThemeMode
import com.example.glass.GlassTabBar
import com.example.glass.GlassTabDestination
import com.example.glass.GlassToast
import com.example.glass.GlassVariant
import com.example.glass.LiquidGlassSurface
import com.example.glass.LocalGlassConfig
import com.example.ui.screens.CollectionsScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ManagementSubScreenHost
import com.example.ui.screens.PermissionOnboardingScreen
import com.example.ui.screens.PhotoEditorOverlay
import com.example.ui.screens.PhotoViewerOverlay
import com.example.ui.screens.SearchOverlayScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.GlasslyTheme
import com.example.viewmodel.AppSubScreen
import com.example.viewmodel.GlasslyViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: GlasslyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.refreshPermissionState()

        setContent {
            val glassConfig by viewModel.glassConfig.collectAsStateWithLifecycle()
            val secureScreenEnabled by viewModel.secureScreenEnabled.collectAsStateWithLifecycle()
            val hidePreviewsInRecents by viewModel.hidePreviewsInRecents.collectAsStateWithLifecycle()

            LaunchedEffect(secureScreenEnabled, hidePreviewsInRecents) {
                if (secureScreenEnabled || hidePreviewsInRecents) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            val systemDark = isSystemInDarkTheme()
            val isDark = when (glassConfig.themeMode) {
                AppThemeMode.SYSTEM -> systemDark
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
            }

            val effectiveGlassConfig = remember(glassConfig, isDark) {
                glassConfig.copy(isDarkTheme = isDark)
            }

            GlasslyTheme(
                darkTheme = isDark,
                accentColor = effectiveGlassConfig.accentTint
            ) {
                CompositionLocalProvider(LocalGlassConfig provides effectiveGlassConfig) {
                    GlasslyRootApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun GlasslyRootApp(
    viewModel: GlasslyViewModel
) {
    var showSplash by remember { mutableStateOf(true) }
    var isTabBarCollapsed by remember { mutableStateOf(false) }
    var scrollToNewestTrigger by remember { mutableIntStateOf(0) }

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val libraryTimeSegment by viewModel.libraryTimeSegment.collectAsStateWithLifecycle()
    val isSelectionMode by viewModel.isSelectionMode.collectAsStateWithLifecycle()
    val isSearchOpen by viewModel.isSearchOpen.collectAsStateWithLifecycle()
    val subScreen by viewModel.subScreen.collectAsStateWithLifecycle()
    val activeViewerItemId by viewModel.activeViewerItemId.collectAsStateWithLifecycle()
    val isEditorOpen by viewModel.isEditorOpen.collectAsStateWithLifecycle()
    val allMedia by viewModel.allMergedMedia.collectAsStateWithLifecycle()
    val visibleLibraryMedia by viewModel.visibleLibraryMedia.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val permissionLevel by viewModel.permissionLevel.collectAsStateWithLifecycle()
    val showPermissionOnboarding by viewModel.showPermissionOnboarding.collectAsStateWithLifecycle()

    // Under-1-second Glassly layered glass splash animation
    LaunchedEffect(Unit) {
        delay(680)
        showSplash = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.refreshPermissionState()
        viewModel.setShowPermissionOnboarding(false)
    }

    val requestMediaPermissions: () -> Unit = {
        val perms = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(perms)
    }

    val viewerItem = remember(activeViewerItemId, allMedia) {
        activeViewerItemId?.let { id -> allMedia.find { it.id == id } }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("glassly_root")
    ) {
        // 1. Primary Tab Destination (Library / Collections / Settings)
        when (currentTab) {
            GlassTabDestination.LIBRARY -> {
                LibraryScreen(
                    viewModel = viewModel,
                    onRequestPermissions = { viewModel.setShowPermissionOnboarding(true) },
                    onScrollDirectionChanged = { collapsed ->
                        isTabBarCollapsed = collapsed
                    },
                    scrollToNewestTrigger = scrollToNewestTrigger
                )
            }

            GlassTabDestination.COLLECTIONS -> {
                CollectionsScreen(
                    viewModel = viewModel
                )
            }

            GlassTabDestination.SETTINGS -> {
                SettingsScreen(
                    viewModel = viewModel,
                    onRequestPermissions = { viewModel.setShowPermissionOnboarding(true) }
                )
            }
        }

        // 2. Floating Shrinking Liquid Glass Tab Bar + Search Orb (2-tab pill + Search button)
        if (!isSelectionMode &&
            subScreen == AppSubScreen.None &&
            viewerItem == null &&
            !isSearchOpen &&
            currentTab != GlassTabDestination.SETTINGS
        ) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                GlassTabBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        isTabBarCollapsed = false
                        if (tab == GlassTabDestination.LIBRARY) {
                            viewModel.setLibraryTimeSegment("All")
                            scrollToNewestTrigger++
                        }
                        viewModel.selectTab(tab)
                    },
                    onSearchClicked = { viewModel.openSearch() },
                    isCollapsed = isTabBarCollapsed && currentTab == GlassTabDestination.LIBRARY,
                    libraryTimeSegment = libraryTimeSegment,
                    onLibraryTimeSegmentSelected = { seg -> viewModel.setLibraryTimeSegment(seg) }
                )
            }
        }

        // 3. Sub-Screens (Album Detail, Trash, Hidden Album PIN Lock, Favorites, Duplicates, Large Files, Slideshow, Glass Gallery Debug)
        if (subScreen != AppSubScreen.None) {
            ManagementSubScreenHost(
                subScreen = subScreen,
                viewModel = viewModel,
                onBack = { viewModel.navigateBackFromSubScreen() },
                onOpenPhoto = { id -> viewModel.openViewer(id) }
            )
        }

        // 4. Search Overlay Screen (with thumb-reach bottom glass search bar)
        AnimatedVisibility(
            visible = isSearchOpen,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(180))
        ) {
            SearchOverlayScreen(
                viewModel = viewModel
            )
        }

        // 5. Fullscreen Photo Viewer & Video Player Overlay
        AnimatedVisibility(
            visible = viewerItem != null && !isEditorOpen,
            enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.92f, animationSpec = tween(240)),
            exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.92f, animationSpec = tween(180))
        ) {
            if (viewerItem != null) {
                PhotoViewerOverlay(
                    item = viewerItem,
                    allItems = visibleLibraryMedia.ifEmpty { listOf(viewerItem) },
                    viewModel = viewModel,
                    onClose = { viewModel.closeViewer() },
                    onOpenEditor = { viewModel.openEditor() },
                    onStartSlideshow = {
                        val slides = visibleLibraryMedia.ifEmpty { listOf(viewerItem) }
                        viewModel.closeViewer()
                        viewModel.navigateToSubScreen(
                            AppSubScreen.Slideshow(viewerItem.locationName.ifBlank { "Slideshow" }, slides)
                        )
                    }
                )
            }
        }

        // 6. Non-Destructive Photo Editor Overlay
        AnimatedVisibility(
            visible = viewerItem != null && isEditorOpen,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(180))
        ) {
            if (viewerItem != null) {
                PhotoEditorOverlay(
                    item = viewerItem,
                    viewModel = viewModel,
                    onClose = { viewModel.closeEditor() }
                )
            }
        }

        // 7. Permission Onboarding Modal
        AnimatedVisibility(
            visible = showPermissionOnboarding,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(180))
        ) {
            PermissionOnboardingScreen(
                permissionLevel = permissionLevel,
                onGrantPermissionClick = { requestMediaPermissions() },
                onContinueToAppClick = { viewModel.setShowPermissionOnboarding(false) }
            )
        }

        // 8. Global Liquid Glass Toast Notification
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 62.dp)
        ) {
            GlassToast(message = toastMessage)
        }

        // 9. Under-1-Second Glassly Layered-Glass Splash Overlay
        AnimatedVisibility(
            visible = showSplash,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(280))
        ) {
            GlasslySplashScreenOverlay()
        }
    }
}

@Composable
private fun GlasslySplashScreenOverlay() {
    val scale = remember { Animatable(0.86f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(220, easing = FastOutSlowInEasing))
        scale.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0A1128),
                        Color(0xFF14213D),
                        Color(0xFF090E1A)
                    )
                )
            )
            .testTag("glassly_splash_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            }
        ) {
            LiquidGlassSurface(
                modifier = Modifier.size(104.dp),
                cornerRadius = 30.dp,
                variant = GlassVariant.TINTED
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(60.dp)) {
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF64D2FF), Color(0xFF007AFF), Color(0xFFAF52DE))
                            ),
                            topLeft = Offset(4.dp.toPx(), 6.dp.toPx()),
                            size = Size(size.width - 8.dp.toPx(), size.height - 12.dp.toPx()),
                            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                        )
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.85f),
                            topLeft = Offset(4.dp.toPx(), 6.dp.toPx()),
                            size = Size(size.width - 8.dp.toPx(), size.height - 12.dp.toPx()),
                            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawCircle(
                            color = Color(0xFFFFD60A),
                            radius = 7.dp.toPx(),
                            center = Offset(size.width * 0.34f, size.height * 0.36f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Glassly",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Liquid Glass Photos • v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.72f)
            )
        }
    }
}
