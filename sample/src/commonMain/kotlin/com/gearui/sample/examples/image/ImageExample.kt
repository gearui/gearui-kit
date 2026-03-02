package com.gearui.sample.examples.image

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.image.GearImage
import com.gearui.components.image.GearAvatar
import com.gearui.components.image.ImagePlaceholder
import com.gearui.components.image.ImageShape
import com.gearui.components.image.ImageFit
import com.gearui.components.image.GearImageWithState
import com.gearui.components.image.ImageLoadState
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Image 组件示例
 *
 * 用于展示效果，主要为上下左右居中裁切、拉伸、平铺等方式
 */
@Composable
fun ImageExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // ========== 组件类型 ==========

        // 裁剪和拉伸
        ExampleSection(
            title = "组件类型",
            description = "不同的图片填充模式"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 裁剪
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "裁剪",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GearImage(
                        painter = null,
                        fit = ImageFit.COVER,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 6.dp,
                        placeholderText = "COVER",
                        modifier = Modifier.size(72.dp)
                    )
                }

                // 拉伸
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "拉伸",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 121.dp, height = 72.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surfaceComponent),
                        contentAlignment = Alignment.Center
                    ) {
                        GearImage(
                            painter = null,
                            fit = ImageFit.FILL,
                            placeholderText = "FILL",
                            modifier = Modifier.size(width = 121.dp, height = 50.dp)
                        )
                    }
                }
            }
        }

        // 适应高和适应宽
        ExampleSection(
            title = "适应模式",
            description = "适应高度或宽度"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 适应高
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "适应高",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 89.dp, height = 72.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surfaceComponent),
                        contentAlignment = Alignment.Center
                    ) {
                        GearImage(
                            painter = null,
                            fit = ImageFit.CONTAIN,
                            placeholderText = "FIT\nHEIGHT",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // 适应宽
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "适应宽",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 72.dp, height = 89.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surfaceComponent),
                        contentAlignment = Alignment.Center
                    ) {
                        GearImage(
                            painter = null,
                            fit = ImageFit.CONTAIN,
                            placeholderText = "FIT\nWIDTH",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // 图片形状
        ExampleSection(
            title = "图片形状",
            description = "方形、圆角方形、圆形"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 方形
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "方形",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GearImage(
                        painter = null,
                        shape = ImageShape.SQUARE,
                        placeholderText = "方形",
                        modifier = Modifier.size(72.dp)
                    )
                }

                // 圆角方形
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "圆角方形",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 6.dp,
                        placeholderText = "圆角",
                        modifier = Modifier.size(72.dp)
                    )
                }

                // 圆形
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "圆形",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GearImage(
                        painter = null,
                        shape = ImageShape.CIRCLE,
                        placeholderText = "圆形",
                        modifier = Modifier.size(72.dp)
                    )
                }
            }
        }

        // ========== 组件状态 ==========

        // 加载状态
        ExampleSection(
            title = "组件状态 - 加载中",
            description = "默认提示和自定义提示"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 加载默认提示
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "加载默认提示",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surfaceComponent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "...",
                            style = Typography.TitleLarge,
                            color = colors.textPlaceholder
                        )
                    }
                }

                // 加载自定义提示
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "加载自定义提示",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surfaceComponent),
                        contentAlignment = Alignment.Center
                    ) {
                        // 自定义加载指示器（使用圆圈模拟）
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(3.dp, colors.primary, CircleShape)
                        )
                    }
                }
            }
        }

        // 失败状态
        ExampleSection(
            title = "组件状态 - 加载失败",
            description = "默认提示和自定义提示"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 失败默认提示
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "失败默认提示",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GearImageWithState(
                        painter = null,
                        loadState = ImageLoadState.Error(""),
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 6.dp,
                        modifier = Modifier.size(72.dp)
                    )
                }

                // 失败自定义提示
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "失败自定义提示",
                        style = Typography.BodyMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surfaceComponent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "加载失败",
                            style = Typography.BodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        // ========== 头像组件 ==========

        ExampleSection(
            title = "头像组件",
            description = "不同尺寸的头像"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GearAvatar(
                        painter = null,
                        size = 32.dp,
                        fallbackText = "小"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "32dp",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GearAvatar(
                        painter = null,
                        size = 48.dp,
                        fallbackText = "中"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "48dp",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GearAvatar(
                        painter = null,
                        size = 64.dp,
                        fallbackText = "大"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "64dp",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GearAvatar(
                        painter = null,
                        size = 80.dp,
                        fallbackText = "张三"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "80dp",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // ========== 带边框 ==========

        ExampleSection(
            title = "带边框图片",
            description = "不同形状的边框样式"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GearImage(
                        painter = null,
                        shape = ImageShape.SQUARE,
                        showBorder = true,
                        borderWidth = 1.dp,
                        placeholderText = "边框",
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "方形",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        showBorder = true,
                        borderWidth = 2.dp,
                        cornerRadius = 6.dp,
                        placeholderText = "边框",
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "圆角",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GearImage(
                        painter = null,
                        shape = ImageShape.CIRCLE,
                        showBorder = true,
                        borderWidth = 2.dp,
                        placeholderText = "边框",
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "圆形",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // ========== 图片占位符 ==========

        ExampleSection(
            title = "图片占位符",
            description = "自定义占位符样式"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ImagePlaceholder(
                    text = "暂无图片",
                    icon = "🖼",
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                )
                ImagePlaceholder(
                    text = "点击上传",
                    icon = "+",
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                )
            }
        }

        // ========== 图片画廊 ==========

        ExampleSection(
            title = "图片画廊",
            description = "九宫格图片展示"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 4.dp,
                        placeholderText = "1",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 4.dp,
                        placeholderText = "2",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 4.dp,
                        placeholderText = "3",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 4.dp,
                        placeholderText = "4",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 4.dp,
                        placeholderText = "5",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 4.dp,
                        placeholderText = "6",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 4.dp,
                        placeholderText = "7",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 4.dp,
                        placeholderText = "8",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                    GearImage(
                        painter = null,
                        shape = ImageShape.ROUNDED,
                        cornerRadius = 4.dp,
                        placeholderText = "9",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}
