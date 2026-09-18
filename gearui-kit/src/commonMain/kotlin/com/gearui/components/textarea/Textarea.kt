package com.gearui.components.textarea
import com.gearui.foundation.typography.resolveFontFamily

import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.gearui.foundation.motion.FeedbackDefaults

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.foundation.text.BasicTextField
import com.tencent.kuikly.compose.foundation.text.maxLength
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
import com.gearui.foundation.keyboard.keyboardDismissExempt
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.TextUnit
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.field.FieldErrorText
import com.gearui.foundation.field.fieldBorderColor
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.field.FieldFocusOverlay
import com.gearui.foundation.field.rememberInputFeedback
import com.gearui.foundation.control.ControlGeometry
import com.gearui.theme.LocalInputColors
import com.tencent.kuikly.compose.foundation.hoverable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsHoveredAsState

/**
 * Textarea layout direction
 */
enum class TextareaLayout {
    HORIZONTAL, // Explicit inline-label variant.
    VERTICAL    // Default: label above the editing surface.
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
    layout: TextareaLayout = TextareaLayout.VERTICAL,
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
                Spacer(modifier = Modifier.height(ControlGeometry.fieldLabelGap))
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
            style = Theme.typography.bodyMedium,
            color = if (enabled) colors.foreground else colors.mutedForeground
        )

        if (required) {
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = "*",
                style = Theme.typography.bodyMedium,
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
    /**
     * Draw a hairline around the **compact** (`bordered = false`) field.
     *
     * `bordered = true` is a different thing: it switches the whole field to the
     * standalone form-control look — surface background and uniform [Spacing.md]
     * padding, which fixes a single line near 48dp. A field that has to sit flush
     * with 32dp controls beside it cannot use that, but it may still need an
     * outline to read as an input rather than as a patch of background.
     */
    outlined: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = Theme.colors
    val inputFocusRequester = focusRequester ?: remember { FocusRequester() }
    val canFocus = enabled && !readOnly
    val inputColors = LocalInputColors.current
    val focusedState = remember { mutableStateOf(false) }
    val hoverSource = remember { MutableInteractionSource() }
    val hoveredState = hoverSource.collectIsHoveredAsState()
    val fieldShape = FieldDefaults.shape
    val standaloneLineHeight = Theme.typography.bodyMedium.lineHeight
    // Kuikly grows with text but does not reserve empty minLines consistently.
    val standaloneMinHeight = textareaMinimumHeight(standaloneLineHeight.value, minLines, autosize)
    val feedback = rememberInputFeedback(
        inputColors, fieldShape, focusedState, hoveredState, enabled,
        if (error != null) colors.destructive else null,
    )
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

    Column(modifier = modifier.graphicsLayer {
        alpha = if (enabled) 1f else FeedbackDefaults.disabledOpacity
    }) {
        // Field container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (bordered) feedback else Modifier)
                .hoverable(hoverSource, enabled = enabled && bordered)
                .clickable(interactionSource = hoverSource, indication = null, enabled = canFocus) {
                    requestInputFocus()
                }
                .then(
                    if (bordered) {
                        Modifier
                            .heightIn(min = standaloneMinHeight)
                            .clip(fieldShape)
                            .border(
                                BorderWidth.thin,
                                if (error != null) colors.destructive else inputColors.border,
                                fieldShape,
                            )
                            .background(inputColors.background)

                    } else {
                        Modifier
                            .clip(Theme.shapes.lg)
                            .background(colors.muted)
                            .then(
                                if (outlined) {
                                    Modifier.border(
                                        BorderWidth.thin,
                                        fieldBorderColor(error = error, enabled = enabled),
                                        Theme.shapes.lg,
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = verticalPadding)
                    }
                )
        ) {
            Column(modifier = if (bordered) Modifier.padding(
                horizontal = FieldSizeTokens.Medium.paddingHorizontal,
                vertical = ControlGeometry.textareaPaddingVertical,
            ) else Modifier) {
                val fontSize = if (bordered) Theme.typography.bodyMedium.fontSize else 16.sp
                val resolvedLineHeight = if (bordered) Theme.typography.bodyMedium.lineHeight else lineHeight
                // The placeholder and body share metrics to prevent first-character layout jumps.
                val inputTextStyle = TextStyle(
                    fontSize = fontSize,
                    lineHeight = resolvedLineHeight,
                    fontFamily = Theme.typography.bodyMedium.resolveFontFamily(),
                    letterSpacing = Theme.typography.bodyMedium.letterSpacing,
                    color = if (bordered) inputColors.foreground else if (enabled) colors.foreground else colors.mutedForeground,
                )
                // The same metrics, converted to the token types the kit Text needs.
                val placeholderTextStyle = com.gearui.foundation.typography.TextStyle(
                    fontSize = fontSize,
                    lineHeight = resolvedLineHeight,
                    fontWeight = com.tencent.kuikly.compose.ui.text.font.FontWeight.Normal,
                    fontFamily = Theme.typography.bodyMedium.fontFamily,
                    letterSpacing = Theme.typography.bodyMedium.letterSpacing,
                )

                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (maxLength == null || newValue.length <= maxLength) {
                            onValueChange(newValue)
                        }
                    },
                    // Rejecting a value in onValueChange does not reset the native field; the
                    // platform view would keep the extra text while the counter stops at the
                    // limit. Kuikly's maxLength modifier enforces it inside the native field.
                    modifier = Modifier.keyboardDismissExempt()
                        .then(if (maxLength != null) Modifier.maxLength(maxLength) else Modifier)
                        .fillMaxWidth()
                        .focusRequester(inputFocusRequester)
                        .onFocusChanged {
                            focusedState.value = it.isFocused
                            onFocusChanged?.invoke(it.isFocused)
                        },
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
                                    color = if (bordered) inputColors.placeholder else colors.mutedForeground,
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
            if (bordered) FieldFocusOverlay(inputColors, fieldShape, focusedState, enabled,
                if (error != null) colors.destructive else null)
        }
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
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (indicator && maxLength != null) {
                    Text(
                        text = "${value.length}/$maxLength",
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
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
    /**
     * Draw a hairline around the field.
     *
     * Off by default because this control is usually embedded in a bar that already
     * frames it. Turn it on where the field sits directly on a surface close in
     * colour to its own [muted][com.gearui.foundation.color.GearColors.muted]
     * fill — without an outline the two blend and the input stops looking tappable.
     *
     * This keeps the compact metrics; it is not the same as the standalone
     * `bordered` form-control look, which fixes a single line near 48dp.
     */
    outlined: Boolean = false,
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
        outlined = outlined,
        modifier = modifier,
    )
}
