#!/usr/bin/env python3
"""Lightweight repository formatting guard with no third-party dependencies."""

from __future__ import annotations

from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
EXTENSIONS = {".kt", ".kts", ".xml", ".md", ".yml", ".yaml", ".properties", ".py"}
SKIP_PARTS = {".git", ".gradle", "build"}


def candidate(path: Path) -> bool:
    return path.is_file() and path.suffix in EXTENSIONS and not any(part in SKIP_PARTS for part in path.parts)


def main() -> int:
    failures: list[str] = []
    for path in sorted(ROOT.rglob("*")):
        if not candidate(path):
            continue
        relative = path.relative_to(ROOT)
        try:
            raw = path.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            failures.append(f"{relative}: not valid UTF-8")
            continue

        if raw and not raw.endswith("\n"):
            failures.append(f"{relative}: missing final newline")

        for number, line in enumerate(raw.splitlines(), start=1):
            if line.rstrip(" \t") != line:
                failures.append(f"{relative}:{number}: trailing whitespace")
            if "\t" in line:
                failures.append(f"{relative}:{number}: tab character")

    if failures:
        print("Formatting check failed:")
        for failure in failures:
            print(f"- {failure}")
        return 1

    print("Formatting check passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
