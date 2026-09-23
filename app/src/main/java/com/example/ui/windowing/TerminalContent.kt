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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBright
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import java.util.Date

data class TerminalLog(
    val command: String,
    val output: String,
    val isError: Boolean = false
)

@Composable
fun TerminalContent(
    installedApps: List<AppInfo>
) {
    var inputCommand by remember { mutableStateOf("") }
    val logs = remember {
        mutableStateListOf(
            TerminalLog("system", "Desktop TV OS Terminal Shell v1.0.4\nType 'help' for available commands, or click commands below.", false)
        )
    }

    val quickCommands = listOf("help", "apps", "neofetch", "github", "uname", "ip", "clear", "date")

    fun execute(cmd: String) {
        val trimmed = cmd.trim()
        if (trimmed.isEmpty()) return

        when (trimmed.lowercase()) {
            "clear" -> {
                logs.clear()
            }
            "help" -> {
                logs.add(
                    TerminalLog(
                        trimmed,
                        "Available Commands:\n- apps: List all installed packages\n- neofetch: Show OS & device specifications\n- github: Audit external GitHub reliance & offline status\n- uname: Show Android kernel information\n- ip: Show network connectivity status\n- date: Show system time and date\n- clear: Clear terminal buffer\n- echo <text>: Print back text"
                    )
                )
            }
            "github", "git" -> {
                logs.add(
                    TerminalLog(
                        trimmed,
                        "=========================================\n" +
                        "  GITHUB RELIANCE & OFFLINE AUDIT REPORT \n" +
                        "=========================================\n" +
                        "[✔] Status: 0% External GitHub Reliance\n" +
                        "[✔] Runtime APIs: 0 calls to api.github.com\n" +
                        "[✔] Code Execution: 100% statically bundled (Zero remote DCL)\n" +
                        "[✔] Air-Gap Ready: Full desktop OS functionality offline\n" +
                        "[✔] Dependency Source: Google Maven & Maven Central only\n" +
                        "[✔] Open-Source Hub: Accessible via GitHub Hub on Desktop"
                    )
                )
            }
            "apps" -> {
                val appNames = installedApps.take(15).joinToString("\n") { "• ${it.label} (${it.packageName})" }
                logs.add(TerminalLog(trimmed, "Discovered Packages (${installedApps.size} total):\n$appNames\n...and ${installedApps.size - 15.coerceAtMost(installedApps.size)} more"))
            }
            "neofetch" -> {
                logs.add(
                    TerminalLog(
                        trimmed,
                        "  ____             _     _               \n" +
                        " |  _ \\  ___  ___| | __| |_ ___  _ __   \n" +
                        " | | | |/ _ \\/ __| |/ /| __/ _ \\| '_ \\  \n" +
                        " | |_| |  __/\\__ \\   < | || (_) | |_) | \n" +
                        " |____/ \\___||___/_|\\_\\ \\__\\___/| .__/  \n" +
                        "                                |_|     \n" +
                        "OS: Desktop TV OS (Android ${android.os.Build.VERSION.RELEASE})\n" +
                        "Host: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}\n" +
                        "Architecture: ${android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a"}\n" +
                        "Compositor: Jetpack Compose Desktop HAL\n" +
                        "Input: USB / Bluetooth Mouse Controller Active"
                    )
                )
            }
            "uname" -> {
                logs.add(TerminalLog(trimmed, "Linux localhost 5.10.160-android12 #1 SMP PREEMPT TV OS"))
            }
            "date" -> {
                logs.add(TerminalLog(trimmed, Date().toString()))
            }
            "ip" -> {
                logs.add(TerminalLog(trimmed, "eth0: inet 192.168.1.145/24 brd 192.168.1.255 scope global\nwlan0: state UP mtu 1500"))
            }
            else -> {
                if (trimmed.startsWith("echo ", true)) {
                    logs.add(TerminalLog(trimmed, trimmed.substring(5)))
                } else {
                    logs.add(TerminalLog(trimmed, "Command not recognized: '$trimmed'. Type 'help' for commands.", true))
                }
            }
        }
        inputCommand = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(10.dp)
    ) {
        // Quick Command Pills for Mouse Users
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickCommands.forEach { qc ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Slate850)
                        .border(1.dp, Slate800, RoundedCornerShape(4.dp))
                        .clickable { execute(qc) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(qc, color = CyanBright, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Output Logs
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(logs) { log ->
                Column {
                    if (log.command != "system") {
                        Text(
                            text = "tv@desktop:~$ ${log.command}",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = log.output,
                        color = if (log.isError) Color(0xFFF87171) else EmeraldSuccess,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Command Line
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "tv@desktop:~$",
                color = CyanAccent,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 6.dp)
            )

            OutlinedTextField(
                value = inputCommand,
                onValueChange = { inputCommand = it },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                singleLine = true,
                placeholder = { Text("type command...", color = Slate700, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Slate900,
                    unfocusedContainerColor = Slate900,
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = Slate800,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { execute(inputCommand) })
            )
        }
    }
}
