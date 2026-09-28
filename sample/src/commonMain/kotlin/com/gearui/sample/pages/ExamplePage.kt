package com.gearui.sample.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.gearui.components.navbar.NavBar
import com.gearui.components.scaffold.PageScaffold
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.GearLazyColumn
import com.gearui.foundation.primitives.Text
import com.gearui.primitives.composite.Card
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.sample.config.ComponentInfo
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier

/**
 * The frame every component page uses: a NavBar with a back button over a scrolling
 * column of [ExampleSection]s, on the grouped page background.
 *
 * Everything here reads tokens, as the pages built on it must: the sample is the code
 * people copy, so it follows the same rules as the components it shows.
 */
@Composable
fun ExamplePage(
    component: ComponentInfo,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val listState = rememberLazyListState()

    PageScaffold(
        backgroundColor = colors.background,
        topSafeAreaColor = colors.surface,
        consumeBottomSafeArea = false
    ) {
        // The list runs under the home indicator (edge to edge) and reserves the inset at
        // its end instead: consuming it in PageScaffold painted a solid strip over it.
        val safeBottom = LocalRuntimeEnvironment.current.safeArea.bottom
        Column(modifier = Modifier.fillMaxSize()) {
            NavBar(
                title = component.nameEn,
                centerTitle = true,
                useDefaultBack = true,
                onBackClick = onBack,
                backgroundColor = colors.surface
            )
            // GearLazyColumn dismisses anchored overlays when the page scrolls.
            GearLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(
                    start = Spacing.lg,
                    top = Spacing.lg,
                    end = Spacing.lg,
                    bottom = Spacing.xxl + safeBottom
                )
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xxl)
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

/**
 * Where a section's examples sit.
 *
 * [Card] for loose controls — buttons, fields, switches — which need a surface to read
 * as a group. [Plain] for a component that is a surface itself — CellGroup, Card, List,
 * Collapse, Alert, a calendar — so it is shown as it will look on a page, not inside a
 * second card.
 */
enum class SectionSurface { Card, Plain }

/**
 * One capability of the component: a title, an optional one-line description, and the
 * examples, on a [SectionSurface].
 */
@Composable
fun ExampleSection(
    title: String,
    description: String = "",
    surface: SectionSurface = SectionSurface.Card,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        if (title.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = title,
                    style = Theme.typography.titleMedium,
                    color = colors.foreground
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                }
            }
        }

        when (surface) {
            SectionSurface.Card -> Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    content()
                }
            }
            SectionSurface.Plain -> Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                content()
            }
        }
    }
}
