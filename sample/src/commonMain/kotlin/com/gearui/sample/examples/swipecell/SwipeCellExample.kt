package com.gearui.sample.examples.swipecell

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.cell.Cell
import com.gearui.components.swipecell.SwipeCell
import com.gearui.components.swipecell.SwipeCellAction
import com.gearui.components.swipecell.SwipeCellActionTheme
import com.gearui.components.swipecell.SwipeCellGroup
import com.gearui.components.swipecell.SwipeCellIconPosition
import com.gearui.components.swipecell.rememberSwipeCellGroupState
import com.gearui.components.swipecell.rememberSwipeCellState
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * SwipeCell 组件示例
 */
@Composable
fun SwipeCellExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 操作结果提示
    var actionResult by remember { mutableStateOf("") }

    // 可删除列表项
    var deleteList by remember { mutableStateOf(listOf("可删除项 1", "可删除项 2", "可删除项 3")) }

    // 组状态 - 用于互斥
    val groupState = rememberSwipeCellGroupState()

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 左滑单操作
        ExampleSection(
            title = "左滑单操作",
            description = "向左滑动显示单个操作按钮"
        ) {
            SwipeCellGroup(state = groupState) { group ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                    modifier = Modifier.background(colors.divider)
                ) {
                    SwipeCell(
                        groupState = group,
                        rightActions = listOf(
                            SwipeCellAction(
                                label = "删除",
                                theme = SwipeCellActionTheme.DANGER,
                                onClick = { actionResult = "点击了删除" }
                            )
                        )
                    ) {
                        Cell(
                            title = "左滑单操作",
                            note = "辅助信息"
                        )
                    }

                    SwipeCell(
                        groupState = group,
                        rightActions = listOf(
                            SwipeCellAction(
                                label = "删除",
                                theme = SwipeCellActionTheme.DANGER,
                                onClick = { actionResult = "点击了删除" }
                            )
                        )
                    ) {
                        Cell(
                            title = "左滑单操作",
                            note = "辅助信息",
                            description = "一段很长很长的内容文字"
                        )
                    }
                }
            }
        }

        // 左滑双操作
        ExampleSection(
            title = "左滑双操作",
            description = "向左滑动显示两个操作按钮"
        ) {
            SwipeCell(
                groupState = groupState,
                rightActions = listOf(
                    SwipeCellAction(
                        label = "编辑",
                        theme = SwipeCellActionTheme.WARNING,
                        onClick = { actionResult = "点击了编辑" }
                    ),
                    SwipeCellAction(
                        label = "删除",
                        theme = SwipeCellActionTheme.DANGER,
                        onClick = { actionResult = "点击了删除" }
                    )
                )
            ) {
                Cell(
                    title = "左滑双操作",
                    note = "辅助信息"
                )
            }
        }

        // 左滑三操作
        ExampleSection(
            title = "左滑三操作",
            description = "向左滑动显示三个操作按钮"
        ) {
            SwipeCell(
                groupState = groupState,
                rightActions = listOf(
                    SwipeCellAction(
                        label = "保存",
                        theme = SwipeCellActionTheme.PRIMARY,
                        onClick = { actionResult = "点击了保存" }
                    ),
                    SwipeCellAction(
                        label = "编辑",
                        theme = SwipeCellActionTheme.WARNING,
                        onClick = { actionResult = "点击了编辑" }
                    ),
                    SwipeCellAction(
                        label = "删除",
                        theme = SwipeCellActionTheme.DANGER,
                        onClick = { actionResult = "点击了删除" }
                    )
                )
            ) {
                Cell(
                    title = "左滑三操作",
                    note = "辅助信息"
                )
            }
        }

        // 右滑单操作
        ExampleSection(
            title = "右滑单操作",
            description = "向右滑动显示操作按钮"
        ) {
            SwipeCell(
                groupState = groupState,
                leftActions = listOf(
                    SwipeCellAction(
                        label = "选择",
                        theme = SwipeCellActionTheme.PRIMARY,
                        onClick = { actionResult = "点击了选择" }
                    )
                )
            ) {
                Cell(
                    title = "右滑操作",
                    note = "辅助信息"
                )
            }
        }

        // 左右滑操作
        ExampleSection(
            title = "左右滑操作",
            description = "支持左右两个方向滑动"
        ) {
            SwipeCell(
                groupState = groupState,
                leftActions = listOf(
                    SwipeCellAction(
                        label = "选择",
                        theme = SwipeCellActionTheme.PRIMARY,
                        onClick = { actionResult = "点击了选择" }
                    )
                ),
                rightActions = listOf(
                    SwipeCellAction(
                        label = "编辑",
                        theme = SwipeCellActionTheme.WARNING,
                        onClick = { actionResult = "点击了编辑" }
                    ),
                    SwipeCellAction(
                        label = "删除",
                        theme = SwipeCellActionTheme.DANGER,
                        onClick = { actionResult = "点击了删除" }
                    )
                )
            ) {
                Cell(
                    title = "左右滑操作",
                    note = "辅助信息"
                )
            }
        }

        // 带图标的滑动操作
        ExampleSection(
            title = "带图标的滑动操作",
            description = "操作按钮支持图标+文字"
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 图标+文字（横向）
                SwipeCell(
                    groupState = groupState,
                    rightActions = listOf(
                        SwipeCellAction(
                            label = "编辑",
                            icon = "✏️",
                            iconPosition = SwipeCellIconPosition.LEFT,
                            theme = SwipeCellActionTheme.WARNING,
                            onClick = { actionResult = "点击了编辑" }
                        ),
                        SwipeCellAction(
                            label = "删除",
                            icon = "🗑️",
                            iconPosition = SwipeCellIconPosition.LEFT,
                            theme = SwipeCellActionTheme.DANGER,
                            onClick = { actionResult = "点击了删除" }
                        )
                    )
                ) {
                    Cell(
                        title = "左滑操作",
                        note = "图标+文字（横向）"
                    )
                }

                // 仅图标
                SwipeCell(
                    groupState = groupState,
                    rightActions = listOf(
                        SwipeCellAction(
                            label = "",
                            icon = "✏️",
                            theme = SwipeCellActionTheme.WARNING,
                            onClick = { actionResult = "点击了编辑" }
                        ),
                        SwipeCellAction(
                            label = "",
                            icon = "🗑️",
                            theme = SwipeCellActionTheme.DANGER,
                            onClick = { actionResult = "点击了删除" }
                        )
                    )
                ) {
                    Cell(
                        title = "左滑操作",
                        note = "仅图标"
                    )
                }

                // 图标+文字（纵向）
                SwipeCell(
                    groupState = groupState,
                    rightActions = listOf(
                        SwipeCellAction(
                            label = "编辑",
                            icon = "✏️",
                            iconPosition = SwipeCellIconPosition.TOP,
                            theme = SwipeCellActionTheme.WARNING,
                            onClick = { actionResult = "点击了编辑" }
                        ),
                        SwipeCellAction(
                            label = "删除",
                            icon = "🗑️",
                            iconPosition = SwipeCellIconPosition.TOP,
                            theme = SwipeCellActionTheme.DANGER,
                            onClick = { actionResult = "点击了删除" }
                        )
                    )
                ) {
                    Cell(
                        title = "左滑操作",
                        note = "图标+文字（纵向）",
                        description = "一段很长很长的内容文字"
                    )
                }
            }
        }

        // 滑动删除列表
        ExampleSection(
            title = "滑动删除列表",
            description = "常见的滑动删除交互场景（同组互斥）"
        ) {
            val deleteGroupState = rememberSwipeCellGroupState()

            SwipeCellGroup(state = deleteGroupState) { group ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                    modifier = Modifier.background(colors.divider)
                ) {
                    deleteList.forEachIndexed { index, item ->
                        key(item) {
                            SwipeCell(
                                groupState = group,
                                rightActions = listOf(
                                    SwipeCellAction(
                                        label = "删除",
                                        theme = SwipeCellActionTheme.DANGER,
                                        onClick = {
                                            deleteList = deleteList.toMutableList().apply { removeAt(index) }
                                            actionResult = "删除了: $item"
                                        }
                                    )
                                )
                            ) {
                                Cell(
                                    title = item,
                                    note = "左滑删除"
                                )
                            }
                        }
                    }

                    if (deleteList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.surface)
                                .padding(32.dp)
                        ) {
                            Text(
                                text = "列表已清空",
                                style = Typography.BodyMedium,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // 操作结果
        if (actionResult.isNotEmpty()) {
            ExampleSection(
                title = "操作结果",
                description = "显示最近一次操作"
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
            description = "SwipeCell 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "手感优化:",
                    style = Typography.Label,
                    color = colors.textPrimary
                )
                Text(
                    text = "• 弹性动画 (Spring Animation)\n• 阻尼感滑动\n• 快速滑动判断\n• 组内互斥关闭",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "滑动方向:",
                    style = Typography.Label,
                    color = colors.textPrimary
                )
                Text(
                    text = "左滑 (rightActions) / 右滑 (leftActions)",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "操作按钮主题:",
                    style = Typography.Label,
                    color = colors.textPrimary
                )
                Text(
                    text = "PRIMARY / DANGER / WARNING / SUCCESS",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}
