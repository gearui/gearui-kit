package com.gearui.sample.examples.refresh

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.cell.Cell
import com.gearui.components.navbar.NavBar
import com.gearui.components.refresh.LoadMoreStatus
import com.gearui.components.refresh.loadMoreItem
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

private const val PageSize = 15
private const val LastPage = 5
private const val FailingPage = 3

/**
 * A paged order list: pull down to reload from page one, scroll to the end for the next
 * page. Page 3 fails once so the retry footer can be seen; page 5 is the last.
 *
 * Like the pull-to-refresh page it owns the whole screen: the footer and the pull both
 * belong to the page's own list, not to a list nested in ExamplePage.
 */
@Composable
fun LoadMoreExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val listState = rememberLazyListState()
    val safeBottom = LocalRuntimeEnvironment.current.safeArea.bottom

    var pages by remember { mutableStateOf(1) }
    var status by remember { mutableStateOf(LoadMoreStatus.Idle) }
    var failedOnce by remember { mutableStateOf(false) }
    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberPullRefreshState(refreshing)

    LaunchedEffect(refreshing) {
        if (!refreshing) return@LaunchedEffect
        delay(1000)
        pages = 1
        failedOnce = false
        status = LoadMoreStatus.Idle
        refreshing = false
    }
    LaunchedEffect(status) {
        if (status != LoadMoreStatus.Loading) return@LaunchedEffect
        delay(800)
        val next = pages + 1
        status = when {
            next == FailingPage && !failedOnce -> {
                failedOnce = true
                LoadMoreStatus.Failed
            }
            else -> {
                pages = next
                if (next >= LastPage) LoadMoreStatus.NoMore else LoadMoreStatus.Idle
            }
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
                        text = "滑到底自动加载下一页；第 $FailingPage 页会失败一次，点击底部重试；" +
                            "共 $LastPage 页。下拉回到第一页。已加载 $pages 页",
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground,
                        modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    )
                }
                items(pages * PageSize) { index ->
                    Cell(
                        title = "订单 ${index + 1}",
                        description = "第 ${index / PageSize + 1} 页",
                    )
                }
                loadMoreItem(
                    listState = listState,
                    status = status,
                    onLoadMore = { status = LoadMoreStatus.Loading },
                )
            }
        }
    }
}
