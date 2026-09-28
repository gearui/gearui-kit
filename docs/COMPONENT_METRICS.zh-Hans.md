# GearUI 组件度量

[English](./COMPONENT_METRICS.md) | [简体中文](./COMPONENT_METRICS.zh-Hans.md)

由 `scripts/component_spec.py` 从 `tokens/controls.tokens.json` 生成，请勿手改：改 token 及其 `$extensions."com.gearui.source"` 后重新生成。取值规则见 [VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md) §2。理由（Why）列保持英文原文。

已标注来源：**171 / 210** 个控件 token。其余列在 `tokens/provenance-baseline.txt`，该清单只能缩小。

## accordion

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `accordionPadding` | 12 | 12 | — | HeroUI |  |
| `accordionSurfacePadding` | 20 | 20 | — | HeroUI |  |
| `accordionTriggerGap` | 16 | 16 | — | HeroUI |  |
| `accordionVerticalPadding` | 16 | 16 | — | HeroUI |  |

## action

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `actionSheetDescriptionRow` | 60 | | | _尚未标注_ | |
| `actionSheetGridRow` | 96 | | | _尚未标注_ | |
| `actionSheetRow` | 40 | | | _尚未标注_ | |

## alert

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `alertActionTop` | 8 | | | _尚未标注_ | |
| `alertGap` | 12 | 12 | — | HeroUI |  |
| `alertIcon` | 18 | 18 | — | HeroUI |  |
| `alertIndicatorOffset` | 3.5 | 3.5 | — | HeroUI |  |
| `alertPadding` | 12 | 12 | — | HeroUI |  |

## avatar

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `avatarGroupSize` | 32 | | | _尚未标注_ | |

## back

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `backTopHalfHeight` | 40 | — | — | GearUI | The half-circle tab that hugs the screen edge. |
| `backTopHalfWidth` | 24 | — | — | GearUI | The half-circle tab that hugs the screen edge. |

## bottom

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `bottomNavBadgeReserveHeight` | 16 | — | — | GearUI | Height of a small badge, reserved so a badge never moves the icon. |
| `bottomNavBadgeReserveWidth` | 30 | — | — | GearUI | Width of the widest small badge (99+), reserved so a badge never moves the icon. |
| `bottomNavHeight` | 56 | — | — | GearUI | The large control height, the same as a tab bar with labels on Android. |
| `bottomNavPillPaddingBlock` | 2 | — | — | GearUI | Vertical inset of the selected pill around icon and label. |

## button

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `buttonGapExtraSmall` | 5 | — | — | GearUI | Half a step below sm for the 28 compact tier, which HeroUI does not have. |
| `buttonGapLarge` | 10 | 10 | — | HeroUI |  |
| `buttonGapMedium` | 8 | 8 | — | HeroUI |  |
| `buttonGapSmall` | 6 | 6 | — | HeroUI |  |
| `buttonIconExtraSmall` | 12 | — | — | GearUI | Icon beside an extra-small button's label. |
| `buttonIconLarge` | 20 | — | — | GearUI | Icon beside a large button's label. |
| `buttonIconMedium` | 16 | — | — | GearUI | Icon beside a medium button's label, the body icon size. |
| `buttonIconSmall` | 14 | — | — | GearUI | Icon beside a small button's label. |
| `buttonPaddingExtraSmall` | 7 | — | — | GearUI | Half a step below sm for the 28 compact tier, which HeroUI does not have. |
| `buttonPaddingLarge` | 20 | 20 | — | HeroUI |  |
| `buttonPaddingMedium` | 16 | 16 | — | HeroUI |  |
| `buttonPaddingSmall` | 14 | 14 | — | HeroUI |  |

## calendar

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `calendarCellGap` | 2 | — | — | GearUI | A hairline of air between day cells so a selected range reads as separate days. |
| `calendarCellHeight` | 44 | — | — | GearUI | The 44 touch minimum; seven columns on a phone leave no room for a taller cell. |
| `calendarNavButton` | 32 | — | — | GearUI | The close-button size, for the month arrows beside the title. |

