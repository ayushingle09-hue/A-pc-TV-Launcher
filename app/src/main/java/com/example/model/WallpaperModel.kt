package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Desktop wallpaper theme presets.
 */
data class WallpaperTheme(
    val id: String,
    val name: String,
    val startColor: Color,
    val centerColor: Color,
    val endColor: Color,
    val accentGlow: Color,
    val style: WallpaperStyle = WallpaperStyle.GRADIENT_GRID
)

enum class WallpaperStyle {
    GRADIENT_GRID,
    CYBER_NEBULA,
    AURORA_NIGHT,
    MINIMAL_DARK,
    RETRO_PC
}

val WallpaperPresets = listOf(
    WallpaperTheme(
        id = "cyber_nebula",
        name = "Deep Nebula",
        startColor = Color(0xFF020617),
        centerColor = Color(0xFF0F172A),
        endColor = Color(0xFF0369A1),
        accentGlow = Color(0xFF38BDF8),
        style = WallpaperStyle.CYBER_NEBULA
    ),
    WallpaperTheme(
        id = "aurora_night",
        name = "Nordic Aurora",
        startColor = Color(0xFF051D1A),
        centerColor = Color(0xFF0B2D27),
        endColor = Color(0xFF047857),
        accentGlow = Color(0xFF34D399),
        style = WallpaperStyle.AURORA_NIGHT
    ),
    WallpaperTheme(
        id = "cyberpunk_neon",
        name = "Neon Horizon",
        startColor = Color(0xFF1E0A3C),
        centerColor = Color(0xFF2E1065),
        endColor = Color(0xFF7E22CE),
        accentGlow = Color(0xFFC084FC),
        style = WallpaperStyle.CYBER_NEBULA
    ),
    WallpaperTheme(
        id = "minimal_dark",
        name = "Midnight Slate",
        startColor = Color(0xFF090D16),
        centerColor = Color(0xFF0F1420),
        endColor = Color(0xFF182234),
        accentGlow = Color(0xFF64748B),
        style = WallpaperStyle.MINIMAL_DARK
    )
)
