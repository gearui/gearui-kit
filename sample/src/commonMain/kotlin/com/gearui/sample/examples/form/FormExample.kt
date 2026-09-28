package com.gearui.sample.examples.form

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.cell.Cell
import com.gearui.components.form.Form
import com.gearui.components.form.FormScope
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
import com.gearui.foundation.field.FieldErrorText
import com.gearui.foundation.field.FieldLabel
import com.gearui.foundation.interaction.disabledAppearance
import com.gearui.foundation.layout.Spacing
import com.gearui.primitives.Divider
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection

/** Label column width for the horizontal layout: fits a four-character label plus the asterisk. */
private val FormLabelWidth = 80.dp

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
    // Form layout: horizontal / vertical
    var isHorizontal by remember { mutableStateOf(true) }

    // Form disabled state
    var formDisabled by remember { mutableStateOf(false) }

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
        usernameError = if (username.isBlank()) "输入不能为空" else null
        // Password: exactly 8 latin letters
        passwordError = if (!Regex("^[a-zA-Z]{8}$").matches(password)) "只能输入8个字符英文" else null
        genderError = if (gender == null) "不能为空" else null
        birthdayError = if (birthday.isBlank()) "不能为空" else null
        placeError = if (place == null) "不能为空" else null
        yearsError = if (years < 3) "工作年限不能少于 3 年" else null
        rateError = if (selfEvaluation < 4) "分数过低会影响整体评价" else null
        resumeError = if (resume.isBlank()) "不能为空" else null
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
        ExampleSection(
            title = "布局与状态",
            description = "切换下方表单的水平 / 竖直排布，或整体禁用"
        ) {
            SegmentedControl(
                options = listOf(true, false),
                selectedOption = isHorizontal,
                onOptionSelected = { isHorizontal = it },
                modifier = Modifier.fillMaxWidth(),
                enabled = !formDisabled,
                labelProvider = { if (it) "水平排布" else "竖直排布" },
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
            description = "点提交逐项校验，错误显示在字段下方"
        ) {
            Form(labelWidth = FormLabelWidth) {
                FormRow("用户名", required = true, isHorizontal = isHorizontal, error = usernameError) {
                    Input(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "请输入用户名",
                        size = InputSize.MEDIUM,
                        enabled = !formDisabled,
                        cardStyle = true
                    )
                }
                Divider()

                FormRow("密码", required = true, isHorizontal = isHorizontal, error = passwordError) {
                    Input(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "请输入密码",
                        size = InputSize.MEDIUM,
                        enabled = !formDisabled,
                        cardStyle = true
                    )
                }
                Divider()

                // Options stack vertically: three radios side by side do not fit next to the label column.
                FormRow("性别", required = true, isHorizontal = isHorizontal, error = genderError) {
                    RadioGroup(
                        options = GENDER_LABELS.keys.toList(),
                        selectedOption = gender,
                        onOptionSelected = { gender = it },
                        enabled = !formDisabled,
                        labelProvider = { GENDER_LABELS[it] ?: it }
                    )
                }
                Divider()

                FormRow("生日", required = true, isHorizontal = isHorizontal, error = birthdayError) {
                    DatePickerInput(
                        value = birthday,
                        onValueChange = { birthday = it },
                        placeholder = "请选择日期",
                        enabled = !formDisabled
                    )
                }
                Divider()

                FormRow("籍贯", required = true, isHorizontal = isHorizontal, error = placeError) {
                    Select(
                        value = place,
                        options = PLACE_OPTIONS,
                        onValueChange = { place = it },
                        placeholder = "请选择籍贯",
                        enabled = !formDisabled
                    )
                }
                Divider()

                FormRow("年限", required = false, isHorizontal = isHorizontal, error = yearsError) {
                    Stepper(
                        value = years,
                        onValueChange = { years = it },
                        min = 0,
                        max = 100,
                        enabled = !formDisabled,
                        size = StepperSize.MEDIUM
                    )
                }
                Divider()

                FormRow("自我评价", required = false, isHorizontal = isHorizontal, error = rateError) {
                    Rate(
                        modifier = Modifier.disabledAppearance(formDisabled),
                        value = selfEvaluation,
                        onValueChange = if (!formDisabled) { { selfEvaluation = it } } else null,
                        count = 5,
                        allowHalf = false,
                        readonly = formDisabled
                    )
                }
                Divider()

                FormRow("个人简介", required = true, isHorizontal = isHorizontal, error = resumeError) {
                    Textarea(
                        value = resume,
                        onValueChange = { resume = it },
                        placeholder = "请输入个人简介",
                        maxLength = 500,
                        indicator = true,
                        minLines = 3,
                        enabled = !formDisabled,
                        readOnly = formDisabled,
                        cardStyle = true
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

/**
 * One form row. Horizontal rows use the kit's [FormScope.FormItem]; it has no stacked
 * variant, so the vertical layout composes the same field parts (label, content, error)
 * in a column.
 */
@Composable
private fun FormScope.FormRow(
    label: String,
    required: Boolean,
    isHorizontal: Boolean,
    error: String?,
    content: @Composable () -> Unit
) {
    if (isHorizontal) {
        FormItem(label = label, required = required) {
            content()
            FieldErrorText(error)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            FieldLabel(text = label, required = required, invalid = error != null)
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
                FieldErrorText(error)
            }
        }
    }
}
