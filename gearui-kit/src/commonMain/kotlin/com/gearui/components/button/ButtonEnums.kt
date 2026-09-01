package com.gearui.components.button

/**
 * Button size
 */
enum class ButtonSize {
    /** large - 48dp tall */
    LARGE,

    /** medium - 40dp tall (default) */
    MEDIUM,

    /** small - 32dp tall */
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
    /** primary / brand (default) */
    PRIMARY,

    /** danger */
    DANGER,

    /** warning */
    WARNING,

    /** success */
    SUCCESS,

    /** default / grey */
    DEFAULT,

    /** light / tinted */
    LIGHT
}

/**
 * Button shape
 */
enum class ButtonShape {
    /** rectangle (default, 6dp radius) */
    RECTANGLE,

    /** rounded rectangle (9dp radius) */
    ROUND,

    /** square (no radius) */
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
