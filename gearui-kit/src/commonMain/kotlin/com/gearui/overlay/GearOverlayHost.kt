package com.gearui.overlay

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.gestures.detectDragGestures
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.*
import com.tencent.kuikly.compose.ui.zIndex
import kotlinx.coroutines.delay

/**
 * GearOverlayHost - Overlay 渲染宿主
 *
 * 这是整个 Overlay 系统最关键的组件
 * 所有 Overlay 都在这里渲染，永远在最顶层
 *
 * 职责：
 * - 渲染所有 Overlay
 * - 处理 outsideClick 事件
 * - 处理 scroll（拖拽）关闭 - 当有需要 scroll 关闭的 overlay 时，监听拖拽
 * - 处理 timeout 自动关闭
 * - 绑定 OverlayManager 供外部通知事件
 */
@Composable
fun GearOverlayHost(
    controller: GearOverlayController,
    content: @Composable () -> Unit
) {
    // 绑定 OverlayManager
    DisposableEffect(controller) {
        OverlayManager.bind(controller)
        onDispose {
            OverlayManager.unbind()
        }
    }

    Box(Modifier.fillMaxSize()) {
        // App 正常内容
        content()

        // Overlay 层（永远在最顶层）
        // 滚动关闭由 GearLazyColumn 等组件通过 OverlayManager.notifyScroll() 触发
        controller.items.forEach { item ->
            GearOverlayItemLayout(
                item = item,
                controller = controller
            )
        }
    }
}

/**
 * Overlay 项布局（处理定位）
 */
@Composable
private fun GearOverlayItemLayout(
    item: GearOverlayItem,
    controller: GearOverlayController
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val options = item.options
    val policy = options.dismissPolicy

    var popupSize by remember { mutableStateOf(IntSize.Zero) }
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    // 位置是否已就绪（需要先测量 popupSize）
    val isPositionReady = popupSize != IntSize.Zero && screenSize != IntSize.Zero

    val offset = remember(item.anchorBounds, popupSize, screenSize, item.options) {
        computeOffset(
            anchor = item.anchorBounds,
            popupSize = popupSize,
            screenSize = screenSize,
            options = item.options,
            density = density
        )
    }

    // 处理 timeout 自动关闭
    LaunchedEffect(item.id, policy.timeoutMillis) {
        policy.timeoutMillis?.let { timeout ->
            delay(timeout)
            controller.dismiss(item.id)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .zIndex(1000f + options.zIndex)
            .onSizeChanged { screenSize = it }
    ) {
        // ===== 背景触摸层（用于触摸外部关闭或阻断事件穿透）=====
        if (options.modal || options.maskColor != null) {
            // 有遮罩的情况 - 拦截所有手势
            Box(
                Modifier
                    .fillMaxSize()
                    .background(options.maskColor ?: Color.Black.copy(alpha = 0.32f))
                    // 拦截拖动/滑动事件，阻止穿透
                    .pointerInput(item.id) {
                        detectDragGestures { _, _ ->
                            // 消费拖动事件，不做任何处理
                        }
                    }
                    // 处理点击（根据 policy.outsideClick 决定是否关闭）
                    .clickable(
                        onClick = {
                            if (policy.outsideClick) {
                                controller.dismiss(item.id)
                            }
                        }
                    )
            )
        } else if (policy.outsideClick || policy.scroll) {
            // 透明触摸层 - 处理点击关闭和拖拽关闭
            Box(
                Modifier
                    .fillMaxSize()
                    .pointerInput(item.id) {
                        val dragThreshold = 10f

                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var totalDrag = 0f
                            var isDrag = false

                            // 跟踪移动
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break

                                if (!change.pressed) {
                                    // 手指抬起 - 如果不是拖拽，则是点击
                                    if (!isDrag && policy.outsideClick) {
                                        controller.dismiss(item.id)
                                    }
                                    break
                                }

                                val delta = change.positionChange()
                                totalDrag += kotlin.math.abs(delta.x) + kotlin.math.abs(delta.y)

                                // 移动超过阈值，认为是拖拽
                                if (!isDrag && totalDrag > dragThreshold) {
                                    isDrag = true
                                    if (policy.scroll) {
                                        controller.dismiss(item.id)
                                    }
                                }
                            }
                        }
                    }
            )
        }

        // ===== Overlay 内容（在点击层之上）=====
        // 内容区域需要拦截点击，防止事件穿透到背景层导致关闭
        if (options.placement == GearOverlayPlacement.Fullscreen) {
            // Fullscreen 模式：直接填满整个屏幕，不需要位置计算
            val safeInsets = configuration.safeAreaInsets
            val safeTop = if (options.safeAreaTop) safeInsets.top.dp else 0.dp
            val safeBottom = if (options.safeAreaBottom) safeInsets.bottom.dp else 0.dp
            val safeLeft = if (options.safeAreaLeft) safeInsets.left.dp else 0.dp
            val safeRight = if (options.safeAreaRight) safeInsets.right.dp else 0.dp
            Box(
                Modifier
                    .fillMaxSize()
                    // 拦截点击事件，不让它传递到背景层
                    .clickable(onClick = {
                        // 空操作，只是拦截事件
                    })
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(
                            start = safeLeft,
                            top = safeTop,
                            end = safeRight,
                            bottom = safeBottom
                        )
                ) {
                    item.content()
                }
            }
        } else {
            // 其他模式：需要位置计算和测量
            Box(
                Modifier
                    .offset { offset }
                    .onSizeChanged { popupSize = it }
                    // 位置未就绪时完全透明，就绪后显示
                    .alpha(if (isPositionReady) 1f else 0f)
                    // 拦截点击事件，不让它传递到背景层
                    .clickable(onClick = {
                        // 空操作，只是拦截事件
                    })
            ) {
                item.content()
            }
        }
    }
}

