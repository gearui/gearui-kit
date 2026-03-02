package com.gearui.foundation.animation

/**
 * GearUI 动画系统
 *
 * 统一管理所有组件的动画规范，确保整个设计系统的动画风格一致。
 *
 * 设计原则：
 * - 快速响应：短时长动画（80-150ms）用于即时反馈
 * - 平滑过渡：中等时长（200-300ms）用于状态切换
 * - 自然流畅：使用 ease 曲线而非线性
 * - 性能优先：避免过度动画
 */
object Animation {

    // ============ 时长定义 ============

    /**
     * 极快动画 - 40ms
     * 用于：微小的视觉反馈（如波纹扩散的开始）
     */
    const val DURATION_INSTANT = 40

    /**
     * 快速动画 - 80ms
     * 用于：按钮按下、开关切换等即时反馈
     */
    const val DURATION_FAST = 80

    /**
     * 标准动画 - 150ms
     * 用于：淡入淡出、透明度变化、小幅位移
     */
    const val DURATION_NORMAL = 150

    /**
     * 中等动画 - 200ms
     * 用于：卡片展开、列表项滑动
     */
    const val DURATION_MEDIUM = 200

    /**
     * 慢速动画 - 300ms
     * 用于：页面过渡、抽屉展开/收起
     */
    const val DURATION_SLOW = 300

    /**
     * 超慢动画 - 500ms
     * 用于：复杂的多阶段动画
     */
    const val DURATION_VERY_SLOW = 500

    // ============ 缓动曲线 ============

    /**
     * 标准缓动 - Ease
     * 最常用的曲线，适合大部分场景
     */
    const val EASING_STANDARD = "ease"

    /**
     * 缓入 - Ease In
     * 用于：元素离开屏幕
     */
    const val EASING_EASE_IN = "ease-in"

    /**
     * 缓出 - Ease Out
     * 用于：元素进入屏幕
     */
    const val EASING_EASE_OUT = "ease-out"

    /**
     * 缓入缓出 - Ease In Out
     * 用于：元素在屏幕内移动
     */
    const val EASING_EASE_IN_OUT = "ease-in-out"

    /**
     * 线性 - Linear
     * 用于：旋转、进度条等匀速动画
     */
    const val EASING_LINEAR = "linear"

    /**
     * 弹性 - Spring
     * 用于：有物理感的交互（iOS 风格）
     */
    const val EASING_SPRING = "spring"

    // ============ 预设动画配置 ============

    /**
     * 按钮按下动画
     * - 时长：80ms
     * - 曲线：Ease Out
     * - 效果：缩放到 0.98
     */
    object Press {
        const val duration = DURATION_FAST
        const val easing = EASING_EASE_OUT
        const val scaleTarget = 0.98f
    }

    /**
     * 淡入淡出动画
     * - 时长：150ms
     * - 曲线：Ease In Out
     */
    object Fade {
        const val duration = DURATION_NORMAL
        const val easing = EASING_EASE_IN_OUT
    }

    /**
     * 缩放动画
     * - 时长：200ms
     * - 曲线：Ease Out
     */
    object Scale {
        const val duration = DURATION_MEDIUM
        const val easing = EASING_EASE_OUT
    }

    /**
     * 滑动动画
     * - 时长：200ms
     * - 曲线：Ease In Out
     */
    object Slide {
        const val duration = DURATION_MEDIUM
        const val easing = EASING_EASE_IN_OUT
    }

    /**
     * 旋转动画
     * - 时长：300ms
     * - 曲线：Linear
     */
    object Rotate {
        const val duration = DURATION_SLOW
        const val easing = EASING_LINEAR
    }

    /**
     * 弹性动画
     * - 时长：500ms
     * - 曲线：Spring
     */
    object Bounce {
        const val duration = DURATION_VERY_SLOW
        const val easing = EASING_SPRING
    }

    /**
     * 展开/收起动画
     * - 时长：300ms
     * - 曲线：Ease In Out
     */
    object Expand {
        const val duration = DURATION_SLOW
        const val easing = EASING_EASE_IN_OUT
    }

    /**
     * 波纹动画
     * - 时长：200ms
     * - 曲线：Ease Out
     */
    object Ripple {
        const val duration = DURATION_MEDIUM
        const val easing = EASING_EASE_OUT
        const val initialAlpha = 0.2f
        const val maxRadius = 300f
    }

    /**
     * 加载动画
     * - 时长：1000ms（循环）
     * - 曲线：Linear
     */
    object Loading {
        const val duration = 1000
        const val easing = EASING_LINEAR
    }

    // ============ 动画参数计算 ============

    /**
     * 根据距离计算动画时长
     *
     * @param distance 移动距离（dp）
     * @param baseDistance 基准距离（dp）
     * @param baseDuration 基准时长（ms）
     * @return 计算后的时长
     */
    fun calculateDurationByDistance(
        distance: Float,
        baseDistance: Float = 100f,
        baseDuration: Int = DURATION_MEDIUM
    ): Int {
        val ratio = (distance / baseDistance).coerceIn(0.5f, 2.0f)
        return (baseDuration * ratio).toInt()
    }

    /**
     * 根据元素大小计算动画时长
     *
     * @param size 元素大小
     * @param baseSize 基准大小
     * @param baseDuration 基准时长
     * @return 计算后的时长
     */
    fun calculateDurationBySize(
        size: Float,
        baseSize: Float = 100f,
        baseDuration: Int = DURATION_MEDIUM
    ): Int {
        val ratio = (size / baseSize).coerceIn(0.8f, 1.5f)
        return (baseDuration * ratio).toInt()
    }
}

/**
 * 动画延迟策略
 */
object AnimationDelay {

    /**
     * 无延迟
     */
    const val NONE = 0

    /**
     * 极短延迟 - 50ms
     */
    const val TINY = 50

    /**
     * 短延迟 - 100ms
     */
    const val SHORT = 100

    /**
     * 中等延迟 - 200ms
     */
    const val MEDIUM = 200

    /**
     * 长延迟 - 300ms
     */
    const val LONG = 300

    /**
     * 计算列表项的交错延迟
     *
     * @param index 项索引
     * @param baseDelay 基础延迟（ms）
     * @return 计算后的延迟
     */
    fun calculateStaggerDelay(index: Int, baseDelay: Int = 30): Int {
        return index * baseDelay
    }
}

/**
 * 动画组合策略
 */
object AnimationCombination {

    /**
     * 顺序执行
     */
    const val SEQUENTIAL = "sequential"

    /**
     * 并行执行
     */
    const val PARALLEL = "parallel"

    /**
     * 交错执行
     */
    const val STAGGER = "stagger"
}
