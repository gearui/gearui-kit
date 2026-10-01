package com.gearui.foundation.primitives

import com.gearui.gestures.ownsHorizontalDrag
import kotlinx.coroutines.delay
import com.tencent.kuikly.compose.foundation.gestures.animateScrollBy
import com.tencent.kuikly.compose.ui.unit.LayoutDirection
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.gearui.overlay.LocalOverlayViewportSize
import com.gearui.foundation.keyboard.keyboardHeight
import com.gearui.foundation.keyboard.LocalKeyboardAvoider
import com.gearui.foundation.keyboard.KeyboardAvoider
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.LazyListScope
import com.tencent.kuikly.compose.foundation.lazy.LazyListState
import com.tencent.kuikly.compose.foundation.lazy.LazyRow
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.overlay.LocalInputBlockedByOverlay
import com.gearui.overlay.OverlayManager
import kotlin.math.abs

/**
 * GearLazyColumn - wrapped vertical lazy list
 *
 * On top of the plain LazyColumn it adds:
 * - notifying OverlayManager when the user drags, so floating layers dismiss on scroll
 * - awaitFirstDown plus movement detection, so a drag is told apart from a tap
 *
 * How it works:
 * - it listens for "the user started dragging", not for "a tap"
 * - dismissal only fires once the finger moves past a threshold
 * - so tapping a Select trigger does not dismiss it by accident
 *
 * Used exactly like LazyColumn
 */
@Composable
fun GearLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    userScrollEnabled: Boolean = true,
    /**
     * Keep a focused text field inside this list above the software keyboard, as iOS
     * does: the list pads its end by the keyboard's overlap and scrolls the field into
     * view. Only fields inside this list count. Turn off for a list whose page already
     * moves its content for the keyboard.
     */
    avoidKeyboard: Boolean = true,
    content: LazyListScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val avoider = remember { KeyboardAvoider() }
    var listBottomInRoot by remember { mutableStateOf(0f) }
    val keyboardPx = with(density) { keyboardHeight().toPx() }
    val screenHeight = LocalOverlayViewportSize.current.height.toFloat()
    val focusedBottom = avoider.focusedBottom
    val keyboardTop = screenHeight - keyboardPx
    val avoiding = avoidKeyboard && keyboardPx > 0f && screenHeight > 0f && focusedBottom != null
    // Room at the end for the part of the list the keyboard covers, so the last fields
    // can scroll above it.
    val overlap = if (avoiding) (listBottomInRoot - keyboardTop).coerceAtLeast(0f) else 0f
    val margin = with(density) { KeyboardAvoidanceMargin.toPx() }
    // Keyed on which field has focus and on the geometry, never on the field's position:
    // scrolling moves the field, and restarting on that cancelled the scroll every frame.
    LaunchedEffect(avoiding, avoider.focusedToken, keyboardTop, listBottomInRoot) {
        if (!avoiding) return@LaunchedEffect
        val visibleBottom = minOf(listBottomInRoot, keyboardTop) - margin
        // The end padding for the keyboard is laid out a frame after it is asked for; a
        // short page cannot scroll until then. Scroll, let a frame pass, re-read where the
        // field is, until it is above the keyboard.
        repeat(10) {
            val remaining = (avoider.focusedBottom ?: return@LaunchedEffect) - visibleBottom
            if (remaining <= 1f) return@LaunchedEffect
            state.animateScrollBy(remaining)
            delay(16)
        }
    }
    val paddedContent = if (overlap > 0f) {
        PaddingValues(
            start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
            top = contentPadding.calculateTopPadding(),
            end = contentPadding.calculateRightPadding(LayoutDirection.Ltr),
            bottom = contentPadding.calculateBottomPadding() + with(density) { overlap.toDp() },
        )
    } else contentPadding

    // Only genuine finger displacement dismisses focus/overlays. Measure in
    // root coordinates: keyboard/reply layout shifts change local coordinates
    // while a finger stays still, incorrectly cancelling long presses/focus.
    // Adding boundsInRoot cancels that shift. isScrollInProgress is unsuitable:
    // programmatic scrolling also sets it and would dismiss the keyboard just
    // after an input gains focus.
    var listOriginInRoot by remember { mutableStateOf(Offset.Zero) }

    CompositionLocalProvider(LocalKeyboardAvoider provides avoider) {
    LazyColumn(
        modifier = modifier
            .onGloballyPositioned {
                val bounds = it.boundsInRoot()
                listOriginInRoot = bounds.topLeft
                listBottomInRoot = bounds.bottom
            }
            .pointerInput(Unit) {
                val dragThreshold = 10f

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val startRoot = listOriginInRoot + down.position
                    var notified = false

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break
                        if (!change.pressed) break

                        val moved = (listOriginInRoot + change.position) - startRoot
                        if (!notified && moved.getDistance() > dragThreshold) {
                            OverlayManager.notifyScroll()
                            focusManager.clearFocus()
                            notified = true
                        }
                    }
                }
            },
        state = state,
        contentPadding = paddedContent,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        // 🔴 An overlay covering the page freezes this list — a native scroll view
        // ignores consumed pointer events, so this flag is the only thing that stops it.
        userScrollEnabled = userScrollEnabled && !LocalInputBlockedByOverlay.current,
        content = content
    )
    }
}

/** Space kept between a focused field and the keyboard. */
private val KeyboardAvoidanceMargin = 12.dp

/**
 * GearLazyRow - wrapped horizontal lazy list
 */
@Composable
fun GearLazyRow(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    userScrollEnabled: Boolean = true,
    content: LazyListScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current

    LazyRow(
        modifier = modifier.ownsHorizontalDrag { state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 0 }.pointerInput(Unit) {
            val dragThreshold = 10f

            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                var totalDrag = 0f
                var notified = false

                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull() ?: break

                    if (!change.pressed) break

                    val delta = change.positionChange()
                    totalDrag += abs(delta.x) + abs(delta.y)

                    if (!notified && totalDrag > dragThreshold) {
                        OverlayManager.notifyScroll()
                        focusManager.clearFocus()
                        notified = true
                    }
                }
            }
        },
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        // 🔴 An overlay covering the page freezes this list — a native scroll view
        // ignores consumed pointer events, so this flag is the only thing that stops it.
        userScrollEnabled = userScrollEnabled && !LocalInputBlockedByOverlay.current,
        content = content
    )
}
