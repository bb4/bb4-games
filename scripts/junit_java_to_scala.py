#!/usr/bin/env python3
"""Convert JUnit Java tests to Scala sources that still use JUnit (vintage).

Keeps assertEquals/@Test/@Before so ports stay close to the originals.
Deletes are left to the caller after compile succeeds.
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TEST = ROOT / "test"
SCALA_TEST = ROOT / "scala-test"


def java_to_scala_path(java_path: Path) -> Path:
    rel = java_path.relative_to(TEST)
    return SCALA_TEST / rel.with_suffix(".scala")


def convert(src: str) -> str:
    copy_m = re.match(r"(/\*.*?\*/)\s*", src, re.S)
    copyright = copy_m.group(1) if copy_m else ""

    pkg_m = re.search(r"package\s+([\w.]+)\s*;", src)
    package = pkg_m.group(1) if pkg_m else ""

    imports = re.findall(r"import\s+(static\s+)?([\w.*]+)\s*;", src)
    import_lines: list[str] = []
    for static, name in imports:
        if name.startswith("junit.framework"):
            continue
        if name.startswith("org.junit"):
            continue
        if static:
            # static import org.junit.Assert.assertEquals → skip; we'll use Assert.
            if "Assert" in name or name.endswith("assertEquals") or name.endswith("assertTrue"):
                continue
            # other static imports: import foo.Bar.BAZ style becomes import foo.Bar.BAZ
            import_lines.append(f"import {name}")
        else:
            import_lines.append(f"import {name}")

    # class header
    class_m = re.search(
        r"(public\s+)?(abstract\s+)?class\s+(\w+)(\s+extends\s+([\w.<>,\s]+))?",
        src,
    )
    if not class_m:
        raise ValueError("no class")
    is_abstract = bool(class_m.group(2))
    cname = class_m.group(3)
    extends = (class_m.group(5) or "").strip()
    extends = re.sub(r"\s+", " ", extends)

    uses_testcase = extends in ("TestCase", "junit.framework.TestCase") or extends.endswith("TestCase")
    parent = ""
    if extends and extends not in ("TestCase", "junit.framework.TestCase"):
        parent = f" extends {extends}"

    body_start = src.find("{", class_m.end())
    body = src[body_start + 1 : src.rfind("}")]

    # annotations / lifecycle
    body = body.replace("@Override\n", "")
    body = body.replace("@Override ", "")
    body = re.sub(
        r"protected\s+void\s+setUp\s*\(\s*\)\s*(throws\s+Exception\s*)?\{",
        "@Before\ndef setUp(): Unit = {",
        body,
    )
    body = re.sub(
        r"protected\s+void\s+tearDown\s*\(\s*\)\s*(throws\s+Exception\s*)?\{",
        "@After\ndef tearDown(): Unit = {",
        body,
    )
    body = body.replace("super.setUp();", "")
    body = body.replace("super.tearDown();", "")

    # @Test methods (JUnit 4) and testXxx (JUnit 3)
    body = re.sub(
        r"@Test\s*(public\s+)?void\s+(\w+)\s*\(\s*\)\s*(throws\s+[\w\s,]+)?\{",
        r"@Test\ndef \2(): Unit = {",
        body,
    )
    body = re.sub(
        r"(public|protected)\s+void\s+(test\w+)\s*\(\s*\)\s*(throws\s+[\w\s,]+)?\{",
        r"@Test\ndef \2(): Unit = {",
        body,
    )

    # assertions via Assert for TestCase subclasses
    if uses_testcase:
        body = re.sub(r"\bassertEquals\(", "Assert.assertEquals(", body)
        body = re.sub(r"\bassertTrue\(", "Assert.assertTrue(", body)
        body = re.sub(r"\bassertFalse\(", "Assert.assertFalse(", body)
        body = re.sub(r"\bassertNull\(", "Assert.assertNull(", body)
        body = re.sub(r"\bassertNotNull\(", "Assert.assertNotNull(", body)
        body = re.sub(r"\bfail\(", "Assert.fail(", body)

    # types
    body = re.sub(r"\bboolean\b", "Boolean", body)
    body = re.sub(r"\bint\b", "Int", body)
    body = re.sub(r"\bdouble\b", "Double", body)
    body = re.sub(r"\bfloat\b", "Float", body)
    body = re.sub(r"\blong\b", "Long", body)
    body = re.sub(r"\bvoid\b", "Unit", body)
    body = re.sub(r"\bpublic\s+", "", body)
    body = re.sub(r"\bprotected\s+", "protected ", body)
    body = re.sub(r"\bprivate\s+", "private ", body)
    body = re.sub(r"\bfinal\s+", "", body)
    body = re.sub(r";\s*\n", "\n", body)

    # field decls: Type name; → var name: Type = null / uninitialized later
    # methods with return types: Type name( → def name(
    body = re.sub(
        r"(?m)^(\s*)(override\s+)?([A-Z][\w.<>,\s\[\]]+?)\s+(\w+)\s*\(",
        lambda m: f"{m.group(1)}{m.group(2) or ''}def {m.group(4)}(",
        body,
    )
    # simple fields Type name = ... or Type name;
    body = re.sub(
        r"(?m)^(\s*)(private\s+|protected\s+)?(static\s+)?([A-Z][\w.<>,\s\[\]]+?)\s+(\w+)\s*=",
        lambda m: (
            f"{m.group(1)}{m.group(2) or ''}{'val ' if m.group(3) else 'var '}"
            f"{m.group(5)}: {m.group(4).strip()} ="
        ),
        body,
    )
    body = re.sub(
        r"(?m)^(\s*)(private\s+|protected\s+)?(static\s+)?([A-Z][\w.<>,\s\[\]]+?)\s+(\w+)\s*$",
        lambda m: (
            f"{m.group(1)}{m.group(2) or ''}{'val ' if m.group(3) else 'var '}"
            f"{m.group(5)}: {m.group(4).strip()} = null"
        ),
        body,
    )

    # null → uninitialized hint for clarity later; leave null for now
    body = body.replace("static ", "")

    kw = "abstract class" if is_abstract else "class"
    out: list[str] = []
    if copyright:
        out.append(copyright)
    out.append(f"package {package}")
    out.append("")
    out.append("import org.junit.Assert")
    out.append("import org.junit.Before")
    out.append("import org.junit.After")
    out.append("import org.junit.Test")
    for line in import_lines:
        out.append(line)
    out.append("")
    out.append(f"{kw} {cname}{parent} {{")
    out.append("")
    for line in body.splitlines():
        out.append(line)
    out.append("}")
    out.append("")
    return "\n".join(out)


def main(paths: list[str]) -> None:
    for p in paths:
        jp = Path(p)
        if not jp.is_absolute():
            jp = ROOT / p
        text = jp.read_text()
        sp = java_to_scala_path(jp)
        sp.parent.mkdir(parents=True, exist_ok=True)
        scala = convert(text)
        sp.write_text(scala)
        print(f"OK {jp.relative_to(ROOT)} -> {sp.relative_to(ROOT)}")


if __name__ == "__main__":
    main(sys.argv[1:])
