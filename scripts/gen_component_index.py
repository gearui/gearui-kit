#!/usr/bin/env python3
"""Render the component index into README.md and README.zh-Hans.md.

The single source of truth is the sample's registry,
sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt.
That file already carries an English and a Chinese name and description for
every component and is what the sample's home page is built from, so a table
generated from it can never disagree with what the app shows.

Hand-written component lists in READMEs drift. This one had drifted to a
vague "50+" by the first release. Do not edit the generated block by hand;
edit ComponentConfig.kt and rerun this script. check_readme_component_index.sh
fails CI when the block is stale.

Usage:
    scripts/gen_component_index.py            # rewrite both READMEs in place
    scripts/gen_component_index.py --check    # exit 1 if either would change
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
REGISTRY = ROOT / "sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt"

# Registered routes that exist to exercise the runtime rather than to ship a
# component. They stay in the sample index because they are useful there, but
# they are not "components" and must not inflate the count.
NON_COMPONENT_IDS = {
    "icon-render",             # PNG/SVG rendering check
    "runtime-insets",          # safe-area snapshot for debugging
    "navigator-kuikly-spike",  # Phase 0 runtime spike
    "navigator-v1-demo",       # Navigator demo page, Navigator is not a widget
}

CATEGORY_ORDER = ["BASIC", "FORM", "NAVIGATION", "DATA_DISPLAY", "FEEDBACK", "LAYOUT"]
CATEGORY_LABEL = {
    "en": {
        "BASIC": "Basic", "FORM": "Form", "NAVIGATION": "Navigation",
        "DATA_DISPLAY": "Data display", "FEEDBACK": "Feedback", "LAYOUT": "Layout",
    },
    "zh": {
        "BASIC": "基础", "FORM": "表单", "NAVIGATION": "导航",
        "DATA_DISPLAY": "数据展示", "FEEDBACK": "反馈", "LAYOUT": "布局",
    },
}

ENTRY_RE = re.compile(
    r'ComponentInfo\(\s*"(?P<id>[^"]+)",\s*"(?P<zh>[^"]*)",\s*"(?P<en>[^"]*)",\s*'
    r'ComponentCategory\.(?P<cat>[A-Z_]+),\s*"[^"]*"'
    r'(?:,\s*"(?P<dzh>[^"]*)")?(?:,\s*"(?P<den>[^"]*)")?\s*\)'
)

BEGIN = "<!-- component-index:begin -->"
END = "<!-- component-index:end -->"


def load_registry() -> list[dict]:
    text = REGISTRY.read_text(encoding="utf-8")
    entries = [m.groupdict() for m in ENTRY_RE.finditer(text)]
    if not entries:
        sys.exit(f"no ComponentInfo entries parsed from {REGISTRY}")
    return entries


def render(entries: list[dict], lang: str) -> str:
    comps = [e for e in entries if e["id"] not in NON_COMPONENT_IDS]
    by_cat: dict[str, list[dict]] = {c: [] for c in CATEGORY_ORDER}
    for e in comps:
        by_cat.setdefault(e["cat"], []).append(e)

    total = len(comps)
    lines: list[str] = [BEGIN]
    if lang == "en":
        lines.append(f"**{total} components** in {len([c for c in by_cat.values() if c])} categories. "
                     f"Every one of them ships a demo page in the sample app.")
        lines.append("")
        lines.append("| Category | Components |")
        lines.append("| --- | --- |")
        for cat in CATEGORY_ORDER:
            items = by_cat.get(cat) or []
            if not items:
                continue
            cell = ", ".join(f"`{e['en']}`" for e in items)
            lines.append(f"| {CATEGORY_LABEL['en'][cat]} ({len(items)}) | {cell} |")
        lines.append("")
        lines.append("<details>")
        lines.append("<summary>What each component does</summary>")
        lines.append("")
        for cat in CATEGORY_ORDER:
            items = by_cat.get(cat) or []
            if not items:
                continue
            lines.append(f"**{CATEGORY_LABEL['en'][cat]}**")
            lines.append("")
            lines.append("| Component | Purpose |")
            lines.append("| --- | --- |")
            for e in items:
                lines.append(f"| `{e['en']}` | {e['den'] or ''} |")
            lines.append("")
        lines.append("</details>")
    else:
        lines.append(f"**{total} 个组件**，分 {len([c for c in by_cat.values() if c])} 类，"
                     f"每一个在 sample 里都有对应演示页。")
        lines.append("")
        lines.append("| 分类 | 组件 |")
        lines.append("| --- | --- |")
        for cat in CATEGORY_ORDER:
            items = by_cat.get(cat) or []
            if not items:
                continue
            cell = "、".join(f"`{e['en']}`" for e in items)
            lines.append(f"| {CATEGORY_LABEL['zh'][cat]}（{len(items)}） | {cell} |")
        lines.append("")
        lines.append("<details>")
        lines.append("<summary>各组件用途</summary>")
        lines.append("")
        for cat in CATEGORY_ORDER:
            items = by_cat.get(cat) or []
            if not items:
                continue
            lines.append(f"**{CATEGORY_LABEL['zh'][cat]}**")
            lines.append("")
            lines.append("| 组件 | 中文名 | 用途 |")
            lines.append("| --- | --- | --- |")
            for e in items:
                lines.append(f"| `{e['en']}` | {e['zh']} | {e['dzh'] or ''} |")
            lines.append("")
        lines.append("</details>")
    lines.append(END)
    return "\n".join(lines)


def splice(readme: Path, block: str) -> tuple[str, str]:
    old = readme.read_text(encoding="utf-8")
    if BEGIN not in old or END not in old:
        sys.exit(f"{readme.name}: markers {BEGIN} / {END} not found")
    a = old.index(BEGIN)
    b = old.index(END) + len(END)
    return old, old[:a] + block + old[b:]


def main() -> int:
    check = "--check" in sys.argv
    entries = load_registry()
    stale = []
    for name, lang in (("README.md", "en"), ("README.zh-Hans.md", "zh")):
        readme = ROOT / name
        old, new = splice(readme, render(entries, lang))
        if old != new:
            if check:
                stale.append(name)
            else:
                readme.write_text(new, encoding="utf-8")
                print(f"updated {name}")
    if check and stale:
        print("component index is stale in: " + ", ".join(stale))
        print("run scripts/gen_component_index.py and commit the result")
        return 1
    if check:
        total = len([e for e in entries if e["id"] not in NON_COMPONENT_IDS])
        print(f"README component index is current. components={total}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
