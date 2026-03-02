package com.gearui.compose

import androidx.compose.runtime.Composable
import com.gearui.foundation.primitives.Text
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.sp
import com.gearui.*

// Button Components
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.TextWhite1),
    )
}

@Composable
fun DangerButton(text: String, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.TextWhite1),
    )
}

@Composable
fun WarningButton(text: String, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.TextWhite1),
    )
}

@Composable
fun SuccessButton(text: String, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.TextWhite1),
    )
}

@Composable
fun PrimaryOutlineButton(text: String, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.BrandNormal),
    )
}

@Composable
fun TextButton(text: String, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.BrandNormal),
    )
}

// Text Components
@Composable
fun HeadlineLarge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.TextPrimary)
    )
}

@Composable
fun HeadlineMedium(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.TextPrimary)
    )
}

@Composable
fun TitleLarge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.TextPrimary)
    )
}

@Composable
fun TitleMedium(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.TextPrimary)
    )
}

@Composable
fun BodyLarge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.TextPrimary)
    )
}

@Composable
fun BodyMedium(text: String, color: Int = ColorTokens.TextPrimary, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(color)
    )
}

@Composable
fun BodySmall(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.TextPrimary)
    )
}

@Composable
fun Label(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(ColorTokens.TextSecondary)
    )
}
