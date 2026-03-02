package com.gearui.sample.examples.grid

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.grid.Grid
import com.gearui.components.grid.ResponsiveGrid
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Grid 组件示例
 *
 * 栅格布局系统，实现响应式布局
 */
@Composable
fun GridExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础用法 - 2列
        ExampleSection(
            title = "基础用法 - 2列",
            description = "2列等宽网格布局"
        ) {
            Grid(
                columns = 2,
                horizontalSpacing = 12.dp,
                verticalSpacing = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(4) { index ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(colors.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "项目 ${index + 1}",
                                style = Typography.BodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
        }

        // 3列布局
        ExampleSection(
            title = "3列布局",
            description = "3列等宽网格布局"
        ) {
            Grid(
                columns = 3,
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(6) { index ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(colors.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = Typography.TitleMedium,
                                color = colors.primary
                            )
                        }
                    }
                }
            }
        }

        // 4列布局
        ExampleSection(
            title = "4列布局",
            description = "4列等宽网格布局，适合图标展示"
        ) {
            Grid(
                columns = 4,
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                val icons = listOf("首页", "搜索", "消息", "我的", "设置", "帮助", "关于", "退出")
                icons.forEach { name ->
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.surfaceVariant)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(colors.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.first().toString(),
                                    style = Typography.BodySmall,
                                    color = colors.textAnti
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = name,
                                style = Typography.BodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // 不同间距
        ExampleSection(
            title = "自定义间距",
            description = "可以设置不同的水平和垂直间距"
        ) {
            Grid(
                columns = 3,
                horizontalSpacing = 16.dp,
                verticalSpacing = 24.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(6) { index ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(colors.success.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "间距 ${index + 1}",
                                style = Typography.BodySmall,
                                color = colors.success
                            )
                        }
                    }
                }
            }
        }

        // 响应式网格
        ExampleSection(
            title = "响应式网格",
            description = "根据容器宽度自动调整列数"
        ) {
            ResponsiveGrid(
                minColumnWidth = 100.dp,
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(9) { index ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(colors.warning.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "响应 ${index + 1}",
                                style = Typography.BodySmall,
                                color = colors.warning
                            )
                        }
                    }
                }
            }
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Grid 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. columns: 设置固定列数",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. horizontalSpacing/verticalSpacing: 自定义间距",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. ResponsiveGrid: 根据宽度自动调整列数",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. item { } 方法添加子项，自动排列",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
