package com.gearui.components.toast

import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.material.MaterialSurface
import com.gearui.foundation.material.Materials
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.lerp
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
                // It reports, it does not take focus: showing it must not close a keyboard the user is typing on.
                dismissKeyboardOnShow = false,
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

    val textColor = toastForeground(toast.type)

    // Reference `.toast__root`: overlay surface and shadow, radius 24, padding 16.
    MaterialSurface(
        material = Materials.Popover,
        shape = OverlayDefaults.panelShape,
        fallback = colors.popover,
    ) {
    Box(
        modifier = Modifier.padding(ControlGeometry.toastPadding),
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
                ToastType.ERROR -> Icons.x
            }

            if (iconName != null) {
                Icon(
                    name = iconName,
                    size = IconSizes.Default.lg,
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
            }

            // Text: reference `.toast__label`, medium weight.
            Text(
                text = toast.message,
                style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = textColor
            )
        }
    }
    }
}

/**
 * Label colour per toast type, on the overlay surface.
 *
 * Reference `.toast__label--variant-*`: default uses the overlay foreground, status
 * variants use their soft foreground — the status colour pulled toward the text
 * colour so yellow and green stay legible on white. Same mixes as the soft Tag.
 */
@Composable
private fun toastForeground(type: ToastType): Color {
    val colors = Theme.colors
    return when (type) {
        ToastType.INFO -> colors.popoverForeground
        ToastType.SUCCESS -> lerp(colors.success, colors.foreground, FeedbackDefaults.tagSuccessForegroundMix)
        ToastType.WARNING -> lerp(colors.warning, colors.foreground, FeedbackDefaults.tagWarningForegroundMix)
        ToastType.ERROR -> colors.destructive
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

    val textColor = toastForeground(type)

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
        MaterialSurface(
            material = Materials.Popover,
            shape = OverlayDefaults.panelShape,
            fallback = colors.popover,
            modifier = Modifier.widthIn(min = 120.dp, max = 280.dp),
        ) {
        Box(
            modifier = Modifier.padding(ControlGeometry.toastPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = textColor,
                style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
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
