package com.gearui

import com.gearui.theme.Typographies
import kotlin.test.Test
import kotlin.test.assertEquals

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
    fun theDefaultIsTheReferenceScaleOnEveryPlatform() {
        // One scale everywhere: a per-platform default made the same screen a different
        // size on iOS than on the web, so neither could be checked against the reference.
        assertEquals(Typographies.Reference.bodyMedium.fontSize.value, Typographies.Default.bodyMedium.fontSize.value)
        assertEquals(Typographies.Reference.titleLarge.fontSize.value, Typographies.Default.titleLarge.fontSize.value)
    }

    @Test
    fun thePlatformScaleIsStillAvailable() {
        // The old iOS metrics remain opt-in for apps that must sit beside system controls.
        assertEquals(17f, Typographies.Platform.bodyMedium.fontSize.value)
        assertEquals(600, Typographies.Platform.markMedium.fontWeight.weight)
    }
}
