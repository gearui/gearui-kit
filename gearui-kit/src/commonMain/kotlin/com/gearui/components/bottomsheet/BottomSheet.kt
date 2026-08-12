package com.gearui.components.bottomsheet

import androidx.compose.runtime.*
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
import com.gearui.theme.Theme
import com.gearui.foundation.typography.Typography
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
    val colors = Theme.colors
    val runtimeFlags = LocalRuntimeFlags.current
    val bottomInset = rememberSafeAreaInset(
        edge = SafeAreaEdge.Bottom,
        consume = runtimeFlags.bottomSheetConsumesBottomSafeArea,
        minimum = Spacing.lg,
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    // Stop events reaching the backdrop
                    detectTapGestures { }
                }
        ) {
            // Main panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(OverlayDefaults.sheetShape)
                    .background(colors.surface)
            ) {
            // Title area
                if (title != null || description != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (title != null) {
                            Text(
                                text = title,
                                style = Typography.TitleMedium,
                                color = colors.foreground
                            )
                        }

                        if (description != null) {
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = description,
                                style = Typography.BodySmall,
                                color = colors.mutedForeground
                            )
                        }
                    }

                    DividerFull()
                }

                // Option list, scrollable
                BottomSheetItemList(
                    items = items,
                    maxHeight = maxListHeight,
                    onDismiss = onDismiss,
                    onItemClick = onItemClick
                )
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
                        style = Typography.BodyLarge,
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
                    style = Typography.BodyLarge,
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
