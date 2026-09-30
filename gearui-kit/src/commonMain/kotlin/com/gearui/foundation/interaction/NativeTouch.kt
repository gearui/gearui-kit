package com.gearui.foundation.interaction

import com.tencent.kuikly.compose.extension.setEvent
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.composed
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration

/**
 * Makes this node's native view the end of the line for a touch nobody inside took.
 *
 * Compose gestures are dispatched from the root and are not affected. What this stops is
 * the native dispatch: KuiklyUI's text fields are native views that take a touch
 * directly, and a native view without a touch listener lets a touch fall through to
 * whatever lies beneath — so a tap on an overlay's scrim, or on a blank part of a page,
 * focused a text field on the page underneath (a hidden page kept alive in the stack
 * included) and raised the keyboard. A touch listener makes the view consume the touch;
 * its children, the page's own fields included, still get it first.
 */
internal fun Modifier.consumeNativeTouches(): Modifier = composed {
    // Android and iOS only. On the Web the DOM already stops a click at the element on
    // top, and a touch listener there kept every click from reaching Compose at all —
    // no Select or Popover opened.
    val configuration = LocalConfiguration.current
    if (configuration.isAndroid || configuration.isIOS) setEvent("touchDown") { } else this
}
