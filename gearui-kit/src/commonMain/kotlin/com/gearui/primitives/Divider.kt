package com.gearui.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.theme.Theme
import com.gearui.foundation.border.BorderWidth

/**
 * Divider - fully Theme-driven divider primitive
 *
 * ✅ Rule: the first line is always `val colors = Theme.colors`
 * ❌ Never: Color(0x...) or hardcoded colours
 *
 * Rework notes:
 * - the hardcoded colours in DividerTokens are gone
 * - Theme.colors.border is used directly
 */

/**
 * Base divider
 *
 * @param thickness line thickness
 * @param insetStart leading inset
 * @param insetEnd trailing inset
 */
@Composable
fun Divider(
    thickness: Dp = 0.5.dp,
    insetStart: Dp = 0.dp,
    insetEnd: Dp = 0.dp
) {
    // ⭐ Framework Rule #1: this is always the first line
    val colors = Theme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = insetStart, end = insetEnd)
            .height(thickness)
            .background(colors.border)
    )
}

/**
 * Convenience aliases for the common dividers
 */

/**
 * Full-width divider (the common case)
 */
@Composable
fun DividerFull() = Divider(
    thickness = BorderWidth.hairline,
    insetStart = 0.dp,
    insetEnd = 0.dp
)

/**
 * Leading-inset divider (for list rows)
 */
@Composable
fun DividerInset() = Divider(
    thickness = BorderWidth.hairline,
    insetStart = 16.dp,
    insetEnd = 0.dp
)

/**
 * Section separator (an 8dp grey block)
 */
@Composable
fun DividerSection() {
    val colors = Theme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(colors.muted)
    )
}
