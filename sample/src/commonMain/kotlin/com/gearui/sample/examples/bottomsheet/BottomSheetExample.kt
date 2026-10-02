package com.gearui.sample.examples.bottomsheet

import com.gearui.components.icon.*
import androidx.compose.runtime.*
import com.gearui.components.bottomsheet.BottomSheet
import com.gearui.components.bottomsheet.BottomSheetItem
import com.gearui.components.bottomsheet.BottomSheetState
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.icon.Icons
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.IconSizes
import com.gearui.theme.Theme

private class SheetRow(val title: String, val description: String? = null, val onClick: () -> Unit)

/**
 * BottomSheet component examples
 *
 * Built on the Overlay system, so it fills the screen wherever it is called from.
 * A BottomSheet can sit directly inside an ExampleSection precisely because it uses the Overlay system.
 */
@Composable
fun BottomSheetExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // State of each bottom sheet
    val basicSheetState = remember { BottomSheetState() }
    val titleSheetState = remember { BottomSheetState() }
    val iconSheetState = remember { BottomSheetState() }
    val dangerSheetState = remember { BottomSheetState() }
    val noCancelSheetState = remember { BottomSheetState() }
    val manyItemsSheetState = remember { BottomSheetState() }
    var disabledSheetVisible by remember { mutableStateOf(false) }
    var customCancelSheetVisible by remember { mutableStateOf(false) }

    // Result
    var selectedAction by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // One entry per sheet variant; every BottomSheet goes through the Overlay system
        ExampleSection(
            surface = SectionSurface.Plain,
            title = "面板类型",
            description = "点击任一行弹出对应配置的 BottomSheet"
        ) {
            SheetRows(
                listOf(
                    SheetRow("基础面板", "简单的选项列表") { basicSheetState.show() },
                    SheetRow("带标题面板", "可以添加标题和描述信息") { titleSheetState.show() },
                    SheetRow("带图标面板", "选项可以带有图标") { iconSheetState.show() },
                    SheetRow("危险操作面板", "危险操作项会高亮显示") { dangerSheetState.show() },
                    SheetRow("带禁用项面板", "某些选项可以设置为禁用状态") { disabledSheetVisible = true },
                    SheetRow("无取消按钮面板", "showCancel = false 隐藏底部的取消按钮") { noCancelSheetState.show() },
                    SheetRow("多选项面板", "支持多个选项") { manyItemsSheetState.show() },
                    SheetRow("自定义取消面板", "cancelText 自定义取消按钮的文字") { customCancelSheetVisible = true },
                )
            )

            if (selectedAction.isNotEmpty()) {
                Text(
                    text = "选择了: $selectedAction",
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }

            // BottomSheet is built on the Overlay system, so it can go anywhere
            BottomSheet(
                visible = basicSheetState.visible,
                onDismiss = { basicSheetState.hide() },
                items = listOf(
                    BottomSheetItem("选项一"),
                    BottomSheetItem("选项二"),
                    BottomSheetItem("选项三")
                ),
                onItemClick = { item, _ ->
                    selectedAction = item.label
                }
            )

            BottomSheet(
                visible = titleSheetState.visible,
                onDismiss = { titleSheetState.hide() },
                title = "请选择操作",
                description = "选择以下操作之一继续",
                items = listOf(
                    BottomSheetItem("编辑"),
                    BottomSheetItem("复制"),
                    BottomSheetItem("分享")
                ),
                onItemClick = { item, _ ->
                    selectedAction = item.label
                }
            )

            BottomSheet(
                visible = iconSheetState.visible,
                onDismiss = { iconSheetState.hide() },
                title = "分享到",
                items = listOf(
                    BottomSheetItem(
                        label = "微信",
                        icon = {
                            Icon(Icons.chatCircle,
                                size = IconSizes.Default.xl,
                                tint = colors.success
                            )
                        }
                    ),
                    BottomSheetItem(
                        label = "朋友圈",
                        icon = {
                            Icon(Icons.usersThree,
                                size = IconSizes.Default.xl,
                                tint = colors.success
                            )
                        }
                    ),
                    BottomSheetItem(
                        label = "微博",
                        icon = {
                            Icon(Icons.shareNetwork,
                                size = IconSizes.Default.xl,
                                tint = colors.destructive
                            )
                        }
                    ),
                    BottomSheetItem(
                        label = "复制链接",
                        icon = {
                            Icon(Icons.link,
                                size = IconSizes.Default.xl,
                                tint = colors.primary
                            )
                        }
                    )
                ),
                onItemClick = { item, _ ->
                    selectedAction = item.label
                }
            )

            BottomSheet(
                visible = dangerSheetState.visible,
                onDismiss = { dangerSheetState.hide() },
                title = "确认操作",
                description = "以下操作不可恢复，请谨慎选择",
                items = listOf(
                    BottomSheetItem("保存草稿"),
                    BottomSheetItem("不保存"),
                    BottomSheetItem("删除", danger = true)
                ),
                onItemClick = { item, _ ->
                    selectedAction = item.label
                }
            )

            BottomSheet(
                visible = disabledSheetVisible,
                onDismiss = { disabledSheetVisible = false },
                title = "选择权限",
                items = listOf(
                    BottomSheetItem("公开"),
                    BottomSheetItem("仅好友可见"),
                    BottomSheetItem("私密（需要会员）", disabled = true),
                    BottomSheetItem("指定人可见（需要会员）", disabled = true)
                ),
                onItemClick = { item, _ ->
                    if (!item.disabled) {
                        selectedAction = item.label
                    }
                }
            )

            BottomSheet(
                visible = noCancelSheetState.visible,
                onDismiss = { noCancelSheetState.hide() },
                title = "快速操作",
                items = listOf(
                    BottomSheetItem("确认"),
                    BottomSheetItem("稍后再说")
                ),
                showCancel = false,
                onItemClick = { item, _ ->
                    selectedAction = item.label
                }
            )

            BottomSheet(
                visible = manyItemsSheetState.visible,
                onDismiss = { manyItemsSheetState.hide() },
                title = "更多操作",
                items = listOf(
                    BottomSheetItem("查看详情"),
                    BottomSheetItem("编辑"),
                    BottomSheetItem("复制"),
                    BottomSheetItem("移动"),
                    BottomSheetItem("重命名"),
                    BottomSheetItem("下载"),
                    BottomSheetItem("删除", danger = true)
                ),
                onItemClick = { item, _ ->
                    selectedAction = item.label
                }
            )

            BottomSheet(
                visible = customCancelSheetVisible,
                onDismiss = { customCancelSheetVisible = false },
                title = "选择语言",
                items = listOf(
                    BottomSheetItem("简体中文"),
                    BottomSheetItem("English"),
                    BottomSheetItem("日本語"),
                    BottomSheetItem("한국어")
                ),
                cancelText = "暂不选择",
                onItemClick = { item, _ ->
                    selectedAction = item.label
                }
            )
        }
    }
}

// One arrow row per trigger (non-anchored overlay entries, see COMPONENT_SPEC §6).
@Composable
private fun SheetRows(rows: List<SheetRow>) {
    CellGroup(items = rows) { row ->
        Cell(
            title = row.title,
            description = row.description,
            arrow = true,
            onClick = row.onClick
        )
    }
}
