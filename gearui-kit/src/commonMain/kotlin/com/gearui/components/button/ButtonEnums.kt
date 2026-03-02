package com.gearui.components.button

/**
 * 按钮尺寸
 */
enum class ButtonSize {
    /** 大尺寸 - 48dp高度 */
    LARGE,

    /** 中等尺寸 - 40dp高度（默认） */
    MEDIUM,

    /** 小尺寸 - 32dp高度 */
    SMALL,

    /** 超小尺寸 - 28dp高度 */
    EXTRA_SMALL
}

/**
 * 按钮类型
 */
enum class ButtonType {
    /** 填充按钮（默认） */
    FILL,

    /** 描边按钮 */
    OUTLINE,

    /** 文本按钮 */
    TEXT,

    /** 幽灵按钮（透明背景） */
    GHOST
}

/**
 * 按钮主题色
 */
enum class ButtonTheme {
    /** 主要/品牌色（默认） */
    PRIMARY,

    /** 危险色 */
    DANGER,

    /** 警告色 */
    WARNING,

    /** 成功色 */
    SUCCESS,

    /** 默认/灰色 */
    DEFAULT,

    /** 浅色/淡色 */
    LIGHT
}

/**
 * 按钮形状
 */
enum class ButtonShape {
    /** 矩形（默认，圆角6dp） */
    RECTANGLE,

    /** 圆角矩形（圆角9dp） */
    ROUND,

    /** 正方形（无圆角） */
    SQUARE,

    /** 圆形（完全圆角） */
    CIRCLE,

    /** 填充圆角（最大圆角，形成胶囊状） */
    FILLED
}

/**
 * 图标位置
 */
enum class ButtonIconPosition {
    /** 左侧 */
    LEFT,

    /** 右侧 */
    RIGHT
}
