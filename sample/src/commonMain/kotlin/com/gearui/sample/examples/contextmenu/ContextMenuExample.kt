package com.gearui.sample.examples.contextmenu

import androidx.compose.runtime.Composable
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonType
import com.gearui.components.contextmenu.ContextMenu
import com.gearui.components.contextmenu.ContextMenuItem
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

@Composable
fun ContextMenuExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val shapes = Theme.shapes
    val menuItems = listOf(
        ContextMenuItem("Copy") { Toast.show("Copy") },
        ContextMenuItem("Share") { Toast.show("Share") },
        ContextMenuItem("Delete", danger = true) { Toast.show("Delete") }
    )

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "Basic Usage",
            description = "Tap trigger to open a context action menu."
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceVariant, shapes.large)
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                ContextMenu(items = menuItems) { onOpen ->
                    Button(
                        text = "Open Context Menu",
                        type = ButtonType.OUTLINE,
                        onClick = onOpen
                    )
                }
            }

            Text(
                text = "Actions: Copy / Share / Delete",
                style = Typography.BodySmall,
                color = colors.textSecondary
            )
        }
    }
}
