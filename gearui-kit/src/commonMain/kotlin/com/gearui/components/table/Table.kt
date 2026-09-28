package com.gearui.components.table

import com.gearui.foundation.control.ControlGeometry
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.LazyRow
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.unit.Dp
import com.gearui.components.checkbox.Checkbox
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth

/**
 * Pinned column side
 */
enum class TableColFixed {
    NONE,
    LEFT,
    RIGHT
}

/**
 * Table column definition
 */
data class TableColumn<T>(
    val key: String,
    val title: String,
    val width: Dp? = null,
    val align: TableAlign = TableAlign.LEFT,
    val fixed: TableColFixed = TableColFixed.NONE,
    val ellipsis: Boolean = false,
    val render: @Composable (item: T, index: Int) -> Unit
)

/**
 * Table alignment
 */
enum class TableAlign {
    LEFT, CENTER, RIGHT
}

/**
 * Table selection state
 */
class TableSelectionState<T> {
    var selectedItems by mutableStateOf<Set<T>>(emptySet())
        private set

    val isAllSelected: Boolean
        get() = selectedItems.isNotEmpty()

    fun toggleItem(item: T) {
        selectedItems = if (item in selectedItems) {
            selectedItems - item
        } else {
            selectedItems + item
        }
    }

    fun toggleAll(items: List<T>) {
        selectedItems = if (isAllSelected) {
            emptySet()
        } else {
            items.toSet()
        }
    }

    fun clear() {
        selectedItems = emptySet()
    }

    fun isSelected(item: T): Boolean {
        return item in selectedItems
    }
}

@Composable
fun <T> rememberTableSelectionState(): TableSelectionState<T> {
    return remember { TableSelectionState() }
}

/**
 * Table - Data table component
 *
 *
 * Rules:
 * - plain table: generated row by row
 * - pinned-column table: generated column by column; the left and right pinned columns stay put, the middle scrolls horizontally, and all rows move together
 *
 * Height and scrolling (plain table):
 * - with a bounded height (an explicit `Modifier.height`, `weight(1f)`, or a parent that
 *   caps it) the rows are a lazy list that scrolls vertically inside the table;
 * - with an unbounded height — the table placed in a scrolling page without a height —
 *   the rows are laid out eagerly at their full height and the page does the scrolling.
 *   A lazy list cannot be measured against an infinite height, so this is what keeps the
 *   table from crashing there. Keep eager tables short; give long ones a height.
 * The pinned-column table always takes its full height and never scrolls vertically.
 *
 * @param shape corner shape; the whole table (header and rows) is clipped to it, and the
 *   border follows it when [bordered].
 */
@Composable
fun <T> Table(
    data: List<T>,
    columns: List<TableColumn<T>>,
    modifier: Modifier = Modifier,
    selectable: Boolean = false,
    selectionState: TableSelectionState<T>? = null,
    striped: Boolean = false,
    bordered: Boolean = false,
    hoverable: Boolean = true,
    rowHeight: Dp = ControlGeometry.controlMedium,
    emptyText: String = I18n.strings.field.tableEmpty,
    onRowClick: ((T, Int) -> Unit)? = null,
    shape: Shape = Theme.shapes.lg,
) {
    val colors = Theme.colors
    val actualSelectionState = selectionState ?: rememberTableSelectionState()

    // Split the columns into left-pinned, unpinned and right-pinned
    val fixedLeftCols = columns.filter { it.fixed == TableColFixed.LEFT }
    val nonFixedCols = columns.filter { it.fixed == TableColFixed.NONE }
    val fixedRightCols = columns.filter { it.fixed == TableColFixed.RIGHT }

    // Check whether any column is pinned
    val hasFixedCols = fixedLeftCols.isNotEmpty() || fixedRightCols.isNotEmpty()

    // BoxWithConstraints only to learn whether the height is bounded; see the KDoc.
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (bordered) Modifier.border(BorderWidth.thin, colors.border, shape)
                else Modifier
            )
    ) {
        val lazyRows = constraints.hasBoundedHeight
        Column(modifier = Modifier.fillMaxWidth()) {
            if (hasFixedCols) {
                // Has pinned columns: generate by column, middle area scrolls horizontally
                FixedColumnTable(
                    data = data,
                    fixedLeftCols = fixedLeftCols,
                    nonFixedCols = nonFixedCols,
                    fixedRightCols = fixedRightCols,
                    selectable = selectable,
                    selectionState = actualSelectionState,
                    striped = striped,
                    rowHeight = rowHeight,
                    emptyText = emptyText,
                    onRowClick = onRowClick
                )
            } else {
                // Plain table: generate by row
                NormalTable(
                    data = data,
                    columns = columns,
                    selectable = selectable,
                    selectionState = actualSelectionState,
                    striped = striped,
                    rowHeight = rowHeight,
                    emptyText = emptyText,
                    onRowClick = onRowClick,
                    lazyRows = lazyRows
                )
            }
        }
    }
}

