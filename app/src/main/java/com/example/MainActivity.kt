package com.example

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.desktop.DesktopWorkspace
import com.example.ui.hardware.MouseRequiredScreen
import com.example.ui.splash.BootSplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Slate950
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Slate950
                ) {
                    val isBootCompleted by viewModel.isBootCompleted.collectAsState()
                    val isMouseActive by viewModel.isMouseActive.collectAsState()
                    val isStartMenuOpen by viewModel.isStartMenuOpen.collectAsState()
                    val contextMenuState by viewModel.contextMenuState.collectAsState()
                    val openWindows by viewModel.windows.collectAsState()
                    val activeWindowId by viewModel.activeWindowId.collectAsState()

                    // TV Back Button Handler
                    BackHandler {
                        when {
                            contextMenuState.isVisible -> {
                                viewModel.closeContextMenu()
                            }
                            isStartMenuOpen -> {
                                viewModel.closeStartMenu()
                            }
                            activeWindowId != null -> {
                                viewModel.closeWindow(activeWindowId!!)
                            }
                            openWindows.isNotEmpty() -> {
                                viewModel.closeWindow(openWindows.last().id)
                            }
                            else -> {
                                // Home launcher does not finish on back press
                            }
                        }
                    }

                    // Main State Transition
                    Crossfade(
                        targetState = Triple(isBootCompleted, isMouseActive, Unit),
                        animationSpec = tween(400),
                        label = "LauncherFlowCrossfade"
                    ) { (bootDone, mouseActive, _) ->
                        when {
                            // 1. Boot Animation Sequence
                            !bootDone -> {
                                BootSplashScreen(
                                    onBootComplete = { viewModel.completeBoot() }
                                )
                            }

                            // 2. Hardware Mouse Restriction Screen
                            !mouseActive -> {
                                MouseRequiredScreen(
                                    onBypassCheck = { viewModel.bypassMouseCheck() },
                                    onRefreshScan = { viewModel.mouseDetector.scanDevices() }
                                )
                            }

                            // 3. Full Desktop Workspace
                            else -> {
                                DesktopWorkspace(
                                    viewModel = viewModel,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh installed apps and connected pointer devices when returning
        viewModel.loadInstalledApps()
        viewModel.mouseDetector.scanDevices()
    }
}
