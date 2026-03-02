package com.gearui.components.tabbar

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.gearui.Spacing

/**
 * TabBar - 底部标签栏组件
 *
 * 用于在不同功能模块之间进行快速切换，位于页面底部。
 *
 * - 纯文本标签栏
 * - 图标+文本标签栏
 * - 纯图标标签栏
 * - 弱选中样式
 * - 胶囊样式
 * - 徽标显示
 */

/**
 * TabBar 基本类型
 */
enum class TabBarType {
    /** 纯文本 */
    TEXT,

    /** 图标+文本 */
    ICON_TEXT,

    /** 纯图标 */
    ICON
}

/**
 * TabBar 组件样式
 */
enum class TabBarComponentType {
    /** 普通样式 */
    NORMAL,

    /** 带胶囊背景的选中样式 */
    LABEL
}

/**
 * TabBar 外观类型
 */
enum class TabBarOutlineType {
    /** 填充样式 */
    FILLED,

    /** 胶囊样式 */
    CAPSULE
}

/**
 * 单个 Tab 配置
 */
data class TabConfig(
    /** Tab 文本 */
    val text: String = "",
    /** 选中图标（可以是 emoji 或图标文字） */
    val selectedIcon: String = "",
    /** 未选中图标 */
    val unselectedIcon: String = "",
    /** 徽标文本（如 "99+"） */
    val badge: String? = null,
    /** 是否显示红点 */
    val showRedPoint: Boolean = false,
    /** 点击回调 */
    val onTap: (() -> Unit)? = null
)

/**
 * TabBar 底部标签栏
 */
@Composable
fun TabBar(
    modifier: Modifier = Modifier,
    type: TabBarType = TabBarType.TEXT,
    componentType: TabBarComponentType = TabBarComponentType.LABEL,
    outlineType: TabBarOutlineType = TabBarOutlineType.FILLED,
    tabs: List<TabConfig>,
    selectedIndex: Int = 0,
    onTabSelected: ((Int) -> Unit)? = null,
    height: Dp = 56.dp,
    backgroundColor: Color? = null,
    selectedBgColor: Color? = null,
    unselectedBgColor: Color? = null,
    showTopBorder: Boolean = true,
    useVerticalDivider: Boolean = false,
    useSafeArea: Boolean = true
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    val isCapsule = outlineType == TabBarOutlineType.CAPSULE
    val bgColor = backgroundColor ?: colors.surface
    val selectedBg = selectedBgColor ?: colors.surfaceVariant
    val tabHeight = if (type == TabBarType.ICON) 48.dp else height
    val configuration = LocalConfiguration.current
    val safeAreaBottom = if (useSafeArea) configuration.safeAreaInsets.bottom.dp else 0.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isCapsule) {
                    Modifier.padding(horizontal = Spacing.spacer16.dp)
                } else {
                    Modifier
                }
            )
    ) {
        // 顶部边框线
        if (showTopBorder && !isCapsule) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(colors.border)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(tabHeight)
                .then(
                    if (isCapsule) {
                        Modifier
                            .clip(RoundedCornerShape(28.dp))
                            .background(bgColor)
                    } else {
                        Modifier.background(bgColor)
                    }
                )
                .padding(
                    horizontal = if (isCapsule) Spacing.spacer8.dp else 0.dp,
                    vertical = if (isCapsule) Spacing.spacer8.dp else 0.dp
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedIndex

                // 分隔线
                if (useVerticalDivider && index > 0 && componentType == TabBarComponentType.NORMAL) {
                    Box(
                        modifier = Modifier
                            .width(0.5.dp)
                            .height(Spacing.spacer32.dp)
                            .background(colors.border)
                    )
                }

                TabBarItem(
                    tab = tab,
                    type = type,
                    componentType = componentType,
                    isSelected = isSelected,
                    selectedBgColor = selectedBg,
                    unselectedBgColor = unselectedBgColor,
                    tabCount = tabs.size,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        tab.onTap?.invoke()
                        onTabSelected?.invoke(index)
                    }
                )
            }
        }

        if (safeAreaBottom > 0.dp) {
            Spacer(modifier = Modifier.height(safeAreaBottom))
        }
    }
}

/**
 * 单个 Tab 项
 */
