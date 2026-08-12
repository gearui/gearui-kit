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
import com.gearui.foundation.layout.Spacing

/**
 * Sidebar style
 */
enum class SidebarStyle {
    NORMAL,  // 通栏样式
    OUTLINE  // 非通栏样式
}

/**
 * Sidebar item data
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
 * Shared layout for the sidebar sub-pages
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
        // Top navigation bar
        NavBar(
            title = title,
            centerTitle = true,
            useDefaultBack = true,
            onBackClick = onBack,
            backgroundColor = colors.surface
        )

        // Optional top action area
        topContent?.invoke()

        // Main content area
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
 * Sidebar component
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
            .background(unSelectedBgColor ?: colors.muted)
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
 * Sidebar item
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
        else -> unSelectedBgColor ?: colors.muted
    }

    val textColor = when {
        selected -> selectedTextColor ?: colors.primary
        item.textColor != null -> item.textColor
        else -> unSelectedTextColor ?: colors.foreground
    }

    val itemModifier = when (style) {
        SidebarStyle.NORMAL -> Modifier
            .fillMaxWidth()
            .background(backgroundColor)
        SidebarStyle.OUTLINE -> Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
            .clip(RoundedCornerShape(Spacing.sm))
            .background(backgroundColor)
    }

    Box(
        modifier = itemModifier
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.lg, horizontal = Spacing.md)
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

            // Badge
            if (item.showDot) {
                Spacer(modifier = Modifier.width(Spacing.xs))
                Badge(type = BadgeType.RedPoint)
            } else if (item.badgeCount != null) {
                Spacer(modifier = Modifier.width(Spacing.xs))
                Badge(type = BadgeType.Message, count = item.badgeCount.toIntOrNull() ?: 0)
            }
        }

        // Selection indicator (NORMAL style only)
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
 * Sidebar item with an icon
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
        else -> unSelectedBgColor ?: colors.muted
    }

    val textColor = when {
        selected -> selectedTextColor ?: colors.primary
        item.textColor != null -> item.textColor
        else -> unSelectedTextColor ?: colors.foreground
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.md, horizontal = Spacing.sm)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            // Icon row (with a badge)
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

        // Selection indicator
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
 * Content block - anchor content
 */
@Composable
fun ContentSection(index: Int) {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
    ) {
        // Title
        Text(
            text = "标题$index",
            style = Typography.TitleSmall,
            color = colors.foreground,
            modifier = Modifier.padding(
                start = Spacing.lg,
                top = Spacing.lg,
                bottom = Spacing.sm
            )
        )

        // List items
        repeat(3) {
            ContentListItem()
            DividerFull()
        }
    }
}

/**
 * Content list item
 */
@Composable
fun ContentListItem() {
    val colors = Theme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Spacing.sm))
                .background(colors.muted),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📷",
                style = Typography.TitleMedium
            )
        }

        // Title
        Text(
            text = "标题",
            style = Typography.BodyLarge,
            color = colors.foreground
        )
    }
}

/**
 * Page content - grid layout
 */
@Composable
fun PageGridContent(index: Int) {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surface)
            .padding(Spacing.lg)
    ) {
        Text(
            text = "标题 $index",
            style = Typography.TitleSmall,
            color = colors.foreground,
            modifier = Modifier.padding(bottom = Spacing.lg)
        )

        // Grid content - 3 columns, 4 rows
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            repeat(4) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
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
 * Grid item
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
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Spacing.sm))
                .background(colors.muted),
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
            color = colors.foreground,
            maxLines = 1
        )
    }
}
