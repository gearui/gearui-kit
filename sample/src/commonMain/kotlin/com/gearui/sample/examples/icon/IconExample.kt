package com.gearui.sample.examples.icon

import com.gearui.components.icon.ImageIcon
import com.gearui.components.icon.IconSource
import com.gearui.components.icon.*
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
    var fill by remember { mutableStateOf(false) }

    val filteredIcons = remember(keyword) {
        val q = keyword.trim()
        if (q.isEmpty()) allIcons else allIcons.filter { it.first.contains(q, ignoreCase = true) }
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
                    Icon(Icons.house, size = size, tint = colors.foreground)
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                listOf(colors.primary, colors.success, colors.warning, colors.destructive, colors.mutedForeground)
                    .forEach { tint -> Icon(Icons.heart, size = IconSizes.Default.xl, tint = tint) }
            }
        }

        ExampleSection(
            title = "应用自带图标",
            description = "应用用 ImageIcon 包一张自己的 PNG，就和内置图标走同一个 Icon API"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Supplied by sample/src/commonMain/assets/icons/app_folder.png, not by the
                // library: an app's own icon set is an object of ImageIcon (or VectorIcon) getters.
                Icon(SampleIcons.appFolder, size = IconSizes.Default.xl)
                Text(
                    text = "app_folder（来自 sample 自己的 assets）",
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }

        ExampleSection(
            title = "内置图标库",
            description = "按名称搜索内置 Phosphor 图标，regular 与 fill 两种"
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
                        text = "实心",
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                    Switch(
                        checked = fill,
                        onCheckedChange = { fill = it },
                        contentDescription = "实心"
                    )
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
                // A page of results: drawing all of them at once is 1500 canvases.
                if (filteredIcons.size > GALLERY_LIMIT) {
                    Text(
                        text = "显示前 $GALLERY_LIMIT 个，搜索名称缩小范围",
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                }
                IconGrid(
                    icons = filteredIcons.take(GALLERY_LIMIT),
                    showBorder = showBorder,
                    fill = fill,
                )
            }
        }
    }
}

@Composable
private fun IconGrid(
    icons: List<Pair<String, IconSource>>,
    showBorder: Boolean,
    fill: Boolean,
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
                rowIcons.forEach { (iconName, icon) ->
                    IconCell(
                        iconName = iconName,
                        icon = icon,
                        fill = fill,
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
    icon: IconSource,
    fill: Boolean,
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
            Icon(icon,
                size = IconSizes.Default.xl,
                fill = fill,
            )
        }

        Text(
            text = iconName,
            style = Theme.typography.bodySmall,
            color = colors.mutedForeground
        )
    }
}

private const val GALLERY_LIMIT = 99

/** The sample's own icons: the way an app extends the set, here with a PNG it ships. */
object SampleIcons {
    val appFolder: IconSource get() = ImageIcon("app_folder", "assets://icons/app_folder.png")
}
