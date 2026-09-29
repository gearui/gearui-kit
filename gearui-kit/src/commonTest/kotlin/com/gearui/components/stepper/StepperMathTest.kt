package com.gearui.components.stepper

import kotlin.test.*

class StepperMathTest {
    @Test fun clampsInsteadOfWrappingAtIntegerLimits() {
        assertEquals(Int.MAX_VALUE, stepperValue(Int.MAX_VALUE - 1, 10, Int.MIN_VALUE, Int.MAX_VALUE))
        assertEquals(Int.MIN_VALUE, stepperValue(Int.MIN_VALUE + 1, -10, Int.MIN_VALUE, Int.MAX_VALUE))
    }
    @Test fun partialFinalStepStopsAtTheBound() {
        assertEquals(10, stepperValue(9, 3, 0, 10))
        assertEquals(0, stepperValue(1, -3, 0, 10))
    }
}
