package com.gearui.components.form

import androidx.compose.runtime.*
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldDescription
import com.gearui.foundation.field.FieldErrorText
import com.gearui.foundation.field.FieldLabel
import com.gearui.foundation.interaction.disabledAppearance
import com.gearui.foundation.layout.Spacing
import com.gearui.i18n.I18n
import com.gearui.i18n.StringPacks
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.Dp

/**
 * Label column width for [FormLayout.HORIZONTAL]: fits a four-character CJK label plus the
 * required asterisk. Kept as a single named value until it moves into the token set.
 */
private val FormHorizontalLabelWidth: Dp = ControlGeometry.formLabelWidth

/**
 * Where a [FormItem] puts its label relative to the control.
 */
enum class FormLayout {
    /** Label above the control, description or error below. Mobile default (HeroUI Native TextField anatomy). */
    VERTICAL,

    /** Label in a fixed-width column beside the control; description or error below the control. */
    HORIZONTAL,
}

private val LocalFormLayout = staticCompositionLocalOf { FormLayout.VERTICAL }
private val LocalFormLabelWidth = staticCompositionLocalOf { FormHorizontalLabelWidth }

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

/**
 * Form field state. Bind it to a control and a [FormItem]:
 *
 * ```kotlin
 * FormItem(label = "Email", required = true, error = state.error) {
 *     Input(value = state.value, onValueChange = state::update, error = state.error)
 * }
 * ```
 */
class FormFieldState(
    private val initialValue: String = "",
    val rules: List<FormRule> = emptyList(),
    val messages: FormMessages = FormMessages.Fallback,
) {
    var value by mutableStateOf(initialValue)
    var error by mutableStateOf<String?>(null)
    var touched by mutableStateOf(false)

    /** Sets the value and re-validates once the field has been touched. */
    fun update(newValue: String) {
        value = newValue
        if (touched) validate()
    }

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
        value = initialValue
        error = null
        touched = false
    }
}

/**
 * Validates and resets a group of [FormFieldState]s together. Fields join it through
 * [rememberFormFieldState] with a `name` and this state.
 */
class FormState {
    private val fields = mutableStateMapOf<String, FormFieldState>()

    fun registerField(name: String, state: FormFieldState) {
        fields[name] = state
    }

    fun unregisterField(name: String) {
        fields.remove(name)
    }

    /** Touches and validates every field; true when all pass. */
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
 * Creates a field state carrying the current locale's validation messages. When both [name]
 * and [formState] are given, the field registers with [formState] while it is in composition.
 */
@Composable
fun rememberFormFieldState(
    initialValue: String = "",
    rules: List<FormRule> = emptyList(),
    name: String? = null,
    formState: FormState? = null,
): FormFieldState {
    val feedback = I18n.strings.feedback
    val state = remember(initialValue, rules, feedback) {
        FormFieldState(
            initialValue = initialValue,
            rules = rules,
            messages = FormMessages(
                validationFailed = feedback.validationFailed,
                fieldRequired = feedback.fieldRequired,
            ),
        )
    }

    DisposableEffect(name, formState, state) {
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
 * Form - vertical stack of [FormItem]s that share one layout.
 *
 * [layout] and [labelWidth] become the defaults of every [FormItem] inside, so a whole
 * form switches between label-above and label-beside with one parameter. The form does
 * not scroll by itself; place it in the page's scroll container.
 *
 * ```kotlin
 * Form(layout = FormLayout.HORIZONTAL) {
 *     FormItem(label = "Username", required = true, error = usernameError) {
 *         Input(value = username, onValueChange = { username = it }, error = usernameError)
 *     }
 * }
 * ```
 */
@Composable
fun Form(
    modifier: Modifier = Modifier,
    layout: FormLayout = FormLayout.VERTICAL,
    labelWidth: Dp = FormHorizontalLabelWidth,
    content: @Composable ColumnScope.() -> Unit,
) {
    CompositionLocalProvider(
        LocalFormLayout provides layout,
        LocalFormLabelWidth provides labelWidth,
    ) {
        Column(modifier = modifier.fillMaxWidth(), content = content)
    }
}

/**
 * FormItem - one labelled field: label, control, then description or error.
 *
 * - [layout] defaults to the enclosing [Form]'s layout ([FormLayout.VERTICAL] outside a Form).
 * - [error] non-null marks the label invalid and replaces [description] with the error text,
 *   below the control in both layouts. Pass the same message to the control's own `error`
 *   so its border turns red too.
 * - [required] appends the required marker to the label.
 * - [enabled] false dims the label and the supporting text to the disabled opacity. The
 *   control is not touched: pass `enabled = false` to it as well, it dims itself.
 */
@Composable
fun FormItem(
    label: String,
    modifier: Modifier = Modifier,
    layout: FormLayout = LocalFormLayout.current,
    required: Boolean = false,
    error: String? = null,
    description: String? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val invalid = error != null
    when (layout) {
        FormLayout.VERTICAL -> Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(FieldDefaults.labelGap),
        ) {
            FieldLabel(text = label, required = required, invalid = invalid, enabled = enabled)
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
                FormItemSupportingText(error = error, description = description, enabled = enabled)
            }
        }

        FormLayout.HORIZONTAL -> Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md),
            verticalAlignment = Alignment.Top,
        ) {
            // The label is centred on a regular control's height so it lines up with
            // single-line controls while staying at the top of taller ones.
            Box(
                modifier = Modifier
                    .width(LocalFormLabelWidth.current)
                    .heightIn(min = ControlGeometry.controlMedium),
                contentAlignment = Alignment.CenterStart,
            ) {
                FieldLabel(text = label, required = required, invalid = invalid, enabled = enabled)
            }

            Spacer(modifier = Modifier.width(Spacing.lg))

            Column(modifier = Modifier.weight(1f)) {
                content()
                FormItemSupportingText(error = error, description = description, enabled = enabled)
            }
        }
    }
}

/** Error when present, otherwise the description; nothing when both are null. */
@Composable
private fun FormItemSupportingText(error: String?, description: String?, enabled: Boolean) {
    if (error == null && description == null) return
    Box(modifier = Modifier.disabledAppearance(!enabled)) {
        if (error != null) {
            FieldErrorText(error)
        } else if (description != null) {
            FieldDescription(text = description, modifier = Modifier.padding(top = Spacing.xs))
        }
    }
}
