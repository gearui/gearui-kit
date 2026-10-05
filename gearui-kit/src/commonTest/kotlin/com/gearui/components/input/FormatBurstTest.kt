package com.gearui.components.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TestTimeSource

class FormatBurstTest {
    @Test
    fun keysAtTypingSpeedAreNotABurst() {
        val clock = TestTimeSource()
        val burst = FormatBurst(clock)
        assertFalse(burst.isBurst("1"))
        clock += 150.milliseconds
        assertFalse(burst.isBurst("13"))
        clock += 120.milliseconds
        assertFalse(burst.isBurst("138"))
    }

    @Test
    fun keysFasterThanAnyHandAreABurst() {
        val clock = TestTimeSource()
        val burst = FormatBurst(clock)
        assertFalse(burst.isBurst("1"))
        clock += 5.milliseconds
        assertTrue(burst.isBurst("13"))
    }

    @Test
    fun theFieldsRepeatedReportIsNotANewKey() {
        val clock = TestTimeSource()
        val burst = FormatBurst(clock)
        assertFalse(burst.isBurst("13"))
        clock += 1.milliseconds
        // The native field reports each change twice; the repeat must not read as a burst.
        assertFalse(burst.isBurst("13"))
    }

    @Test
    fun aHoldKeepsTheTextShownWhenItBegan() {
        val burst = FormatBurst(TestTimeSource())
        burst.hold("138 1")
        burst.hold("13812")
        assertTrue(burst.holding)
        assertEquals("138 1", burst.before)
    }
}
