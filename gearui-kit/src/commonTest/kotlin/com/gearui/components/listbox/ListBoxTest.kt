package com.gearui.components.listbox

import kotlin.test.*

class ListBoxTest {
    @Test fun singleSelectionReplacesPreviousValue() {
        assertEquals(setOf("b"), listBoxSelection(setOf("a"), "b", false))
        assertEquals(setOf("b"), listBoxSelection(setOf("b"), "b", false))
    }
    @Test fun multipleSelectionTogglesOnlyOneValue() {
        assertEquals(setOf("a", "b"), listBoxSelection(setOf("a"), "b", true))
        assertEquals(setOf("a"), listBoxSelection(setOf("a", "b"), "b", true))
    }
}
