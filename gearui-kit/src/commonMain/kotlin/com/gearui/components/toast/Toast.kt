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
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import kotlinx.coroutines.delay
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.typography.IconSizes

/**
 * Toast type
 */
enum class ToastType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

/**
 * Toast position
 */
enum class ToastPosition {
    TOP,
    CENTER,
    BOTTOM
}

/**
 * Toast data (internal)
 */
internal data class ToastData(
    val message: String,
    val duration: Long = 2000L,
    val type: ToastType = ToastType.INFO
)

/**
 * Toast - global lightweight message
 *
 * Characteristics:
 * - singleton queue
 * - auto-dismisses
 * - non-modal
 * - drawn on top, through the overlay architecture
 *
 * Usage:
 * ```kotlin
 * // inside the app, with a ToastHost already placed
 * Toast.show("Saved")
 * Toast.success("Done")
 * Toast.error("Failed")
 * Toast.warning("Careful")
 * ```
 */
object Toast {

    // Current toast; only the newest is kept
    internal var current = mutableStateOf<ToastData?>(null)

    // Version counter, used to trigger updates
    internal var version = mutableStateOf(0L)

    /**
     * Shows a plain toast
     */
    fun show(message: String, duration: Long = 2000L) {
        current.value = ToastData(message, duration, ToastType.INFO)
        version.value++
    }

    /**
     * Shows a success toast
     */
    fun success(message: String, duration: Long = 2000L) {
        current.value = ToastData(message, duration, ToastType.SUCCESS)
        version.value++
    }

    /**
     * Shows an error toast
     */
    fun error(message: String, duration: Long = 2000L) {
        current.value = ToastData(message, duration, ToastType.ERROR)
        version.value++
    }

    /**
     * Shows a warning toast
     */
    fun warning(message: String, duration: Long = 2000L) {
        current.value = ToastData(message, duration, ToastType.WARNING)
        version.value++
    }

    /**
     * Clears the current toast
     */
    fun clear() {
        current.value = null
    }
}

/**
 * ToastHost - toast host
 *
 * Must sit inside OverlayRoot; displays global toasts.
 *
 * ```kotlin
 * App {
 *     OverlayRoot {
 *         ToastHost()
 *         // ... other content
 *     }
 * }
 * ```
 */
@Composable
fun ToastHost() {
    val controller = LocalOverlayController.current

    // Currently displayed overlay id
    var currentOverlayId by remember { mutableStateOf<Long?>(null) }

    // Watch for toast version changes
    val version = Toast.version.value
    val currentToast = Toast.current.value

    LaunchedEffect(version) {
        // Dismiss the one already showing, if any
        currentOverlayId?.let {
            controller.dismiss(it)
            currentOverlayId = null
        }

        val toast = currentToast ?: return@LaunchedEffect

        val id = controller.show(
            anchorBounds = null,
            options = OverlayOptions(
                placement = OverlayPlacement.Center,
                modal = false,
                zIndex = 100f, // Toast 最高层级
                dismissPolicy = OverlayDismissPolicy.toast(toast.duration)
            )
        ) {
            ToastSurface(toast)
        }
        currentOverlayId = id

        // Wait the configured time, then dismiss
        delay(toast.duration)

        // Only dismiss if this is still the toast being shown
        if (currentOverlayId == id) {
            controller.dismiss(id)
            currentOverlayId = null
            Toast.current.value = null
        }
    }
}

/**
 * ToastSurface - Toast visual container (internal)
 */
@Composable
private fun ToastSurface(toast: ToastData) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    // Colour mapping: type -> visuals
    val (backgroundColor, textColor) = when (toast.type) {
        ToastType.INFO -> colors.foreground to colors.background
        ToastType.SUCCESS -> colors.success to colors.successForeground
        ToastType.WARNING -> colors.warning to colors.warningForeground
        ToastType.ERROR -> colors.destructive to colors.destructiveForeground
    }

    Box(
        modifier = Modifier
            .clip(OverlayDefaults.panelShape)
            .background(backgroundColor)
            .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icon
            val iconName = when (toast.type) {
                ToastType.INFO -> null
                ToastType.SUCCESS -> Icons.check
                ToastType.WARNING -> Icons.warning
                ToastType.ERROR -> Icons.close
            }

            if (iconName != null) {
                Icon(
                    name = iconName,
                    size = IconSizes.Default.lg,
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
            }

            // Text
            Text(
                text = toast.message,
                style = Typography.BodyMedium,
                color = textColor
            )
        }
    }
}

/**
 * ToastState - local Toast state
 *
 * Manages Toast state within a single page, independent of the Overlay system
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
 * rememberToastState - remembers a local Toast state
 */
@Composable
fun rememberToastState(): ToastState {
    return remember { ToastState() }
}

/**
 * LocalToast - local Toast component
 *
 * Independent of the Overlay system; renders inside the current layout
 *
 * @param message message body
 * @param visible whether it is visible
 * @param onDismiss dismissal callback
 * @param type Toast type
 * @param duration display duration in milliseconds
 * @param position display position
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

    // Auto-dismiss
    LaunchedEffect(visible) {
        if (visible) {
            delay(duration)
            onDismiss()
        }
    }

    if (!visible) return

    // Colour mapping
    val (backgroundColor, textColor) = when (type) {
        ToastType.INFO -> colors.foreground to colors.background
        ToastType.SUCCESS -> colors.success to colors.successForeground
        ToastType.WARNING -> colors.warning to colors.warningForeground
        ToastType.ERROR -> colors.destructive to colors.destructiveForeground
    }

    // Position alignment
    val alignment = when (position) {
        ToastPosition.TOP -> Alignment.TopCenter
        ToastPosition.CENTER -> Alignment.Center
        ToastPosition.BOTTOM -> Alignment.BottomCenter
    }

    val verticalPadding = when (position) {
        ToastPosition.TOP -> PaddingValues(top = Spacing.massive)
        ToastPosition.CENTER -> PaddingValues(0.dp)
        ToastPosition.BOTTOM -> PaddingValues(bottom = Spacing.massive)
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
                .clip(OverlayDefaults.panelShape)
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
 * LocalToastHost - local Toast host container
 *
 * Used with ToastState, placed in the page root layout
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
