package com.gearui.foundation.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.gearui.runtime.LocalRuntimeEnvironment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.composed
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Keeps a focused text field above the software keyboard, as iOS does.
 *
 * GearUI's host contract is edge to edge, so the window never shrinks for the keyboard
 * and nothing moved a field that the keyboard covered. Now a field reports, while it
 * has focus, where its bottom is to the nearest [com.gearui.foundation.primitives.GearLazyColumn];
 * that list alone pads its end by the keyboard's overlap and scrolls the field into
 * view. A field outside a list (a chat composer the page lays out itself) touches no
 * list, so a page that already handles the keyboard is not padded twice.
 */
internal class KeyboardAvoider {
    /** The focused field's bottom edge in root pixels, or null when none inside has focus. */
    var focusedBottom: Float? by mutableStateOf(null)
        private set
    private var owner: Any? = null

    fun report(token: Any, bottom: Float) {
        owner = token
        focusedBottom = bottom
    }

    fun clear(token: Any) {
        if (owner === token) {
            owner = null
            focusedBottom = null
        }
    }
}

internal val LocalKeyboardAvoider = staticCompositionLocalOf<KeyboardAvoider?> { null }

/**
 * The software keyboard's height as the platform reports it, where GearUI can observe it
 * itself (iOS, through the keyboard notifications). Elsewhere it stays 0 and the host
 * reports the keyboard through [com.gearui.runtime.RuntimeInsetsBridge.updateKeyboardHeight]
 * (an Android activity's IME insets; the sample and privchat-app both do).
 */
internal object PlatformKeyboard {
    var height: Dp by mutableStateOf(0.dp)
    private var started = false

    fun ensureObserving() {
        if (started) return
        started = true
        observePlatformKeyboard { height = it }
    }
}

/** Starts observing the software keyboard's visible height, if the platform lets GearUI. */
internal expect fun observePlatformKeyboard(onHeight: (Dp) -> Unit)

/** The keyboard height to avoid: what the host reports or what the platform reports, whichever is larger. */
@Composable
internal fun keyboardHeight(): Dp {
    LaunchedEffect(Unit) { PlatformKeyboard.ensureObserving() }
    val host = LocalRuntimeEnvironment.current.keyboard.height
    val platform = PlatformKeyboard.height
    return if (host > platform) host else platform
}

/** For a text field: while [focused], reports its bottom to the list it sits in. */
internal fun Modifier.avoidsKeyboard(focused: Boolean): Modifier = composed {
    val avoider = LocalKeyboardAvoider.current
    val token = remember { Any() }
    var bottom by remember { mutableStateOf<Float?>(null) }
    LaunchedEffect(focused, bottom, avoider) {
        val b = bottom
        if (avoider == null) return@LaunchedEffect
        if (focused && b != null) avoider.report(token, b) else avoider.clear(token)
    }
    DisposableEffect(avoider) { onDispose { avoider?.clear(token) } }
    this.onGloballyPositioned { bottom = it.boundsInRoot().bottom }
}
