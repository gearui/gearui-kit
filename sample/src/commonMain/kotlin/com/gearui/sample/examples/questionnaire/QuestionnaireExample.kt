package com.gearui.sample.examples.questionnaire

import androidx.compose.runtime.*
import com.gearui.components.bottomsheet.BottomSheet
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.questionnaire.*
import com.gearui.foundation.layout.Spacing
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.*
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier

private val approach = QuestionnaireItem(
    name = "approach",
    prompt = "你希望采用哪种实现方案？",
    description = "选定后我会按这个方案修改代码",
    choices = listOf(
        QuestionnaireChoice("minimal", "最小修改", "保留现有架构，只改出问题的地方", badge = "推荐"),
        QuestionnaireChoice("rewrite", "完整重构", "统一架构，长期更好维护"),
        QuestionnaireChoice("phased", "分阶段迁移", "分几次提交，降低一次性改动的风险"),
    ),
    input = QuestionnaireInput("其他方案", "说说你的想法"),
)

private val nextStep = QuestionnaireItem(
    name = "next",
    prompt = "接下来做什么？",
    choices = listOf(
        QuestionnaireChoice("fix", "修复 Bug"),
        QuestionnaireChoice("refactor", "重构架构"),
        QuestionnaireChoice("feature", "继续开发"),
    ),
)

private val wizard = listOf(
    QuestionnaireItem(
        name = "kind",
        prompt = "要创建什么项目？",
        choices = listOf(
            QuestionnaireChoice("app", "移动 App", "iOS、Android 一套代码"),
            QuestionnaireChoice("web", "Web 应用", "在浏览器里运行"),
            QuestionnaireChoice("migrate", "迁移已有项目", "把现有界面换成 GearUI Kit"),
        ),
    ),
    QuestionnaireItem(
        name = "platforms",
        prompt = "要支持哪些平台？",
        description = "可多选",
        choices = listOf(
            QuestionnaireChoice("ios", "iOS"),
            QuestionnaireChoice("android", "Android"),
            QuestionnaireChoice("web", "Web"),
            QuestionnaireChoice("harmony", "HarmonyOS"),
        ),
        multiple = true,
        input = QuestionnaireInput("其他平台"),
    ),
    // Asked only when migrating.
    QuestionnaireItem(
        name = "source",
        prompt = "原项目用的是什么？",
        choices = listOf(
            QuestionnaireChoice("compose", "Jetpack Compose"),
            QuestionnaireChoice("native", "原生 UIKit / View"),
            QuestionnaireChoice("flutter", "Flutter"),
        ),
        input = QuestionnaireInput("其他框架"),
        appliesWhen = { "migrate" in (it["kind"]?.choices ?: emptySet()) },
    ),
    QuestionnaireItem(
        name = "notes",
        prompt = "还有什么要补充的？",
        input = QuestionnaireInput("补充说明", "选填"),
        required = false,
    ),
)

private fun describe(answers: Map<String, QuestionnaireAnswer>): String =
    answers.entries.joinToString("；") { (name, a) ->
        name + "=" + (a.choices + listOfNotNull(a.text.takeIf { it.isNotBlank() })).joinToString(",")
    }.ifEmpty { "无" }

@Composable
fun QuestionnaireExample(component: ComponentInfo, onBack: () -> Unit) {
    var approachResult by remember { mutableStateOf<String?>(null) }
    var nextResult by remember { mutableStateOf<String?>(null) }
    var wizardResult by remember { mutableStateOf<String?>(null) }
    var sheetResult by remember { mutableStateOf<String?>(null) }
    var sheetOpen by remember { mutableStateOf(false) }
    val wizardState = rememberQuestionnaireState()
    val approachState = rememberQuestionnaireState()
    val nextState = rememberQuestionnaireState()

    ExamplePage(component, onBack) {
        ExampleSection("方案选择", "选项带说明与「推荐」标记，可填写其他方案", SectionSurface.Plain) {
            Questionnaire(listOf(approach), { approachResult = describe(it) }, state = approachState, submitLabel = "确认")
            Result(approachResult) { approachResult = null; approachState.reset() }
        }
        ExampleSection("选中即提交", "advanceOnChoice：单选题点一下就作答，适合会话里的快速决策", SectionSurface.Plain) {
            Questionnaire(listOf(nextStep), { nextResult = describe(it) }, state = nextState, advanceOnChoice = true)
            Result(nextResult) { nextResult = null; nextState.reset() }
        }
        ExampleSection("多步向导", "进度、上一步、多选、条件题（选「迁移已有项目」才问原框架）、可跳过的选填题", SectionSurface.Plain) {
            Questionnaire(wizard, { wizardResult = describe(it) }, state = wizardState, submitLabel = "创建")
            Result(wizardResult) { wizardResult = null; wizardState.reset() }
        }
        ExampleSection("在底部面板中提问", "AI 会话里弹出提问，回答后关闭", SectionSurface.Plain) {
            Button("让助手提问", { sheetOpen = true }, theme = ButtonTheme.PRIMARY, type = ButtonType.OUTLINE)
            Result(sheetResult) { sheetResult = null }
        }
    }

    BottomSheet(visible = sheetOpen, onDismiss = { sheetOpen = false }, showCancel = false) {
        Box(Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md)) {
            Questionnaire(listOf(approach), { sheetResult = describe(it); sheetOpen = false }, submitLabel = "确认")
        }
    }
}

@Composable
private fun Result(text: String?, onReset: () -> Unit) {
    if (text == null) return
    Row(Modifier.fillMaxWidth(), verticalAlignment = com.tencent.kuikly.compose.ui.Alignment.CenterVertically) {
        Text("提交：$text", style = Theme.typography.bodySmall, color = Theme.colors.mutedForeground, modifier = Modifier.weight(1f))
        Button("重来", onReset, theme = ButtonTheme.DEFAULT, type = ButtonType.TEXT)
    }
}
