package com.gearui.components.slider

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for the pure slider math shared by Slider and RangeSlider.
 */
class SliderMathTest {

    private val range = 0f..100f

    @Test
    fun normalizeClampsIntoZeroOne() {
        assertEquals(0f, SliderMath.normalize(-10f, range))
        assertEquals(0f, SliderMath.normalize(0f, range))
        assertEquals(0.5f, SliderMath.normalize(50f, range))
        assertEquals(1f, SliderMath.normalize(100f, range))
        assertEquals(1f, SliderMath.normalize(150f, range))
    }

    @Test
    fun normalizeHandlesNonZeroStartAndDegenerateRange() {
        assertEquals(0.25f, SliderMath.normalize(125f, 100f..200f))
        // A zero-span range must not produce NaN.
        assertEquals(0f, SliderMath.normalize(42f, 10f..10f))
    }

    @Test
    fun snapWithoutStepsOnlyClamps() {
        assertEquals(50f, SliderMath.snap(50f, range, steps = 0))
        assertEquals(0f, SliderMath.snap(-5f, range, steps = 0))
        assertEquals(100f, SliderMath.snap(120f, range, steps = 0))
    }

    @Test
    fun snapLandsOnStepGrid() {
        // steps = 4 splits 0..100 into a 0/20/40/60/80/100 grid.
        assertEquals(20f, SliderMath.snap(23f, range, steps = 4))
        assertEquals(40f, SliderMath.snap(31f, range, steps = 4))
        assertEquals(0f, SliderMath.snap(9f, range, steps = 4))
        assertEquals(100f, SliderMath.snap(92f, range, steps = 4))
    }

    @Test
    fun snapClampsAfterSnapping() {
        assertEquals(100f, SliderMath.snap(130f, range, steps = 4))
        assertEquals(0f, SliderMath.snap(-130f, range, steps = 4))
    }

    @Test
    fun snapUsesRangeStartAsGridOrigin() {
        // steps = 2 splits 1..10 into the 1/4/7/10 grid, not 1/3/6/9/10.
        assertEquals(1f, SliderMath.snap(1f, 1f..10f, steps = 2))
        assertEquals(1f, SliderMath.snap(2f, 1f..10f, steps = 2))
        assertEquals(4f, SliderMath.snap(3f, 1f..10f, steps = 2))
        assertEquals(7f, SliderMath.snap(6f, 1f..10f, steps = 2))
        assertEquals(10f, SliderMath.snap(9f, 1f..10f, steps = 2))
        assertEquals(10f, SliderMath.snap(10f, 1f..10f, steps = 2))
    }

    @Test
    fun snapHandlesNegativeRanges() {
        // steps = 4 splits -10..0 into the -10/-8/-6/-4/-2/0 grid.
        assertEquals(-4f, SliderMath.snap(-3.1f, -10f..0f, steps = 4))
        assertEquals(-2f, SliderMath.snap(-2f, -10f..0f, steps = 4))
        assertEquals(0f, SliderMath.snap(-0.4f, -10f..0f, steps = 4))
    }

    @Test
    fun snapOnZeroSpanRangeWithStepsReturnsStart() {
        // Degenerate range with steps must not divide by zero.
        assertEquals(10f, SliderMath.snap(5f, 10f..10f, steps = 3))
    }

    @Test
    fun valueAtHonoursNonZeroStart() {
        // 0.5 of 1..10 is 5.5; nearest grid stop on 1/4/7/10 is 7.
        assertEquals(7f, SliderMath.valueAt(0.5f, 1f..10f, steps = 2))
        assertEquals(1f, SliderMath.valueAt(0f, 1f..10f, steps = 2))
        assertEquals(10f, SliderMath.valueAt(1f, 1f..10f, steps = 2))
    }

    @Test
    fun valueAtMapsRatioToSnappedValue() {
        assertEquals(0f, SliderMath.valueAt(0f, range, steps = 0))
        assertEquals(50f, SliderMath.valueAt(0.5f, range, steps = 0))
        assertEquals(100f, SliderMath.valueAt(1f, range, steps = 0))
        // Ratio is clamped before mapping.
        assertEquals(100f, SliderMath.valueAt(1.5f, range, steps = 0))
        // With steps, the mapped value snaps onto the grid.
        assertEquals(40f, SliderMath.valueAt(0.42f, range, steps = 4))
    }

    @Test
    fun formatDropsTrailingFractionForWholeNumbers() {
        assertEquals("3", SliderMath.format(3f))
        assertEquals("3", SliderMath.format(3.0f))
        assertEquals("3.5", SliderMath.format(3.5f))
        // Rounds to one decimal.
        assertEquals("3.5", SliderMath.format(3.47f))
        assertEquals("0", SliderMath.format(0f))
    }

    @Test
    fun nearestThumbPicksTheCloserThumb() {
        assertEquals(0, SliderMath.nearestThumb(value = 12f, start = 10f, end = 20f))
        assertEquals(1, SliderMath.nearestThumb(value = 17f, start = 10f, end = 20f))
        // Ties go to the start thumb.
        assertEquals(0, SliderMath.nearestThumb(value = 15f, start = 10f, end = 20f))
    }
}
