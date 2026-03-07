package com.gearui.sample.examples.common

import androidx.compose.runtime.Composable
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * 通用占位示例页：用于已注册但尚未完成细节对齐的组件。
 */
@Composable
fun ComingSoonExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "示例状态",
            description = "该组件入口已对齐，细节实现正在补齐。"
        ) {
            Text(
                text = "当前状态：可展示入口，后续完善交互与视觉细节。",
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )
        }
    }
}
