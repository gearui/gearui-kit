package com.gearui.components.toolcall

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.codeblock.CodeBlock
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.disclosure.DisclosureBody
import com.gearui.foundation.disclosure.DisclosureHeader
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.status.ActivityStatus
import com.gearui.foundation.status.StatusGlyph
import com.gearui.foundation.status.of
import com.gearui.foundation.typography.codeTextStyle
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.text.style.TextOverflow

/**
 * One call an agent made to a tool: its [name] and [status] on a line, and — opened —
 * what it was given ([input]), what came back ([output]) or what went wrong ([error]).
 * Arguments and results show as code blocks, cut to a few lines until asked for more.
 *
 * @param expanded null opens and closes on its own — closed, except a failed call, which
 *   opens to show its error; a value controls it, with [onExpandedChange] reporting taps.
 */
@Composable
fun ToolCallCard(
    name: String,
    status: ActivityStatus,
    modifier: Modifier = Modifier,
    input: String? = null,
    output: String? = null,
    error: String? = null,
    expanded: Boolean? = null,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val colors = Theme.colors
    val strings = I18n.activity
    val shape = Theme.shapes.md
    var open by remember(status == ActivityStatus.Failed) { mutableStateOf(status == ActivityStatus.Failed) }
    val shown = expanded ?: open
    val hasBody = input != null || output != null || error != null
    val statusText = strings.of(status)
    Column(
        modifier.fillMaxWidth().clip(shape).background(colors.card).border(BorderWidth.hairline, colors.border, shape),
    ) {
        DisclosureHeader(
            expanded = shown,
            onToggle = {
                if (expanded == null) open = !shown
                onExpandedChange?.invoke(!shown)
            },
            label = "$name, $statusText",
            enabled = hasBody,
            padding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
            gap = Spacing.sm,
            shape = shape,
            trailing = { Text(statusText, style = Theme.typography.bodySmall, color = colors.mutedForeground) },
        ) {
            StatusGlyph(status)
            Text(name, style = codeTextStyle(), color = colors.foreground, maxLines = 1,
                overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        }
        if (hasBody) {
            DisclosureBody(
                expanded = shown,
                padding = PaddingValues(start = Spacing.md, end = Spacing.md, bottom = Spacing.md),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    input?.let { Section(strings.toolInput, it) }
                    output?.let { Section(strings.toolOutput, it) }
                    error?.let {
                        Text(strings.toolError, style = Theme.typography.bodyExtraSmall, color = colors.destructive)
                        Text(it, style = Theme.typography.bodySmall, color = colors.destructive)
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, body: String) {
    Text(title, style = Theme.typography.bodyExtraSmall, color = Theme.colors.mutedForeground)
    CodeBlock(body, showLineNumbers = false, collapsedLines = TOOL_SECTION_LINES)
}

/** Arguments and results are usually short; a long one is cut sooner than a code reply. */
private const val TOOL_SECTION_LINES = 8
