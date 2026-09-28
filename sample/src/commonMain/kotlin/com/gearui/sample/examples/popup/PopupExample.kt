package com.gearui.sample.examples.popup

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.closebutton.CloseButton
import com.gearui.components.popup.Popup
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.overlay.OverlayPlacement
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme

// Demo content dimensions: the sizes of the placeholder panels shown in the popups.
private val AnchoredPanelWidth = 200.dp
private val AnchoredPanelHeight = 120.dp
private val CenterPanelSize = 240.dp
private val DialogPanelWidth = 300.dp
private val DialogBodyHeight = 200.dp

private class PopupRow(val title: String, val description: String, val onClick: () -> Unit)

/**
 * Popup component examples.
 *
 * Popup.Host already draws the overlay surface, so the content passed to it is only
 * padding and text — never a second background.
 */
@Composable
fun PopupExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    var showCenterPopup by remember { mutableStateOf(false) }
    var showTitleWithActions by remember { mutableStateOf(false) }
    var showTitleWithClose by remember { mutableStateOf(false) }
    var showModalWithClose by remember { mutableStateOf(false) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "锚点弹出",
            description = "placement 决定弹层相对触发按钮的方向，空间不足时自动翻转"
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                AnchoredPopupButton("顶部", "顶部弹出内容", OverlayPlacement.TopLeft)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.huge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnchoredPopupButton("左侧", "左侧弹出内容", OverlayPlacement.LeftTop)
                    AnchoredPopupButton("右侧", "右侧弹出内容", OverlayPlacement.RightTop)
                }
                AnchoredPopupButton("底部", "底部弹出内容", OverlayPlacement.BottomLeft)
            }
        }

        // Centred popups need no anchor, so their entries are arrow rows
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "居中弹出",
            description = "placement = Center，无需锚点"
        ) {
            CellGroup(
                items = listOf(
                    PopupRow("居中弹出", "只有内容的居中弹层") { showCenterPopup = true },
                    PopupRow("标题与操作", "标题栏两侧放取消 / 确定") { showTitleWithActions = true },
                    PopupRow("标题与关闭", "标题居中，右侧 CloseButton") { showTitleWithClose = true },
                    PopupRow("禁止点击外部关闭", "dismissOnOutside = false，只能通过关闭按钮关闭") { showModalWithClose = true },
                )
            ) { row ->
                Cell(
                    title = row.title,
                    description = row.description,
                    arrow = true,
                    onClick = row.onClick
                )
            }

            Popup.Host(
                visible = showCenterPopup,
                anchorBounds = null,
                placement = OverlayPlacement.Center,
                onDismiss = { showCenterPopup = false }
            ) {
                Box(
                    modifier = Modifier.size(CenterPanelSize),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "居中弹出内容",
                        style = Theme.typography.bodyMedium,
                        color = colors.foreground
                    )
                }
            }

            Popup.Host(
                visible = showTitleWithActions,
                anchorBounds = null,
                placement = OverlayPlacement.Center,
                onDismiss = { showTitleWithActions = false }
            ) {
                Column(modifier = Modifier.width(DialogPanelWidth)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            text = "取消",
                            onClick = { showTitleWithActions = false },
                            type = ButtonType.TEXT,
                            theme = ButtonTheme.DEFAULT,
                            size = ButtonSize.SMALL
                        )
                        Text(
                            text = "标题文字",
                            style = Theme.typography.titleMedium,
                            color = colors.foreground
                        )
                        Button(
                            text = "确定",
                            onClick = { showTitleWithActions = false },
                            type = ButtonType.TEXT,
                            size = ButtonSize.SMALL
                        )
                    }
                    DialogBody()
                }
            }

            Popup.Host(
                visible = showTitleWithClose,
                anchorBounds = null,
                placement = OverlayPlacement.Center,
                onDismiss = { showTitleWithClose = false }
            ) {
                Column(modifier = Modifier.width(DialogPanelWidth)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                    ) {
                        Text(
                            text = "标题文字",
                            style = Theme.typography.titleMedium,
                            color = colors.foreground,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        CloseButton(
                            onClick = { showTitleWithClose = false },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                    DialogBody()
                }
            }

            Popup.Host(
                visible = showModalWithClose,
                anchorBounds = null,
                placement = OverlayPlacement.Center,
                dismissOnOutside = false,
                onDismiss = { showModalWithClose = false }
            ) {
                Box(modifier = Modifier.size(CenterPanelSize)) {
                    Text(
                        text = "点击外部不会关闭",
                        style = Theme.typography.bodyMedium,
                        color = colors.foreground,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    CloseButton(
                        onClick = { showModalWithClose = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(Spacing.xs)
                    )
                }
            }
        }
    }
}

/**
 * A Button that opens an anchored Popup on the given [placement]. It records its own
 * bounds, which the popup is positioned against.
 */
@Composable
private fun AnchoredPopupButton(
    label: String,
    message: String,
    placement: OverlayPlacement
) {
    val colors = Theme.colors
    var visible by remember { mutableStateOf(false) }
    var anchor by remember { mutableStateOf<Rect?>(null) }

    Button(
        text = label,
        onClick = { visible = true },
        modifier = Modifier.onGloballyPositioned { anchor = it.boundsInRoot() }
    )

    Popup.Host(
        visible = visible,
        anchorBounds = anchor,
        placement = placement,
        onDismiss = { visible = false }
    ) {
        Box(
            modifier = Modifier
                .width(AnchoredPanelWidth)
                .height(AnchoredPanelHeight)
                .padding(Spacing.lg)
        ) {
            Text(
                text = message,
                style = Theme.typography.bodyMedium,
                color = colors.foreground
            )
        }
    }
}

/** Placeholder body under a popup's header. */
@Composable
private fun DialogBody() {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(DialogBodyHeight)
            .padding(Spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "内容区域",
            style = Theme.typography.bodyMedium,
            color = colors.mutedForeground
        )
    }
}
