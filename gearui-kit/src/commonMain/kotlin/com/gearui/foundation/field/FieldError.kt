package com.gearui.foundation.field

import androidx.compose.runtime.Composable
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color

/**
 * Shared error presentation for field-like components.
 *
 * Every field in the family says "this value is wrong" the same two ways: the
 * border turns [com.gearui.theme.Colors.destructive], and the message appears
 * below the control in `BodySmall`. Input and Select each had their own copy
 * of that; the rest of the family had no error support at all, so callers
 * either dropped the message or rendered their own, which is how a single
 * visual rule turns into six.
 *
 * The API is `error: String? = null` across the family — null means valid.
 * Deliberately not a `status` enum: warning and success states do not exist
 * yet, and inventing the general shape before there is a second case makes
 * every call site pay for it.
 */

/**
 * Border colour for a field trigger.
 *
 * [active] means the control currently owns the interaction — a dropdown that
 * is open. Precedence is error, then active: an invalid field stays red while
 * its panel is open.
 *
 * Disabled deliberately keeps the normal border. `mutedForeground` is a
 * secondary *text* colour and is far darker than `border`, so using it for a
 * disabled outline made disabled fields read as more prominent than enabled
 * ones. Disabled is signalled by the muted background and muted content,
 * never by a heavier outline.
 *
 * Note this is *not* wired to focus. Input must not vary its border with
 * focus on Kuikly; see [FieldTokens] for why.
 */
@Composable
fun fieldBorderColor(
    error: String? = null,
    enabled: Boolean = true,
    active: Boolean = false,
): Color {
    val colors = Theme.colors
    return when {
        error != null -> colors.destructive
        active && enabled -> colors.primary
        else -> colors.border
    }
}

/**
 * The message under a field. Renders nothing when [error] is null, so call
 * sites can place it unconditionally.
 */
@Composable
fun FieldErrorText(error: String?) {
    if (error == null) return
    Text(
        text = error,
        style = Typography.BodySmall,
        color = Theme.colors.destructive,
        modifier = Modifier.padding(top = Spacing.xs),
    )
}
