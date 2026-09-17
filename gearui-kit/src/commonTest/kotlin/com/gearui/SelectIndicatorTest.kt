package com.gearui

import com.gearui.components.select.selectIndicatorDampingRatio
import com.gearui.components.select.selectIndicatorStiffness
import com.gearui.foundation.motion.Motion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SelectIndicatorTest {
    @Test fun referenceSpringIsMassNormalized() {
        assertEquals(250f, selectIndicatorStiffness(Motion()))
        assertEquals(1.1067972f, selectIndicatorDampingRatio, 0.000001f)
    }

    @Test fun applicationSpeedScalesSpringTimeNotDampingRatio() {
        assertEquals(62.5f, selectIndicatorStiffness(Motion(normal = 300)))
        assertEquals(1000f, selectIndicatorStiffness(Motion(normal = 75)))
        assertTrue(selectIndicatorStiffness(Motion(normal = 0)).isFinite())
    }
}
