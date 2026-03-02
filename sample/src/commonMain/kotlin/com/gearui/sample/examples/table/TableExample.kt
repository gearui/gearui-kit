package com.gearui.sample.examples.table

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.table.Table
import com.gearui.components.table.TableColumn
import com.gearui.components.table.TableAlign
import com.gearui.components.table.TableColFixed
import com.gearui.components.table.SimpleTable
import com.gearui.components.table.rememberTableSelectionState
import com.gearui.components.tag.Tag
import com.gearui.components.tag.TagTheme
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Table 组件示例
 *
 *
 * 表格常用于展示同类结构下的多种数据，易于组织、对比和分析等，
 * 并可对数据进行搜索、筛选、排序等操作。一般包括表头、数据行和表尾三部分。
 */
@Composable
fun TableExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 生成基础示例数据
    fun generateData(count: Int, longContentIndex: Int = -1): List<Map<String, String>> {
        return List(count) { index ->
            if (index == longContentIndex) {
                mapOf(
                    "title1" to "内容内容内容内容",
                    "title2" to "内容",
                    "title3" to "内容",
                    "title4" to "内容"
                )
            } else {
                mapOf(
                    "title1" to "内容",
                    "title2" to "内容",
                    "title3" to "内容",
                    "title4" to "内容"
                )
            }
        }
    }

    // 生成横向平铺数据
    fun generateHorizontalData(): List<Map<String, String>> {
        val data = mutableListOf<Map<String, String>>()
        data.add(mapOf(
            "title1" to "横向平铺内容不省略",
            "title2" to "横向平铺内容不省略",
            "title3" to "横向平铺内容不省略"
        ))
        repeat(10) {
            data.add(mapOf(
                "title1" to "内容",
                "title2" to "内容",
                "title3" to "内容"
            ))
        }
        return data
    }

    // 选择状态
    val selectionState = rememberTableSelectionState<Map<String, String>>()

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ==================== 组件类型 ====================

        // 基础表格
        ExampleSection(
            title = "基础表格",
            description = "最基本的表格展示"
        ) {
            Table(
                data = generateData(10, 9),
                columns = listOf(
                    TableColumn(
                        key = "title1",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title1"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary,
                                maxLines = 1
                            )
                        }
                    ),
                    TableColumn(
                        key = "title2",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title2"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title3",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title3"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title4",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title4"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    )
                ),
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )
        }

        // 带操作按钮表格
        ExampleSection(
            title = "带操作按钮表格",
            description = "表格列中包含操作按钮"
        ) {
            Table(
                data = generateData(10, 9),
                columns = listOf(
                    TableColumn(
                        key = "title1",
                        title = "标题",
                        width = 80.dp,
                        render = { item, _ ->
                            Text(
                                text = item["title1"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary,
                                maxLines = 1
                            )
                        }
                    ),
                    TableColumn(
                        key = "title2",
                        title = "标题",
                        width = 80.dp,
                        render = { item, _ ->
                            Text(
                                text = item["title2"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title3",
                        title = "标题",
                        width = 80.dp,
                        render = { item, _ ->
                            Text(
                                text = item["title3"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "operations",
                        title = "操作",
                        width = 120.dp,
                        render = { _, index ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "修改",
                                    style = Typography.BodyMedium,
                                    color = colors.primary,
                                    modifier = Modifier.clickable {
                                        Toast.show("修改第 ${index + 1} 行")
                                    }
                                )
                                Text(
                                    text = "通过",
                                    style = Typography.BodyMedium,
                                    color = colors.primary,
                                    modifier = Modifier.clickable {
                                        Toast.show("通过第 ${index + 1} 行")
                                    }
                                )
                            }
                        }
                    )
                ),
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )
        }

        // 带图标操作表格
        ExampleSection(
            title = "带图标操作表格",
            description = "使用图标作为操作按钮"
        ) {
            Table(
                data = generateData(10, 9),
                columns = listOf(
                    TableColumn(
                        key = "title1",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title1"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary,
                                maxLines = 1
                            )
                        }
                    ),
                    TableColumn(
                        key = "title2",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title2"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title3",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title3"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "operations",
                        title = "操作",
                        render = { _, index ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "📤",
                                    style = Typography.BodyLarge,
                                    modifier = Modifier.clickable {
                                        Toast.show("上传第 ${index + 1} 行")
                                    }
                                )
                                Text(
                                    text = "🗑️",
                                    style = Typography.BodyLarge,
                                    modifier = Modifier.clickable {
                                        Toast.show("删除第 ${index + 1} 行")
                                    }
                                )
                            }
                        }
                    )
                ),
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )
        }

        // 可选择表格
        ExampleSection(
            title = "可选择表格",
            description = "支持多选行功能"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (selectionState.selectedItems.isNotEmpty()) {
                    Text(
                        text = "已选择 ${selectionState.selectedItems.size} 项",
                        style = Typography.BodySmall,
                        color = colors.primary
                    )
                }

                Table(
                    data = generateData(10),
                    columns = listOf(
                        TableColumn(
                            key = "title1",
                            title = "标题",
                            render = { item, _ ->
                                Text(
                                    text = item["title1"] ?: "",
                                    style = Typography.BodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                        ),
                        TableColumn(
                            key = "title2",
                            title = "标题",
                            render = { item, _ ->
                                Text(
                                    text = item["title2"] ?: "",
                                    style = Typography.BodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                        ),
                        TableColumn(
                            key = "title3",
                            title = "标题",
                            render = { item, _ ->
                                Text(
                                    text = item["title3"] ?: "",
                                    style = Typography.BodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                        ),
                        TableColumn(
                            key = "title4",
                            title = "标题",
                            render = { item, _ ->
                                Text(
                                    text = item["title4"] ?: "",
                                    style = Typography.BodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                        )
                    ),
                    selectable = true,
                    selectionState = selectionState,
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                )
            }
        }

        // 固定列+滚动表格
        ExampleSection(
            title = "固定列+滚动表格",
            description = "左侧列固定，中间列可横向滚动，右侧操作列固定"
        ) {
            data class ProductItem(
                val id: String,
                val name: String,
                val category: String,
                val price: String,
                val stock: String,
                val sales: String
            )

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
                        width = 70.dp,
                        fixed = TableColFixed.LEFT,
                        render = { item, _ ->
                            Text(
                                text = item.id,
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "name",
                        title = "商品名称",
                        width = 130.dp,
                        render = { item, _ ->
                            Text(
                                text = item.name,
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "category",
                        title = "分类",
                        width = 80.dp,
                        render = { item, _ ->
                            Text(
                                text = item.category,
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "price",
                        title = "价格",
                        width = 100.dp,
                        align = TableAlign.RIGHT,
                        render = { item, _ ->
                            Text(
                                text = item.price,
                                style = Typography.BodyMedium,
                                color = colors.danger
                            )
                        }
                    ),
                    TableColumn(
                        key = "stock",
                        title = "库存",
                        width = 80.dp,
                        align = TableAlign.CENTER,
                        render = { item, _ ->
                            Text(
                                text = item.stock,
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "sales",
                        title = "销量",
                        width = 80.dp,
                        align = TableAlign.CENTER,
                        render = { item, _ ->
                            Text(
                                text = item.sales,
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "operations",
                        title = "操作",
                        width = 100.dp,
                        fixed = TableColFixed.RIGHT,
                        render = { item, _ ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "编辑",
                                    style = Typography.BodyMedium,
                                    color = colors.primary,
                                    modifier = Modifier.clickable {
                                        Toast.show("编辑 ${item.name}")
                                    }
                                )
                                Text(
                                    text = "删除",
                                    style = Typography.BodyMedium,
                                    color = colors.danger,
                                    modifier = Modifier.clickable {
                                        Toast.show("删除 ${item.name}")
                                    }
                                )
                            }
                        }
                    )
                ),
                modifier = Modifier.fillMaxWidth().height(350.dp)
            )
        }

        // 横向平铺可滚动表格
        ExampleSection(
            title = "横向平铺可滚动表格",
            description = "内容较宽时可横向滚动"
        ) {
            Table(
                data = generateHorizontalData(),
                columns = listOf(
                    TableColumn(
                        key = "title1",
                        title = "标题",
                        width = 160.dp,
                        render = { item, _ ->
                            Text(
                                text = item["title1"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title2",
                        title = "标题",
                        width = 160.dp,
                        render = { item, _ ->
                            Text(
                                text = item["title2"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title3",
                        title = "标题",
                        width = 160.dp,
                        render = { item, _ ->
                            Text(
                                text = item["title3"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    )
                ),
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )
        }

        // ==================== 组件样式 ====================

        // 带斑马纹表格样式
        ExampleSection(
            title = "带斑马纹表格样式",
            description = "隔行变色，提高可读性"
        ) {
            Table(
                data = generateData(10, 9),
                columns = listOf(
                    TableColumn(
                        key = "title1",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title1"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary,
                                maxLines = 1
                            )
                        }
                    ),
                    TableColumn(
                        key = "title2",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title2"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title3",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title3"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title4",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title4"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    )
                ),
                striped = true,
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )
        }

        // 带边框表格样式
        ExampleSection(
            title = "带边框表格样式",
            description = "单元格带边框线"
        ) {
            Table(
                data = generateData(10, 9),
                columns = listOf(
                    TableColumn(
                        key = "title1",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title1"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary,
                                maxLines = 1
                            )
                        }
                    ),
                    TableColumn(
                        key = "title2",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title2"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title3",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title3"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title4",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title4"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    )
                ),
                bordered = true,
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )
        }

        // 内容居中表格
        ExampleSection(
            title = "内容居中表格",
            description = "所有列内容居中对齐"
        ) {
            Table(
                data = generateData(10),
                columns = listOf(
                    TableColumn(
                        key = "title1",
                        title = "标题",
                        align = TableAlign.CENTER,
                        render = { item, _ ->
                            Text(
                                text = item["title1"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title2",
                        title = "标题",
                        align = TableAlign.CENTER,
                        render = { item, _ ->
                            Text(
                                text = item["title2"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title3",
                        title = "标题",
                        align = TableAlign.CENTER,
                        render = { item, _ ->
                            Text(
                                text = item["title3"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title4",
                        title = "标题",
                        align = TableAlign.CENTER,
                        render = { item, _ ->
                            Text(
                                text = item["title4"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    )
                ),
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )
        }

        // 空数据表格
        ExampleSection(
            title = "空数据表格",
            description = "无数据时显示空状态提示"
        ) {
            Table(
                data = emptyList<Map<String, String>>(),
                columns = listOf(
                    TableColumn(
                        key = "title1",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title1"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title2",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title2"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title3",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title3"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "title4",
                        title = "标题",
                        render = { item, _ ->
                            Text(
                                text = item["title4"] ?: "",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    )
                ),
                emptyText = "暂无数据",
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
        }

        // 简单表格
        ExampleSection(
            title = "简单表格",
            description = "使用字符串数组快速创建表格"
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
                modifier = Modifier.fillMaxWidth().height(250.dp)
            )
        }

        // 带状态标签表格
        ExampleSection(
            title = "带状态标签表格",
            description = "使用标签展示状态信息"
        ) {
            data class OrderItem(
                val id: String,
                val product: String,
                val amount: String,
                val status: String
            )

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
                        render = { item, _ ->
                            Text(
                                text = item.id,
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "product",
                        title = "商品",
                        render = { item, _ ->
                            Text(
                                text = item.product,
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    ),
                    TableColumn(
                        key = "amount",
                        title = "金额",
                        align = TableAlign.RIGHT,
                        render = { item, _ ->
                            Text(
                                text = item.amount,
                                style = Typography.BodyMedium,
                                color = colors.danger
                            )
                        }
                    ),
                    TableColumn(
                        key = "status",
                        title = "状态",
                        align = TableAlign.CENTER,
                        render = { item, _ ->
                            val theme = when (item.status) {
                                "已完成" -> TagTheme.SUCCESS
                                "已发货" -> TagTheme.PRIMARY
                                "处理中" -> TagTheme.WARNING
                                "已取消" -> TagTheme.DEFAULT
                                "待付款" -> TagTheme.DANGER
                                else -> TagTheme.DEFAULT
                            }
                            Tag(text = item.status, theme = theme)
                        }
                    )
                ),
                modifier = Modifier.fillMaxWidth().height(280.dp)
            )
        }
    }
}
