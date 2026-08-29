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
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.layout.Spacing
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
 * How the selected tab is emphasised. Brand-configurable: light brand colors
 * (e.g. yellow) can carry a filled indicator; saturated dark ones (e.g. red)
 * read better as a plain icon swap + tint.
 */
enum class BottomNavSelectionStyle {
    /** No indicator. Selected tab swaps to [BottomNavItem.selectedIcon] (or keeps
     *  the outline icon) tinted with the active color. The classic treatment. */
    TINT,

    /** Capsule indicator in the active color behind icon + label; content renders
     *  in the regular foreground, with [BottomNavItem.selectedFillIcon] layered
     *  under the outline for the two-tone icon. */
    CAPSULE,
}

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
    selectionStyle: BottomNavSelectionStyle = BottomNavSelectionStyle.TINT,
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
                    val capsule = selectionStyle == BottomNavSelectionStyle.CAPSULE
                    val twoTone = isSelected && capsule && item.selectedFillIcon != null
                    val contentColor = when {
                        item.disabled -> colors.mutedForeground
                        twoTone -> colors.foreground
                        isSelected -> selectedColor
                        else -> unselectedColor
                    }
                    // CAPSULE keeps the outline glyph on top (the fill layer sits under it);
                    // TINT swaps to the selectedIcon (typically the filled variant).
                    val iconName = if (isSelected && !capsule) item.selectedIcon ?: item.icon else item.icon

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
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Combined pill + badge layout (a custom Layout):
                        // - child 0 is the "pill": icon + label wrapped tightly by the selection
                        //   indicator (10dp rounded rect in the active color). The indicator must
                        //   NOT include the badge headroom above the icon — it hugs the content.
                        // - child 1 is the optional badge, floated at the icon's top-right corner
                        //   inside the reserved headroom (kept in-bounds: Kuikly clips overflow).
                        // - the layout size is constant per tab regardless of badge presence.
                        //
                        // The clip/background modifiers on the pill stay in the chain permanently
                        // (transparent when unselected): conditionally removing them leaves a
                        // stale background view behind on Kuikly when selection moves.
                        val reservedBadgeW = 30.dp  // widest case ("99+") at BadgeSize.Small
                        val reservedBadgeH = 16.dp  // BadgeSize.Small height
                        val pillPadTop = 2.dp
                        com.tencent.kuikly.compose.ui.layout.Layout(
                            content = {
                                Column(
                                    modifier = Modifier
                                        .clip(Theme.shapes.full)
                                        .background(if (isSelected && capsule) selectedColor else Color.Transparent)
                                        .padding(start = Spacing.sm, end = Spacing.sm, top = pillPadTop, bottom = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
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
                                    Spacer(modifier = Modifier.height(BorderWidth.thick))
                                    Text(
                                        text = item.label,
                                        style = Typography.BodySmall,
                                        color = contentColor,
                                        maxLines = 1
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
                            val reservedW = reservedBadgeW.roundToPx()
                            val reservedH = reservedBadgeH.roundToPx()
                            // The indicator follows the tab cell's width (minus the badge
                            // headroom margin on each side) instead of hugging the label,
                            // so every tab shows the same generous block.
                            val pillW = (constraints.maxWidth - reservedW).coerceAtLeast(0)
                            val pillP = measurables[0].measure(
                                constraints.copy(minWidth = pillW, maxWidth = pillW)
                            )
                            val badgeP = measurables.getOrNull(1)
                                ?.measure(com.tencent.kuikly.compose.ui.unit.Constraints())
                            val totalW = constraints.maxWidth
                            val totalH = pillP.height + reservedH / 2
                            layout(totalW, totalH) {
                                val pillX = reservedW / 2
                                val pillY = reservedH / 2
                                pillP.place(pillX, pillY)
                                // Badge centre = the ICON's top-right corner. The icon (24dp) is
                                // horizontally centred in the pill; its top sits pillPadTop below
                                // the pill's top edge.
                                val iconHalf = (IconSizes.Default.xl / 2).roundToPx()
                                badgeP?.place(
                                    pillX + pillP.width / 2 + iconHalf - badgeP.width / 2,
                                    pillY + pillPadTop.roundToPx() - badgeP.height / 2
                                )
                            }
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
