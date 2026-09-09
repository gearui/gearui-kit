package com.gearui.foundation.keyboard

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.input.pointer.PointerEventPass
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

            fun dismiss() {
                runCatching { focusManager.clearFocus(force = true) }
                keyboardController?.hide()
            }

            awaitEachGesture {
                // 🔴 This container covers the whole app, so the decision has
                // to be made on the Final pass.
                //
                // It used to be `awaitFirstDown(requireUnconsumed = false)` on
                // the default Main pass, which ignores whether a child consumed
                // the event — so **tapping a text field also dismissed the
                // keyboard**. The field took focus, the keyboard came up, and
                // on lift this ran clearFocus + hide: it looked like tapping an
                // input closed the keyboard. Long press had the same problem:
                // any lift under the drag threshold counted as a tap, so the
                // system paste menu was cut off as it appeared.
                //
                // Initial is used only to start tracking the gesture — it
                // decides nothing and consumes nothing. Final runs after every
                // child has had the event, which is the only point where
                // change.isConsumed can be trusted.
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)

                var totalDrag = 0f
                var didDismissForScroll = false

                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Final)
                    val change = event.changes.firstOrNull() ?: break

                    if (!change.pressed) {
                        // A lift consumed by a child means the tap landed on
                        // something interactive — a field, a button, a link, a
                        // long-press menu — not on empty space, so the keyboard
                        // stays.
                        if (!didDismissForScroll &&
                            !change.isConsumed &&
                            totalDrag <= dragThreshold &&
                            mode.dismissOnTap
                        ) {
                            dismiss()
                        }
                        break
                    }

                    val delta = change.positionChange()
                    totalDrag += abs(delta.x) + abs(delta.y)
                    // A drag ignores consumption: a list scroll is consumed by
                    // LazyColumn by definition, and "scrolling dismisses the
                    // keyboard" is exactly the behaviour wanted.
                    if (!didDismissForScroll && totalDrag > dragThreshold && mode.dismissOnScroll) {
                        dismiss()
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

