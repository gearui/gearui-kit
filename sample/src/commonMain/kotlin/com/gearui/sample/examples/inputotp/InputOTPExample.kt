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
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.ui.unit.dp

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
            useCardContainer = false,
            title = "基础",
            description = "6 位数字验证码，点任意格子都会聚焦"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InputOTP(
                    value = code,
                    onValueChange = { code = it },
                    onComplete = { Toast.show("验证码:$it") },
                )
                FieldDescription("已输入 ${code.length} / 6")
            }
        }

        ExampleSection(
            useCardContainer = false,
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
            useCardContainer = false,
            title = "校验与变体",
            description = "invalid 红色描边；SECONDARY 用中性底；禁用态"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val invalid = checked.length == 4 && checked != "1234"
                InputOTP(
                    value = checked,
                    onValueChange = { checked = it },
                    length = 4,
                    invalid = invalid,
                )
                if (invalid) FieldErrorText("验证码不正确")
                InputOTP(
                    value = "8642",
                    onValueChange = {},
                    length = 4,
                    variant = FieldVariant.SECONDARY,
                )
                InputOTP(value = "12", onValueChange = {}, length = 4, enabled = false)
            }
        }
    }
}
