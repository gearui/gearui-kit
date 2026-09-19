package com.gearui.components.bottomsheet

import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.motion.menuItemFeedback
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.draw.alpha
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonShape
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberUpdatedState
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.material.MaterialSurface
import com.gearui.foundation.material.Materials
import com.gearui.foundation.sheet.SheetGrabber
import com.gearui.gestures.swipeDismiss
import com.tencent.kuikly.compose.animation.core.Animatable
import com.tencent.kuikly.compose.animation.core.spring
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.ui.unit.IntOffset
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.gearui.theme.Theme
import com.gearui.overlay.LocalOverlayVisible
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.OverlayDefaults
import com.gearui.foundation.layout.Spacing
import com.gearui.i18n.I18n
import com.gearui.foundation.border.BorderWidth
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.runtime.rememberSafeAreaInset
import com.gearui.runtime.SafeAreaEdge

/**
 * BottomSheet - bottom action panel built on the overlay system
 *
 * Built on the GearUI overlay system, so it presents full-screen wherever it is called from.
 *
 * Features:
 * - global presentation through the overlay system
 * - panel rising from the bottom
 * - list of options, scrollable
 * - optional cancel button
 * - optional title and description
 * - dismiss by tapping the scrim
 * - destructive items highlighted
 *
 * @param visible whether it is shown
 * @param onDismiss dismissal callback
 * @param title title
 * @param description description
 * @param items option list
 * @param showCancel whether to show a cancel button
 * @param cancelText cancel button label
 * @param closeOnClickOutside whether tapping outside dismisses
 * @param maxListHeight maximum list height before scrolling; 400dp by default
 * @param onItemClick option click callback
 */
