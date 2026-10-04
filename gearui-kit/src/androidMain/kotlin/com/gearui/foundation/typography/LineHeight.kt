package com.gearui.foundation.typography

import android.content.res.Resources
import android.util.TypedValue
import com.tencent.kuikly.compose.ui.unit.TextUnit
import com.tencent.kuikly.compose.ui.unit.isSpecified
import com.tencent.kuikly.compose.ui.unit.sp

// The renderer reads the same metrics when it converts a font size (no host font adapter).
internal actual fun rendererLineHeight(lineHeight: TextUnit): TextUnit {
    if (!lineHeight.isSpecified) return lineHeight
    val metrics = Resources.getSystem().displayMetrics
    val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, lineHeight.value, metrics)
    return (px / metrics.density).sp
}
