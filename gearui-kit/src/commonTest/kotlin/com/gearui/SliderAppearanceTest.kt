package com.gearui

import com.gearui.components.slider.SliderMath
import com.gearui.components.slider.sliderDampingRatio
import com.gearui.components.slider.sliderSpringStiffness
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.Motion
import kotlin.test.Test
import kotlin.test.assertEquals

class SliderAppearanceTest {
    @Test fun referenceDimensionsAndSpring() {
        assertEquals(20f, ControlGeometry.sliderTrackHeight.value)
        assertEquals(28f, ControlGeometry.sliderThumbWidth.value)
        assertEquals(20f, ControlGeometry.sliderThumbHeight.value)
        assertEquals(400f, sliderSpringStiffness(Motion()))
        assertEquals(100f, sliderSpringStiffness(Motion(normal = 300)))
        assertEquals(.75f, sliderDampingRatio)
    }
    @Test fun widenedThumbUsesItsCenterForTapMapping() {
        assertEquals(0f, SliderMath.positionRatio(14f, 200f, 28f))
        assertEquals(.5f, SliderMath.positionRatio(100f, 200f, 28f))
        assertEquals(1f, SliderMath.positionRatio(186f, 200f, 28f))
        assertEquals(86f, SliderMath.thumbOffset(.5f, 200f, 28f))
    }
    @Test fun zeroOrNarrowMeasurementCannotPlaceThumbBeforeTrack() {
        for (width in listOf(0f, 20f, 28f)) {
            assertEquals(0f, SliderMath.thumbOffset(.5f, width, 28f))
            assertEquals(0f, SliderMath.positionRatio(10f, width, 28f))
        }
    }
    @Test fun expandedThumbStillRespectsNonzeroStepOrigin() {
        val ratio = SliderMath.positionRatio(100f, 200f, 28f)
        assertEquals(7f, SliderMath.valueAt(ratio, 1f..10f, 2))
    }
}
