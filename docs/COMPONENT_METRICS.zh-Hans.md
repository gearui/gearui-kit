# GearUI 组件度量

[English](./COMPONENT_METRICS.md) | [简体中文](./COMPONENT_METRICS.zh-Hans.md)

由 `scripts/component_spec.py` 从 `tokens/controls.tokens.json` 生成，请勿手改：改 token 及其 `$extensions."com.gearui.source"` 后重新生成。取值规则见 [VISUAL_SPEC.zh-Hans.md](./VISUAL_SPEC.zh-Hans.md) §2。理由（Why）列保持英文原文。

已标注来源：**54 / 133** 个控件 token。其余列在 `tokens/provenance-baseline.txt`，该清单只能缩小。

## accordion

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `accordionPadding` | 12 | | | _尚未标注_ | |
| `accordionSurfacePadding` | 20 | | | _尚未标注_ | |
| `accordionTriggerGap` | 16 | | | _尚未标注_ | |
| `accordionVerticalPadding` | 16 | | | _尚未标注_ | |

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
| `alertGap` | 12 | | | _尚未标注_ | |
| `alertIcon` | 18 | | | _尚未标注_ | |
| `alertIndicatorOffset` | 3.5 | | | _尚未标注_ | |
| `alertPadding` | 12 | | | _尚未标注_ | |

## avatar

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `avatarGroupSize` | 32 | | | _尚未标注_ | |

## button

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `buttonGapExtraSmall` | 5 | — | — | GearUI | Half a step below sm for the 28 compact tier, which HeroUI does not have. |
| `buttonGapLarge` | 10 | 10 | — | HeroUI |  |
| `buttonGapMedium` | 8 | 8 | — | HeroUI |  |
| `buttonGapSmall` | 6 | 6 | — | HeroUI |  |
| `buttonPaddingExtraSmall` | 7 | — | — | GearUI | Half a step below sm for the 28 compact tier, which HeroUI does not have. |
| `buttonPaddingLarge` | 20 | 20 | — | HeroUI |  |
| `buttonPaddingMedium` | 16 | 16 | — | HeroUI |  |
| `buttonPaddingSmall` | 14 | 14 | — | HeroUI |  |

## card

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `cardPadding` | 16 | | | _尚未标注_ | |

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
| `dialogMaxWidth` | 384 | — | — | GearUI | The reference sizes a dialog by the portal's 20 padding only; a cap keeps a tablet or wide window from stretching a card across the screen. |
| `dialogTextGap` | 6 | 6 | — | HeroUI |  |

## field

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `fieldLabelGap` | 6 | | | _尚未标注_ | |
| `fieldPaddingLarge` | 12 | | | _尚未标注_ | |
| `fieldPaddingMedium` | 12 | 12 | — | HeroUI |  |
| `fieldPaddingSmall` | 12 | | | _尚未标注_ | |

## list

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `listCompactMinHeight` | 44 | — | 44 | iOS |  |
| `listCompactPadding` | 8 | | | _尚未标注_ | |
| `listItemGap` | 12 | 12 | — | HeroUI |  |
| `listMinHeight` | 52 | — | 52 | iOS |  |
| `listPaddingBlock` | 14 | 16 | 14 | iOS | HeroUI's 16 on both axes makes a 56pt row; iOS rows are 52. Splitting the axes keeps iOS's height and leading. |
| `listPaddingInline` | 20 | 16 | 20 | iOS | List rhythm follows the platform: text starts where iOS starts it, and the separator starts with the text. |

## menu

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `menuItemGap` | 10 | 10 | — | HeroUI |  |
| `menuItemPaddingBlock` | 8 | 8 | — | HeroUI |  |
| `menuItemPaddingInline` | 10 | 10 | — | HeroUI |  |
| `menuPaddingBlock` | 12 | 12 | — | HeroUI |  |
| `menuPaddingInline` | 6 | 6 | — | HeroUI |  |

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
| `otpCaretWidth` | 2 | | | _尚未标注_ | |
| `otpGap` | 8 | | | _尚未标注_ | |
| `otpSeparatorHeight` | 2 | | | _尚未标注_ | |
| `otpSeparatorWidth` | 8 | | | _尚未标注_ | |
| `otpSlotHeight` | 48 | | | _尚未标注_ | |
| `otpSlotWidth` | 44 | | | _尚未标注_ | |

## overlay

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `overlayOffset` | 9 | 9 | — | HeroUI |  |
| `overlayPadding` | 20 | 20 | — | HeroUI |  |

## popover

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `popoverPaddingBlock` | 12 | 12 | — | HeroUI |  |
| `popoverPaddingInline` | 16 | 16 | — | HeroUI |  |

## pull

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `pullRefreshThreshold` | 80 | | | _尚未标注_ | |

## radio

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `radioThumb` | 10 | | | _尚未标注_ | |

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
| `scrollShadowSize` | 50 | | | _尚未标注_ | |

## search

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `searchClearSize` | 24 | | | _尚未标注_ | |

## select

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `selectContentPadding` | 12 | | | _尚未标注_ | |
| `selectIndicatorSlot` | 20 | | | _尚未标注_ | |
| `selectItemPadding` | 8 | | | _尚未标注_ | |
| `selectPanelOffset` | 8 | | | _尚未标注_ | |

## selection

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `selectionLarge` | 28 | | | _尚未标注_ | |
| `selectionMedium` | 24 | | | _尚未标注_ | |
| `selectionSmall` | 20 | | | _尚未标注_ | |
| `selectionTouchTarget` | 44 | | | _尚未标注_ | |

