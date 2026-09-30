package com.gearui.components.kbd

import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.semantics.semantics
import com.tencent.kuikly.compose.ui.semantics.contentDescription

/** Keyboard shortcut hint; key names are supplied by the host, including platform-specific modifiers. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Kbd(keys: List<String>, modifier: Modifier = Modifier, accessibilityLabel: String = keys.joinToString(" + ")) {
    val colors = Theme.colors
    FlowRow(modifier.semantics(mergeDescendants = true) { contentDescription = accessibilityLabel }, horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        keys.forEach { key ->
            // The cap is a box around the text: on a KuiklyUI text node, padding and a
            // background do not move the glyphs, which stayed in the top-left corner.
            Box(
                Modifier.background(colors.muted, Theme.shapes.sm).border(BorderWidth.thin, colors.border, Theme.shapes.sm)
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Text(key, style = Theme.typography.bodySmall, color = colors.foreground)
            }
        }
    }
}
