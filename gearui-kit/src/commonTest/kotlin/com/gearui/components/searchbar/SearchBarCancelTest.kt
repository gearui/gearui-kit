package com.gearui.components.searchbar

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * When the Cancel button is visible.
 *
 * Worth a test mostly for the compatibility clause: this used to be a bare
 * boolean the caller managed, so any host that set it already has a Cancel
 * button on screen. Making the new mode win would have made that button start
 * appearing and disappearing under them.
 */
class SearchBarCancelTest {

    @Test
    fun whileEditingFollowsFocus() {
        assertTrue(cancelVisible(SearchBarCancel.WhileEditing, legacyShowCancel = false, focused = true))
        assertFalse(cancelVisible(SearchBarCancel.WhileEditing, legacyShowCancel = false, focused = false))
    }

    @Test
    fun alwaysAndNeverIgnoreFocus() {
        assertTrue(cancelVisible(SearchBarCancel.Always, legacyShowCancel = false, focused = false))
        assertFalse(cancelVisible(SearchBarCancel.Never, legacyShowCancel = false, focused = true))
    }

    @Test
    fun theSupersededFlagStillWins() {
        assertTrue(
            cancelVisible(SearchBarCancel.Never, legacyShowCancel = true, focused = false),
            "a caller that asked for a Cancel button keeps it, whatever the new mode says",
        )
    }

    @Test
    fun theDefaultIsTheReferencePlatformBehaviour() {
        // Not a tautology: the default is the one thing a caller does not state,
        // so nothing else in the file would catch it being changed.
        assertTrue(cancelVisible(SearchBarCancel.WhileEditing, legacyShowCancel = false, focused = true))
    }
}
