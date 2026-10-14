package com.gearui.foundation.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.foundation.scroll.*

/**
 * ScrollView - page-level scrolling container primitive
 *
 * Equivalent in role to:
 * - Material3: Scaffold body
 * - Flutter: CustomScrollView
 *
 * Responsibilities:
 * - consistent padding / spacing
 * - consistent bounce / physics
 * - a consistent scrollbar policy
 * - what a page that scrolls owes its overlays and fields, through [GearLazyColumn]:
 *   a drag dismisses anchored overlays (a Select's menu would otherwise float away
 *   from its field) and hides the keyboard, and a focused field inside is kept above
 *   the keyboard. A form page built on a bare LazyColumn left its fields under it.
 *
 * Safe area is not handled here: put the page in PageScaffold.
 *
 * Implementation note:
 * - KuiklyUI does not support Modifier.verticalScroll
 * - the scrolling container is built on GearLazyColumn
 *
 * Use for:
 * - ordinary page scrolling
 * - form pages
 * - detail pages
 *
 * Do not use for:
 * - long lists (use List)
 * - virtualised cases (use List)
 */
@Composable
fun ScrollView(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: ScrollState = rememberScrollState(),
    tokens: ScrollTokens = ScrollTokens.Default,
    physics: ScrollPhysics = ScrollPhysics.Platform,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    GearLazyColumn(
        modifier = modifier.then(physics.modifier()),
        state = state.raw,
        contentPadding = tokens.contentPadding,
        horizontalAlignment = horizontalAlignment
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(tokens.spacing),
                horizontalAlignment = horizontalAlignment,
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
    }
}
