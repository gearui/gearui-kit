package com.gearui.sample.examples.searchbar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.searchbar.SearchBar
import com.gearui.components.searchbar.SearchBarShape
import com.gearui.components.searchbar.SearchBarWithAction
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * SearchBar 组件示例
 *
 */
@Composable
fun SearchBarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 各种搜索栏的状态
    var basicSearchValue by remember { mutableStateOf("") }
    var roundedSearchValue by remember { mutableStateOf("") }
    var squareSearchValue by remember { mutableStateOf("") }
    var cancelSearchValue by remember { mutableStateOf("") }
    var actionSearchValue by remember { mutableStateOf("") }
    var disabledSearchValue by remember { mutableStateOf("搜索内容") }

    // 搜索结果提示
    var searchResult by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础搜索栏
        ExampleSection(
            title = "基础搜索栏",
            description = "最基本的搜索输入框"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SearchBar(
                    value = basicSearchValue,
                    onValueChange = { basicSearchValue = it },
                    placeholder = "请输入搜索内容"
                )

                if (basicSearchValue.isNotEmpty()) {
                    Text(
                        text = "输入内容: $basicSearchValue",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // 圆角样式
        ExampleSection(
            title = "圆角搜索栏",
            description = "圆角矩形样式的搜索框"
        ) {
            SearchBar(
                value = roundedSearchValue,
                onValueChange = { roundedSearchValue = it },
                placeholder = "圆角搜索框",
                shape = SearchBarShape.ROUNDED
            )
        }

        // 直角样式
        ExampleSection(
            title = "直角搜索栏",
            description = "直角矩形样式的搜索框"
        ) {
            SearchBar(
                value = squareSearchValue,
                onValueChange = { squareSearchValue = it },
                placeholder = "直角搜索框",
                shape = SearchBarShape.SQUARE
            )
        }

        // 带取消按钮
        ExampleSection(
            title = "带取消按钮",
            description = "显示取消按钮，点击可清空并取消搜索"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SearchBar(
                    value = cancelSearchValue,
                    onValueChange = { cancelSearchValue = it },
                    placeholder = "搜索",
                    showCancel = true,
                    onCancel = {
                        cancelSearchValue = ""
                        searchResult = "已取消搜索"
                    }
                )

                if (searchResult.isNotEmpty()) {
                    Text(
                        text = searchResult,
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // 带操作按钮
        ExampleSection(
            title = "带搜索按钮",
            description = "右侧带搜索按钮，点击触发搜索"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SearchBarWithAction(
                    value = actionSearchValue,
                    onValueChange = { actionSearchValue = it },
                    actionText = "搜索",
                    onAction = { query ->
                        searchResult = "正在搜索: $query"
                    },
                    placeholder = "输入关键词"
                )

                if (searchResult.isNotEmpty() && actionSearchValue.isNotEmpty()) {
                    Text(
                        text = searchResult,
                        style = Typography.BodySmall,
                        color = colors.primary
                    )
                }
            }
        }

        // 禁用状态
        ExampleSection(
            title = "禁用状态",
            description = "不可编辑的搜索框"
        ) {
            SearchBar(
                value = disabledSearchValue,
                onValueChange = { },
                placeholder = "搜索",
                enabled = false
            )
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "SearchBar 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. 支持圆角和直角两种形状",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. 输入内容时显示清除按钮",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. 可选显示取消按钮",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. SearchBarWithAction 提供搜索按钮",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. 支持禁用状态",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
