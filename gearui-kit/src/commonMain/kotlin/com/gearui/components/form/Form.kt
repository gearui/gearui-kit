package com.gearui.components.form

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography

import com.gearui.theme.Theme
import com.gearui.i18n.StringPacks
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing

/**
 * Form validation rule
 */
data class FormRule(
    val required: Boolean = false,
    val validator: ((String) -> Boolean)? = null,
    /** Left empty, this falls back to [FormMessages], injected by [rememberFormFieldState] for the current language. */
    val message: String = ""
)

/**
 * Validation happens inside [FormFieldState.validate], which is a plain class rather than a composable and
 * therefore cannot read [com.gearui.i18n.LocalStrings]. So the copy is injected at construction time; constructing
 * a [FormFieldState] directly falls back to the English pack (the library fallback language), not to a hardcoded literal.
 */
data class FormMessages(
    val validationFailed: String,
    val fieldRequired: String,
) {
    companion object {
        val Fallback: FormMessages = StringPacks.English.feedback.let {
            FormMessages(
                validationFailed = it.validationFailed,
                fieldRequired = it.fieldRequired,
            )
        }
    }
}

/** Creates a field state for the current locale; prefer this over constructing [FormFieldState] directly. */
@Composable
fun rememberFormFieldState(
    initialValue: String = "",
    rules: List<FormRule> = emptyList(),
): FormFieldState {
    val feedback = I18n.strings.feedback
    return remember(initialValue, rules, feedback) {
        FormFieldState(
            initialValue = initialValue,
            rules = rules,
            messages = FormMessages(
                validationFailed = feedback.validationFailed,
                fieldRequired = feedback.fieldRequired,
            ),
        )
    }
}

/**
 * Form field state
 */
class FormFieldState(
    initialValue: String = "",
    val rules: List<FormRule> = emptyList(),
    val messages: FormMessages = FormMessages.Fallback,
) {
    var value by mutableStateOf(initialValue)
    var error by mutableStateOf<String?>(null)
    var touched by mutableStateOf(false)

    fun validate(): Boolean {
        if (!touched) return true

        for (rule in rules) {
            // Required check
            if (rule.required && value.isBlank()) {
                error = rule.message.ifBlank { messages.fieldRequired }
                return false
            }

            // Custom validator
            rule.validator?.let { validator ->
                if (!validator(value)) {
                    error = rule.message.ifBlank { messages.validationFailed }
                    return false
                }
            }
        }

        error = null
        return true
    }

    fun touch() {
        touched = true
        validate()
    }

    fun reset() {
        value = ""
        error = null
        touched = false
    }
}

/**
 * Form state manager
 */
class FormState {
    private val fields = mutableStateMapOf<String, FormFieldState>()

    fun registerField(name: String, state: FormFieldState) {
        fields[name] = state
    }

    fun unregisterField(name: String) {
        fields.remove(name)
    }

    fun validate(): Boolean {
        var isValid = true
        fields.values.forEach { field ->
            field.touch()
            if (!field.validate()) {
                isValid = false
            }
        }
        return isValid
    }

    fun reset() {
        fields.values.forEach { it.reset() }
    }

    fun getValues(): Map<String, String> {
        return fields.mapValues { it.value.value }
    }
}

@Composable
fun rememberFormState(): FormState {
    return remember { FormState() }
}

/**
 * Form - Form container with validation
 *
 * Form container with validation rules
 *
 * Features:
 * - Field validation with rules
 * - Error display
 * - Required field marking
 * - Form state management
 *
 * Example:
 * ```
 * val formState = rememberFormState()
 *
 * Form(
 *     formState = formState
 * ) {
 *     FormItem(
 *         label = "Username",
 *         required = true
 *     ) {
 *         val fieldState = rememberFormFieldState(
 *             rules = listOf(FormRule(required = true, message = "Please enter a username"))
 *         )
 *         TextField(
 *             value = fieldState.value,
 *             onValueChange = { fieldState.value = it }
 *         )
 *     }
 * }
 * ```
 */
@Composable
fun Form(
    modifier: Modifier = Modifier,
    formState: FormState = rememberFormState(),
    labelWidth: Dp = 80.dp,
    scrollable: Boolean = false,
    content: @Composable FormScope.() -> Unit
) {
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    val scope = remember(formState, labelWidth) {
        FormScopeImpl(formState, labelWidth)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (scrollable) Modifier
                else Modifier
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            scope.content()
        }
    }
}

/**
 * FormScope - DSL scope for Form
 */
interface FormScope {
    val formState: FormState
    val labelWidth: Dp

    @Composable
    fun FormItem(
        label: String,
        name: String? = null,
        required: Boolean = false,
        help: String? = null,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    )
}

private class FormScopeImpl(
    override val formState: FormState,
    override val labelWidth: Dp
) : FormScope {

    @Composable
    override fun FormItem(
        label: String,
        name: String?,
        required: Boolean,
        help: String?,
        modifier: Modifier,
        content: @Composable () -> Unit
    ) {
        val colors = Theme.colors
        val typography = Theme.typography
        val shapes = Theme.shapes

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Label
                Row(
                    modifier = Modifier.width(labelWidth),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (required) {
                        Text(
                            text = "*",
                            style = Typography.BodyMedium,
                            color = colors.destructive
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                    }
                    Text(
                        text = label,
                        style = Typography.BodyMedium,
                        color = colors.foreground
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.lg))

                // Field content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    content()

                    // Help text
                    help?.let {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = it,
                            style = Typography.BodySmall,
                            color = colors.mutedForeground
                        )
                    }
                }
            }
        }
    }
}

/**
 * Remember form field state
 */
@Composable
fun rememberFormFieldState(
    initialValue: String = "",
    rules: List<FormRule> = emptyList(),
    name: String? = null,
    formState: FormState? = null
): FormFieldState {
    val state = remember(initialValue, rules) {
        FormFieldState(initialValue, rules)
    }

    // Auto register/unregister with form
    DisposableEffect(name, formState) {
        if (name != null && formState != null) {
            formState.registerField(name, state)
        }
        onDispose {
            if (name != null && formState != null) {
                formState.unregisterField(name)
            }
        }
    }

    return state
}

/**
 * Form field with built-in error display
 */
@Composable
fun FormField(
    fieldState: FormFieldState,
    modifier: Modifier = Modifier,
    content: @Composable (value: String, onValueChange: (String) -> Unit) -> Unit
) {
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    Column(modifier = modifier) {
        content(fieldState.value) { newValue ->
            fieldState.value = newValue
            if (fieldState.touched) {
                fieldState.validate()
            }
        }

        // Error message
        fieldState.error?.let { error ->
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = error,
                style = Typography.BodySmall,
                color = colors.destructive
            )
        }
    }
}
