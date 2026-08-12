package com.gearui.foundation.primitives

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
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.compose.ui.unit.dp
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
    content: LazyListScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = modifier.pointerInput(Unit) {
            val dragThreshold = 10f // 拖拽阈值（像素）

            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                var totalDrag = 0f
                var notified = false

                // Keep tracking movement until the finger lifts
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull() ?: break

                    if (!change.pressed) break // 手指抬起

                    val delta = change.positionChange()
                    totalDrag += abs(delta.x) + abs(delta.y)

                    // Moved past the threshold: treat it as a drag and notify
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
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        userScrollEnabled = userScrollEnabled,
        content = content
    )
}

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
        modifier = modifier.pointerInput(Unit) {
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
        userScrollEnabled = userScrollEnabled,
        content = content
    )
}
