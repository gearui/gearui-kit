package com.gearui.sample.examples.cell

import androidx.compose.runtime.Composable
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme

/**
 * Cell component examples
 */
@Composable
fun CellExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "基础用法",
            description = "Cell 只管一行：标题、说明、右侧值、箭头；分隔线归 CellGroup 管"
        ) {
            CellGroup(items = listOf(0, 1, 2)) { index ->
                when (index) {
                    0 -> Cell(
                        title = "基础单元格",
                        note = "说明文字"
                    )
                    1 -> Cell(
                        title = "可点击单元格",
                        note = "点击查看",
                        arrow = true,
                        onClick = { Toast.show("点击了 Cell") }
                    )
                    else -> Cell(
                        title = "带描述信息",
                        description = "这是额外的描述文本，用于补充说明"
                    )
                }
            }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "扩展插槽",
            description = "leading 放图标，trailing 放自定义的右侧内容"
        ) {
            CellGroup(items = listOf(0, 1)) { index ->
                when (index) {
                    0 -> Cell(
                        title = "前置内容",
                        leading = {
                            Icon(
                                name = Icons.bell,
                                size = IconSizes.Default.md,
                                tint = colors.primary
                            )
                        }
                    )
                    else -> Cell(
                        title = "后置内容",
                        trailing = {
                            Text(
                                text = "已开启",
                                style = Theme.typography.bodySmall,
                                color = colors.success
                            )
                        }
                    )
                }
            }
        }
    }
}
