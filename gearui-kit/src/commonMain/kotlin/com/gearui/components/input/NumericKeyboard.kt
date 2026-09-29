package com.gearui.components.input

import com.tencent.kuikly.compose.ui.text.input.KeyboardType

/** Avoid the Web renderer's unsupported selection API on HTML number inputs. */
internal expect fun numericKeyboardType(): KeyboardType
