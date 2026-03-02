package com.gearui.components.textarea

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.foundation.text.BasicTextField
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.focus.FocusRequester
import com.tencent.kuikly.compose.ui.focus.focusRequester
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography

/**
 * 文本框布局方式
 */
enum class TextareaLayout {
    HORIZONTAL, // 水平布局（标签在左）
    VERTICAL    // 垂直布局（标签在上）
}

/**
 * Textarea - 多行文本输入框
 *
 * 关键点：
 * 1. 使用 BasicTextField 的 minLines/maxLines 参数让输入框自己控制高度
 * 2. 不使用外层 Box 的 heightIn 约束，避免换行时的跳动
 * 3. decorationBox 用于放置占位符，与输入框同层
 */
@Composable
fun Textarea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    placeholder: String = "",
    label: String? = null,
    labelIcon: String? = null,
    maxLength: Int? = null,
    minLines: Int = 4,
    maxLines: Int? = null,
    indicator: Boolean = false,
    layout: TextareaLayout = TextareaLayout.HORIZONTAL,
    autosize: Boolean = false,
    bordered: Boolean = true,
    cardStyle: Boolean = false,
    required: Boolean = false,
    additionInfo: String? = null
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    val isVertical = layout == TextareaLayout.VERTICAL

    // 整体容器
    Column(modifier = modifier) {
        if (cardStyle) {
            // 卡片样式
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.large)
                    .background(colors.surfaceVariant)
                    .padding(16.dp)
            ) {
                TextareaContent(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    readOnly = readOnly,
                    placeholder = placeholder,
                    label = label,
                    labelIcon = labelIcon,
                    maxLength = maxLength,
                    minLines = minLines,
                    maxLines = maxLines,
                    indicator = indicator,
                    isVertical = isVertical,
                    bordered = false,
                    required = required,
                    additionInfo = additionInfo,
                    autosize = autosize
                )
            }
        } else {
            // 普通样式
            TextareaContent(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                readOnly = readOnly,
                placeholder = placeholder,
                label = label,
                labelIcon = labelIcon,
                maxLength = maxLength,
                minLines = minLines,
                maxLines = maxLines,
                indicator = indicator,
                isVertical = isVertical,
                bordered = bordered,
                required = required,
                additionInfo = additionInfo,
                autosize = autosize
            )
        }
    }
}

@Composable
private fun TextareaContent(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    readOnly: Boolean,
    placeholder: String,
    label: String?,
    labelIcon: String?,
    maxLength: Int?,
    minLines: Int,
    maxLines: Int?,
    indicator: Boolean,
    isVertical: Boolean,
    bordered: Boolean,
    required: Boolean,
    additionInfo: String?,
    autosize: Boolean
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    if (isVertical) {
        // 垂直布局
        Column {
            // 标签行
            if (label != null) {
                LabelRow(
                    label = label,
                    labelIcon = labelIcon,
                    required = required,
                    enabled = enabled
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 输入区域
            TextareaInputArea(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                readOnly = readOnly,
                placeholder = placeholder,
                maxLength = maxLength,
                minLines = minLines,
                maxLines = maxLines,
                indicator = indicator,
                bordered = bordered,
                additionInfo = additionInfo,
                autosize = autosize
            )
        }
    } else {
        // 水平布局
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // 标签
                if (label != null) {
                    LabelRow(
                        label = label,
                        labelIcon = labelIcon,
                        required = required,
                        enabled = enabled,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }

                // 输入区域
                Column(modifier = Modifier.weight(1f)) {
                    TextareaInputArea(
                        value = value,
                        onValueChange = onValueChange,
                        enabled = enabled,
                        readOnly = readOnly,
                        placeholder = placeholder,
                        maxLength = maxLength,
                        minLines = minLines,
                        maxLines = maxLines,
                        indicator = indicator,
                        bordered = bordered,
                        additionInfo = additionInfo,
                        autosize = autosize
                    )
                }
            }
        }
    }
}

@Composable
private fun LabelRow(
    label: String,
    labelIcon: String?,
    required: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (labelIcon != null) {
            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = label,
            style = Typography.BodyMedium,
            color = if (enabled) colors.textPrimary else colors.textDisabled
        )

        if (required) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "*",
                style = Typography.BodyMedium,
                color = colors.danger
            )
        }
    }
}

/**
 *
 * 关键实现：
 * 1. BasicTextField 使用 minLines/maxLines 控制高度
 * 2. decorationBox 放置占位符
 * 3. 不使用外层固定高度的 Box
 */
@Composable
private fun TextareaInputArea(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    readOnly: Boolean,
    placeholder: String,
    maxLength: Int?,
    minLines: Int,
    maxLines: Int?,
    indicator: Boolean,
    bordered: Boolean,
    additionInfo: String?,
    autosize: Boolean
) {
    val colors = Theme.colors
    val inputFocusRequester = remember { FocusRequester() }
    val canFocus = enabled && !readOnly
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

    // autosize == true 时，maxLines = null（无限制）
    // 否则 maxLines = widget.maxLines ?? minLines
    val effectiveMaxLines = when {
        autosize -> Int.MAX_VALUE
        maxLines != null -> maxLines
        else -> minLines
    }

    Column {
        // 输入框容器
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = canFocus) {
                    requestInputFocus()
                }
                .then(
                    if (bordered) {
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                            .background(if (enabled && !readOnly) colors.surface else colors.disabledContainer)
                            .padding(12.dp)
                    } else {
                        Modifier
                    }
                )
        ) {
            // Focus catcher: keep the whole bordered area focusable.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(enabled = canFocus) {
                        requestInputFocus()
                    }
            )

            Column {
                // 设置固定的 fontSize 和 lineHeight，确保空行和有文字的行高度一致
                // lineHeight = fontSize * 1.5 是常用的比例
                val fontSize = 16.sp
                val lineHeight = 24.sp

                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (maxLength == null || newValue.length <= maxLength) {
                            onValueChange(newValue)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(inputFocusRequester),
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = TextStyle(
                        fontSize = fontSize,
                        lineHeight = lineHeight,
                        color = if (enabled) colors.textPrimary else colors.textDisabled
                    ),
                    cursorBrush = SolidColor(colors.primary),
                    singleLine = false,
                    minLines = minLines,
                    maxLines = effectiveMaxLines,
                    decorationBox = { innerTextField ->
                        // 占位符和输入框放在同一个 Box 中
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = canFocus) {
                                    requestInputFocus()
                                }
                        ) {
                            if (value.isEmpty() && placeholder.isNotEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = Typography.BodyMedium,
                                    color = if (readOnly) colors.textDisabled else colors.textPlaceholder
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // 底部信息行
                if (additionInfo != null || (indicator && maxLength != null)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (additionInfo != null) {
                            Text(
                                text = additionInfo,
                                style = Typography.BodySmall,
                                color = colors.textSecondary
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        if (indicator && maxLength != null) {
                            Text(
                                text = "${value.length}/$maxLength",
                                style = Typography.BodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * AutoResizeTextarea - 自动调整高度的文本框
 */
@Composable
fun AutoResizeTextarea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = "",
    maxLength: Int? = null
) {
    Textarea(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        maxLength = maxLength,
        indicator = maxLength != null,
        minLines = 1,
        autosize = true,
        modifier = modifier
    )
}
