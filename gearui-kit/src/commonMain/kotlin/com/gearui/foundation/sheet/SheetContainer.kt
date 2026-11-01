package com.gearui.foundation.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.gestures.shouldCommitDismiss
import com.gearui.overlay.LocalOverlayController
import com.gearui.overlay.LocalOverlayScrim
import com.gearui.overlay.LocalOverlayVisible
import com.gearui.overlay.OverlayDefaults
import com.gearui.overlay.OverlayDismissPolicy
import com.gearui.overlay.OverlayOptions
import com.gearui.overlay.OverlayPlacement
import com.gearui.overlay.OverlayScrim
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.runtime.SafeAreaEdge
import com.gearui.runtime.rememberSafeAreaInset
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.animation.core.Animatable
import com.tencent.kuikly.compose.animation.core.AnimationVector1D
import com.tencent.kuikly.compose.animation.core.CubicBezierEasing
import com.tencent.kuikly.compose.animation.core.spring
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.heightIn
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.widthIn
import com.tencent.kuikly.compose.foundation.shape.CornerBasedShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Shape
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.input.pointer.positionChange
import com.tencent.kuikly.compose.ui.input.pointer.util.VelocityTracker
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.IntOffset
import com.tencent.kuikly.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Shows [content] as a modal sheet while [visible]: the scrim, Back and a tap outside
 * close it (the last unless [closeOnClickOutside] is false). [content] draws the sheet —
 * normally a [SheetContainer].
 */
@Composable
internal fun SheetOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    closeOnClickOutside: Boolean = true,
    content: @Composable () -> Unit,
) {
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
                    dismissPolicy = OverlayDismissPolicy.Sheet.copy(outsideClick = closeOnClickOutside),
                ),
                onDismiss = { currentDismiss() },
            ) { currentContent() }
        } else {
            overlayId?.let(controller::close)
            overlayId = null
        }
    }
    DisposableEffect(Unit) { onDispose { overlayId?.let(controller::close) } }
}

/**
 * A sheet as iOS 26 presents one (UISheetPresentationController at a partial height):
 * a rounded panel floating a little above the bottom edge and inset from the sides,
 * the grabber on top, the [header] under it, then the [body].
 *
 * It rises from the bottom on the iOS sheet curve and drops on close. Dragging it down
 * moves it with the finger — the scrim lightening as it goes — and lets go into a
 * dismissal past a third of its height or on a downward fling; otherwise it springs
 * back. Dragging up stretches it a little against resistance and springs back.
 *
 * What can be dragged: the grabber and the header always; the whole sheet when
 * [dragWholeSheet] — only for a body that does not scroll, since a downward drag in a
 * scrolling body is a scroll. (Kuikly does not hand a scroll's overscroll to its
 * parent, so the iOS hand-off "scrolled to the top, keep pulling to dismiss" is not
 * available.)
 *
 * [background] is the sheet's fill: the grouped background behind an inset list, the
 * surface colour for free content.
 */
@Composable
internal fun SheetContainer(
    onDismiss: () -> Unit,
    background: Color,
    dragWholeSheet: Boolean,
    header: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    body: @Composable () -> Unit,
) {
    val sheet = rememberSheetMotion(onDismiss)
    val bottomInset = rememberSafeAreaInset(
        edge = SafeAreaEdge.Bottom,
        consume = LocalRuntimeFlags.current.bottomSheetConsumesBottomSafeArea,
        minimum = Spacing.sm,
    )
    val shape = floatingSheetShape()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier
                .fillMaxWidth()
                .onSizeChanged { sheet.heightPx = it.height.toFloat() }
                .offset { IntOffset(0, sheet.offset.value.roundToInt()) }
                .padding(start = Spacing.sm, end = Spacing.sm, bottom = bottomInset)
                // Taps on the sheet are its own, not the scrim's.
                .pointerInput(Unit) { detectTapGestures { } }
                .then(if (dragWholeSheet) Modifier.sheetDrag(sheet) else Modifier)
                .clip(shape)
                .background(background),
        ) {
            Column(Modifier.fillMaxWidth().then(if (dragWholeSheet) Modifier else Modifier.sheetDrag(sheet))) {
                SheetGrabber()
                header?.invoke()
            }
            body()
        }
    }
}

