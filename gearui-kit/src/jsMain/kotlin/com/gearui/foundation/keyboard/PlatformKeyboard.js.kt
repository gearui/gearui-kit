package com.gearui.foundation.keyboard

import com.tencent.kuikly.compose.ui.unit.Dp

/** Not observable from the kit here; the host reports it through RuntimeInsetsBridge. */
internal actual fun observePlatformKeyboard(onHeight: (Dp) -> Unit) = Unit
