package com.gearui

/**
 * GearUI colour tokens (plain static constants).
 *
 * Principles:
 * - immutable
 * - stateless
 * - safe across runtimes
 * - serialisable
 */
object ColorTokens {

    // ============ Brand ramp (10 steps) ============

    /** Brand 1 - lightest */
    const val Brand1 = 0xFFF2F3FF.toInt()

    /** Brand 2 */
    const val Brand2 = 0xFFD9E1FF.toInt()

    /** Brand 3 */
    const val Brand3 = 0xFFB5C7FF.toInt()

    /** Brand 4 */
    const val Brand4 = 0xFF8EABFF.toInt()

    /** Brand 5 */
    const val Brand5 = 0xFF618DFF.toInt()

    /** Brand 6 */
    const val Brand6 = 0xFF366EF4.toInt()

    /** Brand 7 - the primary brand colour */
    const val Brand7 = 0xFF0052D9.toInt()

    /** Brand 8 */
    const val Brand8 = 0xFF003CAB.toInt()

    /** Brand 9 */
    const val Brand9 = 0xFF002A7C.toInt()

    /** Brand 10 - darkest */
    const val Brand10 = 0xFF001A57.toInt()

    // ============ Semantic brand colours ============

    /** Brand light, for backgrounds and similar */
    const val BrandLight = Brand1

    /** Brand focus */
    const val BrandFocus = Brand2

    /** Brand disabled */
    const val BrandDisabled = Brand3

    /** Brand hover */
    const val BrandHover = Brand6

    /** Brand normal */
    const val BrandNormal = Brand7

    /** Brand active */
    const val BrandClick = Brand8


    // ============ Error ramp (10 steps) ============

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


    // ============ Warning ramp (10 steps) ============

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


    // ============ Success ramp (10 steps) ============

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


    // ============ Neutral ramp (14 steps) ============

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


    // ============ Text colours ============

    /** Primary text (black at 90% opacity) */
    const val TextPrimary = 0xE6000000.toInt()

    /** Secondary text (black at 60% opacity) */
    const val TextSecondary = 0x99000000.toInt()

    /** Placeholder text (black at 40% opacity) */
    const val TextPlaceholder = 0x66000000.toInt()

    /** Disabled text (black at 25% opacity) */
    const val TextDisabled = 0x40000000.toInt()

    /** Brand text */
    const val TextBrand = BrandNormal

    /** Link text */
    const val TextLink = BrandNormal

    /** Primary text on dark (white at 90% opacity) */
    const val TextWhite1 = 0xE6FFFFFF.toInt()

    /** Secondary text on dark (white at 55% opacity) */
    const val TextWhite2 = 0x8CFFFFFF.toInt()

    /** Placeholder text on dark (white at 35% opacity) */
    const val TextWhite3 = 0x59FFFFFF.toInt()

    /** Disabled text on dark (white at 22% opacity) */
    const val TextWhite4 = 0x38FFFFFF.toInt()


    // ============ Background colours ============

    /** Page background */
    const val BgPage = Gray3

    /** Container background */
    const val BgContainer = Gray1

    /** Secondary container background */
    const val BgContainerSecondary = Gray2


    // ============ Border colours ============

    /** Component outline */
    const val Stroke = Gray6

    /** Component border */
    const val Border = Gray4


    // ============ Mask colours ============

    /** Mask background (black at 60% opacity) */
    const val MaskOverlay = 0x99000000.toInt()

    /** Mask background, dark (black at 80% opacity) */
    const val MaskOverlayDark = 0xCC000000.toInt()

    /** Mask background, light (black at 40% opacity) */
    const val MaskOverlayLight = 0x66000000.toInt()
}
