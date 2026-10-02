package com.gearui.gestures

import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.gestures.horizontalDrag
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.input.pointer.PointerInputChange
import com.tencent.kuikly.compose.ui.input.pointer.PointerInputScope
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import kotlin.math.abs

/**
 * How much more sideways than vertical a drag must be for a sideways control to take it
 * (about 34° from horizontal). A drag that is mostly vertical — scrolling the page, even
 * at an angle — is left to the page.
 */
internal const val SidewaysIntent = 1.5f

/** Whether a drag of ([dx], [dy]) past the touch slop belongs to a sideways control rather than a scroll. */
internal fun isSidewaysIntent(dx: Float, dy: Float): Boolean = abs(dx) > abs(dy) * SidewaysIntent

/**
 * Drag recognition for a control that moves sideways inside something that scrolls
 * vertically (a swipe cell in a list, a slider on a page).
 *
 * `detectHorizontalDragGestures` and `detectDragGestures` take a drag as soon as its
 * travel passes the slop, so an angled scroll that starts on the control moved it. Here
 * the decision is made once the finger has moved past the slop in any direction: a
 * sideways drag goes to the control, anything else to the scroll ([onScroll]).
 *
 * [onDragStart] gets where the finger went down; [onDrag] gets the horizontal movement as
 * an offset with no vertical part.
 */
internal suspend fun PointerInputScope.detectSidewaysDrag(
    onScroll: () -> Unit = {},
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val slop = viewConfiguration.touchSlop
        var start: PointerInputChange? = null
        while (start == null) {
            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: return@awaitEachGesture
            if (!change.pressed || change.isConsumed) return@awaitEachGesture
            val dx = change.position.x - down.position.x
            val dy = change.position.y - down.position.y
            if (dx * dx + dy * dy <= slop * slop) continue
            if (!isSidewaysIntent(dx, dy)) {
                onScroll()
                return@awaitEachGesture
            }
            change.consume()
            start = change
        }
        onDragStart(down.position)
        val ended = horizontalDrag(start.id) { change ->
            onDrag(change, Offset(change.positionChange().x, 0f))
            change.consume()
        }
        if (ended) onDragEnd() else onDragCancel()
    }
}
