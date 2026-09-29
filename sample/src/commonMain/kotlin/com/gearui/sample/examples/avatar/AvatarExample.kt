package com.gearui.sample.examples.avatar

import androidx.compose.runtime.Composable
import com.gearui.components.avatar.Avatar
import com.gearui.components.avatar.AvatarGroup
import com.gearui.components.avatar.AvatarGroupItem
import com.gearui.components.toast.Toast
import com.gearui.foundation.avatar.AvatarSizeTokens
import com.gearui.foundation.layout.Spacing
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.ui.Alignment

/** A picture bundled with the sample (loads on any network), and one that never will. */
private const val PictureUrl = "assets://avatars/gearui.png"
private const val BrokenUrl = "https://invalid.example/avatar.png"

/**
 * Avatar examples: initials, a picture over them, sizes, shapes, and the badge and
 * online dot a chat list puts on it.
 */
@Composable
fun AvatarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "文字头像",
            description = "无图片时显示首字；中文名通常取一个字，英文名取两个"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                Avatar(fallback = "张")
                Avatar(fallback = "AB")
                Avatar(fallback = "王", backgroundColor = Theme.colors.primary, contentColor = Theme.colors.primaryForeground)
            }
        }

        ExampleSection(
            title = "图片与兜底",
            description = "url 异步加载并裁切填满；加载中和失败时露出首字，不出现空白或破图"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                Avatar(fallback = "G", url = PictureUrl, contentDescription = "GearUI")
                Avatar(fallback = "失", url = BrokenUrl, contentDescription = "加载失败的头像")
            }
        }

        ExampleSection(
            title = "头像尺寸",
            description = "AvatarSizeTokens 提供 XSmall 到 XLarge 五档，首字字号随之变化"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(fallback = "李", size = AvatarSizeTokens.XLarge.size)
                Avatar(fallback = "李", size = AvatarSizeTokens.Large.size)
                Avatar(fallback = "李", size = AvatarSizeTokens.Medium.size)
                Avatar(fallback = "李", size = AvatarSizeTokens.Small.size)
                Avatar(fallback = "李", size = AvatarSizeTokens.XSmall.size)
            }
        }

        ExampleSection(
            title = "头像形状",
            description = "默认圆形；聊天应用常用圆角方形，传 shape"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                Avatar(fallback = "圆", url = PictureUrl)
                Avatar(fallback = "方", url = PictureUrl, shape = Theme.shapes.md)
            }
        }

        ExampleSection(
            title = "未读与在线",
            description = "badgeCount 显示数字，badgeDot 显示红点（免打扰），online 显示在线点；都在图片之上"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                Avatar(fallback = "赵", url = PictureUrl, shape = Theme.shapes.md, badgeCount = 5)
                Avatar(fallback = "钱", shape = Theme.shapes.md, badgeCount = 128)
                Avatar(fallback = "孙", shape = Theme.shapes.md, badgeDot = true)
                Avatar(fallback = "周", url = PictureUrl, online = true)
            }
        }

        ExampleSection(
            title = "可点击",
            description = "onClick 带按压缩放，读屏读作按钮"
        ) {
            Avatar(fallback = "我", onClick = { Toast.show("打开个人主页") }, contentDescription = "我的头像")
        }

        ExampleSection(
            title = "AvatarGroup",
            description = "成员头像重叠排列，超出 max 的部分折叠为 +N"
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                AvatarGroup(items = listOf("赵", "钱", "孙").map { AvatarGroupItem(fallback = it) })
                AvatarGroup(
                    items = listOf("A", "B", "C", "D", "E", "F", "G").map {
                        AvatarGroupItem(fallback = it, url = if (it == "A") PictureUrl else null)
                    },
                    max = 4
                )
            }
        }
    }
}
