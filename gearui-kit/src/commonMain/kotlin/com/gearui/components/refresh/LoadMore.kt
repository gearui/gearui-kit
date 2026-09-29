package com.gearui.components.refresh

import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.gearui.foundation.motion.rowPressFeedback
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import com.gearui.components.loading.Loading
import com.gearui.components.loading.LoadingLayout
import com.gearui.components.loading.LoadingSize
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.primitives.Text
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.lazy.LazyListScope
import com.tencent.kuikly.compose.foundation.lazy.LazyListState
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.semantics.Role
import kotlinx.coroutines.flow.first

/**
 * Where a paged list stands, from its footer's point of view.
 *
 * The states of KuiklyUI's own `FooterRefresh` (IDLE, REFRESHING, FAILURE,
 * NONE_MORE_DATA), for a lazy list: a successful load simply returns to [Idle].
 */
enum class LoadMoreStatus {
    /** More pages may exist; the next one loads when the footer comes into view. */
    Idle,

    /** A page is loading. */
    Loading,

    /** The last page failed. Nothing loads until the user taps the footer. */
    Failed,

    /** Every page is loaded. */
    NoMore,
}

/**
 * The footer of a paged list: loads the next page when it scrolls into view, and says
 * what is happening — loading, failed (tap to retry), or no more.
 *
 * Call it last inside a lazy list, as [pullRefreshItem] is called first:
 *
 * ```kotlin
 * GearLazyColumn(state = listState) {
 *     pullRefreshItem(refreshState, onRefresh = ::reload, listState = listState)
 *     items(orders) { OrderRow(it) }
 *     loadMoreItem(listState, status, onLoadMore = ::loadNextPage)
 * }
 * ```
 *
 * [status] is yours, like `isRefreshing`: set it to [LoadMoreStatus.Loading] inside
 * [onLoadMore], then to `Idle`, `Failed` or `NoMore` when the page settles. The footer
 * asks again only once it is visible *and* [status] is back to `Idle`, so a page that
 * pushes it off screen does not chain another request, and one that leaves it on screen
 * (a short first page) fills the viewport.
 *
 * An empty first page is not a footer state: show `Empty` instead of the list.
 */
fun LazyListScope.loadMoreItem(
    listState: LazyListState,
    status: LoadMoreStatus,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    item(key = LoadMoreKey, contentType = LoadMoreKey) {
        val load by rememberUpdatedState(onLoadMore)
        LaunchedEffect(status, listState) {
            if (status != LoadMoreStatus.Idle) return@LaunchedEffect
            // Wait for a layout in which the footer is actually on screen: the frame that
            // appends a page can still hold the footer before measuring pushes it out.
            snapshotFlow { listState.footerVisible() }.first { it }
            load()
        }
        LoadMoreFooter(status = status, onRetry = { load() }, modifier = modifier)
    }
}

/**
 * The footer's look on its own, for a list that pages some other way.
 * [onRetry] is used in the [LoadMoreStatus.Failed] state.
 */
@Composable
fun LoadMoreFooter(
    status: LoadMoreStatus,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = Theme.colors
    val strings = I18n.strings.common
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ControlGeometry.loadMoreHeight)
            .then(
                if (status == LoadMoreStatus.Failed) {
                    Modifier
                        .rowPressFeedback(interaction = interaction, shape = RectangleShape, base = Color.Transparent)
                        .clickable(interactionSource = interaction, indication = null, role = Role.Button) { onRetry() }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        when (status) {
            LoadMoreStatus.Idle, LoadMoreStatus.Loading -> Row(
                horizontalArrangement = Arrangement.spacedBy(ControlGeometry.alertGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Loading(size = LoadingSize.SMALL, layout = LoadingLayout.HORIZONTAL, color = colors.mutedForeground)
                Text(text = strings.loading, style = Theme.typography.bodySmall, color = colors.mutedForeground)
            }
            LoadMoreStatus.Failed ->
                Text(text = strings.loadMoreFailed, style = Theme.typography.bodySmall, color = colors.mutedForeground)
            LoadMoreStatus.NoMore ->
                Text(text = strings.noMoreData, style = Theme.typography.bodySmall, color = colors.mutedForeground)
        }
    }
}

private const val LoadMoreKey = "gearui-load-more"

private fun LazyListState.footerVisible(): Boolean {
    val info = layoutInfo
    val last = info.visibleItemsInfo.lastOrNull() ?: return false
    return last.index == info.totalItemsCount - 1
}
