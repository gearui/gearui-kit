package com.gearui.sample.examples.stepper

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.components.stepper.Stepper
import com.gearui.components.stepper.StepperSize
import com.gearui.components.stepper.StepperWithLabel
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.layout.Spacing

/**
 * Stepper component examples
 */
@Composable
fun StepperExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    // State of each example
    var basicValue by remember { mutableStateOf(1) }
    var smallValue by remember { mutableStateOf(1) }
    var mediumValue by remember { mutableStateOf(1) }
    var largeValue by remember { mutableStateOf(1) }
    var rangeValue by remember { mutableStateOf(5) }
    var stepValue by remember { mutableStateOf(0) }
    var labelValue by remember { mutableStateOf(1) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // Basic stepper
        ExampleSection(
            title = "基础步进器",
            description = "Stepper 单独使用，点击加减按钮"
        ) {
            Stepper(
                value = basicValue,
                onValueChange = { basicValue = it }
            )
        }

        // Sizes
        ExampleSection(
            title = "不同尺寸",
            description = "StepperSize 小、中、大三档"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                StepperWithLabel(
                    value = smallValue,
                    onValueChange = { smallValue = it },
                    label = "小尺寸",
                    size = StepperSize.SMALL
                )

                StepperWithLabel(
                    value = mediumValue,
                    onValueChange = { mediumValue = it },
                    label = "中尺寸",
                    size = StepperSize.MEDIUM
                )

                StepperWithLabel(
                    value = largeValue,
                    onValueChange = { largeValue = it },
                    label = "大尺寸",
                    size = StepperSize.LARGE
                )
            }
        }

        // Bounds
        ExampleSection(
            title = "设置范围",
            description = "最小值 1，最大值 10"
        ) {
            StepperWithLabel(
                value = rangeValue,
                onValueChange = { rangeValue = it },
                label = "范围 1-10",
                min = 1,
                max = 10
            )
        }

        // Step size
        ExampleSection(
            title = "设置步长",
            description = "每次增减 5"
        ) {
            StepperWithLabel(
                value = stepValue,
                onValueChange = { stepValue = it },
                label = "步长 5",
                min = 0,
                max = 100,
                step = 5
            )
        }

        // Disabled state
        ExampleSection(
            title = "禁用状态",
            description = "enabled = false"
        ) {
            StepperWithLabel(
                value = 3,
                onValueChange = { },
                label = "禁用",
                enabled = false
            )
        }

        // Stepper with a label
        ExampleSection(
            title = "带标签的步进器",
            description = "StepperWithLabel 左侧标签、右侧步进器"
        ) {
            StepperWithLabel(
                value = labelValue,
                onValueChange = { labelValue = it },
                label = "购买数量",
                min = 1,
                max = 99
            )
        }
    }
}
