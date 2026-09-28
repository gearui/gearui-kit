package com.gearui.sample.examples.inputotp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.inputotp.InputOTP
import com.gearui.components.toast.Toast
import com.gearui.foundation.field.FieldDescription
import com.gearui.foundation.field.FieldErrorText
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.layout.Spacing
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column

@Composable
fun InputOTPExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var grouped by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf("") }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "基础",
            description = "6 位数字验证码，点任意格子都会聚焦"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                InputOTP(
                    value = code,
                    onValueChange = { code = it },
                    onComplete = { Toast.show("验证码:$it") },
                )
                FieldDescription("已输入 ${code.length} / 6")
            }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "分组与占位",
            description = "groupSize 分隔，placeholder 逐位显示"
        ) {
            InputOTP(
                value = grouped,
                onValueChange = { grouped = it },
                groupSize = 3,
                placeholder = "000000",
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "校验",
            description = "输入 4 位且不是 1234 时 invalid 红色描边"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                val invalid = checked.length == 4 && checked != "1234"
                InputOTP(
                    value = checked,
                    onValueChange = { checked = it },
                    length = 4,
                    invalid = invalid,
                )
                if (invalid) FieldErrorText("验证码不正确")
            }
        }

        ExampleSection(
            title = "卡片上使用",
            description = "FieldVariant.SECONDARY 中性填充底，适合白色卡片"
        ) {
            InputOTP(
                value = "8642",
                onValueChange = {},
                length = 4,
                variant = FieldVariant.SECONDARY,
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "禁用",
            description = "enabled = false"
        ) {
            InputOTP(value = "12", onValueChange = {}, length = 4, enabled = false)
        }
    }
}
