#!/usr/bin/env python3
"""Generate GearUI's icon assets from a local Phosphor distribution.

    python3 scripts/gen_icons.py --phosphor ../phosphor-icons

Why this exists
---------------
The assets used to be produced by hand and drifted: eight icons got a PNG but
no SVG, one PNG had no constant naming it, nine constants were missing from
`Icons.all`, and — worst — the SVGs were Material Symbols while the PNGs were
Phosphor, so the two halves of the set were different icon libraries. Anything
generated is reproducible; anything hand-copied drifts.

Names are Phosphor's own. GearUI used to draw Phosphor artwork under Material
Symbols names, so `Icons.home` fetched `house` and `Icons.close` fetched `x` —
the set was described in a vocabulary it did not use.

Format
------
PNG only, and deliberately so. Kuikly hands an asset URL to the platform image
loader, and neither Android's `BitmapFactory` nor iOS's
`UIImage imageWithContentsOfFile:` decodes SVG — only the web renderer does.

Encoding is 8-bit grey + alpha (`LA`) rather than RGBA. GearUI tints every icon
through `ColorFilter.tint()`, so the colour channels are discarded at render
time and only alpha carries the shape. LA is ~13% smaller than RGBA for the
same pixels, which is free.

Size
----
96px is the default: the largest *inline* step is `IconSizes.Default.xl` = 24dp,
which needs 84px at 3.5x. `IconSizes.Display` reaches 40dp (140px at 3.5x), so
if display-size icons need to be crisp, generate at 192 and pay roughly double.
"""

import argparse
import os
import re
import sys

try:
    from PIL import Image
except ImportError:
    sys.exit("Pillow is required: pip3 install --user Pillow")

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS = os.path.join(ROOT, "gearui-kit/src/commonMain/assets/icons")
ICON_SET = os.path.join(ROOT, "scripts/icon-set.py")


def load_icon_set():
    """(phosphor name, weight) pairs, read from the icon-set module."""
    src = open(ICON_SET, encoding="utf-8").read()
    return re.findall(r"\('([a-z0-9-]+)', '(regular|fill)'\)", src)


def constant_name(phosphor_name, weight):
    """Phosphor's own naming: dashes become underscores, fill keeps its suffix."""
    base = phosphor_name.replace("-", "_")
    return f"{base}_fill" if weight == "fill" else base


def phosphor_path(base, phosphor_name, weight):
    # The fill weight suffixes its filenames; regular does not.
    suffix = "-fill" if weight == "fill" else ""
    return os.path.join(base, "PNGs", weight, f"{phosphor_name}{suffix}.png")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--phosphor", required=True, help="path to the phosphor-icons checkout")
    ap.add_argument("--size", type=int, default=96)
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    icons = load_icon_set()
    missing, written, total = [], 0, 0

    for phosphor_name, weight in sorted(icons):
        name = constant_name(phosphor_name, weight)
        src = phosphor_path(args.phosphor, phosphor_name, weight)
        if not os.path.exists(src):
            missing.append((name, phosphor_name, weight))
            continue
        if args.dry_run:
            written += 1
            continue
        img = Image.open(src).convert("RGBA").resize(
            (args.size, args.size), Image.LANCZOS
        )
        alpha = img.getchannel("A")
        flat = Image.new("L", (args.size, args.size), 0)
        out = os.path.join(ASSETS, f"{name}.png")
        Image.merge("LA", (flat, alpha)).save(out, "PNG", optimize=True)
        total += os.path.getsize(out)
        written += 1

    print(f"{written} icons at {args.size}px", end="")
    if not args.dry_run:
        print(f", {total / 1024:.0f}KB total, {total / max(written, 1):.0f}B each")
    else:
        print(" (dry run)")

    if missing:
        print(f"\n{len(missing)} not found in the Phosphor distribution:")
        for name, ph, w in missing:
            print(f"  {name} -> {w}/{ph}")
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
