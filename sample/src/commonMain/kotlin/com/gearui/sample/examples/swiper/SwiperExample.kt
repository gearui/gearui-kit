package com.gearui.sample.examples.swiper

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.swiper.Swiper
import com.gearui.components.swiper.SwiperNavigation
import com.gearui.components.swiper.SwiperIndicatorPosition
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Swiper 组件示例
 */
@Composable
fun SwiperExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 轮播图颜色列表
    val slideColors = listOf(
        colors.primary,
        colors.success,
        colors.warning,
        colors.danger,
        colors.info,
        colors.primaryActive
    )

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== 组件类型 ==========

        // 点状 (dots)
        ExampleSection(
            title = "点状 (dots)",
            description = "默认点状指示器"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                autoPlayInterval = 3000L,
                navigation = SwiperNavigation.DOTS,
                height = 180.dp
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(slideColors[index % slideColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Slide ${index + 1}",
                        style = Typography.HeadlineMedium,
                        color = colors.textAnti
                    )
                }
            }
        }

        // 点条状 (dots-bar)
        ExampleSection(
            title = "点条状 (dots-bar)",
            description = "选中时指示器变为长条"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                navigation = SwiperNavigation.DOTS_BAR,
                height = 180.dp
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(slideColors[index % slideColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Slide ${index + 1}",
                        style = Typography.HeadlineMedium,
                        color = colors.textAnti
                    )
                }
            }
        }

        // 分式 (fraction)
        ExampleSection(
            title = "分式 (fraction)",
            description = "显示当前页码/总页数"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                navigation = SwiperNavigation.FRACTION,
                height = 180.dp
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(slideColors[index % slideColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Slide ${index + 1}",
                        style = Typography.HeadlineMedium,
                        color = colors.textAnti
                    )
                }
            }
        }

        // 切换按钮 (controls)
        ExampleSection(
            title = "切换按钮 (controls)",
            description = "显示左右切换箭头"
        ) {
            Swiper(
                itemCount = 6,
                loop = false,
                showArrows = true,
                navigation = SwiperNavigation.DOTS,
                height = 180.dp
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(slideColors[index % slideColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Slide ${index + 1}",
                        style = Typography.HeadlineMedium,
                        color = colors.textAnti
                    )
                }
            }
        }

        // 无指示器
        ExampleSection(
            title = "无指示器",
            description = "隐藏指示器，仅支持手势滑动"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                navigation = SwiperNavigation.NONE,
                height = 180.dp
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(slideColors[index % slideColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Slide ${index + 1}",
                        style = Typography.HeadlineMedium,
                        color = colors.textAnti
                    )
                }
            }
        }

        // ========== 组件样式 ==========

        // 内部指示器
        ExampleSection(
            title = "内部指示器",
            description = "指示器显示在轮播图内部底部"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                indicatorPosition = SwiperIndicatorPosition.BOTTOM,
                height = 180.dp
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(slideColors[index % slideColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "内部 ${index + 1}",
                        style = Typography.HeadlineMedium,
                        color = colors.textAnti
                    )
                }
            }
        }

        // 外部指示器
        ExampleSection(
            title = "外部指示器",
            description = "指示器显示在轮播图下方"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                indicatorPosition = SwiperIndicatorPosition.OUTSIDE_BOTTOM,
                height = 160.dp
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(slideColors[index % slideColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "外部 ${index + 1}",
                        style = Typography.HeadlineMedium,
                        color = colors.textAnti
                    )
                }
            }
        }

        // 顶部指示器
        ExampleSection(
            title = "顶部指示器",
            description = "指示器显示在轮播图上方"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                indicatorPosition = SwiperIndicatorPosition.TOP,
                height = 160.dp
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(slideColors[index % slideColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "顶部 ${index + 1}",
                        style = Typography.HeadlineMedium,
                        color = colors.textAnti
                    )
                }
            }
        }

        // ========== 其他配置 ==========

        // 非循环模式
        ExampleSection(
            title = "非循环模式",
            description = "滑动到边界时停止"
        ) {
            Swiper(
                itemCount = 4,
                loop = false,
                showArrows = true,
                height = 160.dp
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(slideColors[index % slideColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "第 ${index + 1} 页",
                        style = Typography.HeadlineMedium,
                        color = colors.textAnti
                    )
                }
            }
        }

        // 监听索引变化
        var currentIndex by remember { mutableStateOf(0) }
        ExampleSection(
            title = "监听索引变化",
            description = "通过 onIndexChanged 获取当前索引"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Swiper(
                    itemCount = 6,
                    height = 140.dp,
                    onIndexChanged = { index ->
                        currentIndex = index
                    }
                ) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(slideColors[index % slideColors.size]),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Index ${index + 1}",
                            style = Typography.HeadlineMedium,
                            color = colors.textAnti
                        )
                    }
                }

                Text(
                    text = "当前索引: $currentIndex",
                    style = Typography.BodyMedium,
                    color = colors.primary
                )
            }
        }

        // 自定义自动播放间隔
        ExampleSection(
            title = "快速轮播",
            description = "自动播放间隔 1.5 秒"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                autoPlayInterval = 1500L,
                height = 140.dp
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(slideColors[index % slideColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Fast ${index + 1}",
                        style = Typography.HeadlineMedium,
                        color = colors.textAnti
                    )
                }
            }
        }

        // 少量页面
        ExampleSection(
            title = "少量页面",
            description = "只有 2 张的轮播图"
        ) {
            Swiper(
                itemCount = 2,
                autoPlay = true,
                loop = true,
                height = 140.dp
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(slideColors[index % slideColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Page ${index + 1}",
                        style = Typography.HeadlineMedium,
                        color = colors.textAnti
                    )
                }
            }
        }
    }
}
