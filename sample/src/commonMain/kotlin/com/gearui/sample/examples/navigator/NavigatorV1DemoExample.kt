package com.gearui.sample.examples.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.navigation.NavOptions
import com.gearui.navigation.Navigator
import com.gearui.navigation.PopDecision
import com.gearui.sample.config.ComponentInfo
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp

/**
 * Navigator v1 demo：
 *
 * - wraps a main -> detail stack in a [Navigator]
 * - demonstrates push / pop / forcePop / popTo / replace / resetTo
 * - demonstrates onPopRequest with Allow and Pending (a dirty-state interception)
 * - demonstrates the exactly-once onEntryRemoved notification
 * - demonstrates the edge swipe back and yielding the system BACK
 *
 * This page deliberately does **not** use the sample's outer SwipeBackHost - Navigator already
 * carries swipeBack, and nesting the two conflicts. MainDemo routes the `navigator-v1-demo` id through a bypass branch.
 */
@Composable
fun NavigatorV1DemoExample(
    component: ComponentInfo,
    onBack: () -> Unit,
) {
    val removedLog = remember { mutableStateListOf<String>() }

    Navigator(
        initialRoute = "main",
        swipeBackEnabled = true,
        handleBack = true,
        onEntryRemoved = { entry ->
            removedLog.add(0, "removed ${entry.key}")
            if (removedLog.size > 20) removedLog.removeAt(removedLog.size - 1)
        },
    ) { entry ->
        when (entry.route) {
            "main" -> MainScreen(
                component = component,
                onExitDemo = onBack,
                removedLog = removedLog,
                push = { route, options -> controller.push(route, options = options) },
                resetTo = { controller.resetTo("main") },
            )

            "detail" -> DetailScreen(
                title = "Detail · key=${entry.key}",
                pushNext = { controller.push("detail") },
                pop = { controller.pop() },
                replaceMe = { controller.replace("detail2") },
                popToMain = { controller.popTo("main") },
            )

            "detail2" -> DetailScreen(
                title = "Detail 2 · key=${entry.key}",
                pushNext = { controller.push("detail") },
                pop = { controller.pop() },
                replaceMe = null,
                popToMain = { controller.popTo("main") },
            )

            "dirty_editor" -> DirtyEditorScreen(
                onTryBack = { controller.pop() },
                onConfirmDiscard = { controller.forcePop() },
            )

            else -> Text("unknown route: ${entry.route}", color = Theme.colors.foreground)
        }
    }
}

@Composable
private fun MainScreen(
    component: ComponentInfo,
    onExitDemo: () -> Unit,
    removedLog: List<String>,
    push: (route: String, options: NavOptions) -> Unit,
    resetTo: () -> Unit,
) {
    val colors = Theme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = component.nameZh, style = Typography.TitleMedium, color = colors.foreground)
        Text(
            text = "栈底 main。push detail 后向右滑动可返回；点系统 BACK 也回退；栈底再按 BACK 应让出 native（exit demo）。",
            style = Typography.BodySmall,
            color = colors.mutedForeground,
        )

        SectionTitle("基本跳转")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(text = "push detail", size = ButtonSize.SMALL, onClick = {
                push("detail", NavOptions.Default)
            })
            Button(text = "push detail2 (replace 入口)", size = ButtonSize.SMALL, onClick = {
                push("detail", NavOptions.Default)
            })
        }

        SectionTitle("pop 拦截（onPopRequest 返回 Pending）")
        Button(
            text = "push dirty_editor",
            size = ButtonSize.SMALL,
            onClick = {
                push(
                    "dirty_editor",
                    NavOptions(
                        onPopRequest = { _ ->
                            // Demonstrates a dirty-state interception: return Pending and show your own confirmation UI
                            PopDecision.Pending
                        },
                    ),
                )
            },
        )

        SectionTitle("栈重置")
        Button(
            text = "resetTo main",
            size = ButtonSize.SMALL,
            theme = ButtonTheme.DEFAULT,
            type = ButtonType.OUTLINE,
            onClick = resetTo,
        )

        SectionTitle("退出 demo（外层）")
        Button(
            text = "Exit Navigator v1 demo",
            size = ButtonSize.SMALL,
            theme = ButtonTheme.DEFAULT,
            type = ButtonType.OUTLINE,
            onClick = onExitDemo,
        )

        SectionTitle("onEntryRemoved 日志（exactly-once）")
        if (removedLog.isEmpty()) {
            Text("(empty)", style = Typography.BodySmall, color = colors.mutedForeground)
        } else {
            removedLog.forEach { line ->
                Text(text = line, style = Typography.BodySmall, color = colors.mutedForeground)
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun DetailScreen(
    title: String,
    pushNext: () -> Unit,
    pop: () -> Boolean,
    replaceMe: (() -> Unit)?,
    popToMain: () -> Boolean,
) {
    val colors = Theme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = title, style = Typography.TitleMedium, color = colors.foreground)
        Text(
            text = "向右滑动 / 系统 BACK 应触发出场动画并返回上一层；按钮可程序化触发等价操作。",
            style = Typography.BodySmall,
            color = colors.mutedForeground,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(text = "push next detail", size = ButtonSize.SMALL, onClick = pushNext)
            Button(
                text = "pop",
                size = ButtonSize.SMALL,
                theme = ButtonTheme.DEFAULT,
                type = ButtonType.OUTLINE,
                onClick = { pop() },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (replaceMe != null) {
                Button(
                    text = "replace with detail2",
                    size = ButtonSize.SMALL,
                    theme = ButtonTheme.DEFAULT,
                    type = ButtonType.OUTLINE,
                    onClick = replaceMe,
                )
            }
            Button(
                text = "popTo main",
                size = ButtonSize.SMALL,
                theme = ButtonTheme.DEFAULT,
                type = ButtonType.OUTLINE,
                onClick = { popToMain() },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center,
        ) {
            Text("（占位内容；可滚动 / 复杂 LazyColumn 放在这里都行）", style = Typography.BodySmall, color = colors.mutedForeground)
        }
    }
}

@Composable
private fun DirtyEditorScreen(
    onTryBack: () -> Unit,
    onConfirmDiscard: () -> Unit,
) {
    val colors = Theme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Dirty Editor", style = Typography.TitleMedium, color = colors.foreground)
        Text(
            text = "本页 onPopRequest 永远返回 Pending：系统 BACK / 边缘滑 / 「尝试 pop」按钮触发后 Navigator 把本次返回视为 consumed，但不真 pop，业务自己挂 confirm UI。",
            style = Typography.BodySmall,
            color = colors.mutedForeground,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(text = "尝试 pop（触发 onPopRequest）", size = ButtonSize.SMALL, onClick = onTryBack)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF7ED))
                .padding(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pending 状态下可调 forcePop() 真正返回（跳过 onPopRequest）。", style = Typography.BodySmall, color = colors.mutedForeground)
                Button(text = "放弃并返回 (forcePop)", size = ButtonSize.SMALL, onClick = onConfirmDiscard)
            }
        }
    }
}

@Composable
private fun SectionTitle(label: String) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = label, style = Typography.BodyMedium, color = Theme.colors.foreground)
}
