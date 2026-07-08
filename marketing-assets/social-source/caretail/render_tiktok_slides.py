from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter, ImageFont


ROOT = Path(__file__).resolve().parent
RAW = ROOT / "screenshots" / "raw"
OUT = ROOT / "slides-2"

W, H = 1080, 1920

CREAM = (255, 247, 235)
AQUA = (229, 248, 244)
TEAL = (19, 126, 119)
TEAL_DARK = (20, 87, 82)
CORAL = (238, 111, 92)
CORAL_DARK = (207, 78, 63)
INK = (34, 45, 49)
MUTED = (91, 111, 114)
WHITE = (255, 255, 255)

FONT_DIR = Path("C:/Windows/Fonts")
FONT_BOLD = FONT_DIR / "seguisb.ttf"
FONT_BLACK = FONT_DIR / "seguibl.ttf"
FONT_REG = FONT_DIR / "segoeui.ttf"


def font(path, size):
    try:
        return ImageFont.truetype(str(path), size)
    except OSError:
        return ImageFont.truetype(str(FONT_DIR / "arial.ttf"), size)


def lerp(a, b, t):
    return int(a + (b - a) * t)


def gradient_bg(top, bottom):
    img = Image.new("RGB", (W, H), top)
    px = img.load()
    for y in range(H):
        t = y / (H - 1)
        for x in range(W):
            drift = 0.028 * (x / (W - 1))
            tt = min(1, max(0, t + drift))
            px[x, y] = tuple(lerp(top[i], bottom[i], tt) for i in range(3))
    return img.convert("RGBA")


def add_noise(img, opacity=10):
    noise = Image.effect_noise((W, H), 18).convert("L")
    alpha = Image.new("L", (W, H), opacity)
    color = Image.new("RGBA", (W, H), (255, 255, 255, 0))
    color.putalpha(Image.eval(noise, lambda p: int(p * opacity / 255)))
    return Image.alpha_composite(img, color)


def rounded_mask(size, radius):
    mask = Image.new("L", size, 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, size[0] - 1, size[1] - 1), radius=radius, fill=255)
    return mask


