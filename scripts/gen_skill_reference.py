#!/usr/bin/env python3
"""Generate the API reference of the GearUI Kit agent skill from the kit's source.

An agent writing an app against GearUI copies signatures from this reference, so it
must be the source itself: every public declaration with its parameter names,
defaults, KDoc and package. A hand-written reference drifts the first time a
parameter is renamed, and then the agent writes code that does not compile.

Output: skills/gearui-kit/references/api/*.md and references/icons.txt.
check_skill_reference.sh (CI) fails when they are stale.

Usage:
    scripts/gen_skill_reference.py            # rewrite the reference
    scripts/gen_skill_reference.py --check    # exit 1 if it would change
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "gearui-kit/src/commonMain/kotlin/com/gearui"
REGISTRY = ROOT / "sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt"
OUT = ROOT / "skills/gearui-kit/references"

# Areas of the kit that are not components but that every app touches. Each becomes
# one reference file; the components are grouped by the sample registry's categories.
CORE_AREAS = [
    ("app-runtime", "App root, View, runtime environment and flags",
     ["App.kt", "View.kt", "runtime", "system"]),
    ("navigation", "Navigator, routes, TabHost", ["navigation"]),
    ("theme-tokens", "Theme, colors, shapes, typography and the token scales",
     ["theme", "ColorTokens.kt", "foundation/layout", "foundation/elevation", "foundation/border",
      "foundation/typography", "foundation/motion", "foundation/animation", "foundation/control",
      "foundation/color", "unit"]),
    ("i18n", "Language runtime and typed string packs", ["i18n"]),
    ("primitives", "Text, Icon, lists, surfaces and other building blocks",
     ["foundation/primitives", "primitives", "foundation/list", "foundation/interaction",
      "foundation/field", "foundation/keyboard", "foundation/scroll", "foundation/material",
      "foundation/avatar", "foundation/button", "foundation/tag", "foundation/tab",
      "foundation/sheet", "foundation/swipecell", "foundation/calendar", "compose", "gestures"]),
    ("overlay", "Overlay runtime", ["overlay"]),
]
CATEGORY_FILE = {
    "BASIC": ("components-basic", "Basic components"),
    "FORM": ("components-form", "Form components"),
    "NAVIGATION": ("components-navigation", "Navigation components"),
    "DATA_DISPLAY": ("components-data-display", "Data display components"),
    "FEEDBACK": ("components-feedback", "Feedback components"),
    "LAYOUT": ("components-layout", "Layout components"),
}
# Component directories whose registry id does not spell the directory name.
DIR_ALIASES = {"pull-refresh": "refresh", "loadmore": "refresh", "agreement": "checkbox",
               "datepicker": "picker", "typed-form": "form", "pressable-feedback": None,
               "list": None, "card": None, "text": None, "badge": None, "divider": None,
               "format": None}

DECL = re.compile(
    r"^(?:(?:public|inline|data|enum|sealed|abstract|open|value|annotation|fun|operator|infix|tailrec|suspend|const)\s+)*"
    r"(fun|class|interface|object|val|var|typealias)\b"
)
HIDDEN = re.compile(r"^(?:private|internal|protected)\b|^\S+\s+(?:private|internal)\b")
KDOC_LINES = 14


def registry():
    text = REGISTRY.read_text()
    return re.findall(r'ComponentInfo\("([^"]+)",\s*"([^"]*)",\s*"([^"]+)",\s*ComponentCategory\.(\w+),\s*"[^"]*",\s*"([^"]*)",\s*"([^"]*)"', text)


def is_component(cid):
    return not cid.startswith("runtime-") and cid not in {
        "typed-form", "icon-render", "navigator-kuikly-spike", "navigator-v1-demo"}


def clean_kdoc(block):
    lines = []
    for raw in block.splitlines():
        line = raw.strip()
        line = re.sub(r"^/\*\*\s?|\s?\*/$", "", line)
        line = re.sub(r"^\*\s?", "", line)
        lines.append(line.rstrip())
    while lines and not lines[0]:
        lines.pop(0)
    while lines and not lines[-1]:
        lines.pop()
    if len(lines) > KDOC_LINES:
        lines = lines[:KDOC_LINES] + ["…"]
    return lines


def header_end(text, start):
    """End of a declaration header: the body's `{` or `=` at nesting depth 0."""
    depth = 0
    i = start
    in_str = False
    while i < len(text):
        c = text[i]
        if in_str:
            if c == "\\":
                i += 2
                continue
            if c == '"':
                in_str = False
        elif c == '"':
            in_str = True
        elif c in "([":
            depth += 1
        elif c in ")]":
            depth -= 1
        elif depth == 0 and c == "{":
            return i, "{"
        elif depth == 0 and c == "=" and text[i + 1:i + 2] != ">" and text[i - 1:i] not in "!<>=":
            return i, "="
        elif depth == 0 and c == "\n":
            # A declaration without a body ends at the line break once its parentheses close,
            # unless the next line continues it (a return type, a supertype list, `where`).
            nxt = text[i + 1:i + 40].lstrip(" ")
            if not re.match(r"[:.,)>]|where\b|->", nxt):
                return i, ""
        i += 1
    return len(text), ""


