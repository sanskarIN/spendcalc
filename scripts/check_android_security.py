#!/usr/bin/env python3
"""Fail CI if SpendCalc's local-first Android sharing policy regresses."""

from __future__ import annotations

from pathlib import Path
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "app/src/main/AndroidManifest.xml"
FILE_PATHS = ROOT / "app/src/main/res/xml/file_paths.xml"
ANDROID = "{http://schemas.android.com/apk/res/android}"


def main() -> int:
    failures: list[str] = []

    manifest_root = ET.parse(MANIFEST).getroot()
    permissions = {
        node.get(f"{ANDROID}name")
        for node in manifest_root.findall("uses-permission")
    }
    if "android.permission.INTERNET" in permissions:
        failures.append("AndroidManifest.xml must not request android.permission.INTERNET")

    application = manifest_root.find("application")
    if application is None:
        failures.append("AndroidManifest.xml is missing <application>")
    else:
        providers = [
            provider
            for provider in application.findall("provider")
            if provider.get(f"{ANDROID}name") == "androidx.core.content.FileProvider"
        ]
        if len(providers) != 1:
            failures.append("exactly one androidx.core.content.FileProvider is required")
        else:
            provider = providers[0]
            if provider.get(f"{ANDROID}exported") != "false":
                failures.append("FileProvider must remain android:exported=\"false\"")
            if provider.get(f"{ANDROID}grantUriPermissions") != "true":
                failures.append("FileProvider must keep temporary URI grants enabled")
            metadata = provider.findall("meta-data")
            if not any(
                item.get(f"{ANDROID}name") == "android.support.FILE_PROVIDER_PATHS"
                and item.get(f"{ANDROID}resource") == "@xml/file_paths"
                for item in metadata
            ):
                failures.append("FileProvider must reference @xml/file_paths")

    paths_root = ET.parse(FILE_PATHS).getroot()
    children = list(paths_root)
    if len(children) != 1:
        failures.append("file_paths.xml must expose only the private export cache path")
    elif (
        children[0].tag != "cache-path"
        or children[0].get("name") != "exports"
        or children[0].get("path") != "exports/"
    ):
        failures.append("file_paths.xml must contain only cache-path exports -> exports/")

    if failures:
        print("Android security policy check failed:")
        for failure in failures:
            print(f"- {failure}")
        return 1

    print("Android security policy check passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
