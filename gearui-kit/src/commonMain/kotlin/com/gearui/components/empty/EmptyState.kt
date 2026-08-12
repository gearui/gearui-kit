package com.gearui.components.empty

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * EmptyState - fully Theme-driven empty state
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: Color(0x...) or hardcoded colours
 *
 * Features:
 * - empty state message
 * - custom icon or image
 * - action button
 * - several presets
 */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    customAction: (@Composable () -> Unit)? = null
) {
    // ⭐ Framework Rule #1: these three are always the first lines
    val colors = Theme.colors
    val shapes = Theme.shapes

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon / image
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.height(Spacing.lg))
        } else {
            // Default empty state icon
            Icon(
                name = Icons.image,
                size = IconSizes.Display.sm,
                tint = colors.mutedForeground
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
        }

        // Primary message
        Text(
            text = message,
            style = Typography.TitleMedium,
            color = colors.foreground
        )

        // Description text
        if (description != null) {
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = description,
                style = Typography.BodyMedium,
                color = colors.mutedForeground
            )
        }

        // Action area (a custom one wins)
        if (customAction != null) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            customAction()
        } else if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            Box(
                modifier = Modifier
                    .clip(shapes.sm)
                    .background(colors.primary)
                    .clickable(onClick = onAction)
                    .padding(horizontal = Spacing.xl, vertical = 10.dp)
            ) {
                Text(
                    text = actionText,
                    style = Typography.BodyMedium,
                    color = colors.primaryForeground
                )
            }
        }
    }
}

/**
 * EmptyStateType - preset empty states
 */
@Composable
fun EmptyStatePreset(
    type: EmptyStateType,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    customAction: (@Composable () -> Unit)? = null
) {
    val s = I18n.strings
    val (message, description, iconName) = when (type) {
        EmptyStateType.NO_DATA -> Triple(s.common.noData, s.feedback.emptyNoDataDescription, Icons.image)
        EmptyStateType.NO_SEARCH_RESULT -> Triple(s.common.noSearchResult, s.feedback.emptyNoSearchResultDescription, Icons.search)
        EmptyStateType.NO_NETWORK -> Triple(s.feedback.emptyNoNetworkTitle, s.feedback.emptyNoNetworkDescription, Icons.warning)
        EmptyStateType.ERROR -> Triple(s.common.loadFailed, s.feedback.emptyErrorDescription, Icons.error)
        EmptyStateType.NO_PERMISSION -> Triple(s.feedback.emptyNoPermissionTitle, s.feedback.emptyNoPermissionDescription, Icons.no_photography)
    }

    val colors = Theme.colors
    val typography = Theme.typography

    EmptyState(
        message = message,
        description = description,
        icon = {
            Icon(
                name = iconName,
                size = IconSizes.Display.sm,
                tint = colors.mutedForeground
            )
        },
        actionText = actionText,
        onAction = onAction,
        customAction = customAction,
        modifier = modifier
    )
}

/**
 * EmptyStateType - empty state type
 */
enum class EmptyStateType {
    /** no data */
    NO_DATA,

    /** no search results */
    NO_SEARCH_RESULT,

    /** no network */
    NO_NETWORK,

    /** error */
    ERROR,

    /** no permission */
    NO_PERMISSION
}
