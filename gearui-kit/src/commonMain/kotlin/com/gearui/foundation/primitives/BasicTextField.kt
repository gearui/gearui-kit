package com.gearui.foundation.primitives

import androidx.compose.runtime.Composable
import com.gearui.foundation.field.fieldName
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.text.KeyboardActions
import com.tencent.kuikly.compose.foundation.text.KeyboardOptions
import com.tencent.kuikly.compose.foundation.text.BasicTextField as KuiklyBasicTextField
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.focus.FocusRequester
import com.tencent.kuikly.compose.ui.focus.focusRequester
import com.tencent.kuikly.compose.ui.graphics.Brush
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.compose.ui.platform.LocalSoftwareKeyboardController
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.input.ImeAction
import com.tencent.kuikly.compose.ui.text.input.KeyboardType
import com.gearui.components.input.numericKeyboardType
import com.tencent.kuikly.compose.ui.text.input.VisualTransformation

/**
 * GearUI unstyled input primitive.
 *
 * - carries no border, background or padding
 * - supplies the theme default text style and cursor colour
 * - optional placeholder
 * - [accessibilityLabel] names the field for screen readers; defaults to [placeholder]
 */
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    imeAction: ImeAction = if (singleLine) ImeAction.Done else ImeAction.Default,
    blurOnImeDone: Boolean = singleLine,
    onImeDone: (() -> Unit)? = null,
    focusRequester: FocusRequester? = null,
    cursorBrush: Brush? = null,
    decorationBox: (@Composable (innerTextField: @Composable () -> Unit) -> Unit)? = null,
    textStyle: TextStyle = TextStyle(
        fontSize = Theme.typography.bodyMedium.fontSize,
        fontWeight = Theme.typography.bodyMedium.fontWeight
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    accessibilityLabel: String? = null,
    /** [KeyboardType.Number] asks for the digit pad (a text input on the Web). */
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val colors = Theme.colors
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val named = modifier.fieldName(accessibilityLabel ?: placeholder)
    val effectiveModifier = if (focusRequester != null) named.focusRequester(focusRequester) else named
    val effectiveTextStyle = textStyle.copy(
        color = when {
            !enabled -> colors.mutedForeground
            textStyle.color != Color.Unspecified -> textStyle.color
            else -> colors.foreground
        }
    )

    KuiklyBasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = effectiveModifier,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (keyboardType == KeyboardType.Number) numericKeyboardType() else keyboardType,
            imeAction = imeAction,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (blurOnImeDone) {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                }
                onImeDone?.invoke()
            }
        ),
        textStyle = effectiveTextStyle,
        cursorBrush = cursorBrush ?: SolidColor(colors.primary),
        visualTransformation = visualTransformation,
        decorationBox = decorationBox ?: { innerTextField ->
            Box {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = Theme.typography.bodyMedium,
                        color = colors.mutedForeground
                    )
                }
                innerTextField()
            }
        }
    )
}
