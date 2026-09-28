package com.gearui.sample.pages

import com.tencent.kuikly.compose.ui.semantics.stateDescription
import com.tencent.kuikly.compose.ui.semantics.semantics
import com.tencent.kuikly.compose.ui.graphics.luminance
import com.gearui.theme.Themes
import com.gearui.foundation.list.CellDefaults
import com.gearui.foundation.typography.IconSizes
import com.gearui.foundation.primitives.Icon
import com.gearui.components.icon.Icons
import com.gearui.components.segmented.SegmentedControl
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.cell.Cell
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.gearui.runtime.LocalRuntimeEnvironment
import androidx.compose.runtime.*
import com.gearui.foundation.primitives.ScrollView
import com.gearui.foundation.scroll.ScrollTokens
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.navbar.NavBar
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
        consumeBottomSafeArea = false
    ) {
        // The list runs under the home indicator (edge to edge) and reserves the inset at its
        // end instead: consuming it in PageScaffold painted a solid strip over the indicator.
        val safeBottom = LocalRuntimeEnvironment.current.safeArea.bottom
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

        // Settings content, built from the kit's own list and segmented controls: a
        // choice among a few short options is a segmented control; a choice with a
        // second line, or among longer labels, is a grouped list with a checkmark —
        // the way the platform's own Settings does it.
        ScrollView(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            tokens = ScrollTokens.Default.copy(spacing = Spacing.lg)
        ) {
            CellGroup(items = languageOptions, title = coreStrings.language) { language ->
                SelectableCell(
                    title = language.displayName,
                    description = language.code,
                    selected = settingsState.languageTag == language.tag,
                    onSelect = { settingsState.languageTag = language.tag },
                )
            }

            SegmentedSection(title = coreStrings.theme) {
                SegmentedControl(
                    options = ThemeStyle.entries,
                    selectedOption = settingsState.themeStyle,
                    onOptionSelected = { settingsState.themeStyle = it },
                    modifier = Modifier.fillMaxWidth(),
                    labelProvider = { themeDisplayNames[it] ?: it.displayName },
                )
            }

            val defaultAccent = (if (colors.background.luminance() < 0.5f) Themes.Dark else Themes.Light).colors.primary
            // The separator starts where the text does, past the swatch.
            CellGroup(
                items = BrandAccent.entries,
                title = sampleStrings.brandAccent,
                separatorInset = CellDefaults.Default.paddingHorizontal + IconSizes.Default.md + Spacing.md,
            ) { accent ->
                SelectableCell(
                    title = when (accent) {
                        BrandAccent.DEFAULT -> sampleStrings.brandDefault
                        BrandAccent.BLUE -> sampleStrings.brandBlue
                        BrandAccent.GREEN -> sampleStrings.brandGreen
                        BrandAccent.ORANGE -> sampleStrings.brandOrange
                    },
                    selected = settingsState.brandAccent == accent,
                    onSelect = { settingsState.brandAccent = accent },
                    leading = { AccentSwatch(accent.color ?: defaultAccent) },
                )
            }

            SegmentedSection(title = sampleStrings.shapeStyle) {
                SegmentedControl(
                    options = listOf(false, true),
                    selectedOption = settingsState.squareControls,
                    onOptionSelected = { settingsState.squareControls = it },
                    modifier = Modifier.fillMaxWidth(),
                    labelProvider = { if (it) sampleStrings.shapeSquare else sampleStrings.shapeRounded },
                )
            }

            CellGroup(
                items = listOf(
                    sampleStrings.versionLabel to SampleBuildInfo.VERSION,
                    "GearUI Kit" to sampleStrings.gearUiComponents,
                ),
                title = sampleStrings.aboutTitle,
            ) { (title, value) ->
                Cell(title = title, note = value)
            }
            Spacer(modifier = Modifier.height(safeBottom))
        }
        }
    }
}

/**
 * A row that is one of a set of choices. The checkmark is decoration; the state
 * reaches a screen reader through stateDescription (VISUAL_SPEC §7).
 */
@Composable
private fun SelectableCell(
    title: String,
    selected: Boolean,
    onSelect: () -> Unit,
    description: String? = null,
    leading: (@Composable () -> Unit)? = null,
) {
    val colors = Theme.colors
    val state = if (selected) I18n.strings.common.selected else I18n.strings.common.unselected
    Cell(
        title = title,
        description = description,
        onClick = onSelect,
        leading = leading,
        modifier = Modifier.semantics { stateDescription = state },
        trailing = if (selected) {
            { Icon(name = Icons.check, size = IconSizes.Default.md, tint = colors.primary) }
        } else null,
    )
}

/**
 * A titled section holding one segmented control. The title uses the grouped list's
 * title style and inset, so a segmented section and a CellGroup read as one page.
 */
@Composable
private fun SegmentedSection(title: String, content: @Composable () -> Unit) {
    val colors = Theme.colors
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = Theme.typography.bodySmall,
            color = colors.mutedForeground,
            modifier = Modifier.padding(
                start = CellDefaults.Default.paddingHorizontal,
                end = CellDefaults.Default.paddingHorizontal,
                top = Spacing.md,
                bottom = Spacing.sm,
            ),
        )
        content()
    }
}

@Composable
private fun AccentSwatch(color: Color) {
    Box(
        modifier = Modifier
            .size(IconSizes.Default.md)
            .clip(Theme.shapes.full)
            .background(color)
    )
}
