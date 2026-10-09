package com.gearui.components.input
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.gearui.components.icon.*
import com.gearui.foundation.keyboard.avoidsKeyboard
import com.gearui.foundation.interaction.LocalControlLabel
import com.gearui.foundation.field.fieldName
import com.gearui.i18n.I18n
import com.gearui.foundation.control.ControlGeometry
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.tencent.kuikly.compose.ui.semantics.semantics
import com.tencent.kuikly.compose.ui.semantics.role
import com.tencent.kuikly.compose.ui.semantics.onClick
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.text.TextRange
import com.tencent.kuikly.compose.ui.text.input.TextFieldValue
import com.gearui.foundation.motion.iconPressFeedback
import com.tencent.kuikly.compose.extension.setProp
import com.gearui.foundation.material.surfaceShadowStyles
import com.gearui.foundation.material.DecoratedSurface
import com.gearui.foundation.typography.resolveFontFamily

import com.tencent.kuikly.compose.ui.graphics.graphicsLayer

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
import com.tencent.kuikly.compose.foundation.text.maxLength
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.focus.FocusRequester
import com.tencent.kuikly.compose.ui.focus.focusRequester
import com.tencent.kuikly.compose.ui.focus.onFocusChanged
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
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
import com.gearui.foundation.interaction.*
import com.gearui.foundation.keyboard.keyboardDismissExempt
import com.gearui.theme.Theme
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.field.FieldFocusOverlay
import com.gearui.foundation.field.LocalFieldEmbedded
import com.gearui.foundation.field.LocalFieldGroupEnabled
import com.gearui.foundation.field.LocalFieldDisabledAppearanceOwned
import com.gearui.foundation.field.LocalFieldErrorOwned
import com.gearui.foundation.field.fieldSupportingText
import com.gearui.foundation.field.FieldFrame
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.field.fill
import com.gearui.foundation.field.shadowed
import com.gearui.foundation.field.border
import com.gearui.foundation.field.rememberInputFeedback
import com.gearui.theme.LocalInputColors
import com.tencent.kuikly.compose.foundation.hoverable
import com.tencent.kuikly.compose.foundation.interaction.collectIsHoveredAsState
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
 * - field variant (primary / secondary)
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
    labelPosition: String = "top", // "left" or "top"
    required: Boolean = false,
    helperText: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.PRIMARY,
    maxLength: Int? = null,
    showCounter: Boolean = false,
    maxLines: Int = 1,
    blurOnImeDone: Boolean = maxLines == 1,
    isPassword: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    clearable: Boolean = false,
    onClear: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    /**
     * Groups the input on screen — [InputFormat.ChinaMobile], [InputFormat.BankCard],
     * [InputFormat.IdCard] or your own. [value] stays raw (no separators); the format
     * also sets the keyboard and the length limit, so leave [maxLength] unset.
     */
    format: InputFormat? = null,
    onSend: (() -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    autoFocus: Boolean = false,
) {
    val enabled = enabled && LocalFieldGroupEnabled.current
    val parentDims = LocalFieldDisabledAppearanceOwned.current
    val colors = Theme.colors
    val inputColors = LocalInputColors.current
    val shapes = Theme.shapes
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val interactionSource = remember { createMutableInteractionSource() }
    val inputFocusRequester = remember { FocusRequester() }
    val focusedState = remember { mutableStateOf(false) }
    // Kuikly only pushes a keyboard type for Number/Email/Password; switching back to
    // plain text sends nothing, so on iOS a revealed password stayed masked
    // (secureTextEntry follows the keyboardType attribute). A field that has ever been a
    // password field therefore always states its keyboard type explicitly.
    var passwordCapable by remember { mutableStateOf(false) }
    if (isPassword) passwordCapable = true
    val explicitKeyboardType = if (!passwordCapable) null else when {
        isPassword -> "password"
        keyboardType == KeyboardType.Number -> if (numericKeyboardType() == KeyboardType.Number) "number" else "text"
        keyboardType == KeyboardType.Email -> "email"
        else -> "text"
    }
    var isFocused by focusedState
    val hoverSource = remember { com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource() }
    val hoveredState = hoverSource.collectIsHoveredAsState()
    val hasError = error != null
    val embedded = LocalFieldEmbedded.current

    // Autofocus only once the field is on screen. Requesting focus during the first
    // composition is silently dropped inside a lazy list, where the item composes
    // before it is attached, so `autoFocus` did nothing on exactly the screens that
    // want it (a search page, a code screen).
    var positioned by remember { mutableStateOf(false) }
    LaunchedEffect(autoFocus, positioned, enabled) {
        if (autoFocus && positioned && enabled) inputFocusRequester.requestFocus()
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
        InputSize.LARGE -> FieldDefaults.largeShape
        InputSize.MEDIUM -> FieldDefaults.shape
        InputSize.SMALL -> FieldDefaults.compactShape
    }

    // borderColor must not depend on isFocused. On Kuikly the modifier chain is
    // rebuilt the moment focus changes, which recreates the underlying EditText;
    val borderColor = when {
        hasError -> colors.destructive
        else -> variant.border(inputColors.border, enabled)
    }

    // Keep border width stable to avoid layout jump when focus/error changes.
    val borderWidth = tokens.borderWidth

    val backgroundColor = variant.fill(inputColors.background, enabled)

    val inputTextStyle = when (size) {
        InputSize.LARGE -> Theme.typography.bodyLarge
        InputSize.MEDIUM -> Theme.typography.bodyMedium
        InputSize.SMALL -> Theme.typography.bodySmall
    }
    val feedback = rememberInputFeedback(
        inputColors, shape, focusedState, hoveredState, enabled,
        if (hasError) colors.destructive else null,
    )

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

        // Only multi-line fields grow. A single-line filled field used to take just a
        // minimum height; with the parent's height unbounded, the weighted content row
        // below then measured to zero and the text, placeholder, prefix and suffix all
        // vanished, leaving an empty gray pill.
        val containerModifier = if (embedded) {
            // The group owns the frame; the field only keeps its height.
            if (maxLines > 1) Modifier.fillMaxWidth().heightIn(min = tokens.height) else Modifier.fillMaxWidth().height(tokens.height)
        } else if (maxLines > 1) {
            // Multiline (textarea): a fixed single-line height would clip the content, so height belongs
            // to the external modifier (pages pass .height(N)); this only guarantees the single-line minimum.
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
        // for Kuikly's intermittent focus loss inside the EditText. focusOnTap sees
        // every press and focuses the field on a tap — and only on a tap.
        //
        // Reference `.input__input--variant-primary`: field colour, no border, and the
        // field shadow stack (`ios:shadow-field`). The secondary variant (muted fill for
        // use on surfaces) has no shadow.
        FieldFrame(embedded = embedded, shape = shape, shadowed = variant.shadowed) {
            Box(
                modifier = feedback.then(containerModifier)
                    .onGloballyPositioned { if (it.size.width > 0) positioned = true }
                    .hoverable(hoverSource, enabled = enabled)
                    .focusOnTap(canFocus) { requestInputFocus() }
            ) {
              Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(
                            horizontal = tokens.paddingHorizontal,
                            vertical = if (variant == FieldVariant.SECONDARY && maxLines > 1) Spacing.md else Spacing.none
                        ),
                    verticalAlignment = if (maxLines > 1) Alignment.Top else Alignment.CenterVertically
                ) {
                    // Leading label, when labelPosition == "left"
                    if (label != null && labelPosition == "left") {
                        // The field applies the disabled opacity itself.
                        com.gearui.foundation.field.FieldLabel(text = label, required = required, invalid = hasError)
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
                        // 🔴 The caret has to be managed here, as Textarea already does.
                        // The String overload of BasicTextField starts every rebuild from
                        // `TextFieldValue(text)`, with the selection at 0 — so a field
                        // opened on existing text ("edit your nickname") puts the caret
                        // before the first character, and the user has to tap at the end
                        // before they can type or delete.
                        //
                        // Text comes from the caller, the caret stays local. When the text
                        // is replaced from outside, the caret goes to the end, the only
                        // place the user would want to continue from.
                        // With a format the field shows the grouped text; the caller's
                        // value stays raw.
                        val shown = format?.format(value) ?: value
                        var caretState by remember {
                            mutableStateOf(TextFieldValue(shown, TextRange(shown.length)))
                        }
                        // Keys closer together than any hand types them — a barcode or card
                        // reader acting as a keyboard — are taken as the field reports them and
                        // grouped once they stop. A grouped text written back crosses
                        // KuiklyUI's asynchronous bridge, and when it lands after the next key
                        // it replaces the field's newer text: that key is lost. Writing back
                        // the field's own text is harmless, as the plain field shows.
                        val burst = remember { FormatBurst() }
                        val holdScope = rememberCoroutineScope()
                        val fieldValue =
                            if (caretState.composition != null || caretState.text == shown || burst.holding) caretState
                            else TextFieldValue(shown, TextRange(shown.length))
                        BasicTextField(
                            value = fieldValue,
                            onValueChange = { newValue ->
                                if (!readOnly && enabled) {
                                    if (format != null) {
                                        val edit = formattedInputEdit(format, fieldValue.text, newValue)
                                        edit.raw?.let { if (it != value) onValueChange(it) }
                                        if (burst.isBurst(newValue.text) && newValue.composition == null) {
                                            burst.hold(fieldValue.text)
                                            caretState = newValue
                                            burst.release?.cancel()
                                            burst.release = holdScope.launch {
                                                delay(FormatBurst.GAP)
                                                caretState = formattedInputEdit(format, burst.before, caretState).fieldValue
                                                burst.holding = false
                                            }
                                        } else if (!burst.holding) {
                                            caretState = edit.fieldValue
                                        }
                                    } else if (maxLength == null || newValue.text.length <= maxLength) {
                                        caretState = newValue
                                        if (newValue.text != value) onValueChange(newValue.text)
                                    }
                                }
                            },
                            textStyle = TextStyle(
                                fontSize = inputTextStyle.fontSize,
                                fontWeight = inputTextStyle.fontWeight,
                                fontFamily = inputTextStyle.resolveFontFamily(),
                                letterSpacing = inputTextStyle.letterSpacing,
                                color = inputColors.foreground,
                                textAlign = textAlign
                            ),
                            cursorBrush = SolidColor(colors.primary),
                            keyboardOptions = KeyboardOptions(
                                // isPassword must go through KeyboardType.Password: on Kuikly iOS the masking
                                // channel is the native secureTextEntry (triggered by keyboardType=password),
                                // and visualTransformation has no effect across the Kuikly bridge.
                                keyboardType = if (isPassword) KeyboardType.Password else (format?.keyboardType ?: keyboardType).let {
                                    if (it == KeyboardType.Number) numericKeyboardType() else it
                                },
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
                            // The onValueChange guard above only protects the Compose value. The native
                            // field keeps whatever was typed, so a rejected keystroke leaves the platform
                            // view and the counter out of sync. Kuikly's maxLength modifier enforces the
                            // limit inside the native field itself.
                            modifier = Modifier.fieldName(label ?: LocalControlLabel.current ?: placeholder).keyboardDismissExempt().avoidsKeyboard(isFocused)
                                .then(
                                    when {
                                        // Accept the entire paste before stripping a country code or separators.
                                        // InputFormat.edit enforces the raw length after normalization.
                                        format != null -> Modifier
                                        maxLength != null -> Modifier.maxLength(maxLength)
                                        else -> Modifier
                                    }
                                )
                                .then(if (explicitKeyboardType != null) Modifier.setProp("keyboardType", explicitKeyboardType) else Modifier)
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
                                            style = inputTextStyle,
                                            color = inputColors.placeholder
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
                    // requestFocus -> IME reappears" flicker. Clearing fires on a tap only, not on a drag or a scroll,
                    // and requestInputFocus is called afterwards as a safeguard.
                    if (clearable && value.isNotEmpty() && enabled && !readOnly) {
                        var clearPressed by remember { mutableStateOf(false) }
                        val clearLabel = I18n.strings.field.clear
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Box(
                            modifier = Modifier
                                .size(ControlGeometry.inputClearSize)
                                .iconPressFeedback(clearPressed, ControlGeometry.inputClearSize, CircleShape)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = clearLabel
                                    onClick { onClear?.invoke(); onValueChange(""); true }
                                }
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        val down = awaitFirstDown(
                                            requireUnconsumed = false,
                                            pass = PointerEventPass.Initial,
                                        )
                                        down.consume()
                                        clearPressed = true
                                        val slop = viewConfiguration.touchSlop
                                        while (true) {
                                            val event = awaitPointerEvent(PointerEventPass.Initial)
                                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                            // A scroll that starts on the button must not clear the field.
                                            if (change.leftTap(down, slop)) break
                                            change.consume()
                                            if (!change.pressed) {
                                                onClear?.invoke()
                                                onValueChange("")
                                                requestInputFocus()
                                                break
                                            }
                                        }
                                        clearPressed = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.x,
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
                }
              }
              // The group owns the focus ring when the field is embedded in one.
              if (!embedded) FieldFocusOverlay(inputColors, shape, focusedState, enabled, if (hasError) colors.destructive else null)
            }
        }
    }

    // Main layout
    Column(modifier = modifier.then(com.tencent.kuikly.compose.ui.Modifier.graphicsLayer {
        alpha = if (enabled || parentDims) 1f else com.gearui.foundation.motion.FeedbackDefaults.disabledOpacity
    })) {
        // Top label (when labelPosition == "top")
        if (label != null && labelPosition == "top") {
            com.gearui.foundation.field.FieldLabel(
                text = label,
                required = required,
                invalid = hasError,
                modifier = Modifier.padding(bottom = com.gearui.foundation.control.ControlGeometry.fieldLabelGap),
            )
        }

        InputField()

        // Metadata never competes with the editable line for horizontal space.
        val bottomText = fieldSupportingText(error, helperText, LocalFieldErrorOwned.current)
        if (bottomText != null || (showCounter && maxLength != null)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                com.gearui.foundation.field.FieldDescription(
                    text = bottomText.orEmpty(),
                    invalid = hasError,
                    modifier = Modifier.weight(1f),
                )
                if (showCounter && maxLength != null) {
                    Text(
                        text = "${value.length}/$maxLength",
                        style = Theme.typography.bodySmall,
                        color = if (hasError) colors.destructiveSoftForeground else colors.mutedForeground,
                    )
                }
            }
        }
    }
}

enum class InputSize { LARGE, MEDIUM, SMALL }
