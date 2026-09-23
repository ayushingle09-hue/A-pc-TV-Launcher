package com.example.viewmodel

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.SystemClock
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hardware.MouseDetector
import com.example.model.AppInfo
import com.example.model.ContextMenuAction
import com.example.model.ContextMenuState
import com.example.model.DesktopWindow
import com.example.model.ResizeDirection
import com.example.model.SystemMetricInfo
import com.example.model.WallpaperPresets
import com.example.model.WallpaperTheme
import com.example.model.WindowType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication()
    val mouseDetector = MouseDetector(context)

    // Hardware mouse requirement
    val isMouseActive: StateFlow<Boolean> = combine(
        mouseDetector.isMouseConnected,
        mouseDetector.isBypassed
    ) { connected, bypassed ->
        connected || bypassed
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Boot splash sequence
    private val _isBootCompleted = MutableStateFlow(false)
    val isBootCompleted: StateFlow<Boolean> = _isBootCompleted.asStateFlow()

    // Apps state
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _pinnedPackages = MutableStateFlow<Set<String>>(emptySet())
    val pinnedPackages: StateFlow<Set<String>> = _pinnedPackages.asStateFlow()

    // Filtered apps based on search
    val filteredApps: StateFlow<List<AppInfo>> = combine(
        _installedApps,
        _searchQuery,
        _pinnedPackages
    ) { apps, query, pinned ->
        val updated = apps.map { app ->
            app.copy(isPinned = pinned.contains(app.packageName))
        }
        if (query.isBlank()) {
            updated
        } else {
            updated.filter {
                it.label.contains(query, ignoreCase = true) ||
                it.packageName.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Windowing system state
    private val _windows = MutableStateFlow<List<DesktopWindow>>(emptyList())
    val windows: StateFlow<List<DesktopWindow>> = _windows.asStateFlow()

    private val _activeWindowId = MutableStateFlow<String?>(null)
    val activeWindowId: StateFlow<String?> = _activeWindowId.asStateFlow()

    private var nextZIndex = 1f

    // Start Menu state
    private val _isStartMenuOpen = MutableStateFlow(false)
    val isStartMenuOpen: StateFlow<Boolean> = _isStartMenuOpen.asStateFlow()

    // Context menu state
    private val _contextMenuState = MutableStateFlow(ContextMenuState())
    val contextMenuState: StateFlow<ContextMenuState> = _contextMenuState.asStateFlow()

    // Clock and Date
    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    private val _currentDate = MutableStateFlow("")
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    // Network status
    private val _networkStatus = MutableStateFlow("Wi-Fi")
    val networkStatus: StateFlow<String> = _networkStatus.asStateFlow()

    // System metrics for Task Manager
    private val _systemMetrics = MutableStateFlow(SystemMetricInfo())
    val systemMetrics: StateFlow<SystemMetricInfo> = _systemMetrics.asStateFlow()

    // Wallpaper
    private val _currentWallpaper = MutableStateFlow(WallpaperPresets.first())
    val currentWallpaper: StateFlow<WallpaperTheme> = _currentWallpaper.asStateFlow()

    // Quick notes content
    private val _notepadText = MutableStateFlow("Desktop TV Quick Notes\n- Use mouse pointer to drag windows by title bar\n- Resize from corners and edges\n- Right-click icons for context menus\n- Press Start for all apps")
    val notepadText: StateFlow<String> = _notepadText.asStateFlow()

    init {
        mouseDetector.startListening()
        loadInstalledApps()
        startClockAndMetricsLoop()
        initDefaultPinnedApps()
    }

    override fun onCleared() {
        super.onCleared()
        mouseDetector.stopListening()
    }

    fun bypassMouseCheck() {
        mouseDetector.setBypass(true)
    }

    fun completeBoot() {
        _isBootCompleted.value = true
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleStartMenu() {
        _isStartMenuOpen.value = !_isStartMenuOpen.value
        if (_contextMenuState.value.isVisible) {
            closeContextMenu()
        }
    }

    fun closeStartMenu() {
        _isStartMenuOpen.value = false
    }

    fun showContextMenu(state: ContextMenuState) {
        _isStartMenuOpen.value = false
        _contextMenuState.value = state
    }

    fun closeContextMenu() {
        _contextMenuState.value = ContextMenuState(isVisible = false)
    }

    fun setWallpaper(wallpaper: WallpaperTheme) {
        _currentWallpaper.value = wallpaper
    }

    fun updateNotepadText(text: String) {
        _notepadText.value = text
    }

    private fun initDefaultPinnedApps() {
        _pinnedPackages.value = setOf(
            "com.android.settings",
            "com.android.chrome",
            "com.google.android.youtube",
            "com.google.android.youtube.tv"
        )
    }

    fun togglePinApp(packageName: String) {
        val current = _pinnedPackages.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _pinnedPackages.value = current
    }

    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            val myPackageName = context.packageName

            val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val leanbackIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
            }

            val standardApps = pm.queryIntentActivities(launcherIntent, 0)
            val tvApps = pm.queryIntentActivities(leanbackIntent, 0)

            val combined = (standardApps + tvApps).distinctBy { it.activityInfo.packageName }

            val appList = combined.mapNotNull { resolveInfo ->
                val pName = resolveInfo.activityInfo.packageName
                if (pName == myPackageName) return@mapNotNull null

                val label = resolveInfo.loadLabel(pm).toString()
                val icon = resolveInfo.loadIcon(pm)
                val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and
                        android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0

                AppInfo(
                    label = label,
                    packageName = pName,
                    activityName = resolveInfo.activityInfo.name,
                    icon = icon,
                    isSystemApp = isSystem
                )
            }.sortedBy { it.label.lowercase(Locale.ROOT) }

            withContext(Dispatchers.Main) {
                _installedApps.value = appList
            }
        }
    }

    fun launchApp(app: AppInfo) {
        try {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
        closeStartMenu()
        closeContextMenu()
    }

    fun openAppDetails(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
        closeContextMenu()
    }

    fun uninstallApp(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
        closeContextMenu()
    }

    fun openSystemTvSettings() {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
        closeStartMenu()
    }

    fun handleContextMenuAction(action: ContextMenuAction, app: AppInfo?) {
        when (action) {
            ContextMenuAction.LAUNCH -> if (app != null) launchApp(app)
            ContextMenuAction.OPEN_SIMULATED_WINDOW -> {
                if (app != null) {
                    openWindow(WindowType.APP_CONTAINER, app.label, extra = app.packageName)
                }
            }
            ContextMenuAction.TOGGLE_PIN -> if (app != null) togglePinApp(app.packageName)
            ContextMenuAction.APP_INFO -> if (app != null) openAppDetails(app.packageName)
            ContextMenuAction.UNINSTALL -> if (app != null) uninstallApp(app.packageName)
            ContextMenuAction.CHANGE_WALLPAPER -> openWindow(WindowType.DISPLAY_SETTINGS, "Display & Wallpapers")
            ContextMenuAction.OPEN_TASK_MANAGER -> openWindow(WindowType.TASK_MANAGER, "Task Manager")
            ContextMenuAction.OPEN_FILE_EXPLORER -> openWindow(WindowType.FILE_EXPLORER, "File Explorer")
            ContextMenuAction.OPEN_TERMINAL -> openWindow(WindowType.TERMINAL, "Terminal Console")
            ContextMenuAction.OPEN_NOTES -> openWindow(WindowType.QUICK_NOTES, "Quick Notes")
            ContextMenuAction.OPEN_SETTINGS -> openWindow(WindowType.DISPLAY_SETTINGS, "Display & Settings")
            ContextMenuAction.OPEN_GITHUB_HUB -> openWindow(WindowType.GITHUB_HUB, "GitHub Hub & Releases")
            ContextMenuAction.REFRESH_DESKTOP -> loadInstalledApps()
        }
        closeContextMenu()
    }

    fun openGitHubHub() {
        openWindow(WindowType.GITHUB_HUB, "GitHub Hub & Releases")
    }

    // ==========================================
    // WINDOW MANAGEMENT METHODS
    // ==========================================

    fun openWindow(type: WindowType, title: String? = null, extra: String? = null) {
        val currentList = _windows.value.toMutableList()
        // Check if window already exists
        val existingIndex = currentList.indexOfFirst { it.type == type && (extra == null || it.extraData == extra) }

        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            nextZIndex += 1f
            currentList[existingIndex] = existing.copy(
                isMinimized = false,
                zIndex = nextZIndex
            )
            _windows.value = currentList
            _activeWindowId.value = existing.id
            closeStartMenu()
            return
        }

        nextZIndex += 1f
        val id = UUID.randomUUID().toString()
        val defaultTitle = title ?: when (type) {
            WindowType.TASK_MANAGER -> "Task Manager"
            WindowType.FILE_EXPLORER -> "File Explorer"
            WindowType.QUICK_NOTES -> "Quick Notes"
            WindowType.DISPLAY_SETTINGS -> "Display & Settings"
            WindowType.TERMINAL -> "Terminal Console"
            WindowType.APP_CONTAINER -> "App: $extra"
            WindowType.GITHUB_HUB -> "GitHub Hub & Releases"
        }

        // Offset cascade based on open window count
        val offsetCascade = (currentList.size % 5) * 32f
        val initialX = 140f + offsetCascade
        val initialY = 80f + offsetCascade

        val (initialW, initialH) = when (type) {
            WindowType.TERMINAL -> 680f to 420f
            WindowType.FILE_EXPLORER -> 760f to 480f
            WindowType.TASK_MANAGER -> 620f to 440f
            WindowType.QUICK_NOTES -> 540f to 380f
            WindowType.DISPLAY_SETTINGS -> 700f to 460f
            WindowType.APP_CONTAINER -> 600f to 420f
            WindowType.GITHUB_HUB -> 780f to 500f
        }

        val newWindow = DesktopWindow(
            id = id,
            title = defaultTitle,
            type = type,
            posX = initialX,
            posY = initialY,
            width = initialW,
            height = initialH,
            isMinimized = false,
            isMaximized = false,
            zIndex = nextZIndex,
            extraData = extra
        )

        currentList.add(newWindow)
        _windows.value = currentList
        _activeWindowId.value = id
        closeStartMenu()
    }

    fun closeWindow(windowId: String) {
        _windows.value = _windows.value.filter { it.id != windowId }
        if (_activeWindowId.value == windowId) {
            _activeWindowId.value = _windows.value.maxByOrNull { it.zIndex }?.id
        }
    }

    fun minimizeWindow(windowId: String) {
        _windows.value = _windows.value.map {
            if (it.id == windowId) it.copy(isMinimized = true) else it
        }
        if (_activeWindowId.value == windowId) {
            _activeWindowId.value = _windows.value.filter { !it.isMinimized }.maxByOrNull { it.zIndex }?.id
        }
    }

    fun toggleMinimizeRestore(windowId: String) {
        val window = _windows.value.find { it.id == windowId } ?: return
        if (window.isMinimized) {
            focusWindow(windowId)
        } else if (_activeWindowId.value == windowId) {
            minimizeWindow(windowId)
        } else {
            focusWindow(windowId)
        }
    }

    fun maximizeWindow(windowId: String) {
        nextZIndex += 1f
        _windows.value = _windows.value.map {
            if (it.id == windowId) {
                it.copy(isMaximized = !it.isMaximized, isMinimized = false, zIndex = nextZIndex)
            } else it
        }
        _activeWindowId.value = windowId
    }

    fun focusWindow(windowId: String) {
        nextZIndex += 1f
        _windows.value = _windows.value.map {
            if (it.id == windowId) it.copy(zIndex = nextZIndex, isMinimized = false) else it
        }
        _activeWindowId.value = windowId
        if (_isStartMenuOpen.value) {
            closeStartMenu()
        }
    }

    fun moveWindow(windowId: String, deltaX: Float, deltaY: Float) {
        _windows.value = _windows.value.map {
            if (it.id == windowId && !it.isMaximized) {
                val newX = (it.posX + deltaX).coerceAtLeast(0f)
                val newY = (it.posY + deltaY).coerceAtLeast(0f)
                it.copy(posX = newX, posY = newY)
            } else it
        }
    }

    fun resizeWindow(windowId: String, direction: ResizeDirection, deltaX: Float, deltaY: Float) {
        _windows.value = _windows.value.map { win ->
            if (win.id == windowId && !win.isMaximized) {
                var newX = win.posX
                var newY = win.posY
                var newW = win.width
                var newH = win.height

                val minWidth = 340f
                val minHeight = 220f

                when (direction) {
                    ResizeDirection.EAST -> {
                        newW = (win.width + deltaX).coerceAtLeast(minWidth)
                    }
                    ResizeDirection.WEST -> {
                        val potentialW = win.width - deltaX
                        if (potentialW >= minWidth) {
                            newW = potentialW
                            newX = win.posX + deltaX
                        }
                    }
                    ResizeDirection.SOUTH -> {
                        newH = (win.height + deltaY).coerceAtLeast(minHeight)
                    }
                    ResizeDirection.NORTH -> {
                        val potentialH = win.height - deltaY
                        if (potentialH >= minHeight) {
                            newH = potentialH
                            newY = win.posY + deltaY
                        }
                    }
                    ResizeDirection.SOUTH_EAST -> {
                        newW = (win.width + deltaX).coerceAtLeast(minWidth)
                        newH = (win.height + deltaY).coerceAtLeast(minHeight)
                    }
                    ResizeDirection.SOUTH_WEST -> {
                        val potentialW = win.width - deltaX
                        if (potentialW >= minWidth) {
                            newW = potentialW
                            newX = win.posX + deltaX
                        }
                        newH = (win.height + deltaY).coerceAtLeast(minHeight)
                    }
                    ResizeDirection.NORTH_EAST -> {
                        newW = (win.width + deltaX).coerceAtLeast(minWidth)
                        val potentialH = win.height - deltaY
                        if (potentialH >= minHeight) {
                            newH = potentialH
                            newY = win.posY + deltaY
                        }
                    }
                    ResizeDirection.NORTH_WEST -> {
                        val potentialW = win.width - deltaX
                        if (potentialW >= minWidth) {
                            newW = potentialW
                            newX = win.posX + deltaX
                        }
                        val potentialH = win.height - deltaY
                        if (potentialH >= minHeight) {
                            newH = potentialH
                            newY = win.posY + deltaY
                        }
                    }
                    ResizeDirection.NONE -> {}
                }

                win.copy(posX = newX, posY = newY, width = newW, height = newH)
            } else win
        }
    }

    private fun startClockAndMetricsLoop() {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                val now = Date()
                _currentTime.value = timeFormat.format(now)
                _currentDate.value = dateFormat.format(now)

                // Network
                checkNetwork()

                // RAM & System metrics
                checkSystemMetrics()

                delay(1000)
            }
        }
    }

    private fun checkNetwork() {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = cm?.activeNetwork
        val caps = cm?.getNetworkCapabilities(activeNetwork)
        val status = when {
            caps == null -> "Offline"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            else -> "Connected"
        }
        _networkStatus.value = status
    }

    private fun checkSystemMetrics() {
        try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager?.getMemoryInfo(memInfo)

            val totalMb = memInfo.totalMem / (1024 * 1024)
            val availMb = memInfo.availMem / (1024 * 1024)
            val usedMb = totalMb - availMb
            val usedPercent = if (totalMb > 0) ((usedMb.toDouble() / totalMb) * 100).toInt() else 0
            val uptimeHrs = SystemClock.elapsedRealtime() / (1000 * 60 * 60)

            _systemMetrics.value = SystemMetricInfo(
                totalRamMb = totalMb,
                availableRamMb = availMb,
                usedRamPercent = usedPercent,
                uptimeHours = uptimeHrs
            )
        } catch (_: Exception) {}
    }
}