@Composable
fun BottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String? = null,
    description: String? = null,
    items: List<BottomSheetItem>,
    showCancel: Boolean = true,
    cancelText: String = I18n.strings.common.cancel,
    closeOnClickOutside: Boolean = true,
    maxListHeight: Dp = 400.dp,
    onItemClick: (BottomSheetItem, Int) -> Unit
) {
    val colors = Theme.colors
    val controller = LocalOverlayController.current
    var overlayId by remember { mutableStateOf<Long?>(null) }

    val currentDismiss by rememberUpdatedState(onDismiss)
    val currentContent by rememberUpdatedState<@Composable () -> Unit>({
                BottomSheetSurface(
                    title = title,
                    description = description,
                    items = items,
                    showCancel = showCancel,
                    cancelText = cancelText,
                    maxListHeight = maxListHeight,
                    onDismiss = onDismiss,
                    onItemClick = onItemClick
                )
    })

    LaunchedEffect(visible) {
        if (visible) {
            overlayId = controller.show(
                anchorBounds = null,
                options = OverlayOptions(
                    placement = OverlayPlacement.Fullscreen,
                    modal = true,
                    maskColor = OverlayDefaults.scrimColor,
                    dismissPolicy = OverlayDismissPolicy.Sheet.copy(
                        outsideClick = closeOnClickOutside
                    )
                ),
                onDismiss = { currentDismiss() }
            ) {
                currentContent()
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

/**
 * BottomSheet with arbitrary content instead of an option list.
 *
 * Same presentation, scrim, drag-to-dismiss and safe-area handling as the
 * item-list overload; the caller owns what goes inside. Use this when the body
 * is not a list of tappable labels — a paginated roster, a form, a chart.
 *
 * The body is responsible for its own height and scrolling: the sheet does not
 * clamp it, because a LazyColumn inside a height-clamped parent stops scrolling.
 *
 * @param visible whether it is shown
 * @param onDismiss dismissal callback
 * @param title title
 * @param description description
 * @param showCancel whether to show a cancel button
 * @param cancelText cancel button label
 * @param closeOnClickOutside whether tapping outside dismisses
 * @param header custom chrome drawn in place of [title]/[description]; it sits in the
 *   sheet's drag region, so dragging down on it dismisses the sheet just like the grabber
 * @param content sheet body
 */
@Composable
fun BottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String? = null,
    description: String? = null,
    showCancel: Boolean = true,
    cancelText: String = I18n.strings.common.cancel,
    closeOnClickOutside: Boolean = true,
    header: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val controller = LocalOverlayController.current
    var overlayId by remember { mutableStateOf<Long?>(null) }

    // The visibility effect captures values only once. Read stable State holders
    // so an open sheet receives async titles/counts along with its body, rather
    // than leaving a read-receipt subtitle stuck at its initial count.
    val currentTitle by rememberUpdatedState(title)
    val currentDescription by rememberUpdatedState(description)
    val currentHeader by rememberUpdatedState(header)
    val currentBody by rememberUpdatedState(content)
    val currentDismiss by rememberUpdatedState(onDismiss)
    val currentShowCancel by rememberUpdatedState(showCancel)
    val currentCancelText by rememberUpdatedState(cancelText)

    LaunchedEffect(visible) {
        if (visible) {
            overlayId = controller.show(
                anchorBounds = null,
                options = OverlayOptions(
                    placement = OverlayPlacement.Fullscreen,
                    modal = true,
                    maskColor = OverlayDefaults.scrimColor,
                    dismissPolicy = OverlayDismissPolicy.Sheet.copy(
                        outsideClick = closeOnClickOutside
                    )
                ),
                onDismiss = { currentDismiss() }
            ) {
                BottomSheetSurface(
                    title = currentTitle,
                    description = currentDescription,
                    showCancel = currentShowCancel,
                    cancelText = currentCancelText,
                    onDismiss = { currentDismiss() },
                    header = currentHeader,
                    body = currentBody,
                )
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

/**
 * BottomSheetSurface - the shared visual container for BottomSheet
 */
@Composable
internal fun BottomSheetSurface(
    title: String? = null,
    description: String? = null,
    items: List<BottomSheetItem>,
    showCancel: Boolean = true,
    cancelText: String = I18n.strings.common.cancel,
    maxListHeight: Dp = 400.dp,
    onDismiss: () -> Unit,
    onItemClick: (BottomSheetItem, Int) -> Unit
) {
    BottomSheetSurface(
        title = title,
        description = description,
        showCancel = showCancel,
        cancelText = cancelText,
        onDismiss = onDismiss,
    ) {
        BottomSheetItemList(
            items = items,
            maxHeight = maxListHeight,
            onDismiss = onDismiss,
            onItemClick = onItemClick,
        )
    }
}

/**
 * BottomSheetSurface - the same chrome (grabber, header, cancel, safe area) around
 * arbitrary content.
 *
 * The item-list sheet is one body among several. A read-receipt list, a member
 * picker or a form needs the identical container but not a `List<BottomSheetItem>`,
 * and rebuilding the chrome per caller is how sheets drift apart: different corner
 * radius, a missing grabber, or the last row sitting on the home indicator because
 * the caller hardcoded 16dp instead of reading the real inset.
 */
@Composable
internal fun BottomSheetSurface(
    title: String? = null,
    description: String? = null,
    showCancel: Boolean = true,
    cancelText: String = I18n.strings.common.cancel,
    onDismiss: () -> Unit,
    header: (@Composable () -> Unit)? = null,
    body: @Composable () -> Unit,
) {
    val colors = Theme.colors
    val runtimeFlags = LocalRuntimeFlags.current
    val bottomInset = rememberSafeAreaInset(
        edge = SafeAreaEdge.Bottom,
        consume = runtimeFlags.bottomSheetConsumesBottomSafeArea,
        minimum = Spacing.lg,
    )

    // One offset for both the presentation and the drag: the sheet's distance below its
    // resting place, in pixels.
    //
    // Presenting animates it from "one sheet height down", which is off the bottom edge,
    // to 0; dismissing animates it back. The drag tracks the finger 1:1 in the same value
    // and springs back when released short of the threshold — a sheet that does not move
    // under the finger reads as one that cannot be dragged.
    //
    // Sharing the value is what makes a drag that turns into a dismissal continue from
    // where the finger left off instead of jumping.
    val dragOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    // Only the first presentation starts from off-screen; a later height change (the
    // keyboard, a list growing) must not replay the entrance.
    var hasEntered by remember { mutableStateOf(false) }

    // 🔴 The sheet animates itself because only it knows how far it has to travel.
    //
    // The host fades the scrim and keeps this content mounted for
    // OverlayDefaults.transitionDurationMillis after dismissal; the motion has to fit in
    // that budget. Height is measured rather than assumed: sheets here range from a
    // three-row action list to a picker at 80% of the screen, and sliding either by a
    // fixed distance would leave it either hanging short or starting off-screen.
    val presented = LocalOverlayVisible.current
    var sheetHeightPx by remember { mutableStateOf(0f) }
    LaunchedEffect(presented, sheetHeightPx) {
        if (sheetHeightPx <= 0f) return@LaunchedEffect
        if (presented) {
            // Start below the edge on the first measured frame, then rise.
            if (!hasEntered) {
                dragOffset.snapTo(sheetHeightPx)
                hasEntered = true
            }
            dragOffset.animateTo(0f, tween(OverlayDefaults.transitionDurationMillis))
        } else {
            dragOffset.animateTo(sheetHeightPx, tween(OverlayDefaults.transitionDurationMillis))
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { sheetHeightPx = it.height.toFloat() }
                .offset { IntOffset(0, dragOffset.value.roundToInt()) }
                .pointerInput(Unit) {
                    // Stop events reaching the backdrop
                    detectTapGestures { }
                }
        ) {
            // Main panel. Sheet material: the shape goes on the material so the
            // blur is clipped with it, not just the content inside it.
            MaterialSurface(
                material = Materials.Sheet,
                modifier = Modifier.fillMaxWidth(),
                shape = OverlayDefaults.sheetShape,
            ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Grabber + header carry the drag, not the body: the body holds a
                // scrollable list and a downward drag there is a scroll.
                // See Modifier.swipeDismiss.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .swipeDismiss(
                            onProgress = { _, dragY -> scope.launch { dragOffset.snapTo(dragY) } },
                            onCancel = { scope.launch { dragOffset.animateTo(0f, spring()) } },
                            onCommit = onDismiss,
                        )
                ) {
                    SheetGrabber()

                // 🔴 A caller-drawn header belongs in the drag region, not in the body.
                //
                // Sheets whose top row is not "centred title + cancel" — a picker with
                // "close / title / multiselect", say — drew it themselves inside the body, which
                // left the grabber as the only thing that could be dragged: a ~20dp
                // strip. Dragging down from the visible title, which is what people
                // actually do, did nothing at all.
                if (header != null) {
                    header()
                } else if (title != null || description != null) {
                    // Reference `.bottom-sheet__label` / `__description`: start-aligned,
                    // large medium title over a muted description; no divider below.
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = ControlGeometry.overlayPadding,
                                end = ControlGeometry.overlayPadding,
                                bottom = ControlGeometry.menuPaddingBlock,
                            ),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (title != null) {
                            Text(
                                text = title,
                                style = Theme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                                color = colors.foreground
                            )
                        }

                        if (description != null) {
                            if (title != null) Spacer(modifier = Modifier.height(ControlGeometry.dialogTextGap))
                            Text(
                                text = description,
                                style = Theme.typography.bodyMedium,
                                color = colors.mutedForeground
                            )
                        }
                    }
                }
                }

                body()

                // Cancel lives inside the sheet as a neutral button (reference
                // tertiary), not as a flat strip under it.
                if (showCancel) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = ControlGeometry.overlayPadding,
                                end = ControlGeometry.overlayPadding,
                                top = ControlGeometry.dialogActionGap,
                            )
                    ) {
                        Button(
                            text = cancelText,
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            theme = ButtonTheme.DEFAULT,
                            type = ButtonType.FILL,
                            size = ButtonSize.MEDIUM,
                            shape = ButtonShape.ROUND,
                            block = true,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(ControlGeometry.menuPaddingBlock))
            }
            }

            // Bottom safe area: read the real inset and treat 16dp only as a floor.
            // A hardcoded 16dp pushes the last row onto the home indicator (about 34pt).
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomInset)
                    .background(colors.surface)
            )
        }
    }
}

