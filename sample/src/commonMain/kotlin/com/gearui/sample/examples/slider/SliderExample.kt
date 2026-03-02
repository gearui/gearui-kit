package com.gearui.sample.examples.slider

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.slider.Slider
import com.gearui.components.slider.RangeSlider
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.gearui.Spacing
import kotlin.math.roundToInt

/**
 * Slider 滑块组件示例
 *
 * 用于选择横轴上的数值、区间、档位。
 */
@Composable
fun SliderExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== 组件类型 ==========

        // 单游标滑块
        var singleValue by remember { mutableStateOf(10f) }
        ExampleSection(
            title = "单游标滑块",
            description = "基础单滑块"
        ) {
            Slider(
                value = singleValue,
                onValueChange = { singleValue = it },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 双游标滑块
        ExampleSection(
            title = "双游标滑块",
            description = "选择数值范围"
        ) {
            RangeSlider(
                values = 10f..60f,
                onValuesChange = { },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 带数值单游标滑块
        var singleWithNumberValue by remember { mutableStateOf(10f) }
        ExampleSection(
            title = "带数值单游标滑块",
            description = "显示左右标签"
        ) {
            Slider(
                value = singleWithNumberValue,
                onValueChange = { singleWithNumberValue = it },
                valueRange = 0f..100f,
                leftLabel = "0",
                rightLabel = "100",
                showThumbValue = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 带数值双游标滑块
        ExampleSection(
            title = "带数值双游标滑块",
            description = "显示左右标签和当前值"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "40 - 60",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "100",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                }
                RangeSlider(
                    values = 40f..60f,
                    onValuesChange = { },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 带刻度单游标滑块
        var scaleValue by remember { mutableStateOf(60f) }
        ExampleSection(
            title = "带刻度单游标滑块",
            description = "显示刻度值"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
            ) {
                Slider(
                    value = scaleValue,
                    onValueChange = { scaleValue = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )
                // 刻度标签
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(0, 20, 40, 60, 80, 100).forEach { tick ->
                        Text(
                            text = tick.toString(),
                            style = Typography.BodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        // 带刻度双游标滑块
        ExampleSection(
            title = "带刻度双游标滑块",
            description = "显示刻度值"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
            ) {
                RangeSlider(
                    values = 40f..70f,
                    onValuesChange = { },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )
                // 刻度标签
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(0, 20, 40, 60, 80, 100).forEach { tick ->
                        Text(
                            text = tick.toString(),
                            style = Typography.BodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        // ========== 组件状态 ==========

        // 禁用状态
        ExampleSection(
            title = "禁用状态",
            description = "单游标禁用"
        ) {
            Slider(
                value = 40f,
                onValueChange = { },
                valueRange = 0f..100f,
                enabled = false,
                leftLabel = "0",
                rightLabel = "100",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 禁用状态 - 带数值双游标
        ExampleSection(
            title = "",
            description = "带数值双游标禁用"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0",
                        style = Typography.BodyMedium,
                        color = colors.textDisabled
                    )
                    Text(
                        text = "20 - 60",
                        style = Typography.BodySmall,
                        color = colors.textDisabled
                    )
                    Text(
                        text = "100",
                        style = Typography.BodyMedium,
                        color = colors.textDisabled
                    )
                }
                RangeSlider(
                    values = 20f..60f,
                    onValuesChange = { },
                    valueRange = 0f..100f,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 禁用状态 - 带刻度双游标
        ExampleSection(
            title = "",
            description = "带刻度双游标禁用"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
            ) {
                RangeSlider(
                    values = 20f..60f,
                    onValuesChange = { },
                    valueRange = 0f..100f,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                // 刻度标签
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(0, 20, 40, 60, 80, 100).forEach { tick ->
                        Text(
                            text = tick.toString(),
                            style = Typography.BodySmall,
                            color = colors.textDisabled
                        )
                    }
                }
            }
        }

        // ========== 特殊样式 ==========

        // 胶囊型滑块示例
        var capsuleValue by remember { mutableStateOf(40f) }
        ExampleSection(
            title = "胶囊型滑块",
            description = "圆角胶囊样式"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)
            ) {
                // 带数值
                Slider(
                    value = capsuleValue,
                    onValueChange = { capsuleValue = it },
                    valueRange = 0f..100f,
                    showThumbValue = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 带标签
                var capsuleValue2 by remember { mutableStateOf(40f) }
                Slider(
                    value = capsuleValue2,
                    onValueChange = { capsuleValue2 = it },
                    valueRange = 0f..100f,
                    leftLabel = "0",
                    rightLabel = "100",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ========== 实际应用场景 ==========

        ExampleSection(
            title = "应用场景",
            description = "实际使用示例"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)
            ) {
                // 音量控制
                var volumeValue by remember { mutableStateOf(70f) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.spacer4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "音量",
                            style = Typography.BodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "${volumeValue.roundToInt()}%",
                            style = Typography.BodySmall,
                            color = colors.textSecondary
                        )
                    }
                    Slider(
                        value = volumeValue,
                        onValueChange = { volumeValue = it },
                        valueRange = 0f..100f,
                        leftLabel = "静音",
                        rightLabel = "最大",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 亮度控制
                var brightnessValue by remember { mutableStateOf(80f) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.spacer4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "亮度",
                            style = Typography.BodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "${brightnessValue.roundToInt()}%",
                            style = Typography.BodySmall,
                            color = colors.textSecondary
                        )
                    }
                    Slider(
                        value = brightnessValue,
                        onValueChange = { brightnessValue = it },
                        valueRange = 0f..100f,
                        leftLabel = "暗",
                        rightLabel = "亮",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 价格筛选
                var priceValue by remember { mutableStateOf(500f) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.spacer4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "价格筛选",
                            style = Typography.BodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "¥${priceValue.roundToInt()}",
                            style = Typography.BodySmall,
                            color = colors.danger
                        )
                    }
                    Slider(
                        value = priceValue,
                        onValueChange = { priceValue = it },
                        valueRange = 0f..1000f,
                        leftLabel = "¥0",
                        rightLabel = "¥1000",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 温度调节
                var temperatureValue by remember { mutableStateOf(24f) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.spacer4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "空调温度",
                            style = Typography.BodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "${temperatureValue.roundToInt()}°C",
                            style = Typography.BodySmall,
                            color = colors.primary
                        )
                    }
                    Slider(
                        value = temperatureValue,
                        onValueChange = { temperatureValue = it },
                        valueRange = 16f..30f,
                        leftLabel = "16°C",
                        rightLabel = "30°C",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
