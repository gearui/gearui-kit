package com.gearui.components.grid

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme

/**
 * Grid - fully Theme-driven grid layout
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: Color(0x...) or hardcoded colours
 *
 * Features:
 * - adaptive column count
 * - fixed column count
 * - custom gaps
 * - responsive layout
 */
@Composable
fun Grid(
    columns: Int,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable GridScope.() -> Unit
) {
    // ⭐ Framework Rule #1: these three are always the first lines
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
                // Fill the blanks
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * GridScope - Grid scope
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
 * ResponsiveGrid - responsive grid (column count follows the width)
 */
@Composable
fun ResponsiveGrid(
    minColumnWidth: Dp = 120.dp,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable GridScope.() -> Unit
) {
    val density = LocalDensity.current

    // The width is only known after the first layout. Stay invisible until
    // it is measured so the grid never flashes with a wrong column count.
    var containerWidthPx by remember { mutableStateOf(0) }
    val columns = GridMath.columnCount(
        containerWidthPx = containerWidthPx,
        minColumnWidthPx = with(density) { minColumnWidth.roundToPx() },
        spacingPx = with(density) { horizontalSpacing.roundToPx() }
    )

    Box(
        modifier = modifier
            .onSizeChanged { containerWidthPx = it.width }
            .alpha(if (containerWidthPx > 0) 1f else 0f)
    ) {
        Grid(
            columns = columns,
            horizontalSpacing = horizontalSpacing,
            verticalSpacing = verticalSpacing,
            content = content
        )
    }
}

/**
 * GridItem - grid item helper
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
