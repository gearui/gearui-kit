package com.gearui.components.slider

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Pure slider math shared by Slider and RangeSlider.
 *
 * Kept free of Compose and density so the behaviour (normalisation, step
 * snapping, range clamping, label formatting, thumb choice) can be unit
 * tested directly. Pixel-to-ratio conversion stays in the composables.
 */
internal object SliderMath {

    /** Normalises a value into 0..1 within the range. Degenerate ranges map to 0. */
    fun normalize(value: Float, range: ClosedFloatingPointRange<Float>): Float {
        val span = range.endInclusive - range.start
        if (span <= 0f) return 0f
        return ((value - range.start) / span).coerceIn(0f, 1f)
    }

    /**
     * Snaps a value onto the step grid and clamps it into the range.
     * `steps` is the number of intermediate stops, so the grid has steps + 2
     * values including both range endpoints. The grid origin is the range
     * start, not zero: legal stops are start + i * stepSize.
     */
    fun snap(value: Float, range: ClosedFloatingPointRange<Float>, steps: Int): Float {
        val span = range.endInclusive - range.start
        if (span <= 0f) return range.start
        if (steps <= 0) return value.coerceIn(range)
        val stepSize = span / (steps + 1)
        val snapped = range.start + ((value - range.start) / stepSize).roundToInt() * stepSize
        return snapped.coerceIn(range)
    }

    /** Value at a normalised track position (ratio clamped to 0..1), step-snapped. */
    fun valueAt(ratio: Float, range: ClosedFloatingPointRange<Float>, steps: Int): Float {
        val clamped = ratio.coerceIn(0f, 1f)
        val value = range.start + (range.endInclusive - range.start) * clamped
        return snap(value, range, steps)
    }

    /** Formats a value for display: whole numbers without a fraction, others to one decimal. */
    fun format(value: Float): String {
        return if (value == value.roundToInt().toFloat()) {
            value.roundToInt().toString()
        } else {
            val rounded = (value * 10).roundToInt() / 10.0
            rounded.toString()
        }
    }

    /** Which thumb a tapped value belongs to: 0 = start, 1 = end (ties go to start). */
    fun nearestThumb(value: Float, start: Float, end: Float): Int {
        return if (abs(value - start) <= abs(value - end)) 0 else 1
    }
}