## card

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `cardPadding` | 16 | | | _尚未标注_ | |

## cascader

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `cascaderDropdownHeight` | 300 | — | — | GearUI | About six 48 option rows before a level scrolls. |

## checkbox

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `checkboxIndicatorTravel` | 4 | | | _尚未标注_ | |

## close

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `closeButtonIcon` | 18 | 18 | — | HeroUI |  |
| `closeButtonSize` | 32 | 32 | — | HeroUI |  |

## control

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `controlExtraSmall` | 28 | — | — | GearUI | HeroUI ships sm/md/lg only. The compact tier takes 28, the height of iOS 26's inline switch track, so chips and small actions line up with switches in the same row. |
| `controlLarge` | 56 | 56 | — | HeroUI |  |
| `controlMedium` | 48 | 48 | — | HeroUI |  |
| `controlSmall` | 40 | 40 | — | HeroUI |  |

## dialog

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `dialogActionGap` | 12 | 12 | — | HeroUI |  |
| `dialogActionsTop` | 20 | 20 | — | HeroUI |  |
| `dialogBodyMaxHeight` | 360 | — | — | GearUI | Scrolling body inside the dialog, so title and actions stay in view. |
| `dialogMaxHeight` | 560 | — | — | GearUI | Leaves the status bar and some page visible on the shortest supported phones. |
| `dialogMaxWidth` | 384 | — | — | GearUI | The reference sizes a dialog by the portal's 20 padding only; a cap keeps a tablet or wide window from stretching a card across the screen. |
| `dialogTextGap` | 6 | 6 | — | HeroUI |  |

## drawer

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `drawerWidth` | 280 | — | — | GearUI | Leaves 80 of a 360-wide phone showing, so the page underneath stays visible as the way back. |

## field

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `fieldLabelGap` | 6 | 6 | — | HeroUI |  |
| `fieldPaddingLarge` | 12 | | | _尚未标注_ | |
| `fieldPaddingMedium` | 12 | 12 | — | HeroUI |  |
| `fieldPaddingSmall` | 12 | | | _尚未标注_ | |

## form

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `formLabelWidth` | 80 | — | — | GearUI | Four CJK characters at body size plus a gap; longer labels belong in the vertical layout. |

## grid

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `gridMinColumnWidth` | 120 | — | — | GearUI | Three columns on a 360-wide phone for the responsive grid. |

## image

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `imageGalleryHeight` | 100 | — | — | GearUI | Thumbnail height in a three-column gallery on a phone. |

## input

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `inputClearSize` | 20 | — | — | GearUI | Smaller than the search field's 24 so the clear button does not crowd the text of a 48 field. |

## list

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `listCompactMinHeight` | 44 | — | 44 | iOS |  |
| `listCompactPadding` | 8 | | | _尚未标注_ | |
| `listItemGap` | 12 | 12 | — | HeroUI |  |
| `listMinHeight` | 52 | — | 52 | iOS |  |
| `listPaddingBlock` | 14 | 16 | 14 | iOS | HeroUI's 16 on both axes makes a 56pt row; iOS rows are 52. Splitting the axes keeps iOS's height and leading. |
| `listPaddingInline` | 20 | 16 | 20 | iOS | List rhythm follows the platform: text starts where iOS starts it, and the separator starts with the text. |

## loading

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `loadingDotLarge` | 10 | — | — | GearUI | Three dots fill the spinner's width with a dot's gap between them. |
| `loadingDotMedium` | 8 | — | — | GearUI | Three dots fill the spinner's width with a dot's gap between them. |
| `loadingDotSmall` | 6 | — | — | GearUI | Three dots fill the spinner's width with a dot's gap between them. |
| `loadingSizeLarge` | 32 | — | — | GearUI | A section or page placeholder. |
| `loadingSizeMedium` | 24 | — | — | GearUI | The standalone default, the size of a toolbar icon. |
| `loadingSizeSmall` | 16 | — | — | GearUI | Inline with body text. |
| `loadingStrokeLarge` | 4 | — | — | GearUI | About an eighth of the spinner's diameter at every size. |
| `loadingStrokeMedium` | 3 | — | — | GearUI | About an eighth of the spinner's diameter at every size. |
| `loadingStrokeSmall` | 2 | — | — | GearUI | About an eighth of the spinner's diameter at every size. |

