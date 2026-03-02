package com.gearui.foundation.scroll

import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.gearui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * ListTokens - 列表设计规范
 *
 * 统一:
 * - Item 间距
 * - Divider 策略
 * - Content padding
 */
data class ListTokens(
    /** 列表项间距 */
    val itemSpacing: Dp,

    /** 是否显示分割线 */
    val divider: Boolean,

    /** 内容区 padding */
    val contentPadding: PaddingValues
) {
    companion object {
        /** 普通列表 */
        val Default = ListTokens(
            itemSpacing = 8.dp,
            divider = false,
            contentPadding = PaddingValues(16.dp)
        )

        /** 设置页风格（带分割线） */
        val Settings = ListTokens(
            itemSpacing = 0.dp,
            divider = true,
            contentPadding = PaddingValues(0.dp)
        )

        /** 超密集 */
        val Dense = Default.copy(
            itemSpacing = 4.dp
        )
    }
}
