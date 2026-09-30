package com.gearui.components.upload

import androidx.compose.runtime.Composable
import com.gearui.components.closebutton.CloseButton
import com.gearui.components.icon.Icons
import com.gearui.components.loading.Loading
import com.gearui.components.loading.LoadingLayout
import com.gearui.components.loading.LoadingSize
import com.gearui.components.progress.LinearProgress
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.interaction.PressableFeedback
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.i18n.I18n
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.semantics
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.ExperimentalLayoutApi
import com.tencent.kuikly.compose.foundation.layout.FlowRow
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.tencent.kuikly.compose.ui.unit.Dp

/** Where an attachment is in its upload. */
enum class UploadStatus { PENDING, UPLOADING, DONE, FAILED }

/**
 * One attachment tile. [thumbnail] is a painter the caller loads (a local file, a
 * remote URL); a null thumbnail falls back to a file icon with the name underneath.
 */
data class UploadItem(
    val id: String,
    val name: String = "",
    val thumbnail: Painter? = null,
    val status: UploadStatus = UploadStatus.DONE,
    /** 0..1 while [UploadStatus.UPLOADING]; ignored otherwise. */
    val progress: Float = 0f,
)

/**
 * Upload — the tiles for a set of attachments: thumbnails, progress, retry and remove,
 * plus the tile that asks for one more.
 *
 * It deliberately owns no platform access. Picking a photo, taking one and the transfer
 * itself belong to the host app, which already has the permissions, the pickers and the
 * transport; a UI library that reached for the camera would drag those into every app
 * that only wanted the layout. So the component renders [items] and reports intent
 * through [onAdd], [onRemove] and [onRetry], and the caller keeps the list.
 *
 * ```kotlin
 * Upload(
 *     items = attachments,
 *     onAdd = { scope.launch { attachments += pickImage() } },
 *     onRemove = { attachments -= it },
 *     onRetry = { retry(it) },
 * )
 * ```
 *
 * The add tile disappears at [maxCount], so there is nothing to tap that would be
 * rejected afterwards.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Upload(
    items: List<UploadItem>,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
    onRemove: ((UploadItem) -> Unit)? = null,
    onRetry: ((UploadItem) -> Unit)? = null,
    onPreview: ((UploadItem) -> Unit)? = null,
    maxCount: Int = 9,
    enabled: Boolean = true,
    tileSize: Dp = ControlGeometry.uploadTileSize,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth().alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity),
        horizontalArrangement = Arrangement.spacedBy(ControlGeometry.uploadTileGap),
        verticalArrangement = Arrangement.spacedBy(ControlGeometry.uploadTileGap),
    ) {
        items.forEach { item ->
            UploadTile(
                item = item,
                size = tileSize,
                enabled = enabled,
                onRemove = onRemove,
                onRetry = onRetry,
                onPreview = onPreview,
            )
        }
        if (items.size < maxCount) {
            AddTile(size = tileSize, enabled = enabled, onAdd = onAdd)
        }
    }
}

@Composable
private fun UploadTile(
    item: UploadItem,
    size: Dp,
    enabled: Boolean,
    onRemove: ((UploadItem) -> Unit)?,
    onRetry: ((UploadItem) -> Unit)?,
    onPreview: ((UploadItem) -> Unit)?,
) {
    val colors = Theme.colors
    val shape = Theme.shapes.lg
    val failed = item.status == UploadStatus.FAILED
    val tap: (() -> Unit)? = when {
        !enabled -> null
        failed && onRetry != null -> ({ onRetry(item) })
        onPreview != null -> ({ onPreview(item) })
        else -> null
    }

    Box(modifier = Modifier.size(size)) {
        val body: @Composable () -> Unit = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(colors.muted)
                    .then(if (failed) Modifier.border(BorderWidth.thin, colors.destructive, shape) else Modifier),
                contentAlignment = Alignment.Center,
            ) {
                if (item.thumbnail != null) {
                    com.gearui.components.image.GearImage(
                        painter = item.thumbnail,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = item.name,
                    )
                } else {
                    // A file tile states its status in place of the paperclip: a scrim over
                    // the name made it unreadable, most of all in the dark theme.
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(ControlGeometry.dialogTextGap),
                        modifier = Modifier.padding(horizontal = ControlGeometry.uploadTileGap),
                    ) {
                        when (item.status) {
                            UploadStatus.PENDING -> Loading(size = LoadingSize.SMALL, layout = LoadingLayout.HORIZONTAL, color = colors.mutedForeground)
                            UploadStatus.FAILED -> Icon(name = Icons.arrow_clockwise, size = ControlGeometry.alertIcon, tint = colors.destructive)
                            else -> Icon(name = Icons.paperclip, size = ControlGeometry.alertIcon, tint = colors.mutedForeground)
                        }
                        if (item.name.isNotEmpty()) {
                            Text(
                                text = item.name,
                                style = Theme.typography.bodyExtraSmall,
                                color = if (failed) colors.destructive else colors.mutedForeground,
                                maxLines = 2,
                            )
                        }
                        if (item.status == UploadStatus.UPLOADING) {
                            LinearProgress(
                                progress = item.progress.coerceIn(0f, 1f),
                                modifier = Modifier.fillMaxWidth(),
                                showLabel = false,
                            )
                        }
                    }
                }
                // A picture keeps its status on a scrim, so progress reads over any image.
                if (item.thumbnail != null) {
                    when (item.status) {
                        UploadStatus.UPLOADING -> UploadingOverlay(item.progress)
                        UploadStatus.PENDING -> StatusOverlay { Loading(size = LoadingSize.SMALL, layout = LoadingLayout.HORIZONTAL, color = Color.White) }
                        UploadStatus.FAILED -> StatusOverlay {
                            Icon(name = Icons.arrow_clockwise, size = ControlGeometry.alertIcon, tint = Color.White)
                        }
                        UploadStatus.DONE -> Unit
                    }
                }
            }
        }
        if (tap != null) {
            // A tile that shows its name needs no description: the name is already read
            // from the text, and adding it here made every tile announce it twice.
            val label = when {
                failed -> I18n.strings.common.retry
                item.thumbnail != null -> item.name
                else -> null
            }
            PressableFeedback(
                onClick = tap,
                shape = shape,
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (label != null) Modifier.semantics { contentDescription = label } else Modifier),
            ) { body() }
        } else {
            body()
        }
        if (onRemove != null && enabled) {
            CloseButton(
                onClick = { onRemove(item) },
                contentDescription = I18n.strings.common.remove,
                modifier = Modifier.align(Alignment.TopEnd),
                size = ControlGeometry.uploadRemoveSize,
                iconSize = ControlGeometry.uploadRemoveIcon,
                containerColor = colors.foreground.copy(alpha = FeedbackDefaults.disabledOpacity),
                iconColor = colors.background,
            )
        }
    }
}

/** Progress reads on top of the thumbnail, so a tile in flight is never mistaken for a finished one. */
@Composable
private fun UploadingOverlay(progress: Float) {
    StatusOverlay {
        LinearProgress(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth().padding(horizontal = ControlGeometry.uploadTileGap),
            // A tile is too small for a number next to the bar; the bar is the status.
            showLabel = false,
        )
    }
}

@Composable
private fun StatusOverlay(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = FeedbackDefaults.disabledOpacity)),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun AddTile(size: Dp, enabled: Boolean, onAdd: () -> Unit) {
    val colors = Theme.colors
    val shape = Theme.shapes.lg
    val label = I18n.strings.common.add
    PressableFeedback(
        onClick = onAdd,
        modifier = Modifier.size(size).semantics { contentDescription = label },
        enabled = enabled,
        shape = shape,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(colors.muted)
                .border(BorderWidth.thin, colors.separator, shape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(name = Icons.plus, size = ControlGeometry.alertIcon, tint = colors.mutedForeground)
        }
    }
}
