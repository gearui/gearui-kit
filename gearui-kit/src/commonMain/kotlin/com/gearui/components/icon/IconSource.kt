package com.gearui.components.icon

import com.tencent.kuikly.compose.ui.graphics.Path

/**
 * An icon that [com.gearui.foundation.primitives.Icon] can draw: GearUI's own [Icons], a
 * third-party pack, or an app's set.
 *
 * Open on purpose. A pack is any object whose members return an [IconSource]:
 *
 * ```kotlin
 * object CustomIcons {
 *     val logo: IconSource get() = ImageIcon("logo", "assets://icons/logo.png")
 *     val spark: IconSource get() = VectorIcon("spark", "M128 16L…Z", fill = "M…Z")
 * }
 * Icon(CustomIcons.spark, fill = selected)
 * ```
 *
 * Declare icons as getters, as above, not stored properties: an object initialises its
 * stored properties together, so using one would keep every icon of the pack in the app.
 */
interface IconSource {
    /** A stable name, for diagnostics; never shown. */
    val name: String

    /** What to draw: the fill form when [fill] is asked for and the pack has one, else the regular form. */
    fun resolve(fill: Boolean): IconAsset
}

/** One drawable form of an icon. GearUI knows how to draw each kind. */
sealed interface IconAsset

/**
 * Path data drawn by GearUI on a canvas, sharp at any size and never shipped unless used.
 *
 * [path] is absolute `M x y`, `L x y`, `Q x1 y1 x y`, `C x1 y1 x2 y2 x y` and `Z`
 * commands in a [viewport]-sized square, filled with the non-zero rule (holes are
 * contours of the opposite direction). `scripts/gen_vector_icons.py` writes it from
 * an SVG.
 */
class VectorAsset(val path: String, val viewport: Float) : IconAsset

/** An image the platform loads (PNG and the like), e.g. `assets://icons/logo.png`. */
class ImageAsset(val url: String) : IconAsset

/** A vector icon with an optional fill form. */
class VectorIcon(
    override val name: String,
    private val regular: String,
    private val fill: String? = null,
    private val viewport: Float = 256f,
) : IconSource {
    override fun resolve(fill: Boolean): IconAsset =
        VectorAsset(if (fill) this.fill ?: regular else regular, viewport)

    // Equal by content: a getter builds a new instance on every read.
    override fun equals(other: Any?): Boolean = other is VectorIcon &&
        name == other.name && regular == other.regular && fill == other.fill && viewport == other.viewport
    override fun hashCode(): Int = name.hashCode() * 31 + regular.hashCode()
    override fun toString(): String = "VectorIcon($name)"
}

/** An image icon with an optional fill form, for packs shipped as images. */
class ImageIcon(
    override val name: String,
    private val regular: String,
    private val fill: String? = null,
) : IconSource {
    override fun resolve(fill: Boolean): IconAsset = ImageAsset(if (fill) this.fill ?: regular else regular)

    override fun equals(other: Any?): Boolean = other is ImageIcon &&
        name == other.name && regular == other.regular && fill == other.fill
    override fun hashCode(): Int = name.hashCode() * 31 + regular.hashCode()
    override fun toString(): String = "ImageIcon($name)"
}

/** [VectorAsset.path] as a Path scaled from its viewport to [size] pixels. */
internal fun vectorPath(path: String, viewport: Float, size: Float): Path {
    val scale = size / viewport
    val out = Path()
    var i = 0
    val n = path.length
    val args = FloatArray(6)
    fun number(): Float {
        while (i < n && path[i] == ' ') i++
        val start = i
        if (i < n && (path[i] == '-' || path[i] == '+')) i++
        while (i < n && (path[i].isDigit() || path[i] == '.')) i++
        return path.substring(start, i).toFloat() * scale
    }
    while (i < n) {
        val cmd = path[i++]
        val count = when (cmd) { 'M', 'L' -> 2; 'Q' -> 4; 'C' -> 6; 'Z' -> 0; ' ' -> -1; else -> error("bad path command $cmd") }
        if (count < 0) continue
        for (k in 0 until count) args[k] = number()
        when (cmd) {
            'M' -> out.moveTo(args[0], args[1])
            'L' -> out.lineTo(args[0], args[1])
            'Q' -> out.quadraticBezierTo(args[0], args[1], args[2], args[3])
            'C' -> out.cubicTo(args[0], args[1], args[2], args[3], args[4], args[5])
            'Z' -> out.close()
        }
    }
    return out
}

/**
 * This icon in its fill form wherever it is drawn — for a component parameter, where
 * there is no `fill` switch: `Button(icon = Icons.star.filled)`. Without a fill form
 * it draws the regular one.
 */
val IconSource.filled: IconSource get() = FilledIcon(this)

private class FilledIcon(private val base: IconSource) : IconSource {
    override val name: String get() = base.name
    override fun resolve(fill: Boolean): IconAsset = base.resolve(true)
    override fun equals(other: Any?): Boolean = other is FilledIcon && base == other.base
    override fun hashCode(): Int = base.hashCode() * 31 + 1
}
