package com.gearui.components.toast

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.layout.Spacing
import com.gearui.theme.Theme
import com.gearui.Spacing as GearSpacing
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.GearOverlayOptions
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.overlay.LocalGearOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import kotlinx.coroutines.delay

/**
 * Toast 类型
 */
enum class ToastType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

/**
 * Toast 位置
 */
enum class ToastPosition {
    TOP,
    CENTER,
    BOTTOM
}

/**
 * Toast 数据（内部使用）
 */
internal data class ToastData(
    val message: String,
    val duration: Long = 2000L,
    val type: ToastType = ToastType.INFO
)

/**
 * Toast - 全局轻提示
 *
 * 特点：
 * - 单例队列管理
 * - 自动消失
 * - 非模态
 * - 顶层显示（基于 Overlay 架构）
 *
 * 使用方式：
 * ```kotlin
 * // 在 GearApp 内使用（需要先放置 ToastHost）
 * Toast.show("保存成功")
 * Toast.success("操作成功")
 * Toast.error("操作失败")
 * Toast.warning("请注意")
 * ```
 */
object Toast {

    // 当前 Toast（只保留最新一条）
    internal var current = mutableStateOf<ToastData?>(null)

    // 版本号，用于触发更新
    internal var version = mutableStateOf(0L)

    /**
     * 显示普通 Toast
     */
    fun show(message: String, duration: Long = 2000L) {
        current.value = ToastData(message, duration, ToastType.INFO)
        version.value++
    }

    /**
     * 显示成功 Toast
     */
    fun success(message: String, duration: Long = 2000L) {
        current.value = ToastData(message, duration, ToastType.SUCCESS)
        version.value++
    }

    /**
     * 显示错误 Toast
     */
    fun error(message: String, duration: Long = 2000L) {
        current.value = ToastData(message, duration, ToastType.ERROR)
        version.value++
    }

    /**
     * 显示警告 Toast
     */
    fun warning(message: String, duration: Long = 2000L) {
        current.value = ToastData(message, duration, ToastType.WARNING)
        version.value++
    }

    /**
     * 清空当前 Toast
     */
    fun clear() {
        current.value = null
    }
}

/**
 * ToastHost - Toast 宿主组件
 *
 * 必须放在 GearOverlayRoot 内部，用于显示全局 Toast
 *
 * ```kotlin
 * GearApp {
 *     GearOverlayRoot {
 *         ToastHost()
 *         // ... 其他内容
 *     }
 * }
 * ```
 */
@Composable
fun ToastHost() {
    val controller = LocalGearOverlayController.current

    // 当前显示的 overlay id
    var currentOverlayId by remember { mutableStateOf<Long?>(null) }

    // 监听 Toast 版本变化
    val version = Toast.version.value
    val currentToast = Toast.current.value

    LaunchedEffect(version) {
        // 如果有正在显示的，先关闭
        currentOverlayId?.let {
            controller.dismiss(it)
            currentOverlayId = null
        }

        val toast = currentToast ?: return@LaunchedEffect

        val id = controller.show(
            anchorBounds = null,
            options = GearOverlayOptions(
                placement = GearOverlayPlacement.Center,
                modal = false,
                zIndex = 100f, // Toast 最高层级
                dismissPolicy = OverlayDismissPolicy.toast(toast.duration)
            )
        ) {
            ToastSurface(toast)
        }
        currentOverlayId = id

        // 等待指定时间后消失
        delay(toast.duration)

        // 只有当前显示的还是这个 Toast 时才关闭
        if (currentOverlayId == id) {
            controller.dismiss(id)
            currentOverlayId = null
            Toast.current.value = null
        }
    }
}

/**
 * ToastSurface - Toast 视觉容器（内部使用）
 */
