package com.gearui.sample.examples.notification

import androidx.compose.runtime.Composable
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.notification.NotificationType
import com.gearui.components.notification.rememberNotificationController
import com.gearui.components.notification.showError
import com.gearui.components.notification.showInfo
import com.gearui.components.notification.showSuccess
import com.gearui.components.notification.showWarning
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface

private const val ActionNotificationMillis = 10_000L
private const val PersistentNotificationMillis = 3_000L

private class NotificationRow(val title: String, val description: String? = null, val onClick: () -> Unit)

/**
 * Notification: a card that drops in from the top of the screen.
 */
@Composable
fun NotificationExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    // Notification controller backed by the Overlay system
    val notificationController = rememberNotificationController()

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "内容",
            description = "只有 title，或 title 加 message"
        ) {
            NotificationRows(
                listOf(
                    NotificationRow("纯标题") {
                        notificationController.show(
                            title = "这是一条普通的通知信息",
                            type = NotificationType.INFO
                        )
                    },
                    NotificationRow("标题加描述") {
                        notificationController.show(
                            title = "系统通知",
                            message = "这是一条带有详细描述的通知信息，可以包含更多内容",
                            type = NotificationType.INFO
                        )
                    },
                )
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "状态",
            description = "showInfo、showSuccess、showWarning、showError"
        ) {
            NotificationRows(
                listOf(
                    NotificationRow("普通通知", "showInfo") {
                        notificationController.showInfo(title = "提示", message = "这是一条普通的通知信息")
                    },
                    NotificationRow("成功通知", "showSuccess") {
                        notificationController.showSuccess(title = "操作成功", message = "您的数据已成功保存")
                    },
                    NotificationRow("警示通知", "showWarning") {
                        notificationController.showWarning(title = "警告提示", message = "您的账户即将过期，请及时续费")
                    },
                    NotificationRow("错误通知", "showError") {
                        notificationController.showError(title = "操作失败", message = "网络连接失败，请检查网络设置")
                    },
                )
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "操作与关闭",
            description = "action 在卡片内放按钮；closable = false 隐藏关闭按钮"
        ) {
            NotificationRows(
                listOf(
                    NotificationRow("带操作的通知", "卡片内带按钮，停留 10 秒") {
                        notificationController.show(
                            title = "新版本可用",
                            message = "发现新版本 v2.0.0，建议更新以获得更好的体验",
                            type = NotificationType.INFO,
                            action = "立即更新",
                            onAction = {
                                notificationController.showSuccess(
                                    title = "开始更新",
                                    message = "正在下载新版本…"
                                )
                            },
                            duration = ActionNotificationMillis
                        )
                    },
                    NotificationRow("不可关闭的通知", "没有关闭按钮，只能自动消失") {
                        notificationController.show(
                            title = "重要通知",
                            message = "系统正在维护中，请稍后再试",
                            type = NotificationType.WARNING,
                            closable = false,
                            duration = PersistentNotificationMillis
                        )
                    },
                )
            )
        }
    }
}

// One arrow row per trigger (non-anchored overlay entries, see COMPONENT_SPEC §6).
@Composable
private fun NotificationRows(rows: List<NotificationRow>) {
    CellGroup(items = rows) { row ->
        Cell(
            title = row.title,
            description = row.description,
            arrow = true,
            onClick = row.onClick
        )
    }
}
