#!/usr/bin/env python3
"""Check an app's Kotlin source against the GearUI Kit standard.

Run it on the code you wrote before you call the work done:

    python3 check_gearui_app.py <dir-or-file> [...]
    python3 check_gearui_app.py src/commonMain --json

Each finding names the rule, the line and the fix. Errors exit 1; warnings do not.
A line that genuinely needs an exception carries a reason on it or on the line above:

    val brand = Color(0xFF7C3AED)  // gearui-allow: GU101 brand accent passed to withBrandAccent

An allow without a reason is itself an error: the reason is what a reviewer reads.
The rules mirror the kit's own CI guards and the runtime contracts in its docs
(ARCHITECTURE.md, VISUAL_SPEC.md, COMPONENT_SPEC.md, I18N.md).
"""
from __future__ import annotations

import json
import re
import sys
from dataclasses import dataclass, asdict
from pathlib import Path

ERROR, WARN = "error", "warning"


@dataclass
class Finding:
    file: str
    line: int
    rule: str
    severity: str
    message: str
    fix: str
    text: str


@dataclass
class Rule:
    id: str
    severity: str
    pattern: re.Pattern
    message: str
    fix: str
    # Only applies when the match is not inside a string literal / when the line passes this test.
    unless: re.Pattern | None = None


def rx(p, flags=0):
    return re.compile(p, flags)


# Literal dp is allowed for a hairline-free zero and in a named constant declaration,
# where the name says what the number is (the kit's sample page standard).
NAMED_CONST = rx(r"^\s*(?:private\s+|internal\s+)?(?:const\s+)?val\s+[A-Z_a-z][\w]*\s*(?::\s*Dp\s*)?=")

