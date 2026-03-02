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
import com.gearui.Spacing
import com.gearui.overlay.LocalGearOverlayController
import com.gearui.overlay.GearOverlayOptions
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import kotlinx.coroutines.delay
import com.gearui.foundation.typography.Typography

/**
 * Notification - 顶部通知卡片组件
 *
 * - 从顶部弹出的通知卡片
 * - 支持标题和详细描述
 * - 支持四种状态类型
 * - 支持操作按钮
 * - 支持自动消失
 */

/**
 * NotificationType - 通知类型
 */
enum class NotificationType {
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
 * NotificationData - 通知数据
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
 * Notification 组件
 *
 * @param title 标题
 * @param visible 是否可见
 * @param onDismiss 关闭回调
 * @param message 详细描述（可选）
 * @param type 通知类型
 * @param action 操作按钮文字（可选）
 * @param onAction 操作按钮点击回调
 * @param duration 自动关闭时间（毫秒），0 表示不自动关闭
 * @param closable 是否显示关闭按钮
 * @param topOffset 距离顶部的距离
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
    topOffset: Float = 48f
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = topOffset.dp)
            .padding(horizontal = Spacing.spacer16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        NotificationContent(
            title = title,
            message = message,
            type = type,
            action = action,
            onAction = onAction,
            closable = closable,
            onDismiss = onDismiss
        )
    }
}

/**
 * Notification 内容组件
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
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    // 获取类型对应的图标和颜色
    val (iconName, iconColor) = when (type) {
        NotificationType.INFO -> Icons.info to colors.primary
        NotificationType.SUCCESS -> Icons.check to colors.success
        NotificationType.WARNING -> Icons.warning to colors.warning
        NotificationType.ERROR -> Icons.close to colors.danger
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(Spacing.spacer8.dp, shapes.default)
            .clip(shapes.default)
            .background(colors.surface)
            .padding(Spacing.spacer16.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing.spacer12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 左侧图标
        Icon(
            name = iconName,
            size = 20.dp,
            tint = iconColor
        )

        // 内容区
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.TitleMedium,
                color = colors.textPrimary
            )

            if (message != null) {
                Spacer(modifier = Modifier.height(Spacing.spacer4.dp))
                Text(
                    text = message,
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }

            if (action != null && onAction != null) {
                Spacer(modifier = Modifier.height(Spacing.spacer8.dp))
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
                size = 18.dp,
                tint = colors.textPlaceholder,
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
    val overlayController = LocalGearOverlayController.current
    return remember { NotificationController(overlayController) }
}

/**
 * NotificationController - 通过 Overlay 系统显示通知
 */
class NotificationController internal constructor(
    private val overlayController: com.gearui.overlay.GearOverlayController
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
        topOffset: Float = 48f
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
            NotificationOverlayContent(
                title = title,
                message = message,
                type = type,
                action = action,
                onAction = onAction,
                duration = duration,
                closable = closable,
                topOffset = topOffset,
                onDismiss = { dismiss() }
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
            NotificationContent(
                title = title,
                message = message,
                type = type,
                action = action,
                onAction = onAction,
                closable = closable,
                onDismiss = onDismiss
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
