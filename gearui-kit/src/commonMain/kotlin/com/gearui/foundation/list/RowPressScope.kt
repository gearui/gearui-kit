package com.gearui.foundation.list

import androidx.compose.runtime.compositionLocalOf
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource

/**
 * The interaction source a container hands to the row it is about to place.
 *
 * A row cannot hide the separators around it: they are drawn by the container, above and
 * below its own bounds. So the container owns the sources, gives one to each row through
 * this local, and watches them — which is what lets a pressed row's fill run through the
 * lines on either side of it, the way the platform's lists do.
 *
 * Null means "nobody is watching": the row makes its own source, as a standalone
 * [com.gearui.components.cell.Cell] does.
 */
internal val LocalRowInteractionSource = compositionLocalOf<MutableInteractionSource?> { null }
