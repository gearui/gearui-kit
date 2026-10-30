package com.gearui.sample.examples.aiblocks

import androidx.compose.runtime.*
import com.gearui.components.agenttrace.AgentStep
import com.gearui.components.agenttrace.AgentTrace
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.codeblock.CodeBlock
import com.gearui.components.icon.*
import com.gearui.components.reasoning.Reasoning
import com.gearui.components.suggestion.Suggestion
import com.gearui.components.suggestion.SuggestionChips
import com.gearui.components.toast.Toast
import com.gearui.components.toolcall.ToolCallCard
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.status.ActivityStatus
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.*
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.*
import kotlinx.coroutines.delay

private const val KOTLIN = """@Composable
fun Greeting(name: String) {
    var count by remember { mutableStateOf(0) }
    Column {
        Text("Hello, ${'$'}name")
        Button(text = "Tapped ${'$'}count times", onClick = { count++ })
    }
}"""

private val LONG = (1..40).joinToString("\n") { "val line$it = \"第 $it 行\"" }

private const val DIFF = """--- a/Settings.kt
+++ b/Settings.kt
@@ -12,6 +12,7 @@
 fun Settings() {
-    val dark = false
+    val dark = isSystemInDarkTheme()
+    val accent = Theme.colors.primary
     Column {"""

// The sample has no clipboard of its own; a real app copies here.
private val sampleCopy: (String) -> Unit = { Toast.show("已复制（示例：由宿主写入剪贴板）") }

@Composable
fun CodeBlockExample(component: ComponentInfo, onBack: () -> Unit) {
    ExamplePage(component, onBack) {
        ExampleSection("代码", "语言标签、行号、复制；不换行，横向滑动", SectionSurface.Plain) {
            CodeBlock(KOTLIN, language = "kotlin", onCopy = sampleCopy)
        }
        ExampleSection("长代码", "超过 12 行先截断，「展开全部」后显示整段", SectionSurface.Plain) {
            CodeBlock(LONG, language = "kotlin", onCopy = sampleCopy)
        }
        ExampleSection("Diff", "diff = true：+ 行标为新增，- 行标为删除", SectionSurface.Plain) {
            CodeBlock(DIFF, language = "diff", diff = true, onCopy = sampleCopy)
        }
        ExampleSection("命令输出", "无标题栏、无行号", SectionSurface.Plain) {
            CodeBlock("$ ./gradlew :sample:installDebug\nBUILD SUCCESSFUL in 41s", showLineNumbers = false)
        }
    }
}

@Composable
fun ReasoningExample(component: ComponentInfo, onBack: () -> Unit) {
    val full = "用户想把设置页改成跟随系统深色模式。先看 Settings.kt 里 dark 是怎么来的——它写死了 false。" +
        "改成 isSystemInDarkTheme() 即可，主题色随之从 Theme 读取，不用另外传。还要确认没有别处覆盖它。"
    var thinking by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(full) }
    var seconds by remember { mutableStateOf(6) }
    LaunchedEffect(thinking) {
        if (!thinking) return@LaunchedEffect
        text = ""
        seconds = 0
        // Streams the reasoning a few characters at a time, as a model would.
        while (text.length < full.length) {
            delay(60)
            text = full.take(text.length + 3)
        }
        seconds = 4
        thinking = false
    }
    ExamplePage(component, onBack) {
        ExampleSection("思考中", "流式输出时展开并显示转圈；结束后自动收起成「已思考 N 秒」", SectionSurface.Plain) {
            Reasoning(text, thinking = thinking, durationSeconds = seconds)
            Button("模拟一次思考", { thinking = true }, theme = ButtonTheme.PRIMARY, type = ButtonType.OUTLINE, disabled = thinking)
        }
        ExampleSection("未知时长", "durationSeconds = null 时标题为「思考过程」", SectionSurface.Plain) {
            Reasoning(full, thinking = false)
        }
    }
}

