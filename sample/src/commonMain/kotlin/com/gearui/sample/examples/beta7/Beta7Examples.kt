package com.gearui.sample.examples.beta7

import androidx.compose.runtime.*
import com.gearui.components.button.*
import com.gearui.components.calendar.CalendarDate
import com.gearui.components.code.*
import com.gearui.components.colorpicker.*
import com.gearui.components.form.*
import com.gearui.components.input.Input
import com.gearui.components.kbd.Kbd
import com.gearui.components.listbox.ListBox
import com.gearui.components.meter.Meter
import com.gearui.components.picker.*
import com.gearui.components.select.SelectOption
import com.gearui.components.toolbar.Toolbar
import com.gearui.components.user.User
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.*
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ListBoxExample(component: ComponentInfo, onBack: () -> Unit) {
    var delivery by remember { mutableStateOf(setOf("delivery")) }
    var toppings by remember { mutableStateOf(setOf("ice")) }
    val deliveryOptions = listOf(SelectOption("delivery", "送货上门", group = "配送方式"), SelectOption("pickup", "门店自提", group = "配送方式"),
        SelectOption("express", "同城急送（暂未开放）", disabled = true, group = "配送方式"))
    val toppingOptions = listOf(SelectOption("ice", "加冰", group = "温度"), SelectOption("hot", "常温", group = "温度"),
        SelectOption("pearl", "珍珠", group = "小料"), SelectOption("pudding", "布丁", group = "小料"), SelectOption("coconut", "椰果", group = "小料"))
    ExamplePage(component, onBack) {
        ExampleSection("单选", "选中项显示对勾，禁用项不可选", SectionSurface.Plain) {
            ListBox(deliveryOptions, delivery, { delivery = it })
            Text("value：" + delivery.joinToString(" / ").ifEmpty { "无" }, style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)
        }
        ExampleSection("多选与分组", "同组选项在一张卡片里，再点一次取消", SectionSurface.Plain) {
            ListBox(toppingOptions, toppings, { toppings = it }, multiple = true)
            Text("value：" + toppings.joinToString(" / ").ifEmpty { "无" }, style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)
        }
    }
}

@Composable
fun ToolbarExample(component: ComponentInfo, onBack: () -> Unit) {
    var selected by remember { mutableStateOf("普通") }
    ExamplePage(component, onBack) {
        ExampleSection("操作工具栏", "窄屏或大字体时换行，动作保持独立可访问", SectionSurface.Plain) {
            Toolbar("编辑操作") {
                listOf("普通", "加粗", "斜体", "下划线").forEach { action -> Button(action, { selected = action }, type = if (selected == action) ButtonType.FILL else ButtonType.TEXT) }
            }
            Text("当前：$selected", style = Theme.typography.bodyMedium, color = Theme.colors.foreground)
        }
    }
}

@Composable
fun KbdExample(component: ComponentInfo, onBack: () -> Unit) {
    ExamplePage(component, onBack) {
        ExampleSection("快捷键提示", "供 Web、外接键盘场景使用") { Kbd(listOf("Cmd", "K"), accessibilityLabel = "Command K"); Kbd(listOf("Ctrl", "Shift", "P")) }
    }
}

@Composable
fun MeterExample(component: ComponentInfo, onBack: () -> Unit) {
    ExamplePage(component, onBack) {
        ExampleSection("计量值", "已知上下限的测量，与加载进度区分") {
            Meter(64f, "存储空间", min = 0f, max = 128f, format = { "${it.toInt()} GB / 128 GB" })
            Meter(2.5f, "评分", min = 0f, max = 5f)
        }
    }
}

@Composable
fun UserExample(component: ComponentInfo, onBack: () -> Unit) {
    var clicked by remember { mutableStateOf(false) }
    ExamplePage(component, onBack) {
        ExampleSection("用户身份摘要", "图片、首字兜底与可选操作", SectionSurface.Plain) {
            User("张三", description = "设计团队", fallback = "张", onClick = { clicked = !clicked })
            User("GearUI", description = "本地图片", avatarUrl = "assets://avatars/gearui.png", fallback = "G")
            Text(if (clicked) "已点击" else "点击用户行", style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)
        }
    }
}

@Composable
fun CodeExample(component: ComponentInfo, onBack: () -> Unit) {
    var received by remember { mutableStateOf("") }
    ExamplePage(component, onBack) {
        ExampleSection("代码与内容块", "复制操作由宿主接入，示例显示回调收到的完整内容") {
            Code("val framework = GearUI")
            Snippet("val price = DecimalValue.parse(\"0.1\")\nval total = price!! + DecimalValue.parse(\"0.2\")!!", copyLabel = "读取内容回调", onCopy = { received = it })
            if (received.isNotEmpty()) Text(received, style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)
        }
    }
}

@Composable
fun ColorPickerExample(component: ComponentInfo, onBack: () -> Unit) {
    val accent = Theme.colors.primary
    var value by remember { mutableStateOf(ColorValue.fromColor(accent)) }
    val labels = mapOf(ColorChannel.HUE to "色相", ColorChannel.SATURATION to "饱和度", ColorChannel.BRIGHTNESS to "明度", ColorChannel.ALPHA to "透明度")
    ExamplePage(component, onBack) {
        ExampleSection("颜色编辑", "色板、二维平面、四通道滑块和十六进制输入") {
            ColorSwatchPicker(listOf(ColorValue(0f, 1f, 1f), ColorValue(120f, 1f, 1f), ColorValue(240f, 1f, 1f)), value, { value = it })
            ColorPicker(value, { value = it }, labels, "颜色值", variant = FieldVariant.SECONDARY)
        }
    }
}

@Composable
fun TypedFormExample(component: ComponentInfo, onBack: () -> Unit) {
    val form = rememberFormState()
    val rules = remember { listOf(TypedFormRule<String>(validate = { if (it.isBlank()) "请输入用户名" else null }, validateAsync = { value -> delay(if (value == "taken") 1200 else 200); if (value == "taken") "用户名已被使用" else null })) }
    val name = rememberTypedFormFieldState("", rules, FormValidationTrigger.CHANGE, "username", form)
    val quantity = rememberTypedFormFieldState(1, name = "quantity", formState = form)
    val scope = rememberCoroutineScope()
    var submitted by remember { mutableStateOf(false) }
    ExamplePage(component, onBack) {
        ExampleSection("类型化与异步校验", "输入 taken 后立即修改，旧结果不得覆盖新值；服务端错误可注入") {
            Input(name.value, { submitted = false; name.update(it) }, variant = FieldVariant.SECONDARY, label = "用户名", placeholder = "请输入用户名（输入 taken 试试）", error = name.error, helperText = if (name.validating) "正在校验" else null)
            com.gearui.components.stepper.Stepper(quantity.value, { submitted = false; quantity.update(it) })
            Text("dirty=${name.dirty} touched=${name.touched}", style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Button("提交", { scope.launch { submitted = form.validateAll() } })
                Button("注入服务端错误", { submitted = false; form.setFieldErrors(mapOf("username" to "服务端拒绝此用户名")) }, type = ButtonType.OUTLINE)
                Button("重置", { form.reset(); submitted = false }, type = ButtonType.TEXT)
            }
            if (submitted) Text("提交值：${form.getTypedValues()}", style = Theme.typography.bodySmall, color = Theme.colors.foreground)
        }
    }
}
