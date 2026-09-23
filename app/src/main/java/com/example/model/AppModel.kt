package com.example.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.geometry.Offset

/**
 * Represents an installed Android application discovered via PackageManager.
 */
data class AppInfo(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false,
    val isPinned: Boolean = false
)

/**
 * Context menu display state and targeted item.
 */
data class ContextMenuState(
    val isVisible: Boolean = false,
    val position: Offset = Offset.Zero,
    val targetApp: AppInfo? = null,
    val isDesktopContext: Boolean = false
)

enum class ContextMenuAction {
    LAUNCH,
    OPEN_SIMULATED_WINDOW,
    TOGGLE_PIN,
    APP_INFO,
    UNINSTALL,
    CHANGE_WALLPAPER,
    OPEN_TASK_MANAGER,
    OPEN_FILE_EXPLORER,
    OPEN_TERMINAL,
    OPEN_NOTES,
    OPEN_SETTINGS,
    OPEN_GITHUB_HUB,
    REFRESH_DESKTOP
}
