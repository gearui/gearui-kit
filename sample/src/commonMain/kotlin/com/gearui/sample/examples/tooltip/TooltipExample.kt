package com.gearui.sample.examples.tooltip

import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonType
import com.gearui.components.tooltip.Tooltip
import com.gearui.components.tooltip.TooltipPlacement
import com.gearui.components.tooltip.rememberTooltipState
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection

@Composable
fun TooltipExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val topState = rememberTooltipState()
    val bottomState = rememberTooltipState()

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "Basic Usage",
            description = "Tap a button to show tooltip in different placements."
        ) {
            Tooltip(
                text = "Tooltip shown above the trigger.",
                state = topState,
                placement = TooltipPlacement.TOP
            ) { onClick ->
                Button(
                    text = "Show Top Tooltip",
                    type = ButtonType.OUTLINE,
                    onClick = onClick
                )
            }

            Tooltip(
                text = "Tooltip shown below the trigger.",
                state = bottomState,
                placement = TooltipPlacement.BOTTOM
            ) { onClick ->
                Button(
                    text = "Show Bottom Tooltip",
                    type = ButtonType.OUTLINE,
                    onClick = onClick
                )
            }
        }
    }
}
