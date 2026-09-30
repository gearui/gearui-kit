package com.gearui.components.select

import com.gearui.foundation.keyboard.keyboardHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.overlay.LocalOverlayViewportSize
import com.gearui.overlay.OverlayController
import com.gearui.runtime.LocalRuntimeEnvironment
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.unit.Density
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Where an open dropdown's panel goes: its layout, the zero-height anchor at the panel's
 * top (which avoids the generic dropdown rule that forbids covering the trigger — the
 * component owns the trigger), and the trigger's width.
 */
internal data class DropdownPlacement(val layout: SelectPanelLayout, val anchor: Rect, val anchorWidth: Float)

/** The panel for [trigger] (root pixels), or null when the trigger is off screen or no row fits. */
internal fun dropdownPlacement(
    trigger: Rect,
    rowCount: Int,
    selectedRow: Int,
    density: Density,
    viewportHeight: Int,
    safeTop: Dp,
    bottomInset: Dp,
): DropdownPlacement? = with(density) {
    if (viewportHeight <= 0 || trigger.bottom <= 0f || trigger.top >= viewportHeight) return null
    val layout = selectPanelLayout(
        rowCount, selectedRow, FieldSizeTokens.Medium.height.value,
        trigger.top.toDp().value, trigger.bottom.toDp().value,
        viewportHeight.toDp().value, safeTop.value, bottomInset.value,
        ControlGeometry.selectPanelOffset.value,
        contentPadding = ControlGeometry.selectContentPadding.value,
    )
    if (layout.height <= 0f) return null
    val top = layout.top.dp.toPx()
    DropdownPlacement(layout, Rect(trigger.left, top, trigger.right, top), trigger.width)
}

/**
 * Keeps an open dropdown against its trigger. When the trigger moves — the window is
 * resized, the page scrolls, the keyboard comes up — the panel is placed again where it
 * is, not closed and reopened; when there is no room for it any more (the trigger
 * scrolled away) [onLost] closes it. The trigger's area is left to the page, so tapping
 * it again reaches the trigger rather than counting as a tap outside.
 */
@Composable
internal fun TrackDropdownAnchor(
    overlay: OverlayController,
    overlayId: Long?,
    trigger: Rect?,
    placementFor: (Rect) -> DropdownPlacement?,
    onPlaced: (DropdownPlacement) -> Unit,
    onLost: () -> Unit,
) {
    val environment = LocalRuntimeEnvironment.current
    val viewport = LocalOverlayViewportSize.current
    val keyboard = keyboardHeight()
    LaunchedEffect(overlayId, trigger, viewport, environment.safeArea, keyboard) {
        val id = overlayId ?: return@LaunchedEffect
        val bounds = trigger ?: return@LaunchedEffect
        val placement = placementFor(bounds)
        if (placement == null) {
            onLost()
        } else {
            onPlaced(placement)
            overlay.updateAnchor(id, placement.anchor, bounds)
        }
    }
}

/** The bottom inset a dropdown must stay above. */
internal fun dropdownBottomInset(safeBottom: Dp, keyboard: Dp): Dp = maxOf(safeBottom.value, keyboard.value).dp
