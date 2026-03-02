package com.gearui.sample.examples.result

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
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
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Result 组件示例
 *
 * 结果页组件，用于展示操作结果反馈
 */
@Composable
fun ResultExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 操作结果提示
    var actionResult by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 成功结果
        ExampleSection(
            title = "成功结果",
            description = "操作成功的反馈页面"
        ) {
            SuccessResult(
                title = "提交成功",
                description = "您的申请已提交，我们会尽快处理",
                primaryAction = {
                    Button(
                        text = "返回首页",
                        onClick = { actionResult = "点击了返回首页" },
                        size = ButtonSize.MEDIUM
                    )
                },
                secondaryAction = {
                    Button(
                        text = "查看详情",
                        onClick = { actionResult = "点击了查看详情" },
                        size = ButtonSize.MEDIUM,
                        theme = ButtonTheme.PRIMARY
                    )
                }
            )
        }

        // 错误结果
        ExampleSection(
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
                        size = ButtonSize.MEDIUM,
                        theme = ButtonTheme.DANGER
                    )
                }
            )
        }

        // 警告结果
        ExampleSection(
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
                        onClick = { actionResult = "点击了立即修改" },
                        size = ButtonSize.MEDIUM
                    )
                }
            )
        }

        // 信息提示
        ExampleSection(
            title = "信息提示",
            description = "普通信息的反馈页面"
        ) {
            Result(
                status = ResultStatus.INFO,
                title = "温馨提示",
                description = "系统将于今晚 22:00 进行维护，届时服务可能暂时不可用"
            )
        }

        // 404 页面
        ExampleSection(
            title = "404 页面",
            description = "页面不存在的反馈"
        ) {
            NotFoundResult(
                primaryAction = {
                    Button(
                        text = "返回首页",
                        onClick = { actionResult = "点击了返回首页" },
                        size = ButtonSize.MEDIUM
                    )
                }
            )
        }

        // 403 无权限
        ExampleSection(
            title = "403 无权限",
            description = "无权访问的反馈页面"
        ) {
            ForbiddenResult(
                primaryAction = {
                    Button(
                        text = "申请权限",
                        onClick = { actionResult = "点击了申请权限" },
                        size = ButtonSize.MEDIUM
                    )
                }
            )
        }

        // 空数据
        ExampleSection(
            title = "空数据状态",
            description = "暂无数据的反馈页面"
        ) {
            EmptyResult(
                title = "暂无数据",
                description = "还没有任何记录，快去创建一个吧",
                primaryAction = {
                    Button(
                        text = "立即创建",
                        onClick = { actionResult = "点击了立即创建" },
                        size = ButtonSize.MEDIUM
                    )
                }
            )
        }

        // 网络错误
        ExampleSection(
            title = "网络错误",
            description = "网络异常的反馈页面"
        ) {
            NetworkErrorResult(
                onRetry = { actionResult = "点击了重试" }
            )
        }

        // 操作结果提示
        if (actionResult.isNotEmpty()) {
            ExampleSection(
                title = "操作反馈",
                description = "按钮点击结果"
            ) {
                Text(
                    text = actionResult,
                    style = Typography.BodyMedium,
                    color = colors.primary
                )
            }
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Result 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. 支持多种状态: SUCCESS, ERROR, WARNING, INFO 等",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. 提供便捷方法: SuccessResult, ErrorResult 等",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. 支持自定义图标和描述",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. 支持主操作和次操作按钮",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. 支持额外内容插槽",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
