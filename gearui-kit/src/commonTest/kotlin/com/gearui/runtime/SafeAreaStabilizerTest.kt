package com.gearui.runtime

import com.tencent.kuikly.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class SafeAreaStabilizerTest {
    @Test
    fun usesFallbackUntilFirstValidInsetArrives() {
        val stabilizer = SafeAreaStabilizer()

        val initial = stabilizer.stabilize(SafeArea(), isPortrait = true, fallbackTop = 44.dp)
        val settled = stabilizer.stabilize(
            SafeArea(top = 59.dp, bottom = 34.dp),
            isPortrait = true,
            fallbackTop = 44.dp
        )

        assertEquals(44.dp, initial.top)
        assertEquals(59.dp, settled.top)
        assertEquals(34.dp, settled.bottom)
    }

    @Test
    fun filtersTransientZeroWithoutFreezingNonZeroChanges() {
        val stabilizer = SafeAreaStabilizer()

        stabilizer.stabilize(SafeArea(top = 59.dp), isPortrait = true, fallbackTop = 44.dp)
        val transientZero = stabilizer.stabilize(SafeArea(), isPortrait = true, fallbackTop = 44.dp)
        val legitimateDecrease = stabilizer.stabilize(
            SafeArea(top = 47.dp),
            isPortrait = true,
            fallbackTop = 44.dp
        )

        assertEquals(59.dp, transientZero.top)
        assertEquals(47.dp, legitimateDecrease.top)
    }

    @Test
    fun clearsCachedInsetWhenOrientationChanges() {
        val stabilizer = SafeAreaStabilizer()

        stabilizer.stabilize(SafeArea(top = 59.dp), isPortrait = true, fallbackTop = 44.dp)
        val landscape = stabilizer.stabilize(SafeArea(), isPortrait = false, fallbackTop = 0.dp)

        assertEquals(0.dp, landscape.top)
    }
}
