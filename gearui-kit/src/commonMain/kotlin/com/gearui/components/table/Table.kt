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

/**
 * 列固定位置
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
 * 规则：
 * - 普通表格：按行生成，支持上下滚动
 * - 固定列表格：按列生成，左右固定列不动，中间列可左右滚动，所有行联动
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
    emptyText: String = "暂无数据",
    onRowClick: ((T, Int) -> Unit)? = null
) {
    val colors = Theme.colors
    val actualSelectionState = selectionState ?: rememberTableSelectionState()

    // 分类列：左固定、非固定、右固定
    val fixedLeftCols = columns.filter { it.fixed == TableColFixed.LEFT }
    val nonFixedCols = columns.filter { it.fixed == TableColFixed.NONE }
    val fixedRightCols = columns.filter { it.fixed == TableColFixed.RIGHT }

    // 检查是否有固定列
    val hasFixedCols = fixedLeftCols.isNotEmpty() || fixedRightCols.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (bordered) Modifier.border(1.dp, colors.border, RoundedCornerShape(8.dp))
                else Modifier
            )
    ) {
        if (hasFixedCols) {
            // 有固定列：按列生成，中间可左右滚动
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
            // 普通表格：按行生成，只支持上下滚动
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
 * 普通表格 - 按行生成，只支持上下滚动
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

    // 表头
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(colors.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selectable) {
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
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
                    .padding(horizontal = 16.dp),
                contentAlignment = getAlignment(column.align)
            ) {
                Text(
                    text = column.title,
                    style = Typography.TitleSmall,
                    color = colors.textSecondary
                )
            }
        }
    }

    // 表头下分割线
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(colors.border)
    )

    // 数据区域
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
                color = colors.textSecondary
            )
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(data) { index, item ->
                val isSelected = selectionState.isSelected(item)
                val backgroundColor = when {
                    isSelected -> colors.primary.copy(alpha = 0.1f)
                    striped && index % 2 == 1 -> colors.surfaceVariant
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
                                .padding(horizontal = 16.dp),
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
                                .padding(horizontal = 16.dp),
                            contentAlignment = getAlignment(column.align)
                        ) {
                            column.render(item, index)
                        }
                    }
                }

                // 行分割线
                if (index < data.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(colors.border)
                    )
                }
            }
        }
    }
}

/**
 * 固定列表格 - 按列生成
 *
 * - 左固定列：不动
 * - 中间列：LazyRow 实现左右滚动，每个 item 是一整列（表头+所有数据行）
 * - 右固定列：不动
 * - 所有行联动滚动
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

    // 计算总高度
    val headerHeight = rowHeight + 0.5.dp
    val dataHeight = if (data.isNotEmpty()) {
        rowHeight * data.size + 0.5.dp * (data.size - 1)
    } else {
        rowHeight * 3  // 空状态高度
    }
    val totalHeight = headerHeight + dataHeight

    Row(modifier = Modifier.fillMaxWidth().height(totalHeight)) {
        // ========== 左固定列 ==========
        if (fixedLeftCols.isNotEmpty()) {
            Row {
                fixedLeftCols.forEach { column ->
                    val colWidth = column.width ?: defaultCellWidth
                    // 每列是一个 Column（表头 + 所有数据行）
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
                // 右边框
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(colors.border)
                )
            }
        }

        // ========== 中间可滚动列 ==========
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

        // ========== 右固定列 ==========
        if (fixedRightCols.isNotEmpty()) {
            Row {
                // 左边框
                Box(
                    modifier = Modifier
                        .width(1.dp)
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
 * 单列内容（表头 + 所有数据行）
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
        // 表头
        Box(
            modifier = Modifier
                .width(colWidth)
                .height(rowHeight)
                .background(colors.surfaceVariant)
                .padding(horizontal = 16.dp),
            contentAlignment = getAlignment(column.align)
        ) {
            Text(
                text = column.title,
                style = Typography.TitleSmall,
                color = colors.textSecondary
            )
        }
        // 表头下分割线
        Box(
            modifier = Modifier
                .width(colWidth)
                .height(0.5.dp)
                .background(colors.border)
        )
        // 数据行
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
                        color = colors.textSecondary
                    )
                }
            }
        } else {
            data.forEachIndexed { index, item ->
                val isSelected = selectionState.isSelected(item)
                val backgroundColor = when {
                    isSelected -> colors.primary.copy(alpha = 0.1f)
                    striped && index % 2 == 1 -> colors.surfaceVariant
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
                        .padding(horizontal = 16.dp),
                    contentAlignment = getAlignment(column.align)
                ) {
                    column.render(item, index)
                }
                // 行分割线
                if (index < data.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(colWidth)
                            .height(0.5.dp)
                            .background(colors.border)
                    )
                }
            }
        }
    }
}

/**
 * 获取对齐方式
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
                    color = colors.textPrimary
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
