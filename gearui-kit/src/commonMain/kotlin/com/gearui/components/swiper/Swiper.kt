package com.gearui.components.swiper

import com.tencent.kuikly.compose.animation.core.animateDpAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.pager.HorizontalPager
import com.tencent.kuikly.compose.foundation.pager.rememberPagerState
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Swiper navigation type - 指示器类型
 */
enum class SwiperNavigation {
    DOTS,       // 点状指示器
    DOTS_BAR,   // 点条状指示器（选中时变长条）
    FRACTION,   // 分数指示器 (1/5)
    NONE        // 无指示器
}

/**
 * Swiper indicator position - 指示器位置
 */
enum class SwiperIndicatorPosition {
    BOTTOM,           // 内部底部
    TOP,              // 外部顶部
    OUTSIDE_BOTTOM    // 外部底部
}

/**
 * Swiper - 轮播图组件
 *
 * 特性：
 * - 使用 HorizontalPager 实现丝滑滑动
 * - 支持自动播放（用户滑动后重置计时器）
 * - 多种指示器类型（点状、点条状、分数）
 * - 指示器动画效果
 * - 支持循环/非循环模式（真正的无限循环）
 * - 支持箭头导航
 *
 * @param itemCount 轮播项数量
 * @param modifier Modifier
 * @param initialIndex 初始索引
 * @param autoPlay 是否自动播放
 * @param autoPlayInterval 自动播放间隔（毫秒）
 * @param loop 是否循环
 * @param navigation 指示器类型
 * @param indicatorPosition 指示器位置
 * @param showArrows 是否显示箭头
 * @param height 轮播图高度
 * @param onIndexChanged 索引变化回调
 * @param content 内容
 */
@Composable
fun Swiper(
    itemCount: Int,
    modifier: Modifier = Modifier,
    initialIndex: Int = 0,
    autoPlay: Boolean = false,
    autoPlayInterval: Long = 3000L,
    loop: Boolean = true,
    navigation: SwiperNavigation = SwiperNavigation.DOTS,
    indicatorPosition: SwiperIndicatorPosition = SwiperIndicatorPosition.BOTTOM,
    showArrows: Boolean = false,
    height: Dp = 200.dp,
    onIndexChanged: ((Int) -> Unit)? = null,
    content: @Composable BoxScope.(index: Int) -> Unit
) {
    if (itemCount <= 0) return

    val colors = Theme.colors
    val scope = rememberCoroutineScope()

    // 循环模式下，在前后各添加一个虚拟页面
    // [lastPage, page0, page1, ..., pageN, firstPage]
    val totalPages = if (loop && itemCount > 1) itemCount + 2 else itemCount
    val startIndex = if (loop && itemCount > 1) initialIndex + 1 else initialIndex

    val pagerState = rememberPagerState(
        initialPage = startIndex.coerceIn(0, totalPages - 1),
        pageCount = { totalPages }
    )

    // 将 pager 页面索引转换为实际内容索引
    fun pageToContentIndex(page: Int): Int {
        return if (loop && itemCount > 1) {
            when (page) {
                0 -> itemCount - 1  // 虚拟首页 -> 最后一项
                totalPages - 1 -> 0  // 虚拟尾页 -> 第一项
                else -> page - 1     // 真实页面
            }
        } else {
            page
        }
    }

    // 当前实际内容索引
    val currentContentIndex = pageToContentIndex(pagerState.currentPage)

    // 通知索引变化
    LaunchedEffect(currentContentIndex) {
        onIndexChanged?.invoke(currentContentIndex)
    }

    // 处理循环边界跳转
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (loop && itemCount > 1 && !pagerState.isScrollInProgress) {
            when (pagerState.currentPage) {
                0 -> {
                    // 滑到虚拟首页，跳转到真实的最后一页
                    pagerState.scrollToPage(totalPages - 2)
                }
                totalPages - 1 -> {
                    // 滑到虚拟尾页，跳转到真实的第一页
                    pagerState.scrollToPage(1)
                }
            }
        }
    }

    // 自动播放计时器重置 key
    // 当用户滑动结束后（isScrollInProgress 从 true 变为 false），重置计时器
    // 使用 settledPage 而不是 currentPage，确保滑动完全停止后才重置
    var autoPlayResetKey by remember { mutableStateOf(0) }

    // 监听滑动状态变化，滑动结束时重置自动播放计时器
    LaunchedEffect(pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            // 滑动结束，重置自动播放计时器
            autoPlayResetKey++
        }
    }

    // 自动播放 - 只有在非滑动状态下才执行
    LaunchedEffect(autoPlayResetKey, autoPlay) {
        if (autoPlay && itemCount > 1 && !pagerState.isScrollInProgress) {
            delay(autoPlayInterval)
            // 再次检查是否正在滑动，避免用户在等待期间开始滑动
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // 顶部指示器
        if (indicatorPosition == SwiperIndicatorPosition.TOP && navigation != SwiperNavigation.NONE) {
            SwiperIndicator(
                currentIndex = currentContentIndex,
                itemCount = itemCount,
                navigation = navigation,
                isOuter = true
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Swiper 内容区
        Box(modifier = Modifier.fillMaxWidth().height(height)) {
            // HorizontalPager - 核心滑动组件
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val contentIndex = pageToContentIndex(page)
                Box(modifier = Modifier.fillMaxSize()) {
                    content(contentIndex)
                }
            }

            // 箭头导航
            if (showArrows && itemCount > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 左箭头
                    val showLeftArrow = loop || currentContentIndex > 0
                    if (showLeftArrow) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colors.surface.copy(alpha = 0.7f))
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                name = Icons.chevron_left,
                                size = 18.dp,
                                tint = colors.textPrimary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(32.dp))
                    }

                    // 右箭头
                    val showRightArrow = loop || currentContentIndex < itemCount - 1
                    if (showRightArrow) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colors.surface.copy(alpha = 0.7f))
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                name = Icons.chevron_right,
                                size = 18.dp,
                                tint = colors.textPrimary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(32.dp))
                    }
                }
            }

            // 内部底部指示器
            if (indicatorPosition == SwiperIndicatorPosition.BOTTOM && navigation != SwiperNavigation.NONE) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                ) {
                    SwiperIndicator(
                        currentIndex = currentContentIndex,
                        itemCount = itemCount,
                        navigation = navigation,
                        isOuter = false
                    )
                }
            }
        }

        // 外部底部指示器
        if (indicatorPosition == SwiperIndicatorPosition.OUTSIDE_BOTTOM && navigation != SwiperNavigation.NONE) {
            Spacer(modifier = Modifier.height(12.dp))
            SwiperIndicator(
                currentIndex = currentContentIndex,
                itemCount = itemCount,
                navigation = navigation,
                isOuter = true
            )
        }
    }
}

