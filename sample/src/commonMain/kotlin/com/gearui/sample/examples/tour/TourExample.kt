package com.gearui.sample.examples.tour

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.components.button.ButtonTheme
import com.gearui.components.tour.Tour
import com.gearui.components.tour.TourStep
import com.gearui.components.tour.rememberTourState
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Tour 组件示例
 *
 * 漫游式引导组件，用于新用户引导或功能介绍
 * - 支持多步骤引导
 * - 支持上一步/下一步导航
 * - 支持跳过功能
 * - 显示进度指示器
 */
@Composable
fun TourExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 基础引导状态
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

    // 多步骤引导状态
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

    // 带跳过的引导状态
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

    // 场景引导状态
    val sceneTourState = rememberTourState(
        steps = listOf(
            TourStep(
                title = "搜索功能",
                description = "在顶部搜索框中输入关键词，快速查找内容。",
                targetKey = "search"
            ),
            TourStep(
                title = "筛选功能",
                description = "点击筛选按钮，可以按类别、时间等条件筛选。",
                targetKey = "filter"
            ),
            TourStep(
                title = "添加内容",
                description = "点击加号按钮，创建新的内容。",
                targetKey = "add"
            )
        )
    )

    // 记录引导完成状态
    var basicTourCompleted by remember { mutableStateOf(false) }
    var multiStepTourCompleted by remember { mutableStateOf(false) }
    var skipTourSkipped by remember { mutableStateOf(false) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== 组件类型 ==========

        // 基础用法
        ExampleSection(
            title = "基础用法",
            description = "简单的三步引导流程"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    text = "开始基础引导",
                    onClick = {
                        basicTourCompleted = false
                        basicTourState.start()
                    },
                    size = ButtonSize.LARGE,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.fillMaxWidth()
                )

                if (basicTourCompleted) {
                    Text(
                        text = "✓ 引导已完成",
                        style = Typography.BodySmall,
                        color = colors.success
                    )
                }

                Tour(
                    state = basicTourState,
                    onFinish = { basicTourCompleted = true }
                )
            }
        }

        // 多步骤引导
        ExampleSection(
            title = "多步骤引导",
            description = "五步详细操作教程"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    text = "开始详细引导（5步）",
                    onClick = {
                        multiStepTourCompleted = false
                        multiStepTourState.start()
                    },
                    size = ButtonSize.LARGE,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.fillMaxWidth()
                )

                if (multiStepTourCompleted) {
                    Text(
                        text = "✓ 详细引导已完成",
                        style = Typography.BodySmall,
                        color = colors.success
                    )
                }

                Tour(
                    state = multiStepTourState,
                    onFinish = { multiStepTourCompleted = true }
                )
            }
        }

        // 可跳过的引导
        ExampleSection(
            title = "可跳过的引导",
            description = "用户可以随时跳过引导"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    text = "开始引导（可跳过）",
                    onClick = {
                        skipTourSkipped = false
                        skipTourState.start()
                    },
                    size = ButtonSize.LARGE,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.fillMaxWidth()
                )

                if (skipTourSkipped) {
                    Text(
                        text = "⚠ 用户跳过了引导",
                        style = Typography.BodySmall,
                        color = colors.warning
                    )
                }

                Tour(
                    state = skipTourState,
                    onFinish = { },
                    onSkip = { skipTourSkipped = true }
                )
            }
        }

        // ========== 使用场景 ==========

        // 模拟场景
        ExampleSection(
            title = "场景示例",
            description = "模拟实际界面中的引导效果"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // 模拟界面
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 顶部栏
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 搜索框模拟
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(colors.surface)
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "🔍 搜索...",
                                    style = Typography.BodyMedium,
                                    color = colors.textPlaceholder
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // 筛选按钮
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⚙",
                                    style = Typography.BodyMedium,
                                    color = colors.textSecondary
                                )
                            }
                        }

                        // 内容区域
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "内容区域",
                                style = Typography.BodyMedium,
                                color = colors.textPlaceholder
                            )
                        }

                        // 底部添加按钮
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(colors.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+",
                                    style = Typography.TitleLarge,
                                    color = colors.onPrimary
                                )
                            }
                        }
                    }
                }

                Button(
                    text = "开始场景引导",
                    onClick = { sceneTourState.start() },
                    size = ButtonSize.LARGE,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.fillMaxWidth()
                )

                Tour(
                    state = sceneTourState,
                    onFinish = { }
                )
            }
        }

        // ========== 快速测试 ==========

        ExampleSection(
            title = "快速测试",
            description = "快速启动不同类型的引导"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    text = "3步",
                    onClick = { basicTourState.start() },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "5步",
                    onClick = { multiStepTourState.start() },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "可跳过",
                    onClick = { skipTourState.start() },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "场景",
                    onClick = { sceneTourState.start() },
                    size = ButtonSize.SMALL,
                    type = ButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
