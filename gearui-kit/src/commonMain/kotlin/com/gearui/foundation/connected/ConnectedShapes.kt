package com.gearui.foundation.connected

import com.tencent.kuikly.compose.foundation.shape.CornerBasedShape
import com.tencent.kuikly.compose.ui.graphics.Shape

/** Where a segment sits in a row of connected controls. */
internal enum class SegmentPosition { Single, First, Middle, Last }

internal fun segmentPosition(index: Int, count: Int): SegmentPosition = when {
    count <= 1 -> SegmentPosition.Single
    index == 0 -> SegmentPosition.First
    index == count - 1 -> SegmentPosition.Last
    else -> SegmentPosition.Middle
}

/**
 * The shape of one segment of a connected group: [outer]'s corners on the group's outside
 * edges, [inner]'s on the sides it shares with a neighbour — both read from the theme, so
 * a square-corner theme squares the whole group. A shape that is not corner-based is
 * used as it is.
 */
internal fun connectedShape(position: SegmentPosition, outer: Shape, inner: Shape): Shape {
    if (outer !is CornerBasedShape || inner !is CornerBasedShape) return outer
    return when (position) {
        SegmentPosition.Single -> outer
        SegmentPosition.First -> outer.copy(topEnd = inner.topEnd, bottomEnd = inner.bottomEnd)
        SegmentPosition.Last -> outer.copy(topStart = inner.topStart, bottomStart = inner.bottomStart)
        SegmentPosition.Middle -> inner
    }
}
