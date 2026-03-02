package com.gearui.sample.examples.loading

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.loading.Loading
import com.gearui.components.loading.LoadingSize
import com.gearui.components.loading.LoadingIcon
import com.gearui.components.loading.LoadingLayout
import com.gearui.components.loading.FullScreenLoading
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Loading 加载组件示例
 *
 * 用于表示页面或操作的加载状态，给予用户反馈的同时减缓等待的焦虑感
 */
@Composable
fun LoadingExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 全屏加载状态
    var showFullScreen by remember { mutableStateOf(false) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ==================== 组件类型 ====================

        // 纯图标
        ExampleSection(
            title = "纯图标",
            description = "三种图标类型：圆形、菊花状、点状"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Loading(
                        size = LoadingSize.SMALL,
                        icon = LoadingIcon.CIRCLE
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "圆形",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Loading(
                        size = LoadingSize.SMALL,
                        icon = LoadingIcon.ACTIVITY
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "菊花状",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Loading(
                        size = LoadingSize.SMALL,
                        icon = LoadingIcon.POINT,
                        color = colors.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "点状",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // 图标加文字横向
        ExampleSection(
            title = "图标加文字横向",
            description = "图标在左，文字在右"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Loading(
                    size = LoadingSize.SMALL,
                    icon = LoadingIcon.CIRCLE,
                    text = "加载中…",
                    layout = LoadingLayout.HORIZONTAL
                )

                Loading(
                    size = LoadingSize.SMALL,
                    icon = LoadingIcon.ACTIVITY,
                    text = "加载中…",
                    layout = LoadingLayout.HORIZONTAL
                )
            }
        }

        // 图标加文字竖向
        ExampleSection(
            title = "图标加文字竖向",
            description = "图标在上，文字在下"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Loading(
                    size = LoadingSize.SMALL,
                    icon = LoadingIcon.CIRCLE,
                    text = "加载中…",
                    layout = LoadingLayout.VERTICAL
                )

                Loading(
                    size = LoadingSize.SMALL,
                    icon = LoadingIcon.ACTIVITY,
                    text = "加载中…",
                    layout = LoadingLayout.VERTICAL
                )
            }
        }

        // 纯文字
        ExampleSection(
            title = "纯文字",
            description = "不显示图标，仅显示文字"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "加载中…",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )

                Text(
                    text = "加载失败",
                    style = Typography.BodyMedium,
                    color = colors.textPlaceholder
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "加载失败",
                        style = Typography.BodyMedium,
                        color = colors.textPlaceholder
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "刷新",
                        style = Typography.BodyMedium,
                        color = colors.primary
                    )
                }
            }
        }

        // ==================== 组件尺寸 ====================

        // 大尺寸
        ExampleSection(
            title = "大尺寸",
            description = "LoadingSize.LARGE"
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Loading(
                    size = LoadingSize.LARGE,
                    icon = LoadingIcon.CIRCLE,
                    text = "加载中…",
                    layout = LoadingLayout.HORIZONTAL
                )
            }
        }

        // 中尺寸
        ExampleSection(
            title = "中尺寸",
            description = "LoadingSize.MEDIUM（默认）"
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Loading(
                    size = LoadingSize.MEDIUM,
                    icon = LoadingIcon.CIRCLE,
                    text = "加载中…",
                    layout = LoadingLayout.HORIZONTAL
                )
            }
        }

        // 小尺寸
        ExampleSection(
            title = "小尺寸",
            description = "LoadingSize.SMALL"
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Loading(
                    size = LoadingSize.SMALL,
                    icon = LoadingIcon.CIRCLE,
                    text = "加载中…",
                    layout = LoadingLayout.HORIZONTAL
                )
            }
        }

        // ==================== 自定义颜色 ====================

        ExampleSection(
            title = "自定义颜色",
            description = "使用主题中的不同颜色"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Loading(
                        size = LoadingSize.MEDIUM,
                        icon = LoadingIcon.CIRCLE,
                        color = colors.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "主色",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Loading(
                        size = LoadingSize.MEDIUM,
                        icon = LoadingIcon.CIRCLE,
                        color = colors.success
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "成功",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Loading(
                        size = LoadingSize.MEDIUM,
                        icon = LoadingIcon.CIRCLE,
                        color = colors.warning
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "警告",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Loading(
                        size = LoadingSize.MEDIUM,
                        icon = LoadingIcon.CIRCLE,
                        color = colors.danger
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "危险",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // ==================== 全屏加载 ====================

        ExampleSection(
            title = "全屏加载",
            description = "展示/隐藏全屏Loading"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    text = "展示Loading",
                    size = ButtonSize.MEDIUM,
                    theme = ButtonTheme.PRIMARY,
                    onClick = { showFullScreen = true },
                    modifier = Modifier.weight(1f)
                )

                Button(
                    text = "隐藏Loading",
                    size = ButtonSize.MEDIUM,
                    theme = ButtonTheme.PRIMARY,
                    onClick = { showFullScreen = false },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ==================== 加载场景 ====================

        ExampleSection(
            title = "加载场景",
            description = "常见的使用场景"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 按钮加载状态
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Loading(
                        size = LoadingSize.SMALL,
                        icon = LoadingIcon.CIRCLE,
                        text = "提交中...",
                        layout = LoadingLayout.HORIZONTAL,
                        color = colors.textAnti
                    )
                }

                // 列表加载
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Loading(
                        size = LoadingSize.LARGE,
                        icon = LoadingIcon.CIRCLE,
                        text = "加载列表数据...",
                        layout = LoadingLayout.VERTICAL
                    )
                }

                // 居中验证（多个Loading并排）
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Loading(
                        size = LoadingSize.LARGE,
                        icon = LoadingIcon.CIRCLE,
                        text = "加载中…",
                        layout = LoadingLayout.VERTICAL
                    )
                    Spacer(modifier = Modifier.width(36.dp))
                    Loading(
                        size = LoadingSize.LARGE,
                        icon = LoadingIcon.ACTIVITY,
                        text = "加载中…",
                        layout = LoadingLayout.VERTICAL
                    )
                }
            }
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Loading 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. LoadingSize: SMALL/MEDIUM/LARGE 三种尺寸",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. LoadingIcon: CIRCLE/ACTIVITY/POINT 三种图标",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. LoadingLayout: VERTICAL/HORIZONTAL 布局方向",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. text: 可选的文字说明",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. color: 自定义加载指示器颜色",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "6. duration: 动画周期（毫秒）",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "7. FullScreenLoading: 全屏遮罩加载",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }

    // 全屏加载层
    FullScreenLoading(
        visible = showFullScreen,
        text = "加载中，请稍候..."
    )

    // 自动关闭全屏加载（3秒后）
    LaunchedEffect(showFullScreen) {
        if (showFullScreen) {
            kotlinx.coroutines.delay(3000)
            showFullScreen = false
        }
    }
}
