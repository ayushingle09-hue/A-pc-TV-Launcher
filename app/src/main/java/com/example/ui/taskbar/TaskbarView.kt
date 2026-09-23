package com.example.ui.taskbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DesktopWindow
import com.example.model.WindowType
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.TaskbarBackground

/**
 * Windows-style Taskbar at the bottom of the TV screen.
 */
@Composable
fun TaskbarView(
    isStartMenuOpen: Boolean,
    onToggleStartMenu: () -> Unit,
    openWindows: List<DesktopWindow>,
    activeWindowId: String?,
    onWindowTabClick: (String) -> Unit,
    onCloseWindow: (String) -> Unit,
    currentTime: String,
    currentDate: String,
    networkStatus: String,
    onOpenTaskManager: () -> Unit,
    onOpenFileExplorer: () -> Unit = onOpenTaskManager,
    onOpenGitHubHub: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onShowDesktop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(12.dp)
            .background(TaskbarBackground)
            .border(width = 1.dp, color = Slate800)
            .testTag("desktop_taskbar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT SECTION: Start Button & Quick Launch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Windows-Style Start Button
                StartButton(
                    isOpen = isStartMenuOpen,
                    onClick = onToggleStartMenu
                )

                // Quick Launch Icons
                QuickLaunchButton(
                    icon = Icons.Default.Folder,
                    label = "Explorer",
                    onClick = onOpenFileExplorer
                )

                QuickLaunchButton(
                    icon = Icons.Default.Monitor,
                    label = "Tasks",
                    onClick = onOpenTaskManager
                )

                QuickLaunchButton(
                    icon = Icons.Default.CloudDownload,
                    label = "GitHub",
                    onClick = onOpenGitHubHub
                )

                QuickLaunchButton(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    onClick = onOpenSettings
                )
            }

            // CENTER SECTION: Running Window Tabs
            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(openWindows, key = { it.id }) { window ->
                    val isActive = window.id == activeWindowId && !window.isMinimized
                    WindowTaskbarTab(
                        window = window,
                        isActive = isActive,
                        onClick = { onWindowTabClick(window.id) },
                        onClose = { onCloseWindow(window.id) }
                    )
                }
            }

            // RIGHT SECTION: System Tray (Network, Audio, Clock, Show Desktop)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Network Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (networkStatus == "Offline") Icons.Default.WifiOff else Icons.Default.NetworkWifi,
                        contentDescription = networkStatus,
                        tint = if (networkStatus == "Offline") Slate400 else CyanBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = networkStatus,
                        color = Slate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Audio Volume Indicator
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Audio",
                    tint = Slate400,
                    modifier = Modifier.size(18.dp)
                )

                // Date & Time Display
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onOpenSettings() }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = currentTime,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentDate,
                        color = Slate400,
                        fontSize = 10.sp
                    )
                }

                // Show Desktop Peek Button (Right Edge)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(12.dp)
                        .background(Slate800)
                        .border(1.dp, Slate700)
                        .clickable(onClick = onShowDesktop)
                )
            }
        }
    }
}

@Composable
private fun StartButton(
    isOpen: Boolean,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isOpen) CyanAccent.copy(alpha = 0.3f)
                else if (isHovered) Slate800
                else Slate900
            )
            .border(
                1.dp,
                if (isOpen || isHovered) CyanAccent else Slate700,
                RoundedCornerShape(8.dp)
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> isHovered = true
                            PointerEventType.Exit -> isHovered = false
                        }
                    }
                }
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.GridView,
            contentDescription = "Start Menu",
            tint = if (isOpen || isHovered) CyanBright else Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Start",
            color = if (isOpen || isHovered) Color.White else Slate400,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuickLaunchButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isHovered) Slate800 else Color.Transparent)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> isHovered = true
                            PointerEventType.Exit -> isHovered = false
                        }
                    }
                }
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isHovered) CyanBright else Slate400,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun WindowTaskbarTab(
    window: DesktopWindow,
    isActive: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    val icon = when (window.type) {
        WindowType.TASK_MANAGER -> Icons.Default.Monitor
        WindowType.FILE_EXPLORER -> Icons.Default.Folder
        WindowType.QUICK_NOTES -> Icons.Default.Note
        WindowType.DISPLAY_SETTINGS -> Icons.Default.Settings
        WindowType.TERMINAL -> Icons.Default.Terminal
        WindowType.APP_CONTAINER -> Icons.Default.Computer
        WindowType.GITHUB_HUB -> Icons.Default.CloudDownload
    }

    Row(
        modifier = Modifier
            .height(34.dp)
            .widthIn(min = 120.dp, max = 180.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isActive) Slate800
                else if (isHovered) Slate850
                else Slate900.copy(alpha = 0.6f)
            )
            .border(
                1.dp,
                if (isActive) CyanAccent else if (isHovered) Slate700 else Slate800,
                RoundedCornerShape(6.dp)
            )
            .pointerInput(window.id) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> isHovered = true
                            PointerEventType.Exit -> isHovered = false
                        }
                    }
                }
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) CyanBright else Slate400,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = window.title,
            color = if (isActive) Color.White else Slate400,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // Close button on tab hover
        if (isHovered || isActive) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Window",
                    tint = Slate400,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
