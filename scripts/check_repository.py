#!/usr/bin/env python3
"""Validate required repository metadata and local Markdown links."""

from __future__ import annotations

from pathlib import Path
import re
import sys
from urllib.parse import unquote

ROOT = Path(__file__).resolve().parents[1]
REQUIRED_FILES = [
    "README.md",
    "LICENSE",
    "CONTRIBUTING.md",
    "CODE_OF_CONDUCT.md",
    "SECURITY.md",
    "SUPPORT.md",
    "PRIVACY.md",
    "CHANGELOG.md",
    "ROADMAP.md",
    "what_changed.md",
    ".gitignore",
    ".editorconfig",
    ".gitattributes",
    ".env.example",
    "docs/architecture.md",
    "docs/setup.md",
    "docs/development.md",
    "docs/testing.md",
    "docs/release.md",
    "docs/troubleshooting.md",
    "docs/accessibility.md",
    "docs/performance.md",
    "docs/backup-restore.md",
    "docs/logging.md",
    "scripts/check_android_resources.py",
    "scripts/check_android_security.py",
]
README_REQUIREMENTS = [
    "Made by the Sanskar",
    "https://buymeacoffee.com/sanskarIN",
    "sanskarin@outlook.in",
    "sanskarin.business@gmail.com",
    "supportramsandesh@gmail.com",
    "https://github.com/sanskarIN",
    "MIT",
]
MARKDOWN_LINK = re.compile(r"(?<!!)\[[^\]]*\]\(([^)]+)\)")


def local_target(markdown: Path, raw_target: str) -> Path | None:
    target = raw_target.strip().split("#", 1)[0]
    if not target or target.startswith(("http://", "https://", "mailto:")):
        return None
    target = unquote(target)
    if target.startswith("/"):
        return ROOT / target.lstrip("/")
    return markdown.parent / target


def main() -> int:
    failures: list[str] = []

    for required in REQUIRED_FILES:
        if not (ROOT / required).exists():
            failures.append(f"missing required file: {required}")

    readme_path = ROOT / "README.md"
    if readme_path.exists():
        readme = readme_path.read_text(encoding="utf-8")
        for required_text in README_REQUIREMENTS:
            if required_text not in readme:
                failures.append(f"README.md missing required text: {required_text}")

    for markdown in sorted(ROOT.rglob("*.md")):
        if any(part in {".git", "build", ".gradle"} for part in markdown.parts):
            continue
        text = markdown.read_text(encoding="utf-8")
        for match in MARKDOWN_LINK.finditer(text):
            target = local_target(markdown, match.group(1))
            if target is not None and not target.exists():
                failures.append(
                    f"{markdown.relative_to(ROOT)}: broken local link -> {match.group(1)}",
                )

    if failures:
        print("Repository audit failed:")
        for failure in failures:
            print(f"- {failure}")
        return 1

    print("Repository audit passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