/**
 * The sheet's shape: the theme's sheet radius on all four corners, since it floats. A
 * square-corner theme (whose sheet shape is not corner-based) keeps its shape.
 */
@Composable
internal fun floatingSheetShape(): Shape {
    val sheet = OverlayDefaults.sheetShape
    return if (sheet is CornerBasedShape) sheet.copy(bottomStart = sheet.topStart, bottomEnd = sheet.topEnd) else sheet
}

/**
 * An iOS sheet title bar: the title centred in a 44-tall row, an optional line under it,
 * and optional [leading] / [trailing] controls level with the title — Cancel and Done,
 * or a close button.
 */
@Composable
internal fun SheetHeader(
    title: String?,
    description: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    if (title == null && description == null && leading == null && trailing == null) return
    val colors = Theme.colors
    val row = ControlGeometry.selectionTouchTarget
    // Clear of the controls on both sides, so the title stays centred on the sheet.
    val side = if (leading != null || trailing != null) Spacing.massive + Spacing.sm else Spacing.lg
    Box(Modifier.fillMaxWidth().padding(start = Spacing.sm, end = Spacing.sm, bottom = Spacing.sm)) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = side),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (title != null) {
                Box(Modifier.heightIn(min = row), contentAlignment = Alignment.Center) {
                    Text(title, style = Theme.typography.titleMedium, color = colors.foreground, textAlign = TextAlign.Center)
                }
            } else {
                Box(Modifier.height(if (description != null) Spacing.sm else row))
            }
            if (description != null) {
                Text(
                    description,
                    style = Theme.typography.bodyExtraSmall,
                    color = colors.mutedForeground,
                    textAlign = TextAlign.Center,
                )
            }
        }
        leading?.let { Box(Modifier.align(Alignment.TopStart).height(row), contentAlignment = Alignment.CenterStart) { it() } }
        trailing?.let { Box(Modifier.align(Alignment.TopEnd).height(row), contentAlignment = Alignment.CenterEnd) { it() } }
    }
}

/** A text button for a sheet's title bar — Cancel, Done — in the accent colour; [emphasized] is bold, as Done is. */
@Composable
internal fun SheetTextAction(label: String, onClick: () -> Unit, emphasized: Boolean = false, enabled: Boolean = true) {
    val colors = Theme.colors
    val interaction = remember { MutableInteractionSource() }
    Box(
        Modifier
            .heightIn(min = ControlGeometry.selectionTouchTarget)
            .widthIn(min = ControlGeometry.selectionTouchTarget)
            .clickable(enabled = enabled, interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = Theme.typography.bodyMedium.copy(fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal),
            color = if (enabled) colors.primarySoftForeground else colors.mutedForeground,
        )
    }
}

/**
 * The sheet's position below its resting place, in pixels, and what moves it: the
 * presentation (up on open, down on close) and the user's drag.
 */
