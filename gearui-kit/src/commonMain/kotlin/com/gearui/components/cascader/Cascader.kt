package com.gearui.components.cascader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.gearui.components.bottomsheet.BottomSheet
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonType
import com.gearui.components.closebutton.CloseButton
import com.gearui.components.icon.Icons
import com.gearui.components.loading.Loading
import com.gearui.components.loading.LoadingSize
import com.gearui.components.tabs.Tab
import com.gearui.components.tabs.Tabs
import com.gearui.components.tabs.TabsOutlineType
import com.gearui.foundation.control.ControlGeometry
import com.gearui.foundation.field.FieldDefaults
import com.gearui.foundation.field.FieldErrorText
import com.gearui.foundation.field.FieldSizeTokens
import com.gearui.foundation.field.FieldSurface
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.field.fieldTriggerModifier
import com.gearui.foundation.field.shadowed
import com.gearui.foundation.interaction.choiceSemantics
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.motion.rowPressFeedback
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.i18n.I18n
import com.gearui.theme.LocalInputColors
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.heightIn
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch

/**
 * One node of a [Cascader]: a province, a city, a department.
 *
 * [value] identifies the node among its siblings; [label] is shown. Two cities may share
 * a name — they never share a path of values.
 *
 * [isLeaf] says whether choosing the node completes the selection. It defaults to
 * "has no children", which is right for a tree given in full. For a tree loaded level by
 * level, give the nodes that have children to load `isLeaf = false` and leave
 * [children] empty: [Cascader] asks `loadChildren` for them when they are opened.
 */
data class CascaderOption(
    val value: String,
    val label: String,
    val children: List<CascaderOption> = emptyList(),
    val disabled: Boolean = false,
    val isLeaf: Boolean = children.isEmpty(),
)

/**
 * Cascader — pick a path through a tree, one level at a time: province, city, district.
 *
 * The field opens a bottom sheet in the shape Chinese address pickers take: a tab for
 * each level chosen so far and one saying "请选择" for the next, above a full-width list of
 * the current level. Choosing a node that has children moves to the next tab; choosing a
 * leaf completes the path, calls [onSelect] and closes the sheet. A tab reopens its
 * level. Closing the sheet part-way leaves [selectedPath] as it was.
 *
 * [loadChildren] fetches a level on demand: it runs the first time a node with
 * `isLeaf = false` and no [CascaderOption.children] is opened, shows a spinner, and on
 * failure (a thrown exception) offers a retry. Results are cached for the life of the
 * field. Region data — administrative division codes and their versions — belongs to
 * the app; the kit ships none.
 *
 * @param selectedPath values from the root to a leaf; empty for no selection.
 * @param title the sheet's title; the placeholder when null.
 */
@Composable
fun Cascader(
    options: List<CascaderOption>,
    selectedPath: List<String>,
    onSelect: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = I18n.strings.field.selectPlaceholder,
    title: String? = null,
    enabled: Boolean = true,
    error: String? = null,
    /** PRIMARY on the page background; SECONDARY on a card, sheet or header. */
    variant: FieldVariant = FieldVariant.PRIMARY,
    separator: String = " / ",
    loadChildren: (suspend (CascaderOption) -> List<CascaderOption>)? = null,
) {
    val colors = Theme.colors
    var open by remember { mutableStateOf(false) }
    // Loaded levels, keyed by the path of the node they belong to. Kept for the field's
    // life, so the chosen labels resolve after the sheet closes.
    val loaded = remember { mutableStateMapOf<String, LevelState>() }

    val labels = cascaderLabels(options, selectedPath) { key -> (loaded[key] as? LevelState.Loaded)?.children }
    val displayText = if (selectedPath.isEmpty()) placeholder else labels.joinToString(separator)

    Column(modifier = modifier) {
        FieldSurface(Modifier.fillMaxWidth(), shadowed = variant.shadowed) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FieldSizeTokens.Medium.height)
                    .then(fieldTriggerModifier(enabled, error, variant) { open = true })
                    .padding(horizontal = FieldSizeTokens.Medium.paddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = displayText,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Theme.typography.bodyMedium,
                    color = if (selectedPath.isNotEmpty()) LocalInputColors.current.foreground
                        else LocalInputColors.current.placeholder,
                )
                Icon(name = Icons.caret_down, size = FieldDefaults.trailingIconSize, tint = colors.mutedForeground)
            }
        }
        FieldErrorText(error)
    }

    BottomSheet.Host(visible = open && enabled, onDismiss = { open = false }) {
        CascaderSheet(
            title = title ?: placeholder,
            options = options,
            initialPath = selectedPath,
            loaded = loaded,
            loadChildren = loadChildren,
            onComplete = { path ->
                open = false
                onSelect(path)
            },
            onClose = { open = false },
        )
    }
}

/** A level fetched through `loadChildren`. */
internal sealed interface LevelState {
    data object Loading : LevelState
    data object Failed : LevelState
    data class Loaded(val children: List<CascaderOption>) : LevelState
}

/** What a level of the sheet shows. */
internal sealed interface Level {
    data class Options(val options: List<CascaderOption>) : Level
    data object Loading : Level
    data object Failed : Level

    /** The parent is a leaf, or its level has not been asked for yet. */
    data object None : Level
}

internal fun pathKey(path: List<String>): String = path.joinToString("\u0000")

/**
 * The options of level [depth] under [path] (the values chosen above it). A node's own
 * [CascaderOption.children] win; a node that is not a leaf and has none looks in [loaded].
 */
