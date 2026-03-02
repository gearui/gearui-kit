package com.gearui.components.navbar

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * NavBar - 导航栏组件
 *
 * 用于不同页面之间切换或者跳转，位于内容区的上方，系统状态栏的下方。
 *
 * 特性：
 * - 支持标题居中/左对齐
 * - 支持左侧返回按钮
 * - 支持左右侧自定义操作按钮
 * - 支持自定义标题组件
 * - 支持副标题
 * - 支持自定义背景色
 *
 * - centerTitle=true: 使用 Box 层叠布局，标题绝对居中
 * - centerTitle=false: 使用 Row 布局，标题在左侧按钮之后
 */
@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    title: String = "",
    titleColor: Color? = null,
    centerTitle: Boolean = true,
    height: Dp = 48.dp,
    backgroundColor: Color? = null,
    useDefaultBack: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    leftItems: List<NavBarItem> = emptyList(),
    rightItems: List<NavBarItem> = emptyList(),
    titleWidget: (@Composable () -> Unit)? = null,
    belowTitleWidget: (@Composable () -> Unit)? = null,
    useSafeArea: Boolean = true,
    showBottomDivider: Boolean = true
) {
    val colors = Theme.colors
    val bgColor = backgroundColor ?: colors.surface
    val textColor = titleColor ?: colors.textPrimary

    // 获取安全区域
    val configuration = LocalConfiguration.current
    val safeAreaTop = if (useSafeArea) configuration.safeAreaInsets.top.dp else 0.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
    ) {
        // 顶部安全区域填充
        if (useSafeArea && safeAreaTop > 0.dp) {
            Spacer(modifier = Modifier.height(safeAreaTop))
        }
        if (centerTitle) {
            // 居中模式：使用 Box 层叠布局，确保标题绝对居中
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .padding(horizontal = 16.dp)
            ) {
                // 底层：标题区域（绝对居中）
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (titleWidget != null) {
                        titleWidget()
                    } else if (title.isNotEmpty()) {
                        Text(
                            text = title,
                            style = Typography.TitleMedium,
                            color = textColor
                        )
                    }
                }

                // 上层：左侧操作区域
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (useDefaultBack) {
                        NavBarIconButton(
                            icon = Icons.chevron_left,
                            iconColor = textColor,
                            onClick = onBackClick
                        )
                    }
                    leftItems.forEach { item ->
                        NavBarIconButton(
                            icon = item.icon,
                            iconColor = item.iconColor ?: textColor,
                            onClick = item.onClick
                        )
                    }
                }

                // 上层：右侧操作区域
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rightItems.forEach { item ->
                        NavBarIconButton(
                            icon = item.icon,
                            iconColor = item.iconColor ?: textColor,
                            onClick = item.onClick
                        )
                    }
                }
            }
        } else {
            // 左对齐模式：使用 Row 布局
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧按钮区域
                if (useDefaultBack) {
                    NavBarIconButton(
                        icon = Icons.chevron_left,
                        iconColor = textColor,
                        onClick = onBackClick
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                leftItems.forEach { item ->
                    NavBarIconButton(
                        icon = item.icon,
                        iconColor = item.iconColor ?: textColor,
                        onClick = item.onClick
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // 标题区域
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (titleWidget != null) {
                        titleWidget()
                    } else if (title.isNotEmpty()) {
                        Text(
                            text = title,
                            style = Typography.TitleMedium,
                            color = textColor
                        )
                    }
                }

                // 右侧按钮区域
                rightItems.forEach { item ->
                    Spacer(modifier = Modifier.width(8.dp))
                    NavBarIconButton(
                        icon = item.icon,
                        iconColor = item.iconColor ?: textColor,
                        onClick = item.onClick
                    )
                }
            }
        }

        // 副标题区域
        if (belowTitleWidget != null) {
            belowTitleWidget()
        }

        if (showBottomDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.divider)
            )
        }
    }
}

/**
 * NavBar 图标按钮
 */
@Composable
private fun NavBarIconButton(
    icon: String,
    iconColor: Color,
    onClick: (() -> Unit)?
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (icon in Icons.all) {
            Icon(
                name = icon,
                size = 24.dp,
                tint = iconColor
            )
        } else {
            Text(
                text = icon,
                style = Typography.TitleLarge,
                color = iconColor
            )
        }
    }
}

/**
 * NavBar 项数据类
 */
data class NavBarItem(
    val icon: String,
    val iconColor: Color? = null,
    val onClick: (() -> Unit)? = null
)
