package com.gearui.sample.examples.alert

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.alert.Alert
import com.gearui.components.alert.AlertStatus
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun AlertExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            useCardContainer = false,
            title = "状态",
            description = "默认、强调、成功、警告、危险；标题用状态色，说明为灰色"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Alert(title = "有 3 条未读通知")
                Alert(
                    title = "已启用双因素验证",
                    description = "下次登录需要输入验证码。",
                    status = AlertStatus.ACCENT,
                )
                Alert(title = "资料已保存", status = AlertStatus.SUCCESS)
                Alert(
                    title = "存储空间不足",
                    description = "剩余 120 MB，建议清理缓存。",
                    status = AlertStatus.WARNING,
                )
                Alert(
                    title = "支付失败",
                    description = "银行返回:余额不足。",
                    status = AlertStatus.DANGER,
                )
            }
        }

        ExampleSection(
            useCardContainer = false,
            title = "操作与关闭",
            description = "action 放按钮，onClose 显示关闭按钮"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Alert(
                    title = "同步失败",
                    description = "网络连接已断开。",
                    status = AlertStatus.DANGER,
                    action = {
                        Button(
                            text = "重试",
                            size = ButtonSize.SMALL,
                            type = ButtonType.OUTLINE,
                            onClick = { Toast.show("重试同步") },
                        )
                    },
                )
                if (visible) {
                    Alert(
                        title = "新版本 1.2.0 已发布",
                        description = "包含性能优化与问题修复。",
                        status = AlertStatus.ACCENT,
                        onClose = { visible = false },
                    )
                } else {
                    Button(text = "重新显示", size = ButtonSize.SMALL, onClick = { visible = true })
                }
            }
        }

        ExampleSection(
            useCardContainer = false,
            title = "图标",
            description = "自定义图标，或不显示图标"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Alert(title = "会员将于 3 天后到期", icon = Icons.clock)
                Alert(title = "无图标的提示", showIcon = false, modifier = Modifier)
            }
        }
    }
}
