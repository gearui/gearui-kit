package com.gearui.foundation.primitives

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.gearui.foundation.scroll.*

/**
 * ScrollView - 页面级滚动容器 Primitive
 *
 * 地位等价于:
 * - Material3: Scaffold body
 * - Flutter: CustomScrollView
 *
 * 职责:
 * - 统一 padding/spacing
 * - 统一 bounce/physics
 * - 统一 scrollbar 策略
 * - Safe area 处理
 *
 * 实现注意:
 * - KuiklyUI 不支持 Modifier.verticalScroll
 * - 使用 LazyColumn 实现滚动容器
 *
 * 使用场景:
 * - 普通页面滚动
 * - 表单页
 * - 详情页
 *
 * 不使用场景:
 * - 长列表 (用 List)
 * - 虚拟化场景 (用 List)
 */
@Composable
fun ScrollView(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: ScrollState = rememberScrollState(),
    tokens: ScrollTokens = ScrollTokens.Default,
    physics: ScrollPhysics = ScrollPhysics.Platform,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    LazyColumn(
        modifier = modifier.then(physics.modifier()),
        state = state.raw,
        contentPadding = tokens.contentPadding,
        horizontalAlignment = horizontalAlignment
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(tokens.spacing),
                horizontalAlignment = horizontalAlignment,
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
    }
}
