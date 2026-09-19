package com.gearui.components.refresh

import androidx.compose.runtime.Composable
import com.gearui.components.loading.Loading
import com.gearui.components.loading.LoadingLayout
import com.gearui.components.loading.LoadingSize
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.primitives.Text
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.lazy.LazyListScope
import com.tencent.kuikly.compose.foundation.lazy.LazyListState
import com.tencent.kuikly.compose.material3.PullToRefreshState
import com.tencent.kuikly.compose.material3.pullToRefreshItem
import com.tencent.kuikly.compose.material3.rememberPullToRefreshState
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.unit.Dp

/**
 * Pull-to-refresh, themed for GearUI.
 *
 * The gesture, the rubber-band and the release threshold belong to the platform list,
 * so this builds on Kuikly's pull-to-refresh item rather than re-reading touches. What
 * GearUI adds is the indicator: the theme's spinner and copy from [I18n], instead of
 * the framework's placeholder arrows.
 *
 * It is the first item of the list, so call it first inside a lazy list:
 *
 * ```kotlin
 * val listState = rememberLazyListState()
 * val refreshState = rememberPullRefreshState(isRefreshing)
 * GearLazyColumn(state = listState) {
 *     pullRefreshItem(refreshState, onRefresh = ::reload, listState = listState)
 *     items(rows) { Row(it) }
 * }
 * ```
 *
 * Keep `isRefreshing` in your own state and set it false when the load finishes; the
 * list stays open at the threshold until you do.
 */
fun LazyListScope.pullRefreshItem(
    state: PullToRefreshState,
    onRefresh: () -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    topInset: Dp = Dp(0f),
    threshold: Dp = ControlGeometry.pullRefreshThreshold,
) {
    pullToRefreshItem(
        state = state,
        onRefresh = onRefresh,
        scrollState = listState,
        modifier = modifier,
        topInset = topInset,
        refreshThreshold = threshold,
    ) { progress, refreshing, _ ->
        PullRefreshIndicator(progress, refreshing)
    }
}

/** Remembers the pull state; [isRefreshing] is owned by the caller's load. */
@Composable
fun rememberPullRefreshState(isRefreshing: Boolean): PullToRefreshState =
    rememberPullToRefreshState(isRefreshing)

/**
 * The indicator: the spinner fades in with the pull and spins while loading. The
 * label says what the gesture will do, so a partial pull is never mistaken for a
 * finished refresh.
 */
@Composable
fun PullRefreshIndicator(progress: Float, refreshing: Boolean) {
    val colors = Theme.colors
    val label = when {
        refreshing -> I18n.strings.common.refreshing
        progress >= 1f -> I18n.strings.common.releaseToRefresh
        else -> I18n.strings.common.pullToRefresh
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(ControlGeometry.alertGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.alpha(if (refreshing) 1f else progress.coerceIn(0f, 1f)),
    ) {
        Loading(size = LoadingSize.SMALL, layout = LoadingLayout.HORIZONTAL, color = colors.mutedForeground)
        Text(text = label, style = Theme.typography.bodySmall, color = colors.mutedForeground)
    }
}
