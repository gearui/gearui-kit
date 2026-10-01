package com.gearui.components.code

import com.gearui.gestures.ownsHorizontalDrag
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.TextStyle
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.lazy.LazyRow
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier

/** Inline source or command text; typography can be replaced with the app's monospace token. */
@Composable
fun Code(text: String, modifier: Modifier = Modifier, style: TextStyle = Theme.typography.bodySmall.copy(fontFamily = listOf("monospace"))) {
    // A box carries the chip: on a KuiklyUI text node padding does not move the glyphs.
    Box(
        modifier.background(Theme.colors.muted, Theme.shapes.sm).padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(text, style = style, color = Theme.colors.foreground)
    }
}

/** Scrollable source block. The host supplies clipboard behavior and its localized action label. */
@Composable
fun Snippet(text: String, modifier: Modifier = Modifier, copyLabel: String? = null,
    onCopy: ((String) -> Unit)? = null, style: TextStyle = Theme.typography.bodySmall.copy(fontFamily = listOf("monospace"))) {
    Column(modifier.fillMaxWidth().background(Theme.colors.muted, Theme.shapes.md).padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        val scroll = rememberLazyListState()
        LazyRow(Modifier.fillMaxWidth().ownsHorizontalDrag { scroll.firstVisibleItemIndex > 0 || scroll.firstVisibleItemScrollOffset > 0 }, scroll) {
            item { Text(text, style = style, color = Theme.colors.foreground) }
        }
        if (onCopy != null) {
            require(!copyLabel.isNullOrBlank()) { "A localized copy label is required" }
            Button(text = copyLabel, onClick = { onCopy(text) }, type = ButtonType.TEXT, size = ButtonSize.SMALL)
        }
    }
}
