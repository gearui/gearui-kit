package com.gearui.sample.examples.popup

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.layout.boundsInRoot
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.components.popup.Popup
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.overlay.GearOverlayPlacement
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme

/**
 * Popup 组件示例
 *
 * - 组件类型：顶部弹出、左侧弹出、居中弹出、底部弹出、右侧弹出
 * - 组件示例：各种带标题、关闭按钮的变体
 */
@Composable
fun PopupExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // ========== 组件类型状态 ==========
    var showTopPopup by remember { mutableStateOf(false) }
    var showLeftPopup by remember { mutableStateOf(false) }
    var showCenterPopup by remember { mutableStateOf(false) }
    var showBottomPopup by remember { mutableStateOf(false) }
    var showRightPopup by remember { mutableStateOf(false) }

    // ========== 组件示例状态 ==========
    var showBottomWithTitleAndOp by remember { mutableStateOf(false) }
    var showBottomWithOp by remember { mutableStateOf(false) }
    var showBottomWithTitleAndClose by remember { mutableStateOf(false) }
    var showBottomWithClose by remember { mutableStateOf(false) }
    var showBottomWithTitle by remember { mutableStateOf(false) }
    var showCenterWithClose by remember { mutableStateOf(false) }
    var showCenterWithUnderClose by remember { mutableStateOf(false) }

    // 锚点位置
    var topAnchor by remember { mutableStateOf<Rect?>(null) }
    var leftAnchor by remember { mutableStateOf<Rect?>(null) }
    var bottomAnchor by remember { mutableStateOf<Rect?>(null) }
    var rightAnchor by remember { mutableStateOf<Rect?>(null) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== 组件类型 ==========

        // 顶部弹出
        ExampleSection(
            title = "顶部弹出",
            description = "从锚点上方弹出"
        ) {
            Button(
                text = "顶部弹出",
                onClick = { showTopPopup = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { topAnchor = it.boundsInRoot() }
            )

            Popup.Host(
                visible = showTopPopup,
                anchorBounds = topAnchor,
                placement = GearOverlayPlacement.TopLeft,
                onDismiss = { showTopPopup = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(colors.surface)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "顶部弹出内容",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // 左侧弹出
        ExampleSection(
            title = "左侧弹出",
            description = "从锚点左侧弹出"
        ) {
            Button(
                text = "左侧弹出",
                onClick = { showLeftPopup = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { leftAnchor = it.boundsInRoot() }
            )

            Popup.Host(
                visible = showLeftPopup,
                anchorBounds = leftAnchor,
                placement = GearOverlayPlacement.LeftTop,
                onDismiss = { showLeftPopup = false }
            ) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(120.dp)
                        .background(colors.surface)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "左侧弹出内容",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // 居中弹出
        ExampleSection(
            title = "居中弹出",
            description = "屏幕居中弹出"
        ) {
            Button(
                text = "居中弹出",
                onClick = { showCenterPopup = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )

            Popup.Host(
                visible = showCenterPopup,
                anchorBounds = null,
                placement = GearOverlayPlacement.Center,
                onDismiss = { showCenterPopup = false }
            ) {
                Box(
                    modifier = Modifier
                        .width(240.dp)
                        .height(240.dp)
                        .background(colors.surface)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "居中弹出内容",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // 底部弹出
        ExampleSection(
            title = "底部弹出",
            description = "从锚点下方弹出"
        ) {
            Button(
                text = "底部弹出",
                onClick = { showBottomPopup = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { bottomAnchor = it.boundsInRoot() }
            )

            Popup.Host(
                visible = showBottomPopup,
                anchorBounds = bottomAnchor,
                placement = GearOverlayPlacement.BottomLeft,
                onDismiss = { showBottomPopup = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(colors.surface)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "底部弹出内容",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // 右侧弹出
        ExampleSection(
            title = "右侧弹出",
            description = "从锚点右侧弹出"
        ) {
            Button(
                text = "右侧弹出",
                onClick = { showRightPopup = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { rightAnchor = it.boundsInRoot() }
            )

            Popup.Host(
                visible = showRightPopup,
                anchorBounds = rightAnchor,
                placement = GearOverlayPlacement.RightTop,
                onDismiss = { showRightPopup = false }
            ) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(120.dp)
                        .background(colors.surface)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "右侧弹出内容",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // ========== 组件示例 ==========

        // 底部弹出层-带标题及操作
        ExampleSection(
            title = "底部弹出层-带标题及操作",
            description = "包含标题栏和取消/确定操作按钮"
        ) {
            Button(
                text = "底部弹出层-带标题及操作",
                onClick = { showBottomWithTitleAndOp = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )

            Popup.Host(
                visible = showBottomWithTitleAndOp,
                anchorBounds = null,
                placement = GearOverlayPlacement.Center,
                onDismiss = { showBottomWithTitleAndOp = false }
            ) {
                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .background(colors.surface)
                ) {
                    // 标题栏
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "取消",
                            style = Typography.BodyMedium,
                            color = colors.textSecondary,
                            modifier = Modifier.clickable { showBottomWithTitleAndOp = false }
                        )
                        Text(
                            text = "标题文字",
                            style = Typography.TitleMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "确定",
                            style = Typography.BodyMedium,
                            color = colors.primary,
                            modifier = Modifier.clickable { showBottomWithTitleAndOp = false }
                        )
                    }
                    // 内容区
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(colors.background)
                    )
                }
            }
        }

        // 底部弹出层-带操作
        ExampleSection(
            title = "底部弹出层-带操作",
            description = "只有取消/确定操作按钮，无标题"
        ) {
            Button(
                text = "底部弹出层-带操作",
                onClick = { showBottomWithOp = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )

            Popup.Host(
                visible = showBottomWithOp,
                anchorBounds = null,
                placement = GearOverlayPlacement.Center,
                onDismiss = { showBottomWithOp = false }
            ) {
                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .background(colors.surface)
                ) {
                    // 操作栏
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "取消",
                            style = Typography.BodyMedium,
                            color = colors.textSecondary,
                            modifier = Modifier.clickable { showBottomWithOp = false }
                        )
                        Text(
                            text = "确定",
                            style = Typography.BodyMedium,
                            color = colors.primary,
                            modifier = Modifier.clickable { showBottomWithOp = false }
                        )
                    }
                    // 内容区
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(colors.background)
                    )
                }
            }
        }

        // 底部弹出层-带标题及关闭
        ExampleSection(
            title = "底部弹出层-带标题及关闭",
            description = "包含居中标题和关闭按钮"
        ) {
            Button(
                text = "底部弹出层-带标题及关闭",
                onClick = { showBottomWithTitleAndClose = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )

            Popup.Host(
                visible = showBottomWithTitleAndClose,
                anchorBounds = null,
                placement = GearOverlayPlacement.Center,
                onDismiss = { showBottomWithTitleAndClose = false }
            ) {
                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .background(colors.surface)
                ) {
                    // 标题栏
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "标题文字",
                            style = Typography.TitleMedium,
                            color = colors.textPrimary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Text(
                            text = "✕",
                            style = Typography.TitleMedium,
                            color = colors.textSecondary,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .clickable { showBottomWithTitleAndClose = false }
                        )
                    }
                    // 内容区
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(colors.background)
                    )
                }
            }
        }

        // 底部弹出层-带关闭
        ExampleSection(
            title = "底部弹出层-带关闭",
            description = "只有关闭按钮，无标题"
        ) {
            Button(
                text = "底部弹出层-带关闭",
                onClick = { showBottomWithClose = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )

            Popup.Host(
                visible = showBottomWithClose,
                anchorBounds = null,
                placement = GearOverlayPlacement.Center,
                onDismiss = { showBottomWithClose = false }
            ) {
                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .background(colors.surface)
                ) {
                    // 关闭按钮
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "✕",
                            style = Typography.TitleMedium,
                            color = colors.textSecondary,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .clickable { showBottomWithClose = false }
                        )
                    }
                    // 内容区
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(colors.background)
                    )
                }
            }
        }

        // 底部弹出层-仅标题
        ExampleSection(
            title = "底部弹出层-仅标题",
            description = "只有标题，无关闭按钮"
        ) {
            Button(
                text = "底部弹出层-仅标题",
                onClick = { showBottomWithTitle = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )

            Popup.Host(
                visible = showBottomWithTitle,
                anchorBounds = null,
                placement = GearOverlayPlacement.Center,
                onDismiss = { showBottomWithTitle = false }
            ) {
                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .background(colors.surface)
                ) {
                    // 标题栏
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "标题文字",
                            style = Typography.TitleMedium,
                            color = colors.textPrimary
                        )
                    }
                    // 内容区
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(colors.background)
                    )
                }
            }
        }

        // 居中弹出层-带关闭
        ExampleSection(
            title = "居中弹出层-带关闭",
            description = "居中显示，右上角关闭按钮"
        ) {
            Button(
                text = "居中弹出层-带关闭",
                onClick = { showCenterWithClose = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )

            Popup.Host(
                visible = showCenterWithClose,
                anchorBounds = null,
                placement = GearOverlayPlacement.Center,
                dismissOnOutside = false,
                onDismiss = { showCenterWithClose = false }
            ) {
                Box(
                    modifier = Modifier
                        .width(240.dp)
                        .height(240.dp)
                        .background(colors.surface)
                ) {
                    // 关闭按钮
                    Text(
                        text = "✕",
                        style = Typography.TitleMedium,
                        color = colors.textSecondary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clickable { showCenterWithClose = false }
                    )
                }
            }
        }

        // 居中弹出层-关闭在下方
        ExampleSection(
            title = "居中弹出层-关闭在下方",
            description = "居中显示，关闭按钮在弹层下方"
        ) {
            Button(
                text = "居中弹出层-关闭在下方",
                onClick = { showCenterWithUnderClose = true },
                size = ButtonSize.LARGE,
                type = ButtonType.OUTLINE,
                modifier = Modifier.fillMaxWidth()
            )

            Popup.Host(
                visible = showCenterWithUnderClose,
                anchorBounds = null,
                placement = GearOverlayPlacement.Center,
                dismissOnOutside = false,
                onDismiss = { showCenterWithUnderClose = false }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 内容区
                    Box(
                        modifier = Modifier
                            .width(240.dp)
                            .height(240.dp)
                            .background(colors.surface)
                    )
                    // 关闭按钮
                    Text(
                        text = "✕",
                        style = Typography.TitleLarge,
                        color = colors.textAnti,
                        modifier = Modifier.clickable { showCenterWithUnderClose = false }
                    )
                }
            }
        }
    }
}
