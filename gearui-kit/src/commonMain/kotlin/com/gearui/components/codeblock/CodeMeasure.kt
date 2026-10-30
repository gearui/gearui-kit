package com.gearui.components.codeblock

/**
 * Whether the renderer measures a text's height against a bounded width even when the
 * text is given a wider one. Kuikly's Web renderer does: a code block measured its
 * unwrapped lines as if wrapped and left empty lines under them, so there the height is
 * set from the line count. Native renderers measure at the given width.
 */
internal expect val measuresTextAtBoundedWidth: Boolean
