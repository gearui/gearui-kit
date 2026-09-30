package com.gearui.foundation.field

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.text.font.FontWeight

/** A composing field can own the one error line without removing the child's invalid appearance. */
internal val LocalFieldErrorOwned = compositionLocalOf { false }

internal fun fieldSupportingText(error: String?, helper: String?, parentOwnsError: Boolean): String? =
    if (error != null && parentOwnsError) null else error ?: helper

/**
 * The text parts of a field, after HeroUI Native `Label`, `Description` and `FieldError`.
 *
 * A field is a stack: label, control, then description or error. Every field-like
 * component (Input, Textarea, Form items, and custom compositions) renders its text
 * through these three so the stack reads the same everywhere:
 *
 * ```kotlin
 * Column(verticalArrangement = Arrangement.spacedBy(FieldDefaults.labelGap)) {
 *     FieldLabel("Email", required = true, invalid = error != null)
 *     Input(value, onValueChange)
 *     if (error != null) FieldErrorText(error) else FieldDescription("We never share it.")
 * }
 * ```
 */

/**
 * Field label: base size, medium weight, foreground. [invalid] turns it danger; [required]
 * appends a danger asterisk (muted when disabled); [enabled] false dims the whole label to
 * the disabled opacity, as the reference does. Pass `enabled = true` when the enclosing
 * field already applies the disabled opacity to itself.
 */
@Composable
fun FieldLabel(
    text: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    invalid: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = Theme.colors
    val style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
    Row(
        modifier = modifier.alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = style, color = if (invalid) colors.destructiveSoftForeground else colors.foreground)
        if (required) {
            Text(
                text = " *",
                style = style,
                color = if (enabled) colors.destructiveSoftForeground else colors.mutedForeground,
            )
        }
    }
}

/**
 * Supporting text under a field: small, muted. [invalid] switches it to danger for
 * fields that reuse the description line for their message.
 */
@Composable
fun FieldDescription(
    text: String,
    modifier: Modifier = Modifier,
    invalid: Boolean = false,
) {
    Text(
        text = text,
        style = Theme.typography.bodySmall,
        color = if (invalid) Theme.colors.destructiveSoftForeground else Theme.colors.mutedForeground,
        modifier = modifier,
    )
}
