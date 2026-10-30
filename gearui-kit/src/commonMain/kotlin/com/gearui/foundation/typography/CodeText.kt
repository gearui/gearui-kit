package com.gearui.foundation.typography

import androidx.compose.runtime.Composable
import com.gearui.theme.Theme

/**
 * Monospace text for code, commands and tool arguments: the small body size, so a code
 * line and a line number of the same style share one line height and stay aligned.
 */
@Composable
internal fun codeTextStyle(): TextStyle = Theme.typography.bodySmall.copy(fontFamily = listOf("monospace"))
