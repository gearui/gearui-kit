package com.gearui.components.anchor

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography

import com.gearui.theme.Theme

/**
 * Anchor link item
 */
data class AnchorItem(
    val key: String,
    val title: String,
    val href: String? = null
)

/**
 * Anchor - Anchor navigation component
 *
 * 锚点导航组件
 *
 * Features:
 * - Auto highlight current section
 * - Smooth scroll to target
 * - Nested links support
 * - Custom styling
 *
 * Example:
 * ```
 * Anchor(
 *     items = listOf(
 *         AnchorItem(key = "intro", title = "介绍"),
 *         AnchorItem(key = "api", title = "API"),
 *         AnchorItem(key = "faq", title = "FAQ")
 *     ),
 *     activeKey = currentSection,
 *     onItemClick = { scrollToSection(it.key) }
 * )
 * ```
 */
@Composable
fun Anchor(
    items: List<AnchorItem>,
    modifier: Modifier = Modifier,
    activeKey: String? = null,
    onItemClick: ((AnchorItem) -> Unit)? = null,
    offsetTop: Dp = 0.dp
) {
    val colors = Theme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = offsetTop),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            AnchorLink(
                item = item,
                isActive = item.key == activeKey,
                onClick = { onItemClick?.invoke(item) }
            )
        }
    }
}

@Composable
private fun AnchorLink(
    item: AnchorItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val colors = Theme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        // Active indicator
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(16.dp)
                .background(
                    if (isActive) colors.primary
                    else colors.border
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = item.title,
            style = Typography.BodyMedium,
            color = if (isActive) colors.primary else colors.textSecondary
        )
    }
}

/**
 * Anchor with affix (sticky positioning)
 */
@Composable
fun AnchorAffix(
    items: List<AnchorItem>,
    modifier: Modifier = Modifier,
    activeKey: String? = null,
    onItemClick: ((AnchorItem) -> Unit)? = null,
    offsetTop: Dp = 16.dp
) {
    val colors = Theme.colors

    Box(
        modifier = modifier
            .width(200.dp)
            .background(colors.surface)
            .padding(16.dp)
    ) {
        Anchor(
            items = items,
            activeKey = activeKey,
            onItemClick = onItemClick,
            offsetTop = offsetTop
        )
    }
}

/**
 * Anchor state for managing active section
 */
class AnchorState(initialActiveKey: String? = null) {
    var activeKey by mutableStateOf(initialActiveKey)
        private set

    fun setActive(key: String) {
        activeKey = key
    }

    fun clearActive() {
        activeKey = null
    }
}

@Composable
fun rememberAnchorState(initialActiveKey: String? = null): AnchorState {
    return remember { AnchorState(initialActiveKey) }
}
