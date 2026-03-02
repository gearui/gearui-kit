package com.gearui.sample.examples.form

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.cell.Cell
import com.gearui.components.input.Input
import com.gearui.components.input.InputSize
import com.gearui.components.picker.DatePickerInput
import com.gearui.components.radio.RadioButton
import com.gearui.components.rate.Rate
import com.gearui.components.stepper.Stepper
import com.gearui.components.stepper.StepperSize
import com.gearui.components.switch.Switch
import com.gearui.components.textarea.Textarea
import com.gearui.components.textarea.TextareaLayout
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.Spacing
import com.gearui.theme.Theme

/**
 * Form 组件示例
 */
@Composable
fun FormExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    // 表单布局方式：水平 / 垂直
    var isHorizontal by remember { mutableStateOf(true) }

    // 表单禁用状态
    var formDisabled by remember { mutableStateOf(false) }

    // 表单数据
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf<String?>(null) }
    var birthday by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var years by remember { mutableStateOf(2) }
    var selfEvaluation by remember { mutableStateOf(2f) }
    var resume by remember { mutableStateOf("") }

    // 验证错误
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var genderError by remember { mutableStateOf<String?>(null) }
    var birthdayError by remember { mutableStateOf<String?>(null) }
    var placeError by remember { mutableStateOf<String?>(null) }
    var yearsError by remember { mutableStateOf<String?>(null) }
    var rateError by remember { mutableStateOf<String?>(null) }
    var resumeError by remember { mutableStateOf<String?>(null) }

    // 性别选项
    val genderOptions = listOf(
        "男" to "0",
        "女" to "1",
        "保密" to "2"
    )

    // 籍贯选项（简化版）
    val placeOptions = listOf(
        "北京市/北京市/东城区",
        "北京市/北京市/西城区",
        "北京市/北京市/朝阳区",
        "天津市/天津市/和平区",
        "天津市/天津市/河东区"
    )
    var showPlacePicker by remember { mutableStateOf(false) }

    // 验证函数
    fun validate(): Boolean {
        var valid = true

        // 用户名验证
        if (username.isBlank()) {
            usernameError = "输入不能为空"
            valid = false
        } else {
            usernameError = null
        }

        // 密码验证 - 只能输入8个字符英文
        val passwordRegex = Regex("^[a-zA-Z]{8}$")
        if (!passwordRegex.matches(password)) {
            passwordError = "只能输入8个字符英文"
            valid = false
        } else {
            passwordError = null
        }

        // 性别验证
        if (gender == null) {
            genderError = "不能为空"
            valid = false
        } else {
            genderError = null
        }

        // 生日验证
        if (birthday.isBlank()) {
            birthdayError = "不能为空"
            valid = false
        } else {
            birthdayError = null
        }

        // 籍贯验证
        if (place.isBlank()) {
            placeError = "不能为空"
            valid = false
        } else {
            placeError = null
        }

        // 年限验证
        if (years < 3) {
            yearsError = "输入的数字不能大于用户所填生日对应的年龄"
            valid = false
        } else {
            yearsError = null
        }

        // 自我评价验证
        if (selfEvaluation < 4) {
            rateError = "分数过低会影响整体评价"
            valid = false
        } else {
            rateError = null
        }

        // 个人简介验证
        if (resume.isBlank()) {
            resumeError = "不能为空"
            valid = false
        } else {
            resumeError = null
        }

        return valid
    }

    // 重置函数
    fun reset() {
        username = ""
        password = ""
        gender = null
        birthday = ""
        place = ""
        years = 0
        selfEvaluation = 2f
        resume = ""

        usernameError = null
        passwordError = null
        genderError = null
        birthdayError = null
        placeError = null
        yearsError = null
        rateError = null
        resumeError = null
    }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础类型
        ExampleSection(
            title = "基础类型",
            description = "基础表单"
        ) {
            // 布局切换按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
            ) {
                // 水平排布按钮
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(shapes.circle)
                        .background(if (isHorizontal) colors.primaryLight else colors.surfaceVariant)
                        .clickable(enabled = !formDisabled) { isHorizontal = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "水平排布",
                        style = Typography.BodyMedium,
                        color = if (isHorizontal) colors.primary else colors.textPrimary
                    )
                }

                // 垂直排布按钮
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(shapes.circle)
                        .background(if (!isHorizontal) colors.primaryLight else colors.surfaceVariant)
                        .clickable(enabled = !formDisabled) { isHorizontal = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "竖直排布",
                        style = Typography.BodyMedium,
                        color = if (!isHorizontal) colors.primary else colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.spacer16.dp))

            // 禁用态开关
            Cell(
                title = "禁用态",
                trailing = {
                    Switch(
                        checked = formDisabled,
                        onCheckedChange = { formDisabled = it }
                    )
                }
            )

            Spacer(modifier = Modifier.height(Spacing.spacer16.dp))

            // 表单内容
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.default)
                    .background(colors.surface)
            ) {
                // 用户名
                FormItem(
                    label = "用户名",
                    required = true,
                    isHorizontal = isHorizontal,
                    error = usernameError,
                    help = "请输入用户名"
                ) {
                    Input(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "请输入用户名",
                        size = InputSize.MEDIUM,
                        disabled = formDisabled
                    )
                }

                FormDivider()

                // 密码
                FormItem(
                    label = "密码",
                    required = true,
                    isHorizontal = isHorizontal,
                    error = passwordError
                ) {
                    Input(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "请输入密码",
                        size = InputSize.MEDIUM,
                        disabled = formDisabled
                    )
                }

                FormDivider()

                // 性别
                FormItem(
                    label = "性别",
                    required = true,
                    isHorizontal = isHorizontal,
                    error = genderError
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        genderOptions.forEach { (label, value) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable(enabled = !formDisabled) {
                                    gender = value
                                }
                            ) {
                                RadioButton(
                                    selected = gender == value,
                                    onClick = { gender = value },
                                    enabled = !formDisabled
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = label,
                                    style = Typography.BodyMedium,
                                    color = if (!formDisabled) colors.textPrimary else colors.textDisabled
                                )
                            }
                        }
                    }
                }

                FormDivider()

                // 生日
                FormItem(
                    label = "生日",
                    required = true,
                    isHorizontal = isHorizontal,
                    error = birthdayError
                ) {
                    DatePickerInput(
                        value = birthday,
                        onValueChange = { birthday = it },
                        placeholder = "请选择日期",
                        enabled = !formDisabled
                    )
                }

                FormDivider()

                // 籍贯
                FormItem(
                    label = "籍贯",
                    required = true,
                    isHorizontal = isHorizontal,
                    error = placeError
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(shapes.small)
                            .border(1.dp, if (!formDisabled) colors.border else colors.disabled, shapes.small)
                            .background(if (!formDisabled) colors.surface else colors.disabledContainer)
                            .clickable(enabled = !formDisabled) { showPlacePicker = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = place.ifEmpty { "请选择籍贯" },
                            style = Typography.BodyMedium,
                            color = if (place.isNotEmpty()) {
                                if (!formDisabled) colors.textPrimary else colors.textDisabled
                            } else {
                                colors.textPlaceholder
                            }
                        )
                    }
                }

                FormDivider()

                // 年限
                FormItem(
                    label = "年限",
                    required = false,
                    isHorizontal = isHorizontal,
                    error = yearsError
                ) {
                    Stepper(
                        value = years,
                        onValueChange = { years = it },
                        min = 0,
                        max = 100,
                        enabled = !formDisabled,
                        size = StepperSize.MEDIUM
                    )
                }

                FormDivider()

                // 自我评价
                FormItem(
                    label = "自我评价",
                    required = false,
                    isHorizontal = isHorizontal,
                    error = rateError
                ) {
                    Rate(
                        value = selfEvaluation,
                        onValueChange = if (!formDisabled) { { selfEvaluation = it } } else null,
                        count = 5,
                        allowHalf = false,
                        readonly = formDisabled
                    )
                }

                FormDivider()

                // 个人简介
                FormItem(
                    label = "个人简介",
                    required = true,
                    isHorizontal = isHorizontal,
                    error = resumeError
                ) {
                    Textarea(
                        value = resume,
                        onValueChange = { resume = it },
                        placeholder = "请输入个人简介",
                        maxLength = 500,
                        indicator = true,
                        minLines = 3,
                        layout = if (isHorizontal) TextareaLayout.HORIZONTAL else TextareaLayout.VERTICAL,
                        enabled = !formDisabled,
                        readOnly = formDisabled
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.spacer16.dp))

            // 提交/重置按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)
            ) {
                Button(
                    text = "重置",
                    onClick = { reset() },
                    size = ButtonSize.LARGE,
                    theme = ButtonTheme.DEFAULT,
                    disabled = formDisabled,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    text = "提交",
                    onClick = { validate() },
                    size = ButtonSize.LARGE,
                    theme = ButtonTheme.PRIMARY,
                    disabled = formDisabled,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // 籍贯选择弹窗
    if (showPlacePicker) {
        PlacePickerDialog(
            options = placeOptions,
            selectedPlace = place,
            onSelect = {
                place = it
                showPlacePicker = false
            },
            onDismiss = { showPlacePicker = false }
        )
    }
}

/**
 * 表单项组件
 */
@Composable
private fun FormItem(
    label: String,
    required: Boolean,
    isHorizontal: Boolean,
    error: String? = null,
    help: String? = null,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors

    if (isHorizontal) {
        // 水平布局
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.spacer16.dp, vertical = Spacing.spacer12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // 标签
            Row(
                modifier = Modifier.width(82.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
                if (required) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "*",
                        style = Typography.BodyMedium,
                        color = colors.danger
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.spacer16.dp))

            // 内容
            Column(modifier = Modifier.weight(1f)) {
                content()

                // 帮助文字
                if (help != null && error == null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = help,
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // 错误提示
                if (error != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = error,
                        style = Typography.BodySmall,
                        color = colors.danger
                    )
                }
            }
        }
    } else {
        // 垂直布局
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.spacer16.dp, vertical = Spacing.spacer12.dp)
        ) {
            // 标签
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
                if (required) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "*",
                        style = Typography.BodyMedium,
                        color = colors.danger
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.spacer8.dp))

            // 内容
            content()

            // 帮助文字
            if (help != null && error == null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = help,
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }

            // 错误提示
            if (error != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = error,
                    style = Typography.BodySmall,
                    color = colors.danger
                )
            }
        }
    }
}

/**
 * 表单分割线
 */
@Composable
private fun FormDivider() {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.spacer16.dp)
            .height(1.dp)
            .background(colors.divider)
    )
}

/**
 * 籍贯选择弹窗
 */
@Composable
private fun PlacePickerDialog(
    options: List<String>,
    selectedPlace: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    // 简单的弹窗实现
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.mask)
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(shapes.large)
                .background(colors.surface)
                .clickable { /* 阻止点击穿透 */ }
                .padding(Spacing.spacer16.dp)
        ) {
            Text(
                text = "选择籍贯",
                style = Typography.TitleMedium,
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(Spacing.spacer16.dp))

            options.forEach { option ->
                val isSelected = option == selectedPlace
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shapes.small)
                        .background(if (isSelected) colors.primaryLight else colors.surface)
                        .clickable { onSelect(option) }
                        .padding(Spacing.spacer12.dp)
                ) {
                    Text(
                        text = option,
                        style = Typography.BodyMedium,
                        color = if (isSelected) colors.primary else colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.spacer16.dp))

            Button(
                text = "取消",
                onClick = onDismiss,
                size = ButtonSize.MEDIUM,
                theme = ButtonTheme.DEFAULT,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
