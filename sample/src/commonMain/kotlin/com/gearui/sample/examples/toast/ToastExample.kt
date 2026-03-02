package com.gearui.sample.examples.toast

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Toast 轻提示组件示例
 *
 */
@Composable
fun ToastExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 纯文字提示
        ExampleSection(
            title = "纯文字",
            description = "最基础的 Toast，仅显示文字"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    text = "纯文字提示",
                    onClick = {
                        Toast.show("这是一条提示消息")
                    },
                    size = ButtonSize.MEDIUM
                )
                Button(
                    text = "长文本提示",
                    onClick = {
                        Toast.show("这是一条较长的提示消息，用于测试多行文本的显示效果")
                    },
                    size = ButtonSize.MEDIUM
                )
            }
        }

        // 成功提示
        ExampleSection(
            title = "成功提示",
            description = "操作成功后的反馈"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    text = "成功提示",
                    onClick = {
                        Toast.success("操作成功")
                    },
                    size = ButtonSize.MEDIUM,
                    theme = ButtonTheme.SUCCESS
                )
                Button(
                    text = "保存成功",
                    onClick = {
                        Toast.success("保存成功")
                    },
                    size = ButtonSize.MEDIUM,
                    theme = ButtonTheme.SUCCESS
                )
            }
        }

        // 错误提示
        ExampleSection(
            title = "错误提示",
            description = "操作失败后的反馈"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    text = "错误提示",
                    onClick = {
                        Toast.error("操作失败")
                    },
                    size = ButtonSize.MEDIUM,
                    theme = ButtonTheme.DANGER
                )
                Button(
                    text = "网络错误",
                    onClick = {
                        Toast.error("网络连接失败，请检查网络")
                    },
                    size = ButtonSize.MEDIUM,
                    theme = ButtonTheme.DANGER
                )
            }
        }

        // 警告提示
        ExampleSection(
            title = "警告提示",
            description = "需要用户注意的信息"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    text = "警告提示",
                    onClick = {
                        Toast.warning("请注意检查输入")
                    },
                    size = ButtonSize.MEDIUM,
                    theme = ButtonTheme.WARNING
                )
                Button(
                    text = "余额不足",
                    onClick = {
                        Toast.warning("账户余额不足")
                    },
                    size = ButtonSize.MEDIUM,
                    theme = ButtonTheme.WARNING
                )
            }
        }

        // 自定义时长
        ExampleSection(
            title = "自定义时长",
            description = "控制 Toast 显示时间"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    text = "短时提示 (1秒)",
                    onClick = {
                        Toast.show("短时提示", duration = 1000L)
                    },
                    size = ButtonSize.MEDIUM
                )
                Button(
                    text = "长时提示 (4秒)",
                    onClick = {
                        Toast.show("长时提示", duration = 4000L)
                    },
                    size = ButtonSize.MEDIUM
                )
            }
        }

        // 连续提示
        ExampleSection(
            title = "连续提示",
            description = "多个 Toast 排队显示"
        ) {
            Button(
                text = "连续3条提示",
                onClick = {
                    Toast.show("第一条提示")
                    Toast.success("第二条提示")
                    Toast.warning("第三条提示")
                },
                size = ButtonSize.MEDIUM
            )
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Toast 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. Toast.show(): 纯文字提示",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. Toast.success(): 成功提示",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. Toast.error(): 错误提示",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. Toast.warning(): 警告提示",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. 队列管理：多个 Toast 按顺序显示",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "6. 自动消失：默认 2 秒后消失",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
