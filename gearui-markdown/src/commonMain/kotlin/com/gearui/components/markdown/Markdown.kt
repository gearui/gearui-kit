package com.gearui.components.markdown

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gearui.foundation.typography.TextStyle
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.toArgb
import com.tencent.kuiklybase.config.FontStyle
import com.tencent.kuiklybase.config.FontWeight
import com.tencent.kuiklybase.config.MarkdownColors
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.config.MarkdownTypography
import com.tencent.kuiklybase.config.TextStyleConfig
import com.tencent.kuiklybase.markdown.compose.MarkdownView

/**
 * Renders Markdown (GFM: headings, emphasis, lists, task lists, links, code, quotes, tables)
 * in the current GearUI theme. Colors come from the theme and every size scales from [style],
 * so it sits in body copy like any other text. Built on KuiklyMarkdown's Compose renderer.
 *
 * The caller decides scrolling (put it in a lazy list item, not inside another scroller) and what
 * a link does; without [onLinkClick] links render as styled text only.
 */
@Composable
fun Markdown(
    content: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Theme.typography.bodyMedium,
    onLinkClick: ((String) -> Unit)? = null,
) {
    val colors = Theme.colors
    val base = style.fontSize.value
    // Line height as a ratio of the size, so scaled headings and code keep the theme's spacing.
    val leading = style.lineHeight.value.takeIf { !it.isNaN() && it > 0f }?.div(base) ?: 1.5f
    val config = remember(colors, base, leading, onLinkClick) {
        MarkdownConfig(
            colors = MarkdownColors(
                text = colors.foreground.argb(), codeText = colors.foreground.argb(),
                codeBackground = colors.muted.argb(), inlineCodeBackground = colors.muted.argb(),
                tableBackground = colors.muted.argb(), blockQuoteBackground = colors.muted.argb(),
                dividerColor = colors.border.argb(), blockQuoteBar = colors.primary.argb(),
                linkColor = colors.primary.argb(),
            ),
            typography = markdownTypography(base, leading),
            onLinkClick = onLinkClick,
            // Agent and user text breaks lines where it was written; honour them.
            eolAsNewLine = true,
        )
    }
    MarkdownView(modifier = modifier, content = content, config = config)
}

private fun Color.argb(): Long = toArgb().toLong() and 0xFFFFFFFFL

private fun markdownTypography(base: Float, leading: Float): MarkdownTypography {
    fun text(scale: Float = 1f, weight: FontWeight = FontWeight.Normal, italic: Boolean = false) =
        TextStyleConfig(fontSize = base * scale, fontWeight = weight, fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
            lineHeight = base * scale * leading)
    fun mono(scale: Float) = TextStyleConfig(fontSize = base * scale, fontFamily = "monospace", lineHeight = base * scale * leading)
    return MarkdownTypography(
        text = text(), paragraph = text(), ordered = text(), bullet = text(), list = text(), textLink = text(),
        quote = text(italic = true), table = text(0.875f),
        code = mono(0.875f), inlineCode = mono(0.875f),
        h1 = text(1.5f, FontWeight.Bold), h2 = text(1.35f, FontWeight.Bold), h3 = text(1.2f, FontWeight.Bold),
        h4 = text(1.1f, FontWeight.SemiBold), h5 = text(1f, FontWeight.SemiBold), h6 = text(1f, FontWeight.SemiBold),
    )
}
