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
 * Two things are worth seeing here rather than reading: whether this device
 * blurs at all, and what the fallback looks like when it does not. The stripes
 * behind each surface exist so an un-blurred material is obvious — over a flat
 * background a failed blur and a working one look identical.
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
            description = "Auto blurs only where the renderer does it on the GPU."
        ) {
            Text(
                text = if (blurred) "Blur: ON" else "Blur: OFF — surfaces render flat",
                style = Theme.typography.titleSmall,
                color = if (blurred) colors.success else colors.mutedForeground,
            )
            Text(
                text = "platform=${configuration.platform} osVersion=${configuration.osVersion} " +
                    "policy=${LocalRuntimeFlags.current.materialPolicy}",
                style = Theme.typography.bodySmall,
                color = colors.mutedForeground,
            )
        }

        ExampleSection(
            title = "Materials",
            description = "Chrome for bars, Sheet for modals, Popover for anchored surfaces."
        ) {
            MaterialSample("Chrome", Materials.Chrome)
            MaterialSample("Sheet", Materials.Sheet)
            MaterialSample("Popover", Materials.Popover)
        }

        ExampleSection(
            title = "Degradation",
            description = "The same material with the policy forced off. This is what every " +
                "platform without a GPU blur shows: opaque, not a tint without a blur."
        ) {
            CompositionLocalProvider(
                LocalRuntimeFlags provides LocalRuntimeFlags.current
                    .copy(materialPolicy = MaterialPolicy.Never)
            ) {
                MaterialSample("Chrome (forced flat)", Materials.Chrome)
            }
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
