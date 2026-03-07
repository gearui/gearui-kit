package com.gearui.sample.pages

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.navbar.NavBar
import com.gearui.foundation.primitives.GearLazyColumn
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.theme.Theme

/**
 * ExamplePage - 组件示例页面通用包装器
 *
 * 为每个组件提供统一的展示容器：
 * - 顶部导航栏（使用 NavBar，带返回按钮）
 * - 可滚动的内容区域（使用 GearLazyColumn，支持滚动关闭弹层）
 * - 一致的样式和布局
 */
@Composable
fun ExamplePage(
    component: ComponentInfo,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val settingsState = LocalSettingsState.current
    val listState = rememberLazyListState()
    val navBarColor = if (settingsState.themeStyle == ThemeStyle.DARK_PURPLE) colors.primaryActive else colors.surface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // 顶部导航栏 - 使用 NavBar
        NavBar(
            title = component.nameEn,
            centerTitle = true,
            useDefaultBack = true,
            onBackClick = onBack,
            backgroundColor = navBarColor
        )

        // 示例内容区域 - 使用 GearLazyColumn（支持滚动关闭弹层）
        GearLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            state = listState,
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * ExampleSection - 示例区块
 *
 * 用于组织同一组件的不同示例
 */
@Composable
fun ExampleSection(
    title: String,
    description: String = "",
    useCardContainer: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 标题和描述
        if (title.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = Typography.TitleMedium,
                    color = colors.textPrimary
                )

                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        if (useCardContainer) {
            // 示例内容
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.large)
                    .background(colors.surface)
                    .border(1.dp, colors.border, shapes.large)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                content()
            }
        } else {
            content()
        }
    }
}
