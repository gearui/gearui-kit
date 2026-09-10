package com.gearui.foundation.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
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
    val exemptions = remember { KeyboardDismissExemptions() }

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
                // Initial runs top-down, so this resets before any child claims.
                exemptions.beginGesture()
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
                //
                // 🔴 Consumption cannot tell an input from a button here.
                //
                // Judging the lift kept the keyboard up for anything interactive,
                // including the login button. Judging the down looked promising —
                // a text field consumes it to place the caret — but measured on
                // device this port's buttons and list rows consume the down too,
                // so it marks them as inputs as well.
                //
                // Comparing coordinates does not work either: boundsInRoot and the
                // pointer position are not in the same space here, and the test
                // came out true everywhere.
                //
                // So an input region declares itself, in this same gesture, through
                // keyboardDismissExempt. Initial runs top-down, so the reset above
                // lands before any child claims, and Final below runs after them.
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)

                var totalDrag = 0f
                var didDismissForScroll = false

                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Final)
                    val change = event.changes.firstOrNull() ?: break

                    if (!change.pressed) {
                        // Keep the keyboard for a tap that landed on an input, or
                        // inside a region a caller marked with keyboardDismissExempt
                        // — a chat composer's send button sits outside the field but
                        // belongs to typing. Everything else dismisses.
                        if (!didDismissForScroll &&
                            !exemptions.isClaimed() &&
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

    CompositionLocalProvider(LocalKeyboardDismissExemptions provides exemptions) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(gestureModifier)
        ) {
            content()
        }
    }
}

