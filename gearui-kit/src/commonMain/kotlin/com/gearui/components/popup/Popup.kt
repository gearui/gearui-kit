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
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.theme.Theme
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.border.BorderWidth

/**
 * Popup - base for anchored floating layers
 *
 * The foundation of every anchor-positioned floating component:
 * - Dropdown
 * - Tooltip
 * - Popover
 * - SelectMenu
 * - ContextMenu
 *
 * Characteristics:
 * - non-modal (does not block interaction)
 * - anchored (positioned relative to the trigger)
 * - optional scrim
 * - tap outside to dismiss
 */
object Popup {

    /**
     * Declarative Popup
     *
     * @param visible whether it is shown
     * @param anchorBounds anchor position
     * @param placement popup placement
     * @param offsetX offset on the X axis
     * @param offsetY offset on the Y axis
     * @param dismissOnOutside whether tapping outside dismisses it
     * @param autoFlip whether to flip automatically when space runs out
     * @param onDismiss dismiss callback
     * @param content the content
     */
    @Composable
    fun Host(
        visible: Boolean,
        anchorBounds: Rect?,
        placement: OverlayPlacement = OverlayPlacement.BottomLeft,
        offsetX: Dp = 0.dp,
        offsetY: Dp = 4.dp,
        dismissOnOutside: Boolean = true,
        autoFlip: Boolean = true,
        onDismiss: () -> Unit = {},
        content: @Composable () -> Unit
    ) {
        val controller = LocalOverlayController.current
        var overlayId by remember { mutableStateOf<Long?>(null) }

        // Anchor position recorded at open time, used to detect scrolling
        var anchorBoundsWhenOpened by remember { mutableStateOf<Rect?>(null) }

        // Keep the onDismiss callback current
        val onDismissState = rememberUpdatedState(onDismiss)

        // React to visible changes
        LaunchedEffect(visible) {
            if (visible) {
                // anchorBounds may be null for a centred popup
                // an anchored popup requires anchorBounds
                val needsAnchor = placement != OverlayPlacement.Center &&
                                  placement != OverlayPlacement.Fullscreen

                if (needsAnchor && anchorBounds == null) {
                    // Anchor required but missing: do not show
                    return@LaunchedEffect
                }

                overlayId = controller.show(
                    anchorBounds = anchorBounds,
                    options = OverlayOptions(
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

        // Scroll detection: dismiss the Overlay when anchorBounds moves while it is open
        LaunchedEffect(anchorBounds) {
            if (visible && overlayId != null && anchorBoundsWhenOpened != null && anchorBounds != null) {
                val openedBounds = anchorBoundsWhenOpened!!
                val currentBounds = anchorBounds
                // A visible shift (more than 1 pixel) means the page scrolled
                if (kotlin.math.abs(openedBounds.top - currentBounds.top) > 1f ||
                    kotlin.math.abs(openedBounds.left - currentBounds.left) > 1f) {
                    overlayId?.let { controller.dismiss(it) }
                }
            }
        }

        // Dismiss when the component leaves composition
        DisposableEffect(Unit) {
            onDispose {
                overlayId?.let { controller.dismiss(it) }
            }
        }
    }
}

/**
 * PopupSurface - shared visual container for Popup
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
            .clip(OverlayDefaults.panelShape)
            .background(colors.surface)
            .border(BorderWidth.thin, colors.border, OverlayDefaults.panelShape)
    ) {
        content()
    }
}
