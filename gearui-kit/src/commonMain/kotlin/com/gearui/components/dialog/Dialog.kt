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
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.Dp
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.OverlayDefaults
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.primitives.ScrollView
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.material.MaterialSurface
import com.gearui.foundation.material.Materials
import com.gearui.overlay.LocalOverlayViewportSize
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonShape
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.gearui.foundation.typography.TextStyle

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
        val currentContent = rememberUpdatedState(content)
        val currentDismiss = rememberUpdatedState(onDismiss)

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
                    onDismiss = { currentDismiss.value() }
                ) {
                    DialogSurface(content = currentContent.value)
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
 *
 * HeroUI Native dialog.css: overlay colour, overlay shadow, radius 24, no border.
 * The portal keeps 20 from each screen edge and the reference dialogs cap the
 * card at `max-w-sm` (384).
 */
@Composable
internal fun DialogSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val density = LocalDensity.current
    val viewportWidth = with(density) { LocalOverlayViewportSize.current.width.toDp() }
    val available = viewportWidth - ControlGeometry.overlayPadding * 2
    val width = if (available.value > 0f && available < ControlGeometry.dialogMaxWidth) {
        available
    } else {
        ControlGeometry.dialogMaxWidth
    }
    MaterialSurface(
        material = Materials.Popover,
        shape = OverlayDefaults.modalShape,
        fallback = colors.popover,
        modifier = modifier
            .width(width)
            // 🔴 Height must HUG the content, and be capped.
            //
            // Constraining width but not height lets any "take all the height you
            // offer" child (a multiline Input is exactly that: it deliberately hands
            // height to the caller) stretch the dialog to the full screen, pushing
            // the actions off screen. A modal card must not grow without bound
            // because one child wants to.
            .wrapContentHeight()
            .heightIn(max = 560.dp),
    ) {
        content()
    }
}

/**
 * DialogContent — title, optional body, then the actions.
 *
 * Follows the HeroUI Native dialog: a start-aligned title (large, medium weight)
 * over a muted description, 20 of padding, and real Buttons below with 32 above
 * them. Callers still pass [DialogAction] roles; the dialog decides the drawing:
 *
 * - **Stacked, full width** when an action is destructive, when there are more
 *   than two, or when a label is long — the reference confirm pattern (danger
 *   button, then a neutral Cancel). CANCEL always goes last.
 * - **One row, end-aligned, small buttons** otherwise — the reference form
 *   footer, with Cancel first and the primary action on the trailing edge.
 *
 * "Long" is measured in characters because Kuikly cannot measure text here, so
 * the threshold is conservative: a wrong guess must never clip a label.
 *
 * @param title the question being asked
 * @param message optional supporting line
 * @param content optional custom body, scrollable, between message and actions
 * @param actions empty renders none
 */
@Composable
fun DialogContent(
    title: String? = null,
    message: String? = null,
    content: (@Composable () -> Unit)? = null,
    actions: List<DialogAction> = emptyList(),
) {
    val colors = Theme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ControlGeometry.overlayPadding),
    ) {
        if (title != null) {
            com.gearui.foundation.primitives.Text(
                text = title,
                style = DialogDefaults.titleStyle,
                color = colors.foreground,
                textAlign = TextAlign.Start,
            )
        }
        if (message != null) {
            if (title != null) Spacer(modifier = Modifier.height(ControlGeometry.dialogTextGap))
            com.gearui.foundation.primitives.Text(
                text = message,
                // A message-only dialog promotes it to the title's colour so it does
                // not read as a footnote under a heading that failed to load.
                style = Theme.typography.bodyMedium,
                color = if (title == null) colors.foreground else colors.mutedForeground,
                textAlign = TextAlign.Start,
            )
        }
        // Custom content SCROLLS; title and actions stay FIXED.
        //
        // Capping only the outer box (heightIn max) is not enough: when content
        // exceeds the cap, under large system fonts, or on small screens, the
        // overflow is clipped together with the actions and the user cannot reach
        // the buttons. Kuikly has no Modifier.verticalScroll, so this uses the
        // repository's ScrollView.
        if (content != null) {
            if (title != null || message != null) Spacer(modifier = Modifier.height(Spacing.lg))
            ScrollView(
                modifier = Modifier
                    .fillMaxWidth()
                    // Leaves room for title and actions within the outer 560 cap.
                    .heightIn(max = 360.dp)
            ) {
                content()
            }
        }
        if (actions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(ControlGeometry.dialogActionsTop))
            DialogActions(actions)
        }
    }
}

/** Longest label, in characters, that still fits a side-by-side pair. */
private const val SIDE_BY_SIDE_MAX_CHARS = 6

@Composable
private fun DialogActions(actions: List<DialogAction>) {
    val others = actions.filter { it.role != DialogActionRole.CANCEL }
    val cancels = actions.filter { it.role == DialogActionRole.CANCEL }
    val stacked = actions.size > 2 ||
        actions.any { it.role == DialogActionRole.DESTRUCTIVE } ||
        actions.any { it.text.length > SIDE_BY_SIDE_MAX_CHARS }
    if (stacked) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(ControlGeometry.dialogActionGap),
        ) {
            (others + cancels).forEach { action ->
                DialogActionButton(action, ButtonSize.MEDIUM, Modifier.fillMaxWidth(), block = true)
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ControlGeometry.dialogActionGap, Alignment.End),
        ) {
            (cancels + others).forEach { action ->
                DialogActionButton(action, ButtonSize.SMALL, Modifier, block = false)
            }
        }
    }
}

@Composable
private fun DialogActionButton(action: DialogAction, size: ButtonSize, modifier: Modifier, block: Boolean) {
    val theme = when (action.role) {
        DialogActionRole.PRIMARY -> ButtonTheme.PRIMARY
        DialogActionRole.DESTRUCTIVE -> ButtonTheme.DANGER
        // Reference: Cancel is a tertiary (neutral fill) button.
        DialogActionRole.NORMAL, DialogActionRole.CANCEL -> ButtonTheme.DEFAULT
    }
    Button(
        text = action.text,
        onClick = action.onClick,
        modifier = modifier,
        theme = theme,
        type = ButtonType.FILL,
        size = size,
        shape = ButtonShape.ROUND,
        disabled = !action.enabled,
        block = block,
    )
}

/** Geometry and type shared by the dialog family. */
object DialogDefaults {
    /** Minimum touch height of an action. */
    val actionHeight: Dp = ControlGeometry.controlSmall

    /** Title: reference `text-lg` at medium weight. */
    val titleStyle: TextStyle
        @Composable get() = Theme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
}
