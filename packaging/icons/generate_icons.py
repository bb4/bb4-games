#!/usr/bin/env python3
"""Generate a family of app icons for bb4-games (PNG + ICO + ICNS).

Visual language (keep this consistent across other bb4 installer repos):
- 1024 canvas, macOS-style rounded squircle
- Deep navy fill (#0D2438), 1px inner rim in muted teal
- Cream glyphs (#F4F1DE) with a shared accent palette (cyan / gold / coral)
- No wordmarks; one distinctive geometric motif per app
"""

from __future__ import annotations

import math
import os
import shutil
import subprocess
import tempfile
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parent
SIZE = 1024
NAVY = (13, 36, 56, 255)
TEAL_RIM = (58, 140, 150, 255)
CREAM = (244, 241, 222, 255)
CYAN = (78, 205, 196, 255)
GOLD = (232, 184, 74, 255)
CORAL = (224, 122, 95, 255)
SOFT = (90, 140, 170, 255)


def new_canvas() -> tuple[Image.Image, ImageDraw.ImageDraw]:
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    pad = 48
    radius = 220
    draw.rounded_rectangle([pad, pad, SIZE - pad, SIZE - pad], radius=radius, fill=NAVY)
    draw.rounded_rectangle(
        [pad + 18, pad + 18, SIZE - pad - 18, SIZE - pad - 18],
        radius=radius - 12,
        outline=TEAL_RIM,
        width=8,
    )
    return img, draw


def save_all(app_id: str, img: Image.Image) -> None:
    out = ROOT / app_id
    out.mkdir(parents=True, exist_ok=True)
    png_path = out / "icon.png"
    img.save(png_path, "PNG")

    ico_sizes = [(16, 16), (32, 32), (48, 48), (64, 64), (128, 128), (256, 256)]
    img.save(out / "icon.ico", sizes=ico_sizes)

    if shutil.which("iconutil") and shutil.which("sips"):
        write_icns(img, out / "icon.icns")
    else:
        print(f"warning: iconutil/sips not available; skipped icns for {app_id}")


def write_icns(img: Image.Image, dest: Path) -> None:
    with tempfile.TemporaryDirectory() as tmp:
        iconset = Path(tmp) / "icon.iconset"
        iconset.mkdir()
        specs = [
            ("icon_16x16.png", 16),
            ("icon_16x16@2x.png", 32),
            ("icon_32x32.png", 32),
            ("icon_32x32@2x.png", 64),
            ("icon_128x128.png", 128),
            ("icon_128x128@2x.png", 256),
            ("icon_256x256.png", 256),
            ("icon_256x256@2x.png", 512),
            ("icon_512x512.png", 512),
            ("icon_512x512@2x.png", 1024),
        ]
        master = Path(tmp) / "master.png"
        img.save(master)
        for name, px in specs:
            subprocess.check_call(
                ["sips", "-z", str(px), str(px), str(master), "--out", str(iconset / name)],
                stdout=subprocess.DEVNULL,
            )
        subprocess.check_call(["iconutil", "-c", "icns", str(iconset), "-o", str(dest)])


def try_font(size: int) -> ImageFont.ImageFont:
    for name in (
        "/System/Library/Fonts/Supplemental/Arial Bold.ttf",
        "/System/Library/Fonts/Helvetica.ttc",
        "/Library/Fonts/Arial.ttf",
        "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
    ):
        if os.path.exists(name):
            try:
                return ImageFont.truetype(name, size=size)
            except OSError:
                continue
    return ImageFont.load_default()


