package com.gearui.components.codeblock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.copy.CopyAction
import com.gearui.foundation.disclosure.DisclosureHeader
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.StyledText
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.codeTextStyle
import com.gearui.gestures.ownsHorizontalDrag
import com.gearui.i18n.I18n
import com.gearui.i18n.formatArgs
import com.gearui.primitives.Divider
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.foundation.lazy.LazyRow
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.text.SpanStyle
import com.tencent.kuikly.compose.ui.text.buildAnnotatedString
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.text.withStyle

/**
 * A block of code, a command's output or a diff, as an agent shows it: an optional
 * header with the language and a copy button, line numbers, and lines that do not wrap
 * — the block scrolls sideways instead, so each number stays beside its line.
 *
 * Beyond [collapsedLines] lines it shows the first ones and offers the rest, so a long
 * file in a reply does not lay out hundreds of lines nobody asked to see. With [diff],
 * lines starting with `+` and `-` are tinted as added and removed.
 *
 * The whole text is two text nodes — the numbers and the code — however long it is.
 *
 * @param onCopy copies the code; Kuikly has no clipboard, so the host does it. Null hides
 *   the copy button.
 */
@Composable
fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    language: String? = null,
    showLineNumbers: Boolean = true,
    collapsedLines: Int = CodeBlockDefaults.CollapsedLines,
    diff: Boolean = false,
    onCopy: ((String) -> Unit)? = null,
) {
    val colors = Theme.colors
    val style = codeTextStyle()
    val shape = Theme.shapes.md
    val lines = remember(code) { code.trimEnd('\n').split('\n').map(::preserveSpaces) }
    var expanded by rememberSaveable(code) { mutableStateOf(false) }
    val shown = visibleLineCount(lines.size, collapsedLines, expanded)

    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.muted)
            .border(BorderWidth.hairline, colors.border, shape),
    ) {
        if (language != null || onCopy != null) {
            Row(
                Modifier.fillMaxWidth().padding(start = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(language.orEmpty(), style = Theme.typography.bodyExtraSmall, color = colors.mutedForeground,
                    modifier = Modifier.weight(1f))
                if (onCopy != null) CopyAction(code, onCopy)
            }
            Divider()
        }
        val body = remember(lines, shown) { lines.subList(0, shown) }
        val numbers = remember(body) { body.indices.joinToString("\n") { (it + 1).toString() } }
        val added = colors.success
        val removed = colors.destructive
        val addedFill = colors.successSoft
        val removedFill = colors.destructiveSoft
        val hunk = colors.mutedForeground
        val text = remember(body, diff, added, removed) {
            buildAnnotatedString {
                body.forEachIndexed { index, line ->
                    if (index > 0) append('\n')
                    when (if (diff) diffLineKind(line) else DiffLineKind.Context) {
                        DiffLineKind.Added -> withStyle(SpanStyle(color = added, background = addedFill)) { append(line) }
                        DiffLineKind.Removed -> withStyle(SpanStyle(color = removed, background = removedFill)) { append(line) }
                        DiffLineKind.Hunk -> withStyle(SpanStyle(color = hunk)) { append(line) }
                        DiffLineKind.Context -> append(line)
                    }
                }
            }
        }
        val scroll = rememberLazyListState()
        val density = LocalDensity.current
        val codeWidth = remember(body, style.fontSize, density) {
            with(density) { (style.fontSize * codeWidthEm(body)).toDp() }
        }
        val codeSize = if (measuresTextAtBoundedWidth) {
            val height = with(density) { style.lineHeight.toDp() } * body.size
            Modifier.width(codeWidth).height(height)
        } else Modifier.width(codeWidth)
        Row(Modifier.fillMaxWidth().padding(vertical = Spacing.sm)) {
            if (showLineNumbers) {
                Text(numbers, style = style, color = colors.mutedForeground, textAlign = TextAlign.End,
                    modifier = Modifier.padding(start = Spacing.md, end = Spacing.sm))
            }
            // Kuikly has no horizontalScroll: a one-item LazyRow scrolls the code sideways.
            // The code gets an explicit width — its longest line, estimated from the
            // monospace advance — so no renderer wraps it and each line stays beside its
            // number. Not softWrap = false: Kuikly's Android renderer reads that as a single
            // line and cut the block to its first line. Not an unbounded measure either:
            // the Web renderer still measures text against a bounded width and wrapped the
            // long lines.
            LazyRow(
                Modifier.weight(1f).ownsHorizontalDrag { scroll.firstVisibleItemIndex > 0 || scroll.firstVisibleItemScrollOffset > 0 },
                scroll,
                contentPadding = PaddingValues(start = if (showLineNumbers) Spacing.none else Spacing.md, end = Spacing.md),
            ) {
                item { StyledText(text, style = style, color = colors.foreground, modifier = codeSize) }
            }
        }
        if (shown < lines.size || (expanded && lines.size > collapsedLines && collapsedLines > 0)) {
            Divider()
            val actions = I18n.actions
            val label = if (expanded) actions.collapse else actions.showAllLinesFormat.formatArgs("count" to lines.size)
            DisclosureHeader(
                expanded = expanded,
                onToggle = { expanded = !expanded },
                label = label,
                padding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
                gap = Spacing.sm,
            ) {
                Text(label, style = Theme.typography.bodySmall, color = colors.primary, modifier = Modifier.weight(1f))
            }
        }
    }
}

object CodeBlockDefaults {
    /** Lines shown before "Show all": a screenful in a reply, not a whole file. */
    const val CollapsedLines: Int = 12
}

/** How many of [total] lines to show: all when expanded or short enough, else [collapsedLines]. */
internal fun visibleLineCount(total: Int, collapsedLines: Int, expanded: Boolean): Int =
    if (expanded || collapsedLines <= 0 || total <= collapsedLines) total else collapsedLines

internal enum class DiffLineKind { Added, Removed, Hunk, Context }

/** A unified-diff line's kind; file headers (`+++`, `---`) count as context. */
internal fun diffLineKind(line: String): DiffLineKind = when {
    line.startsWith("+++") || line.startsWith("---") -> DiffLineKind.Context
    line.startsWith("@@") -> DiffLineKind.Hunk
    line.startsWith("+") -> DiffLineKind.Added
    line.startsWith("-") -> DiffLineKind.Removed
    else -> DiffLineKind.Context
}

/** A code line as displayed: tabs expanded to four spaces. Copying uses the original code. */
internal fun preserveSpaces(line: String): String = line.replace("\t", "    ")

/**
 * The width of the longest line in ems, a little over a monospace advance per character
 * (0.6 em in Menlo, Droid Sans Mono and Courier) and a full em for wide characters —
 * CJK, full-width forms — so the estimate errs wide: too wide only adds scroll room,
 * too narrow wraps the line.
 */
internal fun codeWidthEm(lines: List<String>): Float =
    lines.maxOfOrNull { line -> line.sumOf { c -> if (c.isWide()) WIDE_EM else NARROW_EM } }?.toFloat() ?: 0f

private const val NARROW_EM = 0.62
private const val WIDE_EM = 1.05

private fun Char.isWide(): Boolean {
    val c = code
    return c in 0x1100..0x115F || c in 0x2E80..0xA4CF || c in 0xAC00..0xD7A3 ||
        c in 0xF900..0xFAFF || c in 0xFE30..0xFE4F || c in 0xFF00..0xFF60 || c in 0xFFE0..0xFFE6
}
