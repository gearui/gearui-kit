package com.gearui.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.theme.Theme

/**
 * Page - 页面级容器原语
 *
 * ✅ 自动提供页面背景色（跟随主题）
 * ✅ 自动填充全屏
 * ✅ 业务代码零配置
 *
 * 使用场景：
 * - Gallery 页面根容器
 * - Showcase 页面根容器
 * - 任何需要全屏背景的页面
 *
 * 架构原则：
 * - Page 是 Primitive，自动跟随 Theme
 * - 业务代码不需要手动设置 background
 * - UI = f(Theme) 自动生效
 */
@Composable
fun Page(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val colors = Theme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),  // ⭐ 自动跟随主题
        content = content
    )
}