RULES = [
    # --- Imports: Kuikly Compose lives under com.tencent.kuikly.compose, not androidx.
    Rule("GU001", ERROR, rx(r"^\s*import\s+androidx\.compose\.(?!runtime\b)"),
         "androidx.compose UI/foundation/material import: on KuiklyUI these packages are com.tencent.kuikly.compose.*",
         "Import from com.tencent.kuikly.compose.* (ui, foundation, foundation.layout, ...). Only androidx.compose.runtime.* stays androidx."),
    Rule("GU002", ERROR, rx(r"^\s*import\s+com\.tencent\.kuikly\.compose\.material3?\b"),
         "Material component import: an app on GearUI uses GearUI components, which carry the kit's look, states and accessibility",
         "Use the GearUI component (Button, Input, Switch, Checkbox, Dialog, ...) from com.gearui.components.*; see references/components.md."),
    Rule("GU003", ERROR, rx(r"^\s*import\s+com\.tencent\.kuikly\.compose\.foundation\.lazy\.LazyColumn\b"),
         "Raw LazyColumn: GearLazyColumn is the list that dismisses overlays on scroll, hides the keyboard and keeps a focused field above it",
         "Use com.gearui.foundation.primitives.GearLazyColumn (same parameters plus avoidKeyboard)."),

    # --- Colors.
    Rule("GU101", ERROR, rx(r"\bColor\s*\(\s*(?:0x|red\s*=|\d)"),
         "Hardcoded colour: colours come from the theme so light, dark and brand themes restyle every screen",
         "Use Theme.colors.<role> (val colors = Theme.colors first). A brand colour goes to App(theme = Themes.Light.withBrandAccent(...)) once, with a gearui-allow reason."),
    Rule("GU102", ERROR, rx(r"\bColor\.(?:White|Black|Red|Green|Blue|Gray|Grey|LightGray|DarkGray|Yellow|Cyan|Magenta)\b"),
         "Named platform colour: it does not follow dark mode or the brand",
         "Use Theme.colors.<role>: background/foreground, surface, card, muted/mutedForeground, primary/primaryForeground, destructive, success, warning, separator ..."),

    # --- Geometry.
    Rule("GU201", ERROR, rx(r"\.(?:padding|offset)\s*\([^)]*\b(?!0\.dp)\d+(?:\.\d+)?\.dp"),
         "Literal dp spacing: spacing comes from the 4/8 grid scale",
         "Use Spacing.xs(4) sm(8) md(12) lg(16) xl(24) xxl(32) xxxl(40) huge(48) massive(64)."),
    Rule("GU202", ERROR, rx(r"(?:spacedBy|Spacer\s*\(\s*(?:modifier\s*=\s*)?Modifier\.(?:height|width|size))\s*\(\s*(?!0\.dp)\d+(?:\.\d+)?\.dp"),
         "Literal dp gap: gaps come from the spacing scale",
         "Use Arrangement.spacedBy(Spacing.md) / Spacer(Modifier.height(Spacing.lg))."),
    Rule("GU203", ERROR, rx(r"RoundedCornerShape\s*\(\s*\d"),
         "Literal corner radius: radius is a theme axis (rounded and square presets) owned by Theme.shapes",
         "Use Theme.shapes.sm/md/lg/controlLarge/xl/full (a Shape), or Radius.* where a Dp is needed."),
    Rule("GU204", ERROR, rx(r"\.border\s*\(\s*(?:width\s*=\s*)?\d+(?:\.\d+)?\.dp"),
         "Literal border width",
         "Use BorderWidth.hairline / thin / thick."),
    Rule("GU205", ERROR, rx(r"\belevation\s*=\s*\d+(?:\.\d+)?\.dp|shadow\s*\(\s*(?:elevation\s*=\s*)?\d+(?:\.\d+)?\.dp"),
         "Literal elevation",
         "Use Theme.elevation.raised / floating / modal, or a component that owns its surface (Card)."),
    Rule("GU206", ERROR, rx(r"\bIcon\s*\([^)]*\bsize\s*=\s*\d+(?:\.\d+)?\.dp"),
         "Literal icon size",
         "Use IconSizes.Default.xs..xl beside text, IconSizes.Display.sm/md/lg for illustrations."),
    Rule("GU207", WARN, rx(r"(?<![\w.])(?!0\.dp)\d+(?:\.\d+)?\.dp\b"),
         "Bare dp literal: a number without a name is the drift the token scales exist to stop",
         "Use a scale (Spacing, Radius, IconSizes, ControlGeometry, AvatarSizeTokens) or, for a dimension that is the content itself (an image's height), a named constant at the top of the file.",
         unless=NAMED_CONST),

    # --- Typography.
    Rule("GU301", ERROR, rx(r"\bfontSize\s*=|\bTextStyle\s*\(|\.sp\b"),
         "Hand-made text style: type comes from the theme's semantic styles",
         "Use Theme.typography.<style> (bodyMedium, titleSmall, caption, label, markMedium, ...); never set fontSize."),

    # --- Interaction.
    Rule("GU401", ERROR, rx(r"\.clickable\s*[({]"),
         "Bare clickable: on KuiklyUI LocalIndication never reaches the view, so a clickable shows no press at all",
         "Use the component: Button, Cell(onClick), Modifier.rowClickable(onClick, background) for a row of your own layout, Link, or wrap content in PressableFeedback."),
    Rule("GU402", ERROR, rx(r"\bpointerInput\b.*\bdetectTapGestures\b|\bdetectTapGestures\s*\("),
         "Hand-rolled tap gesture: it gives no press feedback and is not a button to a screen reader",
         "Use Button / Cell / rowClickable / PressableFeedback. Only a custom control's own gesture surface may detect taps."),

    # --- Runtime contracts.
    Rule("GU501", ERROR, rx(r"\b(?:RouterModule|openPage\s*\(|closePage\s*\()"),
         "Kuikly page per screen: an app on GearUI has one @Page holding one Navigator",
         "Declare a sealed interface of NavRoute routes and push/pop them on the Navigator (references/app-structure.md)."),
    Rule("GU502", ERROR, rx(r"\bwhen\s*\(\s*(?:current|selected)?(?:Page|Screen|page|screen|currentPage|currentScreen)\s*\)"),
         "Screen switching with when(page): no swipe back, no stack, every switch rebuilds the screen",
         "Push typed routes on Navigator; for bottom tabs use TabHost(selectedIndex, count)."),
    Rule("GU503", ERROR, rx(r"\bsafeAreaInsets\b"),
         "Reading safe-area insets by hand: the runtime stabilises them and PageScaffold/NavBar/BottomNavBar consume them",
         "Put the page in PageScaffold; for a custom edge use rememberSafeAreaInset(edge) or LocalRuntimeEnvironment.current.safeArea."),
    Rule("GU504", ERROR, rx(r"(?<![\w.])(?:I18nRoot|OverlayRoot|ToastHost|ProvideRuntimeEnvironment)\s*[({]"),
         "Mounting runtime pieces by hand: App mounts I18n, Theme, the overlay host and the toast host exactly once",
         "Call App(...) once at the root (or extend View, which does it); never nest these."),
    Rule("GU505", WARN, rx(r"\bModifier\.(?:alpha|graphicsLayer)\s*\(.*(?:selected|current|tab)", re.I),
         "Hiding tab pages by hand: graphicsLayer does nothing on KuiklyUI and alpha alone leaves the page tappable",
         "Use TabHost(selectedIndex, count) { index -> ... }, which keeps pages alive and blocks touches on hidden ones."),

    # --- Icons.
    Rule("GU601", ERROR, rx("\"[^\"]*[\U0001F300-\U0001FAFF\u2600-\u26ff\u2764][^\"]*\"|\"\\s*[\u2190-\u21ff\u2300-\u23ff\u2713-\u2716\u00d7\u203a\u2039\u00bb\u00ab\u2022\u2b00-\u2bff>]{1,2}\\s*\""),
         "Symbol or emoji used as an icon: it ignores tint, size and dark mode and reads oddly to a screen reader",
         "Use Icon(Icons.<name>) — every Phosphor icon is built in (references/icons.txt), e.g. Icons.check, Icons.x, Icons.caretRight."),
]

