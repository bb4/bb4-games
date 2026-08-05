#!/usr/bin/env python3
"""First-draft Java -> Scala 3 sketch converter for bb4-games port. Output needs manual fixes."""

import re
import sys
from pathlib import Path

PRIMITIVES = {
    "void": "Unit", "boolean": "Boolean", "int": "Int", "long": "Long",
    "float": "Float", "double": "Double", "byte": "Byte", "short": "Short", "char": "Char",
}

ROOT = Path(__file__).resolve().parent.parent


def map_output(java_path: Path) -> Path:
    parts = list(java_path.resolve().parts)
    for i, p in enumerate(parts):
        if p == "source":
            parts[i] = "scala-source"
            break
        if p == "test":
            parts[i] = "scala-test"
            break
    return Path(*parts).with_suffix(".scala")


def extract_copyright(lines: list[str]) -> tuple[list[str], list[str]]:
    header, body, i = [], lines, 0
    while i < len(body) and (body[i].strip().startswith("//") or body[i].strip() == ""):
        if body[i].strip().startswith("// Copyright"):
            header.append(body[i].replace("// ", "/* ", 1).rstrip() + " */")
        elif body[i].strip().startswith("//"):
            header.append(body[i])
        i += 1
    return header, body[i:]


def java_type_to_scala(t: str) -> str:
    t = t.strip()
    t = re.sub(r"<([^<>]+)>", lambda m: "[" + java_type_to_scala(m.group(1)) + "]", t)
    t = re.sub(r"\[\s*\]", "[]", t)
    for j, s in PRIMITIVES.items():
        if t == j:
            return s
    return t


def convert_args(args: str) -> str:
    if not args.strip():
        return ""
    out = []
    for part in re.split(r",(?![^<>]*>)", args):
        part = part.strip()
        m = re.match(r"(.+?)\s+(\w+)\s*$", part)
        if m:
            out.append(f"{m.group(2)}: {java_type_to_scala(m.group(1))}")
        else:
            out.append(part)
    return ", ".join(out)


def convert_method_line(line: str, override_next: bool) -> tuple[str, bool]:
    m = re.match(
        r"^(\s*)(?:(public|private|protected)\s+)?(?:(static)\s+)?"
        r"(?:(final|synchronized)\s+)*([\w<>\[\].,? ]+?)\s+(\w+)\s*\(([^)]*)\)\s*(?:throws\s+[\w.,\s]+)?\s*\{?\s*;?\s*$",
        line,
    )
    if not m:
        return line, False
    indent, vis, _, _, ret, name, args = m.groups()
    if name in ("class", "interface", "enum", "if", "for", "while", "switch", "catch", "return", "new"):
        return line, False
    ret_scala = java_type_to_scala(ret)
    prefix = indent
    if vis in ("private", "protected"):
        prefix += vis + " "
    if override_next or "@Override" in line:
        prefix += "override "
    return f"{prefix}def {name}({convert_args(args)}): {ret_scala}", False


def convert_enum_block(text: str) -> str:
    m = re.search(
        r"(?:(public|private|protected)\s+)?enum\s+(\w+)\s*\{([^}]*)\}",
        text, re.DOTALL,
    )
    if not m:
        return text
    vis, name, body = m.groups()
    cases = [c.strip().rstrip(",;") for c in re.findall(r"\b([A-Z][A-Z0-9_]*)\b", body)
             if c not in ("enum", name)]
    vis_p = f"{vis} " if vis in ("private", "protected") else ""
    repl = f"{vis_p}enum {name} extends java.lang.Enum[{name}]:\n  case {', '.join(cases)}"
    return text[:m.start()] + repl + text[m.end():]


def convert_class_line(line: str) -> str:
    line = re.sub(r"\bpublic\s+abstract\s+class\s+", "abstract class ", line)
    line = re.sub(r"\bpublic\s+interface\s+", "trait ", line)
    line = re.sub(r"\bpublic\s+class\s+", "class ", line)
    line = re.sub(r"\bpublic\s+enum\s+", "enum ", line)
    line = re.sub(r"\bpublic\s+", "", line)
    line = re.sub(r"\bimplements\s+", ", ", line)
    line = re.sub(r"\bextends\s+(\w+)\s*,\s*", r"extends \1, ", line)
    return line


def braces_to_indent(text: str) -> str:
    out, indent = [], 0
    for raw in text.splitlines():
        s = raw.strip()
        if s == "}":
            indent = max(0, indent - 1)
            continue
        if s == "{":
            indent += 1
            continue
        if s.endswith("{"):
            content = s[:-1].rstrip()
            if content.endswith(";"):
                content = content[:-1].rstrip()
            if content:
                if not content.endswith(":") and not content.endswith("=>"):
                    content += ":"
                out.append("  " * indent + content)
            indent += 1
            continue
        s = s.rstrip(";")
        out.append("  " * indent + s)
        if s.endswith(":"):
            indent += 1
    return "\n".join(out)


def convert_java(text: str) -> str:
    lines = text.splitlines()
    header, lines = extract_copyright(lines)
    override_next = False
    converted = []
    for line in lines:
        if line.strip().startswith("import static "):
            line = re.sub(r"import\s+static\s+", "import ", line)
        line = line.rstrip(";")
        if line.strip() == "@Override":
            override_next = True
            continue
        line = convert_class_line(line)
        if re.search(r"\b(def|class|trait|enum|package|import)\b", line) is None:
            nl, override_next = convert_method_line(line, override_next)
            if nl != line:
                line = nl
            elif override_next and line.strip().startswith("def "):
                line = re.sub(r"^(\s*)def ", r"\1override def ", line)
                override_next = False
        line = re.sub(r"\bnew\s+([A-Z]\w*)\s*\(", r"\1(", line)
        line = re.sub(r"\bnew\s+([a-z][\w.]*)\s*\(", r"\1(", line)
        line = re.sub(r"(\w+(?:\.\w+)*)\s*<([^<>]+)>", lambda m: f"{m.group(1)}[{m.group(2).replace('<', '[').replace('>', ']')}]", line)
        for j, s in PRIMITIVES.items():
            line = re.sub(rf"\b{j}\b", s, line)
        converted.append(line)
    body = convert_enum_block("\n".join(converted))
    body = braces_to_indent(body)
    return "\n".join(header + ([""] if header else []) + [body]) + "\n"


def convert_file(java_path: Path) -> Path:
    out = map_output(java_path)
    if out.exists():
        print(f"skip (exists): {out}")
        return out
    out.parent.mkdir(parents=True, exist_ok=True)
    scala = convert_java(java_path.read_text(encoding="utf-8", errors="replace"))
    out.write_text(scala, encoding="utf-8")
    print(out)
    return out


def main() -> None:
    if len(sys.argv) < 2:
        print("Usage: java_to_scala_sketch.py FILE.java | --dir DIR", file=sys.stderr)
        sys.exit(1)
    arg = Path(sys.argv[1])
    if sys.argv[1] == "--dir":
        d = Path(sys.argv[2])
        for jf in sorted(d.rglob("*.java")):
            convert_file(jf if jf.is_absolute() else ROOT / jf)
    else:
        jp = arg if arg.is_absolute() else ROOT / arg
        convert_file(jp)


if __name__ == "__main__":
    main()
