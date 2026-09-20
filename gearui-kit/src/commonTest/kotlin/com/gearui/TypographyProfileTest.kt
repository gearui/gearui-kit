package com.gearui

import com.gearui.theme.Typographies
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The two type scales GearUI ships, and which one is the default. */
class TypographyProfileTest {

    @Test
    fun referenceProfileUsesTheHeroUIScale() {
        val reference = Typographies.Reference
        assertEquals(16f, reference.bodyMedium.fontSize.value)
        assertEquals(14f, reference.bodySmall.fontSize.value)
        assertEquals(18f, reference.bodyLarge.fontSize.value)
        assertEquals(24f, reference.bodyMedium.lineHeight.value)
    }

    @Test
    fun referenceEmphasisIsMediumNotSemibold() {
        // HeroUI has no 600 in its text styles; emphasis is font-medium.
        assertEquals(500, Typographies.Reference.markMedium.fontWeight.weight)
    }

    @Test
    fun theDefaultStaysOnThePlatformScale() {
        // Changing this would reflow every screen in every consumer at once, so the
        // default follows the platform and the reference scale is opt-in.
        assertTrue(Typographies.Default.bodyMedium.fontSize.value != Typographies.Reference.bodyMedium.fontSize.value)
    }
}
