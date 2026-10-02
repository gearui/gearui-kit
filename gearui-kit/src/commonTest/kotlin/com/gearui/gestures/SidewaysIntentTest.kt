package com.gearui.gestures

import kotlin.test.*

class SidewaysIntentTest {
    @Test fun aSidewaysDragIsASwipe() {
        assertTrue(isSidewaysIntent(-40f, 5f))
        assertTrue(isSidewaysIntent(30f, -15f))
    }
    @Test fun aDiagonalScrollIsNotASwipe() {
        // The reported case: scrolling up at an angle, more vertical than sideways.
        assertFalse(isSidewaysIntent(-120f, -200f))
        assertFalse(isSidewaysIntent(-20f, -20f))
        assertFalse(isSidewaysIntent(-29f, -20f))
    }
}
