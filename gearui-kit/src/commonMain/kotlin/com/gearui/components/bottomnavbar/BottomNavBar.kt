package com.gearui.components.bottomnavbar

import androidx.compose.runtime.Composable
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.primitives.Badge
import com.gearui.primitives.BadgeType
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.AnimatedVisibility
import com.tencent.kuikly.compose.animation.fadeIn
import com.tencent.kuikly.compose.animation.fadeOut
import com.tencent.kuikly.compose.animation.slideInVertically
import com.tencent.kuikly.compose.animation.slideOutVertically
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxHeight
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.border.BorderWidth

data class BottomNavItem(
    val id: String,
    val label: String,
    val icon: String,
    val selectedIcon: String? = null,
    val badgeCount: Int = 0,
    val showBadgeDot: Boolean = false,
    val disabled: Boolean = false
)

/**
 * Bottom navigation bar with optional show/hide animation.
 *
 * @param visible When `true` (default), the bar renders normally. When `false`, the
 *   bar animates out via `slideOutVertically` + `fadeOut` and is removed from layout
 *   (returning `true` plays the reverse `slideInVertically` + `fadeIn` enter animation).
 *   This is the hook for app-driven "hide on scroll-down" patterns — the app detects
 *   scroll direction and toggles this flag, and the kit handles the animation primitive.
 *   The default `true` preserves prior call-site behavior (fully backward compatible).
 */
@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    safeAreaExtraBottom: Dp = 0.dp,
    showTopDivider: Boolean = true,
    height: Dp = 56.dp,
    backgroundColor: Color? = null,
    activeColor: Color? = null,
    inactiveColor: Color? = null,
    visible: Boolean = true
) {
    val colors = Theme.colors
    val runtimeFlags = LocalRuntimeFlags.current
    val runtimeEnvironment = LocalRuntimeEnvironment.current
    val configuration = LocalConfiguration.current
    val safeAreaBottom = if (runtimeFlags.unifiedSafeAreaPipeline) {
        if (runtimeFlags.bottomNavBarConsumesBottomSafeArea) {
            runtimeEnvironment.safeArea.bottom + safeAreaExtraBottom
        } else {
            safeAreaExtraBottom
        }
    } else {
        configuration.safeAreaInsets.bottom.dp + safeAreaExtraBottom
    }
    val selected = selectedId ?: items.firstOrNull()?.id.orEmpty()
    val barBackground = backgroundColor ?: colors.surface
    val selectedColor = activeColor ?: colors.primary
    val unselectedColor = inactiveColor ?: colors.mutedForeground

    // Animation wrapper. When `visible=false`, the bar slides down off-screen + fades
    // out, then is removed from layout entirely. The `modifier` is applied here so
    // callers can pass alignment / sizing modifiers (e.g. `Modifier.align(BottomCenter)`)
    // that govern WHERE the bar lives — the AnimatedVisibility container then handles
    // the show/hide transition for that whole subtree.
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(barBackground)
        ) {
            if (showTopDivider) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BorderWidth.thin)
                        .background(colors.border)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            ) {
                items.forEach { item ->
                    val isSelected = selected == item.id
                    val contentColor = when {
                        item.disabled -> colors.mutedForeground
                        isSelected -> selectedColor
                        else -> unselectedColor
                    }
                    val iconName = if (isSelected) item.selectedIcon ?: item.icon else item.icon

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .then(
                                if (!item.disabled) {
                                    // 选中项也要回调：再次点击当前 tab 是一个有意义的交互
                                    // （回到列表顶部 / 跳下一条未读，微信、Telegram 都这么做）。
                                    // 控件不替业务吞事件，是否忽略重复选中由调用方决定。
                                    Modifier.clickable { onSelect(item.id) }
                                } else {
                                    Modifier
                                }
                            )
                            .padding(top = 2.dp, bottom = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // icon + badge 联合布局（自定义 Layout）：
                        // - layout 尺寸**恒定**（不随 badge 出现/消失变化），所有 tab 一致
                        // - icon 视觉**居中**于 layout（左右各预留 badge 半宽空间，badge 占满右半）
                        // - badge 中心对齐 icon 右上角，完全落在 bounds 内（绕开 Kuikly 裁剪）
                        //
                        // 数学：layout = (iconW + badgeMaxW, iconH + badgeMaxH/2)
                        //   "99+" 场景：(24 + 30, 24 + 8) = (54, 32)
                        //   icon 中心点 = (27, 20) = layout 几何中心 → 视觉上 tab 内严格居中
                        //   badge 中心 = (27 + 12, 20 - 12) = (39, 8) = icon 右上角 → 视觉漂浮
                        val reservedBadgeW = 30.dp  // "99+" 在 BadgeSize.Small 下的最大宽度
                        val reservedBadgeH = 16.dp  //  BadgeSize.Small 高度
                        com.tencent.kuikly.compose.ui.layout.Layout(
                            content = {
                                Icon(
                                    name = iconName,
                                    size = 24.dp,
                                    tint = contentColor
                                )
                                when {
                                    item.badgeCount > 0 -> Badge(
                                        type = BadgeType.Message,
                                        count = item.badgeCount,
                                        maxCount = 99
                                    )
                                    item.showBadgeDot -> Badge(type = BadgeType.RedPoint)
                                }
                            }
                        ) { measurables, constraints ->
                            val iconP = measurables[0].measure(constraints)
                            val badgeP = measurables.getOrNull(1)
                                ?.measure(com.tencent.kuikly.compose.ui.unit.Constraints())
                            val reservedW = reservedBadgeW.roundToPx()
                            val reservedH = reservedBadgeH.roundToPx()
                            val totalW = iconP.width + reservedW           // = 54
                            val totalH = iconP.height + reservedH / 2      // = 32
                            layout(totalW, totalH) {
                                // icon 水平居中：左侧留 reservedW/2，右侧留 reservedW/2
                                // icon 垂直顶部留 reservedH/2，下方贴底
                                val iconX = reservedW / 2
                                val iconY = reservedH / 2
                                iconP.place(iconX, iconY)
                                badgeP?.place(
                                    iconX + iconP.width - badgeP.width / 2,
                                    iconY - badgeP.height / 2
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(BorderWidth.thick))

                        Text(
                            text = item.label,
                            style = Typography.BodySmall,
                            color = contentColor,
                            maxLines = 1
                        )
                    }
                }
            }

            if (safeAreaBottom > 0.dp) {
                Spacer(modifier = Modifier.height(safeAreaBottom))
            }
        }
    }
}
