package com.gearui.components.reasoning

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.icon.*
import com.gearui.foundation.disclosure.DisclosureBody
import com.gearui.foundation.disclosure.DisclosureHeader
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.status.ActivityStatus
import com.gearui.foundation.status.StatusGlyph
import com.gearui.foundation.typography.IconSizes
import com.gearui.i18n.ActivityStrings
import com.gearui.i18n.I18n
import com.gearui.i18n.formatArgs
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Modifier

/**
 * A model's reasoning before its answer. While [thinking] it is open and its title says
 * so with a spinner; when thinking ends it closes into one line — "Thought for 12s" — that
 * opens it again. [text] may grow while thinking (streamed); it is one text node.
 *
 * @param durationSeconds how long it thought, for the closed title; null shows a plain title.
 * @param expanded null lets the component open and close itself as above; a value
 *   controls it, with [onExpandedChange] reporting the user's taps.
 */
@Composable
fun Reasoning(
    text: String,
    thinking: Boolean,
    modifier: Modifier = Modifier,
    durationSeconds: Int? = null,
    expanded: Boolean? = null,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val colors = Theme.colors
    val strings = I18n.activity
    var open by remember { mutableStateOf(thinking) }
    // Follow the model: open while thinking, closed once it is done. The user can still
    // open or close it at any time.
    LaunchedEffect(thinking) { open = thinking }
    val shown = expanded ?: open
    val title = reasoningTitle(strings, thinking, durationSeconds)
    Column(modifier.fillMaxWidth()) {
        DisclosureHeader(
            expanded = shown,
            onToggle = {
                if (expanded == null) open = !shown
                onExpandedChange?.invoke(!shown)
            },
            label = title,
            padding = PaddingValues(vertical = Spacing.xs),
            gap = Spacing.sm,
        ) {
            if (thinking) StatusGlyph(ActivityStatus.Running, size = IconSizes.Default.md)
            else Icon(Icons.lightbulb, size = IconSizes.Default.md, tint = colors.mutedForeground)
            Text(title, style = Theme.typography.bodySmall, color = colors.mutedForeground, modifier = Modifier.weight(1f))
        }
        DisclosureBody(expanded = shown, padding = PaddingValues(bottom = Spacing.sm)) {
            Box(Modifier.fillMaxWidth().background(colors.muted, Theme.shapes.sm).padding(Spacing.md)) {
                Text(text, style = Theme.typography.bodySmall, color = colors.mutedForeground)
            }
        }
    }
}

/** "Thinking", "Thought for 12s", or "Thoughts" when the duration is unknown. */
internal fun reasoningTitle(strings: ActivityStrings, thinking: Boolean, durationSeconds: Int?): String = when {
    thinking -> strings.thinking
    durationSeconds != null -> strings.thoughtForFormat.formatArgs("seconds" to durationSeconds)
    else -> strings.thoughts
}