def draw_gomoku(draw: ImageDraw.ImageDraw) -> None:
    # 5-in-a-row on a grid with one winning cyan line.
    origin, step = 220, 70
    for i in range(9):
        draw.line([origin, origin + i * step, origin + 8 * step, origin + i * step], fill=SOFT, width=4)
        draw.line([origin + i * step, origin, origin + i * step, origin + 8 * step], fill=SOFT, width=4)
    stones = [(2, 2, CREAM), (3, 3, CYAN), (4, 4, CYAN), (5, 5, CYAN), (6, 6, CYAN), (7, 7, CYAN),
              (2, 4, CREAM), (3, 5, CREAM), (5, 3, CREAM)]
    for c, r, color in stones:
        x = origin + c * step
        y = origin + r * step
        draw.ellipse([x - 22, y - 22, x + 22, y + 22], fill=color)


def draw_chess(draw: ImageDraw.ImageDraw) -> None:
    # Chessboard with a stylized king silhouette.
    origin, cell = 232, 70
    for r in range(8):
        for c in range(8):
            color = CREAM if (r + c) % 2 == 0 else SOFT
            x0 = origin + c * cell
            y0 = origin + r * cell
            draw.rectangle([x0, y0, x0 + cell, y0 + cell], fill=color)
    # King glyph
    cx, cy = 512, 500
    draw.rectangle([cx - 18, cy - 160, cx + 18, cy - 120], fill=GOLD)
    draw.rectangle([cx - 50, cy - 145, cx + 50, cy - 125], fill=GOLD)
    draw.polygon([(cx - 70, cy + 40), (cx + 70, cy + 40), (cx + 55, cy - 100), (cx - 55, cy - 100)], fill=GOLD)
    draw.ellipse([cx - 55, cy - 120, cx + 55, cy - 60], fill=GOLD)
    draw.rectangle([cx - 90, cy + 40, cx + 90, cy + 70], fill=GOLD)


def draw_checkers(draw: ImageDraw.ImageDraw) -> None:
    origin, cell = 232, 70
    for r in range(8):
        for c in range(8):
            color = CREAM if (r + c) % 2 == 0 else SOFT
            x0 = origin + c * cell
            y0 = origin + r * cell
            draw.rectangle([x0, y0, x0 + cell, y0 + cell], fill=color)
            if (r + c) % 2 == 1 and (r < 3 or r > 4):
                piece = CORAL if r < 3 else CYAN
                cx = x0 + cell // 2
                cy = y0 + cell // 2
                draw.ellipse([cx - 22, cy - 22, cx + 22, cy + 22], fill=piece)
                draw.ellipse([cx - 12, cy - 12, cx + 12, cy + 12], outline=NAVY, width=3)


def draw_go(draw: ImageDraw.ImageDraw) -> None:
    origin, step = 250, 58
    n = 9
    for i in range(n):
        draw.line([origin, origin + i * step, origin + (n - 1) * step, origin + i * step], fill=SOFT, width=4)
        draw.line([origin + i * step, origin, origin + i * step, origin + (n - 1) * step], fill=SOFT, width=4)
    # star points
    for c, r in ((2, 2), (6, 2), (4, 4), (2, 6), (6, 6)):
        x = origin + c * step
        y = origin + r * step
        draw.ellipse([x - 6, y - 6, x + 6, y + 6], fill=SOFT)
    stones = [
        (3, 3, CREAM), (4, 3, NAVY), (5, 3, CREAM),
        (3, 4, NAVY), (4, 4, CREAM), (5, 4, NAVY),
        (4, 5, NAVY), (5, 5, CREAM),
    ]
    for c, r, color in stones:
        x = origin + c * step
        y = origin + r * step
        outline = CREAM if color == NAVY else SOFT
        draw.ellipse([x - 24, y - 24, x + 24, y + 24], fill=color, outline=outline, width=3)


