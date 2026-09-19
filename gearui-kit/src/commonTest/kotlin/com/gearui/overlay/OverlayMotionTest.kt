package com.gearui.overlay

import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.theme.DefaultPalette
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.unit.Density
import com.tencent.kuikly.compose.ui.unit.IntSize
import com.tencent.kuikly.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Overlay defaults and entry motion against HeroUI Native 1.0.9.
 *
 * Density 1, so dp == px. Anchor sits mid-screen; the popup is 80x40.
 */
class OverlayMotionTest {

    private val unitDensity = object : Density {
        override val density = 1f
        override val fontScale = 1f
    }
    private val anchor = Rect(160f, 380f, 240f, 420f)
    private val popup = IntSize(80, 40)

    private fun options(placement: OverlayPlacement, offset: Float) = OverlayOptions(
        placement = placement,
        offsetX = if (placement == OverlayPlacement.RightCenter) offset.dp else 0.dp,
        offsetY = if (placement == OverlayPlacement.BottomCenter || placement == OverlayPlacement.TopCenter) offset.dp else 0.dp,
    )

    private fun motionFor(placement: OverlayPlacement, offset: Float): AnchoredEntryMotion? {
        val opts = options(placement, offset)
        val position = computeOffset(anchor, popup, IntSize(400, 800), opts, unitDensity)
        return anchoredEntryMotion(anchor, position, popup, opts, unitDensity)
    }

    @Test
    fun backdropIsSharedByBothThemes() {
        // OverlayDefaults.scrimColor is a plain value because the reference backdrop is
        // identical in light and dark. If the tokens diverge this must become a lookup.
        assertEquals(DefaultPalette.lightBackdrop, DefaultPalette.darkBackdrop)
        assertEquals(DefaultPalette.lightBackdrop, OverlayDefaults.scrimColor)
        assertEquals(0.2f, OverlayDefaults.scrimColor.alpha, 0.001f)
    }

    @Test
    fun overlayGeometryMatchesReference() {
        assertEquals(24f, ControlGeometry.radiusOverlay.value)
        assertEquals(32f, OverlayDefaults.sheetCornerRadius.value)
        assertEquals(9f, OverlayDefaults.anchorOffset.value)
        assertEquals(200, OverlayDefaults.transitionDurationMillis)
        assertEquals(150, OverlayDefaults.exitDurationMillis)
        assertEquals(0.96f, FeedbackDefaults.dialogEnterScale)
        assertEquals(0.98f, FeedbackDefaults.menuItemPressScale)
    }

    @Test
    fun panelBelowTriggerSlidesDownFromIt() {
        val motion = motionFor(OverlayPlacement.BottomCenter, 9f)!!
        assertTrue(motion.vertical)
        assertEquals(-9f, motion.distancePx)
    }

    @Test
    fun panelAboveTriggerSlidesUpFromIt() {
        val motion = motionFor(OverlayPlacement.TopCenter, 9f)!!
        assertTrue(motion.vertical)
        assertEquals(9f, motion.distancePx)
    }

    @Test
    fun slideDistanceIsCappedAtTwelve() {
        assertEquals(-12f, motionFor(OverlayPlacement.BottomCenter, 30f)!!.distancePx)
    }

    @Test
    fun panelRightOfTriggerSlidesHorizontally() {
        val motion = motionFor(OverlayPlacement.RightCenter, 9f)!!
        assertEquals(false, motion.vertical)
        assertEquals(-9f, motion.distancePx)
    }

    @Test
    fun noAnchorMeansNoSlide() {
        val opts = options(OverlayPlacement.Center, 0f)
        assertNull(anchoredEntryMotion(null, computeOffset(null, popup, IntSize(400, 800), opts, unitDensity), popup, opts, unitDensity))
    }
}
