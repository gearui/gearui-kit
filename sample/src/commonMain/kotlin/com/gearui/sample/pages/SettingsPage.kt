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
import com.gearui.sample.i18n.DefaultSampleLanguageOptions
import com.gearui.sample.i18n.SampleI18n
import com.gearui.theme.Theme
import com.gearui.foundation.layout.Spacing

/**
 * 主题风格选项
 */
enum class ThemeStyle(val displayName: String) {
    LIGHT("浅色模式"),
    DARK("深色模式"),
    DARK_PURPLE("暗紫"),
    SYSTEM("跟随系统")
}

/**
 * 设置状态
 */
class SettingsState {
    var languageTag by mutableStateOf("zh-Hans")
    var themeStyle by mutableStateOf(ThemeStyle.SYSTEM)
}

/**
 * 全局设置状态
 */
val LocalSettingsState = staticCompositionLocalOf { SettingsState() }

/**
 * SettingsPage - 设置页面
 *
 * 支持：
 * - 语言切换（中文/英文）- 实时生效
 * - 主题风格切换（浅色/深色/跟随系统）- 实时生效
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

    // 根据当前语言获取主题风格显示名称
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
        // 顶部导航栏
        NavBar(
            title = sampleStrings.settingsTitle,
            centerTitle = true,
            useDefaultBack = true,
            onBackClick = onBack,
            backgroundColor = navBarColor
        )

        // 设置内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            // 语言设置
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

            // 主题风格设置 - 横向卡片单选框
            SettingsCardSection(title = coreStrings.theme) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    // 第一行：浅色 + 深色
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
                    // 第二行：暗紫 + 跟随系统
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

            // 关于
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
                        value = "1.0.0"
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
 * 设置卡片分组
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
        // 分组标题
        Text(
            text = title,
            style = Typography.TitleMedium,
            color = colors.foreground
        )

        // 分组内容
        content()
    }
}

/**
 * 卡片样式单选项（纵向，带描述）
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
 * 卡片样式单选项（横向，紧凑）
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
 * 信息展示行
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
