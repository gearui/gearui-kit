package com.gearui.sample.examples.cell

import androidx.compose.runtime.Composable
import com.gearui.components.cell.Cell
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Column

/**
 * Cell 组件示例
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
            title = "基础用法",
            description = "标题、说明、箭头与点击事件"
        ) {
            Column {
                Cell(
                    title = "基础单元格",
                    note = "说明文字"
                )
                Cell(
                    title = "可点击单元格",
                    note = "点击查看",
                    arrow = true,
                    onClick = { Toast.show("点击了 Cell") }
                )
                Cell(
                    title = "带描述信息",
                    description = "这是额外的描述文本，用于补充说明"
                )
            }
        }

        ExampleSection(
            title = "扩展插槽",
            description = "前置/后置自定义内容"
        ) {
            Column {
                Cell(
                    title = "前置内容",
                    leading = {
                        Text(
                            text = "A",
                            style = Typography.BodyLarge,
                            color = colors.primary
                        )
                    }
                )
                Cell(
                    title = "后置内容",
                    trailing = {
                        Text(
                            text = "ON",
                            style = Typography.BodySmall,
                            color = colors.success
                        )
                    }
                )
            }
        }
    }
}
