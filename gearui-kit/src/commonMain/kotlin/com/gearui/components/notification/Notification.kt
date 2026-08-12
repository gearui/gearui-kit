package com.gearui.components.notification

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
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
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import kotlinx.coroutines.delay
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.elevation.Elevation
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.typography.IconSizes
import com.gearui.overlay.rememberTopFloatingOffset

/**
 * Notification - top notification card
 *
 * - a card that drops in from the top
 * - title plus optional detail
 * - four state types
 * - optional action button
 * - optional auto-dismiss
 */

/**
 * NotificationType - notification type
 */
enum class NotificationType {
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
 * NotificationData - notification data
 */
data class NotificationData(
    val title: String,
    val message: String? = null,
    val type: NotificationType = NotificationType.INFO,
    val action: String? = null,
    val onAction: (() -> Unit)? = null,
    val duration: Long = 4000L,
    val closable: Boolean = true,
    val onDismiss: (() -> Unit)? = null
)

/**
 * Notification
 *
 * @param title title
 * @param visible whether it is shown
 * @param onDismiss dismissal callback
 * @param message optional detail text
 * @param type notification type
 * @param action optional action button label
 * @param onAction action button callback
 * @param duration auto-dismiss delay in milliseconds; 0 disables it
 * @param closable whether to show a close button
 * @param topOffset minimum distance from the top
 */
@Composable
fun Notification(
    title: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    type: NotificationType = NotificationType.INFO,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    duration: Long = 4000L,
    closable: Boolean = true,
    topOffset: Float = 48f,
    leading: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    LaunchedEffect(visible) {
        if (visible && duration > 0) {
            delay(duration)
            onDismiss()
        }
    }

    if (!visible) return

    val resolvedTopOffset = rememberTopFloatingOffset(topOffset.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = resolvedTopOffset)
            .padding(horizontal = Spacing.lg),
        contentAlignment = Alignment.TopCenter
    ) {
        NotificationContent(
            title = title,
            message = message,
            type = type,
            action = action,
            onAction = onAction,
            closable = closable,
            onDismiss = onDismiss,
            leading = leading,
            onClick = onClick,
        )
    }
}

/**
 * Notification content
 */
@Composable
internal fun NotificationContent(
    title: String,
    message: String?,
    type: NotificationType,
    action: String?,
    onAction: (() -> Unit)?,
    closable: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    /** Custom leading content: avatar, app icon, file icon. Falls back to the type icon when null. */
    leading: (@Composable () -> Unit)? = null,
    /** Whole-card click callback. When null the card is not clickable, preserving existing behaviour. */
    onClick: (() -> Unit)? = null,
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    // Icon and colour for the type
    val (iconName, iconColor) = when (type) {
        NotificationType.INFO -> Icons.info to colors.primary
        NotificationType.SUCCESS -> Icons.check to colors.success
        NotificationType.WARNING -> Icons.warning to colors.warning
        NotificationType.ERROR -> Icons.close to colors.destructive
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(Elevation.floating, OverlayDefaults.panelShape)
            .clip(OverlayDefaults.panelShape)
            .background(colors.surface)
            // Whole-card click, attached when onClick != null. The action and close
            // buttons consume their own clicks — Compose pointerInput does not bubble
            .let { base ->
                if (onClick != null) base.clickable { onClick(); onDismiss() } else base
            }
            .padding(Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.Top
    ) {
        // Leading: the custom slot wins; otherwise the type icon
        if (leading != null) {
            leading()
        } else {
            Icon(
                name = iconName,
                size = IconSizes.Default.lg,
                tint = iconColor
            )
        }

        // 内容区
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.TitleMedium,
                color = colors.foreground
            )

            if (message != null) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = message,
                    style = Typography.BodyMedium,
                    color = colors.mutedForeground
                )
            }

            if (action != null && onAction != null) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = action,
                    style = Typography.BodyMedium,
                    color = colors.primary,
                    modifier = Modifier.clickable(onClick = {
                        onAction()
                        onDismiss()
                    })
                )
            }
        }

        // 关闭按钮
        if (closable) {
            Icon(
                name = Icons.close,
                size = IconSizes.Default.lg,
                tint = colors.mutedForeground,
                modifier = Modifier.clickable(onClick = onDismiss)
            )
        }
    }
}

/**
 * NotificationHost - 通知宿主容器（传统用法）
 */
