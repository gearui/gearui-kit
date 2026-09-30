#!/usr/bin/env python3
"""Accessibility-tree audit of every sample page on an Android device.

    python3 scripts/acceptance/android_accessibility_audit.py <adb-serial> [--out DIR] [--routes a,b] [--themes light,dark]

TalkBack reads the platform accessibility node tree; uiautomator dumps that same
tree. Each route is opened through sample_driver (launch checked, page proven
rendered, tree freshly dumped) and this records, in traversal order, what each
labelled node would announce, and flags:

  unlabeled   a clickable or checkable node with no text or description
  small       a clickable node under 48 x 48 dp (Android's minimum touch target)
  overlap     two clickable nodes whose touch areas intersect (an enlarged target
              stealing its neighbour's taps)

This is the tree TalkBack uses, not TalkBack speaking: focus order as spoken, the
effect of actions and state announcements are checked by hand with TalkBack on.
Exit status: 0 no findings, 1 findings, 2 a page could not be audited.
"""
import argparse
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from sample_driver import PACKAGE, DriverError, device, output_dir, routes, write_meta  # noqa: E402
import re  # noqa: E402


def bounds(node):
    return tuple(map(int, re.findall(r"-?\d+", node.get("bounds", "[0,0][0,0]"))))


def audit(tree, dev):
    findings, lines, targets = [], [], []
    min_px = 48 * dev.density
    for node in tree.iter("node"):
        if node.get("package") != PACKAGE:
            continue
        label = (node.get("content-desc") or node.get("text") or "").strip()
        clickable = node.get("clickable") == "true"
        checkable = node.get("checkable") == "true"
        x1, y1, x2, y2 = bounds(node)
        visible = x2 > x1 and y2 > y1
        state = []
        if checkable:
            state.append("checked" if node.get("checked") == "true" else "not checked")
        if node.get("enabled") == "false":
            state.append("disabled")
        if label or state:
            lines.append(" · ".join([label or "(no label)", node.get("class", "").split(".")[-1], *state]))
        if (clickable or checkable) and not label and visible:
            findings.append(("unlabeled", f"{node.get('class')} at {node.get('bounds')}"))
        # A node cut by the screen edge is not its real size; skip it.
        cut = y1 <= 0 or y2 >= dev.screen_h
        if clickable and visible and not cut:
            if x2 - x1 < min_px or y2 - y1 < min_px:
                findings.append(("small", f"{label[:40]}|{(x2 - x1) / dev.density:.0f}x{(y2 - y1) / dev.density:.0f}dp"))
            targets.append((label, (x1, y1, x2, y2)))
    for i, (la, a) in enumerate(targets):
        for lb, b in targets[i + 1:]:
            nested = (a[0] <= b[0] and a[1] <= b[1] and a[2] >= b[2] and a[3] >= b[3]) or \
                     (b[0] <= a[0] and b[1] <= a[1] and b[2] >= a[2] and b[3] >= a[3])
            if not nested and a[0] < b[2] and b[0] < a[2] and a[1] < b[3] and b[1] < a[3]:
                findings.append(("overlap", f"{la[:30]} ∩ {lb[:30]}"))
    return findings, lines


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("serial")
    ap.add_argument("--out")
    ap.add_argument("--routes")
    ap.add_argument("--themes", default="light")
    a = ap.parse_args()
    try:
        dev = device("android", a.serial)
        info = dev.describe()
    except DriverError as e:
        print(f"FAIL|setup|{e}", flush=True)
        return 2
    wanted = set(a.routes.split(",")) if a.routes else None
    todo = [(r, t) for r, t in routes() if wanted is None or r in wanted]
    if wanted:
        todo += [(r, r) for r in sorted(wanted - {r for r, _ in todo})]
    themes = a.themes.split(",")
    out = output_dir("android-a11y", a.out)
    write_meta(out, **info, pages_expected=len(todo) * len(themes))
    total, done, failed = 0, 0, []
    for route, title in todo:
        for theme in themes:
            try:
                dev.launch(route, theme)
                tree = dev.wait_ready(title)
            except DriverError as e:
                failed.append({"route": route, "theme": theme, "error": str(e)})
                print(f"FAIL|{route}|{theme}|{e}", flush=True)
                continue
            findings, lines = audit(tree, dev)
            with open(os.path.join(out, f"{route}-{theme}.txt"), "w") as f:
                f.write("\n".join(lines) + "\n")
            for kind, detail in findings:
                print(f"ISSUE|{route}|{theme}|{kind}|{detail}", flush=True)
            total += len(findings)
            done += 1
            print(f"PAGE|{route}|{theme}|{len(lines)} nodes|{len(findings)} findings", flush=True)
    with open(os.path.join(out, "result.json"), "w") as f:
        json.dump({"pages_expected": len(todo) * len(themes), "pages_done": done,
                   "findings": total, "failed": failed}, f, indent=2, ensure_ascii=False)
    print(f"DONE|{done}/{len(todo) * len(themes)}|findings={total}|{out}", flush=True)
    return 2 if failed else (1 if total else 0)


if __name__ == "__main__":
    sys.exit(main())
