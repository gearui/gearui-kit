package com.gearui.sample.examples.refresh

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.cell.Cell
import com.gearui.components.navbar.NavBar
import com.gearui.components.refresh.pullRefreshItem
import com.gearui.components.refresh.rememberPullRefreshState
import com.gearui.components.scaffold.PageScaffold
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.GearLazyColumn
import com.gearui.foundation.primitives.Text
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.sample.config.ComponentInfo
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier
import kotlinx.coroutines.delay

/**
 * Pull-to-refresh owns the whole page on purpose.
 *
 * The gesture belongs to the page's own list: a list nested inside another scrolling
 * list never gets the drag, so demonstrating it inside the shared ExamplePage would
 * have shown a control that cannot work the way callers will use it.
 */
@Composable
fun RefreshExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val listState = rememberLazyListState()
    var refreshing by remember { mutableStateOf(false) }
    var round by remember { mutableStateOf(1) }
    val refreshState = rememberPullRefreshState(refreshing)
    val safeBottom = LocalRuntimeEnvironment.current.safeArea.bottom

    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(1200)
            round += 1
            refreshing = false
        }
    }

    PageScaffold(
        backgroundColor = colors.background,
        topSafeAreaColor = colors.surface,
        consumeBottomSafeArea = false,
    ) {
        Column(Modifier.fillMaxSize().background(colors.background)) {
            NavBar(
                title = component.nameEn,
                centerTitle = true,
                useDefaultBack = true,
                onBackClick = onBack,
                backgroundColor = colors.surface,
            )
            GearLazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = safeBottom),
            ) {
                pullRefreshItem(
                    state = refreshState,
                    onRefresh = { refreshing = true },
                    listState = listState,
                )
                item {
                    Text(
                        text = "下拉试试：超过阈值提示松手，刷新中转圈，完成后列表换一批。已刷新 $round 次",
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground,
                        modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    )
                }
                items(20) { index ->
                    Cell(title = "第 $round 批 · 第 ${index + 1} 条")
                }
            }
        }
    }
}
