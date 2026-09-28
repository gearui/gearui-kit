package com.gearui.sample.examples.rate

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.icon.Icons
import com.gearui.components.rate.Rate
import com.gearui.components.rate.RateDisplay
import com.gearui.components.rate.RateWithDescription
import com.gearui.foundation.layout.Spacing
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme

// Star sizes shown off in the size demo, and a denser size for a ten-star row.
private val StarSizeSmall = 16.dp
private val StarSizeMedium = 24.dp
private val StarSizeLarge = 32.dp
private val DenseStarSize = 20.dp
private val DenseStarGap = 2.dp

/**
 * Rate component examples
 */
@Composable
fun RateExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    var rating1 by remember { mutableStateOf(3f) }
    var rating2 by remember { mutableStateOf(3.5f) }
    var rating3 by remember { mutableStateOf(4f) }
    var rating4 by remember { mutableStateOf(7f) }
    var rating5 by remember { mutableStateOf(3f) }
    var rating6 by remember { mutableStateOf(4f) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "基础用法",
            description = "点击星星进行评分"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Rate(
                    value = rating1,
                    onValueChange = { rating1 = it }
                )
                Text(
                    text = "当前评分: ${rating1.toInt()} 星",
                    style = Theme.typography.bodyMedium,
                    color = colors.mutedForeground
                )
            }
        }

        ExampleSection(
            title = "半星评分",
            description = "allowHalf = true：点星星左半边给半星，右半边给整星"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Rate(
                    value = rating2,
                    onValueChange = { rating2 = it },
                    allowHalf = true,
                    allowClear = true,
                )
                Text(
                    text = "当前评分: $rating2 星",
                    style = Theme.typography.bodyMedium,
                    color = colors.mutedForeground
                )
            }
        }

        ExampleSection(
            title = "带描述评分",
            description = "RateWithDescription 按分值显示对应描述"
        ) {
            RateWithDescription(
                value = rating3,
                onValueChange = { rating3 = it },
                descriptions = listOf("很差", "较差", "一般", "满意", "很满意")
            )
        }

        ExampleSection(
            title = "显示分数",
            description = "showText = true 在星星后显示当前分数"
        ) {
            Rate(
                value = rating5,
                onValueChange = { rating5 = it },
                showText = true
            )
        }

        ExampleSection(
            title = "自定义图标与颜色",
            description = "icon 换成心形，activeColor 改选中色"
        ) {
            Rate(
                value = rating6,
                onValueChange = { rating6 = it },
                icon = Icons.heart,
                activeColor = colors.destructive
            )
        }

        ExampleSection(
            title = "自定义星星数量",
            description = "count 设置总数，size / gap 调整星星大小与间距"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Rate(
                    value = 2f,
                    onValueChange = null,
                    count = 3,
                    readonly = true
                )
                Rate(
                    value = rating4,
                    onValueChange = { rating4 = it },
                    count = 10,
                    size = DenseStarSize,
                    gap = DenseStarGap
                )
            }
        }

        ExampleSection(
            title = "不同尺寸",
            description = "size 参数调整星星大小",
            surface = SectionSurface.Plain
        ) {
            CellGroup(
                items = listOf(
                    "小号" to StarSizeSmall,
                    "中号" to StarSizeMedium,
                    "大号" to StarSizeLarge
                )
            ) { (label, size) ->
                Cell(
                    title = label,
                    trailing = {
                        Rate(
                            value = 4f,
                            onValueChange = null,
                            size = size,
                            readonly = true
                        )
                    }
                )
            }
        }

        ExampleSection(
            title = "平均分展示",
            description = "RateDisplay 只读，小数分值就近取半星"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                listOf(4.3f, 3.8f, 2.5f).forEach { average ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        RateDisplay(value = average)
                        Text(
                            text = "原始值 $average",
                            style = Theme.typography.bodySmall,
                            color = colors.mutedForeground
                        )
                    }
                }
            }
        }

        ExampleSection(
            title = "列表中展示",
            description = "RateDisplay(showValue = true) 放在 Cell 右侧",
            surface = SectionSurface.Plain
        ) {
            CellGroup(
                items = listOf("商品评分" to 4.5f, "服务评分" to 5f, "物流评分" to 3f)
            ) { (label, score) ->
                Cell(
                    title = label,
                    trailing = {
                        RateDisplay(
                            value = score,
                            showValue = true
                        )
                    }
                )
            }
        }
    }
}