# User-facing text passed as a literal. Language belongs to App(languageTag) and the
# app's own typed string pack, so a literal in one language is a bug in every other.
TEXT_PARAMS = r"(?:text|title|label|placeholder|message|description|note|subtitle|hint|confirmText|cancelText|contentDescription|emptyText|helperText|error)"
TEXT_CALL = rx(r"\b(?:Text|Button|Tag|Link|Toast\.\w+|Snackbar\.\w+|Notification\.\w+)\s*\(\s*(\"(?:[^\"\\\\\n]|\\\\.)*\")")
TEXT_ARG = rx(r"\b" + TEXT_PARAMS + r"\s*=\s*(\"(?:[^\"\\\\\n]|\\\\.)*\")")
# `label` also names animations and transitions, which are not shown to anyone.
NOT_UI_LABEL = rx(r"Transition|animate\w*\(|Animatable|tween|spring|keyframes")
LETTER = rx(r"[A-Za-z\u00c0-\u024f\u0400-\u04ff\u0e00-\u0e7f\u3040-\u30ff\u4e00-\u9fff\uac00-\ud7af]")


def literal_has_words(lit: str) -> bool:
    """Whether a string literal carries words of its own, not just interpolated values."""
    body = lit[1:-1]
    body = re.sub(r"\$\{[^}]*\}|\$\w+|\\.", "", body)
    return bool(LETTER.search(body))


def text_literal(line: str, context: str = "") -> bool:
    for m in TEXT_CALL.finditer(line):
        if literal_has_words(m.group(1)):
            return True
    for m in TEXT_ARG.finditer(line):
        if m.group(0).lstrip().startswith("label") and NOT_UI_LABEL.search(context + line):
            continue
        if literal_has_words(m.group(1)):
            return True
    return False


TEXT_RULE = ("GU701", ERROR,
             "Literal user-facing text: language is set once on App(languageTag), so text must come from a typed string pack",
             "Add the string to your app's pack (references/i18n-for-apps.md) and read it, e.g. AppI18n.strings.profile.title; kit copy is I18n.strings.*.")

ALLOW = rx(r"//\s*gearui-allow:\s*(GU\d{3}(?:\s*,\s*GU\d{3})*)\s*(.*)$")


def strip_comments_keep_lines(src: str) -> str:
    """Blank out comments and string contents' comment-like text, keeping line numbers."""
    out = []
    i, n = 0, len(src)
    in_block = False
    while i < n:
        if in_block:
            j = src.find("*/", i)
            if j == -1:
                out.append(re.sub(r"[^\n]", " ", src[i:]))
                break
            out.append(re.sub(r"[^\n]", " ", src[i:j + 2]))
            i = j + 2
            in_block = False
            continue
        c = src[i]
        if c == '"':
            # Copy string literals verbatim (rules look at them), skipping escapes and raw strings.
            if src.startswith('"""', i):
                j = src.find('"""', i + 3)
                j = n if j == -1 else j + 3
            else:
                j = i + 1
                while j < n and src[j] != '"' and src[j] != "\n":
                    j += 2 if src[j] == "\\" else 1
                j = min(j + 1, n)
            out.append(src[i:j])
            i = j
            continue
        if src.startswith("/*", i):
            in_block = True
            continue
        if src.startswith("//", i):
            j = src.find("\n", i)
            j = n if j == -1 else j
            out.append(" " * (j - i))
            i = j
            continue
        out.append(c)
        i += 1
    return "".join(out)


def allowed(raw_lines, idx, rule_id, findings_out, path):
    for k in (idx, idx - 1):
        if k < 0:
            continue
        m = ALLOW.search(raw_lines[k])
        if m and rule_id in [r.strip() for r in m.group(1).split(",")]:
            if not m.group(2).strip():
                findings_out.append(Finding(str(path), k + 1, "GU000", ERROR,
                                            "gearui-allow without a reason",
                                            "Say why this line is an exception.", raw_lines[k].strip()))
            return True
    return False


# A string pack is where literal text belongs: a file that builds `…Strings(...)` values.
STRING_PACK = rx(r"(?:=|\bto)\s*\w*Strings\s*\(|\bdata\s+class\s+\w*Strings(?:Patch)?\s*\(")


