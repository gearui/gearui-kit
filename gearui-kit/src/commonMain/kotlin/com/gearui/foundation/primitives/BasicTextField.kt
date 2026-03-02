package com.gearui.foundation.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.text.BasicTextField as KuiklyBasicTextField
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.focus.FocusRequester
import com.tencent.kuikly.compose.ui.focus.focusRequester
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.text.TextStyle
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
    textStyle: TextStyle = TextStyle(
        fontSize = Typography.BodyMedium.fontSize,
        fontWeight = Typography.BodyMedium.fontWeight
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val colors = Theme.colors
    val focusRequester = remember { FocusRequester() }

    KuiklyBasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.focusRequester(focusRequester),
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        textStyle = textStyle.copy(
            color = if (enabled) colors.textPrimary else colors.textDisabled
        ),
        cursorBrush = SolidColor(colors.primary),
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
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
