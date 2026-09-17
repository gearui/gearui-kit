package com.gearui.components.button

/**
 * Button size
 */
enum class ButtonSize {
    /** large - 52dp tall */
    LARGE,

    /** medium - 44dp tall (default) */
    MEDIUM,

    /** small - 36dp tall */
    SMALL,

    /** extra small - 28dp tall */
    EXTRA_SMALL
}

/**
 * Button type
 */
enum class ButtonType {
    /** filled button (default) */
    FILL,

    /** outlined button */
    OUTLINE,

    /** text button */
    TEXT
}

/**
 * Button colour theme
 */
enum class ButtonTheme {
    /** primary / brand (default); use for the main action */
    PRIMARY,

    /** danger */
    DANGER,

    /** warning */
    WARNING,

    /** success */
    SUCCESS,

    /** neutral; use for secondary actions */
    DEFAULT,

    /** light / tinted */
    LIGHT
}

/**
 * Button shape
 */
enum class ButtonShape {
    /** rectangle (default, size-dependent radius) */
    RECTANGLE,

    /** rounded ends (half the control height) */
    ROUND,

    /** square icon target with size-dependent radius */
    SQUARE,

    /** circle (fully rounded) */
    CIRCLE,

    /** pill (maximum radius) */
    FILLED
}

/**
 * Icon position
 */
enum class ButtonIconPosition {
    /** leading */
    LEFT,

    /** trailing */
    RIGHT
}
