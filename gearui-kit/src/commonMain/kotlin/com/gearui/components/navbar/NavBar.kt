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
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.runtime.LocalRuntimeFlags
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
    rightWidget: (@Composable () -> Unit)? = null,
    /**
     * [rightWidget] 槽位宽度。默认 `null` 走 [actionSlotWidth]（=56dp，跟纯图标按钮一致）。
     * 如果 rightWidget 里放的是"完成 / 创建(N)"等文字按钮，需要显式传一个更大的值
     * （建议 80-120dp）；title 居中时的左右 padding 会跟随这个值，避免与标题重叠。
     */
    rightWidgetWidth: Dp? = null,
    showBottomDivider: Boolean = true
) {
    val colors = Theme.colors
    val bgColor = backgroundColor ?: colors.surface
    val textColor = titleColor ?: colors.foreground

    // 获取安全区域
    val runtimeFlags = LocalRuntimeFlags.current
    val runtimeEnvironment = LocalRuntimeEnvironment.current
    val configuration = LocalConfiguration.current
    val safeAreaTop = if (runtimeFlags.unifiedSafeAreaPipeline) {
        if (runtimeFlags.navBarConsumesTopSafeArea) runtimeEnvironment.safeArea.top else 0.dp
    } else {
        configuration.safeAreaInsets.top.dp
    }
    val actionSlotWidth = 56.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
    ) {
        // 顶部安全区域填充
        if (safeAreaTop > 0.dp) {
            Spacer(modifier = Modifier.height(safeAreaTop))
        }
        if (centerTitle) {
            // 居中模式：使用 Box 层叠布局，确保标题绝对居中
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            ) {
                // 上层：左侧操作区域
                val leftCount = (if (useDefaultBack) 1 else 0) + leftItems.size
                val rightCount = rightItems.size
                // 标题两侧留白：取左右操作区域中较大的宽度，确保标题居中不被遮挡
                val leftSlotWidth = (leftCount * actionSlotWidth.value).dp
                val rightSlotWidth = when {
                    rightWidget != null -> rightWidgetWidth ?: actionSlotWidth
                    else -> (rightCount * actionSlotWidth.value).dp
                }
                val titlePadding = maxOf(leftSlotWidth, rightSlotWidth)

                // 底层：标题区域（绝对居中，避开左右按钮）
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

                // 上层：右侧操作区域
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
            // 左对齐模式：使用 Row 布局
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧按钮区域
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

        // 副标题区域
        if (belowTitleWidget != null) {
            belowTitleWidget()
        }

        if (showBottomDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.border)
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
