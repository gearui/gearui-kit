package com.gearui.gestures

import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.input.pointer.PointerInputChange
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.ui.input.pointer.util.VelocityTracker
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import kotlin.math.abs

/**
 * 边缘返回手势配置
 *
 * @param edgeWidthDp          左边缘热区宽度（dp），触摸起始点须在此范围内
 * @param commitDistanceDp     位移提交阈值（dp），超过此值视为用户确认返回
 * @param minFlingDistanceDp   最小 fling 位移（dp），速度足够时允许更短位移触发提交
 * @param flingVelocityDpPerSec  fling 速度阈值（dp/s）
 * @param directionRatio       水平意图比：abs(dx) 须大于 abs(dy) * ratio，排除垂直滑动误触
 */
data class SwipeBackConfig(
    val edgeWidthDp: Float = 24f,
    val commitDistanceDp: Float = 96f,
    val minFlingDistanceDp: Float = 24f,
    val flingVelocityDpPerSec: Float = 1200f,
    val directionRatio: Float = 1.2f,
)

/**
 * 为任意 Composable 附加左边缘右滑返回手势。
 *
 * gearui-kit 只负责识别手势过程并回调，不感知任何导航状态。
 *
 * 手势状态机：
 *   Idle → Tracking（触摸在边缘内）
 *        → Recognized（通过水平意图 + touch-slop 判定）→ 开始 consume 事件
 *        → Committed（位移 or 速度满足阈值）→ onCommit()
 *        or Cancelled（抬手未达阈值 or 识别失败）→ onCancel()
 *
 * consume 时机：仅在 Recognized 之后才接管事件，不会提前抢夺子组件的手势。
 *
 * @param enabled       false 时完全跳过手势识别（根页面、模态页等）
 * @param config        手势参数，可按平台或页面类型调整
 * @param onStart       手势识别成功时回调（Recognized 阶段）
 * @param onProgress    拖动过程中持续回调；progress ∈ [0f, 1f]（相对 commitDistance），dragX 为绝对像素偏移
 * @param onCancel      用户放手但未达提交条件
 * @param onCommit      满足提交条件，应在此执行返回导航
 */
fun Modifier.swipeBack(
    enabled: Boolean = true,
    config: SwipeBackConfig = SwipeBackConfig(),
    onStart: (() -> Unit)? = null,
    onProgress: ((progress: Float, dragX: Float) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onCommit: () -> Unit,
): Modifier {
    if (!enabled) return this
    return this.pointerInput(enabled, config) {
        val edgePx = config.edgeWidthDp * density
        val commitPx = config.commitDistanceDp * density
        val minFlingPx = config.minFlingDistanceDp * density
        val flingVelocityPxPerSec = config.flingVelocityDpPerSec * density

        awaitEachGesture {
            // ── Idle → Tracking ──────────────────────────────────────────
            val down = awaitFirstDown(requireUnconsumed = false)
            if (down.position.x > edgePx) return@awaitEachGesture   // 不在边缘，忽略

            val startX = down.position.x
            val startY = down.position.y
            var totalDx = 0f
            var totalDy = 0f
            var recognized = false
            val velocityTracker = VelocityTracker()
            velocityTracker.addPosition(down.uptimeMillis, down.position)

            // ── Tracking：等待 horizontal touch-slop 或取消 ──────────────
            val slopChange: PointerInputChange? =
                awaitHorizontalTouchSlopOrCancellation(down.id) { change, overSlop ->
                    totalDx += overSlop
                    totalDy = change.position.y - startY
                    // 垂直意图过强时拒绝识别
                    if (abs(totalDx) > abs(totalDy) * config.directionRatio && totalDx > 0f) {
                        change.consume()
                        recognized = true
                    }
                }

            if (!recognized || slopChange == null) return@awaitEachGesture

            // ── Recognized：接管后续事件 ─────────────────────────────────
            onStart?.invoke()

            var dragX = totalDx
            velocityTracker.addPosition(slopChange.uptimeMillis, slopChange.position)

            // 持续读取 move 事件
            var pointer = slopChange.id
            var committed = false
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull { it.id == pointer } ?: break
                if (!change.pressed) break  // 抬手

                val dx = change.positionChange().x
                val dy = change.positionChange().y
                // 过程中若垂直分量远大于水平，取消识别
                dragX += dx
                if (dragX < 0f) dragX = 0f  // 不允许往左滑

                velocityTracker.addPosition(change.uptimeMillis, change.position)
                change.consume()

                val progress = (dragX / commitPx).coerceIn(0f, 1f)
                onProgress?.invoke(progress, dragX)
            }

            // ── End：判断 commit or cancel ───────────────────────────────
            val velocity = velocityTracker.calculateVelocity()
            val vx = velocity.x
            val isFling = vx >= flingVelocityPxPerSec && dragX >= minFlingPx
            val isCommit = dragX >= commitPx || isFling

            if (isCommit) {
                onCommit()
            } else {
                onCancel?.invoke()
            }
        }
    }
}
