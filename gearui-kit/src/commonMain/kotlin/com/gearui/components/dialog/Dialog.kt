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
            .widthIn(min = 270.dp, max = 320.dp)
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
            .shadow(Theme.elevation.modal, OverlayDefaults.modalShape)
            .background(colors.surface, OverlayDefaults.modalShape)
            .border(BorderWidth.thin, colors.border, OverlayDefaults.modalShape)
    ) {
        content()
    }
}

/**
 * DialogContent — title, optional body, then a list of actions.
 *
 * The shape is the platform alert: everything centred, the actions stacked
 * full-width under a hairline, one per row. That is what people already know
 * from every system prompt, and it is why actions are a `List<DialogAction>`
 * rather than a composable slot — the previous slot was a `RowScope` of raw
 * Buttons, so each screen chose its own button type, theme, size and spacing,
 * and the result was a right-aligned desktop dialog with a filled button in
 * the corner. Roles decide the drawing now; callers only say what an action
 * means.
 *
 * Layout rules:
 * - **Two short actions sit side by side**, split by a vertical hairline, the
 *   way a system alert does; anything else stacks. "Short" is measured in
 *   characters because Kuikly cannot measure text here, so the threshold is
 *   deliberately conservative — a wrong guess must never clip a label.
 * - **CANCEL always goes last** regardless of the order passed in. Backing out
 *   belongs in the same place in every dialog; a caller that lists it first
 *   should not move the button under the user's thumb.
 *
 * @param title required; the question being asked
 * @param message optional supporting line
 * @param content optional custom body, scrollable, between message and actions
 * @param actions one row each; empty is not useful and renders nothing
 */
@Composable
fun DialogContent(
    title: String? = null,
    message: String? = null,
    content: (@Composable () -> Unit)? = null,
    actions: List<DialogAction> = emptyList(),
) {
    val colors = Theme.colors

    // Prefer giving a dialog a title: it is the question being asked, and the
    // interaction reference this kit follows (Apple/UIKit, DESIGN_SYSTEM_SPEC
    // §0.1) treats the title as the required part and the message as optional
    // support. A message-only dialog is still allowed, so it has to look
    // deliberate rather than like a dialog whose title failed to load — which
    // is what happens if the message keeps its supporting styling and the
    // layout keeps the empty title's space above it.
    val messageIsPrimary = title == null

    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(
                    // Without a title there is nothing to give the extra room to.
                    top = if (messageIsPrimary) Spacing.lg else Spacing.xl,
                    bottom = if (actions.isEmpty()) Spacing.xl else Spacing.lg,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (title != null) {
                com.gearui.foundation.primitives.Text(
                    text = title,
                    style = com.gearui.foundation.typography.Typography.TitleSmall,
                    color = colors.foreground,
                    textAlign = TextAlign.Center,
                )
            }

            if (message != null) {
                if (title != null) Spacer(modifier = Modifier.height(Spacing.xs))
                com.gearui.foundation.primitives.Text(
                    text = message,
                    // The only text in the card carries it, so it reads as content
                    // rather than as a footnote under a missing heading.
                    style = if (messageIsPrimary) {
                        com.gearui.foundation.typography.Typography.BodyMedium
                    } else {
                        com.gearui.foundation.typography.Typography.BodySmall
                    },
                    color = if (messageIsPrimary) colors.foreground else colors.mutedForeground,
                    textAlign = TextAlign.Center,
                )
            }

            // Custom content SCROLLS; title and actions stay FIXED.
            //
            // Capping only the outer box (heightIn max) is not enough: when content
            // exceeds the cap, under large system fonts, or on small screens, the
            // overflow is clipped together with the actions — the user sees the
            // dialog and cannot reach its buttons, short of killing the app.
            //
            // Kuikly has no Modifier.verticalScroll, so this uses the repository's own
            // ScrollView (a LazyColumn with one item wrapping a Column — the same
            // workaround as the existing nav-return case).
            if (content != null) {
                Spacer(modifier = Modifier.height(Spacing.lg))
                ScrollView(
                    modifier = Modifier
                        .fillMaxWidth()
                        // The cap leaves room for title and actions: 360 + title/actions/padding stays within the outer 560.
                        .heightIn(max = 360.dp)
                ) {
                    content()
                }
            }
        }

        if (actions.isNotEmpty()) {
            DialogActions(actions)
        }
    }
}

/** Longest label, in characters, that still fits a side-by-side pair. */
private const val SIDE_BY_SIDE_MAX_CHARS = 6

@Composable
private fun DialogActions(actions: List<DialogAction>) {
    val colors = Theme.colors

    // Cancel last, everything else in the order given.
    val ordered = actions.filter { it.role != DialogActionRole.CANCEL } +
        actions.filter { it.role == DialogActionRole.CANCEL }

    val sideBySide = ordered.size == 2 && ordered.all { it.text.length <= SIDE_BY_SIDE_MAX_CHARS }

    Box(modifier = Modifier.fillMaxWidth().height(BorderWidth.hairline).background(colors.border))

    if (sideBySide) {
        Row(modifier = Modifier.fillMaxWidth().height(DialogDefaults.actionHeight)) {
            DialogActionCell(ordered[0], Modifier.weight(1f))
            Box(modifier = Modifier.width(BorderWidth.hairline).fillMaxHeight().background(colors.border))
            DialogActionCell(ordered[1], Modifier.weight(1f))
        }
    } else {
        Column(modifier = Modifier.fillMaxWidth()) {
            ordered.forEachIndexed { index, action ->
                if (index > 0) {
                    Box(modifier = Modifier.fillMaxWidth().height(BorderWidth.hairline).background(colors.border))
                }
                DialogActionCell(action, Modifier.fillMaxWidth().height(DialogDefaults.actionHeight))
            }
        }
    }
}

@Composable
private fun DialogActionCell(action: DialogAction, modifier: Modifier) {
    val colors = Theme.colors
    val tint = when (action.role) {
        DialogActionRole.DESTRUCTIVE -> colors.destructive
        else -> colors.primary
    }
    val style = when (action.role) {
        DialogActionRole.PRIMARY, DialogActionRole.CANCEL ->
            com.gearui.foundation.typography.Typography.MarkMedium
        else -> com.gearui.foundation.typography.Typography.BodyMedium
    }

    Box(
        modifier = modifier
            .then(if (action.enabled) Modifier.clickable { action.onClick() } else Modifier)
            .height(DialogDefaults.actionHeight),
        contentAlignment = Alignment.Center,
    ) {
        com.gearui.foundation.primitives.Text(
            text = action.text,
            style = style,
            color = if (action.enabled) tint else colors.mutedForeground,
            textAlign = TextAlign.Center,
        )
    }
}

/** Geometry shared by the dialog family. */
object DialogDefaults {
    /** Height of one action row. Matches the platform alert button. */
    val actionHeight: Dp = 44.dp
}
