#!/usr/bin/env python3
"""Careful Java→Scala conversion for Go JUnit tests."""
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TEST = ROOT / "test"
SCALA = ROOT / "scala-test"
JAVA_SRC = TEST / "com/barrybecker4/game/twoplayer/go"


def convert(src: str) -> str:
    # copyright
    copy_m = re.match(r"(/\*.*?\*/|//[^\n]*)\s*", src, re.S)
    if copy_m and copy_m.group(1).startswith("//"):
        copyright = "/* " + copy_m.group(1)[2:].strip() + " */"
        rest = src[copy_m.end():]
    elif copy_m:
        copyright = copy_m.group(1)
        rest = src[copy_m.end():]
    else:
        copyright = "/* Copyright by Barry G. Becker. Licensed under MIT */"
        rest = src

    pkg = re.search(r"package\s+([\w.]+)\s*;", rest).group(1)

    imports = []
    for m in re.finditer(r"import\s+(static\s+)?([\w.*]+)\s*;", rest):
        static, name = m.group(1), m.group(2)
        if name.startswith("junit.framework") or name.startswith("org.junit"):
            continue
        if static and ("Assert" in name or "assert" in name.split(".")[-1]):
            continue
        imports.append(name)

    # Extract class body
    cm = re.search(
        r"(public\s+)?(abstract\s+)?(final\s+)?class\s+(\w+)(\s+extends\s+(\S+))?(\s+implements\s+([\w.,\s]+))?",
        rest,
    )
    abstract = bool(cm.group(2))
    cname = cm.group(4)
    extends = cm.group(6) or ""
    implements = (cm.group(8) or "").strip()

    body_start = rest.find("{", cm.end())
    body = rest[body_start + 1 : rest.rfind("}")]

    # --- body transforms ---
    body = body.replace("@Override\n", "override ")
    body = body.replace("@Override ", "override ")

    # setUp / tearDown
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
    body = body.replace("super.setUp();", "super.setUp()")
    body = body.replace("super.tearDown();", "super.tearDown()")

    # test methods
    body = re.sub(
        r"(public|protected)\s+void\s+(test\w+)\s*\(\s*\)\s*(throws\s+[\w\s,.]+)?\s*\{",
        r"@Test\ndef \2(): Unit = {",
        body,
    )

    # Assertions from TestCase
    for a in ("assertEquals", "assertTrue", "assertFalse", "assertNull", "assertNotNull", "fail"):
        body = re.sub(rf"(?<!Assert\.)\b{a}\(", f"Assert.{a}(", body)

    # casts (Foo)x
    for _ in range(6):
        nb = re.sub(
            r"\(([A-Z][\w.]*)\)\s*([\w.]+(?:\([^;()]*\))?)",
            r"\2.asInstanceOf[\1]",
            body,
        )
        if nb == body:
            break
        body = nb

    # arrays Type[] → Array[Type]
    for _ in range(4):
        body = re.sub(r"\b([A-Z][\w.]*)\[\]", r"Array[\1]", body)

    # new Type[]{...} → Array(...)
    body = re.sub(
        r"new\s+[\w.]+(?:\[[^\]]*\])?\s*\[\s*\]\s*\{([^{}]*)\}",
        lambda m: "Array(" + m.group(1).strip() + ")",
        body,
    )

    # for-each
    body = re.sub(
        r"for\s*\(\s*([\w.<>,\s\[\]]+?)\s+(\w+)\s*:\s*([^)]+)\)\s*\{",
        r"for (\2 <- \3.asScala) {",
        body,
    )

    # classic for loops: for (int i = 0; i < n; i++) → for (i <- 0 until n)
    body = re.sub(
        r"for\s*\(\s*int\s+(\w+)\s*=\s*([^;]+);\s*\1\s*<\s*([^;]+);\s*\1\+\+\s*\)\s*\{",
        r"for (\1 <- \2 until \3) {",
        body,
    )

    # Method with return type (non-void) — before void/primitives swap
    # protected int getBoardSize() { return 5; }
    body = re.sub(
        r"(override\s+)?(protected\s+|private\s+)?(int|boolean|double|float|long|String|[\w.]+)\s+(\w+)\s*\(([^)]*)\)\s*(throws\s+[\w\s,.]+)?\s*\{\s*return\s+([^;]+);\s*\}",
        lambda m: f"{m.group(1) or ''}{m.group(2) or ''}def {m.group(4)}({_params(m.group(5))}): {_jtype(m.group(3))} = {m.group(7).strip()}",
        body,
    )

    # void methods that aren't tests
    body = re.sub(
        r"(override\s+)?(protected\s+|private\s+)?void\s+(\w+)\s*\(([^)]*)\)\s*(throws\s+[\w\s,.]+)?\s*\{",
        lambda m: f"{m.group(1) or ''}{m.group(2) or ''}def {m.group(3)}({_params(m.group(4))}): Unit = {{",
        body,
    )

    # Remaining methods with return types (multi-statement)
    body = re.sub(
        r"(override\s+)?(protected\s+|private\s+)?(int|boolean|double|float|long|String|[\w.<>,\[\]]+)\s+(\w+)\s*\(([^)]*)\)\s*(throws\s+[\w\s,.]+)?\s*\{",
        lambda m: f"{m.group(1) or ''}{m.group(2) or ''}def {m.group(4)}({_params(m.group(5))}): {_jtype(m.group(3))} = {{",
        body,
    )

    # fields: private static final String X = → private val X =
    body = re.sub(
        r"(private\s+|protected\s+)?static\s+final\s+([\w.<>,\[\]]+)\s+(\w+)\s*=",
        lambda m: f"{m.group(1) or ''}val {m.group(3)}: {_jtype(m.group(2))} =",
        body,
    )
    body = re.sub(
        r"(private\s+|protected\s+)?static\s+([\w.<>,\[\]]+)\s+(\w+)\s*=",
        lambda m: f"{m.group(1) or ''}val {m.group(3)}: {_jtype(m.group(2))} =",
        body,
    )
    body = re.sub(
        r"(private\s+|protected\s+)?(final\s+)?([\w.<>,\[\]]+)\s+(\w+)\s*=",
        lambda m: f"{m.group(1) or ''}var {m.group(4)}: {_jtype(m.group(3))} =",
        body,
    )
    body = re.sub(
        r"(private\s+|protected\s+)?([\w.<>,\[\]]+)\s+(\w+)\s*;",
        lambda m: f"{m.group(1) or ''}var {m.group(3)}: {_jtype(m.group(2))} = scala.compiletime.uninitialized",
        body,
    )

    # primitives in remaining text (careful - already handled in types)
    # remove semicolons at EOL
    body = re.sub(r";(\s*//[^\n]*)?\n", lambda m: (m.group(1) or "") + "\n", body)
    body = re.sub(r";\s*$", "", body, flags=re.M)

    # constructors: public Foo( → def this(
    body = re.sub(
        rf"public\s+{re.escape(cname)}\s*\(([^)]*)\)\s*\{{",
        lambda m: f"def this({_params(m.group(1))}): this.type = {{",
        body,
    )

    # switch → match (basic)
    body = re.sub(r"switch\s*\(([^)]+)\)\s*\{", r"\1 match {", body)
    body = re.sub(r"case\s+(\w+)\s*:", r"case \1 =>", body)
    body = re.sub(r"\bbreak\s*;?", "", body)

    # new ArrayList<T>(n)
    body = re.sub(r"new\s+ArrayList<([^>]+)>\(\s*\d*\s*\)", r"new java.util.ArrayList[\1]()", body)

    # SearchOptions without type params
    body = re.sub(r"SearchOptions(?!\[)", "SearchOptions[?, ?]", body)

    # Fix double override
    body = re.sub(r"override\toverride\s+", "override ", body)

    # getEyeType / getBoardSize parameterless
    body = re.sub(
        r"def getEyeType\(\): EyeType =",
        "override def getEyeType: EyeType =",
        body,
    )
    body = re.sub(
        r"def getBoardSize\(\): Int =",
        "override def getBoardSize: Int =",
        body,
    )
    body = re.sub(
        r"def setOptionOverrides\(options: SearchOptions\[\?, \?\]\): Unit =",
        "override def setOptionOverrides(options: SearchOptions[?, ?]): Unit =",
        body,
    )
    body = re.sub(
        r"def setUp\(\): Unit =",
        "override def setUp(): Unit =",
        body,
    )

    # remove leftover public/private/final/static keywords incorrectly
    body = re.sub(r"\bpublic\s+", "", body)
    body = re.sub(r"\bfinal\s+", "", body)
    body = re.sub(r"\bstatic\s+", "", body)

    # boolean/int leftover in code (not types we already converted)
    # Don't blanket replace - can break "Boolean" and words

    parent = f" extends {extends}" if extends and extends != "TestCase" else ""
    if implements:
        parent += (" with " if parent else " extends ") + " with ".join(x.strip() for x in implements.split(","))

    kw = "abstract class" if abstract else "class"
    out = [copyright, f"package {pkg}", ""]
    out += [
        "import org.junit.Assert",
        "import org.junit.Before",
        "import org.junit.After",
        "import org.junit.Test",
    ]
    for i in imports:
        out.append(f"import {i}")
    if ".asScala" in body:
        out.append("import scala.jdk.CollectionConverters._")
    out.append("")
    out.append(f"{kw} {cname}{parent} {{")
    out.append(body)
    out.append("}")
    out.append("")
    return "\n".join(out)


