package com.example.model

/**
 * Supported window feature types for the simulated windowing environment.
 */
enum class WindowType {
    TASK_MANAGER,
    FILE_EXPLORER,
    QUICK_NOTES,
    DISPLAY_SETTINGS,
    TERMINAL,
    APP_CONTAINER,
    GITHUB_HUB
}

/**
 * State of an active simulated desktop window.
 */
data class DesktopWindow(
    val id: String,
    val title: String,
    val type: WindowType,
    val posX: Float,
    val posY: Float,
    val width: Float,
    val height: Float,
    val isMinimized: Boolean = false,
    val isMaximized: Boolean = false,
    val zIndex: Float = 1f,
    val extraData: String? = null
)

/**
 * Resize handle directions for window borders and corners.
 */
enum class ResizeDirection {
    NONE,
    EAST,
    WEST,
    NORTH,
    SOUTH,
    NORTH_EAST,
    NORTH_WEST,
    SOUTH_EAST,
    SOUTH_WEST
}

/**
 * Live system hardware metrics for Task Manager and status bar.
 */
data class SystemMetricInfo(
    val totalRamMb: Long = 0,
    val availableRamMb: Long = 0,
    val usedRamPercent: Int = 0,
    val cpuCores: Int = Runtime.getRuntime().availableProcessors(),
    val androidVersion: String = android.os.Build.VERSION.RELEASE,
    val deviceModel: String = android.os.Build.MODEL,
    val uptimeHours: Long = 0
)
