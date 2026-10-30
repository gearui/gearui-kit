package com.gearui.foundation.rail

import androidx.compose.runtime.Composable
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.layout.Spacing
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.ColumnScope
import com.tencent.kuikly.compose.foundation.layout.IntrinsicSize
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxHeight
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.heightIn
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier

/**
 * One entry on a vertical rail: a [marker] on the axis, a connector down to the next
 * entry unless [isLast], and [content] beside it. The row is as tall as its content and
 * the connector fills it (IntrinsicSize.Min, as Timeline does), so it reaches the next
 * marker however long the content is.
 */
@Composable
internal fun RailRow(
    isLast: Boolean,
    marker: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = Theme.colors
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Column(
            Modifier.width(ControlGeometry.timelineAxisWidth).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            marker()
            if (!isLast) {
                Box(
                    Modifier.width(BorderWidth.thin).weight(1f)
                        .heightIn(min = ControlGeometry.timelineConnectorMinHeight)
                        .background(colors.border),
                )
            }
        }
        Column(
            Modifier.weight(1f).padding(bottom = if (isLast) Spacing.none else Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            content = content,
        )
    }
}
