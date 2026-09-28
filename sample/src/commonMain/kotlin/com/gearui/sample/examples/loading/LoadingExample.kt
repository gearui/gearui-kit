package com.gearui.sample.examples.loading

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.button.Button
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.loading.FullScreenLoading
import com.gearui.components.loading.Loading
import com.gearui.components.loading.LoadingIcon
import com.gearui.components.loading.LoadingLayout
import com.gearui.components.loading.LoadingSize
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Height of the area standing in for a list that is still loading.
private val ListAreaHeight = 120.dp
private const val SubmitMillis = 2000L
private const val FullScreenMillis = 3000L

/**
 * Loading: shows that a page, an area or an action is in progress.
 */
@Composable
fun LoadingExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    var showFullScreen by remember { mutableStateOf(false) }
    var submitting by remember { mutableStateOf(false) }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "图标类型",
            description = "LoadingIcon：CIRCLE 圆形、ACTIVITY 菊花、POINT 点状"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LabeledLoading(label = "圆形") { Loading(icon = LoadingIcon.CIRCLE) }
                LabeledLoading(label = "菊花") { Loading(icon = LoadingIcon.ACTIVITY) }
                LabeledLoading(label = "点状") { Loading(icon = LoadingIcon.POINT) }
            }
        }

        ExampleSection(
            title = "图标加文字",
            description = "LoadingLayout.HORIZONTAL 文字在右，VERTICAL 文字在下"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Loading(text = "加载中…", layout = LoadingLayout.HORIZONTAL)
                Loading(text = "加载中…", layout = LoadingLayout.VERTICAL)
            }
        }

        ExampleSection(
            title = "尺寸",
            description = "LoadingSize：SMALL、MEDIUM（默认）、LARGE"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LabeledLoading(label = "SMALL") { Loading(size = LoadingSize.SMALL) }
                LabeledLoading(label = "MEDIUM") { Loading(size = LoadingSize.MEDIUM) }
                LabeledLoading(label = "LARGE") { Loading(size = LoadingSize.LARGE) }
            }
        }

        ExampleSection(
            title = "颜色",
            description = "color 使用主题语义色"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColoredLoading(label = "主色", color = colors.primary)
                ColoredLoading(label = "成功", color = colors.success)
                ColoredLoading(label = "警告", color = colors.warning)
                ColoredLoading(label = "危险", color = colors.destructive)
            }
        }

        ExampleSection(
            title = "按钮加载",
            description = "Button 的 loading 状态，点击后提交 2 秒"
        ) {
            Button(
                text = if (submitting) "提交中…" else "提交",
                loading = submitting,
                block = true,
                onClick = { submitting = true }
            )
        }

        ExampleSection(
            title = "区域加载",
            description = "列表等内容区域在数据到达前居中显示"
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(ListAreaHeight),
                contentAlignment = Alignment.Center
            ) {
                Loading(size = LoadingSize.LARGE, text = "加载列表数据…")
            }
        }

        // Full-screen loading is a non-anchored overlay, so its entry is an arrow row
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "全屏加载",
            description = "FullScreenLoading 遮住整页，3 秒后自动关闭"
        ) {
            CellGroup(items = listOf("展示全屏加载")) { title ->
                Cell(
                    title = title,
                    arrow = true,
                    onClick = { showFullScreen = true }
                )
            }
        }
    }

    FullScreenLoading(
        visible = showFullScreen,
        text = "加载中，请稍候…"
    )

    LaunchedEffect(showFullScreen) {
        if (showFullScreen) {
            delay(FullScreenMillis)
            showFullScreen = false
        }
    }

    LaunchedEffect(submitting) {
        if (submitting) {
            delay(SubmitMillis)
            submitting = false
        }
    }
}

@Composable
private fun LabeledLoading(label: String, indicator: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        indicator()
        Text(
            text = label,
            style = Theme.typography.bodySmall,
            color = Theme.colors.mutedForeground
        )
    }
}

@Composable
private fun ColoredLoading(label: String, color: Color) {
    LabeledLoading(label = label) { Loading(color = color) }
}
