#!/usr/bin/env python3
"""Fail CI if Kotlin source uses the reserved `in` keyword unescaped in package paths."""

from __future__ import annotations

from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
SOURCE_ROOT = ROOT / "app" / "src"
INVALID = re.compile(r"^\s*(?:package|import)\s+in\.sanskar\.spendcalc(?:\.|\s|$)")


def main() -> int:
    failures: list[str] = []
    for path in sorted(SOURCE_ROOT.rglob("*.kt")):
        for line_number, line in enumerate(path.read_text(encoding="utf-8").splitlines(), start=1):
            if INVALID.search(line):
                failures.append(
                    f"{path.relative_to(ROOT)}:{line_number}: escape the Kotlin keyword as `in`.sanskar...",
                )

    if failures:
        print("Kotlin namespace check failed:")
        for failure in failures:
            print(f"- {failure}")
        return 1

    print("Kotlin namespace check passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
