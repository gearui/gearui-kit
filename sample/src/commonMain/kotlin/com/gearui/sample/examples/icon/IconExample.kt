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
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Icon 组件示例（对标 tdesign-flutter icon page）
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
            title = "Icon 示例",
            description = "按名称搜索并浏览内置图标"
        ) {
            Text(
                text = "筛选 Icon 可参考：https://fonts.google.com/icons",
                style = Typography.BodySmall,
                color = colors.textSecondary
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
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "显示边框",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
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
                        style = Typography.BodyMedium,
                        color = colors.textSecondary
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
    val cellBorderColor = if (showBorder) colors.stroke else Color.Transparent
    val cellBackground = if (showBorder) colors.surfaceComponent else Color.Transparent

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
                tint = colors.textPrimary
            )
        }

        Text(
            text = iconName,
            style = Typography.BodySmall,
            color = colors.textSecondary
        )
    }
}
