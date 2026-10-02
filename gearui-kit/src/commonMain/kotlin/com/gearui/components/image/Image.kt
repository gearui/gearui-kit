package com.gearui.components.image

import com.gearui.components.icon.*
import com.gearui.foundation.avatar.AvatarSizeTokens
import com.gearui.foundation.layout.Radius
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.interaction.pressScale
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
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
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text

import com.gearui.theme.Theme
import com.gearui.i18n.I18n
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.typography.IconSizes

/**
 * Image fit mode
 */
enum class ImageFit {
    CONTAIN, // scale to fit, keeping the whole image
    COVER, // scale to fill, cropping the overflow
    FILL, // stretch to fill
    NONE, // original size
    SCALE_DOWN // scale down only, never up
}

/**
 * Image shape
 */
enum class ImageShape {
    SQUARE,
    ROUNDED,
    CIRCLE
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
    cornerRadius: Dp = Radius.sm,
    showBorder: Boolean = false,
    borderWidth: Dp = BorderWidth.thin,
    placeholderText: String = I18n.strings.common.loading,
    errorText: String = I18n.strings.common.loadFailed,
    onClick: (() -> Unit)? = null
) {
    val colors = Theme.colors
    val pressInteraction = remember { MutableInteractionSource() }

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
            if (onClick != null) {
                Modifier
                    .pressScale(pressInteraction)
                    .clickable(interactionSource = pressInteraction, indication = null) { onClick() }
            } else Modifier
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
                style = Theme.typography.bodySmall,
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
    cornerRadius: Dp = Radius.sm,
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
                        style = Theme.typography.bodySmall,
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
                        Icon(Icons.x,
                            size = IconSizes.Default.lg,
                            tint = colors.destructiveSoftForeground
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = loadState.message,
                            style = Theme.typography.bodySmall,
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
 * Image gallery component
 */
@Composable
fun ImageGallery(
    painters: List<Painter?>,
    modifier: Modifier = Modifier,
    columns: Int = 3,
    spacing: Dp = Spacing.sm,
    imageHeight: Dp = ControlGeometry.imageGalleryHeight,
    shape: ImageShape = ImageShape.ROUNDED,
    onImageClick: ((Int) -> Unit)? = null
) {
    val colors = Theme.colors

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        painters.chunked(columns).forEachIndexed { rowIndex, rowPainters ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowPainters.forEachIndexed { index, painter ->
                    // Position, not identity: indexOf picked the first match, so every
                    // repeated painter (all nulls while loading) reported index 0.
                    val globalIndex = rowIndex * columns + index
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
    /** An [Icons] name; the image icon when left empty. */
    icon: IconSource? = null
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
            Icon(icon ?: Icons.image,
                size = IconSizes.Default.xl,
                tint = colors.mutedForeground
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = text,
                style = Theme.typography.bodySmall,
                color = colors.mutedForeground
            )
        }
    }
}
