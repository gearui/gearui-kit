package com.gearui.components.listbox

import com.gearui.components.select.SelectOption
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
    @Test fun groupsAreConsecutiveRunsInOrder() {
        val runs = listBoxGroups(listOf(SelectOption(1, "a", group = "x"), SelectOption(2, "b", group = "x"),
            SelectOption(3, "c"), SelectOption(4, "d", group = "x")))
        assertEquals(listOf(listOf(1, 2), listOf(3), listOf(4)), runs.map { run -> run.map { it.value } })
    }
}
