package com.gearui.components.skeleton

import com.gearui.foundation.control.ControlGeometry
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
import com.gearui.foundation.typography.Typography

import com.gearui.foundation.primitives.Text

import com.gearui.theme.Theme
import com.gearui.foundation.layout.Radius
import com.gearui.foundation.layout.Spacing

/**
 * Skeleton animation type
 */
enum class SkeletonAnimation {
    PULSE,
    WAVE,
    NONE
}

/**
 * Skeleton variant
 */
enum class SkeletonVariant {
    TEXT,
    CIRCULAR,
    RECTANGULAR
}

/**
 * Skeleton - Loading placeholder component
 *
 * Skeleton component
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
 *     modifier = Modifier.fillMaxWidth().height(Spacing.lg)
 * )
 * ```
 */
@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    variant: SkeletonVariant = SkeletonVariant.RECTANGULAR,
    animation: SkeletonAnimation = SkeletonAnimation.WAVE,
    cornerRadius: Dp = Radius.sm
) {
    val colors = Theme.colors

    // Reference `.skeleton__root`: the muted text colour at 30%, shimmering by default
    // (skeleton.constants.ts: shimmer 1500ms, pulse 1000ms, both linear).
    val baseColor = colors.mutedForeground.copy(alpha = 0.3f)
    val highlightColor = colors.mutedForeground.copy(alpha = 0.12f)

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
            animation = tween(durationMillis = 1500, easing = LinearEasing),
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
    lineHeight: Dp = Spacing.lg,
    lineSpacing: Dp = Spacing.sm,
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
    size: Dp = Spacing.xxxl,
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
    width: Dp = ControlGeometry.skeletonBlock,
    height: Dp = ControlGeometry.skeletonBlock,
    cornerRadius: Dp = Radius.sm,
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
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Title
        SkeletonText(
            lines = 1,
            lineHeight = Spacing.xl,
            animation = animation,
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        // Image
        if (showImage) {
            SkeletonImage(
                width = Dp.Infinity,
                height = ControlGeometry.skeletonCardImage,
                animation = animation,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Content
        SkeletonText(
            lines = 4,
            lineHeight = Spacing.lg,
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
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        if (showAvatar) {
            SkeletonAvatar(
                size = Spacing.huge,
                animation = animation
            )
        }

        // Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            SkeletonText(
                lines = 1,
                lineHeight = Spacing.lg,
                animation = animation,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            SkeletonText(
                lines = 1,
                lineHeight = ControlGeometry.skeletonLine,
                animation = animation,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }

        // Thumbnail
        if (showThumbnail) {
            SkeletonImage(
                width = ControlGeometry.skeletonAvatar,
                height = ControlGeometry.skeletonAvatar,
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
    imageHeight: Dp = ControlGeometry.skeletonArticleImage
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
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
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.padding(horizontal = Spacing.md)
        ) {
            SkeletonText(
                lines = 1,
                lineHeight = ControlGeometry.skeletonTitleLine,
                animation = animation,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            SkeletonText(
                lines = 2,
                lineHeight = ControlGeometry.skeletonLine,
                animation = animation
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Skeleton(
                    animation = animation,
                    modifier = Modifier.size(ControlGeometry.skeletonButtonWidth, Spacing.xxl)
                )
                Skeleton(
                    animation = animation,
                    modifier = Modifier.size(ControlGeometry.skeletonButtonWidth, Spacing.xxl)
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
    itemHeight: Dp = ControlGeometry.skeletonListItemHeight,
    spacing: Dp = Spacing.md,
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
