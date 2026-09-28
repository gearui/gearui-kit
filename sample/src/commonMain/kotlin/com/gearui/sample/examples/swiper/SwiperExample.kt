package com.gearui.sample.examples.swiper

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
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
import com.gearui.sample.pages.SectionSurface
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme

// Demo content dimensions: the banner heights the swipers are shown at.
private val BannerHeight = 180.dp
private val CompactBannerHeight = 140.dp

/**
 * Swiper component examples.
 *
 * A swiper is a full-width surface of its own, so every section is Plain.
 */
@Composable
fun SwiperExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    var currentIndex by remember { mutableStateOf(0) }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "点状指示器",
            description = "默认 navigation = DOTS，指示器位于轮播内部底部"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                autoPlayInterval = 3000L,
                navigation = SwiperNavigation.DOTS,
                height = BannerHeight
            ) { index -> Slide(index, "Slide ${index + 1}") }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "点条指示器",
            description = "DOTS_BAR：选中项变为长条"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                navigation = SwiperNavigation.DOTS_BAR,
                height = BannerHeight
            ) { index -> Slide(index, "Slide ${index + 1}") }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "分式指示器",
            description = "FRACTION：显示当前页码 / 总页数"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                navigation = SwiperNavigation.FRACTION,
                height = BannerHeight
            ) { index -> Slide(index, "Slide ${index + 1}") }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "无指示器",
            description = "NONE：隐藏指示器，仅手势滑动"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                navigation = SwiperNavigation.NONE,
                height = BannerHeight
            ) { index -> Slide(index, "Slide ${index + 1}") }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "切换箭头",
            description = "showArrows 显示左右切换箭头"
        ) {
            Swiper(
                itemCount = 6,
                showArrows = true,
                height = BannerHeight
            ) { index -> Slide(index, "Slide ${index + 1}") }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "外部指示器",
            description = "OUTSIDE_BOTTOM：指示器位于轮播下方"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                indicatorPosition = SwiperIndicatorPosition.OUTSIDE_BOTTOM,
                height = BannerHeight
            ) { index -> Slide(index, "外部 ${index + 1}") }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "顶部指示器",
            description = "TOP：指示器位于轮播内部顶部"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                indicatorPosition = SwiperIndicatorPosition.TOP,
                height = BannerHeight
            ) { index -> Slide(index, "顶部 ${index + 1}") }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "非循环模式",
            description = "loop = false，滑到首尾页时停止"
        ) {
            Swiper(
                itemCount = 4,
                loop = false,
                showArrows = true,
                height = CompactBannerHeight
            ) { index -> Slide(index, "第 ${index + 1} 页") }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "监听索引变化",
            description = "onIndexChanged 回调当前索引：$currentIndex"
        ) {
            Swiper(
                itemCount = 6,
                height = CompactBannerHeight,
                onIndexChanged = { index -> currentIndex = index }
            ) { index -> Slide(index, "Index ${index + 1}") }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "快速轮播",
            description = "autoPlayInterval = 1500，自动播放间隔 1.5 秒"
        ) {
            Swiper(
                itemCount = 6,
                autoPlay = true,
                autoPlayInterval = 1500L,
                height = CompactBannerHeight
            ) { index -> Slide(index, "Fast ${index + 1}") }
        }

        ExampleSection(
            surface = SectionSurface.Plain,
            title = "少量页面",
            description = "只有 2 页时的循环轮播"
        ) {
            Swiper(
                itemCount = 2,
                autoPlay = true,
                loop = true,
                height = CompactBannerHeight
            ) { index -> Slide(index, "Page ${index + 1}") }
        }
    }
}

/** A coloured placeholder slide; the colour cycles through the theme's status colours. */
@Composable
private fun Slide(index: Int, label: String) {
    val colors = Theme.colors
    val slideColors = listOf(
        colors.primary,
        colors.success,
        colors.warning,
        colors.destructive,
        colors.info
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(Theme.shapes.lg)
            .background(slideColors[index % slideColors.size]),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = Theme.typography.headlineMedium,
            color = colors.primaryForeground
        )
    }
}
