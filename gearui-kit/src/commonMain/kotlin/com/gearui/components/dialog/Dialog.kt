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
            // 🔴 高度必须**贴合内容**，并且封顶。
            //
            // 只限宽不限高时，任何"想要多少高度就占多少"的子组件（多行 Input 就是
            // 这样：它有意把高度交给调用方）会把弹窗撑满整屏——标题顶进状态栏、
            // 按钮被挤出屏幕外，用户连"确定"都点不到。那不是子组件的错，是这里
            // 少了一个约束：模态卡片不该因为某个孩子的意愿而无限长高。
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
        // 🔴 内容区**可滚，标题与按钮固定**。
        //
        // 只给外层封顶（heightIn max）是不够的：内容超过上限、系统大字体、或小屏时，
        // 溢出的那部分会连着"确定/取消"一起被裁掉——用户看得见弹窗却点不到按钮，
        // 只能杀进程。把可变的那一段放进滚动区，操作区就永远在。
        //
        // Kuikly 不支持 Modifier.verticalScroll，所以用仓库自己的 ScrollView
        // （LazyColumn 实现，单 item 包 Column —— 与既有 nav-return 规避一致）。
        if (content != null) {
            ScrollView(
                modifier = Modifier
                    .fillMaxWidth()
                    // 上限留给标题和按钮：360 + 标题/按钮/内边距 仍在外层 560 之内。
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
