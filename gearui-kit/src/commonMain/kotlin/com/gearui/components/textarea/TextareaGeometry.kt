package com.gearui.components.textarea

import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/** Preserve explicit line counts and compact autosize while giving the default field its own surface. */
internal fun textareaMinimumHeight(lineHeight: Float, minLines: Int, autosize: Boolean): Dp {
    val measured = lineHeight.dp * minLines.coerceAtLeast(1) +
        ControlGeometry.textareaPaddingVertical * 2 + BorderWidth.thin * 2
    return if (!autosize && minLines == 4) maxOf(measured, ControlGeometry.textareaMinHeight) else measured
}
