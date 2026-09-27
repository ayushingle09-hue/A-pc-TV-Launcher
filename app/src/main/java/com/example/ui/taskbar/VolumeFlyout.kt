package com.example.ui.taskbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBright
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900

/**
 * Windows 11 style Volume and Sound Control Flyout above the taskbar.
 */
@Composable
fun VolumeFlyout(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    currentVolume: Int,
    maxVolume: Int,
    isMuted: Boolean,
    onVolumeChange: (Int) -> Unit,
    onToggleMute: () -> Unit,
    onOpenSoundSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val safeMax = maxVolume.coerceAtLeast(1)
    val volumePercent = (currentVolume.coerceIn(0, safeMax) * 100 / safeMax)

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .width(320.dp)
                .shadow(24.dp, RoundedCornerShape(14.dp))
                .testTag("volume_flyout"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Slate850),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(CyanAccent.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    isMuted || currentVolume == 0 -> Icons.AutoMirrored.Filled.VolumeMute
                                    volumePercent < 50 -> Icons.AutoMirrored.Filled.VolumeDown
                                    else -> Icons.AutoMirrored.Filled.VolumeUp
                                },
                                contentDescription = null,
                                tint = if (isMuted) RoseDanger else CyanBright,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sound & Volume",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = if (isMuted) "MUTED" else "$volumePercent%",
                        color = if (isMuted) RoseDanger else CyanBright,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Volume Slider Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate900, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Mute / Unmute Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) RoseDanger.copy(alpha = 0.2f) else Slate800)
                            .border(1.dp, if (isMuted) RoseDanger else Slate700, CircleShape)
                            .clickable(onClick = onToggleMute),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = if (isMuted) "Unmute" else "Mute",
                            tint = if (isMuted) RoseDanger else CyanBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Slider
                    Slider(
                        value = if (isMuted) 0f else currentVolume.coerceIn(0, safeMax).toFloat(),
                        onValueChange = { newVal ->
                            onVolumeChange(newVal.toInt())
                        },
                        valueRange = 0f..safeMax.toFloat(),
                        steps = (safeMax - 1).coerceAtLeast(0),
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = CyanBright,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = Slate700
                        )
                    )
                }

                // Open TV Sound Settings Button
                Button(
                    onClick = {
                        onOpenSoundSettings()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Slate800,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = CyanBright
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Open TV Sound Settings",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
