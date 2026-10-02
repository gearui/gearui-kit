package com.gearui.sample.examples.empty

import com.gearui.components.icon.*
import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonTheme
import com.gearui.components.empty.EmptyState
import com.gearui.components.empty.EmptyStatePreset
import com.gearui.components.empty.EmptyStateType
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.typography.IconSizes
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

@Composable
fun EmptyExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(title = "基础用法", description = "默认图标加主文案与描述") {
            EmptyState(message = "暂无内容", description = "这里还没有任何记录")
        }

        ExampleSection(title = "自定义图标", description = "icon 插槽替换默认图标") {
            EmptyState(
                message = "等待处理",
                icon = {
                    Icon(Icons.hourglass,
                        size = IconSizes.Display.md,
                        tint = colors.mutedForeground
                    )
                }
            )
        }

        ExampleSection(title = "带操作", description = "actionText 与 onAction 生成默认操作按钮") {
            EmptyState(
                message = "暂无内容",
                actionText = "刷新",
                onAction = { Toast.show("点击了操作按钮") }
            )
        }

        ExampleSection(title = "自定义操作", description = "customAction 放入任意按钮") {
            EmptyState(
                message = "暂无内容",
                customAction = {
                    Button(
                        text = "清空筛选",
                        theme = ButtonTheme.DANGER,
                        onClick = { Toast.show("点击了自定义操作按钮") }
                    )
                }
            )
        }

        ExampleSection(title = "预设类型", description = "EmptyStatePreset 内置无搜索结果、无网络等文案与图标") {
            EmptyStatePreset(type = EmptyStateType.NO_SEARCH_RESULT)
            EmptyStatePreset(type = EmptyStateType.NO_NETWORK)
        }
    }
}