internal class SheetMotion(
    val offset: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope,
    private val scrim: OverlayScrim?,
    private val density: Float,
    private val dismiss: () -> Unit,
) {
    var heightPx by mutableStateOf(0f)
    var dragging = false
        private set

    fun drag(raw: Float) {
        dragging = true
        val y = if (raw >= 0f) raw else -rubberBand(-raw, heightPx)
        scope.launch { offset.snapTo(y) }
        scrim?.fraction = if (heightPx > 0f) 1f - (y / heightPx).coerceIn(0f, 1f) else 1f
    }

    fun release(raw: Float, velocityPxPerSec: Float) {
        val commit = raw > 0f && shouldCommitDismiss(
            dragPx = raw,
            velocityPxPerSec = velocityPxPerSec,
            commitPx = minOf(heightPx * COMMIT_FRACTION, COMMIT_MAX_DP * density),
            minFlingPx = MIN_FLING_DP * density,
            flingVelocityPxPerSec = FLING_DP_PER_SEC * density,
        )
        dragging = false
        if (commit) {
            dismiss()
        } else {
            scope.launch {
                val from = scrim?.fraction ?: 1f
                val start = offset.value
                offset.animateTo(0f, spring(dampingRatio = 1f, stiffness = SETTLE_STIFFNESS)) {
                    // Brighten back with the sheet; a drag that resumes takes over.
                    if (!dragging && start != 0f) scrim?.fraction = from + (1f - from) * (1f - value / start)
                }
                if (!dragging) scrim?.fraction = 1f
            }
        }
    }

    internal companion object {
        // iOS presents a sheet over about 0.4 s on a curve that starts fast and settles.
        const val ENTER_MILLIS = 400
        val enterEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
        const val COMMIT_FRACTION = 0.33f
        const val COMMIT_MAX_DP = 200f
        const val MIN_FLING_DP = 24f
        const val FLING_DP_PER_SEC = 1000f
        const val SETTLE_STIFFNESS = 600f
    }
}

/**
 * UIScrollView's rubber band: the further past the edge, the less each point of drag
 * moves the sheet, approaching [dimension] at most.
 */
internal fun rubberBand(distance: Float, dimension: Float, constant: Float = 0.55f): Float {
    if (distance <= 0f || dimension <= 0f) return 0f
    return (1f - 1f / (distance * constant / dimension + 1f)) * dimension
}

/** [SheetMotion] for the overlay this composes in, entering on open and leaving on close. */
@Composable
internal fun rememberSheetMotion(onDismiss: () -> Unit): SheetMotion {
    val scope = rememberCoroutineScope()
    val scrim = LocalOverlayScrim.current
    val density = LocalDensity.current.density
    val dismiss by rememberUpdatedState(onDismiss)
    val sheet = remember { SheetMotion(Animatable(0f), scope, scrim, density) { dismiss() } }
    val presented = LocalOverlayVisible.current
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(presented, sheet.heightPx) {
        val height = sheet.heightPx
        if (height <= 0f) return@LaunchedEffect
        if (presented) {
            // Only the first presentation starts below the edge; a later height change
            // (content loading in) must not replay it.
            if (!entered) {
                sheet.offset.snapTo(height)
                entered = true
            }
            // A height change restarts this effect; carry on rising from where it was.
            if (sheet.offset.value > 0f && !sheet.dragging) {
                sheet.offset.animateTo(0f, tween(SheetMotion.ENTER_MILLIS, easing = SheetMotion.enterEasing))
            }
        } else {
            // The host keeps the sheet mounted for its exit; leave within it.
            sheet.offset.animateTo(height, tween(OverlayDefaults.transitionDurationMillis, easing = FeedbackDefaults.overlayEaseIn))
        }
    }
    return sheet
}

/** Drag [sheet] vertically: down to dismiss, up against resistance. Taps pass through. */
internal fun Modifier.sheetDrag(sheet: SheetMotion): Modifier = pointerInput(sheet) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        var recognized = false
        var drag = 0f
        val tracker = VelocityTracker()
        tracker.addPosition(down.uptimeMillis, down.position)
        val slop = awaitVerticalTouchSlopOrCancellation(down.id) { change, overSlop ->
            val across = abs(change.position.x - down.position.x)
            val along = abs(change.position.y - down.position.y)
            if (along > across * DIRECTION_RATIO) {
                change.consume()
                drag = overSlop
                recognized = true
            }
        }
        if (!recognized || slop == null) return@awaitEachGesture
        tracker.addPosition(slop.uptimeMillis, slop.position)
        sheet.drag(drag)
        while (true) {
            val change = awaitPointerEvent().changes.firstOrNull { it.id == slop.id } ?: break
            if (!change.pressed) break
            drag += change.positionChange().y
            tracker.addPosition(change.uptimeMillis, change.position)
            change.consume()
            sheet.drag(drag)
        }
        sheet.release(drag, tracker.calculateVelocity().y)
    }
}

private const val DIRECTION_RATIO = 1.2f
