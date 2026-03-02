package com.gearui.sample.examples.anchor

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.anchor.Anchor
import com.gearui.components.anchor.AnchorAffix
import com.gearui.components.anchor.AnchorItem
import com.gearui.components.anchor.rememberAnchorState
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Anchor 组件示例
 *
 */
@Composable
fun AnchorExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础用法
        ExampleSection(
            title = "基础用法",
            description = "点击锚点项可以高亮选中"
        ) {
            val anchorState = rememberAnchorState(initialActiveKey = "intro")
            val items = listOf(
                AnchorItem(key = "intro", title = "介绍"),
                AnchorItem(key = "install", title = "安装"),
                AnchorItem(key = "usage", title = "使用"),
                AnchorItem(key = "api", title = "API")
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "当前选中: ${anchorState.activeKey ?: "无"}",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )

                Anchor(
                    items = items,
                    activeKey = anchorState.activeKey,
                    onItemClick = { item ->
                        anchorState.setActive(item.key)
                    }
                )
            }
        }

        // 带偏移量
        ExampleSection(
            title = "带偏移量",
            description = "可以设置顶部偏移量"
        ) {
            var activeKey by remember { mutableStateOf("section1") }
            val items = listOf(
                AnchorItem(key = "section1", title = "第一章"),
                AnchorItem(key = "section2", title = "第二章"),
                AnchorItem(key = "section3", title = "第三章")
            )

            Anchor(
                items = items,
                activeKey = activeKey,
                onItemClick = { activeKey = it.key },
                offsetTop = 8.dp
            )
        }

        // 固定侧边栏样式
        ExampleSection(
            title = "固定侧边栏样式",
            description = "使用 AnchorAffix 创建固定的锚点导航"
        ) {
            var activeKey by remember { mutableStateOf("overview") }
            val items = listOf(
                AnchorItem(key = "overview", title = "概述"),
                AnchorItem(key = "features", title = "功能特性"),
                AnchorItem(key = "examples", title = "示例代码"),
                AnchorItem(key = "changelog", title = "更新日志"),
                AnchorItem(key = "faq", title = "常见问题")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 锚点导航
                AnchorAffix(
                    items = items,
                    activeKey = activeKey,
                    onItemClick = { activeKey = it.key },
                    modifier = Modifier.width(150.dp)
                )

                // 模拟内容区域
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.surfaceVariant)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "内容区域",
                        style = Typography.TitleMedium,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "当前显示: ${items.find { it.key == activeKey }?.title ?: ""}",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "点击左侧锚点可以切换内容",
                        style = Typography.BodySmall,
                        color = colors.textPlaceholder
                    )
                }
            }
        }

        // 动态添加锚点
        ExampleSection(
            title = "动态锚点",
            description = "可以动态添加和移除锚点项"
        ) {
            var items by remember {
                mutableStateOf(
                    listOf(
                        AnchorItem(key = "item1", title = "项目 1"),
                        AnchorItem(key = "item2", title = "项目 2")
                    )
                )
            }
            var activeKey by remember { mutableStateOf("item1") }
            var itemCount by remember { mutableStateOf(2) }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        text = "添加",
                        onClick = {
                            itemCount++
                            items = items + AnchorItem(
                                key = "item$itemCount",
                                title = "项目 $itemCount"
                            )
                        },
                        size = ButtonSize.SMALL
                    )

                    Button(
                        text = "移除",
                        onClick = {
                            if (items.size > 1) {
                                val lastItem = items.last()
                                items = items.dropLast(1)
                                if (activeKey == lastItem.key) {
                                    activeKey = items.last().key
                                }
                            }
                        },
                        size = ButtonSize.SMALL
                    )
                }

                Anchor(
                    items = items,
                    activeKey = activeKey,
                    onItemClick = { activeKey = it.key }
                )
            }
        }

        // 清除选中
        ExampleSection(
            title = "清除选中",
            description = "可以清除当前选中状态"
        ) {
            val anchorState = rememberAnchorState(initialActiveKey = "doc1")
            val items = listOf(
                AnchorItem(key = "doc1", title = "文档 1"),
                AnchorItem(key = "doc2", title = "文档 2"),
                AnchorItem(key = "doc3", title = "文档 3")
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        text = "清除选中",
                        onClick = { anchorState.clearActive() },
                        size = ButtonSize.SMALL
                    )

                    Button(
                        text = "选中第一个",
                        onClick = { anchorState.setActive("doc1") },
                        size = ButtonSize.SMALL
                    )
                }

                Text(
                    text = "当前选中: ${anchorState.activeKey ?: "无"}",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )

                Anchor(
                    items = items,
                    activeKey = anchorState.activeKey,
                    onItemClick = { anchorState.setActive(it.key) }
                )
            }
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Anchor 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. Anchor: 基础锚点导航组件",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. AnchorAffix: 固定定位的锚点导航",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. rememberAnchorState: 管理选中状态",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. 支持动态添加和移除锚点项",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. 支持顶部偏移量设置",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
