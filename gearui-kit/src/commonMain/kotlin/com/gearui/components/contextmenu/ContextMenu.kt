package com.gearui.components.contextmenu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.Spacing
import com.gearui.components.popover.PopoverPlacement
import com.gearui.components.popover.PopoverTheme
import com.gearui.components.popover.rememberPopoverState
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.GearOverlayOptions
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberGearOverlay
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.widthIn
import com.tencent.kuikly.compose.foundation.layout.wrapContentWidth
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Context menu action model.
 *
 * @param label action text
 * @param disabled whether action is disabled
 * @param danger whether action uses danger semantic color
 * @param onClick action callback
 */
data class ContextMenuItem(
    val label: String,
    val disabled: Boolean = false,
    val danger: Boolean = false,
    val onClick: () -> Unit
)

/**
 * ContextMenu - menu actions shown in a popover.
 *
 * @param items menu items
 * @param modifier modifier applied to trigger container
 * @param placement menu placement
 * @param theme menu theme
 * @param trigger trigger content with open callback
 */
@Composable
fun ContextMenu(
    items: List<ContextMenuItem>,
    modifier: Modifier = Modifier,
    placement: PopoverPlacement = PopoverPlacement.BOTTOM_LEFT,
    theme: PopoverTheme = PopoverTheme.LIGHT,
    trigger: @Composable (onOpen: () -> Unit) -> Unit
) {
    val state = rememberPopoverState()
    val overlay = rememberGearOverlay()
    val colors = Theme.colors
    val shapes = Theme.shapes
    var triggerBounds by remember { mutableStateOf<Rect?>(null) }
    var pressedIndex by remember { mutableStateOf<Int?>(null) }

    val bounds = triggerBounds
    if (state.isVisible && bounds != null) {
        DisposableEffect(bounds, placement) {
            val overlayId = overlay.show(
                anchorBounds = bounds,
                options = GearOverlayOptions(
                    placement = placementToOverlay(placement),
                    offsetY = Spacing.spacer4.dp,
                    modal = false,
                    maskColor = null,
                    dismissPolicy = OverlayDismissPolicy.Dropdown.copy(
                        outsideClick = true,
                        scroll = true
                    )
                ),
                onDismiss = {
                    state.hide()
                }
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(min = 128.dp, max = 220.dp)
                        .wrapContentWidth()
                        .shadow(Spacing.spacer4.dp, shapes.default)
                        .clip(shapes.default)
                        .background(colors.surface)
                        .border(1.dp, colors.border, shapes.default)
                        .padding(Spacing.spacer4.dp)
                ) {
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shapes.small)
                                .background(
                                    if (pressedIndex == index) colors.surfaceVariant else colors.surface
                                )
                                .pointerInput(index) {
                                    awaitEachGesture {
                                        awaitFirstDown(requireUnconsumed = false)
                                        pressedIndex = index
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val change = event.changes.firstOrNull() ?: break
                                            if (!change.pressed) {
                                                pressedIndex = null
                                                break
                                            }
                                        }
                                    }
                                }
                                .clickable(enabled = !item.disabled) {
                                    item.onClick()
                                    state.hide()
                                }
                                .padding(
                                    horizontal = Spacing.spacer8.dp,
                                    vertical = 6.dp
                                )
                        ) {
                            Text(
                                text = item.label,
                                style = Typography.BodySmall,
                                color = when {
                                    item.disabled -> colors.textDisabled
                                    item.danger -> colors.danger
                                    else -> colors.textPrimary
                                }
                            )
                        }
                    }
                }
            }

            onDispose {
                pressedIndex = null
                overlay.dismiss(overlayId)
            }
        }
    }

    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            triggerBounds = coordinates.boundsInRoot()
        }
    ) {
        trigger {
            if (!state.isVisible) {
                state.show()
            }
        }
    }
}

private fun placementToOverlay(placement: PopoverPlacement): GearOverlayPlacement {
    return when (placement) {
        PopoverPlacement.TOP_LEFT -> GearOverlayPlacement.TopLeft
        PopoverPlacement.TOP -> GearOverlayPlacement.TopCenter
        PopoverPlacement.TOP_RIGHT -> GearOverlayPlacement.TopRight
        PopoverPlacement.BOTTOM_LEFT -> GearOverlayPlacement.BottomLeft
        PopoverPlacement.BOTTOM -> GearOverlayPlacement.BottomCenter
        PopoverPlacement.BOTTOM_RIGHT -> GearOverlayPlacement.BottomRight
        PopoverPlacement.LEFT_TOP -> GearOverlayPlacement.LeftTop
        PopoverPlacement.LEFT -> GearOverlayPlacement.LeftCenter
        PopoverPlacement.LEFT_BOTTOM -> GearOverlayPlacement.LeftBottom
        PopoverPlacement.RIGHT_TOP -> GearOverlayPlacement.RightTop
        PopoverPlacement.RIGHT -> GearOverlayPlacement.RightCenter
        PopoverPlacement.RIGHT_BOTTOM -> GearOverlayPlacement.RightBottom
    }
}
