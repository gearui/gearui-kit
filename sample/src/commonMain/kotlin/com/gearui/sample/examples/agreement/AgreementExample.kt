package com.gearui.sample.examples.agreement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.button.Button
import com.gearui.components.checkbox.AgreementCheckbox
import com.gearui.components.link.LinkedText
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

private const val Consent = "我已阅读并同意《用户协议》和《隐私政策》"

/**
 * The consent line of a Chinese sign-up page: unchecked by default, each document a
 * link, and a primary button that refuses to proceed until the box is ticked.
 */
@Composable
fun AgreementExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val links = mapOf(
        "《用户协议》" to { Toast.show("打开《用户协议》") },
        "《隐私政策》" to { Toast.show("打开《隐私政策》") },
    )

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "登录前同意",
            description = "默认不勾选；点书名号打开协议，点其余文字切换勾选；未勾选时提示"
        ) {
            var agreed by remember { mutableStateOf(false) }
            Button(
                text = "登录",
                block = true,
                onClick = {
                    if (agreed) Toast.show("登录") else Toast.show("请先阅读并同意用户协议和隐私政策")
                },
            )
            AgreementCheckbox(
                checked = agreed,
                onCheckedChange = { agreed = it },
                text = Consent,
                links = links,
            )
        }

        ExampleSection(
            title = "多行文本",
            description = "文字换行时勾选框与第一行对齐"
        ) {
            var agreed by remember { mutableStateOf(false) }
            AgreementCheckbox(
                checked = agreed,
                onCheckedChange = { agreed = it },
                text = "我已阅读并同意《用户协议》《隐私政策》和《儿童个人信息保护规则》，并授权获取本机号码用于一键登录",
                links = links + ("《儿童个人信息保护规则》" to { Toast.show("打开《儿童个人信息保护规则》") }),
            )
        }

        ExampleSection(
            title = "禁用",
            description = "enabled = false"
        ) {
            AgreementCheckbox(
                checked = true,
                onCheckedChange = {},
                text = Consent,
                links = links,
                enabled = false,
            )
        }

        ExampleSection(
            title = "LinkedText",
            description = "一句话里的多个链接，由整句文案和其中的短语定义，便于翻译"
        ) {
            LinkedText(
                text = "注册即表示同意《服务条款》，如有疑问请联系客服",
                links = mapOf(
                    "《服务条款》" to { Toast.show("打开《服务条款》") },
                    "联系客服" to { Toast.show("联系客服") },
                ),
                style = Theme.typography.bodyMedium,
            )
        }
    }
}