def _jtype(t: str) -> str:
    t = t.strip()
    t = t.replace("boolean", "Boolean").replace("int", "Int").replace("double", "Double")
    t = t.replace("float", "Float").replace("long", "Long").replace("void", "Unit")
    t = re.sub(r"\b([A-Z][\w.]*)\[\]", r"Array[\1]", t)
    t = t.replace("<", "[").replace(">", "]")
    return t


def _params(p: str) -> str:
    if not p or not p.strip():
        return ""
    parts = []
    for part in re.split(r",(?![^<>]*>)", p):
        part = part.strip()
        if not part:
            continue
        m = re.match(r"([\w.<>,\s\[\]]+?)\s+(\w+)$", part)
        if m:
            parts.append(f"{m.group(2)}: {_jtype(m.group(1))}")
        else:
            parts.append(part)
    return ", ".join(parts)


def main() -> None:
    files = sorted(JAVA_SRC.rglob("*.java"))
    if len(sys.argv) > 1:
        files = [Path(p) if Path(p).is_absolute() else ROOT / p for p in sys.argv[1:]]
    for jp in files:
        text = jp.read_text()
        rel = jp.relative_to(TEST)
        sp = SCALA / rel.with_suffix(".scala")
        sp.parent.mkdir(parents=True, exist_ok=True)
        try:
            sp.write_text(convert(text))
            print(f"OK {rel}")
        except Exception as e:
            print(f"FAIL {rel}: {e}")


if __name__ == "__main__":
    main()
