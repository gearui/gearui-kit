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

/**
 * ImageViewerState - 图片预览器状态
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
 * 记住 ImageViewer 状态
 */
@Composable
fun rememberImageViewerState(
    initialIndex: Int = 0
): ImageViewerState {
    return remember { ImageViewerState(initialIndex) }
}

/**
 * ImageViewer - 图片预览组件
 *
 * 特性：
 * - 全屏图片预览
 * - 支持左右滑动切换
 * - 显示页码指示器
 * - 支持关闭按钮
 * - 支持删除按钮
 * - 支持图片标题/描述
 * - 点击关闭
 * - 支持长按回调
 *
 * @param images 图片列表（Painter 列表，null 显示占位符）
 * @param state 预览器状态
 * @param labels 图片标题列表（可选）
 * @param showIndex 是否显示页码
 * @param showCloseBtn 是否显示关闭按钮
 * @param showDeleteBtn 是否显示删除按钮
 * @param onClose 关闭回调
 * @param onDelete 删除回调
 * @param onIndexChange 页码变化回调
 * @param onLongPress 长按回调
 */
@Composable
fun ImageViewer(
    images: List<Painter?>,
    state: ImageViewerState,
    modifier: Modifier = Modifier,
    labels: List<String>? = null,
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

    val colors = Theme.colors
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = state.currentIndex.coerceIn(0, images.size - 1),
        pageCount = { images.size }
    )

    // 同步 pagerState 到 state
    LaunchedEffect(pagerState.currentPage) {
        state.currentIndex = pagerState.currentPage
        onIndexChange?.invoke(pagerState.currentPage)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // 图片内容区
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
                // 图片或占位符
                val painter = images.getOrNull(page)
                if (painter != null) {
                    GearImage(
                        painter = painter,
                        fit = ImageFit.CONTAIN,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // 占位符
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                name = Icons.image,
                                size = 36.dp,
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "图片 ${page + 1}",
                                style = Typography.TitleMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // 顶部导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 关闭按钮
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
                        size = 18.dp,
                        tint = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(32.dp))
            }

            // 标题和页码
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // 标题
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

                // 页码
                if (showIndex && images.size > 1) {
                    Text(
                        text = "${state.currentIndex + 1} / ${images.size}",
                        style = Typography.BodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // 删除按钮
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
                        size = 18.dp,
                        tint = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(32.dp))
            }
        }

        // 底部指示器（多张图片时显示）
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                images.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
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
 * ImageViewerDialog - 以对话框形式显示图片预览
 *
 * 用法：
 * ```kotlin
 * val viewerState = rememberImageViewerState()
 *
 * // 在某处触发显示
 * Button(onClick = { viewerState.show(0) }) { Text("查看图片") }
 *
 * // 渲染 Viewer
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

    // 缩略图网格
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
                            .clip(RoundedCornerShape(4.dp))
                            .background(colors.surfaceVariant)
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
                                color = colors.textSecondary
                            )
                        }

                        // 显示更多数量
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

    // 图片预览器
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