@Composable
private fun TabBarItem(
    tab: TabConfig,
    type: TabBarType,
    componentType: TabBarComponentType,
    isSelected: Boolean,
    selectedBgColor: Color,
    unselectedBgColor: Color?,
    tabCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val textColor = if (isSelected) colors.textPrimary else colors.textSecondary
    val iconColor = if (isSelected) colors.textPrimary else colors.textSecondary

    // 计算胶囊背景的 padding
    val horizontalPadding = if (tabCount > 3) Spacing.spacer8.dp else Spacing.spacer12.dp

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // 胶囊背景
        if (componentType == TabBarComponentType.LABEL) {
            val showBg = isSelected || unselectedBgColor != null
            if (showBg) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
                        .then(
                            if (type == TabBarType.TEXT) {
                                Modifier.height(Spacing.spacer32.dp)
                            } else {
                                Modifier.fillMaxHeight().padding(vertical = Spacing.spacer4.dp)
                            }
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            if (isSelected) selectedBgColor
                            else unselectedBgColor ?: Color.Transparent
                        )
                )
            }
        }

        // 内容
        when (type) {
            TabBarType.TEXT -> {
                TextTabContent(
                    text = tab.text,
                    isSelected = isSelected,
                    textColor = textColor,
                    badge = tab.badge,
                    showRedPoint = tab.showRedPoint
                )
            }

            TabBarType.ICON -> {
                IconTabContent(
                    icon = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                    iconColor = iconColor,
                    badge = tab.badge,
                    showRedPoint = tab.showRedPoint
                )
            }

            TabBarType.ICON_TEXT -> {
                IconTextTabContent(
                    icon = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                    text = tab.text,
                    isSelected = isSelected,
                    iconColor = iconColor,
                    textColor = textColor,
                    badge = tab.badge,
                    showRedPoint = tab.showRedPoint
                )
            }
        }
    }
}

/**
 * 纯文本 Tab 内容
 */
@Composable
private fun TextTabContent(
    text: String,
    isSelected: Boolean,
    textColor: Color,
    badge: String?,
    showRedPoint: Boolean
) {
    val colors = Theme.colors

    Box {
        Text(
            text = text,
            style = if (isSelected) Typography.TitleSmall else Typography.BodyMedium,
            color = textColor
        )

        // 徽标
        BadgeIndicator(
            badge = badge,
            showRedPoint = showRedPoint,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = Spacing.spacer12.dp, y = (-6).dp)
        )
    }
}

/**
 * 纯图标 Tab 内容
 */
@Composable
private fun IconTabContent(
    icon: String,
    iconColor: Color,
    badge: String?,
    showRedPoint: Boolean
) {
    Box {
        if (icon in Icons.all) {
            Icon(
                name = icon,
                size = 22.dp,
                tint = iconColor
            )
        } else {
            Text(
                text = icon,
                style = Typography.TitleLarge,
                color = iconColor
            )
        }

        BadgeIndicator(
            badge = badge,
            showRedPoint = showRedPoint,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = Spacing.spacer8.dp, y = (-Spacing.spacer4).dp)
        )
    }
}

/**
 * 图标+文本 Tab 内容
 */
@Composable
private fun IconTextTabContent(
    icon: String,
    text: String,
    isSelected: Boolean,
    iconColor: Color,
    textColor: Color,
    badge: String?,
    showRedPoint: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            if (icon in Icons.all) {
                Icon(
                    name = icon,
                    size = 20.dp,
                    tint = iconColor
                )
            } else {
                Text(
                    text = icon,
                    style = Typography.TitleMedium,
                    color = iconColor
                )
            }

            BadgeIndicator(
                badge = badge,
                showRedPoint = showRedPoint,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = Spacing.spacer8.dp, y = (-Spacing.spacer4).dp)
            )
        }

        if (text.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = text,
                style = Typography.BodyExtraSmall,
                color = textColor
            )
        }
    }
}

/**
 * 徽标指示器
 */
@Composable
private fun BadgeIndicator(
    badge: String?,
    showRedPoint: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors

    when {
        badge != null -> {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(Spacing.spacer8.dp))
                    .background(colors.danger)
                    .padding(horizontal = Spacing.spacer4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = badge,
                    style = Typography.BodyExtraSmall,
                    color = colors.onPrimary
                )
            }
        }

        showRedPoint -> {
            Box(
                modifier = modifier
                    .size(Spacing.spacer8.dp)
                    .clip(RoundedCornerShape(Spacing.spacer4.dp))
                    .background(colors.danger)
            )
        }
    }
}
