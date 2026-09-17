package com.gearui.foundation.typography

import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.gearui.unit.TextUnit
import com.gearui.theme.Typographies
import com.tencent.kuikly.compose.ui.unit.sp

/**
 * TextStyle - text style token
 *
 * Contains:
 * - fontSize
 * - lineHeight
 * - fontWeight
 */
data class TextStyle(
    val fontSize: TextUnit,
    val lineHeight: TextUnit,
    val fontWeight: FontWeight,
    val fontFamily: List<String> = listOf("system-ui"),
    val letterSpacing: TextUnit = 0.sp,
)

/** Static defaults use the generated platform profile. Components read Theme.typography. */
object Typography {
    val DisplayLarge: TextStyle = Typographies.Default.displayLarge
    val DisplayMedium: TextStyle = Typographies.Default.displayMedium
    val HeadlineLarge: TextStyle = Typographies.Default.headlineLarge
    val HeadlineMedium: TextStyle = Typographies.Default.headlineMedium
    val HeadlineSmall: TextStyle = Typographies.Default.headlineSmall
    val TitleExtraLarge: TextStyle = Typographies.Default.titleExtraLarge
    val TitleLarge: TextStyle = Typographies.Default.titleLarge
    val TitleMedium: TextStyle = Typographies.Default.titleMedium
    val TitleSmall: TextStyle = Typographies.Default.titleSmall
    val BodyExtraLarge: TextStyle = Typographies.Default.bodyExtraLarge
    val BodyLarge: TextStyle = Typographies.Default.bodyLarge
    val BodyMedium: TextStyle = Typographies.Default.bodyMedium
    val BodySmall: TextStyle = Typographies.Default.bodySmall
    val BodyExtraSmall: TextStyle = Typographies.Default.bodyExtraSmall
    val MarkLarge: TextStyle = Typographies.Default.markLarge
    val MarkMedium: TextStyle = Typographies.Default.markMedium
    val MarkSmall: TextStyle = Typographies.Default.markSmall
    val MarkExtraSmall: TextStyle = Typographies.Default.markExtraSmall
    val LinkLarge: TextStyle = Typographies.Default.linkLarge
    val LinkMedium: TextStyle = Typographies.Default.linkMedium
    val LinkSmall: TextStyle = Typographies.Default.linkSmall
    val Caption: TextStyle = Typographies.Default.caption
    val Label: TextStyle = Typographies.Default.label
}
