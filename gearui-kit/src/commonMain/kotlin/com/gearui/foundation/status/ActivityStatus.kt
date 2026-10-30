package com.gearui.foundation.status

import androidx.compose.runtime.Composable
import com.gearui.components.icon.*
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.LoadingIndicator
import com.gearui.foundation.typography.IconSizes
import com.gearui.i18n.ActivityStrings
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.Dp

/** Where a piece of work stands: a step of an agent, a tool call. */
enum class ActivityStatus {
    Pending,
    Running,
    Succeeded,
    Failed,
    Cancelled,
}

/** Whether the work is over, whatever the outcome. */
val ActivityStatus.isFinished: Boolean
    get() = this == ActivityStatus.Succeeded || this == ActivityStatus.Failed || this == ActivityStatus.Cancelled

/** The spoken and shown word for [status]. */
internal fun ActivityStrings.of(status: ActivityStatus): String = when (status) {
    ActivityStatus.Pending -> pending
    ActivityStatus.Running -> running
    ActivityStatus.Succeeded -> succeeded
    ActivityStatus.Failed -> failed
    ActivityStatus.Cancelled -> cancelled
}

/**
 * The mark for [status]: an empty ring while waiting, a spinner while running, a check,
 * a cross or a dash when over — in the theme's semantic colours, so a trace reads at a
 * glance in light and dark. Only Running animates.
 */
@Composable
internal fun StatusGlyph(status: ActivityStatus, modifier: Modifier = Modifier, size: Dp = IconSizes.Default.md) {
    val colors = Theme.colors
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        when (status) {
            ActivityStatus.Pending -> Icon(Icons.circle, size = size, tint = colors.mutedForeground)
            ActivityStatus.Running -> LoadingIndicator(size = size, color = colors.primary, strokeWidth = BorderWidth.thick)
            ActivityStatus.Succeeded -> Icon(Icons.checkCircle, size = size, tint = colors.success, fill = true)
            ActivityStatus.Failed -> Icon(Icons.xCircle, size = size, tint = colors.destructive, fill = true)
            ActivityStatus.Cancelled -> Icon(Icons.minusCircle, size = size, tint = colors.mutedForeground)
        }
    }
}
