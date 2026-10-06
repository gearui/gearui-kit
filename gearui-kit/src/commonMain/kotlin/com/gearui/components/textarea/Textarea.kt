package com.gearui.components.textarea
import com.gearui.foundation.keyboard.avoidsKeyboard
import com.gearui.foundation.interaction.LocalControlLabel
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.gearui.foundation.field.multiLineFieldName
import com.gearui.foundation.field.FieldSurface
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.field.fill
import com.gearui.foundation.field.shadowed
import com.gearui.foundation.field.border
import com.gearui.foundation.typography.resolveFontFamily

import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.gearui.foundation.motion.FeedbackDefaults

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
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
import com.tencent.kuikly.compose.ui.text.input.TextFieldValue
import com.tencent.kuikly.compose.ui.text.TextRange
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

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
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.PRIMARY,
    placeholder: String = "",
    label: String? = null,
    maxLength: Int? = null,
    minLines: Int = 4,
    maxLines: Int? = null,
    indicator: Boolean = false,
    layout: TextareaLayout = TextareaLayout.VERTICAL,
    autosize: Boolean = false,
    required: Boolean = false,
    additionInfo: String? = null
) {
    val isVertical = layout == TextareaLayout.VERTICAL

    Column(modifier = modifier) {
        TextareaContent(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            placeholder = placeholder,
            label = label,
            maxLength = maxLength,
            minLines = minLines,
            maxLines = maxLines,
            indicator = indicator,
            isVertical = isVertical,
            variant = variant,
            error = error,
            required = required,
            additionInfo = additionInfo,
            autosize = autosize
        )

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
    maxLength: Int?,
    minLines: Int,
    maxLines: Int?,
    indicator: Boolean,
    isVertical: Boolean,
    variant: FieldVariant,
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
                    required = required,
                    enabled = enabled,
                    invalid = error != null
                )
                Spacer(modifier = Modifier.height(ControlGeometry.fieldLabelGap))
            }

            // Input area
            TextareaInputArea(
                accessibilityLabel = label,
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                readOnly = readOnly,
                placeholder = placeholder,
                maxLength = maxLength,
                minLines = minLines,
                maxLines = maxLines,
                indicator = indicator,
                variant = variant,
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
                        required = required,
                        enabled = enabled,
                        invalid = error != null,
                        modifier = Modifier.padding(end = Spacing.lg)
                    )
                }

                // Input area
                Column(modifier = Modifier.weight(1f)) {
                    TextareaInputArea(
                        accessibilityLabel = label,
                        value = value,
                        onValueChange = onValueChange,
                        enabled = enabled,
                        readOnly = readOnly,
                        placeholder = placeholder,
                        maxLength = maxLength,
                        minLines = minLines,
                        maxLines = maxLines,
                        indicator = indicator,
                        variant = variant,
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
    required: Boolean,
    enabled: Boolean,
    invalid: Boolean = false,
    modifier: Modifier = Modifier
) {
    com.gearui.foundation.field.FieldLabel(
        text = label,
        required = required,
        invalid = invalid,
        enabled = enabled,
        modifier = modifier,
    )
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
    variant: FieldVariant,
    error: String? = null,
    additionInfo: String?,
    autosize: Boolean,
    focusRequester: FocusRequester? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    verticalPadding: Dp = Spacing.sm,
    lineHeight: TextUnit = 24.sp,
    /**
     * Compact metrics for [AutoResizeTextarea]: [verticalPadding], [lineHeight] and a
     * 16sp body, so a single line can sit flush with 32dp controls. Off, the field uses
     * the standalone form-control metrics (uniform padding, a single line near 48dp),
     * the shared field border and the focus ring.
     */
    compact: Boolean = false,
    /** The screen-reader name when the field has a visible label; else the placeholder. */
    accessibilityLabel: String? = null,
    /**
     * Draw a hairline around the **compact** field. A compact field that sits beside
     * 32dp controls may still need an outline to read as an input rather than as a
     * patch of background.
     */
    outlined: Boolean = false,
    /**
     * Minimum height of the **compact** field, border included, with the text centred
     * vertically in it. Unspecified: the padding and the line decide.
     */
    compactMinHeight: Dp = Dp.Unspecified,
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
        // Field container. Fill and shadow come from the variant; the compact and
        // standalone metrics are independent of it.
        val fieldFill = variant.fill(inputColors.background, enabled)
        val containerShape = if (compact) Theme.shapes.lg else fieldShape
        FieldSurface(Modifier.fillMaxWidth(), shape = containerShape, shadowed = variant.shadowed) {
            Box(
                contentAlignment = if (compact && compactMinHeight != Dp.Unspecified) Alignment.CenterStart else Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (!compact) feedback else Modifier)
                    .hoverable(hoverSource, enabled = enabled && !compact)
                    // A tap gesture, not a clickable: a clickable container is one accessibility
                    // element, and it would hide the native text view inside it.
                    .pointerInput(canFocus) { detectTapGestures { if (canFocus) requestInputFocus() } }
                    .then(
                        if (!compact) {
                            Modifier
                                .heightIn(min = standaloneMinHeight)
                                .clip(fieldShape)
                                .border(
                                    BorderWidth.thin,
                                    if (error != null) colors.destructive else variant.border(inputColors.border, enabled),
                                    fieldShape,
                                )
                                .background(fieldFill)

                        } else {
                            Modifier
                                .then(if (compactMinHeight != Dp.Unspecified) Modifier.heightIn(min = compactMinHeight) else Modifier)
                                .clip(containerShape)
                                .background(fieldFill)
                                .then(
                                    // The secondary field is drawn by its hairline, so a compact one
                                    // keeps it too: its fill matches the bar it sits in.
                                    if (outlined || variant == FieldVariant.SECONDARY) {
                                        Modifier.border(
                                            BorderWidth.thin,
                                            if (error != null) colors.destructive else variant.border(colors.border, enabled),
                                            containerShape,
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .padding(horizontal = ControlGeometry.textareaCompactPaddingInline, vertical = verticalPadding)
                        }
                    )
            ) {
                Column(modifier = if (!compact) Modifier.padding(
                    horizontal = FieldSizeTokens.Medium.paddingHorizontal,
                    vertical = ControlGeometry.textareaPaddingVertical,
                ) else Modifier) {
                    val fontSize = if (!compact) Theme.typography.bodyMedium.fontSize else 16.sp
                    val resolvedLineHeight = if (!compact) Theme.typography.bodyMedium.lineHeight else lineHeight
                    // The placeholder and body share metrics to prevent first-character layout jumps.
                    val inputTextStyle = TextStyle(
                        fontSize = fontSize,
                        lineHeight = resolvedLineHeight,
                        fontFamily = Theme.typography.bodyMedium.resolveFontFamily(),
                        letterSpacing = Theme.typography.bodyMedium.letterSpacing,
                        color = if (!compact) inputColors.foreground else if (enabled) colors.foreground else colors.mutedForeground,
                    )
                    // The same metrics, converted to the token types the kit Text needs.
                    val placeholderTextStyle = com.gearui.foundation.typography.TextStyle(
                        fontSize = fontSize,
                        lineHeight = resolvedLineHeight,
                        fontWeight = com.tencent.kuikly.compose.ui.text.font.FontWeight.Normal,
                        fontFamily = Theme.typography.bodyMedium.fontFamily,
                        letterSpacing = Theme.typography.bodyMedium.letterSpacing,
                    )

                    // 🔴 The caret has to be managed here. The String overload of BasicTextField
                    // starts every **rebuild** from `TextFieldValue(text)`, with the selection at 0.
                    // Leave with a draft and come back (the chat composer drops the whole field
                    // out of composition in voice mode) and the caret lands before the text, so
                    // typing or deleting first needs a tap at the end.
                    //
                    // Text comes from the caller, the caret stays local. When the text is replaced
                    // from outside (cleared after send, draft restored) the caret goes to the end,
                    // the only place the user would want to continue editing.
                    var caretState by remember {
                        mutableStateOf(TextFieldValue(value, TextRange(value.length)))
                    }
                    val fieldValue =
                        if (caretState.text == value) caretState
                        else TextFieldValue(value, TextRange(value.length))
                    BasicTextField(
                        value = fieldValue,
                        onValueChange = { newValue ->
                            if (maxLength == null || newValue.text.length <= maxLength) {
                                caretState = newValue
                                if (newValue.text != value) onValueChange(newValue.text)
                            }
                        },
                        // Rejecting a value in onValueChange does not reset the native field; the
                        // platform view would keep the extra text while the counter stops at the
                        // limit. Kuikly's maxLength modifier enforces it inside the native field.
                        modifier = Modifier.multiLineFieldName(accessibilityLabel ?: LocalControlLabel.current ?: placeholder).keyboardDismissExempt().avoidsKeyboard(focusedState.value)
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
                                        color = if (!compact) inputColors.placeholder else colors.mutedForeground,
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
                if (!compact) FieldFocusOverlay(inputColors, fieldShape, focusedState, enabled,
                    if (error != null) colors.destructive else null)
            }
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
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.SECONDARY,
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
     * Draw a hairline around a [FieldVariant.PRIMARY] field. A [FieldVariant.SECONDARY]
     * field (the default) always has one: its fill is the field colour, the same as the
     * bar it usually sits in, so the hairline is what shows it.
     *
     * This keeps the compact metrics; it is not the same as the standalone
     * [Textarea] form-control look, which fixes a single line near 48dp.
     */
    outlined: Boolean = false,
    /**
     * Exact height of the collapsed (single-line) field, border included; the text is
     * centred vertically in it and the field still grows past it as lines are added.
     *
     * Use this, not [verticalPadding] arithmetic, to line the field up with the controls
     * beside it: the native line box comes out a little taller than [lineHeight], so a
     * height computed from padding lands a dp short (a 42dp target rendered at 41dp).
     */
    minHeight: Dp = Dp.Unspecified,
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
        variant = variant,
        additionInfo = null,
        autosize = true,
        focusRequester = inputFocusRequester,
        onFocusChanged = onFocusChanged,
        verticalPadding = verticalPadding,
        lineHeight = lineHeight,
        compact = true,
        outlined = outlined,
        compactMinHeight = minHeight,
        modifier = modifier,
    )
}