## menu

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `menuItemGap` | 10 | 10 | — | HeroUI |  |
| `menuItemPaddingBlock` | 8 | 8 | — | HeroUI |  |
| `menuItemPaddingInline` | 10 | 10 | — | HeroUI |  |
| `menuMaxWidth` | 260 | — | — | GearUI | Leaves the page visible beside the menu on a 360-wide phone. |
| `menuMinWidth` | 160 | — | — | GearUI | Room for an icon and a four-character action; shared by ContextMenu and Popover menus, which disagreed (140 and 160). |
| `menuPaddingBlock` | 12 | 12 | — | HeroUI |  |
| `menuPaddingInline` | 6 | 6 | — | HeroUI |  |

## nav

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `navBarActionSlot` | 56 | — | — | GearUI | One 24 icon with 16 either side; both slots take it, so a one-sided bar still centres its title. |
| `navBarHeight` | 48 | — | — | GearUI | The 48 medium control row, so the back and action buttons get a full touch target and line up with a 48 field below the bar. |

## notice

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `noticeBarGap` | 8 | | | _尚未标注_ | |
| `noticeBarHeight` | 40 | | | _尚未标注_ | |
| `noticeBarPaddingInline` | 12 | | | _尚未标注_ | |
| `noticeBarScrollGap` | 48 | | | _尚未标注_ | |
| `noticeBarScrollSpeed` | 40 | | | _尚未标注_ | |

## otp

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `otpCaretMax` | 18 | | | _尚未标注_ | |
| `otpCaretMin` | 16 | | | _尚未标注_ | |
| `otpCaretWidth` | 2 | 2 | — | HeroUI |  |
| `otpGap` | 8 | 8 | — | HeroUI |  |
| `otpSeparatorHeight` | 2 | 2 | — | HeroUI |  |
| `otpSeparatorWidth` | 8 | 8 | — | HeroUI |  |
| `otpSlotHeight` | 48 | 48 | — | HeroUI |  |
| `otpSlotWidth` | 44 | 44 | — | HeroUI |  |

## overlay

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `overlayOffset` | 9 | 9 | — | HeroUI |  |
| `overlayPadding` | 20 | 20 | — | HeroUI |  |

## picker

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `pickerFadeHeight` | 80 | — | — | GearUI | Two rows of fade above and below the selection band. |
| `pickerItemHeight` | 40 | — | — | GearUI | Row pitch of the wheel; five fill the viewport. |
| `pickerWheelHeight` | 200 | — | — | GearUI | Five 40 rows: the selection with two above and two below. |

## popover

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `popoverArrowSize` | 8 | — | — | GearUI | Arrow depth; the arrow's base is twice this. |
| `popoverPaddingBlock` | 12 | 12 | — | HeroUI |  |
| `popoverPaddingInline` | 16 | 16 | — | HeroUI |  |

## progress

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `progressCircleSize` | 48 | — | — | GearUI | The medium control size, so a ring sits in a list row. |
| `progressCircleStroke` | 4 | — | — | GearUI | A twelfth of the ring's diameter. |
| `progressHeight` | 8 | — | — | GearUI | A track thick enough to read at a glance without looking like a control. |
| `progressLabelMinHeight` | 24 | — | — | GearUI | Room for an inline percentage label at caption size. |

## pull

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `pullRefreshThreshold` | 80 | | | _尚未标注_ | |

## radio

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `radioThumb` | 10 | 10 | — | HeroUI |  |

