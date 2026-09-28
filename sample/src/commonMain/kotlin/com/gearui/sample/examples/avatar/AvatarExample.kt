package com.gearui.sample.examples.avatar

import androidx.compose.runtime.Composable
import com.gearui.components.image.AvatarGroup
import com.gearui.components.image.AvatarGroupItem
import com.gearui.foundation.avatar.AvatarSizeTokens
import com.gearui.foundation.layout.Radius
import com.gearui.foundation.layout.Spacing
import com.gearui.primitives.Avatar
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.ui.Alignment

/**
 * Avatar component examples
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
        ExampleSection(
            title = "文字头像",
            description = "无图片时显示姓名首字，最多两个字符"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                Avatar(text = "A")
                Avatar(text = "用")
                Avatar(text = "AB")
            }
        }

        ExampleSection(
            title = "头像尺寸",
            description = "AvatarSizeTokens 提供 XSmall 到 XLarge 五档"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(text = "XL", size = AvatarSizeTokens.XLarge.size)
                Avatar(text = "L", size = AvatarSizeTokens.Large.size)
                Avatar(text = "M", size = AvatarSizeTokens.Medium.size)
                Avatar(text = "S", size = AvatarSizeTokens.Small.size)
                Avatar(text = "XS", size = AvatarSizeTokens.XSmall.size)
            }
        }

        ExampleSection(
            title = "头像形状",
            description = "默认圆形；radius 可改为圆角方形"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                Avatar(text = "圆")
                Avatar(text = "方", radius = Radius.lg)
            }
        }

        ExampleSection(
            title = "带徽标",
            description = "badgeDot 显示红点，badgeCount 显示数字"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                Avatar(text = "U", badgeDot = true)
                Avatar(text = "U", badgeCount = 5)
                Avatar(text = "U", badgeCount = 99)
            }
        }

        ExampleSection(
            title = "AvatarGroup",
            description = "成员头像重叠排列，超出 max 的部分折叠为 +N"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                AvatarGroup(
                    items = listOf("赵", "钱", "孙").map { AvatarGroupItem(fallbackText = it) }
                )
                AvatarGroup(
                    items = listOf("A", "B", "C", "D", "E", "F", "G").map {
                        AvatarGroupItem(fallbackText = it)
                    },
                    max = 4
                )
            }
        }
    }
}