def draw_blockade(draw: ImageDraw.ImageDraw) -> None:
    origin, cell = 240, 68
    for i in range(9):
        draw.line([origin, origin + i * cell, origin + 8 * cell, origin + i * cell], fill=SOFT, width=3)
        draw.line([origin + i * cell, origin, origin + i * cell, origin + 8 * cell], fill=SOFT, width=3)
    # pawns
    for cx, cy, color in ((origin + 2 * cell, origin + 7 * cell, CYAN),
                          (origin + 6 * cell, origin + cell, CORAL)):
        draw.ellipse([cx - 22, cy - 22, cx + 22, cy + 22], fill=color)
    # walls
    draw.rectangle([origin + 3 * cell - 8, origin + 3 * cell, origin + 5 * cell + 8, origin + 3 * cell + 16], fill=GOLD)
    draw.rectangle([origin + 5 * cell, origin + 4 * cell - 8, origin + 5 * cell + 16, origin + 6 * cell + 8], fill=GOLD)


def draw_hex(draw: ImageDraw.ImageDraw) -> None:
    def hexagon(cx: float, cy: float, r: float, fill, outline=SOFT):
        pts = []
        for i in range(6):
            a = math.radians(60 * i - 30)
            pts.append((cx + r * math.cos(a), cy + r * math.sin(a)))
        draw.polygon(pts, fill=fill, outline=outline)

    rows = 5
    size = 58
    dx = size * math.sqrt(3)
    dy = size * 1.5
    start_x, start_y = 320, 300
    for r in range(rows):
        for c in range(rows):
            cx = start_x + c * dx + r * dx * 0.5
            cy = start_y + r * dy
            if r == 0 or r == rows - 1:
                fill = (CYAN[0], CYAN[1], CYAN[2], 90)
            elif c == 0 or c == rows - 1:
                fill = (CORAL[0], CORAL[1], CORAL[2], 90)
            else:
                fill = (SOFT[0], SOFT[1], SOFT[2], 60)
            hexagon(cx, cy, size - 4, fill)
    # connecting path
    path = [(1, 1, CYAN), (2, 1, CYAN), (2, 2, CYAN), (3, 2, CYAN), (3, 3, CYAN)]
    for c, r, color in path:
        cx = start_x + c * dx + r * dx * 0.5
        cy = start_y + r * dy
        hexagon(cx, cy, size - 10, color, outline=CREAM)


