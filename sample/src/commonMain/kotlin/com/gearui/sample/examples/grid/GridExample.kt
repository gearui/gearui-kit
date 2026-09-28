package com.gearui.sample.examples.grid

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.grid.Grid
import com.gearui.components.grid.ResponsiveGrid
import com.gearui.components.icon.Icons
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

// Demo content dimensions: the placeholder blocks the grid lays out.
private val CellHeight = 56.dp
private val ResponsiveMinColumnWidth = 100.dp

private data class GridEntry(val icon: String, val label: String)

private val iconEntries = listOf(
    GridEntry(Icons.house, "首页"),
    GridEntry(Icons.magnifying_glass, "搜索"),
    GridEntry(Icons.chat_circle, "消息"),
    GridEntry(Icons.user, "我的"),
    GridEntry(Icons.gear, "设置"),
    GridEntry(Icons.question, "帮助"),
    GridEntry(Icons.info, "关于"),
    GridEntry(Icons.sign_out, "退出"),
)

/**
 * Grid component examples: fixed-column and responsive layouts.
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
        ExampleSection(
            title = "基础用法",
            description = "columns 设置固定列数，子项等宽排列"
        ) {
            Grid(
                columns = 2,
                horizontalSpacing = Spacing.md,
                verticalSpacing = Spacing.md,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(4) { index ->
                    item { PlaceholderCell("项目 ${index + 1}") }
                }
            }
        }

        ExampleSection(
            title = "三列布局",
            description = "columns = 3"
        ) {
            Grid(
                columns = 3,
                horizontalSpacing = Spacing.sm,
                verticalSpacing = Spacing.sm,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(6) { index ->
                    item { PlaceholderCell("${index + 1}") }
                }
            }
        }

        ExampleSection(
            title = "图标宫格",
            description = "四列布局，常用于功能入口"
        ) {
            Grid(
                columns = 4,
                horizontalSpacing = Spacing.sm,
                verticalSpacing = Spacing.lg,
                modifier = Modifier.fillMaxWidth()
            ) {
                iconEntries.forEach { entry ->
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Icon(
                                name = entry.icon,
                                size = IconSizes.Default.xl,
                                tint = colors.primary
                            )
                            Text(
                                text = entry.label,
                                style = Theme.typography.bodySmall,
                                color = colors.foreground
                            )
                        }
                    }
                }
            }
        }

        ExampleSection(
            title = "自定义间距",
            description = "horizontalSpacing 与 verticalSpacing 分别设置"
        ) {
            Grid(
                columns = 3,
                horizontalSpacing = Spacing.lg,
                verticalSpacing = Spacing.xl,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(6) { index ->
                    item { PlaceholderCell("间距 ${index + 1}") }
                }
            }
        }

        ExampleSection(
            title = "响应式网格",
            description = "ResponsiveGrid 按 minColumnWidth 与容器宽度自动决定列数"
        ) {
            ResponsiveGrid(
                minColumnWidth = ResponsiveMinColumnWidth,
                horizontalSpacing = Spacing.sm,
                verticalSpacing = Spacing.sm,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(9) { index ->
                    item { PlaceholderCell("响应 ${index + 1}") }
                }
            }
        }
    }
}

/** A labelled placeholder block standing in for real grid content. */
@Composable
private fun PlaceholderCell(label: String, height: Dp = CellHeight) {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(Theme.shapes.md)
            .background(colors.muted),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = Theme.typography.bodySmall,
            color = colors.mutedForeground
        )
    }
}
