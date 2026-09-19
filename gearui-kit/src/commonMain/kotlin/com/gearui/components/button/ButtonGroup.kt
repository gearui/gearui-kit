package com.gearui.components.button

import androidx.compose.runtime.Composable
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.RowScope
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip

/**
 * ButtonGroup — HeroUI v3 `ButtonGroup` for mobile: related actions joined into one
 * control, so they read as one choice rather than several buttons that happen to sit
 * together ("Day / Week / Month", "Copy / Share").
 *
 * The group clips its children to a single pill and draws a hairline between them.
 * Give each child `Modifier.weight(1f)` for equal widths:
 *
 * ```kotlin
 * ButtonGroup {
 *     Button("Copy", onClick = ::copy, type = ButtonType.TEXT, modifier = Modifier.weight(1f))
 *     Button("Share", onClick = ::share, type = ButtonType.TEXT, modifier = Modifier.weight(1f))
 * }
 * ```
 *
 * For a group where exactly one option stays selected use
 * [com.gearui.components.segmented.SegmentedControl] instead; ButtonGroup fires actions.
 */
@Composable
fun ButtonGroup(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = Theme.colors
    Row(
        modifier = modifier
            .clip(Theme.shapes.full)
            .background(colors.muted),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        content()
    }
}

/** A hairline between two [ButtonGroup] children. Place it yourself so groups can skip it. */
@Composable
fun ButtonGroupDivider() {
    Row(
        Modifier
            .width(BorderWidth.thin)
            .height(ControlGeometry.controlMedium)
            .background(Theme.colors.separator)
    ) {}
}
