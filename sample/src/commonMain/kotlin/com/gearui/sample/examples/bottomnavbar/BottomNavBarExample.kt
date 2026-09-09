package com.gearui.sample.examples.bottomnavbar

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
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

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
        ExampleSection(
            title = "Basic Usage",
            description = "Bottom navigation for app-level section switching."
        ) {
            var selectedId by remember { mutableStateOf("messages") }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BottomNavBar(
                    items = listOf(
                        BottomNavItem(
                            id = "messages",
                            label = "Messages",
                            icon = Icons.chats,
                            badgeCount = 12
                        ),
                        BottomNavItem(
                            id = "contacts",
                            label = "Contacts",
                            icon = Icons.address_book,
                            showBadgeDot = true
                        ),
                        BottomNavItem(
                            id = "me",
                            label = "Me",
                            icon = Icons.user_circle
                        )
                    ),
                    selectedId = selectedId,
                    onSelect = { selectedId = it }
                )

                Text(
                    text = "Selected tab: $selectedId",
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }

        ExampleSection(
            title = "Badge Count Overflow",
            description = "Verifies badge layout for single digit / two digits / >99 (must show 99+ without truncation)."
        ) {
            var selectedId by remember { mutableStateOf("five") }

            BottomNavBar(
                items = listOf(
                    BottomNavItem(id = "five", label = "Few", icon = Icons.chats, badgeCount = 5),
                    BottomNavItem(id = "ninetynine", label = "Edge", icon = Icons.bell, badgeCount = 99),
                    BottomNavItem(id = "overflow", label = "Overflow", icon = Icons.envelope_simple, badgeCount = 120)
                ),
                selectedId = selectedId,
                onSelect = { selectedId = it }
            )
        }

        ExampleSection(
            title = "Disabled Item",
            description = "Disabled tabs are visible but not clickable."
        ) {
            var selectedId by remember { mutableStateOf("feed") }

            BottomNavBar(
                items = listOf(
                    BottomNavItem(id = "feed", label = "Feed", icon = Icons.house),
                    BottomNavItem(id = "discover", label = "Discover", icon = Icons.magnifying_glass, disabled = true),
                    BottomNavItem(id = "profile", label = "Profile", icon = Icons.user)
                ),
                selectedId = selectedId,
                onSelect = { selectedId = it }
            )
        }
    }
}
