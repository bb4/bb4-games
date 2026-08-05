#!/usr/bin/env python3
"""Port Go JUnit Java tests under test/.../go/ to Scala 3 under scala-test/.

Produces a first draft; compile-fix loop still required.
Deletes are left to the caller.
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TEST = ROOT / "test"
SCALA_TEST = ROOT / "scala-test"
GO_JAVA = TEST / "com/barrybecker4/game/twoplayer/go"


def java_to_scala_path(java_path: Path) -> Path:
    rel = java_path.relative_to(TEST)
    return SCALA_TEST / rel.with_suffix(".scala")


def strip_java_comments_keep_copyright(src: str) -> tuple[str, str]:
    copy_m = re.match(r"(/\*.*?\*/)\s*", src, re.S)
    copyright = copy_m.group(1) if copy_m else "/* Copyright by Barry G. Becker. Licensed under MIT License */"
    rest = src[copy_m.end() :] if copy_m else src
    # drop single-line // comments that are full-line annotations leftovers later
    return copyright, rest


def convert_casts(s: str) -> str:
    # (Foo)expr → expr.asInstanceOf[Foo]  (simple identifiers / method calls)
    def repl(m: re.Match) -> str:
        typ = m.group(1)
        expr = m.group(2)
        return f"{expr}.asInstanceOf[{typ}]"

    # repeated application for nested-ish cases
    for _ in range(5):
        ns = re.sub(
            r"\(([A-Z][\w.]*(?:\[[^\]]+\])?)\)\s*((?:new\s+)?[\w.]+(?:\([^)]*\))?)",
            repl,
            s,
        )
        if ns == s:
            break
        s = ns
    return s


def convert_new_array(s: str) -> str:
    # new Foo[]{ a, b } → Array(a, b)
    s = re.sub(
        r"new\s+[\w.]+(?:\[[^\]]*\])?\s*\[\s*\]\s*\{([^{}]*)\}",
        lambda m: "Array(" + m.group(1).strip() + ")",
        s,
    )
    # new Type[n] → new Array[Type](n)  — leave mostly alone; rare in tests
    return s


def convert_for_each(s: str) -> str:
    # for (Type x : coll) { → for (x <- coll.asScala) {  — better: import converters
    s = re.sub(
        r"for\s*\(\s*([\w.<>,\s\[\]]+?)\s+(\w+)\s*:\s*([^)]+)\)\s*\{",
        r"for (\2 <- \3) {",
        s,
    )
    return s


def convert_body(body: str, uses_assert_from_testcase: bool) -> str:
    body = body.replace("@Override\n", "")
    body = body.replace("@Override ", "")
    body = re.sub(
        r"protected\s+void\s+setUp\s*\(\s*\)\s*(throws\s+[\w\s,.]+)?\s*\{",
        "@Before\ndef setUp(): Unit = {",
        body,
    )
    body = re.sub(
        r"protected\s+void\s+tearDown\s*\(\s*\)\s*(throws\s+[\w\s,.]+)?\s*\{",
        "@After\ndef tearDown(): Unit = {",
        body,
    )
    body = body.replace("super.setUp();", "")
    body = body.replace("super.tearDown();", "")

    body = re.sub(
        r"@Test\s*(public\s+)?void\s+(\w+)\s*\(\s*\)\s*(throws\s+[\w\s,.]+)?\s*\{",
        r"@Test\ndef \2(): Unit = {",
        body,
    )
    body = re.sub(
        r"(public|protected)\s+void\s+(test\w+)\s*\(\s*\)\s*(throws\s+[\w\s,.]+)?\s*\{",
        r"@Test\ndef \2(): Unit = {",
        body,
    )

    if uses_assert_from_testcase:
        for name in (
            "assertEquals",
            "assertTrue",
            "assertFalse",
            "assertNull",
            "assertNotNull",
            "fail",
        ):
            body = re.sub(rf"\b{name}\(", f"Assert.{name}(", body)

    body = convert_casts(body)
    body = convert_new_array(body)
    body = convert_for_each(body)

    # primitives / modifiers
    body = re.sub(r"\bboolean\b", "Boolean", body)
    body = re.sub(r"\bint\b", "Int", body)
    body = re.sub(r"\bdouble\b", "Double", body)
    body = re.sub(r"\bfloat\b", "Float", body)
    body = re.sub(r"\blong\b", "Long", body)
    body = re.sub(r"\bvoid\b", "Unit", body)
    body = re.sub(r"\bpublic\s+", "", body)
    body = re.sub(r"\bfinal\s+", "", body)
    body = re.sub(r"\bstatic\s+", "", body)

    # remove trailing semicolons
    body = re.sub(r";\s*\n", "\n", body)
    body = re.sub(r";\s*$", "", body, flags=re.M)

    # method defs: Type name( → def name(
    body = re.sub(
        r"(?m)^(\s*)(override\s+)?(protected\s+|private\s+)?([A-Z][\w.<>,\s\[\]]+?)\s+(\w+)\s*\(",
        lambda m: f"{m.group(1)}{m.group(2) or ''}{m.group(3) or ''}def {m.group(5)}(",
        body,
    )

    # fields with initializer
    body = re.sub(
        r"(?m)^(\s*)(private\s+|protected\s+)?([A-Z][\w.<>,\s\[\]]+?)\s+(\w+)\s*=",
        lambda m: f"{m.group(1)}{m.group(2) or ''}var {m.group(4)}: {m.group(3).strip()} =",
        body,
    )
    # fields without initializer
    body = re.sub(
        r"(?m)^(\s*)(private\s+|protected\s+)?([A-Z][\w.<>,\s\[\]]+?)\s+(\w+)\s*$",
        lambda m: (
            f"{m.group(1)}{m.group(2) or ''}var {m.group(4)}: {m.group(3).strip()} = "
            f"scala.compiletime.uninitialized"
        ),
        body,
    )

    # Java-style method params Type name → name: Type (best-effort inside defs)
    def fix_params(m: re.Match) -> str:
        prefix, params, rest = m.group(1), m.group(2), m.group(3)
        if not params.strip():
            return m.group(0)
        parts = []
        for p in re.split(r",(?![^<>]*>)", params):
            p = p.strip()
            if not p:
                continue
            pm = re.match(r"([\w.<>,\s\[\]]+?)\s+(\w+)$", p)
            if pm:
                parts.append(f"{pm.group(2)}: {pm.group(1).strip()}")
            else:
                parts.append(p)
        return f"{prefix}{', '.join(parts)}{rest}"

    body = re.sub(r"(def\s+\w+\s*\()([^)]*)(\))", fix_params, body)

    # constructors: ClassName( → def this(  — leave; handled per-file often
    # switch → match (very rough)
    body = re.sub(
        r"switch\s*\(([^)]+)\)\s*\{",
        r"\1 match {",
        body,
    )
    body = re.sub(r"case\s+(\w+)\s*:", r"case \1 =>", body)
    body = body.replace("break\n", "\n")

    # System.out.println stays
    # .size() on java collections often stays; Scala Set from getGroups may need care

    # null assignments already have uninitialized for bare fields
    body = body.replace("= null", "= scala.compiletime.uninitialized")

    # ArrayList / List creation
    body = re.sub(r"new\s+ArrayList<[^>]+>\(\s*\d*\s*\)", "new java.util.ArrayList()", body)
    body = re.sub(r"new\s+ArrayList<[^>]+>\(\)", "new java.util.ArrayList()", body)

    return body


def convert(src: str) -> str:
    copyright, rest = strip_java_comments_keep_copyright(src)

    pkg_m = re.search(r"package\s+([\w.]+)\s*;", rest)
    package = pkg_m.group(1) if pkg_m else ""

    imports = re.findall(r"import\s+(static\s+)?([\w.*]+)\s*;", rest)
    import_lines: list[str] = []
    need_assert = False
    for static, name in imports:
        if name.startswith("junit.framework"):
            need_assert = True
            continue
        if name.startswith("org.junit"):
            need_assert = True
            continue
        if static:
            if "Assert" in name or name.split(".")[-1].startswith("assert"):
                need_assert = True
                continue
            import_lines.append(f"import {name}")
        else:
            import_lines.append(f"import {name}")

    # class / interface
    class_m = re.search(
        r"(public\s+)?(abstract\s+)?(final\s+)?(class|interface)\s+(\w+)(\s+extends\s+([\w.<>,\s]+))?(\s+implements\s+([\w.<>,\s]+))?",
        rest,
    )
    if not class_m:
        raise ValueError("no class")

    is_abstract = bool(class_m.group(2))
    is_iface = class_m.group(4) == "interface"
    cname = class_m.group(5)
    extends = (class_m.group(7) or "").strip()
    extends = re.sub(r"\s+", " ", extends)
    implements = (class_m.group(9) or "").strip()
    implements = re.sub(r"\s+", " ", implements)

    uses_testcase = (
        extends in ("TestCase", "junit.framework.TestCase")
        or extends.endswith("TestCase")
        or extends.endswith("WorthCalculatorBase")
        or extends.endswith("TestEyeTypeAnalyzer")
        or "assertEquals" in rest
        or "assertTrue" in rest
    )
    need_assert = need_assert or uses_testcase

    parent = ""
    if extends and extends not in ("TestCase", "junit.framework.TestCase"):
        parent = f" extends {extends}"
    if implements:
        parent += (" with " if parent else " extends ") + implements.replace(",", " with")

    body_start = rest.find("{", class_m.end())
    body = rest[body_start + 1 : rest.rfind("}")]
    body = convert_body(body, uses_testcase)

    # nested enum → Scala enum
    def nest_enum(m: re.Match) -> str:
        name = m.group(1)
        cases = [c.strip() for c in m.group(2).split(",") if c.strip()]
        cases = [re.sub(r"\s+.*", "", c) for c in cases]
        return f"private enum {name}:\n        case {', '.join(cases)}\n"

    body = re.sub(
        r"private\s+enum\s+(\w+)\s*\{([^}]+)\}",
        nest_enum,
        body,
    )

    kw = "trait" if is_iface else ("abstract class" if is_abstract else "class")
    out: list[str] = [copyright, f"package {package}", ""]
    if need_assert:
        out.append("import org.junit.Assert")
        out.append("import org.junit.Before")
        out.append("import org.junit.After")
        out.append("import org.junit.Test")
    for line in import_lines:
        out.append(line)
    # CollectionConverters often needed for Java Sets from Scala APIs that return Java
    if "for (" in body and " <- " in body:
        out.append("import scala.jdk.CollectionConverters._")
    out.append("")
    out.append(f"{kw} {cname}{parent} {{")
    out.append("")
    out.extend(body.splitlines())
    out.append("}")
    out.append("")
    return "\n".join(out)


def main() -> None:
    files = sorted(GO_JAVA.rglob("*.java"))
    if len(sys.argv) > 1:
        files = [Path(p) if Path(p).is_absolute() else ROOT / p for p in sys.argv[1:]]
    for jp in files:
        text = jp.read_text()
        sp = java_to_scala_path(jp)
        sp.parent.mkdir(parents=True, exist_ok=True)
        try:
            scala = convert(text)
        except Exception as e:
            print(f"FAIL {jp}: {e}")
            continue
        sp.write_text(scala)
        print(f"OK {jp.relative_to(ROOT)} -> {sp.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
