package com.gearui.foundation.primitives

import androidx.compose.runtime.Composable
import com.gearui.foundation.typography.Typography
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
import com.tencent.kuikly.compose.ui.text.input.VisualTransformation

/**
 * GearUI 无样式输入原语。
 *
 * - 不附带边框、背景、内边距等视觉样式
 * - 提供主题默认文本样式与光标色
 * - 可选 placeholder
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
        fontSize = Typography.BodyMedium.fontSize,
        fontWeight = Typography.BodyMedium.fontWeight
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val colors = Theme.colors
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val effectiveModifier = if (focusRequester != null) {
        modifier.focusRequester(focusRequester)
    } else {
        modifier
    }
    val effectiveTextStyle = textStyle.copy(
        color = when {
            !enabled -> colors.textDisabled
            textStyle.color != Color.Unspecified -> textStyle.color
            else -> colors.textPrimary
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
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
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
                        style = Typography.BodyMedium,
                        color = colors.textPlaceholder
                    )
                }
                innerTextField()
            }
        }
    )
}
