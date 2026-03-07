package com.gearui.sample.examples.refresh

import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * 下拉刷新展示页。
 *
 * 说明：KuiklyUI 已提供该能力，本页先提供入口和交互预期说明，后续补充完整示例。
 */
@Composable
fun RefreshExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "下拉刷新（展示）",
            description = "KuiklyUI 已支持 pull-to-refresh，本示例先展示入口和预期行为。"
        ) {
            Text(
                text = "预期行为：下拉触发刷新、展示刷新中状态、完成后回弹并更新列表数据。",
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )
            Button(
                text = "模拟刷新完成",
                theme = ButtonTheme.PRIMARY,
                type = ButtonType.GHOST,
                block = true,
                onClick = { Toast.show("刷新完成（演示）") }
            )
        }
    }
}
