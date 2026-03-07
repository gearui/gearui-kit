package com.gearui.components.bottomnavbar

import androidx.compose.runtime.Composable
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
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
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.widthIn
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

data class BottomNavItem(
    val id: String,
    val label: String,
    val icon: String,
    val selectedIcon: String? = null,
    val badgeCount: Int = 0,
    val showBadgeDot: Boolean = false,
    val disabled: Boolean = false
)

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    useSafeArea: Boolean = true,
    showTopDivider: Boolean = true,
    height: Dp = 56.dp,
    backgroundColor: Color? = null,
    activeColor: Color? = null,
    inactiveColor: Color? = null
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val configuration = LocalConfiguration.current
    val safeAreaBottom = if (useSafeArea) configuration.safeAreaInsets.bottom.dp else 0.dp
    val selected = selectedId ?: items.firstOrNull()?.id.orEmpty()
    val barBackground = backgroundColor ?: colors.surface
    val selectedColor = activeColor ?: colors.primary
    val unselectedColor = inactiveColor ?: colors.textSecondary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(barBackground)
    ) {
        if (showTopDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.divider)
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
                    item.disabled -> colors.textDisabled
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
                                Modifier.clickable {
                                    if (!isSelected) {
                                        onSelect(item.id)
                                    }
                                }
                            } else {
                                Modifier
                            }
                        )
                        .padding(top = 6.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            name = iconName,
                            size = 22.dp,
                            tint = contentColor
                        )

                        if (item.badgeCount > 0 || item.showBadgeDot) {
                            val badgeText = if (item.badgeCount > 99) "99+" else item.badgeCount.toString()
                            if (item.badgeCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 8.dp, y = (-8).dp)
                                        .clip(shapes.round)
                                        .background(colors.danger)
                                        .height(16.dp)
                                        .widthIn(min = 16.dp)
                                        .padding(horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = badgeText,
                                        style = Typography.Label,
                                        color = colors.textAnti
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp)
                                        .size(8.dp)
                                        .clip(shapes.circle)
                                        .background(colors.danger)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

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
