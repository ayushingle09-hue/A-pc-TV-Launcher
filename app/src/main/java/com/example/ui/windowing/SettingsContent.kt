package com.example.ui.windowing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WallpaperPresets
import com.example.model.WallpaperTheme
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBright
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900

@Composable
fun SettingsContent(
    currentWallpaper: WallpaperTheme,
    onSelectWallpaper: (WallpaperTheme) -> Unit,
    onOpenSystemTvSettings: () -> Unit
) {
    val configuration = LocalConfiguration.current
    var mouseSensitivity by remember { mutableFloatStateOf(1.0f) }
    var soundEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Section: Wallpapers
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DisplaySettings, contentDescription = null, tint = CyanBright, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Desktop Wallpaper Theme", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WallpaperPresets.forEach { theme ->
                val isSelected = currentWallpaper.id == theme.id
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(84.dp)
                        .clickable { onSelectWallpaper(theme) },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) CyanAccent else Slate800
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(listOf(theme.startColor, theme.centerColor, theme.endColor)))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = theme.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(18.dp)
                                    .background(CyanAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        HorizontalDivider(color = Slate800)
        Spacer(modifier = Modifier.height(18.dp))

        // Section: TV Display Information
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Tv, contentDescription = null, tint = CyanBright, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("TV Display & Resolution", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate850),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Screen Bounds: ${configuration.screenWidthDp}dp x ${configuration.screenHeightDp}dp", color = Color.White, fontSize = 12.sp)
                Text("Screen Density DPI: ${configuration.densityDpi} DPI", color = Slate400, fontSize = 11.sp)
                Text("Orientation: Landscape (TV Mode)", color = Slate400, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        HorizontalDivider(color = Slate800)
        Spacer(modifier = Modifier.height(18.dp))

        // Section: Mouse Settings
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Mouse, contentDescription = null, tint = CyanBright, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mouse Cursor Sensitivity", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Slider(
            value = mouseSensitivity,
            onValueChange = { mouseSensitivity = it },
            valueRange = 0.5f..2.0f,
            colors = SliderDefaults.colors(
                thumbColor = CyanAccent,
                activeTrackColor = CyanAccent,
                inactiveTrackColor = Slate800
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Sensitivity Factor: ${String.format("%.1f", mouseSensitivity)}x",
            color = Slate400,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onOpenSystemTvSettings,
            colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Android TV System Settings", fontSize = 12.sp)
        }
    }
}
