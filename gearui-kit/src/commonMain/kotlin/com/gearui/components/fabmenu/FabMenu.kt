package com.gearui.components.fabmenu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonShape
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.icon.*
import com.gearui.foundation.layout.Spacing
import com.gearui.i18n.I18n
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.LocalOverlayViewportSize
import com.gearui.overlay.OverlayDefaults
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.platform.LocalDensity

/** One action of a [FabMenu]. */
class FabMenuItem(
    val label: String,
    val icon: IconSource,
    val onClick: () -> Unit,
)

/**
 * A floating button that opens a short list of actions above itself — "new chat, new
 * group, scan". Opened, the page dims, the actions rise from the button, and the button
 * turns into a close button in the same place. Back, a tap on the dimmed page, the close
 * button or choosing an action closes it. While open, screen readers and the Web
 * keyboard stay in the menu.
 *
 * Place it where the floating button belongs — usually bottom-end of the page, above the
 * tab bar.
 */
@Composable
fun FabMenu(
    icon: IconSource,
    items: List<FabMenuItem>,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val controller = LocalOverlayController.current
    val actions = I18n.actions
    var anchor by remember { mutableStateOf<Rect?>(null) }
    var overlayId by remember { mutableStateOf<Long?>(null) }
    val currentItems by rememberUpdatedState(items)

    fun close() {
        overlayId?.let(controller::close)
        overlayId = null
    }

    DisposableEffect(Unit) { onDispose { close() } }

    Box(modifier.onGloballyPositioned { anchor = it.boundsInRoot() }) {
        Button(
            onClick = {
                val bounds = anchor ?: return@Button
                overlayId = controller.show(
                    anchorBounds = bounds,
                    options = OverlayOptions(
                        placement = OverlayPlacement.Fullscreen,
                        maskColor = OverlayDefaults.scrimColor,
                        dismissPolicy = OverlayDismissPolicy.Sheet,
                    ),
                    onDismiss = { overlayId = null },
                ) {
                    FabMenuPanel(bounds, currentItems, onClose = ::close)
                }
            },
            icon = icon,
            shape = ButtonShape.CIRCLE,
            size = ButtonSize.LARGE,
            theme = ButtonTheme.PRIMARY,
            contentDescription = contentDescription ?: actions.openMenu,
        )
    }
}

/** The open menu: the actions stacked above a close button drawn over the floating button. */
@Composable
private fun FabMenuPanel(anchor: Rect, items: List<FabMenuItem>, onClose: () -> Unit) {
    val viewport = LocalOverlayViewportSize.current
    val density = LocalDensity.current
    // Pin the column's bottom-end corner to the floating button's, so the close button
    // lands exactly where the floating button is.
    val end = with(density) { (viewport.width - anchor.right).coerceAtLeast(0f).toDp() }
    val bottom = with(density) { (viewport.height - anchor.bottom).coerceAtLeast(0f).toDp() }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Column(
            Modifier.padding(end = end, bottom = bottom),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            items.forEach { item ->
                Button(
                    text = item.label,
                    onClick = {
                        onClose()
                        item.onClick()
                    },
                    icon = item.icon,
                    size = ButtonSize.LARGE,
                    theme = ButtonTheme.DEFAULT,
                    type = ButtonType.FILL,
                )
            }
            Button(
                onClick = onClose,
                icon = Icons.x,
                shape = ButtonShape.CIRCLE,
                size = ButtonSize.LARGE,
                theme = ButtonTheme.PRIMARY,
                contentDescription = I18n.actions.closeMenu,
            )
        }
    }
}
