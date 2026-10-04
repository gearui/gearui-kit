package com.gearui.foundation.typography

import com.tencent.kuikly.compose.ui.unit.TextUnit

/**
 * [lineHeight] as the renderer must receive it to grow with the user's font size the way
 * the font size does.
 *
 * KuiklyUI's Android renderer converts a font size as sp, so it follows the system font
 * size, but a line height as dp, so it does not: at a large font size the lines of a
 * wrapped text drew on top of each other. On Android this converts the line height as sp
 * too — through the platform, so Android 14's non-linear scaling of large sizes applies
 * to both alike. Elsewhere the renderer scales neither, and the value is returned as is.
 */
internal expect fun rendererLineHeight(lineHeight: TextUnit): TextUnit
