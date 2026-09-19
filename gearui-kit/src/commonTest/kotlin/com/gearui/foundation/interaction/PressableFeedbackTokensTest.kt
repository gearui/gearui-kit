package com.gearui.foundation.interaction

import com.gearui.foundation.button.buttonPressScale
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.theme.DefaultPalette
import com.tencent.kuikly.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

/** PressableFeedback and CloseButton values against HeroUI Native 1.0.9. */
class PressableFeedbackTokensTest {

    @Test
    fun highlightMatchesReference() {
        assertEquals(Color(0xFF3F3F46), DefaultPalette.lightPressHighlight)
        assertEquals(Color(0xFFD4D4D8), DefaultPalette.darkPressHighlight)
        assertEquals(0.1f, FeedbackDefaults.pressHighlightOpacity)
    }

    @Test
    fun scaleIsCompensatedByWidthInDp() {
        // 0.985 at the 300dp reference width; a 32dp close button shrinks far more.
        assertEquals(0.985f, buttonPressScale(300f, true, true, 300), 0.0001f)
        assertEquals(1f - 0.015f * 300f / 32f, buttonPressScale(32f, true, true, 300), 0.0001f)
        assertEquals(1f, buttonPressScale(32f, false, true, 300))
    }

    @Test
    fun closeButtonGeometryMatchesReference() {
        assertEquals(32f, ControlGeometry.closeButtonSize.value)
        assertEquals(18f, ControlGeometry.closeButtonIcon.value)
    }
}
