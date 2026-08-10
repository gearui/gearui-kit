package com.gearui.components.contextmenu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.foundation.layout.Spacing
import com.gearui.components.popover.PopoverPlacement
import com.gearui.components.popover.PopoverTheme
import com.gearui.components.popover.rememberPopoverState
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberOverlay
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.IntrinsicSize
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.layout.widthIn
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.elevation.Elevation
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.border.BorderWidth

/**
 * Context menu action model.
 *
 * @param label action text
 * @param icon optional leading icon (gearui [Icon] name, e.g. `Icons.groups`)
 * @param disabled whether action is disabled
 * @param danger whether action uses danger semantic color
 * @param onClick action callback
 */
data class ContextMenuItem(
    val label: String,
    val icon: String? = null,
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
    val overlay = rememberOverlay()
    val colors = Theme.colors
    val shapes = Theme.shapes
    var triggerBounds by remember { mutableStateOf<Rect?>(null) }
    var pressedIndex by remember { mutableStateOf<Int?>(null) }

    val bounds = triggerBounds
    if (state.isVisible && bounds != null) {
        DisposableEffect(bounds, placement) {
            val overlayId = overlay.show(
                anchorBounds = bounds,
                options = OverlayOptions(
                    placement = placementToOverlay(placement),
                    offsetY = Spacing.xs,
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
                        // width(IntrinsicSize.Max): 列宽 = 最长 item 的固有宽度，
                        // 配合 widthIn 防止极短/极长内容跑偏；这样不会出现"短文字撑满 max"的虚胖。
                        .width(IntrinsicSize.Max)
                        .widthIn(min = 140.dp, max = 260.dp)
                        .shadow(Elevation.raised, OverlayDefaults.panelShape)
                        .clip(OverlayDefaults.panelShape)
                        .background(colors.surface)
                        .border(BorderWidth.thin, colors.border, OverlayDefaults.panelShape)
                        .padding(Spacing.xs)
                ) {
                    items.forEachIndexed { index, item ->
                        val itemColor = when {
                            item.disabled -> colors.mutedForeground
                            item.danger -> colors.destructive
                            else -> colors.foreground
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shapes.sm)
                                .background(
                                    if (pressedIndex == index) colors.muted else colors.surface
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
                                    horizontal = Spacing.md,
                                    vertical = 10.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (item.icon != null) {
                                Icon(
                                    name = item.icon,
                                    size = 18.dp,
                                    tint = itemColor,
                                )
                                Spacer(Modifier.width(10.dp))
                            }
                            Text(
                                text = item.label,
                                style = Typography.BodyMedium,
                                color = itemColor,
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

private fun placementToOverlay(placement: PopoverPlacement): OverlayPlacement {
    return when (placement) {
        PopoverPlacement.TOP_LEFT -> OverlayPlacement.TopLeft
        PopoverPlacement.TOP -> OverlayPlacement.TopCenter
        PopoverPlacement.TOP_RIGHT -> OverlayPlacement.TopRight
        PopoverPlacement.BOTTOM_LEFT -> OverlayPlacement.BottomLeft
        PopoverPlacement.BOTTOM -> OverlayPlacement.BottomCenter
        PopoverPlacement.BOTTOM_RIGHT -> OverlayPlacement.BottomRight
        PopoverPlacement.LEFT_TOP -> OverlayPlacement.LeftTop
        PopoverPlacement.LEFT -> OverlayPlacement.LeftCenter
        PopoverPlacement.LEFT_BOTTOM -> OverlayPlacement.LeftBottom
        PopoverPlacement.RIGHT_TOP -> OverlayPlacement.RightTop
        PopoverPlacement.RIGHT -> OverlayPlacement.RightCenter
        PopoverPlacement.RIGHT_BOTTOM -> OverlayPlacement.RightBottom
    }
}
