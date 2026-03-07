package com.gearui.sample.examples.pagination

import androidx.compose.runtime.*
import com.gearui.components.pagination.Pagination
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

@Composable
fun PaginationExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    var pageA by remember { mutableStateOf(1) }
    var pageB by remember { mutableStateOf(6) }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "Basic Pagination",
            description = "Windowed pages with ellipsis."
        ) {
            Pagination(
                currentPage = pageA,
                totalPages = 12,
                onPageChange = { pageA = it }
            )
            Text(
                text = "Current page: $pageA",
                style = Typography.BodySmall,
                color = colors.textSecondary
            )
        }

        ExampleSection(
            title = "Compact Pagination",
            description = "Limits the visible pages to 3."
        ) {
            Pagination(
                currentPage = pageB,
                totalPages = 20,
                maxVisiblePages = 3,
                onPageChange = { pageB = it }
            )
            Text(
                text = "Current page: $pageB",
                style = Typography.BodySmall,
                color = colors.textSecondary
            )
        }
    }
}
