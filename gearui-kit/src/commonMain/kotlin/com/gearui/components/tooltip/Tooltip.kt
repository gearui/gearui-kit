package com.gearui.components.tooltip

import androidx.compose.runtime.Composable
import com.gearui.components.popover.PopoverPlacement
import com.gearui.components.popover.PopoverState
import com.gearui.components.popover.PopoverTheme
import com.gearui.components.popover.rememberPopoverState
import com.tencent.kuikly.compose.ui.Modifier

typealias TooltipState = PopoverState
typealias TooltipPlacement = PopoverPlacement
typealias TooltipTheme = PopoverTheme

/**
 * Remembers tooltip state.
 */
@Composable
fun rememberTooltipState(): TooltipState = rememberPopoverState()

/**
 * Tooltip - lightweight text hint based on GearUI popover.
 *
 * @param text tooltip text content
 * @param state controlled tooltip state
 * @param modifier modifier applied to trigger container
 * @param placement tooltip placement relative to trigger
 * @param theme visual theme
 * @param trigger trigger content with open callback
 */
@Composable
fun Tooltip(
    text: String,
    state: TooltipState,
    modifier: Modifier = Modifier,
    placement: TooltipPlacement = TooltipPlacement.BOTTOM,
    theme: TooltipTheme = TooltipTheme.DARK,
    autoDismissMillis: Long = 1500L,
    trigger: @Composable (onClick: () -> Unit) -> Unit
) {
    com.gearui.components.popover.Tooltip(
        text = text,
        state = state,
        modifier = modifier,
        placement = placement,
        theme = theme,
        autoDismissMillis = autoDismissMillis,
        trigger = trigger
    )
}
