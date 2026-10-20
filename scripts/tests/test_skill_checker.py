"""Tests for the agent skill's app checker (skills/gearui-kit/scripts/check_gearui_app.py).

Each case is one line an agent might write: the rule it must trip, or that it must not.
A false positive teaches an agent to sprinkle allows; a miss lets the drift through.
"""
import importlib.util
import sys
import tempfile
import textwrap
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location(
    "check_gearui_app", ROOT / "skills/gearui-kit/scripts/check_gearui_app.py")
checker = importlib.util.module_from_spec(spec)
sys.modules[spec.name] = checker  # dataclasses resolve annotations through sys.modules
spec.loader.exec_module(checker)


def rules_for(source: str):
    with tempfile.TemporaryDirectory() as d:
        path = Path(d) / "Screen.kt"
        path.write_text(textwrap.dedent(source))
        findings, src = checker.check_file(path)
        findings += checker.check_app_roots([(path, src)])
        return sorted({f.rule for f in findings if f.severity == checker.ERROR})


class FlagsTheDrift(unittest.TestCase):
    def assertFlags(self, line, rule):
        self.assertIn(rule, rules_for(line), line)

    def test_imports(self):
        self.assertFlags("import androidx.compose.foundation.layout.Column", "GU001")
        self.assertFlags("import com.tencent.kuikly.compose.material3.Button", "GU002")
        self.assertFlags("import com.tencent.kuikly.compose.foundation.lazy.LazyColumn", "GU003")

    def test_colours(self):
        self.assertFlags("val c = Color(0xFF112233)", "GU101")
        self.assertFlags("Box(Modifier.background(Color.White))", "GU102")

    def test_geometry(self):
        self.assertFlags("Column(Modifier.padding(16.dp))", "GU201")
        self.assertFlags("Row(horizontalArrangement = Arrangement.spacedBy(8.dp))", "GU202")
        self.assertFlags("val s = RoundedCornerShape(12.dp)", "GU203")
        self.assertFlags("Box(Modifier.border(1.dp, colors.border))", "GU204")
        self.assertFlags("Icon(Icons.house, size = 20.dp)", "GU206")

    def test_typography(self):
        self.assertFlags("Text(t, style = Theme.typography.bodyMedium.copy(fontSize = 13.sp))", "GU301")

    def test_interaction(self):
        self.assertFlags("Row(Modifier.clickable { open() })", "GU401")

    def test_runtime(self):
        self.assertFlags("RouterModule.openPage(\"Detail\")", "GU501")
        self.assertFlags("when (currentPage) { }", "GU502")
        self.assertFlags("val top = pagerData.safeAreaInsets.top", "GU503")
        self.assertFlags("OverlayRoot { }", "GU504")

    def test_two_roots(self):
        self.assertFlags("""
            fun A() { App { } }
            fun B() { App { } }
        """, "GU507")
        self.assertFlags("""
            class P : View() {
                override fun Content() { App { } }
            }
        """, "GU506")

    def test_symbols_as_icons(self):
        self.assertFlags('Text("✓")', "GU601")
        self.assertFlags('Text("🔥 hot")', "GU601")

    def test_literal_text(self):
        self.assertFlags('Text("Settings")', "GU701")
        self.assertFlags('Button(text = "保存", onClick = {})', "GU701")
        self.assertFlags('Toast.show("Saved")', "GU701")
        self.assertFlags('Icon(Icons.x, contentDescription = "Close")', "GU701")

    def test_allow_needs_reason(self):
        self.assertFlags("val c = Color(0xFF112233) // gearui-allow: GU101", "GU000")


class LeavesStandardCodeAlone(unittest.TestCase):
    def assertClean(self, source):
        self.assertEqual([], rules_for(source), source)

    def test_tokens(self):
        self.assertClean("""
            val colors = Theme.colors
            Column(Modifier.padding(Spacing.lg).background(colors.surface).clip(Theme.shapes.lg),
                   verticalArrangement = Arrangement.spacedBy(Spacing.md)) {}
            Icon(Icons.house, size = IconSizes.Default.md)
            Text(strings.title, style = Theme.typography.titleSmall.copy(fontWeight = FontWeight.Medium))
        """)

    def test_named_constant_and_zero(self):
        self.assertClean("""
            private val CoverHeight = 180.dp
            Spacer(Modifier.height(0.dp))
        """)

    def test_text_from_packs_and_values(self):
        self.assertClean("""
            Text(strings.settings.title)
            Text("${count}")
            Cell(title = "($n)")
            Text(I18n.strings.common.confirm)
            val t = rememberInfiniteTransition(label = "pulse")
        """)

    def test_safe_area_through_runtime(self):
        self.assertClean("val bottom = LocalRuntimeEnvironment.current.safeArea.bottom")

    def test_string_pack_file(self):
        self.assertClean("""
            data class SettingsStrings(val title: String)
            val SettingsEnUs = SettingsStrings(title = "Settings")
            val SettingsZhHans = SettingsStrings(title = "设置")
        """)

    def test_comments_and_strings_are_not_code(self):
        self.assertClean("""
            // Do not use .clickable or Color(0xFF000000) here
            Text(strings.hint) // was Text("Hint")
        """)

    def test_reasoned_allow(self):
        self.assertClean("private val Brand = Color(0xFF7C3AED) // gearui-allow: GU101 brand accent, set once at the root")

    def test_one_root_with_view_opt_out(self):
        self.assertClean("""
            class P : View() {
                override fun autoWrapApp(): Boolean = false
                override fun Content() { App { } }
            }
        """)


if __name__ == "__main__":
    unittest.main()
