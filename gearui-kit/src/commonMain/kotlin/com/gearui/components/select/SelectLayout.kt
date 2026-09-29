package com.gearui.components.select

/** A heading is a real list row, so selection/scroll indices include it. */
internal data class SelectRow<T>(val heading: String? = null, val option: SelectOption<T>? = null)

internal fun <T> selectRows(options: List<SelectOption<T>>): List<SelectRow<T>> = buildList {
    var previousGroup: String? = null
    options.forEach { option ->
        val group = option.group?.takeIf { it.isNotBlank() }
        if (group != null && group != previousGroup) add(SelectRow(heading = group))
        add(SelectRow(option = option))
        previousGroup = group
    }
}

internal data class SelectPanelLayout(val top: Float, val height: Float, val firstRow: Int)

/** Values are logical pixels, measured in the overlay root's coordinate space. */
internal fun selectPanelLayout(
    rowCount: Int, selectedRow: Int, rowHeight: Float,
    anchorTop: Float, anchorBottom: Float,
    viewportHeight: Float, safeTop: Float, safeBottom: Float,
    margin: Float,
    contentPadding: Float = 0f,
): SelectPanelLayout {
    val top = (safeTop + margin).coerceAtMost(viewportHeight)
    val bottom = (viewportHeight - safeBottom - margin).coerceAtLeast(top)
    val padding = contentPadding.coerceAtLeast(0f)
    val content = rowCount.coerceAtLeast(1) * rowHeight + padding * 2
    val below = (bottom - anchorBottom - margin).coerceAtLeast(0f)
    val above = (anchorTop - top - margin).coerceAtLeast(0f)
    val selected = selectedRow.coerceIn(0, (rowCount - 1).coerceAtLeast(0))
    // Prefer the reference's bottom placement whenever the entire panel fits.
    val opensBelow = below >= content || below >= above
    val height = minOf(content, if (opensBelow) below else above)
    val desiredTop = if (opensBelow) anchorBottom + margin
        else anchorTop - margin - height
    val y = desiredTop.coerceIn(top, (bottom - height).coerceAtLeast(top))
    val visibleRows = ((height - padding * 2) / rowHeight).toInt().coerceAtLeast(1)
    val first = if (selected < visibleRows) 0
        else (selected - visibleRows + 1).coerceIn(0, (rowCount - visibleRows).coerceAtLeast(0))
    return SelectPanelLayout(y, height, first)
}
