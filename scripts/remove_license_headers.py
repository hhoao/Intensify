#!/usr/bin/env python3
"""Remove verbose MPL headers from Java source files."""

from __future__ import annotations

import argparse
from pathlib import Path


DEFAULT_ROOTS = (Path("src"),)
JAVA_DECLARATION_MARKERS = (
    "package ",
    "import ",
    "public class ",
    "class ",
    "public interface ",
    "interface ",
    "public enum ",
    "enum ",
    "public record ",
    "record ",
)


def strip_license_header(text: str) -> tuple[str, bool]:
    if not text.startswith("/*"):
        return text, False

    header_end = text.find("*/")
    if header_end == -1:
        return text, False

    header = text[: header_end + 2]
    if "Mozilla Public License" not in header:
        return text, False

    rest = text[header_end + 2 :].lstrip("\r\n")
    stripped_rest = rest.lstrip()
    if not stripped_rest.startswith(JAVA_DECLARATION_MARKERS):
        return text, False

    return rest, True


def iter_java_files(roots: list[Path]) -> list[Path]:
    files: list[Path] = []
    for root in roots:
        if root.is_file() and root.suffix == ".java":
            files.append(root)
        elif root.is_dir():
            files.extend(root.rglob("*.java"))
    return sorted(files)


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Remove file-level Mozilla Public License comments from Java sources."
    )
    parser.add_argument(
        "paths",
        nargs="*",
        type=Path,
        default=list(DEFAULT_ROOTS),
        help="Java files or directories to scan. Defaults to src/.",
    )
    parser.add_argument(
        "--check",
        action="store_true",
        help="Only report files that would change.",
    )
    args = parser.parse_args()

    changed: list[Path] = []
    for path in iter_java_files(args.paths):
        text = path.read_text(encoding="utf-8")
        new_text, did_change = strip_license_header(text)
        if not did_change:
            continue

        changed.append(path)
        if not args.check:
            path.write_text(new_text, encoding="utf-8")

    action = "Would remove" if args.check else "Removed"
    for path in changed:
        print(path)
    print(f"{action} {len(changed)} license header(s).")
    return 1 if args.check and changed else 0


if __name__ == "__main__":
    raise SystemExit(main())
