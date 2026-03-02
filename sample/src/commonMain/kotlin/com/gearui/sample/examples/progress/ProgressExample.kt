package com.gearui.sample.examples.progress

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.progress.LinearProgress
import com.gearui.components.progress.CircularProgress
import com.gearui.components.progress.ProgressStatus
import com.gearui.components.progress.ProgressLabelPosition
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import kotlinx.coroutines.delay

/**
 * Progress 进度条组件示例
 *
 * 进度展示组件，支持线性和环形
 */
@Composable
fun ProgressExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 动态进度
    var dynamicProgress by remember { mutableStateOf(0f) }
    var isRunning by remember { mutableStateOf(false) }

    // 自动进度动画
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
        // 基础线性进度条
        ExampleSection(
            title = "基础进度条",
            description = "不同进度值的展示"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LinearProgress(
                    progress = 0.3f,
                    modifier = Modifier.fillMaxWidth()
                )

                LinearProgress(
                    progress = 0.6f,
                    modifier = Modifier.fillMaxWidth()
                )

                LinearProgress(
                    progress = 1f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 不同状态
        ExampleSection(
            title = "不同状态",
            description = "主色、成功、警告、危险状态"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "主色",
                        style = Typography.BodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.width(40.dp)
                    )
                    LinearProgress(
                        progress = 0.7f,
                        status = ProgressStatus.PRIMARY,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "成功",
                        style = Typography.BodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.width(40.dp)
                    )
                    LinearProgress(
                        progress = 1f,
                        status = ProgressStatus.SUCCESS,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "警告",
                        style = Typography.BodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.width(40.dp)
                    )
                    LinearProgress(
                        progress = 0.5f,
                        status = ProgressStatus.WARNING,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "危险",
                        style = Typography.BodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.width(40.dp)
                    )
                    LinearProgress(
                        progress = 0.2f,
                        status = ProgressStatus.DANGER,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 标签位置
        ExampleSection(
            title = "标签位置",
            description = "右侧显示和内部显示"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "右侧显示百分比",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
                LinearProgress(
                    progress = 0.65f,
                    showLabel = true,
                    labelPosition = ProgressLabelPosition.RIGHT,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "内部显示百分比",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
                LinearProgress(
                    progress = 0.75f,
                    showLabel = true,
                    labelPosition = ProgressLabelPosition.INSIDE,
                    height = 24.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 不同高度
        ExampleSection(
            title = "不同高度",
            description = "自定义进度条高度"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "4dp",
                        style = Typography.BodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.width(40.dp)
                    )
                    LinearProgress(
                        progress = 0.6f,
                        height = 4.dp,
                        showLabel = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "8dp",
                        style = Typography.BodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.width(40.dp)
                    )
                    LinearProgress(
                        progress = 0.6f,
                        height = 8.dp,
                        showLabel = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "16dp",
                        style = Typography.BodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.width(40.dp)
                    )
                    LinearProgress(
                        progress = 0.6f,
                        height = 16.dp,
                        showLabel = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 环形进度条
        ExampleSection(
            title = "环形进度条",
            description = "圆形进度展示"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgress(
                    progress = 0.25f,
                    size = 48.dp,
                    status = ProgressStatus.PRIMARY
                )

                CircularProgress(
                    progress = 0.5f,
                    size = 56.dp,
                    status = ProgressStatus.SUCCESS
                )

                CircularProgress(
                    progress = 0.75f,
                    size = 64.dp,
                    status = ProgressStatus.WARNING
                )

                CircularProgress(
                    progress = 1f,
                    size = 72.dp,
                    status = ProgressStatus.PRIMARY
                )
            }
        }

        // 不同尺寸的环形进度
        ExampleSection(
            title = "环形进度尺寸",
            description = "自定义环形进度条大小"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgress(
                        progress = 0.6f,
                        size = 36.dp,
                        strokeWidth = 3.dp,
                        showLabel = false
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "小",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgress(
                        progress = 0.6f,
                        size = 56.dp,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "中",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgress(
                        progress = 0.6f,
                        size = 80.dp,
                        strokeWidth = 6.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "大",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // 动态进度
        ExampleSection(
            title = "动态进度",
            description = "带动画的进度变化"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                        onClick = {
                            isRunning = false
                            dynamicProgress = 0f
                        }
                    )
                }

                LinearProgress(
                    progress = dynamicProgress,
                    status = when {
                        dynamicProgress >= 1f -> ProgressStatus.SUCCESS
                        dynamicProgress >= 0.7f -> ProgressStatus.WARNING
                        else -> ProgressStatus.PRIMARY
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgress(
                        progress = dynamicProgress,
                        size = 80.dp,
                        status = when {
                            dynamicProgress >= 1f -> ProgressStatus.SUCCESS
                            dynamicProgress >= 0.7f -> ProgressStatus.WARNING
                            else -> ProgressStatus.PRIMARY
                        }
                    )
                }
            }
        }

        // 应用场景
        ExampleSection(
            title = "应用场景",
            description = "实际使用中的进度展示"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 文件上传
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "文件上传中...",
                            style = Typography.BodySmall,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "2.5MB / 5MB",
                            style = Typography.BodySmall,
                            color = colors.textSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgress(
                        progress = 0.5f,
                        showLabel = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 存储空间
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "存储空间",
                            style = Typography.BodySmall,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "85GB / 100GB",
                            style = Typography.BodySmall,
                            color = colors.warning
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgress(
                        progress = 0.85f,
                        status = ProgressStatus.WARNING,
                        showLabel = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 任务完成度
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgress(
                        progress = 0.8f,
                        size = 48.dp,
                        status = ProgressStatus.SUCCESS
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "今日任务",
                            style = Typography.BodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "已完成 8/10 项任务",
                            style = Typography.BodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Progress 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. LinearProgress: 线性进度条",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. CircularProgress: 环形进度条",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. ProgressStatus: PRIMARY/SUCCESS/WARNING/DANGER",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. showLabel: 显示百分比标签",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. animated: 开启动画过渡效果",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
