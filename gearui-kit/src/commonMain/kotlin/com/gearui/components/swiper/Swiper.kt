package com.gearui.components.swiper

import com.gearui.i18n.I18n
import com.gearui.foundation.interaction.IconActionButton
import com.gearui.foundation.control.ControlGeometry
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
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * Swiper navigation type - indicator style
 */
enum class SwiperNavigation {
    DOTS,
    DOTS_BAR, // dots; the current one stretches into a bar
    FRACTION, // a fraction, such as 1/5
    NONE
}

/**
 * Swiper indicator position
 */
enum class SwiperIndicatorPosition {
    BOTTOM, // inside, at the bottom
    TOP, // outside, above
    OUTSIDE_BOTTOM // outside, below
}

/**
 * Swiper - carousel
 *
 * Features:
 * - smooth swiping via HorizontalPager
 * - autoplay, with the timer reset after a manual swipe
 * - several indicator styles: dots, dot bars, fraction
 * - animated indicators
 * - looping or non-looping, with genuine infinite loop
 * - optional arrow navigation
 *
 * @param itemCount number of slides
 * @param modifier Modifier
 * @param initialIndex initial index
 * @param autoPlay whether to autoplay
 * @param autoPlayInterval autoplay interval in milliseconds
 * @param loop whether to loop
 * @param navigation indicator style
 * @param indicatorPosition indicator position
 * @param showArrows whether to show arrows
 * @param height carousel height
 * @param onIndexChanged index change callback
 * @param content slide content
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
    height: Dp = ControlGeometry.swiperHeight,
    onIndexChanged: ((Int) -> Unit)? = null,
    content: @Composable BoxScope.(index: Int) -> Unit
) {
    if (itemCount <= 0) return

    val colors = Theme.colors
    val scope = rememberCoroutineScope()

    // In loop mode, add one virtual page at each end
    // [lastPage, page0, page1, ..., pageN, firstPage]
    val totalPages = if (loop && itemCount > 1) itemCount + 2 else itemCount
    val startIndex = if (loop && itemCount > 1) initialIndex + 1 else initialIndex

    val pagerState = rememberPagerState(
        initialPage = startIndex.coerceIn(0, totalPages - 1),
        pageCount = { totalPages }
    )

    // Map a pager page index to the real content index
    fun pageToContentIndex(page: Int): Int {
        return if (loop && itemCount > 1) {
            when (page) {
                0 -> itemCount - 1 // leading clone -> last item
                totalPages - 1 -> 0 // trailing clone -> first item
                else -> page - 1 // a real page
            }
        } else {
            page
        }
    }

    // Current real content index
    val currentContentIndex = pageToContentIndex(pagerState.currentPage)

    // Report index changes
    LaunchedEffect(currentContentIndex) {
        onIndexChanged?.invoke(currentContentIndex)
    }

    // Handle the loop boundary jump
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (loop && itemCount > 1 && !pagerState.isScrollInProgress) {
            when (pagerState.currentPage) {
                0 -> {
                    // Reached the virtual first page: jump to the real last page
                    pagerState.scrollToPage(totalPages - 2)
                }
                totalPages - 1 -> {
                    // Reached the virtual last page: jump to the real first page
                    pagerState.scrollToPage(1)
                }
            }
        }
    }

    // Key that resets the autoplay timer.
    // Reset once the user finishes swiping (isScrollInProgress goes true -> false).
    // settledPage rather than currentPage, so the reset waits for the swipe to stop.
    var autoPlayResetKey by remember { mutableStateOf(0) }

    // Watch the scroll state and reset the autoplay timer when it settles
    LaunchedEffect(pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            // Swipe finished: reset the autoplay timer
            autoPlayResetKey++
        }
    }

    // Autoplay - only runs while not being dragged
    LaunchedEffect(autoPlayResetKey, autoPlay) {
        if (autoPlay && itemCount > 1 && !pagerState.isScrollInProgress) {
            delay(autoPlayInterval)
            // Check again for a drag, in case the user started one during the wait
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Top indicator
        if (indicatorPosition == SwiperIndicatorPosition.TOP && navigation != SwiperNavigation.NONE) {
            SwiperIndicator(
                currentIndex = currentContentIndex,
                itemCount = itemCount,
                navigation = navigation,
                isOuter = true
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
        }

        // Swiper content area
        Box(modifier = Modifier.fillMaxWidth().height(height)) {
            // HorizontalPager - the actual swiping component
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val contentIndex = pageToContentIndex(page)
                Box(modifier = Modifier.fillMaxSize()) {
                    content(contentIndex)
                }
            }

            // Arrow navigation
            if (showArrows && itemCount > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(horizontal = Spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left arrow
                    val showLeftArrow = loop || currentContentIndex > 0
                    if (showLeftArrow) {
                        IconActionButton(
                            icon = Icons.caret_left,
                            contentDescription = I18n.strings.media.previousSlide,
                            onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                            size = ControlGeometry.swiperArrowSize,
                            iconSize = IconSizes.Default.lg,
                            background = colors.surface.copy(alpha = 0.7f),
                            tint = colors.foreground,
                        )
                    } else {
                        Spacer(modifier = Modifier.size(ControlGeometry.swiperArrowSize))
                    }

                    // Right arrow
                    val showRightArrow = loop || currentContentIndex < itemCount - 1
                    if (showRightArrow) {
                        IconActionButton(
                            icon = Icons.caret_right,
                            contentDescription = I18n.strings.media.nextSlide,
                            onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                            size = ControlGeometry.swiperArrowSize,
                            iconSize = IconSizes.Default.lg,
                            background = colors.surface.copy(alpha = 0.7f),
                            tint = colors.foreground,
                        )
                    } else {
                        Spacer(modifier = Modifier.size(ControlGeometry.swiperArrowSize))
                    }
                }
            }

            // Inner bottom indicator
            if (indicatorPosition == SwiperIndicatorPosition.BOTTOM && navigation != SwiperNavigation.NONE) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = Spacing.md)
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

        // Outer bottom indicator
        if (indicatorPosition == SwiperIndicatorPosition.OUTSIDE_BOTTOM && navigation != SwiperNavigation.NONE) {
            Spacer(modifier = Modifier.height(Spacing.md))
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
 * SwiperIndicator - the indicator
 */
