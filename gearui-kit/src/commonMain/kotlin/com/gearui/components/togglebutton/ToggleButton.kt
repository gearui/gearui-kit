package com.gearui.components.togglebutton

import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonShape
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier

/**
 * ToggleButton — HeroUI v3 `ToggleButton` for mobile: a button that stays pressed,
 * for a setting the user flips in place (bold in an editor, "only show unread").
 *
 * Selected is the filled button, unselected the outline one, so the state is legible
 * without relying on a tint the user has to compare against a neighbour. Switch is for
 * a setting in a list; ToggleButton belongs in a toolbar or a row of filters.
 */
@Composable
fun ToggleButton(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    text: String = "",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: String? = null,
    size: ButtonSize = ButtonSize.MEDIUM,
    shape: ButtonShape = ButtonShape.ROUND,
    theme: ButtonTheme = ButtonTheme.PRIMARY,
) {
    Button(
        text = text,
        onClick = { onSelectedChange(!selected) },
        modifier = modifier,
        theme = if (selected) theme else ButtonTheme.DEFAULT,
        type = if (selected) ButtonType.FILL else ButtonType.OUTLINE,
        size = size,
        shape = shape,
        disabled = !enabled,
        icon = icon,
    )
}

/** One option in a [ToggleButtonGroup]. */
data class ToggleButtonItem(
    val key: String,
    val label: String = "",
    val icon: String? = null,
    val enabled: Boolean = true,
)

/**
 * ToggleButtonGroup — a row of [ToggleButton]s sharing one selection.
 *
 * [multiple] false keeps exactly one selected at a time, like a radio row that looks
 * like buttons; true lets any number be selected, like a filter bar. Tapping the only
 * selected item in single mode clears it, matching TagGroup.
 */
@Composable
fun ToggleButtonGroup(
    items: List<ToggleButtonItem>,
    selectedKeys: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    multiple: Boolean = false,
    size: ButtonSize = ButtonSize.MEDIUM,
    theme: ButtonTheme = ButtonTheme.PRIMARY,
    itemSpacing: com.tencent.kuikly.compose.ui.unit.Dp = com.gearui.foundation.layout.Spacing.sm,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            ToggleButton(
                selected = item.key in selectedKeys,
                onSelectedChange = { onSelectionChange(nextToggleSelection(selectedKeys, item.key, multiple)) },
                text = item.label,
                enabled = enabled && item.enabled,
                icon = item.icon,
                size = size,
                theme = theme,
            )
        }
    }
}

/** Selection after tapping [key]; single mode clears when the selected item is tapped again. */
internal fun nextToggleSelection(current: Set<String>, key: String, multiple: Boolean): Set<String> = when {
    multiple && key in current -> current - key
    multiple -> current + key
    key in current -> emptySet()
    else -> setOf(key)
}
