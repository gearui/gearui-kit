package com.gearui.components.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Modifier

/** Stable sibling identity is independent of the translated label. */
data class PickerOption(val value: String, val label: String, val children: List<PickerOption> = emptyList())

internal fun pickerPath(options: List<PickerOption>, requested: List<String>, columns: Int): List<PickerOption> {
    require(columns > 0)
    val path = mutableListOf<PickerOption>()
    var level = options
    var matchedAncestors = true
    repeat(columns) { depth ->
        require(level.map { it.value }.distinct().size == level.size) { "Picker values must be unique among siblings" }
        val requestedNode = if (matchedAncestors) level.firstOrNull { it.value == requested.getOrNull(depth) } else null
        if (requestedNode == null) matchedAncestors = false
        val node = requestedNode ?: level.firstOrNull()
        if (node != null) { path += node; level = node.children } else level = emptyList()
    }
    return path
}

/** Typed wheels shared by Picker's stable-value entry points. */
@Composable
internal fun StablePicker(
    visible: Boolean, title: String?, options: List<List<PickerOption>>,
    selectedValues: List<String>, onConfirm: (List<PickerOption>) -> Unit,
    onCancel: () -> Unit, onDismiss: () -> Unit,
) {
    var values by remember(visible, options, selectedValues) {
        mutableStateOf(options.mapIndexed { i, column ->
            require(column.map { it.value }.distinct().size == column.size)
            column.firstOrNull { it.value == selectedValues.getOrNull(i) }?.value ?: column.firstOrNull()?.value
        })
    }
    val valid = options.isNotEmpty() && options.all { it.isNotEmpty() }
    PickerSheet(visible, title, onCancel, {
        if (valid) onConfirm(options.mapIndexed { i, column -> column.first { it.value == values[i] } })
    }, onDismiss, confirmEnabled = valid) {
        PickerWheels(options.size) { column ->
            val items = options[column]
            if (items.isNotEmpty()) key(column, items) {
                WheelPickerColumn(items.map { it.label }, items.indexOfFirst { it.value == values[column] }.coerceAtLeast(0),
                    { index -> values = values.toMutableList().also { it[column] = items[index].value } }, Modifier.weight(1f))
            }
        }
    }
}
