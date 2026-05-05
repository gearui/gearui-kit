package com.gearui.runtime

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.unit.Dp

/**
 * Host-provided runtime insets fallback bridge.
 *
 * Kuikly runtime safeAreaInsets may be incomplete on some Android hosts.
 * App host can push measured insets here, and Gear runtime will merge by max().
 */
object RuntimeInsetsBridge {
    var safeAreaOverride: SafeArea by mutableStateOf(SafeArea())
        private set

    fun updateSafeArea(top: Dp, bottom: Dp, left: Dp, right: Dp) {
        safeAreaOverride = SafeArea(
            top = top,
            bottom = bottom,
            left = left,
            right = right
        )
    }

    fun clear() {
        safeAreaOverride = SafeArea()
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
