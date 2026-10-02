package com.gearui.components.listbox

import com.gearui.components.icon.*
import androidx.compose.runtime.*
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.icon.Icons
import com.gearui.components.select.SelectOption
import com.gearui.foundation.interaction.choiceSemantics
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.list.CellDefaults
import com.gearui.foundation.list.LocalRowInteractionSource
import com.gearui.foundation.motion.rowPressFeedback
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.compose.ui.semantics.Role

/**
 * Standalone option list, laid out as the platform's grouped list: each run of options
 * with the same [SelectOption.group] is one card under its group title, rows are divided
 * by inset separators, and the chosen rows carry a trailing check. The row itself is not
 * tinted, so a selection reads the same in single and multiple mode.
 *
 * It takes the same values and groups as Select, without a trigger or overlay. It does
 * not scroll by itself: place it in the page's scroll, or use Select for a long list.
 */
@Composable
fun <T> ListBox(
    options: List<SelectOption<T>>, selectedValues: Set<T>, onSelectionChange: (Set<T>) -> Unit,
    modifier: Modifier = Modifier, multiple: Boolean = false, enabled: Boolean = true,
) {
    require(options.map { it.value }.distinct().size == options.size)
    val colors = Theme.colors
    if (options.isEmpty()) {
        Text(I18n.strings.common.noData, style = Theme.typography.bodyMedium, color = colors.mutedForeground, modifier = modifier.padding(Spacing.lg))
        return
    }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        listBoxGroups(options).forEach { run ->
            CellGroup(run, title = run.first().group, key = { it.value as Any }) { option ->
                ListBoxRow(option, option.value in selectedValues, enabled && !option.disabled) {
                    onSelectionChange(listBoxSelection(selectedValues, option.value, multiple))
                }
            }
        }
    }
}

@Composable
private fun <T> ListBoxRow(option: SelectOption<T>, selected: Boolean, usable: Boolean, choose: () -> Unit) {
    val colors = Theme.colors
    val tokens = CellDefaults.Default
    val interaction = LocalRowInteractionSource.current ?: remember { MutableInteractionSource() }
    Row(Modifier.fillMaxWidth().heightIn(min = tokens.minHeight)
        .choiceSemantics(option.label, selected, Role.Button, if (usable) choose else null)
        .rowPressFeedback(interaction, RectangleShape, enabled = usable, scale = false, base = colors.surface)
        .clickable(enabled = usable, interactionSource = interaction, indication = null, onClick = choose)
        .padding(horizontal = tokens.paddingHorizontal, vertical = tokens.paddingVertical),
        verticalAlignment = Alignment.CenterVertically) {
        Text(option.label, style = Theme.typography.bodyMedium,
            color = if (usable) colors.foreground else colors.mutedForeground, modifier = Modifier.weight(1f))
        if (selected) Icon(Icons.check, size = IconSizes.Default.md, tint = if (usable) colors.primary else colors.mutedForeground)
    }
}

/** Consecutive options that share a group, in order; an ungrouped run has a null group. */
internal fun <T> listBoxGroups(options: List<SelectOption<T>>): List<List<SelectOption<T>>> {
    val runs = mutableListOf<MutableList<SelectOption<T>>>()
    options.forEach { option ->
        val last = runs.lastOrNull()
        if (last != null && last.first().group == option.group) last += option else runs += mutableListOf(option)
    }
    return runs
}

internal fun <T> listBoxSelection(current: Set<T>, value: T, multiple: Boolean): Set<T> =
    if (!multiple) setOf(value) else if (value in current) current - value else current + value