def check_file(path: Path):
    raw = path.read_text(encoding="utf-8", errors="replace")
    is_pack = bool(STRING_PACK.search(raw))
    raw_lines = raw.split("\n")
    code_lines = strip_comments_keep_lines(raw).split("\n")
    # Code rules read code, not what a string says: blank out string contents.
    bare_lines = [re.sub(r'"(?:[^"\\\n]|\\.)*"', '""', l) for l in code_lines]
    findings = []
    is_import_only = re.compile(r"^\s*import\s")
    for idx, line in enumerate(code_lines):
        if not line.strip():
            continue
        for rule in RULES:
            if rule.id.startswith("GU0") != bool(is_import_only.match(line)):
                continue
            m = rule.pattern.search(line if rule.id == "GU601" else bare_lines[idx])
            if not m:
                continue
            if rule.unless and rule.unless.search(bare_lines[idx]):
                continue
            # A literal dp the stricter rules already reported is not reported twice.
            if rule.id == "GU207" and any(f.line == idx + 1 and f.rule.startswith("GU2") for f in findings):
                continue
            if allowed(raw_lines, idx, rule.id, findings, path):
                continue
            findings.append(Finding(str(path), idx + 1, rule.id, rule.severity, rule.message, rule.fix, raw_lines[idx].strip()))
        if not is_pack and not is_import_only.match(line) and text_literal(line, "\n".join(code_lines[max(0, idx - 4):idx])):
            rid, sev, msg, fix = TEXT_RULE
            if not allowed(raw_lines, idx, rid, findings, path):
                findings.append(Finding(str(path), idx + 1, rid, sev, msg, fix, raw_lines[idx].strip()))
    return findings, raw


APP_CALL = rx(r"(?<![\w.])App\s*[({]")
DECLARES = rx(r"(?:class|object|interface|fun|val|var|typealias)\s+$")


def app_calls(code):
    """Calls of the App root — `App(...)` or `App { }` — not a declaration named App."""
    return [m for m in APP_CALL.finditer(code) if not DECLARES.search(code[max(0, m.start() - 20):m.start()])]


def check_app_roots(files_and_sources):
    """One App per composition: more than one App call is a second runtime."""
    findings = []
    calls = []
    for path, src in files_and_sources:
        code = re.sub(r'"(?:[^"\\\n]|\\.)*"', '""', strip_comments_keep_lines(src))
        found = app_calls(code)
        for m in found:
            calls.append((path, code.count("\n", 0, m.start()) + 1))
        if found and re.search(r":\s*View\s*\(\s*\)", code) \
                and not re.search(r"autoWrapApp\s*\(\s*\)\s*(?::\s*Boolean\s*)?=\s*false", code):
            m = found[0]
            findings.append(Finding(str(path), code.count("\n", 0, m.start()) + 1, "GU506", ERROR,
                                    "App called inside a View that still wraps App itself: two runtimes, two overlay hosts",
                                    "Override autoWrapApp() = false on the View, or drop the App call and let View mount it.",
                                    "App("))
    if len(calls) > 1:
        for path, line in calls[1:]:
            findings.append(Finding(str(path), line, "GU507", ERROR,
                                    f"Second App root (first at {calls[0][0]}:{calls[0][1]}): a nested App re-provides runtime flags and the overlay host",
                                    "Keep exactly one App at the page root; screens go inside its Navigator.", "App("))
    return findings


def main(argv):
    as_json = "--json" in argv
    targets = [a for a in argv if not a.startswith("--")]
    if not targets:
        print(__doc__)
        return 2
    files = []
    for t in targets:
        p = Path(t)
        if p.is_file() and p.suffix == ".kt":
            files.append(p)
        elif p.is_dir():
            files.extend(sorted(f for f in p.rglob("*.kt") if "/build/" not in str(f)))
    findings, sources = [], []
    for f in files:
        fs, src = check_file(f)
        findings.extend(fs)
        sources.append((f, src))
    findings.extend(check_app_roots(sources))
    findings.sort(key=lambda f: (f.file, f.line, f.rule))
    errors = [f for f in findings if f.severity == ERROR]
    if as_json:
        print(json.dumps({"files": len(files), "errors": len(errors),
                          "warnings": len(findings) - len(errors),
                          "findings": [asdict(f) for f in findings]}, ensure_ascii=False, indent=2))
    else:
        for f in findings:
            print(f"{f.file}:{f.line}: {f.severity} {f.rule} {f.message}\n    {f.text}\n    fix: {f.fix}")
        print(f"\n{len(files)} files, {len(errors)} errors, {len(findings) - len(errors)} warnings")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
