package com.gearui.sample.examples.input

import com.gearui.components.input.InputFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.i18n.I18n
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.icon.Icons
import com.gearui.components.input.Input
import com.gearui.components.toast.Toast
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldDescription
import com.gearui.foundation.field.FieldErrorText
import com.gearui.foundation.field.FieldLabel
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.interaction.PressableFeedback
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.ui.text.style.TextAlign

/**
 * Input component examples.
 *
 * Every field sits on a white Card section, so it uses the filled variant
 * (`variant = FieldVariant.SECONDARY`); the shadowed default variant is shown once, on the page
 * background, where it belongs.
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
        ExampleSection(
            title = "字段结构 Label / Description / FieldError",
            description = "自定义组合时用同一套字段文字：标签、必填星号、说明与错误"
        ) {
            var email by remember { mutableStateOf("") }
            val emailError = if (email.isNotEmpty() && !email.contains("@")) "邮箱格式不正确" else null
            Column(verticalArrangement = Arrangement.spacedBy(FieldDefaults.labelGap)) {
                FieldLabel("邮箱", required = true, invalid = emailError != null)
                Input(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "name@example.com",
                    variant = FieldVariant.SECONDARY
                )
                if (emailError != null) {
                    FieldErrorText(emailError)
                } else {
                    FieldDescription("仅用于找回密码，不会公开")
                }
            }
            FieldLabel("禁用字段", required = true, enabled = false)
        }

        ExampleSection(
            title = "基础输入框",
            description = "标签、必填、无标签与辅助说明"
        ) {
            var value1 by remember { mutableStateOf("") }
            Input(
                value = value1,
                onValueChange = { value1 = it },
                label = "标签文字",
                required = true,
                placeholder = "请输入文字",
                variant = FieldVariant.SECONDARY
            )

            var value2 by remember { mutableStateOf("") }
            Input(
                value = value2,
                onValueChange = { value2 = it },
                placeholder = "请输入文字（无标签）",
                variant = FieldVariant.SECONDARY
            )

            var value3 by remember { mutableStateOf("") }
            Input(
                value = value3,
                onValueChange = { value3 = it },
                label = "标签文字",
                placeholder = "请输入文字（选填）",
                helperText = "辅助说明",
                variant = FieldVariant.SECONDARY
            )
        }

        ExampleSection(
            title = "标签位置",
            description = "labelPosition：标签在上方或左侧"
        ) {
            var top by remember { mutableStateOf("") }
            Input(
                value = top,
                onValueChange = { top = it },
                label = "上方标签",
                labelPosition = "top",
                placeholder = "请输入文字",
                variant = FieldVariant.SECONDARY
            )
            var left by remember { mutableStateOf("") }
            Input(
                value = left,
                onValueChange = { left = it },
                label = "左侧标签",
                labelPosition = "left",
                placeholder = "请输入文字",
                variant = FieldVariant.SECONDARY
            )
        }

        ExampleSection(
            title = "字数限制",
            description = "maxLength + showCounter，汉字按一个字符计数"
        ) {
            var value by remember { mutableStateOf("") }
            Input(
                value = value,
                onValueChange = { value = it },
                label = "标签文字",
                placeholder = "请输入文字",
                maxLength = 10,
                showCounter = true,
                helperText = "最多输入 10 个字符",
                variant = FieldVariant.SECONDARY
            )
        }

        ExampleSection(
            title = "前后缀",
            description = "清除按钮、后缀按钮与图标、前缀图标"
        ) {
            var value1 by remember { mutableStateOf("可清除的内容") }
            Input(
                value = value1,
                onValueChange = { value1 = it },
                label = "可清除",
                placeholder = "请输入文字",
                clearable = true,
                onClear = { value1 = "" },
                variant = FieldVariant.SECONDARY
            )

            var value2 by remember { mutableStateOf("") }
            Input(
                value = value2,
                onValueChange = { value2 = it },
                label = "后缀按钮",
                placeholder = "请输入文字",
                variant = FieldVariant.SECONDARY,
                suffix = {
                    Button(
                        text = "操作按钮",
                        size = ButtonSize.SMALL,
                        theme = ButtonTheme.PRIMARY,
                        onClick = { Toast.show("点击操作按钮") }
                    )
                }
            )

            var value3 by remember { mutableStateOf("") }
            Input(
                value = value3,
                onValueChange = { value3 = it },
                label = "后缀图标",
                placeholder = "请输入文字",
                variant = FieldVariant.SECONDARY,
                suffix = {
                    PressableFeedback(onClick = { Toast.show("点击图标") }, contentDescription = "账号") {
                        Icon(
                            name = Icons.user,
                            size = IconSizes.Default.md,
                            tint = colors.foreground,
                        )
                    }
                }
            )

            var value4 by remember { mutableStateOf("") }
            Input(
                value = value4,
                onValueChange = { value4 = it },
                placeholder = "搜索",
                variant = FieldVariant.SECONDARY,
                prefix = {
                    Icon(
                        name = Icons.magnifying_glass,
                        size = IconSizes.Default.md,
                        tint = colors.mutedForeground,
                    )
                }
            )
        }

        ExampleSection(
            title = "特定类型",
            description = "密码、验证码、手机号、价格与数量"
        ) {
            var password by remember { mutableStateOf("") }
            var showPassword by remember { mutableStateOf(false) }
            Input(
                value = password,
                onValueChange = { password = it },
                label = "密码",
                placeholder = "请输入密码",
                isPassword = !showPassword,
                variant = FieldVariant.SECONDARY,
                suffix = {
                    PressableFeedback(
                        onClick = { showPassword = !showPassword },
                        contentDescription = if (showPassword) I18n.strings.field.hidePassword else I18n.strings.field.showPassword,
                    ) {
                        Icon(
                            name = if (showPassword) Icons.eye else Icons.eye_slash,
                            size = IconSizes.Default.md,
                            tint = colors.mutedForeground,
                        )
                    }
                }
            )

            var verifyCode by remember { mutableStateOf("") }
            Input(
                value = verifyCode,
                onValueChange = { verifyCode = it },
                label = "验证码",
                placeholder = "输入验证码",
                variant = FieldVariant.SECONDARY,
                suffix = {
                    Button(
                        text = "ABCD",
                        size = ButtonSize.SMALL,
                        theme = ButtonTheme.LIGHT,
                        onClick = { Toast.show("点击更换验证码") }
                    )
                }
            )

            var phone by remember { mutableStateOf("") }
            Input(
                value = phone,
                onValueChange = { phone = it },
                label = "手机号",
                placeholder = "输入手机号",
                variant = FieldVariant.SECONDARY,
                suffix = {
                    Button(
                        text = "发送验证码",
                        size = ButtonSize.SMALL,
                        type = ButtonType.TEXT,
                        onClick = { Toast.show("发送验证码") }
                    )
                }
            )

            var price by remember { mutableStateOf("") }
            Input(
                value = price,
                onValueChange = { price = it },
                label = "价格",
                placeholder = "0.00",
                textAlign = TextAlign.End,
                variant = FieldVariant.SECONDARY,
                suffix = {
                    Text(text = "元", style = Theme.typography.bodyMedium, color = colors.foreground)
                }
            )

            var quantity by remember { mutableStateOf("") }
            Input(
                value = quantity,
                onValueChange = { quantity = it },
                label = "数量",
                placeholder = "填写个数",
                textAlign = TextAlign.End,
                variant = FieldVariant.SECONDARY,
                suffix = {
                    Text(text = "个", style = Theme.typography.bodyMedium, color = colors.foreground)
                }
            )
        }

        ExampleSection(
            title = "格式化输入",
            description = "format 分组显示，value 保持纯数字；可粘贴带 +86 或空格的号码"
        ) {
            var mobile by remember { mutableStateOf("") }
            Input(
                value = mobile,
                onValueChange = { mobile = it },
                label = "手机号",
                placeholder = "请输入手机号",
                format = InputFormat.ChinaMobile,
                clearable = true,
                variant = FieldVariant.SECONDARY,
                helperText = "value：${mobile.ifEmpty { "空" }}",
            )
            var card by remember { mutableStateOf("") }
            Input(
                value = card,
                onValueChange = { card = it },
                label = "银行卡号",
                placeholder = "请输入银行卡号",
                format = InputFormat.BankCard,
                clearable = true,
                variant = FieldVariant.SECONDARY,
                helperText = "value：${card.ifEmpty { "空" }}",
            )
            var idCard by remember { mutableStateOf("") }
            Input(
                value = idCard,
                onValueChange = { idCard = it },
                label = "身份证号",
                placeholder = "请输入身份证号",
                format = InputFormat.IdCard,
                clearable = true,
                variant = FieldVariant.SECONDARY,
                helperText = "value：${idCard.ifEmpty { "空" }}",
                error = if (idCard.length == 18 && !InputFormat.IdCard.isComplete(idCard)) "身份证号校验位不正确" else null,
            )
        }

        ExampleSection(
            title = "状态",
            description = "错误、只读与禁用"
        ) {
            var errorValue by remember { mutableStateOf("错误的输入内容") }
            Input(
                value = errorValue,
                onValueChange = { errorValue = it },
                label = "错误",
                placeholder = "请输入文字",
                error = "错误提示说明",
                variant = FieldVariant.SECONDARY
            )
            Input(
                value = "不可编辑文字",
                onValueChange = {},
                label = "只读",
                readOnly = true,
                variant = FieldVariant.SECONDARY
            )
            Input(
                value = "禁用状态的内容",
                onValueChange = {},
                label = "禁用",
                enabled = false,
                variant = FieldVariant.SECONDARY
            )
        }

        ExampleSection(
            title = "内容超长",
            description = "长标签与最多两行的长内容"
        ) {
            var value by remember { mutableStateOf("") }
            Input(
                value = value,
                onValueChange = { value = it },
                label = "标签超长时最多十个字",
                placeholder = "请输入文字",
                variant = FieldVariant.SECONDARY
            )
            Input(
                value = "输入文字超长不超过两行输入文字超长不超过两行",
                onValueChange = {},
                label = "标签文字",
                maxLines = 2,
                variant = FieldVariant.SECONDARY
            )
        }

        ExampleSection(
            title = "内容对齐",
            description = "textAlign：左对齐、居中、右对齐"
        ) {
            var value1 by remember { mutableStateOf("左对齐内容") }
            Input(
                value = value1,
                onValueChange = { value1 = it },
                label = "左对齐",
                textAlign = TextAlign.Start,
                variant = FieldVariant.SECONDARY
            )
            var value2 by remember { mutableStateOf("居中内容") }
            Input(
                value = value2,
                onValueChange = { value2 = it },
                label = "居中",
                textAlign = TextAlign.Center,
                variant = FieldVariant.SECONDARY
            )
            var value3 by remember { mutableStateOf("右对齐内容") }
            Input(
                value = value3,
                onValueChange = { value3 = it },
                label = "右对齐",
                textAlign = TextAlign.End,
                variant = FieldVariant.SECONDARY
            )
        }

        ExampleSection(
            title = "页面背景上的默认样式",
            description = "FieldVariant.PRIMARY：直接放在页面背景上时用带阴影的默认样式",
            surface = SectionSurface.Plain
        ) {
            var value by remember { mutableStateOf("") }
            Input(
                value = value,
                onValueChange = { value = it },
                label = "标签文字",
                placeholder = "请输入文字"
            )
        }
    }
}
