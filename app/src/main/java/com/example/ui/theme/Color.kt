package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// iOS 26/27 Liquid Glass Color Palette
val GlassBlue = Color(0xFF0A84FF)
val GlassBlueLight = Color(0xFF4AA3FF)
val GlassCyan = Color(0xFF32ADE6)
val GlassIndigo = Color(0xFF5E5CE6)
val GlassPurple = Color(0xFFBF5AF2)
val GlassPink = Color(0xFFFF375F)
val GlassOrange = Color(0xFFFF9F0A)
val GlassYellow = Color(0xFFFFD60A)
val GlassGreen = Color(0xFF34C759)
val GlassRed = Color(0xFFFF3B30)

// Light Theme Surfaces & Glass Tokens
val LightBackground = Color(0xFFF6F7FB)
val LightSurface = Color(0xFFFFFFFF)
val LightRowFill = Color(0xFFF0F0F5)
val LightSheetFill = Color(0xFFF2F2F7)
val LightTextPrimary = Color(0xFF0B0C10)
val LightTextSecondary = Color(0xFF6C6C72)
val LightGlassRegular = Color(0x9EFFFFFF)     // ~62% translucent white
val LightGlassClear = Color(0x52FFFFFF)       // ~32% ultra-clear
val LightGlassBorderTop = Color(0xD9FFFFFF)   // Top-left bevel highlight
val LightGlassBorderBottom = Color(0x40FFFFFF)
val LightHairline = Color(0x1F000000)

// Dark Theme Surfaces & Glass Tokens
val DarkBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF161618)
val DarkRowFill = Color(0xFF1C1C1E)
val DarkSheetFill = Color(0xFF0E0E10)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFF98989F)
val DarkGlassRegular = Color(0x8C3A3A3E)      // ~55% translucent dark graphite
val DarkGlassClear = Color(0x6628282C)        // ~40% clear dark glass
val DarkGlassBorderTop = Color(0x73FFFFFF)    // Specular rim top-left
val DarkGlassBorderBottom = Color(0x24FFFFFF)
val DarkHairline = Color(0x26FFFFFF)

// Accent Swatches for Settings -> Appearance -> Accent Tint
enum class AccentTintOption(val labelEn: String, val labelBn: String, val color: Color) {
    OCEAN_BLUE("Ocean Blue", "নীল", GlassBlue),
    CYBER_CYAN("Crystal Cyan", "সায়ান", GlassCyan),
    ROYAL_INDIGO("Royal Indigo", "ইন্ডিগো", GlassIndigo),
    ROSE_PINK("Rose Quartz", "গোলাপী", GlassPink),
    AMBER_GOLD("Amber Gold", "সোনালী", GlassOrange),
    EMERALD_GREEN("Emerald", "সবুজ", GlassGreen)
}
