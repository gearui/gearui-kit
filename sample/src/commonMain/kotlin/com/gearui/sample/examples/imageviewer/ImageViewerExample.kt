package com.gearui.sample.examples.imageviewer

import com.gearui.components.icon.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gearui.components.actionsheet.ActionSheet
import com.gearui.components.actionsheet.ActionSheetItem
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.imageviewer.ImageViewer
import com.gearui.components.imageviewer.rememberImageViewerState
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.tencent.kuikly.compose.ui.unit.dp

// Demo dimensions: the constrained viewer sizes that mimic very wide or very tall images.
private val UltraWideHeight = 140.dp
private val UltraTallWidth = 180.dp

/**
 * ImageViewer component examples
 */
@Composable
fun ImageViewerExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val images = remember {
        // This sample uses placeholders; swap in a real list of Painters when integrating
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
            ActionSheetItem(label = "保存图片", icon = Icons.download_simple),
            ActionSheetItem(label = "删除图片", icon = Icons.trash)
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
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "预览方式",
            description = "点击任一行打开对应配置的 ImageViewer"
        ) {
            val entries = listOf<Triple<String, String, () -> Unit>>(
                Triple("基础预览", "只显示图片", { basicViewerState.show(0) }),
                Triple("带操作", "显示页码和删除按钮", { actionViewerState.show(0) }),
                Triple("长按操作", "长按图片打开操作面板", { longPressViewerState.show(0) }),
                Triple("超宽图片", "限制预览高度", { ultraWidthViewerState.show(0) }),
                Triple("超高图片", "限制预览宽度", { ultraHeightViewerState.show(0) }),
                Triple("带图片标题", "每张图下方显示标题", { labelViewerState.show(0) })
            )
            CellGroup(items = entries) { (title, description, open) ->
                Cell(
                    title = title,
                    description = description,
                    arrow = true,
                    onClick = open
                )
            }
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
            height = UltraWideHeight,
            onLongPress = openImageActionSheet
        )
    }

    if (ultraHeightViewerState.isVisible) {
        ImageViewer(
            images = images,
            state = ultraHeightViewerState,
            showIndex = true,
            width = UltraTallWidth,
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
