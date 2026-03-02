package com.gearui.sample.examples.bottomsheet

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.bottomsheet.BottomSheet
import com.gearui.components.bottomsheet.BottomSheetItem
import com.gearui.components.bottomsheet.BottomSheetState
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * BottomSheet 组件示例
 *
 * 基于 Overlay 系统实现，无论在哪里调用都能全屏弹出。
 * BottomSheet 可以直接放在 ExampleSection 内部，因为它使用 Overlay 系统。
 */
@Composable
fun BottomSheetExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    // 各种底部面板的状态
    val basicSheetState = remember { BottomSheetState() }
    val titleSheetState = remember { BottomSheetState() }
    val iconSheetState = remember { BottomSheetState() }
    val dangerSheetState = remember { BottomSheetState() }
    val noCancelSheetState = remember { BottomSheetState() }
    val manyItemsSheetState = remember { BottomSheetState() }
    var disabledSheetVisible by remember { mutableStateOf(false) }
    var customCancelSheetVisible by remember { mutableStateOf(false) }

    // 操作结果
    var selectedAction by remember { mutableStateOf("") }

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础用法
        ExampleSection(
            title = "基础用法",
            description = "简单的选项列表"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    text = "显示基础面板",
                    onClick = { basicSheetState.show() },
                    size = ButtonSize.MEDIUM
                )

                if (selectedAction.isNotEmpty()) {
                    Text(
                        text = "选择了: $selectedAction",
                        style = Typography.BodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            // BottomSheet 基于 Overlay 系统，可以放在任何地方
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
        }

        // 带标题和描述
        ExampleSection(
            title = "带标题和描述",
            description = "可以添加标题和描述信息"
        ) {
            Button(
                text = "显示带标题面板",
                onClick = { titleSheetState.show() },
                size = ButtonSize.MEDIUM
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
        }

        // 带图标
        ExampleSection(
            title = "带图标",
            description = "选项可以带有图标"
        ) {
            Button(
                text = "显示带图标面板",
                onClick = { iconSheetState.show() },
                size = ButtonSize.MEDIUM
            )

            BottomSheet(
                visible = iconSheetState.visible,
                onDismiss = { iconSheetState.hide() },
                title = "分享到",
                items = listOf(
                    BottomSheetItem(
                        label = "微信",
                        icon = {
                            Text(
                                text = "W",
                                style = Typography.BodyMedium,
                                color = colors.success
                            )
                        }
                    ),
                    BottomSheetItem(
                        label = "朋友圈",
                        icon = {
                            Text(
                                text = "M",
                                style = Typography.BodyMedium,
                                color = colors.success
                            )
                        }
                    ),
                    BottomSheetItem(
                        label = "微博",
                        icon = {
                            Text(
                                text = "B",
                                style = Typography.BodyMedium,
                                color = colors.danger
                            )
                        }
                    ),
                    BottomSheetItem(
                        label = "复制链接",
                        icon = {
                            Text(
                                text = "L",
                                style = Typography.BodyMedium,
                                color = colors.primary
                            )
                        }
                    )
                ),
                onItemClick = { item, _ ->
                    selectedAction = item.label
                }
            )
        }

        // 危险操作
        ExampleSection(
            title = "危险操作",
            description = "危险操作项会高亮显示"
        ) {
            Button(
                text = "显示危险操作面板",
                onClick = { dangerSheetState.show() },
                size = ButtonSize.MEDIUM,
                theme = ButtonTheme.DANGER
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
        }

        // 禁用选项
        ExampleSection(
            title = "禁用选项",
            description = "某些选项可以设置为禁用状态"
        ) {
            Button(
                text = "显示带禁用项面板",
                onClick = { disabledSheetVisible = true },
                size = ButtonSize.MEDIUM
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
        }

        // 不显示取消按钮
        ExampleSection(
            title = "不显示取消按钮",
            description = "可以隐藏底部的取消按钮"
        ) {
            Button(
                text = "显示无取消按钮面板",
                onClick = { noCancelSheetState.show() },
                size = ButtonSize.MEDIUM
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
        }

        // 多选项
        ExampleSection(
            title = "多选项",
            description = "支持多个选项"
        ) {
            Button(
                text = "显示多选项面板",
                onClick = { manyItemsSheetState.show() },
                size = ButtonSize.MEDIUM
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
        }

        // 自定义取消文字
        ExampleSection(
            title = "自定义取消文字",
            description = "可以自定义取消按钮的文字"
        ) {
            Button(
                text = "显示自定义取消面板",
                onClick = { customCancelSheetVisible = true },
                size = ButtonSize.MEDIUM
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

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "BottomSheet 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. 基于 Overlay 系统，全局弹出",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. BottomSheetState: 管理显示/隐藏状态",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. BottomSheetItem: 定义选项数据",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. 支持标题和描述",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. 支持图标",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "6. 支持危险操作高亮",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "7. 支持禁用状态",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "8. 点击遮罩层关闭",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
