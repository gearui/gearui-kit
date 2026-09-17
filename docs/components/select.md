# Select

## 结构与参考

以锁定版本的开源 HeroUI Native `select.tsx` 和 `select.css` 为参考：
触发器、独立浮动面板、分组标题、连续选项、尾部选中标记及触摸滚动视口。
面板采用 Theme.popover 表面、Theme.shapes.xl 圆角、12px 内容留白；
行内留白 8px，勾选槽宽 20px。尺寸来自 DTCG 数据，不将每项画成独立卡片。
选中通过勾选表达，不再永久填灰；按压和悬停仍保留瞬时反馈。

```kotlin
var fruit by remember { mutableStateOf<String?>("apple") }
Select(
    value = fruit,
    options = listOf(
        SelectOption("apple", "Apple", group = "Fruits"),
        SelectOption("pear", "Pear", group = "Fruits"),
    ),
    onValueChange = { fruit = it },
)
```

## 行为契约

- `TRIGGER_OVERLAID` 是保留的旧名称，现在为默认模式：独立锚定面板，优先下方，空间不足时翻至上方。
- `ITEM_ALIGNED` 仍可显式使用。当前项尽量与触发器对齐；内容留白计入定位计算。
- 高度来自 Overlay 根视口与可用安全区域，不读取设备屏幕高度，也不固定为 240。
- 长列表打开时把当前项带入可视区；分组标题计入滚动行号。
- 分组按连续出现顺序展示，不偷偷重排调用方数据。
- 单选先关闭再回调；多选保持展开，选中位置始终预留尾部标记宽度。
- 展开状态不是焦点或错误状态，不将边框切成主色；单选与多选共用触发器的悬停、按压和禁用规则。
- sample 保留原版页面和演示分区，不因组件样式调整而重新设计页面。
- 禁用项不触发回调；退场期间不重复提交。多选达到上限仍允许取消已有选项。
- 外部点击、返回、页面滚动、离开组合沿用统一 Overlay 生命周期。
- 根视口改变或组件禁用时关闭，避免在旧坐标留下浮层。
- 触发器和选项单行省略，不能把箭头或选中标记挤出面板。

## 边界

当前实现为自绘锚定面板，不是系统选择器。
HeroUI Native 另外支持 bottom-sheet/dialog 展示；本批未实现这些呈现方式。
键盘方向键、文字检索、焦点恢复与上游动画驱动尚未验收，不能宣称全行为一致。
Kuikly 当前源码的 `KeyEvent.key/type` 声明被注释，不能只挂一个 Modifier 就声称实现键盘导航。
面板继续读取当前 Theme，支持明暗与品牌配色。字体配置、分组行高、
浮层过渡及各平台滚动物理尚未与运行中的参考应用逐项验收。