## radius

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `radiusDefault` | 14 | | | _尚未标注_ | |
| `radiusFull` | 9999 | 9999 | — | HeroUI |  |
| `radiusLarge` | 16 | 16 | — | HeroUI |  |
| `radiusMedium` | 12 | 12 | — | HeroUI |  |
| `radiusMenuItem` | 16 | 16 | — | HeroUI |  |
| `radiusNone` | 0 | — | — | GearUI | No rounding; the zero of the scale. |
| `radiusOverlay` | 24 | 24 | — | HeroUI |  |
| `radiusSheet` | 32 | 32 | — | HeroUI |  |
| `radiusSmall` | 8 | 8 | — | HeroUI |  |

## rate

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `rateStarGap` | 6 | | | _尚未标注_ | |
| `rateStarSize` | 24 | | | _尚未标注_ | |
| `rateStarSizeCompact` | 20 | | | _尚未标注_ | |

## scroll

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `scrollShadowSize` | 50 | 50 | — | HeroUI |  |

## search

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `searchClearSize` | 24 | 24 | — | HeroUI |  |

## select

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `selectContentPadding` | 12 | 12 | — | HeroUI |  |
| `selectIndicatorSlot` | 20 | | | _尚未标注_ | |
| `selectItemPadding` | 8 | 8 | — | HeroUI |  |
| `selectPanelOffset` | 8 | | | _尚未标注_ | |

## selection

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `selectionLarge` | 28 | — | — | GearUI | HeroUI's checkbox and radio ship one size (24); the small and large tiers step 4 either side so they sit with the 20/28 text and control tiers. |
| `selectionMedium` | 24 | 24 | — | HeroUI |  |
| `selectionSmall` | 20 | — | — | GearUI | HeroUI's checkbox and radio ship one size (24); the small and large tiers step 4 either side so they sit with the 20/28 text and control tiers. |
| `selectionTouchTarget` | 44 | — | 44 | iOS |  |

## separator

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `separatorThickness` | 1 | 0.33 | 1 | iOS | A hairline is one device pixel and needs a darker colour to be seen; iOS grouped lists rule at 1pt in a lighter colour. |

## sheet

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `sheetMaxListHeight` | 400 | — | — | GearUI | About eight 48 rows; a longer list scrolls inside the sheet instead of covering the page. |
| `sheetMenuPaddingInline` | 12 | | | _尚未标注_ | |
| `sheetMenuRowGap` | 8 | | | _尚未标注_ | |

## skeleton

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `skeletonArticleImage` | 150 | — | — | GearUI | Article illustration placeholder. |
| `skeletonAvatar` | 60 | — | — | GearUI | List-item avatar placeholder. |
| `skeletonBlock` | 100 | — | — | GearUI | Default square placeholder. |
| `skeletonButtonWidth` | 80 | — | — | GearUI | A short button placeholder. |
| `skeletonCardImage` | 200 | — | — | GearUI | Card cover placeholder, the Swiper banner height. |
| `skeletonLine` | 14 | — | — | GearUI | Body line placeholder, a little under the body line height so lines read as separate. |
| `skeletonListItemHeight` | 120 | — | — | GearUI | A list item with avatar, two lines and an image. |
| `skeletonTitleLine` | 18 | — | — | GearUI | Title line placeholder, a step above the body line. |

## slider

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `sliderCapsuleHeight` | 24 | | | _尚未标注_ | |
| `sliderCapsuleInset` | 3 | | | _尚未标注_ | |
| `sliderScaleDot` | 8 | — | — | GearUI | Tick mark on a stepped slider, the swiper's active dot size. |
| `sliderThumbHeight` | 20 | 20 | — | HeroUI |  |
| `sliderThumbInset` | 2 | 2 | — | HeroUI |  |
| `sliderThumbWidth` | 28 | 28 | — | HeroUI |  |
| `sliderTrackHeight` | 20 | 20 | — | HeroUI |  |
| `sliderValueLabelHeight` | 20 | — | — | GearUI | Room above the track for the thumb's value label. |