## separator

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `separatorThickness` | 1 | 0.33 | 1 | iOS | A hairline is one device pixel and needs a darker colour to be seen; iOS grouped lists rule at 1pt in a lighter colour. |

## sheet

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `sheetMenuPaddingInline` | 12 | | | _尚未标注_ | |
| `sheetMenuRowGap` | 8 | | | _尚未标注_ | |

## slider

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `sliderCapsuleHeight` | 24 | | | _尚未标注_ | |
| `sliderCapsuleInset` | 3 | | | _尚未标注_ | |
| `sliderThumbHeight` | 20 | | | _尚未标注_ | |
| `sliderThumbInset` | 2 | | | _尚未标注_ | |
| `sliderThumbWidth` | 28 | | | _尚未标注_ | |
| `sliderTrackHeight` | 20 | | | _尚未标注_ | |

## switch

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `switchHeight` | 28 | 24 | 28 | iOS | The switch is the platform's signature control; next to iOS lists a 48pt switch reads as a web toggle. HeroUI's own switch is already the wide-thumb shape, so only the size moves. |
| `switchInset` | 2 | 2 | 2 | iOS |  |
| `switchThumbHeight` | 24 | 20 | 24 | iOS | The switch is the platform's signature control; next to iOS lists a 48pt switch reads as a web toggle. HeroUI's own switch is already the wide-thumb shape, so only the size moves. |
| `switchThumbWidth` | 37 | 28 | 37 | iOS | The switch is the platform's signature control; next to iOS lists a 48pt switch reads as a web toggle. HeroUI's own switch is already the wide-thumb shape, so only the size moves. |
| `switchWidth` | 63 | 48 | 63 | iOS | The switch is the platform's signature control; next to iOS lists a 48pt switch reads as a web toggle. HeroUI's own switch is already the wide-thumb shape, so only the size moves. |

## tabs

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `tabsIndicatorHeight` | 2 | 2 | — | HeroUI |  |
| `tabsListGap` | 4 | 4 | — | HeroUI |  |
| `tabsListPadding` | 3 | 3 | — | HeroUI |  |
| `tabsTriggerPaddingBlock` | 6 | 6 | — | HeroUI |  |
| `tabsTriggerPaddingInline` | 12 | 12 | — | HeroUI |  |

## tag

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `tagGroupGap` | 8 | | | _尚未标注_ | |
| `tagGroupLargePaddingBlock` | 6 | | | _尚未标注_ | |
| `tagGroupLargePaddingInline` | 12 | | | _尚未标注_ | |
| `tagGroupLargeRadius` | 24 | | | _尚未标注_ | |
| `tagGroupMediumPaddingBlock` | 4 | | | _尚未标注_ | |
| `tagGroupMediumPaddingInline` | 10 | | | _尚未标注_ | |
| `tagGroupMediumRadius` | 16 | | | _尚未标注_ | |
| `tagGroupRemoveIcon` | 14 | | | _尚未标注_ | |
| `tagGroupSmallPaddingBlock` | 2 | | | _尚未标注_ | |
| `tagGroupSmallPaddingInline` | 8 | | | _尚未标注_ | |
| `tagGroupSmallRadius` | 12 | | | _尚未标注_ | |
| `tagHeightLarge` | 36 | | | _尚未标注_ | |
| `tagHeightMedium` | 28 | | | _尚未标注_ | |
| `tagHeightSmall` | 20 | | | _尚未标注_ | |
| `tagPaddingLarge` | 16 | | | _尚未标注_ | |
| `tagPaddingMedium` | 12 | | | _尚未标注_ | |
| `tagPaddingSmall` | 8 | | | _尚未标注_ | |

## textarea

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `textareaMinHeight` | 128 | 128 | — | HeroUI |  |
| `textareaPaddingVertical` | 8 | 8 | — | HeroUI |  |

## toast

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `toastPadding` | 16 | | | _尚未标注_ | |

## tree

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `treeIndent` | 16 | | | _尚未标注_ | |

## upload

| Token | GearUI | HeroUI Native | iOS | 取值依据 | 理由 |
| --- | ---: | ---: | ---: | --- | --- |
| `uploadRemoveIcon` | 12 | | | _尚未标注_ | |
| `uploadRemoveSize` | 20 | | | _尚未标注_ | |
| `uploadTileGap` | 8 | | | _尚未标注_ | |
| `uploadTileSize` | 80 | | | _尚未标注_ | |

## 参考出处

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
- heroui-native 1.0.9 menu/popover DEFAULT_OFFSET
- heroui-native 1.0.9 dialog.css content padding
- heroui-native 1.0.9 popover.css content padding-block
- heroui-native 1.0.9 popover.css content padding-inline
- heroui-native 1.0.9 switch.css border-radius 9999px
- heroui-native 1.0.9 theme.css --radius-2xl
- heroui-native 1.0.9 theme.css --radius-xl
- heroui-native 1.0.9 menu.css item --radius-2xl
- heroui-native 1.0.9 theme.css --radius-3xl (dialog, menu, popover)
- heroui-native 1.0.9 theme.css --radius-4xl
- heroui-native 1.0.9 theme.css --radius-lg
- heroui-native 1.0.9 separator.css hairlineWidth() at 3x
- iOS 26.2 Settings, measured on simulator (grouped list separator)
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
- heroui-native 1.0.9 tabs.css trigger padding-inline
- heroui-native 1.0.9 text-area.css height
- heroui-native 1.0.9 text-area.css padding-block
