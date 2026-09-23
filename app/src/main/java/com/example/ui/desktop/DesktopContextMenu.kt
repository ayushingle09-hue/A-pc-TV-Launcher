package com.example.ui.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.ContextMenuAction
import com.example.model.ContextMenuState
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBright
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import kotlin.math.roundToInt

/**
 * Windows-style Right-Click Floating Context Menu.
 */
@Composable
fun DesktopContextMenu(
    menuState: ContextMenuState,
    onDismiss: () -> Unit,
    onAction: (ContextMenuAction, AppInfo?) -> Unit
) {
    if (!menuState.isVisible) return

    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val screenWidthPx = with(density) { config.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { config.screenHeightDp.dp.toPx() }

    // Clamp menu to screen bounds
    val menuWidthPx = with(density) { 240.dp.toPx() }
    val menuHeightPx = with(density) { 280.dp.toPx() }

    val posX = if (menuState.position.x + menuWidthPx > screenWidthPx) {
        (screenWidthPx - menuWidthPx - 16).coerceAtLeast(8f)
    } else {
        menuState.position.x
    }

    val posY = if (menuState.position.y + menuHeightPx > screenHeightPx) {
        (screenHeightPx - menuHeightPx - 56).coerceAtLeast(8f) // 56dp for taskbar
    } else {
        menuState.position.y
    }

    // Dismiss layer on background click
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) {
                onDismiss()
            }
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(posX.roundToInt(), posY.roundToInt()) }
                .widthIn(min = 220.dp, max = 260.dp)
                .shadow(16.dp, RoundedCornerShape(12.dp))
                .background(Slate900.copy(alpha = 0.98f), RoundedCornerShape(12.dp))
                .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                .padding(vertical = 6.dp)
                .testTag("desktop_context_menu")
        ) {
            Column {
                if (menuState.targetApp != null) {
                    // Context Menu for App Icon
                    val app = menuState.targetApp

                    Text(
                        text = app.label,
                        color = CyanBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        maxLines = 1
                    )

                    ContextMenuItem(
                        icon = Icons.Default.Launch,
                        label = "Open Application",
                        onClick = { onAction(ContextMenuAction.LAUNCH, app) }
                    )

                    ContextMenuItem(
                        icon = Icons.Default.OpenInNew,
                        label = "Open in Window (Simulated)",
                        onClick = { onAction(ContextMenuAction.OPEN_SIMULATED_WINDOW, app) }
                    )

                    ContextMenuItem(
                        icon = Icons.Default.PushPin,
                        label = if (app.isPinned) "Unpin from Taskbar" else "Pin to Taskbar",
                        onClick = { onAction(ContextMenuAction.TOGGLE_PIN, app) }
                    )

                    HorizontalDivider(color = Slate800, modifier = Modifier.padding(vertical = 4.dp))

                    ContextMenuItem(
                        icon = Icons.Default.Info,
                        label = "App Information",
                        onClick = { onAction(ContextMenuAction.APP_INFO, app) }
                    )

                    if (!app.isSystemApp) {
                        ContextMenuItem(
                            icon = Icons.Default.Delete,
                            label = "Uninstall",
                            tint = RoseDanger,
                            onClick = { onAction(ContextMenuAction.UNINSTALL, app) }
                        )
                    }
                } else {
                    // Context Menu for Desktop Background
                    Text(
                        text = "Desktop Actions",
                        color = Slate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )

                    ContextMenuItem(
                        icon = Icons.Default.Refresh,
                        label = "Refresh Apps",
                        onClick = { onAction(ContextMenuAction.REFRESH_DESKTOP, null) }
                    )

                    ContextMenuItem(
                        icon = Icons.Default.Folder,
                        label = "File Explorer",
                        onClick = { onAction(ContextMenuAction.OPEN_FILE_EXPLORER, null) }
                    )

                    ContextMenuItem(
                        icon = Icons.Default.Monitor,
                        label = "Task Manager",
                        onClick = { onAction(ContextMenuAction.OPEN_TASK_MANAGER, null) }
                    )

                    ContextMenuItem(
                        icon = Icons.Default.Terminal,
                        label = "Open Terminal Console",
                        onClick = { onAction(ContextMenuAction.OPEN_TERMINAL, null) }
                    )

                    ContextMenuItem(
                        icon = Icons.Default.Note,
                        label = "Quick Notes",
                        onClick = { onAction(ContextMenuAction.OPEN_NOTES, null) }
                    )

                    ContextMenuItem(
                        icon = Icons.Default.CloudDownload,
                        label = "GitHub Releases & Audit",
                        onClick = { onAction(ContextMenuAction.OPEN_GITHUB_HUB, null) }
                    )

                    HorizontalDivider(color = Slate800, modifier = Modifier.padding(vertical = 4.dp))

                    ContextMenuItem(
                        icon = Icons.Default.Settings,
                        label = "Display & Wallpapers",
                        onClick = { onAction(ContextMenuAction.CHANGE_WALLPAPER, null) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContextMenuItem(
    icon: ImageVector,
    label: String,
    tint: Color = Slate300,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxSize()
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
            .background(if (isHovered) Slate800 else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isHovered) CyanAccent else tint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = if (isHovered) Color.White else Slate300,
            fontSize = 13.sp,
            fontWeight = if (isHovered) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