## stepper

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `stepperFieldWidth` | 180 | — | — | GearUI | Two buttons and a five-digit value. |
| `stepperSizeLarge` | 40 | — | — | GearUI | The small control height. |
| `stepperSizeMedium` | 32 | — | — | GearUI | Button size of the default stepper, the close-button size. |
| `stepperSizeSmall` | 24 | — | — | GearUI | Button size of the compact stepper. |

## steps

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `stepsConnectorMinHeight` | 48 | — | — | GearUI | Vertical connector between steps, the medium control height. |
| `stepsDotSize` | 12 | — | — | GearUI | The dot style's marker. |
| `stepsIconSize` | 32 | — | — | GearUI | The numbered marker, the close-button size. |

## swiper

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `swiperArrowSize` | 32 | — | — | GearUI | The close-button size: a compact round control that sits over media. |
| `swiperBarActive` | 20 | — | — | GearUI | The current indicator stretched into a bar, about three dots wide. |
| `swiperDot` | 6 | — | — | GearUI | Indicator dot; small enough for ten to fit on a banner without crowding it. |
| `swiperDotActive` | 8 | — | — | GearUI | The current dot grows by one step so position reads without colour. |
| `swiperDotGap` | 6 | — | — | GearUI | One dot's width between dots. |
| `swiperHeight` | 200 | — | — | GearUI | A banner at roughly 16:9 on a 360-wide phone. |
| `swiperIndicatorPaddingBlock` | 6 | — | — | GearUI | One dot's height above and below the indicator row. |

## switch

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `switchHeight` | 28 | 24 | 28 | iOS | The switch is the platform's signature control; next to iOS lists a 48pt switch reads as a web toggle. HeroUI's own switch is already the wide-thumb shape, so only the size moves. |
| `switchInset` | 2 | 2 | 2 | iOS |  |
| `switchThumbHeight` | 24 | 20 | 24 | iOS | The switch is the platform's signature control; next to iOS lists a 48pt switch reads as a web toggle. HeroUI's own switch is already the wide-thumb shape, so only the size moves. |
| `switchThumbWidth` | 37 | 28 | 37 | iOS | The switch is the platform's signature control; next to iOS lists a 48pt switch reads as a web toggle. HeroUI's own switch is already the wide-thumb shape, so only the size moves. |
| `switchWidth` | 63 | 48 | 63 | iOS | The switch is the platform's signature control; next to iOS lists a 48pt switch reads as a web toggle. HeroUI's own switch is already the wide-thumb shape, so only the size moves. |

## table

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `tableCellWidth` | 100 | — | — | GearUI | Four CJK characters at body size with cell padding; wider columns set their own width. |
| `tableSelectionColumn` | 56 | — | — | GearUI | A 24 checkbox with 16 either side. |

## tabs

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `tabsHeightLarge` | 52 | — | — | GearUI | The list row height, for tabs that head a list. |
| `tabsHeightMedium` | 44 | — | — | GearUI | The 44 touch minimum. |
| `tabsHeightSmall` | 36 | — | — | GearUI | The dense tier for toolbars and filters, below the 44 touch minimum only where the whole row is tappable. |
| `tabsIndicatorHeight` | 2 | 2 | — | HeroUI |  |
| `tabsListGap` | 4 | 4 | — | HeroUI |  |
| `tabsListPadding` | 3 | 3 | — | HeroUI |  |
| `tabsTriggerPaddingBlock` | 6 | 6 | — | HeroUI |  |

