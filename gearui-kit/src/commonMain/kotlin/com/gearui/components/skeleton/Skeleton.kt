package com.gearui.components.skeleton

import com.tencent.kuikly.compose.animation.core.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Brush
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.typography.Typography

import com.gearui.foundation.primitives.Text

import com.gearui.theme.Theme
import com.gearui.Spacing

/**
 * Skeleton animation type
 */
enum class SkeletonAnimation {
    PULSE,      // 脉冲动画
    WAVE,       // 波浪动画
    NONE        // 无动画
}

/**
 * Skeleton variant
 */
enum class SkeletonVariant {
    TEXT,       // 文本骨架
    CIRCULAR,   // 圆形骨架
    RECTANGULAR // 矩形骨架
}

/**
 * Skeleton - Loading placeholder component
 *
 * 骨架屏组件
 *
 * Features:
 * - Multiple animation types (pulse, wave)
 * - Multiple variants (text, circular, rectangular)
 * - Customizable size and shape
 * - Pre-built templates (article, list, card)
 *
 * Example:
 * ```
 * Skeleton(
 *     variant = SkeletonVariant.TEXT,
 *     animation = SkeletonAnimation.PULSE,
 *     modifier = Modifier.fillMaxWidth().height(20.dp)
 * )
 * ```
 */
@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    variant: SkeletonVariant = SkeletonVariant.RECTANGULAR,
    animation: SkeletonAnimation = SkeletonAnimation.PULSE,
    cornerRadius: Dp = Spacing.spacer4.dp
) {
    val colors = Theme.colors

    val baseColor = colors.surfaceVariant
    val highlightColor = colors.surface

    val infiniteTransition = rememberInfiniteTransition()

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val backgroundColor = when (animation) {
        SkeletonAnimation.PULSE -> baseColor.copy(alpha = alpha)
        SkeletonAnimation.WAVE -> baseColor
        SkeletonAnimation.NONE -> baseColor
    }

    Box(
        modifier = modifier
            .then(
                when (variant) {
                    SkeletonVariant.CIRCULAR -> Modifier.clip(CircleShape)
                    SkeletonVariant.RECTANGULAR, SkeletonVariant.TEXT ->
                        Modifier.clip(RoundedCornerShape(cornerRadius))
                }
            )
            .background(
                if (animation == SkeletonAnimation.WAVE) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            baseColor,
                            highlightColor,
                            baseColor
                        ),
                        startX = shimmerTranslate - 500f,
                        endX = shimmerTranslate + 500f
                    )
                } else {
                    Brush.linearGradient(listOf(backgroundColor, backgroundColor))
                }
            )
    )
}

/**
 * Skeleton text line
 */
@Composable
fun SkeletonText(
    modifier: Modifier = Modifier,
    lines: Int = 1,
    lineHeight: Dp = Spacing.spacer16.dp,
    lineSpacing: Dp = Spacing.spacer8.dp,
    animation: SkeletonAnimation = SkeletonAnimation.PULSE,
    lastLineWidth: Float = 0.6f
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(lineSpacing)
    ) {
        repeat(lines) { index ->
            Skeleton(
                variant = SkeletonVariant.TEXT,
                animation = animation,
                modifier = Modifier
                    .fillMaxWidth(if (index == lines - 1) lastLineWidth else 1f)
                    .height(lineHeight)
            )
        }
    }
}

/**
 * Skeleton avatar
 */
@Composable
fun SkeletonAvatar(
    modifier: Modifier = Modifier,
    size: Dp = Spacing.spacer40.dp,
    animation: SkeletonAnimation = SkeletonAnimation.PULSE
) {
    Skeleton(
        variant = SkeletonVariant.CIRCULAR,
        animation = animation,
        modifier = modifier.size(size)
    )
}

/**
 * Skeleton image
 */
@Composable
fun SkeletonImage(
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 100.dp,
    cornerRadius: Dp = Spacing.spacer8.dp,
    animation: SkeletonAnimation = SkeletonAnimation.PULSE
) {
    Skeleton(
        variant = SkeletonVariant.RECTANGULAR,
        animation = animation,
        cornerRadius = cornerRadius,
        modifier = modifier.size(width, height)
    )
}

/**
 * Skeleton article template
 */
@Composable
fun SkeletonArticle(
    modifier: Modifier = Modifier,
    animation: SkeletonAnimation = SkeletonAnimation.PULSE,
    showImage: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.spacer16.dp)
    ) {
        // Title
        SkeletonText(
            lines = 1,
            lineHeight = Spacing.spacer24.dp,
            animation = animation,
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        // Image
        if (showImage) {
            SkeletonImage(
                width = Dp.Infinity,
                height = 200.dp,
                animation = animation,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Content
        SkeletonText(
            lines = 4,
            lineHeight = Spacing.spacer16.dp,
            animation = animation
        )
    }
}

/**
 * Skeleton list item template
 */
@Composable
fun SkeletonListItem(
    modifier: Modifier = Modifier,
    animation: SkeletonAnimation = SkeletonAnimation.PULSE,
    showAvatar: Boolean = true,
    showThumbnail: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.spacer12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        if (showAvatar) {
            SkeletonAvatar(
                size = Spacing.spacer48.dp,
                animation = animation
            )
        }

        // Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
        ) {
            SkeletonText(
                lines = 1,
                lineHeight = Spacing.spacer16.dp,
                animation = animation,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            SkeletonText(
                lines = 1,
                lineHeight = 14.dp,
                animation = animation,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }

        // Thumbnail
        if (showThumbnail) {
            SkeletonImage(
                width = 60.dp,
                height = 60.dp,
                animation = animation
            )
        }
    }
}

/**
 * Skeleton card template
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    animation: SkeletonAnimation = SkeletonAnimation.PULSE,
    imageHeight: Dp = 150.dp
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.spacer12.dp)
    ) {
        // Image
        SkeletonImage(
            width = Dp.Infinity,
            height = imageHeight,
            animation = animation,
            modifier = Modifier.fillMaxWidth()
        )

        // Content
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp),
            modifier = Modifier.padding(horizontal = Spacing.spacer12.dp)
        ) {
            SkeletonText(
                lines = 1,
                lineHeight = 18.dp,
                animation = animation,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            SkeletonText(
                lines = 2,
                lineHeight = 14.dp,
                animation = animation
            )

            Spacer(modifier = Modifier.height(Spacing.spacer4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.spacer8.dp)
            ) {
                Skeleton(
                    animation = animation,
                    modifier = Modifier.size(80.dp, Spacing.spacer32.dp)
                )
                Skeleton(
                    animation = animation,
                    modifier = Modifier.size(80.dp, Spacing.spacer32.dp)
                )
            }
        }
    }
}

/**
 * Skeleton grid template
 */
@Composable
fun SkeletonGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    rows: Int = 2,
    itemHeight: Dp = 120.dp,
    spacing: Dp = Spacing.spacer12.dp,
    animation: SkeletonAnimation = SkeletonAnimation.PULSE
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(columns) {
                    Skeleton(
                        animation = animation,
                        modifier = Modifier
                            .weight(1f)
                            .height(itemHeight)
                    )
                }
            }
        }
    }
}
