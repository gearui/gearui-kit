package com.gearui.components.suggestion

import androidx.compose.runtime.Composable
import com.gearui.components.icon.IconSource
import com.gearui.foundation.chip.ChipSurface
import com.gearui.foundation.layout.Spacing
import com.gearui.gestures.ownsHorizontalDrag
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.ExperimentalLayoutApi
import com.tencent.kuikly.compose.foundation.layout.FlowRow
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.lazy.LazyRow
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier

/** One suggestion. [id] keys it. */
class Suggestion(
    val id: String,
    val label: String,
    val icon: IconSource? = null,
)

/**
 * Suggested questions or quick replies as pills: tapping one hands it to [onSelect] — to
 * send it, or to fill the input. One sideways-scrolling line by default (under a reply,
 * above the input); with [wrap] they flow onto as many lines as they need (a welcome
 * screen).
 *
 * @param contentPadding the line's inset, so the first pill lines up with the page while
 *   the scroll still reaches the screen edge.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestionChips(
    suggestions: List<Suggestion>,
    onSelect: (Suggestion) -> Unit,
    modifier: Modifier = Modifier,
    wrap: Boolean = false,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (wrap) {
        FlowRow(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            suggestions.forEach { s -> ChipSurface(s.label, { onSelect(s) }, icon = s.icon, enabled = enabled) }
        }
    } else {
        val scroll = rememberLazyListState()
        LazyRow(
            modifier.fillMaxWidth().ownsHorizontalDrag { scroll.firstVisibleItemIndex > 0 || scroll.firstVisibleItemScrollOffset > 0 },
            scroll,
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            items(suggestions, key = { it.id }) { s -> ChipSurface(s.label, { onSelect(s) }, icon = s.icon, enabled = enabled) }
        }
    }
}
