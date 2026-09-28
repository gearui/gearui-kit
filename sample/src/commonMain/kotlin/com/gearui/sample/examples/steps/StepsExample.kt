package com.gearui.sample.examples.steps

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.steps.StepItem
import com.gearui.foundation.layout.Spacing
import com.gearui.components.steps.StepStatus
import com.gearui.components.steps.Steps
import com.gearui.components.steps.StepsDirection
import com.gearui.components.steps.StepsTheme
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme

/**
 * Steps component examples
 */
@Composable
fun StepsExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // Interactive steps state
    var currentStep by remember { mutableStateOf(1) }

    // Form wizard state
    var formStep by remember { mutableStateOf(0) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // Basic steps
        ExampleSection(
            title = "基础用法",
            description = "水平步骤条，展示流程进度"
        ) {
            Steps(
                current = 1,
                items = listOf(
                    StepItem(title = "步骤一"),
                    StepItem(title = "步骤二"),
                    StepItem(title = "步骤三"),
                    StepItem(title = "步骤四")
                )
            )
        }

        // Steps with descriptions
        ExampleSection(
            title = "带描述",
            description = "每个步骤可以添加描述信息"
        ) {
            Steps(
                current = 2,
                items = listOf(
                    StepItem(
                        title = "注册账号",
                        description = "填写基本信息"
                    ),
                    StepItem(
                        title = "完善资料",
                        description = "补充详细信息"
                    ),
                    StepItem(
                        title = "身份认证",
                        description = "实名验证"
                    ),
                    StepItem(
                        title = "完成",
                        description = "开始使用"
                    )
                )
            )
        }

        // Interactive steps
        ExampleSection(
            title = "可交互",
            description = "current 随按钮前进或后退"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                Steps(
                    current = currentStep,
                    items = listOf(
                        StepItem(title = "第一步"),
                        StepItem(title = "第二步"),
                        StepItem(title = "第三步"),
                        StepItem(title = "第四步")
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        text = "上一步",
                        onClick = { if (currentStep > 0) currentStep-- },
                        size = ButtonSize.SMALL,
                        theme = ButtonTheme.DEFAULT,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        text = "下一步",
                        onClick = { if (currentStep < 3) currentStep++ },
                        size = ButtonSize.SMALL,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Vertical steps
        ExampleSection(
            title = "垂直布局",
            description = "纵向展示的步骤条"
        ) {
            Steps(
                current = 2,
                direction = StepsDirection.VERTICAL,
                items = listOf(
                    StepItem(
                        title = "提交申请",
                        description = "已于 2024-01-15 提交"
                    ),
                    StepItem(
                        title = "初审",
                        description = "预计 1-2 个工作日"
                    ),
                    StepItem(
                        title = "复审",
                        description = "预计 3-5 个工作日"
                    ),
                    StepItem(
                        title = "审批完成",
                        description = "审批结果将通知您"
                    )
                )
            )
        }

        // Dot theme
        ExampleSection(
            title = "点状主题",
            description = "简洁的点状步骤指示器"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                // Horizontal dots
                Steps(
                    current = 2,
                    theme = StepsTheme.DOT,
                    items = listOf(
                        StepItem(title = "步骤一"),
                        StepItem(title = "步骤二"),
                        StepItem(title = "步骤三"),
                        StepItem(title = "步骤四")
                    )
                )

                // Vertical dots
                Steps(
                    current = 1,
                    direction = StepsDirection.VERTICAL,
                    theme = StepsTheme.DOT,
                    items = listOf(
                        StepItem(title = "开始"),
                        StepItem(title = "处理中"),
                        StepItem(title = "完成")
                    )
                )
            }
        }

        // Error state
        ExampleSection(
            title = "错误状态",
            description = "当前步骤显示错误状态"
        ) {
            Steps(
                current = 2,
                status = StepStatus.ERROR,
                items = listOf(
                    StepItem(
                        title = "填写信息",
                        description = "已完成"
                    ),
                    StepItem(
                        title = "上传文件",
                        description = "已完成"
                    ),
                    StepItem(
                        title = "验证失败",
                        description = "请重新上传"
                    ),
                    StepItem(
                        title = "提交",
                        description = "待处理"
                    )
                )
            )
        }

        // Real use case: a form wizard
        ExampleSection(
            title = "表单向导",
            description = "Steps 与按钮配合完成分步表单"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                Steps(
                    current = formStep,
                    items = listOf(
                        StepItem(
                            title = "基本信息",
                            description = "填写姓名、手机"
                        ),
                        StepItem(
                            title = "详细信息",
                            description = "填写地址、邮箱"
                        ),
                        StepItem(
                            title = "确认提交",
                            description = "核对信息"
                        )
                    )
                )

                // Content of the current step
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.sm)
                ) {
                    when (formStep) {
                        0 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                                Text(
                                    text = "第一步：基本信息",
                                    style = Theme.typography.titleMedium,
                                    color = colors.foreground
                                )
                                Text(
                                    text = "请输入您的姓名和手机号码",
                                    style = Theme.typography.bodyMedium,
                                    color = colors.mutedForeground
                                )
                            }
                        }
                        1 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                                Text(
                                    text = "第二步：详细信息",
                                    style = Theme.typography.titleMedium,
                                    color = colors.foreground
                                )
                                Text(
                                    text = "请输入您的地址和邮箱",
                                    style = Theme.typography.bodyMedium,
                                    color = colors.mutedForeground
                                )
                            }
                        }
                        else -> {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                                Text(
                                    text = "第三步：确认信息",
                                    style = Theme.typography.titleMedium,
                                    color = colors.foreground
                                )
                                Text(
                                    text = "请核对您填写的所有信息",
                                    style = Theme.typography.bodyMedium,
                                    color = colors.mutedForeground
                                )
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (formStep > 0) {
                        Button(
                            text = "上一步",
                            onClick = { formStep-- },
                            size = ButtonSize.MEDIUM,
                            theme = ButtonTheme.DEFAULT,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Button(
                        text = if (formStep < 2) "下一步" else "提交",
                        onClick = {
                            if (formStep < 2) {
                                formStep++
                            } else {
                                // Reset the demo
                                formStep = 0
                            }
                        },
                        size = ButtonSize.MEDIUM,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
