package com.gearui.sample.examples.icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.icon.Icons
import com.gearui.components.searchbar.SearchBar
import com.gearui.components.switch.Switch
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/** Side of one tile in the icon gallery grid. */
private val IconCellSize = 44.dp

/**
 * Icon component examples (the icon gallery)
 */
@Composable
fun IconExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    var keyword by remember { mutableStateOf("") }
    var showBorder by remember { mutableStateOf(false) }

    val filteredIcons = remember(keyword) {
        val q = keyword.trim()
        if (q.isEmpty()) Icons.all else Icons.all.filter { it.contains(q, ignoreCase = true) }
    }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "尺寸与颜色",
            description = "IconSizes.Default 的 xs–xl 五档，tint 取主题色"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                val sizes = IconSizes.Default
                listOf(sizes.xs, sizes.sm, sizes.md, sizes.lg, sizes.xl).forEach { size ->
                    Icon(name = Icons.house, size = size, tint = colors.foreground)
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                listOf(colors.primary, colors.success, colors.warning, colors.destructive, colors.mutedForeground)
                    .forEach { tint -> Icon(name = Icons.heart, size = IconSizes.Default.xl, tint = tint) }
            }
        }

        ExampleSection(
            title = "应用自带图标",
            description = "应用把 PNG 放进自己的 assets/icons/，即可用同一个 API 按名称渲染"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Supplied by sample/src/commonMain/assets/icons/app_folder.png,
                // not by the library. Android merges library and app assets into
                // one tree; iOS looks in the bundle and then falls through to the
                // host adapter. So this is the extension point, and it needs no
                // API of its own.
                Icon(name = "app_folder", size = IconSizes.Default.xl, tint = colors.foreground)
                Text(
                    text = "app_folder（来自 sample 自己的 assets）",
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }

        ExampleSection(
            title = "内置图标库",
            description = "按名称搜索并浏览内置 Phosphor 图标"
        ) {
            Text(
                text = "筛选 Icon 可参考：https://phosphoricons.com",
                style = Theme.typography.bodySmall,
                color = colors.mutedForeground
            )

            SearchBar(
                value = keyword,
                onValueChange = { keyword = it },
                placeholder = "搜索图标名称"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "图标数量：${filteredIcons.size}",
                    style = Theme.typography.bodyMedium,
                    color = colors.foreground
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Text(
                        text = "显示边框",
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                    Switch(
                        checked = showBorder,
                        onCheckedChange = { showBorder = it },
                        contentDescription = "显示边框"
                    )
                }
            }

            if (filteredIcons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xl),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无匹配图标",
                        style = Theme.typography.bodyMedium,
                        color = colors.mutedForeground
                    )
                }
            } else {
                IconGrid(
                    icons = filteredIcons,
                    showBorder = showBorder
                )
            }
        }
    }
}

@Composable
private fun IconGrid(
    icons: List<String>,
    showBorder: Boolean
) {
    val columns = 3

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        icons.chunked(columns).forEach { rowIcons ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                rowIcons.forEach { iconName ->
                    IconCell(
                        iconName = iconName,
                        showBorder = showBorder,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(columns - rowIcons.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun IconCell(
    iconName: String,
    showBorder: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    val cellShape = Theme.shapes.sm
    val cellBorderWidth = if (showBorder) BorderWidth.thin else BorderWidth.none
    val cellBorderColor = if (showBorder) colors.border else Color.Transparent
    val cellBackground = if (showBorder) colors.surface else Color.Transparent

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .size(IconCellSize)
                .border(cellBorderWidth, cellBorderColor, cellShape)
                .background(cellBackground, cellShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                name = iconName,
                size = IconSizes.Default.xl,
                tint = colors.foreground
            )
        }

        Text(
            text = iconName,
            style = Theme.typography.bodySmall,
            color = colors.mutedForeground
        )
    }
}
