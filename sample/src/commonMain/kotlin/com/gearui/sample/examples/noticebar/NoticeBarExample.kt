package com.gearui.sample.examples.noticebar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.button.Button
import com.gearui.components.icon.Icons
import com.gearui.components.link.LinkButton
import com.gearui.components.noticebar.NoticeBar
import com.gearui.components.noticebar.NoticeBarTone
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.foundation.layout.Spacing
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column

/**
 * NoticeBar: a one-line announcement strip, scrolling when the text overflows.
 */
@Composable
fun NoticeBarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "滚动公告",
            description = "文字超出宽度才滚动，循环之间留空隙"
        ) {
            NoticeBar(
                text = "系统将于今晚 23:00 至次日 00:00 进行维护升级，期间充值与提现暂停服务，给您带来不便敬请谅解。",
            )
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "语气",
            description = "用 soft 底色与 soft 前景，与 Alert 同一套状态色"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                NoticeBar("新版本已发布，去看看更新了什么", tone = NoticeBarTone.INFO)
                NoticeBar("实名认证已通过", tone = NoticeBarTone.SUCCESS)
                NoticeBar("账户余额不足，请及时充值", tone = NoticeBarTone.WARNING)
                NoticeBar("检测到异地登录，请确认是否本人操作", tone = NoticeBarTone.DANGER)
                NoticeBar("群主设置了公告", tone = NoticeBarTone.NEUTRAL, icon = Icons.bell)
            }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "操作与关闭",
            description = "action 放入口，onClose 可关闭"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                NoticeBar(
                    text = "您有 2 笔待支付订单",
                    tone = NoticeBarTone.WARNING,
                    action = { LinkButton("去支付", onClick = { Toast.show("去支付") }) },
                )
                if (visible) {
                    NoticeBar(
                        text = "点击右侧关闭这条公告",
                        onClose = { visible = false },
                        onClick = { Toast.show("点击了公告") },
                    )
                } else {
                    Button(text = "重新显示", block = true, onClick = { visible = true })
                }
            }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "不滚动",
            description = "scroll = false 时超长文字省略"
        ) {
            NoticeBar(
                text = "不滚动的公告：超出部分直接省略，不再一直移动，适合放在长期可见的位置。",
                scroll = false,
                tone = NoticeBarTone.NEUTRAL,
            )
        }
    }
}
