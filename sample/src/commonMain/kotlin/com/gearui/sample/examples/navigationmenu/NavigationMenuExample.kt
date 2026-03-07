package com.gearui.sample.examples.navigationmenu

import androidx.compose.runtime.*
import com.gearui.Spacing
import com.gearui.components.navigationmenu.NavigationMenu
import com.gearui.components.navigationmenu.NavigationMenuSection
import com.gearui.components.navigationmenu.NavigationMenuItem
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun NavigationMenuExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    var selectedSection by remember { mutableStateOf("getting-started") }
    var selectedItem by remember { mutableStateOf("intro") }

    val sections = listOf(
        NavigationMenuSection(
            id = "getting-started",
            label = "Getting started",
            items = listOf(
                NavigationMenuItem("intro", "Introduction", "Reusable components built with GearUI."),
                NavigationMenuItem("install", "Installation", "How to install dependencies and structure your app."),
                NavigationMenuItem("typography", "Typography", "Styles for headings, paragraphs, lists...etc")
            )
        ),
        NavigationMenuSection(
            id = "components",
            label = "Components",
            items = listOf(
                NavigationMenuItem("button", "Button", "Press action entry."),
                NavigationMenuItem("dialog", "Dialog", "Modal interaction entry."),
                NavigationMenuItem("tooltip", "Tooltip", "Lightweight text hints.")
            )
        ),
        NavigationMenuSection(
            id = "docs",
            label = "Docs",
            items = emptyList()
        )
    )

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "Navigation Menu",
            description = "Top triggers + dropdown panel style.",
            useCardContainer = false
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.large)
                    .background(colors.surface)
                    .border(1.dp, colors.border, shapes.large)
                    .padding(Spacing.spacer16.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)
            ) {
                NavigationMenu(
                    sections = sections,
                    selectedSectionId = selectedSection,
                    selectedItemId = selectedItem,
                    onSectionSelect = { selectedSection = it },
                    onItemSelect = { selectedItem = it }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(shapes.default)
                        .background(colors.background)
                        .padding(Spacing.spacer12.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
                ) {
                    Text(
                        text = "Selected Section: $selectedSection",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Selected Item: $selectedItem",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}
