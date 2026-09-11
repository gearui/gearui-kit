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
    content: LazyListScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current

    // 手指真实位移超过阈值才算"用户在滚列表"：收焦点、通知弹层关闭。
    //
    // 🔴 位移要按**屏幕坐标**算。
    //
    // 组件内坐标会被布局重排骗到：收键盘、插入一条回复引用，整个列表平移几百像素，手指
    // 明明没动也会被判成滑动，于是"按住一条消息"被当成滚动，焦点被收、长按被取消。
    // 用列表自身的 boundsInRoot 把触点换算回屏幕坐标，重排时两边同步位移，差值为零。
    //
    // 也不能改用 state.isScrollInProgress：页面自己的 animateScrollToItem（键盘弹出时滚到
    // 底部）同样会让它为 true，于是刚点上输入框就被收掉焦点，键盘再也起不来。
    var listOriginInRoot by remember { mutableStateOf(Offset.Zero) }

    LazyColumn(
        modifier = modifier
            .onGloballyPositioned { listOriginInRoot = it.boundsInRoot().topLeft }
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
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        // 🔴 An overlay covering the page freezes this list — a native scroll view
        // ignores consumed pointer events, so this flag is the only thing that stops it.
        userScrollEnabled = userScrollEnabled && !LocalInputBlockedByOverlay.current,
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
        // 🔴 An overlay covering the page freezes this list — a native scroll view
        // ignores consumed pointer events, so this flag is the only thing that stops it.
        userScrollEnabled = userScrollEnabled && !LocalInputBlockedByOverlay.current,
        content = content
    )
}
