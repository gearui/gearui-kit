package com.gearui.sample.examples.numberfield

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.numberfield.NumberField
import com.gearui.foundation.field.FieldVariant
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection

@Composable
fun NumberFieldExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var quantity by remember { mutableStateOf<Double?>(1.0) }
    var price by remember { mutableStateOf<Double?>(19.9) }
    var offset by remember { mutableStateOf<Double?>(0.0) }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "数量",
            description = "可直接输入，也可用两侧按钮增减；到达上下限时按钮置灰"
        ) {
            NumberField(
                value = quantity,
                onValueChange = { quantity = it },
                label = "购买数量",
                description = "每单最多 10 件",
                min = 1.0,
                max = 10.0,
                required = true,
                variant = FieldVariant.SECONDARY,
            )
        }

        ExampleSection(
            title = "小数步进",
            description = "step = 0.1，保留输入中的半成品文本"
        ) {
            NumberField(
                value = price,
                onValueChange = { price = it },
                label = "单价",
                step = 0.1,
                min = 0.0,
                variant = FieldVariant.SECONDARY,
            )
        }

        ExampleSection(
            title = "负数与校验",
            description = "允许负值；超出范围时显示错误"
        ) {
            NumberField(
                value = offset,
                onValueChange = { offset = it },
                label = "时区偏移",
                min = -12.0,
                max = 14.0,
                error = if ((offset ?: 0.0) != 0.0) "当前设备时区为 UTC+8" else null,
                variant = FieldVariant.SECONDARY,
            )
        }
    }
}