/**
 * Plain table - generated row by row. [lazyRows] picks a vertically scrolling lazy list
 * (bounded height) or an eager column (unbounded height, the page scrolls).
 */
@Composable
private fun <T> NormalTable(
    data: List<T>,
    columns: List<TableColumn<T>>,
    selectable: Boolean,
    selectionState: TableSelectionState<T>,
    striped: Boolean,
    rowHeight: Dp,
    emptyText: String,
    onRowClick: ((T, Int) -> Unit)?,
    lazyRows: Boolean
) {
    val colors = Theme.colors

    // Header
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(colors.muted),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selectable) {
            Box(
                modifier = Modifier
                    .width(ControlGeometry.tableSelectionColumn)
                    .fillMaxHeight()
                    .padding(horizontal = Spacing.lg),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = selectionState.isAllSelected && data.isNotEmpty(),
                    onCheckedChange = { selectionState.toggleAll(data) }
                )
            }
        }
        columns.forEach { column ->
            Box(
                modifier = Modifier
                    .then(
                        if (column.width != null) Modifier.width(column.width)
                        else Modifier.weight(1f)
                    )
                    .fillMaxHeight()
                    .padding(horizontal = Spacing.lg),
                contentAlignment = getAlignment(column.align)
            ) {
                Text(
                    text = column.title,
                    style = Theme.typography.titleSmall,
                    color = colors.mutedForeground
                )
            }
        }
    }

    // Divider under the header
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(BorderWidth.hairline)
            .background(colors.border)
    )

    // Data area
    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight * 3)
                .background(colors.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyText,
                style = Theme.typography.bodyMedium,
                color = colors.mutedForeground
            )
        }
    } else {
        if (lazyRows) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(data) { index, item ->
                    NormalTableRow(item, index, data.size, columns, selectable, selectionState, striped, rowHeight, onRowClick)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                data.forEachIndexed { index, item ->
                    NormalTableRow(item, index, data.size, columns, selectable, selectionState, striped, rowHeight, onRowClick)
                }
            }
        }
    }
}

/** One data row of the plain table, followed by its divider unless it is the last. */
@Composable
private fun <T> NormalTableRow(
    item: T,
    index: Int,
    rowCount: Int,
    columns: List<TableColumn<T>>,
    selectable: Boolean,
    selectionState: TableSelectionState<T>,
    striped: Boolean,
    rowHeight: Dp,
    onRowClick: ((T, Int) -> Unit)?
) {
    val colors = Theme.colors
    val isSelected = selectionState.isSelected(item)
    val backgroundColor = when {
        isSelected -> colors.primary.copy(alpha = 0.1f)
        striped && index % 2 == 1 -> colors.muted
        else -> colors.surface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(backgroundColor)
            .then(
                if (onRowClick != null) Modifier.clickable { onRowClick(item, index) }
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selectable) {
            Box(
                modifier = Modifier
                    .width(ControlGeometry.tableSelectionColumn)
                    .fillMaxHeight()
                    .padding(horizontal = Spacing.lg),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { selectionState.toggleItem(item) }
                )
            }
        }
        columns.forEach { column ->
            Box(
                modifier = Modifier
                    .then(
                        if (column.width != null) Modifier.width(column.width)
                        else Modifier.weight(1f)
                    )
                    .fillMaxHeight()
                    .padding(horizontal = Spacing.lg),
                contentAlignment = getAlignment(column.align)
            ) {
                column.render(item, index)
            }
        }
    }

    // Row divider
    if (index < rowCount - 1) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(BorderWidth.hairline)
                .background(colors.border)
        )
    }
}

/**
 * Pinned-column table - generated column by column
 *
 * - left pinned columns: fixed
 * - middle columns: a LazyRow scrolling horizontally, each item being a whole column (header + every data row)
 * - right pinned columns: fixed
 * - every row scrolls together
 */
