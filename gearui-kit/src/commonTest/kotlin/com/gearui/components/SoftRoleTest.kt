package com.gearui.components

import com.gearui.components.alert.AlertStatus
import com.gearui.components.alert.alertStatusColor
import com.gearui.components.noticebar.NoticeBarTone
import com.gearui.components.noticebar.noticeBarFill
import com.gearui.components.noticebar.noticeBarForeground
import com.gearui.theme.Colors
import com.gearui.theme.DefaultPalette
import com.gearui.theme.Themes
import com.gearui.theme.withBrandAccent
import com.tencent.kuikly.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Soft status roles: where they come from and who reads them. */
class SoftRoleTest {

    @Test
    fun builtInThemesTakeSoftRolesFromTheTokens() {
        val light = Themes.Light.colors
        assertEquals(DefaultPalette.lightAccentSoft, light.primarySoft)
        assertEquals(DefaultPalette.lightAccentSoftForeground, light.primarySoftForeground)
        assertEquals(DefaultPalette.lightWarningSoftForeground, light.warningSoftForeground)
        val dark = Themes.Dark.colors
        assertEquals(DefaultPalette.darkDangerSoft, dark.destructiveSoft)
        assertEquals(DefaultPalette.darkSuccessSoftForeground, dark.successSoftForeground)
    }

    @Test
    fun softFillsAreTheStatusColourAtFifteenPercent() {
        assertEquals(0.15f, Themes.Light.colors.primarySoft.alpha, 0.001f)
        assertEquals(0.15f, Themes.Dark.colors.warningSoft.alpha, 0.001f)
    }

    @Test
    fun aBrandAccentTakesTheSoftPairWithIt() {
        // withBrandAccent is the supported way to rebrand; the soft roles are derived
        // from the accent, so they must follow it rather than stay on the built-in blue.
        val purple = Color(0xFF7C3AED)
        val brand = Themes.Light.withBrandAccent(purple).colors
        assertEquals(0.15f, brand.primarySoft.alpha, 0.001f)
        assertEquals(purple.red, brand.primarySoft.red, 0.001f)
        assertTrue(brand.primarySoftForeground != Themes.Light.colors.primarySoftForeground)
    }

    @Test
    fun aHandBuiltThemeDerivesItsSoftRoles() {
        // Constructing Colors without the soft arguments derives them from the theme's
        // own accent, so a brand that never heard of soft roles still gets a matching pair.
        val base = Themes.Light.colors
        val brand = Colors(
            background = base.background, foreground = base.foreground,
            surface = base.surface, surfaceForeground = base.surfaceForeground,
            card = base.card, cardForeground = base.cardForeground,
            popover = base.popover, popoverForeground = base.popoverForeground,
            muted = base.muted, mutedForeground = base.mutedForeground,
            primary = Color(0xFF7C3AED), primaryForeground = Color.White,
            secondary = base.secondary, secondaryForeground = base.secondaryForeground,
            accent = base.accent, accentForeground = base.accentForeground,
            destructive = base.destructive, destructiveForeground = base.destructiveForeground,
            success = base.success, successForeground = base.successForeground,
            warning = base.warning, warningForeground = base.warningForeground,
            info = base.info, infoForeground = base.infoForeground,
            border = base.border, input = base.input, ring = base.ring,
        )
        assertEquals(0.15f, brand.primarySoft.alpha, 0.001f)
        assertEquals(Color(0xFF7C3AED).red, brand.primarySoft.red, 0.001f)
    }

    @Test
    fun alertAndNoticeBarReadTheSameRoles() {
        val colors: Colors = Themes.Light.colors
        assertEquals(colors.warningSoftForeground, alertStatusColor(colors, AlertStatus.WARNING))
        assertEquals(colors.warningSoftForeground, noticeBarForeground(colors, NoticeBarTone.WARNING))
        assertEquals(colors.warningSoft, noticeBarFill(colors, NoticeBarTone.WARNING))
        // The neutral tone is the muted fill, not a status colour.
        assertEquals(colors.muted, noticeBarFill(colors, NoticeBarTone.NEUTRAL))
        assertEquals(colors.foreground, alertStatusColor(colors, AlertStatus.DEFAULT))
    }
}
