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
import com.gearui.foundation.layout.Spacing
import kotlin.math.roundToInt

/**
 * Slider component examples
 *
 * Selects a value, a range or a step along an axis.
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
        // ========== Component types ==========

        // Single-thumb slider
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

        // Two-thumb slider
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

        // Single-thumb slider with a value
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

        // Two-thumb slider with values
        ExampleSection(
            title = "带数值双游标滑块",
            description = "显示左右标签和当前值"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0",
                        style = Typography.BodyMedium,
                        color = colors.foreground
                    )
                    Text(
                        text = "40 - 60",
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                    Text(
                        text = "100",
                        style = Typography.BodyMedium,
                        color = colors.foreground
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

        // Single-thumb slider with ticks
        var scaleValue by remember { mutableStateOf(60f) }
        ExampleSection(
            title = "带刻度单游标滑块",
            description = "显示刻度值"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Slider(
                    value = scaleValue,
                    onValueChange = { scaleValue = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )
                // Tick labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(0, 20, 40, 60, 80, 100).forEach { tick ->
                        Text(
                            text = tick.toString(),
                            style = Typography.BodySmall,
                            color = colors.mutedForeground
                        )
                    }
                }
            }
        }

        // Two-thumb slider with ticks
        ExampleSection(
            title = "带刻度双游标滑块",
            description = "显示刻度值"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                RangeSlider(
                    values = 40f..70f,
                    onValuesChange = { },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )
                // Tick labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(0, 20, 40, 60, 80, 100).forEach { tick ->
                        Text(
                            text = tick.toString(),
                            style = Typography.BodySmall,
                            color = colors.mutedForeground
                        )
                    }
                }
            }
        }

        // ========== Component states ==========

        // Disabled state
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

        // Disabled state - two thumbs with values
        ExampleSection(
            title = "",
            description = "带数值双游标禁用"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0",
                        style = Typography.BodyMedium,
                        color = colors.mutedForeground
                    )
                    Text(
                        text = "20 - 60",
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                    Text(
                        text = "100",
                        style = Typography.BodyMedium,
                        color = colors.mutedForeground
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

        // Disabled state - two thumbs with ticks
        ExampleSection(
            title = "",
            description = "带刻度双游标禁用"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                RangeSlider(
                    values = 20f..60f,
                    onValuesChange = { },
                    valueRange = 0f..100f,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                // Tick labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(0, 20, 40, 60, 80, 100).forEach { tick ->
                        Text(
                            text = tick.toString(),
                            style = Typography.BodySmall,
                            color = colors.mutedForeground
                        )
                    }
                }
            }
        }

        // ========== Special styles ==========

        // Pill-shaped slider
        var capsuleValue by remember { mutableStateOf(40f) }
        ExampleSection(
            title = "胶囊型滑块",
            description = "圆角胶囊样式"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                // With a value
                Slider(
                    value = capsuleValue,
                    onValueChange = { capsuleValue = it },
                    valueRange = 0f..100f,
                    showThumbValue = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // With a label
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

        // ========== Real use cases ==========

        ExampleSection(
            title = "应用场景",
            description = "实际使用示例"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                // Volume control
                var volumeValue by remember { mutableStateOf(70f) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "音量",
                            style = Typography.BodyMedium,
                            color = colors.foreground
                        )
                        Text(
                            text = "${volumeValue.roundToInt()}%",
                            style = Typography.BodySmall,
                            color = colors.mutedForeground
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

                // Brightness control
                var brightnessValue by remember { mutableStateOf(80f) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "亮度",
                            style = Typography.BodyMedium,
                            color = colors.foreground
                        )
                        Text(
                            text = "${brightnessValue.roundToInt()}%",
                            style = Typography.BodySmall,
                            color = colors.mutedForeground
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

                // Price filter
                var priceValue by remember { mutableStateOf(500f) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "价格筛选",
                            style = Typography.BodyMedium,
                            color = colors.foreground
                        )
                        Text(
                            text = "¥${priceValue.roundToInt()}",
                            style = Typography.BodySmall,
                            color = colors.destructive
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

                // Temperature control
                var temperatureValue by remember { mutableStateOf(24f) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "空调温度",
                            style = Typography.BodyMedium,
                            color = colors.foreground
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
