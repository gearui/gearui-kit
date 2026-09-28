package com.gearui.sample.examples.tooltip

import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.tooltip.Tooltip
import com.gearui.components.tooltip.TooltipPlacement
import com.gearui.components.tooltip.TooltipTheme
import com.gearui.components.tooltip.rememberTooltipState
import com.gearui.foundation.layout.Spacing
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row

/**
 * Tooltip: a short hint anchored to its trigger, dismissed automatically.
 */
@Composable
fun TooltipExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val topState = rememberTooltipState()
    val bottomState = rememberTooltipState()
    val darkState = rememberTooltipState()
    val lightState = rememberTooltipState()

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "位置",
            description = "placement 决定提示出现在触发元素的上方或下方"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Tooltip(
                    text = "提示显示在按钮上方",
                    state = topState,
                    placement = TooltipPlacement.TOP
                ) { onClick ->
                    Button(text = "上方", onClick = onClick)
                }
                Tooltip(
                    text = "提示显示在按钮下方",
                    state = bottomState,
                    placement = TooltipPlacement.BOTTOM
                ) { onClick ->
                    Button(text = "下方", onClick = onClick)
                }
            }
        }

        ExampleSection(
            title = "主题",
            description = "theme：DARK 深色（默认）、LIGHT 浅色"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Tooltip(
                    text = "深色提示",
                    state = darkState,
                    theme = TooltipTheme.DARK
                ) { onClick ->
                    Button(text = "深色", onClick = onClick)
                }
                Tooltip(
                    text = "浅色提示",
                    state = lightState,
                    theme = TooltipTheme.LIGHT
                ) { onClick ->
                    Button(text = "浅色", onClick = onClick)
                }
            }
        }
    }
}
