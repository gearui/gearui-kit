package com.gearui.sample.examples.skeleton

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
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
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Skeleton component examples
 */
@Composable
fun SkeletonExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // Basic skeletons
        ExampleSection(
            title = "基础用法",
            description = "不同形状的骨架屏"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Rectangle
                Text(
                    text = "矩形骨架",
                    style = Typography.BodySmall,
                    color = colors.mutedForeground
                )
                Skeleton(
                    variant = SkeletonVariant.RECTANGULAR,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )

                // Circle
                Text(
                    text = "圆形骨架",
                    style = Typography.BodySmall,
                    color = colors.mutedForeground
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Skeleton(
                        variant = SkeletonVariant.CIRCULAR,
                        modifier = Modifier.size(40.dp)
                    )
                    Skeleton(
                        variant = SkeletonVariant.CIRCULAR,
                        modifier = Modifier.size(48.dp)
                    )
                    Skeleton(
                        variant = SkeletonVariant.CIRCULAR,
                        modifier = Modifier.size(56.dp)
                    )
                }

                // Text
                Text(
                    text = "文本骨架",
                    style = Typography.BodySmall,
                    color = colors.mutedForeground
                )
                Skeleton(
                    variant = SkeletonVariant.TEXT,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
            }
        }

        // Animation types
        ExampleSection(
            title = "动画类型",
            description = "脉冲动画、波浪动画、无动画"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Pulse
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "脉冲动画 (PULSE)",
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                    Skeleton(
                        animation = SkeletonAnimation.PULSE,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }

                // Wave
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "波浪动画 (WAVE)",
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                    Skeleton(
                        animation = SkeletonAnimation.WAVE,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }

                // No animation
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "无动画 (NONE)",
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                    Skeleton(
                        animation = SkeletonAnimation.NONE,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }
        }

        // Text skeleton
        ExampleSection(
            title = "多行文本",
            description = "SkeletonText 支持多行文本"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SkeletonText(
                    lines = 1,
                    lineHeight = 20.dp,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )

                SkeletonText(
                    lines = 3,
                    lineHeight = 16.dp,
                    lineSpacing = 10.dp,
                    lastLineWidth = 0.7f
                )

                SkeletonText(
                    lines = 4,
                    lineHeight = 14.dp,
                    lineSpacing = 8.dp,
                    lastLineWidth = 0.5f,
                    animation = SkeletonAnimation.WAVE
                )
            }
        }

        // Avatars and images
        ExampleSection(
            title = "头像和图片",
            description = "SkeletonAvatar 和 SkeletonImage"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SkeletonAvatar(size = 40.dp)
                    Text(
                        text = "小",
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SkeletonAvatar(size = 56.dp)
                    Text(
                        text = "中",
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SkeletonAvatar(size = 72.dp)
                    Text(
                        text = "大",
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    SkeletonImage(
                        width = 80.dp,
                        height = 80.dp,
                        cornerRadius = 8.dp
                    )
                    Text(
                        text = "图片",
                        style = Typography.BodySmall,
                        color = colors.mutedForeground
                    )
                }
            }
        }

        // List row template
        ExampleSection(
            title = "列表项模板",
            description = "SkeletonListItem 常用于列表加载"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SkeletonListItem(
                    showAvatar = true,
                    showThumbnail = false
                )
                SkeletonListItem(
                    showAvatar = true,
                    showThumbnail = true
                )
                SkeletonListItem(
                    showAvatar = false,
                    showThumbnail = false,
                    animation = SkeletonAnimation.WAVE
                )
            }
        }

        // Card template
        ExampleSection(
            title = "卡片模板",
            description = "SkeletonCard 常用于卡片加载"
        ) {
            SkeletonCard(
                imageHeight = 120.dp,
                animation = SkeletonAnimation.PULSE
            )
        }

        // Article template
        ExampleSection(
            title = "文章模板",
            description = "SkeletonArticle 常用于文章加载"
        ) {
            SkeletonArticle(
                showImage = true,
                animation = SkeletonAnimation.WAVE
            )
        }

        // Grid template
        ExampleSection(
            title = "网格模板",
            description = "SkeletonGrid 常用于网格加载"
        ) {
            SkeletonGrid(
                columns = 3,
                rows = 2,
                itemHeight = 80.dp,
                spacing = 8.dp
            )
        }

        // Usage notes
        ExampleSection(
            title = "使用说明",
            description = "Skeleton 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. 支持三种形状: 矩形、圆形、文本",
                    style = Typography.BodyMedium,
                    color = colors.mutedForeground
                )
                Text(
                    text = "2. 支持三种动画: 脉冲、波浪、无动画",
                    style = Typography.BodyMedium,
                    color = colors.mutedForeground
                )
                Text(
                    text = "3. 提供预设模板: 列表项、卡片、文章、网格",
                    style = Typography.BodyMedium,
                    color = colors.mutedForeground
                )
                Text(
                    text = "4. 可自定义尺寸和圆角",
                    style = Typography.BodyMedium,
                    color = colors.mutedForeground
                )
            }
        }
    }
}
