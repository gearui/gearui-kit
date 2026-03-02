package com.gearui.foundation.keyboard

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.compose.ui.platform.LocalSoftwareKeyboardController
import kotlin.math.abs

enum class KeyboardDismissMode {
    None,
    OnTap,
    OnScroll,
    OnTapOrScroll,
}

private val KeyboardDismissMode.dismissOnTap: Boolean
    get() = this == KeyboardDismissMode.OnTap || this == KeyboardDismissMode.OnTapOrScroll

private val KeyboardDismissMode.dismissOnScroll: Boolean
    get() = this == KeyboardDismissMode.OnScroll || this == KeyboardDismissMode.OnTapOrScroll

@Composable
internal fun KeyboardDismissContainer(
    mode: KeyboardDismissMode,
    content: @Composable () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val gestureModifier = if (mode == KeyboardDismissMode.None) {
        Modifier
    } else {
        Modifier.pointerInput(mode) {
            val dragThreshold = 10f

            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)

                var totalDrag = 0f
                var didDismissForScroll = false

                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull() ?: break

                    if (!change.pressed) {
                        if (!didDismissForScroll && totalDrag <= dragThreshold && mode.dismissOnTap) {
                            runCatching { focusManager.clearFocus(force = true) }
                            keyboardController?.hide()
                        }
                        break
                    }

                    val delta = change.positionChange()
                    totalDrag += abs(delta.x) + abs(delta.y)
                    if (!didDismissForScroll && totalDrag > dragThreshold && mode.dismissOnScroll) {
                        runCatching { focusManager.clearFocus(force = true) }
                        keyboardController?.hide()
                        didDismissForScroll = true
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(gestureModifier)
    ) {
        content()
    }
}

