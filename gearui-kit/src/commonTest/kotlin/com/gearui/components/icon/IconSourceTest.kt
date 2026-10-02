package com.gearui.components.icon

import kotlin.test.*

class IconSourceTest {
    @Test fun anIconIsEqualToItselfAcrossReads() {
        assertEquals(Icons.heart, Icons.heart)
        assertNotEquals(Icons.heart, Icons.star)
        assertEquals(Icons.heart.filled, Icons.heart.filled)
    }

    @Test fun fillFallsBackToRegular() {
        val regularOnly = VectorIcon("only", "M0 0L1 1Z")
        assertEquals((regularOnly.resolve(false) as VectorAsset).path, (regularOnly.resolve(true) as VectorAsset).path)
        val both = VectorIcon("both", "M0 0Z", fill = "M1 1Z")
        assertEquals("M1 1Z", (both.resolve(true) as VectorAsset).path)
        assertEquals("M1 1Z", (both.filled.resolve(false) as VectorAsset).path)
    }

    @Test fun imageIconsResolveToTheirUrls() {
        val icon = ImageIcon("logo", "assets://icons/logo.png", fill = "assets://icons/logo_fill.png")
        assertEquals("assets://icons/logo.png", (icon.resolve(false) as ImageAsset).url)
        assertEquals("assets://icons/logo_fill.png", (icon.resolve(true) as ImageAsset).url)
    }
}
