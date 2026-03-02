package com.gearui.components.snackbar

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.animation.core.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.graphics.Color
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.Spacing
import com.gearui.overlay.LocalGearOverlayController
import com.gearui.overlay.GearOverlayOptions
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import kotlinx.coroutines.delay

/**
 * Snackbar - 顶部消息提示条
 *
 * - 从顶部弹出（默认距顶部 80dp）
 * - 支持四种类型：INFO、SUCCESS、WARNING、ERROR
 * - 自动消失（默认 3 秒）
 * - 可带操作按钮
 * - 可带关闭按钮
 * - 可带图标
 */

/**
 * SnackbarType - Snackbar 类型/主题
 */
enum class SnackbarType {
    /** 普通信息 */
    INFO,
    /** 成功 */
    SUCCESS,
    /** 警告 */
    WARNING,
    /** 错误 */
    ERROR
}

/**
 * Snackbar 数据类
 */
data class SnackbarData(
    val message: String,
    val type: SnackbarType = SnackbarType.INFO,
    val action: String? = null,
    val onActionClick: (() -> Unit)? = null,
    val showCloseButton: Boolean = false,
    val showIcon: Boolean = true,
    val duration: Long = 3000L,
    val onDismiss: (() -> Unit)? = null
)

/**
 * SnackbarHostState - Snackbar 状态管理
 *
 * 用于通过 Overlay 系统显示 Snackbar
 */
class SnackbarHostState {
    var currentSnackbar by mutableStateOf<SnackbarData?>(null)
        private set

    private var currentOverlayId: Long? = null

    /**
     * 显示 Snackbar
     */
    fun show(
        message: String,
        type: SnackbarType = SnackbarType.INFO,
        action: String? = null,
        onActionClick: (() -> Unit)? = null,
        showCloseButton: Boolean = false,
        showIcon: Boolean = true,
        duration: Long = 3000L
    ) {
        currentSnackbar = SnackbarData(
            message = message,
            type = type,
            action = action,
            onActionClick = onActionClick,
            showCloseButton = showCloseButton,
            showIcon = showIcon,
            duration = duration
        )
    }

    /**
     * 关闭 Snackbar
     */
    fun dismiss() {
        currentSnackbar?.onDismiss?.invoke()
        currentSnackbar = null
    }

    internal fun setOverlayId(id: Long?) {
        currentOverlayId = id
    }
}

/**
 * 创建并记住 SnackbarHostState
 */
@Composable
fun rememberSnackbarHostState(): SnackbarHostState {
    return remember { SnackbarHostState() }
}

/**
 * Snackbar 组件 - 顶部消息提示
 *
 * @param message 消息内容
 * @param visible 是否可见
 * @param onDismiss 关闭回调
 * @param type 类型（INFO/SUCCESS/WARNING/ERROR）
 * @param action 操作按钮文字
 * @param onActionClick 操作按钮点击回调
 * @param showCloseButton 是否显示关闭按钮
 * @param showIcon 是否显示图标
 * @param duration 自动关闭时间（毫秒），0 表示不自动关闭
 * @param topOffset 距离顶部的距离
 */
@Composable
fun Snackbar(
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    type: SnackbarType = SnackbarType.INFO,
    action: String? = null,
    onActionClick: (() -> Unit)? = null,
    showCloseButton: Boolean = false,
    showIcon: Boolean = true,
    duration: Long = 3000L,
    topOffset: Float = 80f
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    // 自动关闭定时器
    LaunchedEffect(visible) {
        if (visible && duration > 0) {
            delay(duration)
            onDismiss()
        }
    }

    if (!visible) return

    // 顶部弹出位置
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = topOffset.dp)
            .padding(horizontal = Spacing.spacer16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        SnackbarContent(
            message = message,
            type = type,
            action = action,
            onActionClick = onActionClick,
            showCloseButton = showCloseButton,
            showIcon = showIcon,
            onDismiss = onDismiss
        )
    }
}

/**
 * Snackbar 内容组件
 */
