package com.gearui.sample.navigation

import androidx.compose.runtime.Composable
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.examples.button.ButtonExample
import com.gearui.sample.examples.icon.IconExample
import com.gearui.sample.examples.text.TextExample
import com.gearui.sample.examples.tag.TagExample
import com.gearui.sample.examples.badge.BadgeExample
import com.gearui.sample.examples.divider.DividerExample
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
import com.gearui.sample.examples.transfer.TransferExample
import com.gearui.sample.examples.treeselect.TreeSelectExample
import com.gearui.sample.examples.navbar.NavbarExample
import com.gearui.sample.examples.tab.TabExample
import com.gearui.sample.examples.sidebar.SidebarExample
import com.gearui.sample.examples.tabbar.TabBarExample
import com.gearui.sample.examples.cell.CellExample
import com.gearui.sample.examples.drawer.DrawerExample
import com.gearui.sample.examples.steps.StepsExample
import com.gearui.sample.examples.breadcrumb.BreadcrumbExample
import com.gearui.sample.examples.anchor.AnchorExample
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
import com.gearui.sample.examples.tree.TreeExample
import com.gearui.sample.examples.calendar.CalendarExample
import com.gearui.sample.examples.watermark.WatermarkExample
import com.gearui.sample.examples.swipecell.SwipeCellExample
import com.gearui.sample.examples.actionsheet.ActionSheetExample
import com.gearui.sample.examples.toast.ToastExample
import com.gearui.sample.examples.dialog.DialogExample
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
import com.gearui.sample.pages.ExamplePage
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * NavigationManager - 导航管理器
 *
 * 负责根据组件 ID 路由到对应的示例页面
 *
 * 已注册组件总数：动态统计
 */
object NavigationManager {

    /**
     * 获取组件的示例页面
     */
    @Composable
    fun getExamplePage(
        component: ComponentInfo,
        onBack: () -> Unit
    ) {
        when (component.id) {
            // 基础组件
            "button" -> ButtonExample(component, onBack)
            "icon" -> IconExample(component, onBack)
            "icon-render" -> IconExample(component, onBack)
            "text" -> TextExample(component, onBack)
            "tag" -> TagExample(component, onBack)
            "badge" -> BadgeExample(component, onBack)
            "divider" -> DividerExample(component, onBack)

            // 表单组件 (15个)
            "input" -> InputExample(component, onBack)
            "checkbox" -> CheckboxExample(component, onBack)
            "radio" -> RadioExample(component, onBack)
            "switch" -> SwitchExample(component, onBack)
            "slider" -> SliderExample(component, onBack)
            "stepper" -> StepperExample(component, onBack)
            "textarea" -> TextareaExample(component, onBack)
            "rate" -> RateExample(component, onBack)
            "select" -> SelectExample(component, onBack)
            "picker" -> PickerExample(component, onBack)
            "datepicker" -> DatePickerExample(component, onBack)
            "form" -> FormExample(component, onBack)
            "cascader" -> CascaderExample(component, onBack)
            "transfer" -> TransferExample(component, onBack)
            "treeselect" -> TreeSelectExample(component, onBack)

            // 导航组件 (9个)
            "navbar" -> NavbarExample(component, onBack)
            "tab" -> TabExample(component, onBack)
            "sidebar" -> SidebarExample(component, onBack)
            "tabbar" -> TabBarExample(component, onBack)
            "drawer" -> DrawerExample(component, onBack)
            "steps" -> StepsExample(component, onBack)
            "breadcrumb" -> BreadcrumbExample(component, onBack)
            "anchor" -> AnchorExample(component, onBack)
            "segmented" -> SegmentedExample(component, onBack)

            // 数据展示组件 (15个)
            "list" -> ListExample(component, onBack)
            "card" -> CardExample(component, onBack)
            "cell" -> CellExample(component, onBack)
            "table" -> TableExample(component, onBack)
            "image" -> ImageExample(component, onBack)
            "imageviewer" -> ImageViewerExample(component, onBack)
            "avatar" -> AvatarExample(component, onBack)
            "collapse" -> CollapseExample(component, onBack)
            "progress" -> ProgressExample(component, onBack)
            "empty" -> EmptyExample(component, onBack)
            "skeleton" -> SkeletonExample(component, onBack)
            "timeline" -> TimelineExample(component, onBack)
            "tree" -> TreeExample(component, onBack)
            "calendar" -> CalendarExample(component, onBack)
            "watermark" -> WatermarkExample(component, onBack)

            // 反馈组件 (11个)
            "swipecell" -> SwipeCellExample(component, onBack)
            "actionsheet" -> ActionSheetExample(component, onBack)
            "toast" -> ToastExample(component, onBack)
            "dialog" -> DialogExample(component, onBack)
            "loading" -> LoadingExample(component, onBack)
            "notification" -> NotificationExample(component, onBack)
            "snackbar" -> SnackbarExample(component, onBack)
            "popup" -> PopupExample(component, onBack)
            "popover" -> PopoverExample(component, onBack)
            "result" -> ResultExample(component, onBack)
            "tour" -> TourExample(component, onBack)

            // 布局组件 (5个)
            "grid" -> GridExample(component, onBack)
            "swiper" -> SwiperExample(component, onBack)
            "searchbar" -> SearchBarExample(component, onBack)
            "bottomsheet" -> BottomSheetExample(component, onBack)
            "backtop" -> BackTopExample(component, onBack)

            // 未找到的组件
            else -> PlaceholderExample(component, onBack)
        }
    }
}

/**
 * 占位示例页面
 *
 * 用于还未实现的组件示例
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
            style = Typography.BodyLarge,
            color = colors.textSecondary
        )

        Text(
            text = "组件 ID: ${component.id}",
            style = Typography.BodySmall,
            color = colors.textPlaceholder
        )
    }
}