def body_span(text, open_brace):
    depth = 0
    i = open_brace
    in_str = False
    while i < len(text):
        c = text[i]
        if in_str:
            if c == "\\":
                i += 2
                continue
            if c == '"':
                in_str = False
        elif c == '"':
            in_str = True
        elif c == "{":
            depth += 1
        elif c == "}":
            depth -= 1
            if depth == 0:
                return i
        i += 1
    return len(text)


def members(body, enum):
    """Public members one level down: enum entries, object/class vals, funs and nested objects.

    A value is kept when it is short: `val md: Dp = 12.dp` tells an agent what the step
    is, and token objects are exactly where that matters.
    """
    out = []
    plain = re.sub(r"/\*.*?\*/|//[^\n]*", "", body, flags=re.S)
    if enum:
        head = plain.split(";")[0]
        names = [m.group(1) for m in (re.match(r"\s*([A-Z][A-Za-z0-9_]*)", part) for part in head.split(",")) if m]
        if names:
            out.append("entries: " + ", ".join(names))
    lines = plain.split("\n")
    i = 0
    while i < len(lines):
        line = lines[i]
        m = re.match(r"^ {4}(?!private|internal|protected|override)((?:@\w+\s+)*(?:(?:open|inline|const|operator|data)\s+)*(?:val|var|fun|object|class)\s.*)$", line)
        if not m:
            i += 1
            continue
        sig = m.group(1).rstrip()
        if sig.endswith("{"):
            sig = sig[:-1].rstrip()
        if re.search(r"\($", sig) or (sig.count("(") > sig.count(")")):
            # A multi-line initializer or parameter list: keep it if it is short.
            block = [sig]
            j = i + 1
            while j < len(lines) and not re.match(r"^ {4}\)", lines[j]) and j - i < 10:
                block.append(lines[j].strip())
                j += 1
            if j < len(lines) and re.match(r"^ {4}\)", lines[j]) and j - i < 10:
                sig = " ".join(block) + " )"
                i = j
            else:
                sig = sig + " …)"
        out.append(sig)
        i += 1
    return out[:80]


def declarations(path: Path):
    text = path.read_text()
    package = re.search(r"^package\s+([\w.]+)", text, re.M).group(1)
    decls = []
    pos = 0
    kdoc = None
    annotations = []
    lines = text.split("\n")
    offsets = []
    acc = 0
    for line in lines:
        offsets.append(acc)
        acc += len(line) + 1
    i = 0
    while i < len(lines):
        line = lines[i]
        if line.startswith("/**"):
            j = i
            while "*/" not in lines[j]:
                j += 1
            kdoc = "\n".join(lines[i:j + 1])
            i = j + 1
            continue
        if line.startswith("@"):
            annotations.append(line.strip())
            i += 1
            continue
        if not line or line[0] in " \t}/" or line.startswith("import") or line.startswith("package"):
            if line.strip() and not line.startswith("import") and not line.startswith("package"):
                kdoc, annotations = None, []
            i += 1
            continue
        if HIDDEN.match(line) or any(a.startswith("@Deprecated") for a in annotations):
            start = offsets[i]
            end, kind = header_end(text, start)
            if kind == "{":
                end = body_span(text, end)
            i = text.count("\n", 0, end) + 1
            kdoc, annotations = None, []
            continue
        m = DECL.match(line)
        if not m:
            kdoc, annotations = None, []
            i += 1
            continue
        start = offsets[i]
        end, kind = header_end(text, start)
        header = text[start:end].rstrip()
        body_members = []
        stop = end
        if kind == "{":
            close = body_span(text, end)
            if m.group(1) in ("class", "object", "interface"):
                body_members = members(text[end + 1:close], "enum class" in header)
            stop = close
        elif kind == "=":
            nl = text.find("\n", end)
            stop = nl if nl != -1 else len(text)
            if m.group(1) in ("val", "var"):
                header = text[start:stop].rstrip()
        name = re.search(r"(?:fun|class|interface|object|val|var|typealias)\s+(?:<[^>]*>\s*)?(?:[\w.<>]+\.)?(\w+)", header)
        decls.append({
            "package": package,
            "kind": m.group(1),
            "name": name.group(1) if name else "?",
            "annotations": [a for a in annotations if a.split("(")[0] in ("@Composable", "@Stable", "@Immutable")],
            "kdoc": clean_kdoc(kdoc) if kdoc else [],
            "header": dedent_header(header),
            "members": body_members,
        })
        kdoc, annotations = None, []
        i = text.count("\n", 0, stop) + 1
    return decls


