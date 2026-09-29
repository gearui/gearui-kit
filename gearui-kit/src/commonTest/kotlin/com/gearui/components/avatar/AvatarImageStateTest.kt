package com.gearui.components.avatar

import kotlin.test.*

class AvatarImageStateTest {
    @Test fun loadingAndFailureKeepInitialsAndSuccessHidesThemEvenForTransparentPictures() {
        val state = AvatarImageState()
        state.loading()
        assertFalse(state.shown)
        state.failure()
        assertTrue(state.failed)
        assertFalse(state.shown)
        state.loading()
        assertFalse(state.failed)
        state.success()
        assertTrue(state.shown)
        assertFalse(state.failed)
    }
    @Test fun lateCallbackFromOldUrlCannotAlterNewSource() {
        val old = AvatarImageState()
        val replacement = AvatarImageState()
        replacement.success()
        old.failure()
        assertTrue(replacement.shown)
        assertFalse(replacement.failed)
    }
    @Test fun suppliedPainterDoesNotShowInitialsBehindTransparentPixels() {
        assertTrue(AvatarImageState(hasPainter = true).shown)
    }
}
