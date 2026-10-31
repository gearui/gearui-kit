package com.gearui.components.actionsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.primitives.Badge
import com.gearui.primitives.BadgeType
import com.gearui.foundation.sheet.ActionSheetDefaults
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.motion.rowPressFeedback
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.overlay.LocalOverlayVisible
import com.gearui.overlay.OverlayDefaults
import com.gearui.overlay.LocalOverlayViewportSize
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.gearui.primitives.Divider
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.runtime.SafeAreaEdge
import com.gearui.runtime.rememberSafeAreaInset
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.core.Animatable
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.heightIn
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/**
 * The list action sheet as iOS draws one (UIAlertController, `.actionSheet`): the actions
 * in a rounded card inset from the screen edges, rows of equal height divided by
 * hairlines, labels in the accent colour, a destructive one in red; the optional
 * description as a small centred heading inside the card; Cancel on a card of its own,
 * bold. The cards are plain card colour: the sheet material's inset hairline, drawn as a
 * border as wide as its blur, ringed these small cards in grey. It rises from the bottom; a tap outside or Back closes it. There is no grabber
 * and no drag: an action sheet is a question, not a sheet.
 */
@Composable
internal fun ActionSheetCards(
    items: List<ActionSheetItem>,
    align: ActionSheetAlign,
    description: String?,
    showCancel: Boolean,
    cancelText: String,
    maxListHeight: Dp,
    onSelected: ((ActionSheetItem, Int) -> Unit)?,
    onDismiss: () -> Unit,
) {
    val colors = Theme.colors
    val bottomInset = rememberSafeAreaInset(
        edge = SafeAreaEdge.Bottom,
        consume = LocalRuntimeFlags.current.actionSheetConsumesBottomSafeArea,
        minimum = Spacing.sm,
    )
    // Rises by its own height, as a sheet does; the host fades the scrim.
    val presented = LocalOverlayVisible.current
    val offset = remember { Animatable(0f) }
    var heightPx by remember { mutableStateOf(0f) }
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(presented, heightPx) {
        if (heightPx <= 0f) return@LaunchedEffect
        if (presented) {
            if (!entered) {
                offset.snapTo(heightPx)
                entered = true
            }
            offset.animateTo(0f, tween(OverlayDefaults.transitionDurationMillis))
        } else {
            offset.animateTo(heightPx, tween(OverlayDefaults.transitionDurationMillis))
        }
    }
    val shape = Theme.shapes.lg
    // Rows with icons line their labels up behind a shared icon column.
    val withIcons = items.any { it.icon != null }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            Modifier
                .fillMaxWidth()
                .onSizeChanged { heightPx = it.height.toFloat() }
                .offset { IntOffset(0, offset.value.roundToInt()) }
                .padding(start = Spacing.sm, end = Spacing.sm, bottom = bottomInset)
                // Taps on the cards are theirs, not the scrim's.
                .pointerInput(Unit) { detectTapGestures { } },
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Column(Modifier.fillMaxWidth().clip(shape).background(colors.card)) {
                if (description != null) {
                    Text(
                        description,
                        style = Theme.typography.bodyExtraSmall.copy(fontWeight = FontWeight.Medium),
                        color = colors.mutedForeground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    )
                    Divider()
                }
                val rows: @Composable (Int, ActionSheetItem) -> Unit = { index, item ->
                    if (index > 0) Divider()
                    ActionSheetCardRow(item, align, withIcons) {
                        if (!item.disabled) onSelected?.invoke(item, index)
                    }
                }
                // Scroll only when the actions would not fit on most of the screen: iOS shows
                // a long list whole while it fits.
                val viewport = LocalOverlayViewportSize.current
                val limit = maxOf(maxListHeight, with(LocalDensity.current) { (viewport.height * SCROLL_FRACTION).toDp() })
                if (actionSheetCardsHeight(items) > limit) {
                    LazyColumn(Modifier.fillMaxWidth().height(limit)) {
                        itemsIndexed(items) { index, item -> rows(index, item) }
                    }
                } else {
                    items.forEachIndexed { index, item -> rows(index, item) }
                }
            }
            if (showCancel) {
                Box(Modifier.fillMaxWidth().clip(shape).background(colors.card)) {
                    CardButton(onDismiss) {
                        Text(
                            cancelText,
                            style = Theme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.primary,
                        )
                    }
                }
            }
        }
    }
}

/** One action: the label in the accent colour (red when destructive), the row's full width tappable. */
@Composable
private fun ActionSheetCardRow(item: ActionSheetItem, align: ActionSheetAlign, withIcons: Boolean, onClick: () -> Unit) {
    val colors = Theme.colors
    val danger = item.textColor == colors.destructive
    val color = when {
        item.disabled -> colors.mutedForeground
        danger -> colors.destructive
        item.textColor != null -> item.textColor
        else -> colors.primary
    }
    val center = align == ActionSheetAlign.CENTER
    CardButton(onClick, enabled = !item.disabled, danger = danger, center = center) {
        if (item.icon != null) {
            Icon(item.icon, size = IconSizes.Default.xl, tint = color)
        } else if (withIcons && !center) {
            Spacer(Modifier.width(IconSizes.Default.xl))
        }
        Column(horizontalAlignment = if (center) Alignment.CenterHorizontally else Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.label, style = Theme.typography.bodyLarge, color = color)
                if (item.badge != null) {
                    Spacer(Modifier.width(Spacing.sm))
                    Badge(type = BadgeType.Message, message = item.badge)
                } else if (item.showRedPoint) {
                    Spacer(Modifier.width(Spacing.xs))
                    Box(Modifier.size(ActionSheetDefaults.Default.redDot).clip(CircleShape).background(colors.destructive))
                }
            }
            if (item.description != null) {
                Text(item.description, style = Theme.typography.bodySmall, color = colors.mutedForeground)
            }
        }
    }
}

/** A full-width row of the card, at least the iOS action height, pressed with the kit's row feedback. */
@Composable
private fun CardButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    danger: Boolean = false,
    center: Boolean = true,
    content: @Composable () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = ControlGeometry.actionSheetCardRow)
            .rowPressFeedback(interaction, RectangleShape, enabled = enabled, danger = danger, scale = false)
            .clickable(enabled = enabled, interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            // Disabled reads in the muted colour, as iOS greys a disabled action; no
            // alpha layer, which Kuikly draws as a separate view inside the padding.
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        horizontalArrangement = if (center) Arrangement.spacedBy(Spacing.md, Alignment.CenterHorizontally)
            else Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) { content() }
}

/** The share of the screen the actions may take before their card scrolls. */
private const val SCROLL_FRACTION = 0.65f

/** The actions' natural height, to decide when the card scrolls instead of growing. */
internal fun actionSheetCardsHeight(items: List<ActionSheetItem>): Dp =
    ControlGeometry.actionSheetCardRow * items.size