/**
 * BottomSheetItemList - option list, scrollable
 */
@Composable
private fun BottomSheetItemList(
    items: List<BottomSheetItem>,
    maxHeight: Dp,
    onDismiss: () -> Unit,
    onItemClick: (BottomSheetItem, Int) -> Unit
) {
    val colors = Theme.colors

    // List height: one reference menu row per item, capped at maxHeight
    val totalHeight = ControlGeometry.actionSheetRow * items.size
    val listHeight = if (totalHeight > maxHeight) maxHeight else totalHeight

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(listHeight)
            .padding(horizontal = ControlGeometry.sheetMenuPaddingInline)
    ) {
        itemsIndexed(items) { index, item ->
            BottomSheetItemRow(
                item = item,
                onClick = {
                    if (!item.disabled) {
                        onItemClick(item, index)
                        onDismiss()
                    }
                }
            )
        }
    }
}

/**
 * BottomSheetItemRow - one option row
 */
@Composable
private fun BottomSheetItemRow(
    item: BottomSheetItem,
    onClick: () -> Unit
) {
    val colors = Theme.colors
    val interaction = remember { MutableInteractionSource() }

    val textColor = when {
        item.danger -> colors.destructive
        else -> colors.foreground
    }

    // Reference `.menu__item`: radius 16, padding 10, gap 10, animated press fill.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ControlGeometry.actionSheetRow)
            .menuItemFeedback(
                interaction = interaction,
                shape = RoundedCornerShape(ControlGeometry.radiusMenuItem),
                enabled = !item.disabled,
                danger = item.danger,
            )
            .clickable(enabled = !item.disabled, interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = ControlGeometry.menuItemPaddingInline)
            .alpha(if (item.disabled) FeedbackDefaults.disabledOpacity else 1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ControlGeometry.menuItemGap)
    ) {
        if (item.icon != null) {
            item.icon.invoke()
        }
        Text(
            text = item.label,
            style = Theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = textColor
        )
    }
}

