#!/usr/bin/env python3
"""Fail fast when app string-resource references cannot resolve to a default value."""

from __future__ import annotations

import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
APP = ROOT / "app"
DEFAULT_VALUES = APP / "src" / "main" / "res" / "values"
SOURCE_ROOT = APP / "src"
MAIN_RES = APP / "src" / "main" / "res"
MANIFEST = APP / "src" / "main" / "AndroidManifest.xml"

KOTLIN_STRING_REFERENCE = re.compile(r"(?<!android\.)\bR\.string\.([A-Za-z0-9_]+)")
XML_STRING_REFERENCE = re.compile(r"@string/([A-Za-z0-9_]+)")


def collect_default_strings() -> tuple[set[str], list[str]]:
    names: set[str] = set()
    errors: list[str] = []

    if not DEFAULT_VALUES.is_dir():
        return names, [f"Missing default values directory: {DEFAULT_VALUES.relative_to(ROOT)}"]

    for path in sorted(DEFAULT_VALUES.glob("*.xml")):
        try:
            root = ET.parse(path).getroot()
        except ET.ParseError as exc:
            errors.append(f"Invalid XML in {path.relative_to(ROOT)}: {exc}")
            continue

        for element in root:
            is_string = element.tag == "string"
            is_string_item = element.tag == "item" and element.attrib.get("type") == "string"
            if not (is_string or is_string_item):
                continue

            name = element.attrib.get("name", "").strip()
            if not name:
                errors.append(f"Unnamed string resource in {path.relative_to(ROOT)}")
                continue
            if name in names:
                errors.append(f"Duplicate default string resource: {name}")
            names.add(name)

    return names, errors


def collect_references() -> dict[str, set[str]]:
    references: dict[str, set[str]] = {}

    for path in sorted(SOURCE_ROOT.rglob("*.kt")):
        text = path.read_text(encoding="utf-8")
        for name in KOTLIN_STRING_REFERENCE.findall(text):
            references.setdefault(name, set()).add(str(path.relative_to(ROOT)))

    xml_paths = list(MAIN_RES.rglob("*.xml"))
    if MANIFEST.is_file():
        xml_paths.append(MANIFEST)

    for path in sorted(set(xml_paths)):
        text = path.read_text(encoding="utf-8")
        for name in XML_STRING_REFERENCE.findall(text):
            references.setdefault(name, set()).add(str(path.relative_to(ROOT)))

    return references


def main() -> int:
    strings, errors = collect_default_strings()
    references = collect_references()

    for name in sorted(references.keys() - strings):
        locations = ", ".join(sorted(references[name]))
        errors.append(f"Missing default string resource '{name}' referenced by: {locations}")

    if errors:
        print("Android string resource audit failed:")
        for error in errors:
            print(f"- {error}")
        return 1

    print(
        "Android string resource audit passed: "
        f"{len(strings)} default strings, {len(references)} referenced strings checked."
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
