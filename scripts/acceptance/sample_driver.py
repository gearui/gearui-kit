"""Shared driver for the acceptance scripts: open a sample route and prove it rendered.

A page counts as open only when the accessibility tree shows the route's NavBar title
(`ComponentInfo.nameEn`) near the top of the screen and at least one labelled node
below it. Every platform command is checked; a failure raises `DriverError` instead of
leaving an earlier file or an unrendered frame to be counted as evidence.
"""
import datetime
import json
import os
import re
import subprocess
import time
import xml.etree.ElementTree as ET

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
PACKAGE = "com.gearui.kit.sample"
ACTIVITY = f"{PACKAGE}/com.gearui.sample.MainActivity"
REGISTRY = os.path.join(ROOT, "sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt")


class DriverError(Exception):
    """A step that did not happen: launch, dump, screenshot or the page itself."""


def routes():
    """(route id, NavBar title) for every registered sample page."""
    return re.findall(r'ComponentInfo\("([^"]+)",\s*"[^"]*",\s*"([^"]+)"', open(REGISTRY).read())


def run(cmd, what, timeout=60):
    try:
        p = subprocess.run(cmd, capture_output=True, timeout=timeout)
    except subprocess.TimeoutExpired:
        raise DriverError(f"{what}: timed out after {timeout}s")
    except FileNotFoundError:
        raise DriverError(f"{what}: {cmd[0]} not found")
    if p.returncode != 0:
        err = (p.stderr or p.stdout).decode(errors="replace").strip().splitlines()
        raise DriverError(f"{what}: exit {p.returncode}: {err[-1] if err else ''}")
    return p.stdout


def output_dir(platform, base=None):
    """A fresh directory per run; never reuses or overwrites earlier evidence."""
    sha = subprocess.run(["git", "-C", ROOT, "rev-parse", "--short=10", "HEAD"],
                         capture_output=True, text=True).stdout.strip() or "nosha"
    stamp = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
    path = base or os.path.join(ROOT, "build/acceptance", f"{platform}-{stamp}-{sha}")
    if os.path.exists(path) and os.listdir(path):
        raise DriverError(f"output directory {path} is not empty")
    os.makedirs(path, exist_ok=True)
    return path


def write_meta(out, **fields):
    dirty = subprocess.run(["git", "-C", ROOT, "status", "--porcelain", "--untracked-files=no"],
                           capture_output=True, text=True).stdout.strip()
    meta = {
        "sha": subprocess.run(["git", "-C", ROOT, "rev-parse", "HEAD"], capture_output=True, text=True).stdout.strip(),
        "worktree_dirty": bool(dirty),
        "started": datetime.datetime.now().isoformat(timespec="seconds"),
        **fields,
    }
    with open(os.path.join(out, "run.json"), "w") as f:
        json.dump(meta, f, indent=2, ensure_ascii=False)
    return meta


def ready(nodes, title, screen_h):
    """The title sits in the top fifth and something labelled is drawn below it."""
    top = [b for label, b in nodes if label == title and b[1] < screen_h * 0.2]
    if not top:
        return False
    title_bottom = top[0][3]
    return any(b[1] >= title_bottom and label != title for label, b in nodes)