/**
 * 位置计算核心算法
 * 支持 12 种位置 + Center + Fullscreen
 *
 * 修复：
 * 1. 边界约束考虑锚点位置，避免弹层覆盖锚点
 * 2. 自动翻转后正确计算位置
 * 3. 弹层超大时的合理处理
 */
private fun computeOffset(
    anchor: Rect?,
    popupSize: IntSize,
    screenSize: IntSize,
    options: GearOverlayOptions,
    density: Density
): IntOffset {

    if (anchor == null) {
        // 无锚点，使用屏幕居中
        return when (options.placement) {
            GearOverlayPlacement.Center -> IntOffset(
                ((screenSize.width - popupSize.width) / 2).coerceAtLeast(0),
                ((screenSize.height - popupSize.height) / 2).coerceAtLeast(0)
            )
            GearOverlayPlacement.Fullscreen -> IntOffset.Zero
            else -> IntOffset.Zero
        }
    }

    val offsetX = with(density) { options.offsetX.roundToPx() }
    val offsetY = with(density) { options.offsetY.roundToPx() }

    // 计算各方向可用空间（Float）
    val spaceBelow = screenSize.height - anchor.bottom
    val spaceAbove = anchor.top
    val spaceRight = screenSize.width - anchor.right
    val spaceLeft = anchor.left

    // 判断是否需要翻转（基于弹出方向）
    val isVerticalPlacement = options.placement in listOf(
        GearOverlayPlacement.TopLeft, GearOverlayPlacement.TopCenter, GearOverlayPlacement.TopRight, GearOverlayPlacement.TopStart,
        GearOverlayPlacement.BottomLeft, GearOverlayPlacement.BottomCenter, GearOverlayPlacement.BottomRight, GearOverlayPlacement.BottomStart
    )

    val isTopPlacement = options.placement in listOf(
        GearOverlayPlacement.TopLeft, GearOverlayPlacement.TopCenter, GearOverlayPlacement.TopRight, GearOverlayPlacement.TopStart
    )

    val isBottomPlacement = options.placement in listOf(
        GearOverlayPlacement.BottomLeft, GearOverlayPlacement.BottomCenter, GearOverlayPlacement.BottomRight, GearOverlayPlacement.BottomStart
    )

    val isLeftPlacement = options.placement in listOf(
        GearOverlayPlacement.LeftTop, GearOverlayPlacement.LeftCenter, GearOverlayPlacement.LeftBottom
    )

    val isRightPlacement = options.placement in listOf(
        GearOverlayPlacement.RightTop, GearOverlayPlacement.RightCenter, GearOverlayPlacement.RightBottom
    )

    // 决定实际弹出方向（考虑自动翻转）
    val actuallyAbove = when {
        isTopPlacement -> {
            // 原本在上方，检查是否需要翻转到下方
            if (options.autoFlip && spaceAbove < popupSize.height && spaceBelow > spaceAbove) false else true
        }
        isBottomPlacement -> {
            // 原本在下方，检查是否需要翻转到上方
            if (options.autoFlip && spaceBelow < popupSize.height && spaceAbove > spaceBelow) true else false
        }
        else -> false // 左右方向不适用
    }

    val actuallyLeft = when {
        isLeftPlacement -> {
            // 左侧放不下时，如果右侧空间更大就翻转
            val shouldFlip = options.autoFlip && spaceLeft < popupSize.width && spaceRight > spaceLeft
            println("[GearUI] LeftPlacement: spaceLeft=$spaceLeft, popupWidth=${popupSize.width}, spaceRight=$spaceRight, shouldFlip=$shouldFlip")
            !shouldFlip
        }
        isRightPlacement -> {
            // 右侧放不下时，如果左侧空间更大就翻转
            val shouldFlip = options.autoFlip && spaceRight < popupSize.width && spaceLeft > spaceRight
            shouldFlip
        }
        else -> false
    }

    // 计算 X 坐标
    val x = when (options.placement) {
        // 上方/下方 - 左对齐
        GearOverlayPlacement.TopLeft,
        GearOverlayPlacement.BottomLeft,
        GearOverlayPlacement.TopStart,
        GearOverlayPlacement.BottomStart ->
            (anchor.left + offsetX).toInt()

        // 上方/下方 - 居中
        GearOverlayPlacement.TopCenter,
        GearOverlayPlacement.BottomCenter ->
            (anchor.center.x - popupSize.width / 2f + offsetX).toInt()

        // 上方/下方 - 右对齐
        GearOverlayPlacement.TopRight,
        GearOverlayPlacement.BottomRight ->
            (anchor.right - popupSize.width + offsetX).toInt()

        // 左侧 - popup 在 anchor 左边
        GearOverlayPlacement.LeftTop,
        GearOverlayPlacement.LeftCenter,
        GearOverlayPlacement.LeftBottom -> {
            if (actuallyLeft) {
                // 弹层在锚点左边，右边缘对齐锚点左边缘，再减去偏移
                (anchor.left - popupSize.width - offsetX).toInt()
            } else {
                // 翻转到右侧
                (anchor.right + offsetX).toInt()
            }
        }

        // 右侧 - popup 在 anchor 右边
        GearOverlayPlacement.RightTop,
        GearOverlayPlacement.RightCenter,
        GearOverlayPlacement.RightBottom -> {
            if (!actuallyLeft) {
                (anchor.right + offsetX).toInt()
            } else {
                // 翻转到左侧
                (anchor.left - popupSize.width - offsetX).toInt()
            }
        }

        GearOverlayPlacement.Center ->
            ((screenSize.width - popupSize.width) / 2f).toInt()

        GearOverlayPlacement.Fullscreen -> 0
    }

    // 计算 Y 坐标
    val y = when (options.placement) {
        // 上方 - popup 在 anchor 上面
        GearOverlayPlacement.TopLeft,
        GearOverlayPlacement.TopCenter,
        GearOverlayPlacement.TopRight,
        GearOverlayPlacement.TopStart -> {
            if (actuallyAbove) {
                (anchor.top - popupSize.height + offsetY).toInt()
            } else {
                // 翻转到下方
                (anchor.bottom + offsetY).toInt()
            }
        }

        // 下方 - popup 在 anchor 下面
        GearOverlayPlacement.BottomLeft,
        GearOverlayPlacement.BottomCenter,
        GearOverlayPlacement.BottomRight,
        GearOverlayPlacement.BottomStart -> {
            if (!actuallyAbove) {
                (anchor.bottom + offsetY).toInt()
            } else {
                // 翻转到上方
                (anchor.top - popupSize.height + offsetY).toInt()
            }
        }

        // 左侧/右侧 - 顶部对齐
        GearOverlayPlacement.LeftTop,
        GearOverlayPlacement.RightTop ->
            (anchor.top + offsetY).toInt()

        // 左侧/右侧 - 垂直居中
        GearOverlayPlacement.LeftCenter,
        GearOverlayPlacement.RightCenter ->
            (anchor.center.y - popupSize.height / 2f + offsetY).toInt()

        // 左侧/右侧 - 底部对齐
        GearOverlayPlacement.LeftBottom,
        GearOverlayPlacement.RightBottom ->
            (anchor.bottom - popupSize.height + offsetY).toInt()

        GearOverlayPlacement.Center ->
            ((screenSize.height - popupSize.height) / 2f).toInt()

        GearOverlayPlacement.Fullscreen -> 0
    }

    // 边界约束 - 核心修复
    // 对于垂直方向弹出（上/下），约束时要避免覆盖锚点
    val constrainedX: Int
    val constrainedY: Int

    if (isVerticalPlacement) {
        // 垂直弹出：X 方向正常约束，Y 方向要考虑锚点
        constrainedX = x.coerceIn(0, (screenSize.width - popupSize.width).coerceAtLeast(0))

        constrainedY = if (actuallyAbove) {
            // 在上方：底部不能超过 anchor.top
            val maxY = (anchor.top - popupSize.height).toInt()
            val minY = 0
            if (maxY < minY) {
                // 空间不足，贴顶显示
                minY
            } else {
                y.coerceIn(minY, maxY)
            }
        } else {
            // 在下方：顶部不能小于 anchor.bottom
            val minY = anchor.bottom.toInt()
            val maxY = (screenSize.height - popupSize.height).coerceAtLeast(minY)
            y.coerceIn(minY, maxY)
        }
    } else if (isLeftPlacement || isRightPlacement) {
        // 水平弹出：Y 方向正常约束，X 方向要考虑锚点
        constrainedY = y.coerceIn(0, (screenSize.height - popupSize.height).coerceAtLeast(0))

        constrainedX = if (actuallyLeft) {
            // 在左侧：弹层右边缘紧贴锚点左边缘
            // x = anchor.left - popupSize.width - offsetX
            // 如果空间不足，让弹层超出屏幕左边，不要压住锚点
            x // 不做约束，直接用计算好的位置（可能是负数，超出屏幕左边）
        } else {
            // 在右侧：弹层左边缘紧贴锚点右边缘
            // 如果空间不足，让弹层超出屏幕右边，不要压住锚点
            x // 不做约束
        }
    } else {
        // 居中或全屏：正常约束
        constrainedX = x.coerceIn(0, (screenSize.width - popupSize.width).coerceAtLeast(0))
        constrainedY = y.coerceIn(0, (screenSize.height - popupSize.height).coerceAtLeast(0))
    }

    return IntOffset(constrainedX, constrainedY)
}
