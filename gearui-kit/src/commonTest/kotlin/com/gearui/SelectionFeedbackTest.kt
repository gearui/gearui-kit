package com.gearui

import com.gearui.foundation.control.*
import com.gearui.foundation.motion.Motion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SelectionFeedbackTest {
    @Test fun defaultGeometryMatchesNativeSource() {
        assertEquals(24f, ControlGeometry.selectionMedium.value)
        assertEquals(48f, ControlGeometry.switchWidth.value)
        assertEquals(24f, ControlGeometry.switchHeight.value)
        assertEquals(28f, ControlGeometry.switchThumbWidth.value)
        assertEquals(20f, ControlGeometry.switchThumbHeight.value)
        assertEquals(10f, ControlGeometry.radioThumb.value)
        assertTrue(ControlGeometry.selectionTouchTarget >= ControlGeometry.selectionLarge)
    }
    @Test fun switchTravelPreservesBothInsets() {
        assertEquals(2f, switchThumbPosition(0f, 48f, 28f, 2f))
        assertEquals(18f, switchThumbPosition(1f, 48f, 28f, 2f))
        assertEquals(10f, switchThumbPosition(.5f, 48f, 28f, 2f))
    }
    @Test fun overshootCannotEscapeTheTrack() {
        assertEquals(2f, switchThumbPosition(-.2f, 48f, 28f, 2f))
        assertEquals(18f, switchThumbPosition(1.2f, 48f, 28f, 2f))
        assertEquals(2f, switchThumbPosition(1f, 20f, 28f, 2f))
    }
    @Test fun massNormalizationAndMotionOverride() {
        assertEquals(800f, switchSpringStiffness(Motion()))
        assertEquals(200f, switchSpringStiffness(Motion(normal = 300)))
        assertEquals(1.0606602f, switchDampingRatio, .000001f)
    }
}