def cover_crop(im, size, focus_y=0.5):
    sw, sh = size
    scale = max(sw / im.width, sh / im.height)
    nw, nh = int(im.width * scale), int(im.height * scale)
    resized = im.resize((nw, nh), Image.Resampling.LANCZOS)
    left = max(0, (nw - sw) // 2)
    top = int(max(0, min(nh - sh, (nh - sh) * focus_y)))
    return resized.crop((left, top, left + sw, top + sh))


def contain(im, width=None, height=None):
    if width:
        scale = width / im.width
    else:
        scale = height / im.height
    return im.resize((int(im.width * scale), int(im.height * scale)), Image.Resampling.LANCZOS)


def paste_card(base, im, xy, radius=56, shadow=34, tilt=0):
    card = im.convert("RGBA")
    if tilt:
        card = card.rotate(tilt, expand=True, resample=Image.Resampling.BICUBIC, fillcolor=(0, 0, 0, 0))
    mask = rounded_mask(card.size, radius)
    alpha = Image.eval(card.getchannel("A"), lambda a: min(a, 255))
    alpha = Image.composite(alpha, Image.new("L", card.size, 0), mask)
    shadow_img = Image.new("RGBA", card.size, (0, 0, 0, 118))
    shadow_img.putalpha(alpha.filter(ImageFilter.GaussianBlur(shadow)))
    sx, sy = xy[0] + 16, xy[1] + 22
    base.alpha_composite(shadow_img, (sx, sy))
    card.putalpha(alpha)
    base.alpha_composite(card, xy)


def text_size(draw, text, fnt):
    box = draw.textbbox((0, 0), text, font=fnt)
    return box[2] - box[0], box[3] - box[1]


def wrap_text(draw, text, fnt, max_width):
    words = text.split()
    lines, line = [], ""
    for word in words:
        test = word if not line else f"{line} {word}"
        if text_size(draw, test, fnt)[0] <= max_width:
            line = test
        else:
            if line:
                lines.append(line)
            line = word
    if line:
        lines.append(line)
    return lines


def draw_text_block(base, xy, text, max_width, size=70, color=TEAL_DARK, align="left", pad=(34, 26), fill=None):
    draw = ImageDraw.Draw(base)
    fnt = font(FONT_BLACK, size)
    lines = wrap_text(draw, text, fnt, max_width)
    line_h = int(size * 1.06)
    width = max(text_size(draw, line, fnt)[0] for line in lines)
    height = line_h * len(lines) - int(size * 0.08)
    x, y = xy
    if fill:
        draw.rounded_rectangle((x - pad[0], y - pad[1], x + width + pad[0], y + height + pad[1]), radius=30, fill=fill)
    for i, line in enumerate(lines):
        tw = text_size(draw, line, fnt)[0]
        tx = x if align == "left" else x + (width - tw) // 2
        draw.text((tx, y + i * line_h), line, font=fnt, fill=color)
    return (x, y, x + width, y + height)


def draw_subtext(base, xy, text, max_width, size=34, color=MUTED, align="left"):
    draw = ImageDraw.Draw(base)
    fnt = font(FONT_REG, size)
    lines = wrap_text(draw, text, fnt, max_width)
    line_h = int(size * 1.28)
    x, y = xy
    width = max(text_size(draw, line, fnt)[0] for line in lines)
    for i, line in enumerate(lines):
        tw = text_size(draw, line, fnt)[0]
        tx = x if align == "left" else x + (width - tw) // 2
        draw.text((tx, y + i * line_h), line, font=fnt, fill=color)


def pill(draw, xy, text, fill, outline=None, text_color=TEAL_DARK, size=30):
    fnt = font(FONT_BOLD, size)
    tw, th = text_size(draw, text, fnt)
    x, y = xy
    box = (x, y, x + tw + 40, y + th + 24)
    draw.rounded_rectangle(box, radius=24, fill=fill, outline=outline, width=3 if outline else 1)
    draw.text((x + 20, y + 9), text, font=fnt, fill=text_color)


def sticker_check(draw, center, r=28):
    x, y = center
    draw.ellipse((x - r, y - r, x + r, y + r), fill=(255, 255, 255, 232), outline=TEAL, width=4)
    draw.line((x - 13, y + 1, x - 3, y + 12, x + 16, y - 13), fill=TEAL, width=6, joint="curve")


def sticker_calendar(draw, xy):
    x, y = xy
    draw.rounded_rectangle((x, y, x + 66, y + 70), radius=14, fill=(255, 255, 255, 232), outline=CORAL, width=4)
    draw.rectangle((x, y + 20, x + 66, y + 30), fill=CORAL)
    draw.ellipse((x + 16, y + 43, x + 25, y + 52), fill=TEAL)
    draw.ellipse((x + 40, y + 43, x + 49, y + 52), fill=TEAL)


def arrow(draw, points, color=CORAL, width=5):
    draw.line(points, fill=color, width=width, joint="curve")
    x1, y1 = points[-2]
    x2, y2 = points[-1]
    draw.polygon([(x2, y2), (x2 - 22, y2 - 8), (x2 - 9, y2 - 24)], fill=color)


def slide_1():
    base = add_noise(gradient_bg((255, 248, 235), (224, 248, 244)), 8)
    shot = Image.open(RAW / "caretail-01-home-dashboard.png")
    crop = cover_crop(shot, (1030, 1820), focus_y=0.18)
    paste_card(base, crop, (25, 74), radius=58, shadow=26)
    box = draw_text_block(base, (82, 124), "Today's Care. Upcoming. Done.", 520, size=62, fill=(255, 248, 235, 230))
    d = ImageDraw.Draw(base)
    d.line((90, box[3] + 30, 465, box[3] + 30), fill=CORAL, width=12)
    sticker_calendar(d, (910, 165))
    sticker_check(d, (948, 300), 26)
    draw_subtext(base, (86, box[3] + 58), "Luna, Max, reminders, quick actions: all on one dashboard.", 550, 31, TEAL_DARK)
    return base


def slide_2():
    base = add_noise(gradient_bg((233, 250, 246), (255, 247, 235)), 8)
    d = ImageDraw.Draw(base)
    d.ellipse((250, 250, 830, 830), fill=(133, 220, 210, 70))
    d.ellipse((322, 322, 758, 758), fill=(255, 255, 255, 55))
    shot = Image.open(RAW / "caretail-02-pet-profile.png")
    crop = cover_crop(shot, (910, 1520), focus_y=0.22)
    paste_card(base, crop, (85, 300), radius=54, shadow=30)
    draw_text_block(base, (162, 96), "Luna has her own care hub.", 760, size=64, color=TEAL_DARK, align="center")
    pill(d, (410, 225), "Pet profile", (255, 255, 255, 232), TEAL, TEAL_DARK, 28)
    draw_subtext(base, (175, 1780), "Profile, reminders, diary, and report export in one place.", 730, 32, MUTED, "center")
    return base


def slide_3():
    base = add_noise(gradient_bg((255, 248, 235), (230, 249, 246)), 8)
    shot = Image.open(RAW / "caretail-03-reminders-list.png")
    crop = cover_crop(shot, (850, 1645), focus_y=0.17)
    paste_card(base, crop, (115, 208), radius=52, shadow=30)
    draw_text_block(base, (98, 62), "3 active reminders, zero guessing.", 850, size=63, color=TEAL_DARK, fill=(255, 255, 255, 225))
    d = ImageDraw.Draw(base)
    d.rounded_rectangle((690, 486, 905, 548), radius=30, outline=CORAL, width=6)
    d.line((545, 133, 823, 133), fill=CORAL, width=10)
    draw_subtext(base, (154, 1745), "Medication, vaccine, grooming, completed care.", 780, 33, MUTED, "center")
    return base


def slide_4():
    base = add_noise(gradient_bg((224, 249, 246), (255, 249, 238)), 8)
    shot = Image.open(RAW / "caretail-04-add-reminder.png")
    crop = cover_crop(shot, (650, 1540), focus_y=0.18)
    paste_card(base, crop, (390, 210), radius=54, shadow=30)
    draw_text_block(base, (56, 276), "Pick a pet. Pick the care.", 310, size=67, color=TEAL_DARK)
    draw_subtext(base, (60, 520), "Date, time, repeat, notes.", 300, 35, MUTED)
    d = ImageDraw.Draw(base)
    pill(d, (58, 650), "Luna", (255, 255, 255, 220), TEAL, TEAL_DARK, 29)
    pill(d, (58, 725), "Vaccine", (255, 255, 255, 220), CORAL, CORAL_DARK, 29)
    arrow(d, [(300, 690), (405, 650), (520, 626)], CORAL, 5)
    arrow(d, [(300, 765), (435, 785), (548, 815)], TEAL, 5)
    arrow(d, [(260, 870), (435, 950), (565, 1005)], CORAL, 5)
    return base


def slide_5():
    base = add_noise(gradient_bg((255, 250, 241), (235, 250, 247)), 15)
    d = ImageDraw.Draw(base)
    for y in range(250, 1800, 84):
        d.line((0, y, W, y), fill=(237, 225, 209, 65), width=2)
    shot = Image.open(RAW / "caretail-05-diary-notes.png")
    crop = cover_crop(shot, (850, 1560), focus_y=0.15)
    paste_card(base, crop, (78, 242), radius=52, shadow=30)
    d.rounded_rectangle((438, 92, 1012, 296), radius=42, fill=(255, 238, 229, 236), outline=(255, 210, 196, 180), width=3)
    draw_text_block(base, (474, 124), "Today's note is already logged.", 475, size=56, color=CORAL_DARK)
    sticker_check(d, (915, 349), 24)
    d.heart = None
    d.line((876, 365, 895, 386, 928, 344), fill=TEAL, width=5)
    draw_subtext(base, (138, 1760), "Mood, appetite, energy, and care notes.", 760, 33, MUTED, "center")
    return base


def slide_6():
    base = add_noise(gradient_bg((232, 249, 246), (255, 248, 235)), 8)
    d = ImageDraw.Draw(base)
    for i, x in enumerate((70, 850)):
        d.rounded_rectangle((x, 300 + i * 80, x + 150, 500 + i * 80), radius=20, fill=(255, 255, 255, 95), outline=(19, 126, 119, 55), width=3)
        d.rectangle((x + 25, 360 + i * 80, x + 125, 372 + i * 80), fill=(238, 111, 92, 90))
        d.rectangle((x + 25, 400 + i * 80, x + 110, 410 + i * 80), fill=(19, 126, 119, 80))
    shot = Image.open(RAW / "caretail-06-documents.png")
    crop = cover_crop(shot, (800, 1385), focus_y=0.14)
    paste_card(base, crop, (148, 315), radius=52, shadow=30, tilt=-2)
    draw_text_block(base, (138, 92), "Vet records live here too.", 800, size=68, color=TEAL_DARK)
    d.line((535, 177, 760, 177), fill=CORAL, width=10)
    draw_subtext(base, (160, 1742), "Vaccine records, insurance, visit notes, all linked to pets.", 740, 32, MUTED, "center")
    return base


def slide_7():
    base = add_noise(gradient_bg((255, 249, 238), (228, 248, 245)), 8)
    d = ImageDraw.Draw(base)
    d.ellipse((250, 620, 830, 1200), fill=(238, 111, 92, 45))
    d.ellipse((325, 695, 755, 1125), fill=(19, 126, 119, 50))
    shot = Image.open(RAW / "caretail-08-premium.png")
    crop = cover_crop(shot, (820, 1350), focus_y=0.24)
    paste_card(base, crop, (130, 360), radius=52, shadow=30)
    draw_text_block(base, (105, 98), "More pets. More reminders. More room.", 870, size=62, color=TEAL_DARK, align="center")
    for x in (392, 536, 680):
        d.ellipse((x - 8, 284, x + 8, 300), fill=CORAL)
    draw_subtext(base, (210, 1692), "Premium is for bigger care systems.", 650, 34, MUTED, "center")
    return base


SLIDES = [
    ("caretail-tiktok-01-todays-care.png", slide_1),
    ("caretail-tiktok-02-pet-profile.png", slide_2),
    ("caretail-tiktok-03-active-reminders.png", slide_3),
    ("caretail-tiktok-04-pick-care.png", slide_4),
    ("caretail-tiktok-05-diary-logged.png", slide_5),
    ("caretail-tiktok-06-vet-records.png", slide_6),
    ("caretail-tiktok-07-more-room.png", slide_7),
]


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    for name, maker in SLIDES:
        img = maker().convert("RGB")
        out = OUT / name
        img.save(out, "PNG", optimize=True)
        print(f"{out.name}\t{img.size[0]}x{img.size[1]}\t{out.stat().st_size}")


if __name__ == "__main__":
    main()
