package com.gearui.sample.examples.message

import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.components.snackbar.SnackbarType
import com.gearui.components.snackbar.rememberSnackbarController
import com.gearui.components.snackbar.showSuccess
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun MessageExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val message = "这是一条普通的通知信息"
    val longMessage = "这是一条普通的通知信息，这是一条普通的通知信息，这是一条普通的通知信息。"
    val controller = rememberSnackbarController()

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(title = "组件类型", description = "纯文字、图标、关闭、滚动文案、操作按钮") {
            Button(
                text = "纯文字的通知",
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                block = true,
                onClick = { controller.show(message = message, showIcon = false) }
            )
            Button(
                text = "带图标的通知",
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                block = true,
                onClick = { controller.show(message = message, showIcon = true) }
            )
            Button(
                text = "带关闭的通知",
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                block = true,
                onClick = {
                    controller.show(
                        message = message,
                        showIcon = true,
                        showCloseButton = true,
                        duration = 12_000L
                    )
                }
            )
            Button(
                text = "可滚动长文案（模拟）",
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                block = true,
                onClick = {
                    controller.show(
                        message = longMessage,
                        showIcon = false,
                        duration = 6_000L
                    )
                }
            )
            Button(
                text = "带按钮的通知",
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                block = true,
                onClick = {
                    controller.show(
                        message = message,
                        showIcon = true,
                        action = "按钮",
                        onActionClick = { controller.showSuccess("已点击按钮") }
                    )
                }
            )
        }

        ExampleSection(title = "组件状态", description = "普通、成功、警示、错误") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    text = "普通通知",
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f),
                    onClick = { controller.show(message = message, type = SnackbarType.INFO) }
                )
                Button(
                    text = "成功通知",
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f),
                    onClick = { controller.show(message = "这是一条成功的通知信息", type = SnackbarType.SUCCESS) }
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    text = "警示通知",
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f),
                    onClick = { controller.show(message = "这是一条警示的通知信息", type = SnackbarType.WARNING) }
                )
                Button(
                    text = "错误通知",
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f),
                    onClick = { controller.show(message = "这是一条错误的通知信息", type = SnackbarType.ERROR) }
                )
            }
        }
    }
}
