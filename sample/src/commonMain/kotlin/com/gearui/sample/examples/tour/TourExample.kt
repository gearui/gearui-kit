package com.gearui.sample.examples.tour

import androidx.compose.runtime.*
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.tour.Tour
import com.gearui.components.tour.TourStep
import com.gearui.components.tour.rememberTourState
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface

// note = the outcome of the last run (completed / skipped), shown at the row's trailing edge
private class TourRow(
    val title: String,
    val description: String,
    val note: String?,
    val onClick: () -> Unit,
)

/**
 * Tour component examples
 *
 * A guided walkthrough, for onboarding new users or introducing a feature
 * - multiple steps
 * - previous / next navigation
 * - skipping
 * - a progress indicator
 */
@Composable
fun TourExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    // Basic tour state
    val basicTourState = rememberTourState(
        steps = listOf(
            TourStep(
                title = "欢迎使用",
                description = "这是一个漫游式引导组件，可以帮助用户了解产品功能。"
            ),
            TourStep(
                title = "功能介绍",
                description = "通过分步引导，逐步展示应用的主要功能和操作方式。"
            ),
            TourStep(
                title = "开始体验",
                description = "现在你已经了解了基本功能，开始使用吧！"
            )
        )
    )

    // Multi-step tour state
    val multiStepTourState = rememberTourState(
        steps = listOf(
            TourStep(
                title = "第一步：创建项目",
                description = "点击右上角的「新建」按钮创建一个新项目。"
            ),
            TourStep(
                title = "第二步：编辑内容",
                description = "在编辑器中输入你的内容，支持富文本格式。"
            ),
            TourStep(
                title = "第三步：预览效果",
                description = "点击「预览」按钮查看最终效果。"
            ),
            TourStep(
                title = "第四步：发布分享",
                description = "确认无误后，点击「发布」按钮分享给其他人。"
            ),
            TourStep(
                title = "完成",
                description = "恭喜！你已经掌握了基本操作流程。"
            )
        )
    )

    // Skippable tour state
    val skipTourState = rememberTourState(
        steps = listOf(
            TourStep(
                title = "新功能介绍",
                description = "我们增加了一些新功能，让我来为你介绍一下。"
            ),
            TourStep(
                title = "深色模式",
                description = "现在支持深色模式，在设置中可以切换。"
            ),
            TourStep(
                title = "快捷操作",
                description = "长按卡片可以快速进行删除、编辑等操作。"
            )
        )
    )

    // Records whether the tour has been completed
    var basicTourCompleted by remember { mutableStateOf(false) }
    var multiStepTourCompleted by remember { mutableStateOf(false) }
    var skipTourSkipped by remember { mutableStateOf(false) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "引导类型",
            description = "点击任一行开始对应的引导，结束后行尾显示结果"
        ) {
            CellGroup(
                items = listOf(
                    TourRow("开始基础引导", "简单的三步引导流程", if (basicTourCompleted) "已完成" else null) {
                        basicTourCompleted = false
                        basicTourState.start()
                    },
                    TourRow("开始详细引导（5步）", "五步详细操作教程", if (multiStepTourCompleted) "已完成" else null) {
                        multiStepTourCompleted = false
                        multiStepTourState.start()
                    },
                    TourRow("开始引导（可跳过）", "用户可以随时跳过引导", if (skipTourSkipped) "已跳过" else null) {
                        skipTourSkipped = false
                        skipTourState.start()
                    },
                )
            ) { row ->
                Cell(
                    title = row.title,
                    description = row.description,
                    note = row.note,
                    arrow = true,
                    onClick = row.onClick
                )
            }

            Tour(
                state = basicTourState,
                onFinish = { basicTourCompleted = true }
            )

            Tour(
                state = multiStepTourState,
                onFinish = { multiStepTourCompleted = true }
            )

            Tour(
                state = skipTourState,
                onFinish = { },
                onSkip = { skipTourSkipped = true }
            )
        }
    }
}
