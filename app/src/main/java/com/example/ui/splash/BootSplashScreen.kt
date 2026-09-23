package com.example.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Premium cinematic boot loading animation for the Desktop TV OS experience.
 */
@Composable
fun BootSplashScreen(
    onBootComplete: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var logIndex by remember { mutableIntStateOf(0) }
    val fadeAlpha = remember { Animatable(1f) }

    val bootLogs = listOf(
        "Booting Desktop TV OS Kernel v5.19...",
        "Initializing Leanback Compositor HAL...",
        "Scanning USB & Bluetooth Pointer Controllers...",
        "Hardware Mouse Subsystem Online.",
        "Mounting Desktop Windowing Manager...",
        "Indexing Installed TV & Mobile Applications...",
        "Loading Desktop Workspace Environment..."
    )

    // Infinite rotating animation for orbital rings
    val infiniteTransition = rememberInfiniteTransition(label = "BootRingRotation")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        val duration = 2800L
        while (System.currentTimeMillis() - startTime < duration) {
            val elapsed = System.currentTimeMillis() - startTime
            progress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
            logIndex = ((elapsed.toFloat() / duration) * (bootLogs.size - 1)).toInt().coerceIn(0, bootLogs.size - 1)
            delay(40)
        }
        progress = 1f
        delay(200)
        fadeAlpha.animateTo(0f, animationSpec = tween(400))
        onBootComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .alpha(fadeAlpha.value)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onBootComplete()
            },
        contentAlignment = Alignment.Center
    ) {
        // Futuristic background grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 60.dp.toPx()
            val gridColor = Color(0x0C38BDF8)
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
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // High-Tech Orbital Rings Loader Animation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .scale(pulseScale)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val outerRadius = size.width / 2.2f
                    val middleRadius = size.width / 2.8f
                    val innerRadius = size.width / 3.8f

                    // Outer glowing segmented ring
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(CyanGlow.copy(alpha = 0.2f), Color.Transparent),
                            center = center,
                            radius = outerRadius * 1.3f
                        ),
                        radius = outerRadius * 1.2f,
                        center = center
                    )

                    drawArc(
                        color = Slate800,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(CyanAccent, CyanGlow, CyanBright, Color.Transparent)
                        ),
                        startAngle = ringRotation,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Counter-rotating inner ring
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Color.Transparent, CyanBright, CyanAccent)
                        ),
                        startAngle = -ringRotation * 1.5f,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Orbiting satellite dots
                    val angleRad = Math.toRadians((ringRotation * 2.0)).toFloat()
                    val satX = center.x + innerRadius * cos(angleRad)
                    val satY = center.y + innerRadius * sin(angleRad)
                    drawCircle(CyanGlow, radius = 5.dp.toPx(), center = Offset(satX, satY))
                }

                // Center Icon
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = null,
                    tint = CyanBright,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // OS Title
            Text(
                text = "DESKTOP TV OS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 4.sp,
                color = Color.White
            )

            Text(
                text = "PRECISION MOUSE WORKSPACE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                color = CyanBright
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(360.dp)
                    .height(6.dp),
                color = CyanAccent,
                trackColor = Slate800,
                strokeCap = StrokeCap.Round,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Terminal boot diagnostics message
            Text(
                text = bootLogs[logIndex],
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Slate400
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Skip button
            OutlinedButton(
                onClick = onBootComplete,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate400),
                modifier = Modifier.alpha(0.8f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mouse,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = CyanBright
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Click to Skip Boot Animation", fontSize = 12.sp)
                }
            }
        }
    }
}