@Composable
private fun SwiperIndicator(
    currentIndex: Int,
    itemCount: Int,
    navigation: SwiperNavigation,
    isOuter: Boolean = false
) {
    val colors = Theme.colors

    // Colours: brand colour outside, white inside
    val activeColor = if (isOuter) colors.primary else colors.primaryForeground
    val inactiveColor = if (isOuter) colors.border else colors.primaryForeground.copy(alpha = 0.4f)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when (navigation) {
            SwiperNavigation.DOTS -> {
                // Dot indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier
                        .clip(Theme.shapes.full)
                        .background(
                            if (isOuter) colors.surface.copy(alpha = 0f)
                            else colors.surface.copy(alpha = 0.3f)
                        )
                        .padding(horizontal = Spacing.md, vertical = ControlGeometry.swiperIndicatorPaddingBlock)
                ) {
                    repeat(itemCount) { index ->
                        val isActive = index == currentIndex
                        // Animated size
                        val size by animateDpAsState(
                            targetValue = if (isActive) ControlGeometry.swiperDotActive else ControlGeometry.swiperDot,
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
                // Bar indicator (the selected dot stretches into a bar)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(ControlGeometry.swiperDotGap),
                    modifier = Modifier
                        .clip(Theme.shapes.full)
                        .background(
                            if (isOuter) colors.surface.copy(alpha = 0f)
                            else colors.surface.copy(alpha = 0.3f)
                        )
                        .padding(horizontal = Spacing.md, vertical = ControlGeometry.swiperIndicatorPaddingBlock)
                ) {
                    repeat(itemCount) { index ->
                        val isActive = index == currentIndex
                        // Animated width
                        val width by animateDpAsState(
                            targetValue = if (isActive) ControlGeometry.swiperBarActive else ControlGeometry.swiperDot,
                            animationSpec = tween(durationMillis = 200)
                        )
                        Box(
                            modifier = Modifier
                                .width(width)
                                .height(ControlGeometry.swiperDot)
                                .clip(Theme.shapes.sm)
                                .background(if (isActive) activeColor else inactiveColor)
                        )
                    }
                }
            }

            SwiperNavigation.FRACTION -> {
                // Fraction indicator
                Box(
                    modifier = Modifier
                        .clip(Theme.shapes.xl)
                        .background(
                            if (isOuter) colors.mutedForeground
                            else colors.surface.copy(alpha = 0.5f)
                        )
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                ) {
                    Text(
                        text = "${currentIndex + 1}/$itemCount",
                        style = Theme.typography.bodySmall,
                        color = if (isOuter) colors.primaryForeground else colors.primaryForeground
                    )
                }
            }

            SwiperNavigation.NONE -> {}
        }
    }
}

/**
 * SwiperState - externally controlled state
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
