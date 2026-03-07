package com.gearui.sample.config

/**
 * 组件配置中心 - 管理所有组件的元数据
 *
 */
data class ComponentInfo(
    val id: String,
    val nameZh: String,
    val nameEn: String,
    val category: ComponentCategory,
    val route: String,
    val descriptionZh: String = "",
    val descriptionEn: String = ""
)

enum class ComponentCategory {
    BASIC,
    FORM,
    NAVIGATION,
    DATA_DISPLAY,
    FEEDBACK,
    LAYOUT
}

object ComponentConfig {
    val all: List<ComponentInfo> = listOf(
        // 基础组件
        ComponentInfo("button", "按钮", "Button", ComponentCategory.BASIC, "/components/button", "用于触发操作", "Trigger actions"),
        ComponentInfo("icon", "图标", "Icon", ComponentCategory.BASIC, "/components/icon", "图标展示", "Icon display"),
        ComponentInfo("icon-render", "图标渲染验证", "Icon Render", ComponentCategory.BASIC, "/components/icon-render", "PNG/SVG 资源渲染能力验证", "PNG/SVG resource rendering"),
        ComponentInfo("font", "字体", "Font", ComponentCategory.BASIC, "/components/font", "字体规范展示", "Typography tokens"),
        ComponentInfo("radius", "圆角", "Radius", ComponentCategory.BASIC, "/components/radius", "圆角规范展示", "Radius tokens"),
        ComponentInfo("shadows", "阴影", "Shadows", ComponentCategory.BASIC, "/components/shadows", "阴影规范展示", "Shadow tokens"),
        ComponentInfo("theme", "主题色", "Theme", ComponentCategory.BASIC, "/components/theme", "主题与色板展示", "Theme colors"),
        ComponentInfo("link", "链接", "Link", ComponentCategory.BASIC, "/components/link", "文本链接样式", "Link text"),
        ComponentInfo("text", "文本", "Text", ComponentCategory.BASIC, "/components/text", "文本展示", "Text display"),
        ComponentInfo("tag", "标签", "Tag", ComponentCategory.BASIC, "/components/tag", "标记和分类", "Marking and classification"),
        ComponentInfo("badge", "徽标", "Badge", ComponentCategory.BASIC, "/components/badge", "消息数量提示", "Message count indicator"),
        ComponentInfo("divider", "分割线", "Divider", ComponentCategory.BASIC, "/components/divider", "内容分隔", "Content separator"),

        // 表单组件
        ComponentInfo("input", "输入框", "Input", ComponentCategory.FORM, "/components/input", "文本输入", "Text input"),
        ComponentInfo("checkbox", "复选框", "Checkbox", ComponentCategory.FORM, "/components/checkbox", "多选操作", "Multiple selection"),
        ComponentInfo("radio", "单选框", "Radio", ComponentCategory.FORM, "/components/radio", "单选操作", "Single selection"),
        ComponentInfo("switch", "开关", "Switch", ComponentCategory.FORM, "/components/switch", "开关选择", "Toggle switch"),
        ComponentInfo("slider", "滑块", "Slider", ComponentCategory.FORM, "/components/slider", "数值选择", "Value selection"),
        ComponentInfo("stepper", "步进器", "Stepper", ComponentCategory.FORM, "/components/stepper", "数字增减", "Number stepper"),
        ComponentInfo("textarea", "多行输入", "Textarea", ComponentCategory.FORM, "/components/textarea", "多行文本输入", "Multiline text input"),
        ComponentInfo("rate", "评分", "Rate", ComponentCategory.FORM, "/components/rate", "评分操作", "Rating"),
        ComponentInfo("select", "下拉选择", "Select", ComponentCategory.FORM, "/components/select", "下拉选择器", "Dropdown selector"),
        ComponentInfo("picker", "选择器", "Picker", ComponentCategory.FORM, "/components/picker", "多列选择", "Multi-column picker"),
        ComponentInfo("datepicker", "日期选择", "DatePicker", ComponentCategory.FORM, "/components/datepicker", "日期时间选择", "Date & time picker"),
        ComponentInfo("dropdownmenu", "下拉菜单", "DropdownMenu", ComponentCategory.FORM, "/components/dropdownmenu", "筛选下拉菜单", "Filter dropdown menu"),
        ComponentInfo("upload", "上传", "Upload", ComponentCategory.FORM, "/components/upload", "文件上传", "File upload"),
        ComponentInfo("form", "表单", "Form", ComponentCategory.FORM, "/components/form", "表单容器", "Form container"),
        ComponentInfo("cascader", "级联选择", "Cascader", ComponentCategory.FORM, "/components/cascader", "级联选择器", "Cascade selector"),
        ComponentInfo("transfer", "穿梭框", "Transfer", ComponentCategory.FORM, "/components/transfer", "数据穿梭选择", "Data transfer"),
        ComponentInfo("treeselect", "树选择", "TreeSelect", ComponentCategory.FORM, "/components/treeselect", "树形选择器", "Tree selector"),

        // 导航组件
        ComponentInfo("navbar", "导航栏", "NavBar", ComponentCategory.NAVIGATION, "/components/navbar", "H5页面导航栏", "Page navigation bar"),
        ComponentInfo("bottom-navbar", "底部导航栏", "BottomNavBar", ComponentCategory.NAVIGATION, "/components/bottom-navbar", "应用底部主导航", "App bottom navigation"),
        ComponentInfo("tabs", "选项卡", "Tabs", ComponentCategory.NAVIGATION, "/components/tabs", "内容切换", "Content switching"),
        ComponentInfo("navigation-menu", "导航菜单", "NavigationMenu", ComponentCategory.NAVIGATION, "/components/navigation-menu", "顶部导航菜单", "Top navigation menu"),
        ComponentInfo("sidebar", "侧边栏", "Sidebar", ComponentCategory.NAVIGATION, "/components/sidebar", "侧边导航", "Side navigation"),
        ComponentInfo("indexes", "索引列表", "Indexes", ComponentCategory.NAVIGATION, "/components/indexes", "快速索引导航", "Index navigation"),
        ComponentInfo("drawer", "抽屉", "Drawer", ComponentCategory.NAVIGATION, "/components/drawer", "侧滑抽屉", "Slide drawer"),
        ComponentInfo("steps", "步骤条", "Steps", ComponentCategory.NAVIGATION, "/components/steps", "步骤指示", "Step indicator"),
        ComponentInfo("pagination", "分页", "Pagination", ComponentCategory.NAVIGATION, "/components/pagination", "页码导航", "Pagination navigation"),
        ComponentInfo("breadcrumb", "面包屑", "Breadcrumb", ComponentCategory.NAVIGATION, "/components/breadcrumb", "路径导航", "Path navigation"),
        ComponentInfo("anchor", "锚点", "Anchor", ComponentCategory.NAVIGATION, "/components/anchor", "页面锚点导航", "Page anchor navigation"),
        ComponentInfo("segmented", "分段控制", "Segmented", ComponentCategory.NAVIGATION, "/components/segmented", "分段选择", "Segmented control"),
        ComponentInfo("fab", "悬浮按钮", "FAB", ComponentCategory.NAVIGATION, "/components/fab", "浮动操作按钮", "Floating action button"),

        // 数据展示
        ComponentInfo("list", "列表", "List", ComponentCategory.DATA_DISPLAY, "/components/list", "列表展示", "List display"),
        ComponentInfo("card", "卡片", "Card", ComponentCategory.DATA_DISPLAY, "/components/card", "卡片容器", "Card container"),
        ComponentInfo("cell", "单元格", "Cell", ComponentCategory.DATA_DISPLAY, "/components/cell", "列表单元组件", "List cell component"),
        ComponentInfo("table", "表格", "Table", ComponentCategory.DATA_DISPLAY, "/components/table", "数据表格", "Data table"),
        ComponentInfo("image", "图片", "Image", ComponentCategory.DATA_DISPLAY, "/components/image", "图片展示", "Image display"),
        ComponentInfo("imageviewer", "图片预览", "ImageViewer", ComponentCategory.DATA_DISPLAY, "/components/imageviewer", "图片预览查看", "Image preview"),
        ComponentInfo("avatar", "头像", "Avatar", ComponentCategory.DATA_DISPLAY, "/components/avatar", "用户头像", "User avatar"),
        ComponentInfo("collapse", "折叠面板", "Collapse", ComponentCategory.DATA_DISPLAY, "/components/collapse", "内容折叠", "Content collapse"),
        ComponentInfo("progress", "进度条", "Progress", ComponentCategory.DATA_DISPLAY, "/components/progress", "进度展示", "Progress display"),
        ComponentInfo("empty", "空状态", "Empty", ComponentCategory.DATA_DISPLAY, "/components/empty", "空数据提示", "Empty state"),
        ComponentInfo("skeleton", "骨架屏", "Skeleton", ComponentCategory.DATA_DISPLAY, "/components/skeleton", "加载占位", "Loading placeholder"),
        ComponentInfo("timeline", "时间轴", "Timeline", ComponentCategory.DATA_DISPLAY, "/components/timeline", "时间线展示", "Timeline display"),
        ComponentInfo("timecounter", "时间计时器", "TimeCounter", ComponentCategory.DATA_DISPLAY, "/components/timecounter", "倒计时与计时展示", "Time counter"),
        ComponentInfo("tree", "树", "Tree", ComponentCategory.DATA_DISPLAY, "/components/tree", "树形结构", "Tree structure"),
        ComponentInfo("calendar", "日历", "Calendar", ComponentCategory.DATA_DISPLAY, "/components/calendar", "日历展示", "Calendar display"),
        ComponentInfo("footer", "页脚", "Footer", ComponentCategory.DATA_DISPLAY, "/components/footer", "页面底部信息区", "Footer section"),
        ComponentInfo("watermark", "水印", "Watermark", ComponentCategory.DATA_DISPLAY, "/components/watermark", "页面水印", "Page watermark"),

        // 反馈组件
        ComponentInfo("swipecell", "滑动单元格", "SwipeCell", ComponentCategory.FEEDBACK, "/components/swipecell", "滑动操作单元格", "Swipeable cell"),
        ComponentInfo("actionsheet", "动作面板", "ActionSheet", ComponentCategory.FEEDBACK, "/components/actionsheet", "底部动作面板", "Bottom action sheet"),
        ComponentInfo("toast", "轻提示", "Toast", ComponentCategory.FEEDBACK, "/components/toast", "消息提示", "Message toast"),
        ComponentInfo("dialog", "对话框", "Dialog", ComponentCategory.FEEDBACK, "/components/dialog", "模态对话框", "Modal dialog"),
        ComponentInfo("tooltip", "文字提示", "Tooltip", ComponentCategory.FEEDBACK, "/components/tooltip", "文字提示", "Tooltip"),
        ComponentInfo("context-menu", "上下文菜单", "ContextMenu", ComponentCategory.FEEDBACK, "/components/context-menu", "上下文菜单", "Context menu"),
        ComponentInfo("loading", "加载", "Loading", ComponentCategory.FEEDBACK, "/components/loading", "加载状态", "Loading state"),
        ComponentInfo("message", "消息提醒", "Message", ComponentCategory.FEEDBACK, "/components/message", "全局消息提示", "Global message"),
        ComponentInfo("noticebar", "公告栏", "NoticeBar", ComponentCategory.FEEDBACK, "/components/noticebar", "滚动公告提醒", "Notice bar"),
        ComponentInfo("notification", "通知", "Notification", ComponentCategory.FEEDBACK, "/components/notification", "全局通知", "Global notification"),
        ComponentInfo("snackbar", "消息条", "Snackbar", ComponentCategory.FEEDBACK, "/components/snackbar", "底部消息", "Bottom message"),
        ComponentInfo("popup", "弹出层", "Popup", ComponentCategory.FEEDBACK, "/components/popup", "弹出内容", "Popup content"),
        ComponentInfo("popover", "气泡", "Popover", ComponentCategory.FEEDBACK, "/components/popover", "气泡提示", "Popover tooltip"),
        ComponentInfo("result", "结果", "Result", ComponentCategory.FEEDBACK, "/components/result", "操作结果反馈", "Operation result"),
        ComponentInfo("tour", "引导", "Tour", ComponentCategory.FEEDBACK, "/components/tour", "功能引导", "Feature guide"),

        // 布局组件
        ComponentInfo("grid", "栅格", "Grid", ComponentCategory.LAYOUT, "/components/grid", "栅格布局", "Grid layout"),
        ComponentInfo("swiper", "轮播", "Swiper", ComponentCategory.LAYOUT, "/components/swiper", "内容轮播", "Content carousel"),
        ComponentInfo("searchbar", "搜索栏", "SearchBar", ComponentCategory.LAYOUT, "/components/searchbar", "搜索输入", "Search input"),
        ComponentInfo("refresh", "下拉刷新", "Refresh", ComponentCategory.LAYOUT, "/components/refresh", "下拉刷新展示（演示页）", "Pull-to-refresh showcase"),
        ComponentInfo("bottomsheet", "底部抽屉", "BottomSheet", ComponentCategory.LAYOUT, "/components/bottomsheet", "底部弹出", "Bottom sheet"),
        ComponentInfo("backtop", "回到顶部", "BackTop", ComponentCategory.LAYOUT, "/components/backtop", "返回顶部", "Back to top")
    )

    /**
     * 按分类获取组件
     */
    fun getByCategory(category: ComponentCategory): List<ComponentInfo> {
        return all.filter { it.category == category }
    }

    /**
     * 按 ID 获取组件
     */
    fun getById(id: String): ComponentInfo? {
        return all.find { it.id == id }
    }

    /**
     * 所有分类及其组件数量
     */
    val categoryCounts: Map<ComponentCategory, Int> = all
        .groupBy { it.category }
        .mapValues { it.value.size }
}

/**
 * 根据语言代码获取组件的本地化名称
 */
fun ComponentInfo.localizedName(isEnglish: Boolean): String {
    return if (isEnglish) nameEn else nameZh
}

/**
 * 根据语言代码获取组件的本地化描述
 */
fun ComponentInfo.localizedDescription(isEnglish: Boolean): String {
    return if (isEnglish) descriptionEn else descriptionZh
}
