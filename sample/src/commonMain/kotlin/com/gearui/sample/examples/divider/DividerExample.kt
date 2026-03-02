package com.gearui.sample.examples.divider

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.primitives.Divider
import com.gearui.primitives.DividerFull
import com.gearui.primitives.DividerInset
import com.gearui.primitives.DividerSection
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Divider 组件示例
 */
@Composable
fun DividerExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 全宽分割线
        ExampleSection(
            title = "全宽分割线",
            description = "占据容器全部宽度的分割线"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "上方内容",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
                DividerFull()
                Text(
                    text = "下方内容",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
            }
        }

        // 缩进分割线
        ExampleSection(
            title = "缩进分割线",
            description = "左侧有缩进的分割线，常用于列表项"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "列表项 1",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
                DividerInset()
                Text(
                    text = "列表项 2",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
                DividerInset()
                Text(
                    text = "列表项 3",
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )
            }
        }

        // 章节分隔
        ExampleSection(
            title = "章节分隔",
            description = "用于分隔不同章节的粗分割线"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "章节 1 内容",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                }
                DividerSection()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "章节 2 内容",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // 自定义分割线
        ExampleSection(
            title = "自定义分割线",
            description = "自定义粗细和缩进的分割线"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Divider(thickness = 1.dp)
                Divider(thickness = 2.dp)
                Divider(insetStart = 32.dp, insetEnd = 32.dp)
            }
        }
    }
}
