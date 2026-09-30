#!/usr/bin/env python3
"""Screenshot sample pages once each has rendered, light and dark.

    python3 scripts/acceptance/capture_sample.py android|ios <serial-or-udid> [--build debug|release]
        [--out DIR] [--routes a,b] [--themes light,dark] [--scroll]

Each page is shot only after sample_driver proves the target route rendered. With
--scroll, pages that scroll are also shot at the end of their content. A page that
fails is listed and the run exits non-zero; nothing is counted from an earlier run.
Images are evidence for review, not automatic acceptance.
"""
import argparse
import json
import os
import sys
import time

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from sample_driver import DriverError, device, output_dir, routes, run, write_meta  # noqa: E402


def scroll_to_end(dev):
    """Fling until the tree stops changing; returns the number of flings."""
    before = None
    for n in range(12):
        rows = dev.nodes()
        labels = [l for l, _ in rows]
        if labels == before:
            return n
        before = labels
        if dev.platform == "android":
            dev.adb("shell", "input", "swipe", "700", "2400", "700", "900", "250", what="scroll")
        else:
            run(["idb", "ui", "swipe", "--udid", dev.udid, "200", "700", "200", "250", "--duration", "0.25"], "scroll")
        time.sleep(1.0)
    return 12


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("platform", choices=["android", "ios"])
    ap.add_argument("device")
    ap.add_argument("--out")
    ap.add_argument("--routes", help="comma-separated subset")
    ap.add_argument("--themes", default="light,dark")
    ap.add_argument("--scroll", action="store_true")
    ap.add_argument("--build", choices=["debug", "release"],
                    help="build type of the installed app, when it cannot be read from the device")
    a = ap.parse_args()

    try:
        dev = device(a.platform, a.device)
        info = dev.describe()
        if a.build and info["build"] not in ("unknown", a.build):
            raise DriverError(f"--build {a.build} but the installed app is a {info['build']} build")
        if info["build"] == "unknown" and a.build:
            info["build"] = f"{a.build} (declared)"
    except DriverError as e:
        print(f"FAIL|setup|{e}", flush=True)
        return 2
    wanted = set(a.routes.split(",")) if a.routes else None
    todo = [(r, t) for r, t in routes() if wanted is None or r in wanted]
    if wanted:
        # Unknown ids are tried with their own name as title, so a typo fails loudly.
        todo += [(r, r) for r in sorted(wanted - {r for r, _ in todo})]
    themes = a.themes.split(",")
    out = output_dir(a.platform, a.out)
    write_meta(out, **info, pages_expected=len(todo) * len(themes))

    done, failed = 0, []
    for route, title in todo:
        for theme in themes:
            try:
                dev.launch(route, theme)
                dev.wait_ready(title)
                time.sleep(0.8)  # let entry animations and the status bar settle
                dev.screenshot(os.path.join(out, f"{route}-{theme}.png"))
                if a.scroll and scroll_to_end(dev) > 1:
                    dev.screenshot(os.path.join(out, f"{route}-{theme}-end.png"))
                done += 1
                print(f"OK|{route}|{theme}", flush=True)
            except DriverError as e:
                failed.append({"route": route, "theme": theme, "error": str(e)})
                print(f"FAIL|{route}|{theme}|{e}", flush=True)

    summary = {"pages_expected": len(todo) * len(themes), "pages_done": done, "failed": failed}
    with open(os.path.join(out, "result.json"), "w") as f:
        json.dump(summary, f, indent=2, ensure_ascii=False)
    print(f"DONE|{done}/{summary['pages_expected']}|{out}", flush=True)
    return 0 if not failed else 1


if __name__ == "__main__":
    sys.exit(main())
