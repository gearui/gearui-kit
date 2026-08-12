package com.gearui.gestures

import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.input.pointer.PointerInputChange
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.ui.input.pointer.util.VelocityTracker
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import kotlin.math.abs

/**
 * Edge-swipe back gesture configuration
 *
 * @param edgeWidthDp          width of the left edge hot zone (dp); the touch must start inside it
 * @param commitDistanceDp     commit distance threshold (dp); past this the user is taken to have confirmed the back
 * @param minFlingDistanceDp   minimum fling distance (dp); a fast enough flick may commit over a shorter distance
 * @param flingVelocityDpPerSec  fling velocity threshold (dp/s)
 * @param directionRatio       horizontal intent ratio: abs(dx) must exceed abs(dy) * ratio, ruling out vertical drags
 */
data class SwipeBackConfig(
    val edgeWidthDp: Float = 24f,
    val commitDistanceDp: Float = 96f,
    val minFlingDistanceDp: Float = 24f,
    val flingVelocityDpPerSec: Float = 1200f,
    val directionRatio: Float = 1.2f,
)

/**
 * Attaches the left-edge swipe-back gesture to any composable.
 *
 * gearui-kit only recognises the gesture and reports it; it knows nothing about navigation state.
 *
 * Gesture state machine:
 *   Idle -> Tracking (touch started inside the edge)
 *        -> Recognized (horizontal intent + touch slop met) -> starts consuming events
 *        -> Committed (distance or velocity threshold met) -> onCommit()
 *        or Cancelled (released short of the threshold, or recognition failed) -> onCancel()
 *
 * Consumption: events are taken over only after Recognized, so child gestures are never stolen early.
 *
 * @param enabled       false skips recognition entirely (root pages, modal pages, ...)
 * @param config        gesture parameters, tunable per platform or page type
 * @param onStart       fired once the gesture is recognised (the Recognized phase)
 * @param onProgress    fired continuously while dragging; progress is in [0f, 1f] (relative to commitDistance) and dragX is the absolute pixel offset
 * @param onCancel      released without meeting the commit conditions
 * @param onCommit      commit conditions met; perform the back navigation here
 */
fun Modifier.swipeBack(
    enabled: Boolean = true,
    config: SwipeBackConfig = SwipeBackConfig(),
    onStart: (() -> Unit)? = null,
    onProgress: ((progress: Float, dragX: Float) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onCommit: () -> Unit,
): Modifier {
    if (!enabled) return this
    return this.pointerInput(enabled, config) {
        val edgePx = config.edgeWidthDp * density
        val commitPx = config.commitDistanceDp * density
        val minFlingPx = config.minFlingDistanceDp * density
        val flingVelocityPxPerSec = config.flingVelocityDpPerSec * density

        awaitEachGesture {
            // ── Idle → Tracking ──────────────────────────────────────────
            val down = awaitFirstDown(requireUnconsumed = false)
            if (down.position.x > edgePx) return@awaitEachGesture   // 不在边缘，忽略

            val startX = down.position.x
            val startY = down.position.y
            var totalDx = 0f
            var totalDy = 0f
            var recognized = false
            val velocityTracker = VelocityTracker()
            velocityTracker.addPosition(down.uptimeMillis, down.position)

            // -- Tracking: wait for horizontal touch slop, or cancel ------------
            val slopChange: PointerInputChange? =
                awaitHorizontalTouchSlopOrCancellation(down.id) { change, overSlop ->
                    totalDx += overSlop
                    totalDy = change.position.y - startY
                    // Refuse recognition when the vertical intent is too strong
                    if (abs(totalDx) > abs(totalDy) * config.directionRatio && totalDx > 0f) {
                        change.consume()
                        recognized = true
                    }
                }

            if (!recognized || slopChange == null) return@awaitEachGesture

            // -- Recognized: take over the remaining events ----------------------
            onStart?.invoke()

            var dragX = totalDx
            velocityTracker.addPosition(slopChange.uptimeMillis, slopChange.position)

            // Keep reading move events
            var pointer = slopChange.id
            var committed = false
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull { it.id == pointer } ?: break
                if (!change.pressed) break  // 抬手

                val dx = change.positionChange().x
                val dy = change.positionChange().y
                // Cancel if the vertical component grows far beyond the horizontal one
                dragX += dx
                if (dragX < 0f) dragX = 0f  // 不允许往左滑

                velocityTracker.addPosition(change.uptimeMillis, change.position)
                change.consume()

                val progress = (dragX / commitPx).coerceIn(0f, 1f)
                onProgress?.invoke(progress, dragX)
            }

            // -- End: commit or cancel -------------------------------------------
            val velocity = velocityTracker.calculateVelocity()
            val vx = velocity.x
            val isFling = vx >= flingVelocityPxPerSec && dragX >= minFlingPx
            val isCommit = dragX >= commitPx || isFling

            if (isCommit) {
                onCommit()
            } else {
                onCancel?.invoke()
            }
        }
    }
}
