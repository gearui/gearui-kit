package com.gearui.components.scaffold

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.runtime.LocalRuntimeFlags

/**
 * 页面根脚手架——**统一消费 safe-area** 的唯一正确入口。
 *
 * 架构原则（替代「NavBar 内部独揽 top safe-area」的旧设计）：
 * - safe-area 在框架根层（[com.gearui.runtime.ProvideRuntimeEnvironment]）稳定化为 stableInsets；
 * - 页面根容器 [PageScaffold] 按**显式声明的策略**消费；
 * - NavBar / SearchBar / 自定义 header 都只是放在 safe-area 之下的普通顶部组件，
 *   无论页面顶部是什么，都不会压到状态栏。
 *
 * 策略由页面显式声明，不靠组件去猜：
 * @param consumeTopSafeArea    顶部留出状态栏安全区（默认 true）。
 * @param consumeBottomSafeArea 底部留出 home indicator 安全区（默认 false；底部通常由
 *                              BottomNavBar / 输入框各自消费，避免双重留白）。
 * @param edgeToEdge            全屏页（如图片预览）：忽略安全区，由页面控件自处理（top/bottom=0）。
 */
@Composable
fun PageScaffold(
    modifier: Modifier = Modifier,
    consumeTopSafeArea: Boolean = true,
    consumeBottomSafeArea: Boolean = false,
    edgeToEdge: Boolean = false,
    backgroundColor: Color? = null,
    content: @Composable () -> Unit
) {
    val env = LocalRuntimeEnvironment.current
    val flags = LocalRuntimeFlags.current
    val pipelineOn = flags.unifiedSafeAreaPipeline

    val topPad = if (!edgeToEdge && consumeTopSafeArea && pipelineOn) env.safeArea.top else 0.dp
    val bottomPad = if (!edgeToEdge && consumeBottomSafeArea && pipelineOn) env.safeArea.bottom else 0.dp

    val base = if (backgroundColor != null) modifier.background(backgroundColor) else modifier
    Column(modifier = base.fillMaxSize()) {
        if (topPad > 0.dp) {
            Spacer(modifier = Modifier.height(topPad))
        }
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            content()
        }
        if (bottomPad > 0.dp) {
            Spacer(modifier = Modifier.height(bottomPad))
        }
    }
}
