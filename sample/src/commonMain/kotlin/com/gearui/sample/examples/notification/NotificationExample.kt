package com.gearui.sample.examples.notification

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.components.button.ButtonTheme
import com.gearui.components.notification.*
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Notification component examples
 *
 * - a notification card dropping in from the top
 * - title plus detail
 * - four states
 * - an action button
 */
@Composable
fun NotificationExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // Notification controller backed by the Overlay system
    val notificationController = rememberNotificationController()

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== Component types ==========

        // Title-only notification
        ExampleSection(
            title = "纯标题的通知",
            description = "只显示标题，不带详细描述"
        ) {
            Button(
                text = "纯标题的通知",
                onClick = {
                    notificationController.show(
                        title = "这是一条普通的通知信息",
                        type = NotificationType.INFO
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Notification with a description
        ExampleSection(
            title = "带描述的通知",
            description = "显示标题和详细描述"
        ) {
            Button(
                text = "带描述的通知",
                onClick = {
                    notificationController.show(
                        title = "系统通知",
                        message = "这是一条带有详细描述的通知信息，可以包含更多内容",
                        type = NotificationType.INFO
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Notification with an action
        ExampleSection(
            title = "带操作的通知",
            description = "带有操作按钮的通知"
        ) {
            Button(
                text = "带操作的通知",
                onClick = {
                    notificationController.show(
                        title = "新版本可用",
                        message = "发现新版本 v2.0.0，建议更新以获得更好的体验",
                        type = NotificationType.INFO,
                        action = "立即更新",
                        onAction = {
                            notificationController.showSuccess(
                                title = "开始更新",
                                message = "正在下载新版本..."
                            )
                        },
                        duration = 10000L
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Non-dismissable notification
        ExampleSection(
            title = "不可关闭的通知",
            description = "隐藏关闭按钮，只能自动消失"
        ) {
            Button(
                text = "不可关闭的通知",
                onClick = {
                    notificationController.show(
                        title = "重要通知",
                        message = "系统正在维护中，请稍后再试",
                        type = NotificationType.WARNING,
                        closable = false,
                        duration = 3000L
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ========== Component states ==========

        // Normal notification
        ExampleSection(
            title = "普通通知",
            description = "普通信息提示"
        ) {
            Button(
                text = "普通通知",
                onClick = {
                    notificationController.showInfo(
                        title = "提示",
                        message = "这是一条普通的通知信息"
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Success notification
        ExampleSection(
            title = "成功通知",
            description = "操作成功时的通知"
        ) {
            Button(
                text = "成功通知",
                onClick = {
                    notificationController.showSuccess(
                        title = "操作成功",
                        message = "您的数据已成功保存"
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Warning notification
        ExampleSection(
            title = "警示通知",
            description = "警告信息的通知"
        ) {
            Button(
                text = "警示通知",
                onClick = {
                    notificationController.showWarning(
                        title = "警告提示",
                        message = "您的账户即将过期，请及时续费"
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Error notification
        ExampleSection(
            title = "错误通知",
            description = "错误信息的通知"
        ) {
            Button(
                text = "错误通知",
                onClick = {
                    notificationController.showError(
                        title = "操作失败",
                        message = "网络连接失败，请检查网络设置"
                    )
                },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ========== Rapid switching test ==========

        ExampleSection(
            title = "快速切换测试",
            description = "快速点击不同按钮测试切换效果"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    text = "信息",
                    onClick = {
                        notificationController.showInfo(
                            title = "信息通知"
                        )
                    },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "成功",
                    onClick = {
                        notificationController.showSuccess(
                            title = "成功通知"
                        )
                    },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "警告",
                    onClick = {
                        notificationController.showWarning(
                            title = "警告通知"
                        )
                    },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "错误",
                    onClick = {
                        notificationController.showError(
                            title = "错误通知"
                        )
                    },
                    size = ButtonSize.SMALL,
                    theme = ButtonTheme.DANGER,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
