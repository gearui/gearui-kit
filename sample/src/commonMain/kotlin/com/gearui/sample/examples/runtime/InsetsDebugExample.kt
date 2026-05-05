package com.gearui.sample.examples.runtime

import androidx.compose.runtime.Composable
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.runtime.LocalRuntimeEnvironment
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration

@Composable
fun InsetsDebugExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val configuration = LocalConfiguration.current
    val rawInsets = configuration.safeAreaInsets
    val runtimeEnvironment = LocalRuntimeEnvironment.current
    val runtimeFlags = LocalRuntimeFlags.current

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "Runtime Insets Snapshot",
            description = "用于验证 Kuikly safeAreaInsets 与 Gear runtime safeArea 是否一致。"
        ) {
            Text(
                text = "Kuikly safeAreaInsets",
                style = Typography.TitleSmall,
                color = colors.textPrimary
            )
            Text(
                text = "top=${rawInsets.top}, bottom=${rawInsets.bottom}, left=${rawInsets.left}, right=${rawInsets.right}",
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )

            Text(
                text = "Gear Runtime safeArea",
                style = Typography.TitleSmall,
                color = colors.textPrimary
            )
            Text(
                text = "top=${runtimeEnvironment.safeArea.top}, bottom=${runtimeEnvironment.safeArea.bottom}, " +
                    "left=${runtimeEnvironment.safeArea.left}, right=${runtimeEnvironment.safeArea.right}",
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )

            Text(
                text = "Runtime flags",
                style = Typography.TitleSmall,
                color = colors.textPrimary
            )
            Text(
                text = "unifiedSafeAreaPipeline=${runtimeFlags.unifiedSafeAreaPipeline}",
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )
            Text(
                text = "navBarConsumesTopSafeArea=${runtimeFlags.navBarConsumesTopSafeArea}",
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )
            Text(
                text = "bottomNavBarConsumesBottomSafeArea=${runtimeFlags.bottomNavBarConsumesBottomSafeArea}",
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )
            Text(
                text = "drawerConsumesVerticalSafeArea=${runtimeFlags.drawerConsumesVerticalSafeArea}",
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )
            Text(
                text = "actionSheetConsumesBottomSafeArea=${runtimeFlags.actionSheetConsumesBottomSafeArea}",
                style = Typography.BodyMedium,
                color = colors.textSecondary
            )
        }
    }
}
