package com.gearui.sample.examples.avatar

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.primitives.Avatar
import com.gearui.foundation.AvatarSpecs
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection

/**
 * Avatar 组件示例
 */
@Composable
fun AvatarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 头像类型
        ExampleSection(
            title = "头像类型",
            description = "图片、文字、图标头像"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Avatar(
                    text = "A",
                    size = AvatarSpecs.Size.medium
                )
                Avatar(
                    text = "用",
                    size = AvatarSpecs.Size.medium
                )
                Avatar(
                    text = "AB",
                    size = AvatarSpecs.Size.medium
                )
            }
        }

        // 头像尺寸
        ExampleSection(
            title = "头像尺寸",
            description = "提供多种尺寸的头像"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = com.tencent.kuikly.compose.ui.Alignment.CenterVertically
            ) {
                Avatar(
                    text = "L",
                    size = AvatarSpecs.Size.large
                )
                Avatar(
                    text = "M",
                    size = AvatarSpecs.Size.medium
                )
                Avatar(
                    text = "S",
                    size = AvatarSpecs.Size.small
                )
                Avatar(
                    text = "XS",
                    size = 32.dp
                )
            }
        }

        // 头像形状
        ExampleSection(
            title = "头像形状",
            description = "圆形和方形头像"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Avatar(
                    text = "圆",
                    size = AvatarSpecs.Size.medium,
                    radius = AvatarSpecs.defaultRadius  // 圆形
                )
                Avatar(
                    text = "方",
                    size = AvatarSpecs.Size.medium,
                    radius = 8.dp  // 圆角矩形
                )
            }
        }

        // 带徽标的头像
        ExampleSection(
            title = "带徽标的头像",
            description = "头像右上角显示徽标"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Avatar(
                    text = "U",
                    size = AvatarSpecs.Size.medium,
                    badgeDot = true
                )
                Avatar(
                    text = "U",
                    size = AvatarSpecs.Size.medium,
                    badgeCount = 5
                )
                Avatar(
                    text = "U",
                    size = AvatarSpecs.Size.medium,
                    badgeCount = 99
                )
            }
        }
    }
}
