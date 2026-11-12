#!/usr/bin/env python3
"""Mechanically migrate JUnit 4 Scala tests to JUnit Jupiter.

- Rewrites imports and annotations
- Reorders assert*(message, ...) to Jupiter's (..., message) form
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

ANNOTATION_MAP = {
    "org.junit.Test": "org.junit.jupiter.api.Test",
    "org.junit.Before": "org.junit.jupiter.api.BeforeEach",
    "org.junit.After": "org.junit.jupiter.api.AfterEach",
    "org.junit.BeforeClass": "org.junit.jupiter.api.BeforeAll",
    "org.junit.AfterClass": "org.junit.jupiter.api.AfterAll",
    "org.junit.Ignore": "org.junit.jupiter.api.Disabled",
}

# assert* overloads where the first arg was a String message in JUnit 4
MSG_FIRST_ASSERTS = {
    "assertEquals",
    "assertNotEquals",
    "assertTrue",
    "assertFalse",
    "assertNull",
    "assertNotNull",
    "assertSame",
    "assertNotSame",
    "assertArrayEquals",
}


def split_top_level_args(arg_str: str) -> list[str]:
    args: list[str] = []
    buf: list[str] = []
    depth = 0
    in_str = False
    quote = ""
    i = 0
    while i < len(arg_str):
        ch = arg_str[i]
        if in_str:
            buf.append(ch)
            if ch == "\\" and i + 1 < len(arg_str):
                buf.append(arg_str[i + 1])
                i += 2
                continue
            if ch == quote:
                in_str = False
            i += 1
            continue
        if ch in ('"', "'"):
            in_str = True
            quote = ch
            buf.append(ch)
        elif ch in "([{":
            depth += 1
            buf.append(ch)
        elif ch in ")]}":
            depth -= 1
            buf.append(ch)
        elif ch == "," and depth == 0:
            args.append("".join(buf).strip())
            buf = []
        else:
            buf.append(ch)
        i += 1
    if buf or args:
        args.append("".join(buf).strip())
    return [a for a in args if a]


def looks_like_message(expr: str) -> bool:
    e = expr.strip()
    if e.startswith('"') or e.startswith("'"):
        return True
    # msg + "..." / "..." + x
    if "+" in e and ('"' in e or "'" in e):
        return True
    # bare identifier named msg / message
    if re.fullmatch(r"(msg|message|err|error|failureMsg)", e):
        return True
    return False


def reorder_assert_call(name: str, args: list[str]) -> list[str]:
    if name not in MSG_FIRST_ASSERTS or len(args) < 2:
        return args
    if not looks_like_message(args[0]):
        return args
    # JUnit4 assertEquals(msg, expected, actual[, delta])
    # Jupiter assertEquals(expected, actual[, delta], msg)
    if name in ("assertEquals", "assertNotEquals", "assertArrayEquals"):
        if len(args) == 3:
            return [args[1], args[2], args[0]]
        if len(args) == 4:
            # msg, expected, actual, delta  OR  expected, actual, delta, msg (already jupiter)
            # If first looks like message, treat as JUnit4 with delta
            return [args[1], args[2], args[3], args[0]]
        return args
    # assertTrue/False/Null/NotNull/Same/NotSame(msg, value)
    if len(args) == 2:
        return [args[1], args[0]]
    return args


def rewrite_asserts(text: str) -> str:
    pattern = re.compile(r"\b(" + "|".join(MSG_FIRST_ASSERTS) + r")\s*\(")

    out: list[str] = []
    i = 0
    while True:
        m = pattern.search(text, i)
        if not m:
            out.append(text[i:])
            break
        out.append(text[i : m.start()])
        name = m.group(1)
        start_args = m.end()
        depth = 1
        j = start_args
        in_str = False
        quote = ""
        while j < len(text) and depth:
            ch = text[j]
            if in_str:
                if ch == "\\" and j + 1 < len(text):
                    j += 2
                    continue
                if ch == quote:
                    in_str = False
                j += 1
                continue
            if ch in ('"', "'"):
                in_str = True
                quote = ch
            elif ch == "(":
                depth += 1
            elif ch == ")":
                depth -= 1
                if depth == 0:
                    break
            j += 1
        arg_str = text[start_args:j]
        args = split_top_level_args(arg_str)
        new_args = reorder_assert_call(name, args)
        out.append(name + "(" + ", ".join(new_args) + ")")
        i = j + 1
    return "".join(out)


def rewrite_imports_and_annotations(text: str) -> str:
    # import org.junit.Assert._ / Assert.assertX
    text = re.sub(
        r"import\s+org\.junit\.Assert(\._)?",
        "import org.junit.jupiter.api.Assertions\\1",
        text,
    )
    text = re.sub(
        r"import\s+org\.junit\.Assert\.(\w+)",
        r"import org.junit.jupiter.api.Assertions.\1",
        text,
    )
    text = re.sub(
        r"import\s+org\.junit\.\{([^}]+)\}",
        lambda m: rewrite_brace_import(m.group(1)),
        text,
    )
    for old, new in ANNOTATION_MAP.items():
        text = text.replace(f"import {old}", f"import {new}")
        simple = old.rsplit(".", 1)[-1]
        new_simple = new.rsplit(".", 1)[-1]
        # @Before -> @BeforeEach etc when simple name used
        text = re.sub(rf"@{simple}\b", f"@{new_simple}", text)
    # bare org.junit.Test in import lists already handled; clean leftover org.junit.
    text = text.replace("org.junit.Assert", "org.junit.jupiter.api.Assertions")
    return text


def rewrite_brace_import(inner: str) -> str:
    parts = [p.strip() for p in inner.split(",")]
    mapped = []
    for p in parts:
        full = f"org.junit.{p}"
        if full in ANNOTATION_MAP:
            mapped.append(ANNOTATION_MAP[full].rsplit(".", 1)[-1])
        elif p == "Assert":
            mapped.append("Assertions")  # wrong package; fixed below
        else:
            mapped.append(p)
    # Prefer individual jupiter imports
    lines = []
    for p in parts:
        full = f"org.junit.{p}"
        if full in ANNOTATION_MAP:
            lines.append(f"import {ANNOTATION_MAP[full]}")
        elif p == "Assert":
            lines.append("import org.junit.jupiter.api.Assertions")
        else:
            lines.append(f"import org.junit.{p}")
    return "\n".join(lines)


def migrate_file(path: Path) -> bool:
    original = path.read_text(encoding="utf-8")
    if "org.junit" not in original or "org.junit.jupiter" in original and "org.junit.Test" not in original and "org.junit.Assert" not in original and "org.junit.Before" not in original:
        # still may have org.junit.After etc
        if not re.search(r"org\.junit\.(?!jupiter)", original):
            return False
    text = rewrite_imports_and_annotations(original)
    text = rewrite_asserts(text)
    # Remove obsolete runner imports left for later manual fix
    if text != original:
        path.write_text(text, encoding="utf-8")
        return True
    return False


def main(roots: list[str]) -> None:
    changed = 0
    for root in roots:
        base = Path(root)
        for path in sorted(base.rglob("*.scala")):
            if migrate_file(path):
                print(f"updated {path}")
                changed += 1
    print(f"changed {changed} files")


if __name__ == "__main__":
    main(sys.argv[1:] or ["scala-test"])
