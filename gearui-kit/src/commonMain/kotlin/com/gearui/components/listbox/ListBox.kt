package com.gearui.components.listbox

import androidx.compose.runtime.*
import com.gearui.components.select.SelectOption
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.interaction.choiceSemantics
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.motion.rowPressFeedback
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.typography.IconSizes
import com.gearui.components.icon.Icons
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.semantics.Role

/** Standalone option list; uses the same values and groups as Select, without a trigger or overlay. */
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
    LazyColumn(modifier.fillMaxWidth().heightIn(max = ControlGeometry.cascaderListHeight)) {
        itemsIndexed(options) { index, option ->
            val selected = option.value in selectedValues
            val usable = enabled && !option.disabled
            val interaction = remember { MutableInteractionSource() }
            val choose = { onSelectionChange(listBoxSelection(selectedValues, option.value, multiple)) }
            Column {
                if (option.group != null && (index == 0 || options[index - 1].group != option.group)) {
                    Text(option.group, style = Theme.typography.label, color = colors.mutedForeground, modifier = Modifier.padding(Spacing.md))
                }
                Row(Modifier.fillMaxWidth().heightIn(min = ControlGeometry.controlMedium)
                    .choiceSemantics(option.label, selected, Role.Button, if (usable) choose else null)
                    .rowPressFeedback(interaction, Theme.shapes.sm, enabled = usable, base = if (selected) colors.primarySoft else colors.surface)
                    .clickable(enabled = usable, interactionSource = interaction, indication = null, onClick = choose)
                    .padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
                    Text(option.label, style = Theme.typography.bodyMedium, color = if (!usable) colors.mutedForeground else colors.foreground, modifier = Modifier.weight(1f))
                    if (selected) Icon(Icons.check, size = IconSizes.Default.md, tint = colors.primary)
                }
            }
        }
    }
}

internal fun <T> listBoxSelection(current: Set<T>, value: T, multiple: Boolean): Set<T> =
    if (!multiple) setOf(value) else if (value in current) current - value else current + value
