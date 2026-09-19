package com.gearui.sample.pages

import com.gearui.runtime.LocalRuntimeEnvironment
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.navbar.NavBar
import com.gearui.components.scaffold.PageScaffold
import com.gearui.foundation.primitives.GearLazyColumn
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.theme.Theme

/**
 * ExamplePage - shared wrapper for component example pages
 *
 * Gives every component the same container:
 * - a top navigation bar (NavBar, with a back button)
 * - a scrollable content area (GearLazyColumn, dismissing floating layers on scroll)
 * - consistent styling and layout
 */
@Composable
fun ExamplePage(
    component: ComponentInfo,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val settingsState = LocalSettingsState.current
    val listState = rememberLazyListState()
    val navBarColor = colors.surface

    PageScaffold(
        backgroundColor = colors.background,
        topSafeAreaColor = navBarColor,
        consumeBottomSafeArea = false
    ) {
        // The list runs under the home indicator (edge to edge) and reserves the inset at its
        // end instead: consuming it in PageScaffold painted a solid strip over the indicator.
        val safeBottom = LocalRuntimeEnvironment.current.safeArea.bottom
        Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Top navigation bar - NavBar
        NavBar(
            title = component.nameEn,
            centerTitle = true,
            useDefaultBack = true,
            onBackClick = onBack,
            backgroundColor = navBarColor
        )

        // Example content area - GearLazyColumn (dismisses floating layers on scroll)
        GearLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            state = listState,
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp + safeBottom)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    content()
                }
            }
        }
        }
    }
}

/**
 * ExampleSection - example block
 *
 * Groups the different examples of one component
 */
@Composable
fun ExampleSection(
    title: String,
    description: String = "",
    useCardContainer: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title and description
        if (title.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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

        if (useCardContainer) {
            // Example content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.lg)
                    .background(colors.surface)
                    .border(1.dp, colors.border, shapes.lg)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                content()
            }
        } else {
            content()
        }
    }
}
