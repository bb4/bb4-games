#!/usr/bin/env python3
"""Post-fix rough port_go_tests.py output into compilable Scala 3."""
from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
GO = ROOT / "scala-test/com/barrybecker4/game/twoplayer/go"


def fix_file(text: str) -> str:
    # Remove unused junit lifecycle imports when not needed later — keep for now

    # Type[] → Array[Type], Type[][] → Array[Array[Type]]
    for _ in range(4):
        text = re.sub(r"\b([A-Z][\w.]*)\[\]", r"Array[\1]", text)

    # def foo(...) { return expr }  → def foo(...): ??? = expr   (infer later)
    # Single-statement return methods
    text = re.sub(
        r"(?m)^(\s*)(override\s+)?(protected\s+|private\s+)?def\s+(\w+)\s*\(([^)]*)\)\s*\{\s*\n\s*return\s+([^;\n]+)\s*;?\s*\n\s*\}",
        r"\1\2\3def \4(\5) = \6",
        text,
    )

    # def foo(...) {  (non-Unit looking, often overrides) → def foo(...): Unit = {
    # Only when next non-empty isn't return (already handled) and method name suggests Unit
    text = re.sub(
        r"(?m)^(\s*)(override\s+)?(protected\s+|private\s+)?def\s+(\w+)\s*\(([^)]*)\)\s*\{",
        r"\1\2\3def \4(\5): Unit = {",
        text,
    )

    # Fix double Unit annotation from @Test methods already having Unit
    text = text.replace("): Unit = (): Unit = {", "): Unit = {")
    text = re.sub(r"def (\w+)\(\): Unit = \(\): Unit = \{", r"def \1(): Unit = {", text)

    # getBoardSize() = 5 style — add Int
    text = re.sub(
        r"def getBoardSize\(\) = (\d+)",
        r"override def getBoardSize: Int = \1",
        text,
    )
    text = re.sub(
        r"def getBoardSize\(\): Unit = \{[^}]*return\s+(\d+)[^}]*\}",
        r"override def getBoardSize: Int = \1",
        text,
        flags=re.S,
    )

    # getEyeType
    text = re.sub(
        r"def getEyeType\(\) = (EyeType\.\w+)",
        r"override def getEyeType: EyeType = \1",
        text,
    )
    text = re.sub(
        r"protected def getEyeType\(\): Unit = \{\s*return (EyeType\.\w+)\s*\}",
        r"override protected def getEyeType: EyeType = \1",
        text,
        flags=re.S,
    )

    # setOptionOverrides — fix SearchOptions typing and override
    text = re.sub(
        r"protected def setOptionOverrides\(options: SearchOptions\): Unit = \{",
        "override protected def setOptionOverrides(options: SearchOptions[?, ?]): Unit = {",
        text,
    )
    text = re.sub(
        r"protected def setOptionOverrides\(sOptions: SearchOptions\): Unit = \{",
        "override protected def setOptionOverrides(sOptions: SearchOptions[?, ?]): Unit = {",
        text,
    )

    # Strip leftover Java semicolons mid-line that aren't in strings (best effort)
    # Keep ; inside comments
    lines = []
    for line in text.splitlines():
        if "//" in line:
            code, comment = line.split("//", 1)
            code = re.sub(r";(\s*)$", r"\1", code)
            # also remove ; before comment
            code = re.sub(r";(\s+)$", r"\1", code.rstrip() + " ")
            line = code.rstrip() + (" //" + comment if comment is not None else "")
            # simpler:
        line2 = re.sub(r";(\s*//.*)$", r"\1", line)
        line2 = re.sub(r";(\s*)$", r"\1", line2)
        lines.append(line2)
    text = "\n".join(lines) + "\n"

    # Fix Assert.assertTrue(true); remnants
    text = text.replace("Assert.assertTrue(true);", "Assert.assertTrue(true)")

    # new ArrayList leftover
    text = text.replace("new java.util.ArrayList()", "new java.util.ArrayList[AnyRef]()")

    # Empty param methods that should be parameterless in Scala for overrides
    # getBoardSize() already handled

    # Fix `@Test\ndef` indentation
    text = re.sub(r"@Test\ndef ", "@Test\n    def ", text)
    text = re.sub(r"@Before\ndef ", "@Before\n    def ", text)
    text = re.sub(r"@After\ndef ", "@After\n    def ", text)

    # controller.undoLastMove() may need asInstanceOf
    # leave for compile

    # Fix for-loops that became broken: for (i <- Int = 0 i < n i++) 
    # Leave for now

    # Boolean/Int field decls that became broken with "var x: Boolean = false" is OK

    # Fix `throws Exception` leftovers in signatures if any
    text = re.sub(r"\)\s*throws\s+[\w.\s,]+:", "):", text)

    return text


def main() -> None:
    # Skip hand-written bases
    skip = {
        "GoTestCase.scala",
        "GoBoardConfigurator.scala",
        "WorthCalculatorBase.scala",
        "TestEyeTypeAnalyzer.scala",
        "StubGoEye.scala",
        "StubGoGroup.scala",
        "StubLifeAnalyzer.scala",
        "UpdateStats.scala",
        "GoMoveStub.scala",
    }
    for path in sorted(GO.rglob("*.scala")):
        if path.name in skip and path.name != "StubGoEye.scala":
            # StubGoEye appears twice; always fix non-hand ones... skip both package stubs by path check
            if "Stub" in path.name or path.name in skip:
                # Only skip known hand-written paths
                rel = str(path.relative_to(GO))
                hand = {
                    "GoTestCase.scala",
                    "board/GoBoardConfigurator.scala",
                    "board/analysis/WorthCalculatorBase.scala",
                    "board/analysis/eye/TestEyeTypeAnalyzer.scala",
                    "board/analysis/eye/information/StubGoEye.scala",
                    "board/analysis/group/StubGoGroup.scala",
                    "board/analysis/group/StubLifeAnalyzer.scala",
                    "board/analysis/group/eye/StubGoEye.scala",
                    "board/update/UpdateStats.scala",
                    "board/update/GoMoveStub.scala",
                }
                if rel in hand:
                    print(f"SKIP {rel}")
                    continue
        text = path.read_text()
        fixed = fix_file(text)
        path.write_text(fixed)
        print(f"FIX {path.relative_to(GO)}")


if __name__ == "__main__":
    main()
