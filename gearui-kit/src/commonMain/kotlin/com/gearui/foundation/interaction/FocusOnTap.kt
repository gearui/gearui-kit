package com.gearui.foundation.interaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.input.pointer.PointerEventPass
import com.tencent.kuikly.compose.ui.input.pointer.PointerInputChange
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput

/**
 * Runs [onTap] when a press on this node is a tap, to focus the text field inside it.
 *
 * A press that turns into a scroll is not a tap, and must not raise the keyboard. Telling
 * them apart is the point: the gesture detectors do not manage it on KuiklyUI, where a
 * list is a native scroll view. The field moves with the finger, so its own pointer
 * position barely changes and the press never leaves its bounds; on iOS a scroll that
 * started on a search field still focused it seven times in ten. What does tell is that
 * the native list hands its pointer events over already consumed — read in the Initial
 * pass, before the text field inside consumes anything of its own — and a drag the node
 * can see moves past the touch slop.
 *
 * Every press is seen, consumed or not: the text field inside consumes the down.
 */
@Composable
internal fun Modifier.focusOnTap(enabled: Boolean, onTap: () -> Unit): Modifier {
    val currentOnTap by rememberUpdatedState(onTap)
    return pointerInput(enabled) {
        if (!enabled) return@pointerInput
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val slop = viewConfiguration.touchSlop
            while (true) {
                val change = awaitPointerEvent(PointerEventPass.Initial).changes.firstOrNull { it.id == down.id }
                if (change == null) {
                    currentOnTap()
                    break
                }
                if (change.leftTap(down, slop)) break
                if (!change.pressed) {
                    currentOnTap()
                    break
                }
            }
        }
    }
}

/**
 * Whether a press is no longer a tap: a scroll took it, or it moved past [slop].
 * Read it in the Initial pass, before anything on this node consumes the change.
 */
internal fun PointerInputChange.leftTap(down: PointerInputChange, slop: Float): Boolean =
    isConsumed || (position - down.position).getDistance() > slop
