package com.gearui.sample.navigation

import com.gearui.sample.examples.refresh.LoadMoreExample
import com.gearui.sample.examples.format.FormatExample
import com.gearui.sample.examples.indexbar.IndexBarExample
import com.gearui.sample.examples.agreement.AgreementExample
import com.gearui.sample.examples.runtime.PerformanceExample
import androidx.compose.runtime.Composable
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.examples.button.ButtonExample
import com.gearui.sample.examples.icon.IconExample
import com.gearui.sample.examples.text.TextExample
import com.gearui.sample.examples.tag.TagExample
import com.gearui.sample.examples.badge.BadgeExample
import com.gearui.sample.examples.divider.DividerExample
import com.gearui.sample.examples.beta7.*
import com.gearui.sample.examples.input.InputExample
import com.gearui.sample.examples.checkbox.CheckboxExample
import com.gearui.sample.examples.radio.RadioExample
import com.gearui.sample.examples.switch.SwitchExample
import com.gearui.sample.examples.slider.SliderExample
import com.gearui.sample.examples.stepper.StepperExample
import com.gearui.sample.examples.textarea.TextareaExample
import com.gearui.sample.examples.rate.RateExample
import com.gearui.sample.examples.select.SelectExample
import com.gearui.sample.examples.picker.PickerExample
import com.gearui.sample.examples.datepicker.DatePickerExample
import com.gearui.sample.examples.form.FormExample
import com.gearui.sample.examples.cascader.CascaderExample
import com.gearui.sample.examples.navbar.NavbarExample
import com.gearui.sample.examples.bottomnavbar.BottomNavBarExample
import com.gearui.sample.examples.tab.TabsExample
import com.gearui.sample.examples.cell.CellExample
import com.gearui.sample.examples.cellgroup.CellGroupExample
import com.gearui.sample.examples.drawer.DrawerExample
import com.gearui.sample.examples.steps.StepsExample
import com.gearui.sample.examples.segmented.SegmentedExample
import com.gearui.sample.examples.list.ListExample
import com.gearui.sample.examples.card.CardExample
import com.gearui.sample.examples.table.TableExample
import com.gearui.sample.examples.image.ImageExample
import com.gearui.sample.examples.imageviewer.ImageViewerExample
import com.gearui.sample.examples.avatar.AvatarExample
import com.gearui.sample.examples.collapse.CollapseExample
import com.gearui.sample.examples.progress.ProgressExample
import com.gearui.sample.examples.empty.EmptyExample
import com.gearui.sample.examples.skeleton.SkeletonExample
import com.gearui.sample.examples.timeline.TimelineExample
import com.gearui.sample.examples.calendar.CalendarExample
import com.gearui.sample.examples.watermark.WatermarkExample
import com.gearui.sample.examples.swipecell.SwipeCellExample
import com.gearui.sample.examples.actionsheet.ActionSheetExample
import com.gearui.sample.examples.toast.ToastExample
import com.gearui.sample.examples.dialog.DialogExample
import com.gearui.sample.examples.tooltip.TooltipExample
import com.gearui.sample.examples.contextmenu.ContextMenuExample
import com.gearui.sample.examples.loading.LoadingExample
import com.gearui.sample.examples.notification.NotificationExample
import com.gearui.sample.examples.snackbar.SnackbarExample
import com.gearui.sample.examples.popup.PopupExample
import com.gearui.sample.examples.popover.PopoverExample
import com.gearui.sample.examples.result.ResultExample
import com.gearui.sample.examples.tour.TourExample
import com.gearui.sample.examples.grid.GridExample
import com.gearui.sample.examples.swiper.SwiperExample
import com.gearui.sample.examples.searchbar.SearchBarExample
import com.gearui.sample.examples.bottomsheet.BottomSheetExample
import com.gearui.sample.examples.backtop.BackTopExample
import com.gearui.sample.examples.runtime.InsetsDebugExample
import com.gearui.sample.examples.runtime.MaterialExample
import com.gearui.sample.examples.runtime.TabHostExample
import com.gearui.sample.examples.navigator.NavigatorKuiklySpikeExample
import com.gearui.sample.examples.navigator.NavigatorV1DemoExample
import com.gearui.sample.examples.link.LinkExample
import com.gearui.sample.examples.inputgroup.InputGroupExample
import com.gearui.sample.examples.combobox.ComboBoxExample
import com.gearui.sample.examples.numberfield.NumberFieldExample
import com.gearui.sample.examples.togglebutton.ToggleButtonExample
import com.gearui.sample.examples.alert.AlertExample
import com.gearui.sample.examples.inputotp.InputOTPExample
import com.gearui.sample.examples.scrollshadow.ScrollShadowExample
import com.gearui.sample.examples.closebutton.CloseButtonExample
import com.gearui.sample.examples.pressablefeedback.PressableFeedbackExample
import com.gearui.sample.examples.noticebar.NoticeBarExample
import com.gearui.sample.examples.refresh.RefreshExample
import com.gearui.sample.examples.upload.UploadExample
import com.gearui.sample.pages.ExamplePage
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme

/**
 * NavigationManager - navigation manager
 *
 * Routes to the matching example page by component ID
 *
 * Total registered components: counted dynamically
 */
object NavigationManager {