@Composable
private fun <T> FixedColumnTable(
    data: List<T>,
    fixedLeftCols: List<TableColumn<T>>,
    nonFixedCols: List<TableColumn<T>>,
    fixedRightCols: List<TableColumn<T>>,
    selectable: Boolean,
    selectionState: TableSelectionState<T>,
    striped: Boolean,
    rowHeight: Dp,
    emptyText: String,
    onRowClick: ((T, Int) -> Unit)?
) {
    val colors = Theme.colors
    val defaultCellWidth = ControlGeometry.tableCellWidth

    // Total height
    val headerHeight = rowHeight + BorderWidth.hairline
    val dataHeight = if (data.isNotEmpty()) {
        rowHeight * data.size + BorderWidth.hairline * (data.size - 1)
    } else {
        rowHeight * 3 // empty-state height
    }
    val totalHeight = headerHeight + dataHeight

    Row(modifier = Modifier.fillMaxWidth().height(totalHeight)) {
        // ========== Left pinned columns ==========
        if (fixedLeftCols.isNotEmpty()) {
            Row {
                fixedLeftCols.forEach { column ->
                    val colWidth = column.width ?: defaultCellWidth
                    // Each column is one Column (header + every data row)
                    ColumnContent(
                        column = column,
                        colWidth = colWidth,
                        data = data,
                        rowHeight = rowHeight,
                        selectionState = selectionState,
                        striped = striped,
                        emptyText = emptyText,
                        onRowClick = onRowClick,
                        showEmptyText = false
                    )
                }
                // Right border
                Box(
                    modifier = Modifier
                        .width(BorderWidth.thin)
                        .fillMaxHeight()
                        .background(colors.border)
                )
            }
        }

        // ========== Middle scrollable columns ==========
        LazyRow(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            userScrollEnabled = true
        ) {
            items(nonFixedCols) { column ->
                val colWidth = column.width ?: defaultCellWidth
                ColumnContent(
                    column = column,
                    colWidth = colWidth,
                    data = data,
                    rowHeight = rowHeight,
                    selectionState = selectionState,
                    striped = striped,
                    emptyText = emptyText,
                    onRowClick = onRowClick,
                    showEmptyText = column == nonFixedCols.firstOrNull()
                )
            }
        }

        // ========== Right pinned columns ==========
        if (fixedRightCols.isNotEmpty()) {
            Row {
                // Left border
                Box(
                    modifier = Modifier
                        .width(BorderWidth.thin)
                        .fillMaxHeight()
                        .background(colors.border)
                )
                fixedRightCols.forEach { column ->
                    val colWidth = column.width ?: defaultCellWidth
                    ColumnContent(
                        column = column,
                        colWidth = colWidth,
                        data = data,
                        rowHeight = rowHeight,
                        selectionState = selectionState,
                        striped = striped,
                        emptyText = emptyText,
                        onRowClick = onRowClick,
                        showEmptyText = false
                    )
                }
            }
        }
    }
}

/**
 * A single column (header + every data row)
 */
@Composable
private fun <T> ColumnContent(
    column: TableColumn<T>,
    colWidth: Dp,
    data: List<T>,
    rowHeight: Dp,
    selectionState: TableSelectionState<T>,
    striped: Boolean,
    emptyText: String,
    onRowClick: ((T, Int) -> Unit)?,
    showEmptyText: Boolean
) {
    val colors = Theme.colors

    Column {
        // Header
        Box(
            modifier = Modifier
                .width(colWidth)
                .height(rowHeight)
                .background(colors.muted)
                .padding(horizontal = Spacing.lg),
            contentAlignment = getAlignment(column.align)
        ) {
            Text(
                text = column.title,
                style = Theme.typography.titleSmall,
                color = colors.mutedForeground
            )
        }
        // Divider under the header
        Box(
            modifier = Modifier
                .width(colWidth)
                .height(BorderWidth.hairline)
                .background(colors.border)
        )
        // Data rows
        if (data.isEmpty()) {
            Box(
                modifier = Modifier
                    .width(colWidth)
                    .height(rowHeight * 3)
                    .background(colors.surface),
                contentAlignment = Alignment.Center
            ) {
                if (showEmptyText) {
                    Text(
                        text = emptyText,
                        style = Theme.typography.bodyMedium,
                        color = colors.mutedForeground
                    )
                }
            }
        } else {
            data.forEachIndexed { index, item ->
                val isSelected = selectionState.isSelected(item)
                val backgroundColor = when {
                    isSelected -> colors.primary.copy(alpha = 0.1f)
                    striped && index % 2 == 1 -> colors.muted
                    else -> colors.surface
                }
                Box(
                    modifier = Modifier
                        .width(colWidth)
                        .height(rowHeight)
                        .background(backgroundColor)
                        .then(
                            if (onRowClick != null) Modifier.clickable { onRowClick(item, index) }
                            else Modifier
                        )
                        .padding(horizontal = Spacing.lg),
                    contentAlignment = getAlignment(column.align)
                ) {
                    column.render(item, index)
                }
                // Row divider
                if (index < data.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(colWidth)
                            .height(BorderWidth.hairline)
                            .background(colors.border)
                    )
                }
            }
        }
    }
}

/**
 * Resolves the alignment
 */
private fun getAlignment(align: TableAlign): Alignment {
    return when (align) {
        TableAlign.LEFT -> Alignment.CenterStart
        TableAlign.CENTER -> Alignment.Center
        TableAlign.RIGHT -> Alignment.CenterEnd
    }
}

/**
 * Simple table for basic use cases
 */
@Composable
fun SimpleTable(
    headers: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier,
    striped: Boolean = false,
    bordered: Boolean = false,
    shape: Shape = Theme.shapes.lg,
) {
    val colors = Theme.colors

    val columns = headers.mapIndexed { index, header ->
        TableColumn<List<String>>(
            key = "col_$index",
            title = header,
            render = { row, _ ->
                Text(
                    text = row.getOrNull(index) ?: "",
                    style = Theme.typography.bodyMedium,
                    color = colors.foreground
                )
            }
        )
    }

    Table(
        data = rows,
        columns = columns,
        modifier = modifier,
        striped = striped,
        bordered = bordered,
        shape = shape
    )
}
