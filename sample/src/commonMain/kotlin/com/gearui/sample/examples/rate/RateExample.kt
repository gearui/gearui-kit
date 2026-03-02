package com.gearui.sample.examples.rate

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.rate.Rate
import com.gearui.components.rate.RateDisplay
import com.gearui.components.rate.RateWithDescription
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Rate 组件示例
 */
@Composable
fun RateExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 基础评分
    var rating1 by remember { mutableStateOf(3f) }

    // 半星评分
    var rating2 by remember { mutableStateOf(3.5f) }

    // 带描述评分
    var rating3 by remember { mutableStateOf(4f) }

    // 自定义数量
    var rating4 by remember { mutableStateOf(7f) }

    // 显示文字
    var rating5 by remember { mutableStateOf(3f) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础用法
        ExampleSection(
            title = "基础用法",
            description = "点击星星进行评分"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Rate(
                    value = rating1,
                    onValueChange = { rating1 = it }
                )

                Text(
                    text = "当前评分: ${rating1.toInt()} 星",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }

        // 半星评分
        ExampleSection(
            title = "半星评分",
            description = "设置 allowHalf=true 支持半星选择"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Rate(
                    value = rating2,
                    onValueChange = { rating2 = it },
                    allowHalf = true
                )

                Text(
                    text = "当前评分: $rating2 星",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }

        // 带描述文字
        ExampleSection(
            title = "带描述评分",
            description = "根据评分显示对应的描述文字"
        ) {
            RateWithDescription(
                value = rating3,
                onValueChange = { rating3 = it },
                descriptions = listOf("很差", "较差", "一般", "满意", "很满意")
            )
        }

        // 显示分数
        ExampleSection(
            title = "显示分数",
            description = "设置 showText=true 显示当前分数"
        ) {
            Rate(
                value = rating5,
                onValueChange = { rating5 = it },
                showText = true
            )
        }

        // 自定义数量
        ExampleSection(
            title = "自定义星星数量",
            description = "通过 count 参数设置星星总数"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "3 颗星",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
                Rate(
                    value = 2f,
                    onValueChange = null,
                    count = 3,
                    readonly = true
                )

                Text(
                    text = "10 颗星",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
                Rate(
                    value = rating4,
                    onValueChange = { rating4 = it },
                    count = 10,
                    size = 20.dp,
                    gap = 2.dp
                )
            }
        }

        // 不同尺寸
        ExampleSection(
            title = "不同尺寸",
            description = "通过 size 参数调整星星大小"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "小号",
                        style = Typography.BodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.width(40.dp)
                    )
                    Rate(
                        value = 4f,
                        onValueChange = null,
                        size = 16.dp,
                        readonly = true
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "中号",
                        style = Typography.BodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.width(40.dp)
                    )
                    Rate(
                        value = 4f,
                        onValueChange = null,
                        size = 24.dp,
                        readonly = true
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "大号",
                        style = Typography.BodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.width(40.dp)
                    )
                    Rate(
                        value = 4f,
                        onValueChange = null,
                        size = 32.dp,
                        readonly = true
                    )
                }
            }
        }

        // 只读模式
        ExampleSection(
            title = "只读展示",
            description = "使用 RateDisplay 只读显示评分"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "商品评分:",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    RateDisplay(
                        value = 4.5f,
                        showValue = true
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "服务评分:",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    RateDisplay(
                        value = 5f,
                        showValue = true
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "物流评分:",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    RateDisplay(
                        value = 3f,
                        showValue = true
                    )
                }
            }
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Rate 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. 支持整星和半星评分",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. 可自定义星星数量",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. 支持显示描述文字",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. 支持只读展示模式",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. 可配置星星大小和间距",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
