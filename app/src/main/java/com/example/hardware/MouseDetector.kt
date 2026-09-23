package com.example.hardware

import android.content.Context
import android.hardware.input.InputManager
import android.os.Handler
import android.os.Looper
import android.view.InputDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ConnectedMouseInfo(
    val id: Int,
    val name: String,
    val isExternal: Boolean,
    val hasRelativeMotion: Boolean
)

/**
 * Manages physical USB / Bluetooth mouse detection and hot-plug event monitoring.
 */
class MouseDetector(private val context: Context) : InputManager.InputDeviceListener {

    private val inputManager = context.getSystemService(Context.INPUT_SERVICE) as? InputManager
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isMouseConnected = MutableStateFlow(false)
    val isMouseConnected: StateFlow<Boolean> = _isMouseConnected.asStateFlow()

    private val _connectedMice = MutableStateFlow<List<ConnectedMouseInfo>>(emptyList())
    val connectedMice: StateFlow<List<ConnectedMouseInfo>> = _connectedMice.asStateFlow()

    private val _isBypassed = MutableStateFlow(false)
    val isBypassed: StateFlow<Boolean> = _isBypassed.asStateFlow()

    fun startListening() {
        inputManager?.registerInputDeviceListener(this, mainHandler)
        scanDevices()
    }

    fun stopListening() {
        inputManager?.unregisterInputDeviceListener(this)
    }

    fun setBypass(bypass: Boolean) {
        _isBypassed.value = bypass
    }

    /**
     * Inspects all currently connected input devices for mouse or touchpad pointers.
     */
    fun scanDevices() {
        val deviceIds = InputDevice.getDeviceIds() ?: IntArray(0)
        val mouseList = mutableListOf<ConnectedMouseInfo>()

        for (id in deviceIds) {
            val device = InputDevice.getDevice(id) ?: continue
            val sources = device.sources

            val hasMouseSource = (sources and InputDevice.SOURCE_MOUSE == InputDevice.SOURCE_MOUSE)
            val hasTouchpadSource = (sources and InputDevice.SOURCE_TOUCHPAD == InputDevice.SOURCE_TOUCHPAD)
            val hasTrackballSource = (sources and InputDevice.SOURCE_TRACKBALL == InputDevice.SOURCE_TRACKBALL)

            if (hasMouseSource || hasTouchpadSource || hasTrackballSource) {
                // If the device has mouse capability and is external or physical
                val info = ConnectedMouseInfo(
                    id = device.id,
                    name = device.name,
                    isExternal = device.isExternal,
                    hasRelativeMotion = hasMouseSource
                )
                mouseList.add(info)
            }
        }

        _connectedMice.value = mouseList
        _isMouseConnected.value = mouseList.isNotEmpty()
    }

    override fun onInputDeviceAdded(deviceId: Int) {
        scanDevices()
    }

    override fun onInputDeviceRemoved(deviceId: Int) {
        scanDevices()
    }

    override fun onInputDeviceChanged(deviceId: Int) {
        scanDevices()
    }
}
