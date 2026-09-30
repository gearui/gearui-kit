#!/usr/bin/env python3
"""Interaction acceptance for form, picker and overlay families on a device (gate D2/D1).

    python3 scripts/acceptance/interactions.py android|ios <serial-or-udid> [--only form,picker,…]

Each check drives a sample page through sample_driver and asserts what the user would
see: errors after an empty submit, a picker's cancel leaving the value and its confirm
writing it, an overlay closing on BACK (Android) or its cancel button, a mask that must
not close a confirm dialog, the keyboard inside a dialog. One line per check; exit
status 1 if any fails. What this does not cover — TalkBack/VoiceOver speech, where focus
lands after an overlay closes — is checked by hand (see docs/BETA7_ACCEPTANCE.md).
"""
import argparse
import os
import sys
import time

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from sample_driver import DriverError, device, run  # noqa: E402


class Page:
    def __init__(self, dev):
        self.d = dev
        self.scale = dev.density if dev.platform == "android" else 1.0

    # --- reading -----------------------------------------------------------
    def labels(self):
        return self.d.nodes()

    def find(self, text, exact=False):
        """A node labelled [text]; unless [exact], a "text, description" node (a Cell or
        a choice announcing its state) is preferred over a bare heading with the same text."""
        rows = self.labels()
        if not exact:
            for label, b in rows:
                if label.startswith(text + ","):
                    return b
        for label, b in rows:
            if label == text:
                return b
        return None

    def shown(self, text, exact=False):
        return self.find(text, exact) is not None

    def any_startswith(self, prefix):
        return any(label.startswith(prefix) for label, _ in self.labels())

    def wait(self, predicate, timeout=3.0):
        end = time.monotonic() + timeout
        while time.monotonic() < end:
            if predicate():
                return True
            time.sleep(0.3)
        return predicate()

    # --- acting ------------------------------------------------------------
    def size(self):
        if self.d.platform == "android":
            w, h = self.d.adb("shell", "wm", "size", what="size").decode().split()[-1].split("x")
            return int(w), int(h)
        self.d.nodes()
        return 402, self.d.screen_h or 874

    def tap_xy(self, x, y, settle=0.9):
        if self.d.platform == "android":
            self.d.adb("shell", "input", "tap", str(int(x)), str(int(y)), what="tap")
        else:
            run(["idb", "ui", "tap", "--udid", self.d.udid, str(int(x)), str(int(y))], "tap")
        time.sleep(settle)

    def tap(self, text, exact=False):
        b = self.find(text, exact) or self.scroll_to(text, exact)
        if b is None:
            raise DriverError(f"'{text}' not on screen")
        # A row just under the navigation bar may be partly covered by it; bring it
        # down first. (Not at the bottom edge: that is where sheets put their buttons,
        # and a swipe there could drag a sheet closed.)
        w, h = self.size()
        if (b[1] + b[3]) / 2 < h * 0.2:
            self.nudge(down=True)
            b = self.find(text, exact) or b
        self.tap_xy((b[0] + b[2]) / 2, (b[1] + b[3]) / 2)

    def center(self, text, exact=False):
        """Bring a page element (not one on an overlay) toward the middle of the screen."""
        w, h = self.size()
        for _ in range(3):
            b = self.find(text, exact)
            if b is None or h * 0.25 <= (b[1] + b[3]) / 2 <= h * 0.75:
                return
            self.nudge(down=(b[1] + b[3]) / 2 < h * 0.25)

    def nudge(self, down):
        """A short scroll of about a quarter screen."""
        w, h = self.size()
        a, b = (h // 2, h * 3 // 4) if down else (h * 3 // 4, h // 2)
        if self.d.platform == "android":
            self.d.adb("shell", "input", "swipe", str(w // 2), str(a), str(w // 2), str(b), "400", what="scroll")
        else:
            run(["idb", "ui", "swipe", "--udid", self.d.udid, str(w // 2), str(a), str(w // 2), str(b), "--duration", "0.5"], "scroll")
        time.sleep(0.9 if self.d.platform == "android" else 1.6)

    def scroll_to(self, text, exact=False):
        w, h = self.size()
        for _ in range(6):
            if self.d.platform == "android":
                self.d.adb("shell", "input", "swipe", str(w // 2), str(h * 3 // 4), str(w // 2), str(h // 3), "300", what="scroll")
            else:
                run(["idb", "ui", "swipe", "--udid", self.d.udid, str(w // 2), str(h * 3 // 4), str(w // 2), str(h // 3), "--duration", "0.3"], "scroll")
            time.sleep(0.9 if self.d.platform == "android" else 1.6)
            b = self.find(text, exact)
            if b is not None:
                return b
        return None

    def swipe(self, down):
        w, h = self.size()
        a, b = (h // 3, h * 3 // 4) if down else (h * 3 // 4, h // 3)
        if self.d.platform == "android":
            self.d.adb("shell", "input", "swipe", str(w // 2), str(a), str(w // 2), str(b), "300", what="scroll")
        else:
            run(["idb", "ui", "swipe", "--udid", self.d.udid, str(w // 2), str(a), str(w // 2), str(b), "--duration", "0.3"], "scroll")
        # iOS decelerates for a while; a tap before it stops only stops the scroll.
        time.sleep(0.9 if self.d.platform == "android" else 1.6)

    def seek(self, text, exact=True):
        """Whether [text] is anywhere on the page: scrolls to the top, then down."""
        if self.find(text, exact):
            return True
        for _ in range(5):
            self.swipe(down=True)
        for _ in range(8):
            if self.find(text, exact):
                return True
            self.swipe(down=False)
        return self.find(text, exact) is not None

    def tap_mask(self):
        """A point on the dimmed page above an open overlay (below the status bar)."""
        w, h = self.size()
        self.tap_xy(w * 0.5, h * 0.12)

    def back(self):
        self.d.adb("shell", "input", "keyevent", "4", what="back")
        time.sleep(0.9)

    def type(self, text):
        if self.d.platform == "android":
            self.d.adb("shell", "input", "text", text, what="type")
        else:
            run(["idb", "ui", "text", "--udid", self.d.udid, text], "type")
        time.sleep(0.8)

    def keyboard_up(self):
        if self.d.platform == "android":
            return "mInputShown=true" in self.d.adb("shell", "dumpsys", "input_method", what="ime").decode()
        return None  # the simulator's hardware keyboard hides the software one

    def open(self, route, title):
        self.d.launch(route, "light")
        self.d.wait_ready(title)
        time.sleep(0.6)


RESULTS = []


def check(family, name, ok, detail=""):
    RESULTS.append(ok)
    print(f"{'PASS' if ok else 'FAIL'}|{family}|{name}{'|' + detail if detail and not ok else ''}", flush=True)


def form(p):
    p.open("form", "Form")
    p.tap("提交", exact=True)
    check("form", "empty submit shows the errors", p.seek("密码须为 8 位英文字母") and p.seek("请选择性别"))
    p.seek("男", exact=False)
    p.center("男")
    p.tap("男")
    p.tap("提交", exact=True)
    check("form", "choosing a value clears its error", not p.seek("请选择性别") and p.seek("密码须为 8 位英文字母"))
    p.tap("重置", exact=True)
    check("form", "reset clears the errors (enabled)", not p.seek("密码须为 8 位英文字母"))
    p.tap("提交", exact=True)
    p.seek("密码须为 8 位英文字母")
    p.seek("禁用态")
    p.center("禁用态")
    p.tap("禁用态")
    check("form", "the switch disables the form", p.wait(lambda: p.shown("禁用态, 已开启", True)))
    # Reset would clear the errors; on a disabled form it must do nothing.
    p.tap("重置", exact=True)
    check("form", "disabled form ignores its buttons", p.seek("密码须为 8 位英文字母"))


def picker(p):
    p.open("picker", "Picker")
    p.tap("单列 · 地区")
    check("picker", "opens", p.wait(lambda: p.shown("选择地区", True)))
    p.tap("取消", exact=True)
    check("picker", "cancel closes without writing", p.wait(lambda: not p.shown("选择地区", True)) and p.shown("单列 · 地区, 请选择", True))
    p.tap("单列 · 地区")
    p.wait(lambda: p.shown("选择地区", True))
    p.tap("确定", exact=True)
    check("picker", "confirm writes the value", p.wait(lambda: p.shown("单列 · 地区, 广州市", True) and not p.shown("选择地区", True)))


def datepicker(p):
    p.open("datepicker", "DatePicker")
    p.tap("请选择日期", exact=True)
    check("datepicker", "opens", p.wait(lambda: p.shown("选择日期", True)))
    p.tap("取消", exact=True)
    check("datepicker", "cancel leaves it empty", p.wait(lambda: p.shown("已选择: 未选择", True)))
    p.tap("请选择日期", exact=True)
    p.wait(lambda: p.shown("选择日期", True))
    p.tap("确定", exact=True)
    check("datepicker", "confirm writes a date", p.wait(lambda: p.any_startswith("已选择: 20")))


def cascader(p):
    p.open("cascader", "Cascader")
    p.tap("请选择所在地区", exact=True)
    for step in ("浙江省", "杭州市", "西湖区"):
        p.wait(lambda: p.shown(step))
        p.tap(step)
    check("cascader", "leaf closes and writes the path", p.wait(lambda: p.shown("value：330000 / 330100 / 330106", True)))


def dialog(p):
    p.open("dialog", "Dialog")
    p.tap("带标题对话框")
    check("dialog", "opens", p.wait(lambda: p.shown("对话框标题", True)))
    p.tap_mask()
    check("dialog", "mask does not close a confirm dialog", p.shown("对话框标题", True))
    if p.d.platform == "android":
        p.back()
        check("dialog", "BACK closes the dialog, not the page", p.wait(lambda: not p.shown("对话框标题", True) and p.shown("带标题对话框")))
    else:
        p.tap("取消", exact=True)
        check("dialog", "cancel closes it", p.wait(lambda: p.shown("点击了取消", True)))
    p.tap("表单对话框")
    p.wait(lambda: p.shown("新名称"))
    p.tap("新名称")
    up = p.keyboard_up()
    if up is not None:
        check("dialog", "keyboard comes up for the dialog's input", up)
    p.type("abc")
    p.tap("确定", exact=True)
    check("dialog", "confirm writes and closes", p.wait(lambda: p.shown("已重命名", True) and not p.shown("重命名", True)))
    if up is not None:
        check("dialog", "keyboard goes when the dialog closes", p.wait(lambda: not p.keyboard_up()))


def actionsheet(p):
    p.open("actionsheet", "ActionSheet")
    p.tap("常规列表")
    check("actionsheet", "opens", p.wait(lambda: p.shown("选项一", True)))
    if p.d.platform == "android":
        p.back()
        check("actionsheet", "BACK closes the sheet, not the page", p.wait(lambda: not p.shown("选项一", True)) and p.shown("常规列表", True))
        p.tap("常规列表")
        p.wait(lambda: p.shown("选项一", True))
    p.tap("取消", exact=True)
    check("actionsheet", "cancel closes it", p.wait(lambda: not p.shown("选项一", True)))


def bottomsheet(p):
    p.open("bottomsheet", "BottomSheet")
    p.tap("基础面板")
    p.wait(lambda: p.shown("选项二", True))
    p.tap("选项二", exact=True)
    check("bottomsheet", "choosing writes and closes", p.wait(lambda: p.shown("选择了: 选项二", True)))
    p.tap("基础面板")
    p.wait(lambda: p.shown("选项二", True))
    p.tap_mask()
    check("bottomsheet", "mask closes it", p.wait(lambda: not p.shown("选项二", True)))


def popup(p):
    p.open("popup", "Popup")
    p.tap("居中弹出")
    check("popup", "opens", p.wait(lambda: p.shown("居中弹出内容", True)))
    p.tap_mask()
    check("popup", "outside tap closes it", p.wait(lambda: not p.shown("居中弹出内容", True)))
    p.tap("禁止点击外部关闭")
    p.wait(lambda: p.shown("点击外部不会关闭", True))
    time.sleep(0.6)  # past the entry animation
    p.tap_mask()
    check("popup", "a popup that forbids it stays on outside tap", p.shown("点击外部不会关闭", True))
    if p.d.platform == "android":
        p.back()
        check("popup", "BACK still closes it", p.wait(lambda: not p.shown("点击外部不会关闭", True) and p.shown("居中弹出")))


def dropdowns(p):
    p.open("select", "Select")
    p.tap("请选择城市", exact=True)
    check("dropdowns", "select opens", p.wait(lambda: p.shown("广州")))
    p.tap("请选择城市", exact=True)
    check("dropdowns", "tapping the trigger again closes it", p.wait(lambda: not p.shown("广州")))
    p.tap("请选择城市", exact=True)
    p.wait(lambda: p.shown("广州"))
    p.tap_mask()
    check("dropdowns", "outside tap closes it", p.wait(lambda: not p.shown("广州")))
    p.tap("请选择城市", exact=True)
    p.wait(lambda: p.shown("广州"))
    p.tap("广州")
    check("dropdowns", "choosing writes the value", p.wait(lambda: p.shown("广州", True) and not p.shown("广州, 未选择", True)))

    p.open("combo-box", "ComboBox")
    field = p.find("输入城市名", True)
    fx, fy = (field[0] + field[2]) / 2, (field[1] + field[3]) / 2
    p.tap_xy(fx, fy)
    check("dropdowns", "combo opens on focus", p.wait(lambda: p.shown("上海")))
    p.tap_xy(fx, fy)
    check("dropdowns", "tapping the focused field keeps it open", p.wait(lambda: p.shown("上海")))
    up = p.keyboard_up()
    if up is not None:
        check("dropdowns", "the field keeps the keyboard", up)
    p.tap("深圳")
    check("dropdowns", "combo choice writes the value", p.wait(lambda: p.shown("已选择:深圳", True)))
    cur = p.find("输入币种或代码", True)
    p.tap_xy((cur[0] + cur[2]) / 2, (cur[1] + cur[3]) / 2)
    # Upper case: iOS autocorrects "usd" to "USD", and a correction committed after the
    # choice overwrites the chosen value (KuiklyUI ignores autoCorrectEnabled on iOS).
    p.type("USD")
    check("dropdowns", "typing filters the options", p.wait(lambda: p.shown("美元 USD") and not p.shown("人民币 CNY"), 5))
    p.tap("美元 USD")
    check("dropdowns", "a filtered choice writes the value", p.wait(lambda: p.shown("美元 USD", True)))


FAMILIES = {f.__name__: f for f in (form, picker, datepicker, cascader, dropdowns, dialog, actionsheet, bottomsheet, popup)}


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("platform", choices=["android", "ios"])
    ap.add_argument("device")
    ap.add_argument("--only")
    a = ap.parse_args()
    p = Page(device(a.platform, a.device))
    for name in (a.only.split(",") if a.only else FAMILIES):
        try:
            FAMILIES[name](p)
        except DriverError as e:
            check(name, "ran to the end", False, str(e))
    print(f"DONE|{sum(RESULTS)}/{len(RESULTS)} passed")
    return 0 if all(RESULTS) else 1


if __name__ == "__main__":
    sys.exit(main())