@Composable
internal fun SnackbarContent(
    message: String,
    type: SnackbarType,
    action: String?,
    onActionClick: (() -> Unit)?,
    showCloseButton: Boolean,
    showIcon: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    // 根据类型获取背景色
    val backgroundColor = when (type) {
        SnackbarType.INFO -> colors.surface
        SnackbarType.SUCCESS -> colors.successLight
        SnackbarType.WARNING -> colors.warningLight
        SnackbarType.ERROR -> colors.dangerLight
    }

    // 根据类型获取图标颜色
    val iconColor = when (type) {
        SnackbarType.INFO -> colors.primary
        SnackbarType.SUCCESS -> colors.success
        SnackbarType.WARNING -> colors.warning
        SnackbarType.ERROR -> colors.danger
    }

    // 文字颜色
    val textColor = colors.textPrimary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(Spacing.spacer4.dp, shapes.small)
            .clip(shapes.small)
            .background(backgroundColor)
            .padding(horizontal = Spacing.spacer16.dp, vertical = Spacing.spacer12.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图标
        if (showIcon) {
            val iconName = when (type) {
                SnackbarType.INFO -> Icons.info
                SnackbarType.SUCCESS -> Icons.check
                SnackbarType.WARNING -> Icons.warning
                SnackbarType.ERROR -> Icons.close
            }
            Icon(
                name = iconName,
                size = 20.dp,
                tint = iconColor
            )
        }

        // 消息文字
        Text(
            text = message,
            style = Typography.BodyMedium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        // 操作按钮
        if (action != null && onActionClick != null) {
            Text(
                text = action,
                style = Typography.BodyMedium,
                color = colors.primary,
                modifier = Modifier.clickable(onClick = {
                    onActionClick()
                    onDismiss()
                })
            )
        }

        // 关闭按钮
        if (showCloseButton) {
            Icon(
                name = Icons.close,
                size = 16.dp,
                tint = colors.textPlaceholder,
                modifier = Modifier.clickable(onClick = onDismiss)
            )
        }
    }
}

/**
 * SnackbarHost - Snackbar 宿主，配合 SnackbarHostState 使用
 *
 * 放置在页面顶层，用于显示 Snackbar
 */
@Composable
fun SnackbarHost(
    state: SnackbarHostState,
    modifier: Modifier = Modifier,
    topOffset: Float = 80f
) {
    val current = state.currentSnackbar

    if (current != null) {
        Snackbar(
            message = current.message,
            visible = true,
            onDismiss = { state.dismiss() },
            modifier = modifier,
            type = current.type,
            action = current.action,
            onActionClick = current.onActionClick,
            showCloseButton = current.showCloseButton,
            showIcon = current.showIcon,
            duration = current.duration,
            topOffset = topOffset
        )
    }
}

/**
 * 通过 Overlay 系统显示 Snackbar（全局顶部弹出）
 *
 * 这是推荐的使用方式，可以在任何地方调用
 */
@Composable
fun rememberSnackbarController(): SnackbarController {
    val overlayController = LocalGearOverlayController.current
    return remember { SnackbarController(overlayController) }
}

/**
 * Snackbar 控制器 - 通过 Overlay 系统显示 Snackbar
 */
class SnackbarController internal constructor(
    private val overlayController: com.gearui.overlay.GearOverlayController
) {
    private var currentOverlayId: Long? = null

    /**
     * 显示 Snackbar
     */
    fun show(
        message: String,
        type: SnackbarType = SnackbarType.INFO,
        action: String? = null,
        onActionClick: (() -> Unit)? = null,
        showCloseButton: Boolean = false,
        showIcon: Boolean = true,
        duration: Long = 3000L,
        topOffset: Float = 80f
    ) {
        // 先关闭之前的
        dismiss()

        currentOverlayId = overlayController.show(
            options = GearOverlayOptions(
                placement = GearOverlayPlacement.Fullscreen,
                modal = false,
                dismissPolicy = OverlayDismissPolicy.toast(duration)
            )
        ) {
            SnackbarOverlayContent(
                message = message,
                type = type,
                action = action,
                onActionClick = onActionClick,
                showCloseButton = showCloseButton,
                showIcon = showIcon,
                duration = duration,
                topOffset = topOffset,
                onDismiss = { dismiss() }
            )
        }
    }

    /**
     * 关闭当前 Snackbar
     */
    fun dismiss() {
        currentOverlayId?.let {
            overlayController.dismiss(it)
            currentOverlayId = null
        }
    }
}

/**
 * Overlay 模式下的 Snackbar 内容
 */
@Composable
private fun SnackbarOverlayContent(
    message: String,
    type: SnackbarType,
    action: String?,
    onActionClick: (() -> Unit)?,
    showCloseButton: Boolean,
    showIcon: Boolean,
    duration: Long,
    topOffset: Float,
    onDismiss: () -> Unit
) {
    // 自动关闭定时器
    LaunchedEffect(Unit) {
        if (duration > 0) {
            delay(duration)
            onDismiss()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .padding(top = topOffset.dp)
                .padding(horizontal = Spacing.spacer16.dp)
        ) {
            SnackbarContent(
                message = message,
                type = type,
                action = action,
                onActionClick = onActionClick,
                showCloseButton = showCloseButton,
                showIcon = showIcon,
                onDismiss = onDismiss
            )
        }
    }
}


/**
 * 显示普通信息提示
 */
fun SnackbarController.showInfo(
    message: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null,
    showCloseButton: Boolean = false,
    duration: Long = 3000L
) = show(message, SnackbarType.INFO, action, onActionClick, showCloseButton, true, duration)

/**
 * 显示成功提示
 */
fun SnackbarController.showSuccess(
    message: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null,
    showCloseButton: Boolean = false,
    duration: Long = 3000L
) = show(message, SnackbarType.SUCCESS, action, onActionClick, showCloseButton, true, duration)

/**
 * 显示警告提示
 */
fun SnackbarController.showWarning(
    message: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null,
    showCloseButton: Boolean = false,
    duration: Long = 3000L
) = show(message, SnackbarType.WARNING, action, onActionClick, showCloseButton, true, duration)

/**
 * 显示错误提示
 */
fun SnackbarController.showError(
    message: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null,
    showCloseButton: Boolean = false,
    duration: Long = 3000L
) = show(message, SnackbarType.ERROR, action, onActionClick, showCloseButton, true, duration)