/**
 * BottomSheetItem - bottom sheet option data class
 */
data class BottomSheetItem(
    /** option label */
    val label: String,

    /** optional option icon */
    val icon: (@Composable () -> Unit)? = null,

    /** whether it is a destructive action (red text) */
    val danger: Boolean = false,

    /** whether it is disabled */
    val disabled: Boolean = false,

    /** attached payload */
    val data: Any? = null
)

/**
 * BottomSheet.Host - general bottom sheet container built on the Overlay system
 *
 * For bottom sheets holding custom content
 */
object BottomSheet {

    @Composable
    fun Host(
        visible: Boolean,
        onDismiss: () -> Unit,
        closeOnClickOutside: Boolean = true,
        content: @Composable () -> Unit
    ) {
        val colors = Theme.colors
        val controller = LocalOverlayController.current
        var overlayId by remember { mutableStateOf<Long?>(null) }

        val currentContent by rememberUpdatedState(content)
        val currentDismiss by rememberUpdatedState(onDismiss)

        LaunchedEffect(visible) {
            if (visible) {
                overlayId = controller.show(
                    anchorBounds = null,
                    options = OverlayOptions(
                        placement = OverlayPlacement.Fullscreen,
                        modal = true,
                        maskColor = OverlayDefaults.scrimColor,
                        dismissPolicy = OverlayDismissPolicy.Sheet.copy(
                            outsideClick = closeOnClickOutside
                        )
                    ),
                    onDismiss = { currentDismiss() }
                ) {
                    BottomSheetHostSurface(content = currentContent)
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
 * BottomSheetHostSurface - bottom sheet container for custom content
 */
@Composable
private fun BottomSheetHostSurface(
    content: @Composable () -> Unit
) {
    val colors = Theme.colors

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(OverlayDefaults.sheetShape)
                .background(colors.surface)
                .pointerInput(Unit) {
                    // Stop events passing through to the background
                    detectTapGestures { }
                }
        ) {
            content()
        }
    }
}

/**
 * BottomSheetState - convenience state holder
 *
 * Usage:
 * ```
 * val sheetState = remember { BottomSheetState() }
 *
 * BottomSheet(
 *     visible = sheetState.visible,
 *     onDismiss = { sheetState.hide() },
 *     items = listOf(
 *         BottomSheetItem("Share"),
 *         BottomSheetItem("Delete", danger = true)
 *     ),
 *     onItemClick = { item, index ->
 *         // handle the tap
 *     }
 * )
 * ```
 */
class BottomSheetState {
    var visible by mutableStateOf(false)
        private set

    fun show() {
        visible = true
    }

    fun hide() {
        visible = false
    }

    fun toggle() {
        visible = !visible
    }
}
