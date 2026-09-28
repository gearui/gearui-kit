package com.gearui.sample.examples.collapse

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.collapse.Collapse
import com.gearui.components.collapse.CollapsePanel
import com.gearui.components.collapse.CollapseStyle
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme

private const val SAMPLE_CONTENT = "折叠面板内容区域可以自定义，支持任意内容。" +
        "GearUI 是一个基于 Compose Multiplatform 的跨平台 UI 组件库，" +
        "提供丰富的组件和主题支持，帮助开发者快速构建现代化的用户界面。"

private const val PANEL_COUNT = 3

@Composable
private fun PanelHeader(index: Int) {
    Text(
        text = "标题 ${index + 1}",
        style = Theme.typography.titleMedium,
        color = Theme.colors.foreground
    )
}

@Composable
private fun PanelBody() {
    Text(
        text = SAMPLE_CONTENT,
        style = Theme.typography.bodyMedium,
        color = Theme.colors.mutedForeground
    )
}

/** A controlled Collapse: each panel toggles on its own. */
@Composable
private fun ControlledCollapse(
    style: CollapseStyle,
    expandIconText: ((Boolean) -> String)? = null
) {
    var expanded by remember { mutableStateOf(List(PANEL_COUNT) { false }) }
    Collapse(
        style = style,
        expansionCallback = { index, isExpanded ->
            expanded = expanded.toMutableList().apply { this[index] = !isExpanded }
        },
        children = expanded.mapIndexed { index, isExpanded ->
            CollapsePanel(
                headerBuilder = { PanelHeader(index) },
                expandIconTextBuilder = expandIconText,
                isExpanded = isExpanded,
                body = { PanelBody() }
            )
        }
    )
}

/**
 * Collapse component examples
 */
@Composable
fun CollapseExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "通栏样式",
            description = "CollapseStyle.Block，各面板独立展开"
        ) {
            ControlledCollapse(style = CollapseStyle.Block)
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "卡片样式",
            description = "CollapseStyle.Card，整体带圆角"
        ) {
            ControlledCollapse(style = CollapseStyle.Card)
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "带操作说明",
            description = "展开图标旁显示「展开 / 收起」文案"
        ) {
            ControlledCollapse(
                style = CollapseStyle.Block,
                expandIconText = { isExpanded -> if (isExpanded) "收起" else "展开" }
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "手风琴",
            description = "Collapse.Accordion 同时只展开一个面板"
        ) {
            Collapse.Accordion(
                style = CollapseStyle.Block,
                children = (0 until PANEL_COUNT).map { index ->
                    CollapsePanel(
                        value = index,
                        headerBuilder = { PanelHeader(index) },
                        body = { PanelBody() }
                    )
                }
            )
        }
    }
}
