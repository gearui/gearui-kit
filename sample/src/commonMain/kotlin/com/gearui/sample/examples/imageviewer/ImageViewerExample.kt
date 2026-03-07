package com.gearui.sample.examples.imageviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gearui.components.actionsheet.ActionSheet
import com.gearui.components.actionsheet.ActionSheetItem
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.imageviewer.ImageViewer
import com.gearui.components.imageviewer.rememberImageViewerState
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * ImageViewer 组件示例（示例页）
 */
@Composable
fun ImageViewerExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val images = remember {
        // 当前 sample 使用占位图，业务接入时替换为真实 Painter 列表
        listOf<Painter?>(null, null)
    }

    val basicViewerState = rememberImageViewerState()
    val actionViewerState = rememberImageViewerState()
    val longPressViewerState = rememberImageViewerState()
    val ultraWidthViewerState = rememberImageViewerState()
    val ultraHeightViewerState = rememberImageViewerState()
    val labelViewerState = rememberImageViewerState()

    val actionSheetItems = remember {
        listOf(
            ActionSheetItem(label = "保存图片", icon = "💾"),
            ActionSheetItem(label = "删除图片", icon = "🗑️")
        )
    }

    val openImageActionSheet: (Int) -> Unit = { index ->
        ActionSheet.showList(
            items = actionSheetItems,
            onSelected = { item, _ ->
                Toast.show("${item.label}（第 ${index + 1} 张）")
            }
        )
    }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(title = "基础图片预览", description = "点击按钮打开图片预览") {
            Button(
                text = "基础图片预览",
                theme = ButtonTheme.PRIMARY,
                type = ButtonType.GHOST,
                size = ButtonSize.LARGE,
                block = true,
                onClick = { basicViewerState.show(0) }
            )
        }

        ExampleSection(title = "带操作图片预览", description = "显示页码和删除按钮") {
            Button(
                text = "带操作图片预览",
                theme = ButtonTheme.PRIMARY,
                type = ButtonType.GHOST,
                size = ButtonSize.LARGE,
                block = true,
                onClick = { actionViewerState.show(0) }
            )
        }

        ExampleSection(title = "长按图片", description = "长按图片打开操作面板") {
            Button(
                text = "长按图片",
                theme = ButtonTheme.PRIMARY,
                type = ButtonType.GHOST,
                size = ButtonSize.LARGE,
                block = true,
                onClick = { longPressViewerState.show(0) }
            )
        }

        ExampleSection(title = "图片超宽情况", description = "限制预览高度，模拟超宽图观感") {
            Button(
                text = "图片超宽情况",
                theme = ButtonTheme.PRIMARY,
                type = ButtonType.GHOST,
                size = ButtonSize.LARGE,
                block = true,
                onClick = { ultraWidthViewerState.show(0) }
            )
        }

        ExampleSection(title = "图片超高情况", description = "限制预览宽度，模拟超高图观感") {
            Button(
                text = "图片超高情况",
                theme = ButtonTheme.PRIMARY,
                type = ButtonType.GHOST,
                size = ButtonSize.LARGE,
                block = true,
                onClick = { ultraHeightViewerState.show(0) }
            )
        }

        ExampleSection(title = "带图片标题", description = "显示图片标题") {
            Button(
                text = "带图片标题",
                theme = ButtonTheme.PRIMARY,
                type = ButtonType.GHOST,
                size = ButtonSize.LARGE,
                block = true,
                onClick = { labelViewerState.show(0) }
            )
        }
    }

    if (basicViewerState.isVisible) {
        ImageViewer(
            images = images,
            state = basicViewerState
        )
    }

    if (actionViewerState.isVisible) {
        ImageViewer(
            images = images,
            state = actionViewerState,
            showIndex = true,
            showDeleteBtn = true,
            onDelete = { index ->
                Toast.show("删除图片 ${index + 1}")
            }
        )
    }

    if (longPressViewerState.isVisible) {
        ImageViewer(
            images = images,
            state = longPressViewerState,
            showIndex = true,
            showDeleteBtn = true,
            onLongPress = openImageActionSheet
        )
    }

    if (ultraWidthViewerState.isVisible) {
        ImageViewer(
            images = images,
            state = ultraWidthViewerState,
            showIndex = true,
            height = 140.dp,
            onLongPress = openImageActionSheet
        )
    }

    if (ultraHeightViewerState.isVisible) {
        ImageViewer(
            images = images,
            state = ultraHeightViewerState,
            showIndex = true,
            width = 180.dp,
            onLongPress = openImageActionSheet
        )
    }

    if (labelViewerState.isVisible) {
        ImageViewer(
            images = images,
            state = labelViewerState,
            labels = listOf("图片标题1", "图片标题2")
        )
    }

    ActionSheet.Host()
}
