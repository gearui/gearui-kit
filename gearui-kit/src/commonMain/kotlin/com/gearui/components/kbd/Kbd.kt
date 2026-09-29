package com.gearui.components.kbd

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
            Text(key, style = Theme.typography.bodySmall, color = colors.foreground, modifier = Modifier
                .background(colors.muted, Theme.shapes.sm).border(BorderWidth.thin, colors.border, Theme.shapes.sm).padding(Spacing.sm))
        }
    }
}
