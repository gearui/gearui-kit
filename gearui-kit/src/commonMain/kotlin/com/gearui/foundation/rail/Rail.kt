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
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.layout.Layout
import com.tencent.kuikly.compose.ui.unit.Constraints

/**
 * One entry on a vertical rail: a [marker] on the axis, a connector down to the next
 * entry unless [isLast], and [content] beside it. The connector runs the full height of
 * the content, so it reaches the next marker however long the content is.
 *
 * Measured by hand rather than with IntrinsicSize: content may hold lazy layouts (a
 * CodeBlock scrolls sideways in a LazyRow), and asking a lazy layout for its intrinsic
 * size throws — opening a trace whose step held a code block crashed.
 */
@Composable
internal fun RailRow(
    isLast: Boolean,
    marker: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = Theme.colors
    Layout(
        modifier = modifier,
        content = {
            Box { marker() }
            if (!isLast) Box(Modifier.background(colors.border))
            Column(
                Modifier.padding(bottom = if (isLast) Spacing.none else Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                content = content,
            )
        },
    ) { measurables, constraints ->
        val axis = ControlGeometry.timelineAxisWidth.roundToPx()
        val gap = Spacing.md.roundToPx()
        val line = BorderWidth.thin.roundToPx().coerceAtLeast(1)
        val markerP = measurables[0].measure(Constraints(maxWidth = axis))
        val contentP = measurables.last().measure(
            constraints.copy(minWidth = 0, maxWidth = (constraints.maxWidth - axis - gap).coerceAtLeast(0), minHeight = 0),
        )
        val height = maxOf(contentP.height, markerP.height)
        val lineP = if (isLast) null else measurables[1].measure(
            Constraints.fixed(line, (height - markerP.height).coerceAtLeast(0)),
        )
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else axis + gap + contentP.width
        layout(width, height) {
            markerP.place((axis - markerP.width) / 2, 0)
            lineP?.place((axis - line) / 2, markerP.height)
            contentP.place(axis + gap, 0)
        }
    }
}
