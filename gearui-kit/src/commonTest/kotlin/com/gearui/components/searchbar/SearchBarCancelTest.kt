package com.gearui.components.searchbar

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** When the Cancel button is visible. */
class SearchBarCancelTest {

    @Test
    fun whileEditingFollowsFocus() {
        assertTrue(cancelVisible(SearchBarCancel.WhileEditing, focused = true))
        assertFalse(cancelVisible(SearchBarCancel.WhileEditing, focused = false))
    }

    @Test
    fun alwaysAndNeverIgnoreFocus() {
        assertTrue(cancelVisible(SearchBarCancel.Always, focused = false))
        assertFalse(cancelVisible(SearchBarCancel.Never, focused = true))
    }
}
