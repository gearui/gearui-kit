package com.gearui.components.colorpicker

import com.gearui.gestures.ownsHorizontalDrag
import com.tencent.kuikly.compose.ui.Alignment
import com.gearui.foundation.interaction.hitTarget
import androidx.compose.runtime.*
import com.gearui.components.input.Input
import com.gearui.components.slider.Slider
import com.gearui.foundation.border.BorderWidth
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.interaction.choiceSemantics
import com.gearui.foundation.interaction.disabledAppearance
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.motion.rowPressFeedback
import com.gearui.foundation.primitives.Text
import com.gearui.i18n.I18n
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.*
import com.tencent.kuikly.compose.foundation.gestures.detectDragGestures
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.graphics.Brush
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.drawscope.Stroke
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.semantics.*
import com.tencent.kuikly.compose.ui.unit.IntSize

/** Static color sample; its accessibility name is supplied by the host or falls back to hexadecimal. */
@Composable
fun ColorSwatch(value: ColorValue, modifier: Modifier = Modifier, label: String = value.toHex()) {
    Box(modifier.size(ControlGeometry.colorSwatchSize).clip(Theme.shapes.sm).background(value.toColor())
        .border(BorderWidth.thin, Theme.colors.border, Theme.shapes.sm).semantics { contentDescription = label })
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorSwatchPicker(values: List<ColorValue>, value: ColorValue?, onValueChange: (ColorValue) -> Unit,
    modifier: Modifier = Modifier, enabled: Boolean = true) {
    FlowRow(modifier.disabledAppearance(!enabled), horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        values.forEach { swatch ->
            val selected = swatch.toHex(true) == value?.toHex(true)
            val interaction = remember { MutableInteractionSource() }
            // Hit region of at least 44 (HIG) around the drawn swatch.
            Box(Modifier.hitTarget().choiceSemantics(swatch.toHex(), selected, Role.Button, if (enabled) ({ onValueChange(swatch) }) else null)
                .clickable(enabled = enabled, interactionSource = interaction, indication = null) { onValueChange(swatch) },
                contentAlignment = Alignment.Center) {
            Box(Modifier.rowPressFeedback(interaction, Theme.shapes.sm, enabled = enabled)
                .border(if (selected) BorderWidth.thick else BorderWidth.thin, if (selected) Theme.colors.primary else Theme.colors.border, Theme.shapes.sm)) {
                ColorSwatch(swatch)
            }
            }
        }
    }
}

enum class ColorChannel { HUE, SATURATION, BRIGHTNESS, ALPHA }

@Composable
fun ColorSlider(value: ColorValue, channel: ColorChannel, label: String, onValueChange: (ColorValue) -> Unit,
    modifier: Modifier = Modifier, enabled: Boolean = true) {
    val n = when (channel) { ColorChannel.HUE -> value.hue; ColorChannel.SATURATION -> value.saturation
        ColorChannel.BRIGHTNESS -> value.brightness; ColorChannel.ALPHA -> value.alpha }
    Column(modifier.semantics { contentDescription = label }) {
        Text(label, style = Theme.typography.bodySmall, color = Theme.colors.foreground)
        Slider(n, { next -> onValueChange(when (channel) {
            ColorChannel.HUE -> value.copy(hue = next); ColorChannel.SATURATION -> value.copy(saturation = next)
            ColorChannel.BRIGHTNESS -> value.copy(brightness = next); ColorChannel.ALPHA -> value.copy(alpha = next)
        }) }, enabled = enabled, valueRange = if (channel == ColorChannel.HUE) 0f..360f else 0f..1f)
    }
}

/** Saturation/brightness plane. Sliders in ColorPicker supply an accessible alternative to dragging. */
@Composable
fun ColorArea(value: ColorValue, onValueChange: (ColorValue) -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    var measured by remember { mutableStateOf(IntSize.Zero) }
    val current by rememberUpdatedState(value)
    val change by rememberUpdatedState(onValueChange)
    val ring = Theme.colors.foreground
    Canvas(modifier.disabledAppearance(!enabled).ownsHorizontalDrag { enabled }.fillMaxWidth().height(ControlGeometry.colorAreaHeight).clip(Theme.shapes.md).onSizeChanged { measured = it }
        .pointerInput(enabled) { if (enabled) detectTapGestures { point ->
            if (measured.width > 0 && measured.height > 0) change(areaColor(current, point.x / measured.width, point.y / measured.height))
        } }
        .pointerInput(enabled) { if (enabled) detectDragGestures { move, _ ->
            move.consume()
            if (measured.width > 0 && measured.height > 0) change(areaColor(current, move.position.x / measured.width, move.position.y / measured.height))
        } }) {
        // These are mathematical endpoints of HSV, independent of the app's surface palette.
        val white = ColorValue(0f, 0f, 1f).toColor()
        val black = ColorValue(0f, 0f, 0f).toColor()
        drawRect(Brush.horizontalGradient(listOf(white, ColorValue(value.hue, 1f, 1f).toColor())))
        drawRect(Brush.verticalGradient(listOf(black.copy(alpha = 0f), black)))
        val radius = ControlGeometry.colorThumbSize.toPx() / 2
        val inset = radius + BorderWidth.thick.toPx() / 2
        drawCircle(ring, radius,
            Offset((value.saturation * size.width).coerceIn(inset, maxOf(inset, size.width - inset)),
                ((1 - value.brightness) * size.height).coerceIn(inset, maxOf(inset, size.height - inset))),
            style = Stroke(BorderWidth.thick.toPx()))
    }
}

@Composable
fun ColorField(value: ColorValue, onValueChange: (ColorValue) -> Unit, label: String, modifier: Modifier = Modifier, enabled: Boolean = true,
    variant: FieldVariant = FieldVariant.PRIMARY) {
    var text by remember { mutableStateOf(value.toHex()) }
    var emitted by remember { mutableStateOf(value) }
    LaunchedEffect(value) { if (value != emitted) text = value.toHex() }
    Input(text, { next -> text = next; ColorValue.parseHex(next)?.let { emitted = it; onValueChange(it) } },
        modifier, enabled = enabled, variant = variant, label = label, onFocusChanged = { if (!it) text = value.toHex() })
}

/** Full color editor. All visible labels come from the host's language pack. */
@Composable
fun ColorPicker(value: ColorValue, onValueChange: (ColorValue) -> Unit, labels: Map<ColorChannel, String>,
    fieldLabel: String, modifier: Modifier = Modifier, enabled: Boolean = true, showAlpha: Boolean = true,
    variant: FieldVariant = FieldVariant.PRIMARY) {
    val channels = ColorChannel.entries.filter { showAlpha || it != ColorChannel.ALPHA }
    require(channels.all { !labels[it].isNullOrBlank() })
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        ColorArea(value, onValueChange, enabled = enabled)
        channels.forEach { ColorSlider(value, it, labels.getValue(it), onValueChange, enabled = enabled) }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            ColorSwatch(value)
            ColorField(value, onValueChange, fieldLabel, Modifier.weight(1f), enabled, variant)
        }
    }
}
