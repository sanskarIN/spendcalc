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
    "docs/accessibility.md",
    "docs/adr/0001-use-bigdecimal-for-finance.md",
    "docs/adr/0002-local-first-core.md",
    "docs/adr/0003-room-and-datastore.md",
    "docs/adr/0004-versioned-local-backup.md",
    "docs/architecture.md",
    "docs/assets/screenshots/README.md",
    "docs/assets/spendcalc-logo.svg",
    "docs/backup-restore.md",
    "docs/codebase-reference.md",
    "docs/design-system.md",
    "docs/development.md",
    "docs/documentation-map.md",
    "docs/features.md",
    "docs/github-maintenance.md",
    "docs/logging.md",
    "docs/performance.md",
    "docs/persistence-invariants.md",
    "docs/privacy-backup.md",
    "docs/release-candidate-final-audit.md",
    "docs/release.md",
    "docs/security-backup.md",
    "docs/setup.md",
    "docs/testing.md",
    "docs/troubleshooting.md",
    "docs/verification.md",
    "scripts/check_android_resources.py",
    "scripts/check_android_security.py",
    "scripts/check_documentation_coverage.py",
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
