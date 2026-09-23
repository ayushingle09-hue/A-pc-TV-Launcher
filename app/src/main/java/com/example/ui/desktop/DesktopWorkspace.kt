package com.example.ui.desktop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ContextMenuState
import com.example.model.WindowType
import com.example.ui.taskbar.StartMenuView
import com.example.ui.taskbar.TaskbarView
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBright
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.windowing.FileExplorerContent
import com.example.ui.windowing.GitHubHubContent
import com.example.ui.windowing.QuickNotesContent
import com.example.ui.windowing.SettingsContent
import com.example.ui.windowing.SimulatedWindowView
import com.example.ui.windowing.TaskManagerContent
import com.example.ui.windowing.TerminalContent
import com.example.viewmodel.LauncherViewModel

@Composable
fun DesktopWorkspace(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val wallpaper by viewModel.currentWallpaper.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isStartMenuOpen by viewModel.isStartMenuOpen.collectAsState()
    val contextMenuState by viewModel.contextMenuState.collectAsState()
    val openWindows by viewModel.windows.collectAsState()
    val activeWindowId by viewModel.activeWindowId.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val networkStatus by viewModel.networkStatus.collectAsState()
    val systemMetrics by viewModel.systemMetrics.collectAsState()
    val notepadText by viewModel.notepadText.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("desktop_workspace")
    ) {
        // ==========================================
        // DESKTOP WALLPAPER & CANVAS
        // ==========================================
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Gradient fill
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(wallpaper.startColor, wallpaper.centerColor, wallpaper.endColor),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height)
                )
            )

            // High-tech subtle grid lines
            val step = 72.dp.toPx()
            val gridColor = wallpaper.accentGlow.copy(alpha = 0.04f)
            var x = 0f
            while (x < size.width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                x += step
            }
            var y = 0f
            while (y < size.height) {
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                y += step
            }

            // Radial soft glow in center
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(wallpaper.accentGlow.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.width / 2f
                )
            )
        }

        // ==========================================
        // DESKTOP BACKGROUND INTERACTION (Right-Click for Desktop Context Menu)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 52.dp) // Leave space for taskbar
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Press) {
                                val change = event.changes.firstOrNull()
                                if (event.buttons.isSecondaryPressed) {
                                    change?.consume()
                                    val clickPos = change?.position ?: Offset.Zero
                                    viewModel.showContextMenu(
                                        ContextMenuState(
                                            isVisible = true,
                                            position = clickPos,
                                            targetApp = null,
                                            isDesktopContext = true
                                        )
                                    )
                                } else if (change?.pressed == true) {
                                    // Clicking empty desktop closes Start Menu and Context Menu
                                    if (isStartMenuOpen) viewModel.closeStartMenu()
                                    if (contextMenuState.isVisible) viewModel.closeContextMenu()
                                }
                            }
                        }
                    }
                }
        ) {
            // Desktop Header / Quick TV Status Widget
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // GitHub Reliance / Releases Hub Quick Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { viewModel.openGitHubHub() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GitHub Reliance: 0% (Offline-Ready)",
                            color = EmeraldSuccess,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = "Open Hub",
                            tint = CyanBright,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mouse,
                            contentDescription = null,
                            tint = CyanBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mouse Navigation Active",
                            color = Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Desktop App Icon Grid (Left side, Windows desktop style)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 96.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, top = 20.dp, end = 160.dp, bottom = 16.dp),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(installedApps, key = { it.packageName }) { app ->
                    DesktopIconView(
                        app = app,
                        onClick = { viewModel.launchApp(app) },
                        onRightClick = { clickPos ->
                            viewModel.showContextMenu(
                                ContextMenuState(
                                    isVisible = true,
                                    position = clickPos,
                                    targetApp = app,
                                    isDesktopContext = false
                                )
                            )
                        }
                    )
                }
            }

            // ==========================================
            // SIMULATED WINDOWS LAYER
            // ==========================================
            openWindows.forEach { window ->
                val isActive = window.id == activeWindowId
                SimulatedWindowView(
                    window = window,
                    isActive = isActive,
                    onFocus = { viewModel.focusWindow(window.id) },
                    onClose = { viewModel.closeWindow(window.id) },
                    onMinimize = { viewModel.minimizeWindow(window.id) },
                    onMaximize = { viewModel.maximizeWindow(window.id) },
                    onMove = { dx, dy -> viewModel.moveWindow(window.id, dx, dy) },
                    onResize = { dir, dx, dy -> viewModel.resizeWindow(window.id, dir, dx, dy) }
                ) {
                    when (window.type) {
                        WindowType.TASK_MANAGER -> {
                            TaskManagerContent(
                                metrics = systemMetrics,
                                openWindows = openWindows,
                                onFocusWindow = { viewModel.focusWindow(it) },
                                onCloseWindow = { viewModel.closeWindow(it) }
                            )
                        }
                        WindowType.FILE_EXPLORER -> {
                            FileExplorerContent()
                        }
                        WindowType.QUICK_NOTES -> {
                            QuickNotesContent(
                                text = notepadText,
                                onTextChange = { viewModel.updateNotepadText(it) }
                            )
                        }
                        WindowType.DISPLAY_SETTINGS -> {
                            SettingsContent(
                                currentWallpaper = wallpaper,
                                onSelectWallpaper = { viewModel.setWallpaper(it) },
                                onOpenSystemTvSettings = { viewModel.openSystemTvSettings() }
                            )
                        }
                        WindowType.TERMINAL -> {
                            TerminalContent(installedApps = installedApps)
                        }
                        WindowType.APP_CONTAINER -> {
                            val targetApp = installedApps.find { it.packageName == window.extraData }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Slate900)
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.DesktopWindows,
                                        contentDescription = null,
                                        tint = CyanBright,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "${targetApp?.label ?: window.title} (Sandboxed App Container)",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Package: ${window.extraData}\nClick below to launch full-screen or keep sandboxed.",
                                        color = Slate400,
                                        fontSize = 12.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    androidx.compose.material3.Button(
                                        onClick = {
                                            targetApp?.let { viewModel.launchApp(it) }
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Launch Full Screen")
                                    }
                                }
                            }
                        }
                        WindowType.GITHUB_HUB -> {
                            GitHubHubContent()
                        }
                    }
                }
            }
        }

        // ==========================================
        // FLOATING START MENU FLYOUT
        // ==========================================
        StartMenuView(
            isOpen = isStartMenuOpen,
            onDismiss = { viewModel.closeStartMenu() },
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            apps = filteredApps,
            onLaunchApp = { viewModel.launchApp(it) },
            onOpenTaskManager = { viewModel.openWindow(WindowType.TASK_MANAGER) },
            onOpenFileExplorer = { viewModel.openWindow(WindowType.FILE_EXPLORER) },
            onOpenNotes = { viewModel.openWindow(WindowType.QUICK_NOTES) },
            onOpenSettings = { viewModel.openWindow(WindowType.DISPLAY_SETTINGS) },
            onOpenTerminal = { viewModel.openWindow(WindowType.TERMINAL) },
            onOpenGitHubHub = { viewModel.openGitHubHub() },
            onOpenTvSettings = { viewModel.openSystemTvSettings() },
            onReloadApps = { viewModel.loadInstalledApps() }
        )

        // ==========================================
        // FLOATING RIGHT-CLICK CONTEXT MENU
        // ==========================================
        DesktopContextMenu(
            menuState = contextMenuState,
            onDismiss = { viewModel.closeContextMenu() },
            onAction = { action, app ->
                viewModel.handleContextMenuAction(action, app)
            }
        )

        // ==========================================
        // WINDOWS-STYLE TASKBAR AT BOTTOM
        // ==========================================
        TaskbarView(
            isStartMenuOpen = isStartMenuOpen,
            onToggleStartMenu = { viewModel.toggleStartMenu() },
            openWindows = openWindows,
            activeWindowId = activeWindowId,
            onWindowTabClick = { viewModel.toggleMinimizeRestore(it) },
            onCloseWindow = { viewModel.closeWindow(it) },
            currentTime = currentTime,
            currentDate = currentDate,
            networkStatus = networkStatus,
            onOpenTaskManager = { viewModel.openWindow(WindowType.TASK_MANAGER) },
            onOpenFileExplorer = { viewModel.openWindow(WindowType.FILE_EXPLORER) },
            onOpenGitHubHub = { viewModel.openGitHubHub() },
            onOpenSettings = { viewModel.openWindow(WindowType.DISPLAY_SETTINGS) },
            onShowDesktop = {
                // Minimize all open windows
                openWindows.forEach { viewModel.minimizeWindow(it.id) }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