@Composable
private fun ToastSurface(toast: ToastData) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    // 颜色映射：类型 → 视觉
    val (backgroundColor, textColor) = when (toast.type) {
        ToastType.INFO -> colors.inverseSurface to colors.inverseOnSurface
        ToastType.SUCCESS -> colors.success to colors.onPrimary
        ToastType.WARNING -> colors.warning to colors.onPrimary
        ToastType.ERROR -> colors.danger to colors.onPrimary
    }

    Box(
        modifier = Modifier
            .clip(shapes.small)
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 图标
            val iconName = when (toast.type) {
                ToastType.INFO -> null
                ToastType.SUCCESS -> Icons.check
                ToastType.WARNING -> Icons.warning
                ToastType.ERROR -> Icons.close
            }

            if (iconName != null) {
                Icon(
                    name = iconName,
                    size = 18.dp,
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // 文字
            Text(
                text = toast.message,
                style = Typography.BodyMedium,
                color = textColor
            )
        }
    }
}

/**
 * ToastState - Toast 局部状态管理
 *
 * 用于在单个页面内管理 Toast 状态，不依赖 Overlay 系统
 */
class ToastState {
    var visible by mutableStateOf(false)
        private set
    var message by mutableStateOf("")
        private set
    var type by mutableStateOf(ToastType.INFO)
        private set

    fun show(message: String, type: ToastType = ToastType.INFO) {
        this.message = message
        this.type = type
        this.visible = true
    }

    fun showSuccess(message: String) = show(message, ToastType.SUCCESS)
    fun showWarning(message: String) = show(message, ToastType.WARNING)
    fun showError(message: String) = show(message, ToastType.ERROR)

    fun dismiss() {
        visible = false
    }
}

/**
 * rememberToastState - 记住 Toast 局部状态
 */
@Composable
fun rememberToastState(): ToastState {
    return remember { ToastState() }
}

/**
 * LocalToast - 局部 Toast 组件
 *
 * 不依赖 Overlay 系统，直接在当前布局中显示
 *
 * @param message 消息内容
 * @param visible 是否可见
 * @param onDismiss 消失回调
 * @param type Toast 类型
 * @param duration 显示时长（毫秒）
 * @param position 显示位置
 */
@Composable
fun LocalToast(
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    type: ToastType = ToastType.INFO,
    duration: Long = 2000L,
    position: ToastPosition = ToastPosition.CENTER
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    // 自动消失
    LaunchedEffect(visible) {
        if (visible) {
            delay(duration)
            onDismiss()
        }
    }

    if (!visible) return

    // 颜色映射
    val (backgroundColor, textColor) = when (type) {
        ToastType.INFO -> colors.inverseSurface to colors.inverseOnSurface
        ToastType.SUCCESS -> colors.success to colors.onPrimary
        ToastType.WARNING -> colors.warning to colors.onPrimary
        ToastType.ERROR -> colors.danger to colors.onPrimary
    }

    // 位置对齐
    val alignment = when (position) {
        ToastPosition.TOP -> Alignment.TopCenter
        ToastPosition.CENTER -> Alignment.Center
        ToastPosition.BOTTOM -> Alignment.BottomCenter
    }

    val verticalPadding = when (position) {
        ToastPosition.TOP -> PaddingValues(top = GearSpacing.spacer64.dp)
        ToastPosition.CENTER -> PaddingValues(0.dp)
        ToastPosition.BOTTOM -> PaddingValues(bottom = GearSpacing.spacer64.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(verticalPadding),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 120.dp, max = 280.dp)
                .clip(shapes.default)
                .background(backgroundColor)
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = textColor,
                style = Typography.BodyMedium
            )
        }
    }
}

/**
 * LocalToastHost - 局部 Toast 宿主容器
 *
 * 配合 ToastState 使用，放在页面根布局中
 */
@Composable
fun LocalToastHost(
    state: ToastState,
    duration: Long = 2000L,
    position: ToastPosition = ToastPosition.CENTER
) {
    LocalToast(
        message = state.message,
        visible = state.visible,
        onDismiss = { state.dismiss() },
        type = state.type,
        duration = duration,
        position = position
    )
}
