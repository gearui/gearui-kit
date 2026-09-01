package com.gearui.overlay

import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.unit.Density
import com.tencent.kuikly.compose.ui.unit.IntOffset
import com.tencent.kuikly.compose.ui.unit.IntSize
import com.tencent.kuikly.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [computeOffset], the anchoring algorithm behind Popover,
 * Select, Tooltip and every anchored overlay.
 *
 * Screen is 400x800 px, popup is 80x40 px, density 1 so dp == px.
 */
class OverlayComputeOffsetTest {

    private val unitDensity = object : Density {
        override val density = 1f
        override val fontScale = 1f
    }

    private val screenSize = IntSize(400, 800)
    private val popupSize = IntSize(80, 40)

    /** Zero offsets so placement math is exact. */
    private fun options(placement: OverlayPlacement, autoFlip: Boolean = true) = OverlayOptions(
        placement = placement,
        offsetX = 0.dp,
        offsetY = 0.dp,
        autoFlip = autoFlip
    )

    private fun compute(
        anchor: Rect?,
        placement: OverlayPlacement,
        autoFlip: Boolean = true
    ): IntOffset = computeOffset(anchor, popupSize, screenSize, options(placement, autoFlip), unitDensity)

    @Test
    fun bottomCenterPlacesBelowAndCentredOnTheAnchor() {
        // Anchor centred horizontally; plenty of space below.
        val anchor = Rect(180f, 300f, 220f, 340f)
        // x = anchor centre (200) - popup half-width (40); y = anchor bottom.
        assertEquals(IntOffset(160, 340), compute(anchor, OverlayPlacement.BottomCenter))
    }

    @Test
    fun bottomPlacementFlipsAboveWhenSpaceRunsOut() {
        // Only 10 px below the anchor but 750 above.
        val anchor = Rect(180f, 750f, 220f, 790f)
        // y = anchor top (750) - popup height (40).
        assertEquals(IntOffset(160, 710), compute(anchor, OverlayPlacement.BottomCenter))
    }

    @Test
    fun topPlacementFlipsBelowWhenSpaceRunsOut() {
        // Only 5 px above the anchor but 770 below.
        val anchor = Rect(180f, 5f, 220f, 30f)
        // y = anchor bottom.
        assertEquals(IntOffset(160, 30), compute(anchor, OverlayPlacement.TopCenter))
    }

    @Test
    fun autoFlipCanBeDisabled() {
        // Same bottom-of-screen anchor, but flipping is off: stay below anyway.
        val anchor = Rect(180f, 750f, 220f, 790f)
        // y is clamped so the top never goes above the anchor bottom.
        assertEquals(IntOffset(160, 790), compute(anchor, OverlayPlacement.BottomCenter, autoFlip = false))
    }

    @Test
    fun leftPlacementFlipsRightWhenSpaceRunsOut() {
        // Only 10 px left of the anchor but 370 to the right.
        val anchor = Rect(10f, 300f, 30f, 340f)
        // x = anchor right; y = anchor centre (320) - popup half-height (20).
        assertEquals(IntOffset(30, 300), compute(anchor, OverlayPlacement.LeftCenter))
    }

    @Test
    fun rightPlacementFlipsLeftWhenSpaceRunsOut() {
        // Only 10 px right of the anchor but 370 to the left.
        val anchor = Rect(370f, 300f, 390f, 340f)
        // x = anchor left (370) - popup width (80); y centred on the anchor.
        assertEquals(IntOffset(290, 300), compute(anchor, OverlayPlacement.RightCenter))
    }

    @Test
    fun verticalPlacementClampsXInsideTheScreen() {
        // Anchor hugs the right edge; the popup must be pulled back on-screen.
        val anchor = Rect(380f, 300f, 395f, 340f)
        // x = anchor left (380) clamped to 400 - 80 = 320.
        assertEquals(IntOffset(320, 340), compute(anchor, OverlayPlacement.BottomLeft))
    }

    @Test
    fun nullAnchorCentresTheOverlayOnScreen() {
        assertEquals(IntOffset(160, 380), compute(null, OverlayPlacement.Center))
    }

    @Test
    fun nullAnchorFullscreenStaysAtTheOrigin() {
        assertEquals(IntOffset.Zero, compute(null, OverlayPlacement.Fullscreen))
    }

    @Test
    fun dpOffsetsConvertThroughTheDensity() {
        val anchor = Rect(100f, 100f, 140f, 130f)
        val doubleDensity = object : Density {
            override val density = 2f
            override val fontScale = 1f
        }
        val opts = OverlayOptions(
            placement = OverlayPlacement.BottomLeft,
            offsetX = 8.dp,
            offsetY = 6.dp
        )
        // 8.dp -> 16 px, 6.dp -> 12 px at density 2.
        val offset = computeOffset(anchor, popupSize, screenSize, opts, doubleDensity)
        assertEquals(IntOffset(116, 142), offset)
    }
}
