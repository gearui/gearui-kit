#!/usr/bin/env python3
"""Turn Phosphor's flat SVGs into GearUI vector icons, as Kotlin code.

    python3 scripts/gen_vector_icons.py --phosphor ../phosphor-icons --out <file.kt> \
        --object Icons --names x-circle,heart,...

Why code, not files
-------------------
Neither Android's nor iOS's image loader decodes SVG, and an asset file is copied
into every app whether it is used or not. Path data compiled into Kotlin is drawn by
GearUI itself on a Canvas, at any size, and an icon nothing references is removed by
the compiler (Kotlin/Native and Kotlin/JS always; Android when R8 runs).

So each icon is a getter, never a stored property: an object's stored properties are
initialised together, and using one icon would keep them all.

Format
------
Phosphor's "SVGs Flat" set: one filled path per icon in a 256x256 viewport, holes cut by
contours of the opposite direction (non-zero winding). Every command is rewritten to
absolute M, L, Q, C and Z; elliptical arcs become cubic Béziers here, so the runtime
parser handles five letters. Coordinates keep one decimal (1/2560 of the icon).
"""

import argparse
import math
import os
import re
import sys

NUM = r"[-+]?(?:\d+\.?\d*|\.\d+)(?:[eE][-+]?\d+)?"
TOKEN = re.compile(rf"([MmLlHhVvCcSsQqTtAaZz])|({NUM})")


def tokens(d):
    for cmd, num in TOKEN.findall(d):
        yield cmd if cmd else float(num)


def arc_to_cubics(x1, y1, rx, ry, phi, large, sweep, x2, y2):
    """SVG endpoint arc to cubic Bézier segments (SVG 1.1 implementation notes, F.6)."""
    if (x1, y1) == (x2, y2):
        return []
    if rx == 0 or ry == 0:
        return [(x1, y1, x2, y2, x2, y2)]
    rx, ry = abs(rx), abs(ry)
    cp, sp = math.cos(math.radians(phi)), math.sin(math.radians(phi))
    dx, dy = (x1 - x2) / 2, (y1 - y2) / 2
    x1p, y1p = cp * dx + sp * dy, -sp * dx + cp * dy
    lam = (x1p ** 2) / (rx ** 2) + (y1p ** 2) / (ry ** 2)
    if lam > 1:
        rx, ry = rx * math.sqrt(lam), ry * math.sqrt(lam)
    num = rx * rx * ry * ry - rx * rx * y1p * y1p - ry * ry * x1p * x1p
    den = rx * rx * y1p * y1p + ry * ry * x1p * x1p
    coef = math.sqrt(max(0.0, num / den)) if den else 0.0
    if large == sweep:
        coef = -coef
    cxp, cyp = coef * rx * y1p / ry, -coef * ry * x1p / rx
    cx = cp * cxp - sp * cyp + (x1 + x2) / 2
    cy = sp * cxp + cp * cyp + (y1 + y2) / 2

    def angle(ux, uy, vx, vy):
        a = math.atan2(ux * vy - uy * vx, ux * vx + uy * vy)
        return a

    t1 = angle(1, 0, (x1p - cxp) / rx, (y1p - cyp) / ry)
    dt = angle((x1p - cxp) / rx, (y1p - cyp) / ry, (-x1p - cxp) / rx, (-y1p - cyp) / ry)
    if not sweep and dt > 0:
        dt -= 2 * math.pi
    elif sweep and dt < 0:
        dt += 2 * math.pi
    segments = max(1, int(math.ceil(abs(dt) / (math.pi / 2) - 1e-9)))
    delta = dt / segments
    k = 4 / 3 * math.tan(delta / 4)
    out = []
    t = t1
    for _ in range(segments):
        c1, s1 = math.cos(t), math.sin(t)
        c2, s2 = math.cos(t + delta), math.sin(t + delta)
        p1 = (c1 - k * s1, s1 + k * c1)
        p2 = (c2 + k * s2, s2 - k * c2)
        p3 = (c2, s2)

        def tr(p):
            x, y = p[0] * rx, p[1] * ry
            return cp * x - sp * y + cx, sp * x + cp * y + cy

        out.append((*tr(p1), *tr(p2), *tr(p3)))
        t += delta
    return out


