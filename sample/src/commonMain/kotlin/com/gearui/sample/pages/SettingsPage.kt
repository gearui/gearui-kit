package com.gearui.sample.pages

import androidx.compose.runtime.*
import com.gearui.foundation.primitives.ScrollView
import com.gearui.foundation.scroll.ScrollTokens
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.navbar.NavBar
import com.gearui.components.radio.RadioButton
import com.gearui.components.scaffold.PageScaffold
import com.gearui.foundation.primitives.Text
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
enum class BrandAccent(val title: String, val color: Color?) {
    DEFAULT("Default", null), BLUE("Blue", Color(0xFF0088FF)),
    GREEN("Green", Color(0xFF12875C)), ORANGE("Orange", Color(0xFFFF9500))
}

class SettingsState {
    var brandAccent by mutableStateOf(BrandAccent.DEFAULT)
    var squareControls by mutableStateOf(false)
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
    val navBarColor = colors.surface
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
        topSafeAreaColor = navBarColor,
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
        ScrollView(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            tokens = ScrollTokens.Default.copy(spacing = Spacing.xl)
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

            SettingsCardSection(title = sampleStrings.brandAccent) {
                BrandAccent.entries.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        row.forEach { accent ->
                            RadioCardItemCompact(
                                selected = settingsState.brandAccent == accent,
                                onClick = { settingsState.brandAccent = accent },
                                title = when (accent) {
                                    BrandAccent.DEFAULT -> sampleStrings.brandDefault
                                    BrandAccent.BLUE -> sampleStrings.brandBlue
                                    BrandAccent.GREEN -> sampleStrings.brandGreen
                                    BrandAccent.ORANGE -> sampleStrings.brandOrange
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            SettingsCardSection(title = sampleStrings.shapeStyle) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    listOf(false to sampleStrings.shapeRounded, true to sampleStrings.shapeSquare).forEach { (square, title) ->
                        RadioCardItemCompact(
                            selected = settingsState.squareControls == square,
                            onClick = { settingsState.squareControls = square },
                            title = title,
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
                        .clip(Theme.shapes.lg)
                        .background(colors.surface)
                        .border(
                            width = 1.dp,
                            color = colors.border,
                            shape = Theme.shapes.lg
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
            style = Theme.typography.titleMedium,
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
            .clip(Theme.shapes.lg)
            .background(cardBackground)
            .border(
                width = 1.dp,
                color = cardBorderColor,
                shape = Theme.shapes.lg
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
                style = Theme.typography.bodyLarge,
                color = colors.foreground
            )
            Text(
                text = description,
                style = Theme.typography.bodySmall,
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
            .clip(Theme.shapes.lg)
            .background(cardBackground)
            .border(
                width = 1.dp,
                color = cardBorderColor,
                shape = Theme.shapes.lg
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = Theme.typography.bodyMedium,
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
            style = Theme.typography.bodyMedium,
            color = colors.foreground
        )
        Text(
            text = value,
            style = Theme.typography.bodySmall,
            color = colors.mutedForeground
        )
    }
}
