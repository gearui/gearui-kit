package com.gearui.components.inputotp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldSurface
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.keyboard.keyboardDismissExempt
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.theme.LocalInputColors
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.core.Animatable
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.extension.setProp
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.text.BasicTextField
import com.tencent.kuikly.compose.foundation.text.KeyboardOptions
import com.tencent.kuikly.compose.foundation.text.maxLength
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.focus.FocusRequester
import com.tencent.kuikly.compose.ui.focus.focusRequester
import com.tencent.kuikly.compose.ui.focus.onFocusChanged
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.input.KeyboardType
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * InputOTP — HeroUI Native `InputOTP`: a one-time code entered into separate slots.
 *
 * Structure follows the reference: one native text field owns the value, the keyboard
 * and paste, and the slots only *display* it. The field sits invisibly over the slots,
 * so a tap anywhere focuses it and deleting works as in any text field; there is no
 * per-slot focus juggling to go wrong.
 *
 * Slots are 44x48 on the field fill (with the field shadow for [FieldVariant.PRIMARY],
 * the neutral fill for [FieldVariant.SECONDARY]). The active slot gets a 2dp accent
 * outline and a blinking caret; [invalid] turns every outline danger.
 *
 * - [groupSize] splits the slots into groups with a separator, e.g. 3 for "123-456".
 * - [numeric] limits input to digits and asks for the number keyboard.
 * - [placeholder] is shown in empty slots, one character per slot.
 * - [onComplete] fires once each time the code reaches [length].
 */
@Composable
fun InputOTP(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    groupSize: Int? = null,
    enabled: Boolean = true,
    invalid: Boolean = false,
    numeric: Boolean = true,
    placeholder: String? = null,
    variant: FieldVariant = FieldVariant.PRIMARY,
    autoFocus: Boolean = false,
    focusRequester: FocusRequester? = null,
    onComplete: ((String) -> Unit)? = null,
) {
    val requester = focusRequester ?: remember { FocusRequester() }
    var focused by remember { mutableStateOf(false) }
    val code = sanitizeOtp(value, length, numeric)

    LaunchedEffect(code) {
        if (code.length == length) onComplete?.invoke(code)
    }
    LaunchedEffect(autoFocus) {
        if (autoFocus && enabled) requester.requestFocus()
    }

    Box(modifier = modifier.alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ControlGeometry.otpGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (index in 0 until length) {
                if (groupSize != null && groupSize > 0 && index > 0 && index % groupSize == 0) {
                    OtpSeparator()
                }
                OtpSlot(
                    char = code.getOrNull(index),
                    placeholder = placeholder?.getOrNull(index),
                    active = focused && enabled && index == activeOtpSlot(code.length, length),
                    invalid = invalid,
                    variant = variant,
                )
            }
        }
        // The only real input. Text and caret are transparent: the slots draw both.
        BasicTextField(
            value = code,
            onValueChange = { next -> if (enabled) onValueChange(sanitizeOtp(next, length, numeric)) },
            enabled = enabled,
            singleLine = true,
            textStyle = TextStyle(color = Color.Transparent),
            cursorBrush = SolidColor(Color.Transparent),
            keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Number else KeyboardType.Text),
            modifier = Modifier
                .matchParentSize()
                .keyboardDismissExempt()
                .maxLength(length)
                .then(if (numeric) Modifier.setProp("keyboardType", "number") else Modifier)
                .focusRequester(requester)
                .onFocusChanged { focused = it.isFocused },
        )
    }
}

@Composable
private fun OtpSlot(
    char: Char?,
    placeholder: Char?,
    active: Boolean,
    invalid: Boolean,
    variant: FieldVariant,
) {
    val colors = Theme.colors
    val inputColors = LocalInputColors.current
    val shape = FieldDefaults.shape
    val style = Theme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
    FieldSurface(
        modifier = Modifier.size(ControlGeometry.otpSlotWidth, ControlGeometry.otpSlotHeight),
        shape = shape,
        shadowed = variant == FieldVariant.PRIMARY,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(if (variant == FieldVariant.PRIMARY) inputColors.background else colors.muted),
            contentAlignment = Alignment.Center,
        ) {
            when {
                char != null -> Text(text = char.toString(), style = style, color = inputColors.foreground)
                active -> OtpCaret(inputColors.placeholder)
                placeholder != null -> Text(
                    text = placeholder.toString(),
                    style = style,
                    color = inputColors.placeholder.copy(alpha = inputColors.placeholder.alpha * 0.5f),
                )
            }
        }
        // Outline as a real view so it paints above the fill on every platform.
        val outline = when {
            invalid -> colors.destructive
            active -> colors.primary
            else -> Color.Transparent
        }
        Box(Modifier.matchParentSize().border(BorderWidth.thick, outline, shape))
    }
}

/** Reference caret: opacity 0..1 and height 16..18, 500ms each way, repeating. */
@Composable
private fun OtpCaret(color: Color) {
    val opacity = remember { Animatable(1f) }
    val height = remember { Animatable(ControlGeometry.otpCaretMax.value) }
    val motion = Theme.motion
    LaunchedEffect(Unit) {
        if (motion.normal <= 0) return@LaunchedEffect
        while (true) {
            opacity.animateTo(0f, tween(500))
            opacity.animateTo(1f, tween(500))
        }
    }
    LaunchedEffect(Unit) {
        if (motion.normal <= 0) return@LaunchedEffect
        while (true) {
            height.animateTo(ControlGeometry.otpCaretMin.value, tween(500))
            height.animateTo(ControlGeometry.otpCaretMax.value, tween(500))
        }
    }
    Box(
        Modifier
            .width(ControlGeometry.otpCaretWidth)
            .height(height.value.dp)
            .alpha(opacity.value)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun OtpSeparator() {
    Box(
        Modifier
            .size(ControlGeometry.otpSeparatorWidth, ControlGeometry.otpSeparatorHeight)
            .clip(CircleShape)
            .background(Theme.colors.separator.copy(alpha = Theme.colors.separator.alpha * 0.5f))
    )
}

/** Keeps at most [length] characters, digits only when [numeric]. Pasted codes such as "123 456" survive. */
internal fun sanitizeOtp(raw: String, length: Int, numeric: Boolean): String =
    (if (numeric) raw.filter { it.isDigit() } else raw.filter { !it.isWhitespace() }).take(length)

/** The slot the next character goes into; the last slot once the code is full. */
internal fun activeOtpSlot(filled: Int, length: Int): Int = filled.coerceIn(0, (length - 1).coerceAtLeast(0))
