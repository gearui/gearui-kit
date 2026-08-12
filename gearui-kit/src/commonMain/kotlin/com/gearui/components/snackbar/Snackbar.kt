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
import com.gearui.foundation.layout.Spacing
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import kotlinx.coroutines.delay
import com.gearui.foundation.elevation.Elevation
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.typography.IconSizes
import com.gearui.overlay.rememberTopFloatingOffset

/**
 * Snackbar - top message bar
 *
 * - rises from the top (80dp down by default)
 * - four types: INFO, SUCCESS, WARNING, ERROR
 * - auto-dismisses (3 seconds by default)
 * - optional action button
 * - optional close button
 * - optional icon
 */

/**
 * SnackbarType - Snackbar type and theme
 */
enum class SnackbarType {
    /** Information */
    INFO,
    /** Success */
    SUCCESS,
    /** Warning */
    WARNING,
    /** Error */
    ERROR
}

/**
 * Snackbar data
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
 * SnackbarHostState - Snackbar state holder
 *
 * Shows Snackbars through the overlay system.
 */
class SnackbarHostState {
    var currentSnackbar by mutableStateOf<SnackbarData?>(null)
        private set

    private var currentOverlayId: Long? = null

    /**
     * Shows a Snackbar
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
     * Dismisses the Snackbar
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
 * Creates and remembers a SnackbarHostState
 */
@Composable
fun rememberSnackbarHostState(): SnackbarHostState {
    return remember { SnackbarHostState() }
}

/**
 * Snackbar - top message bar
 *
 * @param message message text
 * @param visible whether it is shown
 * @param onDismiss dismissal callback
 * @param type INFO, SUCCESS, WARNING or ERROR
 * @param action action button label
 * @param onActionClick action button callback
 * @param showCloseButton whether to show a close button
 * @param showIcon whether to show an icon
 * @param duration auto-dismiss delay in milliseconds; 0 disables it
 * @param topOffset minimum distance from the top
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

    // Auto-dismiss timer
    LaunchedEffect(visible) {
        if (visible && duration > 0) {
            delay(duration)
            onDismiss()
        }
    }

    if (!visible) return

    val resolvedTopOffset = rememberTopFloatingOffset(topOffset.dp)

    // 顶部弹出位置
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = resolvedTopOffset)
            .padding(horizontal = Spacing.lg),
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
        SnackbarType.SUCCESS -> colors.success.copy(alpha = 0.12f)
        SnackbarType.WARNING -> colors.warning.copy(alpha = 0.12f)
        SnackbarType.ERROR -> colors.destructive.copy(alpha = 0.12f)
    }

    // 根据类型获取图标颜色
    val iconColor = when (type) {
        SnackbarType.INFO -> colors.primary
        SnackbarType.SUCCESS -> colors.success
        SnackbarType.WARNING -> colors.warning
        SnackbarType.ERROR -> colors.destructive
    }

    // 文字颜色
    val textColor = colors.foreground

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(Elevation.raised, OverlayDefaults.panelShape)
            .clip(OverlayDefaults.panelShape)
            .background(backgroundColor)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
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
                size = IconSizes.Default.lg,
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
                size = IconSizes.Default.md,
                tint = colors.mutedForeground,
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
    val overlayController = LocalOverlayController.current
    return remember { SnackbarController(overlayController) }
}

/**
 * Snackbar 控制器 - 通过 Overlay 系统显示 Snackbar
 */
class SnackbarController internal constructor(
    private val overlayController: com.gearui.overlay.OverlayController
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
            options = OverlayOptions(
                placement = OverlayPlacement.Fullscreen,
                modal = false,
                // Snackbar is non-blocking feedback: outside the banner, the
                // underlying page should keep receiving taps and scrolls.
                passThroughOutside = true,
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

    val resolvedTopOffset = rememberTopFloatingOffset(topOffset.dp)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .padding(top = resolvedTopOffset)
                .padding(horizontal = Spacing.lg)
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
