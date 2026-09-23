package com.example.ui.desktop

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.PushPin
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.GlassSurfaceHover
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

/**
 * Desktop Application Icon with Advanced Mouse Hover scaling and Secondary-Click (Right-Click) handling.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DesktopIconView(
    app: AppInfo,
    onClick: () -> Unit,
    onRightClick: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    var rootPosition by remember { mutableStateOf(Offset.Zero) }

    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.08f else 1.0f,
        animationSpec = tween(150),
        label = "iconScale"
    )

    // Convert Android Drawable to Compose ImageBitmap safely
    val imageBitmap = remember(app.icon) {
        app.icon?.let { d ->
            try {
                val w = d.intrinsicWidth.takeIf { it > 0 } ?: 96
                val h = d.intrinsicHeight.takeIf { it > 0 } ?: 96
                val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bmp)
                d.setBounds(0, 0, canvas.width, canvas.height)
                d.draw(canvas)
                bmp.asImageBitmap()
            } catch (_: Exception) {
                null
            }
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                rootPosition = coordinates.positionInRoot()
            }
            .pointerInput(app.packageName) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> isHovered = true
                            PointerEventType.Exit -> isHovered = false
                            PointerEventType.Press -> {
                                val change = event.changes.firstOrNull()
                                if (event.buttons.isSecondaryPressed) {
                                    change?.consume()
                                    val clickPos = change?.position ?: Offset.Zero
                                    onRightClick(rootPosition + clickPos)
                                }
                            }
                        }
                    }
                }
            }
            .combinedClickable(
                onClick = onClick,
                onLongClick = { onRightClick(rootPosition + Offset(40f, 40f)) }
            )
            .scale(scale)
            .padding(4.dp)
            .testTag("desktop_icon_${app.packageName}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(88.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isHovered) GlassSurfaceHover else Color.Transparent)
                .border(
                    width = if (isHovered) 1.5.dp else 0.dp,
                    color = if (isHovered) CyanAccent.copy(alpha = 0.8f) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 8.dp, horizontal = 6.dp)
        ) {
            // App Icon Container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .shadow(
                        elevation = if (isHovered) 10.dp else 2.dp,
                        shape = RoundedCornerShape(14.dp),
                        ambientColor = if (isHovered) CyanGlow else Color.Black,
                        spotColor = if (isHovered) CyanBright else Color.Black
                    )
                    .background(Slate800.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        if (isHovered) CyanBright else Slate700,
                        RoundedCornerShape(14.dp)
                    )
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = app.label,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = app.label,
                        tint = CyanBright,
                        modifier = Modifier.size(36.dp)
                    )
                }

                if (app.isPinned) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .size(14.dp)
                            .background(CyanAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = Color.White,
                            modifier = Modifier.size(9.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // App Label with Desktop Text Drop Shadow
            Text(
                text = app.label,
                color = if (isHovered) CyanBright else Color.White,
                fontSize = 11.sp,
                fontWeight = if (isHovered) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )
        }
    }
}
