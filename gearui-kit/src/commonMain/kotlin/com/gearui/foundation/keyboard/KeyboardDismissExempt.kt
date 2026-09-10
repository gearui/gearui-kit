package com.gearui.foundation.keyboard

import androidx.compose.runtime.staticCompositionLocalOf
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.composed
import com.tencent.kuikly.compose.ui.input.pointer.PointerEventPass
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput

/**
 * Where a touch claims "this one landed inside an input".
 *
 * The test has to be **whether the down landed inside an input area**, not
 * whether the touch was consumed. Buttons consume touches too, so using
 * consumption as a proxy means tapping "Sign in" does not dismiss the keyboard
 * either — the bug reported on 2026-09-10.
 *
 * Comparing coordinates does not work either: in Kuikly `boundsInRoot()` and a
 * pointerInput's local position are not in the same coordinate space, and the
 * check measured out as always true, so nothing dismissed the keyboard anywhere.
 *
 * So the claim is passed **within one gesture** instead. Compose's Initial pass
 * runs top-down: the container gets the down first and resets, the input claims
 * it in its own Initial, and the container reads the result on Final, once every
 * child has had its turn. No coordinate conversion anywhere.
 */
class KeyboardDismissExemptions {
    private var claimed = false

    /** The container resets this at the start of every gesture. */
    internal fun beginGesture() {
        claimed = false
    }

    /** An input area claims the gesture when it receives the down. */
    internal fun claim() {
        claimed = true
    }

    internal fun isClaimed(): Boolean = claimed
}

val LocalKeyboardDismissExemptions = staticCompositionLocalOf { KeyboardDismissExemptions() }

/**
 * Marks this area as "tapping here does not dismiss the keyboard".
 *
 * GearUI's text inputs carry it by default. A **composite input bar** — the
 * chat-page kind with a field, an emoji button and a send button — should carry
 * it as a whole: send sits outside the text field but belongs to the act of
 * typing, and dismissing the keyboard on every message sent is not what anyone
 * wants.
 */
fun Modifier.keyboardDismissExempt(): Modifier = composed {
    val exemptions = LocalKeyboardDismissExemptions.current
    pointerInput(exemptions) {
        awaitEachGesture {
            // Register, do not consume: this gesture started inside an input.
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            exemptions.claim()
        }
    }
}
