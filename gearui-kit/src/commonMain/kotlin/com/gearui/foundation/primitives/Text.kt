package com.gearui.foundation.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.text.BasicText
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.gearui.foundation.typography.*
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.ui.text.TextStyle as KuiklyTextStyle

/**
 * Text - the kit's text primitive.
 *
 * Size, weight, line height and family come from [style], a token of
 * `Theme.typography`; colour from [color], `Theme.colors.foreground` unless given.
 * One way to set each: a one-off size belongs in a style, not in a parameter.
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Theme.typography.bodyMedium,
    color: Color = Theme.colors.foreground,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    /**
     * Horizontal alignment of each line within the text block.
     *
     * Distinct from centring the composable itself: a centred Text whose
     * alignment is Start still renders a ragged left edge once the string
     * wraps, which is visible in dialogs and empty states.
     */
    textAlign: TextAlign? = null
) {
    val kuiklyStyle = KuiklyTextStyle(
        fontSize = style.fontSize,
        lineHeight = rendererLineHeight(style.lineHeight),
        fontWeight = style.fontWeight,
        fontFamily = style.resolveFontFamily(),
        letterSpacing = style.letterSpacing,
        color = color,
        // Kuikly's TextStyle takes a non-null TextAlign; Start is its own default.
        textAlign = textAlign ?: TextAlign.Start
    )

    BasicText(
        text = text,
        modifier = modifier,
        style = kuiklyStyle,
        maxLines = maxLines,
        overflow = overflow,
        softWrap = softWrap,
        color = { color }
    )
}
