package com.gearui.sample.examples.table

import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonShape
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.icon.Icons
import com.gearui.components.table.SimpleTable
import com.gearui.components.table.Table
import com.gearui.components.table.TableAlign
import com.gearui.components.table.TableColFixed
import com.gearui.components.table.TableColumn
import com.gearui.components.table.rememberTableSelectionState
import com.gearui.components.tag.Tag
import com.gearui.components.tag.TagTheme
import com.gearui.components.toast.Toast
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

// Demo dimensions: the viewport heights and column widths are what the examples show off.
private val TableHeight = 300.dp
private val EmptyTableHeight = 150.dp
private val SimpleTableHeight = 250.dp
private val NarrowColumnWidth = 72.dp
private val MediumColumnWidth = 96.dp
private val WideColumnWidth = 128.dp
private val ExtraWideColumnWidth = 160.dp
private val StatusColumnWidth = 92.dp

private typealias Row4 = Map<String, String>

private val defaultKeys = listOf("title1", "title2", "title3", "title4")

/** Rows of placeholder content; [longContentIndex] gets a longer first cell to show truncation. */
private fun generateData(count: Int, longContentIndex: Int = -1): List<Row4> =
    List(count) { index ->
        mapOf(
            "title1" to if (index == longContentIndex) "内容内容内容内容" else "内容",
            "title2" to "内容",
            "title3" to "内容",
            "title4" to "内容"
        )
    }

/** Wide rows: the first one is long enough to push the table past the screen width. */
private fun generateWideData(): List<Row4> =
    listOf(
        mapOf(
            "title1" to "横向平铺内容不省略",
            "title2" to "横向平铺内容不省略",
            "title3" to "横向平铺内容不省略"
        )
    ) + List(10) { mapOf("title1" to "内容", "title2" to "内容", "title3" to "内容") }

/** A plain text column reading [key] from a map row. */
private fun textColumn(
    key: String,
    title: String = "标题",
    width: Dp? = null,
    align: TableAlign = TableAlign.LEFT
): TableColumn<Row4> = TableColumn(
    key = key,
    title = title,
    width = width,
    align = align,
    render = { item, _ -> CellText(item[key] ?: "") }
)

@Composable
private fun CellText(text: String, danger: Boolean = false) {
    Text(
        text = text,
        style = Theme.typography.bodyMedium,
        color = if (danger) Theme.colors.destructive else Theme.colors.foreground,
        maxLines = 1
    )
}

private data class ProductItem(
    val id: String,
    val name: String,
    val category: String,
    val price: String,
    val stock: String,
    val sales: String
)

private data class OrderItem(
    val id: String,
    val product: String,
    val amount: String,
    val status: String
)

/**
 * Table component examples
 *
 * Tables present several pieces of data sharing one structure, making them easy to organise,
 * compare and analyse. A table usually has a header, data rows and a footer.
 */
