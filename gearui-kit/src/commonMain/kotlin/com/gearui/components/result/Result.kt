package com.gearui.components.result

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography

import com.gearui.theme.Theme
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing

/**
 * Result status type
 */
enum class ResultStatus {
    SUCCESS,    // 成功
    ERROR,      // 错误
    WARNING,    // 警告
    INFO,       // 信息
    QUESTION,   // 疑问
    FORBIDDEN,  // 禁止
    NOT_FOUND   // 404
}

/**
 * Result - Result page component
 *
 * 结果页组件
 *
 * Features:
 * - Multiple status types (success, error, warning, etc.)
 * - Icon display
 * - Title and description
 * - Action buttons
 * - Extra content support
 *
 * Example:
 * ```
 * Result(
 *     status = ResultStatus.SUCCESS,
 *     title = "操作成功",
 *     description = "您的操作已成功完成",
 *     primaryAction = {
 *         Button(text = "返回首页", onClick = { })
 *     }
 * )
 * ```
 */
@Composable
fun Result(
    status: ResultStatus,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: String? = null,
    primaryAction: (@Composable () -> Unit)? = null,
    secondaryAction: (@Composable () -> Unit)? = null,
    extraContent: (@Composable () -> Unit)? = null
) {
    val colors = Theme.colors
    val typography = Theme.typography
    val shapes = Theme.shapes

    val (defaultIconName, iconColor) = getStatusIconAndColor(status, colors)
    val displayIconName = icon ?: defaultIconName

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        if (displayIconName != null) {
            Icon(
                name = displayIconName,
                size = 40.dp,
                tint = iconColor
            )
        } else {
            Text(
                text = "404",
                style = Typography.HeadlineLarge,
                color = iconColor
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Title
        Text(
            text = title,
            style = Typography.HeadlineMedium,
            color = colors.foreground
        )

        // Description
        description?.let { desc ->
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = desc,
                style = Typography.BodyMedium,
                color = colors.mutedForeground
            )
        }

        // Extra content
        extraContent?.let {
            Spacer(modifier = Modifier.height(Spacing.lg))
            it()
        }

        // Actions
        if (primaryAction != null || secondaryAction != null) {
            Spacer(modifier = Modifier.height(Spacing.xxl))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                primaryAction?.invoke()
                secondaryAction?.invoke()
            }
        }
    }
}

private fun getStatusIconAndColor(status: ResultStatus, colors: com.gearui.theme.Colors): Pair<String?, Color> {
    return when (status) {
        ResultStatus.SUCCESS -> Icons.check to colors.success
        ResultStatus.ERROR -> Icons.close to colors.destructive
        ResultStatus.WARNING -> Icons.warning to colors.warning
        ResultStatus.INFO -> Icons.info to colors.primary
        ResultStatus.QUESTION -> Icons.info to colors.primary
        ResultStatus.FORBIDDEN -> Icons.no_photography to colors.destructive
        ResultStatus.NOT_FOUND -> null to colors.mutedForeground
    }
}

/**
 * Success result shorthand
 */
@Composable
fun SuccessResult(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    primaryAction: (@Composable () -> Unit)? = null,
    secondaryAction: (@Composable () -> Unit)? = null
) {
    Result(
        status = ResultStatus.SUCCESS,
        title = title,
        description = description,
        primaryAction = primaryAction,
        secondaryAction = secondaryAction,
        modifier = modifier
    )
}

/**
 * Error result shorthand
 */
@Composable
fun ErrorResult(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    primaryAction: (@Composable () -> Unit)? = null,
    secondaryAction: (@Composable () -> Unit)? = null
) {
    Result(
        status = ResultStatus.ERROR,
        title = title,
        description = description,
        primaryAction = primaryAction,
        secondaryAction = secondaryAction,
        modifier = modifier
    )
}

/**
 * 404 Not Found result
 */
@Composable
fun NotFoundResult(
    modifier: Modifier = Modifier,
    title: String = I18n.strings.feedback.notFoundTitle,
    description: String? = I18n.strings.feedback.notFoundDescription,
    primaryAction: (@Composable () -> Unit)? = null
) {
    Result(
        status = ResultStatus.NOT_FOUND,
        title = title,
        description = description,
        primaryAction = primaryAction,
        modifier = modifier
    )
}

/**
 * 403 Forbidden result
 */
@Composable
fun ForbiddenResult(
    modifier: Modifier = Modifier,
    title: String = I18n.strings.feedback.forbiddenTitle,
    description: String? = I18n.strings.feedback.forbiddenDescription,
    primaryAction: (@Composable () -> Unit)? = null
) {
    Result(
        status = ResultStatus.FORBIDDEN,
        title = title,
        description = description,
        primaryAction = primaryAction,
        modifier = modifier
    )
}

/**
 * Empty result (no data)
 */
@Composable
fun EmptyResult(
    modifier: Modifier = Modifier,
    title: String = I18n.strings.common.noData,
    description: String? = null,
    icon: String = Icons.image,
    primaryAction: (@Composable () -> Unit)? = null
) {
    Result(
        status = ResultStatus.INFO,
        title = title,
        description = description,
        icon = icon,
        primaryAction = primaryAction,
        modifier = modifier
    )
}

/**
 * Loading result (operation in progress)
 */
@Composable
fun LoadingResult(
    modifier: Modifier = Modifier,
    title: String = I18n.strings.feedback.processingTitle,
    description: String? = I18n.strings.feedback.processingDescription,
    icon: String = Icons.hourglass_empty
) {
    Result(
        status = ResultStatus.INFO,
        title = title,
        description = description,
        icon = icon,
        modifier = modifier
    )
}

/**
 * Network error result
 */
@Composable
fun NetworkErrorResult(
    modifier: Modifier = Modifier,
    title: String = I18n.strings.common.networkError,
    description: String? = I18n.strings.feedback.networkErrorDescription,
    onRetry: (() -> Unit)? = null
) {
    Result(
        status = ResultStatus.ERROR,
        title = title,
        description = description,
        icon = Icons.warning,
        primaryAction = onRetry?.let {
            {
                Button(
                    text = I18n.strings.common.retry,
                    onClick = it
                )
            }
        },
        modifier = modifier
    )
}
