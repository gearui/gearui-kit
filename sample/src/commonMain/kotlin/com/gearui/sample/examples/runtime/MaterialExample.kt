package com.gearui.sample.examples.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.material.Material
import com.gearui.foundation.material.MaterialPolicy
import com.gearui.foundation.material.MaterialSurface
import com.gearui.foundation.material.Materials
import com.gearui.foundation.material.isMaterialBlurEnabled
import com.gearui.foundation.primitives.Text
import com.gearui.runtime.LocalRuntimeFlags
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Frosted-glass probe.
 *
 * GearUI ships with `MaterialPolicy.Never`, so the flat surface is what real
 * screens get today — see `docs/UPSTREAM_KUIKLYUI_BLUR.md`. This page forces
 * the blur on anyway, because the reason it is off is a cross-renderer
 * calibration problem, and that is only visible by looking at the same
 * material on several devices.
 *
 * The backdrop is deliberately hostile: fine stripes alone prove nothing,
 * since a strong blur averages them to a flat tone and a *failed* blur
 * produces a flat tone too.
 */
@Composable
fun MaterialExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val configuration = LocalConfiguration.current
    val blurred = isMaterialBlurEnabled()

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "Capability",
            description = "GearUI ships with the blur off. This page forces it on to compare renderers."
        ) {
            Text(
                text = "Shipping policy: ${LocalRuntimeFlags.current.materialPolicy}" +
                    if (blurred) " — blurring" else " — flat surfaces",
                style = Theme.typography.titleSmall,
                color = colors.foreground,
            )
            Text(
                text = "platform=${configuration.platform} osVersion=${configuration.osVersion}",
                style = Theme.typography.bodySmall,
                color = colors.mutedForeground,
            )
        }

        ExampleSection(
            title = "Materials, blur forced on",
            description = "Chrome for bars, Sheet for modals, Popover for anchored surfaces. " +
                "Compare these across platforms: the same radius is scaled differently by " +
                "every renderer, which is why the default is off."
        ) {
            CompositionLocalProvider(
                LocalRuntimeFlags provides LocalRuntimeFlags.current
                    .copy(materialPolicy = MaterialPolicy.Always)
            ) {
                MaterialSample("Chrome", Materials.Chrome)
                MaterialSample("Sheet", Materials.Sheet)
                MaterialSample("Popover", Materials.Popover)
            }
        }

        ExampleSection(
            title = "What ships today",
            description = "The same material under the shipping policy: opaque, not a tint " +
                "without a blur. A tint is calibrated against a blurred backdrop; over raw " +
                "content its contrast depends on the user's data."
        ) {
            MaterialSample("Chrome (as shipped)", Materials.Chrome)
        }
    }
}

@Composable
private fun MaterialSample(label: String, material: Material) {
    val colors = Theme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .padding(vertical = Spacing.xs)
    ) {
        // The backdrop has to survive the blur to prove anything. Fine stripes
        // alone do not: a 12.5pt Gaussian averages them to a flat tone, and a
        // flat tone is exactly what a *failed* blur also produces. So half the
        // width is a solid block — no blur radius can erase a 100dp field, and
        // if the surface shows a left-to-right step, the blur is real.
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(colors.primary)
            )
            Column(modifier = Modifier.weight(1f).fillMaxSize()) {
                repeat(8) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(if (index % 2 == 0) colors.foreground else colors.background)
                    )
                }
            }
        }
        MaterialSurface(
            material = material,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xxl, vertical = Spacing.md),
            shape = Theme.shapes.lg,
        ) {
            Text(
                text = "$label · radius ${material.blurRadius} · tint ${material.tintAlpha}",
                style = Theme.typography.bodySmall,
                color = colors.foreground,
                modifier = Modifier.padding(Spacing.md),
            )
        }
    }
}
