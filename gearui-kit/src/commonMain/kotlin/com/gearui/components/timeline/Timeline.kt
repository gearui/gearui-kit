package com.gearui.components.timeline

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
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography

import com.gearui.theme.Theme

/**
 * Timeline item data
 */
data class TimelineItem(
    val content: String,
    val timestamp: String? = null,
    val icon: String? = null,
    val color: TimelineColor = TimelineColor.DEFAULT
)

/**
 * Timeline color theme
 */
enum class TimelineColor {
    DEFAULT,
    PRIMARY,
    SUCCESS,
    WARNING,
    ERROR
}

/**
 * Timeline mode
 */
enum class TimelineMode {
    LEFT,       // 内容在左侧
    RIGHT,      // 内容在右侧
    ALTERNATE   // 交替显示
}

/**
 * Timeline - Timeline component
 *
 * 时间轴组件
 *
 * Features:
 * - Multiple modes (left, right, alternate)
 * - Custom icons
 * - Color themes
 * - Timestamp support
 * - Custom content
 *
 * Example:
 * ```
 * Timeline(
 *     items = listOf(
 *         TimelineItem(
 *             content = "创建订单",
 *             timestamp = "2024-01-01 10:00",
 *             color = TimelineColor.SUCCESS
 *         ),
 *         TimelineItem(
 *             content = "支付完成",
 *             timestamp = "2024-01-01 10:05"
 *         )
 *     )
 * )
 * ```
 */
@Composable
fun Timeline(
    items: List<TimelineItem>,
    modifier: Modifier = Modifier,
    mode: TimelineMode = TimelineMode.LEFT,
    reverse: Boolean = false
) {
    val colors = Theme.colors

    val displayItems = if (reverse) items.reversed() else items

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        displayItems.forEachIndexed { index, item ->
            val isLast = index == displayItems.size - 1
            val position = when (mode) {
                TimelineMode.LEFT -> TimelinePosition.LEFT
                TimelineMode.RIGHT -> TimelinePosition.RIGHT
                TimelineMode.ALTERNATE -> if (index % 2 == 0) TimelinePosition.LEFT else TimelinePosition.RIGHT
            }

            TimelineItemView(
                item = item,
                position = position,
                isLast = isLast
            )
        }
    }
}

private enum class TimelinePosition {
    LEFT, RIGHT
}

@Composable
private fun TimelineItemView(
    item: TimelineItem,
    position: TimelinePosition,
    isLast: Boolean
) {
    val colors = Theme.colors

    val dotColor = when (item.color) {
        TimelineColor.DEFAULT -> colors.textSecondary
        TimelineColor.PRIMARY -> colors.primary
        TimelineColor.SUCCESS -> colors.success
        TimelineColor.WARNING -> colors.warning
        TimelineColor.ERROR -> colors.danger
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (position == TimelinePosition.RIGHT) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        if (position == TimelinePosition.RIGHT) {
            // Content on the right
            Box(modifier = Modifier.weight(1f))
        }

        // Dot and line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            // Dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(dotColor)
                    .border(2.dp, colors.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                item.icon?.let { icon ->
                    Text(
                        text = icon,
                        style = Typography.BodySmall,
                        color = colors.textAnti
                    )
                }
            }

            // Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(48.dp)
                        .background(colors.border)
                )
            }
        }

        if (position == TimelinePosition.LEFT) {
            // Content on the left
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = if (!isLast) 24.dp else 0.dp)
            ) {
                Text(
                    text = item.content,
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )

                item.timestamp?.let { timestamp ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = timestamp,
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        } else {
            // Content on the right
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = if (!isLast) 24.dp else 0.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = item.content,
                    style = Typography.BodyMedium,
                    color = colors.textPrimary
                )

                item.timestamp?.let { timestamp ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = timestamp,
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

/**
 * Timeline with custom content
 */
@Composable
fun TimelineCustom(
    itemCount: Int,
    modifier: Modifier = Modifier,
    mode: TimelineMode = TimelineMode.LEFT,
    dotColor: @Composable (Int) -> Color = { Theme.colors.primary },
    content: @Composable (index: Int) -> Unit
) {
    val colors = Theme.colors

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        repeat(itemCount) { index ->
            val isLast = index == itemCount - 1
            val position = when (mode) {
                TimelineMode.LEFT -> TimelinePosition.LEFT
                TimelineMode.RIGHT -> TimelinePosition.RIGHT
                TimelineMode.ALTERNATE -> if (index % 2 == 0) TimelinePosition.LEFT else TimelinePosition.RIGHT
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (position == TimelinePosition.RIGHT) {
                    Arrangement.End
                } else {
                    Arrangement.Start
                }
            ) {
                if (position == TimelinePosition.RIGHT) {
                    Box(modifier = Modifier.weight(1f))
                }

                // Dot and line
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(dotColor(index))
                            .border(2.dp, colors.surface, CircleShape)
                    )

                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(48.dp)
                                .background(colors.border)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = if (!isLast) 24.dp else 0.dp),
                    horizontalAlignment = if (position == TimelinePosition.RIGHT) {
                        Alignment.End
                    } else {
                        Alignment.Start
                    }
                ) {
                    content(index)
                }
            }
        }
    }
}
