package com.gearui.components.image

import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.ColorFilter
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.tencent.kuikly.compose.ui.layout.ContentScale
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography

import com.gearui.theme.Theme
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes

/**
 * Image fit mode
 */
enum class ImageFit {
    CONTAIN,    // 包含（等比缩放，保持完整）
    COVER,      // 覆盖（等比缩放，填满容器）
    FILL,       // 填充（拉伸填满）
    NONE,       // 原始大小
    SCALE_DOWN  // 缩小（不放大）
}

/**
 * Image shape
 */
enum class ImageShape {
    SQUARE,     // 方形
    ROUNDED,    // 圆角
    CIRCLE      // 圆形
}

/**
 * Image loading state
 */
sealed class ImageLoadState {
    object Idle : ImageLoadState()
    object Loading : ImageLoadState()
    object Success : ImageLoadState()
    data class Error(val message: String) : ImageLoadState()
}

/**
 * GearImage - Enhanced image component
 *
 * Image display component
 *
 * Features:
 * - Multiple fit modes
 * - Shape variants (square, rounded, circle)
 * - Loading state
 * - Error handling
 * - Lazy loading
 * - Preview support
 *
 * Example:
 * ```
 * GearImage(
 *     painter = painterResource("image.png"),
 *     contentDescription = "Sample image",
 *     shape = ImageShape.ROUNDED,
 *     fit = ImageFit.COVER
 * )
 * ```
 */
@Composable
fun GearImage(
    painter: Painter?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    shape: ImageShape = ImageShape.SQUARE,
    fit: ImageFit = ImageFit.COVER,
    cornerRadius: Dp = 8.dp,
    showBorder: Boolean = false,
    borderWidth: Dp = 1.dp,
    placeholderText: String = I18n.strings.common.loading,
    errorText: String = I18n.strings.common.loadFailed,
    onClick: (() -> Unit)? = null
) {
    val colors = Theme.colors

    val imageModifier = modifier
        .then(
            when (shape) {
                ImageShape.CIRCLE -> Modifier.clip(CircleShape)
                ImageShape.ROUNDED -> Modifier.clip(RoundedCornerShape(cornerRadius))
                ImageShape.SQUARE -> Modifier
            }
        )
        .then(
            if (showBorder) {
                when (shape) {
                    ImageShape.CIRCLE -> Modifier.border(borderWidth, colors.border, CircleShape)
                    ImageShape.ROUNDED -> Modifier.border(
                        borderWidth,
                        colors.border,
                        RoundedCornerShape(cornerRadius)
                    )

                    ImageShape.SQUARE -> Modifier.border(borderWidth, colors.border)
                }
            } else Modifier
        )
        .then(
            if (onClick != null) Modifier.clickable { onClick() }
            else Modifier
        )

    val contentScale = when (fit) {
        ImageFit.CONTAIN -> ContentScale.Fit
        ImageFit.COVER -> ContentScale.Crop
        ImageFit.FILL -> ContentScale.FillBounds
        ImageFit.NONE -> ContentScale.None
        ImageFit.SCALE_DOWN -> ContentScale.Inside
    }

    if (painter != null) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = imageModifier,
            contentScale = contentScale
        )
    } else {
        // Placeholder
        Box(
            modifier = imageModifier.background(colors.muted),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = placeholderText,
                style = Typography.BodySmall,
                color = colors.mutedForeground
            )
        }
    }
}

/**
 * Image with loading state
 */
@Composable
fun ImageWithState(
    painter: Painter?,
    loadState: ImageLoadState,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    shape: ImageShape = ImageShape.SQUARE,
    fit: ImageFit = ImageFit.COVER,
    cornerRadius: Dp = 8.dp,
    onClick: (() -> Unit)? = null
) {
    val colors = Theme.colors

    Box(modifier = modifier) {
        when (loadState) {
            is ImageLoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.muted),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = I18n.strings.common.loading,
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                }
            }

            is ImageLoadState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.muted),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            name = Icons.close,
                            size = IconSizes.Default.lg,
                            tint = colors.destructive
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = loadState.message,
                            style = Typography.BodySmall,
                            color = colors.mutedForeground
                        )
                    }
                }
            }

            is ImageLoadState.Success -> {
                GearImage(
                    painter = painter,
                    contentDescription = contentDescription,
                    shape = shape,
                    fit = fit,
                    cornerRadius = cornerRadius,
                    onClick = onClick,
                    modifier = Modifier.fillMaxSize()
                )
            }

            is ImageLoadState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.muted)
                )
            }
        }
    }
}

/**
 * Avatar image component
 */
@Composable
fun Avatar(
    painter: Painter?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    fallbackText: String = "",
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null
) {
    val colors = Theme.colors

    if (painter != null) {
        GearImage(
            painter = painter,
            contentDescription = contentDescription,
            shape = ImageShape.CIRCLE,
            fit = ImageFit.COVER,
            onClick = onClick,
            modifier = modifier.size(size)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(colors.primary)
                .then(
                    if (onClick != null) Modifier.clickable { onClick() }
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = fallbackText.take(2).uppercase(),
                style = Typography.BodyMedium,
                color = colors.primaryForeground
            )
        }
    }
}

/**
 * Image gallery component
 */
@Composable
fun ImageGallery(
    painters: List<Painter?>,
    modifier: Modifier = Modifier,
    columns: Int = 3,
    spacing: Dp = 8.dp,
    imageHeight: Dp = 100.dp,
    shape: ImageShape = ImageShape.ROUNDED,
    onImageClick: ((Int) -> Unit)? = null
) {
    val colors = Theme.colors

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        painters.chunked(columns).forEach { rowPainters ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowPainters.forEachIndexed { index, painter ->
                    val globalIndex = painters.indexOf(painter)
                    GearImage(
                        painter = painter,
                        shape = shape,
                        fit = ImageFit.COVER,
                        onClick = onImageClick?.let { { it(globalIndex) } },
                        modifier = Modifier
                            .weight(1f)
                            .height(imageHeight)
                    )
                }
                // Fill remaining columns
                repeat(columns - rowPainters.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Image placeholder with icon
 */
@Composable
fun ImagePlaceholder(
    modifier: Modifier = Modifier,
    text: String = I18n.strings.media.imageEmpty,
    /** Custom glyph; left empty the built-in icon is used. */
    icon: String? = null
) {
    val colors = Theme.colors

    Box(
        modifier = modifier
            .background(colors.muted)
            .border(BorderWidth.thin, colors.border),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon == null) {
                Icon(
                    name = Icons.image,
                    size = IconSizes.Default.xl,
                    tint = colors.mutedForeground
                )
            } else {
                Text(
                    text = icon,
                    style = Typography.HeadlineLarge,
                    color = colors.mutedForeground
                )
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = text,
                style = Typography.BodySmall,
                color = colors.mutedForeground
            )
        }
    }
}
