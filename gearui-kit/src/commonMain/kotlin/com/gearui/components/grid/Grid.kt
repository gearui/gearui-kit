package com.gearui.components.grid

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme

/**
 * Grid - 100% Theme 驱动的网格布局
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：Color(0x...) / 硬编码颜色
 *
 * 特性：
 * - 自适应列数
 * - 固定列数
 * - 自定义间距
 * - 响应式布局
 */
@Composable
fun Grid(
    columns: Int,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable GridScope.() -> Unit
) {
    // ⭐ Framework Rule #1: 第一行永远是这三个
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    val scope = GridScopeImpl()
    scope.content()
    val items = scope.items

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        items.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        item()
                    }
                }
                // 填充空白
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * GridScope - Grid 作用域
 */
interface GridScope {
    fun item(content: @Composable () -> Unit)
}

private class GridScopeImpl : GridScope {
    val items = mutableListOf<@Composable () -> Unit>()

    override fun item(content: @Composable () -> Unit) {
        items.add(content)
    }
}

/**
 * ResponsiveGrid - 响应式网格（根据宽度自动调整列数）
 */
@Composable
fun ResponsiveGrid(
    minColumnWidth: Dp = 120.dp,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable GridScope.() -> Unit
) {
    // 简化实现：固定 3 列
    // TODO: 需要测量容器宽度来动态计算列数
    Grid(
        columns = 3,
        modifier = modifier,
        horizontalSpacing = horizontalSpacing,
        verticalSpacing = verticalSpacing,
        content = content
    )
}

/**
 * GridItem - 网格项辅助函数
 */
@Composable
fun GridItem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
    }
}
