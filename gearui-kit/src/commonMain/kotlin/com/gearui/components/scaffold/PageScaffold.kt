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
 * Page root scaffold - the one correct place where safe-area is **consumed**.
 *
 * Architectural rules (replacing the old design where NavBar owned the top safe-area):
 * - safe-area is stabilised into stableInsets at the framework root ([com.gearui.runtime.ProvideRuntimeEnvironment]);
 * - the page root container [PageScaffold] consumes it according to an **explicitly declared policy**;
 * - NavBar, SearchBar and custom headers are just ordinary top components sitting below the safe area,
 *   so whatever is at the top of a page, it never runs under the status bar.
 *
 * The policy is declared by the page and never guessed by a component:
 * @param consumeTopSafeArea    leave room for the status bar at the top (default true).
 * @param consumeBottomSafeArea leave room for the home indicator at the bottom (default false; the bottom is usually
 *                              consumed by BottomNavBar or an input field, and doing both would double the padding).
 * @param edgeToEdge            fullscreen pages (image preview and the like): ignore the safe area and let the page handle it (top/bottom = 0).
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
