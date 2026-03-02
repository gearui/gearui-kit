package com.gearui.sample.examples.sidebar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.navbar.NavBar
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.primitives.Badge
import com.gearui.primitives.BadgeType
import com.gearui.primitives.DividerFull
import com.gearui.theme.Theme
import com.gearui.Spacing

/**
 * 侧边栏样式
 */
enum class SidebarStyle {
    NORMAL,  // 通栏样式
    OUTLINE  // 非通栏样式
}

/**
 * 侧边栏项数据
 */
data class SidebarItemData(
    val index: Int,
    val label: String,
    val value: Int,
    val icon: String? = null,
    val showDot: Boolean = false,
    val badgeCount: String? = null,
    val textColor: Color? = null
)

/**
 * 侧边栏子页面通用布局
 */
@Composable
fun SidebarSubPageLayout(
    title: String,
    onBack: () -> Unit,
    topContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // 顶部导航栏
        NavBar(
            title = title,
            centerTitle = true,
            useDefaultBack = true,
            onBackClick = onBack,
            backgroundColor = colors.surface
        )

        // 顶部操作区（可选）
        topContent?.invoke()

        // 主内容区
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.surface)
        ) {
            content()
        }
    }
}

/**
 * 侧边栏组件
 */
@Composable
fun Sidebar(
    items: List<SidebarItemData>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    style: SidebarStyle = SidebarStyle.NORMAL,
    showIcon: Boolean = false,
    selectedBgColor: Color? = null,
    unSelectedBgColor: Color? = null,
    selectedTextColor: Color? = null,
    unSelectedTextColor: Color? = null
) {
    val colors = Theme.colors

    LazyColumn(
        modifier = modifier
            .width(110.dp)
            .fillMaxHeight()
            .background(unSelectedBgColor ?: colors.surfaceVariant)
    ) {
        itemsIndexed(items) { index, item ->
            if (showIcon && item.icon != null) {
                SidebarItemWithIcon(
                    item = item,
                    selected = index == selectedIndex,
                    onClick = { onItemSelected(index) },
                    selectedBgColor = selectedBgColor,
                    unSelectedBgColor = unSelectedBgColor,
                    selectedTextColor = selectedTextColor,
                    unSelectedTextColor = unSelectedTextColor
                )
            } else {
                SidebarItem(
                    item = item,
                    selected = index == selectedIndex,
                    onClick = { onItemSelected(index) },
                    style = style,
                    selectedBgColor = selectedBgColor,
                    unSelectedBgColor = unSelectedBgColor,
                    selectedTextColor = selectedTextColor,
                    unSelectedTextColor = unSelectedTextColor
                )
            }
        }
    }
}

/**
 * 侧边栏项
 */
@Composable
private fun SidebarItem(
    item: SidebarItemData,
    selected: Boolean,
    onClick: () -> Unit,
    style: SidebarStyle,
    selectedBgColor: Color? = null,
    unSelectedBgColor: Color? = null,
    selectedTextColor: Color? = null,
    unSelectedTextColor: Color? = null
) {
    val colors = Theme.colors

    val backgroundColor = when {
        selected -> selectedBgColor ?: colors.surface
        else -> unSelectedBgColor ?: colors.surfaceVariant
    }

    val textColor = when {
        selected -> selectedTextColor ?: colors.primary
        item.textColor != null -> item.textColor
        else -> unSelectedTextColor ?: colors.textPrimary
    }

    val itemModifier = when (style) {
        SidebarStyle.NORMAL -> Modifier
            .fillMaxWidth()
            .background(backgroundColor)
        SidebarStyle.OUTLINE -> Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.spacer8.dp, vertical = Spacing.spacer4.dp)
            .clip(RoundedCornerShape(Spacing.spacer8.dp))
            .background(backgroundColor)
    }

    Box(
        modifier = itemModifier
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.spacer16.dp, horizontal = Spacing.spacer12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.label,
                style = Typography.BodyMedium,
                color = textColor
            )

            // 徽标
            if (item.showDot) {
                Spacer(modifier = Modifier.width(Spacing.spacer4.dp))
                Badge(type = BadgeType.RedPoint)
            } else if (item.badgeCount != null) {
                Spacer(modifier = Modifier.width(Spacing.spacer4.dp))
                Badge(type = BadgeType.Message, count = item.badgeCount.toIntOrNull() ?: 0)
            }
        }

        // 选中指示器（仅 NORMAL 样式）
        if (selected && style == SidebarStyle.NORMAL) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .height(16.dp)
                    .background(colors.primary)
            )
        }
    }
}

