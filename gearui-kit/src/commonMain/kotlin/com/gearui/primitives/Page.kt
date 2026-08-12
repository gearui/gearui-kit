package com.gearui.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.theme.Theme

/**
 * Page - page-level container primitive
 *
 * ✅ Supplies the page background colour automatically (following the theme)
 * ✅ Fills the screen automatically
 * ✅ Zero configuration in product code
 *
 * Use cases:
 * - root container of a Gallery page
 * - root container of a Showcase page
 * - any page needing a fullscreen background
 *
 * Architectural rules:
 * - Page is a primitive and follows the Theme automatically
 * - product code never sets background by hand
 * - UI = f(Theme) holds automatically
 */
@Composable
fun Page(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    // ⭐ Framework Rule #1: this is always the first line
    val colors = Theme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),  // ⭐ 自动跟随主题
        content = content
    )
}
