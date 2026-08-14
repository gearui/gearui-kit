package com.gearui.sample.pages

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.navbar.NavBar
import com.gearui.components.radio.RadioButton
import com.gearui.components.scaffold.PageScaffold
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.i18n.I18n
import com.gearui.sample.SampleBuildInfo
import com.gearui.sample.i18n.DefaultSampleLanguageOptions
import com.gearui.sample.i18n.SampleI18n
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing

/**
 * Theme style options
 */
enum class ThemeStyle(val displayName: String) {
    LIGHT("浅色模式"),
    DARK("深色模式"),
    DARK_PURPLE("暗紫"),
    SYSTEM("跟随系统")
}

/**
 * Settings state
 */
class SettingsState {
    var languageTag by mutableStateOf("zh-Hans")
    var themeStyle by mutableStateOf(ThemeStyle.SYSTEM)
}

/**
 * Global settings state
 */
val LocalSettingsState = staticCompositionLocalOf { SettingsState() }

/**
 * SettingsPage - settings screen
 *
 * Supports:
 * - language switching (Chinese / English), applied immediately
 * - theme style switching (light / dark / follow system), applied immediately
 */
@Composable
fun SettingsPage(
    settingsState: SettingsState,
    onBack: () -> Unit
) {
    val colors = Theme.colors
    val coreStrings = I18n.strings
    val sampleStrings = SampleI18n.strings
    val navBarColor = if (settingsState.themeStyle == ThemeStyle.DARK_PURPLE) colors.primary else colors.surface
    val languageOptions = DefaultSampleLanguageOptions

    // Display name of the theme style in the current language
    val themeDisplayNames = mapOf(
        ThemeStyle.LIGHT to coreStrings.light,
        ThemeStyle.DARK to coreStrings.dark,
        ThemeStyle.DARK_PURPLE to sampleStrings.darkPurple,
        ThemeStyle.SYSTEM to coreStrings.system
    )

    PageScaffold(
        backgroundColor = colors.background,
        consumeBottomSafeArea = true
    ) {
        Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Top navigation bar
        NavBar(
            title = sampleStrings.settingsTitle,
            centerTitle = true,
            useDefaultBack = true,
            onBackClick = onBack,
            backgroundColor = navBarColor
        )

        // Settings content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            // Language
            SettingsCardSection(title = coreStrings.language) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    languageOptions.forEach { language ->
                        RadioCardItem(
                            selected = settingsState.languageTag == language.tag,
                            onClick = { settingsState.languageTag = language.tag },
                            title = language.displayName,
                            description = language.code
                        )
                    }
                }
            }

            // Theme style - horizontal card radio group
            SettingsCardSection(title = coreStrings.theme) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    // First row: light + dark
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        RadioCardItemCompact(
                            selected = settingsState.themeStyle == ThemeStyle.LIGHT,
                            onClick = { settingsState.themeStyle = ThemeStyle.LIGHT },
                            title = themeDisplayNames[ThemeStyle.LIGHT] ?: ThemeStyle.LIGHT.displayName,
                            modifier = Modifier.weight(1f)
                        )
                        RadioCardItemCompact(
                            selected = settingsState.themeStyle == ThemeStyle.DARK,
                            onClick = { settingsState.themeStyle = ThemeStyle.DARK },
                            title = themeDisplayNames[ThemeStyle.DARK] ?: ThemeStyle.DARK.displayName,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Second row: deep purple + follow system
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        RadioCardItemCompact(
                            selected = settingsState.themeStyle == ThemeStyle.DARK_PURPLE,
                            onClick = { settingsState.themeStyle = ThemeStyle.DARK_PURPLE },
                            title = themeDisplayNames[ThemeStyle.DARK_PURPLE] ?: ThemeStyle.DARK_PURPLE.displayName,
                            modifier = Modifier.weight(1f)
                        )
                        RadioCardItemCompact(
                            selected = settingsState.themeStyle == ThemeStyle.SYSTEM,
                            onClick = { settingsState.themeStyle = ThemeStyle.SYSTEM },
                            title = themeDisplayNames[ThemeStyle.SYSTEM] ?: ThemeStyle.SYSTEM.displayName,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // About
            SettingsCardSection(
                title = sampleStrings.aboutTitle
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Spacing.sm))
                        .background(colors.surface)
                        .border(
                            width = 1.dp,
                            color = colors.border,
                            shape = RoundedCornerShape(Spacing.sm)
                        )
                        .padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    SettingsInfoRow(
                        title = sampleStrings.versionLabel,
                        value = SampleBuildInfo.VERSION
                    )
                    SettingsInfoRow(
                        title = "GearUI Kit",
                        value = sampleStrings.gearUiComponents
                    )
                }
            }
        }
        }
    }
}

/**
 * Settings card group
 */
@Composable
private fun SettingsCardSection(
    title: String,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Group title
        Text(
            text = title,
            style = Typography.TitleMedium,
            color = colors.foreground
        )

        // Group content
        content()
    }
}

/**
 * Card-style radio option (vertical, with a description)
 */
@Composable
private fun RadioCardItem(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    val cardBackground = if (selected) colors.muted else colors.surface
    val cardBorderColor = if (selected) colors.primary.copy(alpha = 0.72f) else colors.border

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.sm))
            .background(cardBackground)
            .border(
                width = 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(Spacing.sm)
            )
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = title,
                style = Typography.BodyLarge,
                color = colors.foreground
            )
            Text(
                text = description,
                style = Typography.BodySmall,
                color = colors.mutedForeground
            )
        }
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}


/**
 * Card-style radio option (horizontal, compact)
 */
@Composable
private fun RadioCardItemCompact(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors
    val cardBackground = if (selected) colors.muted else colors.surface
    val cardBorderColor = if (selected) colors.primary.copy(alpha = 0.72f) else colors.border

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Spacing.sm))
            .background(cardBackground)
            .border(
                width = 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(Spacing.sm)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = Typography.BodyMedium,
            color = colors.foreground
        )
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}

/**
 * Information row
 */
@Composable
private fun SettingsInfoRow(
    title: String,
    value: String
) {
    val colors = Theme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = Typography.BodyMedium,
            color = colors.foreground
        )
        Text(
            text = value,
            style = Typography.BodySmall,
            color = colors.mutedForeground
        )
    }
}
