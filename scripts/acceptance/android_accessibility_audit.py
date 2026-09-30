#!/usr/bin/env python3
"""Screen-reader audit of every sample page on an Android device.

    python3 scripts/acceptance/android_accessibility_audit.py <adb-serial> [out-dir]

TalkBack reads the platform accessibility node tree; uiautomator dumps that same
tree. For each route (light theme) this records, in traversal order, what TalkBack
would announce for every labelled node, and flags:

  unlabeled   a clickable, checkable or focusable node with no text or description
  small       a clickable node under 48 x 48 dp (Android's minimum touch target)

Output: <out>/<route>.txt (the readout) and one ISSUE line per finding on stdout.
This is the tree TalkBack uses, not a recording of TalkBack speaking.
"""
import os
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
SERIAL = sys.argv[1]
OUT = sys.argv[2] if len(sys.argv) > 2 else os.path.join(ROOT, "build/beta7-acceptance/android-a11y")
APP = "com.gearui.kit.sample/com.gearui.sample.MainActivity"
PACKAGE = "com.gearui.kit.sample"


def adb(*args):
    return subprocess.run(["adb", "-s", SERIAL, *args], capture_output=True, text=True).stdout


def routes():
    path = os.path.join(ROOT, "sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt")
    return re.findall(r'ComponentInfo\("([^"]+)"', open(path).read())


def bounds(node):
    x1, y1, x2, y2 = map(int, re.findall(r"\d+", node.get("bounds", "[0,0][0,0]")))
    return x1, y1, x2, y2


def main():
    os.makedirs(OUT, exist_ok=True)
    density = int(re.search(r"(\d+)\s*$", adb("shell", "wm", "density").strip()).group(1)) / 160
    min_px = 48 * density
    screen_h = int(adb("shell", "wm", "size").strip().split("x")[-1])
    total = 0
    for route in routes():
        adb("shell", "am", "start", "-S", "-W", "-n", APP, "--es", "route", route, "--es", "theme", "light")
        time.sleep(1.5)
        adb("shell", "uiautomator", "dump", "/sdcard/a11y.xml")
        xml = adb("exec-out", "cat", "/sdcard/a11y.xml")
        try:
            tree = ET.fromstring(xml[xml.index("<?xml"):])
        except (ET.ParseError, ValueError):
            print(f"ISSUE|{route}|dump|could not read the node tree", flush=True)
            total += 1
            continue
        lines = []
        for node in tree.iter("node"):
            if node.get("package") != PACKAGE:
                continue
            label = (node.get("content-desc") or node.get("text") or "").strip()
            clickable = node.get("clickable") == "true"
            interactive = clickable or node.get("checkable") == "true"
            if label:
                lines.append(label)
            x1, y1, x2, y2 = bounds(node)
            visible = x2 > x1 and y2 > y1
            if interactive and not label and visible:
                print(f"ISSUE|{route}|unlabeled|{node.get('class')} at {node.get('bounds')}", flush=True)
                total += 1
            # A node cut by the screen edge is not its real size; skip it.
            cut = y1 <= 0 or y2 >= screen_h
            if clickable and label and visible and not cut and (x2 - x1 < min_px or y2 - y1 < min_px):
                print(f"ISSUE|{route}|small|{label[:40]}|{(x2 - x1) / density:.0f}x{(y2 - y1) / density:.0f}dp", flush=True)
                total += 1
        with open(os.path.join(OUT, f"{route}.txt"), "w") as f:
            f.write("\n".join(lines) + "\n")
        print(f"PAGE|{route}|{len(lines)} announced", flush=True)
    print(f"TOTAL|{total}")


if __name__ == "__main__":
    main()
