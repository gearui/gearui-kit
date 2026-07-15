package com.gearui.runtime

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Host-provided runtime insets fallback bridge.
 *
 * Kuikly runtime safeAreaInsets may be incomplete on some Android hosts.
 * App host can push measured insets here, and Gear runtime will merge by max().
 */
object RuntimeInsetsBridge {
    var safeAreaOverride: SafeArea by mutableStateOf(SafeArea())
        private set

    var keyboardHeightOverride: Dp by mutableStateOf(0.dp)
        private set

    fun updateSafeArea(top: Dp, bottom: Dp, left: Dp, right: Dp) {
        safeAreaOverride = SafeArea(
            top = top,
            bottom = bottom,
            left = left,
            right = right
        )
    }

    /** Keyboard/IME inset is deliberately separate from the system safe area. */
    fun updateKeyboardHeight(height: Dp) {
        keyboardHeightOverride = if (height > 0.dp) height else 0.dp
    }

    fun clear() {
        safeAreaOverride = SafeArea()
        keyboardHeightOverride = 0.dp
    }

    private fun max(a: Dp, b: Dp): Dp = if (a.value >= b.value) a else b

    fun mergeWith(base: SafeArea): SafeArea {
        val override = safeAreaOverride
        return SafeArea(
            top = max(base.top, override.top),
            bottom = max(base.bottom, override.bottom),
            left = max(base.left, override.left),
            right = max(base.right, override.right)
        )
    }
}