class Android:
    platform = "android"

    def __init__(self, serial):
        self.serial = serial
        size = self.adb("shell", "wm", "size", what="read screen size").decode()
        self.screen_h = int(size.strip().split("x")[-1])
        dens = self.adb("shell", "wm", "density", what="read density").decode()
        self.density = int(re.search(r"(\d+)\s*$", dens.strip()).group(1)) / 160

    def adb(self, *args, what, timeout=60):
        return run(["adb", "-s", self.serial, *args], what, timeout)

    def describe(self):
        pkg = self.adb("shell", "dumpsys", "package", PACKAGE, what="read package").decode()
        version = re.search(r"versionName=(\S+)", pkg)
        model = self.adb("shell", "getprop", "ro.product.model", what="read model").decode().strip()
        release = self.adb("shell", "getprop", "ro.build.version.release", what="read OS").decode().strip()
        return {
            "platform": "android",
            "device": f"{model} (Android {release}, {self.serial})",
            "build": "debug" if "DEBUGGABLE" in pkg else "release",
            "app_version": version.group(1) if version else None,
        }

    def launch(self, route, theme, extra=()):
        out = self.adb("shell", "am", "start", "-S", "-W", "-n", ACTIVITY,
                       "--es", "route", route, "--es", "theme", theme, *extra,
                       what=f"launch {route}").decode()
        if "Status: ok" not in out or "Error" in out:
            raise DriverError(f"launch {route}: {out.strip().splitlines()[-1] if out.strip() else 'no output'}")

    def dump(self):
        """The current accessibility tree, freshly dumped (never a leftover file)."""
        remote = "/sdcard/gearui-acceptance.xml"
        last = ""
        for _ in range(3):
            self.adb("shell", "rm", "-f", remote, what="clear old dump")
            out = self.adb("shell", "uiautomator", "dump", remote, what="dump tree").decode()
            last = out.strip()
            if "dumped to" in out:
                xml = self.adb("exec-out", "cat", remote, what="read dump").decode(errors="replace")
                try:
                    return ET.fromstring(xml[xml.index("<?xml"):])
                except (ValueError, ET.ParseError) as e:
                    last = f"unreadable dump: {e}"
            time.sleep(0.5)
        raise DriverError(f"dump tree: {last or 'no output'}")

    def nodes(self, tree=None):
        tree = tree if tree is not None else self.dump()
        rows = []
        for n in tree.iter("node"):
            if n.get("package") != PACKAGE:
                continue
            label = (n.get("content-desc") or n.get("text") or "").strip()
            if label:
                rows.append((label, tuple(map(int, re.findall(r"-?\d+", n.get("bounds", "[0,0][0,0]"))))))
        return rows

    def wait_ready(self, title, timeout=15):
        end = time.monotonic() + timeout
        while time.monotonic() < end:
            tree = self.dump()
            if ready(self.nodes(tree), title, self.screen_h):
                return tree
            time.sleep(0.5)
        raise DriverError(f"page '{title}' did not render within {timeout}s")

    def screenshot(self, path):
        data = self.adb("exec-out", "screencap", "-p", what="screenshot")
        if not data.startswith(b"\x89PNG"):
            raise DriverError("screenshot: not a PNG")
        with open(path, "wb") as f:
            f.write(data)


class Ios:
    platform = "ios"

    def __init__(self, udid):
        self.udid = udid
        self.screen_h = None

    def describe(self):
        devices = json.loads(run(["xcrun", "simctl", "list", "devices", "-j"], "list simulators"))
        for runtime, items in devices["devices"].items():
            for d in items:
                if d["udid"] == self.udid:
                    if d["state"] != "Booted":
                        raise DriverError(f"simulator {self.udid} is {d['state']}")
                    app = run(["xcrun", "simctl", "get_app_container", self.udid, PACKAGE], "find app").decode().strip()
                    # Xcode names the products directory after the configuration.
                    build = "release" if "/Release-" in os.path.realpath(app) else "unknown"
                    info = os.path.join(app, "Info.plist")
                    version = subprocess.run(["/usr/libexec/PlistBuddy", "-c", "Print CFBundleShortVersionString", info],
                                             capture_output=True, text=True).stdout.strip()
                    return {"platform": "ios-simulator", "device": f"{d['name']} ({runtime.split('.')[-1]}, {self.udid})",
                            "build": build, "app_version": version or None}
        raise DriverError(f"simulator {self.udid} not found")

    def launch(self, route, theme, extra=()):
        subprocess.run(["xcrun", "simctl", "terminate", self.udid, PACKAGE], capture_output=True)
        run(["xcrun", "simctl", "launch", self.udid, PACKAGE, "-route", route, "-theme", theme, *extra],
            f"launch {route}")

    def nodes(self):
        raw = run(["idb", "ui", "describe-all", "--udid", self.udid], "describe tree").decode()
        try:
            data = json.loads(raw)
        except json.JSONDecodeError as e:
            raise DriverError(f"describe tree: unreadable output: {e}")
        rows = []
        for e in data:
            label = (e.get("AXLabel") or "").strip()
            f = e.get("frame") or {}
            if label and f:
                rows.append((label, (f["x"], f["y"], f["x"] + f["width"], f["y"] + f["height"])))
            if e.get("type") == "Application" and f:
                self.screen_h = f["height"]
        return rows

    def wait_ready(self, title, timeout=20):
        end = time.monotonic() + timeout
        while time.monotonic() < end:
            rows = self.nodes()
            if self.screen_h and ready(rows, title, self.screen_h):
                return rows
            time.sleep(0.5)
        raise DriverError(f"page '{title}' did not render within {timeout}s")

    def screenshot(self, path):
        run(["xcrun", "simctl", "io", self.udid, "screenshot", path], "screenshot")
        with open(path, "rb") as f:
            if f.read(8) != b"\x89PNG\r\n\x1a\n":
                raise DriverError("screenshot: not a PNG")


def device(platform, ident):
    if platform == "android":
        return Android(ident)
    if platform == "ios":
        return Ios(ident)
    raise DriverError(f"unsupported platform {platform}")
