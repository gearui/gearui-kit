package com.gearui.sample.examples.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.progress.CircularProgress
import com.gearui.components.progress.LinearProgress
import com.gearui.components.progress.ProgressLabelPosition
import com.gearui.components.progress.ProgressStatus
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Demo dimensions: the bar heights, ring sizes and stroke widths being shown off.
private val RowLabelWidth = 40.dp
private val InsideLabelBarHeight = 24.dp
private val BarHeightThin = 4.dp
private val BarHeightMedium = 8.dp
private val BarHeightThick = 16.dp
private val RingSmall = 36.dp
private val RingMedium = 56.dp
private val RingLarge = 80.dp
private val StrokeSmall = 3.dp
private val StrokeMedium = 4.dp
private val StrokeLarge = 6.dp
private val RingInline = 48.dp

/** A caption on the left and a progress bar filling the rest of the row. */
@Composable
private fun LabeledBar(label: String, bar: @Composable (Modifier) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Text(
            text = label,
            style = Theme.typography.bodySmall,
            color = Theme.colors.mutedForeground,
            modifier = Modifier.width(RowLabelWidth)
        )
        bar(Modifier.weight(1f))
    }
}

/** A titled bar with a value on the right, as in an upload or storage row. */
@Composable
private fun UsageBar(title: String, value: String, progress: Float, status: ProgressStatus) {
    val colors = Theme.colors
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = Theme.typography.bodySmall, color = colors.foreground)
            Text(
                text = value,
                style = Theme.typography.bodySmall,
                color = if (status == ProgressStatus.WARNING) colors.warning else colors.mutedForeground
            )
        }
        LinearProgress(
            progress = progress,
            status = status,
            showLabel = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun statusFor(progress: Float): ProgressStatus = when {
    progress >= 1f -> ProgressStatus.SUCCESS
    progress >= 0.7f -> ProgressStatus.WARNING
    else -> ProgressStatus.PRIMARY
}

/**
 * Progress component examples
 */
@Composable
fun ProgressExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    var dynamicProgress by remember { mutableStateOf(0f) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (dynamicProgress < 1f) {
                delay(50)
                dynamicProgress = (dynamicProgress + 0.01f).coerceAtMost(1f)
            }
            isRunning = false
        }
    }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "基础进度条",
            description = "LinearProgress 按 progress 填充"
        ) {
            listOf(0.3f, 0.6f, 1f).forEach {
                LinearProgress(progress = it, modifier = Modifier.fillMaxWidth())
            }
        }

        ExampleSection(
            title = "状态",
            description = "主色、成功、警告、危险"
        ) {
            listOf(
                Triple("主色", 0.7f, ProgressStatus.PRIMARY),
                Triple("成功", 1f, ProgressStatus.SUCCESS),
                Triple("警告", 0.5f, ProgressStatus.WARNING),
                Triple("危险", 0.2f, ProgressStatus.DANGER)
            ).forEach { (label, value, status) ->
                LabeledBar(label) { modifier ->
                    LinearProgress(progress = value, status = status, modifier = modifier)
                }
            }
        }

        ExampleSection(
            title = "标签位置",
            description = "百分比显示在右侧或条内"
        ) {
            LinearProgress(
                progress = 0.65f,
                showLabel = true,
                labelPosition = ProgressLabelPosition.RIGHT,
                modifier = Modifier.fillMaxWidth()
            )
            LinearProgress(
                progress = 0.75f,
                showLabel = true,
                labelPosition = ProgressLabelPosition.INSIDE,
                height = InsideLabelBarHeight,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExampleSection(
            title = "高度",
            description = "height 自定义进度条粗细"
        ) {
            listOf<Pair<String, Dp>>(
                "细" to BarHeightThin,
                "中" to BarHeightMedium,
                "粗" to BarHeightThick
            ).forEach { (label, height) ->
                LabeledBar(label) { modifier ->
                    LinearProgress(
                        progress = 0.6f,
                        height = height,
                        showLabel = false,
                        modifier = modifier
                    )
                }
            }
        }

        ExampleSection(
            title = "环形进度",
            description = "CircularProgress 的尺寸与线宽可调"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgress(
                    progress = 0.25f,
                    size = RingSmall,
                    strokeWidth = StrokeSmall,
                    showLabel = false
                )
                CircularProgress(
                    progress = 0.6f,
                    size = RingMedium,
                    strokeWidth = StrokeMedium,
                    status = ProgressStatus.SUCCESS
                )
                CircularProgress(
                    progress = 0.75f,
                    size = RingLarge,
                    strokeWidth = StrokeLarge,
                    status = ProgressStatus.WARNING
                )
            }
        }

        ExampleSection(
            title = "动态进度",
            description = "进度变化时条和环同步过渡，状态随进度切换"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Button(
                    text = "开始",
                    size = ButtonSize.SMALL,
                    onClick = {
                        dynamicProgress = 0f
                        isRunning = true
                    }
                )
                Button(
                    text = "重置",
                    size = ButtonSize.SMALL,
                    theme = ButtonTheme.DEFAULT,
                    onClick = {
                        isRunning = false
                        dynamicProgress = 0f
                    }
                )
            }
            LinearProgress(
                progress = dynamicProgress,
                status = statusFor(dynamicProgress),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgress(
                    progress = dynamicProgress,
                    size = RingLarge,
                    status = statusFor(dynamicProgress)
                )
            }
        }

        ExampleSection(
            title = "应用场景",
            description = "上传、存储占用、任务完成度"
        ) {
            UsageBar("文件上传中…", "2.5MB / 5MB", 0.5f, ProgressStatus.PRIMARY)
            UsageBar("存储空间", "85GB / 100GB", 0.85f, ProgressStatus.WARNING)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                CircularProgress(
                    progress = 0.8f,
                    size = RingInline,
                    status = ProgressStatus.SUCCESS
                )
                Column {
                    Text(
                        text = "今日任务",
                        style = Theme.typography.bodyMedium,
                        color = colors.foreground
                    )
                    Text(
                        text = "已完成 8/10 项任务",
                        style = Theme.typography.bodySmall,
                        color = colors.mutedForeground
                    )
                }
            }
        }
    }
}