/**
 * SwiperIndicator - 指示器组件
 */
@Composable
private fun SwiperIndicator(
    currentIndex: Int,
    itemCount: Int,
    navigation: SwiperNavigation,
    isOuter: Boolean = false
) {
    val colors = Theme.colors

    // 颜色配置：外部用品牌色，内部用白色
    val activeColor = if (isOuter) colors.primary else colors.textAnti
    val inactiveColor = if (isOuter) colors.border else colors.textAnti.copy(alpha = 0.4f)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when (navigation) {
            SwiperNavigation.DOTS -> {
                // 点状指示器
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            if (isOuter) colors.surface.copy(alpha = 0f)
                            else colors.surface.copy(alpha = 0.3f)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    repeat(itemCount) { index ->
                        val isActive = index == currentIndex
                        // 动画尺寸
                        val size by animateDpAsState(
                            targetValue = if (isActive) 8.dp else 6.dp,
                            animationSpec = tween(durationMillis = 150)
                        )
                        Box(
                            modifier = Modifier
                                .size(size)
                                .clip(CircleShape)
                                .background(if (isActive) activeColor else inactiveColor)
                        )
                    }
                }
            }

            SwiperNavigation.DOTS_BAR -> {
                // 点条状指示器（选中时变长条）
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            if (isOuter) colors.surface.copy(alpha = 0f)
                            else colors.surface.copy(alpha = 0.3f)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    repeat(itemCount) { index ->
                        val isActive = index == currentIndex
                        // 动画宽度
                        val width by animateDpAsState(
                            targetValue = if (isActive) 20.dp else 6.dp,
                            animationSpec = tween(durationMillis = 200)
                        )
                        Box(
                            modifier = Modifier
                                .width(width)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isActive) activeColor else inactiveColor)
                        )
                    }
                }
            }

            SwiperNavigation.FRACTION -> {
                // 分数指示器
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isOuter) colors.textPlaceholder
                            else colors.surface.copy(alpha = 0.5f)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${currentIndex + 1}/$itemCount",
                        style = Typography.BodySmall,
                        color = if (isOuter) colors.textAnti else colors.textAnti
                    )
                }
            }

            SwiperNavigation.NONE -> {}
        }
    }
}

/**
 * SwiperState - 外部控制状态
 */
@Stable
class SwiperState(
    initialIndex: Int = 0,
    private val itemCount: Int
) {
    var currentIndex by mutableStateOf(initialIndex)
        internal set

    fun next(loop: Boolean = true) {
        if (loop || currentIndex < itemCount - 1) {
            currentIndex = if (currentIndex == itemCount - 1) 0 else currentIndex + 1
        }
    }

    fun previous(loop: Boolean = true) {
        if (loop || currentIndex > 0) {
            currentIndex = if (currentIndex == 0) itemCount - 1 else currentIndex - 1
        }
    }

    fun jumpTo(index: Int) {
        if (index in 0 until itemCount) {
            currentIndex = index
        }
    }
}

@Composable
fun rememberSwiperState(
    initialIndex: Int = 0,
    itemCount: Int
): SwiperState {
    return remember(itemCount) {
        SwiperState(initialIndex, itemCount)
    }
}
