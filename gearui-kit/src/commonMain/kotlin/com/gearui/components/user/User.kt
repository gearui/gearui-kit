package com.gearui.components.user

import androidx.compose.runtime.Composable
import com.gearui.components.avatar.Avatar
import com.gearui.foundation.interaction.PressableFeedback
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier

/** Identity summary sharing Avatar's picture and fallback rules; application account state stays outside it. */
@Composable
fun User(name: String, modifier: Modifier = Modifier, description: String? = null, avatarUrl: String? = null,
    fallback: String = name.take(2), onClick: (() -> Unit)? = null) {
    val body: @Composable () -> Unit = {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
            Avatar(fallback, url = avatarUrl, contentDescription = name)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(name, style = Theme.typography.markMedium, color = Theme.colors.foreground)
                if (description != null) Text(description, style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)
            }
        }
    }
    if (onClick != null) PressableFeedback(onClick, modifier.fillMaxWidth(), shape = Theme.shapes.md) { body() }
    else Box(modifier.fillMaxWidth()) { body() }
}
