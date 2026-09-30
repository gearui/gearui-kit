package com.gearui.components.input

import com.tencent.kuikly.compose.ui.text.input.KeyboardType

/**
 * What a digits-only field asks the platform for. Native platforms get KuiklyUI's
 * "number" keyboard. The Web gets a text input: KuiklyUI's Web renderer turns "number"
 * into an HTML number input and then calls `setSelectionRange` on it, which browsers
 * reject.
 */
internal expect fun numericKeyboardType(): KeyboardType

/**
 * The keyboard for a field that holds a number.
 *
 * KuiklyUI has exactly one numeric keyboard, "number", and it is a digit pad: iOS
 * `UIKeyboardTypeNumberPad`, Android `TYPE_CLASS_NUMBER`, neither with a decimal point
 * or a minus sign. So a field that takes only non-negative whole numbers gets that pad,
 * and a field that takes decimals or negatives gets the text keyboard, the only one on
 * which "." and "-" can be typed.
 */
internal fun numberFieldKeyboard(wholeNonNegative: Boolean): KeyboardType =
    if (wholeNonNegative) KeyboardType.Number else KeyboardType.Text
