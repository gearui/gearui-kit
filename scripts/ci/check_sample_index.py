#!/usr/bin/env python3
"""Sample gallery index integrity.

Replaces an inline check in ci.yml that compared route ids to example directory
names as if they were one namespace. They are not, and the mismatch made the
check report seven "missing" example directories and four "uncovered" SDK
components that were all fine:

  bottom-navbar  -> examples/bottomnavbar     (hyphens)
  context-menu   -> examples/contextmenu
  navigation-menu-> examples/navigationmenu
  icon-render    -> examples/icon             (route named for the demo)
  tabs           -> examples/tab              (plural route, singular directory)
  runtime-insets -> examples/runtime

Its id regex was `[a-z0-9]+`, which silently dropped all seven hyphenated ids
from both sides at once — so the route-integrity half passed by being equally
blind to both, while the directory half failed for reasons that were never
real. The lesson is that a check comparing two lists has to be told what
relates them; matching on names that happen to look alike is a guess.

Four things are actually checkable:

  1. Route integrity     ComponentConfig ids == NavigationManager branch ids.
  2. Example implementation  Every route target resolves to a composable that
                         exists in the sample sources.
  3. SDK coverage        Every tracked component directory is reachable from a
                         route, matching dir name against the route id with
                         hyphens removed. Exceptions need a written reason.
  4. Release integrity   No indexed route opens ComingSoonExample. Waivers are
                         listed with reasons and may only shrink.

Coverage is derived, not hand-listed: the rule covers 56 of 57 components, so
the exceptions file holds one entry instead of a table that would rot.
"""

from __future__ import annotations

import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SAMPLE = ROOT / "sample/src/commonMain/kotlin/com/gearui/sample"
COMPONENTS = "gearui-kit/src/commonMain/kotlin/com/gearui/components"
EXCEPTIONS = ROOT / "scripts/ci/sample_index_exceptions.txt"

ID = r'"([a-z0-9-]+)"'


def tracked_dirs(prefix: str) -> set[str]:
    """First path segment under `prefix`, for git-tracked files only.

    Untracked empty directories are invisible here on purpose: three of them
    (accordion, appbar, tabbar) linger in local checkouts and would otherwise
    be reported as uncovered SDK components that do not exist.
    """
    out = subprocess.run(
        ["git", "ls-files", prefix], capture_output=True, text=True, cwd=ROOT
    ).stdout.split()
    return {f[len(prefix):].lstrip("/").split("/")[0] for f in out if f.startswith(prefix)}


def load_exceptions() -> tuple[dict[str, str], dict[str, str]]:
    uncovered: dict[str, str] = {}
    comingsoon: dict[str, str] = {}
    for raw in EXCEPTIONS.read_text().splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        parts = line.split(None, 2)
        if len(parts) < 3:
            sys.exit(f"Malformed exception (needs '<kind> <id> <reason>'): {raw}")
        kind, ident, reason = parts
        {"uncovered": uncovered, "comingsoon": comingsoon}[kind][ident] = reason
    return uncovered, comingsoon


def main() -> int:
    config = (SAMPLE / "config/ComponentConfig.kt").read_text()
    nav = (SAMPLE / "navigation/NavigationManager.kt").read_text()

    config_ids = set(re.findall(rf"ComponentInfo\({ID}", config))
    routes = dict(re.findall(rf"{ID}\s*->\s*(\w+)\s*\(", nav))
    uncovered_ok, comingsoon_ok = load_exceptions()

    errors: list[str] = []

    # 1. Route integrity.
    if config_ids != set(routes):
        errors.append(
            f"config vs nav mismatch: config-only={sorted(config_ids - set(routes))}, "
            f"nav-only={sorted(set(routes) - config_ids)}"
        )

    # 2. Example implementation.
    defined: set[str] = set()
    for f in subprocess.run(
        ["git", "ls-files", "sample/src"], capture_output=True, text=True, cwd=ROOT
    ).stdout.split():
        if f.endswith(".kt"):
            defined |= set(re.findall(r"^fun ([A-Za-z0-9_]+)\s*\(", (ROOT / f).read_text(), re.M))
    unimplemented = sorted({c for c in routes.values() if c not in defined})
    if unimplemented:
        errors.append(f"routes point at composables with no definition: {unimplemented}")

    # 3. SDK coverage.
    reachable = {r.replace("-", "") for r in routes}
    uncovered = sorted(d for d in tracked_dirs(COMPONENTS) if d not in reachable)
    unexplained = [d for d in uncovered if d not in uncovered_ok]
    if unexplained:
        errors.append(
            f"SDK components with no demo route and no recorded reason: {unexplained}"
        )
    stale_cover = sorted(set(uncovered_ok) - set(uncovered))
    if stale_cover:
        errors.append(f"coverage exceptions no longer needed, delete them: {stale_cover}")

    # 4. Release integrity.
    placeholders = sorted(r for r, c in routes.items() if c == "ComingSoonExample")
    unwaived = [r for r in placeholders if r not in comingsoon_ok]
    if unwaived:
        errors.append(f"indexed routes opening ComingSoonExample: {unwaived}")
    stale_waivers = sorted(set(comingsoon_ok) - set(placeholders))
    if stale_waivers:
        errors.append(f"coming-soon waivers no longer needed, delete them: {stale_waivers}")

    if errors:
        print("Sample index integrity failed:")
        for e in errors:
            print(f"  - {e}")
        print(f"\nExceptions live in {EXCEPTIONS.relative_to(ROOT)} and may only shrink.")
        return 1

    print(
        f"Sample index verified. routes={len(routes)} "
        f"components={len(tracked_dirs(COMPONENTS))} "
        f"coverage_exceptions={len(uncovered_ok)} coming_soon={len(comingsoon_ok)}"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
