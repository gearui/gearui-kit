package com.gearui.sample.examples.bottomnavbar

import com.gearui.components.icon.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.bottomnavbar.BottomNavBar
import com.gearui.components.bottomnavbar.BottomNavItem
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.theme.Theme

private val BASIC_LABELS = mapOf("messages" to "消息", "contacts" to "通讯录", "me" to "我")

@Composable
fun BottomNavBarExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // The bar is a surface itself, so every section shows it on the page background.
        ExampleSection(
            title = "基础用法",
            description = "应用一级页面之间的切换，可带数字角标或红点",
            surface = SectionSurface.Plain
        ) {
            var selectedId by remember { mutableStateOf("messages") }

            BottomNavBar(
                items = listOf(
                    BottomNavItem(
                        id = "messages",
                        label = "消息",
                        icon = Icons.chats,
                        badgeCount = 12
                    ),
                    BottomNavItem(
                        id = "contacts",
                        label = "通讯录",
                        icon = Icons.addressBook,
                        showBadgeDot = true
                    ),
                    BottomNavItem(
                        id = "me",
                        label = "我",
                        icon = Icons.userCircle
                    )
                ),
                selectedId = selectedId,
                onSelect = { selectedId = it }
            )

            Text(
                text = "当前选中:${BASIC_LABELS[selectedId] ?: selectedId}",
                style = Theme.typography.bodySmall,
                color = colors.mutedForeground
            )
        }

        ExampleSection(
            title = "角标溢出",
            description = "一位数、两位数与超过 99 的角标，超出显示 99+ 且不被截断",
            surface = SectionSurface.Plain
        ) {
            var selectedId by remember { mutableStateOf("five") }

            BottomNavBar(
                items = listOf(
                    BottomNavItem(id = "five", label = "少量", icon = Icons.chats, badgeCount = 5),
                    BottomNavItem(id = "ninetynine", label = "临界", icon = Icons.bell, badgeCount = 99),
                    BottomNavItem(id = "overflow", label = "溢出", icon = Icons.envelopeSimple, badgeCount = 120)
                ),
                selectedId = selectedId,
                onSelect = { selectedId = it }
            )
        }

        ExampleSection(
            title = "禁用项",
            description = "禁用的标签可见但不可点击",
            surface = SectionSurface.Plain
        ) {
            var selectedId by remember { mutableStateOf("feed") }

            BottomNavBar(
                items = listOf(
                    BottomNavItem(id = "feed", label = "首页", icon = Icons.house),
                    BottomNavItem(id = "discover", label = "发现", icon = Icons.magnifyingGlass, disabled = true),
                    BottomNavItem(id = "profile", label = "我的", icon = Icons.user)
                ),
                selectedId = selectedId,
                onSelect = { selectedId = it }
            )
        }
    }
}
