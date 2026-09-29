package com.gearui.sample.examples.radio

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.radio.*
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme

/**
 * Radio component examples
 */
@Composable
fun RadioExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== Component types ==========

        // Vertical radio buttons
        var verticalSelected by remember { mutableStateOf("0") }
        ExampleSection(
            title = "纵向单选框",
            description = "RadioButtonWithLabel 纵向排列"
        ) {
            Column {
                RadioButtonWithLabel(
                    selected = verticalSelected == "0",
                    onClick = { verticalSelected = "0" },
                    label = "单选标题"
                )
                RadioButtonWithLabel(
                    selected = verticalSelected == "1",
                    onClick = { verticalSelected = "1" },
                    label = "单选标题"
                )
                RadioButtonWithLabel(
                    selected = verticalSelected == "2",
                    onClick = { verticalSelected = "2" },
                    label = "单选标题"
                )
            }
        }

        // Horizontal radio buttons
        var horizontalSelected by remember { mutableStateOf("1") }
        ExampleSection(
            title = "横向单选框",
            description = "RadioButtonWithLabel 横向排列"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xl),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButtonWithLabel(
                    selected = horizontalSelected == "0",
                    onClick = { horizontalSelected = "0" },
                    label = "单选"
                )
                RadioButtonWithLabel(
                    selected = horizontalSelected == "1",
                    onClick = { horizontalSelected = "1" },
                    label = "单选"
                )
                RadioButtonWithLabel(
                    selected = horizontalSelected == "2",
                    onClick = { horizontalSelected = "2" },
                    label = "四字"
                )
            }
        }

        // ========== Component states ==========

        // Radio button states
        ExampleSection(
            title = "单选框状态",
            description = "禁用状态下的选中与未选中"
        ) {
            Column {
                RadioButtonWithLabel(
                    selected = true,
                    onClick = { },
                    label = "选项禁用-已选",
                    enabled = false
                )
                RadioButtonWithLabel(
                    selected = false,
                    onClick = { },
                    label = "选项禁用-默认",
                    enabled = false
                )
            }
        }

        // ========== Component styles ==========

        // Radio button sizes
        var sizeSelected by remember { mutableStateOf("medium") }
        ExampleSection(
            title = "单选框尺寸",
            description = "RadioSize 大、中、小三档"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "大尺寸",
                        style = Theme.typography.bodyMedium,
                        color = colors.mutedForeground
                    )
                    RadioButton(
                        selected = sizeSelected == "large",
                        onClick = { sizeSelected = "large" },
                        size = RadioSize.LARGE,
                        contentDescription = "大尺寸"
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "中尺寸",
                        style = Theme.typography.bodyMedium,
                        color = colors.mutedForeground
                    )
                    RadioButton(
                        selected = sizeSelected == "medium",
                        onClick = { sizeSelected = "medium" },
                        size = RadioSize.MEDIUM,
                        contentDescription = "中尺寸"
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "小尺寸",
                        style = Theme.typography.bodyMedium,
                        color = colors.mutedForeground
                    )
                    RadioButton(
                        selected = sizeSelected == "small",
                        onClick = { sizeSelected = "small" },
                        size = RadioSize.SMALL,
                        contentDescription = "小尺寸"
                    )
                }
            }
        }

        // Radio in list rows: the mark trails, each row carries a description
        var rowSelected by remember { mutableStateOf(1) }
        ExampleSection(
            title = "列表单选",
            description = "Cell 整行可点，RadioButton 放在 trailing",
            surface = SectionSurface.Plain
        ) {
            CellGroup(items = listOf(0, 1, 2)) { index ->
                Cell(
                    title = "方案 ${index + 1}",
                    description = "描述信息",
                    onClick = { rowSelected = index },
                    trailing = {
                        RadioButton(
                            selected = rowSelected == index,
                            onClick = { rowSelected = index }
                        )
                    }
                )
            }
        }

        // Marker position: the mark leads the row
        var leadingSelected by remember { mutableStateOf(0) }
        ExampleSection(
            title = "勾选在左侧",
            description = "RadioButton 放在 Cell 的 leading",
            surface = SectionSurface.Plain
        ) {
            CellGroup(items = listOf(0, 1)) { index ->
                Cell(
                    title = if (index == 0) "按时间排序" else "按热度排序",
                    onClick = { leadingSelected = index },
                    leading = {
                        RadioButton(
                            selected = leadingSelected == index,
                            onClick = { leadingSelected = index }
                        )
                    }
                )
            }
        }

        // Radio group
        var groupSelected by remember { mutableStateOf("选项B") }
        ExampleSection(
            title = "单选框组",
            description = "RadioGroup 由选项列表生成一组单选"
        ) {
            RadioGroup(
                options = listOf("选项A", "选项B", "选项C", "选项D"),
                selectedOption = groupSelected,
                onOptionSelected = { groupSelected = it }
            )
        }
    }
}
