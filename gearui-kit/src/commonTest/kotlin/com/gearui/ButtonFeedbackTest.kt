package com.gearui

import com.gearui.foundation.button.buttonPressScale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.gearui.foundation.motion.Motion
import com.gearui.foundation.motion.feedbackDuration
import com.gearui.foundation.button.buttonHighlightColor
import com.gearui.foundation.button.buttonHighlightProgress
import com.tencent.kuikly.compose.ui.graphics.Color

class ButtonFeedbackTest {
    @Test fun transparentCustomThemeDoesNotDarkenMixedHue() {
        val mixed = buttonHighlightColor(Color.Transparent, Color.White, false)
        assertEquals(1f, mixed.red, 0.004f)
        assertEquals(0.1f, mixed.alpha, 0.004f)
        assertEquals(Color.Transparent, buttonHighlightColor(Color.Transparent, Color.Transparent, true))
    }
    @Test fun neutralAndAccentHaveDistinctPerceptualMixWeights() {
        val neutral = buttonHighlightColor(Color.White, Color.Black, true)
        val accent = buttonHighlightColor(Color.White, Color.Black, false)
        // OKLab L=.96 and L=.90 converted to sRGB, allowing 8-bit rounding.
        assertEquals(0.9475f, neutral.red, 0.004f)
        assertEquals(0.8698f, accent.red, 0.004f)
        assertTrue(neutral.red > accent.red)
    }

    @Test fun highlightCurveHasReferenceEndpointsAndQuarterSamples() {
        assertEquals(0f, buttonHighlightProgress(0f))
        assertEquals(0.125f, buttonHighlightProgress(0.25f))
        assertEquals(0.5f, buttonHighlightProgress(0.5f))
        assertEquals(0.875f, buttonHighlightProgress(0.75f))
        assertEquals(1f, buttonHighlightProgress(1f))
    }
    @Test fun applicationMotionOverridesAreHonored() {
        assertEquals(300, Motion().feedbackDuration(300))
        assertEquals(600, Motion(normal = 300).feedbackDuration(300))
        assertEquals(0, Motion(normal = 0).feedbackDuration(300))
    }
    @Test fun referenceWidthAndNarrowButtons() {
        assertEquals(0.985f, buttonPressScale(300f, true, true, 300), 0.00001f)
        assertEquals(0.955f, buttonPressScale(100f, true, true, 300), 0.00001f)
    }

    @Test fun releaseDisabledAndMotionSuppressionResetScale() {
        assertEquals(1f, buttonPressScale(100f, false, true, 300))
        assertEquals(1f, buttonPressScale(100f, true, false, 300))
        assertEquals(1f, buttonPressScale(100f, true, true, 0))
    }

    @Test fun InvalidMeasurementCannotInvertOrPoisonTransform() {
        for (width in listOf(0f, -1f, Float.NaN, Float.POSITIVE_INFINITY)) {
            assertEquals(1f, buttonPressScale(width, true, true, 300))
        }
        assertTrue(buttonPressScale(1f, true, true, 300) in 0f..1f)
    }
}
