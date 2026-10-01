package com.gearui.components.swipecell

import kotlin.test.*

class SwipeIntentTest {
    @Test fun aSidewaysDragIsASwipe() {
        assertTrue(isSwipeIntent(-40f, 5f))
        assertTrue(isSwipeIntent(30f, -15f))
    }
    @Test fun aDiagonalScrollIsNotASwipe() {
        // The reported case: scrolling up at an angle, more vertical than sideways.
        assertFalse(isSwipeIntent(-120f, -200f))
        assertFalse(isSwipeIntent(-20f, -20f))
        assertFalse(isSwipeIntent(-29f, -20f))
    }
}
