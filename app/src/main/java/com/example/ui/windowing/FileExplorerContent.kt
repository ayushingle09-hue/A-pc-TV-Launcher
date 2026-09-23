package com.example.ui.windowing

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBright
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import java.io.File

data class ExplorerItem(
    val name: String,
    val isDirectory: Boolean,
    val path: String,
    val sizeString: String
)

@Composable
fun FileExplorerContent() {
    var currentPath by remember {
        mutableStateOf(Environment.getExternalStorageDirectory()?.absolutePath ?: "/sdcard")
    }

    // Load directory files safely
    val items = remember(currentPath) {
        try {
            val dir = File(currentPath)
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.map { file ->
                    val sizeStr = if (file.isDirectory) {
                        "${file.list()?.size ?: 0} items"
                    } else {
                        "${(file.length() / 1024).coerceAtLeast(1)} KB"
                    }
                    ExplorerItem(file.name, file.isDirectory, file.absolutePath, sizeStr)
                }?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })) ?: emptyList()
            } else {
                // Default virtual structure if access is restricted
                listOf(
                    ExplorerItem("Downloads", true, "$currentPath/Download", "Folder"),
                    ExplorerItem("Documents", true, "$currentPath/Documents", "Folder"),
                    ExplorerItem("Movies", true, "$currentPath/Movies", "Folder"),
                    ExplorerItem("Pictures", true, "$currentPath/Pictures", "Folder"),
                    ExplorerItem("system_info.txt", false, "$currentPath/system_info.txt", "2 KB")
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(12.dp)
    ) {
        // Toolbar: Back button and path breadcrumb
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Slate850)
                .border(1.dp, Slate800, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val parent = File(currentPath).parent
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (parent != null) Slate800 else Color.Transparent)
                    .clickable(enabled = parent != null) {
                        if (parent != null) currentPath = parent
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Up directory",
                    tint = if (parent != null) CyanBright else Slate700,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = currentPath,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Files Grid
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("This folder is empty", color = Slate400, fontSize = 12.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items, key = { it.path }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (item.isDirectory) {
                                    currentPath = item.path
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Slate850),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val icon = when {
                                item.isDirectory -> Icons.Default.Folder
                                item.name.endsWith(".mp4", true) || item.name.endsWith(".mkv", true) -> Icons.Default.Movie
                                item.name.endsWith(".jpg", true) || item.name.endsWith(".png", true) -> Icons.Default.Image
                                item.name.endsWith(".mp3", true) -> Icons.Default.MusicNote
                                else -> Icons.Default.Description
                            }

                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (item.isDirectory) CyanBright else Slate400,
                                modifier = Modifier.size(32.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.name,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = item.sizeString,
                                color = Slate400,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
