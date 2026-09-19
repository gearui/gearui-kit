package com.gearui.sample.examples.inputgroup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.icon.Icons
import com.gearui.components.input.Input
import com.gearui.components.inputgroup.InputGroup
import com.gearui.components.inputgroup.InputGroupAddon
import com.gearui.components.inputgroup.InputGroupDivider
import com.gearui.components.toast.Toast
import com.gearui.foundation.field.FieldDescription
import com.gearui.foundation.field.FieldLabel
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.text.input.KeyboardType
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun InputGroupExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            useCardContainer = false,
            title = "前置附加块",
            description = "区号这类固定前缀，和输入框共用一个外框"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel("手机号", required = true)
                InputGroup {
                    InputGroupAddon(text = "+86")
                    InputGroupDivider()
                    Input(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.weight(1f),
                        placeholder = "请输入手机号",
                        keyboardType = KeyboardType.Number,
                    )
                }
                FieldDescription("附加块不可编辑，点击不会聚焦输入框")
            }
        }

        ExampleSection(
            useCardContainer = false,
            title = "后置附加块",
            description = "单位、域名后缀"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InputGroup {
                    Input(
                        value = amount,
                        onValueChange = { amount = it },
                        modifier = Modifier.weight(1f),
                        placeholder = "金额",
                        keyboardType = KeyboardType.Number,
                    )
                    InputGroupDivider()
                    InputGroupAddon(text = "元")
                }
                InputGroup {
                    InputGroupAddon(text = "https://")
                    InputGroupDivider()
                    Input(
                        value = host,
                        onValueChange = { host = it },
                        modifier = Modifier.weight(1f),
                        placeholder = "example.com",
                    )
                }
            }
        }

        ExampleSection(
            useCardContainer = false,
            title = "可点击的附加块",
            description = "带图标或动作，例如获取验证码"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InputGroup {
                    InputGroupAddon(icon = Icons.magnifying_glass)
                    Input(
                        value = code,
                        onValueChange = { code = it },
                        modifier = Modifier.weight(1f),
                        placeholder = "搜索订单号",
                    )
                    InputGroupDivider()
                    InputGroupAddon(text = "获取验证码", onClick = { Toast.show("已发送验证码") })
                }
                InputGroup(enabled = false) {
                    InputGroupAddon(text = "+86")
                    InputGroupDivider()
                    Input(
                        value = "138 0000 0000",
                        onValueChange = {},
                        modifier = Modifier.weight(1f),
                        enabled = false,
                    )
                }
            }
        }
    }
}
