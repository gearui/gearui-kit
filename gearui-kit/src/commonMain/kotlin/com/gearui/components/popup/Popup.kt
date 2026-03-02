package com.gearui.components.popup

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.overlay.GearOverlayOptions
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.overlay.LocalGearOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.theme.Theme

/**
 * Popup - 锚点浮层基类
 *
 * 所有锚点定位的浮层组件的基础：
 * - Dropdown
 * - Tooltip
 * - Popover
 * - SelectMenu
 * - ContextMenu
 *
 * 特点：
 * - 非模态（不阻断交互）
 * - 锚点定位（相对于触发元素）
 * - 可选遮罩
 * - 点击外部关闭
 */
object Popup {

    /**
     * 声明式 Popup
     *
     * @param visible 是否显示
     * @param anchorBounds 锚点位置
     * @param placement 弹出位置
     * @param offsetX X 轴偏移
     * @param offsetY Y 轴偏移
     * @param dismissOnOutside 点击外部是否关闭
     * @param autoFlip 空间不足时是否自动翻转
     * @param onDismiss 关闭回调
     * @param content 内容
     */
    @Composable
    fun Host(
        visible: Boolean,
        anchorBounds: Rect?,
        placement: GearOverlayPlacement = GearOverlayPlacement.BottomStart,
        offsetX: Dp = 0.dp,
        offsetY: Dp = 4.dp,
        dismissOnOutside: Boolean = true,
        autoFlip: Boolean = true,
        onDismiss: () -> Unit = {},
        content: @Composable () -> Unit
    ) {
        val controller = LocalGearOverlayController.current
        var overlayId by remember { mutableStateOf<Long?>(null) }

        // 记录打开时的锚点位置，用于检测滚动
        var anchorBoundsWhenOpened by remember { mutableStateOf<Rect?>(null) }

        // 保持 onDismiss 回调最新
        val onDismissState = rememberUpdatedState(onDismiss)

        // 监听 visible 变化
        LaunchedEffect(visible) {
            if (visible) {
                // 居中弹出时 anchorBounds 可以为 null
                // 锚点弹出时需要 anchorBounds
                val needsAnchor = placement != GearOverlayPlacement.Center &&
                                  placement != GearOverlayPlacement.Fullscreen

                if (needsAnchor && anchorBounds == null) {
                    // 需要锚点但没有，不显示
                    return@LaunchedEffect
                }

                overlayId = controller.show(
                    anchorBounds = anchorBounds,
                    options = GearOverlayOptions(
                        placement = placement,
                        offsetX = offsetX,
                        offsetY = offsetY,
                        modal = false,
                        autoFlip = autoFlip,
                        dismissPolicy = OverlayDismissPolicy.Dropdown.copy(
                            outsideClick = dismissOnOutside
                        )
                    ),
                    onDismiss = { onDismissState.value() }
                ) {
                    PopupSurface(content = content)
                }
                anchorBoundsWhenOpened = anchorBounds
            } else {
                overlayId?.let { controller.dismiss(it) }
                overlayId = null
                anchorBoundsWhenOpened = null
            }
        }

        // 检测滚动：当 anchorBounds 变化且 Overlay 打开时，关闭 Overlay
        LaunchedEffect(anchorBounds) {
            if (visible && overlayId != null && anchorBoundsWhenOpened != null && anchorBounds != null) {
                val openedBounds = anchorBoundsWhenOpened!!
                val currentBounds = anchorBounds
                // 如果位置发生了明显变化（超过 1 像素），说明发生了滚动
                if (kotlin.math.abs(openedBounds.top - currentBounds.top) > 1f ||
                    kotlin.math.abs(openedBounds.left - currentBounds.left) > 1f) {
                    overlayId?.let { controller.dismiss(it) }
                }
            }
        }

        // 组件销毁时关闭
        DisposableEffect(Unit) {
            onDispose {
                overlayId?.let { controller.dismiss(it) }
            }
        }
    }
}

/**
 * PopupSurface - Popup 统一视觉容器
 */
@Composable
internal fun PopupSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    Box(
        modifier = modifier
            .clip(shapes.small)
            .background(colors.surface)
            .border(1.dp, colors.border, shapes.small)
    ) {
        content()
    }
}
