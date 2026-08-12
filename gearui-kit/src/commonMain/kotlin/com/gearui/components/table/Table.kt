package com.gearui.components.table

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
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.checkbox.Checkbox
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth

/**
 * Pinned column side
 */
enum class TableColFixed {
    NONE,   // 不固定
    LEFT,   // 左侧固定
    RIGHT   // 右侧固定
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
 * - plain table: generated row by row, scrolls vertically
 * - pinned-column table: generated column by column; the left and right pinned columns stay put, the middle scrolls horizontally, and all rows move together
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
    rowHeight: Dp = 48.dp,
    emptyText: String = I18n.strings.field.tableEmpty,
    onRowClick: ((T, Int) -> Unit)? = null
) {
    val colors = Theme.colors
    val actualSelectionState = selectionState ?: rememberTableSelectionState()

    // Split the columns into left-pinned, unpinned and right-pinned
    val fixedLeftCols = columns.filter { it.fixed == TableColFixed.LEFT }
    val nonFixedCols = columns.filter { it.fixed == TableColFixed.NONE }
    val fixedRightCols = columns.filter { it.fixed == TableColFixed.RIGHT }

    // Check whether any column is pinned
    val hasFixedCols = fixedLeftCols.isNotEmpty() || fixedRightCols.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (bordered) Modifier.border(BorderWidth.thin, colors.border, Theme.shapes.lg)
                else Modifier
            )
    ) {
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
            // Plain table: generate by row, vertical scrolling only
            NormalTable(
                data = data,
                columns = columns,
                selectable = selectable,
                selectionState = actualSelectionState,
                striped = striped,
                rowHeight = rowHeight,
                emptyText = emptyText,
                onRowClick = onRowClick
            )
        }
    }
}

/**
 * Plain table - generated row by row, vertical scrolling only
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
    onRowClick: ((T, Int) -> Unit)?
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
                    .width(56.dp)
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
                    style = Typography.TitleSmall,
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
                style = Typography.BodyMedium,
                color = colors.mutedForeground
            )
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(data) { index, item ->
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
                                .width(56.dp)
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
                if (index < data.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(BorderWidth.hairline)
                            .background(colors.border)
                    )
                }
            }
        }
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
    val defaultCellWidth = 100.dp

    // Total height
    val headerHeight = rowHeight + 0.5.dp
    val dataHeight = if (data.isNotEmpty()) {
        rowHeight * data.size + 0.5.dp * (data.size - 1)
    } else {
        rowHeight * 3  // 空状态高度
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
                style = Typography.TitleSmall,
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
                        style = Typography.BodyMedium,
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
    bordered: Boolean = false
) {
    val colors = Theme.colors

    val columns = headers.mapIndexed { index, header ->
        TableColumn<List<String>>(
            key = "col_$index",
            title = header,
            render = { row, _ ->
                Text(
                    text = row.getOrNull(index) ?: "",
                    style = Typography.BodyMedium,
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
        bordered = bordered
    )
}
