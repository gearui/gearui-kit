#!/usr/bin/env python3
"""Measure the sample's performance budgets on an iOS simulator.

    python3 scripts/perf/ios_perf.py <simulator-udid> [--runs 5]

Needs idb (`pip install fb-idb`, `brew install idb-companion`) and the sample
installed on the simulator. Prints one JSON object.

What each number is — and is not:

* startup_content: process start → first frame of the home screen's content, taken
  in-app from the kernel's process start time, one cold start per run. A simulator
  runs on the Mac's CPU, so compare runs; it is not device TTI.
* theme: the in-app benchmark on the Performance page (state change → second
  frame after it), 20 flips of ~200 components.
* frames: the in-app frame-interval recorder while the script flings the
  1000-row list for five seconds. It sees the Compose frame clock, not Core
  Animation's commit, so it is a regression signal, not a Core Animation trace.
"""
import json
import re
import statistics
import subprocess
import sys
import time

APP = "com.gearui.kit.sample"


def run(*cmd, check=True):
    return subprocess.run(cmd, capture_output=True, text=True, check=check).stdout


class Sim:
    def __init__(self, udid):
        self.udid = udid

    def tree(self):
        raw = run("idb", "ui", "describe-all", "--udid", self.udid, check=False)
        try:
            data = json.loads(raw)
        except json.JSONDecodeError:
            data = [json.loads(l) for l in raw.splitlines() if l.strip()]
        rows = []

        def visit(node):
            if isinstance(node, list):
                for n in node:
                    visit(n)
            elif isinstance(node, dict):
                label = node.get("AXLabel") or node.get("AXValue") or ""
                frame = node.get("frame") or {}
                if label and frame:
                    rows.append((label, frame, node.get("type", "")))
                for n in node.get("children", []) or []:
                    visit(n)

        visit(data)
        return rows

    def find(self, pred):
        return next(((l, f, t) for l, f, t in self.tree() if pred(l, t)), None)

    def tap(self, frame):
        x = frame["x"] + frame["width"] / 2
        y = frame["y"] + frame["height"] / 2
        run("idb", "ui", "tap", "--udid", self.udid, str(int(x)), str(int(y)))

    def swipe(self, x0, y0, x1, y1, dur=0.12):
        run("idb", "ui", "swipe", "--udid", self.udid, str(x0), str(y0), str(x1), str(y1), "--duration", str(dur))

    def text(self, s):
        run("idb", "ui", "text", "--udid", self.udid, s)

    def launch(self):
        run("xcrun", "simctl", "launch", self.udid, APP, check=False)

    def terminate(self):
        run("xcrun", "simctl", "terminate", self.udid, APP, check=False)


def wait_for(sim, pred, timeout=30.0):
    end = time.monotonic() + timeout
    while time.monotonic() < end:
        hit = sim.find(pred)
        if hit:
            return hit
        time.sleep(0.2)
    return None


def startup(sim, runs):
    """Process start → first frame of the home screen's content, marked in-app.

    Each run is a cold start; the Performance page then shows the mark that process
    took. Unlike timing a launch from outside, this includes no tool latency.
    """
    samples, pages = [], []
    for _ in range(runs):
        open_performance(sim)                 # terminates, launches, navigates
        hit = wait_for(sim, lambda l, t: l.startswith("PERF startup"), 10)
        value = parse(hit[0], "content") if hit else None
        if value is not None:
            samples.append(value)
            page = parse(hit[0], "page")
            if page is not None:
                pages.append(page)
    if not samples:
        return {"runs": 0}
    out = {"runs": len(samples), "median_ms": statistics.median(samples),
           "min_ms": min(samples), "max_ms": max(samples)}
    if pages:
        out["page_created_median_ms"] = statistics.median(pages)
        out["page_to_content_median_ms"] = statistics.median([c - p for c, p in zip(samples, pages)])
    return out


def open_performance(sim):
    sim.terminate(); time.sleep(1); sim.launch()
    search = wait_for(sim, lambda l, t: l.startswith("搜索组件名称") or l.startswith("Search"))
    sim.tap(search[1]); time.sleep(1.0)
    sim.text("Performance"); time.sleep(1.5)
    entry = wait_for(sim, lambda l, t: l.startswith("Performance,") or l.startswith("性能基准,"), 10)
    sim.tap(entry[1]); time.sleep(2.5)


def parse(line, key):
    m = re.search(rf"{key}=([\d.]+)", line)
    return float(m.group(1)) if m else None


def theme(sim):
    button = wait_for(sim, lambda l, t: l.startswith("运行主题切换测试") and t == "Button", 10)
    sim.tap(button[1])
    hit = wait_for(sim, lambda l, t: l.startswith("PERF theme n=") and "…" not in l, 60)
    if not hit:
        return {"error": "no result"}
    line = hit[0]
    return {"n": int(parse(line, "n")), "median_ms": parse(line, "median"),
            "p90_ms": parse(line, "p90"), "max_ms": parse(line, "max"), "raw": line}


def frames(sim):
    # bring the recorder into view: it sits below the theme workload
    for _ in range(12):
        hit = sim.find(lambda l, t: l.startswith("录制 5 秒帧间隔") and t == "Button")
        if hit and 120 < hit[1]["y"] < 700:
            break
        sim.swipe(220, 760, 220, 260, 0.2); time.sleep(0.6)
    # A fling keeps the page moving after the swipe ends; tapping the position read
    # mid-flight misses. Let it settle, read the position again, and confirm the
    # recorder actually started.
    for _ in range(3):
        time.sleep(1.2)
        hit = sim.find(lambda l, t: l.startswith("录制 5 秒帧间隔") and t == "Button")
        if not hit:
            continue
        sim.tap(hit[1]); time.sleep(0.4)
        if sim.find(lambda l, t: l.startswith("PERF frames recording")):
            break
    else:
        return {"error": "recorder did not start"}
    list_y = hit[1]["y"] + 300
    end = time.monotonic() + 4.6
    down = True
    while time.monotonic() < end:
        if down:
            sim.swipe(220, int(list_y + 180), 220, int(list_y - 180), 0.08)
        else:
            sim.swipe(220, int(list_y - 180), 220, int(list_y + 180), 0.08)
        down = not down
    hit = wait_for(sim, lambda l, t: l.startswith("PERF frames n=") and "too few" not in l, 20)
    if not hit:
        return {"error": "no result"}
    line = hit[0]
    return {"n": int(parse(line, "n")), "period_ms": parse(line, "period"), "p95_ms": parse(line, "p95"),
            "max_ms": parse(line, "max"), "janky_pct": parse(line, "janky"), "raw": line}


def main():
    if len(sys.argv) < 2:
        print(__doc__); return 2
    udid = sys.argv[1]
    runs = int(sys.argv[sys.argv.index("--runs") + 1]) if "--runs" in sys.argv else 5
    sim = Sim(udid)
    result = {"platform": "ios-simulator", "udid": udid, "startup_content": startup(sim, runs)}
    result["theme"] = theme(sim)
    result["frames"] = frames(sim)
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return 0


if __name__ == "__main__":
    sys.exit(main())
