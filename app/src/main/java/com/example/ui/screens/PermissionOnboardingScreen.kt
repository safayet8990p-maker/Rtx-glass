package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.glass.AppLanguage
import com.example.glass.FlatTonalRow
import com.example.glass.GlassButton
import com.example.glass.GlassCard
import com.example.glass.GlassBackdropProvider
import com.example.glass.GlassVariant
import com.example.glass.LiquidGlassSurface
import com.example.glass.LocalGlassConfig
import com.example.viewmodel.MediaPermissionLevel

/**
 * Friendly Permission & Onboarding Glass Screen for Android 13 / 14 / 15+ (including partial photo access).
 */
@Composable
fun PermissionOnboardingScreen(
    permissionLevel: MediaPermissionLevel,
    onGrantPermissionClick: () -> Unit,
    onContinueToAppClick: () -> Unit
) {
    val config = LocalGlassConfig.current
    val isBangla = config.language == AppLanguage.BANGLA

    GlassBackdropProvider(
        backdropLuminanceHint = 0.28f,
        backgroundContent = {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF070B19),
                            Color(0xFF122C4F),
                            Color(0xFF1F4E79),
                            Color(0xFF0A1128)
                        ),
                        start = Offset.Zero,
                        end = Offset(w, h)
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0A84FF).copy(alpha = 0.45f), Color.Transparent),
                        center = Offset(w * 0.25f, h * 0.25f),
                        radius = w * 0.6f
                    ),
                    radius = w * 0.6f,
                    center = Offset(w * 0.25f, h * 0.25f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFF375F).copy(alpha = 0.35f), Color.Transparent),
                        center = Offset(w * 0.78f, h * 0.68f),
                        radius = w * 0.55f
                    ),
                    radius = w * 0.55f,
                    center = Offset(w * 0.78f, h * 0.68f)
                )
            }
        },
        floatingOverlayContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Layered Liquid Glass Hero Illustration
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LiquidGlassSurface(
                        modifier = Modifier.size(96.dp),
                        cornerRadius = 28.dp,
                        variant = GlassVariant.TINTED,
                        thicknessMultiplier = 1.4f
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Glassly Emblem",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Text(
                        text = "Glassly",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (isBangla) {
                            "iOS 27 লিকুইড গ্লাস ফটো গ্যালারি"
                        } else {
                            "Liquid Glass Photos & Media Experience"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.82f),
                        textAlign = TextAlign.Center
                    )
                }

                // Feature & Permission Explanation Glass Card
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FlatTonalRow(
                            title = if (isBangla) "অ্যান্ড্রয়েড ১৩/১৪/১৫ মিডিয়া অ্যাক্সেস" else "Full or Selected Photo Access",
                            subtitle = if (isBangla) {
                                "আপনার ক্যামেরার সব ছবি অথবা শুধুমাত্র নির্বাচিত ছবিগুলোর অনুমতি দিন"
                            } else {
                                "Supports Android 14/15 Partial Photo Access as well as full MediaStore library sync."
                            },
                            leadingIcon = Icons.Default.PhotoLibrary
                        )
                        FlatTonalRow(
                            title = if (isBangla) "১০০% অন-ডিভাইস প্রাইভেসি" else "100% Private & On-Device",
                            subtitle = if (isBangla) {
                                "আপনার কোনো ছবি কখনোই ইন্টারনেটে আপলোড হয় না"
                            } else {
                                "Your photos, videos, and non-destructive edits never leave your phone."
                            },
                            leadingIcon = Icons.Default.Security
                        )
                        FlatTonalRow(
                            title = if (isBangla) "সুরক্ষিত হিডেন অ্যালবাম" else "Biometric Hidden Album",
                            subtitle = if (isBangla) {
                                "পিন এবং ফিঙ্গারপ্রিন্ট দিয়ে ব্যক্তিগত ছবি লুকিয়ে রাখুন"
                            } else {
                                "Lock sensitive photos behind a refractive glass PIN pad and biometric shield."
                            },
                            leadingIcon = Icons.Default.Lock
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassButton(
                        onClick = onGrantPermissionClick,
                        text = if (isBangla) "ফটো ও ভিডিও অ্যাক্সেস দিন" else "Grant Photo & Video Access",
                        variant = GlassVariant.TINTED,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "onboarding_grant_permission_btn"
                    )
                    GlassButton(
                        onClick = onContinueToAppClick,
                        text = if (isBangla) "স্টুডিও গ্যালারি দিয়ে শুরু করুন" else "Continue with Curated Studio Library",
                        variant = GlassVariant.REGULAR,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "onboarding_continue_studio_btn"
                    )
                }
            }
        }
    )
}