@Composable
fun ToolCallExample(component: ComponentInfo, onBack: () -> Unit) {
    ExamplePage(component, onBack) {
        ExampleSection("运行中", "参数已发出，等待结果", SectionSurface.Plain) {
            ToolCallCard("read_file", ActivityStatus.Running, input = "{ \"path\": \"Settings.kt\" }")
        }
        ExampleSection("成功", "点开看参数和结果；结果过长先截断", SectionSurface.Plain) {
            ToolCallCard("search_code", ActivityStatus.Succeeded, input = "{ \"query\": \"isSystemInDarkTheme\" }",
                output = (1..20).joinToString("\n") { "src/screen$it/Theme.kt:$it" })
        }
        ExampleSection("失败", "失败时默认展开，直接看到错误", SectionSurface.Plain) {
            ToolCallCard("write_file", ActivityStatus.Failed, input = "{ \"path\": \"/etc/hosts\" }",
                error = "Permission denied: /etc/hosts")
        }
    }
}

@Composable
fun AgentTraceExample(component: ComponentInfo, onBack: () -> Unit) {
    val plan = listOf("读取设置页代码", "查找深色模式的用法", "修改 Settings.kt", "运行测试")
    var reached by remember { mutableStateOf(plan.size) }
    var failAt by remember { mutableStateOf(-1) }
    var running by remember { mutableStateOf(false) }
    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        reached = 0
        while (reached < plan.size) {
            delay(1200)
            reached++
            if (reached == failAt + 1) break
        }
        running = false
    }
    fun status(i: Int): ActivityStatus = when {
        i == failAt && reached > i -> ActivityStatus.Failed
        failAt in 0 until i && reached > failAt -> ActivityStatus.Cancelled
        i < reached -> ActivityStatus.Succeeded
        i == reached && running -> ActivityStatus.Running
        else -> ActivityStatus.Pending
    }
    val steps = plan.mapIndexed { i, title ->
        AgentStep("s$i", title, status(i), description = if (i == 2) "改 2 处，+2 −1 行" else null,
            detail = if (i == 2 && status(i) == ActivityStatus.Succeeded) ({
                CodeBlock(DIFF, language = "diff", diff = true, showLineNumbers = false)
            }) else null)
    }
    ExamplePage(component, onBack) {
        ExampleSection("执行过程", "运行中只有当前步骤转圈；全部结束后折叠成一行摘要，可再展开", SectionSurface.Plain) {
            AgentTrace(steps, title = "改为跟随系统深色模式")
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Button("运行", { failAt = -1; running = true }, theme = ButtonTheme.PRIMARY, type = ButtonType.OUTLINE, disabled = running)
                Button("运行（第 3 步失败）", { failAt = 2; running = true }, theme = ButtonTheme.DEFAULT, type = ButtonType.OUTLINE, disabled = running)
            }
        }
        ExampleSection("步骤里放工具调用", "detail 可放任意内容", SectionSurface.Plain) {
            AgentTrace(
                listOf(
                    AgentStep("a", "搜索代码", ActivityStatus.Succeeded, detail = {
                        ToolCallCard("search_code", ActivityStatus.Succeeded, input = "{ \"query\": \"dark\" }", output = "Settings.kt:14")
                    }),
                    AgentStep("b", "等待你确认改动", ActivityStatus.Pending),
                ),
                collapseWhenDone = false,
            )
        }
    }
}

@Composable
fun SuggestionExample(component: ComponentInfo, onBack: () -> Unit) {
    var picked by remember { mutableStateOf<String?>(null) }
    val quick = listOf(
        Suggestion("explain", "解释这段代码", Icons.lightbulb),
        Suggestion("test", "帮我写单测", Icons.check),
        Suggestion("refactor", "重构成更易读的写法", Icons.sparkle),
        Suggestion("perf", "有没有性能问题？"),
    )
    ExamplePage(component, onBack) {
        ExampleSection("快捷回复", "单行横滑，放在回复下方或输入框上方", SectionSurface.Plain) {
            SuggestionChips(quick, { picked = it.label })
        }
        ExampleSection("欢迎页推荐问题", "wrap = true：自动换行", SectionSurface.Plain) {
            SuggestionChips(quick + Suggestion("more", "今天有什么待办？"), { picked = it.label }, wrap = true)
        }
        Text("选择：" + (picked ?: "无"), style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground)
    }
}
