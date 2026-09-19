package com.gearui.components.contextmenu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.tencent.kuikly.compose.ui.graphics.Color
import androidx.compose.runtime.setValue
import com.gearui.foundation.layout.Spacing
import com.gearui.components.popover.PopoverPlacement
import com.gearui.components.popover.PopoverTheme
import com.gearui.components.popover.rememberPopoverState
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.rememberOverlay
import com.gearui.foundation.material.MaterialSurface
import com.gearui.foundation.material.Materials
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
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
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.menuItemFeedback
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.text.font.FontWeight

/**
 * Context menu action model.
 *
 * @param label action text
 * @param icon optional leading icon (gearui [Icon] name, e.g. `Icons.users_three`)
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
    val currentItems by rememberUpdatedState(items)

    val bounds = triggerBounds
    // 🔴 Re-anchoring is not the user closing the menu.
    //
    // The DisposableEffect below is keyed on bounds: when the anchor moves it
    // disposes the old overlay and shows a new one at the new position. But
    // dispose calls overlay.dismiss(), which invokes onDismiss, which is
    // state.hide() — so the menu closes itself by moving, and never comes back.
    //
    // The trigger is ordinary: OverlayHost dismisses the system keyboard before
    // showing an overlay (dismissKeyboardOnShow), the list reflows as the
    // keyboard goes, and the anchor moves with it. This flag separates
    // "disposed in order to move" from "actually dismissed".
    val reanchoring = remember { mutableStateOf(false) }
    if (state.isVisible && bounds != null) {
        DisposableEffect(bounds, placement) {
            val overlayId = overlay.show(
                anchorBounds = bounds,
                options = OverlayOptions(
                    placement = placementToOverlay(placement),
                    offsetY = OverlayDefaults.anchorOffset,
                    modal = false,
                    maskColor = null,
                    dismissPolicy = OverlayDismissPolicy.Dropdown.copy(
                        outsideClick = true,
                        scroll = true
                    )
                ),
                onDismiss = {
                    if (!reanchoring.value) state.hide()
                }
            ) {
                val colors = Theme.colors
                val shapes = Theme.shapes
                Column(
                    modifier = Modifier
                        // width(IntrinsicSize.Max): the column width is the intrinsic width of the longest item,
                        // with widthIn keeping very short or very long content in check, so short text does not bloat out to max.
                        .width(IntrinsicSize.Max)
                        .widthIn(min = 140.dp, max = 260.dp)
                ) {
                // MaterialSurface owns the token shadow stack outside its content clip.
                MaterialSurface(
                    material = Materials.Popover,
                    shape = OverlayDefaults.panelShape,
                ) {
                val colors = Theme.colors
                // HeroUI Native menu.css: no border (the overlay shadow separates it),
                // padding-inline 6 / padding-block 12, rows at radius 16.
                val itemShape = RoundedCornerShape(ControlGeometry.radiusMenuItem)
                Column(
                    modifier = Modifier.padding(
                        horizontal = ControlGeometry.menuPaddingInline,
                        vertical = ControlGeometry.menuPaddingBlock,
                    )
                ) {
                    currentItems.forEach { item ->
                        val interaction = remember(item) { MutableInteractionSource() }
                        val itemColor = when {
                            item.disabled -> colors.mutedForeground
                            item.danger -> colors.destructive
                            else -> colors.foreground
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuItemFeedback(
                                    interaction = interaction,
                                    shape = itemShape,
                                    enabled = !item.disabled,
                                    danger = item.danger,
                                )
                                .clickable(enabled = !item.disabled, interactionSource = interaction, indication = null) {
                                    item.onClick()
                                    state.hide()
                                }
                                .padding(
                                    horizontal = ControlGeometry.menuItemPaddingInline,
                                    vertical = ControlGeometry.menuItemPaddingBlock,
                                )
                                .alpha(if (item.disabled) FeedbackDefaults.disabledOpacity else 1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(ControlGeometry.menuItemGap),
                        ) {
                            if (item.icon != null) {
                                Icon(
                                    name = item.icon,
                                    size = IconSizes.Default.lg,
                                    tint = itemColor,
                                )
                            }
                            Text(
                                text = item.label,
                                style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = if (item.disabled) colors.foreground else itemColor,
                            )
                        }
                    }
                }
                }
                }
            }

            onDispose {
                reanchoring.value = true
                overlay.dismiss(overlayId)
                reanchoring.value = false
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
