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
 * GearLazyColumn - 封装的垂直懒加载列表
 *
 * 相比原生 LazyColumn 增加了：
 * - 用户拖拽时自动通知 OverlayManager，支持滚动关闭弹层
 * - 使用 awaitFirstDown + 移动检测，确保是拖拽而非点击
 *
 * 核心原理：
 * - 监听的是"用户开始拖拽"，不是"点击"
 * - 只有当手指移动超过阈值时才触发关闭
 * - 这样点击 Select 触发器不会误触发关闭
 *
 * 使用方式与 LazyColumn 完全相同
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

                // 持续跟踪移动，直到手指抬起
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull() ?: break

                    if (!change.pressed) break // 手指抬起

                    val delta = change.positionChange()
                    totalDrag += abs(delta.x) + abs(delta.y)

                    // 移动超过阈值，认为是拖拽，通知关闭
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
 * GearLazyRow - 封装的水平懒加载列表
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
