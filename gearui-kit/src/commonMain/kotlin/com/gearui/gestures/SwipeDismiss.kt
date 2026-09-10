package com.gearui.gestures

import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.input.pointer.PointerInputChange
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.ui.input.pointer.util.VelocityTracker
import kotlin.math.abs

/**
 * Drag-down-to-dismiss configuration.
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
 * @param onProgress `dragY` in pixels, `progress` normalised against
 *   [SwipeDismissConfig.commitDistanceDp] and clamped to `[0, 1]`
 */
fun Modifier.swipeDismiss(
    enabled: Boolean = true,
    config: SwipeDismissConfig = SwipeDismissConfig(),
    onStart: (() -> Unit)? = null,
    onProgress: ((progress: Float, dragY: Float) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onCommit: () -> Unit,
): Modifier {
    if (!enabled) return this
    return this.pointerInput(enabled, config) {
        val commitPx = config.commitDistanceDp * density
        val minFlingPx = config.minFlingDistanceDp * density
        val flingVelocityPxPerSec = config.flingVelocityDpPerSec * density

        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val startX = down.position.x
            var totalDy = 0f
            var totalDx = 0f
            var recognized = false
            val velocityTracker = VelocityTracker()
            velocityTracker.addPosition(down.uptimeMillis, down.position)

            val slopChange: PointerInputChange? =
                awaitVerticalTouchSlopOrCancellation(down.id) { change, overSlop ->
                    totalDy += overSlop
                    totalDx = change.position.x - startX
                    // Downward only, and only when the vertical intent dominates.
                    if (totalDy > 0f && abs(totalDy) > abs(totalDx) * config.directionRatio) {
                        change.consume()
                        recognized = true
                    }
                }

            if (!recognized || slopChange == null) return@awaitEachGesture

            onStart?.invoke()

            var dragY = totalDy
            velocityTracker.addPosition(slopChange.uptimeMillis, slopChange.position)

            val pointer = slopChange.id
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull { it.id == pointer } ?: break
                if (!change.pressed) break

                dragY += change.positionChange().y
                // Dragging back up past the start does not lift the sheet above
                // its resting place; it just returns to it.
                if (dragY < 0f) dragY = 0f

                velocityTracker.addPosition(change.uptimeMillis, change.position)
                change.consume()

                onProgress?.invoke((dragY / commitPx).coerceIn(0f, 1f), dragY)
            }

            val vy = velocityTracker.calculateVelocity().y
            val commit = shouldCommitDismiss(
                dragPx = dragY,
                velocityPxPerSec = vy,
                commitPx = commitPx,
                minFlingPx = minFlingPx,
                flingVelocityPxPerSec = flingVelocityPxPerSec,
            )
            if (commit) onCommit() else onCancel?.invoke()
        }
    }
}
