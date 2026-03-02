package com.gearui.components.steps

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography

import com.gearui.theme.Theme

/**
 * Step item data
 */
data class StepItem(
    val title: String,
    val description: String? = null,
    val icon: String? = null
)

/**
 * Step status
 */
enum class StepStatus {
    WAITING,    // 等待
    PROCESS,    // 进行中
    FINISH,     // 完成
    ERROR       // 错误
}

/**
 * Steps direction
 */
enum class StepsDirection {
    HORIZONTAL,
    VERTICAL
}

/**
 * Steps theme
 */
enum class StepsTheme {
    DEFAULT,    // 默认带图标
    DOT         // 点状
}

/**
 * Steps - Step progress indicator
 *
 * 步骤条组件
 *
 * Features:
 * - Horizontal/Vertical layout
 * - Multiple themes (default, dot)
 * - Step status (waiting, process, finish, error)
 * - Custom icons
 * - Description support
 *
 * Example:
 * ```
 * Steps(
 *     current = 1,
 *     items = listOf(
 *         StepItem(title = "步骤一", description = "描述文字"),
 *         StepItem(title = "步骤二", description = "描述文字"),
 *         StepItem(title = "步骤三", description = "描述文字")
 *     )
 * )
 * ```
 */
@Composable
fun Steps(
    current: Int,
    items: List<StepItem>,
    modifier: Modifier = Modifier,
    direction: StepsDirection = StepsDirection.HORIZONTAL,
    theme: StepsTheme = StepsTheme.DEFAULT,
    status: StepStatus = StepStatus.PROCESS,
    onChange: ((Int) -> Unit)? = null
) {
    val colors = Theme.colors

    when (direction) {
        StepsDirection.HORIZONTAL -> {
            HorizontalSteps(
                current = current,
                items = items,
                theme = theme,
                status = status,
                modifier = modifier,
                onChange = onChange
            )
        }

        StepsDirection.VERTICAL -> {
            VerticalSteps(
                current = current,
                items = items,
                theme = theme,
                status = status,
                modifier = modifier,
                onChange = onChange
            )
        }
    }
}

@Composable
private fun HorizontalSteps(
    current: Int,
    items: List<StepItem>,
    theme: StepsTheme,
    status: StepStatus,
    modifier: Modifier,
    onChange: ((Int) -> Unit)?
) {
    val colors = Theme.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items.forEachIndexed { index, item ->
            val stepStatus = getStepStatus(index, current, status)

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Icon/Number
                    StepIcon(
                        index = index,
                        item = item,
                        status = stepStatus,
                        theme = theme
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Title
                    Text(
                        text = item.title,
                        style = Typography.BodyMedium,
                        color = when (stepStatus) {
                            StepStatus.FINISH -> colors.success
                            StepStatus.PROCESS -> colors.primary
                            StepStatus.ERROR -> colors.danger
                            StepStatus.WAITING -> colors.textSecondary
                        }
                    )

                    // Description
                    item.description?.let { desc ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            style = Typography.BodySmall,
                            color = colors.textSecondary
                        )
                    }
                }

                // Connector line
                if (index < items.size - 1) {
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .height(2.dp)
                            .background(
                                if (index < current) colors.success
                                else colors.border
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun VerticalSteps(
    current: Int,
    items: List<StepItem>,
    theme: StepsTheme,
    status: StepStatus,
    modifier: Modifier,
    onChange: ((Int) -> Unit)?
) {
    val colors = Theme.colors

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        items.forEachIndexed { index, item ->
            val stepStatus = getStepStatus(index, current, status)

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon and line
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StepIcon(
                        index = index,
                        item = item,
                        status = stepStatus,
                        theme = theme
                    )

                    // Connector line
                    if (index < items.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(48.dp)
                                .background(
                                    if (index < current) colors.success
                                    else colors.border
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = if (index < items.size - 1) 24.dp else 0.dp)
                ) {
                    Text(
                        text = item.title,
                        style = Typography.BodyMedium,
                        color = when (stepStatus) {
                            StepStatus.FINISH -> colors.success
                            StepStatus.PROCESS -> colors.primary
                            StepStatus.ERROR -> colors.danger
                            StepStatus.WAITING -> colors.textSecondary
                        }
                    )

                    item.description?.let { desc ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            style = Typography.BodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIcon(
    index: Int,
    item: StepItem,
    status: StepStatus,
    theme: StepsTheme
) {
    val colors = Theme.colors

    val iconSize = if (theme == StepsTheme.DOT) 12.dp else 32.dp
    val backgroundColor = when (status) {
        StepStatus.FINISH -> colors.success
        StepStatus.PROCESS -> colors.primary
        StepStatus.ERROR -> colors.danger
        StepStatus.WAITING -> colors.surfaceVariant
    }

    val contentColor = when (status) {
        StepStatus.WAITING -> colors.textSecondary
        else -> colors.textAnti
    }

    Box(
        modifier = Modifier
            .size(iconSize)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (status == StepStatus.WAITING && theme != StepsTheme.DOT) {
                    Modifier.border(2.dp, colors.border, CircleShape)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (theme == StepsTheme.DEFAULT) {
            when (status) {
                StepStatus.FINISH -> {
                    Icon(
                        name = Icons.check,
                        size = 14.dp,
                        tint = contentColor
                    )
                }

                StepStatus.ERROR -> {
                    Icon(
                        name = Icons.close,
                        size = 14.dp,
                        tint = contentColor
                    )
                }

                else -> {
                    item.icon?.let { icon ->
                        Text(
                            text = icon,
                            style = Typography.BodySmall,
                            color = contentColor
                        )
                    } ?: run {
                        Text(
                            text = (index + 1).toString(),
                            style = Typography.BodySmall,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

private fun getStepStatus(index: Int, current: Int, currentStatus: StepStatus): StepStatus {
    return when {
        index < current -> StepStatus.FINISH
        index == current -> currentStatus
        else -> StepStatus.WAITING
    }
}
