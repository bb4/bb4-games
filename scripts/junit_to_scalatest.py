#!/usr/bin/env python3
"""Mechanical JUnit TestCase → ScalaTest AnyFunSuite converter (best-effort)."""
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


def convert(src: str, class_name: str) -> str:
    # Strip package/import later — work on body
    pkg_m = re.search(r"package\s+([\w.]+)\s*;", src)
    package = pkg_m.group(1) if pkg_m else ""

    imports = re.findall(r"import\s+([\w.*]+)\s*;", src)
    # Drop junit imports
    imports = [i for i in imports if "junit" not in i.lower()]

    # Find class declaration
    class_m = re.search(
        r"(public\s+)?(abstract\s+)?class\s+(\w+)(\s+extends\s+([\w.<>,\s]+))?",
        src,
    )
    if not class_m:
        raise ValueError(f"No class in {class_name}")
    is_abstract = bool(class_m.group(2))
    cname = class_m.group(3)
    extends = (class_m.group(5) or "").strip()
    extends = re.sub(r"\s+", " ", extends)
    if extends in ("TestCase", "junit.framework.TestCase"):
        extends = "AnyFunSuite with BeforeAndAfterEach"
    elif "TestCase" in extends:
        extends = extends.replace("TestCase", "GoTestCase")  # may need manual fix
        if "GoTestCase" not in extends and "AnyFunSuite" not in extends:
            extends = f"{extends}, BeforeAndAfterEach"
    else:
        if not extends:
            extends = "AnyFunSuite with BeforeAndAfterEach"
        elif "AnyFunSuite" not in extends and "GoTestCase" not in extends:
            extends = f"{extends} with AnyFunSuite with BeforeAndAfterEach"

    body_start = src.find("{", class_m.end())
    body = src[body_start + 1 : src.rfind("}")]

    # setUp / tearDown
    body = re.sub(
        r"@Override\s*protected\s+void\s+setUp\s*\(\s*\)\s*(throws\s+Exception\s*)?\{",
        "override def beforeEach(): Unit = {",
        body,
    )
    body = re.sub(
        r"@Override\s*protected\s+void\s+tearDown\s*\(\s*\)\s*(throws\s+Exception\s*)?\{",
        "override def afterEach(): Unit = {",
        body,
    )
    body = body.replace("super.setUp();", "")
    body = body.replace("super.tearDown();", "")

    # test methods
    def test_repl(m: re.Match) -> str:
        name = m.group(1)
        # camelCase → spaced words roughly
        nice = re.sub(r"([a-z])([A-Z])", r"\1 \2", name)
        nice = nice.replace("_", " ")
        if nice.startswith("test ") or nice.startswith("test"):
            nice = nice[4:].lstrip()
        return f'test("{nice}") {{'

    body = re.sub(
        r"(public|protected)\s+void\s+(test\w+)\s*\(\s*\)\s*(throws\s+[\w\s,]+)?\{",
        lambda m: test_repl(m) if True else "",
        body,
    )
    # Fix: above lambda used wrong group — redo properly
    body = re.sub(
        r'(public|protected)\s+void\s+(test\w+)\s*\(\s*\)\s*(throws\s+[\w\s,]+)?\{',
        lambda m: (
            lambda name: (
                (lambda nice: f'test("{nice}") {{')(
                    re.sub(r"^test\s*", "", re.sub(r"([a-z])([A-Z])", r"\1 \2", name).replace("_", " ")).strip()
                    or name
                )
            )
        )(m.group(2)),
        body,
    )

    # assertions
    body = re.sub(
        r"assertEquals\(\s*\"([^\"]*)\"\s*,\s*([^,]+)\s*,\s*([^)]+)\)\s*;",
        r'assert(\2 == \3, "\1")',
        body,
    )
    body = re.sub(
        r"assertEquals\(\s*([^,]+)\s*,\s*([^)]+)\)\s*;",
        r"assert(\1 == \2)",
        body,
    )
    # swap assertEquals expected/actual — JUnit is (msg?, expected, actual); we did expected==actual which is wrong order for (expected, actual)
    # Actually I mapped assertEquals(msg, expected, actual) → assert(expected == actual) — wait I used \2 == \3 where \2 is expected and \3 actual in (msg, expected, actual). Good.
    # For (expected, actual) → assert(expected == actual) — but I wrote assert(\1 == \2) which is expected == actual. Good.

    body = re.sub(r"assertTrue\(\s*\"([^\"]*)\"\s*,\s*([^)]+)\)\s*;", r'assert(\2, "\1")', body)
    body = re.sub(r"assertTrue\(\s*([^)]+)\)\s*;", r"assert(\1)", body)
    body = re.sub(r"assertFalse\(\s*\"([^\"]*)\"\s*,\s*([^)]+)\)\s*;", r'assert(!(\2), "\1")', body)
    body = re.sub(r"assertFalse\(\s*([^)]+)\)\s*;", r"assert(!(\1))", body)
    body = re.sub(r"assertNull\(\s*([^)]+)\)\s*;", r"assert(\1 == null)", body)
    body = re.sub(r"assertNotNull\(\s*([^)]+)\)\s*;", r"assert(\1 != null)", body)
    body = re.sub(r"fail\(\s*\"([^\"]*)\"\s*\)\s*;", r'fail("\1")', body)

    # types / syntax
    body = body.replace("void ", "def ")
    body = re.sub(r"\bprotected\s+", "", body)
    body = re.sub(r"\bprivate\s+", "private ", body)
    body = re.sub(r"\bpublic\s+", "", body)
    body = re.sub(r"\bfinal\s+", "", body)
    body = re.sub(r"\bnew\s+", "", body)  # often OK in Scala 3
    body = body.replace("null;", "null")
    body = re.sub(r";\s*\n", "\n", body)
    body = re.sub(r"\(String ", "(name: String ", body)  # weak
    body = re.sub(r"\bboolean\b", "Boolean", body)
    body = re.sub(r"\bint\b", "Int", body)
    body = re.sub(r"\bdouble\b", "Double", body)
    body = re.sub(r"\bfloat\b", "Float", body)
    body = re.sub(r"\blong\b", "Long", body)
    body = re.sub(r"\bString\b", "String", body)

    # methods: Type name( → def name(: still broken for return types
    body = re.sub(
        r"(?m)^\s*(override\s+)?(def\s+)?([A-Z][\w.<>,\s\[\]]+)\s+(\w+)\s*\(",
        lambda m: f"  {m.group(1) or ''}def {m.group(4)}(",
        body,
    )

    kw = "abstract class" if is_abstract else "class"
    out = []
    out.append(src.split("package")[0].split("/*")[0] if False else "")
    # copyright from source
    copy_m = re.match(r"(/\*.*?\*/)\s*", src, re.S)
    if copy_m:
        out.append(copy_m.group(1))
    out.append(f"package {package}")
    out.append("")
    out.append("import org.scalatest.funsuite.AnyFunSuite")
    out.append("import org.scalatest.BeforeAndAfterEach")
    for i in imports:
        out.append(f"import {i}")
    out.append("")
    out.append(f"{kw} {cname} extends {extends}:")
    out.append("")
    # indent body
    for line in body.splitlines():
        if line.strip():
            out.append("  " + line if not line.startswith("  ") else line)
        else:
            out.append("")
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
        try:
            scala = convert(text, jp.stem)
            sp.write_text(scala)
            print(f"OK {jp} -> {sp}")
        except Exception as e:
            print(f"FAIL {jp}: {e}", file=sys.stderr)


if __name__ == "__main__":
    main(sys.argv[1:])
