package com.gearui.sample.examples.imageviewer

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.components.button.ButtonTheme
import com.gearui.components.imageviewer.ImageViewer
import com.gearui.components.imageviewer.ImageViewerTrigger
import com.gearui.components.imageviewer.rememberImageViewerState
import com.gearui.components.toast.LocalToast
import com.gearui.components.toast.ToastType
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * ImageViewer 组件示例
 *
 * 用于图片内容的缩略展示与查看
 */
@Composable
fun ImageViewerExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 模拟图片列表（实际使用时传入 Painter）
    val imageCount = 5
    val images = List(imageCount) { null as Nothing? }

    // 各示例的状态
    val basicViewerState = rememberImageViewerState()
    val actionViewerState = rememberImageViewerState()
    val longPressViewerState = rememberImageViewerState()
    val labelViewerState = rememberImageViewerState()

    // Toast 状态
    var toastVisible by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    // 可删除图片列表
    var deletableImages by remember { mutableStateOf(List(4) { null as Nothing? }) }
    val deleteViewerState = rememberImageViewerState()

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== 组件类型 ==========

        // 基础图片预览
        ExampleSection(
            title = "基础图片预览",
            description = "点击按钮打开图片预览"
        ) {
            Button(
                text = "基础图片预览",
                theme = ButtonTheme.PRIMARY,
                type = ButtonType.GHOST,
                size = ButtonSize.LARGE,
                block = true,
                onClick = { basicViewerState.show(0) }
            )
        }

        // 带操作图片预览
        ExampleSection(
            title = "带操作图片预览",
            description = "显示页码和删除按钮"
        ) {
            Button(
                text = "带操作图片预览",
                theme = ButtonTheme.PRIMARY,
                type = ButtonType.GHOST,
                size = ButtonSize.LARGE,
                block = true,
                onClick = { actionViewerState.show(0) }
            )
        }

        // 长按图片
        ExampleSection(
            title = "长按图片",
            description = "长按图片触发回调"
        ) {
            Button(
                text = "长按图片（长按试试）",
                theme = ButtonTheme.PRIMARY,
                type = ButtonType.GHOST,
                size = ButtonSize.LARGE,
                block = true,
                onClick = { longPressViewerState.show(0) }
            )
        }

        // 带图片标题
        ExampleSection(
            title = "带图片标题",
            description = "显示图片标题描述"
        ) {
            Button(
                text = "带图片标题",
                theme = ButtonTheme.PRIMARY,
                type = ButtonType.GHOST,
                size = ButtonSize.LARGE,
                block = true,
                onClick = { labelViewerState.show(0) }
            )
        }

        // ========== 缩略图触发器 ==========

        ExampleSection(
            title = "缩略图触发器",
            description = "点击缩略图打开预览"
        ) {
            ImageViewerTrigger(
                images = images,
                thumbnailSize = 80,
                spacing = 8,
                maxDisplay = 9,
                showIndex = true
            )
        }

        // 可删除的图片预览
        ExampleSection(
            title = "可删除的图片预览",
            description = "支持删除图片操作"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    text = "打开可删除图片预览 (${deletableImages.size}张)",
                    theme = ButtonTheme.PRIMARY,
                    type = ButtonType.GHOST,
                    size = ButtonSize.LARGE,
                    block = true,
                    onClick = {
                        if (deletableImages.isNotEmpty()) {
                            deleteViewerState.show(0)
                        }
                    }
                )

                if (deletableImages.isEmpty()) {
                    Text(
                        text = "图片已全部删除",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                // 重置按钮
                if (deletableImages.size < 4) {
                    Button(
                        text = "重置图片列表",
                        theme = ButtonTheme.DEFAULT,
                        type = ButtonType.OUTLINE,
                        size = ButtonSize.SMALL,
                        onClick = {
                            deletableImages = List(4) { null }
                        }
                    )
                }
            }
        }

        // 九宫格展示
        ExampleSection(
            title = "九宫格展示",
            description = "常见的图片九宫格布局"
        ) {
            val gridImages = List(9) { null as Nothing? }
            ImageViewerTrigger(
                images = gridImages,
                thumbnailSize = 100,
                spacing = 4,
                maxDisplay = 9
            )
        }

        // 超出数量显示
        ExampleSection(
            title = "超出数量显示",
            description = "超过最大显示数量时显示 +N"
        ) {
            val manyImages = List(15) { null as Nothing? }
            ImageViewerTrigger(
                images = manyImages,
                thumbnailSize = 80,
                spacing = 8,
                maxDisplay = 6
            )
        }
    }

    // ========== 图片预览器 ==========

    // 基础预览
    if (basicViewerState.isVisible) {
        ImageViewer(
            images = images,
            state = basicViewerState,
            showIndex = true,
            showDeleteBtn = false
        )
    }

    // 带操作预览
    if (actionViewerState.isVisible) {
        ImageViewer(
            images = images,
            state = actionViewerState,
            showIndex = true,
            showDeleteBtn = true,
            onDelete = { index ->
                toastMessage = "删除图片 ${index + 1}"
                toastVisible = true
            }
        )
    }

    // 长按预览
    if (longPressViewerState.isVisible) {
        ImageViewer(
            images = images,
            state = longPressViewerState,
            showIndex = true,
            showDeleteBtn = true,
            onLongPress = { index ->
                toastMessage = "长按了图片 ${index + 1}"
                toastVisible = true
            }
        )
    }

    // 带标题预览
    if (labelViewerState.isVisible) {
        ImageViewer(
            images = images,
            state = labelViewerState,
            labels = listOf("风景图片 1", "风景图片 2", "风景图片 3", "风景图片 4", "风景图片 5"),
            showIndex = true
        )
    }

    // 可删除预览
    if (deleteViewerState.isVisible && deletableImages.isNotEmpty()) {
        ImageViewer(
            images = deletableImages,
            state = deleteViewerState,
            showIndex = true,
            showDeleteBtn = true,
            onDelete = { index ->
                val newList = deletableImages.toMutableList().apply {
                    removeAt(index)
                }
                deletableImages = newList
                toastMessage = "删除了图片 ${index + 1}"
                toastVisible = true
                if (newList.isEmpty()) {
                    deleteViewerState.hide()
                }
            }
        )
    }

    // Toast
    LocalToast(
        message = toastMessage,
        visible = toastVisible,
        onDismiss = { toastVisible = false },
        type = ToastType.INFO
    )
}
