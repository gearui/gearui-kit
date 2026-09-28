package com.gearui.sample.examples.scrollshadow

import androidx.compose.runtime.Composable
import com.gearui.components.scrollshadow.ScrollShadow
import com.gearui.components.image.GearImage
import com.gearui.components.image.ImageShape
import com.gearui.foundation.layout.Radius
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.GearLazyColumn
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.gestures.Orientation
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.lazy.LazyRow
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

// Demo dimensions: the scroll viewport and the tiles scrolled inside it.
private val ViewportHeight = 200.dp
private val TileSize = 88.dp

@Composable
fun ScrollShadowExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val vertical = rememberLazyListState()
    val horizontal = rememberLazyListState()

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "纵向",
            description = "上下还有内容时才渐隐，渐隐色取容器底色"
        ) {
            ScrollShadow(
                state = vertical,
                modifier = Modifier.fillMaxWidth().height(ViewportHeight),
                color = colors.surface,
            ) {
                GearLazyColumn(state = vertical, modifier = Modifier.fillMaxWidth()) {
                    items(12) { index ->
                        Text(
                            text = "第 ${index + 1} 条协议条款",
                            style = Theme.typography.bodyMedium,
                            color = colors.foreground,
                            modifier = Modifier.padding(vertical = Spacing.sm),
                        )
                    }
                }
            }
        }

        ExampleSection(
            title = "横向",
            description = "orientation 设为 Horizontal，左右两端渐隐"
        ) {
            ScrollShadow(
                state = horizontal,
                modifier = Modifier.fillMaxWidth(),
                orientation = Orientation.Horizontal,
                color = colors.surface,
            ) {
                LazyRow(
                    state = horizontal,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    items(10) { index ->
                        GearImage(
                            painter = null,
                            shape = ImageShape.ROUNDED,
                            cornerRadius = Radius.xl,
                            placeholderText = "#${index + 1}",
                            modifier = Modifier.size(TileSize),
                        )
                    }
                }
            }
        }
    }
}