internal fun cascaderLevel(
    options: List<CascaderOption>,
    path: List<String>,
    depth: Int,
    loaded: (String) -> LevelState?,
): Level {
    var current = options
    for (level in 0 until depth) {
        val node = current.firstOrNull { it.value == path.getOrNull(level) } ?: return Level.None
        current = when {
            node.children.isNotEmpty() -> node.children
            node.isLeaf -> return Level.None
            else -> when (val state = loaded(pathKey(path.take(level + 1)))) {
                is LevelState.Loaded -> state.children
                LevelState.Loading -> return if (level == depth - 1) Level.Loading else Level.None
                LevelState.Failed -> return if (level == depth - 1) Level.Failed else Level.None
                null -> return Level.None
            }
        }
    }
    return Level.Options(current)
}

/** The labels along [path]; a value whose node cannot be found shows as itself. */
internal fun cascaderLabels(
    options: List<CascaderOption>,
    path: List<String>,
    loaded: (String) -> List<CascaderOption>?,
): List<String> {
    val labels = ArrayList<String>(path.size)
    var current: List<CascaderOption>? = options
    path.forEachIndexed { level, value ->
        val node = current?.firstOrNull { it.value == value }
        labels += node?.label ?: value
        current = node?.children?.takeIf { it.isNotEmpty() } ?: loaded(pathKey(path.take(level + 1)))
    }
    return labels
}

@Composable
private fun CascaderSheet(
    title: String,
    options: List<CascaderOption>,
    initialPath: List<String>,
    loaded: SnapshotStateMap<String, LevelState>,
    loadChildren: (suspend (CascaderOption) -> List<CascaderOption>)?,
    onComplete: (List<String>) -> Unit,
    onClose: () -> Unit,
) {
    val colors = Theme.colors
    val strings = I18n.strings
    val scope = rememberCoroutineScope()
    val load by rememberUpdatedState(loadChildren)
    // The path being built; committed only when a leaf is chosen.
    var draft by remember { mutableStateOf(initialPath) }
    var active by remember { mutableStateOf((initialPath.size - 1).coerceAtLeast(0)) }

    fun request(node: CascaderOption, nodePath: List<String>) {
        val loader = load ?: return
        val key = pathKey(nodePath)
        if (loaded[key] is LevelState.Loaded || loaded[key] == LevelState.Loading) return
        loaded[key] = LevelState.Loading
        scope.launch {
            loaded[key] = try {
                LevelState.Loaded(loader(node))
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                LevelState.Failed
            }
        }
    }

    fun nodeAt(depth: Int): CascaderOption? {
        val level = cascaderLevel(options, draft, depth) { loaded[it] } as? Level.Options ?: return null
        return level.options.firstOrNull { it.value == draft.getOrNull(depth) }
    }

    val level = cascaderLevel(options, draft, active) { loaded[it] }
    // Opening a level whose parent has children still to load asks for them.
    LaunchedEffect(active, draft) {
        if (active > 0 && level == Level.None) {
            nodeAt(active - 1)?.let { parent ->
                if (!parent.isLeaf && parent.children.isEmpty()) request(parent, draft.take(active))
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(ControlGeometry.controlLarge).padding(horizontal = Spacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = title, style = Theme.typography.titleMedium, color = colors.foreground)
            CloseButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterEnd))
        }

        val chosen = cascaderLabels(options, draft) { key -> (loaded[key] as? LevelState.Loaded)?.children }
        // A tab per level chosen, and "请选择" for the next one until a leaf ends the path.
        val complete = draft.isNotEmpty() && nodeAt(draft.lastIndex)?.isLeaf == true
        val tabs = chosen.mapIndexed { i, label -> Tab(id = i.toString(), label = label) } +
            if (complete) emptyList() else listOf(Tab(id = chosen.size.toString(), label = strings.field.selectPlaceholder))
        Tabs(
            items = tabs,
            selectedId = active.toString(),
            onSelect = { active = it.toInt() },
            // Level names start where the list's rows do.
            modifier = Modifier.padding(horizontal = Spacing.sm),
            isScrollable = true,
            outlineType = TabsOutlineType.UNDERLINE,
        )

        Box(modifier = Modifier.fillMaxWidth().height(ControlGeometry.cascaderListHeight), contentAlignment = Alignment.Center) {
            when (level) {
                is Level.Options -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(level.options, key = { it.value }) { option ->
                        CascaderRow(
                            option = option,
                            selected = draft.getOrNull(active) == option.value,
                            onClick = {
                                val path = draft.take(active) + option.value
                                draft = path
                                if (option.isLeaf) {
                                    onComplete(path)
                                } else {
                                    active += 1
                                    if (option.children.isEmpty()) request(option, path)
                                }
                            },
                        )
                    }
                }
                Level.Loading, Level.None -> Loading(size = LoadingSize.MEDIUM)
                Level.Failed -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Text(text = strings.common.loadFailed, style = Theme.typography.bodyMedium, color = colors.mutedForeground)
                    Button(
                        text = strings.common.retry,
                        type = ButtonType.TEXT,
                        size = ButtonSize.SMALL,
                        onClick = {
                            val parentPath = draft.take(active)
                            loaded.remove(pathKey(parentPath))
                            nodeAt(active - 1)?.let { request(it, parentPath) }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CascaderRow(option: CascaderOption, selected: Boolean, onClick: () -> Unit) {
    val colors = Theme.colors
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = FieldSizeTokens.Medium.height)
            .choiceSemantics(option.label, selected, Role.Button, onClick = if (option.disabled) null else onClick)
            .rowPressFeedback(interaction = interaction, shape = RectangleShape, enabled = !option.disabled, base = Color.Transparent)
            .clickable(enabled = !option.disabled, interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = option.label,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = Theme.typography.bodyMedium,
            color = when {
                option.disabled -> colors.mutedForeground
                selected -> colors.primary
                else -> colors.foreground
            },
        )
        if (selected) Icon(name = Icons.check, size = FieldDefaults.trailingIconSize, tint = colors.primary)
    }
}
