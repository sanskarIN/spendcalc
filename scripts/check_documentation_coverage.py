#!/usr/bin/env python3
"""Ensure every tracked repository file is documented exactly once."""

from __future__ import annotations

from collections import Counter
from pathlib import Path
import re
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
REFERENCE = ROOT / "docs" / "codebase-reference.md"
START_MARKER = "<!-- FILE-INDEX:START -->"
END_MARKER = "<!-- FILE-INDEX:END -->"
ENTRY = re.compile(r"^- `([^`]+)` — ")


def tracked_files() -> set[str]:
    try:
        result = subprocess.run(
            ["git", "ls-files", "-z"],
            cwd=ROOT,
            check=True,
            capture_output=True,
        )
    except FileNotFoundError as exc:
        raise RuntimeError("Git is required for documentation coverage checks") from exc
    except subprocess.CalledProcessError as exc:
        stderr = exc.stderr.decode("utf-8", errors="replace").strip()
        detail = f": {stderr}" if stderr else ""
        raise RuntimeError(f"Unable to list tracked files{detail}") from exc

    try:
        decoded = result.stdout.decode("utf-8")
    except UnicodeDecodeError as exc:
        raise RuntimeError("Tracked repository paths are not valid UTF-8") from exc
    return {path for path in decoded.split("\0") if path}


def documented_files() -> tuple[set[str], list[str]]:
    if not REFERENCE.is_file():
        raise RuntimeError("Missing docs/codebase-reference.md")

    text = REFERENCE.read_text(encoding="utf-8")
    if text.count(START_MARKER) != 1 or text.count(END_MARKER) != 1:
        raise RuntimeError("Codebase reference must contain exactly one file-index marker pair")

    start = text.index(START_MARKER) + len(START_MARKER)
    end = text.index(END_MARKER)
    if end <= start:
        raise RuntimeError("Codebase reference file-index markers are misordered")

    entries: list[str] = []
    for line in text[start:end].splitlines():
        match = ENTRY.match(line)
        if match:
            entries.append(match.group(1))

    counts = Counter(entries)
    duplicates = sorted(path for path, count in counts.items() if count > 1)
    return set(entries), duplicates


def main() -> int:
    try:
        tracked = tracked_files()
        documented, duplicates = documented_files()
    except (OSError, RuntimeError) as exc:
        print(f"Documentation coverage audit failed: {exc}")
        return 1

    missing = sorted(tracked - documented)
    stale = sorted(documented - tracked)

    if missing or stale or duplicates:
        print("Documentation coverage audit failed:")
        if missing:
            print("- Tracked files missing from docs/codebase-reference.md:")
            for path in missing:
                print(f"  - {path}")
        if stale:
            print("- Documented paths that are not tracked:")
            for path in stale:
                print(f"  - {path}")
        if duplicates:
            print("- Paths documented more than once:")
            for path in duplicates:
                print(f"  - {path}")
        return 1

    print(
        "Documentation coverage passed: "
        f"{len(tracked)} tracked files documented exactly once."
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
