package com.gearui.components.dialog

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.OverlayDefaults
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.elevation.Elevation
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.primitives.ScrollView

/**
 * Dialog - base for modal dialogs
 *
 * The foundation of every modal dialog:
 * - Dialog
 * - ConfirmDialog
 * - AlertDialog
 * - InputDialog
 * - ImageDialog
 *
 * Characteristics:
 * - modal (blocks interaction)
 * - centred
 * - has a scrim
 * - optionally dismissed by tapping outside
 */
object Dialog {

    /**
     * Declarative Dialog
     *
     * @param visible whether it is shown
     * @param dismissOnOutside whether tapping outside dismisses it
     * @param maskColor scrim colour
     * @param onDismiss dismiss callback
     * @param content the content
     */
    @Composable
    fun Host(
        visible: Boolean,
        dismissOnOutside: Boolean = false,
        maskColor: Color? = null,
        onDismiss: () -> Unit = {},
        content: @Composable () -> Unit
    ) {
        val colors = Theme.colors
        val controller = LocalOverlayController.current
        val effectiveMaskColor = maskColor ?: OverlayDefaults.scrimColor
        var overlayId by remember { mutableStateOf<Long?>(null) }

        LaunchedEffect(visible) {
            if (visible) {
                overlayId = controller.show(
                    anchorBounds = null,
                    options = OverlayOptions(
                        placement = OverlayPlacement.Center,
                        modal = true,
                        maskColor = effectiveMaskColor,
                        dismissPolicy = OverlayDismissPolicy.Modal.copy(
                            outsideClick = dismissOnOutside
                        )
                    ),
                    onDismiss = onDismiss
                ) {
                    DialogSurface(content = content)
                }
            } else {
                overlayId?.let { controller.dismiss(it) }
                overlayId = null
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                overlayId?.let { controller.dismiss(it) }
            }
        }
    }
}

/**
 * DialogSurface - shared visual container for Dialog
 */
@Composable
internal fun DialogSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes

    Box(
        modifier = modifier
            .widthIn(min = 280.dp, max = 360.dp)
            // 🔴 Height must HUG the content, and be capped.
            //
            // Constraining width but not height lets any "take all the height you
            // offer" child (a multiline Input is exactly that: it deliberately hands
            // height to the caller) stretch the dialog to the full screen — the
            // title runs into the status bar, the buttons are pushed off screen,
            // and the user cannot even tap Confirm. That is not the child's fault;
            // this layer was missing one constraint: a modal card must not grow
            // without bound because one child wants to.
            .wrapContentHeight()
            .heightIn(max = 560.dp)
            .shadow(Elevation.modal, OverlayDefaults.modalShape)
            .background(colors.surface, OverlayDefaults.modalShape)
            .border(BorderWidth.thin, colors.border, OverlayDefaults.modalShape)
    ) {
        content()
    }
}

/**
 * DialogContent - Dialog content layout helper
 */
@Composable
fun DialogContent(
    title: String? = null,
    message: String? = null,
    content: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit
) {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.xl)
    ) {
        // Title
        if (title != null) {
            com.gearui.foundation.primitives.Text(
                text = title,
                style = com.gearui.foundation.typography.Typography.TitleMedium,
                color = colors.foreground
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
        }

        // Message
        if (message != null) {
            com.gearui.foundation.primitives.Text(
                text = message,
                style = com.gearui.foundation.typography.Typography.BodyMedium,
                color = colors.mutedForeground
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
        }

        // Custom content
        //
        // 🔴 Content SCROLLS; title and actions stay FIXED.
        //
        // Capping only the outer box (heightIn max) is not enough: when content
        // exceeds the cap, under large system fonts, or on small screens, the
        // overflow is clipped together with Confirm/Cancel — the user sees the
        // dialog and cannot reach its buttons, short of killing the app. Put the
        // variable part in a scroll area and the action row is always there.
        //
        // Kuikly has no Modifier.verticalScroll, so this uses the repository's own
        // ScrollView (a LazyColumn with one item wrapping a Column — the same
        // workaround as the existing nav-return case).
        if (content != null) {
            ScrollView(
                modifier = Modifier
                    .fillMaxWidth()
                    // The cap leaves room for title and actions: 360 + title/buttons/padding stays within the outer 560.
                    .heightIn(max = 360.dp)
            ) {
                content()
            }
            Spacer(modifier = Modifier.height(Spacing.lg))
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
}
