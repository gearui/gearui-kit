package com.gearui.foundation.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.text.BasicText
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.ColorProducer
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.TextUnit
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.foundation.typography.*
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.ui.text.TextStyle as KuiklyTextStyle

/**
 * Text - fully Theme-driven text primitive
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: Color(0x...) or hardcoded colours
 *
 * Rework notes:
 * - the TextColors dependency is gone
 * - Theme.colors is used directly
 * - primary / secondary / tertiary semantics are supported
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,

    /** text style token (semantic) */
    style: TextStyle = Typography.BodyMedium,

    /** text colour - set explicitly */
    color: Color? = null,

    /** whether this is secondary text (uses textSecondary) */
    secondary: Boolean = false,

    /** whether this is tertiary text (uses textTertiary) */
    tertiary: Boolean = false,

    /** maximum number of lines */
    maxLines: Int = Int.MAX_VALUE,

    /** overflow handling */
    overflow: TextOverflow = TextOverflow.Clip,

    /** whether to wrap automatically */
    softWrap: Boolean = true,

    /** font size - backwards-compatible parameter, takes precedence over style.fontSize */
    fontSize: TextUnit? = null,

    /** font weight - backwards-compatible parameter, takes precedence over style.fontWeight */
    fontWeight: FontWeight? = null
) {
    // ⭐ Framework Rule #1: this is always the first line
    val themeColors = Theme.colors

    // Colour precedence: color > tertiary > secondary > primary
    val finalColor = color ?: when {
        tertiary -> themeColors.mutedForeground
        secondary -> themeColors.mutedForeground
        else -> themeColors.foreground
    }

    // Font size and weight can be overridden
    val finalFontSize = fontSize ?: style.fontSize
    val finalFontWeight = fontWeight ?: style.fontWeight

    // Converts a GearUI TextStyle into a Kuikly TextStyle
    val kuiklyStyle = KuiklyTextStyle(
        fontSize = finalFontSize,
        lineHeight = style.lineHeight,
        fontWeight = finalFontWeight,
        color = finalColor
    )

    BasicText(
        text = text,
        modifier = modifier,
        style = kuiklyStyle,
        maxLines = maxLines,
        overflow = overflow,
        softWrap = softWrap,
        color = { finalColor }
    )
}