def draw_galactic(draw: ImageDraw.ImageDraw) -> None:
    # Starfield with a few planets and a fleet wedge.
    import random
    rng = random.Random(42)
    for _ in range(40):
        x, y = rng.randint(180, 840), rng.randint(180, 840)
        r = rng.randint(2, 5)
        draw.ellipse([x - r, y - r, x + r, y + r], fill=CREAM)
    planets = [(340, 380, 70, CYAN), (620, 300, 50, GOLD), (700, 620, 90, CORAL), (420, 680, 40, SOFT)]
    for x, y, r, color in planets:
        draw.ellipse([x - r, y - r, x + r, y + r], fill=color)
        draw.arc([x - r, y - r // 2, x + r, y + r // 2], start=200, end=340, fill=CREAM, width=6)
    # fleet triangle
    draw.polygon([(480, 480), (560, 500), (490, 560)], fill=CREAM)


def draw_poker(draw: ImageDraw.ImageDraw) -> None:
    # Two overlapping playing cards with suit marks.
    def card(x0, y0, rot_hint_color):
        draw.rounded_rectangle([x0, y0, x0 + 280, y0 + 400], radius=28, fill=CREAM)
        draw.rounded_rectangle([x0 + 12, y0 + 12, x0 + 268, y0 + 388], radius=20, outline=rot_hint_color, width=6)

    card(280, 300, CORAL)
    card(420, 340, CYAN)
    font = try_font(120)
    draw.text((320, 360), "A", fill=CORAL, font=font)
    draw.text((470, 420), "K", fill=CYAN, font=font)
    # club / heart dots
    draw.ellipse([360, 520, 420, 580], fill=CORAL)
    draw.polygon([(540, 560), (570, 610), (510, 610)], fill=CYAN)


def draw_mancala(draw: ImageDraw.ImageDraw) -> None:
    # Board with pits and stones.
    draw.rounded_rectangle([180, 360, 844, 680], radius=80, fill=SOFT)
    # stores
    for x in (220, 760):
        draw.ellipse([x - 50, 420, x + 50, 620], fill=NAVY, outline=TEAL_RIM, width=6)
    # pits
    for i in range(6):
        for row, y in enumerate((440, 560)):
            x = 320 + i * 70
            draw.ellipse([x - 28, y - 28, x + 28, y + 28], fill=NAVY, outline=CREAM, width=4)
            colors = [CYAN, GOLD, CORAL]
            for j, color in enumerate(colors[: (i + row) % 3 + 1]):
                draw.ellipse([x - 8 + j * 6, y - 8, x + 8 + j * 6, y + 8], fill=color)


def draw_set(draw: ImageDraw.ImageDraw) -> None:
    # Three set cards with matching/differing attributes.
    cards = [
        (220, CYAN, "oval", 1),
        (420, GOLD, "diamond", 2),
        (620, CORAL, "squiggle", 3),
    ]
    for x, color, shape, count in cards:
        draw.rounded_rectangle([x, 280, x + 160, 760], radius=24, fill=CREAM)
        spacing = 90
        start_y = 420 - (count - 1) * spacing // 2
        for i in range(count):
            cy = start_y + i * spacing
            if shape == "oval":
                draw.ellipse([x + 30, cy - 28, x + 130, cy + 28], outline=color, width=10)
            elif shape == "diamond":
                draw.polygon([(x + 80, cy - 32), (x + 130, cy), (x + 80, cy + 32), (x + 30, cy)], outline=color, width=10)
            else:
                draw.arc([x + 30, cy - 30, x + 130, cy + 30], start=200, end=340, fill=color, width=10)
                draw.arc([x + 30, cy - 10, x + 130, cy + 50], start=20, end=160, fill=color, width=10)


def draw_tictactoe(draw: ImageDraw.ImageDraw) -> None:
    origin, cell = 250, 170
    for i in range(1, 3):
        draw.line([origin + i * cell, origin, origin + i * cell, origin + 3 * cell], fill=SOFT, width=14)
        draw.line([origin, origin + i * cell, origin + 3 * cell, origin + i * cell], fill=SOFT, width=14)
    # X
    for (c, r) in ((0, 0), (1, 1), (2, 2)):
        cx = origin + c * cell + cell // 2
        cy = origin + r * cell + cell // 2
        s = 50
        draw.line([cx - s, cy - s, cx + s, cy + s], fill=CYAN, width=18)
        draw.line([cx + s, cy - s, cx - s, cy + s], fill=CYAN, width=18)
    # O
    for (c, r) in ((0, 2), (2, 0)):
        cx = origin + c * cell + cell // 2
        cy = origin + r * cell + cell // 2
        draw.ellipse([cx - 55, cy - 55, cx + 55, cy + 55], outline=CORAL, width=18)


def draw_trivial(draw: ImageDraw.ImageDraw) -> None:
    # Simple high-score contest: three bars of different heights.
    bars = [(300, 520, CYAN), (460, 360, GOLD), (620, 440, CORAL)]
    base = 760
    for x, top, color in bars:
        draw.rounded_rectangle([x, top, x + 100, base], radius=20, fill=color)
    font = try_font(90)
    draw.text((430, 240), "?", fill=CREAM, font=font)


DRAWERS = {
    "gomoku": draw_gomoku,
    "chess": draw_chess,
    "checkers": draw_checkers,
    "go": draw_go,
    "blockade": draw_blockade,
    "hex": draw_hex,
    "galactic": draw_galactic,
    "poker": draw_poker,
    "mancala": draw_mancala,
    "set": draw_set,
    "tictactoe": draw_tictactoe,
    "trivial": draw_trivial,
}


def main() -> None:
    os.chdir(ROOT)
    for app_id, drawer in DRAWERS.items():
        img, draw = new_canvas()
        drawer(draw)
        save_all(app_id, img)
        print(f"wrote {app_id}")


if __name__ == "__main__":
    main()
