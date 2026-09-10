package com.gearui.foundation.sheet

import androidx.compose.runtime.Composable
import com.gearui.foundation.layout.Spacing
import com.gearui.theme.Theme
import com.gearui.unit.dp
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier

/**
 * The bar at the top of a sheet that says it can be dragged away.
 *
 * It is an affordance, not decoration: a sheet with no grabber and no visible
 * close control gives no indication that a downward drag does anything, and a
 * gesture nobody knows about is not a feature. So a sheet shows it exactly when
 * the drag is wired — see [com.gearui.gestures.swipeDismiss].
 *
 * Size follows the platform it is borrowed from: 36 x 5 points, fully rounded,
 * centred, in a fill light enough to read as a handle rather than as content.
 */
@Composable
fun SheetGrabber(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.xs, bottom = Spacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(5.dp)
                .background(
                    // Border rather than mutedForeground: the grabber sits above
                    // the content and must not compete with it for attention.
                    color = Theme.colors.border,
                    // A capsule, which at 5dp tall is the shape scale's `full`
                    // rather than an off-scale radius picked to look right.
                    shape = Theme.shapes.full,
                ),
        )
    }
}
