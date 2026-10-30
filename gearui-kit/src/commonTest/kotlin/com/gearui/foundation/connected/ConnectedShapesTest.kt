package com.gearui.foundation.connected

import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ConnectedShapesTest {
    @Test
    fun positionsFollowTheIndex() {
        assertEquals(SegmentPosition.Single, segmentPosition(0, 1))
        assertEquals(SegmentPosition.First, segmentPosition(0, 3))
        assertEquals(SegmentPosition.Middle, segmentPosition(1, 3))
        assertEquals(SegmentPosition.Last, segmentPosition(2, 3))
    }

    @Test
    fun sharedSidesTakeTheInnerCorners() {
        val outer = RoundedCornerShape(24.dp)
        val inner = RoundedCornerShape(4.dp)
        assertEquals(RoundedCornerShape(24.dp, 4.dp, 4.dp, 24.dp), connectedShape(SegmentPosition.First, outer, inner))
        assertEquals(RoundedCornerShape(4.dp, 24.dp, 24.dp, 4.dp), connectedShape(SegmentPosition.Last, outer, inner))
        assertSame(inner, connectedShape(SegmentPosition.Middle, outer, inner))
        assertSame(outer, connectedShape(SegmentPosition.Single, outer, inner))
    }

    @Test
    fun aSquareThemeStaysSquare() {
        assertSame(RectangleShape, connectedShape(SegmentPosition.First, RectangleShape, RoundedCornerShape(4.dp)))
    }
}
