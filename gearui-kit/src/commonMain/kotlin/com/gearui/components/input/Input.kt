package com.gearui.components.input

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.text.BasicTextField
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.focus.FocusRequester
import com.tencent.kuikly.compose.ui.focus.focusRequester
import com.tencent.kuikly.compose.ui.focus.onFocusChanged
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.input.PasswordVisualTransformation
import com.tencent.kuikly.compose.ui.text.input.VisualTransformation
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.interaction.*
import com.gearui.foundation.tokens.*
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography

/**
 * GearUI Input - 100% Theme 驱动
 *
 * ✅ 规则：第一行永远是 val colors = Theme.colors
 * ❌ 禁止：ColorTokens / 硬编码颜色
 *
 * 支持功能：
 * - 基础输入
 * - 标签（左侧/顶部）
 * - 必填标记
 * - 前缀/后缀
 * - 清除按钮
 * - 字数限制
 * - 密码模式
 * - 多行输入
 * - 卡片样式
 * - 文本对齐
 * - 状态（正常/错误/禁用/只读）
 */
@Composable
fun Input(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: InputSize = InputSize.MEDIUM,
    placeholder: String = "",
    label: String? = null,
    labelPosition: String = "left", // "left" or "top"
    required: Boolean = false,
    helperText: String? = null,
    errorText: String? = null,
    disabled: Boolean = false,
    readOnly: Boolean = false,
    maxLength: Int? = null,
    showCounter: Boolean = false,
    maxLines: Int = 1,
    isPassword: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    clearable: Boolean = false,
    onClear: (() -> Unit)? = null,
    cardStyle: Boolean = false,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    val interactionSource = remember { createMutableInteractionSource() }
    val inputFocusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    val hasError = errorText != null

    when {
        disabled -> interactionSource.updateState(InteractionState.Disabled)
        hasError && !isFocused -> interactionSource.updateState(InteractionState.Normal)
        isFocused -> interactionSource.updateState(InteractionState.Focused)
        else -> interactionSource.updateState(InteractionState.Normal)
    }

    val tokens = when (size) {
        InputSize.LARGE -> InputTokens.Large
        InputSize.MEDIUM -> InputTokens.Medium
        InputSize.SMALL -> InputTokens.Small
    }

    val shape = when (size) {
        InputSize.LARGE -> shapes.default
        InputSize.MEDIUM -> shapes.default
        InputSize.SMALL -> shapes.small
    }

    val borderColor = when {
        hasError -> colors.danger
        isFocused -> colors.primary
        !interactionSource.currentState.isInteractive -> colors.border
        else -> colors.border
    }

    // Keep border width stable to avoid layout jump when focus/error changes.
    val borderWidth = 1f

    val backgroundColor = when {
        disabled -> colors.disabledContainer
        cardStyle -> colors.surfaceVariant
        else -> colors.surface
    }

    // 输入框内容
    @Composable
    fun InputField() {
        val canFocus = !disabled && !readOnly
        var focusRequestTick by remember { mutableStateOf(0) }

        LaunchedEffect(focusRequestTick, canFocus) {
            if (focusRequestTick > 0 && canFocus) {
                inputFocusRequester.requestFocus()
            }
        }

        fun requestInputFocus() {
            if (canFocus) {
                focusRequestTick++
            }
        }

        val borderModifier = if (borderWidth > 0f) {
            Modifier.border(borderWidth.dp, borderColor, shape)
        } else {
            Modifier
        }

        val containerModifier = if (cardStyle) {
            Modifier
                .fillMaxWidth()
                .heightIn(min = tokens.height.dp)
                .clip(shape)
                .background(backgroundColor)
                .then(borderModifier)
        } else {
            Modifier
                .fillMaxWidth()
                .height(tokens.height.dp)
                .clip(shape)
                .background(backgroundColor)
                .then(borderModifier)
        }

        Box(
            modifier = containerModifier
                .clickable(enabled = canFocus) {
                    requestInputFocus()
                }
        ) {
            // Focus catcher: ensures any tappable area inside border can request focus,
            // including left label/text regions.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = canFocus) {
                        requestInputFocus()
                    }
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = tokens.paddingHorizontal.dp,
                        vertical = if (cardStyle) 12.dp else 0.dp
                    ),
                verticalAlignment = if (maxLines > 1) Alignment.Top else Alignment.CenterVertically
            ) {
                // 左侧标签（labelPosition == "left" 时）
                if (label != null && labelPosition == "left") {
                    Row {
                        if (required) {
                            Text(
                                text = "*",
                                style = Typography.BodyMedium,
                                color = colors.danger
                            )
                        }
                        Text(
                            text = label,
                            style = Typography.BodyMedium,
                            color = if (disabled) colors.textDisabled else colors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // 前缀
                if (prefix != null) {
                    prefix()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // 输入区域
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = when (textAlign) {
                        TextAlign.Center -> Alignment.Center
                        TextAlign.End -> Alignment.CenterEnd
                        else -> Alignment.CenterStart
                    }
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = { newValue ->
                            if (!readOnly && !disabled) {
                                if (maxLength == null || newValue.length <= maxLength) {
                                    onValueChange(newValue)
                                }
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = Typography.BodyMedium.fontSize,
                            fontWeight = Typography.BodyMedium.fontWeight,
                            color = if (disabled) colors.textDisabled else colors.textPrimary,
                            textAlign = textAlign
                        ),
                        cursorBrush = SolidColor(colors.primary),
                        singleLine = maxLines == 1,
                        maxLines = maxLines,
                        readOnly = readOnly,
                        enabled = !disabled,
                        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(inputFocusRequester)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                            },
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(enabled = canFocus) {
                                        requestInputFocus()
                                    },
                                contentAlignment = when (textAlign) {
                                    TextAlign.Center -> Alignment.Center
                                    TextAlign.End -> Alignment.CenterEnd
                                    else -> Alignment.CenterStart
                                }
                            ) {
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

                // 清除按钮
                if (clearable && value.isNotEmpty() && !disabled && !readOnly) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(shapes.circle)
                            .background(colors.surfaceVariant)
                            .clickable {
                                onClear?.invoke()
                                onValueChange("")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            name = Icons.close,
                            size = 12.dp,
                            tint = colors.textSecondary
                        )
                    }
                }

                // 后缀
                if (suffix != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    suffix()
                }

                // 字数统计
                if (showCounter && maxLength != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${value.length}/$maxLength",
                        style = Typography.BodySmall,
                        color = if (value.length >= maxLength) colors.danger else colors.textSecondary
                    )
                }
            }
        }
    }

    // 主布局
    Column(modifier = modifier) {
        // 顶部标签（labelPosition == "top" 时）
        if (label != null && labelPosition == "top") {
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                if (required) {
                    Text(
                        text = "*",
                        style = Typography.BodyMedium,
                        color = colors.danger
                    )
                }
                Text(
                    text = label,
                    style = Typography.BodyMedium,
                    color = if (disabled) colors.textDisabled else colors.textPrimary
                )
            }
        }

        InputField()

        // 底部提示文字
        val bottomText = errorText ?: helperText
        if (bottomText != null) {
            Text(
                text = bottomText,
                style = Typography.BodySmall,
                color = if (hasError) colors.danger else colors.textSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

enum class InputSize { LARGE, MEDIUM, SMALL }