/**
 * 带图标的侧边栏项
 */
@Composable
private fun SidebarItemWithIcon(
    item: SidebarItemData,
    selected: Boolean,
    onClick: () -> Unit,
    selectedBgColor: Color? = null,
    unSelectedBgColor: Color? = null,
    selectedTextColor: Color? = null,
    unSelectedTextColor: Color? = null
) {
    val colors = Theme.colors

    val backgroundColor = when {
        selected -> selectedBgColor ?: colors.surface
        else -> unSelectedBgColor ?: colors.surfaceVariant
    }

    val textColor = when {
        selected -> selectedTextColor ?: colors.primary
        item.textColor != null -> item.textColor
        else -> unSelectedTextColor ?: colors.textPrimary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.spacer12.dp, horizontal = Spacing.spacer8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.spacer4.dp)
        ) {
            // 图标行（带徽标）
            Box {
                Text(
                    text = item.icon ?: "",
                    style = Typography.TitleLarge
                )
                if (item.showDot) {
                    Box(modifier = Modifier.align(Alignment.TopEnd)) {
                        Badge(type = BadgeType.RedPoint)
                    }
                } else if (item.badgeCount != null) {
                    Box(modifier = Modifier.align(Alignment.TopEnd)) {
                        Badge(type = BadgeType.Message, count = item.badgeCount.toIntOrNull() ?: 0)
                    }
                }
            }

            Text(
                text = item.label,
                style = Typography.BodySmall,
                color = textColor
            )
        }

        // 选中指示器
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .height(16.dp)
                    .background(colors.primary)
            )
        }
    }
}

/**
 * 内容区块 - 锚点内容
 */
@Composable
fun ContentSection(index: Int) {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
    ) {
        // 标题
        Text(
            text = "标题$index",
            style = Typography.TitleSmall,
            color = colors.textPrimary,
            modifier = Modifier.padding(
                start = Spacing.spacer16.dp,
                top = Spacing.spacer16.dp,
                bottom = Spacing.spacer8.dp
            )
        )

        // 列表项
        repeat(3) {
            ContentListItem()
            DividerFull()
        }
    }
}

/**
 * 内容列表项
 */
@Composable
fun ContentListItem() {
    val colors = Theme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.spacer16.dp, vertical = Spacing.spacer16.dp),
        horizontalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图片占位
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Spacing.spacer8.dp))
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📷",
                style = Typography.TitleMedium
            )
        }

        // 标题
        Text(
            text = "标题",
            style = Typography.BodyLarge,
            color = colors.textPrimary
        )
    }
}

/**
 * 切页内容 - 网格布局
 */
@Composable
fun PageGridContent(index: Int) {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surface)
            .padding(Spacing.spacer16.dp)
    ) {
        Text(
            text = "标题 $index",
            style = Typography.TitleSmall,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = Spacing.spacer16.dp)
        )

        // 网格内容 - 3列4行
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)
        ) {
            repeat(4) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)
                ) {
                    repeat(3) { col ->
                        val itemIndex = row * 3 + col
                        GridItem(
                            title = "${itemIndex}最多六个字",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 网格项
 */
@Composable
private fun GridItem(
    title: String,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Spacing.spacer8.dp))
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📷",
                style = Typography.TitleMedium
            )
        }

        Text(
            text = title,
            style = Typography.BodySmall,
            color = colors.textPrimary,
            maxLines = 1
        )
    }
}
