package com.gearui.components.bottomsheet

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
import com.gearui.primitives.DividerFull
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
                onDismiss = onDismiss
            ) {
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

    // 🔴 标题与副标题必须跟着数据变，不能停在 show 的那一刻。
    //
    // 下面的 LaunchedEffect 只在 visible 变化时跑一次，闭包捕获的是当时的字符串值。
    // 而这类弹层的常见形态是"打开时数据还没到"——比如已读名单的副标题要等 RPC 回来
    // 才知道是几人。捕获值的结果是：列表行出来了（body 是 composable，读的是 state），
    // 副标题却永远停在「0/0 人已读」。
    // rememberUpdatedState 给的是稳定 State，surface 在 host 的 composition 里读它。
    val currentTitle by rememberUpdatedState(title)
    val currentDescription by rememberUpdatedState(description)
    val currentHeader by rememberUpdatedState(header)

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
                onDismiss = onDismiss
            ) {
                BottomSheetSurface(
                    title = currentTitle,
                    description = currentDescription,
                    showCancel = showCancel,
                    cancelText = cancelText,
                    onDismiss = onDismiss,
                    header = currentHeader,
                    body = content,
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
                // 「关闭 / 标题 / 多选」, say — drew it themselves inside the body, which
                // left the grabber as the only thing that could be dragged: a ~20dp
                // strip. Dragging down from the visible title, which is what people
                // actually do, did nothing at all.
                if (header != null) {
                    header()
                } else if (title != null || description != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (title != null) {
                            Text(
                                text = title,
                                style = Theme.typography.titleMedium,
                                color = colors.foreground
                            )
                        }

                        if (description != null) {
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = description,
                                style = Theme.typography.bodySmall,
                                color = colors.mutedForeground
                            )
                        }
                    }

                    DividerFull()
                }
                }

                body()
            }
            }

            // Cancel button
            if (showCancel) {
                Spacer(modifier = Modifier.height(Spacing.sm))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .clickable(onClick = onDismiss)
                        .padding(vertical = Spacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cancelText,
                        style = Theme.typography.bodyLarge,
                        color = colors.foreground
                    )
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

    // List height: 56dp per item, capped at maxHeight
    val itemHeight = 56
    val totalHeightValue = items.size * itemHeight
    val totalHeight = totalHeightValue.dp
    val listHeight = if (totalHeight > maxHeight) maxHeight else totalHeight

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(listHeight)
            .background(colors.surface)
    ) {
        itemsIndexed(items) { index, item ->
            BottomSheetItemRow(
                item = item,
                showDivider = index < items.size - 1,
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
    showDivider: Boolean = true,
    onClick: () -> Unit
) {
    val colors = Theme.colors

    val textColor = when {
        item.disabled -> colors.mutedForeground
        item.danger -> colors.destructive
        else -> colors.foreground
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(enabled = !item.disabled, onClick = onClick)
                .padding(horizontal = Spacing.lg),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (item.icon != null) {
                    item.icon.invoke()
                    Spacer(modifier = Modifier.width(Spacing.sm))
                }

                Text(
                    text = item.label,
                    style = Theme.typography.bodyLarge,
                    color = textColor
                )
            }
        }

        // Divider
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .height(BorderWidth.hairline)
                    .background(colors.border)
            )
        }
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
                    onDismiss = onDismiss
                ) {
                    BottomSheetHostSurface(content = content)
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
