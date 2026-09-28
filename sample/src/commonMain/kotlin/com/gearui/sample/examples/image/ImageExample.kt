package com.gearui.sample.examples.image

import androidx.compose.runtime.Composable
import com.gearui.components.image.GearImage
import com.gearui.components.image.ImageFit
import com.gearui.components.image.ImageLoadState
import com.gearui.components.image.ImagePlaceholder
import com.gearui.components.image.ImageShape
import com.gearui.components.image.ImageWithState
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.layout.Radius
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.aspectRatio
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

// Demo dimensions: the size of the image slots being shown.
private val ImageSize = 72.dp
private val PlaceholderHeight = 100.dp

/** An image slot with its caption underneath. */
@Composable
private fun LabeledImage(label: String, content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        content()
        Text(
            text = label,
            style = Theme.typography.bodySmall,
            color = Theme.colors.mutedForeground
        )
    }
}

/**
 * Image component examples
 */
@Composable
fun ImageExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "填充模式",
            description = "fit 决定图片如何填入容器：裁剪、拉伸或完整显示"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                listOf(
                    "裁剪" to ImageFit.COVER,
                    "拉伸" to ImageFit.FILL,
                    "适应" to ImageFit.CONTAIN
                ).forEach { (label, fit) ->
                    LabeledImage(label) {
                        GearImage(
                            painter = null,
                            fit = fit,
                            shape = ImageShape.ROUNDED,
                            cornerRadius = Radius.md,
                            placeholderText = fit.name,
                            modifier = Modifier.size(ImageSize)
                        )
                    }
                }
            }
        }

        ExampleSection(
            title = "图片形状",
            description = "方形、圆角方形、圆形"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                listOf(
                    "方形" to ImageShape.SQUARE,
                    "圆角" to ImageShape.ROUNDED,
                    "圆形" to ImageShape.CIRCLE
                ).forEach { (label, shape) ->
                    LabeledImage(label) {
                        GearImage(
                            painter = null,
                            shape = shape,
                            cornerRadius = Radius.md,
                            placeholderText = label,
                            modifier = Modifier.size(ImageSize)
                        )
                    }
                }
            }
        }

        ExampleSection(
            title = "加载状态",
            description = "ImageWithState 按 loadState 显示加载中或加载失败"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                LabeledImage("加载中") {
                    ImageWithState(
                        painter = null,
                        loadState = ImageLoadState.Loading,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = Radius.md,
                        modifier = Modifier.size(ImageSize)
                    )
                }
                LabeledImage("加载失败") {
                    ImageWithState(
                        painter = null,
                        loadState = ImageLoadState.Error(""),
                        shape = ImageShape.ROUNDED,
                        cornerRadius = Radius.md,
                        modifier = Modifier.size(ImageSize)
                    )
                }
                LabeledImage("自定义失败文案") {
                    ImageWithState(
                        painter = null,
                        loadState = ImageLoadState.Error("加载失败"),
                        shape = ImageShape.ROUNDED,
                        cornerRadius = Radius.md,
                        modifier = Modifier.size(ImageSize)
                    )
                }
            }
        }

        ExampleSection(
            title = "带边框",
            description = "showBorder 描边，borderWidth 控制粗细"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                LabeledImage("方形") {
                    GearImage(
                        painter = null,
                        shape = ImageShape.SQUARE,
                        showBorder = true,
                        borderWidth = BorderWidth.thin,
                        placeholderText = "边框",
                        modifier = Modifier.size(ImageSize)
                    )
                }
                LabeledImage("圆角") {
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        showBorder = true,
                        borderWidth = BorderWidth.thick,
                        cornerRadius = Radius.md,
                        placeholderText = "边框",
                        modifier = Modifier.size(ImageSize)
                    )
                }
                LabeledImage("圆形") {
                    GearImage(
                        painter = null,
                        shape = ImageShape.CIRCLE,
                        showBorder = true,
                        borderWidth = BorderWidth.thick,
                        placeholderText = "边框",
                        modifier = Modifier.size(ImageSize)
                    )
                }
            }
        }

        ExampleSection(
            title = "图片占位符",
            description = "ImagePlaceholder 用于无图或待上传的位置"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                ImagePlaceholder(
                    modifier = Modifier.weight(1f).height(PlaceholderHeight)
                )
                ImagePlaceholder(
                    text = "点击上传",
                    modifier = Modifier.weight(1f).height(PlaceholderHeight)
                )
            }
        }

        ExampleSection(
            title = "九宫格",
            description = "等宽正方形格子排成三列"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                (1..9).chunked(3).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row.forEach { number ->
                            GearImage(
                                painter = null,
                                shape = ImageShape.ROUNDED,
                                cornerRadius = Radius.sm,
                                placeholderText = "$number",
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
