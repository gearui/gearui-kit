package com.gearui.components.imageviewer

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
import com.tencent.kuikly.compose.foundation.gestures.detectTransformGestures
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.pager.HorizontalPager
import com.tencent.kuikly.compose.foundation.pager.rememberPagerState
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.graphicsLayer
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.image.GearImage
import com.gearui.components.image.ImageFit
import com.gearui.components.image.ImageShape
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import kotlinx.coroutines.launch
import com.gearui.i18n.I18n
import com.gearui.i18n.formatArgs
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.typography.IconSizes

/**
 * ImageViewerState - image viewer state
 */
@Stable
class ImageViewerState(
    initialIndex: Int = 0
) {
    var currentIndex by mutableStateOf(initialIndex)
        internal set

    var isVisible by mutableStateOf(false)
        internal set

    fun show(index: Int = 0) {
        currentIndex = index
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }
}

/**
 * Remembers an ImageViewer state
 */
@Composable
fun rememberImageViewerState(
    initialIndex: Int = 0
): ImageViewerState {
    return remember { ImageViewerState(initialIndex) }
}

/**
 * ImageViewer - fullscreen image preview
 *
 * Features:
 * - fullscreen preview
 * - swipe between images
 * - page indicator
 * - optional close button
 * - optional delete button
 * - optional caption
 * - tap to dismiss
 * - long-press callback
 *
 * @param images image list as Painters; null renders a placeholder
 * @param state viewer state
 * @param labels optional captions
 * @param showIndex whether to show the page number
 * @param showCloseBtn whether to show the close button
 * @param showDeleteBtn whether to show the delete button
 * @param onClose close callback
 * @param onDelete delete callback
 * @param onIndexChange page change callback
 * @param onLongPress long-press callback
 */
@Composable
fun ImageViewer(
    images: List<Painter?>,
    state: ImageViewerState,
    modifier: Modifier = Modifier,
    labels: List<String>? = null,
    width: Dp? = null,
    height: Dp? = null,
    showIndex: Boolean = true,
    showCloseBtn: Boolean = true,
    showDeleteBtn: Boolean = false,
    backgroundColor: Color = Color.Black.copy(alpha = 0.9f),
    onClose: ((Int) -> Unit)? = null,
    onDelete: ((Int) -> Unit)? = null,
    onIndexChange: ((Int) -> Unit)? = null,
    onLongPress: ((Int) -> Unit)? = null,
    onTap: ((Int) -> Unit)? = null
) {
    if (!state.isVisible || images.isEmpty()) return
    require(labels == null || labels.size == images.size) {
        "labels.size must equal images.size"
    }

    val colors = Theme.colors
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = state.currentIndex.coerceIn(0, images.size - 1),
        pageCount = { images.size }
    )

    // Keep pagerState and state in sync
    LaunchedEffect(pagerState.currentPage) {
        state.currentIndex = pagerState.currentPage
        onIndexChange?.invoke(pagerState.currentPage)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Image area
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onTap?.invoke(state.currentIndex) ?: state.hide()
                        },
                        onLongPress = {
                            onLongPress?.invoke(state.currentIndex)
                        }
                    )
                }
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Image or placeholder
                val painter = images.getOrNull(page)
                if (painter != null) {
                    val imageModifier = when {
                        width != null && height != null -> Modifier.size(width, height)
                        width != null -> Modifier.width(width).fillMaxHeight()
                        height != null -> Modifier.fillMaxWidth().height(height)
                        else -> Modifier.fillMaxSize()
                    }
                    GearImage(
                        painter = painter,
                        fit = ImageFit.CONTAIN,
                        modifier = imageModifier
                    )
                } else {
                    // Placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(Spacing.huge),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                name = Icons.image,
                                size = IconSizes.Display.md,
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(Spacing.lg))
                            Text(
                                text = I18n.strings.media.imageIndexFormat.formatArgs("index" to (page + 1)),
                                style = Typography.TitleMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close button
            if (showCloseBtn) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable {
                            onClose?.invoke(state.currentIndex) ?: state.hide()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        name = Icons.close,
                        size = IconSizes.Default.lg,
                        tint = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(Spacing.xxl))
            }

            // Caption and page number
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Caption
                labels?.getOrNull(state.currentIndex)?.let { label ->
                    if (label.isNotEmpty()) {
                        Text(
                            text = label,
                            style = Typography.TitleSmall,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }

                // Page number
                if (showIndex && images.size > 1) {
                    Text(
                        text = "${state.currentIndex + 1} / ${images.size}",
                        style = Typography.BodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Delete button
            if (showDeleteBtn) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable {
                            onDelete?.invoke(state.currentIndex)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        name = Icons.delete,
                        size = IconSizes.Default.lg,
                        tint = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(Spacing.xxl))
            }
        }

        // Bottom indicator, shown when there is more than one image
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Spacing.xxl),
                horizontalArrangement = Arrangement.Center
            ) {
                images.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = Spacing.xs)
                            .size(if (index == state.currentIndex) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == state.currentIndex)
                                    Color.White
                                else
                                    Color.White.copy(alpha = 0.4f)
                            )
                    )
                }
            }
        }
    }
}

/**
 * ImageViewerDialog - image preview presented as a dialog
 *
 * Usage:
 * ```kotlin
 * val viewerState = rememberImageViewerState()
 *
 * // trigger it from somewhere
 * Button(onClick = { viewerState.show(0) }) { Text("View images") }
 *
 * // render the viewer
 * if (viewerState.isVisible) {
 *     ImageViewer(
 *         images = imageList,
 *         state = viewerState
 *     )
 * }
 * ```
 */
@Composable
fun ImageViewerTrigger(
    images: List<Painter?>,
    modifier: Modifier = Modifier,
    initialIndex: Int = 0,
    thumbnailSize: Int = 72,
    spacing: Int = 8,
    maxDisplay: Int = 9,
    labels: List<String>? = null,
    showIndex: Boolean = true,
    showDeleteBtn: Boolean = false,
    onDelete: ((Int) -> Unit)? = null
) {
    val colors = Theme.colors
    val state = rememberImageViewerState(initialIndex)

    // Thumbnail grid
    val displayImages = images.take(maxDisplay)
    val columns = when {
        displayImages.size <= 1 -> 1
        displayImages.size <= 4 -> 2
        else -> 3
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.dp)
    ) {
        displayImages.chunked(columns).forEachIndexed { rowIndex, rowImages ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.dp)
            ) {
                rowImages.forEachIndexed { colIndex, painter ->
                    val index = rowIndex * columns + colIndex
                    Box(
                        modifier = Modifier
                            .size(thumbnailSize.dp)
                            .clip(Theme.shapes.sm)
                            .background(colors.muted)
                            .clickable { state.show(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (painter != null) {
                            GearImage(
                                painter = painter,
                                fit = ImageFit.COVER,
                                shape = ImageShape.ROUNDED,
                                cornerRadius = 4.dp,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                style = Typography.BodyMedium,
                                color = colors.mutedForeground
                            )
                        }

                        // "more" count
                        if (index == maxDisplay - 1 && images.size > maxDisplay) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${images.size - maxDisplay}",
                                    style = Typography.TitleSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // The image viewer
    if (state.isVisible) {
        ImageViewer(
            images = images,
            state = state,
            labels = labels,
            showIndex = showIndex,
            showDeleteBtn = showDeleteBtn,
            onDelete = onDelete
        )
    }
}
