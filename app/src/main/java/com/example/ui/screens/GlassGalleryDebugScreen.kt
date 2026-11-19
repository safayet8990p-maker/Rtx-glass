package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.glass.FlatTonalRow
import com.example.glass.GlassActionBar
import com.example.glass.GlassActionItem
import com.example.glass.GlassAlert
import com.example.glass.GlassBadge
import com.example.glass.GlassBottomSheet
import com.example.glass.GlassButton
import com.example.glass.GlassButtonShape
import com.example.glass.GlassCard
import com.example.glass.GlassChip
import com.example.glass.GlassBackdropProvider
import com.example.glass.GlassContainer
import com.example.glass.GlassMenu
import com.example.glass.GlassMenuItemData
import com.example.glass.GlassPicker
import com.example.glass.GlassProgressLoader
import com.example.glass.GlassScrubberBubble
import com.example.glass.GlassSearchField
import com.example.glass.GlassSegmentedControl
import com.example.glass.GlassSlider
import com.example.glass.GlassStepper
import com.example.glass.GlassSwitch
import com.example.glass.GlassToolbar
import com.example.glass.GlassVariant

/**
 * Hidden "Glass Gallery" debug proving-ground screen inspecting all 20 shared Liquid Glass
 * components in every state (normal, pressed, selected, disabled, loading, Regular/Clear/Tinted).
 */
@Composable
fun GlassGalleryDebugScreen(
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var segValue by remember { mutableStateOf("Regular") }
    var switchChecked by remember { mutableStateOf(true) }
    var sliderVal by remember { mutableFloatStateOf(0.68f) }
    var stepperVal by remember { mutableIntStateOf(3) }
    var searchQuery by remember { mutableStateOf("") }
    var pickerVal by remember { mutableStateOf("Date Captured") }
    var showDemoAlert by remember { mutableStateOf(false) }
    var showDemoSheet by remember { mutableStateOf(false) }
    var showDemoMenu by remember { mutableStateOf(false) }

    GlassBackdropProvider(
        backdropLuminanceHint = 0.35f,
        backgroundContent = {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0A1128),
                            Color(0xFF1C3F60),
                            Color(0xFF5E5CE6),
                            Color(0xFFFF375F)
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    )
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 100.dp, bottom = 120.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    GlassCard {
                        Text(
                            text = "1. GlassButton Variants & States",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GlassButton(onClick = {}, text = "Regular", variant = GlassVariant.REGULAR)
                            GlassButton(onClick = {}, text = "Clear", variant = GlassVariant.CLEAR)
                            GlassButton(onClick = {}, text = "Tinted", variant = GlassVariant.TINTED)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            GlassButton(onClick = {}, text = "Selected", selected = true)
                            GlassButton(onClick = {}, text = "Disabled", enabled = false)
                            GlassButton(onClick = {}, text = "Loading", loading = true)
                            GlassButton(
                                onClick = {},
                                icon = Icons.Default.Add,
                                contentDescription = "Circle Add",
                                shapeType = GlassButtonShape.CIRCLE
                            )
                        }
                    }
                }

                item {
                    GlassCard {
                        Text(
                            text = "2. GlassContainer (Liquid Droplet Fusion)",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        GlassContainer(spacing = 8.dp) {
                            GlassButton(onClick = {}, text = "Droplet A")
                            GlassButton(onClick = {}, icon = Icons.Default.Favorite, contentDescription = "Droplet B")
                            GlassButton(onClick = {}, icon = Icons.Default.Share, contentDescription = "Droplet C")
                        }
                    }
                }

                item {
                    GlassCard {
                        Text(
                            text = "3. GlassSegmentedControl, GlassSwitch, GlassSlider, GlassStepper",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        GlassSegmentedControl(
                            options = listOf("Regular", "Clear", "Tinted"),
                            selectedOption = segValue,
                            onOptionSelected = { segValue = it },
                            labelProvider = { it }
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            GlassSwitch(checked = switchChecked, onCheckedChange = { switchChecked = it })
                            GlassStepper(value = stepperVal, onValueChange = { stepperVal = it }, range = 1..10)
                        }
                        GlassSlider(
                            value = sliderVal,
                            onValueChange = { sliderVal = it },
                            leadingIcon = Icons.Default.AutoAwesome,
                            trailingIcon = Icons.Default.Tune
                        )
                    }
                }

                item {
                    GlassCard {
                        Text(
                            text = "4. GlassChip, GlassBadge, GlassScrubber, GlassProgressLoader",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GlassChip(label = "Santorini", onClick = {}, selected = true)
                            GlassChip(label = "Videos", onClick = {})
                            GlassBadge(text = "0:24")
                            GlassBadge(text = "RAW")
                        }
                        Row(
                            modifier = Modifier.padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GlassScrubberBubble(dateLabel = "Jul 13, 2025")
                            GlassProgressLoader(label = "Refracting…")
                        }
                    }
                }

                item {
                    GlassCard {
                        Text(
                            text = "5. GlassSearchField, GlassPicker & Overlays",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        GlassSearchField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onClear = { searchQuery = "" }
                        )
                        Row(
                            modifier = Modifier.padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GlassButton(onClick = { showDemoMenu = true }, text = "Expand Menu")
                            GlassButton(onClick = { showDemoAlert = true }, text = "Expand Alert")
                            GlassButton(onClick = { showDemoSheet = true }, text = "Open Sheet")
                        }
                    }
                }

                item {
                    GlassPicker(
                        title = "Sort Order Picker",
                        options = listOf("Date Captured", "Date Added", "File Size"),
                        selectedOption = pickerVal,
                        onOptionSelected = { pickerVal = it }
                    )
                }

                item {
                    GlassActionBar(
                        selectedCount = 3,
                        actions = listOf(
                            GlassActionItem("Share", Icons.Default.Share) {},
                            GlassActionItem("Favorite", Icons.Default.Favorite) {},
                            GlassActionItem("Delete", Icons.Default.Delete, isDestructive = true) {}
                        )
                    )
                }
            }
        },
        floatingOverlayContent = {
            GlassToolbar(
                title = "Glass Gallery",
                subtitle = "20 Shared Liquid Glass Components",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )

            GlassMenu(
                expanded = showDemoMenu,
                onDismiss = { showDemoMenu = false },
                headerTitle = "Demo Context Menu",
                items = listOf(
                    GlassMenuItemData("All Items", isSelected = true) {},
                    GlassMenuItemData("Favorites", icon = Icons.Default.Favorite) {},
                    GlassMenuItemData("Delete", icon = Icons.Default.Delete, isDestructive = true) {}
                )
            )

            GlassAlert(
                visible = showDemoAlert,
                title = "In-Place Liquid Glass Alert",
                message = "Notice how thickness, shadow elevation, and edge refraction increase when expanded.",
                confirmText = "Confirm",
                onConfirm = { showDemoAlert = false },
                onDismiss = { showDemoAlert = false }
            )

            GlassBottomSheet(
                visible = showDemoSheet,
                title = "Liquid Glass Sheet",
                onDismiss = { showDemoSheet = false }
            ) {
                FlatTonalRow(
                    title = "Flat Tonal Row Inside Sheet",
                    subtitle = "Respects 'Never glass inside glass' architectural rule"
                )
            }
        }
    )
}