## tag

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `tagGroupGap` | 8 | 8 | — | HeroUI |  |
| `tagGroupLargePaddingBlock` | 6 | 6 | — | HeroUI |  |
| `tagGroupLargePaddingInline` | 12 | 12 | — | HeroUI |  |
| `tagGroupLargeRadius` | 24 | 24 | — | HeroUI |  |
| `tagGroupMediumPaddingBlock` | 4 | 4 | — | HeroUI |  |
| `tagGroupMediumPaddingInline` | 10 | 10 | — | HeroUI |  |
| `tagGroupMediumRadius` | 16 | 16 | — | HeroUI |  |
| `tagGroupRemoveIcon` | 12 | 12 | — | HeroUI |  |
| `tagGroupSmallPaddingBlock` | 2 | 2 | — | HeroUI |  |
| `tagGroupSmallPaddingInline` | 8 | 8 | — | HeroUI |  |
| `tagGroupSmallRadius` | 12 | 12 | — | HeroUI |  |
| `tagHeightLarge` | 36 | | | _尚未标注_ | |
| `tagHeightMedium` | 28 | | | _尚未标注_ | |
| `tagHeightSmall` | 20 | | | _尚未标注_ | |
| `tagPaddingLarge` | 16 | | | _尚未标注_ | |
| `tagPaddingMedium` | 12 | | | _尚未标注_ | |
| `tagPaddingSmall` | 8 | | | _尚未标注_ | |

## textarea

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `textareaCompactPaddingInline` | 10 | — | — | GearUI | The chat composer's inset, tighter than a form field's 12 so a bar keeps more text visible. |
| `textareaMinHeight` | 128 | 128 | — | HeroUI |  |
| `textareaPaddingVertical` | 8 | 8 | — | HeroUI |  |

## timeline

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `timelineAxisWidth` | 32 | — | — | GearUI | The column that holds the dot and the line. |
| `timelineConnectorMinHeight` | 48 | — | — | GearUI | Line between two events, the medium control height. |
| `timelineDot` | 12 | — | — | GearUI | The event marker. |

## toast

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `toastMaxWidth` | 280 | — | — | GearUI | Leaves 40 either side on a 360-wide phone; longer text wraps. |
| `toastMinWidth` | 120 | — | — | GearUI | Keeps a one-word toast from shrinking to a pill around the text. |
| `toastPadding` | 16 | | | _尚未标注_ | |

## tour

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `tourProgressHeight` | 4 | — | — | GearUI | Step progress under the tour card's text. |

## upload

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `uploadRemoveIcon` | 12 | | | _尚未标注_ | |
| `uploadRemoveSize` | 20 | | | _尚未标注_ | |
| `uploadTileGap` | 8 | | | _尚未标注_ | |
| `uploadTileSize` | 80 | | | _尚未标注_ | |

## 参考出处