    /**
     * Returns the example page for a component
     */
    @Composable
    fun getExamplePage(
        component: ComponentInfo,
        onBack: () -> Unit
    ) {
        when (component.id) {
            // Basic components
            "button" -> ButtonExample(component, onBack)
            "icon" -> IconExample(component, onBack)
            "icon-render" -> IconExample(component, onBack)
            "link" -> LinkExample(component, onBack)
            "input-group" -> InputGroupExample(component, onBack)
            "combo-box" -> ComboBoxExample(component, onBack)
            "number-field" -> NumberFieldExample(component, onBack)
            "toggle-button" -> ToggleButtonExample(component, onBack)
            "alert" -> AlertExample(component, onBack)
            "input-otp" -> InputOTPExample(component, onBack)
            "scroll-shadow" -> ScrollShadowExample(component, onBack)
            "close-button" -> CloseButtonExample(component, onBack)
            "pressable-feedback" -> PressableFeedbackExample(component, onBack)
            "text" -> TextExample(component, onBack)
            "tag" -> TagExample(component, onBack)
            "badge" -> BadgeExample(component, onBack)
            "divider" -> DividerExample(component, onBack)

            // Form components (15)
            "listbox" -> ListBoxExample(component, onBack)
            "typed-form" -> TypedFormExample(component, onBack)
            "toolbar" -> ToolbarExample(component, onBack)
            "submenu" -> SubMenuExample(component, onBack)
            "kbd" -> KbdExample(component, onBack)
            "meter" -> MeterExample(component, onBack)
            "user" -> UserExample(component, onBack)
            "code" -> CodeExample(component, onBack)
            "colorpicker" -> ColorPickerExample(component, onBack)
            "input" -> InputExample(component, onBack)
            "checkbox" -> CheckboxExample(component, onBack)
            "agreement" -> AgreementExample(component, onBack)
            "radio" -> RadioExample(component, onBack)
            "switch" -> SwitchExample(component, onBack)
            "slider" -> SliderExample(component, onBack)
            "stepper" -> StepperExample(component, onBack)
            "textarea" -> TextareaExample(component, onBack)
            "rate" -> RateExample(component, onBack)
            "select" -> SelectExample(component, onBack)
            "picker" -> PickerExample(component, onBack)
            "datepicker" -> DatePickerExample(component, onBack)
            "upload" -> UploadExample(component, onBack)
            "form" -> FormExample(component, onBack)
            "cascader" -> CascaderExample(component, onBack)

            // Navigation components (9)
            "navbar" -> NavbarExample(component, onBack)
            "bottom-navbar" -> BottomNavBarExample(component, onBack)
            "tabs" -> TabsExample(component, onBack)
            "drawer" -> DrawerExample(component, onBack)
            "steps" -> StepsExample(component, onBack)
            "indexbar" -> IndexBarExample(component, onBack)
            "segmented" -> SegmentedExample(component, onBack)
            "runtime-insets" -> InsetsDebugExample(component, onBack)
            "runtime-material" -> MaterialExample(component, onBack)
            "runtime-tabhost" -> TabHostExample(component, onBack)
            "runtime-performance" -> PerformanceExample(component, onBack)
            "navigator-kuikly-spike" -> NavigatorKuiklySpikeExample(component, onBack)
            "navigator-v1-demo" -> NavigatorV1DemoExample(component, onBack)

            // Data display components (15)
            "list" -> ListExample(component, onBack)
            "card" -> CardExample(component, onBack)
            "cell" -> CellExample(component, onBack)
            "cellgroup" -> CellGroupExample(component, onBack)
            "table" -> TableExample(component, onBack)
            "image" -> ImageExample(component, onBack)
            "imageviewer" -> ImageViewerExample(component, onBack)
            "avatar" -> AvatarExample(component, onBack)
            "collapse" -> CollapseExample(component, onBack)
            "progress" -> ProgressExample(component, onBack)
            "empty" -> EmptyExample(component, onBack)
            "skeleton" -> SkeletonExample(component, onBack)
            "timeline" -> TimelineExample(component, onBack)
            "calendar" -> CalendarExample(component, onBack)
            "format" -> FormatExample(component, onBack)
            "watermark" -> WatermarkExample(component, onBack)

            // Feedback components (11)
            "swipecell" -> SwipeCellExample(component, onBack)
            "actionsheet" -> ActionSheetExample(component, onBack)
            "toast" -> ToastExample(component, onBack)
            "dialog" -> DialogExample(component, onBack)
            "tooltip" -> TooltipExample(component, onBack)
            "context-menu" -> ContextMenuExample(component, onBack)
            "loading" -> LoadingExample(component, onBack)
            "noticebar" -> NoticeBarExample(component, onBack)
            "notification" -> NotificationExample(component, onBack)
            "snackbar" -> SnackbarExample(component, onBack)
            "popup" -> PopupExample(component, onBack)
            "popover" -> PopoverExample(component, onBack)
            "result" -> ResultExample(component, onBack)
            "tour" -> TourExample(component, onBack)

            // Layout components (5)
            "grid" -> GridExample(component, onBack)
            "swiper" -> SwiperExample(component, onBack)
            "searchbar" -> SearchBarExample(component, onBack)
            "refresh" -> RefreshExample(component, onBack)
            "loadmore" -> LoadMoreExample(component, onBack)
            "bottomsheet" -> BottomSheetExample(component, onBack)
            "backtop" -> BackTopExample(component, onBack)

            // Component not found
            else -> PlaceholderExample(component, onBack)
        }
    }
}

/**
 * Placeholder example page
 *
 * Used for component examples that are not implemented yet
 */
@Composable
private fun PlaceholderExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        Text(
            text = "该组件示例页面即将推出",
            style = Theme.typography.bodyLarge,
            color = colors.mutedForeground
        )

        Text(
            text = "组件 ID: ${component.id}",
            style = Theme.typography.bodySmall,
            color = colors.mutedForeground
        )
    }
}