def normalise(d):
    """Path data to a list of absolute (cmd, numbers) in M, L, Q, C, Z."""
    it = list(tokens(d))
    i, out = 0, []
    cmd = None
    cx = cy = sx = sy = 0.0
    last_c = last_q = None

    def nums(n):
        nonlocal i
        vals = it[i:i + n]
        i += n
        return vals

    while i < len(it):
        if isinstance(it[i], str):
            cmd = it[i]
            i += 1
            if cmd in "Zz":
                out.append(("Z", []))
                cx, cy = sx, sy
                last_c = last_q = None
                continue
        rel = cmd.islower()
        c = cmd.upper()
        if c == "M":
            x, y = nums(2)
            if rel:
                x, y = cx + x, cy + y
            out.append(("M", [x, y]))
            cx, cy, sx, sy = x, y, x, y
            cmd = "l" if rel else "L"   # subsequent pairs are line-tos
            last_c = last_q = None
        elif c == "L":
            x, y = nums(2)
            if rel:
                x, y = cx + x, cy + y
            out.append(("L", [x, y]))
            cx, cy = x, y
            last_c = last_q = None
        elif c == "H":
            (x,) = nums(1)
            x = cx + x if rel else x
            out.append(("L", [x, cy]))
            cx = x
            last_c = last_q = None
        elif c == "V":
            (y,) = nums(1)
            y = cy + y if rel else y
            out.append(("L", [cx, y]))
            cy = y
            last_c = last_q = None
        elif c in "CS":
            if c == "C":
                x1, y1, x2, y2, x, y = nums(6)
                if rel:
                    x1, y1, x2, y2, x, y = cx + x1, cy + y1, cx + x2, cy + y2, cx + x, cy + y
            else:
                x2, y2, x, y = nums(4)
                if rel:
                    x2, y2, x, y = cx + x2, cy + y2, cx + x, cy + y
                x1, y1 = (2 * cx - last_c[0], 2 * cy - last_c[1]) if last_c else (cx, cy)
            out.append(("C", [x1, y1, x2, y2, x, y]))
            last_c, last_q = (x2, y2), None
            cx, cy = x, y
        elif c in "QT":
            if c == "Q":
                x1, y1, x, y = nums(4)
                if rel:
                    x1, y1, x, y = cx + x1, cy + y1, cx + x, cy + y
            else:
                x, y = nums(2)
                if rel:
                    x, y = cx + x, cy + y
                x1, y1 = (2 * cx - last_q[0], 2 * cy - last_q[1]) if last_q else (cx, cy)
            out.append(("Q", [x1, y1, x, y]))
            last_q, last_c = (x1, y1), None
            cx, cy = x, y
        elif c == "A":
            rx, ry, phi, large, sweep, x, y = nums(7)
            if rel:
                x, y = cx + x, cy + y
            for seg in arc_to_cubics(cx, cy, rx, ry, phi, int(large), int(sweep), x, y):
                out.append(("C", list(seg)))
            cx, cy = x, y
            last_c = last_q = None
        else:
            raise ValueError(f"unsupported command {cmd}")
    return out


def fmt(v):
    s = f"{round(v, 1):.1f}".rstrip("0").rstrip(".")
    return "0" if s in ("-0", "") else s


def encode(d):
    parts = []
    for cmd, vals in normalise(d):
        parts.append(cmd + " ".join(fmt(v) for v in vals))
    return "".join(parts)


def svg_path(phosphor, weight, name):
    suffix = "-fill" if weight == "fill" else ""
    path = os.path.join(phosphor, "SVGs Flat", weight, f"{name}{suffix}.svg")
    if not os.path.exists(path):
        return None
    paths = re.findall(r' d="([^"]*)"', open(path, encoding="utf-8").read())
    return " ".join(paths)


def camel(name):
    head, *rest = name.split("-")
    ident = head + "".join(p[:1].upper() + p[1:] for p in rest)
    return f"`{ident}`" if ident[:1].isdigit() or ident in KOTLIN_KEYWORDS else ident


