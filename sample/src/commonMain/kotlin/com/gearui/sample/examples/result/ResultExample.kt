package com.gearui.sample.examples.result

import androidx.compose.runtime.*
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.result.Result
import com.gearui.components.result.ResultStatus
import com.gearui.components.result.SuccessResult
import com.gearui.components.result.ErrorResult
import com.gearui.components.result.NotFoundResult
import com.gearui.components.result.ForbiddenResult
import com.gearui.components.result.EmptyResult
import com.gearui.components.result.NetworkErrorResult
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme

/**
 * Result component examples: feedback shown after an action.
 *
 * Result is a page-level block rather than a control, so every section is Plain.
 */
@Composable
fun ResultExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // Result message
    var actionResult by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // Success
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "成功结果",
            description = "操作成功的反馈页面"
        ) {
            SuccessResult(
                title = "提交成功",
                description = "您的申请已提交，我们会尽快处理",
                primaryAction = {
                    Button(
                        text = "返回首页",
                        onClick = { actionResult = "点击了返回首页" }
                    )
                },
                secondaryAction = {
                    Button(
                        text = "查看详情",
                        onClick = { actionResult = "点击了查看详情" },
                        type = ButtonType.OUTLINE
                    )
                }
            )
        }

        // Error
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "错误结果",
            description = "操作失败的反馈页面"
        ) {
            ErrorResult(
                title = "提交失败",
                description = "网络异常，请稍后重试",
                primaryAction = {
                    Button(
                        text = "重新提交",
                        onClick = { actionResult = "点击了重新提交" },
                        theme = ButtonTheme.DANGER
                    )
                }
            )
        }

        // Warning
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "警告结果",
            description = "警告提示的反馈页面"
        ) {
            Result(
                status = ResultStatus.WARNING,
                title = "警告提示",
                description = "您的账户存在安全风险，请尽快修改密码",
                primaryAction = {
                    Button(
                        text = "立即修改",
                        onClick = { actionResult = "点击了立即修改" }
                    )
                }
            )
        }

        // Information
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "信息提示",
            description = "普通信息的反馈页面"
        ) {
            Result(
                status = ResultStatus.INFO,
                title = "温馨提示",
                description = "系统将于今晚 22:00 进行维护，届时服务可能暂时不可用"
            )
        }

        // 404 page
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "404 页面",
            description = "页面不存在的反馈"
        ) {
            NotFoundResult(
                primaryAction = {
                    Button(
                        text = "返回首页",
                        onClick = { actionResult = "点击了返回首页" }
                    )
                }
            )
        }

        // 403, no permission
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "403 无权限",
            description = "无权访问的反馈页面"
        ) {
            ForbiddenResult(
                primaryAction = {
                    Button(
                        text = "申请权限",
                        onClick = { actionResult = "点击了申请权限" }
                    )
                }
            )
        }

        // Empty data
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "空数据状态",
            description = "暂无数据的反馈页面"
        ) {
            EmptyResult(
                title = "暂无数据",
                description = "还没有任何记录，快去创建一个吧",
                primaryAction = {
                    Button(
                        text = "立即创建",
                        onClick = { actionResult = "点击了立即创建" }
                    )
                }
            )
        }

        // Network error
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "网络错误",
            description = "网络异常的反馈页面"
        ) {
            NetworkErrorResult(
                onRetry = { actionResult = "点击了重试" }
            )
        }

        // Result message
        if (actionResult.isNotEmpty()) {
            ExampleSection(
                title = "操作反馈",
                description = "按钮点击结果"
            ) {
                Text(
                    text = actionResult,
                    style = Theme.typography.bodyMedium,
                    color = colors.primarySoftForeground
                )
            }
        }
    }
}
