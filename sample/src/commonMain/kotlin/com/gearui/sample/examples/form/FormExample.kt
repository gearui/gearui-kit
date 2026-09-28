package com.gearui.sample.examples.form

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.cell.Cell
import com.gearui.components.form.Form
import com.gearui.components.form.FormItem
import com.gearui.components.form.FormLayout
import com.gearui.components.input.Input
import com.gearui.components.input.InputSize
import com.gearui.components.picker.DatePickerInput
import com.gearui.components.radio.RadioGroup
import com.gearui.components.rate.Rate
import com.gearui.components.segmented.SegmentedControl
import com.gearui.components.select.Select
import com.gearui.components.select.SelectOption
import com.gearui.components.stepper.Stepper
import com.gearui.components.stepper.StepperSize
import com.gearui.components.switch.Switch
import com.gearui.components.textarea.Textarea
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.interaction.disabledAppearance
import com.gearui.foundation.layout.Spacing
import com.gearui.primitives.Divider
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection

private val GENDER_LABELS = mapOf("0" to "男", "1" to "女", "2" to "保密")

private val PLACE_OPTIONS = listOf(
    "北京市/北京市/东城区",
    "北京市/北京市/西城区",
    "北京市/北京市/朝阳区",
    "天津市/天津市/和平区",
    "天津市/天津市/河东区"
).map { SelectOption(value = it, label = it) }

/**
 * Form component examples
 */
@Composable
fun FormExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    // Label above the control is the mobile default.
    var layout by remember { mutableStateOf(FormLayout.VERTICAL) }
    var formDisabled by remember { mutableStateOf(false) }
    val enabled = !formDisabled

    // Form data
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf<String?>(null) }
    var birthday by remember { mutableStateOf("") }
    var place by remember { mutableStateOf<String?>(null) }
    var years by remember { mutableStateOf(2) }
    var selfEvaluation by remember { mutableStateOf(2f) }
    var resume by remember { mutableStateOf("") }

    // Validation errors
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var genderError by remember { mutableStateOf<String?>(null) }
    var birthdayError by remember { mutableStateOf<String?>(null) }
    var placeError by remember { mutableStateOf<String?>(null) }
    var yearsError by remember { mutableStateOf<String?>(null) }
    var rateError by remember { mutableStateOf<String?>(null) }
    var resumeError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        usernameError = if (username.isBlank()) "请输入用户名" else null
        // Password: exactly 8 latin letters
        passwordError = if (!Regex("^[a-zA-Z]{8}$").matches(password)) "密码须为 8 位英文字母" else null
        genderError = if (gender == null) "请选择性别" else null
        birthdayError = if (birthday.isBlank()) "请选择生日" else null
        placeError = if (place == null) "请选择籍贯" else null
        yearsError = if (years < 3) "工作年限不能少于 3 年" else null
        rateError = if (selfEvaluation < 4) "分数过低会影响整体评价" else null
        resumeError = if (resume.isBlank()) "请输入个人简介" else null
        return listOf(
            usernameError, passwordError, genderError, birthdayError,
            placeError, yearsError, rateError, resumeError
        ).all { it == null }
    }

    fun reset() {
        username = ""
        password = ""
        gender = null
        birthday = ""
        place = null
        years = 2
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
        ExampleSection(
            title = "布局与状态",
            description = "一个参数切换整张表单的标签位置：竖直（标签在上）或水平（标签在左）；也可整体禁用"
        ) {
            SegmentedControl(
                options = listOf(FormLayout.VERTICAL, FormLayout.HORIZONTAL),
                selectedOption = layout,
                onOptionSelected = { layout = it },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                labelProvider = { if (it == FormLayout.VERTICAL) "竖直排布" else "水平排布" },
            )
            Cell(
                title = "禁用态",
                trailing = {
                    Switch(
                        checked = formDisabled,
                        onCheckedChange = { formDisabled = it }
                    )
                }
            )
        }

        ExampleSection(
            title = "表单校验",
            description = "点提交逐项校验，错误显示在字段下方，标签同时标红"
        ) {
            Form(layout = layout) {
                FormItem(
                    label = "用户名",
                    required = true,
                    error = usernameError,
                    description = "2-16 个字符，注册后不可修改",
                    enabled = enabled
                ) {
                    Input(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "请输入用户名",
                        size = InputSize.MEDIUM,
                        enabled = enabled,
                        error = usernameError,
                        variant = FieldVariant.SECONDARY
                    )
                }
                Divider()

                FormItem(label = "密码", required = true, error = passwordError, enabled = enabled) {
                    Input(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "请输入密码",
                        size = InputSize.MEDIUM,
                        enabled = enabled,
                        error = passwordError,
                        variant = FieldVariant.SECONDARY
                    )
                }
                Divider()

                // Options stack vertically so they also fit beside the label column.
                FormItem(label = "性别", required = true, error = genderError, enabled = enabled) {
                    RadioGroup(
                        options = GENDER_LABELS.keys.toList(),
                        selectedOption = gender,
                        onOptionSelected = { gender = it },
                        enabled = enabled,
                        labelProvider = { GENDER_LABELS[it] ?: it }
                    )
                }
                Divider()

                FormItem(label = "生日", required = true, error = birthdayError, enabled = enabled) {
                    DatePickerInput(
                        value = birthday,
                        onValueChange = { birthday = it },
                        placeholder = "请选择日期",
                        enabled = enabled,
                        error = birthdayError,
                        variant = FieldVariant.SECONDARY
                    )
                }
                Divider()

                FormItem(label = "籍贯", required = true, error = placeError, enabled = enabled) {
                    Select(
                        value = place,
                        options = PLACE_OPTIONS,
                        onValueChange = { place = it },
                        placeholder = "请选择籍贯",
                        enabled = enabled,
                        error = placeError,
                        variant = FieldVariant.SECONDARY
                    )
                }
                Divider()

                FormItem(label = "年限", error = yearsError, enabled = enabled) {
                    Stepper(
                        value = years,
                        onValueChange = { years = it },
                        min = 0,
                        max = 100,
                        enabled = enabled,
                        size = StepperSize.MEDIUM
                    )
                }
                Divider()

                FormItem(
                    label = "自我评价",
                    error = rateError,
                    description = "4 星及以上视为合格",
                    enabled = enabled
                ) {
                    Rate(
                        modifier = Modifier.disabledAppearance(formDisabled),
                        value = selfEvaluation,
                        onValueChange = if (enabled) { { selfEvaluation = it } } else null,
                        count = 5,
                        allowHalf = false,
                        readonly = formDisabled
                    )
                }
                Divider()

                FormItem(label = "个人简介", required = true, error = resumeError, enabled = enabled) {
                    Textarea(
                        value = resume,
                        onValueChange = { resume = it },
                        placeholder = "请输入个人简介",
                        maxLength = 500,
                        indicator = true,
                        minLines = 3,
                        enabled = enabled,
                        readOnly = formDisabled,
                        error = resumeError,
                        variant = FieldVariant.SECONDARY
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
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
}
