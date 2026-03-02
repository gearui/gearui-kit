package com.gearui.sample.examples.input

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.input.Input
import com.gearui.components.input.InputSize
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Input 组件示例
 *
 * 用于在预设的一组选项中执行单项选择，并呈现选择结果。
 */
@Composable
fun InputExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ==================== 组件类型 ====================

        // 基础输入框
        ExampleSection(
            title = "基础输入框",
            description = "标准文本输入框"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 带标签
                var value1 by remember { mutableStateOf("") }
                Input(
                    value = value1,
                    onValueChange = { value1 = it },
                    label = "Label Text",
                    placeholder = "Please enter text"
                )

                // 必填项
                var value2 by remember { mutableStateOf("") }
                Input(
                    value = value2,
                    onValueChange = { value2 = it },
                    label = "标签文字",
                    required = true,
                    placeholder = "请输入文字"
                )

                // 选填项
                var value3 by remember { mutableStateOf("") }
                Input(
                    value = value3,
                    onValueChange = { value3 = it },
                    label = "标签文字",
                    placeholder = "请输入文字(选填)"
                )

                // 纯输入框（无标签）
                var value4 by remember { mutableStateOf("") }
                Input(
                    value = value4,
                    onValueChange = { value4 = it },
                    placeholder = "请输入文字"
                )

                // 带辅助说明
                var value5 by remember { mutableStateOf("") }
                Input(
                    value = value5,
                    onValueChange = { value5 = it },
                    label = "标签文字",
                    placeholder = "请输入文字",
                    helperText = "辅助说明"
                )
            }
        }

        // 带字数限制输入框
        ExampleSection(
            title = "带字数限制输入框",
            description = "限制最大输入字符数"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                var value1 by remember { mutableStateOf("") }
                Input(
                    value = value1,
                    onValueChange = { value1 = it },
                    label = "标签文字",
                    placeholder = "请输入文字",
                    maxLength = 10,
                    showCounter = true,
                    helperText = "最大输入10个字符"
                )

                var value2 by remember { mutableStateOf("") }
                Input(
                    value = value2,
                    onValueChange = { value2 = it },
                    label = "标签文字",
                    placeholder = "请输入文字",
                    maxLength = 10,
                    showCounter = true,
                    helperText = "最大输入10个字符，汉字算两个"
                )
            }
        }

        // 带操作输入框
        ExampleSection(
            title = "带操作输入框",
            description = "输入框右侧带操作按钮或图标"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 带清除图标
                var value1 by remember { mutableStateOf("可清除的内容") }
                Input(
                    value = value1,
                    onValueChange = { value1 = it },
                    label = "标签文字",
                    placeholder = "请输入文字",
                    clearable = true,
                    onClear = { value1 = "" }
                )

                // 带操作按钮
                var value2 by remember { mutableStateOf("") }
                Input(
                    value = value2,
                    onValueChange = { value2 = it },
                    label = "标签文字",
                    placeholder = "请输入文字",
                    suffix = {
                        Button(
                            text = "操作按钮",
                            size = ButtonSize.SMALL,
                            theme = ButtonTheme.PRIMARY,
                            onClick = { Toast.show("点击操作按钮") }
                        )
                    }
                )

                // 带图标按钮
                var value3 by remember { mutableStateOf("") }
                Input(
                    value = value3,
                    onValueChange = { value3 = it },
                    label = "标签文字",
                    placeholder = "请输入文字",
                    suffix = {
                        Text(
                            text = "👤",
                            style = Typography.BodyLarge,
                            modifier = Modifier.clickable { Toast.show("点击图标") }
                        )
                    }
                )
            }
        }

        // 带图标输入框
        ExampleSection(
            title = "带图标输入框",
            description = "输入框左侧带图标"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                var value1 by remember { mutableStateOf("") }
                Input(
                    value = value1,
                    onValueChange = { value1 = it },
                    label = "标签文字",
                    placeholder = "请输入文字",
                    prefix = {
                        Text(
                            text = "📱",
                            style = Typography.BodyMedium
                        )
                    }
                )

                var value2 by remember { mutableStateOf("") }
                Input(
                    value = value2,
                    onValueChange = { value2 = it },
                    placeholder = "请输入文字",
                    prefix = {
                        Text(
                            text = "🔍",
                            style = Typography.BodyMedium
                        )
                    }
                )
            }
        }

        // 特定类型输入框
        ExampleSection(
            title = "特定类型输入框",
            description = "密码、验证码、手机号、价格等"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 密码输入框
                var password by remember { mutableStateOf("") }
                var showPassword by remember { mutableStateOf(false) }
                Input(
                    value = password,
                    onValueChange = { password = it },
                    label = "输入密码",
                    placeholder = "请输入密码",
                    isPassword = !showPassword,
                    suffix = {
                        Text(
                            text = if (showPassword) "👁" else "👁‍🗨",
                            style = Typography.BodyLarge,
                            modifier = Modifier.clickable { showPassword = !showPassword }
                        )
                    }
                )

                // 验证码输入框
                var verifyCode by remember { mutableStateOf("") }
                Input(
                    value = verifyCode,
                    onValueChange = { verifyCode = it },
                    label = "验证码",
                    placeholder = "输入验证码",
                    suffix = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(24.dp)
                                    .background(colors.border)
                            )
                            Box(
                                modifier = Modifier
                                    .width(72.dp)
                                    .height(36.dp)
                                    .clip(Theme.shapes.small)
                                    .background(colors.surfaceVariant)
                                    .clickable { Toast.show("点击更换验证码") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "ABCD",
                                    style = Typography.TitleSmall,
                                    color = colors.primary
                                )
                            }
                        }
                    }
                )

                // 手机号输入框
                var phone by remember { mutableStateOf("") }
                var countdown by remember { mutableStateOf(0) }
                Input(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "手机号",
                    placeholder = "输入手机号",
                    suffix = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(24.dp)
                                    .background(colors.border)
                            )
                            Text(
                                text = if (countdown > 0) "重发(${countdown}秒)" else "发送验证码",
                                style = Typography.BodyMedium,
                                color = if (countdown > 0) colors.textDisabled else colors.primary,
                                modifier = Modifier.clickable(enabled = countdown == 0) {
                                    Toast.show("发送验证码")
                                }
                            )
                        }
                    }
                )

                // 价格输入框
                var price by remember { mutableStateOf("") }
                Input(
                    value = price,
                    onValueChange = { price = it },
                    label = "价格",
                    placeholder = "0.00",
                    textAlign = TextAlign.End,
                    suffix = {
                        Text(
                            text = "元",
                            style = Typography.BodyMedium,
                            color = colors.textPrimary
                        )
                    }
                )

                // 数量输入框
                var quantity by remember { mutableStateOf("") }
                Input(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = "数量",
                    placeholder = "填写个数",
                    textAlign = TextAlign.End,
                    suffix = {
                        Text(
                            text = "个",
                            style = Typography.BodyMedium,
                            color = colors.textPrimary
                        )
                    }
                )
            }
        }

        // ==================== 组件状态 ====================

        // 输入框状态
        ExampleSection(
            title = "输入框状态",
            description = "错误提示、只读状态"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 错误状态
                var errorValue by remember { mutableStateOf("错误的输入内容") }
                Input(
                    value = errorValue,
                    onValueChange = { errorValue = it },
                    label = "标签文字",
                    placeholder = "请输入文字",
                    errorText = "错误提示说明"
                )

                // 只读状态
                Input(
                    value = "不可编辑文字",
                    onValueChange = {},
                    label = "标签文字",
                    readOnly = true
                )
            }
        }

        // 信息超长状态
        ExampleSection(
            title = "信息超长状态",
            description = "标签超长、输入超长"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 标签超长
                var value1 by remember { mutableStateOf("") }
                Input(
                    value = value1,
                    onValueChange = { value1 = it },
                    label = "标签超长时最多十个字",
                    placeholder = "请输入文字"
                )

                // 输入超长
                Input(
                    value = "输入文字超长不超过两行输入文字超长不超过两行",
                    onValueChange = {},
                    label = "标签文字",
                    maxLines = 2
                )
            }
        }

        // ==================== 组件样式 ====================

        // 内容位置
        ExampleSection(
            title = "内容位置",
            description = "左对齐、居中、右对齐"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                var value1 by remember { mutableStateOf("左对齐内容") }
                Input(
                    value = value1,
                    onValueChange = { value1 = it },
                    label = "左对齐",
                    textAlign = TextAlign.Start
                )

                var value2 by remember { mutableStateOf("居中内容") }
                Input(
                    value = value2,
                    onValueChange = { value2 = it },
                    label = "居中",
                    textAlign = TextAlign.Center
                )

                var value3 by remember { mutableStateOf("右对齐内容") }
                Input(
                    value = value3,
                    onValueChange = { value3 = it },
                    label = "右对齐",
                    textAlign = TextAlign.End
                )
            }
        }

        // 竖排样式
        ExampleSection(
            title = "竖排样式",
            description = "标签在输入框上方"
        ) {
            var value by remember { mutableStateOf("") }
            Input(
                value = value,
                onValueChange = { value = it },
                label = "标签文字",
                placeholder = "请输入文字",
                labelPosition = "top",
                suffix = {
                    Text(
                        text = "⚠️",
                        style = Typography.BodyLarge,
                        modifier = Modifier.clickable { Toast.show("点击右侧按钮") }
                    )
                }
            )
        }

        // 非通栏样式
        ExampleSection(
            title = "非通栏样式",
            description = "卡片式输入框"
        ) {
            var value by remember { mutableStateOf("") }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Input(
                    value = value,
                    onValueChange = { value = it },
                    label = "标签文字",
                    placeholder = "请输入文字",
                    cardStyle = true
                )
            }
        }

        // 标签外置样式
        ExampleSection(
            title = "标签外置样式",
            description = "标签在输入框外部上方"
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "标签文字",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
                var value by remember { mutableStateOf("") }
                Input(
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "请输入文字",
                    cardStyle = true,
                    suffix = {
                        Text(
                            text = "⚠️",
                            style = Typography.BodyLarge,
                            modifier = Modifier.clickable { Toast.show("点击右侧按钮") }
                        )
                    }
                )
            }
        }

        // 禁用状态
        ExampleSection(
            title = "禁用状态",
            description = "输入框不可交互"
        ) {
            Input(
                value = "禁用状态的内容",
                onValueChange = {},
                label = "标签文字",
                disabled = true
            )
        }
    }
}
