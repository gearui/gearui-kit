package com.gearui.components.bottomnavbar

import androidx.compose.runtime.Composable
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.primitives.Badge
import com.gearui.primitives.BadgeType
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
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes
import com.gearui.runtime.rememberSafeAreaInset
import com.gearui.runtime.SafeAreaEdge

data class BottomNavItem(
    val id: String,
    val label: String,
    val icon: String,
    val selectedIcon: String? = null,
    /**
     * Interior fill layer for the two-tone selected treatment: when set, the selected
     * tab stacks this asset tinted with the active color UNDER the outline icon tinted
     * with the regular foreground — dark outline, brand-colored inside (and the label
     * goes foreground too). When null, selection falls back to a plain active-color tint.
     */
    val selectedFillIcon: String? = null,
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
    val safeAreaBottom = rememberSafeAreaInset(
        edge = SafeAreaEdge.Bottom,
        consume = runtimeFlags.bottomNavBarConsumesBottomSafeArea,
        extra = safeAreaExtraBottom,
    )
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
                    val twoTone = isSelected && item.selectedFillIcon != null
                    val contentColor = when {
                        item.disabled -> colors.mutedForeground
                        twoTone -> colors.foreground
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
                                    // The selected item fires the callback too: tapping the current tab again is a meaningful
                                    // interaction (scroll the list back to top, jump to the next unread; both WeChat and Telegram do it).
                                    // The control does not swallow the event on the caller's behalf; ignoring a repeat selection is the caller's call.
                                    Modifier.clickable { onSelect(item.id) }
                                } else {
                                    Modifier
                                }
                            )
                            .padding(bottom = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Selected-tab indicator: a soft rounded rectangle in the active color
                        // behind icon + label (M3-style, small radius). clip/background stay in
                        // the modifier chain permanently (transparent when unselected) because
                        // conditionally removing them leaves a stale background view behind on
                        // Kuikly when selection moves to another tab.
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) selectedColor else Color.Transparent)
                                .padding(start = 6.dp, end = 6.dp, bottom = 3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                        // Combined icon + badge layout (a custom Layout):
                        // - the layout size is **constant** (it does not change as the badge appears or disappears), identical for every tab
                        // - the icon is **centred** in the layout (half a badge width is reserved on each side, and the badge fills the right half)
                        // - the badge centre sits at the icon's top-right corner, entirely inside the bounds (working around Kuikly clipping)
                        //
                        // The maths: layout = (iconW + badgeMaxW, iconH + badgeMaxH/2)
                        //   the "99+" case: (24 + 30, 24 + 8) = (54, 32)
                        //   icon centre = (27, 20) = the geometric centre of the layout -> visually centred within the tab
                        //   badge centre = (27 + 12, 20 - 12) = (39, 8) = the icon's top-right corner -> floats visually
                        val reservedBadgeW = 30.dp  // "99+" 在 BadgeSize.Small 下的最大宽度
                        val reservedBadgeH = 16.dp  //  BadgeSize.Small 高度
                        com.tencent.kuikly.compose.ui.layout.Layout(
                            content = {
                                Box {
                                    if (twoTone) {
                                        Icon(
                                            name = item.selectedFillIcon!!,
                                            size = IconSizes.Default.xl,
                                            tint = selectedColor
                                        )
                                    }
                                    Icon(
                                        name = iconName,
                                        size = IconSizes.Default.xl,
                                        tint = contentColor
                                    )
                                }
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
                                // Icon centred horizontally: reservedW/2 on the left and reservedW/2 on the right
                                // Icon has reservedH/2 above and sits flush with the bottom
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
            }

            if (safeAreaBottom > 0.dp) {
                Spacer(modifier = Modifier.height(safeAreaBottom))
            }
        }
    }
}
