package com.gearui.components.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Modifier

/**
 * One choice of a picker. [value] identifies it among its siblings and is what a picker
 * returns; [label] is only shown, so translations and duplicate names are harmless.
 * Siblings sharing a value (bad server data) are reduced to the first of them.
 */
data class PickerOption(val value: String, val label: String, val children: List<PickerOption> = emptyList())

internal fun pickerPath(options: List<PickerOption>, requested: List<String>, columns: Int): List<PickerOption> {
    require(columns > 0)
    val path = mutableListOf<PickerOption>()
    var level = options
    var matchedAncestors = true
    repeat(columns) { depth ->
        level = level.distinctByValue()
        val requestedNode = if (matchedAncestors) level.firstOrNull { it.value == requested.getOrNull(depth) } else null
        if (requestedNode == null) matchedAncestors = false
        val node = requestedNode ?: level.firstOrNull()
        if (node != null) { path += node; level = node.children } else level = emptyList()
    }
    return path
}

/** A path is complete when it fills every column or ends at a leaf. */
internal fun pickerPathComplete(path: List<PickerOption>, columns: Int): Boolean =
    path.isNotEmpty() && (path.size == columns || path.last().children.isEmpty())

internal fun List<PickerOption>.distinctByValue(): List<PickerOption> =
    if (map { it.value }.toSet().size == size) this else distinctBy { it.value }

/** Typed wheels shared by Picker's stable-value entry points. */
@Composable
internal fun StablePicker(
    visible: Boolean, title: String?, options: List<List<PickerOption>>,
    selectedValues: List<String>, onConfirm: (List<PickerOption>) -> Unit,
    onCancel: () -> Unit, onDismiss: () -> Unit,
) {
    val columns = remember(options) { options.map { it.distinctByValue() } }
    var values by remember(visible, columns, selectedValues) {
        mutableStateOf(columns.mapIndexed { i, column ->
            column.firstOrNull { it.value == selectedValues.getOrNull(i) }?.value ?: column.firstOrNull()?.value
        })
    }
    val valid = columns.isNotEmpty() && columns.all { it.isNotEmpty() }
    PickerSheet(visible, title, onCancel, {
        if (valid) onConfirm(columns.mapIndexed { i, column -> column.first { it.value == values[i] } })
    }, onDismiss, confirmEnabled = valid) {
        PickerWheels(columns.size) { column ->
            val items = columns[column]
            if (items.isNotEmpty()) key(column, items) {
                WheelPickerColumn(items.map { it.label }, items.indexOfFirst { it.value == values[column] }.coerceAtLeast(0),
                    { index -> values = values.toMutableList().also { it[column] = items[index].value } }, Modifier.weight(1f))
            }
        }
    }
}
