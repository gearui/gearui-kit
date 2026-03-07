package com.gearui.components.pagination

import androidx.compose.runtime.Composable
import com.gearui.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Pagination - page number switcher with previous/next controls.
 *
 * @param currentPage current page, 1-based
 * @param totalPages total pages
 * @param onPageChange callback when page changes
 * @param modifier modifier applied to root container
 * @param maxVisiblePages max numeric buttons shown
 * @param previousLabel previous button text
 * @param nextLabel next button text
 */
@Composable
fun Pagination(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxVisiblePages: Int = 5,
    previousLabel: String = "<",
    nextLabel: String = ">"
) {
    if (totalPages <= 0) return

    val safeCurrent = currentPage.coerceIn(1, totalPages)
    val items = buildPaginationItems(
        currentPage = safeCurrent,
        totalPages = totalPages,
        maxVisiblePages = maxVisiblePages
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PaginationButton(
            label = previousLabel,
            enabled = safeCurrent > 1,
            onClick = { onPageChange(safeCurrent - 1) }
        )

        items.forEach { item ->
            when (item) {
                is PaginationItem.Page -> {
                    val selected = item.value == safeCurrent
                    val colors = Theme.colors
                    val shapes = Theme.shapes
                    Box(
                        modifier = Modifier
                            .background(
                                if (selected) colors.primary else colors.surface,
                                shapes.default
                            )
                            .border(
                                width = 1.dp,
                                color = if (selected) colors.primary else colors.border,
                                shape = shapes.default
                            )
                            .clickable { onPageChange(item.value) }
                            .padding(horizontal = Spacing.spacer12.dp, vertical = Spacing.spacer8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.value.toString(),
                            style = Typography.BodySmall,
                            color = if (selected) colors.onPrimary else colors.textPrimary
                        )
                    }
                }

                PaginationItem.Ellipsis -> {
                    Text(
                        text = "...",
                        style = Typography.BodySmall,
                        color = Theme.colors.textSecondary
                    )
                }
            }
        }

        PaginationButton(
            label = nextLabel,
            enabled = safeCurrent < totalPages,
            onClick = { onPageChange(safeCurrent + 1) }
        )
    }
}

@Composable
private fun PaginationButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    Box(
        modifier = Modifier
            .background(
                if (enabled) colors.surface else colors.disabledContainer,
                shapes.default
            )
            .border(1.dp, colors.border, shapes.default)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = Spacing.spacer12.dp, vertical = Spacing.spacer8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = Typography.BodySmall,
            color = if (enabled) colors.textPrimary else colors.textDisabled,
            maxLines = 1,
            softWrap = false
        )
    }
}

private sealed class PaginationItem {
    data class Page(val value: Int) : PaginationItem()
    object Ellipsis : PaginationItem()
}

private fun buildPaginationItems(
    currentPage: Int,
    totalPages: Int,
    maxVisiblePages: Int
): List<PaginationItem> {
    val normalizedMax = maxVisiblePages.coerceAtLeast(3)
    if (totalPages <= normalizedMax) {
        return (1..totalPages).map { PaginationItem.Page(it) }
    }

    val siblingCount = ((normalizedMax - 3) / 2).coerceAtLeast(0)
    val leftSibling = (currentPage - siblingCount).coerceAtLeast(2)
    val rightSibling = (currentPage + siblingCount).coerceAtMost(totalPages - 1)
    val showLeftEllipsis = leftSibling > 2
    val showRightEllipsis = rightSibling < totalPages - 1

    return when {
        !showLeftEllipsis && showRightEllipsis -> {
            val leftRangeEnd = (2 + siblingCount * 2).coerceAtMost(totalPages - 1)
            buildList {
                (1..leftRangeEnd).forEach { add(PaginationItem.Page(it)) }
                add(PaginationItem.Ellipsis)
                add(PaginationItem.Page(totalPages))
            }
        }

        showLeftEllipsis && !showRightEllipsis -> {
            val rightRangeStart = (totalPages - 1 - siblingCount * 2).coerceAtLeast(2)
            buildList {
                add(PaginationItem.Page(1))
                add(PaginationItem.Ellipsis)
                (rightRangeStart..totalPages).forEach { add(PaginationItem.Page(it)) }
            }
        }

        else -> {
            buildList {
                add(PaginationItem.Page(1))
                add(PaginationItem.Ellipsis)
                (leftSibling..rightSibling).forEach { add(PaginationItem.Page(it)) }
                add(PaginationItem.Ellipsis)
                add(PaginationItem.Page(totalPages))
            }
        }
    }
}
