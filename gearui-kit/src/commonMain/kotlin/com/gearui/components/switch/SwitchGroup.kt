package com.gearui.components.switch

import com.gearui.foundation.interaction.controlSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldDescription
import com.gearui.foundation.field.FieldLabel
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.i18n.I18n
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.interaction.collectIsPressedAsState
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.heightIn
import com.tencent.kuikly.compose.foundation.selection.toggleable
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.semantics.Role

/** One switch in a [SwitchGroup]. [key] identifies it in the checked set. */
data class SwitchGroupItem(
    val key: String,
    val label: String,
    val description: String? = null,
    val enabled: Boolean = true,
)

/**
 * SwitchGroup — HeroUI v3 `SwitchGroup` for mobile: a labelled set of independent
 * settings, each a label with optional description and a trailing switch.
 *
 * The whole row toggles, not only the thumb. The group [label] and [description]
 * use the field text primitives so a group reads like any other field.
 */
@Composable
fun SwitchGroup(
    items: List<SwitchGroupItem>,
    checkedKeys: Set<String>,
    onCheckedChange: (key: String, checked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    description: String? = null,
    enabled: Boolean = true,
    size: SwitchSize = SwitchSize.MEDIUM,
) {
    Column(
        modifier = modifier.alpha(if (enabled) 1f else FeedbackDefaults.disabledOpacity),
        verticalArrangement = Arrangement.spacedBy(FieldDefaults.labelGap),
    ) {
        if (label != null) FieldLabel(text = label)
        if (description != null) FieldDescription(text = description)
        items.forEach { item ->
            SwitchGroupRow(
                item = item,
                checked = item.key in checkedKeys,
                enabled = enabled && item.enabled,
                size = size,
                onCheckedChange = { onCheckedChange(item.key, it) },
            )
        }
    }
}

@Composable
private fun SwitchGroupRow(
    item: SwitchGroupItem,
    checked: Boolean,
    enabled: Boolean,
    size: SwitchSize,
    onCheckedChange: (Boolean) -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = ControlGeometry.selectionTouchTarget)
            .alpha(if (item.enabled) 1f else FeedbackDefaults.disabledOpacity)
            .controlSemantics(item.label, switchState(checked), Role.Switch,
                onClick = if (enabled) ({ onCheckedChange(!checked) }) else null, extra = item.description)
            .toggleable(
                value = checked,
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ),
        horizontalArrangement = Arrangement.spacedBy(ControlGeometry.alertGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            FieldLabel(text = item.label)
            if (item.description != null) FieldDescription(text = item.description)
        }
        SwitchVisual(
            checked, enabled, pressed, SwitchType.FILL, size, null, null,
            I18n.strings.field.switchOn, I18n.strings.field.switchOff,
        )
    }
}
