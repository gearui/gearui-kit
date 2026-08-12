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
import com.gearui.foundation.layout.Spacing
import com.gearui.theme.Theme
import com.gearui.overlay.OverlayDefaults
import com.tencent.kuikly.compose.foundation.shape.CircleShape

/**
 * Form component examples
 */
@Composable
fun FormExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    // Form layout: horizontal / vertical
    var isHorizontal by remember { mutableStateOf(true) }

    // Form disabled state
    var formDisabled by remember { mutableStateOf(false) }

    // Form data
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf<String?>(null) }
    var birthday by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
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

    // Gender options
    val genderOptions = listOf(
        "男" to "0",
        "女" to "1",
        "保密" to "2"
    )

    // Place of origin options (shortened)
    val placeOptions = listOf(
        "北京市/北京市/东城区",
        "北京市/北京市/西城区",
        "北京市/北京市/朝阳区",
        "天津市/天津市/和平区",
        "天津市/天津市/河东区"
    )
    var showPlacePicker by remember { mutableStateOf(false) }

    // Validation
    fun validate(): Boolean {
        var valid = true

        // Username
        if (username.isBlank()) {
            usernameError = "输入不能为空"
            valid = false
        } else {
            usernameError = null
        }

        // Password - exactly 8 latin characters
        val passwordRegex = Regex("^[a-zA-Z]{8}$")
        if (!passwordRegex.matches(password)) {
            passwordError = "只能输入8个字符英文"
            valid = false
        } else {
            passwordError = null
        }

        // Gender
        if (gender == null) {
            genderError = "不能为空"
            valid = false
        } else {
            genderError = null
        }

        // Date of birth
        if (birthday.isBlank()) {
            birthdayError = "不能为空"
            valid = false
        } else {
            birthdayError = null
        }

        // Place of origin
        if (place.isBlank()) {
            placeError = "不能为空"
            valid = false
        } else {
            placeError = null
        }

        // Years of experience
        if (years < 3) {
            yearsError = "输入的数字不能大于用户所填生日对应的年龄"
            valid = false
        } else {
            yearsError = null
        }

        // Self assessment
        if (selfEvaluation < 4) {
            rateError = "分数过低会影响整体评价"
            valid = false
        } else {
            rateError = null
        }

        // Bio
        if (resume.isBlank()) {
            resumeError = "不能为空"
            valid = false
        } else {
            resumeError = null
        }

        return valid
    }

    // Reset
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
        // Basic types
        ExampleSection(
            title = "基础类型",
            description = "基础表单"
        ) {
            // Layout toggle buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                // Horizontal layout button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(CircleShape)
                        .background(if (isHorizontal) colors.muted else colors.muted)
                        .clickable(enabled = !formDisabled) { isHorizontal = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "水平排布",
                        style = Typography.BodyMedium,
                        color = if (isHorizontal) colors.primary else colors.foreground
                    )
                }

                // Vertical layout button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(CircleShape)
                        .background(if (!isHorizontal) colors.muted else colors.muted)
                        .clickable(enabled = !formDisabled) { isHorizontal = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "竖直排布",
                        style = Typography.BodyMedium,
                        color = if (!isHorizontal) colors.primary else colors.foreground
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Disabled toggle
            Cell(
                title = "禁用态",
                trailing = {
                    Switch(
                        checked = formDisabled,
                        onCheckedChange = { formDisabled = it }
                    )
                }
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Form content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.md)
                    .background(colors.surface)
            ) {
                // Username
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
                        enabled = !formDisabled
                    )
                }

                FormDivider()

                // Password
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
                        enabled = !formDisabled
                    )
                }

                FormDivider()

                // Gender
                FormItem(
                    label = "性别",
                    required = true,
                    isHorizontal = isHorizontal,
                    error = genderError
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
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
                                    color = if (!formDisabled) colors.foreground else colors.mutedForeground
                                )
                            }
                        }
                    }
                }

                FormDivider()

                // Date of birth
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

                // Place of origin
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
                            .clip(shapes.sm)
                            .border(1.dp, if (!formDisabled) colors.border else colors.mutedForeground, shapes.sm)
                            .background(if (!formDisabled) colors.surface else colors.muted)
                            .clickable(enabled = !formDisabled) { showPlacePicker = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = place.ifEmpty { "请选择籍贯" },
                            style = Typography.BodyMedium,
                            color = if (place.isNotEmpty()) {
                                if (!formDisabled) colors.foreground else colors.mutedForeground
                            } else {
                                colors.mutedForeground
                            }
                        )
                    }
                }

                FormDivider()

                // Years of experience
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

                // Self assessment
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

                // Bio
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

            Spacer(modifier = Modifier.height(Spacing.lg))

                // Submit / reset buttons
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

    // Place of origin picker dialog
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
 * Form field component
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
        // Horizontal layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalAlignment = Alignment.Top
        ) {
            // Label
            Row(
                modifier = Modifier.width(82.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = Typography.BodyMedium,
                    color = colors.foreground
                )
                if (required) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "*",
                        style = Typography.BodyMedium,
                        color = colors.destructive
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.lg))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                content()

                // Helper text
                if (help != null && error == null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = help,
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                }

                // Error message
                if (error != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = error,
                        style = Typography.BodySmall,
                        color = colors.destructive
                    )
                }
            }
        }
    } else {
        // Vertical layout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md)
        ) {
            // Label
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = Typography.BodyMedium,
                    color = colors.foreground
                )
                if (required) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "*",
                        style = Typography.BodyMedium,
                        color = colors.destructive
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Content
            content()

            // Helper text
            if (help != null && error == null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = help,
                    style = Typography.BodySmall,
                    color = colors.mutedForeground
                )
            }

            // Error message
            if (error != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = error,
                    style = Typography.BodySmall,
                    color = colors.destructive
                )
            }
        }
    }
}

/**
 * Form divider
 */
@Composable
private fun FormDivider() {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.lg)
            .height(1.dp)
            .background(colors.border)
    )
}

/**
 * Place of origin picker dialog
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

    // A simple dialog implementation
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OverlayDefaults.scrimColor)
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(shapes.lg)
                .background(colors.surface)
                .clickable { /* 阻止点击穿透 */ }
                .padding(Spacing.lg)
        ) {
            Text(
                text = "选择籍贯",
                style = Typography.TitleMedium,
                color = colors.foreground
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            options.forEach { option ->
                val isSelected = option == selectedPlace
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shapes.sm)
                        .background(if (isSelected) colors.muted else colors.surface)
                        .clickable { onSelect(option) }
                        .padding(Spacing.md)
                ) {
                    Text(
                        text = option,
                        style = Typography.BodyMedium,
                        color = if (isSelected) colors.primary else colors.foreground
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

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
