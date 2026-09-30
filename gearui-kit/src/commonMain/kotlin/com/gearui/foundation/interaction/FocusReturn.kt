package com.gearui.foundation.interaction

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gearui.i18n.currentEpochMillis
import com.tencent.kuikly.compose.extension.nativeRef
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.core.base.DeclarativeBaseView

/**
 * Where screen-reader focus goes back to when an overlay closes: the control that opened
 * it (GearUI spec, Overlay family: "ownership and focus restoration"; what UIKit does for
 * a dismissed modal).
 *
 * The controls that usually open overlays — Button, field triggers, Cell, PressableFeedback
 * — mark themselves when tapped; an overlay shown right after takes that control as its
 * return target, and when the last overlay is gone, focus is moved back with KuiklyUI's
 * `accessibilityFocus()` (VoiceOver: screen-changed on the view; TalkBack: accessibility
 * focus). Without a screen reader running this posts an event nobody reads.
 */
internal class ActivationTracker {
    var view: DeclarativeBaseView<*, *>? = null

    fun mark() {
        LastActivated.view = view
        LastActivated.at = currentEpochMillis()
    }
}

internal object LastActivated {
    var view: DeclarativeBaseView<*, *>? = null
    var at: Long = 0L

    /** The control tapped in the last second, taken so a later overlay does not reuse it. */
    fun takeRecent(): DeclarativeBaseView<*, *>? {
        val v = view
        view = null
        return v?.takeIf { currentEpochMillis() - at < 1_000L }
    }
}

@Composable
internal fun rememberActivationTracker(): ActivationTracker {
    val tracker = remember { ActivationTracker() }
    // Leaving the composition (a popped page) drops the view, here and as the last tap,
    // so neither keeps a page's native tree alive.
    DisposableEffect(tracker) {
        onDispose {
            if (LastActivated.view === tracker.view) LastActivated.view = null
            tracker.view = null
        }
    }
    return tracker
}

/** Registers this node's native view as the control [tracker] marks. */
internal fun Modifier.activationTarget(tracker: ActivationTracker): Modifier =
    nativeRef { tracker.view = this }
