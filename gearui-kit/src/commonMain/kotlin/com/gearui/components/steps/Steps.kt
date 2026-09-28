package com.gearui.components.steps

import com.gearui.foundation.control.ControlGeometry
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
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.text.style.TextOverflow

import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes

/**
 * Step item data
 */
data class StepItem(
    val title: String,
    val description: String? = null,
    /** An [com.gearui.components.icon.Icons] name drawn in place of the step number. */
    val icon: String? = null
)

/**
 * Step status
 */
enum class StepStatus {
    WAITING,
    PROCESS,
    FINISH,
    ERROR
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
    DEFAULT,
    DOT
}

/**
 * Steps - Step progress indicator
 *
 * Steps component
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
 *         StepItem(title = "Step one", description = "Description text"),
 *         StepItem(title = "Step two", description = "Description text"),
 *         StepItem(title = "Step three", description = "Description text")
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

    // Every step takes an equal column. The connector is two half-segments on either
    // side of the icon, at the icon's centre line, so it meets the next icon however
    // long the labels are; the labels get the full column width below it. The old
    // layout gave the connector a third of each column and centred it on the labels,
    // which squeezed four-character titles onto two lines.
    Row(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            val stepStatus = getStepStatus(index, current, status)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StepConnector(visible = index > 0, done = index <= current, modifier = Modifier.weight(1f))
                    StepIcon(index = index, item = item, status = stepStatus, theme = theme)
                    StepConnector(visible = index < items.size - 1, done = index < current, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                Text(
                    text = item.title,
                    style = Theme.typography.bodySmall,
                    color = when (stepStatus) {
                        StepStatus.FINISH -> colors.success
                        StepStatus.PROCESS -> colors.primary
                        StepStatus.ERROR -> colors.destructive
                        StepStatus.WAITING -> colors.mutedForeground
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Spacing.xs),
                )

                item.description?.let { desc ->
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = desc,
                        style = Theme.typography.caption,
                        color = colors.mutedForeground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = Spacing.xs),
                    )
                }
            }
        }
    }
}

/** One half of the line between two step icons; [done] once the step before it is. */
@Composable
private fun StepConnector(visible: Boolean, done: Boolean, modifier: Modifier) {
    val colors = Theme.colors
    Box(
        modifier = modifier
            .height(BorderWidth.thick)
            .background(
                when {
                    !visible -> Color.Transparent
                    done -> colors.success
                    else -> colors.border
                }
            )
    )
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
                                .width(BorderWidth.thick)
                                .height(ControlGeometry.stepsConnectorMinHeight)
                                .background(
                                    if (index < current) colors.success
                                    else colors.border
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.lg))

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = if (index < items.size - 1) Spacing.xl else Spacing.none)
                ) {
                    Text(
                        text = item.title,
                        style = Theme.typography.bodyMedium,
                        color = when (stepStatus) {
                            StepStatus.FINISH -> colors.success
                            StepStatus.PROCESS -> colors.primary
                            StepStatus.ERROR -> colors.destructive
                            StepStatus.WAITING -> colors.mutedForeground
                        }
                    )

                    item.description?.let { desc ->
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = desc,
                            style = Theme.typography.bodySmall,
                            color = colors.mutedForeground
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

    val iconSize = if (theme == StepsTheme.DOT) ControlGeometry.stepsDotSize else ControlGeometry.stepsIconSize
    val backgroundColor = when (status) {
        StepStatus.FINISH -> colors.success
        StepStatus.PROCESS -> colors.primary
        StepStatus.ERROR -> colors.destructive
        StepStatus.WAITING -> colors.muted
    }

    val contentColor = when (status) {
        StepStatus.WAITING -> colors.mutedForeground
        else -> colors.primaryForeground
    }

    Box(
        modifier = Modifier
            .size(iconSize)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (status == StepStatus.WAITING && theme != StepsTheme.DOT) {
                    Modifier.border(BorderWidth.thick, colors.border, CircleShape)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (theme == StepsTheme.DEFAULT) {
            when (status) {
                StepStatus.FINISH -> {
                    Icon(
                        name = Icons.check,
                        size = IconSizes.Default.sm,
                        tint = contentColor
                    )
                }

                StepStatus.ERROR -> {
                    Icon(
                        name = Icons.x,
                        size = IconSizes.Default.sm,
                        tint = contentColor
                    )
                }

                else -> {
                    item.icon?.let { icon ->
                        Icon(name = icon, size = IconSizes.Default.sm, tint = contentColor)
                    } ?: run {
                        Text(
                            text = (index + 1).toString(),
                            style = Theme.typography.bodySmall,
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
