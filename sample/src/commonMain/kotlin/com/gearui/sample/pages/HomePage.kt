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
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.gearui.components.navbar.NavBar
import com.gearui.components.navbar.NavBarItem
import com.gearui.components.icon.Icons
import com.gearui.components.searchbar.SearchBar
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
 * HomePage - 组件列表主页
 *
 * 按分类展示所有组件，点击跳转到对应的详情页
 * 使用 NavBar 作为顶部导航栏
 *
 * @param listState 列表滚动状态，从外部传入以保持滚动位置
 * @param onComponentClick 组件点击回调
 * @param onSettingsClick 设置按钮点击回调
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
    val navBarColor = if (settingsState.themeStyle == ThemeStyle.DARK_PURPLE) colors.primaryActive else colors.surface

    // 获取安全区域
    val configuration = LocalConfiguration.current
    val safeAreaBottom = configuration.safeAreaInsets.bottom.dp

    val isEnglish = settingsState.languageTag.startsWith("en", ignoreCase = true)

    // 根据搜索关键词过滤组件（搜索同时匹配中英文）
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

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        // 复合顶部区域：NavBar + SearchBar 一体化
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

            // 搜索栏
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

            // 分割线应在搜索栏下方
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.divider)
            )
        }

        // 组件列表
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // 显示搜索结果或分类列表
            if (searchQuery.isNotEmpty()) {
                // 搜索模式：显示过滤后的结果
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
                                color = colors.textPlaceholder
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
                // 正常模式：按分类展示
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

            // 底部空白（包含安全区域）
            item {
                Spacer(modifier = Modifier.height(32.dp + safeAreaBottom))
            }
        }
    }
}

/**
 * 获取分类的本地化名称
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
 * 分类标题
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
                color = colors.textPrimary
            )

            Text(
                text = "$count${strings.componentCountSuffix}",
                style = Typography.BodySmall,
                color = colors.textSecondary
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
            .clip(shapes.large)
            .background(colors.surface)
            .border(1.dp, colors.border, shapes.large)
    ) {
        content()
    }
}

/**
 * 组件列表项
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
                // 组件名称
                Text(
                    text = name,
                    style = Typography.BodyLarge,
                    color = colors.textPrimary
                )

                // 组件描述
                if (description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // 如果在搜索模式，显示组件ID
                if (searchQuery.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ID: ${component.id}",
                        style = Typography.BodySmall,
                        color = colors.textPlaceholder
                    )
                }
            }

            // 右箭头
            Text(
                text = "›",
                style = Typography.TitleLarge,
                color = colors.textPlaceholder
            )
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.divider)
            )
        }
    }
}
