package com.gearui.sample.examples.radio

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.radio.*
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Radio 组件示例
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
        // ========== 组件类型 ==========

        // 纵向单选框
        var verticalSelected by remember { mutableStateOf("0") }
        ExampleSection(
            title = "纵向单选框",
            description = "垂直排列的单选框组"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
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

        // 横向单选框
        var horizontalSelected by remember { mutableStateOf("1") }
        ExampleSection(
            title = "横向单选框",
            description = "水平排列的单选框组"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
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

        // ========== 组件状态 ==========

        // 单选框状态
        ExampleSection(
            title = "单选框状态",
            description = "禁用状态下的选中与未选中"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
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

        // ========== 组件样式 ==========

        // 单选框尺寸
        var sizeSelected by remember { mutableStateOf("medium") }
        ExampleSection(
            title = "单选框尺寸",
            description = "大、中、小三种尺寸"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "大尺寸",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary
                    )
                    RadioButton(
                        selected = sizeSelected == "large",
                        onClick = { sizeSelected = "large" },
                        size = RadioSize.LARGE
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "中尺寸",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary
                    )
                    RadioButton(
                        selected = sizeSelected == "medium",
                        onClick = { sizeSelected = "medium" },
                        size = RadioSize.MEDIUM
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "小尺寸",
                        style = Typography.BodyMedium,
                        color = colors.textSecondary
                    )
                    RadioButton(
                        selected = sizeSelected == "small",
                        onClick = { sizeSelected = "small" },
                        size = RadioSize.SMALL
                    )
                }
            }
        }

        // 勾选显示位置
        var positionSelected1 by remember { mutableStateOf(true) }
        var positionSelected2 by remember { mutableStateOf(true) }
        ExampleSection(
            title = "勾选显示位置",
            description = "单选框在左侧或右侧"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                // 单选框在右侧（默认）
                RadioButtonWithLabel(
                    selected = positionSelected1,
                    onClick = { positionSelected1 = !positionSelected1 },
                    label = "单选框在右侧"
                )
                // 单选框在左侧
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { positionSelected2 = !positionSelected2 }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "单选框在左侧",
                        style = Typography.BodyLarge,
                        color = colors.textPrimary
                    )
                    RadioButton(
                        selected = positionSelected2,
                        onClick = { positionSelected2 = !positionSelected2 }
                    )
                }
            }
        }

        // ========== 特殊样式 ==========

        // 纵向卡片单选框
        var cardVerticalSelected by remember { mutableStateOf("1") }
        ExampleSection(
            title = "纵向卡片单选框",
            description = "带边框的卡片样式"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("0", "1", "2", "3").forEach { id ->
                    RadioCardItem(
                        selected = cardVerticalSelected == id,
                        onClick = { cardVerticalSelected = id },
                        title = "单选",
                        description = "描述信息"
                    )
                }
            }
        }

        // 横向卡片单选框
        var cardHorizontalSelected by remember { mutableStateOf("1") }
        ExampleSection(
            title = "横向卡片单选框",
            description = "横向排列的卡片样式"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RadioCardItemCompact(
                        selected = cardHorizontalSelected == "0",
                        onClick = { cardHorizontalSelected = "0" },
                        title = "单选",
                        modifier = Modifier.weight(1f)
                    )
                    RadioCardItemCompact(
                        selected = cardHorizontalSelected == "1",
                        onClick = { cardHorizontalSelected = "1" },
                        title = "单选",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RadioCardItemCompact(
                        selected = cardHorizontalSelected == "2",
                        onClick = { cardHorizontalSelected = "2" },
                        title = "单选",
                        modifier = Modifier.weight(1f)
                    )
                    RadioCardItemCompact(
                        selected = cardHorizontalSelected == "3",
                        onClick = { cardHorizontalSelected = "3" },
                        title = "单选",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 单选框组
        var groupSelected by remember { mutableStateOf("选项B") }
        ExampleSection(
            title = "单选框组",
            description = "使用 RadioGroup 组件"
        ) {
            RadioGroup(
                options = listOf("选项A", "选项B", "选项C", "选项D"),
                selectedOption = groupSelected,
                onOptionSelected = { groupSelected = it }
            )
        }
    }
}

/**
 * 卡片样式单选项（纵向，带描述）
 */
@Composable
private fun RadioCardItem(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) colors.primary else colors.border,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = Typography.BodyLarge,
                color = colors.textPrimary
            )
            Text(
                text = description,
                style = Typography.BodySmall,
                color = colors.textSecondary
            )
        }
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}

/**
 * 卡片样式单选项（横向，紧凑）
 */
@Composable
private fun RadioCardItemCompact(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) colors.primary else colors.border,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = Typography.BodyMedium,
            color = colors.textPrimary
        )
        RadioButton(
            selected = selected,
            onClick = onClick,
            size = RadioSize.SMALL
        )
    }
}
