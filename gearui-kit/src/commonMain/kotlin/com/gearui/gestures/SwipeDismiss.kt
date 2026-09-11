package com.gearui.gestures

import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import com.tencent.kuikly.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.input.pointer.PointerInputChange
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.ui.input.pointer.util.VelocityTracker
import kotlin.math.abs

/**
 * Which way a surface is dragged to dismiss it.
 *
 * The direction is where the surface *leaves*, which is the edge it is anchored
 * to: a bottom sheet leaves downward, a right-hand drawer leaves to the right.
 */
enum class DismissDirection {
    Down,
    Left,
    Right,
}

/**
 * Drag-to-dismiss configuration.
 *
 * @param commitDistanceDp past this the drag counts as a dismissal
 * @param minFlingDistanceDp a fast enough flick commits over a shorter distance
 * @param flingVelocityDpPerSec fling threshold in dp/s
 * @param directionRatio vertical intent: `abs(dy)` must exceed `abs(dx) * ratio`,
 *   which rules out a horizontal swipe that happens to drift down
 */
data class SwipeDismissConfig(
    val commitDistanceDp: Float = 96f,
    val minFlingDistanceDp: Float = 24f,
    val flingVelocityDpPerSec: Float = 1000f,
    val directionRatio: Float = 1.2f,
)

/**
 * Whether a released drag counts as a dismissal.
 *
 * Pulled out of the gesture loop because it is the only part with a decision in
 * it, and inside a pointer loop nothing can reach it. The fling clause is not a
 * shortcut: a quick flick travels a short distance, and without it the gesture
 * feels stuck for exactly the users who perform it fastest.
 */
internal fun shouldCommitDismiss(
    dragPx: Float,
    velocityPxPerSec: Float,
    commitPx: Float,
    minFlingPx: Float,
    flingVelocityPxPerSec: Float,
): Boolean {
    if (dragPx >= commitPx) return true
    return velocityPxPerSec >= flingVelocityPxPerSec && dragPx >= minFlingPx
}

/**
 * Drag an edge-anchored sheet downward to dismiss it.
 *
 * The counterpart to [swipeBack], and built the same way: recognise, then
 * consume. Events are taken over only after the vertical slop is met, so a tap
 * on a row inside the sheet still reaches the row.
 *
 * ## Attach it to the header, not the whole sheet
 *
 * A sheet usually contains a scrollable list, and a downward drag is what
 * scrolls it. On iOS the two are reconciled by the scroll position: drag down
 * while the list is already at the top and the sheet moves instead. Kuikly's
 * compose layer gives a pointer modifier no view into a child's scroll offset,
 * so that rule cannot be implemented here — and guessing it wrong means the
 * sheet slides away while the user is trying to scroll, which is worse than not
 * having the gesture.
 *
 * So this attaches to the grabber and header region, which is the other half of
 * the same iOS behaviour and the part users reach for deliberately. Attaching it
 * to a scrollable body is a bug, not a configuration.
 *
 * ## Inside a lazy list, pass a key
 *
 * 🔴 A recycled row keeps the gesture block of the row it used to be.
 *
 * `pointerInput` restarts only when its keys change, and the callbacks here close
 * over whatever the caller captured — which row, which message. A LazyColumn reuses
 * a row for a different item without any of these keys changing, so the gesture goes
 * on acting for the previous item: the swipe commits against the wrong one, and the
 * stale block can swallow the press the row's own long-press handler was waiting for.
 * Pass the item's stable identity as [key].
 *
 * @param key extra `pointerInput` key; the item's identity when this is attached to a
 *   row in a lazy list
 * @param onProgress `dragY` in pixels, `progress` normalised against
 *   [SwipeDismissConfig.commitDistanceDp] and clamped to `[0, 1]`
 */
fun Modifier.swipeDismiss(
    direction: DismissDirection = DismissDirection.Down,
    enabled: Boolean = true,
    config: SwipeDismissConfig = SwipeDismissConfig(),
    key: Any? = null,
    onStart: (() -> Unit)? = null,
    onProgress: ((progress: Float, drag: Float) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onCommit: () -> Unit,
): Modifier {
    if (!enabled) return this
    return this.pointerInput(enabled, config, direction, key) {
        val commitPx = config.commitDistanceDp * density
        val minFlingPx = config.minFlingDistanceDp * density
        val flingVelocityPxPerSec = config.flingVelocityDpPerSec * density
        val vertical = direction == DismissDirection.Down

        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val start = down.position
            var along = 0f
            var across = 0f
            var recognized = false
            val velocityTracker = VelocityTracker()
            velocityTracker.addPosition(down.uptimeMillis, down.position)

            val onSlop: (PointerInputChange, Float) -> Unit = { change, overSlop ->
                along += signOf(direction) * overSlop
                across = if (vertical) change.position.x - start.x else change.position.y - start.y
                // Only in the direction the surface leaves, and only when the
                // intent along that axis dominates the drift across it.
                if (along > 0f && abs(along) > abs(across) * config.directionRatio) {
                    change.consume()
                    recognized = true
                }
            }

            val slopChange: PointerInputChange? =
                if (vertical) {
                    awaitVerticalTouchSlopOrCancellation(down.id, onSlop)
                } else {
                    awaitHorizontalTouchSlopOrCancellation(down.id, onSlop)
                }

            if (!recognized || slopChange == null) return@awaitEachGesture

            onStart?.invoke()

            var drag = along
            velocityTracker.addPosition(slopChange.uptimeMillis, slopChange.position)

            val pointer = slopChange.id
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull { it.id == pointer } ?: break
                if (!change.pressed) break

                val delta = change.positionChange()
                drag += signOf(direction) * (if (vertical) delta.y else delta.x)
                // Dragging back past the start does not pull the surface beyond
                // its resting place; it just returns to it.
                if (drag < 0f) drag = 0f

                velocityTracker.addPosition(change.uptimeMillis, change.position)
                change.consume()

                onProgress?.invoke((drag / commitPx).coerceIn(0f, 1f), drag)
            }

            val v = velocityTracker.calculateVelocity()
            val commit = shouldCommitDismiss(
                dragPx = drag,
                velocityPxPerSec = signOf(direction) * (if (vertical) v.y else v.x),
                commitPx = commitPx,
                minFlingPx = minFlingPx,
                flingVelocityPxPerSec = flingVelocityPxPerSec,
            )
            if (commit) onCommit() else onCancel?.invoke()
        }
    }
}

/** +1 when the surface leaves toward growing coordinates, -1 when it leaves toward zero. */
internal fun signOf(direction: DismissDirection): Float =
    if (direction == DismissDirection.Left) -1f else 1f
