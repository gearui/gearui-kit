package com.gearui.sample.examples.breadcrumb

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Breadcrumb 组件示例
 *
 * 面包屑导航示例展示
 */
@Composable
fun BreadcrumbExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 点击结果
    var clickedItem by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础用法
        ExampleSection(
            title = "基础用法",
            description = "使用斜杠分隔的面包屑导航"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Breadcrumb(
                    items = listOf(
                        BreadcrumbItem("首页"),
                        BreadcrumbItem("组件"),
                        BreadcrumbItem("导航")
                    ),
                    onItemClick = { clickedItem = it.label }
                )

                if (clickedItem.isNotEmpty()) {
                    Text(
                        text = "点击了: $clickedItem",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // 自定义分隔符
        ExampleSection(
            title = "自定义分隔符",
            description = "可以使用不同的分隔符"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 箭头分隔符
                Breadcrumb(
                    items = listOf(
                        BreadcrumbItem("首页"),
                        BreadcrumbItem("产品"),
                        BreadcrumbItem("详情")
                    ),
                    separator = ">",
                    onItemClick = { clickedItem = it.label }
                )

                // 点号分隔符
                Breadcrumb(
                    items = listOf(
                        BreadcrumbItem("首页"),
                        BreadcrumbItem("设置"),
                        BreadcrumbItem("账户")
                    ),
                    separator = ".",
                    onItemClick = { clickedItem = it.label }
                )

                // 横杠分隔符
                Breadcrumb(
                    items = listOf(
                        BreadcrumbItem("首页"),
                        BreadcrumbItem("帮助"),
                        BreadcrumbItem("常见问题")
                    ),
                    separator = "-",
                    onItemClick = { clickedItem = it.label }
                )
            }
        }

        // 带图标
        ExampleSection(
            title = "带图标",
            description = "面包屑项可以带有图标"
        ) {
            Breadcrumb(
                items = listOf(
                    BreadcrumbItem("首页", icon = "H"),
                    BreadcrumbItem("用户", icon = "U"),
                    BreadcrumbItem("设置", icon = "S")
                ),
                onItemClick = { clickedItem = it.label }
            )
        }

        // 多级路径
        ExampleSection(
            title = "多级路径",
            description = "支持多级嵌套路径"
        ) {
            Breadcrumb(
                items = listOf(
                    BreadcrumbItem("首页"),
                    BreadcrumbItem("商品分类"),
                    BreadcrumbItem("电子产品"),
                    BreadcrumbItem("手机"),
                    BreadcrumbItem("智能手机")
                ),
                onItemClick = { clickedItem = it.label }
            )
        }

        // 折叠模式
        ExampleSection(
            title = "折叠模式",
            description = "路径过长时可以折叠中间项"
        ) {
            BreadcrumbCollapsed(
                items = listOf(
                    BreadcrumbItem("首页"),
                    BreadcrumbItem("一级目录"),
                    BreadcrumbItem("二级目录"),
                    BreadcrumbItem("三级目录"),
                    BreadcrumbItem("四级目录"),
                    BreadcrumbItem("当前页面")
                ),
                maxVisible = 3,
                onItemClick = { clickedItem = it.label }
            )
        }

        // 禁用状态
        ExampleSection(
            title = "禁用状态",
            description = "某些面包屑项可以禁用"
        ) {
            Breadcrumb(
                items = listOf(
                    BreadcrumbItem("首页"),
                    BreadcrumbItem("已归档", disabled = true),
                    BreadcrumbItem("详情")
                ),
                onItemClick = { clickedItem = it.label }
            )
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Breadcrumb 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. 显示当前页面在系统层级结构中的位置",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. 支持自定义分隔符",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. 支持图标和文字组合",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. 最后一项表示当前页面，不可点击",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. 支持折叠模式处理长路径",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}

/**
 * 面包屑项数据
 */
private data class BreadcrumbItem(
    val label: String,
    val icon: String? = null,
    val disabled: Boolean = false
)

/**
 * 基础面包屑导航
 */
@Composable
private fun Breadcrumb(
    items: List<BreadcrumbItem>,
    separator: String = "/",
    onItemClick: (BreadcrumbItem) -> Unit
) {
    val colors = Theme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val isLast = index == items.lastIndex
            val isClickable = !isLast && !item.disabled

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .then(
                        if (isClickable) {
                            Modifier.clickable { onItemClick(item) }
                        } else {
                            Modifier
                        }
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 图标
                if (item.icon != null) {
                    Text(
                        text = item.icon,
                        style = Typography.BodySmall,
                        color = when {
                            item.disabled -> colors.textDisabled
                            isLast -> colors.textPrimary
                            else -> colors.primary
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // 文字
                Text(
                    text = item.label,
                    style = Typography.BodyMedium,
                    color = when {
                        item.disabled -> colors.textDisabled
                        isLast -> colors.textPrimary
                        else -> colors.primary
                    }
                )
            }

            // 分隔符
            if (!isLast) {
                Text(
                    text = " $separator ",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}

/**
 * 折叠面包屑导航
 */
@Composable
private fun BreadcrumbCollapsed(
    items: List<BreadcrumbItem>,
    maxVisible: Int = 3,
    onItemClick: (BreadcrumbItem) -> Unit
) {
    val colors = Theme.colors
    var expanded by remember { mutableStateOf(false) }

    if (items.size <= maxVisible || expanded) {
        Breadcrumb(
            items = items,
            onItemClick = onItemClick
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 第一项
            val firstItem = items.first()
            Text(
                text = firstItem.label,
                style = Typography.BodyMedium,
                color = colors.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onItemClick(firstItem) }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Text(
                text = " / ",
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )

            // 折叠的省略号
            Text(
                text = "...",
                style = Typography.BodyMedium,
                color = colors.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Text(
                text = " / ",
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )

            // 最后一项
            val lastItem = items.last()
            Text(
                text = lastItem.label,
                style = Typography.BodyMedium,
                color = colors.textPrimary,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}
