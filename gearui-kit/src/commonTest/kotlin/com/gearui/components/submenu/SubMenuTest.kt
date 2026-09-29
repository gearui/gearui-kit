package com.gearui.components.submenu

import kotlin.test.*

class SubMenuTest {
    @Test fun invalidPathStopsAtLastExistingParent() {
        val tree = listOf(SubMenuItem("a", "Same", listOf(SubMenuItem("b", "Same"))))
        assertEquals(listOf("a", "b"), subMenuPath(tree, listOf("a", "b")).map { it.id })
        assertEquals(listOf("a"), subMenuPath(tree, listOf("a", "missing", "b")).map { it.id })
        assertTrue(subMenuPath(tree, listOf("missing")).isEmpty())
    }
}
