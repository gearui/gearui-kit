package com.gearui.sample.examples.slider

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.components.slider.Slider
import com.gearui.components.slider.RangeSlider
import com.gearui.components.slider.SliderStyle
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import kotlin.math.roundToInt

// Four inner stops split 0..100 into 20-point ticks.
private const val TICK_STEPS = 4

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
        // Single-thumb slider
        var singleValue by remember { mutableStateOf(10f) }
        ExampleSection(
            title = "单游标滑块",
            description = "拖动或点击轨道取值"
        ) {
            Slider(
                value = singleValue,
                onValueChange = { singleValue = it },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Two-thumb slider
        var rangeValue by remember { mutableStateOf(10f..60f) }
        ExampleSection(
            title = "双游标滑块",
            description = "RangeSlider 选择一个数值区间"
        ) {
            RangeSlider(
                values = rangeValue,
                onValuesChange = { rangeValue = it },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Labels and thumb value
        var singleWithNumberValue by remember { mutableStateOf(10f) }
        var rangeWithNumberValue by remember { mutableStateOf(40f..60f) }
        ExampleSection(
            title = "显示数值",
            description = "leftLabel / rightLabel 两端标签，showThumbValue 游标上显示当前值"
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
            RangeSlider(
                values = rangeWithNumberValue,
                onValuesChange = { rangeWithNumberValue = it },
                valueRange = 0f..100f,
                leftLabel = "0",
                rightLabel = "100",
                showThumbValue = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Steps with scale values
        var scaleValue by remember { mutableStateOf(60f) }
        var scaleRange by remember { mutableStateOf(40f..80f) }
        ExampleSection(
            title = "刻度步进",
            description = "steps 吸附到刻度，showScaleValue 显示刻度值"
        ) {
            Slider(
                value = scaleValue,
                onValueChange = { scaleValue = it },
                valueRange = 0f..100f,
                steps = TICK_STEPS,
                showScaleValue = true,
                modifier = Modifier.fillMaxWidth()
            )
            RangeSlider(
                values = scaleRange,
                onValuesChange = { scaleRange = it },
                valueRange = 0f..100f,
                steps = TICK_STEPS,
                showScaleValue = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Disabled state
        ExampleSection(
            title = "禁用状态",
            description = "enabled = false，单游标与双游标"
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
            RangeSlider(
                values = 20f..60f,
                onValuesChange = { },
                valueRange = 0f..100f,
                enabled = false,
                steps = TICK_STEPS,
                showScaleValue = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Capsule style
        var capsuleValue by remember { mutableStateOf(40f) }
        ExampleSection(
            title = "胶囊型滑块",
            description = "SliderStyle.CAPSULE 加粗的胶囊轨道"
        ) {
            Slider(
                value = capsuleValue,
                onValueChange = { capsuleValue = it },
                valueRange = 0f..100f,
                style = SliderStyle.CAPSULE,
                showThumbValue = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Real use cases
        ExampleSection(
            title = "应用场景",
            description = "标题行显示当前值，自定义值域"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                var volumeValue by remember { mutableStateOf(70f) }
                LabeledSlider(
                    title = "音量",
                    valueText = "${volumeValue.roundToInt()}%",
                    valueColor = colors.mutedForeground
                ) {
                    Slider(
                        value = volumeValue,
                        onValueChange = { volumeValue = it },
                        valueRange = 0f..100f,
                        leftLabel = "静音",
                        rightLabel = "最大",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                var priceValue by remember { mutableStateOf(500f) }
                LabeledSlider(
                    title = "价格筛选",
                    valueText = "¥${priceValue.roundToInt()}",
                    valueColor = colors.destructive
                ) {
                    Slider(
                        value = priceValue,
                        onValueChange = { priceValue = it },
                        valueRange = 0f..1000f,
                        leftLabel = "¥0",
                        rightLabel = "¥1000",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                var temperatureValue by remember { mutableStateOf(24f) }
                LabeledSlider(
                    title = "空调温度",
                    valueText = "${temperatureValue.roundToInt()}°C",
                    valueColor = colors.primary
                ) {
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

/** A title row with the live value above a slider. */
@Composable
private fun LabeledSlider(
    title: String,
    valueText: String,
    valueColor: com.tencent.kuikly.compose.ui.graphics.Color,
    slider: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = Theme.typography.bodyMedium,
                color = Theme.colors.foreground
            )
            Text(
                text = valueText,
                style = Theme.typography.bodySmall,
                color = valueColor
            )
        }
        slider()
    }
}
