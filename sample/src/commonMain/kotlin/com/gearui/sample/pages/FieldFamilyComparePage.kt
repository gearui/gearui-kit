package com.gearui.sample.pages

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.gearui.foundation.primitives.Icon
import com.gearui.components.cascader.Cascader
import com.gearui.components.cascader.CascaderOption
import com.gearui.components.input.Input
import com.gearui.components.picker.DatePickerInput
import com.gearui.components.picker.TimePickerInput
import com.gearui.components.searchbar.SearchBar
import com.gearui.components.searchbar.SearchBarShape
import com.gearui.components.select.Select
import com.gearui.components.select.SelectOption
import com.gearui.components.tree.TreeNode
import com.gearui.components.treeselect.TreeSelect
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.GearLazyColumn
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Internal visual regression page — NOT part of the component gallery.
 *
 * Stacks every field-family trigger so the shared FieldTokens geometry can be
 * checked side by side: they should agree on height (40dp), corner radius
 * (`Shapes.md`, 6dp), horizontal padding, border weight and trailing icon
 * size. Reach for this whenever those tokens change, instead of rebuilding a
 * throwaway page each time.
 *
 * Deliberately not registered in ComponentConfig / NavigationManager and not
 * linked from the home page: the CI index check ties `examples/<dir>` to those
 * two registries, and this is a diagnostic rather than documentation. To view
 * it, point `MainDemoContent` at `FieldFamilyComparePage()` in place of
 * `MainDemoContentInner(...)`, and revert before committing.
 *
 * The icon probe row at the top is load-bearing. Icons resolve through coil3
 * from `assets://icons/<name>.png`, so a missing asset renders as a correctly
 * sized blank box with no error anywhere — that is how the iOS Copy Pods
 * Resources phase went missing unnoticed. A blank probe row means the asset
 * pipeline is broken, not that the icon name is wrong.
 *
 * Focused state cannot be shown statically — tap a field to check it.
 *
 * The family now exposes the same states everywhere — `enabled: Boolean = true`
 * and `error: String? = null` — so the disabled and error rows are complete.
 * SearchBar is the one exception and takes no `error`: it is a search
 * affordance rather than a form field, with no value to validate.
 */
@Composable
fun FieldFamilyComparePage() {
    val colors = Theme.colors

    var text by remember { mutableStateOf("") }
    var filled by remember { mutableStateOf("Filled value") }
    var search by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<String?>(null) }
    var path by remember { mutableStateOf(listOf<String>()) }
    var treeKey by remember { mutableStateOf<String?>(null) }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    val options = listOf(
        SelectOption("a", "Option A"),
        SelectOption("b", "Option B"),
        SelectOption("c", "Option C"),
    )
    val cascade = listOf(
        CascaderOption("cn", "China", listOf(CascaderOption("bj", "Beijing"))),
        CascaderOption("jp", "Japan", listOf(CascaderOption("tk", "Tokyo"))),
    )
    val tree = listOf(
        TreeNode("root", "Root", listOf(TreeNode("leaf", "Leaf"))),
    )

    Column(
        modifier = Modifier.fillMaxSize().background(colors.background)
    ) {
        Text(
            text = "Field family — geometry cross-check",
            style = Typography.TitleMedium,
            color = colors.foreground,
            modifier = Modifier.padding(
                start = Spacing.lg,
                end = Spacing.lg,
                top = 60.dp,
                bottom = Spacing.sm,
            ),
        )

        GearLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item {
                Section("Icon probe — chevron / search / calendar") {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                        Icon(name = Icons.keyboard_arrow_down, size = 24.dp)
                        Icon(name = Icons.search, size = 24.dp)
                        Text(text = "\uD83D\uDCC5", style = Typography.BodyLarge, color = colors.foreground)
                    }
                }
            }

            item {
                Section("Default") {
                    Input(value = text, onValueChange = { text = it }, placeholder = "Input")
                    SearchBar(
                        value = search,
                        onValueChange = { search = it },
                        shape = SearchBarShape.SQUARE,
                    )
                    Select(
                        value = selected,
                        options = options,
                        onValueChange = { selected = it },
                        placeholder = "Select",
                    )
                    Cascader(
                        options = cascade,
                        selectedPath = path,
                        onSelect = { path = it },
                        placeholder = "Cascader",
                    )
                    TreeSelect(
                        nodes = tree,
                        selectedKey = treeKey,
                        onSelect = { treeKey = it },
                        placeholder = "TreeSelect",
                    )
                    DatePickerInput(value = date, onValueChange = { date = it })
                    TimePickerInput(value = time, onValueChange = { time = it })
                }
            }

            item {
                Section("Filled") {
                    Input(value = filled, onValueChange = { filled = it })
                    Select(
                        value = "b",
                        options = options,
                        onValueChange = {},
                    )
                    DatePickerInput(value = "2026-08-10", onValueChange = {})
                    TimePickerInput(value = "09:30", onValueChange = {})
                }
            }

            item {
                Section("Disabled") {
                    Input(value = "Disabled", onValueChange = {}, enabled = false)
                    SearchBar(value = "", onValueChange = {}, enabled = false, shape = SearchBarShape.SQUARE)
                    Select(value = null, options = options, onValueChange = {}, enabled = false)
                    Cascader(
                        options = cascade,
                        selectedPath = emptyList(),
                        onSelect = {},
                        enabled = false,
                    )
                    TreeSelect(nodes = tree, selectedKey = null, onSelect = {}, enabled = false)
                    DatePickerInput(value = "", onValueChange = {}, enabled = false)
                    TimePickerInput(value = "", onValueChange = {}, enabled = false)
                }
            }

            item {
                Section("Error — SearchBar excluded, it has no value to validate") {
                    Input(value = "bad", onValueChange = {}, error = "Something is wrong")
                    Select(
                        value = null,
                        options = options,
                        onValueChange = {},
                        error = "Something is wrong",
                    )
                    Cascader(
                        options = cascade,
                        selectedPath = emptyList(),
                        onSelect = {},
                        error = "Something is wrong",
                    )
                    TreeSelect(
                        nodes = tree,
                        selectedKey = null,
                        onSelect = {},
                        error = "Something is wrong",
                    )
                    DatePickerInput(value = "", onValueChange = {}, error = "Something is wrong")
                    TimePickerInput(value = "", onValueChange = {}, error = "Something is wrong")
                }
            }

            item { Spacer(modifier = Modifier.height(120.dp)) }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    val colors = Theme.colors
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(text = title, style = Typography.BodySmall, color = colors.mutedForeground)
        content()
    }
}
