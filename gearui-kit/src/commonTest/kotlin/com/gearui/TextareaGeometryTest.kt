package com.gearui

import com.gearui.components.textarea.textareaMinimumHeight
import kotlin.test.Test
import kotlin.test.assertEquals

class TextareaGeometryTest {
    @Test fun defaultStandaloneUsesReferenceMinimum() {
        assertEquals(128f, textareaMinimumHeight(22f, 4, false).value)
    }
    @Test fun largeFontsAreNotClippedToReferenceHeight() {
        assertEquals(178f, textareaMinimumHeight(40f, 4, false).value)
    }
    @Test fun autosizeAndExplicitLineCountsStayCompact() {
        assertEquals(40f, textareaMinimumHeight(22f, 1, true).value)
        assertEquals(62f, textareaMinimumHeight(22f, 2, false).value)
    }
}
