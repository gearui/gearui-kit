package com.gearui.components.toolbar

import androidx.compose.runtime.Composable
import com.gearui.foundation.layout.Spacing
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.semantics.semantics
import com.tencent.kuikly.compose.ui.semantics.contentDescription

/** Wrapping action region. Use Button, ToggleButton and CloseButton so every action retains its own semantics. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Toolbar(label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    FlowRow(modifier.fillMaxWidth().background(Theme.colors.surface).padding(Spacing.sm)
        .semantics { contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) { content() }
}
