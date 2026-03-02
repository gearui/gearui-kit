package com.gearui

/**
 * GearUI 颜色 Token（纯静态常量）
 *
 * 设计原则：
 * - 不可变
 * - 无状态
 * - 跨 runtime 安全
 * - 可序列化
 */
object ColorTokens {

    // ============ 品牌色系（10 级） ============

    /** 品牌色 1 - 最浅 */
    const val Brand1 = 0xFFF2F3FF.toInt()

    /** 品牌色 2 */
    const val Brand2 = 0xFFD9E1FF.toInt()

    /** 品牌色 3 */
    const val Brand3 = 0xFFB5C7FF.toInt()

    /** 品牌色 4 */
    const val Brand4 = 0xFF8EABFF.toInt()

    /** 品牌色 5 */
    const val Brand5 = 0xFF618DFF.toInt()

    /** 品牌色 6 */
    const val Brand6 = 0xFF366EF4.toInt()

    /** 品牌色 7 - 主品牌色 */
    const val Brand7 = 0xFF0052D9.toInt()

    /** 品牌色 8 */
    const val Brand8 = 0xFF003CAB.toInt()

    /** 品牌色 9 */
    const val Brand9 = 0xFF002A7C.toInt()

    /** 品牌色 10 - 最深 */
    const val Brand10 = 0xFF001A57.toInt()

    // ============ 语义化品牌色 ============

    /** 品牌浅色（用于背景等） */
    const val BrandLight = Brand1

    /** 品牌聚焦色 */
    const val BrandFocus = Brand2

    /** 品牌禁用色 */
    const val BrandDisabled = Brand3

    /** 品牌悬停色 */
    const val BrandHover = Brand6

    /** 品牌常规色 */
    const val BrandNormal = Brand7

    /** 品牌点击色 */
    const val BrandClick = Brand8


    // ============ 错误色系（10 级） ============

    const val Error1 = 0xFFFDECEE.toInt()
    const val Error2 = 0xFFF9D7D9.toInt()
    const val Error3 = 0xFFF8B9BE.toInt()
    const val Error4 = 0xFFF78D94.toInt()
    const val Error5 = 0xFFF36D78.toInt()
    const val Error6 = 0xFFE34D59.toInt()
    const val Error7 = 0xFFC9353F.toInt()
    const val Error8 = 0xFFB11F26.toInt()
    const val Error9 = 0xFF951114.toInt()
    const val Error10 = 0xFF680506.toInt()

    const val ErrorLight = Error1
    const val ErrorFocus = Error2
    const val ErrorDisabled = Error3
    const val ErrorHover = Error5
    const val ErrorNormal = Error6
    const val ErrorClick = Error7


    // ============ 警告色系（10 级） ============

    const val Warning1 = 0xFFFEF3E6.toInt()
    const val Warning2 = 0xFFF9E0C7.toInt()
    const val Warning3 = 0xFFF7C797.toInt()
    const val Warning4 = 0xFFF2995F.toInt()
    const val Warning5 = 0xFFE37318.toInt()
    const val Warning6 = 0xFFD35A21.toInt()
    const val Warning7 = 0xFFBA431B.toInt()
    const val Warning8 = 0xFFA0300C.toInt()
    const val Warning9 = 0xFF842109.toInt()
    const val Warning10 = 0xFF5A1907.toInt()

    const val WarningLight = Warning1
    const val WarningFocus = Warning2
    const val WarningDisabled = Warning3
    const val WarningHover = Warning4
    const val WarningNormal = Warning5
    const val WarningClick = Warning6


    // ============ 成功色系（10 级） ============

    const val Success1 = 0xFFE8F8F2.toInt()
    const val Success2 = 0xFFBCEBDC.toInt()
    const val Success3 = 0xFF85DBBE.toInt()
    const val Success4 = 0xFF48C79C.toInt()
    const val Success5 = 0xFF00A870.toInt()
    const val Success6 = 0xFF078D5C.toInt()
    const val Success7 = 0xFF067945.toInt()
    const val Success8 = 0xFF056334.toInt()
    const val Success9 = 0xFF044F2A.toInt()
    const val Success10 = 0xFF033A20.toInt()

    const val SuccessLight = Success1
    const val SuccessFocus = Success2
    const val SuccessDisabled = Success3
    const val SuccessHover = Success4
    const val SuccessNormal = Success5
    const val SuccessClick = Success6


    // ============ 中性色系（14 级） ============

    const val Gray1 = 0xFFFFFFFF.toInt()
    const val Gray2 = 0xFFF9F9F9.toInt()
    const val Gray3 = 0xFFF5F5F5.toInt()
    const val Gray4 = 0xFFF0F0F0.toInt()
    const val Gray5 = 0xFFEBEBEB.toInt()
    const val Gray6 = 0xFFDCDCDC.toInt()
    const val Gray7 = 0xFFD4D4D4.toInt()
    const val Gray8 = 0xFFBBBBBB.toInt()
    const val Gray9 = 0xFF999999.toInt()
    const val Gray10 = 0xFF777777.toInt()
    const val Gray11 = 0xFF555555.toInt()
    const val Gray12 = 0xFF444444.toInt()
    const val Gray13 = 0xFF333333.toInt()
    const val Gray14 = 0xFF000000.toInt()


    // ============ 文本色系 ============

    /** 主要文本色（黑色 90% 透明度） */
    const val TextPrimary = 0xE6000000.toInt()

    /** 次要文本色（黑色 60% 透明度） */
    const val TextSecondary = 0x99000000.toInt()

    /** 占位文本色（黑色 40% 透明度） */
    const val TextPlaceholder = 0x66000000.toInt()

    /** 禁用文本色（黑色 25% 透明度） */
    const val TextDisabled = 0x40000000.toInt()

    /** 品牌文本色 */
    const val TextBrand = BrandNormal

    /** 链接文本色 */
    const val TextLink = BrandNormal

    /** 白色主文本（白色 90% 透明度） */
    const val TextWhite1 = 0xE6FFFFFF.toInt()

    /** 白色次文本（白色 55% 透明度） */
    const val TextWhite2 = 0x8CFFFFFF.toInt()

    /** 白色占位文本（白色 35% 透明度） */
    const val TextWhite3 = 0x59FFFFFF.toInt()

    /** 白色禁用文本（白色 22% 透明度） */
    const val TextWhite4 = 0x38FFFFFF.toInt()


    // ============ 背景色系 ============

    /** 页面背景色 */
    const val BgPage = Gray3

    /** 容器背景色 */
    const val BgContainer = Gray1

    /** 次要容器背景色 */
    const val BgContainerSecondary = Gray2


    // ============ 边框色系 ============

    /** 组件描边色 */
    const val Stroke = Gray6

    /** 组件边框色 */
    const val Border = Gray4


    // ============ 蒙层色系 ============

    /** 蒙层背景（黑色 60% 透明度） */
    const val MaskOverlay = 0x99000000.toInt()

    /** 蒙层背景深色（黑色 80% 透明度） */
    const val MaskOverlayDark = 0xCC000000.toInt()

    /** 蒙层背景浅色（黑色 40% 透明度） */
    const val MaskOverlayLight = 0x66000000.toInt()
}
