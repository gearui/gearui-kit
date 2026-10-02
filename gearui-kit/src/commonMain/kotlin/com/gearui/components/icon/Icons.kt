package com.gearui.components.icon

/**
 * GearUI's icons — [Phosphor](https://phosphoricons.com), every icon in its regular
 * form with its fill form alongside:
 *
 * ```kotlin
 * import com.gearui.components.icon.*
 *
 * Icon(Icons.house)
 * Icon(Icons.heart, fill = liked)
 * Button(icon = Icons.star.filled, …)
 * ```
 *
 * Each icon is an extension property, generated into `generated/` by
 * `scripts/gen_vector_icons.py` from Phosphor's SVGs as path data, and drawn by GearUI —
 * sharp at any size. Being code, an icon nothing references is left out of the app by
 * the compiler (Kotlin/Native and Kotlin/JS always; Android when R8 runs), so the whole
 * set costs only what is used. That is also why they need an import, as Compose's
 * Material icons do.
 *
 * Other packs follow the same contract: an object whose getters return an
 * [IconSource] — a [VectorIcon] (path data) or an [ImageIcon] (an image the app ships).
 */
object Icons
