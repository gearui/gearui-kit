package com.gearui.components.dialog

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.overlay.GearOverlayOptions
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.overlay.LocalGearOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.theme.Theme
import com.gearui.Spacing

/**
 * Dialog - 模态弹窗基类
 *
 * 所有模态弹窗的基础：
 * - Dialog
 * - ConfirmDialog
 * - AlertDialog
 * - InputDialog
 * - ImageDialog
 *
 * 特点：
 * - 模态（阻断交互）
 * - 居中显示
 * - 有遮罩
 * - 可选点击外部关闭
 */
object Dialog {

    /**
     * 声明式 Dialog
     *
     * @param visible 是否显示
     * @param dismissOnOutside 点击外部是否关闭
     * @param maskColor 遮罩颜色
     * @param onDismiss 关闭回调
     * @param content 内容
     */
    @Composable
    fun Host(
        visible: Boolean,
        dismissOnOutside: Boolean = false,
        maskColor: Color? = null,
        onDismiss: () -> Unit = {},
        content: @Composable () -> Unit
    ) {
        val colors = Theme.colors
        val controller = LocalGearOverlayController.current
        val effectiveMaskColor = maskColor ?: colors.mask
        var overlayId by remember { mutableStateOf<Long?>(null) }

        LaunchedEffect(visible) {
            if (visible) {
                overlayId = controller.show(
                    anchorBounds = null,
                    options = GearOverlayOptions(
                        placement = GearOverlayPlacement.Center,
                        modal = true,
                        maskColor = effectiveMaskColor,
                        dismissPolicy = OverlayDismissPolicy.Modal.copy(
                            outsideClick = dismissOnOutside
                        )
                    ),
                    onDismiss = onDismiss
                ) {
                    DialogSurface(content = content)
                }
            } else {
                overlayId?.let { controller.dismiss(it) }
                overlayId = null
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                overlayId?.let { controller.dismiss(it) }
            }
        }
    }
}

/**
 * DialogSurface - Dialog 统一视觉容器
 */
@Composable
internal fun DialogSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    Box(
        modifier = modifier
            .widthIn(min = 280.dp, max = 360.dp)
            .shadow(6.dp, shapes.large)
            .background(colors.surface, shapes.large)
            .border(1.dp, colors.border, shapes.large)
    ) {
        content()
    }
}

/**
 * DialogContent - Dialog 内容布局辅助
 */
@Composable
fun DialogContent(
    title: String? = null,
    message: String? = null,
    content: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit
) {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.spacer24.dp)
    ) {
        // 标题
        if (title != null) {
            com.gearui.foundation.primitives.Text(
                text = title,
                style = com.gearui.foundation.typography.Typography.TitleMedium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(Spacing.spacer8.dp))
        }

        // 消息
        if (message != null) {
            com.gearui.foundation.primitives.Text(
                text = message,
                style = com.gearui.foundation.typography.Typography.BodyMedium,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(Spacing.spacer16.dp))
        }

        // 自定义内容
        if (content != null) {
            content()
            Spacer(modifier = Modifier.height(Spacing.spacer16.dp))
        }

        // 操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
}
