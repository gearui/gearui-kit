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
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.runtime.LocalRuntimeFlags
import kotlinx.coroutines.delay

/**
 * OverlayHost - the render host for overlays.
 *
 * The most load-bearing piece of the overlay system: every overlay renders
 * here, always on top of everything else.
 *
 * Responsibilities:
 * - render every overlay
 * - handle outside-click dismissal
 * - handle scroll (drag) dismissal, by listening for drags whenever an overlay asks for it
 * - handle timeout dismissal
 * - bind OverlayManager so callers can notify it of events
 */
@Composable
fun OverlayHost(
    controller: OverlayController,
    content: @Composable () -> Unit
) {
    // Bind the OverlayManager
    DisposableEffect(controller) {
        OverlayManager.bind(controller)
        onDispose {
            OverlayManager.unbind()
        }
    }

    Box(Modifier.fillMaxSize()) {
        // Normal app content
        content()

        // Overlay layer, always on top.
        // Scroll dismissal is triggered by components such as GearLazyColumn via OverlayManager.notifyScroll().
        controller.items.forEach { item ->
            OverlayItemLayout(
                item = item,
                controller = controller
            )
        }
    }
}

/**
 * Overlay item layout, including positioning.
 */
@Composable
private fun OverlayItemLayout(
    item: OverlayItem,
    controller: OverlayController
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val runtimeFlags = LocalRuntimeFlags.current
    val runtimeEnvironment = LocalRuntimeEnvironment.current
    val options = item.options
    val policy = options.dismissPolicy

    var popupSize by remember { mutableStateOf(IntSize.Zero) }
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    // Whether the position is settled; popupSize has to be measured first.
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

    // Timeout dismissal
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
        // ===== Backdrop touch layer: outside-click dismissal and event blocking =====
        if (options.modal || options.maskColor != null) {
            // With a scrim: intercept every gesture so nothing reaches scrollable content behind.
            Box(
                Modifier
                    .fillMaxSize()
                    .background(options.maskColor ?: OverlayDefaults.scrimColor)
                    // Consume every pointer change up front so a LazyColumn behind
                    // never sees them. detectDragGestures is not enough — it lets the
                    // down event reach the layer below first, where the native scroll
                    // view takes it.
                    .pointerInput(item.id) {
                        // 🔴 tap 判定必须在**这同一个**手势循环里做。这层为了不让底下的
                        // LazyColumn 滚动，把所有指针事件都 consume 掉——后挂的 .clickable
                        // 等的是未消费的 down，永远等不到，点遮罩关闭（outsideClick）就
                        // 整个失效：sheet/picker 全都点空白关不掉（iOS 的手势语义是
                        // sheet 点遮罩即关，alert 才不关）。
                        val dragThreshold = 10f
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            down.consume()
                            var totalDrag = 0f
                            while (true) {
                                val event = awaitPointerEvent()
                                event.changes.forEach {
                                    totalDrag += kotlin.math.abs(it.positionChange().x) +
                                        kotlin.math.abs(it.positionChange().y)
                                    it.consume()
                                }
                                if (event.changes.all { !it.pressed }) {
                                    // 抬指且没拖动 = tap；按 policy 决定是否关闭。
                                    if (totalDrag <= dragThreshold && policy.outsideClick) {
                                        controller.dismiss(item.id)
                                    }
                                    break
                                }
                            }
                        }
                    }
            )
        } else if (policy.outsideClick || policy.scroll) {
            // Transparent touch layer for click and drag dismissal.
            Box(
                Modifier
                    .fillMaxSize()
                    .pointerInput(item.id) {
                        val dragThreshold = 10f

                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var totalDrag = 0f
                            var isDrag = false

                            // Track movement
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break

                                if (!change.pressed) {
                                // Finger lifted: if it was not a drag, it was a tap.
                                    if (!isDrag && policy.outsideClick) {
                                        controller.dismiss(item.id)
                                    }
                                    break
                                }

                                val delta = change.positionChange()
                                totalDrag += kotlin.math.abs(delta.x) + kotlin.math.abs(delta.y)

                                // Moved past the threshold, so treat it as a drag.
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

        // ===== Overlay content, above the touch layer =====
        // Content must intercept clicks, or they fall through to the backdrop and dismiss it.
        if (options.placement == OverlayPlacement.Fullscreen) {
            // Fullscreen: fills the screen, no position calculation needed.
            val safeTop = if (options.safeAreaTop) {
                if (runtimeFlags.unifiedSafeAreaPipeline) {
                    runtimeEnvironment.safeArea.top
                } else {
                    configuration.safeAreaInsets.top.dp
                }
            } else {
                0.dp
            }
            val safeBottom = if (options.safeAreaBottom) {
                if (runtimeFlags.unifiedSafeAreaPipeline) {
                    runtimeEnvironment.safeArea.bottom
                } else {
                    configuration.safeAreaInsets.bottom.dp
                }
            } else {
                0.dp
            }
            val safeLeft = if (options.safeAreaLeft) {
                if (runtimeFlags.unifiedSafeAreaPipeline) {
                    runtimeEnvironment.safeArea.left
                } else {
                    configuration.safeAreaInsets.left.dp
                }
            } else {
                0.dp
            }
            val safeRight = if (options.safeAreaRight) {
                if (runtimeFlags.unifiedSafeAreaPipeline) {
                    runtimeEnvironment.safeArea.right
                } else {
                    configuration.safeAreaInsets.right.dp
                }
            } else {
                0.dp
            }
            // passThroughOutside: a non-modal banner such as an in-app notification must
            // not freeze the whole screen — gestures outside its content belong to the
            // page below. The default is still to intercept, so modal and dialog
            // semantics are unchanged.
            val fullscreenModifier = if (options.passThroughOutside) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .fillMaxSize()
                    // 🔴 这层铺满全屏、压在 backdrop 之上——backdrop 的 outsideClick
                    // 永远收不到事件。所以「点空白关闭」必须由**这里**执行，而不是
                    // 只做无脑拦截：sheet/picker 点内容之外即关（iOS 手势语义），
                    // 真正的面板内容（surface）自己消费点击，不会冒泡到这。
                    .clickable(onClick = {
                        if (policy.outsideClick) {
                            controller.dismiss(item.id)
                        }
                    })
            }
            Box(fullscreenModifier) {
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
            // Other placements need measurement and position calculation.
            Box(
                Modifier
                    .offset { offset }
                    .onSizeChanged { popupSize = it }
                    // Fully transparent until the position is settled, then shown.
                    .alpha(if (isPositionReady) 1f else 0f)
                    // Intercept clicks so they do not reach the backdrop.
                    .clickable(onClick = {
                        // Intentionally empty: interception is the point.
                    })
            ) {
                item.content()
            }
        }
    }
}

/**
 * Position calculation.
 * Supports 12 placements plus Center and Fullscreen.
 *
 * Fixes:
 * 1. Boundary clamping accounts for the anchor, so the overlay never covers it.
 * 2. Positions are recalculated correctly after an automatic flip.
 * 3. Sensible handling when the overlay is larger than the space available.
 */
private fun computeOffset(
    anchor: Rect?,
    popupSize: IntSize,
    screenSize: IntSize,
    options: OverlayOptions,
    density: Density
): IntOffset {

    if (anchor == null) {
        // No anchor: centre on screen.
        return when (options.placement) {
            OverlayPlacement.Center -> IntOffset(
                ((screenSize.width - popupSize.width) / 2).coerceAtLeast(0),
                ((screenSize.height - popupSize.height) / 2).coerceAtLeast(0)
            )
            OverlayPlacement.Fullscreen -> IntOffset.Zero
            else -> IntOffset.Zero
        }
    }

    val offsetX = with(density) { options.offsetX.roundToPx() }
    val offsetY = with(density) { options.offsetY.roundToPx() }

    // Available space in each direction (Float)
    val spaceBelow = screenSize.height - anchor.bottom
    val spaceAbove = anchor.top
    val spaceRight = screenSize.width - anchor.right
    val spaceLeft = anchor.left

    // Decide whether a flip is needed, based on the requested direction.
    val isVerticalPlacement = options.placement in listOf(
        OverlayPlacement.TopLeft, OverlayPlacement.TopCenter, OverlayPlacement.TopRight,
        OverlayPlacement.BottomLeft, OverlayPlacement.BottomCenter, OverlayPlacement.BottomRight
    )

    val isTopPlacement = options.placement in listOf(
        OverlayPlacement.TopLeft, OverlayPlacement.TopCenter, OverlayPlacement.TopRight
    )

    val isBottomPlacement = options.placement in listOf(
        OverlayPlacement.BottomLeft, OverlayPlacement.BottomCenter, OverlayPlacement.BottomRight
    )

    val isLeftPlacement = options.placement in listOf(
        OverlayPlacement.LeftTop, OverlayPlacement.LeftCenter, OverlayPlacement.LeftBottom
    )

    val isRightPlacement = options.placement in listOf(
        OverlayPlacement.RightTop, OverlayPlacement.RightCenter, OverlayPlacement.RightBottom
    )

    // Resolve the actual direction, taking auto-flip into account.
    val actuallyAbove = when {
        isTopPlacement -> {
            // Requested above: check whether it needs to flip below.
            if (options.autoFlip && spaceAbove < popupSize.height && spaceBelow > spaceAbove) false else true
        }
        isBottomPlacement -> {
            // Requested below: check whether it needs to flip above.
            if (options.autoFlip && spaceBelow < popupSize.height && spaceAbove > spaceBelow) true else false
        }
        else -> false // 左右方向不适用
    }

    val actuallyLeft = when {
        isLeftPlacement -> {
            // Does not fit on the left; flip if there is more room on the right.
            val shouldFlip = options.autoFlip && spaceLeft < popupSize.width && spaceRight > spaceLeft
            println("[GearUI] LeftPlacement: spaceLeft=$spaceLeft, popupWidth=${popupSize.width}, spaceRight=$spaceRight, shouldFlip=$shouldFlip")
            !shouldFlip
        }
        isRightPlacement -> {
            // Does not fit on the right; flip if there is more room on the left.
            val shouldFlip = options.autoFlip && spaceRight < popupSize.width && spaceLeft > spaceRight
            shouldFlip
        }
        else -> false
    }

    // X coordinate
    val x = when (options.placement) {
        // Above / below, left aligned
        OverlayPlacement.TopLeft,
        OverlayPlacement.BottomLeft ->
            (anchor.left + offsetX).toInt()

        // Above / below, centred
        OverlayPlacement.TopCenter,
        OverlayPlacement.BottomCenter ->
            (anchor.center.x - popupSize.width / 2f + offsetX).toInt()

        // Above / below, right aligned
        OverlayPlacement.TopRight,
        OverlayPlacement.BottomRight ->
            (anchor.right - popupSize.width + offsetX).toInt()

        // Left: the popup sits to the left of the anchor.
        OverlayPlacement.LeftTop,
        OverlayPlacement.LeftCenter,
        OverlayPlacement.LeftBottom -> {
            if (actuallyLeft) {
                // Right edge aligns to the anchor's left edge, minus the offset.
                (anchor.left - popupSize.width - offsetX).toInt()
            } else {
                // Flipped to the right
                (anchor.right + offsetX).toInt()
            }
        }

        // Right: the popup sits to the right of the anchor.
        OverlayPlacement.RightTop,
        OverlayPlacement.RightCenter,
        OverlayPlacement.RightBottom -> {
            if (!actuallyLeft) {
                (anchor.right + offsetX).toInt()
            } else {
                // Flipped to the left
                (anchor.left - popupSize.width - offsetX).toInt()
            }
        }

        OverlayPlacement.Center ->
            ((screenSize.width - popupSize.width) / 2f).toInt()

        OverlayPlacement.Fullscreen -> 0
    }

    // Y coordinate
    val y = when (options.placement) {
        // Above: the popup sits above the anchor.
        OverlayPlacement.TopLeft,
        OverlayPlacement.TopCenter,
        OverlayPlacement.TopRight -> {
            if (actuallyAbove) {
                (anchor.top - popupSize.height + offsetY).toInt()
            } else {
                // Flipped below
                (anchor.bottom + offsetY).toInt()
            }
        }

        // Below: the popup sits below the anchor.
        OverlayPlacement.BottomLeft,
        OverlayPlacement.BottomCenter,
        OverlayPlacement.BottomRight -> {
            if (!actuallyAbove) {
                (anchor.bottom + offsetY).toInt()
            } else {
                // Flipped above
                (anchor.top - popupSize.height + offsetY).toInt()
            }
        }

        // Left / right, top aligned
        OverlayPlacement.LeftTop,
        OverlayPlacement.RightTop ->
            (anchor.top + offsetY).toInt()

        // Left / right, vertically centred
        OverlayPlacement.LeftCenter,
        OverlayPlacement.RightCenter ->
            (anchor.center.y - popupSize.height / 2f + offsetY).toInt()

        // Left / right, bottom aligned
        OverlayPlacement.LeftBottom,
        OverlayPlacement.RightBottom ->
            (anchor.bottom - popupSize.height + offsetY).toInt()

        OverlayPlacement.Center ->
            ((screenSize.height - popupSize.height) / 2f).toInt()

        OverlayPlacement.Fullscreen -> 0
    }

    // Boundary clamping — the core of the fix.
    // For vertical placements, clamping must avoid covering the anchor.
    val constrainedX: Int
    val constrainedY: Int

    if (isVerticalPlacement) {
        // Vertical: clamp X normally; Y has to respect the anchor.
        constrainedX = x.coerceIn(0, (screenSize.width - popupSize.width).coerceAtLeast(0))

        constrainedY = if (actuallyAbove) {
            // Above: the bottom must not pass anchor.top.
            val maxY = (anchor.top - popupSize.height).toInt()
            val minY = 0
            if (maxY < minY) {
                // Not enough room, so pin to the top of the screen.
                minY
            } else {
                y.coerceIn(minY, maxY)
            }
        } else {
            // Below: the top must not go above anchor.bottom.
            val minY = anchor.bottom.toInt()
            val maxY = (screenSize.height - popupSize.height).coerceAtLeast(minY)
            y.coerceIn(minY, maxY)
        }
    } else if (isLeftPlacement || isRightPlacement) {
        // Horizontal: clamp Y normally; X has to respect the anchor.
        constrainedY = y.coerceIn(0, (screenSize.height - popupSize.height).coerceAtLeast(0))

        constrainedX = if (actuallyLeft) {
            // Left: the popup's right edge meets the anchor's left edge.
            // x = anchor.left - popupSize.width - offsetX
            // If there is not enough room, let it overflow off-screen rather than cover the anchor.
            x // 不做约束，直接用计算好的位置（可能是负数，超出屏幕左边）
        } else {
            // Right: the popup's left edge meets the anchor's right edge.
            // If there is not enough room, let it overflow off-screen rather than cover the anchor.
            x // 不做约束
        }
    } else {
        // Centre or fullscreen: clamp normally.
        constrainedX = x.coerceIn(0, (screenSize.width - popupSize.width).coerceAtLeast(0))
        constrainedY = y.coerceIn(0, (screenSize.height - popupSize.height).coerceAtLeast(0))
    }

    return IntOffset(constrainedX, constrainedY)
}
