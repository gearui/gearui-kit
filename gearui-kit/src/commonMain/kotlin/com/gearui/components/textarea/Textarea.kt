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
import com.tencent.kuikly.compose.ui.focus.onFocusChanged
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.layout.Spacing
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.TextUnit
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.field.FieldErrorText
import com.gearui.foundation.field.fieldBorderColor

/**
 * Textarea layout direction
 */
enum class TextareaLayout {
    HORIZONTAL, // 水平布局（标签在左）
    VERTICAL    // 垂直布局（标签在上）
}

/**
 * Textarea - multi-line text input
 *
 * Key points:
 * 1. BasicTextField's minLines/maxLines let the field manage its own height
 * 2. No heightIn constraint on the outer Box, which would make it jump on wrap
 * 3. decorationBox holds the placeholder, on the same layer as the field
 */
@Composable
fun Textarea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    error: String? = null,
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

    // Outer container
    Column(modifier = modifier) {
        if (cardStyle) {
            // Card style
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.lg)
                    .background(colors.muted)
                    .padding(Spacing.lg)
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
                    error = error,
                    required = required,
                    additionInfo = additionInfo,
                    autosize = autosize
                )
            }
        } else {
            // Plain style
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
                error = error,
                required = required,
                additionInfo = additionInfo,
                autosize = autosize
            )
        }

        FieldErrorText(error)
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
    error: String? = null,
    required: Boolean,
    additionInfo: String?,
    autosize: Boolean
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    if (isVertical) {
        // Vertical layout
        Column {
            // Label row
            if (label != null) {
                LabelRow(
                    label = label,
                    labelIcon = labelIcon,
                    required = required,
                    enabled = enabled
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            }

            // Input area
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
                error = error,
                additionInfo = additionInfo,
                autosize = autosize
            )
        }
    } else {
        // Horizontal layout
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Label
                if (label != null) {
                    LabelRow(
                        label = label,
                        labelIcon = labelIcon,
                        required = required,
                        enabled = enabled,
                        modifier = Modifier.padding(end = Spacing.lg)
                    )
                }

                // Input area
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
                        error = error,
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
            Spacer(modifier = Modifier.width(Spacing.xs))
        }

        Text(
            text = label,
            style = Typography.BodyMedium,
            color = if (enabled) colors.foreground else colors.mutedForeground
        )

        if (required) {
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = "*",
                style = Typography.BodyMedium,
                color = colors.destructive
            )
        }
    }
}

/**
 *
 * Key implementation notes:
 * 1. BasicTextField controls the height through minLines/maxLines
 * 2. decorationBox holds the placeholder
 * 3. No outer Box with a fixed height
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
    error: String? = null,
    additionInfo: String?,
    autosize: Boolean,
    focusRequester: FocusRequester? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    verticalPadding: Dp = Spacing.sm,
    lineHeight: TextUnit = 24.sp,
    modifier: Modifier = Modifier,
) {
    val colors = Theme.colors
    val inputFocusRequester = focusRequester ?: remember { FocusRequester() }
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

    // maxLines = null (unbounded) when autosize == true,
    // otherwise maxLines = widget.maxLines ?: minLines
    val effectiveMaxLines = when {
        autosize && maxLines != null -> maxLines
        autosize -> Int.MAX_VALUE
        maxLines != null -> maxLines
        else -> minLines
    }

    Column(modifier = modifier) {
        // Field container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = canFocus) {
                    requestInputFocus()
                }
                .then(
                    if (bordered) {
                        Modifier
                            .clip(Theme.shapes.xl)
                            .border(
                                BorderWidth.thin,
                                fieldBorderColor(error = error, enabled = enabled),
                                Theme.shapes.xl,
                            )
                            .background(if (enabled && !readOnly) colors.surface else colors.muted)
                            .padding(Spacing.md)
                    } else {
                        Modifier
                            .clip(Theme.shapes.lg)
                            .background(colors.muted)
                            .padding(horizontal = 10.dp, vertical = verticalPadding)
                    }
                )
        ) {
            Column {
                val fontSize = 16.sp
                // The placeholder must use the SAME size/line-height as the body text: Typography.BodyMedium is
                // 14sp/22sp, a different line box, so empty and filled states would differ by a few dp and the field would jump on the first character.
                val inputTextStyle = TextStyle(
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                    color = if (enabled) colors.foreground else colors.mutedForeground,
                )
                // The same metrics, converted to the token types the kit Text needs.
                val placeholderTextStyle = com.gearui.foundation.typography.TextStyle(
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                    fontWeight = com.tencent.kuikly.compose.ui.text.font.FontWeight.Normal,
                )

                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (maxLength == null || newValue.length <= maxLength) {
                            onValueChange(newValue)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(inputFocusRequester)
                        .then(
                            if (onFocusChanged != null) {
                                Modifier.onFocusChanged { onFocusChanged(it.isFocused) }
                            } else Modifier
                        ),
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = inputTextStyle,
                    cursorBrush = SolidColor(colors.primary),
                    singleLine = false,
                    minLines = minLines,
                    maxLines = effectiveMaxLines,
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (value.isEmpty() && placeholder.isNotEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = placeholderTextStyle,
                                    color = colors.mutedForeground,
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Footer info row
                if (additionInfo != null || (indicator && maxLength != null)) {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (additionInfo != null) {
                            Text(
                                text = additionInfo,
                                style = Typography.BodySmall,
                                color = colors.mutedForeground
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        if (indicator && maxLength != null) {
                            Text(
                                text = "${value.length}/$maxLength",
                                style = Typography.BodySmall,
                                color = colors.mutedForeground
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * AutoResizeTextarea - self-sizing text area
 */
@Composable
fun AutoResizeTextarea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = "",
    maxLength: Int? = null,
    maxLines: Int? = null,
    autoFocus: Boolean = false,
    focusRequester: FocusRequester? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    /**
     * Vertical padding inside the field. A single line is `2 * verticalPadding + 24dp`,
     * so this is how a caller matches the field's collapsed height to the controls
     * beside it (4dp -> 32dp, the default 8dp -> 40dp).
     */
    verticalPadding: Dp = Spacing.sm,
    /**
     * Line box height. Kuikly has no `LineHeightStyle`, so whatever exceeds the font's
     * natural line is added *below* the baseline — a collapsed single line then sits
     * visibly high in the field. Callers that need a tight, vertically centred single
     * line pass a value close to the natural line height (~1.25 * font size).
     */
    lineHeight: TextUnit = 24.sp,
) {
    val inputFocusRequester = focusRequester ?: remember { FocusRequester() }

    if (autoFocus) {
        LaunchedEffect(Unit) {
            inputFocusRequester.requestFocus()
        }
    }

    TextareaInputArea(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = false,
        placeholder = placeholder,
        maxLength = maxLength,
        minLines = 1,
        maxLines = maxLines,
        indicator = false,
        bordered = false,
        additionInfo = null,
        autosize = true,
        focusRequester = inputFocusRequester,
        onFocusChanged = onFocusChanged,
        verticalPadding = verticalPadding,
        lineHeight = lineHeight,
        modifier = modifier,
    )
}
