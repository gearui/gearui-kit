package com.gearui.components.submenu

import androidx.compose.runtime.*
import com.gearui.components.bottomsheet.BottomSheet
import com.gearui.components.closebutton.CloseButton
import com.gearui.components.icon.Icons
import com.gearui.components.listbox.ListBox
import com.gearui.components.select.SelectOption
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier

/** A menu node is either a nested group or an action. Stable IDs identify siblings. */
data class SubMenuItem(val id: String, val label: String, val children: List<SubMenuItem> = emptyList(),
    val disabled: Boolean = false, val onClick: (() -> Unit)? = null)

/** Mobile nested menu: one full-width level at a time, with an explicit route back to its parent. */
@Composable
fun SubMenu(title: String, items: List<SubMenuItem>, modifier: Modifier = Modifier,
    trigger: @Composable (open: () -> Unit) -> Unit) {
    var open by remember { mutableStateOf(false) }
    var path by remember { mutableStateOf<List<String>>(emptyList()) }
    Box(modifier) { trigger { path = emptyList(); open = true } }
    BottomSheet.Host(open, { open = false }) {
        val ancestors = subMenuPath(items, path)
        val level = ancestors.lastOrNull()?.children ?: items
        Column(Modifier.fillMaxWidth().padding(Spacing.md)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (path.isNotEmpty()) CloseButton({ path = path.dropLast(1) }, icon = Icons.caret_left, contentDescription = I18n.strings.common.back)
                Text(ancestors.lastOrNull()?.label ?: title, style = Theme.typography.titleMedium, color = Theme.colors.foreground, modifier = Modifier.weight(1f))
                CloseButton({ open = false })
            }
            ListBox(options = level.map { SelectOption(it.id, it.label, disabled = it.disabled) },
                selectedValues = emptySet(), onSelectionChange = { selected ->
                    val item = level.first { it.id == selected.first() }
                    if (item.children.isNotEmpty()) path = ancestors.map { it.id } + item.id
                    else { open = false; item.onClick?.invoke() }
                })
        }
    }
}

internal fun subMenuPath(items: List<SubMenuItem>, ids: List<String>): List<SubMenuItem> {
    val result = mutableListOf<SubMenuItem>()
    var level = items
    for (id in ids) {
        val node = level.firstOrNull { it.id == id } ?: break
        result += node
        level = node.children
    }
    return result
}
