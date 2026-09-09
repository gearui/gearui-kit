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
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.icon.Icons
import com.gearui.components.searchbar.SearchBar
import com.gearui.components.switch.Switch
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

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
            title = "应用自带图标",
            description = "GearUI 内置的是精选集。应用把 PNG 放进自己的 " +
                "assets/icons/ 就能用同一个 API 渲染 —— 图标名不必在 Icons 里。"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Supplied by sample/src/commonMain/assets/icons/app_folder.png,
                // not by the library. Android merges library and app assets into
                // one tree; iOS looks in the bundle and then falls through to the
                // host adapter. So this is the extension point, and it needs no
                // API of its own.
                Icon(name = "app_folder", size = 24.dp, tint = colors.foreground)
                Text(
                    text = "app_folder（来自 sample 自己的 assets）",
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }

        ExampleSection(
            title = "Icon 示例",
            description = "按名称搜索并浏览内置图标"
        ) {
            Text(
                text = "筛选 Icon 可参考：https://phosphoricons.com",
                style = Theme.typography.bodySmall,
                color = colors.mutedForeground
            )

            SearchBar(
                value = keyword,
                onValueChange = { keyword = it },
                placeholder = "搜索 icon 名称..."
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "icon数量: ${filteredIcons.size}",
                    style = Theme.typography.bodyMedium,
                    color = colors.foreground
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "显示边框",
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                    Switch(
                        checked = showBorder,
                        onCheckedChange = { showBorder = it }
                    )
                }
            }

            if (filteredIcons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        icons.chunked(columns).forEach { rowIcons ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
    val cellShape = RoundedCornerShape(6.dp)
    val cellBorderWidth = if (showBorder) 1.dp else 0.dp
    val cellBorderColor = if (showBorder) colors.border else Color.Transparent
    val cellBackground = if (showBorder) colors.surface else Color.Transparent

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .border(cellBorderWidth, cellBorderColor, cellShape)
                .background(cellBackground, cellShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                name = iconName,
                size = 24.dp,
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
