package com.gearui.sample.examples.buttongroup

import androidx.compose.runtime.*
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.buttongroup.ButtonGroup
import com.gearui.components.buttongroup.ButtonGroupItem
import com.gearui.components.buttongroup.SplitButton
import com.gearui.components.buttongroup.SplitButtonItem
import com.gearui.components.fabmenu.FabMenu
import com.gearui.components.fabmenu.FabMenuItem
import com.gearui.components.icon.*
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.*
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier

@Composable
private fun Last(text: String) =
    Text("最近：$text", style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)

@Composable
fun ButtonGroupExample(component: ComponentInfo, onBack: () -> Unit) {
    var last by remember { mutableStateOf("无") }
    ExamplePage(component, onBack) {
        ExampleSection("按钮组", "相关操作连成一组：外侧圆角，相接处近似直角", SectionSurface.Plain) {
            ButtonGroup(listOf(
                ButtonGroupItem("回复", { last = "回复" }, Icons.arrowBendUpLeft),
                ButtonGroupItem("转发", { last = "转发" }, Icons.arrowBendUpRight),
                ButtonGroupItem("删除", { last = "删除" }, Icons.trash),
            ))
            ButtonGroup(listOf(
                ButtonGroupItem("日", { last = "日" }), ButtonGroupItem("周", { last = "周" }),
                ButtonGroupItem("月", { last = "月" }), ButtonGroupItem("年", { last = "年" }, disabled = true),
            ), type = ButtonType.FILL, block = true)
            ButtonGroup(listOf(
                ButtonGroupItem(icon = Icons.textB, onClick = { last = "粗体" }, contentDescription = "粗体"),
                ButtonGroupItem(icon = Icons.textItalic, onClick = { last = "斜体" }, contentDescription = "斜体"),
                ButtonGroupItem(icon = Icons.textUnderline, onClick = { last = "下划线" }, contentDescription = "下划线"),
            ), size = ButtonSize.SMALL)
            Last(last)
        }
        ExampleSection("分体按钮", "左侧执行主操作，右侧打开更多选项（底部动作面板）", SectionSurface.Plain) {
            SplitButton("发送", { last = "发送" }, listOf(
                SplitButtonItem("定时发送", { last = "定时发送" }, Icons.clock),
                SplitButtonItem("作为文件发送", { last = "作为文件发送" }, Icons.file),
                SplitButtonItem("放弃草稿", { last = "放弃草稿" }, Icons.trash, destructive = true),
            ), icon = Icons.paperPlaneTilt)
            SplitButton("导出", { last = "导出" }, listOf(
                SplitButtonItem("导出为 PDF", { last = "导出为 PDF" }), SplitButtonItem("导出为图片", { last = "导出为图片" }),
            ), theme = ButtonTheme.DEFAULT, type = ButtonType.OUTLINE, size = ButtonSize.SMALL)
            Last(last)
        }
    }
}

@Composable
fun FabMenuExample(component: ComponentInfo, onBack: () -> Unit) {
    var last by remember { mutableStateOf("无") }
    Box(Modifier.fillMaxSize()) {
        ExamplePage(component, onBack) {
            ExampleSection("浮动按钮菜单", "点右下角按钮：页面变暗，操作从按钮上方展开；返回键、点空白处或 × 关闭", SectionSurface.Plain) {
                Last(last)
            }
        }
        FabMenu(
            icon = Icons.plus,
            items = listOf(
                FabMenuItem("发起群聊", Icons.users) { last = "发起群聊" },
                FabMenuItem("添加朋友", Icons.userPlus) { last = "添加朋友" },
                FabMenuItem("扫一扫", Icons.scan) { last = "扫一扫" },
            ),
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = Spacing.lg, bottom = Spacing.xxl),
        )
    }
}
