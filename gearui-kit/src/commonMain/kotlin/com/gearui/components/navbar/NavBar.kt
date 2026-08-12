package com.gearui.components.navbar

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.theme.Theme
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes
import com.gearui.runtime.rememberSafeAreaInset
import com.gearui.runtime.SafeAreaEdge

/**
 * NavBar - navigation bar
 *
 * Used to move between pages. Sits above the content area and below the system status bar.
 *
 * Features:
 * - centred or left-aligned title
 * - optional back button on the left
 * - custom action buttons on either side
 * - custom title component
 * - subtitle
 * - custom background colour
 *
 * - centerTitle=true: a Box overlay layout, so the title is absolutely centred
 * - centerTitle=false: a Row layout, with the title after the left buttons
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
    rightWidget: (@Composable () -> Unit)? = null,
    /**
     * Slot width for [rightWidget]. `null` (the default) uses [actionSlotWidth] (56dp, matching an icon-only button).
     * If rightWidget holds a text button such as "Done" or "Create (N)", pass a larger value explicitly
     * (80-120dp suggested); the padding on both sides of a centred title follows this value so the two do not overlap.
     */
    rightWidgetWidth: Dp? = null,
    showBottomDivider: Boolean = true
) {
    val colors = Theme.colors
    val bgColor = backgroundColor ?: colors.surface
    val textColor = titleColor ?: colors.foreground

    // Safe area insets
    val runtimeFlags = LocalRuntimeFlags.current
    val safeAreaTop = rememberSafeAreaInset(
        edge = SafeAreaEdge.Top,
        consume = runtimeFlags.navBarConsumesTopSafeArea,
    )
    val actionSlotWidth = 56.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
    ) {
        // Top safe area filler
        if (safeAreaTop > 0.dp) {
            Spacer(modifier = Modifier.height(safeAreaTop))
        }
        if (centerTitle) {
            // Centred mode: a Box overlay layout keeps the title absolutely centred
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            ) {
                // Upper layer: left action area
                val leftCount = (if (useDefaultBack) 1 else 0) + leftItems.size
                val rightCount = rightItems.size
                // Padding beside the title: the larger of the two action areas, so the centred title is never covered
                val leftSlotWidth = (leftCount * actionSlotWidth.value).dp
                val rightSlotWidth = when {
                    rightWidget != null -> rightWidgetWidth ?: actionSlotWidth
                    else -> (rightCount * actionSlotWidth.value).dp
                }
                val titlePadding = maxOf(leftSlotWidth, rightSlotWidth)

                // Lower layer: title area (absolutely centred, clear of the side buttons)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = titlePadding),
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
                Row(
                    modifier = Modifier
                        .width((leftCount * actionSlotWidth.value).dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    if (useDefaultBack) {
                        NavBarIconButton(
                            icon = Icons.chevron_left,
                            iconColor = textColor,
                            onClick = onBackClick,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                    leftItems.forEach { item ->
                        NavBarIconButton(
                            icon = item.icon,
                            iconColor = item.iconColor ?: textColor,
                            onClick = item.onClick,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }

                // Upper layer: right action area
                if (rightWidget != null) {
                    Box(
                        modifier = Modifier
                            .width(rightWidgetWidth ?: actionSlotWidth)
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        rightWidget()
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .width((rightCount * actionSlotWidth.value).dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        rightItems.forEach { item ->
                            NavBarIconButton(
                                icon = item.icon,
                                iconColor = item.iconColor ?: textColor,
                                onClick = item.onClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }
        } else {
            // Left-aligned mode: a Row layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left button area
                if (useDefaultBack) {
                    NavBarIconButton(
                        icon = Icons.chevron_left,
                        iconColor = textColor,
                        onClick = onBackClick,
                        modifier = Modifier
                            .width(actionSlotWidth)
                            .fillMaxHeight()
                    )
                }
                leftItems.forEach { item ->
                    NavBarIconButton(
                        icon = item.icon,
                        iconColor = item.iconColor ?: textColor,
                        onClick = item.onClick,
                        modifier = Modifier
                            .width(actionSlotWidth)
                            .fillMaxHeight()
                    )
                }

                // Title area
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

                // Right button area
                if (rightWidget != null) {
                    Box(
                        modifier = Modifier
                            .width(rightWidgetWidth ?: actionSlotWidth)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        rightWidget()
                    }
                } else {
                    rightItems.forEach { item ->
                        NavBarIconButton(
                            icon = item.icon,
                            iconColor = item.iconColor ?: textColor,
                            onClick = item.onClick,
                            modifier = Modifier
                                .width(actionSlotWidth)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }

        // Subtitle area
        if (belowTitleWidget != null) {
            belowTitleWidget()
        }

        if (showBottomDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BorderWidth.thin)
                    .background(colors.border)
            )
        }
    }
}

/**
 * NavBar icon button
 */
@Composable
private fun NavBarIconButton(
    icon: String,
    iconColor: Color,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
        .fillMaxHeight()
        .widthIn(min = 56.dp)
) {
    Box(
        modifier = modifier
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
                size = IconSizes.Default.xl,
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
 * NavBar item data class
 */
data class NavBarItem(
    val icon: String,
    val iconColor: Color? = null,
    val onClick: (() -> Unit)? = null
)
