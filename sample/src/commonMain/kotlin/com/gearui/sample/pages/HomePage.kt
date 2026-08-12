package com.gearui.sample.pages

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.LazyListState
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.gearui.components.navbar.NavBar
import com.gearui.components.navbar.NavBarItem
import com.gearui.components.icon.Icons
import com.gearui.components.searchbar.SearchBar
import com.gearui.components.scaffold.PageScaffold
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.i18n.SampleI18n
import com.gearui.sample.i18n.SampleStrings
import com.gearui.sample.config.ComponentCategory
import com.gearui.sample.config.ComponentConfig
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.config.localizedDescription
import com.gearui.theme.Theme

/**
 * HomePage - component index
 *
 * Lists every component by category; tapping one opens its detail page
 * Uses NavBar as the top navigation bar
 *
 * @param listState list scroll state, passed in so the scroll position survives
 * @param onComponentClick component tap callback
 * @param onSettingsClick settings button tap callback
 */
@Composable
fun HomePage(
    listState: LazyListState = rememberLazyListState(),
    onComponentClick: (ComponentInfo) -> Unit,
    onSettingsClick: () -> Unit = {}
) {
    val colors = Theme.colors
    val settingsState = LocalSettingsState.current
    val strings = SampleI18n.strings
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }
    val navBarColor = if (settingsState.themeStyle == ThemeStyle.DARK_PURPLE) colors.primary else colors.surface

    val isEnglish = settingsState.languageTag.startsWith("en", ignoreCase = true)

    // Filter components by search keyword (matching both English and Chinese)
    val filteredComponents = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            ComponentConfig.all
        } else {
            ComponentConfig.all.filter { component ->
                component.nameZh.contains(searchQuery, ignoreCase = true) ||
                        component.nameEn.contains(searchQuery, ignoreCase = true) ||
                        component.descriptionZh.contains(searchQuery, ignoreCase = true) ||
                        component.descriptionEn.contains(searchQuery, ignoreCase = true) ||
                        component.id.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    PageScaffold(
        backgroundColor = colors.background,
        consumeBottomSafeArea = true
    ) {
        Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
            // Combined top area: NavBar and SearchBar as one
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(navBarColor)
            ) {
                NavBar(
                    title = strings.homeTitle,
                    centerTitle = true,
                    backgroundColor = navBarColor,
                    showBottomDivider = false,
                    rightItems = listOf(
                        NavBarItem(
                            icon = Icons.settings,
                            onClick = onSettingsClick
                        )
                    )
                )

                // Search bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(navBarColor)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    SearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = strings.searchPlaceholder,
                        showCancel = searchQuery.isNotEmpty(),
                        onCancel = {
                            searchQuery = ""
                        }
                    )
                }

                // The divider belongs below the search bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.border)
                )
            }

            // Component list
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {

                // Search results, or the category list
                if (searchQuery.isNotEmpty()) {
                    // Search mode: filtered results
                    if (filteredComponents.isEmpty()) {
                        item(key = "no_results") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = strings.noResults,
                                    style = Typography.BodyMedium,
                                    color = colors.mutedForeground
                                )
                            }
                        }
                    } else {
                        item(key = "search_result_card") {
                            ListCard {
                                filteredComponents.forEachIndexed { index, component ->
                                    ComponentListItem(
                                        component = component,
                                        isEnglish = isEnglish,
                                        onClick = {
                                            focusManager.clearFocus()
                                            onComponentClick(component)
                                        },
                                        searchQuery = searchQuery,
                                        showDivider = index < filteredComponents.lastIndex
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Normal mode: grouped by category
                    ComponentCategory.entries.forEach { category ->
                        val components = ComponentConfig.getByCategory(category)

                        if (components.isNotEmpty()) {
                            item(key = "category_${category.name}") {
                                CategoryHeader(
                                    category = category,
                                    count = components.size,
                                    strings = strings
                                )
                            }

                            item(key = "category_card_${category.name}") {
                                ListCard {
                                    components.forEachIndexed { index, component ->
                                        ComponentListItem(
                                            component = component,
                                            isEnglish = isEnglish,
                                            onClick = {
                                                focusManager.clearFocus()
                                                onComponentClick(component)
                                            },
                                            showDivider = index < components.lastIndex
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom blank space (including the safe area)
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            }
        }
    }

    /**
     * Localised category name
     */
    private fun getCategoryDisplayName(category: ComponentCategory, strings: SampleStrings): String {
        return when (category) {
            ComponentCategory.BASIC -> strings.categoryBasic
            ComponentCategory.FORM -> strings.categoryForm
            ComponentCategory.NAVIGATION -> strings.categoryNavigation
            ComponentCategory.DATA_DISPLAY -> strings.categoryDataDisplay
            ComponentCategory.FEEDBACK -> strings.categoryFeedback
            ComponentCategory.LAYOUT -> strings.categoryLayout
        }
    }

    /**
     * Category header
     */
    @Composable
    private fun CategoryHeader(
        category: ComponentCategory,
        count: Int,
        strings: SampleStrings
    ) {
        val colors = Theme.colors

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = getCategoryDisplayName(category, strings),
                    style = Typography.BodyMedium,
                    color = colors.foreground
                )

                Text(
                    text = "$count${strings.componentCountSuffix}",
                    style = Typography.BodySmall,
                    color = colors.mutedForeground
                )
            }
        }
    }

    @Composable
    private fun ListCard(
        content: @Composable ColumnScope.() -> Unit
    ) {
        val colors = Theme.colors
        val shapes = Theme.shapes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shapes.lg)
                .background(colors.surface)
                .border(1.dp, colors.border, shapes.lg)
        ) {
            content()
        }
    }

    /**
     * Component list row
     */
    @Composable
    private fun ComponentListItem(
        component: ComponentInfo,
        isEnglish: Boolean = false,
        onClick: () -> Unit,
        searchQuery: String = "",
        showDivider: Boolean = false
    ) {
        val colors = Theme.colors
        val name = component.nameEn
        val description = component.localizedDescription(isEnglish)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .background(colors.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Component name
                    Text(
                        text = name,
                        style = Typography.BodyLarge,
                        color = colors.foreground
                    )

                    // Component description
                    if (description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            style = Typography.BodySmall,
                            color = colors.mutedForeground
                        )
                    }

                    // Show the component ID in search mode
                    if (searchQuery.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ID: ${component.id}",
                            style = Typography.BodySmall,
                            color = colors.mutedForeground
                        )
                    }
            }

            // Chevron
            Text(
                text = "›",
                style = Typography.TitleLarge,
                color = colors.mutedForeground
            )
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.border)
            )
        }
    }
}