KOTLIN_KEYWORDS = {
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in",
    "interface", "is", "null", "object", "package", "return", "super", "this", "throw",
    "true", "try", "typealias", "typeof", "val", "var", "when", "while",
}


def icon_block(phosphor, receiver, name):
    regular = svg_path(phosphor, "regular", name)
    if regular is None:
        return None
    fill = svg_path(phosphor, "fill", name)
    fill_arg = f'"{encode(fill)}"' if fill else "null"
    return [
        f"/** Phosphor `{name}`. */",
        f"val {receiver}.{camel(name)}: IconSource",
        f'    get() = VectorIcon("{name}", "{encode(regular)}", {fill_arg})',
        "",
    ]


def header(package):
    lines = [
        "// Generated by scripts/gen_vector_icons.py from Phosphor (MIT). Do not edit.",
        f"package {package}",
        "",
    ]
    if package != "com.gearui.components.icon":
        lines += ["import com.gearui.components.icon.IconSource", "import com.gearui.components.icon.VectorIcon", ""]
    return lines


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--phosphor", required=True)
    ap.add_argument("--package", default="com.gearui.components.icon")
    ap.add_argument("--receiver", default="Icons", help="the object the getters extend")
    ap.add_argument("--names", help="comma-separated Phosphor names; every icon when omitted")
    ap.add_argument("--out", help="one output file (with --names)")
    ap.add_argument("--out-dir", help="one file per initial letter (the whole set)")
    ap.add_argument("--list-out", help="also write a Kotlin list of every icon, for a gallery")
    ap.add_argument("--list-package", default="com.gearui.sample.examples.icon")
    ap.add_argument("--check", action="store_true", help="fail if the output is not up to date")
    args = ap.parse_args()

    if args.names:
        names = [n.strip() for n in args.names.split(",") if n.strip()]
    else:
        names = sorted(f[:-4] for f in os.listdir(os.path.join(args.phosphor, "SVGs Flat", "regular")) if f.endswith(".svg"))

    files = {}
    missing = []
    for name in names:
        block = icon_block(args.phosphor, args.receiver, name)
        if block is None:
            missing.append(name)
            continue
        key = args.out if args.out else os.path.join(args.out_dir, f"Icons{name[0].upper()}.generated.kt")
        files.setdefault(key, header(args.package)).extend(block)

    if args.list_out:
        lines = [
            "// Generated by scripts/gen_vector_icons.py. Do not edit.",
            f"package {args.list_package}",
            "",
            "import com.gearui.components.icon.IconSource",
            "import com.gearui.components.icon.Icons",
            "import com.gearui.components.icon.*",
            "",
            "/** Every icon, for the gallery. Kept out of the kit: a list of all icons keeps them all in an app. */",
            "val allIcons: List<Pair<String, IconSource>> = listOf(",
        ]
        lines += [f'    "{camel(n).strip("`")}" to Icons.{camel(n)},' for n in names if n not in missing]
        lines += [")", ""]
        files[args.list_out] = lines

    stale = []
    for path, lines in files.items():
        text = "\n".join(lines)
        if args.check:
            if not os.path.exists(path) or open(path, encoding="utf-8").read() != text:
                stale.append(path)
            continue
        os.makedirs(os.path.dirname(os.path.abspath(path)), exist_ok=True)
        open(path, "w", encoding="utf-8").write(text)
    if args.out_dir and not args.check:
        expected = {os.path.basename(p) for p in files if os.path.dirname(p) == args.out_dir}
        for f in os.listdir(args.out_dir):
            if f.endswith(".generated.kt") and f not in expected:
                os.remove(os.path.join(args.out_dir, f))
    size = sum(len("\n".join(l)) for p, l in files.items() if p != args.list_out)
    print(f"{len(names) - len(missing)} icons, {size / 1024:.0f}KB of Kotlin in {len(files) - bool(args.list_out)} files")
    if missing:
        print("not found: " + ", ".join(missing))
        return 1
    if stale:
        print("stale (rerun without --check): " + ", ".join(stale))
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