- heroui-native 1.0.9 accordion.css trigger padding-inline
- heroui-native 1.0.9 accordion.css trigger surface padding-inline
- heroui-native 1.0.9 accordion.css trigger gap
- heroui-native 1.0.9 accordion.css trigger padding-block
- heroui-native 1.0.9 alert.css root gap
- heroui-native 1.0.9 alert.constants.ts DEFAULT_ICON_SIZE
- heroui-native 1.0.9 alert.css indicator padding-top
- heroui-native 1.0.9 alert.css root padding
- heroui-native 1.0.9 button.css size-lg gap
- heroui-native 1.0.9 button.css size-md gap
- heroui-native 1.0.9 button.css size-sm gap
- heroui-native 1.0.9 button.css size-lg padding-inline
- heroui-native 1.0.9 button.css size-md padding-inline
- heroui-native 1.0.9 button.css size-sm padding-inline
- heroui-native 1.0.9 close-button.tsx icon size
- heroui-native 1.0.9 close-button.css height
- heroui-native 1.0.9 button.css size-lg height
- heroui-native 1.0.9 button.css size-md height
- heroui-native 1.0.9 button.css size-sm height
- heroui-native 1.0.9 dialog.md example gap-3 between actions
- heroui-native 1.0.9 dialog.md example mb-5 above the actions
- heroui-native 1.0.9 dialog.md example gap-1.5 between title and description
- heroui-native 1.0.9 text-field.css root gap
- heroui-native 1.0.9 input.css padding-inline
- Apple HIG minimum hit target
- heroui-native 1.0.9 list-group.css item gap
- iOS 26.2 Settings, measured on simulator (row height)
- heroui-native 1.0.9 list-group.css item padding
- iOS 26.2 Settings, measured on simulator (52pt row, one 24pt text line centred)
- iOS 26.2 Settings, measured on simulator (content leading inside the card)
- heroui-native 1.0.9 menu.css item gap
- heroui-native 1.0.9 menu.css item padding-block
- heroui-native 1.0.9 menu.css item padding-inline
- heroui-native 1.0.9 menu.css content padding-block
- heroui-native 1.0.9 menu.css content padding-inline
- heroui-native 1.0.9 input-otp.css slot-caret width
- heroui-native 1.0.9 input-otp.css group gap
- heroui-native 1.0.9 input-otp.css separator height
- heroui-native 1.0.9 input-otp.css separator width
- heroui-native 1.0.9 input-otp.css slot height
- heroui-native 1.0.9 input-otp.css slot width
- heroui-native 1.0.9 menu/popover DEFAULT_OFFSET
- heroui-native 1.0.9 dialog.css content padding
- heroui-native 1.0.9 popover.css content padding-block
- heroui-native 1.0.9 popover.css content padding-inline
- heroui-native 1.0.9 radio.css indicator size
- heroui-native 1.0.9 switch.css border-radius 9999px
- heroui-native 1.0.9 theme.css --radius-2xl
- heroui-native 1.0.9 theme.css --radius-xl
- heroui-native 1.0.9 menu.css item --radius-2xl
- heroui-native 1.0.9 theme.css --radius-3xl (dialog, menu, popover)
- heroui-native 1.0.9 theme.css --radius-4xl
- heroui-native 1.0.9 theme.css --radius-lg
- heroui-native 1.0.9 scroll-shadow.constants.ts DEFAULT_SHADOW_SIZE
- heroui-native 1.0.9 search-field.css clear button size
- heroui-native 1.0.9 select.css content padding
- heroui-native 1.0.9 select.css item padding-inline
- heroui-native 1.0.9 checkbox.css / radio.css root size
- heroui-native 1.0.9 separator.css hairlineWidth() at 3x
- iOS 26.2 Settings, measured on simulator (grouped list separator)
- heroui-native 1.0.9 slider.css thumb height (horizontal)
- heroui-native 1.0.9 slider.css thumb-container padding
- heroui-native 1.0.9 slider.css thumb width (horizontal)
- heroui-native 1.0.9 slider.css track height
- heroui-native 1.0.9 switch.css root height
- iOS 26.2 Settings, measured on simulator
- heroui-native 1.0.9 switch.css thumb inset
- heroui-native 1.0.9 switch.css thumb height
- heroui-native 1.0.9 switch.css thumb width
- heroui-native 1.0.9 switch.css root width
- heroui-native 1.0.9 tabs.css secondary indicator border-bottom
- heroui-native 1.0.9 tabs.css list gap
- heroui-native 1.0.9 tabs.css list primary padding
- heroui-native 1.0.9 tabs.css trigger padding-block
- heroui-native 1.0.9 tag-group.css list gap
- heroui-native 1.0.9 tag-group.css tag size-lg padding
- heroui-native 1.0.9 tag-group.css tag size-lg padding-inline
- heroui-native 1.0.9 tag-group.css tag size-lg --radius-3xl
- heroui-native 1.0.9 tag-group.css tag size-md padding
- heroui-native 1.0.9 tag-group.css tag size-md padding-inline
- heroui-native 1.0.9 tag-group.css tag size-md --radius-2xl
- heroui-native 1.0.9 tag-group.tsx remove icon size
- heroui-native 1.0.9 tag-group.css tag size-sm padding
- heroui-native 1.0.9 tag-group.css tag size-sm padding-inline
- heroui-native 1.0.9 tag-group.css tag size-sm --radius-xl
- heroui-native 1.0.9 text-area.css height
- heroui-native 1.0.9 text-area.css padding-block
