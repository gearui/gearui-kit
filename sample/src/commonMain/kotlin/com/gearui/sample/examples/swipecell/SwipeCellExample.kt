package com.gearui.sample.examples.swipecell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.button.Button
import com.gearui.components.cell.Cell
import com.gearui.components.cellgroup.CellGroup
import com.gearui.components.icon.Icons
import com.gearui.components.swipecell.SwipeCell
import com.gearui.components.swipecell.SwipeCellAction
import com.gearui.components.swipecell.SwipeCellActionTheme
import com.gearui.components.swipecell.SwipeCellIconPosition
import com.gearui.components.swipecell.rememberSwipeCellGroupState
import com.gearui.components.toast.Toast
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface

private val InitialDeleteList = listOf("可删除项 1", "可删除项 2", "可删除项 3")

/**
 * SwipeCell: a row that reveals actions when swiped sideways.
 */
@Composable
fun SwipeCellExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    // Rows sharing a group state are mutually exclusive: opening one closes the others.
    val groupState = rememberSwipeCellGroupState()
    val deleteGroupState = rememberSwipeCellGroupState()
    var deleteList by remember { mutableStateOf(InitialDeleteList) }

    fun action(label: String, theme: SwipeCellActionTheme) =
        SwipeCellAction(label = label, theme = theme, onClick = { Toast.show("点击了$label") })

    fun iconAction(
        label: String,
        icon: String,
        theme: SwipeCellActionTheme,
        position: SwipeCellIconPosition = SwipeCellIconPosition.LEFT
    ) = SwipeCellAction(
        label = label,
        icon = icon,
        iconPosition = position,
        theme = theme,
        onClick = { Toast.show(if (label.isEmpty()) "点击了图标操作" else "点击了$label") }
    )

    val edit = action("编辑", SwipeCellActionTheme.WARNING)
    val delete = action("删除", SwipeCellActionTheme.DANGER)
    val save = action("保存", SwipeCellActionTheme.PRIMARY)
    val select = action("选择", SwipeCellActionTheme.PRIMARY)

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "左滑操作",
            description = "rightActions 放一到三个操作，同组内只展开一行",
            surface = SectionSurface.Plain
        ) {
            val rows = listOf(
                "单操作" to listOf(delete),
                "双操作" to listOf(edit, delete),
                "三操作" to listOf(save, edit, delete)
            )
            CellGroup(items = rows) { (title, actions) ->
                SwipeCell(groupState = groupState, rightActions = actions) {
                    Cell(title = title, note = "左滑")
                }
            }
        }

        ExampleSection(
            title = "右滑与双向",
            description = "leftActions 右滑展开，两侧可同时设置",
            surface = SectionSurface.Plain
        ) {
            CellGroup(items = listOf(0, 1)) { index ->
                if (index == 0) {
                    SwipeCell(groupState = groupState, leftActions = listOf(select)) {
                        Cell(title = "右滑操作", note = "右滑")
                    }
                } else {
                    SwipeCell(
                        groupState = groupState,
                        leftActions = listOf(select),
                        rightActions = listOf(edit, delete)
                    ) {
                        Cell(title = "双向操作", note = "左滑或右滑")
                    }
                }
            }
        }

        ExampleSection(
            title = "带图标",
            description = "图标在文字左侧、仅图标、图标在文字上方",
            surface = SectionSurface.Plain
        ) {
            val rows = listOf(
                "图标加文字（横向）" to listOf(
                    iconAction("编辑", Icons.pencil_simple, SwipeCellActionTheme.WARNING),
                    iconAction("删除", Icons.trash, SwipeCellActionTheme.DANGER)
                ),
                "仅图标" to listOf(
                    iconAction("", Icons.pencil_simple, SwipeCellActionTheme.WARNING),
                    iconAction("", Icons.trash, SwipeCellActionTheme.DANGER)
                ),
                "图标加文字（纵向）" to listOf(
                    iconAction("编辑", Icons.pencil_simple, SwipeCellActionTheme.WARNING, SwipeCellIconPosition.TOP),
                    iconAction("删除", Icons.trash, SwipeCellActionTheme.DANGER, SwipeCellIconPosition.TOP)
                )
            )
            CellGroup(items = rows) { (title, actions) ->
                SwipeCell(groupState = groupState, rightActions = actions) {
                    Cell(title = title, note = "左滑")
                }
            }
        }

        ExampleSection(
            title = "滑动删除",
            description = "点击删除后行从列表移除",
            surface = SectionSurface.Plain
        ) {
            if (deleteList.isNotEmpty()) {
                CellGroup(items = deleteList) { item ->
                    // Keyed so a removed row does not hand its swipe state to the next one.
                    key(item) {
                        SwipeCell(
                            groupState = deleteGroupState,
                            rightActions = listOf(
                                SwipeCellAction(
                                    label = "删除",
                                    theme = SwipeCellActionTheme.DANGER,
                                    onClick = {
                                        deleteList = deleteList - item
                                        Toast.show("删除了：$item")
                                    }
                                )
                            )
                        ) {
                            Cell(title = item, note = "左滑删除")
                        }
                    }
                }
            } else {
                Button(
                    text = "恢复列表",
                    block = true,
                    onClick = { deleteList = InitialDeleteList }
                )
            }
        }
    }
}