@Composable
fun NotificationHost(
    state: NotificationHostState,
    modifier: Modifier = Modifier,
    topOffset: Float = 48f
) {
    val current = state.currentNotification

    if (current != null) {
        Notification(
            title = current.title,
            message = current.message,
            visible = true,
            onDismiss = { state.dismiss() },
            modifier = modifier,
            type = current.type,
            action = current.action,
            onAction = current.onAction,
            duration = current.duration,
            closable = current.closable,
            topOffset = topOffset
        )
    }
}

/**
 * NotificationHostState - 传统状态管理
 */
class NotificationHostState {
    var currentNotification by mutableStateOf<NotificationData?>(null)
        private set

    fun show(
        title: String,
        message: String? = null,
        type: NotificationType = NotificationType.INFO,
        action: String? = null,
        onAction: (() -> Unit)? = null,
        duration: Long = 4000L,
        closable: Boolean = true
    ) {
        currentNotification = NotificationData(
            title = title,
            message = message,
            type = type,
            action = action,
            onAction = onAction,
            duration = duration,
            closable = closable
        )
    }

    fun dismiss() {
        currentNotification?.onDismiss?.invoke()
        currentNotification = null
    }
}

/**
 * 创建并记住 NotificationHostState
 */
@Composable
fun rememberNotificationHostState(): NotificationHostState {
    return remember { NotificationHostState() }
}

// ============ Overlay 系统集成 ============

/**
 * 通过 Overlay 系统显示 Notification（推荐用法）
 */
@Composable
fun rememberNotificationController(): NotificationController {
    val overlayController = LocalOverlayController.current
    return remember { NotificationController(overlayController) }
}

/**
 * NotificationController - 通过 Overlay 系统显示通知
 */
class NotificationController internal constructor(
    private val overlayController: com.gearui.overlay.OverlayController
) {
    private var currentOverlayId: Long? = null

    /**
     * 显示通知
     */
    fun show(
        title: String,
        message: String? = null,
        type: NotificationType = NotificationType.INFO,
        action: String? = null,
        onAction: (() -> Unit)? = null,
        duration: Long = 4000L,
        closable: Boolean = true,
        topOffset: Float = 48f,
        leading: (@Composable () -> Unit)? = null,
        onClick: (() -> Unit)? = null,
    ) {
        // 先关闭之前的
        dismiss()

        currentOverlayId = overlayController.show(
            options = OverlayOptions(
                placement = OverlayPlacement.Fullscreen,
                modal = false,
                // 通知是非阻断提示：横幅之外的区域照常可点/可滚，不冻结整屏交互。
                passThroughOutside = true,
                dismissPolicy = OverlayDismissPolicy.toast(duration)
            )
        ) {
            NotificationOverlayContent(
                title = title,
                message = message,
                type = type,
                action = action,
                onAction = onAction,
                duration = duration,
                closable = closable,
                topOffset = topOffset,
                onDismiss = { dismiss() },
                leading = leading,
                onClick = onClick,
            )
        }
    }

    /**
     * 关闭当前通知
     */
    fun dismiss() {
        currentOverlayId?.let {
            overlayController.dismiss(it)
            currentOverlayId = null
        }
    }
}

/**
 * Overlay 模式下的 Notification 内容
 */
@Composable
private fun NotificationOverlayContent(
    title: String,
    message: String?,
    type: NotificationType,
    action: String?,
    onAction: (() -> Unit)?,
    duration: Long,
    closable: Boolean,
    topOffset: Float,
    onDismiss: () -> Unit,
    leading: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
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
            NotificationContent(
                title = title,
                message = message,
                type = type,
                action = action,
                onAction = onAction,
                closable = closable,
                onDismiss = onDismiss,
                leading = leading,
                onClick = onClick,
            )
        }
    }
}

// ============ 便捷函数 ============

/**
 * 显示普通信息通知
 */
fun NotificationController.showInfo(
    title: String,
    message: String? = null,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    duration: Long = 4000L,
    closable: Boolean = true
) = show(title, message, NotificationType.INFO, action, onAction, duration, closable)

/**
 * 显示成功通知
 */
fun NotificationController.showSuccess(
    title: String,
    message: String? = null,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    duration: Long = 4000L,
    closable: Boolean = true
) = show(title, message, NotificationType.SUCCESS, action, onAction, duration, closable)

/**
 * 显示警告通知
 */
fun NotificationController.showWarning(
    title: String,
    message: String? = null,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    duration: Long = 4000L,
    closable: Boolean = true
) = show(title, message, NotificationType.WARNING, action, onAction, duration, closable)

/**
 * 显示错误通知
 */
fun NotificationController.showError(
    title: String,
    message: String? = null,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    duration: Long = 4000L,
    closable: Boolean = true
) = show(title, message, NotificationType.ERROR, action, onAction, duration, closable)