def dedent_header(h):
    out = []
    for line in h.splitlines():
        # Inline comments inside parameter lists (`// ...`) are kept: they often carry
        # the one sentence an agent needs about a parameter. Long block comments are not.
        out.append(line.rstrip())
    joined = "\n".join(out)
    joined = re.sub(r"\n\s*/\*\*.*?\*/", "", joined, flags=re.S)
    return joined


def render_decl(d):
    parts = []
    if d["kdoc"]:
        parts.append("\n".join(f"// {l}" if l else "//" for l in d["kdoc"]))
    parts.extend(d["annotations"])
    parts.append(d["header"])
    if d["members"]:
        parts.append("    // members: " + "\n    //   ".join(d["members"]))
    return "\n".join(parts)


def collect(paths):
    files = []
    for p in paths:
        full = SRC / p
        if full.is_file():
            files.append(full)
        elif full.is_dir():
            files.extend(sorted(f for f in full.rglob("*.kt") if "generated" not in f.parts))
    return files


def render_file(title, intro, groups):
    out = [f"# {title}", "", "<!-- Generated by scripts/gen_skill_reference.py from the kit source. Do not edit. -->", ""]
    if intro:
        out += [intro, ""]
    for heading, files in groups:
        sections = []
        for f in files:
            decls = declarations(f)
            if not decls:
                continue
            rel = f.relative_to(SRC.parent.parent.parent.parent)
            pkg = decls[0]["package"]
            body = "\n\n".join(render_decl(d) for d in decls)
            sections.append(f"### `{f.stem}` — `import {pkg}.*`\n\nSource: `{rel}`\n\n```kotlin\n{body}\n```\n")
        if sections:
            out.append(f"## {heading}\n")
            out.extend(sections)
    return "\n".join(out).rstrip() + "\n"


def icon_names():
    gen = SRC / "components/icon/generated"
    names = set()
    for f in gen.rglob("*.kt"):
        names.update(re.findall(r"^val Icons\.(\w+)\b", f.read_text(), re.M))
    return sorted(names)


def build():
    files = {}
    for key, title, paths in CORE_AREAS:
        files[f"api/{key}.md"] = render_file(title, None, [(title, collect(paths))])

    comps = sorted(p.name for p in (SRC / "components").iterdir() if p.is_dir())
    entries = [e for e in registry() if is_component(e[0])]
    used = set()
    by_cat = {c: [] for c in CATEGORY_FILE}
    index_rows = []
    for cid, zh, en, cat, desc_zh, desc_en in entries:
        d = DIR_ALIASES.get(cid, cid.replace("-", ""))
        fname, _ = CATEGORY_FILE[cat]
        where = f"api/{fname}.md" if d in comps else "api/primitives.md"
        if d in comps and d not in used:
            used.add(d)
            by_cat[cat].append((d, collect([f"components/{d}"])))
        index_rows.append(f"| `{en}` | {desc_en} / {desc_zh} | {where} |")
    leftovers = [d for d in comps if d not in used]
    for d in leftovers:
        by_cat["LAYOUT" if d == "scaffold" else "DATA_DISPLAY"].append((d, collect([f"components/{d}"])))

    for cat, (fname, title) in CATEGORY_FILE.items():
        groups = [(f"components/{d}", fs) for d, fs in by_cat[cat]]
        files[f"api/{fname}.md"] = render_file(title, None, groups)

    index = ["# Component index", "",
             "<!-- Generated by scripts/gen_skill_reference.py. Do not edit. -->", "",
             "Every component in the kit, from the sample registry. Find the one you need, then",
             "read its signatures in the file named in the last column.", "",
             "| Component | Purpose | Signatures |", "| --- | --- | --- |", *index_rows, ""]
    files["components.md"] = "\n".join(index)
    files["icons.txt"] = "\n".join(icon_names()) + "\n"
    return files


def main():
    check = "--check" in sys.argv
    files = build()
    stale = []
    for rel, content in files.items():
        path = OUT / rel
        if check:
            if not path.exists() or path.read_text() != content:
                stale.append(rel)
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(content)
    if check:
        existing = {str(p.relative_to(OUT)) for p in (OUT / "api").glob("*.md")} if (OUT / "api").exists() else set()
        stale += sorted(existing - set(files))
        if stale:
            print("Skill reference is stale; run scripts/gen_skill_reference.py:\n  " + "\n  ".join(stale))
            return 1
        print(f"Skill reference current ({len(files)} files).")
        return 0
    print(f"Wrote {len(files)} files under {OUT.relative_to(ROOT)}.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