@Composable
fun TableExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val selectionState = rememberTableSelectionState<Row4>()

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "基础表格",
            description = "表头加数据行，超出高度时纵向滚动"
        ) {
            Table(
                data = generateData(10, 9),
                columns = defaultKeys.map { textColumn(it) },
                modifier = Modifier.fillMaxWidth().height(TableHeight)
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "自适应高度",
            description = "不设高度时按行数完整展开，随页面一起滚动"
        ) {
            Table(
                data = generateData(3),
                columns = defaultKeys.map { textColumn(it) }
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "操作列",
            description = "行内操作用文字按钮或图标按钮"
        ) {
            Table(
                data = generateData(10, 9),
                columns = listOf(
                    textColumn("title1", width = NarrowColumnWidth),
                    textColumn("title2", width = NarrowColumnWidth),
                    TableColumn(
                        key = "operations",
                        title = "操作",
                        render = { _, index ->
                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                Button(
                                    text = "修改",
                                    type = ButtonType.TEXT,
                                    size = ButtonSize.EXTRA_SMALL,
                                    onClick = { Toast.show("修改第 ${index + 1} 行") }
                                )
                                Button(
                                    icon = Icons.upload_simple,
                                    type = ButtonType.TEXT,
                                    theme = ButtonTheme.DEFAULT,
                                    shape = ButtonShape.SQUARE,
                                    size = ButtonSize.EXTRA_SMALL,
                                    onClick = { Toast.show("上传第 ${index + 1} 行") },
                                    contentDescription = "上传第 ${index + 1} 行"
                                )
                                Button(
                                    icon = Icons.trash,
                                    type = ButtonType.TEXT,
                                    theme = ButtonTheme.DANGER,
                                    shape = ButtonShape.SQUARE,
                                    size = ButtonSize.EXTRA_SMALL,
                                    onClick = { Toast.show("删除第 ${index + 1} 行") },
                                    contentDescription = "删除第 ${index + 1} 行"
                                )
                            }
                        }
                    )
                ),
                modifier = Modifier.fillMaxWidth().height(TableHeight)
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "可选择表格",
            description = "selectable 开启行多选，选中结果在 selectionState 中"
        ) {
            if (selectionState.selectedItems.isNotEmpty()) {
                Text(
                    text = "已选择 ${selectionState.selectedItems.size} 项",
                    style = Theme.typography.bodySmall,
                    color = colors.primarySoftForeground
                )
            }
            Table(
                data = generateData(10),
                columns = defaultKeys.map { textColumn(it) },
                selectable = true,
                selectionState = selectionState,
                modifier = Modifier.fillMaxWidth().height(TableHeight)
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "固定列",
            description = "左侧编号列与右侧操作列固定，中间列横向滚动"
        ) {
            val products = listOf(
                ProductItem("P001", "iPhone 15 Pro", "手机", "¥8,999", "156", "2,341"),
                ProductItem("P002", "MacBook Air M3", "电脑", "¥9,499", "89", "1,234"),
                ProductItem("P003", "iPad Pro 12.9", "平板", "¥8,999", "67", "876"),
                ProductItem("P004", "AirPods Pro 2", "配件", "¥1,899", "234", "5,678"),
                ProductItem("P005", "Apple Watch S9", "手表", "¥3,299", "123", "2,456"),
                ProductItem("P006", "HomePod mini", "音箱", "¥749", "45", "1,234"),
                ProductItem("P007", "Magic Keyboard", "配件", "¥2,349", "78", "567"),
                ProductItem("P008", "Studio Display", "显示器", "¥11,499", "23", "234")
            )

            Table(
                data = products,
                columns = listOf(
                    TableColumn(
                        key = "id",
                        title = "编号",
                        width = NarrowColumnWidth,
                        fixed = TableColFixed.LEFT,
                        render = { item, _ -> CellText(item.id) }
                    ),
                    TableColumn(
                        key = "name",
                        title = "商品名称",
                        width = WideColumnWidth,
                        render = { item, _ -> CellText(item.name) }
                    ),
                    TableColumn(
                        key = "category",
                        title = "分类",
                        width = NarrowColumnWidth,
                        render = { item, _ -> CellText(item.category) }
                    ),
                    TableColumn(
                        key = "price",
                        title = "价格",
                        width = MediumColumnWidth,
                        align = TableAlign.RIGHT,
                        render = { item, _ -> CellText(item.price, danger = true) }
                    ),
                    TableColumn(
                        key = "stock",
                        title = "库存",
                        width = NarrowColumnWidth,
                        align = TableAlign.CENTER,
                        render = { item, _ -> CellText(item.stock) }
                    ),
                    TableColumn(
                        key = "sales",
                        title = "销量",
                        width = NarrowColumnWidth,
                        align = TableAlign.CENTER,
                        render = { item, _ -> CellText(item.sales) }
                    ),
                    TableColumn(
                        key = "operations",
                        title = "操作",
                        width = MediumColumnWidth,
                        fixed = TableColFixed.RIGHT,
                        render = { item, _ ->
                            Button(
                                text = "编辑",
                                type = ButtonType.TEXT,
                                size = ButtonSize.EXTRA_SMALL,
                                onClick = { Toast.show("编辑 ${item.name}") }
                            )
                        }
                    )
                ),
                modifier = Modifier.fillMaxWidth().height(TableHeight)
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "横向滚动",
            description = "列宽之和超过屏幕时，整表横向滚动"
        ) {
            Table(
                data = generateWideData(),
                columns = listOf("title1", "title2", "title3").map {
                    textColumn(it, width = ExtraWideColumnWidth)
                },
                modifier = Modifier.fillMaxWidth().height(TableHeight)
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "斑马纹与边框",
            description = "striped 隔行变色，bordered 加外框"
        ) {
            Table(
                data = generateData(10, 9),
                columns = defaultKeys.map { textColumn(it, align = TableAlign.CENTER) },
                striped = true,
                bordered = true,
                modifier = Modifier.fillMaxWidth().height(TableHeight)
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "状态标签",
            description = "列内渲染 Tag，金额右对齐、状态居中"
        ) {
            val orders = listOf(
                OrderItem("20240101001", "iPhone 15", "¥6,999", "已发货"),
                OrderItem("20240101002", "MacBook Pro", "¥14,999", "待付款"),
                OrderItem("20240101003", "iPad Air", "¥4,799", "已完成"),
                OrderItem("20240101004", "AirPods Pro", "¥1,899", "已取消"),
                OrderItem("20240101005", "Apple Watch", "¥3,299", "处理中")
            )

            Table(
                data = orders,
                columns = listOf(
                    TableColumn(
                        key = "id",
                        title = "订单号",
                        render = { item, _ -> CellText(item.id) }
                    ),
                    TableColumn(
                        key = "product",
                        title = "商品",
                        render = { item, _ -> CellText(item.product) }
                    ),
                    TableColumn(
                        key = "amount",
                        title = "金额",
                        align = TableAlign.RIGHT,
                        render = { item, _ -> CellText(item.amount, danger = true) }
                    ),
                    TableColumn(
                        key = "status",
                        title = "状态",
                        // A three-character tag and the cell's padding; shared equally, the
                        // status was cut to "已…".
                        width = StatusColumnWidth,
                        align = TableAlign.CENTER,
                        render = { item, _ ->
                            val theme = when (item.status) {
                                "已完成" -> TagTheme.SUCCESS
                                "已发货" -> TagTheme.PRIMARY
                                "处理中" -> TagTheme.WARNING
                                "待付款" -> TagTheme.DANGER
                                else -> TagTheme.DEFAULT
                            }
                            Tag(text = item.status, theme = theme)
                        }
                    )
                ),
                modifier = Modifier.fillMaxWidth().height(TableHeight)
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "空数据",
            description = "无数据时显示 emptyText"
        ) {
            Table(
                data = emptyList<Row4>(),
                columns = defaultKeys.map { textColumn(it) },
                emptyText = "暂无数据",
                modifier = Modifier.fillMaxWidth().height(EmptyTableHeight)
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "SimpleTable",
            description = "用字符串二维数组快速生成表格"
        ) {
            SimpleTable(
                headers = listOf("编号", "名称", "数量", "价格"),
                rows = listOf(
                    listOf("001", "苹果", "50", "¥5.00"),
                    listOf("002", "香蕉", "30", "¥3.00"),
                    listOf("003", "橙子", "45", "¥4.50"),
                    listOf("004", "葡萄", "25", "¥8.00"),
                    listOf("005", "西瓜", "15", "¥15.00")
                ),
                striped = true,
                modifier = Modifier.fillMaxWidth().height(SimpleTableHeight)
            )
        }
    }
}
