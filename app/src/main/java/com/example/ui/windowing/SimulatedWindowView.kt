package com.example.ui.windowing

import android.view.PointerIcon as AndroidPointerIcon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.model.DesktopWindow
import com.example.model.ResizeDirection
import com.example.model.WindowType
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBright
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.WindowBackground
import com.example.ui.theme.WindowHeaderBackground
import kotlin.math.roundToInt

/**
 * Draggable, resizable simulated desktop window with Title Bar and system cursor icons.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimulatedWindowView(
    window: DesktopWindow,
    isActive: Boolean,
    onFocus: () -> Unit,
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onMaximize: () -> Unit,
    onMove: (Float, Float) -> Unit,
    onResize: (ResizeDirection, Float, Float) -> Unit,
    content: @Composable () -> Unit
) {
    if (window.isMinimized) return

    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    // Desktop area bounds (leaving room for 52dp taskbar)
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = (configuration.screenHeightDp - 52).dp

    // Pointer cursors using Android system pointer icons
    val horizontalArrowIcon = remember {
        PointerIcon(AndroidPointerIcon.getSystemIcon(context, AndroidPointerIcon.TYPE_HORIZONTAL_DOUBLE_ARROW))
    }
    val verticalArrowIcon = remember {
        PointerIcon(AndroidPointerIcon.getSystemIcon(context, AndroidPointerIcon.TYPE_VERTICAL_DOUBLE_ARROW))
    }
    val diagonalTopLeftIcon = remember {
        PointerIcon(AndroidPointerIcon.getSystemIcon(context, AndroidPointerIcon.TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW))
    }
    val diagonalTopRightIcon = remember {
        PointerIcon(AndroidPointerIcon.getSystemIcon(context, AndroidPointerIcon.TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW))
    }

    val windowIcon = when (window.type) {
        WindowType.TASK_MANAGER -> Icons.Default.Monitor
        WindowType.FILE_EXPLORER -> Icons.Default.Folder
        WindowType.QUICK_NOTES -> Icons.Default.Note
        WindowType.DISPLAY_SETTINGS -> Icons.Default.Settings
        WindowType.TERMINAL -> Icons.Default.Terminal
        WindowType.APP_CONTAINER -> Icons.Default.Web
        WindowType.GITHUB_HUB -> Icons.Default.CloudDownload
    }

    // Layout dimensions depending on maximized state
    val winWidth = if (window.isMaximized) screenWidthDp else with(density) { window.width.toDp() }
    val winHeight = if (window.isMaximized) screenHeightDp else with(density) { window.height.toDp() }
    val offsetX = if (window.isMaximized) 0 else window.posX.roundToInt()
    val offsetY = if (window.isMaximized) 0 else window.posY.roundToInt()

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX, offsetY) }
            .size(winWidth, winHeight)
            .zIndex(window.zIndex)
            .shadow(
                elevation = if (isActive) 20.dp else 8.dp,
                shape = RoundedCornerShape(if (window.isMaximized) 0.dp else 12.dp)
            )
            .background(
                WindowBackground,
                shape = RoundedCornerShape(if (window.isMaximized) 0.dp else 12.dp)
            )
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = if (isActive) CyanAccent.copy(alpha = 0.8f) else Slate800,
                shape = RoundedCornerShape(if (window.isMaximized) 0.dp else 12.dp)
            )
            .pointerInput(window.id) {
                // Focus window when clicked anywhere inside
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.any { it.pressed }) {
                            onFocus()
                        }
                    }
                }
            }
            .testTag("simulated_window_${window.id}")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ==========================================
            // TITLE BAR (Draggable with Mouse Pointer)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(WindowHeaderBackground)
                    .border(
                        width = 1.dp,
                        color = if (isActive) Slate700 else Slate800
                    )
                    // Drag and drop gesture on title bar
                    .pointerInput(window.id, window.isMaximized) {
                        if (!window.isMaximized) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                onMove(dragAmount.x, dragAmount.y)
                            }
                        }
                    }
                    .combinedClickable(
                        onDoubleClick = onMaximize,
                        onClick = onFocus
                    )
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Window Title and App Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = windowIcon,
                        contentDescription = null,
                        tint = if (isActive) CyanBright else Slate400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = window.title,
                        color = if (isActive) Color.White else Slate400,
                        fontSize = 12.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Window Action Buttons (Minimize, Maximize/Restore, Close)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Minimize Button
                    WindowControlButton(
                        icon = Icons.Default.Minimize,
                        contentDescription = "Minimize",
                        hoverTint = CyanBright,
                        onClick = onMinimize
                    )

                    // Maximize / Restore Button
                    WindowControlButton(
                        icon = if (window.isMaximized) Icons.Default.FilterNone else Icons.Default.CropSquare,
                        contentDescription = if (window.isMaximized) "Restore" else "Maximize",
                        hoverTint = CyanBright,
                        onClick = onMaximize
                    )

                    // Close Button
                    WindowControlButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Close",
                        hoverTint = RoseDanger,
                        onClick = onClose
                    )
                }
            }

            // Window Body Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }
        }

        // ==========================================
        // RESIZE HANDLES (With Custom Pointer Icons)
        // ==========================================
        if (!window.isMaximized) {
            val handleThickness = 8.dp

            // East (Right Edge)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(handleThickness)
                    .align(Alignment.CenterEnd)
                    .pointerHoverIcon(horizontalArrowIcon)
                    .pointerInput(window.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(ResizeDirection.EAST, dragAmount.x, dragAmount.y)
                        }
                    }
            )

            // West (Left Edge)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(handleThickness)
                    .align(Alignment.CenterStart)
                    .pointerHoverIcon(horizontalArrowIcon)
                    .pointerInput(window.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(ResizeDirection.WEST, dragAmount.x, dragAmount.y)
                        }
                    }
            )

            // North (Top Edge)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(handleThickness)
                    .align(Alignment.TopCenter)
                    .pointerHoverIcon(verticalArrowIcon)
                    .pointerInput(window.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(ResizeDirection.NORTH, dragAmount.x, dragAmount.y)
                        }
                    }
            )

            // South (Bottom Edge)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(handleThickness)
                    .align(Alignment.BottomCenter)
                    .pointerHoverIcon(verticalArrowIcon)
                    .pointerInput(window.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(ResizeDirection.SOUTH, dragAmount.x, dragAmount.y)
                        }
                    }
            )

            // South-East Corner
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomEnd)
                    .pointerHoverIcon(diagonalTopLeftIcon)
                    .pointerInput(window.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(ResizeDirection.SOUTH_EAST, dragAmount.x, dragAmount.y)
                        }
                    }
            )

            // South-West Corner
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomStart)
                    .pointerHoverIcon(diagonalTopRightIcon)
                    .pointerInput(window.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(ResizeDirection.SOUTH_WEST, dragAmount.x, dragAmount.y)
                        }
                    }
            )

            // North-East Corner
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .pointerHoverIcon(diagonalTopRightIcon)
                    .pointerInput(window.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(ResizeDirection.NORTH_EAST, dragAmount.x, dragAmount.y)
                        }
                    }
            )

            // North-West Corner
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopStart)
                    .pointerHoverIcon(diagonalTopLeftIcon)
                    .pointerInput(window.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(ResizeDirection.NORTH_WEST, dragAmount.x, dragAmount.y)
                        }
                    }
            )
        }
    }
}

@Composable
private fun WindowControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    hoverTint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Slate800)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Slate400,
            modifier = Modifier.size(12.dp)
        )
    }
}
