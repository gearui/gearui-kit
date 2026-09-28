package com.gearui.sample.examples.skeleton

import androidx.compose.runtime.Composable
import com.gearui.components.skeleton.Skeleton
import com.gearui.components.skeleton.SkeletonAnimation
import com.gearui.components.skeleton.SkeletonArticle
import com.gearui.components.skeleton.SkeletonAvatar
import com.gearui.components.skeleton.SkeletonCard
import com.gearui.components.skeleton.SkeletonGrid
import com.gearui.components.skeleton.SkeletonImage
import com.gearui.components.skeleton.SkeletonListItem
import com.gearui.components.skeleton.SkeletonText
import com.gearui.components.skeleton.SkeletonVariant
import com.gearui.foundation.avatar.AvatarSizeTokens
import com.gearui.foundation.layout.Radius
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

// Demo dimensions: the placeholder blocks being shown off.
private val BlockHeight = 40.dp
private val TextLineHeight = 16.dp
private val ImageBlockSize = 80.dp
private val CardImageHeight = 120.dp
private val GridItemHeight = 80.dp

/** A muted caption above an example. */
@Composable
private fun Caption(text: String) {
    Text(
        text = text,
        style = Theme.typography.bodySmall,
        color = Theme.colors.mutedForeground
    )
}

/**
 * Skeleton component examples
 */
@Composable
fun SkeletonExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        ExampleSection(
            title = "形状",
            description = "variant 可选矩形、圆形、文本"
        ) {
            Caption("矩形 RECTANGULAR")
            Skeleton(
                variant = SkeletonVariant.RECTANGULAR,
                modifier = Modifier.fillMaxWidth().height(BlockHeight)
            )
            Caption("圆形 CIRCULAR")
            Skeleton(
                variant = SkeletonVariant.CIRCULAR,
                modifier = Modifier.size(BlockHeight)
            )
            Caption("文本 TEXT")
            Skeleton(
                variant = SkeletonVariant.TEXT,
                modifier = Modifier.fillMaxWidth().height(TextLineHeight)
            )
        }

        ExampleSection(
            title = "动画",
            description = "脉冲 PULSE、波浪 WAVE、无动画 NONE"
        ) {
            listOf(
                "脉冲 PULSE" to SkeletonAnimation.PULSE,
                "波浪 WAVE" to SkeletonAnimation.WAVE,
                "无动画 NONE" to SkeletonAnimation.NONE
            ).forEach { (label, animation) ->
                Caption(label)
                Skeleton(
                    animation = animation,
                    modifier = Modifier.fillMaxWidth().height(BlockHeight)
                )
            }
        }

        ExampleSection(
            title = "多行文本",
            description = "SkeletonText 的行数、行距与末行宽度可调"
        ) {
            SkeletonText(
                lines = 3,
                lineHeight = TextLineHeight,
                lineSpacing = Spacing.sm,
                lastLineWidth = 0.7f
            )
        }

        ExampleSection(
            title = "头像与图片",
            description = "SkeletonAvatar 与 SkeletonImage"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonAvatar(size = AvatarSizeTokens.Small.size)
                SkeletonAvatar(size = AvatarSizeTokens.Medium.size)
                SkeletonAvatar(size = AvatarSizeTokens.Large.size)
                SkeletonImage(
                    width = ImageBlockSize,
                    height = ImageBlockSize,
                    cornerRadius = Radius.lg
                )
            }
        }

        ExampleSection(
            title = "列表项模板",
            description = "SkeletonListItem 可带头像与缩略图"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                SkeletonListItem(showAvatar = true, showThumbnail = false)
                SkeletonListItem(showAvatar = true, showThumbnail = true)
                SkeletonListItem(showAvatar = false, showThumbnail = false)
            }
        }

        ExampleSection(
            title = "卡片模板",
            description = "SkeletonCard 占住图片与文字区域"
        ) {
            SkeletonCard(imageHeight = CardImageHeight)
        }

        ExampleSection(
            title = "文章模板",
            description = "SkeletonArticle 包含标题、配图与段落"
        ) {
            SkeletonArticle(showImage = true)
        }

        ExampleSection(
            title = "网格模板",
            description = "SkeletonGrid 按行列排出等高格子"
        ) {
            SkeletonGrid(
                columns = 3,
                rows = 2,
                itemHeight = GridItemHeight,
                spacing = Spacing.sm
            )
        }
    }
}
