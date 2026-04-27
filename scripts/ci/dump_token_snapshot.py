#!/usr/bin/env python3
"""Dump GearUI Kit semantic token snapshot.

SPEC 4.1 / SPEC_CI_MAPPING token-compat-check.

Detects:
  1. Deletion or rename of fields on GearColors / GearTypography / GearShapes.
  2. Semantic drift of Themes.Light / Themes.Dark color presets
     (field value changed without bumping the schema).

ColorTokens (the `const val` palette) is already covered by
binary-compatibility-validator (const value changes are ABI-breaking),
so it is intentionally not duplicated here.
"""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
THEME_DIR = ROOT / "gearui-kit/src/commonMain/kotlin/com/gearui/theme"

DATA_CLASSES = [
    ("GearColors", THEME_DIR / "GearColors.kt"),
    ("GearTypography", THEME_DIR / "GearTypography.kt"),
    ("GearShapes", THEME_DIR / "GearShapes.kt"),
]

THEME_PRESETS_FILE = THEME_DIR / "GearColors.kt"
THEME_PRESETS = ["Light", "Dark"]

VAL_FIELD_RE = re.compile(r"^\s*val\s+(\w+)\s*:\s*\w", re.MULTILINE)
LINE_COMMENT_RE = re.compile(r"//.*$")
ASSIGN_RE = re.compile(r"^\s*(\w+)\s*=\s*(.+?),?\s*$")


def find_balanced_block(text: str, start_pat: str) -> str | None:
    """Return the body inside the parentheses of the first match of `start_pat(`."""
    m = re.search(start_pat, text)
    if not m:
        return None
    open_idx = text.find("(", m.end() - 1)
    if open_idx < 0:
        return None
    depth = 1
    i = open_idx + 1
    while i < len(text):
        c = text[i]
        if c == "(":
            depth += 1
        elif c == ")":
            depth -= 1
            if depth == 0:
                return text[open_idx + 1:i]
        i += 1
    return None


def extract_data_class_fields(source: str, class_name: str) -> list[str]:
    body = find_balanced_block(source, rf"data class\s+{class_name}\s*\(")
    if body is None:
        sys.stderr.write(f"ERROR: data class {class_name} not found\n")
        sys.exit(2)
    return sorted(VAL_FIELD_RE.findall(body))


def extract_assignments(body: str) -> list[tuple[str, str]]:
    """Pull `field = expr` pairs from a balanced block, one per logical line.

    Strips line comments. Skips blank lines and decorator lines.
    """
    pairs: list[tuple[str, str]] = []
    for raw in body.splitlines():
        line = LINE_COMMENT_RE.sub("", raw).strip()
        if not line:
            continue
        m = ASSIGN_RE.match(line)
        if not m:
            continue
        pairs.append((m.group(1), m.group(2).rstrip(",").strip()))
    return pairs


def extract_theme_preset(source: str, preset: str) -> list[tuple[str, str]]:
    """Extract `Themes.<preset> = ThemeSpec(colors = GearColors(...))` leaf assignments."""
    spec_body = find_balanced_block(
        source, rf"\bval\s+{preset}\s*=\s*ThemeSpec\s*\("
    )
    if spec_body is None:
        sys.stderr.write(f"ERROR: Themes.{preset} not found\n")
        sys.exit(2)
    colors_body = find_balanced_block(spec_body, r"colors\s*=\s*GearColors\s*\(")
    if colors_body is None:
        sys.stderr.write(f"ERROR: Themes.{preset}.colors block not found\n")
        sys.exit(2)
    pairs = extract_assignments(colors_body)
    pairs.sort(key=lambda kv: kv[0])
    return pairs


def main() -> None:
    out: list[str] = []
    out.append("# GearUI Token Schema Snapshot v1")
    out.append("# SPEC 4.1 token-compat-check baseline.")
    out.append("# Refresh with:  ./scripts/ci/dump_token_snapshot.sh > gearui-kit/api/tokens.api")
    out.append("# DO NOT edit by hand.")
    out.append("")

    for cls, path in DATA_CLASSES:
        out.append(f"[fields] {cls}")
        for f in extract_data_class_fields(path.read_text(encoding="utf-8"), cls):
            out.append(f"  {f}")
        out.append("")

    presets_source = THEME_PRESETS_FILE.read_text(encoding="utf-8")
    for preset in THEME_PRESETS:
        out.append(f"[preset] Themes.{preset}.colors")
        for field, expr in extract_theme_preset(presets_source, preset):
            out.append(f"  {field} = {expr}")
        out.append("")

    sys.stdout.write("\n".join(out).rstrip() + "\n")


if __name__ == "__main__":
    main()
