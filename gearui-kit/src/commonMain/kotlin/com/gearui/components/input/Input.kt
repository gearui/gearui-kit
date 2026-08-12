package com.gearui.components.input

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.text.BasicTextField
import com.tencent.kuikly.compose.foundation.text.KeyboardActions
import com.tencent.kuikly.compose.foundation.text.KeyboardOptions
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.focus.FocusRequester
import com.tencent.kuikly.compose.ui.focus.focusRequester
import com.tencent.kuikly.compose.ui.focus.onFocusChanged
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.input.pointer.PointerEventPass
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.compose.ui.platform.LocalSoftwareKeyboardController
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.input.ImeAction
import com.tencent.kuikly.compose.ui.text.input.KeyboardType
import com.tencent.kuikly.compose.ui.text.input.PasswordVisualTransformation
import com.tencent.kuikly.compose.ui.text.input.VisualTransformation
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.interaction.*
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * GearUI Input - fully theme-driven
 *
 * ✅ Rule: the first line is always val colors = Theme.colors
 * ❌ Never: ColorTokens or hardcoded colours
 *
 * Supports:
 * - plain input
 * - label, leading or above
 * - required marker
 * - prefix and suffix
 * - clear button
 * - character limit
 * - password mode
 * - multiline
 * - card style
 * - text alignment
 * - states: normal, error, disabled, read-only
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
    error: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    maxLength: Int? = null,
    showCounter: Boolean = false,
    maxLines: Int = 1,
    blurOnImeDone: Boolean = maxLines == 1,
    isPassword: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    clearable: Boolean = false,
    onClear: (() -> Unit)? = null,
    cardStyle: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onSend: (() -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    autoFocus: Boolean = false,
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val interactionSource = remember { createMutableInteractionSource() }
    val inputFocusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    val hasError = error != null

    // Autofocus
    if (autoFocus) {
        LaunchedEffect(Unit) {
            inputFocusRequester.requestFocus()
        }
    }

    when {
        !enabled -> interactionSource.updateState(InteractionState.Disabled)
        hasError && !isFocused -> interactionSource.updateState(InteractionState.Normal)
        isFocused -> interactionSource.updateState(InteractionState.Focused)
        else -> interactionSource.updateState(InteractionState.Normal)
    }

    val tokens = when (size) {
        InputSize.LARGE -> FieldSizeTokens.Large
        InputSize.MEDIUM -> FieldSizeTokens.Medium
        InputSize.SMALL -> FieldSizeTokens.Small
    }

    val shape = when (size) {
        InputSize.LARGE -> FieldDefaults.shape
        InputSize.MEDIUM -> FieldDefaults.shape
        InputSize.SMALL -> FieldDefaults.compactShape
    }

    // borderColor must not depend on isFocused. On Kuikly the modifier chain is
    // rebuilt the moment focus changes, which recreates the underlying EditText;
    val borderColor = when {
        hasError -> colors.destructive
        else -> colors.border
    }

    // Keep border width stable to avoid layout jump when focus/error changes.
    val borderWidth = tokens.borderWidth

    val backgroundColor = when {
        !enabled -> colors.muted
        cardStyle -> colors.muted
        else -> colors.surface
    }

    // Input content
    @Composable
    fun InputField() {
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

        // Width comes from the field tokens and is never zero, so this is not
        // conditional. It was already constant-true before (borderWidth = 1f).
        val borderModifier = Modifier.border(borderWidth, borderColor, shape)

        val containerModifier = if (cardStyle) {
            Modifier
                .fillMaxWidth()
                .heightIn(min = tokens.height)
                .clip(shape)
                .background(backgroundColor)
                .then(borderModifier)
        } else {
            Modifier
                .fillMaxWidth()
                .height(tokens.height)
                .clip(shape)
                .background(backgroundColor)
                .then(borderModifier)
        }

        // Key detail: pointerInput with requireUnconsumed = false catches every tap.
        // Compose's clickable never fires when a child composable (BasicTextField)
        // consumes the event, so tapping the field itself never reaches an outer
        // clickable — that only covers taps on the padding, and cannot compensate
        // for Kuikly's intermittent focus loss inside the EditText. pointerInput
        Box(
            modifier = containerModifier
                .pointerInput(canFocus) {
                    if (!canFocus) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id }
                            if (change == null || !change.pressed) {
                                requestInputFocus()
                                break
                            }
                        }
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = tokens.paddingHorizontal,
                        vertical = if (cardStyle) 12.dp else 0.dp
                    ),
                verticalAlignment = if (maxLines > 1) Alignment.Top else Alignment.CenterVertically
            ) {
                // Leading label, when labelPosition == "left"
                if (label != null && labelPosition == "left") {
                    Row {
                        if (required) {
                            Text(
                                text = "*",
                                style = Typography.BodyMedium,
                                color = colors.destructive
                            )
                        }
                        Text(
                            text = label,
                            style = Typography.BodyMedium,
                            color = if (!enabled) colors.mutedForeground else colors.foreground
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                }

                // Prefix
                if (prefix != null) {
                    prefix()
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }

                // Input area.
                // Architecture notes:
                // 1) BasicTextField uses fillMaxWidth, not fillMaxSize; otherwise taps never reach the outer layer.
                // 2) The placeholder goes back inside decorationBox: it belongs to BasicTextField's own render
                //    tree, so a tap on the placeholder and a tap on innerTextField are handled the same way
                //    and it cannot steal focus the way a sibling Text does.
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
                            if (!readOnly && enabled) {
                                if (maxLength == null || newValue.length <= maxLength) {
                                    onValueChange(newValue)
                                }
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = Typography.BodyMedium.fontSize,
                            fontWeight = Typography.BodyMedium.fontWeight,
                            color = if (!enabled) colors.mutedForeground else colors.foreground,
                            textAlign = textAlign
                        ),
                        cursorBrush = SolidColor(colors.primary),
                        keyboardOptions = KeyboardOptions(
                            // isPassword must go through KeyboardType.Password: on Kuikly iOS the masking
                            // channel is the native secureTextEntry (triggered by keyboardType=password),
                            // and visualTransformation has no effect across the Kuikly bridge.
                            keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
                            imeAction = when {
                                onSend != null -> ImeAction.Send
                                maxLines == 1 -> ImeAction.Done
                                else -> ImeAction.Default
                            }
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = { onSend?.invoke() },
                            onDone = {
                                if (blurOnImeDone && maxLines == 1) {
                                    focusManager.clearFocus(force = true)
                                    keyboardController?.hide()
                                }
                            }
                        ),
                        singleLine = maxLines == 1,
                        maxLines = maxLines,
                        readOnly = readOnly,
                        enabled = enabled,
                        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(inputFocusRequester)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                onFocusChanged?.invoke(focusState.isFocused)
                            },
                        decorationBox = { innerTextField ->
                            Box(
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
                                        color = colors.mutedForeground
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                // Clear button
                // pointerInput consumes the down event in the Initial pass so it never reaches the
                // underlying native EditText, which would produce a visible "blur -> IME hides ->
                // requestFocus -> IME reappears" flicker. Clearing fires on tap only, not on drag,
                // and requestInputFocus is called afterwards as a safeguard.
                if (clearable && value.isNotEmpty() && enabled && !readOnly) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(colors.muted)
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(
                                        requireUnconsumed = false,
                                        pass = PointerEventPass.Initial,
                                    )
                                    down.consume()
                                    while (true) {
                                        val event = awaitPointerEvent(PointerEventPass.Initial)
                                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                        change.consume()
                                        if (!change.pressed) {
                                            onClear?.invoke()
                                            onValueChange("")
                                            requestInputFocus()
                                            break
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            name = Icons.close,
                            size = IconSizes.Default.xs,
                            tint = colors.mutedForeground
                        )
                    }
                }

                // Suffix
                if (suffix != null) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    suffix()
                }

                // Character counter
                if (showCounter && maxLength != null) {
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = "${value.length}/$maxLength",
                        style = Typography.BodySmall,
                        color = if (value.length >= maxLength) colors.destructive else colors.mutedForeground
                    )
                }
            }
        }
    }

    // Main layout
    Column(modifier = modifier) {
        // Top label (when labelPosition == "top")
        if (label != null && labelPosition == "top") {
            Row(modifier = Modifier.padding(bottom = Spacing.sm)) {
                if (required) {
                    Text(
                        text = "*",
                        style = Typography.BodyMedium,
                        color = colors.destructive
                    )
                }
                Text(
                    text = label,
                    style = Typography.BodyMedium,
                    color = if (!enabled) colors.mutedForeground else colors.foreground
                )
            }
        }

        InputField()

        // Helper text below
        val bottomText = error ?: helperText
        if (bottomText != null) {
            Text(
                text = bottomText,
                style = Typography.BodySmall,
                color = if (hasError) colors.destructive else colors.mutedForeground,
                modifier = Modifier.padding(top = Spacing.xs)
            )
        }
    }
}

enum class InputSize { LARGE, MEDIUM, SMALL }
