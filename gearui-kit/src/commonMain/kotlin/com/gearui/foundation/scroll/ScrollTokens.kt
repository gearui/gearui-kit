package com.gearui.foundation.scroll

import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * ScrollTokens - 滚动容器设计规范
 *
 * 统一所有页面的:
 * - Content padding
 * - 元素间距
 * - Bounce 行为
 * - Scrollbar 显示策略
 */
data class ScrollTokens(
    /** 内容区 padding */
    val contentPadding: PaddingValues,

    /** 元素间距 */
    val spacing: Dp,

    /** 是否启用弹性回弹 */
    val bounceEnabled: Boolean,

    /** 是否显示滚动条 */
    val showScrollbar: Boolean
) {
    companion object {
        /** 默认页面滚动 */
        val Default = ScrollTokens(
            contentPadding = PaddingValues(16.dp),
            spacing = 12.dp,
            bounceEnabled = true,
            showScrollbar = false
        )

        /** 密集布局 */
        val Dense = Default.copy(
            spacing = 8.dp
        )

        /** 无 padding 场景（如全屏） */
        val None = Default.copy(
            contentPadding = PaddingValues(0.dp)
        )
    }
}
